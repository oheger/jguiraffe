/*
 * Copyright 2006-2017 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.window.tags;

import net.sf.jguiraffe.gui.builder.components.tags.UseBeanBaseTag;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.builder.window.WindowBuilderData;
import net.sf.jguiraffe.gui.builder.window.WindowClosingStrategy;
import net.sf.jguiraffe.gui.builder.window.WindowListener;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A tag handler class that allows the creation of window controllers.
 * </p>
 * <p>
 * A window controller is an object that is associated with a window. It will
 * typically handle events triggered by the window or its components and thus
 * implement logic. A standard use case would be a controller that performs
 * validation of user input and allows closing a dialog only if all input fields
 * contain valid data.
 * </p>
 * <p>
 * With this tag such controller objects can be automatically created and
 * initialized with data related to the window and the whole builder process. As
 * an alternative it is also possible to create a controller object manually and
 * initialize it after the builder process has been completed.
 * </p>
 * <p>
 * This tag is derived from {@link UseBeanBaseTag} and thus provides advanced
 * functionality for creating a bean instance of an arbitrary class and
 * initializing its properties. Especially the full power of the
 * <em>dependency injection framework</em> can be used for constructing
 * controller beans. Bean definitions for controller beans per default have
 * access to a bunch of objects that may be of interest for such a controller,
 * e.g. the current window, the form object, the created GUI controls, or the
 * defined action objects. The Javadocs for the classes
 * {@link net.sf.jguiraffe.gui.builder.components.ComponentBuilderData
 * ComponentBuilderData},
 * {@link net.sf.jguiraffe.gui.builder.action.ActionBuilder ActionBuilder}, and
 * {@link WindowBuilderData} describe all objects that can be accessed in the
 * current bean context.
 * </p>
 * <p>
 * An example of using this tag could look as follows:
 *
 * <pre>
 * &lt;!-- Define the bean for the controller --&gt;
 * &lt;di:bean name="CTRL_WINDOW" singleton="false"
 *   beanClass="com.mypackage.WindowController"&gt;
 *   &lt;di:setProperty property="window" refName="CURRENT_WINDOW"/&gt;
 * &lt;/di:bean&gt;
 *
 * &lt;!-- The frame definition including the controller --&gt;
 * &lt;w:frame title="Testwindow"&gt;
 *   &lt;!-- Define the GUI here --&gt;
 *      ...
 *
 *   <strong>&lt;w:controller beanName="CTRL_WINDOW"/&gt;</strong>
 * &lt;/w:frame&gt;
 * </pre>
 *
 * </p>
 * <p>
 * After the controller bean has been created and initialized it is checked
 * whether it implements certain interfaces:
 * <ul>
 * <li>If it implements the {@link WindowListener} interface, it will be
 * registered as a window listener at the newly created window.</li>
 * <li>If it implements the
 * {@link net.sf.jguiraffe.gui.builder.window.WindowClosingStrategy
 * WindowClosingStrategy} interface, it will be set as the new window's closing
 * strategy.</li>
 * </ul>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: WindowControllerTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class WindowControllerTag extends UseBeanBaseTag
{
    /**
     * Creates a new instance of <code>WindowControllerTag</code>.
     */
    public WindowControllerTag()
    {
        super();
    }

    /**
     * Creates a new instance of <code>WindowControllerTag</code> and allows
     * setting a default bean class and a base class. The default class is used
     * when no bean class is specified. If a base class is set, the tag checks
     * whether the bean is of this class or a derived class.
     *
     * @param defaultClass the default class
     * @param baseClass the base class
     */
    public WindowControllerTag(Class<?> defaultClass, Class<?> baseClass)
    {
        super(defaultClass, baseClass);
    }

    /**
     * Processes and initializes the controller bean and passes it to an
     * enclosing window tag. Here dependency injection is prepared and performed,
     * and the controller tag is further processed if necessary.
     *
     * @param bean the controller bean to initialize
     * @return a flag whether the bean could be passed to a target
     * @throws JellyTagException if an error occurs
     */
    @Override
    protected boolean passResults(Object bean) throws JellyTagException
    {
        super.passResults(bean);
        registerController(bean, fetchWindow());
        WindowBaseTag parent = (WindowBaseTag) findAncestorWithClass(WindowBaseTag.class);
        if (parent != null)
        {
            parent.setController(bean);
            return true;
        }
        return false;
    }

    /**
     * Checks the interfaces implemented by the controller bean and performs
     * necessary registrations.
     *
     * @param controller the controller bean
     * @param window the controller's window
     * @throws JellyTagException if an error occurs
     */
    protected void registerController(Object controller, Window window)
            throws JellyTagException
    {
        if (controller instanceof WindowListener)
        {
            window.addWindowListener((WindowListener) controller);
        }

        if (controller instanceof WindowClosingStrategy)
        {
            window.setWindowClosingStrategy((WindowClosingStrategy) controller);
        }
    }

    /**
     * Obtains the window for this controller. This implementation checks if the
     * tag is nested inside a window tag. If this is the case, the window is
     * obtained from this tag. Otherwise it is tried to fetch the window from
     * the window builder data object. If this fails, too, an exception will be
     * thrown.
     *
     * @return the window for this controller
     * @throws JellyTagException if the window cannot be obtained
     */
    protected Window fetchWindow() throws JellyTagException
    {
        Window result = null;

        WindowBaseTag parent = (WindowBaseTag) findAncestorWithClass(WindowBaseTag.class);
        if (parent != null)
        {
            result = parent.getWindow();
        }
        else
        {
            WindowBuilderData data = WindowBuilderData.get(getContext());
            if (data != null)
            {
                result = data.getResultWindow();
            }
        }

        if (result == null)
        {
            throw new JellyTagException("Window could not be obtained!");
        }
        return result;
    }
}
