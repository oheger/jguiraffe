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

import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

import net.sf.jguiraffe.gui.builder.event.FormEventManager;
import net.sf.jguiraffe.gui.builder.event.FormListenerType;
import net.sf.jguiraffe.gui.builder.event.PlatformEventManager;
import net.sf.jguiraffe.gui.forms.ComponentHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * Swing specific implementation of the <code>PlatformEventManager</code>
 * interface.
 * </p>
 * <p>
 * This class provides functionality for registering event listeners at Swing
 * components. It can deal with <code>ComponentHandler</code> objects have been
 * created by the Swing-specific implementation of the
 * <code>ComponentManager</code> interface; especially they must implement the
 * {@link SwingEventSource} interface. Other {@code ComponentHandler} objects
 * are silently ignored.
 * </p>
 * <p>
 * The Swing-specific implementation of events relies on some features of the
 * {@link FormEventManager} class:
 * <ul>
 * <li>Registration and unregistration of event listeners are synchronized for
 * the same event listener types (i.e. there won't be concurrent calls of
 * <code>registerListener()</code> and/or <code>unregisterListener()</code> for
 * the same listener type).</li>
 * <li>Each component is added only a single event listener of the same type
 * (multiplexing of event listeners is done by the <code>FormEventManager</code>
 * )</li>
 * </ul>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingEventManager.java 205 2012-01-29 18:29:57Z oheger $
 */
public class SwingEventManager implements PlatformEventManager
{
    /** The logger. */
    private final Log log = LogFactory.getLog(getClass());

    /** Stores a map with the so far registered event adapters. */
    private final Map<FormListenerType, Map<String, EventListener>> registeredListeners;

    /**
     * Creates a new instance of <code>SwingEventManager</code>.
     */
    public SwingEventManager()
    {
        registeredListeners = new HashMap<FormListenerType, Map<String, EventListener>>();
        for (FormListenerType type : FormListenerType.values())
        {
            Map<String, EventListener> typeListeners = new HashMap<String, EventListener>();
            registeredListeners.put(type, typeListeners);
        }
    }

    /**
     * Registers the event manager as an event listener at the specified
     * component.
     *
     * @param name the component's name
     * @param handler the component handler
     * @param eventManager the event manager
     * @param type the listener type
     */
    public void registerListener(String name, ComponentHandler<?> handler,
            FormEventManager eventManager, FormListenerType type)
    {
        if (type == null)
        {
            throw new IllegalArgumentException(
                    "Listener type must not be null!");
        }
        SwingEventSource source = obtainSource(handler);
        if (source == null)
        {
            return;
        }
        EventListener listener = null;

        switch (type)
        {
        case ACTION:
            ActionListener al = new ActionEventAdapter(eventManager, handler,
                    name);
            source.addActionListener(al);
            listener = al;
            break;

        case CHANGE:
            ChangeListener cl = new ChangeEventAdapter(eventManager, handler,
                    name);
            source.addChangeListener(cl);
            listener = cl;
            break;

        case FOCUS:
            FocusListener fl = new FocusEventAdapter(eventManager, handler,
                    name);
            source.addFocusListener(fl);
            listener = fl;
            break;

        default:  // must be type MOUSE
            MouseListener ml = new MouseEventAdapter(eventManager, handler,
                    name);
            source.addMouseListener(ml);
            listener = ml;
            break;
        }

        registeredListeners.get(type).put(name, listener);
    }

    /**
     * Unregisters the event listener from the specified component.
     *
     * @param name the component's name
     * @param handler the component handler
     * @param eventManager the event manager
     * @param type the listener type
     */
    public void unregisterListener(String name, ComponentHandler<?> handler,
            FormEventManager eventManager, FormListenerType type)
    {
        if (type == null)
        {
            throw new IllegalArgumentException(
                    "Listener type must not be null!");
        }
        SwingEventSource source = obtainSource(handler);
        if (source == null)
        {
            return;
        }
        EventListener l = registeredListeners.get(type).remove(name);
        assert l != null : "Try to remove non existing listener!";

        switch (type)
        {
        case ACTION:
            source.removeActionListener((ActionListener) l);
            break;
        case CHANGE:
            source.removeChangeListener((ChangeListener) l);
            break;
        case FOCUS:
            source.removeFocusListener((FocusListener) l);
            break;
        default:  // must be type MOUSE
            source.removeMouseListener((MouseListener) l);
            break;
        }
    }

    /**
     * Obtains the {@code SwingEventSource} for the given component handler.
     * This method checks whether the handler is supported. If this is the case,
     * it can be casted to an event source. Otherwise, <b>null</b> is returned.
     *
     * @param handler the {@code ComponentHandler}
     * @return the corresponding {@code SwingEventSource} or <b>null</b>
     */
    private SwingEventSource obtainSource(ComponentHandler<?> handler)
    {
        if (handler instanceof SwingEventSource)
        {
            return (SwingEventSource) handler;
        }

        if (log.isDebugEnabled())
        {
            log.debug("Component handler is no SwingEventSource: " + handler
                    + ". Ignoring.");
        }
        return null;
    }
}
