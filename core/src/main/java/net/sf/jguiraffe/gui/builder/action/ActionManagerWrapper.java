/*
 * Copyright 2006-2013 The JGUIraffe Team.
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
 * A base class for wrapper implementations of the {@link ActionManager}
 * interface.
 * </p>
 * <p>
 * An instance of this class is initialized with a reference to an
 * {@link ActionManager} object. All methods simply delegate to this object.
 * </p>
 * <p>
 * This class is especially useful if a custom implementation of
 * {@link ActionManager} is to be created based on an existing implementation.
 * Then only the methods to be customized have to be implemented. All other
 * methods can still delegate to the existing implementation.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ActionManagerWrapper.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class ActionManagerWrapper implements ActionManager
{
    /** Stores the wrapped action manager. */
    private final ActionManager wrappedManager;

    /**
     * Creates a new instance of {@code ActionManagerWrapper} and initializes it
     * with the wrapped {@code ActionManager}. Note: This method does not check
     * whether the passed in {@code ActionManager} object is <b>null</b> because
     * we do not want to enforce this restriction on all subclasses. The passed
     * in wrapped action manager is stored in an internal field and can be
     * accessed using the {@link #getWrappedActionManager()} method. If a
     * subclass needs another mechanism to access the wrapped manager, it has to
     * override the {@link #getWrappedActionManager()} method.
     *
     * @param wrapped the wrapped {@code ActionManager}
     */
    protected ActionManagerWrapper(ActionManager wrapped)
    {
        wrappedManager = wrapped;
    }

    /**
     * Returns a reference to the wrapped {@code ActionManager} object.
     *
     * @return the wrapped {@code ActionManager}
     */
    public ActionManager getWrappedActionManager()
    {
        return wrappedManager;
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ActionManager} object.
     */
    public FormAction createAction(ActionBuilder actionBuilder,
            ActionData actionData) throws FormActionException
    {
        return getWrappedActionManager()
                .createAction(actionBuilder, actionData);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ActionManager} object.
     */
    public Object createMenuItem(ActionBuilder actionBuilder,
            FormAction action, boolean checked, Object parent)
            throws FormActionException
    {
        return getWrappedActionManager().createMenuItem(actionBuilder, action,
                checked, parent);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ActionManager} object.
     */
    public ComponentHandler<?> createMenuItem(ActionBuilder actionBuilder,
            ActionData actionData, boolean checked, Object parent)
            throws FormActionException
    {
        return getWrappedActionManager().createMenuItem(actionBuilder,
                actionData, checked, parent);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ActionManager} object.
     */
    public Object createMenuBar(ActionBuilder actionBuilder)
            throws FormActionException
    {
        return getWrappedActionManager().createMenuBar(actionBuilder);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ActionManager} object.
     */
    public Object createMenu(ActionBuilder actionBuilder, Object menu,
            TextIconData data, Object parent) throws FormActionException
    {
        return getWrappedActionManager().createMenu(actionBuilder, menu, data,
                parent);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ActionManager} object.
     */
    public Object createToolbar(ActionBuilder actionBuilder)
            throws FormActionException
    {
        return getWrappedActionManager().createToolbar(actionBuilder);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ActionManager} object.
     */
    public Object createToolbarButton(ActionBuilder actionBuilder,
            FormAction action, boolean checked, Object parent)
            throws FormActionException
    {
        return getWrappedActionManager().createToolbarButton(actionBuilder,
                action, checked, parent);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ActionManager} object.
     */
    public ComponentHandler<?> createToolbarButton(ActionBuilder actionBuilder,
            ActionData data, boolean checked, Object parent)
            throws FormActionException
    {
        return getWrappedActionManager().createToolbarButton(actionBuilder,
                data, checked, parent);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ActionManager} object.
     */
    public void addMenuSeparator(ActionBuilder actionBuilder, Object menu)
            throws FormActionException
    {
        getWrappedActionManager().addMenuSeparator(actionBuilder, menu);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ActionManager} object.
     */
    public void addToolBarSeparator(ActionBuilder actionBuilder, Object toolBar)
            throws FormActionException
    {
        getWrappedActionManager().addToolBarSeparator(actionBuilder, toolBar);
    }

    /**
     * {@inheritDoc} Just delegates to the wrapped {@code ActionManager} object.
     */
    public void registerPopupMenuHandler(Object component,
            PopupMenuHandler handler, ComponentBuilderData compData)
            throws FormActionException
    {
        getWrappedActionManager().registerPopupMenuHandler(component, handler,
                compData);
    }
}
