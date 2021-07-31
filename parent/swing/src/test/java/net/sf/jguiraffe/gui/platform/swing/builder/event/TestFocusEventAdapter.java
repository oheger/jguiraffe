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

import static org.junit.Assert.assertEquals;

import java.awt.event.FocusEvent;

import javax.swing.JTextField;

import net.sf.jguiraffe.gui.builder.event.FormEventManager;
import net.sf.jguiraffe.gui.builder.event.FormFocusEvent;
import net.sf.jguiraffe.gui.builder.event.FormFocusListener;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code FocusEventAdapter}.
 *
 * @author Oliver Heger
 * @version $Id: TestFocusEventAdapter.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestFocusEventAdapter
{
    /** Constant for the test focus event. */
    private static final FocusEvent FOCUS_EVENT = new FocusEvent(
            new JTextField(), 42);

    /** The test event manager. */
    private EventManagerImpl eventManager;

    @Before
    public void setUp() throws Exception
    {
        eventManager = new EventManagerImpl();
    }

    /**
     * Checks the specified focus event.
     *
     * @param event the event to be checked
     * @param type the expected type
     */
    private void checkFocusEvent(FormFocusEvent event, FormFocusEvent.Type type)
    {
        assertEquals("Wrong event type", type, event.getType());
    }

    /**
     * Checks the properties of a focus event.
     *
     * @param type the expected focus type
     */
    private void checkFocusEvent(FormFocusEvent.Type type)
    {
        FormFocusEvent event = eventManager.checkFormEvent(
                FormFocusEvent.class, FOCUS_EVENT);
        checkFocusEvent(event, type);
    }

    /**
     * Checks whether the specified listener has received the expected event.
     *
     * @param l the listener
     * @param type the expected event type
     */
    private void checkFocusListener(FormFocusListenerTestImpl l,
            FormFocusEvent.Type type)
    {
        eventManager.checkFormEvent(l.event, FOCUS_EVENT);
        checkFocusEvent(l.event, type);
    }

    /**
     * Tests whether focus gained events are correctly delivered to the event
     * manager.
     */
    @Test
    public void testFocusGainedEventManager()
    {
        FocusEventAdapter adapter = new FocusEventAdapter(eventManager,
                eventManager.getHandler(), EventManagerImpl.NAME);
        adapter.focusGained(FOCUS_EVENT);
        checkFocusEvent(FormFocusEvent.Type.FOCUS_GAINED);
    }

    /**
     * Tests whether a focus lost event is correctly delivered to the event
     * manager.
     */
    @Test
    public void testFocusLostEventManager()
    {
        FocusEventAdapter adapter = new FocusEventAdapter(eventManager,
                eventManager.getHandler(), EventManagerImpl.NAME);
        adapter.focusLost(FOCUS_EVENT);
        checkFocusEvent(FormFocusEvent.Type.FOCUS_LOST);
    }

    /**
     * Tests whether a focus gained event is correctly delivered to a listener.
     */
    @Test
    public void testFocusGainedEventListener()
    {
        FormFocusListenerTestImpl l = new FormFocusListenerTestImpl();
        FocusEventAdapter adapter = new FocusEventAdapter(l, eventManager
                .getHandler(), EventManagerImpl.NAME);
        adapter.focusGained(FOCUS_EVENT);
        checkFocusListener(l, FormFocusEvent.Type.FOCUS_GAINED);
    }

    /**
     * Tests whether a focus lost event is correctly delivered to a listener.
     */
    @Test
    public void testFocusLostEventListener()
    {
        FormFocusListenerTestImpl l = new FormFocusListenerTestImpl();
        FocusEventAdapter adapter = new FocusEventAdapter(l, eventManager
                .getHandler(), EventManagerImpl.NAME);
        adapter.focusLost(FOCUS_EVENT);
        checkFocusListener(l, FormFocusEvent.Type.FOCUS_LOST);
    }

    /**
     * Tries to create a focus adapter without an event manager. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoEventManager()
    {
        new FocusEventAdapter((FormEventManager) null, eventManager
                .getHandler(), EventManagerImpl.NAME);
    }

    /**
     * Tries to create an instance without a listener. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoListener()
    {
        new FocusEventAdapter((FormFocusListener) null, eventManager
                .getHandler(), EventManagerImpl.NAME);
    }

    /**
     * A test focus listener implementation for testing whether focus events are
     * correctly received.
     */
    private static class FormFocusListenerTestImpl implements FormFocusListener
    {
        /** The received event. */
        FormFocusEvent event;

        /**
         * Records this invocation.
         */
        public void focusGained(FormFocusEvent e)
        {
            event = e;
        }

        /**
         * Records this invocation.
         */
        public void focusLost(FormFocusEvent e)
        {
            event = e;
        }
    }
}
