/*
 * Copyright 2006-2015 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.action.tags;

import net.sf.jguiraffe.gui.builder.event.filter.AndEventFilter;
import net.sf.jguiraffe.gui.builder.event.filter.ClassEventFilter;
import net.sf.jguiraffe.gui.builder.event.filter.EventFilter;
import net.sf.jguiraffe.gui.builder.event.filter.OrEventFilter;
import net.sf.jguiraffe.gui.builder.event.filter.TypeEventFilter;
import net.sf.jguiraffe.gui.builder.window.WindowEvent;

import org.apache.commons.jelly.JellyTagException;

/**
 * Test class for the event filter tags.
 *
 * @author Oliver Heger
 * @version $Id: TestEventFilterTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestEventFilterTag extends AbstractActionTagTest
{
    /** Constant for the test script. */
    private static final String SCRIPT = "eventfilter";

    /** Constant for the simple filter builder. */
    private static final String BUILDER_SIMPLE = "BUILDER_SIMPLE";

    /** Constant for the or filter builder. */
    private static final String BUILDER_OR = "BUILDER_OR";

    /** Constant for the and filter builder. */
    private static final String BUILDER_AND = "BUILDER_AND";

    /** Constant for the nested builder. */
    private static final String BUILDER_NESTED = "BUILDER_NESTED";

    /** Constant for the wrong class builder. */
    private static final String BUILDER_ERR_CLASS = "BUILDER_ERR_CLASS";

    /**
     * Executes the test script with the specified builder and returns the
     * current filter.
     *
     * @param builderName the builder name
     * @return the current filter
     * @throws Exception if an error occurs
     */
    private EventFilter createFilter(String builderName) throws Exception
    {
        builderData.setBuilderName(builderName);
        executeScript(SCRIPT);
        EventFilter filter = (EventFilter) context
                .getVariable(EventFilterTag.CURRENT_FILTER);
        assertNotNull("Current filter was not set", filter);
        return filter;
    }

    /**
     * Tests creating a simple event filter.
     */
    public void testCreateSimpleFilter() throws Exception
    {
        TypeEventFilter filter = (TypeEventFilter) createFilter(BUILDER_SIMPLE);
        assertEquals("Incorrect event type", "WINDOW_OPENED",
                filter.getEventType());
    }

    /**
     * Tests creating a chained or filter.
     */
    public void testCreateOrFilter() throws Exception
    {
        builderData.setBuilderName(BUILDER_OR);
        executeScript(SCRIPT);
        OrEventFilter filter = (OrEventFilter) context
                .getVariable("myOrFilter");
        checkOrFilter(filter);
    }

    /**
     * Helper method for checking an or filter and its children.
     *
     * @param filter the filter to check
     */
    private void checkOrFilter(OrEventFilter filter)
    {
        assertNotNull("Undefined filter", filter);
        assertEquals("Wrong number of child filters", 2, filter.getFilters()
                .size());
        TypeEventFilter f = (TypeEventFilter) filter.getFilters().get(0);
        assertEquals("Wrong first event type", "WINDOW_OPENED",
                f.getEventType());
        f = (TypeEventFilter) filter.getFilters().get(1);
        assertEquals("Wrong second event type", "WINDOW_CLOSED",
                f.getEventType());
    }

    /**
     * Tests creating a more complex and filter.
     */
    public void testCreateAndFilter() throws Exception
    {
        AndEventFilter filter = (AndEventFilter) createFilter(BUILDER_AND);
        assertEquals("Wrong number of child filters", 2, filter.getFilters()
                .size());
        ClassEventFilter f = (ClassEventFilter) filter.getFilters().get(0);
        assertEquals("Wrong base class", WindowEvent.class, f.getBaseClass());
        checkOrFilter((OrEventFilter) filter.getFilters().get(1));
    }

    /**
     * Tests creating of nested chained filters.
     */
    public void testCreateNestedFilters() throws Exception
    {
        OrEventFilter filter = (OrEventFilter) createFilter(BUILDER_NESTED);
        assertEquals("Wrong number of children", 1, filter.getFilters().size());
        checkOrFilter((OrEventFilter) filter.getFilters().get(0));
    }

    /**
     * Tests creating a chained filter when a wrong class is specified.
     */
    public void testCreateChainedFilterWithWrongClass() throws Exception
    {
        builderData.setBuilderName(BUILDER_ERR_CLASS);
        try
        {
            executeScript(SCRIPT);
            fail("Could create chained filter with wrong class!");
        }
        catch (JellyTagException jtex)
        {
            // ok
        }
    }
}
