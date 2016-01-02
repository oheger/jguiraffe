/*
 * Copyright 2006-2015 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.action.tags;

import net.sf.jguiraffe.gui.builder.action.ActionHelper;
import net.sf.jguiraffe.gui.builder.action.ActionTask;
import net.sf.jguiraffe.gui.builder.action.FormAction;
import net.sf.jguiraffe.gui.builder.action.FormActionException;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderCallBack;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.event.BuilderEvent;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * A tag handler class for creating action objects.
 * </p>
 * <p>
 * The purpose of this tag is to create a
 * {@link net.sf.jguiraffe.gui.builder.action.FormAction FormAction} instance
 * using the current {@link net.sf.jguiraffe.gui.builder.action.ActionManager
 * ActionManager}. All properties of the new action must be provided in
 * attributes.
 * </p>
 * <p>
 * After the action has been created, it is stored in the current
 * {@link net.sf.jguiraffe.gui.builder.action.ActionStore ActionStore} instance.
 * From there it can be accessed by interested components. The following table
 * lists all attributes supported by this tag (in addition the attributes
 * defined in the base class can of course also be used):
 * </p>
 * <p>
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">taskBean</td>
 * <td>Defines the name of the task for this action. It will be looked up in the
 * current {@link net.sf.jguiraffe.di.BeanContext BeanContext} (so it can be an
 * arbitrary bean created by the dependency injection framework). If it does not
 * exists, an exception will be thrown.</td>
 * <td valign="top">depends</td>
 * </tr>
 * <tr>
 * <td valign="top">taskBeanClass</td>
 * <td>This attribute works similar to the <code>task</code> attribute, but the
 * bean to be used for the task is specified by its class. The
 * {@link net.sf.jguiraffe.di.BeanContext BeanContext} will be asked for a bean
 * of the specified class.</td>
 * <td valign="top">depends</td>
 * </tr>
 * <tr>
 * <td valign="top">taskcls</td>
 * <td>With this attribute the action's task can be specified as a fully
 * qualified class name. An instance of this class will be created (which must
 * implement one of the supported interfaces) and passed to the action. Either
 * one of the attributes <code>taskBean</code>, <code>taskBeanClass</code>, or
 * <code>taskClass</code> must be provided.</td>
 * <td valign="top">depends</td>
 * </tr>
 * <tr>
 * <td valign="top">group</td>
 * <td>Here the name of an action group can be specified, to which the action
 * will be added. The {@link net.sf.jguiraffe.gui.builder.action.ActionStore
 * ActionStore} class provides support for groups of actions. If this attribute
 * is specified, the current
 * {@code net.sf.jguiraffe.gui.builder.action.ActionStore ActionStore} is told
 * to add the action to the corresponding group. Refer to the
 * {@link net.sf.jguiraffe.gui.builder.action.ActionStore ActionStore} class for
 * further information about action groups.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">enabled</td>
 * <td>A boolean attribute which can be used to set the initial enabled state
 * of the action. Per default, actions are enabled after they have been
 * created. With this attribute, actions can be initially disabled.
 * </td>
 * <td valign="top">yes</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ActionTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ActionTag extends AbstractActionDataTag
{
    /** Stores the name of the action's task bean. */
    private String taskBean;

    /** Stores the class of the action's task bean. */
    private Object taskBeanClass;

    /** Stores the class of the action's task. */
    private Object taskClass;

    /** Stores the task for the action. */
    private Object actionTask;

    /** The name of the group for the action. */
    private String group;

    /** The enabled flag for this action. */
    private boolean enabled = true;

    /**
     * Setter method for the taskBean attribute.
     *
     * @param name the attribute's value
     */
    public void setTaskBean(String name)
    {
        taskBean = name;
    }

    /**
     * Setter method for the taskBeanClass attribute.
     *
     * @param c the attribute's value
     */
    public void setTaskBeanClass(Object c)
    {
        taskBeanClass = c;
    }

    /**
     * Setter method for the taskClass attribute.
     *
     * @param c the attribute value
     */
    public void setTaskClass(Object c)
    {
        taskClass = c;
    }

    /**
     * Returns the task for the represented action.
     *
     * @return the task for this action
     */
    public Object getTask()
    {
        return actionTask;
    }

    /**
     * Allows to directly set the task for the represented action. This method
     * can be used by sub tasks.
     *
     * @param t the task for this action
     */
    public void setTask(Object t)
    {
        actionTask = t;
    }

    /**
     * Setter method for the group attribute.
     *
     * @param s the attribute value
     */
    public void setGroup(String s)
    {
        group = s;
    }

    /**
     * Returns the value of the enabled attribute.
     *
     * @return the attribute value
     * @since 1.3.1
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Setter method for the enabled attribute.
     *
     * @param enabled the attribute value
     * @since 1.3.1
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * Executes this task. Creates a new action object and stores it in the
     * current action store.
     *
     * @throws JellyTagException if attributes are incorrect
     * @throws FormBuilderException if an error occurs creating the action
     */
    @Override
    protected void process() throws JellyTagException, FormBuilderException
    {
        checkAttributes();
        if (StringUtils.isEmpty(getName()))
        {
            throw new MissingAttributeException("name");
        }

        if (getTask() == null)
        {
            setTask(createTask(taskBean, classFromAttribute(taskBeanClass),
                    classFromAttribute(taskClass)));
        }

        FormAction action = getActionManager().createAction(getActionBuilder(),
                this);
        action.setEnabled(isEnabled());
        getActionBuilder().getActionStore().addAction(action);
        if (group != null)
        {
            getActionBuilder().getActionStore().addActionToGroup(getName(),
                    group);
        }
    }

    /**
     * Creates the task for this action. This task can be either defined by a
     * reference to a bean, in which case it is looked up using the current bean
     * context. Or it is defined through its class name, in which case a new
     * instance of this class will be created. If the task bean is not yet
     * available (because it is defined later in the builder script), a deferred
     * task is created acting as a wrapper around the actual task which is
     * initialized at the end of the builder operation.
     *
     * @param taskName the name of the task bean
     * @param taskBeanClass the class of the task bean
     * @param taskClass the task's class
     * @return the new task
     * @throws JellyTagException if attributes are invalid
     * @throws FormActionException if an error occurs
     */
    protected Object createTask(String taskName, Class<?> taskBeanClass,
            Class<?> taskClass) throws JellyTagException, FormActionException
    {
        Object task = null;
        boolean error = false;

        if (taskClass != null)
        {
            task = createTaskByClass(taskClass);
        }

        if (StringUtils.isNotEmpty(taskName))
        {
            if (task != null)
            {
                error = true;
            }
            else
            {
                if (getBuilderData().getBeanContext().containsBean(taskName))
                {
                    task = getBuilderData().getBeanContext().getBean(taskName);
                }
                else
                {
                    task = new DeferredActionTask(null, taskName);
                    getBuilderData().addCallBack(
                            (ComponentBuilderCallBack) task, null);
                }
            }
        }

        if (taskBeanClass != null)
        {
            if (task != null)
            {
                error = true;
            }
            else
            {
                if (getBuilderData().getBeanContext().containsBean(
                        taskBeanClass))
                {
                    task = getBuilderData().getBeanContext().getBean(
                            taskBeanClass);
                }
                else
                {
                    task = new DeferredActionTask(taskBeanClass, null);
                    getBuilderData().addCallBack(
                            (ComponentBuilderCallBack) task, null);
                }
            }
        }

        if (error || task == null)
        {
            throw new JellyTagException(
                    "Exactly one of the attributes taskBean, "
                            + "taskBeanClass, or taskClass must be specified!");
        }
        return task;
    }

    /**
     * Creates the action's task if it is defined by its class.
     *
     * @param taskClass the class
     * @return the task
     * @throws FormActionException if an error occurs
     */
    protected Object createTaskByClass(Class<?> taskClass)
            throws FormActionException
    {
        try
        {
            return taskClass.newInstance();
        }
        catch (InstantiationException iex)
        {
            throw new FormActionException("Cannot create instance of class "
                    + taskClass.getName(), iex);
        }
        catch (IllegalAccessException iaex)
        {
            throw new FormActionException("Cannot create instance of class "
                    + taskClass.getName(), iaex);
        }
    }

    /**
     * Helper method for obtaining a class from a class attribute. The class can
     * specified either by name or by a real class. It can also be undefined; in
     * this case <b>null</b> is returned.
     *
     * @param attr the value of the class attribute
     * @return the corresponding class or <b>null</b>
     */
    private Class<?> classFromAttribute(Object attr)
    {
        return (attr != null) ? convertToClass(attr) : null;
    }

    /**
     * An internally used helper class for wrapping action tasks that cannot be
     * created immediately. When a builder script is processed the order of the
     * beans is important. Sometimes action tasks need references to beans that
     * can be declared only after the action definitions (e.g. because they need
     * access to graphical components which are defined after the tool bar with
     * actions). This would cause a cyclic dependency. With this class the cycle
     * can be broken. An instance is responsible for the creation of the actual
     * action task. But it does this at the end of the builder script by
     * implementing the {@code ComponentBuilderCallBack} interface. It also
     * implements the {@code ActionTask} interface. This implementation simply
     * delegates to the actual task after it has been obtained from the bean
     * context.
     */
    private static class DeferredActionTask implements
            ComponentBuilderCallBack, ActionTask
    {
        /** Stores the class of the task bean. */
        private final Class<?> taskBeanClass;

        /** Stores the name of the task bean. */
        private final String taskBeanName;

        /** The actual task object. */
        private Object task;

        /**
         * Creates a new instance of {@code DeferredActionTask} and initializes
         * it.
         *
         * @param tskCls the class of the task bean
         * @param tskName the name of the task bean
         */
        public DeferredActionTask(Class<?> tskCls, String tskName)
        {
            taskBeanClass = tskCls;
            taskBeanName = tskName;
        }

        /**
         * This method is invoked at the end of the builder operation. It tries
         * to obtain the actual task.
         *
         * @param builderData the {@code ComponentBuilderData}
         * @param params additional parameters (ignored)
         * @throws FormBuilderException if the task is invalid
         * @throws net.sf.jguiraffe.di.InjectionException if the task bean cannot be resolved
         */
        public void callBack(ComponentBuilderData builderData, Object params)
                throws FormBuilderException
        {
            if (taskBeanClass != null)
            {
                task = builderData.getBeanContext().getBean(taskBeanClass);
            }
            else
            {
                task = builderData.getBeanContext().getBean(taskBeanName);
            }

            if (!ActionHelper.isValidActionTask(task))
            {
                throw new FormBuilderException("Not a valid action task: "
                        + task);
            }
        }

        /**
         * Invokes this action task. This implementation delegates to the
         * wrapped task.
         *
         * @param action the action
         * @param event the current event
         */
        public void run(FormAction action, BuilderEvent event)
        {
            assert task != null : "Action task not set!";
            ActionHelper.invokeActionTask(task, action, event);
        }
    }
}
