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
package net.sf.jguiraffe.gui.platform.swing.builder.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import net.sf.jguiraffe.gui.builder.event.FormEvent;
import net.sf.jguiraffe.gui.builder.event.FormEventManager;
import net.sf.jguiraffe.gui.builder.event.FormListenerType;
import net.sf.jguiraffe.gui.forms.ComponentHandler;

/**
 * A test implementation of the event manager class that is used to find out
 * whether events are correctly fired.
 *
 * @author Oliver Heger
 * @version $Id: EventManagerImpl.java 205 2012-01-29 18:29:57Z oheger $
 */
class EventManagerImpl extends FormEventManager
{
    /** Constant for the name of a test component. */
    public static final String NAME = "TestComponent";

    /** The event passed to fireEvent().*/
    private FormEvent event;

    /** The listener type passed to fireEvent().*/
    private FormListenerType type;

    /** The expected component handler passed to the events.*/
    private ComponentHandler<?> handler;

    public EventManagerImpl()
    {
        super(null);
    }

    /**
     * Records this invocation.
     */
    @Override
    public void fireEvent(FormEvent event, FormListenerType type)
    {
        this.event = event;
        this.type = type;
    }

    /**
     * Returns the expected component handler. A test handler instance is created
     * when this object is constructed. The handler can also be set to another
     * object. When checking the event passed to fireEvent() this handler is
     * expected.
     * @return the component handler
     */
    public ComponentHandler<?> getHandler()
    {
        return handler;
    }

    /**
     * Sets the expected component handler.
     * @param handler the handler
     */
    public void setHandler(ComponentHandler<?> handler)
    {
        this.handler = handler;
    }

    /**
     * Returns the event that was passed to fireEvent().
     * @return the fired event
     */
    public FormEvent getEvent()
    {
        return event;
    }

    /**
     * Returns the listener type that was passed to fireEvent().
     * @return the listener type
     */
    public FormListenerType getType()
    {
        return type;
    }

    /**
     * Tests the basic properties of the specified event. This method especially
     * checks the component handler and the component name.
     * @param e the event to be checked
     * @param source the expected event source
     */
    public void checkFormEvent(FormEvent e, Object source)
    {
        assertNotNull("Event is null", e);
        assertSame("Wrong source", source, e.getSource());
        assertSame("Wrong component handler", handler, e.getHandler());
        assertEquals("Wrong name", NAME, e.getName());
    }

    /**
     * Checks the fields of a generated form event.
     *
     * @param clazz the expected event class
     * @param source the expected event source
     * @return the event
     */
    public <T extends FormEvent> T checkFormEvent(Class<T> clazz,
            Object source)
    {
        checkFormEvent(event, source);
        assertEquals("Wrong event class", clazz, event.getClass());
        @SuppressWarnings("unchecked")
        T result = (T) event;
        return result;
    }
}