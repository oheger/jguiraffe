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
package net.sf.jguiraffe.gui.builder.action;

import java.io.IOException;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.Container;
import net.sf.jguiraffe.gui.builder.components.tags.TextIconData;
import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.forms.ComponentHandlerImpl;

/**
 * <p>
 * A test implementation of the <code>ActionManager</code> interface.
 * </p>
 * <p>
 * This implementation returns no functional objects wherever possible, but
 * simple strings allowing test cases to exactly compare the results of method
 * calls with expected values.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ActionManagerImpl.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ActionManagerImpl implements ActionManager
{
    /** Constant for the separator text. */
    public static final String SEPARATOR = "<SEPARATOR>";

    /** Constant for the popup menu handler element. */
    public static final String POPUP_HANDLER = "<popupHandler compData=\"%s\">%s</popupHandler>";

    /** Constant for the name of a menu item component. */
    static final String COMP_MENUITEM = "MENUITEM";

    /** Constant for the name of a toolbar button component. */
    static final String COMP_TOOLBUTTON = "TOOLBUTTON";

    /**
     * Creates an action object. This implementation returns a
     * <code>FormActionImpl</code> instance whose <code>data</code> property
     * is set to a textual representation of the passed in parameters.
     *
     * @param actionBuilder the builder
     * @param actionData parameters for the new action
     * @return the new action
     * @throws FormActionException if an error occurs
     */
    public FormAction createAction(ActionBuilder actionBuilder,
            ActionData actionData) throws FormActionException
    {
        FormActionImpl action = new FormActionImpl(actionData.getName());
        action.setData(dumpActionData(actionData));
        return action;
    }

    /**
     * Creates a menu item based on the given action.
     *
     * @param actionBuilder the builder
     * @param action the action
     * @param checked the checked flag
     * @param parent the parent menu to which the new item should be added
     * @return a string representing the new menu item
     * @throws FormActionException if an error occurs
     */
    public Object createMenuItem(ActionBuilder actionBuilder,
            FormAction action, boolean checked, Object parent)
            throws FormActionException
    {
        return createActionComponent(COMP_MENUITEM, action, checked, parent);
    }

    /**
     * Creates a menu item based on the given action data. The return value will
     * be a ComponentHandlerImpl object with a string representing the menu item
     * as component.
     *
     * @param actionBuilder the builder
     * @param actionData the action data
     * @param checked the checked flag
     * @param parent the parent menu to which the new item should be added
     * @return a component handler for the new menu item
     * @throws FormActionException if an error occurs
     */
    public ComponentHandler<?> createMenuItem(ActionBuilder actionBuilder,
            ActionData actionData, boolean checked, Object parent)
            throws FormActionException
    {
        return createActionComponentHandler(COMP_MENUITEM, actionData, checked,
                parent);
    }

    /**
     * Creates a menu bar. This implementation returns a Container object, to
     * which later new menu containers can be added.
     *
     * @param actionBuilder the action builder
     * @throws FormActionException if an error occurs
     */
    public Object createMenuBar(ActionBuilder actionBuilder)
            throws FormActionException
    {
        return new Container("MENUBAR");
    }

    /**
     * Creates a new (sub) menu.
     *
     * @param actionBuilder the action builder
     * @param data the data defining the menu
     * @param parent the parent menu
     * @return the new menu
     * @throws FormActionException if an error occurs
     */
    public Object createMenu(ActionBuilder actionBuilder, Object menu,
            TextIconData data, Object parent) throws FormActionException
    {
        if (menu == null)
        {
            menu = new Container("MENU");
        }
        else
        {
            StringBuilder buf = new StringBuilder();
            appendAttr(buf, "TEXT", data.getCaption());
            appendAttr(buf, "ICON", data.getIcon());
            if (data.getMnemonic() != 0)
            {
                appendAttr(buf, "MNEMO", new Character((char) data
                        .getMnemonic()));
            }
            ((Container) menu).setAttributes(buf.toString());
            ((Container) parent).addComponent(menu, null);
        }
        return menu;
    }

    /**
     * Adds a separator to a menu. This implementation expects the passed in
     * menu object to be a Container. The separator is simply a text that is
     * added to the container's text.
     *
     * @param actionBuilder the action builder
     * @param menu the menu
     * @throws FormActionException if an error occurs
     */
    public void addMenuSeparator(ActionBuilder actionBuilder, Object menu)
            throws FormActionException
    {
        addSeparator(menu);
    }

    /**
     * Adds a separator to a tool bar. This is basically the same as
     * <code>addMenuSeparator()</code>.
     *
     * @param actionBuilder the action builder
     * @param toolBar the tool bar
     * @throws FormActionException if an error occurs
     */
    public void addToolBarSeparator(ActionBuilder actionBuilder, Object toolBar)
            throws FormActionException
    {
        addSeparator(toolBar);
    }

    /**
     * Creates a toolbar. This implementation returns a Container object.
     *
     * @param actionBuilder the action builder
     * @return the toolbar
     * @throws FormActionException in case of an error
     */
    public Object createToolbar(ActionBuilder actionBuilder)
            throws FormActionException
    {
        return new Container("TOOLBAR");
    }

    /**
     * Creates a toolbar button based on an action.
     *
     * @param actionBuilder the action builder
     * @param action the action
     * @param checked the checked flag
     * @param parent the parent (a Container object)
     * @return the new button
     * @throws FormActionException if an error occurs
     */
    public Object createToolbarButton(ActionBuilder actionBuilder,
            FormAction action, boolean checked, Object parent)
            throws FormActionException
    {
        return createActionComponent(COMP_TOOLBUTTON, action, checked, parent);
    }

    /**
     * Creates a component handler for a toolbar button.
     *
     * @param actionBuilder the action builder
     * @param data the action data object
     * @param checked the checked flag
     * @param parent the parent (a Container object)
     * @return the component handler for the new button
     * @throws FormActionException if an error occurs
     */
    public ComponentHandler<?> createToolbarButton(ActionBuilder actionBuilder,
            ActionData data, boolean checked, Object parent)
            throws FormActionException
    {
        return createActionComponentHandler(COMP_TOOLBUTTON, data, checked,
                parent);
    }

    /**
     * Registers a handler for a context menu at a component. This
     * implementation checks whether the component is a Container. If this is
     * the case, a text will be added to the container. Otherwise no action is
     * performed.
     *
     * @param component the component
     * @param handler the handler
     * @param compData the component builder data object
     * @throws FormActionException if an error occurs
     */
    public void registerPopupMenuHandler(Object component,
            PopupMenuHandler handler, ComponentBuilderData compData)
            throws FormActionException
    {
        String text = popupHandlerText(handler, compData);
        if (component instanceof Container)
        {
            ((Container) component).addComponent(text, null);
        }
        else if (component instanceof Appendable)
        {
            try
            {
                ((Appendable) component).append(text);
            }
            catch (IOException e)
            {
                throw new FormActionException(e);
            }
        }
    }

    /**
     * Builds a string referring to the specified handler object.
     *
     * @param handler the handler
     * @param compData the component builder data object
     * @return a string referring to this handler
     */
    public static String popupHandlerText(Object handler,
            ComponentBuilderData compData)
    {
        return String.format(POPUP_HANDLER, String.valueOf(compData), String
                .valueOf(handler));
    }

    /**
     * Helper method for creating a string representation for an action data
     * object.
     *
     * @param actionData the action data object
     * @return a string for this object
     */
    private static String dumpActionData(ActionData actionData)
    {
        StringBuilder buf = new StringBuilder();
        appendAttr(buf, "TEXT", actionData.getText());
        appendAttr(buf, "ICON", actionData.getIcon());
        appendAttr(buf, "TOOLTIP", actionData.getToolTip());
        if (actionData.getMnemonicKey() > 0)
        {
            appendAttr(buf, "MNEMO", new Character((char) actionData
                    .getMnemonicKey()));
        }
        appendAttr(buf, "ACC", actionData.getAccelerator());
        if (actionData.getTask() != null)
        {
            appendAttr(buf, "TASK", actionData.getTask().getClass());
        }
        return buf.toString();
    }

    /**
     * Helper method for appending an attribute if it is defined.
     *
     * @param buf the buffer
     * @param attr the attribute's name
     * @param val the attribute's value
     */
    private static void appendAttr(StringBuilder buf, String attr, Object val)
    {
        if (val != null)
        {
            buf.append(' ').append(attr).append(" = ").append(val);
        }
    }

    /**
     * Helper method for adding a checked attribute to a component text.
     *
     * @param buf the target buffer
     * @param checked the attribute's value
     */
    private static void appendCheckedAttr(StringBuilder buf, boolean checked)
    {
        if (checked)
        {
            buf.append(" CHECKED");
        }
    }

    /**
     * Adds a separator to a container.
     *
     * @param container the container
     */
    private static void addSeparator(Object container)
    {
        ((Container) container).addComponent(SEPARATOR, null);
    }

    /**
     * Helper method for creating a component handler for an action control.
     *
     * @param compName the name of the component to create
     * @param data the action data object
     * @param checked the checked flag
     * @param parent the parent component
     * @return the new component handler
     */
    private static ComponentHandler<?> createActionComponentHandler(
            String compName, ActionData data, boolean checked, Object parent)
    {
        ComponentHandlerImpl handler = new ComponentHandlerImpl();
        StringBuilder buf = new StringBuilder(compName);
        buf.append(" [");
        buf.append(dumpActionData(data));
        appendCheckedAttr(buf, checked);
        buf.append(" ]");
        handler.setComponent(buf.toString());
        ((Container) parent).addComponent(handler.getComponent(), null);
        return handler;
    }

    /**
     * Helper method for creating an action component based on an action.
     *
     * @param compName the name of the component to create
     * @param action the action
     * @param checked the checked flag
     * @param parent the parent component
     * @return the new component
     */
    private static Object createActionComponent(String compName,
            FormAction action, boolean checked, Object parent)
    {
        StringBuilder buf = new StringBuilder(compName);
        buf.append(" [");
        appendAttr(buf, "ACTION", action);
        appendCheckedAttr(buf, checked);
        buf.append(" ]");
        ((Container) parent).addComponent(buf.toString(), null);
        return buf.toString();
    }
}
