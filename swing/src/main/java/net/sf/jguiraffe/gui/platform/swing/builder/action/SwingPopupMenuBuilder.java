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
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JPopupMenu;

import net.sf.jguiraffe.gui.builder.action.ActionData;
import net.sf.jguiraffe.gui.builder.action.FormAction;
import net.sf.jguiraffe.gui.builder.action.PopupMenuBuilder;

/**
 * <p>
 * A specialized implementation of the <code>PopupMenuBuilder</code> interface
 * for constructing Swing popup menus.
 * </p>
 * <p>
 * This implementation creates a <code>javax.swing.JPopupMenu</code> object when
 * a new builder instance is created. The several <code>add()</code> methods
 * populate this menu. They expect that objects compatible with Swing are passed
 * as parameters (e.g. Swing actions or Swing menu components).
 * </p>
 * <p>
 * The <code>create()</code> method returns the current popup menu. It also
 * displays the menu for the affected component (this component and the location
 * where to display the popup must be specified at construction time).
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingPopupMenuBuilder.java 205 2012-01-29 18:29:57Z oheger $
 */
public class SwingPopupMenuBuilder implements PopupMenuBuilder
{
    /** Stores the original mouse event. */
    private final MouseEvent triggeringEvent;

    /** The menu that is constructed by this builder. */
    private final JPopupMenu menu;

    /**
     * Creates a new instance of <code>SwingPopupMenuBuilder</code> and
     * initializes it with the mouse event that triggered the invocation of this
     * builder.
     *
     * @param event the triggering event (must not be <b>null</b>)
     * @throws IllegalArgumentException if the event is <b>null</b>
     */
    public SwingPopupMenuBuilder(MouseEvent event)
    {
        if (event == null)
        {
            throw new IllegalArgumentException(
                    "Triggering event must not be null!");
        }

        triggeringEvent = event;
        menu = new JPopupMenu();
    }

    /**
     * Returns the event that triggered the invocation of this builder.
     *
     * @return the triggering mouse event
     */
    public MouseEvent getTriggeringEvent()
    {
        return triggeringEvent;
    }

    /**
     * Adds an action to the popup menu constructed by this builder. The action
     * passed in must implement the <code>javax.swing.Action</code> interface.
     *
     * @param action the action to add
     * @return a reference to this builder
     * @throws IllegalArgumentException if the action is no Swing action
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
     * Adds a separator to the menu constructed by this builder.
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
     * Displays the popup menu constructed by this builder. This method must be
     * called when the construction of the menu is complete.
     *
     * @return the popup menu
     */
    public Object create()
    {
        showMenu(getMenu());
        return getMenu();
    }

    /**
     * Returns a builder for constructing a sub menu. This implementation
     * returns a specialized implementation of <code>PopupMenuBuilder</code>,
     * which can construct sub menus and is initialized with the specified menu
     * description.
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
     * Returns the menu that is constructed by this builder.
     *
     * @return the current menu
     */
    protected JPopupMenu getMenu()
    {
        return menu;
    }

    /**
     * Displays the specified popu menu. This method is called by
     * <code>create()</code> with the current menu. This implementation displays
     * the popup menu as specified by the triggering event.
     *
     * @param m the menu to display
     */
    protected void showMenu(JPopupMenu m)
    {
        m.show(getTriggeringEvent().getComponent(),
                getTriggeringEvent().getX(), getTriggeringEvent().getY());
    }
}
