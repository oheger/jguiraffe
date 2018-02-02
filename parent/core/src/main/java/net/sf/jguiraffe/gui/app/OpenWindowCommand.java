/*
 * Copyright 2006-2018 The JGUIraffe Team.
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

import net.sf.jguiraffe.gui.builder.Builder;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.cmd.CommandBase;
import net.sf.jguiraffe.locators.Locator;

/**
 * <p>
 * A specialized {@code Command} implementation for opening a sub window.
 * </p>
 * <p>
 * This {@code Command} class provides functionality for opening sub frames or
 * dialog boxes. An instance is initialized with the {@link Locator} to the
 * builder script that defines the window. It executes this script in the worker
 * thread and eventually displays the resulting window.
 * </p>
 * <p>
 * Most applications need to open dialog boxes or other sub windows. With this
 * command this can be done in a standard way. Using {@link CommandActionTask}
 * an instance can be associated with an action. It is possible to define such
 * an action completely in a builder script using the dependency injection
 * framework. This can look as follows:
 *
 * <pre>
 * &lt;!-- Definition of the command bean for opening a dialog --&gt;
 * &lt;di:bean name=&quot;openDialogCommand&quot; singleton=&quot;false&quot;
 *   beanClass=&quot;net.sf.jguiraffe.gui.app.OpenWindowCommand&quot;&gt;
 *   &lt;di:constructor&gt;
 *     &lt;di:param&gt;
 *       &lt;di:bean class=&quot;net.sf.jguiraffe.locators.ClassPathLocator&quot;&gt;
 *         &lt;di:factory&gt;
 *           &lt;di:methodInvocation method=&quot;getInstance&quot;&gt;
 *             &lt;di:param value=&quot;myDialog.jelly&quot;/&gt;
 *           &lt;/di:methodInvocation&gt;
 *         &lt;/di:factory&gt;
 *       &lt;/di:bean&gt;
 *     &lt;/di:param&gt;
 *   &lt;/di:constructor&gt;
 * &lt;/di:bean&gt;
 * &lt;!-- The command task used by the action for opening a dialog --&gt;
 * &lt;di:bean name=&quot;openDialogTask&quot;
 *   beanClass=&quot;net.sf.jguiraffe.gui.app.CommandActionTask&quot;&gt;
 *   &lt;di:setProperty property=&quot;commandBeanName&quot;
 *     value=&quot;openDialogCommand&quot;/&gt;
 *   &lt;di:setProperty property=&quot;beforeEnabler&quot;&gt;
 *     &lt;di:bean beanClass=&quot;net.sf.jguiraffe.gui.builder.enablers.ActionEnabler&quot;&gt;
 *       &lt;di:constructor&gt;
 *         &lt;di:param value=&quot;openDialogAction&quot;/&gt;
 *       &lt;/di:constructor&gt;
 *     &lt;/di:bean&gt;
 *   &lt;/di:setProperty&gt;
 * &lt;/di:bean&gt;
 * &lt;!-- The action for opening a dialog --&gt;
 * &lt;a:action name=&quot;openDialogAction&quot; text=&quot;Open dialog...&quot;
 *   taskBean=&quot;openDialogTask&quot;/&gt;
 * </pre>
 *
 * This fragment first defines the {@code OpenWindowCommand} bean. The bean is
 * passed a newly created {@code Locator} object when it is constructed. Here a
 * locator implementation is used that searches the builder script in the class
 * path. It follows the definition of the action task bean. This is a
 * {@link CommandActionTask} which is configured with our command bean. Note
 * that also an <em>action enabler</em> is specified; this object disables the
 * action while it is executed. Finally the action itself is defined. It
 * references the task bean. The action can later be used to create a menu item
 * or a button in a tool bar.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: OpenWindowCommand.java 205 2012-01-29 18:29:57Z oheger $
 */
public class OpenWindowCommand extends CommandBase implements ApplicationClient
{
    /** The locator to the builder script. */
    private final Locator locator;

    /** Stores the central application object. */
    private Application application;

    /** The window created in the background thread. */
    private Window window;

    /**
     * Creates a new instance of {@code OpenWindowCommand} and initializes it
     * with the {@code Locator} pointing to the builder script. The command will
     * execute this builder script and display the resulting window.
     *
     * @param loc the {@code Locator} to the builder script (must not be
     *        <b>null</b>)
     * @throws IllegalArgumentException if the {@code Locator} is <b>null</b>
     */
    public OpenWindowCommand(Locator loc)
    {
        super(true);
        if (loc == null)
        {
            throw new IllegalArgumentException("Locator must not be null!");
        }
        locator = loc;
    }

    /**
     * Returns the {@code Locator} to the builder script executed by this
     * command.
     *
     * @return the {@code Locator}
     */
    public final Locator getLocator()
    {
        return locator;
    }

    /**
     * Returns the central {@code Application} instance.
     *
     * @return the {@code Application}
     */
    public final Application getApplication()
    {
        return application;
    }

    /**
     * Sets the central {@code Application} instance. From this object the
     * {@code ApplicationContext} is obtained, which provides access to the
     * builder. This method is typically called by the dependency injection
     * framework.
     *
     * @param app the central {@code Application} object
     */
    public final void setApplication(Application app)
    {
        application = app;
    }

    /**
     * <p>
     * Executes this command. This implementation calls the builder to execute
     * the builder script defining the window to be opened. Then the window is
     * actually displayed.
     * </p>
     * <p>
     * Note: This method does not implement a sophisticated exception handling.
     * It expects the current {@code Locator} to be valid and to point to a
     * builder script that actually returns a window. Otherwise, an exception is
     * thrown which will be passed to the {@code onException()} method.
     * </p>
     * @throws Exception if an error occurs
     */
    public void execute() throws Exception
    {
        if (getApplication() == null)
        {
            throw new IllegalStateException("No Application reference set!");
        }

        Builder builder = getApplication().getApplicationContext().newBuilder();
        ApplicationBuilderData builderData = getApplication()
                .getApplicationContext().initBuilderData();
        prepareBuilderData(builderData);

        window = builder.buildWindow(getLocator(), builderData);
    }

    /**
     * Updates the UI after background processing is complete. This
     * implementation opens the window created in the background thread unless
     * an exception occurred before.
     */
    @Override
    protected void performGUIUpdate()
    {
        if (getException() == null)
        {
            assert getWindow() != null : "No window!";
            getWindow().open();
        }
    }

    /**
     * Prepares the {@code ApplicationBuilderData} object. This method is called
     * by {@code execute()} with the {@code ApplicationBuilderData} object
     * obtained from the {@link ApplicationContext}. Derived classes can
     * override it to perform custom initialization. This base implementation is
     * empty.
     *
     * @param builderData the {@code ApplicationBuilderData} object to be
     *        initialized
     */
    protected void prepareBuilderData(ApplicationBuilderData builderData)
    {
    }

    /**
     * Returns the {@code Window} that was built in the background thread. This
     * method is called by the GUI updater to obtain the window to be displayed.
     * It exists mainly for testing purposes.
     *
     * @return the {@code Window} to be displayed
     */
    Window getWindow()
    {
        return window;
    }
}
