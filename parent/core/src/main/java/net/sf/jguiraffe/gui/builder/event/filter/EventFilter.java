/*
 * Copyright 2006-2016 The JGUIraffe Team.
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

/**
 * <p>
 * Definition of an interface for filtering events.
 * </p>
 * <p>
 * This interface defines a simple method for testing whether a passed in object
 * is accepted by the filter. Note that objects processed by an event filter
 * are of type <code>Object</code> rather than an event type; this may be
 * useful for special cases (e.g. if the objects dealt with are not
 * always event objects) and is included here to gain a greater flexibility. Simple
 * event filters that do not need such advanced processing should subclass the
 * <code>{@link AbstractEventFilter}</code> base class, which hides this
 * complexity.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: EventFilter.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface EventFilter
{
    /**
     * Tests whether the passed in object is accepted by this filter. This is
     * the main method for filtering. Note that it deals with the generic Object
     * type rather than an event type. Before this method is called the other
     * &quot;declarative&quot; methods are invoked to find out the minimum
     * criteria for supported objects. Only if these criteria are matched, the
     * object is passed to the filter method.
     *
     * @param obj the object to be tested
     * @return a flag whether the passed in object is accepted
     */
    boolean accept(Object obj);
}
