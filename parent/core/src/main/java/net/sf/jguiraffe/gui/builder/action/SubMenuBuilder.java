/*
 * Copyright 2006-2022 The JGUIraffe Team.
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

import net.sf.jguiraffe.gui.builder.components.FormBuilderRuntimeException;
import net.sf.jguiraffe.gui.builder.components.tags.TextIconData;

/**
 * <p>
 * An internally used {@link PopupMenuBuilder} implementation for creating sub
 * menus.
 * </p>
 * <p>
 * An instance of this class is used by {@link AbstractPopupMenuBuilder} when a
 * builder for a sub menu is requested. Most of the functionality is inherited
 * from the base class. In the {@code create()} method a sub menu is created
 * (using the associated action manager) and added to the parent popup menu.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id$
 */
class SubMenuBuilder extends AbstractPopupMenuBuilder
{
    /** The parent menu to which the sub menu has to be added. */
    private final Object parentMenu;

    /** The sub menu to be defined by this builder. */
    private final Object subMenu;

    /** The data object with the properties of the sub menu to be constructed. */
    private final TextIconData tiData;

    /**
     * Creates a new instance of {@code SubMenuBuilder}.
     *
     * @param manager the {@code ActionManager}
     * @param builder the {@code ActionBuilder}
     * @param menuOfParent the menu of the parent builder
     * @param menu the sub menu to be defined by this builder
     * @param subMenuData the data object describing the sub menu
     */
    public SubMenuBuilder(ActionManager manager, ActionBuilder builder,
            Object menuOfParent, Object menu, TextIconData subMenuData)
    {
        super(manager, builder);
        parentMenu = menuOfParent;
        subMenu = menu;
        this.tiData = subMenuData;
    }

    @Override
    protected Object getMenuUnderConstruction()
    {
        return subMenu;
    }

    /**
     * {@inheritDoc} This implementation calls the action manager to create an
     * initialized sub menu and add it to the menu of the parent builder.
     */
    public Object create()
    {
        try
        {
            return getActionManager().createMenu(getActionBuilder(),
                    getMenuUnderConstruction(), tiData, parentMenu);
        }
        catch (FormActionException e)
        {
            throw new FormBuilderRuntimeException(e);
        }
    }
}
