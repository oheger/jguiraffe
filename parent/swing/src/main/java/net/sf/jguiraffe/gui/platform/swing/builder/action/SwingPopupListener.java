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
package net.sf.jguiraffe.gui.platform.swing.builder.action;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import net.sf.jguiraffe.gui.builder.action.ActionBuilder;
import net.sf.jguiraffe.gui.builder.action.ActionManager;
import net.sf.jguiraffe.gui.builder.action.FormActionException;
import net.sf.jguiraffe.gui.builder.action.PopupMenuBuilder;
import net.sf.jguiraffe.gui.builder.action.PopupMenuHandler;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.FormBuilderRuntimeException;

/**
 * <p>
 * A specialized mouse listener for bringing up a popup menu for a GUI
 * component.
 * </p>
 * <p>
 * An instance of this class is used by the
 * {@code registerPopupMenuHandler()} method of
 * {@link SwingActionManager}. It is initialized with the
 * {@link PopupMenuHandler} object responsible for this menu.
 * Whenever an event is detected which triggers a popup menu a Swing-specific
 * {@link PopupMenuBuilder} is created and passed to the
 * {@link PopupMenuHandler}. Using this object the handler can
 * define and display the menu.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingPopupListener.java 205 2012-01-29 18:29:57Z oheger $
 */
class SwingPopupListener extends MouseAdapter
{
    /** Stores the menu handler for constructing the menu. */
    private final PopupMenuHandler menuHandler;

    /** Stores a reference to the component builder data. */
    private final ComponentBuilderData componentBuilderData;

    /** Stores the current action manager. */
    private final ActionManager actionManager;

    /** Stores the action builder data object. */
    private final ActionBuilder actionBuilder;

    /**
     * Creates a new instance of {@code SwingPopupListener} and initializes it
     * with the handler for constructing the menu.
     *
     * @param handler the menu handler (must not be <b>null</b>)
     * @param compData the component builder data object (must not be
     *        <b>null</b>)
     * @param actMan the current {@code ActionManager}
     * @param builder the {@code ActionBuilder}
     * @throws IllegalArgumentException if a required parameter is <b>null</b>
     */
    public SwingPopupListener(PopupMenuHandler handler,
            ComponentBuilderData compData, ActionManager actMan,
            ActionBuilder builder)
    {
        if (handler == null)
        {
            throw new IllegalArgumentException("Menu handler must not be null!");
        }
        if (compData == null)
        {
            throw new IllegalArgumentException(
                    "ComponentBuilderData must not be null!");
        }

        menuHandler = handler;
        componentBuilderData = compData;
        actionManager = actMan;
        actionBuilder = builder;
    }

    /**
     * Returns the menu handler used by this object.
     *
     * @return the menu handler
     */
    public PopupMenuHandler getMenuHandler()
    {
        return menuHandler;
    }

    /**
     * Returns the {@code ComponentBuilderData} object. This object will be
     * passed to the menu handler.
     *
     * @return the {@code ComponentBuilderData}
     */
    public ComponentBuilderData getComponentBuilderData()
    {
        return componentBuilderData;
    }

    /**
     * Returns the current {@code ActionManager}.
     *
     * @return the {@code ActionManager}
     */
    public ActionManager getActionManager()
    {
        return actionManager;
    }

    /**
     * Returns the {@code ActionBuilder}.
     *
     * @return the {@code ActionBuilder}
     */
    public ActionBuilder getActionBuilder()
    {
        return actionBuilder;
    }

    /**
     * A mouse button was pressed. This implementation delegates to
     * {@code maybeShowPopup()}.
     *
     * @param event the mouse event
     */
    @Override
    public void mousePressed(MouseEvent event)
    {
        maybeShowPopup(event);
    }

    /**
     * A mouse button was released. This implementation delegates to
     * {@code maybeShowPopup()}.
     *
     * @param event the mouse event
     */
    @Override
    public void mouseReleased(MouseEvent event)
    {
        maybeShowPopup(event);
    }

    /**
     * Checks whether the passed in event is a trigger for a popup menu and
     * invokes the menu handler if this is the case. This method is called by
     * other event handler methods.
     *
     * @param event the mouse event
     * @throws FormBuilderRuntimeException if the creation of the menu causes an
     *         error
     */
    protected void maybeShowPopup(MouseEvent event)
    {
        if (event.isPopupTrigger())
        {
            try
            {
                getMenuHandler().constructPopup(createMenuBuilder(event),
                        getComponentBuilderData());
            }
            catch (FormActionException faex)
            {
                throw new FormBuilderRuntimeException(
                        "Exception when creating popup menu", faex);
            }
        }
    }

    /**
     * Creates a {@code PopupMenuBuilder} for defining a menu. This method
     * is called when an event is detected that triggers a context menu. The
     * builder created by this method is passed to the
     * {@code PopupMenuHandler}.
     *
     * @param event the mouse event
     * @return the {@code PopupMenuBuilder} to be used for constructing the
     *         menu
     */
    protected PopupMenuBuilder createMenuBuilder(MouseEvent event)
    {
        return new SwingPopupMenuBuilder(getActionManager(),
                getActionBuilder(), event);
    }
}
