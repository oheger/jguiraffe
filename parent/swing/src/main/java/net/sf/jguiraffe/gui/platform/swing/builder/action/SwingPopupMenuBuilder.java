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

import javax.swing.JPopupMenu;
import java.awt.event.MouseEvent;

import net.sf.jguiraffe.gui.builder.action.AbstractPopupMenuBuilder;
import net.sf.jguiraffe.gui.builder.action.ActionBuilder;
import net.sf.jguiraffe.gui.builder.action.ActionManager;

/**
 * <p>
 * A specialized implementation of the {@code PopupMenuBuilder} interface
 * for constructing Swing popup menus.
 * </p>
 * <p>
 * This implementation creates a {@code javax.swing.JPopupMenu} object when
 * a new builder instance is created. The several {@code add()} methods
 * populate this menu. They expect that objects compatible with Swing are passed
 * as parameters (e.g. Swing actions or Swing menu components).
 * </p>
 * <p>
 * The {@code create()} method returns the current popup menu. It also
 * displays the menu for the affected component (this component and the location
 * where to display the popup must be specified at construction time).
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingPopupMenuBuilder.java 205 2012-01-29 18:29:57Z oheger $
 */
public class SwingPopupMenuBuilder extends AbstractPopupMenuBuilder
{
    /** Stores the original mouse event. */
    private final MouseEvent triggeringEvent;

    /** The menu that is constructed by this builder. */
    private final JPopupMenu menu;

    /**
     * Creates a new instance of {@code SwingPopupMenuBuilder} and initializes
     * it with information about the action manager to be delegated to and the
     * mouse event that triggered the invocation of this builder.
     *
     * @param actMan the {@code ActionManager}
     * @param builder the {@code ActionBuilder}
     * @param event the triggering event (must not be <b>null</b>)
     * @throws IllegalArgumentException if the event is <b>null</b>
     */
    public SwingPopupMenuBuilder(ActionManager actMan, ActionBuilder builder,
            MouseEvent event)
    {
        super(actMan, builder);
        if (event == null)
        {
            throw new IllegalArgumentException(
                    "Triggering event must not be null!");
        }

        triggeringEvent = event;
        menu = new JPopupMenu();
    }

    /**
     * Creates a new instance of {@code SwingPopupMenuBuilder} and initializes
     * it with the triggering mouse event.
     *
     * @param event the triggering event (must not be <b>null</b>)
     * @throws IllegalArgumentException if the event is <b>null</b>
     * @deprecated Use the constructor which expects information about an
     *             {@code ActionManager}; this constructor only creates a partly
     *             initialized object.
     */
    @Deprecated
    public SwingPopupMenuBuilder(MouseEvent event)
    {
        this(null, null, event);
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
     * Returns the menu that is constructed by this builder.
     *
     * @return the current menu
     */
    protected JPopupMenu getMenu()
    {
        return menu;
    }

    /**
     * Displays the specified popup menu. This method is called by
     * {@code create()} with the current menu. This implementation displays
     * the popup menu as specified by the triggering event.
     *
     * @param m the menu to display
     */
    protected void showMenu(JPopupMenu m)
    {
        m.show(getTriggeringEvent().getComponent(),
                getTriggeringEvent().getX(), getTriggeringEvent().getY());
    }

    @Override
    protected Object getMenuUnderConstruction()
    {
        return getMenu();
    }
}
