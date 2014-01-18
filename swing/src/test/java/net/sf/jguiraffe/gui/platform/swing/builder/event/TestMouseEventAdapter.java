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
package net.sf.jguiraffe.gui.platform.swing.builder.event;

import static org.junit.Assert.assertEquals;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import net.sf.jguiraffe.gui.builder.event.FormEventManager;
import net.sf.jguiraffe.gui.builder.event.FormMouseEvent;
import net.sf.jguiraffe.gui.builder.event.FormMouseListener;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code MouseEventAdapter}.
 *
 * @author Oliver Heger
 * @version $Id: TestMouseEventAdapter.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestMouseEventAdapter
{
    /** Constant for the test mouse event. */
    private static final MouseEvent MOUSE_EVENT = new MouseEvent(new JLabel(),
            42, System.currentTimeMillis(), InputEvent.SHIFT_DOWN_MASK
                    | InputEvent.CTRL_DOWN_MASK, 320, 200, 1, false,
            MouseEvent.BUTTON2);

    /** Constant for the test mouse event used for double clicks. */
    private static final MouseEvent MOUSE_DOUBLE_EVENT = new MouseEvent(
            new JLabel(), 42, System.currentTimeMillis(),
            InputEvent.ALT_DOWN_MASK | InputEvent.ALT_GRAPH_DOWN_MASK
                    | InputEvent.META_DOWN_MASK, 160, 100, 2, false,
            MouseEvent.BUTTON1);

    /** The test event manager. */
    private EventManagerImpl eventManager;

    @Before
    public void setUp() throws Exception
    {
        eventManager = new EventManagerImpl();
    }

    /**
     * Tests the properties of the specified mouse event.
     *
     * @param event the event to be checked
     * @param type the expected event type
     * @param srcEvent the original Swing mouse event
     */
    private static void checkMouseEvent(FormMouseEvent event,
            FormMouseEvent.Type type, MouseEvent srcEvent)
    {
        assertEquals("Wrong event type", type, event.getType());
        assertEquals("Wrong X", srcEvent.getX(), event.getX());
        assertEquals("Wrong Y", srcEvent.getY(), event.getY());
        assertEquals("Wrong button", SwingEventConstantMapper
                .convertSwingButtons(srcEvent.getButton()), event.getButton());
        assertEquals("Wrong modifiers", SwingEventConstantMapper
                .convertSwingModifiers(srcEvent.getModifiers()), event
                .getModifiers());
    }

    /**
     * Tests the properties of the current event received by the event manager.
     *
     * @param type the expected event type
     * @param srcEvent the original Swing mouse event
     */
    private void checkMouseEvent(FormMouseEvent.Type type, MouseEvent srcEvent)
    {
        FormMouseEvent event = eventManager.checkFormEvent(
                FormMouseEvent.class, srcEvent);
        checkMouseEvent(event, type, srcEvent);
    }

    /**
     * Tests the properties of the event received by the test listener.
     *
     * @param l the listener
     * @param type the expected event type
     * @param srcEvent the original Swing mouse event
     */
    private void checkMouseListener(FormMouseListenerTestImpl l,
            FormMouseEvent.Type type, MouseEvent srcEvent)
    {
        eventManager.checkFormEvent(l.formEvent, srcEvent);
        checkMouseEvent(l.formEvent, type, srcEvent);
    }

    /**
     * Creates a test adapter associated with the event manager.
     *
     * @return the test adapter
     */
    private MouseEventAdapter setUpAdapter()
    {
        return new MouseEventAdapter(eventManager, eventManager.getHandler(),
                EventManagerImpl.NAME);
    }

    /**
     * Creates a test adapter associated with an event listener.
     *
     * @param l the listener
     * @return the test adapter
     */
    private MouseEventAdapter setUpAdapter(FormMouseListener l)
    {
        return new MouseEventAdapter(l, eventManager.getHandler(),
                EventManagerImpl.NAME);
    }

    /**
     * Tests whether a mouse entered event is correctly passed to the event
     * manager.
     */
    @Test
    public void testMouseEnteredEventManager()
    {
        MouseEventAdapter adapter = setUpAdapter();
        adapter.mouseEntered(MOUSE_EVENT);
        checkMouseEvent(FormMouseEvent.Type.MOUSE_ENTERED, MOUSE_EVENT);
    }

    /**
     * Tests whether a mouse exited event is correctly passed to the event
     * manager.
     */
    @Test
    public void testMouseExitedEventManager()
    {
        MouseEventAdapter adapter = setUpAdapter();
        adapter.mouseExited(MOUSE_EVENT);
        checkMouseEvent(FormMouseEvent.Type.MOUSE_EXITED, MOUSE_EVENT);
    }

    /**
     * Tests whether a mouse pressed event is correctly passed to the event
     * manager.
     */
    @Test
    public void testMousePressedEventManager()
    {
        MouseEventAdapter adapter = setUpAdapter();
        adapter.mousePressed(MOUSE_EVENT);
        checkMouseEvent(FormMouseEvent.Type.MOUSE_PRESSED, MOUSE_EVENT);
    }

    /**
     * Tests whether a mouse released event is correctly passed to the event
     * manager.
     */
    @Test
    public void testMouseReleasedEventManager()
    {
        MouseEventAdapter adapter = setUpAdapter();
        adapter.mouseReleased(MOUSE_EVENT);
        checkMouseEvent(FormMouseEvent.Type.MOUSE_RELEASED, MOUSE_EVENT);
    }

    /**
     * Tests whether a mouse clicked event is correctly passed to the event
     * manager.
     */
    @Test
    public void testMouseClickedEventManager()
    {
        MouseEventAdapter adapter = setUpAdapter();
        adapter.mouseClicked(MOUSE_EVENT);
        checkMouseEvent(FormMouseEvent.Type.MOUSE_CLICKED, MOUSE_EVENT);
    }

    /**
     * Tests whether a mouse double clicked event is correctly passed to the
     * event manager.
     */
    @Test
    public void testMouseClickedDoubleEventManager()
    {
        MouseEventAdapter adapter = setUpAdapter();
        adapter.mouseClicked(MOUSE_DOUBLE_EVENT);
        checkMouseEvent(FormMouseEvent.Type.MOUSE_DOUBLE_CLICKED,
                MOUSE_DOUBLE_EVENT);
    }

    /**
     * Tests whether a mouse entered event is correctly passed to an event
     * listener.
     */
    @Test
    public void testMouseEnteredListener()
    {
        FormMouseListenerTestImpl l = new FormMouseListenerTestImpl();
        MouseEventAdapter adapter = setUpAdapter(l);
        adapter.mouseEntered(MOUSE_EVENT);
        checkMouseListener(l, FormMouseEvent.Type.MOUSE_ENTERED, MOUSE_EVENT);
    }

    /**
     * Tests whether a mouse exited event is correctly passed to an event
     * listener.
     */
    @Test
    public void testMouseExitedListener()
    {
        FormMouseListenerTestImpl l = new FormMouseListenerTestImpl();
        MouseEventAdapter adapter = setUpAdapter(l);
        adapter.mouseExited(MOUSE_EVENT);
        checkMouseListener(l, FormMouseEvent.Type.MOUSE_EXITED, MOUSE_EVENT);
    }

    /**
     * Tests whether a mouse pressed event is correctly passed to an event
     * listener.
     */
    @Test
    public void testMousePressedListener()
    {
        FormMouseListenerTestImpl l = new FormMouseListenerTestImpl();
        MouseEventAdapter adapter = setUpAdapter(l);
        adapter.mousePressed(MOUSE_EVENT);
        checkMouseListener(l, FormMouseEvent.Type.MOUSE_PRESSED, MOUSE_EVENT);
    }

    /**
     * Tests whether a mouse released event is correctly passed to an event
     * listener.
     */
    @Test
    public void testMouseReleasedListener()
    {
        FormMouseListenerTestImpl l = new FormMouseListenerTestImpl();
        MouseEventAdapter adapter = setUpAdapter(l);
        adapter.mouseReleased(MOUSE_EVENT);
        checkMouseListener(l, FormMouseEvent.Type.MOUSE_RELEASED, MOUSE_EVENT);
    }

    /**
     * Tests whether a mouse clicked event is correctly passed to an event
     * listener.
     */
    @Test
    public void testMouseClickedListener()
    {
        FormMouseListenerTestImpl l = new FormMouseListenerTestImpl();
        MouseEventAdapter adapter = setUpAdapter(l);
        adapter.mouseClicked(MOUSE_EVENT);
        checkMouseListener(l, FormMouseEvent.Type.MOUSE_CLICKED, MOUSE_EVENT);
    }

    /**
     * Tests whether a mouse double clicked event is correctly passed to an
     * event listener.
     */
    @Test
    public void testMouseClickedDoubleListener()
    {
        FormMouseListenerTestImpl l = new FormMouseListenerTestImpl();
        MouseEventAdapter adapter = setUpAdapter(l);
        adapter.mouseClicked(MOUSE_DOUBLE_EVENT);
        checkMouseListener(l, FormMouseEvent.Type.MOUSE_DOUBLE_CLICKED,
                MOUSE_DOUBLE_EVENT);
    }

    /**
     * Tries to create an instance without an event manager. This should cause
     * an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoEventManager()
    {
        new MouseEventAdapter((FormEventManager) null, eventManager
                .getHandler(), EventManagerImpl.NAME);
    }

    /**
     * Tries to create an instance without an event listener. This should cause
     * an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoEventListener()
    {
        new MouseEventAdapter((FormMouseListener) null, eventManager
                .getHandler(), EventManagerImpl.NAME);
    }

    /**
     * A test mouse listener implementation for testing whether correct events
     * are received.
     */
    private static class FormMouseListenerTestImpl implements FormMouseListener
    {
        /** The event that was received. */
        FormMouseEvent formEvent;

        public void mouseClicked(FormMouseEvent event)
        {
            formEvent = event;
        }

        public void mouseDoubleClicked(FormMouseEvent event)
        {
            formEvent = event;
        }

        public void mouseEntered(FormMouseEvent event)
        {
            formEvent = event;
        }

        public void mouseExited(FormMouseEvent event)
        {
            formEvent = event;
        }

        public void mousePressed(FormMouseEvent event)
        {
            formEvent = event;
        }

        public void mouseReleased(FormMouseEvent event)
        {
            formEvent = event;
        }
    }
}
