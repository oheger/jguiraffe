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
package net.sf.jguiraffe.gui.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.jguiraffe.di.BeanCreationListener;
import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.InvocationHelper;
import net.sf.jguiraffe.gui.builder.BeanBuilderResult;
import net.sf.jguiraffe.gui.builder.Builder;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code ApplicationBuilderData}. Note that some functionality
 * of this class is already tested by the test class for {@code
 * ApplicationContextImpl}.
 *
 * @author Oliver Heger
 * @version $Id: TestApplicationBuilderData.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestApplicationBuilderData
{
    /** Constant for a property key. */
    private static final String PROP = "testProperty";

    /** Constant for a property value. */
    private static final Object VAL = "testPropertyValue";

    /** The object to be tested.*/
    private ApplicationBuilderData data;

    @Before
    public void setUp() throws Exception
    {
        data = new ApplicationBuilderData();
    }

    /**
     * Tests whether the root store from a builder data object can be obtained.
     */
    @Test
    public void testGetRootStore()
    {
        BeanBuilderResult mockResult = EasyMock
                .createMock(BeanBuilderResult.class);
        BeanStore mockStore = EasyMock.createMock(BeanStore.class);
        EasyMock.expect(mockResult.getBeanStore(null)).andReturn(mockStore);
        EasyMock.replay(mockResult, mockStore);
        data.setBeanBuilderResult(mockResult);
        assertEquals("Wrong root store returned", mockStore, data
                .getRootStore());
        EasyMock.verify(mockResult, mockStore);
    }

    /**
     * Tries to obtain the root bean store when there is no result object. This
     * should cause an exception.
     */
    @Test(expected = IllegalStateException.class)
    public void testGetRootStoreUndefined()
    {
        data.getRootStore();
    }

    /**
     * Tests whether the builder on the data object can be set.
     */
    @Test
    public void testSetBuilder()
    {
        Builder builder = EasyMock.createMock(Builder.class);
        EasyMock.replay(builder);
        data.setBuilder(builder);
        assertEquals("Builder not set", builder, data.getBuilder());
        EasyMock.verify(builder);
    }

    /**
     * Tests whether a collection of bean creation listeners can be set.
     */
    @Test
    public void testAddBeanCreationListeners()
    {
        Collection<BeanCreationListener> listeners = new ArrayList<BeanCreationListener>();
        BeanCreationListener l1 = EasyMock
                .createMock(BeanCreationListener.class);
        BeanCreationListener l2 = EasyMock.createMock(BeanCreationListener.class);
        EasyMock.replay(l1, l2);
        listeners.add(l1);
        listeners.add(l2);
        data.addBeanCreationListeners(listeners);
        Collection<BeanCreationListener> col = data.getBeanCreationListeners();
        assertEquals("Wrong number of listeners", 2, col.size());
        Iterator<BeanCreationListener> it = col.iterator();
        assertEquals("Wrong listener 1", l1, it.next());
        assertEquals("Wrong listener 2", l2, it.next());
        EasyMock.verify(l1, l2);
    }

    /**
     * Tries to pass a null collection of bean creation listeners. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddBeanCreationListenersNull()
    {
        data.addBeanCreationListeners(null);
    }

    /**
     * Tries to pass a collection of bean creation listeners that contain a null
     * element. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddBeanCreationListenersNullElement()
    {
        Collection<BeanCreationListener> listeners = new ArrayList<BeanCreationListener>();
        listeners.add(EasyMock.createNiceMock(BeanCreationListener.class));
        listeners.add(null);
        data.addBeanCreationListeners(listeners);
    }

    /**
     * Tests that the collection of bean creation listeners cannot be modified.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetBeanCreationListenersModify()
    {
        data.addBeanCreationListener(EasyMock
                .createNiceMock(BeanCreationListener.class));
        Collection<BeanCreationListener> listeners = data
                .getBeanCreationListeners();
        assertEquals("Wrong number of listeners", 1, listeners.size());
        listeners.clear();
    }

    /**
     * Tests getProperties() if none have been set.
     */
    @Test
    public void testGetPropertiesUndefined()
    {
        assertNull("Got properties", data.getProperties());
    }

    /**
     * Tests whether a map with properties can be set.
     */
    @Test
    public void testSetProperties()
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(PROP, VAL);
        data.setProperties(map);
        assertSame("Wrong properties", map, data.getProperties());
    }

    /**
     * Tests whether a property can be added.
     */
    @Test
    public void testAddProperty()
    {
        data.addProperty(PROP, VAL);
        Map<String, Object> props = data.getProperties();
        assertEquals("Wrong size", 1, props.size());
        assertEquals("Wrong property value", VAL, props.get(PROP));
    }

    /**
     * Tests whether a property can be added to an already existing map.
     */
    @Test
    public void testAddPropertyExistingMap()
    {
        Map<String, Object> map = new HashMap<String, Object>();
        data.setProperties(map);
        data.addProperty(PROP, VAL);
        assertSame("Wrong properties", map, data.getProperties());
        assertEquals("Wrong property value", VAL, map.get(PROP));
    }

    /**
     * Tests whether the invocation helper can be set.
     */
    @Test
    public void testSetInvocationHelper()
    {
        InvocationHelper invHlp = new InvocationHelper();
        data.setInvocationHelper(invHlp);
        assertSame("Invocation helper not set", invHlp,
                data.getInvocationHelper());
    }
}
