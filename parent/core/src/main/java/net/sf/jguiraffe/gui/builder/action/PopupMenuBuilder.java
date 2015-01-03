/*
 * Copyright 2006-2015 The JGUIraffe Team.
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

/**
 * <p>
 * A <em>builder</em> interface for creating popup menus.
 * </p>
 * <p>
 * Using the methods defined in this interface arbitrary complex popup menus can
 * be created. A popup menu consists of an arbitrary number of
 * <code>{@link FormAction}</code> objects plus optional separators and sub
 * menus. The basic usage pattern is to invoke the several <code>add()</code>
 * methods for defining the menu's content. Finally the <code>create()</code>
 * method must be called, which will actually create the menu.
 * </p>
 * <p>
 * This interface provides a platform-independent view on popup menus. There
 * will be concrete implementations for the GUI libraries supported. These
 * implementations take care of creating the correct, platform-specific menu
 * objects.
 * </p>
 * <p>
 * As is common practice for builder-like structures this interface supports
 * method chaining, i.e. most methods return a reference to the builder itself,
 * which can be used for immediately adding the next element. The following
 * example fragment shows how a popup menu with some items and a sub menu can be
 * constructed. It assumes that the references to the actions involved are
 * defined somewhere else:
 *
 * <pre>
 * PopupMenuBuilder builder = ...  // obtain the builder
 *
 * Object popup = builder
 *     .addAction(actionOpen)
 *     .addAction(actionSave)
 *     .addAction(actionSaveAs)
 *     .addSeparator()
 *     .addSubMenu(builder.subMenuBuilder(tiMenuEdit)
 *         .addAction(actionCopy)
 *         .addAction(actionCut)
 *         .addAction(actionPaste)
 *         .create())
 *     .addSeparator()
 *     .addAction(actionExit)
 *     .create();
 * </pre>
 *
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: $
 */
public interface PopupMenuBuilder
{
    /**
     * Adds an action to the current menu. This will create a menu item that
     * invokes this action when it is selected by the user. The properties of
     * this item (like text, icon, etc.) are obtained from the action object.
     *
     * @param action the action to be added (must not be <b>null</b>)
     * @return a reference to this builder
     * @throws IllegalArgumentException if the action is <b>null</b>
     */
    PopupMenuBuilder addAction(FormAction action);

    /**
     * Adds a separator to the current menu. Separators can be used for grouping
     * related menu items.
     *
     * @return a reference to this builder
     */
    PopupMenuBuilder addSeparator();

    /**
     * Adds a sub menu to the current menu. This allows for complex structures
     * of hierarchical menus. The object passed to this method must be a menu
     * that was created by a sub menu builder.
     *
     * @param subMenu the sub menu to add (must not be <b>null</b>)
     * @return a reference to this builder
     * @throws IllegalArgumentException if the sub menu is <b>null</b>
     * @see #subMenuBuilder(ActionData)
     */
    PopupMenuBuilder addSubMenu(Object subMenu);

    /**
     * Returns a builder for creating a sub menu. The builder returned by this
     * method can be used to define the sub menu (i.e. add actions, separators,
     * and further sub menus as desired). Its <code>create()</code> method
     * returns the menu created. The passed in <code>ActionData</code> object
     * contains the definition of the menu as it will be displayed in the parent
     * menu (i.e. its text, icon, etc.).
     *
     * @param menuDesc an <code>ActionData</code> object with the properties of
     *        the sub menu (must not be <b>null</b>)
     * @return a builder for defining the new sub menu
     * @throws IllegalArgumentException if the menu description is <b>null</b>
     */
    PopupMenuBuilder subMenuBuilder(ActionData menuDesc);

    /**
     * Creates the menu and returns a reference to it. If this is the top-level
     * builder (i.e. not a builder for a sub menu), the popup menu will be
     * displayed.
     *
     * @return the menu created by this builder
     */
    Object create();
}
