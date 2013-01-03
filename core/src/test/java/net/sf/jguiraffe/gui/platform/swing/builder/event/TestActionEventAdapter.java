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

import static org.junit.Assert.assertEquals;

import java.awt.event.ActionEvent;

import net.sf.jguiraffe.gui.builder.event.FormActionEvent;
import net.sf.jguiraffe.gui.builder.event.FormActionListener;
import net.sf.jguiraffe.gui.builder.event.FormEventManager;
import net.sf.jguiraffe.gui.builder.event.FormListenerType;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code ActionEventAdapter}.
 *
 * @author Oliver Heger
 * @version $Id: TestActionEventAdapter.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestActionEventAdapter
{
    /** Constant for the test action event. */
    private static final ActionEvent ACTION_EVENT = new ActionEvent(
            new Object(), 42, "testCommand");

    /** The test event manager. */
    private EventManagerImpl eventManager;

    @Before
    public void setUp() throws Exception
    {
        eventManager = new EventManagerImpl();
    }

    /**
     * Tests whether action events can be passed to the event manager.
     */
    @Test
    public void testActionPerformedEventManager()
    {
        ActionEventAdapter adapter = new ActionEventAdapter(eventManager,
                eventManager.getHandler(), EventManagerImpl.NAME);
        adapter.actionPerformed(ACTION_EVENT);
        assertEquals("Wrong listener type", FormListenerType.ACTION,
                eventManager.getType());
        FormActionEvent ev = eventManager.checkFormEvent(FormActionEvent.class,
                ACTION_EVENT);
        assertEquals("Wrong action command", ACTION_EVENT.getActionCommand(),
                ev.getCommand());
    }

    /**
     * Tests whether the adapter can map an event to a specific listener.
     */
    @Test
    public void testActionPerformedSpecificListener()
    {
        FormActionListenerTestImpl l = new FormActionListenerTestImpl();
        ActionEventAdapter adapter = new ActionEventAdapter(l, eventManager
                .getHandler(), EventManagerImpl.NAME);
        adapter.actionPerformed(ACTION_EVENT);
        eventManager.checkFormEvent(l.event, ACTION_EVENT);
        assertEquals("Wrong action command", ACTION_EVENT.getActionCommand(),
                l.event.getCommand());
    }

    /**
     * Tries to create an action adapter without an event manager. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoEventManager()
    {
        new ActionEventAdapter((FormEventManager) null, eventManager
                .getHandler(), EventManagerImpl.NAME);
    }

    /**
     * Tries to create an instance without a listener. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoListener()
    {
        new ActionEventAdapter((FormActionListener) null, eventManager
                .getHandler(), EventManagerImpl.NAME);
    }

    /**
     * A test form action listener implementation.
     */
    private static class FormActionListenerTestImpl implements
            FormActionListener
    {
        /** The received event. */
        FormActionEvent event;

        /**
         * Records this invocation.
         */
        public void actionPerformed(FormActionEvent e)
        {
            event = e;
        }
    }
}
