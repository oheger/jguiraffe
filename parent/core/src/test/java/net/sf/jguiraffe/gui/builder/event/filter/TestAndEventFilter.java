/*
 * Copyright 2006-2018 The JGUIraffe Team.
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
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for AndEventFilter.
 *
 * @author Oliver Heger
 * @version $Id: TestAndEventFilter.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestAndEventFilter extends AbstractChainedEventFilterTest
{
    /**
     * Creates an empty AndEventFilter.
     *
     * @return the new filter
     */
    @Override
    protected ChainedEventFilter createEmptyFilter()
    {
        return new AndEventFilter();
    }

    /**
     * Creates a new AndEventFilter and initializes it with the given collection
     * of child filters.
     *
     * @param children the child filters
     * @return the filter to test
     */
    @Override
    protected ChainedEventFilter createFilterWithChildren(
            Collection<EventFilter> children)
    {
        return new AndEventFilter(children);
    }

    /**
     * Tests the accept() method when the filter is empty.
     */
    @Test
    public void testAcceptEmpty()
    {
        AndEventFilter filter = new AndEventFilter();
        assertTrue("Wrong accept() result for empty filter", filter
                .accept(TEST_OBJ));
    }

    /**
     * Tests the accept() method when at least one of the child filters returns
     * false. The remaining filters should not be invoked after that.
     */
    @Test
    public void testAcceptFalse()
    {
        AndEventFilter filter = new AndEventFilter();
        EventFilter mock1 = addMockFilter(filter, true, true);
        EventFilter mock2 = addMockFilter(filter, false, true);
        EventFilter mock3 = addMockFilter(filter, true, false);
        EasyMock.replay(mock3);
        assertFalse("Wrong accept() result", filter.accept(TEST_OBJ));
        EasyMock.verify(mock1, mock2, mock3);
    }

    /**
     * Tests the accept() method when all of the child filters return true.
     */
    @Test
    public void testAcceptTrue()
    {
        AndEventFilter filter = new AndEventFilter();
        EventFilter mock1 = addMockFilter(filter, true, true);
        EventFilter mock2 = addMockFilter(filter, true, true);
        EventFilter mock3 = addMockFilter(filter, true, true);
        assertTrue("Wrong accept() result", filter.accept(TEST_OBJ));
        EasyMock.verify(mock1, mock2, mock3);
    }
}
