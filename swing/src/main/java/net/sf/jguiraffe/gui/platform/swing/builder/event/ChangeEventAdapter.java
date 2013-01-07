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
package net.sf.jguiraffe.gui.platform.swing.builder.event;

import net.sf.jguiraffe.gui.builder.event.FormChangeEvent;
import net.sf.jguiraffe.gui.builder.event.FormChangeListener;
import net.sf.jguiraffe.gui.builder.event.FormEventManager;
import net.sf.jguiraffe.gui.builder.event.FormListenerType;
import net.sf.jguiraffe.gui.forms.ComponentHandler;

/**
 * <p>
 * A specific event adapter for change events.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ChangeEventAdapter.java 205 2012-01-29 18:29:57Z oheger $
 */
class ChangeEventAdapter extends SwingEventAdapter implements ChangeListener
{
    /**
     * Creates a new instance of {@code ChangeEventAdapter} that passes received
     * events to the {@code FormEventManager}.
     *
     * @param eventManager the {@code FormEventManager} (must not be
     *        <b>null</b>)
     * @param handler the component handler
     * @param name the component name
     * @throws IllegalArgumentException if the event manager is undefined
     */
    public ChangeEventAdapter(FormEventManager eventManager,
            ComponentHandler<?> handler, String name)
    {
        super(eventManager, handler, name);
    }

    /**
     * Creates a new instance of {@code ChangeEventAdapter} that passes received
     * events to a {@code FormChangeListener}.
     *
     * @param listener the {@code FormChangeListener} (must not be <b>null</b>)
     * @param handler the component handler
     * @param name the component name
     * @throws IllegalArgumentException if the event listener is undefined
     */
    public ChangeEventAdapter(FormChangeListener listener,
            ComponentHandler<?> handler, String name)
    {
        super(listener, handler, name);
    }

    /**
     * Call back method for change events.
     *
     * @param event the event
     */
    public void componentChanged(Object event)
    {
        fireEvent(createChangeEvent(event));
    }

    /**
     * Creates a general form change event from the passed in Swing event.
     *
     * @param event the original event
     * @return the form event
     */
    protected FormChangeEvent createChangeEvent(Object event)
    {
        return new FormChangeEvent(event, getHandler(), getName());
    }

    /**
     * Returns the event listener type for this adapter.
     *
     * @return the listener type
     */
    @Override
    protected FormListenerType getListenerType()
    {
        return FormListenerType.CHANGE;
    }
}
