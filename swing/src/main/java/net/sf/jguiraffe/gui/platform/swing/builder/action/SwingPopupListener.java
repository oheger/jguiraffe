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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
 * <code>registerPopupMenuHandler()</code> method of
 * <code>{@link SwingActionManager}</code>. It is initialized with the
 * <code>{@link PopupMenuHandler}</code> object responsible for this menu.
 * Whenever an event is detected which triggers a popup menu a Swing-specific
 * <code>{@link PopupMenuBuilder}</code> is created and passed to the
 * <code>{@link PopupMenuHandler}</code>. Using this object the handler can
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

    /**
     * Creates a new instance of <code>SwingPopupListener</code> and initializes
     * it with the handler for constructing the menu.
     *
     * @param handler the menu handler (must not be <b>null</b>)
     * @param compData the component builder data object (must not be
     *        <b>null</b>)
     * @throws IllegalArgumentException if a required parameter is <b>null</b>
     */
    public SwingPopupListener(PopupMenuHandler handler,
            ComponentBuilderData compData)
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
     * Returns the <code>ComponentBuilderData</code> object. This object will be
     * passed to the menu handler.
     *
     * @return the <code>ComponentBuilderData</code>
     */
    public ComponentBuilderData getComponentBuilderData()
    {
        return componentBuilderData;
    }

    /**
     * A mouse button was pressed. This implementation delegates to
     * <code>maybeShowPopup()</code>.
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
     * <code>maybeShowPopup()</code>.
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
     * Creates a <code>PopupMenuBuilder</code> for defining a menu. This method
     * is called when an event is detected that triggers a context menu. The
     * builder created by this method is passed to the
     * <code>PopupMenuHandler</code>.
     *
     * @param event the mouse event
     * @return the <code>PopupMenuBuilder</code> to be used for constructing the
     *         menu
     */
    protected PopupMenuBuilder createMenuBuilder(MouseEvent event)
    {
        return new SwingPopupMenuBuilder(event);
    }
}
