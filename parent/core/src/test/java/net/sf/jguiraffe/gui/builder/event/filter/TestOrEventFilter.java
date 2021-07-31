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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for OrEventFilter.
 *
 * @author Oliver Heger
 * @version $Id: TestOrEventFilter.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestOrEventFilter extends AbstractChainedEventFilterTest
{
    /**
     * Returns an empty OrEventFilter.
     *
     * @return an empty filter
     */
    @Override
    protected ChainedEventFilter createEmptyFilter()
    {
        return new OrEventFilter();
    }

    /**
     * Returns an OrEventFilter that is initialized with the given collection of
     * child filters.
     *
     * @param children the child filters
     * @return the new filter
     */
    @Override
    protected ChainedEventFilter createFilterWithChildren(
            Collection<EventFilter> children)
    {
        return new OrEventFilter(children);
    }

    /**
     * Tests the behavior of an empty filter.
     */
    @Test
    public void testAcceptEmpty()
    {
        OrEventFilter filter = new OrEventFilter();
        assertFalse("Wrong accept() result of empty filter", filter
                .accept(TEST_OBJ));
    }

    /**
     * Tests the accept() method if all child filters return false.
     */
    @Test
    public void testAcceptFalse()
    {
        OrEventFilter filter = new OrEventFilter();
        EventFilter mock1 = addMockFilter(filter, false, true);
        EventFilter mock2 = addMockFilter(filter, false, true);
        EventFilter mock3 = addMockFilter(filter, false, true);
        assertFalse("Wrong accept() result", filter.accept(TEST_OBJ));
        EasyMock.verify(mock1, mock2, mock3);
    }

    /**
     * Tests the accept() method if one child returns true. The remaining
     * children should not be called.
     */
    @Test
    public void testAcceptTrue()
    {
        OrEventFilter filter = new OrEventFilter();
        EventFilter mock1 = addMockFilter(filter, false, true);
        EventFilter mock2 = addMockFilter(filter, true, true);
        EventFilter mock3 = addMockFilter(filter, false, false);
        EasyMock.replay(mock3);
        assertTrue("Wrong accept() result", filter.accept(TEST_OBJ));
        EasyMock.verify(mock1, mock2, mock3);
    }
}
