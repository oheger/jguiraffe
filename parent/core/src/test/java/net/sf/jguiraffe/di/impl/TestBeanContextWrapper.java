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
package net.sf.jguiraffe.di.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.di.BeanContextClient;
import net.sf.jguiraffe.di.BeanCreationListener;
import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.impl.providers.ConstantBeanProvider;
import net.sf.jguiraffe.di.impl.providers.SingletonBeanProvider;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for BeanContextWrapper.
 *
 * @author Oliver Heger
 * @version $Id: TestBeanContextWrapper.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestBeanContextWrapper
{
    /** Constant for a test bean name. */
    private static final String BEAN_NAME = "MyBean";

    /** Constant for the test bean itself. */
    private static final Object TEST_BEAN = new Object();

    /** Stores the wrapped bean context. */
    private BeanContext wrappedContext;

    /** Stores the default bean store. */
    private BeanStore store;

    /** Stores another store. */
    private BeanStore otherStore;

    /** Stores the wrapped context to be tested. */
    private BeanContextWrapperTestImpl wrapper;

    /**
     * Sets up the test environment. Creates mock objects for the wrapped
     * context and the bean stores and creates the fixture.
     */
    @Before
    public void setUp() throws Exception
    {
        wrappedContext = EasyMock.createMock(BeanContext.class);
        store = EasyMock.createMock(BeanStore.class);
        otherStore = EasyMock.createMock(BeanStore.class);
        EasyMock.replay(store, otherStore);
        wrapper = new BeanContextWrapperTestImpl(wrappedContext, store);
    }

    /**
     * Cleans the test environment. This implementation also verifies the used
     * mock objects.
     */
    @After
    public void tearDown() throws Exception
    {
        EasyMock.verify(store, otherStore, wrappedContext);
    }

    /**
     * Replays the wrapped context mock.
     */
    private void replay()
    {
        EasyMock.replay(wrappedContext);
    }

    /**
     * Tests whether the correct underlying context is returned.
     */
    @Test
    public void testGetWrappedContext()
    {
        replay();
        assertSame("Wrong wrapped context", wrappedContext, wrapper
                .getWrappedContext());
    }

    /**
     * Tests whether the correct default store is returned.
     */
    @Test
    public void testGetDefaultStore()
    {
        replay();
        assertSame("Wrong default store", store, wrapper.getDefaultBeanStore());
    }

    /**
     * Tests whether the default store can be changed.
     */
    @Test
    public void testSetDefaultStore()
    {
        replay();
        wrapper.setDefaultBeanStore(otherStore);
        assertSame("Bean store not changed", otherStore, wrapper
                .getDefaultBeanStore());
    }

    /**
     * Tests the constructor that does not take a bean store.
     */
    @Test
    public void testInitNoStore()
    {
        replay();
        wrapper = new BeanContextWrapperTestImpl(wrappedContext);
        assertNull("Already a default store set", wrapper.getDefaultBeanStore());
    }

    /**
     * Tries creating a wrapper with a null context. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNullContext()
    {
        replay();
        new BeanContextWrapper(null, store);
    }

    /**
     * Tests whether the wrapped context is correctly initialized.
     */
    @Test
    public void testInitWrappedContext()
    {
        wrapper.invokeInitWrappedContext = true;
        assertNotNull("No creation listeners object", wrapper
                .getBeanCreationListeners());
        wrappedContext.addBeanCreationListener(wrapper
                .getBeanCreationListeners());
        replay();
        wrapper.initWrappedContext(wrappedContext, wrapper
                .getBeanCreationListeners());
    }

    /**
     * Tests whether initWrappedContext() is called by the constructor.
     */
    @Test
    public void testInitWrappedContextCalled()
    {
        replay();
        assertEquals("Wrong number of initWrappedContext() invocations", 1,
                wrapper.initWrappedContextCount);
    }

    /**
     * Tests querying the bean classes from the default store.
     */
    @Test
    public void testBeanClassesDefStore()
    {
        Set<Class<?>> res = new HashSet<Class<?>>();
        EasyMock.expect(wrappedContext.beanClasses(store)).andReturn(res);
        replay();
        assertSame("Wrong set of classes returned", res, wrapper.beanClasses());
    }

    /**
     * Tests querying the bean classes from a given store.
     */
    @Test
    public void testBeanClasses()
    {
        Set<Class<?>> res = new HashSet<Class<?>>();
        EasyMock.expect(wrappedContext.beanClasses(otherStore)).andReturn(res);
        replay();
        assertSame("Wrong set of classes returned", res, wrapper
                .beanClasses(otherStore));
    }

    /**
     * Tests querying the bean names from the default bean store.
     */
    @Test
    public void testBeanNamesDefStore()
    {
        Set<String> res = new HashSet<String>();
        EasyMock.expect(wrappedContext.beanNames(store)).andReturn(res);
        replay();
        assertSame("Wrong set of names returned", res, wrapper.beanNames());
    }

    /**
     * Tests querying the bean names from a given bean store.
     */
    @Test
    public void testBeanNames()
    {
        Set<String> res = new HashSet<String>();
        EasyMock.expect(wrappedContext.beanNames(otherStore)).andReturn(res);
        replay();
        assertSame("Wrong set of names returned", res, wrapper
                .beanNames(otherStore));
    }

    /**
     * Tests the contains() method for a bean name from the default store.
     */
    @Test
    public void testContainsNameDefStore()
    {
        EasyMock.expect(wrappedContext.containsBean(BEAN_NAME, store))
                .andReturn(true);
        replay();
        assertTrue("Bean not contained", wrapper.containsBean(BEAN_NAME));
    }

    /**
     * Tests the contains() method for a bean name from a given store.
     */
    @Test
    public void testContainsName()
    {
        EasyMock.expect(wrappedContext.containsBean(BEAN_NAME, otherStore))
                .andReturn(true);
        replay();
        assertTrue("Bean not contained", wrapper.containsBean(BEAN_NAME,
                otherStore));
    }

    /**
     * Tests the contains() method for a bean class from the default store.
     */
    @Test
    public void testContainsClassDefStore()
    {
        EasyMock.expect(wrappedContext.containsBean(getClass(), store))
                .andReturn(true);
        replay();
        assertTrue("Bean not contained", wrapper.containsBean(getClass()));
    }

    /**
     * Tests the contains() method for a bean class from a given store.
     */
    @Test
    public void testContainsClass()
    {
        EasyMock.expect(wrappedContext.containsBean(getClass(), otherStore))
                .andReturn(true);
        replay();
        assertTrue("Bean not contained", wrapper.containsBean(getClass(),
                otherStore));
    }

    /**
     * Tests obtaining a bean by name from the default bean store.
     */
    @Test
    public void testGetBeanNameDefStore()
    {
        EasyMock.expect(wrappedContext.getBean(BEAN_NAME, store)).andReturn(
                TEST_BEAN);
        replay();
        assertEquals("Wrong bean returned", TEST_BEAN, wrapper
                .getBean(BEAN_NAME));
    }

    /**
     * Tests obtaining a bean by name from a specified bean store.
     */
    @Test
    public void testGetBeanName()
    {
        EasyMock.expect(wrappedContext.getBean(BEAN_NAME, otherStore))
                .andReturn(TEST_BEAN);
        replay();
        assertEquals("Wrong bean returned", TEST_BEAN, wrapper.getBean(
                BEAN_NAME, otherStore));
    }

    /**
     * Tests querying a bean by class from the default bean store.
     */
    @Test
    public void testGetBeanClassDefStore()
    {
        wrappedContext.getBean(getClass(), store);
        EasyMock.expectLastCall().andReturn(TEST_BEAN);
        replay();
        assertEquals("Wrong bean returned", TEST_BEAN, wrapper
                .getBean(getClass()));
    }

    /**
     * Tests querying a bean by class from a specified bean store.
     */
    @Test
    public void testGetBeanClass()
    {
        wrappedContext.getBean(getClass(), otherStore);
        EasyMock.expectLastCall().andReturn(TEST_BEAN);
        replay();
        assertEquals("Wrong bean returned", TEST_BEAN, wrapper.getBean(
                getClass(), otherStore));
    }

    /**
     * Tests querying the name of a bean provider from the default bean store.
     */
    @Test
    public void testBeanNameForDefStore()
    {
        BeanProvider provider = EasyMock.createMock(BeanProvider.class);
        EasyMock.expect(wrappedContext.beanNameFor(provider, store)).andReturn(
                BEAN_NAME);
        replay();
        EasyMock.replay(provider);
        assertEquals("Wrong bean name", BEAN_NAME, wrapper
                .beanNameFor(provider));
        EasyMock.verify(provider);
    }

    /**
     * Tests querying the name of a bean provider from a specified bean store.
     */
    @Test
    public void testBeanNameFor()
    {
        BeanProvider provider = EasyMock.createMock(BeanProvider.class);
        EasyMock.expect(wrappedContext.beanNameFor(provider, otherStore))
                .andReturn(BEAN_NAME);
        replay();
        EasyMock.replay(provider);
        assertEquals("Wrong bean name", BEAN_NAME, wrapper.beanNameFor(
                provider, otherStore));
        EasyMock.verify(provider);
    }

    /**
     * Tests adding a bean creation listener.
     */
    @Test
    public void testAddBeanCreationListener()
    {
        BeanCreationListener l = EasyMock
                .createMock(BeanCreationListener.class);
        replay();
        EasyMock.replay(l);
        wrapper.addBeanCreationListener(l);
        assertEquals("Wrong number of listeners", 1, wrapper
                .getBeanCreationListeners().getCreationListeners().size());
        assertEquals("Wrong listener", l, wrapper.getBeanCreationListeners()
                .getCreationListeners().get(0));
        EasyMock.verify(l);
    }

    /**
     * Tests removing a bean creation listener.
     */
    @Test
    public void testRemoveBeanCreationListener()
    {
        BeanCreationListener l = EasyMock
                .createMock(BeanCreationListener.class);
        replay();
        EasyMock.replay(l);
        wrapper.addBeanCreationListener(l);
        wrapper.removeBeanCreationListener(l);
        assertTrue("Listener not removed", wrapper.getBeanCreationListeners()
                .getCreationListeners().isEmpty());
        EasyMock.verify(l);
    }

    /**
     * Tests closing a context wrapper.
     */
    @Test
    public void testClose()
    {
        wrappedContext.removeBeanCreationListener(wrapper
                .getBeanCreationListeners());
        replay();
        wrapper.close();
    }

    /**
     * Tests whether a bean implementing the BeanContextClient interface is
     * correctly initialized.
     */
    @Test
    public void testGetBeanContextClient()
    {
        BeanContextClient client = EasyMock.createMock(BeanContextClient.class);
        SingletonBeanProvider provider = new SingletonBeanProvider(
                ConstantBeanProvider.getInstance(client));
        DefaultBeanStore store = new DefaultBeanStore();
        store.addBeanProvider(BEAN_NAME, provider);
        DefaultBeanContext childCtx = new DefaultBeanContext(store);
        BeanContextWrapper context = new BeanContextWrapper(childCtx, store);
        client.setBeanContext(context);
        EasyMock.replay(client);
        replay();
        assertEquals("Wrong bean", client, context.getBean(BEAN_NAME));
        EasyMock.verify(client);
    }

    /**
     * Tests the implementation of getClassLoaderProvider().
     */
    @Test
    public void testGetClassLoaderProvider()
    {
        ClassLoaderProvider clp =
                EasyMock.createMock(ClassLoaderProvider.class);
        EasyMock.expect(wrappedContext.getClassLoaderProvider()).andReturn(clp);
        EasyMock.replay(clp);
        replay();
        assertSame("Wrong class loader provider", clp,
                wrapper.getClassLoaderProvider());
        EasyMock.verify(clp);
    }

    /**
     * Tests the implementation of setClassLoaderProvider().
     */
    @Test
    public void testSetClassLoaderProvider()
    {
        ClassLoaderProvider clp =
                EasyMock.createMock(ClassLoaderProvider.class);
        wrappedContext.setClassLoaderProvider(clp);
        EasyMock.replay(clp);
        replay();
        wrapper.setClassLoaderProvider(clp);
        EasyMock.verify(clp);
    }

    /**
     * A test implementation of BeanContextWrapper.
     */
    private static class BeanContextWrapperTestImpl extends BeanContextWrapper
    {
        /** A counter for the initWrappedContext() invocations. */
        int initWrappedContextCount;

        /** A flag whether initWrappedContext() should be invoked. */
        boolean invokeInitWrappedContext;

        public BeanContextWrapperTestImpl(BeanContext wrappedContext)
        {
            super(wrappedContext);
        }

        public BeanContextWrapperTestImpl(BeanContext wrappedContext,
                BeanStore defaultStore)
        {
            super(wrappedContext, defaultStore);
        }

        @Override
        void initWrappedContext(BeanContext wrappedContext,
                BeanCreationListenerSupport support)
        {
            initWrappedContextCount++;
            if (invokeInitWrappedContext)
            {
                super.initWrappedContext(wrappedContext, support);
            }
        }
    }
}
