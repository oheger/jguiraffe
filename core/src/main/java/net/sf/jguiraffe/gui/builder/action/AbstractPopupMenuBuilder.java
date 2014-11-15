/*
 * Copyright 2006-2014 The JGUIraffe Team.
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

/**
 * <p>
 * An abstract base class for implementations of the {@code PopupMenuBuilder}
 * interface.
 * </p>
 * <p>
 * The idea behind this class is that the basic functionality required for a
 * popup builder is already provided by an {@link ActionManager}. For instance,
 * methods for adding actions, separators, or sub menus can be directly
 * delegated to the corresponding methods of the action manager. Therefore, an
 * instance is initialized with an {@code ActionManager} reference and meta data
 * to be passed to the methods of the action manager. Based on this most of the
 * creation methods can be implemented. Concrete subclasses just have to provide
 * the actual creation methods - i.e. they have to create the correct objects
 * required by the UI toolkit in use.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id$
 * @since 1.3
 */
public abstract class AbstractPopupMenuBuilder implements PopupMenuBuilder
{
    /** The action manager this builder delegates to. */
    private final ActionManager actionManager;

    /** The data object to be passed to the action manager. */
    private final ActionBuilder actionBuilder;

    /**
     * Creates a new instance of {@code AbstractPopupMenuBuilder}.
     *
     * @param manager the {@code ActionManager}
     * @param builder the {@code ActionBuilder}
     */
    protected AbstractPopupMenuBuilder(ActionManager manager,
            ActionBuilder builder)
    {
        actionManager = manager;
        actionBuilder = builder;
    }

    /**
     * Returns the {@code ActionManager} this builder delegates to.
     *
     * @return the {@code ActionManager}
     */
    public ActionManager getActionManager()
    {
        return actionManager;
    }

    /**
     * Returns the {@code ActionBuilder} data object to be passed to the action
     * manager.
     *
     * @return the {@code ActionBuilder}
     */
    public ActionBuilder getActionBuilder()
    {
        return actionBuilder;
    }

    /**
     * {@inheritDoc} This implementation delegates to the
     * {@link ActionManager#createMenuItem(ActionBuilder, FormAction, boolean, Object)}
     * method.
     */
    public PopupMenuBuilder addAction(FormAction action)
    {
        try
        {
            getActionManager().createMenuItem(getActionBuilder(), action,
                    false, getMenuUnderConstruction());
        }
        catch (FormActionException e)
        {
            throw new FormBuilderRuntimeException(e);
        }
        return this;
    }

    /**
     * {@inheritDoc} This implementation delegates to the
     * {@link ActionManager#addMenuSeparator(ActionBuilder, Object)} method of
     * the wrapped action builder.
     */
    public PopupMenuBuilder addSeparator()
    {
        try
        {
            getActionManager().addMenuSeparator(getActionBuilder(),
                    getMenuUnderConstruction());
        }
        catch (FormActionException e)
        {
            throw new FormBuilderRuntimeException(e);
        }
        return this;
    }

    /**
     * {@inheritDoc} This implementation is actually a dummy. The newly created
     * sub menu is already added by the sub menu builder.
     */
    public PopupMenuBuilder addSubMenu(Object subMenu)
    {
        return this;
    }

    /**
     * Returns the menu which is currently under construction. This object is
     * passed to the {@code ActionManager} in order to add new elements to it.
     *
     * @return the menu to be constructed
     */
    protected abstract Object getMenuUnderConstruction();
}
