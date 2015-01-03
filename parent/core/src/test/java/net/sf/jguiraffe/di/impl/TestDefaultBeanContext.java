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
package net.sf.jguiraffe.di.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jguiraffe.di.BeanContextClient;
import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.InjectionException;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class of DefaultBeanContext.
 *
 * @author Oliver Heger
 * @version $Id: TestDefaultBeanContext.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestDefaultBeanContext extends AbstractDependentProviderTest
{
    /** An array with classes used for testing. */
    private static final Class<?>[] TEST_CLASSES =
    { Integer.class, TestDefaultBeanContext.class, Boolean.class,
            Dependency.class };

    /** An array with instances of the test classes. */
    private static final Object[] TEST_OBJECTS =
    { 42, new TestDefaultBeanContext(), Boolean.TRUE,
            NameDependency.getInstance("test") };

    /** Constant for the prefix of a test bean. */
    private static final String BEAN_PREFIX = "TestBean";

    /** Constant for a test bean.*/
    private static final Object TEST_BEAN = new Object();

    /** Constant for the lock ID of the current transaction. */
    private static final Long LOCK_ID = 1L;

    /** The context to be tested. */
    private DefaultBeanContextTestImpl context;

    @Override
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        context = new DefaultBeanContextTestImpl(store);
        DefaultBeanContext.resetTxID();
    }

    /**
     * Tests a newly created instance.
     */
    @Test
    public void testInit()
    {
        assertSame("Wrong default bean store", store,
                context.getDefaultBeanStore());
        DefaultClassLoaderProvider clp =
                (DefaultClassLoaderProvider) context.getClassLoaderProvider();
        assertNotNull("No class loader provider", clp);
        assertTrue("Already class loaders registered", clp.classLoaderNames()
                .isEmpty());
    }

    /**
     * Tests an instance that was created using the standard constructor.
     */
    @Test
    public void testInitStdCtor()
    {
        DefaultBeanContext ctx = new DefaultBeanContext();
        assertNull("Got a default store", ctx.getDefaultBeanStore());
    }

    /**
     * Tests collecting the names of the defined beans.
     */
    @Test
    public void testBeanNames()
    {
        final int count = 10;
        int idx = createProviderChain(count, false);
        replayProviders();
        Set<String> names = context.beanNames();
        assertEquals("Wrong number of bean names", count, names.size());
        for (int i = 0; i < count; i++)
        {
            String name = getProviderName(idx + i);
            assertTrue("Name not found: " + name, names.contains(name));
        }
    }

    /**
     * Tests collecting the names of a hierarchy of bean stores.
     */
    @Test
    public void testBeanNamesHierarchical()
    {
        final int count = 12;
        String[] moreNames =
        { "anotherBean", "yetAnotherBean", "additionalBean" };
        int idx = createProviderChain(count, true);
        DefaultBeanStore subStore = new DefaultBeanStore("SubStore", store);
        for (String n : moreNames)
        {
            subStore.addBeanProvider(n, createProviderMock());
        }
        replayProviders();
        Set<String> names = context.beanNames(subStore);
        assertEquals("Wrong number of beans", count + moreNames.length, names
                .size());
        for (int i = 0; i < count; i++)
        {
            String name = getProviderName(idx + i);
            assertTrue("Name not found: " + name, names.contains(name));
        }
        for (String n : moreNames)
        {
            assertTrue("Name not found: " + n, names.contains(n));
        }
    }

    /**
     * Tests querying the bean names from a null store.
     */
    @Test
    public void testBeanNamesNull()
    {
        assertTrue("Wrong result for null store", context.beanNames(null)
                .isEmpty());
    }

    /**
     * Adds a mock bean provider for beans of the specified class to the given
     * bean store.
     *
     * @param c the class of the provider
     * @param store the store
     * @return the index of the auto-generated name
     */
    private int addClassProvider(Class<?> c, DefaultBeanStore store)
    {
        BeanProvider p = createProviderMock();
        p.getBeanClass(context.getInternalDependencyProvider());
        EasyMock.expectLastCall().andStubReturn(c);
        int idx = nextIndex();
        store.addBeanProvider(getProviderName(idx), p);
        return idx;
    }

    /**
     * Tests accessing the defined bean classes.
     */
    @Test
    public void testBeanClasses()
    {
        Class<?>[] classes1 = TEST_CLASSES;
        for (Class<?> c : classes1)
        {
            addClassProvider(c, store);
        }
        replayProviders();
        Set<Class<?>> clsSet = context.beanClasses();
        assertEquals("Wrong number of classes", classes1.length, clsSet.size());
        for (Class<?> c : classes1)
        {
            assertTrue("Class not found " + c, clsSet.contains(c));
        }
    }

    /**
     * Tests collecting the defined bean classes in a hierarchy of stores.
     */
    @Test
    public void testBeanClassesHierarchical()
    {
        Class<?>[] classes1 =
        { String.class, getClass(), DefaultBeanContext.class };
        Class<?>[] classes2 = TEST_CLASSES;
        for (Class<?> c : classes1)
        {
            addClassProvider(c, store);
        }
        DefaultBeanStore subStore = new DefaultBeanStore("SubStore", store);
        for (Class<?> c : classes2)
        {
            addClassProvider(c, subStore);
        }
        replayProviders();
        Set<Class<?>> clsSet = context.beanClasses(subStore);
        List<Class<?>> clsList = new ArrayList<Class<?>>();
        clsList.addAll(Arrays.asList(classes1));
        clsList.addAll(Arrays.asList(classes2));
        for (Class<?> c : clsList)
        {
            assertTrue("Class not found " + c, clsSet.contains(c));
        }
    }

    /**
     * Tests querying the classes from a null bean store.
     */
    @Test
    public void testBeanClassesNull()
    {
        assertTrue("Wrong result for null bean store", context
                .beanClasses(null).isEmpty());
    }

    /**
     * Tests the containsBean() method in the default bean store.
     */
    @Test
    public void testContainsBean()
    {
        final int count = 26;
        int idx = createProviderChain(count, false);
        replayProviders();
        for (int i = 0; i < count; i++)
        {
            String name = getProviderName(idx + i);
            assertTrue("Bean not found " + name, context.containsBean(name));
        }
    }

    /**
     * Tests the containsBean() method in a hierarchy of stores.
     */
    @Test
    public void testContainsBeanHierarchical()
    {
        final int count = 11;
        int idx = createProviderChain(count, false);
        DefaultBeanStore subStore = new DefaultBeanStore("SubStore", store);
        String[] moreBeans =
        { "AnotherBean", "TestBean", "Harry", "Hirsch" };
        for (String n : moreBeans)
        {
            subStore.addBeanProvider(n, createProviderMock());
        }
        replayProviders();
        for (int i = 0; i < count; i++)
        {
            String name = getProviderName(idx + i);
            assertTrue("Bean not found " + name, context.containsBean(name,
                    subStore));
        }
        for (String name : moreBeans)
        {
            assertTrue("Bean not found " + name, context.containsBean(name,
                    subStore));
            assertFalse("Bean found in wrong hierarchy " + name, context
                    .containsBean(name));
        }
    }

    /**
     * Tests the containsBean() method when a null store is provided.
     */
    @Test
    public void testContainsBeanNullStore()
    {
        assertFalse("Wrong result for null store", context.containsBean("test",
                null));
    }

    /**
     * Tests the containsBean() method when a null bean is provided.
     */
    @Test
    public void testContainsBeanNullBean()
    {
        assertFalse("Wrong result for null bean", context
                .containsBean((String) null));
    }

    /**
     * Tests the containsBean() method for classes.
     */
    @Test
    public void testContainsBeanClass()
    {
        Class<?>[] classes1 = TEST_CLASSES;
        for (Class<?> c : classes1)
        {
            addClassProvider(c, store);
        }
        replayProviders();
        for (Class<?> c : classes1)
        {
            assertTrue("Bean with class not found " + c, context
                    .containsBean(c));
        }
    }

    /**
     * Tests the containsBean() method for classes in a hierarchy of stores.
     */
    @Test
    public void testContainsBeanClassHierarchical()
    {
        Class<?>[] classes1 =
        { String.class, Byte.class, Short.class };
        Class<?>[] classes2 = TEST_CLASSES;
        for (Class<?> c : classes1)
        {
            addClassProvider(c, store);
        }
        DefaultBeanStore subStore = new DefaultBeanStore("SubStore", store);
        for (Class<?> c : classes2)
        {
            addClassProvider(c, subStore);
        }
        replayProviders();
        for (Class<?> c : classes1)
        {
            assertTrue("Bean with class not found " + c, context.containsBean(
                    c, subStore));
        }
        for (Class<?> c : classes2)
        {
            assertTrue("Bean with class not found " + c, context.containsBean(
                    c, subStore));
            assertFalse("Bean found in wrong hierarchy " + c, context
                    .containsBean(c));
        }
    }

    /**
     * Tests the containsBean() method for classes when a null store is
     * provided.
     */
    @Test
    public void testContainsBeanClassNullStore()
    {
        assertFalse("Wrong result for null store", context.containsBean(
                getClass(), null));
    }

    /**
     * Tests the containsBean() method for classes when a null class is
     * provided.
     */
    @Test
    public void testContainsBeanClassNullClass()
    {
        assertFalse("Wrong result for null class", context
                .containsBean((Class<?>) null));
    }

    /**
     * Creates some mock providers with dependencies that can be used for
     * testing the overloaded getBean() methods. For each element of the test
     * classes array an instance will be created.
     *
     * @return the index of the first created provider
     */
    private int setUpProviders()
    {
        final int count = TEST_CLASSES.length;
        int idx = createProviderChain(count, false, count + 1);
        int i = 0;
        for (BeanProvider p : getProviders(idx, idx + count - 1))
        {
            p.getBeanClass((DependencyProvider) EasyMock.anyObject());
            EasyMock.expectLastCall().andStubReturn(TEST_CLASSES[i]);
            EasyMock.expect(
                    p.getBean((DependencyProvider) EasyMock.anyObject()))
                    .andReturn(TEST_OBJECTS[i]).times(0, count + 1);
            p.setLockID(LOCK_ID);
            EasyMock.expectLastCall().times(0, count + 1);
            p.setLockID(null);
            EasyMock.expectLastCall().times(0, count + 1);
            i++;
        }
        return idx;
    }

    /**
     * A simple test for accessing beans by name. This is not yet a real world
     * scenario with concurrent thread access.
     */
    @Test
    public void testGetBeanByName()
    {
        int idx = setUpProviders();
        replayProviders();
        for (int i = 0; i < TEST_CLASSES.length; i++)
        {
            DefaultBeanContext.resetTxID();
            assertEquals("Wrong bean at " + i, TEST_OBJECTS[i], context
                    .getBean(getProviderName(idx + i)));
        }
    }

    /**
     * A simple test for accessing beans by their class. This is not yet a real
     * world scenario with concurrent thread access.
     */
    @Test
    public void testGetBeanByClass()
    {
        setUpProviders();
        replayProviders();
        for (int i = 0; i < TEST_CLASSES.length; i++)
        {
            DefaultBeanContext.resetTxID();
            assertEquals("Wrong bean at " + i, TEST_OBJECTS[i], context
                    .getBean(TEST_CLASSES[i]));
        }
    }

    /**
     * Tries to obtain a bean with a null name. This should cause an exception.
     */
    @Test(expected = InjectionException.class)
    public void testGetBeanByNameNull()
    {
        setUpProviders();
        replayProviders();
        context.getBean((String) null);
    }

    /**
     * Tries to obtain a bean by name when a null store is provided. This should
     * cause an exception.
     */
    @Test(expected = InjectionException.class)
    public void testGetBeanByNameNullStore()
    {
        context.getBean("TestBean", null);
    }

    /**
     * Tries to obtain a bean with a name that cannot be resolved. This should
     * cause an exception.
     */
    @Test(expected = InjectionException.class)
    public void testGetBeanByNameNonExisting()
    {
        setUpProviders();
        replayProviders();
        context.getBean("A non existing bean!");
    }

    /**
     * Tries to obtain a bean with a null class. This should cause an exception.
     */
    @Test(expected = InjectionException.class)
    public void testGetBeanByClassNull()
    {
        setUpProviders();
        replayProviders();
        context.getBean((Class<?>) null);
    }

    /**
     * Tests querying a bean by class when the store is null. This should cause
     * an exception.
     */
    @Test(expected = InjectionException.class)
    public void testGetBeanByClassNullStore()
    {
        context.getBean(TEST_CLASSES[0], null);
    }

    /**
     * Tries to obtain a bean with a class that cannot be resolved. This should
     * cause an exception.
     */
    @Test(expected = InjectionException.class)
    public void testGetBeanByClassNonExisting()
    {
        setUpProviders();
        replayProviders();
        context.getBean(DefaultBeanContext.class);
    }

    /**
     * Tests concurrent access to the getBean() method. We simulate an already
     * running transaction that locked one of the dependent bean providers. In
     * the 2nd trial the current transaction should be successful.
     */
    @Test
    public void testGetBeanConcurrent()
    {
        int idx1 = setUpProviders();
        BeanProvider p = createProviderMock();
        EasyMock.expect(p.getDependencies()).andStubReturn(null);
        EasyMock.expect(p.getLockID()).andReturn(42L);
        EasyMock.expect(p.getLockID()).andReturn(null);
        p.setLockID(LOCK_ID);
        p.setLockID(null);
        int idx2 = addProvider(p);
        p = createProviderMock(true, 2, getProviderName(idx1),
                getProviderName(idx2));
        p.setLockID(LOCK_ID);
        p.setLockID(null);
        EasyMock.expect(p.getBean((DependencyProvider) EasyMock.anyObject()))
                .andReturn(BEAN_PREFIX);
        store.addBeanProvider(BEAN_PREFIX, p);
        replayProviders();
        final Object monitor = new Object();
        context.initWaitMonitor(monitor);
        new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    synchronized (monitor)
                    {
                        while (!context.isWaitingTx())
                        {
                            monitor.wait();
                        }
                    }
                    synchronized (store)
                    {
                        store.notifyAll();
                    }
                }
                catch (InterruptedException iex)
                {
                    iex.printStackTrace();
                }
            }
        }.start();
        assertEquals("Wrong bean value", BEAN_PREFIX, context
                .getBean(BEAN_PREFIX));
    }

    /**
     * Tests searching for a bean provider in the default bean store.
     */
    @Test
    public void testBeanNameForDefault()
    {
        final int count = 12;
        Map<Integer, BeanProvider> providers = new HashMap<Integer, BeanProvider>();
        for (int i = 0; i < count; i++)
        {
            BeanProvider bp = createProviderMock();
            providers.put(addProvider(bp), bp);
        }
        replayProviders();
        for (Map.Entry<Integer, BeanProvider> e : providers.entrySet())
        {
            assertEquals("Wrong name for provider " + e.getKey(),
                    getProviderName(e.getKey()), context.beanNameFor(e
                            .getValue()));
        }
    }

    /**
     * Tests searching for a bean provider in a hierarchical structure of bean
     * stores.
     */
    @Test
    public void testBeanNameForHierarchical()
    {
        DefaultBeanStore subStore = new DefaultBeanStore("subStore", store);
        Map<String, BeanProvider> providers = new HashMap<String, BeanProvider>();
        final String[] names = {
                "Romeo", "Juliett", "Titus Adronicus", "John Falstaff",
                "Desdemona"
        };
        for (String bean : names)
        {
            BeanProvider bp = createProviderMock();
            subStore.addBeanProvider(bean, bp);
            providers.put(bean, bp);
            addProvider(createProviderMock());
        }
        replayProviders();
        for (Map.Entry<String, BeanProvider> e : providers.entrySet())
        {
            assertEquals("Wrong name for provider " + e.getKey(), e.getKey(),
                    context.beanNameFor(e.getValue(), subStore));
        }
    }

    /**
     * Tests querying the name for an unknown bean provider.
     */
    @Test
    public void testBeanNameForUnknown()
    {
        for (int i = 0; i < 25; i++)
        {
            addProvider(createProviderMock());
        }
        BeanProvider check = createProviderMock();
        replayProviders();
        assertNull("Wrong result for unknown provider", context
                .beanNameFor(check));
    }

    /**
     * Tests querying the name for a bean provider when a null store is
     * provided.
     */
    @Test
    public void testBeanNameForNullStore()
    {
        BeanProvider check = createProviderMock();
        replayProviders();
        assertNull("Wrong result for null store", context.beanNameFor(check,
                null));
    }

    /**
     * Tests adding a null creation listener. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddBeanCreationListenerNull()
    {
        context.addBeanCreationListener(null);
    }

    /**
     * Tests receiving bean creation events.
     */
    @Test
    public void testAddBeanCreationListenerReceive()
    {
        DefaultDependencyProvider depProvider = new DefaultDependencyProvider(
                context);
        BeanProvider provider = createProviderMock();
        replayProviders();
        BeanCreationListenerTestImpl l1 = new BeanCreationListenerTestImpl();
        BeanCreationListenerTestImpl l2 = new BeanCreationListenerTestImpl();
        context.addBeanCreationListener(l1);
        context.addBeanCreationListener(l2);
        context.beanCreated(TEST_BEAN, provider, depProvider);
        l1.checkNextEvent(context, TEST_BEAN, provider, depProvider);
        assertFalse("Too many events (1)", l1.hasMoreEvents());
        l2.checkNextEvent(context, TEST_BEAN, provider, depProvider);
        assertFalse("Too many events (2)", l2.hasMoreEvents());
    }

    /**
     * Tests removing a bean creation listener.
     */
    @Test
    public void testRemoveBeanCreationListener()
    {
        DefaultDependencyProvider depProvider = new DefaultDependencyProvider(
                context);
        BeanProvider provider = createProviderMock();
        replayProviders();
        BeanCreationListenerTestImpl l1 = new BeanCreationListenerTestImpl();
        BeanCreationListenerTestImpl l2 = new BeanCreationListenerTestImpl();
        context.addBeanCreationListener(l1);
        context.addBeanCreationListener(l2);
        context.removeBeanCreationListener(l2);
        context.beanCreated(TEST_BEAN, provider, depProvider);
        assertTrue("Too few events", l1.hasMoreEvents());
        assertFalse("Listener not removed", l2.hasMoreEvents());
    }

    /**
     * Tests the creation of a bean implementing the BeanContextClient
     * interface. This bean must be passed the current context.
     */
    @Test
    public void testBeanCreatedContextClient()
    {
        DefaultDependencyProvider depProvider = new DefaultDependencyProvider(
                context);
        BeanProvider provider = createProviderMock();
        BeanContextClient client = EasyMock.createMock(BeanContextClient.class);
        client.setBeanContext(context);
        replayProviders();
        EasyMock.replay(client);
        context.beanCreated(client, provider, depProvider);
        EasyMock.verify(client);
    }

    /**
     * Tests the close() method. We cannot test much here, only that no
     * collaborators are touched.
     */
    @Test
    public void testClose()
    {
        BeanStore mockStore = EasyMock.createMock(BeanStore.class);
        EasyMock.replay(mockStore);
        context.setDefaultBeanStore(mockStore);
        context.close();
        EasyMock.verify(mockStore);
    }

    /**
     * Tests setting a new class loader provider.
     */
    @Test
    public void testSetClassLoaderProvider()
    {
        ClassLoaderProvider clp = EasyMock
                .createMock(ClassLoaderProvider.class);
        EasyMock.replay(clp);
        context.setClassLoaderProvider(clp);
        assertEquals("Wrong class loader provider", clp, context
                .getClassLoaderProvider());
        EasyMock.verify(clp);
    }

    /**
     * Tests setting a null CLP. This should not be allowed.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetClassLoaderProviderNull()
    {
        context.setClassLoaderProvider(null);
    }

    /**
     * A test implementation of the context class that allows for better control
     * of concurrent transactions. The waitForTx() method is overridden to allow
     * a thread to be continued when another thread has to wait.
     */
    static class DefaultBeanContextTestImpl extends DefaultBeanContext
    {
        /** A monitor object for a waiting thread. */
        private Object waitMonitor;

        /** A flag if a transaction is already waiting. */
        private volatile boolean waitingTx;

        public DefaultBeanContextTestImpl(BeanStore store)
        {
            super(store);
        }

        /**
         * Initializes the wait monitor. This monitor object will be triggered
         * by the overridden waitForTx() method. If a thread is waiting at this
         * monitor, it will gain the running state again.
         *
         * @param obj the monitor object
         */
        public void initWaitMonitor(Object obj)
        {
            waitMonitor = obj;
        }

        public boolean isWaitingTx()
        {
            return waitingTx;
        }

        /**
         * Suspends a transaction. If a waitMonitor was set, this object will be
         * notified.
         */
        @Override
        protected void waitForTx(BeanStore root) throws InterruptedException
        {
            if (waitMonitor != null)
            {
                waitingTx = true;
                synchronized (waitMonitor)
                {
                    waitMonitor.notifyAll();
                }
            }
            super.waitForTx(root);
        }
    }
}
