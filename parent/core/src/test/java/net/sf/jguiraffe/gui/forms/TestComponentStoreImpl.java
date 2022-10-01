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
package net.sf.jguiraffe.gui.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.easymock.EasyMock;

/**
 * Test class for ComponentStoreImpl.
 *
 * @author Oliver Heger
 * @version $Id: TestComponentStoreImpl.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestComponentStoreImpl extends TestCase
{
    /** Constant for the prefix of test component handler. */
    private static final String PRE_COMPHANDLERS = "compHandler";

    /** Constant for the prefix of test field handlers. */
    private static final String PRE_FIELDHANDLERS = "fieldHandler";

    /** Constant for the prefix of test components. */
    private static final String PRE_COMPONENTS = "component";

    /** Constant for the number of test elements. */
    private static final int COUNT = 5;

    /** Stores the object to be tested. */
    private ComponentStoreImpl store;

    /** A list with the test component handlers. */
    private List<ComponentHandler<?>> testCompHandlers;

    /** A list with the test field handlers. */
    private List<FieldHandler> testFieldHandlers;

    /**
     * Sets up the test environment. Creates some test objects and adds them to
     * the store.
     */
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        store = new ComponentStoreImpl();
        testCompHandlers = new ArrayList<ComponentHandler<?>>(COUNT);
        testFieldHandlers = new ArrayList<FieldHandler>(COUNT);
        for (int i = 0; i < COUNT; i++)
        {
            store.add(PRE_COMPONENTS + i, PRE_COMPONENTS + i);
            ComponentHandler<?> ch = createMockComponentHandler(i);
            testCompHandlers.add(ch);
            store.addComponentHandler(PRE_COMPHANDLERS + i, ch);
            FieldHandler fh = createMockFieldHandler(i);
            testFieldHandlers.add(fh);
            store.addFieldHandler(PRE_FIELDHANDLERS + i, fh);
        }
    }

    /**
     * Creates a component handler mock. An associated component is also added
     * to this handler whose name is determined by the given prefix.
     *
     * @param prefix the name prefix for the dummy component to be created
     * @return the mock for the component handler
     */
    private ComponentHandler<?> createMockComponentHandler(String prefix)
    {
        ComponentHandler<?> mock = EasyMock.createMock(ComponentHandler.class);
        EasyMock.expect(mock.getComponent()).andStubReturn(
                prefix + PRE_COMPONENTS);
        EasyMock.replay(mock);
        return mock;
    }

    /**
     * Creates a component handler mock whose name is determined by the given
     * index.
     *
     * @param idx the index
     * @return the mock component handler
     */
    private ComponentHandler<?> createMockComponentHandler(int idx)
    {
        return createMockComponentHandler(PRE_COMPHANDLERS + idx);
    }

    /**
     * Creates a field handler mock. The name of the associated component
     * handler's component is determined by the given index.
     *
     * @param idx the index
     * @return the mock for the field handler
     */
    private FieldHandler createMockFieldHandler(int idx)
    {
        FieldHandler mock = EasyMock.createMock(FieldHandler.class);
        mock.getComponentHandler();
        EasyMock.expectLastCall().andStubReturn(
                createMockComponentHandler(PRE_FIELDHANDLERS + idx));
        EasyMock.replay(mock);
        return mock;
    }

    /**
     * Tests a newly created or cleared store. It should be empty.
     */
    private void checkNewStore()
    {
        assertTrue("Component names not empty", store.getComponentNames()
                .isEmpty());
        assertTrue("Component handler names not empty", store
                .getComponentHandlerNames().isEmpty());
        assertTrue("Field handler names not empty", store
                .getFieldHandlerNames().isEmpty());
        for (int i = 0; i < COUNT; i++)
        {
            assertNull("Could find component " + i, store
                    .findComponent(PRE_COMPONENTS + i));
            assertNull("Could find component handler " + i, store
                    .findComponentHandler(PRE_COMPHANDLERS + i));
            assertNull("Could find field handler " + i, store
                    .findFieldHandler(PRE_FIELDHANDLERS + i));
        }
    }

    /**
     * Tests a newly created store.
     */
    public void testInit()
    {
        store = new ComponentStoreImpl();
        checkNewStore();
    }

    /**
     * Tests the names of the field handlers.
     */
    public void testGetFieldHandlerNames()
    {
        Set<String> names = store.getFieldHandlerNames();
        assertEquals("Wrong number of field handlers", COUNT, names.size());
        for (int i = 0; i < COUNT; i++)
        {
            assertTrue("Cannot find field handler name " + i, names
                    .contains(PRE_FIELDHANDLERS + i));
        }
    }

    /**
     * Tests accessing the names of the component handlers.
     */
    public void testGetComponentHandlerNames()
    {
        Set<String> names = store.getComponentHandlerNames();
        assertEquals("Wrong number of component handlers", 2 * COUNT, names
                .size());
        for (int i = 0; i < COUNT; i++)
        {
            assertTrue("Cannot find field handler component name " + i, names
                    .contains(PRE_FIELDHANDLERS + i));
            assertTrue("Cannot find component handler name " + i, names
                    .contains(PRE_COMPHANDLERS + i));
        }
    }

    /**
     * Tests accessing the names of the components.
     */
    public void testGetComponentNames()
    {
        Set<String> names = store.getComponentNames();
        assertEquals("Wrong number of components", 3 * COUNT, names.size());
        for (int i = 0; i < COUNT; i++)
        {
            assertTrue("Cannot find field handler component name " + i, names
                    .contains(PRE_FIELDHANDLERS + i));
            assertTrue("Cannot find component handler name " + i, names
                    .contains(PRE_COMPHANDLERS + i));
            assertTrue("Cannot find component name " + i, names
                    .contains(PRE_COMPONENTS + i));
        }
    }

    /**
     * Tests accessing the field handlers from the store.
     */
    public void testFindFieldHandler()
    {
        for (int i = 0; i < COUNT; i++)
        {
            assertEquals("Could not find field handler " + i, testFieldHandlers
                    .get(i), store.findFieldHandler(PRE_FIELDHANDLERS + i));
        }
    }

    /**
     * Tests accessing the component handlers from the store.
     */
    public void testFindComponentHandler()
    {
        for (int i = 0; i < COUNT; i++)
        {
            assertEquals("Could not find component handler " + i,
                    testCompHandlers.get(i), store
                            .findComponentHandler(PRE_COMPHANDLERS + i));
            assertEquals("Could not find comp handler for field handler " + i,
                    testFieldHandlers.get(i).getComponentHandler(), store
                            .findComponentHandler(PRE_FIELDHANDLERS + i));
        }
    }

    /**
     * Tests accessing components from the store.
     */
    public void testFindComponent()
    {
        for (int i = 0; i < COUNT; i++)
        {
            assertEquals("Could not find component " + i, PRE_COMPONENTS + i,
                    store.findComponent(PRE_COMPONENTS + i));
            assertEquals("Could not find component for comp handler " + i,
                    PRE_COMPHANDLERS + i + PRE_COMPONENTS, store
                            .findComponent(PRE_COMPHANDLERS + i));
            assertEquals("Could not find component for field handler " + i,
                    PRE_FIELDHANDLERS + i + PRE_COMPONENTS, store
                            .findComponent(PRE_FIELDHANDLERS + i));
        }
    }

    /**
     * Tries to access an unknown field handler.
     */
    public void testFindFieldHandlerUnknown()
    {
        assertNull("Unknown field handler found", store
                .findFieldHandler("unknown"));
    }

    /**
     * Tries to access an unknown component handler.
     */
    public void testFindComponentHandlerUnknown()
    {
        assertNull("Unknown component handler found", store
                .findComponentHandler("unknown"));
    }

    /**
     * Tries to access an unknown component.
     */
    public void testFindComponentUnknown()
    {
        assertNull("Unknown component found", store.findComponent("unknown"));
    }

    /**
     * Tests the clear method. The store should be empty afterwards.
     */
    public void testClear()
    {
        store.clear();
        checkNewStore();
    }

    /**
     * Tests adding a component handler under a name, for which a component is
     * already stored. The handler's component should override the existing
     * component.
     */
    public void testAddComponentHandlerOverride()
    {
        final String prefix = "test";
        final String name = PRE_COMPONENTS + 0;
        ComponentHandler<?> mockHandler = createMockComponentHandler(prefix);
        store.addComponentHandler(name, mockHandler);
        assertEquals("Component was not overriden", prefix + PRE_COMPONENTS,
                store.findComponent(name));
    }

    /**
     * Tests adding a field handler under a name, for which a component handler
     * is already stored. The field handler's component handler should override
     * the existing handler.
     */
    public void testAddFieldHandlerOverride()
    {
        final String name = PRE_COMPHANDLERS + 0;
        FieldHandler mockHandler = createMockFieldHandler(0);
        store.addFieldHandler(name, mockHandler);
        assertEquals("Component handler was not overriden", mockHandler
                .getComponentHandler(), store.findComponentHandler(name));
    }

    /**
     * Tests adding a null component. This should cause an exception.
     */
    public void testAddComponentNull()
    {
        try
        {
            store.add(PRE_COMPONENTS, null);
            fail("Could add null component!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tests adding a component with a null name. This should cause an
     * exception.
     */
    public void testAddComponentNullName()
    {
        try
        {
            store.add(null, PRE_COMPONENTS);
            fail("Could add component with null name!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tests adding a null component handler. This should cause an exception.
     */
    public void testAddComponentHandlerNull()
    {
        try
        {
            store.addComponentHandler(PRE_COMPHANDLERS, null);
            fail("Could add null component handler!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tests adding a component handler with a null name. This should cause an
     * exception.
     */
    public void testAddComponentHandlerNullName()
    {
        try
        {
            store.addComponentHandler(null, createMockComponentHandler(COUNT));
            fail("Could add component handler with null name!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tests adding a null field handler. This should cause an exception.
     */
    public void testAddFieldHandlerNull()
    {
        try
        {
            store.addFieldHandler(PRE_FIELDHANDLERS, null);
            fail("Could add null field handler!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tests adding a field handler with a null name. This should cause an
     * exception.
     */
    public void testAddFieldHandlerNullName()
    {
        try
        {
            store.addFieldHandler(null, createMockFieldHandler(COUNT));
            fail("Could add field handler with null name!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tests adding a component handler that does not have a component. This
     * should be allowed.
     */
    public void testAddComponentHandlerNoComponent()
    {
        ComponentHandler<?> mockHandler = EasyMock
                .createMock(ComponentHandler.class);
        EasyMock.expect(mockHandler.getComponent()).andReturn(null);
        EasyMock.replay(mockHandler);
        store.addComponentHandler(PRE_COMPHANDLERS, mockHandler);
        assertEquals("Handler was not added", mockHandler, store
                .findComponentHandler(PRE_COMPHANDLERS));
        assertNull("A component was added", store
                .findComponent(PRE_COMPHANDLERS));
        EasyMock.verify(mockHandler);
    }

    /**
     * Tests adding a field handler that does not have a component handler.
     */
    public void testAddFieldHandlerNoComponentHandler()
    {
        FieldHandler mockHandler = EasyMock.createMock(FieldHandler.class);
        EasyMock.expect(mockHandler.getComponentHandler()).andReturn(null);
        EasyMock.replay(mockHandler);
        store.addFieldHandler(PRE_FIELDHANDLERS, mockHandler);
        assertEquals("Handler was not added", mockHandler, store
                .findFieldHandler(PRE_FIELDHANDLERS));
        assertNull("A component handler was added", store
                .findComponentHandler(PRE_FIELDHANDLERS));
    }
}
