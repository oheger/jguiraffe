/*
 * Copyright 2006-2025 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.event.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import net.sf.jguiraffe.gui.builder.event.BuilderEvent;
import net.sf.jguiraffe.gui.builder.event.FormChangeEvent;
import net.sf.jguiraffe.gui.builder.event.FormEvent;
import net.sf.jguiraffe.gui.builder.window.WindowEvent;
import net.sf.jguiraffe.gui.builder.window.WindowImpl;
import net.sf.jguiraffe.gui.forms.ComponentHandler;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for TypeEventFilter.
 *
 * @author Oliver Heger
 * @version $Id: TestTypeEventFilter.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestTypeEventFilter extends AbstractEventFilterTest
{
    /** Constant for the test event type. */
    private static final String EVENT_TYPE = WindowEvent.Type.WINDOW_OPENED
            .name();

    /**
     * Creates the test filter.
     *
     * @return the test filter
     */
    @Override
    protected AbstractEventFilter createFilter()
    {
        return new TypeEventFilter(EVENT_TYPE);
    }

    /**
     * Tests filtering an event of the correct type.
     */
    @Test
    public void testAcceptEventTrue()
    {
        BuilderEvent event = new WindowEvent(this, new WindowImpl(),
                WindowEvent.Type.WINDOW_OPENED);
        assertTrue("Event not accepted", filter.accept(event));
    }

    /**
     * Tests filtering an event of a wrong type.
     */
    @Test
    public void testAcceptEventWrongType()
    {
        BuilderEvent event = new WindowEvent(this, new WindowImpl(),
                WindowEvent.Type.WINDOW_ACTIVATED);
        assertFalse("Wrong event was accepted", filter.accept(event));
    }

    /**
     * Tests the default constructor.
     */
    @Test
    public void testInitDefault()
    {
        TypeEventFilter filter = new TypeEventFilter();
        assertNull("Got an event type", filter.getEventType());
    }

    /**
     * Tests filtering an event when no type name is set. In this case no event
     * is accepted.
     */
    @Test
    public void testAcceptEventNullType()
    {
        ((TypeEventFilter) filter).setEventType(null);
        assertFalse("Event accepted", filter.accept(new WindowEvent(this,
                new WindowImpl(), WindowEvent.Type.WINDOW_ACTIVATED)));
    }

    /**
     * Tests filtering an event that does not support types. It must not be
     * accepted.
     */
    @Test
    public void testAcceptEventNoType()
    {
        assertFalse("Event with no type accepted", filter
                .accept(new FormChangeEvent(this, EasyMock
                        .createNiceMock(ComponentHandler.class), EVENT_TYPE)));
    }

    /**
     * Tests filtering an event with a getType() method of wrong type.
     */
    @Test
    public void testAcceptEventNoEnumType()
    {
        assertFalse("Event with no enum type accepted", filter
                .accept(new TestTypeEvent(this, EVENT_TYPE)));
    }

    /**
     * Tests filtering an event whose getType() method returns null.
     */
    @Test
    public void testAcceptEventTypeNull()
    {
        assertFalse("Event with null type accepted", filter
                .accept(new TestTypeEvent(this, null)));
    }

    /**
     * An event class with a type property that is no enum.
     */
    @SuppressWarnings("serial")
    private static class TestTypeEvent extends FormEvent
    {
        /** Stores the event type. */
        private String type;

        public TestTypeEvent(Object source, String eventType)
        {
            super(source, EasyMock.createNiceMock(ComponentHandler.class),
                    EVENT_TYPE);
            type = eventType;
        }

        @SuppressWarnings("unused")
        public String getType()
        {
            return type;
        }
    }
}
