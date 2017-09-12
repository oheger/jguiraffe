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
package net.sf.jguiraffe.gui.builder.action;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.tags.TextIconData;
import net.sf.jguiraffe.gui.forms.ComponentHandler;

/**
 * <p>
 * Definition of an interface for a component that creates GUI library specific
 * action objects and related classes.
 * </p>
 * <p>
 * This interface plays a similar role for the action builder as the
 * {@link net.sf.jguiraffe.gui.forms.components.ComponentManager ComponentManager}
 * interface for the form builder: It hides the details of creating GUI library
 * specific objects. Instead of form elements this interface deals with objects
 * like actions, menus, and toolbar buttons.
 * </p>
 * <p>
 * There will be concrete implementations of this interface for all supported
 * GUI libraries. When the action builder is invoked, an implementation must be
 * provided. This object is then accessed by Jelly tag handler classes to create
 * the objects they represent. With the objects created by this implementation
 * the action related components of an application can be constructed.
 * </p>
 * <p>
 * <strong>Note:</strong> This interface is not intended to be directly
 * implemented by client code. It is subject to change even in minor releases as
 * new features are made available. Therefore if an application needs to provide
 * a custom implementation of this interface, it should extend an existing
 * implementation. For instance, the {@link ActionManagerWrapper} class is a
 * good candidate if only a subset of methods is to be modified.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ActionManager.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface ActionManager
{
    /**
     * Creates an action object based on the provided information.
     *
     * @param actionBuilder the central builder data object
     * @param actionData an object with all information about the action to
     * create
     * @return the new action object
     * @throws FormActionException if an error occurs
     */
    FormAction createAction(ActionBuilder actionBuilder, ActionData actionData)
            throws FormActionException;

    /**
     * Creates a menu item based on the specified action object. The menu item
     * will be associated with the given action. Its properties (e.g. text and
     * icon) will be obtained from the action, too.
     *
     * @param actionBuilder the action builder
     * @param action the action
     * @param checked a flag if a checked menu item should be created
     * @param parent the parent menu to which the new item should be added
     * @return the new menu item
     * @throws FormActionException if an error occurs
     */
    Object createMenuItem(ActionBuilder actionBuilder, FormAction action,
            boolean checked, Object parent) throws FormActionException;

    /**
     * Creates a menu item based on the specified action data object and returns
     * a component handler for it. The data of this component handler will be a
     * boolean value representing the checked state for checked menu items. For
     * other menu items it is undefined.
     *
     * @param actionBuilder the action builder
     * @param actionData an object with all information about the menu item
     * @param checked a flag if a checked menu item should be created
     * @return a component handler for the new menu item
     * @param parent the parent menu to which the new item should be added
     * @throws FormActionException if an error occurs
     */
    ComponentHandler<?> createMenuItem(ActionBuilder actionBuilder,
            ActionData actionData, boolean checked, Object parent)
            throws FormActionException;

    /**
     * Creates a menu bar. Later defined (sub) menus will be added to this bar.
     *
     * @param actionBuilder the action builder
     * @return the new menu bar
     * @throws FormActionException if an error occurs
     */
    Object createMenuBar(ActionBuilder actionBuilder)
            throws FormActionException;

    /**
     * Creates a (sub) menu. The new menu will be added to the specified parent
     * menu, which can be either a menu bar or another menu. This method will be
     * called twice for each menu to be created. In the first call not all
     * initialization properties might be available (e.g. the icon), but the
     * menu must be created nevertheless (to allow for the menu items being
     * added). In the second call missing initialization can be performed. An
     * implementation should only create an uninitialized menu on the first
     * call. On the second call it should initialize the menu's properties.
     *
     * @param actionBuilder the action builder
     * @param menu the menu object; this will be <b>null</b> on the first call;
     * on the second call the object returned by the first call will be passed
     * @param data data defining the new menu
     * @param parent the parent menu
     * @return the new menu
     * @throws FormActionException if an error occurs
     */
    Object createMenu(ActionBuilder actionBuilder, Object menu,
            TextIconData data, Object parent) throws FormActionException;

    /**
     * Creates a toolbar object. Later defined toolbar buttons will be added to
     * this bar.
     *
     * @param actionBuilder the action builder
     * @return the new toolbar object
     * @throws FormActionException if an error occurs
     */
    Object createToolbar(ActionBuilder actionBuilder)
            throws FormActionException;

    /**
     * Creates a toolbar button based on the specified action object. The button
     * will be associated with this action and obtain its properties from there.
     *
     * @param actionBuilder the action builder
     * @param action the action
     * @param checked a flag if a checked (toggle) button should be created
     * @param parent the parent component (a toolbar) to which the new button
     * should be added
     * @return the new button
     * @throws FormActionException if an error occurs
     */
    Object createToolbarButton(ActionBuilder actionBuilder, FormAction action,
            boolean checked, Object parent) throws FormActionException;

    /**
     * Creates a toolbar button based on the given action data object and
     * returns a component handler for it. The data of this component handler
     * will be a boolean value representing the checked state for checked
     * toolbar buttons. For other toolbar buttons it is undefined.
     *
     * @param actionBuilder the action builder
     * @param data a data object defining all properties of the button
     * @param checked a flag if a checked (toggle) button should be created
     * @param parent the parent component (a toolbar) to which the new button
     * should be added
     * @return a component handler for the new button
     * @throws FormActionException if an error occurs
     */
    ComponentHandler<?> createToolbarButton(ActionBuilder actionBuilder,
            ActionData data, boolean checked, Object parent)
            throws FormActionException;

    /**
     * Adds a separator to the specified menu. The passed in menu object must
     * have been created using the <code>createMenu()</code> method.
     *
     * @param actionBuilder the action builder
     * @param menu the menu to which the separator should be added
     * @throws FormActionException if an error occurs
     */
    void addMenuSeparator(ActionBuilder actionBuilder, Object menu)
            throws FormActionException;

    /**
     * Adds a separator to the specified tool bar. The passed in tool bar object
     * must have been created using the <code>createToolBar()</code> method.
     *
     * @param actionBuilder the action builder
     * @param toolBar the tool bar to which the separator should be added
     * @throws FormActionException if an error occurs
     */
    void addToolBarSeparator(ActionBuilder actionBuilder, Object toolBar)
            throws FormActionException;

    /**
     * Registers the specified <code>PopupMenuHandler</code> at the given UI
     * component. This will cause the handler to be invoked whenever the user
     * triggers the context menu for this component (e.g. by right clicking it
     * with the mouse). A concrete implementation has to install a proper event
     * listener at the component that takes care of calling the handler when it
     * detects a gesture that should bring up the context menu.
     *
     * @param component the component
     * @param handler the handler for creating the menu
     * @param compData the <code>ComponentBuilderData</code> object
     * @throws FormActionException if an error occurs
     */
    void registerPopupMenuHandler(Object component, PopupMenuHandler handler,
            ComponentBuilderData compData) throws FormActionException;
}
