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
package net.sf.jguiraffe.gui.platform.swing.builder.event;

import java.awt.List;
import java.awt.event.ItemEvent;

import net.sf.jguiraffe.gui.builder.event.FormChangeEvent;
import net.sf.jguiraffe.gui.builder.event.FormChangeListener;
import net.sf.jguiraffe.gui.builder.event.FormEventManager;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code ChangeEventAdapter}.
 *
 * @author Oliver Heger
 * @version $Id: TestChangeEventAdapter.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestChangeEventAdapter
{
    /** Constant for the test change event. */
    private static final ItemEvent CHANGE_EVENT = new ItemEvent(new List(), 42,
            "Test", ItemEvent.SELECTED);

    /** The test event manager. */
    private EventManagerImpl eventManager;

    @Before
    public void setUp() throws Exception
    {
        eventManager = new EventManagerImpl();
    }

    /**
     * Tests whether change events can be passed to the event manager.
     */
    @Test
    public void testComponentChangedEventManager()
    {
        ChangeEventAdapter adapter = new ChangeEventAdapter(eventManager,
                eventManager.getHandler(), EventManagerImpl.NAME);
        adapter.componentChanged(CHANGE_EVENT);
        eventManager.checkFormEvent(FormChangeEvent.class, CHANGE_EVENT);
    }

    /**
     * Tests whether change events can be passed to a specific event listener.
     */
    @Test
    public void testComponentChangedEventListener()
    {
        FormChangeListenerTestImpl l = new FormChangeListenerTestImpl();
        ChangeEventAdapter adapter = new ChangeEventAdapter(l, eventManager
                .getHandler(), EventManagerImpl.NAME);
        adapter.componentChanged(CHANGE_EVENT);
        eventManager.checkFormEvent(l.event, CHANGE_EVENT);
    }

    /**
     * Tries to create a change adapter without an event manager. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoEventManager()
    {
        new ChangeEventAdapter((FormEventManager) null, eventManager
                .getHandler(), EventManagerImpl.NAME);
    }

    /**
     * Tries to create an instance without a listener. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoListener()
    {
        new ChangeEventAdapter((FormChangeListener) null, eventManager
                .getHandler(), EventManagerImpl.NAME);
    }

    /**
     * A test listener implementation for testing whether change events are
     * correctly sent.
     */
    private static class FormChangeListenerTestImpl implements
            FormChangeListener
    {
        /** The received change event. */
        FormChangeEvent event;

        /**
         * Records this invocation.
         */
        public void elementChanged(FormChangeEvent e)
        {
            event = e;
        }
    }
}
