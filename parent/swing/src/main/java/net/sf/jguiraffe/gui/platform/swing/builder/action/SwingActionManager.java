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
package net.sf.jguiraffe.gui.platform.swing.builder.action;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import java.awt.Component;
import java.util.Set;

import net.sf.jguiraffe.gui.builder.action.Accelerator;
import net.sf.jguiraffe.gui.builder.action.ActionBuilder;
import net.sf.jguiraffe.gui.builder.action.ActionData;
import net.sf.jguiraffe.gui.builder.action.ActionManager;
import net.sf.jguiraffe.gui.builder.action.FormAction;
import net.sf.jguiraffe.gui.builder.action.FormActionException;
import net.sf.jguiraffe.gui.builder.action.PopupMenuHandler;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.tags.TextIconData;
import net.sf.jguiraffe.gui.builder.event.Modifiers;
import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.platform.swing.builder.components.SwingButtonHandler;
import net.sf.jguiraffe.gui.platform.swing.builder.event.SwingEventConstantMapper;

/**
 * <p>
 * The Swing specific implementation of the {@code ActionManager}
 * interface.
 * </p>
 * <p>
 * This class implements the {@code ActionManager} methods in a way that
 * correctly initialized Swing objects (like Actions and JMenus) are created.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingActionManager.java 205 2012-01-29 18:29:57Z oheger $
 */
public class SwingActionManager implements ActionManager
{
    /**
     * Creates an action object. The returned object will also implement Swing's
     * {@code Action} interface.
     *
     * @param actionBuilder the action builder
     * @param actionData the properties of the new action
     * @return the newly created action
     * @throws FormActionException if an error occurs
     */
    public FormAction createAction(ActionBuilder actionBuilder,
            ActionData actionData) throws FormActionException
    {
        SwingFormAction action = new SwingFormAction(actionData.getName(),
                actionData.getTask());
        initActionProperty(action, Action.NAME, actionData.getText());
        initActionProperty(action, Action.SHORT_DESCRIPTION, actionData
                .getToolTip());
        initActionProperty(action, Action.SMALL_ICON, actionData.getIcon());
        if (actionData.getMnemonicKey() > 0)
        {
            initActionProperty(action, Action.MNEMONIC_KEY, Integer.valueOf(
                    actionData.getMnemonicKey()));
        }
        initActionProperty(action, Action.ACCELERATOR_KEY,
                keyStrokeFromAccelerator(actionData.getAccelerator()));
        return action;
    }

    /**
     * Creates a menu item based on the given action. Depending on the
     * {@code checked} argument either a {@code JMenuItem} or a
     * {@code JCheckBoxMenuItem} object will be returned.
     *
     * @param actionBuilder the action builder
     * @param formAction the action to associate with the menu item; this object
     * must implement Swing's {@code Action} interface
     * @param checked the checked flag
     * @param parent the parent menu; this must be an instance of
     * {@code JMenu}
     * @return the new menu item
     * @throws FormActionException if an error occurs
     */
    public Object createMenuItem(ActionBuilder actionBuilder,
            FormAction formAction, boolean checked, Object parent)
            throws FormActionException
    {
        Action action = (Action) formAction;
        JMenuItem item = checked ? new JCheckBoxMenuItem(action)
                : new JMenuItem(action);
        if (!actionBuilder.isMenuIcon())
        {
            // explicitly set the icon to null
            item.setIcon(null);
        }

        addToMenu(parent, item);
        return item;
    }

    /**
     * Creates a menu item based on the passed in data object. Depending on the
     * {@code checked} argument either a {@code JMenuItem} or a
     * {@code JCheckBoxMenuItem} object will be returned.
     *
     * @param actionBuilder the action builder
     * @param actionData the action data object
     * @param checked the checked flag
     * @param parent the parent menu; this must be an instance of
     * {@code JMenu}
     * @return the new menu item
     * @throws FormActionException if an error occurs
     *
     */
    public ComponentHandler<?> createMenuItem(ActionBuilder actionBuilder,
            ActionData actionData, boolean checked, Object parent)
            throws FormActionException
    {
        JMenuItem item = checked ? new JCheckBoxMenuItem() : new JMenuItem();
        initFromActionData(item, actionData);
        item.setAccelerator(keyStrokeFromAccelerator(actionData.getAccelerator()));
        addToMenu(parent, item);
        return new SwingButtonHandler(item);
    }

    /**
     * Creates a menu bar. This implementation will return a new
     * {@code JMenuBar} object.
     *
     * @param actionBuilder the action builder
     * @return the new menu bar
     * @throws FormActionException if an error occurs
     */
    public Object createMenuBar(ActionBuilder actionBuilder)
            throws FormActionException
    {
        return new JMenuBar();
    }

    /**
     * Creates a menu based on the given data. The return value will be a
     * {@code JMenu} object.
     *
     * @param actionBuilder the action builder
     * @param menu the menu object (<b>null</b> in the creation phase, a not
     * <b>null</b> {@code JMenu} instance in the initialization phase)
     * @param data the data for the new menu
     * @param parent the menu's parent (a menu bar or a menu)
     * @return the new menu
     * @throws FormActionException if an error occurs, i.e. if the parent is
     * undefined
     */
    public Object createMenu(ActionBuilder actionBuilder, Object menu,
            TextIconData data, Object parent) throws FormActionException
    {
        if (menu == null)
        {
            // Creation phase
            return new JMenu();
        }

        else
        {
            // Initialization phase
            JMenu men = (JMenu) menu;
            men.setText(data.getCaption());
            men.setIcon((Icon) data.getIcon());
            men.setMnemonic(data.getMnemonic());

            if (parent != null)
            {
                if (parent instanceof JMenuBar)
                {
                    ((JMenuBar) parent).add(men);
                }
                else
                {
                    addToMenu(parent, men);
                }
            }
            else
            {
                throw new FormActionException("A parent must be provided!");
            }

            return men;
        }
    }

    /**
     * Creates a tool bar object. This implementation returns a
     * {@code JToolBar} object.
     *
     * @param actionBuilder the action builder
     * @return the new tool bar
     * @throws FormActionException if an error occurs
     */
    public Object createToolbar(ActionBuilder actionBuilder)
            throws FormActionException
    {
        return new JToolBar();
    }

    /**
     * Creates a toolbar button based on the given action. Depending on the
     * {@code checked} argument either a {@code JButton} or a
     * {@code JToggleButton} object will be returned.
     *
     * @param actionBuilder the action builder
     * @param formAction the action to associate with the menu item; this object
     * must implement Swing's {@code Action} interface
     * @param checked the checked flag
     * @param parent the parent toolbar; this must be an instance of
     * {@code JToolBar}
     * @return the new tool button
     * @throws FormActionException if an error occurs
     */
    public Object createToolbarButton(ActionBuilder actionBuilder,
            FormAction formAction, boolean checked, Object parent)
            throws FormActionException
    {
        Action action = (Action) formAction;
        AbstractButton btn = checked ? new JToggleButton(
                action) : (AbstractButton) new JButton(action);
        if (!actionBuilder.isToolbarText())
        {
            // explicitly set the text to null
            btn.setText(null);
        }

        addToToolbar(parent, btn);
        return btn;
    }

    /**
     * Creates a toolbar button based on the passed in action data object and
     * returns a component handler for it. Depending on the {@code checked}
     * argument either a {@code JButton} or a {@code JToggleButton}
     * object will be created.
     *
     * @param actionBuilder the action builder
     * @param data the action data object with the properties for the tool
     * button
     * @param checked the checked flag
     * @param parent the parent toolbar; this must be an instance of
     * {@code JToolBar}
     * @return a component handler for the new tool button
     * @throws FormActionException if an error occurs
     */
    public ComponentHandler<?> createToolbarButton(ActionBuilder actionBuilder,
            ActionData data, boolean checked, Object parent)
            throws FormActionException
    {
        AbstractButton btn = checked ? new JToggleButton()
                : (AbstractButton) new JButton();
        initFromActionData(btn, data);
        addToToolbar(parent, btn);
        return new SwingButtonHandler(btn);
    }

    /**
     * Adds a separator to the given menu. The passed in menu must be an
     * instance of {@code JMenu}.
     *
     * @param actionBuilder the action builder
     * @param menu the menu
     * @throws FormActionException if an error occurs
     */
    public void addMenuSeparator(ActionBuilder actionBuilder, Object menu)
            throws FormActionException
    {
        ((JMenu) menu).addSeparator();
    }

    /**
     * Adds a separator to the given tool bar. The passed in object must be an
     * instance of {@code JToolBar}.
     *
     * @param actionBuilder the action builder
     * @param toolBar the tool bar
     * @throws FormActionException if an error occurs
     */
    public void addToolBarSeparator(ActionBuilder actionBuilder, Object toolBar)
            throws FormActionException
    {
        ((JToolBar) toolBar).addSeparator();
    }

    /**
     * Associates a {@code PopupMenuHandler} with a UI component. This
     * implementation expects that the passed in object is derived from
     * {@code java.awt.Component}. It registers a special mouse listener at
     * this component that is looking for gestures triggering a popup menu. When
     * such a gesture is detected the {@code PopupMenuHandler} object is
     * invoked.
     *
     * @param component the component
     * @param handler the handler to register
     * @param compData the component builder data object
     * @throws FormActionException if an error occurs
     * @throws IllegalArgumentException if a required parameter is missing
     */
    public void registerPopupMenuHandler(Object component,
            PopupMenuHandler handler, ComponentBuilderData compData)
            throws FormActionException
    {
        if (!(component instanceof Component))
        {
            throw new FormActionException(
                    "Component object must be derived from java.awt.Component: "
                            + component);
        }

        ((Component) component).addMouseListener(new SwingPopupListener(
                handler, compData, this, fetchActionBuilder(compData)));
    }

    /**
     * Adds a menu item to a parent menu. The menu must be an instance of
     * {@code JMenu} or {@code JPopupMenu}.
     *
     * @param parent the parent menu
     * @param item the item to add
     */
    protected void addToMenu(Object parent, JMenuItem item)
    {
        if (parent instanceof JPopupMenu)
        {
            ((JPopupMenu) parent).add(item);
        }
        else
        {
            ((JMenu) parent).add(item);
        }
    }

    /**
     * Adds a tool button to the parent bar. The bar must be an instance of
     * {@code JToolBar}.
     *
     * @param parent the parent tool bar
     * @param button the button to add
     */
    protected void addToToolbar(Object parent, AbstractButton button)
    {
        ((JToolBar) parent).add(button);
    }

    /**
     * Helper method for setting an action's property. This method sets the
     * property only if its value is defined.
     *
     * @param action the action to initialize
     * @param property the name of the property
     * @param value the property's value
     */
    private static void initActionProperty(Action action, String property,
            Object value)
    {
        if (value != null)
        {
            action.putValue(property, value);
        }
    }

    /**
     * Helper method for setting a button's properties to the values provided in
     * the action data object. This method can be used for menu items and
     * toolbar buttons.
     *
     * @param c the button to initialize
     * @param data the action data object
     */
    static void initFromActionData(AbstractButton c, ActionData data)
    {
        c.setText(data.getText());
        c.setToolTipText(data.getToolTip());
        c.setMnemonic(data.getMnemonicKey());
        c.setIcon((Icon) data.getIcon());
    }

    /**
     * Transforms a generic accelerator definition into a Swing-specific key
     * stroke.
     *
     * @param acc the accelerator
     * @return the key stroke
     */
    static KeyStroke keyStrokeFromAccelerator(Accelerator acc)
    {
        if (acc == null)
        {
            return null;
        }

        int modifiers = convertAcceleratorModifiers(acc);
        if (acc.getKey() != null)
        {
            return KeyStroke.getKeyStroke(acc.getKey(), modifiers);
        }
        else
        {
            int keyCode = (acc.getSpecialKey() != null) ? SwingEventConstantMapper
                    .convertStandardKey(acc.getSpecialKey())
                    : acc.getKeyCode();
            return KeyStroke.getKeyStroke(keyCode, modifiers);
        }
    }

    /**
     * Converts the modifiers defined for the given accelerator into a
     * corresponding bit map.
     *
     * @param acc the accelerator
     * @return the corresponding bits
     */
    private static int convertAcceleratorModifiers(Accelerator acc)
    {
        Set<Modifiers> mods = acc.getModifiers();
        return SwingEventConstantMapper.convertStandardModifiers(mods);
    }

    /**
     * Obtains the {@code ActionBuilder} object from the bean context of the
     * passed in {@code ComponentBuilderData}.
     *
     * @param compData the {@code ComponentBuilderData}
     * @return the current {@code ActionBuilder}
     */
    private static ActionBuilder fetchActionBuilder(
            ComponentBuilderData compData)
    {
        return (ActionBuilder) compData.getBeanContext().getBean(
                ActionBuilder.KEY_ACTION_BUILDER);
    }
}
