/*
 * Copyright 2006-2021 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.platform.swing.builder.event;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import net.sf.jguiraffe.gui.builder.event.FormEventManager;
import net.sf.jguiraffe.gui.builder.event.FormFocusEvent;
import net.sf.jguiraffe.gui.builder.event.FormFocusListener;
import net.sf.jguiraffe.gui.builder.event.FormListenerType;
import net.sf.jguiraffe.gui.forms.ComponentHandler;

/**
 * <p>
 * A specific event adapter for Swing focus events.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FocusEventAdapter.java 205 2012-01-29 18:29:57Z oheger $
 */
class FocusEventAdapter extends SwingEventAdapter implements FocusListener
{
    /**
     * Creates a new instance of {@code FocusEventAdapter} that passes received
     * events to a {@code FormEventManager}.
     *
     * @param eventManager the {@code FormEventManager} (must not be
     *        <b>null</b>)
     * @param handler the component handler
     * @param name the component's name
     * @throws IllegalArgumentException if the {@code FormEventManager} is
     *         <b>null</b>
     */
    public FocusEventAdapter(FormEventManager eventManager,
            ComponentHandler<?> handler, String name)
    {
        super(eventManager, handler, name);
    }

    /**
     * Creates a new instance of {@code FocusEventAdapter} that passes received
     * events to a {@code FormFocusListener}.
     *
     * @param listener the {@code FormFocusListener} (must not be <b>null</b>)
     * @param handler the component handler
     * @param name the component's name
     * @throws IllegalArgumentException if the {@code FormFocusListener} is
     *         <b>null</b>
     */
    public FocusEventAdapter(FormFocusListener listener,
            ComponentHandler<?> handler, String name)
    {
        super(listener, handler, name);
    }

    /**
     * Call back for focus gained events.
     *
     * @param event the event
     */
    public void focusGained(FocusEvent event)
    {
        fireFocusEvent(event, FormFocusEvent.Type.FOCUS_GAINED);
    }

    /**
     * Call back for focus lost events.
     *
     * @param event the event
     */
    public void focusLost(FocusEvent event)
    {
        fireFocusEvent(event, FormFocusEvent.Type.FOCUS_LOST);
    }

    /**
     * Sends a focus event to the event manager. The form event is created from
     * the passed in focus event.
     *
     * @param event the Swing focus event
     * @param type the event type
     */
    protected void fireFocusEvent(FocusEvent event, FormFocusEvent.Type type)
    {
        fireEvent(createFocusEvent(event, type));
    }

    /**
     * Creates a general form focus event from the passed in Swing focus event.
     *
     * @param event the original event
     * @param type the event type
     * @return the form event
     */
    protected FormFocusEvent createFocusEvent(FocusEvent event,
            FormFocusEvent.Type type)
    {
        return new FormFocusEvent(event, getHandler(), getName(), type);
    }

    /**
     * Returns the form listener type for this adapter.
     *
     * @return the listener type
     */
    @Override
    protected FormListenerType getListenerType()
    {
        return FormListenerType.FOCUS;
    }
}
