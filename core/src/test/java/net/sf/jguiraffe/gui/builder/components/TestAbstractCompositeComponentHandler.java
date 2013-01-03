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
package net.sf.jguiraffe.gui.builder.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sf.jguiraffe.JGuiraffeTestHelper;
import net.sf.jguiraffe.gui.forms.ComponentHandler;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code AbstractCompositeComponentHandler}.
 *
 * @author Oliver Heger
 * @version $Id: TestAbstractCompositeComponentHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestAbstractCompositeComponentHandler
{
    /** Constant for the name prefix for child handlers. */
    private static final String CHILD_PREFIX = "childHandler";

    /** Constant for the number of child handlers. */
    private static final int CHILD_COUNT = 8;

    /** The handler to be tested. */
    private AbstractCompositeComponentHandler<Object, Integer> handler;

    @Before
    public void setUp() throws Exception
    {
        handler = new AbstractCompositeComponentHandlerTestImpl();
    }

    /**
     * Helper method for creating a child handler.
     *
     * @return the child handler
     */
    private static ComponentHandler<Integer> createChildHandler()
    {
        @SuppressWarnings("unchecked")
        ComponentHandler<Integer> child = EasyMock
                .createMock(ComponentHandler.class);
        return child;
    }

    /**
     * Tests the getComponent() implementation.
     */
    @Test
    public void testGetComponent()
    {
        assertNull("Got a component", handler.getComponent());
    }

    /**
     * Tests the getOuterComponent() implementation.
     */
    @Test
    public void testGetOuterComponent()
    {
        assertNull("Got an outer component", handler.getOuterComponent());
    }

    /**
     * Tests whether the correct type is returned.
     */
    @Test
    public void testGetType()
    {
        assertEquals("Wrong type", Object.class, handler.getType());
    }

    /**
     * Tests getChildHandlers() for a newly created instance.
     */
    @Test
    public void testGetChildHandlersInit()
    {
        assertTrue("Got child handlers", handler.getChildHandlers().isEmpty());
    }

    /**
     * Tests getChildHandlerNames() for a newly created instance.
     */
    @Test
    public void testGetChildHandlerNamesInit()
    {
        assertTrue("Got child handler names", handler.getChildHandlerNames()
                .isEmpty());
    }

    /**
     * Tests getChildHandlerCount() for a newly created instance.
     */
    @Test
    public void testGetChildHandlerCountInit()
    {
        assertEquals("Wrong number of child handlers", 0, handler
                .getChildHandlerCount());
    }

    /**
     * Helper method for adding test handlers to the composite handler.
     *
     * @param replay a flag whether the mock handlers should be replayed
     * @return the list with the added test handlers
     */
    private List<ComponentHandler<Integer>> addChildHandlers(boolean replay)
    {
        List<ComponentHandler<Integer>> handlers = new ArrayList<ComponentHandler<Integer>>(
                CHILD_COUNT);
        for (int i = 0; i < CHILD_COUNT; i++)
        {
            handlers.add(createChildHandler());
        }
        if (replay)
        {
            EasyMock.replay(handlers.toArray());
        }
        int idx = 1;
        for (ComponentHandler<Integer> ch : handlers)
        {
            handler.addHandler(CHILD_PREFIX + idx, ch);
            idx++;
        }
        return handlers;
    }

    /**
     * Tests whether the child handlers that have been added can be queried.
     */
    @Test
    public void testGetChildHandlersAfterAddHandler()
    {
        List<ComponentHandler<Integer>> children = addChildHandlers(true);
        List<ComponentHandler<Integer>> handlerChildren = handler
                .getChildHandlers();
        assertTrue("Wrong child handlers", JGuiraffeTestHelper
                .collectionEquals(children, handlerChildren));
    }

    /**
     * Tests whether the names of the added child handlers can be queried.
     */
    @Test
    public void testGetChildHandlerNamesAfterAddHandler()
    {
        List<ComponentHandler<Integer>> children = addChildHandlers(true);
        int idx = 1;
        Set<String> names = handler.getChildHandlerNames();
        assertEquals("Wrong number of handler names", children.size(), names
                .size());
        for (String name : names)
        {
            assertEquals("Wrong name at " + idx, CHILD_PREFIX + idx, name);
            idx++;
        }
    }

    /**
     * Tests whether the correct number of child handlers can be retrieved.
     */
    @Test
    public void testGetChildHandlerCounterAfterAddHandler()
    {
        addChildHandlers(true);
        assertEquals("Wrong number of child handlers", CHILD_COUNT, handler
                .getChildHandlerCount());
    }

    /**
     * Tests whether child handlers can be accessed by name.
     */
    @Test
    public void testGetChildHandler()
    {
        List<ComponentHandler<Integer>> children = addChildHandlers(true);
        for (int i = 1; i <= CHILD_COUNT; i++)
        {
            String key = CHILD_PREFIX + i;
            assertEquals("Wrong handler for key " + key, children.get(i - 1),
                    handler.getChildHandler(key));
        }
    }

    /**
     * Tests getChildHandler() if the name cannot be resolved.
     */
    @Test
    public void testGetChildHandlerUnknown()
    {
        addChildHandlers(true);
        assertNull("Got unknown child handler", handler
                .getChildHandler("unknown name"));
    }

    /**
     * Tries to add a null handler. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddHandlerNull()
    {
        handler.addHandler(CHILD_PREFIX, null);
    }

    /**
     * Tries to add a handler with a name that already exists. This should cause
     * an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddHandlerDuplicate()
    {
        addChildHandlers(true);
        handler.addHandler(CHILD_PREFIX + "1", createChildHandler());
    }

    /**
     * Tests the isEnabled() implementation if some of the child handlers are
     * disabled.
     */
    @Test
    public void testIsEnabledSomeDisabled()
    {
        List<ComponentHandler<Integer>> children = addChildHandlers(false);
        int idx = CHILD_COUNT / 2;
        for (int i = 0; i < idx; i++)
        {
            EasyMock.expect(children.get(i).isEnabled())
                    .andReturn(Boolean.TRUE);
        }
        EasyMock.expect(children.get(idx).isEnabled()).andReturn(Boolean.FALSE);
        EasyMock.replay(children.toArray());
        assertFalse("Handler is enabled", handler.isEnabled());
        EasyMock.verify(children.toArray());
    }

    /**
     * Tests isEnabled() if all child handlers are enabled.
     */
    @Test
    public void testIsEnabledTrue()
    {
        List<ComponentHandler<Integer>> children = addChildHandlers(false);
        for (ComponentHandler<Integer> ch : children)
        {
            EasyMock.expect(ch.isEnabled()).andReturn(Boolean.TRUE);
            EasyMock.replay(ch);
        }
        assertTrue("Not enabled", handler.isEnabled());
        EasyMock.verify(children.toArray());
    }

    /**
     * Tests whether the enabled state can be changed.
     */
    @Test
    public void testSetEnabled()
    {
        List<ComponentHandler<Integer>> children = addChildHandlers(false);
        for (ComponentHandler<Integer> ch : children)
        {
            ch.setEnabled(false);
            ch.setEnabled(true);
            EasyMock.replay(ch);
        }
        handler.setEnabled(false);
        handler.setEnabled(true);
        EasyMock.verify(children.toArray());
    }

    /**
     * Tests whether correct indices for child handlers can be queried.
     */
    @Test
    public void testGetChildHandlerIndex()
    {
        addChildHandlers(true);
        for (int i = 1; i <= CHILD_COUNT; i++)
        {
            assertEquals("Wrong index", i - 1, handler
                    .getChildHandlerIndex(CHILD_PREFIX + i));
        }
    }

    /**
     * Tests getChildHandlerIndex() if an unknown handler name is passed in.
     */
    @Test
    public void testGetChildHandlerIndexUnknown()
    {
        addChildHandlers(true);
        assertEquals("Wrong index for unknown handler", -1, handler
                .getChildHandlerIndex("unknown child handler"));
    }

    /**
     * Tests whether the names for child handlers at given indices can be
     * queried.
     */
    @Test
    public void testGetChildHandlerNameAt()
    {
        addChildHandlers(true);
        for (int i = 1; i <= CHILD_COUNT; i++)
        {
            assertEquals("Wrong name", CHILD_PREFIX + i, handler
                    .getChildHandlerNameAt(i - 1));
        }
    }

    /**
     * Tests getChildHandlerNameAt() for a negative index.
     */
    @Test
    public void testGetChildHandlerNameAtNegativeIndex()
    {
        addChildHandlers(true);
        assertNull("Got a name", handler.getChildHandlerNameAt(-1));
    }

    /**
     * Tests getChildHandlerNameAt() if the index is greater than the number of
     * child handlers.
     */
    @Test
    public void testGetChildHandlerNameAtIndexTooLarge()
    {
        addChildHandlers(true);
        assertNull("Got a name", handler.getChildHandlerNameAt(handler
                .getChildHandlerCount()));
    }

    /**
     * A concrete test implementation of {@code
     * AbstractCompositeComponentHandler}.
     */
    private static class AbstractCompositeComponentHandlerTestImpl extends
            AbstractCompositeComponentHandler<Object, Integer>
    {
        public AbstractCompositeComponentHandlerTestImpl()
        {
            super(Object.class);
        }

        public Object getData()
        {
            return null;
        }

        public void setData(Object data)
        {
        }
    }
}
