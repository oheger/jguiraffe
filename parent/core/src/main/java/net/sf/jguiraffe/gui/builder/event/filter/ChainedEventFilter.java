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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * An abstract base class for filters that operate on multiple filters.
 * </p>
 * <p>
 * This class can be used as base class for filters that combine the results of
 * other filters. It supports methods for adding and managing an arbitrary
 * number of (child) filters. The <code>accept()</code> method is not
 * implemented; this is left for concrete subclasses.
 * </p>
 * <p>
 * A concrete implementation for instance could invoke its child filters and
 * return only <b>true</b> if all child filters accept the object in question.
 * This would be the implementation of an AND semantics.
 * </p>
 * <p>
 * Implementation note: chained filters are not thread-safe. If they are
 * accessed concurrently by different threads synchronization has to be ensured
 * by the developer.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ChainedEventFilter.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class ChainedEventFilter implements EventFilter
{
    /** Stores a list with the managed child filters. */
    private final List<EventFilter> filters;

    /**
     * Creates a new instance of <code>ChainedEventFilter</code>.
     */
    protected ChainedEventFilter()
    {
        filters = new ArrayList<EventFilter>();
    }

    /**
     * Creates a new instance of <code>ChainedEventFilter</code> and initializes
     * it with the given list of child filters. The passed in collection must be
     * non <b>null</b> and must not contain any <b>null</b> references.
     *
     * @param childFilters a collection with the child filters (must not be
     *        <b>null</b>)
     * @throws IllegalArgumentException if the collection with child filters is
     *         <b>null</b> or contains a <b>null</b> element
     */
    protected ChainedEventFilter(Collection<EventFilter> childFilters)
    {
        if (childFilters == null)
        {
            throw new IllegalArgumentException(
                    "List of child filters must not be null!");
        }

        filters = new ArrayList<EventFilter>(childFilters);
        checkChildFilters(filters);
    }

    /**
     * Returns a list with all contained filters. This list is never
     * <b>null</b>.
     *
     * @return a (unmodifiable) list with the contained filters
     */
    public List<EventFilter> getFilters()
    {
        return Collections.unmodifiableList(filters);
    }

    /**
     * Returns an iterator to the internal list of child filters. This method is
     * intended to be called by sub classes for iterating over the list of child
     * filters.
     *
     * @return an iterator for iterating over all contained filters
     */
    protected Iterator<EventFilter> getFilterIterator()
    {
        return filters.iterator();
    }

    /**
     * Adds the given filter to this chained filter.
     *
     * @param filter the filter to be added (must not be <b>null</b>)
     * @throws IllegalArgumentException if the filter is <b>null</b>
     */
    public void addFilter(EventFilter filter)
    {
        if (filter == null)
        {
            throw new IllegalArgumentException("Child filter must not be null!");
        }

        filters.add(filter);
    }

    /**
     * Adds all filters in the given collection to this chained filter. The
     * collection must contain non <b>null</b> objects that implement the
     * {@link EventFilter} interface.
     *
     * @param childFilters the collection with the filters to add (must not be
     *        <b>null</b>)
     * @throws IllegalArgumentException if the collection is <b>null</b> or
     *         contains a <b>null</b> reference
     */
    public void addFilters(Collection<EventFilter> childFilters)
    {
        if (childFilters == null)
        {
            throw new IllegalArgumentException(
                    "Filter collection must not be null!");
        }

        // Make a defensive copy
        Collection<EventFilter> children = new ArrayList<EventFilter>(
                childFilters);
        checkChildFilters(children);
        filters.addAll(children);
    }

    /**
     * Removes all child filters from this chained filter.
     */
    public void clear()
    {
        filters.clear();
    }

    /**
     * Tests that the collection with the child filters does not contain a
     * <b>null</b> reference. If one is found, an exception is thrown.
     *
     * @param childFilters the collection to check
     * @throws IllegalArgumentException if the collection contains a <b>null</b>
     *         entry
     */
    private void checkChildFilters(Collection<EventFilter> childFilters)
    {
        for (EventFilter f : childFilters)
        {
            if (f == null)
            {
                throw new IllegalArgumentException(
                        "Null child filters are not allowed!");
            }
        }
    }
}
