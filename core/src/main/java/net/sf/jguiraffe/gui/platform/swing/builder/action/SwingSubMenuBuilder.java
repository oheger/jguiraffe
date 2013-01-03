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
package net.sf.jguiraffe.gui.platform.swing.builder.action;

import java.awt.Component;

import javax.swing.Action;
import javax.swing.JMenu;

import net.sf.jguiraffe.gui.builder.action.ActionData;
import net.sf.jguiraffe.gui.builder.action.FormAction;
import net.sf.jguiraffe.gui.builder.action.PopupMenuBuilder;

/**
 * <p>
 * A specialized implementation of the <code>PopuMenuBuilder</code> interface
 * for constructing sub menus.
 * </p>
 * <p>
 * An instance of this class is returned by the <code>subMenuBuilder()</code>
 * method of the Swing-specific <code>PopupMenuBuilder</code> implementation. It
 * creates a <code>javax.swing.JMenu</code> object and implements the builder
 * methods in a way that the correct items are added to this menu.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingSubMenuBuilder.java 205 2012-01-29 18:29:57Z oheger $
 */
class SwingSubMenuBuilder implements PopupMenuBuilder
{
    /** Stores the menu that is constructed by this builder. */
    private final JMenu menu;

    /**
     * Creates a new instance of <code>SwingSubMenuBuilder</code>. The sub menu
     * constructed by this builder is initialized based on the properties of the
     * passed in <code>ActionData</code> object.
     *
     * @param menuDesc the description of the menu to be created (must not be
     *        <b>null</b>)
     * @throws IllegalArgumentException if the menu description is <b>null</b>
     */
    public SwingSubMenuBuilder(ActionData menuDesc)
    {
        if (menuDesc == null)
        {
            throw new IllegalArgumentException(
                    "Menu description must not be null!");
        }

        menu = createMenu(menuDesc);
    }

    /**
     * Adds the specified action to this menu. This implementation expects that
     * the passed in action object implements Swing's <code>Action</code>
     * interface. If this is not the case, an exception is thrown.
     *
     * @param action the action to add to this menu
     * @return a reference to this builder
     * @throws IllegalArgumentException if the passed in action is not a Swing
     *         action
     */
    public PopupMenuBuilder addAction(FormAction action)
    {
        if (!(action instanceof Action))
        {
            throw new IllegalArgumentException("Action must be a Swing Action!");
        }

        getMenu().add((Action) action);
        return this;
    }

    /**
     * Adds a separator to the menu.
     *
     * @return a reference to this builder
     */
    public PopupMenuBuilder addSeparator()
    {
        getMenu().addSeparator();
        return this;
    }

    /**
     * Adds a sub menu to the current menu. This implementation allows adding
     * arbitrary <code>java.awt.Component</code> objects, which is in line with
     * the <code>add()</code> method of <code>javax.swing.JMenu</code>. If the
     * object passed to this method is not a <code>Component</code>, an
     * exception is thrown.
     *
     * @param subMenu the menu object to add
     * @return a reference to this builder
     * @throws IllegalArgumentException if the passed in object is not supported
     */
    public PopupMenuBuilder addSubMenu(Object subMenu)
    {
        if (!(subMenu instanceof Component))
        {
            throw new IllegalArgumentException(
                    "Object to add to the menu must be an awt Component!");
        }

        getMenu().add((Component) subMenu);
        return this;
    }

    /**
     * Creates the menu. This implementation returns the <code>JMenu</code> that
     * is constructed by this builder.
     *
     * @return the menu created by this builder
     */
    public Object create()
    {
        return getMenu();
    }

    /**
     * Returns a builder for constructing a sub menu. This implementation
     * returns another instance of <code>SwingSubMenuBuilder</code>, which is
     * initialized with the specified menu description.
     *
     * @param menuDesc the description of the new sub menu (must not be
     *        <b>null</b>)
     * @return a builder for defining the new menu
     * @throws IllegalArgumentException if the menu description is undefined
     */
    public PopupMenuBuilder subMenuBuilder(ActionData menuDesc)
    {
        return new SwingSubMenuBuilder(menuDesc);
    }

    /**
     * Returns the menu that is constructed by this builder. This method can be
     * called at any time for obtaining a reference to the current menu.
     *
     * @return the menu that is currently constructed
     */
    protected JMenu getMenu()
    {
        return menu;
    }

    /**
     * Creates a menu object and initializes it with the properties of the
     * passed in action data.
     *
     * @param actData the action data
     * @return the corresponding menu
     */
    private JMenu createMenu(ActionData actData)
    {
        JMenu menu = new JMenu();
        SwingActionManager.initFromActionData(menu, actData);
        return menu;
    }
}
