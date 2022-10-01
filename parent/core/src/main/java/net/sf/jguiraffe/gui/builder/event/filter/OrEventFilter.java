/*
 * Copyright 2006-2022 The JGUIraffe Team.
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

import java.util.Collection;
import java.util.Iterator;

/**
 * <p>
 * A specialized chained filter implementation that provides a OR or ANY
 * semantics.
 * </p>
 * <p>
 * This filter class implements the <code>accept()</code> method in a way that
 * it returns <b>true</b> if at least one of the child filters returns
 * <b>true</b>.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: OrEventFilter.java 205 2012-01-29 18:29:57Z oheger $
 */
public class OrEventFilter extends ChainedEventFilter
{
    /**
     * Creates a new instance of <code>OrEventFilter</code> that has no child
     * filters.
     */
    public OrEventFilter()
    {
        super();
    }

    /**
     * Creates a new instance of <code>OrEventFilter</code> and initializes it
     * with the given list of child filters. The collection must contain non
     * <b>null</b> objects implementing the <code>EventFilter</code> interface.
     *
     * @param childFilters the child filters (must not be <b>null</b>)
     * @throws IllegalArgumentException if the list with child filters is
     *         <b>null</b>
     */
    public OrEventFilter(Collection<EventFilter> childFilters)
    {
        super(childFilters);
    }

    /**
     * Tests the specified object. This implementation iterates over all child
     * filters and passes the object to be tested to each. When the first child
     * filter returns <b>true</b>, the iteration is aborted and the result
     * <b>true</b> is returned. Otherwise the result is <b>false</b>. This
     * implies that a filter with an empty list of children will return
     * <b>false</b>.
     *
     * @param obj the object to be tested
     * @return a flag if this object is accepted by this filter
     */
    public boolean accept(Object obj)
    {
        boolean result = false;
        Iterator<EventFilter> it = getFilterIterator();

        while (it.hasNext() && !result)
        {
            result = ((EventFilter) it.next()).accept(obj);
        }

        return result;
    }
}
