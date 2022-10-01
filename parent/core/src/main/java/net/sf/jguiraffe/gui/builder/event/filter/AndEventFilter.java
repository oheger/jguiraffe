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
 * A specialized chained filter implementation that provides an AND or ALL
 * semantics.
 * </p>
 * <p>
 * This filter class implements the <code>accept()</code> method in a way that
 * it returns <b>true</b> only if all of the child filters return <b>true</b>.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: AndEventFilter.java 205 2012-01-29 18:29:57Z oheger $
 */
public class AndEventFilter extends ChainedEventFilter
{
    /**
     * Creates a new, empty instance of <code>AndEventFilter</code>.
     */
    public AndEventFilter()
    {
        super();
    }

    /**
     * Creates a new instance of <code>AndEventFilter</code> and initializes it
     * with the given list of child filters. The collection must contain non
     * <b>null</b> objects implementing the <code>EventFilter</code> interface.
     *
     * @param childFilters the child filters (must not be <b>null</b>
     * @throws IllegalArgumentException if the collection with child filters is
     *         <b>null</b> or contains <b>null</b> references
     */
    public AndEventFilter(Collection<EventFilter> childFilters)
    {
        super(childFilters);
    }

    /**
     * Tests the specified object whether it is accepted by this filter. This
     * implementation iterates over the child filters. When the first child
     * returns <b>false</b> iteration is aborted, and the method's result is
     * <b>false</b>. If all child filters return <b>true</b>, or the filter has
     * no child filters, result is <b>true</b>.
     *
     * @param obj the object to be tested
     * @return a flag whether this object is accepted
     */
    public boolean accept(Object obj)
    {
        Iterator<EventFilter> it = getFilterIterator();
        boolean result = true;

        while (it.hasNext() && result)
        {
            result = ((EventFilter) it.next()).accept(obj);
        }

        return result;
    }
}
