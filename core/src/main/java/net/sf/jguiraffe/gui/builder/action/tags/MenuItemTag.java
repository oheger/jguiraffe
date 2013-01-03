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
package net.sf.jguiraffe.gui.builder.action.tags;

import net.sf.jguiraffe.gui.builder.action.ActionData;
import net.sf.jguiraffe.gui.builder.action.ActionManager;
import net.sf.jguiraffe.gui.builder.action.FormAction;
import net.sf.jguiraffe.gui.builder.action.FormActionException;
import net.sf.jguiraffe.gui.forms.ComponentHandler;

/**
 * <p>
 * A tag handler class that creates menu items.
 * </p>
 * <p>
 * With this tag class menu items can be created. The corresponding tags must be
 * nested inside menu tags. A menu item can be defined either by referencing an
 * action or by manually setting the corresponding attributes. Refer also to the
 * documentation of the base class.
 * </p>
 * <p>
 * In addition to the attributes defined by the base class, this tag handler
 * class does not support own attributes.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: MenuItemTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class MenuItemTag extends ActionControlTag
{
    /**
     * Returns the container tag handler class this tag must be nested inside.
     * This is the {@link MenuTag} class.
     *
     * @return the nesting container class
     */
    @Override
    protected Class<?> getContainerClass()
    {
        return MenuTag.class;
    }

    /**
     * Creates a menu item based on an action definition.
     *
     * @param manager the action manager
     * @param action the action
     * @param parent the parent container (a menu)
     * @throws FormActionException if an error occurs
     */
    @Override
    protected void createActionControl(ActionManager manager,
            FormAction action, Object parent) throws FormActionException
    {
        manager.createMenuItem(getActionBuilder(), action, isChecked(), parent);
    }

    /**
     * Creates a menu item based on an action data object.
     *
     * @param manager the action manager
     * @param data the action data object
     * @param parent the parent container (a menu)
     * @return the component handler for the new menu item
     * @throws FormActionException if an error occurs
     */
    @Override
    protected ComponentHandler<?> createElementHandler(ActionManager manager,
            ActionData data, Object parent) throws FormActionException
    {
        return manager.createMenuItem(getActionBuilder(), data, isChecked(),
                parent);
    }
}
