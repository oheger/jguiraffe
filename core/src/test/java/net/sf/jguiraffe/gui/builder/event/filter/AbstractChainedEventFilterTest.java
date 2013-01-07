/*
 * Copyright 2006-2013 The JGUIraffe Team.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Base test class for classes derived from ChainedEventFilter. This class
 * provides some test cases for accessing the child filters.
 *
 * @author Oliver Heger
 * @version $Id: AbstractChainedEventFilterTest.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class AbstractChainedEventFilterTest
{
    /** Constant for an object that can be passed to the test filter. */
    protected static final Object TEST_OBJ = new Object();

    /**
     * Creates a chained filter implementation that does not contain any child
     * filters.
     *
     * @return an empty chained filter
     */
    protected abstract ChainedEventFilter createEmptyFilter();

    /**
     * Creates a chained filter implementation that is initialized with the
     * given list of child filters.
     *
     * @param children the list with child filters
     * @return the chained filter
     */
    protected abstract ChainedEventFilter createFilterWithChildren(
            Collection<EventFilter> children);

    /**
     * Adds a filter mock as child to the given chained filter. This can be used
     * to add filters with a defined behavior to the chained filter.
     *
     * @param cf the chained filter (can be <b>null</b>)
     * @param accept a flag for the return value of the mock filter's accept()
     *        method
     * @param init if <b>true</b>, the mock is initialized to expect an
     *        invocation of the accept() method and its replay() method is
     *        called
     * @return the control object for the filter mock
     */
    protected EventFilter addMockFilter(ChainedEventFilter cf, boolean accept,
            boolean init)
    {
        EventFilter mockFilter = EasyMock.createMock(EventFilter.class);
        if (init)
        {
            EasyMock.expect(mockFilter.accept(TEST_OBJ)).andReturn(
                    Boolean.valueOf(accept));
            EasyMock.replay(mockFilter);
        }
        if (cf != null)
        {
            cf.addFilter(mockFilter);
        }

        return mockFilter;
    }

    /**
     * Helper method for creating a dummy filter. This method returns an
     * implementation of the <code>EventFilter</code> interface without any
     * specific functionality.
     *
     * @return a filter dummy
     */
    protected EventFilter createDummyFilter()
    {
        return addMockFilter(null, false, false);
    }

    /**
     * Creates a collection with dummy filters of the given size.
     *
     * @param size the size
     * @return a collection containing dummy filters
     */
    protected Collection<EventFilter> createDummyFilterList(int size)
    {
        Collection<EventFilter> children = new ArrayList<EventFilter>(size);
        for (int i = 0; i < size; i++)
        {
            children.add(createDummyFilter());
        }
        return children;
    }

    /**
     * Checks the filter's child filters.
     *
     * @param expected a list with the expected children
     * @param cf the filter to check
     */
    protected void checkChildFilters(Collection<EventFilter> expected,
            ChainedEventFilter cf)
    {
        assertEquals("Wrong number of children", expected.size(), cf
                .getFilters().size());
        for (Iterator<EventFilter> it = cf.getFilterIterator(), it2 = expected
                .iterator(); it.hasNext();)
        {
            assertEquals("Wrong child filter", it2.next(), it.next());
        }
    }

    /**
     * Tests a newly created empty chained filter.
     */
    @Test
    public void testInitEmpty()
    {
        ChainedEventFilter filter = createEmptyFilter();
        assertTrue("Filter has child filters", filter.getFilters().isEmpty());
        Iterator<EventFilter> it = filter.getFilterIterator();
        assertFalse("Iteration is not empty", it.hasNext());
    }

    /**
     * Tests creating a chained filter with a list of child filters.
     */
    @Test
    public void testInitFromList()
    {
        Collection<EventFilter> children = createDummyFilterList(4);
        ChainedEventFilter filter = createFilterWithChildren(children);
        checkChildFilters(children, filter);
    }

    /**
     * Tests that a defensive copy is made from the filter list passed to the
     * constructor.
     */
    @Test
    public void testInitFromListModify()
    {
        final int count = 11;
        Collection<EventFilter> children = createDummyFilterList(count);
        ChainedEventFilter filter = createFilterWithChildren(children);
        children.clear();
        assertEquals("Wrong size of children", count, filter.getFilters()
                .size());
    }

    /**
     * Tests creating a chained filter with a null list. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitFilterFromNullList()
    {
        createFilterWithChildren(null);
    }

    /**
     * Tests creating a chained filter from a list that contains null elements.
     * This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitFilterFromListNullChildren()
    {
        Collection<EventFilter> children = createDummyFilterList(5);
        children.add(null);
        createFilterWithChildren(children);
    }

    /**
     * Tests adding a new filter.
     */
    @Test
    public void testAddFilter()
    {
        ChainedEventFilter filter = createEmptyFilter();
        EventFilter f = createDummyFilter();
        filter.addFilter(f);
        assertEquals("Wrong number of children", 1, filter.getFilters().size());
        assertEquals("Wrong child filter", f, filter.getFilterIterator().next());
        f = createDummyFilter();
        filter.addFilter(f);
        assertEquals("Wrong number of children 2", 2, filter.getFilters()
                .size());
        assertEquals("Wrong 2nd child", f, filter.getFilters().get(1));
    }

    /**
     * Tests adding a new null filter. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddFilterNull()
    {
        ChainedEventFilter filter = createEmptyFilter();
        filter.addFilter(null);
    }

    /**
     * Tests adding a list of child filters.
     */
    @Test
    public void testAddFilters()
    {
        ChainedEventFilter filter = createEmptyFilter();
        Collection<EventFilter> children = createDummyFilterList(10);
        filter.addFilters(children);
        checkChildFilters(children, filter);
    }

    /**
     * Tests adding a null list of child filters. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddFiltersNull()
    {
        ChainedEventFilter filter = createEmptyFilter();
        filter.addFilters(null);
    }

    /**
     * Tests the clear method.
     */
    @Test
    public void testClear()
    {
        ChainedEventFilter filter = createEmptyFilter();
        filter.clear();
        assertTrue("Filter is not empty", filter.getFilters().isEmpty());
        filter.addFilters(createDummyFilterList(5));
        assertFalse("Filter is empty", filter.getFilters().isEmpty());
        filter.clear();
        assertTrue("Filter is not empty again", filter.getFilters().isEmpty());
    }

    /**
     * Tests that the collection returned by getFilters() cannot be modified.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetFiltersModify()
    {
        ChainedEventFilter filter = createFilterWithChildren(createDummyFilterList(25));
        filter.getFilters().clear();
    }
}
