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
package net.sf.jguiraffe.gui.platform.swing.builder.event;

import net.sf.jguiraffe.gui.builder.event.FormEvent;
import net.sf.jguiraffe.gui.builder.event.FormEventListener;
import net.sf.jguiraffe.gui.builder.event.FormEventManager;
import net.sf.jguiraffe.gui.builder.event.FormListenerType;
import net.sf.jguiraffe.gui.forms.ComponentHandler;

/**
 * <p>
 * The base class for Swing event adapters.
 * </p>
 * <p>
 * An event adapter is responsible for transforming a Swing specific event
 * notification into a platform independent form builder event. This base class
 * provides a great deal of common functionality useful for different event
 * types. Concrete sub classes will deal with specific event types.
 * </p>
 * <p>
 * This base class already stores important information (e.g. about the
 * component this event adapter is associated with) in member fields. It also
 * supports two different ways to map Swing events to platform-independent
 * events:
 * <ul>
 * <li>If a {@code FormEventManager} is specified, its {@code fireEvent()}
 * method is invoked. This automatically calls all listeners registered for
 * specific or all components.</li>
 * <li>It is also possible to map the Swing-specific events to a specific event
 * listener which has to be passed to the constructor. In this case only this
 * listener is invoked.</li>
 * </ul>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingEventAdapter.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class SwingEventAdapter
{
    /** Stores a reference to the event manager. */
    private final FormEventManager eventManager;

    /** Stores a reference to the associated event listener. */
    private final FormEventListener listener;

    /** Stores a reference to the associated component handler. */
    private final ComponentHandler<?> handler;

    /** The name of the component this adapter is registered at. */
    private final String name;

    /**
     * Creates a new instance of {@code SwingEventAdapter} and sets all
     * properties.
     *
     * @param evMan the {@code FormEventManager}
     * @param l the event listener
     * @param h the {@code ComponentHandler}
     * @param n the name of the component
     */
    private SwingEventAdapter(FormEventManager evMan, FormEventListener l,
            ComponentHandler<?> h, String n)
    {
        eventManager = evMan;
        listener = l;
        handler = h;
        name = n;
    }

    /**
     * Creates a new instance of {@code SwingEventAdapter} that uses the {@code
     * FormEventManager} to broadcast events.
     *
     * @param eventManager the event manager (must not be <b>null</b>)
     * @param handler the component handler
     * @param name the component's name
     * @throws IllegalArgumentException if the {@code FormEventManager} is
     *         <b>null</b>
     */
    protected SwingEventAdapter(FormEventManager eventManager,
            ComponentHandler<?> handler, String name)
    {
        this(eventManager, null, handler, name);
        if (eventManager == null)
        {
            throw new IllegalArgumentException(
                    "FormEventManager must not be null!");
        }
    }

    /**
     * Creates a new instance of {@code SwingEventAdapter} that serves a
     * specific event listener.
     *
     * @param l the event listener (must not be <b>null</b>)
     * @param handler the {@code ComponentHandler}
     * @param name the name of the component
     * @throws IllegalArgumentException if the event listener is <b>null</b>
     */
    protected SwingEventAdapter(FormEventListener l,
            ComponentHandler<?> handler, String name)
    {
        this(null, l, handler, name);
        if (l == null)
        {
            throw new IllegalArgumentException(
                    "Event listener must not be null!");
        }
    }

    /**
     * Returns a reference to the form event manager. This can be <b>null</b> if
     * this adapter is not associated with the event manager.
     *
     * @return the event manager
     */
    public FormEventManager getEventManager()
    {
        return eventManager;
    }

    /**
     * Returns the event listener this adapter is associated with. This can be
     * <b>null</b> if this listener is not associated with an event listener.
     *
     * @return the event listener
     */
    public FormEventListener getEventListener()
    {
        return listener;
    }

    /**
     * Returns a reference to the associated component handler.
     *
     * @return the component handler
     */
    public ComponentHandler<?> getHandler()
    {
        return handler;
    }

    /**
     * Returns the name of the component this adapter is registered at.
     *
     * @return the component name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Notifies the event manager about a new event. With this method an event
     * can be sent to all registered listeners.
     *
     * @param event the event to send
     */
    protected void fireEvent(FormEvent event)
    {
        if (getEventManager() != null)
        {
            getEventManager().fireEvent(event, getListenerType());
        }
        else
        {
            getListenerType().callListener(getEventListener(), event);
        }
    }

    /**
     * Returns the event listener type used by this adapter. This method must be
     * defined in concrete sub classes.
     *
     * @return the event listener type
     */
    protected abstract FormListenerType getListenerType();
}
