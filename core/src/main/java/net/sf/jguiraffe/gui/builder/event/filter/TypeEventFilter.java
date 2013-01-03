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
package net.sf.jguiraffe.gui.builder.event.filter;

import java.lang.reflect.Method;

import net.sf.jguiraffe.gui.builder.event.BuilderEvent;

/**
 * <p>
 * A specialized {@code EventFilter} implementation that filters by an event
 * type.
 * </p>
 * <p>
 * This event filter class allows to filter by concrete event (sub) types. So a
 * developer can specify in a fine-grained way, which events should pass the
 * filter. For instance it is possible to filter only for window events of type
 * {@code WINDOW_OPENED}.
 * </p>
 * <p>
 * Instances of this class are initialized with the name of the type to filter
 * for. It is not necessary to specify a specific event class because event type
 * names should be unique. Because not every event class supports sub types this
 * filter implementation uses reflection for obtaining the type. An event object
 * is accepted only if
 * <ul>
 * <li>it has a public method named {@code getType()}</li>
 * <li>invocation of the {@code getType()} method does not cause an exception
 * and returns an {@code Enum} object</li>
 * <li>the name of the {@code Enum} object matches the type name this filter was
 * initialized with.</li>
 * </ul>
 * Any exceptions related to reflection that occur while checking whether the
 * event is accepted cause the event to be not accepted.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TypeEventFilter.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TypeEventFilter extends AbstractEventFilter
{
    /** Constant for the name of the getType() method. */
    private static final String METHOD_TYPE = "getType";

    /** Stores the name of the event type. */
    private String eventType;

    /**
     * Creates a new instance of <code>TypeEventFilter</code>. The event type is
     * set to the default type. <b>null</b> values are not accepted.
     */
    public TypeEventFilter()
    {
        this(null);
    }

    /**
     * Creates a new instance of <code>TypeEventFilter</code> and sets the name
     * of the event type to be filtered. If the type is <b>null</b>, no events
     * are accepted.
     *
     * @param type the name of the event type
     */
    public TypeEventFilter(String type)
    {
        super();
        setEventType(type);
    }

    /**
     * Tests the given event. This implementation compares the event's type with
     * the configured event type.
     *
     * @param event the event to be tested
     * @return a flag if this event is accepted
     */
    @Override
    protected boolean acceptEvent(BuilderEvent event)
    {
        if (getEventType() == null)
        {
            return false;
        }
        assert event != null : "Event is null!";

        Class<?> eventClass = event.getClass();
        try
        {
            Method method = eventClass.getMethod(METHOD_TYPE);
            Object eventType = method.invoke(event);
            if (eventType instanceof Enum<?>)
            {
                return getEventType().equals(((Enum<?>) eventType).name());
            }
        }
        catch (Exception ex)
        {
            // All exceptions cause the event to be not accepted
        }

        return false;
    }

    /**
     * Returns the event type name.
     *
     * @return the event type
     */
    public String getEventType()
    {
        return eventType;
    }

    /**
     * Sets the event type name. Only events with this type can pass this
     * filter. If the type is <b>null</b>, no events are accepted.
     *
     * @param eventType the event type
     */
    public void setEventType(String eventType)
    {
        this.eventType = eventType;
    }
}
