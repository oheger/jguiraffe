/*
 * Copyright 2006-2012 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.components;

import java.util.Map;

import net.sf.jguiraffe.gui.builder.components.model.StaticTextData;
import net.sf.jguiraffe.gui.builder.components.tags.BorderLayoutTag;
import net.sf.jguiraffe.gui.builder.components.tags.ButtonLayoutTag;
import net.sf.jguiraffe.gui.builder.components.tags.ButtonTag;
import net.sf.jguiraffe.gui.builder.components.tags.CheckboxTag;
import net.sf.jguiraffe.gui.builder.components.tags.ComboBoxTag;
import net.sf.jguiraffe.gui.builder.components.tags.DesktopPanelTag;
import net.sf.jguiraffe.gui.builder.components.tags.FontTag;
import net.sf.jguiraffe.gui.builder.components.tags.LabelTag;
import net.sf.jguiraffe.gui.builder.components.tags.ListBoxTag;
import net.sf.jguiraffe.gui.builder.components.tags.PanelTag;
import net.sf.jguiraffe.gui.builder.components.tags.PasswordFieldTag;
import net.sf.jguiraffe.gui.builder.components.tags.PercentLayoutTag;
import net.sf.jguiraffe.gui.builder.components.tags.ProgressBarTag;
import net.sf.jguiraffe.gui.builder.components.tags.RadioButtonTag;
import net.sf.jguiraffe.gui.builder.components.tags.SliderTag;
import net.sf.jguiraffe.gui.builder.components.tags.SplitterTag;
import net.sf.jguiraffe.gui.builder.components.tags.StaticTextTag;
import net.sf.jguiraffe.gui.builder.components.tags.TabbedPaneTag;
import net.sf.jguiraffe.gui.builder.components.tags.TextAreaTag;
import net.sf.jguiraffe.gui.builder.components.tags.TextFieldTag;
import net.sf.jguiraffe.gui.builder.components.tags.ToggleButtonTag;
import net.sf.jguiraffe.gui.builder.components.tags.TreeTag;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableTag;
import net.sf.jguiraffe.gui.builder.event.PlatformEventManager;
import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.locators.Locator;

/**
 * <p>
 * Definition of an interface to a concrete GUI manager.
 * </p>
 * <p>
 * The form framework tries to be independent of a specific GUI technology like
 * Swing or SWT. Instead widgets are addressed in a generic way, as objects.
 * This interface defines accessor methods for such generic GUI objects.
 * Concrete implementation classes will map these methods onto an existing GUI
 * library.
 * </p>
 * <p>
 * The most important client for this interface is the form builder jelly tag
 * library. The tags represent generic form elements, e.g. labels, text fields
 * or radio buttons. Concrete implementations for these elements are created
 * through interface methods. This makes it possible to plug in different GUI
 * libraries. So one and the same jelly script could once create a Swing GUI and
 * another time an SWT GUI.
 * </p>
 * <p>
 * A large part of the methods defined in this interface deal with the creation
 * of GUI components. Those components are wrapped into a
 * {@link ComponentHandler} implementation, which makes it possible to add them
 * to a {@link net.sf.jguiraffe.gui.forms.Form Form} object and to access their
 * data. Some of these methods have a boolean argument <code>create</code>.
 * During the processing of the Jelly script that defines the GUI they are
 * called twice, once with the argument set to <b>true</b> and once with the
 * argument set to <b>false</b>. The first call comes before the tag's body is
 * evaluated, the second after that. This allows for different ways of
 * constructing component hierarchies (e.g. in Swing components are added to
 * containers using an <code>add()</code> of the container, SWT on the other
 * hand requires the container being passed into the component's constructor -
 * thus the container must have been created prior than the component.
 * </p>
 * <p>
 * Additional methods are responsible for creating auxiliary objects, like
 * icons, fonts or layout constraints, or for manipulating container objects,
 * which can contain other GUI elements.
 * </p>
 * <p>
 * <strong>Note:</strong> This interface is not intended to be directly
 * implemented by client code. It is subject to change even in minor releases as
 * new features are made available. Therefore if an application needs to provide
 * a custom implementation of this interface, it should extend an existing
 * implementation. For instance, the {@link ComponentManagerWrapper} class is a
 * good candidate if only a subset of methods is to be modified.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ComponentManager.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface ComponentManager
{
    /**
     * Adds a component to a container using the specified constraints. This
     * method is called to populate container objects.
     *
     * @param container the container
     * @param component the component which is to be added to the container
     * @param constraints a generic constraints object; this object must be
     * compatible with the layout manager set for the container; it may be
     * <b>null</b>
     */
    void addContainerComponent(Object container, Object component,
            Object constraints);

    /**
     * Defines the layout manager for a container.
     *
     * @param container the container
     * @param layout the new layout manager
     */
    void setContainerLayout(Object container, Object layout);

    /**
     * Returns a concrete implementation of the
     * <code>PlatformEventManager</code> interface for dealing with event
     * notifications. This method is called once when the event handling
     * features are used for the first time.
     *
     * @return the platform specific event manager
     */
    PlatformEventManager createEventManager();

    /**
     * Returns a <code>WidgetHandler</code> for the specified component. This
     * method is called by {@link ComponentBuilderData} when the user asks for a
     * handler to a certain component. The passed in object is one of the
     * components that was created during the last builder operation. An
     * implementation may throw a runtime exception if a widget handler for the
     * passed in component cannot be obtained.
     *
     * @param component the component
     * @return a <code>WidgetHandler</code> wrapping the specified component
     */
    WidgetHandler getWidgetHandlerFor(Object component);

    /**
     * Creates a label object. All needed properties are extracted from the
     * passed in tag.
     *
     * @param tag the label tag
     * @param create the create flag
     * @return the new label
     * @throws FormBuilderException if an error occurs
     */
    Object createLabel(LabelTag tag, boolean create)
            throws FormBuilderException;

    /**
     * Associates a link with another component. This method will be called if
     * the <code>componentref</code> attribute of a <code>LabelTag</code>
     * was used to define a link between a lable and another component. An
     * implementation will have to use library specific methods to establish
     * such a connection. Eventually the label's text has to be set to the given
     * text (this is the case if the label is undefined; its text is then
     * obtained from the component's display name).
     *
     * @param label the label (as returned by the <code>createLabel()</code>
     * method
     * @param component the component to be linked to the label
     * @param text an optional text to be set for the label; can be <b>null</b>,
     * then no text has to be set
     * @throws FormBuilderException if an error occurs
     */
    void linkLabel(Object label, Object component, String text)
            throws FormBuilderException;

    /**
     * Creates an icon object with the data obtained from the specified
     * <code>Locator</code>
     *
     * @param locator the <code>Locator</code> pointing to the data
     * @return the new icon object
     * @throws FormBuilderException if an error occurs
     */
    Object createIcon(Locator locator) throws FormBuilderException;

    /**
     * Creates a font object from the data specified by the given {@code
     * FontTag}.
     *
     * @param tag the {@code FontTag}
     * @return the newly created font
     * @throws FormBuilderException if an error occurs
     */
    Object createFont(FontTag tag) throws FormBuilderException;

    /**
     * Creates a percent layout object whose parameters are obtained from the
     * specified tag.
     *
     * @param tag the percent layout tag
     * @return the new layout object
     * @throws FormBuilderException if an error occurs
     */
    Object createPercentLayout(PercentLayoutTag tag)
            throws FormBuilderException;

    /**
     * Creates a button layout object whose parameters are obtained from the
     * specified tag.
     *
     * @param tag the button layout tag
     * @return the new layout object
     * @throws FormBuilderException if an error occurs
     */
    Object createButtonLayout(ButtonLayoutTag tag) throws FormBuilderException;

    /**
     * Creates a border layout object whose parameters are obtained form the
     * specified tag.
     *
     * @param tag the border layout tag
     * @return the new layout object
     * @throws FormBuilderException if an error occurs
     */
    Object createBorderLayout(BorderLayoutTag tag) throws FormBuilderException;

    /**
     * Creates a panel whose parameters are obtained from the specified tag.
     *
     * @param tag the panel tag
     * @param create the create flag
     * @return the new panel object
     * @throws FormBuilderException if an error occurs
     */
    Object createPanel(PanelTag tag, boolean create)
            throws FormBuilderException;

    /**
     * Creates a desktop panel whose parameters are obtained from the specified
     * tag. The desktop panel can be used as basic background for MDI child
     * windows.
     *
     * @param tag the desktop panel tag
     * @param create the create flag
     * @return the new desktop panel object
     * @throws FormBuilderException if an error occurs
     */
    Object createDesktopPanel(DesktopPanelTag tag, boolean create)
            throws FormBuilderException;

    /**
     * Creates a splitter whose parameters are obtained from the specified tag.
     *
     * @param tag the splitter tag
     * @param create the create flag
     * @return the new splitter component
     * @throws FormBuilderException if an error occurs
     */
    Object createSplitter(SplitterTag tag, boolean create)
            throws FormBuilderException;

    /**
     * Creates a radio group which contains the radio buttons stored in the
     * passed in map. Radio groups are treated as non visual components that
     * merely implement the radio logic. They will not be inserted into a GUI
     * container. The passed in map contains the names of the radio buttons as
     * keys and the corresponding component objects as values.
     *
     * @param radioButtons a map with the radio button components that belong to
     *        the group
     * @return the new radio group
     * @throws FormBuilderException if an error occurs
     */
    Object createRadioGroup(Map<String, Object> radioButtons)
            throws FormBuilderException;

    /**
     * Creates a button component. All needed properties are extracted from the
     * passed in tag. Buttons (or to be more precise: command buttons) are
     * considered as input components though they do not gather user input. The
     * reason for this is that the event mechanism is coupled to
     * <code>ComponentHandler</code> instances, so for a button to generate
     * events there must be an associated component handler. Note that the
     * returned component handler is usually not added to the generated
     * <code>Form</code> object. If the button really should be used as input
     * component, its data is a boolean value indicating whether the button is
     * selected (which makes sense if the button is used as a toggle button).
     *
     * @param tag the button tag
     * @param create the create flag
     * @return the component handler for the new button
     * @throws FormBuilderException if an error occurs
     */
    ComponentHandler<Boolean> createButton(ButtonTag tag, boolean create)
            throws FormBuilderException;

    /**
     * Creates a toggle button component. A toggle button is a simple switch
     * that can be selected (pressed) or not. So the data associated with
     * components of this type is simply a boolean.
     *
     * @param tag the tag defining the toggle button
     * @param create the create flag
     * @return the component handler for the new toggle button
     * @throws FormBuilderException if an error occurs
     */
    ComponentHandler<Boolean> createToggleButton(ToggleButtonTag tag, boolean create)
            throws FormBuilderException;

    /**
     * Creates a component handler that wraps a text field. The data type of
     * this handler is String.
     *
     * @param tag the tag defining the text field
     * @param create the create flag
     * @return the component handler for the text field
     * @throws FormBuilderException if an error occurs
     */
    ComponentHandler<String> createTextField(TextFieldTag tag, boolean create)
            throws FormBuilderException;

    /**
     * Creates a component handler that wraps a text area. The data type of this
     * handler is String.
     *
     * @param tag the tag defining the text area
     * @param create the create flag
     * @return the component handler for the text area
     * @throws FormBuilderException if an error occurs
     */
    ComponentHandler<String> createTextArea(TextAreaTag tag, boolean create)
            throws FormBuilderException;

    /**
     * Creates a {@code ComponentHandler} that wraps a password text field. This
     * handler acts like a regular handler for text input fields. Only the
     * visual representation is different because the characters typed by the
     * user in the text field are not readable.
     *
     * @param tag the tag defining the password text field
     * @param create the create flag
     * @return the {@code ComponentHandler} for the password text field
     * @throws FormBuilderException if an error occurs
     */
    ComponentHandler<String> createPasswordField(PasswordFieldTag tag,
            boolean create) throws FormBuilderException;

    /**
     * Creates a component handler that wraps a checkbox. This handler's data is
     * of type boolean.
     *
     * @param tag the tag defining the checkbox
     * @param create the create flag
     * @return the component handler for the checkbox
     * @throws FormBuilderException if an error occurs
     */
    ComponentHandler<Boolean> createCheckbox(CheckboxTag tag, boolean create)
            throws FormBuilderException;

    /**
     * Creates a component handler that wraps a radio button. This handler's
     * data is of type boolean.
     *
     * @param tag the tag defining the radio button
     * @param create the create flag
     * @return the component handler for the radio button
     * @throws FormBuilderException if an error occurs
     */
    ComponentHandler<Boolean> createRadioButton(RadioButtonTag tag, boolean create)
            throws FormBuilderException;

    /**
     * Creates a component handler that wraps a combo box. This handler
     * maintains a single data object of the same type as the combo box's list
     * model.
     *
     * @param tag the tag defining the combo box
     * @param create the create flag
     * @return the component handler for the combo box
     * @throws FormBuilderException if an error occurs
     */
    ComponentHandler<Object> createComboBox(ComboBoxTag tag, boolean create)
            throws FormBuilderException;

    /**
     * Creates a component handler that wraps a list box. This handler's data
     * type is based on the type of the list's model and on the list's multi
     * selection flag: for a single selection list the type is the same as the
     * list's model's type, for a multi selection list it is an array of this
     * type.
     *
     * @param tag the tag defining the list box
     * @param create the create flag
     * @return the component handler for the list box
     * @throws FormBuilderException if an error occurs
     */
    ComponentHandler<Object> createListBox(ListBoxTag tag, boolean create)
            throws FormBuilderException;

    /**
     * Creates a component handler that wraps a tabbed pane. The handler's data
     * consists of a single <code>Integer</code> object, which represents the
     * (0-based) index of the selected tab. It can be read and set.
     *
     * @param tag the tag defining the tabbed pane
     * @param create the create flag
     * @return the component handler for the tabbed pane
     * @throws FormBuilderException if an error occurs
     */
    ComponentHandler<Integer> createTabbedPane(TabbedPaneTag tag, boolean create)
            throws FormBuilderException;

    /**
     * Creates a component handler that wraps a static text element. The
     * handler's data consists of a
     * <code>{@link net.sf.jguiraffe.gui.builder.components.model.StaticTextData
     * StaticTextData}</code>
     * object, which can be used to read and write the static text's properties.
     * The returned handler can be casted into a
     * <code>{@link net.sf.jguiraffe.gui.builder.components.model.StaticTextHandler}</code>
     * object; the additional methods defined by this interface can be used to
     * directly set the key properties of the static text component.
     *
     * @param tag the tag defining the static text
     * @param create the create flag
     * @return the component handler for the static text
     * @throws FormBuilderException if an error occurs
     */
    ComponentHandler<StaticTextData> createStaticText(StaticTextTag tag, boolean create)
            throws FormBuilderException;

    /**
     * Creates a component handler that wraps a progress bar element. The
     * handler's data is an integer value representing the current position of
     * the progress bar. The handler can be casted into a
     * {@link net.sf.jguiraffe.gui.builder.components.model.ProgressBarHandler}
     * object; the additional methods defined by this interface can be used to
     * manipulate further properties of the progress bar.
     *
     * @param tag the tag defining the progress bar
     * @param create the create flag
     * @return the component handler for the progress bar
     * @throws FormBuilderException if an error occurs
     */
    ComponentHandler<Integer> createProgressBar(ProgressBarTag tag,
            boolean create) throws FormBuilderException;

    /**
     * Creates a component handler that wraps a slider component. The handler's
     * data is an integer value representing the current value of the slider.
     *
     * @param tag the tag defining the slider
     * @param create the create flag
     * @return the component handler for the slider
     * @throws FormBuilderException if an error occurs
     */
    ComponentHandler<Integer> createSlider(SliderTag tag, boolean create)
            throws FormBuilderException;

    /**
     * Creates a table based on the information stored in the passed in table
     * tag. This is a complex operation because it has to be ensured that the
     * table is fully initialized. It is guaranteed that the tag contains only
     * valid data. The table's data model, its columns, and form objects to be
     * used for rendering or editing cells are available. The returned
     * <code>ComponentHandler</code> object wraps the table component. It can
     * be casted into a
     * <code>{@link net.sf.jguiraffe.gui.builder.components.model.TableHandler TableHandler}</code>
     * object. Its data depends on the selection type of the table: in single
     * selection mode it is the index of the selected row; in multi selection
     * mode it is an int[] with the indices of the selected rows. By registering
     * a change listener at the returned <code>ComponentHandler</code>
     * interested components can be notified when the table's selection changes.
     *
     * @param tag the table tag
     * @param create the create flag
     * @return the component handler for the new table component
     * @throws FormBuilderException if an error occurs
     */
    ComponentHandler<Object> createTable(TableTag tag, boolean create)
            throws FormBuilderException;

    /**
     * Creates a tree component based on the information stored in the passed in
     * tree tag. The tag has been fully initialized, it especially contains the
     * model to be used for the tree. The <code>ComponentHandler</code> returned
     * by this method can be casted into a
     * <code>{@link net.sf.jguiraffe.gui.builder.components.model.TreeHandler TreeHandler}</code>
     * object. Refer to the documentation of this class for more information
     * about the data supported by this handler.
     *
     * @param tag the tree tag
     * @param create the create flag
     * @return the component handler for the new tree component
     * @throws FormBuilderException if an error occurs
     */
    ComponentHandler<Object> createTree(TreeTag tag, boolean create)
            throws FormBuilderException;
}
