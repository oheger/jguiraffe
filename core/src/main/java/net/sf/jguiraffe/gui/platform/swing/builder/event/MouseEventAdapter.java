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
package net.sf.jguiraffe.gui.platform.swing.builder.event;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import net.sf.jguiraffe.gui.builder.event.FormEventManager;
import net.sf.jguiraffe.gui.builder.event.FormListenerType;
import net.sf.jguiraffe.gui.builder.event.FormMouseEvent;
import net.sf.jguiraffe.gui.builder.event.FormMouseListener;
import net.sf.jguiraffe.gui.forms.ComponentHandler;

/**
 * <p>
 * A specific Swing event adapter implementation that deals with mouse events.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: MouseEventAdapter.java 205 2012-01-29 18:29:57Z oheger $
 */
public class MouseEventAdapter extends SwingEventAdapter implements MouseListener
{
    /**
     * Creates a new instance of {@code MouseEventAdapter} that passes the
     * events it receives to the specified {@code FormMouseListener}.
     *
     * @param l the {@code FormMouseListener} (must not be <b>null</b>)
     * @param handler the {@code ComponentHandler}
     * @param name the name of the component
     * @throws IllegalArgumentException if the {@code FormMouseListener} is
     *         <b>null</b>
     */
    public MouseEventAdapter(FormMouseListener l, ComponentHandler<?> handler,
            String name)
    {
        super(l, handler, name);
    }

    /**
     * Creates a new instance of {@code MouseEventAdapter} that passes the
     * events it receives to the {@code FormEventManager}.
     *
     * @param eventManager the {@code FormEventManager} (must not be
     *        <b>null</b>)
     * @param handler the {@code ComponentHandler}
     * @param name the name of the component
     * @throws IllegalArgumentException if the {@code FormEventManager} is
     *         <b>null</b>
     */
    public MouseEventAdapter(FormEventManager eventManager,
            ComponentHandler<?> handler, String name)
    {
        super(eventManager, handler, name);
    }

    /**
     * Notifies this listener about a mouse entered event. This implementation
     * checks the {@code clickCount} property to find out whether this is a
     * normal click or a double click. This determines the type of the
     * corresponding {@code FormMouseEvent}. Then such a {@code FormMouseEvent}
     * is created and passed to the
     * {@link #fireEvent(net.sf.jguiraffe.gui.builder.event.FormEvent)} method.
     *
     * @param event the Swing mouse event
     */
    public void mouseClicked(MouseEvent event)
    {
        fireEvent(createEvent(
                event,
                (event.getClickCount() > 1) ? FormMouseEvent.Type.MOUSE_DOUBLE_CLICKED
                        : FormMouseEvent.Type.MOUSE_CLICKED));
    }

    /**
     * Notifies this listener about a mouse entered event. This implementation
     * creates a corresponding {@code FormMouseEvent} and passes it to the
     * {@link #fireEvent(net.sf.jguiraffe.gui.builder.event.FormEvent)} method.
     *
     * @param event the Swing mouse event
     */
    public void mouseEntered(MouseEvent event)
    {
        fireEvent(createEvent(event, FormMouseEvent.Type.MOUSE_ENTERED));
    }

    /**
     * Notifies this listener about a mouse exited event. This implementation
     * creates a corresponding {@code FormMouseEvent} and passes it to the
     * {@link #fireEvent(net.sf.jguiraffe.gui.builder.event.FormEvent)} method.
     *
     * @param event the Swing mouse event
     */
    public void mouseExited(MouseEvent event)
    {
        fireEvent(createEvent(event, FormMouseEvent.Type.MOUSE_EXITED));
    }

    /**
     * Notifies this listener about a mouse pressed event. This implementation
     * creates a corresponding {@code FormMouseEvent} and passes it to the
     * {@link #fireEvent(net.sf.jguiraffe.gui.builder.event.FormEvent)} method.
     *
     * @param event the Swing mouse event
     */
    public void mousePressed(MouseEvent event)
    {
        fireEvent(createEvent(event, FormMouseEvent.Type.MOUSE_PRESSED));
    }

    /**
     * Notifies this listener about a mouse released event. This implementation
     * creates a corresponding {@code FormMouseEvent} and passes it to the
     * {@link #fireEvent(net.sf.jguiraffe.gui.builder.event.FormEvent)} method.
     *
     * @param event the Swing mouse event
     */
    public void mouseReleased(MouseEvent event)
    {
        fireEvent(createEvent(event, FormMouseEvent.Type.MOUSE_RELEASED));
    }

    /**
     * Returns the {@code FormListenerType} for this event adapter. This
     * implementation returns the type for mouse listeners.
     *
     * @return the {@code FormListenerType}
     */
    @Override
    protected FormListenerType getListenerType()
    {
        return FormListenerType.MOUSE;
    }

    /**
     * Creates a {@code FormMouseEvent} from the specified source Swing event
     * using the given event type.
     *
     * @param srcEvent the source Swing event
     * @param type the event type
     * @return the new {@code FormMouseEvent}
     */
    protected FormMouseEvent createEvent(MouseEvent srcEvent,
            FormMouseEvent.Type type)
    {
        return new FormMouseEvent(srcEvent, getHandler(), getName(), type,
                srcEvent.getX(), srcEvent.getY(), SwingEventConstantMapper
                        .convertSwingButtons(srcEvent.getButton()),
                SwingEventConstantMapper.convertSwingModifiers(srcEvent
                        .getModifiers()));
    }
}
