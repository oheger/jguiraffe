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

/**
 * <p>
 * A specialized action task for exiting the current application.
 * </p>
 * <p>
 * This action task simply calls the
 * {@link Application#shutdown(Object, Object)} method, which is the default way
 * of terminating the currently running application. It can be associated with a
 * {@code File|Exit} menu item defined by most applications.
 * </p>
 * <p>
 * The {@code shutdown()} method of {@link Application} checks whether currently
 * still background tasks are running. If this is the case, a message box is
 * displayed asking the user whether the application should shutdown anyway.
 * Both the title and the message displayed by this message box can be
 * specified. This class defines corresponding properties of type {@code Object}
 * according to the way resource texts are specified. Here either resource IDs
 * can be passed in or {@code Message} objects. If no specific values for these
 * properties are set, default values are used referring to standard resources
 * shipped with the library.
 * </p>
 * <p>
 * The following example fragment shows how the action for terminating the
 * application can be defined in a Jelly builder script:
 *
 * <pre>
 * &lt;!-- The action task for quitting the application --&gt;
 * &lt;di:bean name=&quot;exitTask&quot;
 *   beanClass=&quot;net.sf.jguiraffe.gui.app.ApplicationShutdownActionTask&quot;&gt;
 *   &lt;di:setProperty property=&quot;exitPromptMessageResource&quot;&gt;
 *     &lt;di:bean beanClass=&quot;net.sf.jguiraffe.resources.Message&quot;&gt;
 *       &lt;di:constructor&gt;
 *         &lt;di:param value=&quot;EXIT_MESSAGE&quot;/&gt;
 *       &lt;/di:constructor&gt;
 *     &lt;/di:bean&gt;
 *   &lt;/di:setProperty&gt;
 * &lt;/di:bean&gt;
 *
 * &lt;!-- The action itself --&gt;
 * &lt;a:action name=&quot;exitAction&quot; text=&quot;Exit&quot; taskBean=&quot;exitTask&quot;/&gt;
 * </pre>
 *
 * This fragment declares an {@code ApplicationShutdownActionTask} bean with a
 * custom prompt message to be displayed if there is still background activity.
 * A {@code Message} object initialized with a resource ID is constructed and
 * passed to the {@code exitPromptMessageResource} property (the default
 * resource group is used; it would also be possible to pass a resource group to
 * the {@code Message} object). The resource ID for the title of the exit prompt
 * dialog is not set, so here the default is used.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: $
 */
public class ApplicationShutdownActionTask implements Runnable,
        ApplicationClient
{
    /** Stores the central application object. */
    private Application application;

    /** The resource specification for the message of the exit prompt dialog. */
    private Object exitPromptMessageResource;

    /** The resource specification for the title of the exit prompt dialog. */
    private Object exitPromptTitleResource;

    /**
     * Returns the resource ID for the message of the exit prompt dialog. This
     * implementation never returns <b>null</b>; if the property was not set, a
     * default value is returned.
     *
     * @return the resource ID for the exit prompt dialog message
     */
    public Object getExitPromptMessageResource()
    {
        return (exitPromptMessageResource != null) ? exitPromptMessageResource
                : ApplicationResources
                        .message(ApplicationResources.Keys.EXIT_PROMPT_MSG);
    }

    /**
     * Sets the resource ID for the message of the exit prompt dialog. This
     * dialog is displayed if there are still running background tasks when the
     * application should shut down. The parameter object can be a {@code
     * Message} object. Otherwise, it is interpreted as resource ID and resolved
     * against the default resource group.
     *
     * @param exitPromptMessageResource the resource ID
     */
    public void setExitPromptMessageResource(Object exitPromptMessageResource)
    {
        this.exitPromptMessageResource = exitPromptMessageResource;
    }

    /**
     * Returns the resource ID for the title of the exit prompt dialog. This
     * implementation never returns <b>null</b>; if the property was not set, a
     * default value is returned.
     *
     * @return the resource ID for the exit prompt dialog title
     */
    public Object getExitPromptTitleResource()
    {
        return (exitPromptTitleResource != null) ? exitPromptTitleResource
                : ApplicationResources
                        .message(ApplicationResources.Keys.EXIT_PROMPT_TIT);
    }

    /**
     * Sets the resource ID for the title of the exit prompt dialog. This dialog
     * is displayed if there are still running background tasks when the
     * application should shut down. The parameter object can be a {@code
     * Message} object. Otherwise, it is interpreted as resource ID and resolved
     * against the default resource group.
     *
     * @param exitPromtTitleResource the resource ID
     */
    public void setExitPromptTitleResource(Object exitPromtTitleResource)
    {
        this.exitPromptTitleResource = exitPromtTitleResource;
    }

    /**
     * Returns the central {@code Application} object. This is the application
     * this task is going to shut down.
     *
     * @return the {@code Application}
     */
    public Application getApplication()
    {
        return application;
    }

    /**
     * Sets the central {@code Application} object. If this bean is created by
     * the dependency injection framework (which is the intended default way of
     * using this class), this reference is automatically injected.
     *
     * @param app the {@code Application} object
     */
    public void setApplication(Application app)
    {
        application = app;
    }

    /**
     * Executes this task. This implementation calls the
     * {@link Application#shutdown(Object, Object)} method on the {@code
     * Application} object passed to {@link #setApplication(Application)}. If no
     * {@code Application} was set, an exception is thrown.
     *
     * @throws IllegalStateException if no {@code Application} has been set
     */
    public void run()
    {
        if (getApplication() == null)
        {
            throw new IllegalStateException("No Application reference set!");
        }

        getApplication().shutdown(getExitPromptMessageResource(),
                getExitPromptTitleResource());
    }
}
