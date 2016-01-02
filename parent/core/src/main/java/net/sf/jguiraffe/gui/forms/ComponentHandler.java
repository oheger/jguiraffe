/*
 * Copyright 2006-2016 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.forms;

/**
 * <p>
 * Definition of an interface for accessing GUI components independently from
 * their type.
 * </p>
 * <p>
 * This interface represents a Java GUI widget like a text field or a checkbox.
 * There will be concrete implementations for real components, which implement
 * data exchange.
 * </p>
 * <p>
 * The form framework uses implementations of this interface to transfer data
 * from and to GUI components. The details of this data transfer are hidden by
 * concrete implementations. This makes it possible for instance to read a
 * complete form and store the entered data in a bean.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ComponentHandler.java 205 2012-01-29 18:29:57Z oheger $
 * @param <T> the data type used by this component handler
 */
public interface ComponentHandler<T>
{
    /**
     * Returns a reference to the real component that is wrapped by this
     * component handler.
     *
     * @return the underlying component
     */
    Object getComponent();

    /**
     * Returns the outer most component, which is the component to be added to
     * the enclosing container. It may sometimes be necessary that for one GUI
     * widget not a single component can be created, but multiple ones are
     * necessary. An example would be a text area in Swing, which is comprised
     * of the Swing text area itself plus a scroll pane object. In this example
     * the outer component would be the scroll pane, the component (returned by
     * <code>{@link #getComponent()}</code> would be the text area. The
     * mechanism with the outer component allows a GUI library specific
     * implementation to construct composite components for certain complex
     * widgets, but from the client's view they behave like a single one.
     *
     * @return the outer component
     */
    Object getOuterComponent();

    /**
     * Returns the data of the wrapped component. This is the data the user has
     * entered, e.g. text.
     *
     * @return the component's data
     */
    T getData();

    /**
     * Sets the data of the wrapped component. This method can be used to
     * initialize GUI widgets, e.g. to set default text at start up.
     *
     * @param data the data to set
     */
    void setData(T data);

    /**
     * Returns the data type used by this component. The <code>Class</code>
     * object returned here determines, which type is allowed for the
     * <code>getData()</code> and <code>setData()</code> methods. It depends
     * on the concrete GUI component. For text fields it will be a string, for
     * checkboxes probably a boolean etc.
     *
     * @return the data type used by this component
     */
    Class<?> getType();

    /**
     * Returns a flag whether this component is enabled.
     *
     * @return the enabled flag
     */
    boolean isEnabled();

    /**
     * Allows to set the enabled flag. A disabled component cannot be focused
     * and does not accept user input.
     *
     * @param f the value of the enabled flag
     */
    void setEnabled(boolean f);
}
