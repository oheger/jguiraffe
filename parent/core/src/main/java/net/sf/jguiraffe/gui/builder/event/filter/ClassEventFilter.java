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
package net.sf.jguiraffe.gui.builder.event.filter;

import net.sf.jguiraffe.gui.builder.event.BuilderEvent;

/**
 * <p>
 * A specialized <code>EventFilter</code> implementation that filters by an
 * event class.
 * </p>
 * <p>
 * This event filter class can be used if only certain event classes are to be
 * selected and the concrete event type does not matter. For instance, this
 * class allows you to select all types of <code>WindowEvent</code>s.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ClassEventFilter.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ClassEventFilter extends AbstractEventFilter
{
    /**
     * Creates a new instance of <code>ClassEventFilter</code>. The class to
     * filter is set to <code>BuilderEvent</code>, <b>null</b> values wont't
     * be accepted.
     */
    public ClassEventFilter()
    {
        super();
    }

    /**
     * Creates a new instance of <code>ClassEventFilter</code> and initializes
     * it with the class to filter. <b>null</b> values wont't be accepted.
     *
     * @param baseClass the class to filter
     */
    public ClassEventFilter(Class<?> baseClass)
    {
        super(baseClass);
    }

    /**
     * Creates a new instance of <code>ClassEventFilter</code> and initializes
     * it.
     *
     * @param baseClass the class to filter
     * @param acceptNull the <code>acceptNull</code> flag
     */
    public ClassEventFilter(Class<?> baseClass, boolean acceptNull)
    {
        super(baseClass, acceptNull);
    }

    /**
     * Tests the specified event object.
     *
     * @param event the event to test
     * @return a flag if this event is accepted
     */
    @Override
    protected boolean acceptEvent(BuilderEvent event)
    {
        // if we reach this point, the class has already been verified
        return true;
    }
}
