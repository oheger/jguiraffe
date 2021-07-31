/*
 * Copyright 2006-2021 The JGUIraffe Team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.jguiraffe.gui.app;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.di.BeanContextClient;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.enablers.ElementEnabler;
import net.sf.jguiraffe.gui.builder.enablers.NullEnabler;
import net.sf.jguiraffe.gui.cmd.Command;
import net.sf.jguiraffe.gui.cmd.CommandQueue;
import net.sf.jguiraffe.gui.cmd.CommandWrapper;
import net.sf.jguiraffe.gui.cmd.ScheduleAware;

/**
 * <p>
 * A specialized action task that executes a {@link Command} object.
 * </p>
 * <p>
 * This class can be used as a task for
 * {@link net.sf.jguiraffe.gui.builder.action.FormAction FormAction} objects.
 * When invoked by the associated action it will put a command object in the
 * application's command queue. This is especially useful for longer running
 * actions that should not block the application's event dispatch thread.
 * </p>
 * <p>
 * Instances can be initialized with either a concrete {@link Command} object or
 * with the name of a command bean. In the former case the {@link Command}
 * object will be reused each time the action is triggered. In the latter case
 * for each invocation a bean with the specified name is queried from the
 * current {@link BeanContext}. (This bean must implement the {@link Command}
 * interface.)
 * </p>
 * <p>
 * This class implements some support for disabling and enabling UI elements
 * during the execution of the associated command. For instance, it may make
 * sense to disable certain actions (e.g. menu items and/or tool bar buttons)
 * while the command is running in background. After completion of the
 * background task these actions can be enabled again. For this purpose two
 * {@link ElementEnabler} objects can be set: one is invoked when the associated
 * {@code Command} is passed to the command queue for execution. The other one
 * is triggered at the end of the {@code Command}'s execution (in the event
 * dispatch thread). The second {@code ElementEnabler} can be omitted if it is
 * the exact counterpart of the first one.
 * </p>
 * <p>
 * A {@code CommandActionTask} object can be fully declared in a builder script
 * using the capabilities of the dependency injection framework and the
 * {@link net.sf.jguiraffe.gui.builder.action.tags.ActionTaskTag ActionTaskTag}.
 * Because this class implements the {@link BeanContextClient} interface the
 * reference to the current {@link BeanContext} (which is required for accessing
 * the central {@link Application} and also the command beans) will then be
 * automatically set. If the object is created by hand, it lies in the
 * responsibility of the developer to ensure that the reference to the
 * {@link BeanContext} is correctly initialized. An example for an action
 * declaration in a builder script making use of this class could look as
 * follows:
 *
 * <pre>
 * &lt;!-- Definition of the command bean --&gt;
 * &lt;di:bean name=&quot;commandBean&quot; singleton=&quot;false&quot;
 *   beanClass=&quot;com.acme.CommandBeanImpl&quot;/&gt;
 * &lt;!-- The command task used by the action --&gt;
 * &lt;di:bean name=&quot;commandTask&quot;
 *   beanClass=&quot;net.sf.jguiraffe.gui.app.CommandActionTask&quot;&gt;
 *   &lt;di:setProperty property=&quot;commandBeanName&quot; value=&quot;commandBean&quot;/&gt;
 *   &lt;di:setProperty property=&quot;beforeEnabler&quot;&gt;
 *     &lt;di:bean beanClass=&quot;net.sf.jguiraffe.gui.builder.enablers.ActionEnabler&quot;&gt;
 *       &lt;di:constructor&gt;
 *         &lt;di:param value=&quot;testAction&quot;/&gt;
 *       &lt;/di:constructor&gt;
 *     &lt;/di:bean&gt;
 *   &lt;/di:setProperty&gt;
 * &lt;/di:bean&gt;
 * &lt;!-- The action itself --&gt;
 * &lt;a:action name=&quot;testAction&quot; text=&quot;Test action&quot;
 *   taskBean=&quot;commandTask&quot;/&gt;
 * </pre>
 *
 * This script creates an action named <em>testAction</em> that is associated
 * with a {@code CommandActionTask} object as its task. The task uses a
 * {@code Command} object that is also defined as a bean. (Note that the command
 * bean has the {@code singleton} attribute set to <b>false</b>, so each time
 * the action task is executed a new instance of the command class will be
 * created.) In this example also an {@link ElementEnabler} is defined. The way
 * this enabler is defined, it disables the action when the task is executed and
 * enables it again after the task's execution. (Thus the user cannot activate
 * the action again as long as it is running.) <em>Note:</em> When executing a
 * builder script default converters are in place. There is also a default
 * converter which can deal with {@code ElementEnabler} objects, so you can use
 * an abbreviated form in most cases.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: CommandActionTask.java 205 2012-01-29 18:29:57Z oheger $
 */
public class CommandActionTask implements Runnable, BeanContextClient
{
    /** Stores the current bean context. */
    private BeanContext beanContext;

    /** Stores a command object. */
    private Command command;

    /** Stores the name of the command bean. */
    private String commandBeanName;

    /** The optional before enabler. */
    private ElementEnabler beforeEnabler;

    /** The optional after enabler. */
    private ElementEnabler afterEnabler;

    /**
     * Returns the command object.
     *
     * @return the command object
     */
    public Command getCommand()
    {
        return command;
    }

    /**
     * Sets the command object. Here an object can be passed that will be reused
     * for each invocation.
     *
     * @param command the command object to be used
     */
    public void setCommand(Command command)
    {
        this.command = command;
    }

    /**
     * Returns the name of the command bean.
     *
     * @return the name of the command bean
     */
    public String getCommandBeanName()
    {
        return commandBeanName;
    }

    /**
     * Sets the name of the command bean. If this property is set, the {@code
     * Command} object to be executed by this task is obtained from the current
     * {@link BeanContext} using this property as bean name. However, this
     * property is only evaluated if no {@code Command} object was set using the
     * {@link #setCommand(Command)} method.
     *
     * @param commandBeanName the name of the command bean
     */
    public void setCommandBeanName(String commandBeanName)
    {
        this.commandBeanName = commandBeanName;
    }

    /**
     * Returns the {@code ElementEnabler} that is invoked before the execution
     * of this task. This method never returns <b>null</b>; if no enabler has
     * been set explicitly, a default dummy enabler is returned.
     *
     * @return the {@code ElementEnabler} before the execution
     */
    public ElementEnabler getBeforeEnabler()
    {
        return (beforeEnabler != null) ? beforeEnabler : NullEnabler.INSTANCE;
    }

    /**
     * Sets the {@code ElementEnabler} that is invoked before the execution of
     * this task. As soon as this task is triggered, this {@code ElementEnabler}
     * is invoked with a value of <b>false</b> for the enabled state argument.
     * If no after enabler was set, it is also called after the execution of the
     * {@code Command} - this time with a value of <b>true</b> for the enabled
     * state argument.
     *
     * @param beforeEnabler the {@code ElementEnabler} before the execution
     * @see #setAfterEnabler(ElementEnabler)
     */
    public void setBeforeEnabler(ElementEnabler beforeEnabler)
    {
        this.beforeEnabler = beforeEnabler;
    }

    /**
     * Returns the {@code ElementEnabler} that is invoked after the execution of
     * this task. This method never returns <b>null</b>; if no enabler has been
     * set explicitly, a default dummy enabler is returned.
     *
     * @return the {@code ElementEnabler} after the execution
     */
    public ElementEnabler getAfterEnabler()
    {
        return (afterEnabler != null) ? afterEnabler : getBeforeEnabler();
    }

    /**
     * Sets the {@code ElementEnabler} that is invoked after the execution of
     * this task. After the command has been executed this {@code
     * ElementEnabler} is invoked (in the event dispatch thread) with a value of
     * <b>true</b> for the enabled state argument. An after enabler is only
     * necessary if the enabling/disabling is asymmetric, i.e. before the
     * execution different elements are enabled/disabled than after the
     * execution. If no after enabler is set, the before enabler is invoked both
     * before and after execution. Also note that the after enabler is always
     * called with the value <b>true</b> for the enabled state argument; if a
     * different flag value is required, an
     * {@link net.sf.jguiraffe.gui.builder.enablers.InverseEnabler} can be used
     * to switch the behavior.
     *
     * @param afterEnabler the {@code ElementEnabler} after the execution
     */
    public void setAfterEnabler(ElementEnabler afterEnabler)
    {
        this.afterEnabler = afterEnabler;
    }

    /**
     * Returns the current {@code BeanContext}.
     *
     * @return the current {@code BeanContext}
     */
    public BeanContext getBeanContext()
    {
        return beanContext;
    }

    /**
     * Sets the current {@code BeanContext}. This method is usually
     * automatically called by the dependency injection framework.
     *
     * @param context the current {@code BeanContext}
     */
    public void setBeanContext(BeanContext context)
    {
        beanContext = context;
    }

    /**
     * Executes this task. Obtains the {@code Command} object by invoking
     * {@link #fetchCommand()} and {@link #createCommandWrapper(Command)} and
     * passes it to the application.
     */
    public void run()
    {
        Command cmd = createCommandWrapper(fetchCommand());
        getApplication().execute(cmd);
    }

    /**
     * Returns the {@code BeanContext} to use and checks whether it is defined.
     * This method delegates to {@link #getBeanContext()} and throws an
     * exception if no context was set.
     *
     * @return the current {@code BeanContext}
     * @throws IllegalStateException if no {@code BeanContext} was set
     */
    protected BeanContext fetchBeanContext()
    {
        BeanContext ctx = getBeanContext();
        if (ctx == null)
        {
            throw new IllegalStateException("No BeanContext set!");
        }
        return ctx;
    }

    /**
     * Returns a reference to the global application object. It is obtained
     * through the current {@link BeanContext}.
     *
     * @return the application object
     */
    protected Application getApplication()
    {
        return Application.getInstance(fetchBeanContext());
    }

    /**
     * Obtains the command object to be executed. This implementation will use
     * the command object if one was set. Otherwise it requests the command
     * object from the current {@code BeanContext} using the bean name specified
     * by the {@code commandBeanName} property. If neither a {@code Command}
     * object nor the name of a {@code Command} bean was set, an
     * {@link ApplicationRuntimeException} exception is thrown.
     *
     * @return the command object to use
     * @throws ApplicationRuntimeException if the command is not specified
     * @throws net.sf.jguiraffe.di.InjectionException if the command bean cannot
     *         be obtained
     */
    protected Command fetchCommand() throws ApplicationRuntimeException
    {
        Command result = getCommand();

        if (result == null)
        {
            if (getCommandBeanName() == null)
            {
                throw new ApplicationRuntimeException(
                        "Command to be executed is undefined! "
                                + "Set either the command or the "
                                + "commandBeanName property of CommandActionTask.");
            }
            result = (Command) fetchBeanContext().getBean(getCommandBeanName());
        }

        return result;
    }

    /**
     * Creates the {@code Command} object wrapper that is passed to the
     * {@link Application#execute(Command)} method. This method is called by
     * {@code run()} with the {@code Command} object returned by
     * {@link #fetchCommand()} as argument. Because some additional tasks have
     * to be performed (e.g. invoking the {@code ElementEnabler}) the {@code
     * Command} managed by this task is not directly executed. Instead, a
     * wrapper is created around this {@code Command}, which takes care about
     * these tasks. This method creates this wrapper.
     *
     * @param actualCommand the {@code Command} object implementing the actual
     *        logic to be executed
     * @return a {@code Command} wrapping the actual command and performing
     *         additional housekeeping tasks
     */
    protected Command createCommandWrapper(Command actualCommand)
    {
        return new CmdActionTaskWrapper(actualCommand);
    }

    /**
     * Helper method for invoking an {@code ElementEnabler}. The
     * {@link ComponentBuilderData} object required for the invocation is
     * obtained from the {@code BeanContext}. If the {@code ElementEnabler}
     * throws an exception, it is re-thrown as a runtime exception.
     *
     * @param enabler the enabler to invoke
     * @param state the enabled state
     * @throws ApplicationRuntimeException if the enabler throws an exception
     * @throws net.sf.jguiraffe.di.InjectionException if the component builder
     *         data bean cannot be obtained
     */
    private void invokeElementEnabler(ElementEnabler enabler, boolean state)
    {
        ComponentBuilderData compData = (ComponentBuilderData) fetchBeanContext()
                .getBean(ComponentBuilderData.KEY_COMPONENT_BUILDER_DATA);
        try
        {
            enabler.setEnabledState(compData, state);
        }
        catch (FormBuilderException fbex)
        {
            throw new ApplicationRuntimeException(
                    "Element enabler threw an exception", fbex);
        }
    }

    /**
     * A command wrapper implementation that wraps the command to be executed by
     * this task. This wrapper class performs some housekeeping tasks before and
     * after the execution of the actual command.
     */
    private class CmdActionTaskWrapper extends CommandWrapper implements
            ScheduleAware
    {
        /**
         * Creates a new instance of {@code CmdActionTaskWrapper} and sets the
         * command to be wrapped.
         *
         * @param wrappedCmd the wrapped command
         */
        public CmdActionTaskWrapper(Command wrappedCmd)
        {
            super(wrappedCmd);
        }

        /**
         * Notifies this command that it was passed to a command queue. This
         * implementation invokes the before enabler.
         *
         * @param queue the command queue (unused)
         */
        public void commandScheduled(CommandQueue queue)
        {
            invokeElementEnabler(getBeforeEnabler(), false);
        }

        /**
         * Performs GUI updates after the execution of the command. The returned
         * object invokes the GUI updater of the wrapped command if any. It also
         * calls the after enabler.
         *
         * @return a {@code Runnable} object for updating the GUI
         */
        @Override
        public Runnable getGUIUpdater()
        {
            final Runnable wrappedUpdater = getWrappedCommand().getGUIUpdater();
            return new Runnable()
            {
                public void run()
                {
                    if (wrappedUpdater != null)
                    {
                        wrappedUpdater.run();
                    }
                    invokeElementEnabler(getAfterEnabler(), true);
                }
            };
        }
    }
}
