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
package net.sf.jguiraffe.di.impl.providers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.InjectionException;
import net.sf.jguiraffe.di.InvocationHelper;
import net.sf.jguiraffe.di.ReflectionTestClass;
import net.sf.jguiraffe.di.impl.ChainedInvocation;
import net.sf.jguiraffe.di.impl.ClassDescription;
import net.sf.jguiraffe.di.impl.ConstructorInvocation;
import net.sf.jguiraffe.di.impl.DefaultBeanContext;
import net.sf.jguiraffe.di.impl.DefaultBeanStore;
import net.sf.jguiraffe.di.impl.MethodInvocation;
import net.sf.jguiraffe.di.impl.NameDependency;
import net.sf.jguiraffe.di.impl.SetPropertyInvocation;

import org.junit.Before;
import org.junit.Test;

/**
 * A test class for several {@code BeanProvider} implementations that
 * tests querying beans under real-life conditions. Several complex scenarios,
 * including concurrent access to a bean store, will be tested.
 *
 * @author Oliver Heger
 * @version $Id: TestBeanProviders.java 207 2012-02-09 07:30:13Z oheger $
 */
public class TestBeanProviders
{
    /** Constant for the dependency to bean A. */
    private static final NameDependency REF_BEAN_A = NameDependency
            .getInstance("BEAN_A");

    /** Constant for the dependency to bean B. */
    private static final NameDependency REF_BEAN_B = NameDependency
            .getInstance("BEAN_B");

    /** Constant for the dependency to the test data bean. */
    private static final NameDependency REF_DATA = NameDependency
            .getInstance("TEST_DATA");

    /** Constant for the dependency of the string data bean. */
    private static final NameDependency REF_STR_DATA = NameDependency
            .getInstance("STR_TEST_DATA");

    /** Constant for the dependency of the int data bean. */
    private static final NameDependency REF_INT_DATA = NameDependency
            .getInstance("INT_TEST_DATA");

    /** Constant for an invocation of bean A's default constructor. */
    private static final ConstructorInvocation CTOR_BEAN_A = new ConstructorInvocation(
            ClassDescription.getInstance(BeanA.class), null);

    /** Constant for an invocation of bean A's constructor with a reference to B. */
    private static final ConstructorInvocation CTOR_BEAN_A_REF = new ConstructorInvocation(
            ClassDescription.getInstance(BeanA.class), new ClassDescription[]
            { ClassDescription.getInstance(BeanB.class) }, REF_BEAN_B);

    /** Constant for an invocation of bean B's constructor with a reference to A. */
    private static final ConstructorInvocation CTOR_BEAN_B_REF = new ConstructorInvocation(
            ClassDescription.getInstance(BeanB.class), new ClassDescription[]
            { ClassDescription.getInstance(BeanA.class) }, REF_BEAN_A);

    /** Constant for an invocation for setting the data bean property. */
    private static final SetPropertyInvocation SET_DATA_INV = new SetPropertyInvocation(
            "testData", REF_DATA);

    /** Stores the parent bean store. */
    private DefaultBeanStore parentStore;

    /** Stores the main bean store. */
    private DefaultBeanStore store;

    /** Stores the bean context used for testing. */
    private DefaultBeanContext context;

    /** Stores a list where test threads can store their data. */
    private Collection<Object> threadData;

    @Before
    public void setUp() throws Exception
    {
        parentStore = new DefaultBeanStore();
        store = new DefaultBeanStore("mainStore", parentStore);
        context = new DefaultBeanContext(store);
        BeanA.instances.set(0);
        BeanB.instances.set(0);
    }

    /**
     * Adds a bean provider for a data bean to the test bean store (the parent
     * store). This can be either a singleton or a factory provider.
     *
     * @param singleton a flag whether a singleton provider should be used
     */
    private void setUpDataBean(boolean singleton)
    {
        parentStore.addBeanProvider(REF_STR_DATA.getName(),
                ConstantBeanProvider
                        .getInstance(SimpleBeanProviderTestHelper.STR_VALUE));
        parentStore.addBeanProvider(REF_INT_DATA.getName(),
                ConstantBeanProvider
                        .getInstance(SimpleBeanProviderTestHelper.INT_VALUE));
        BeanProvider creator = new ConstructorBeanProvider(
                new ConstructorInvocation(ClassDescription
                        .getInstance(ReflectionTestClass.class), null));
        MethodInvocation inv = new MethodInvocation("initialize", null,
                REF_STR_DATA, REF_INT_DATA);
        BeanProvider provider = singleton ? new SingletonBeanProvider(creator,
                inv) : new FactoryBeanProvider(creator, inv);
        parentStore.addBeanProvider(REF_DATA.getName(), provider);
    }

    /**
     * Tests whether the data bean was correctly initialized.
     *
     * @param dataBean the data bean
     */
    private void checkDataBean(ReflectionTestClass dataBean)
    {
        assertEquals("Wrong string property",
                SimpleBeanProviderTestHelper.STR_VALUE, dataBean
                        .getStringProp());
        assertEquals("Wrong int property",
                SimpleBeanProviderTestHelper.INT_VALUE, dataBean.getIntProp());
    }

    /**
     * Performs a multi-threading test. The method with the specified name will
     * be invoked by the given number of test threads concurrently.
     *
     * @param methodName the name of the test method
     * @param count the number of test threads to create
     */
    private void performThreadTest(String methodName, int count)
    {
        if (threadData == null)
        {
            threadData = Collections.synchronizedSet(new HashSet<Object>(count));
        }

        CountDownLatch startLatch = new CountDownLatch(count);
        InvocationHelper invHlp = new InvocationHelper();
        List<TestExecutorThread> threads =
                new ArrayList<TestExecutorThread>(count);
        for (int i = 0; i < count; i++)
        {
            TestExecutorThread t =
                    new TestExecutorThread(startLatch, methodName, invHlp);
            threads.add(t);
            t.start();
        }

        try
        {
            for (TestExecutorThread t : threads)
            {
                t.join();
                if (t.exception != null)
                {
                    fail("Exception in thread: " + t.exception);
                }
            }
        }
        catch (InterruptedException iex)
        {
            fail("Waiting for threads interrupted: " + iex);
        }
    }

    /**
     * Tests obtaining a bean from a singleton provider.
     */
    @Test
    public void testGetSingletonBean()
    {
        initTestSingletonBean();
        final int count = 10;
        BeanA bean = (BeanA) context.getBean(REF_BEAN_A.getName());
        checkDataBean(bean.getTestData());
        for (int i = 0; i < count; i++)
        {
            assertSame("Different instance returned", bean, context
                    .getBean(REF_BEAN_A.getName()));
        }
        assertEquals("Wrong number of instances created", 1, BeanA
                .getInstanceCount());
    }

    /**
     * Tests obtaining a bean from a singleton provider with multiple threads.
     */
    @Test
    public void testConcurrentGetSingletonBean()
    {
        initTestSingletonBean();
        performThreadTest("concurrentGetSingletonBean", 20);
        assertEquals("Wrong number of test instances", 1, threadData.size());
        assertEquals("Wrong number of instances created", 1, BeanA
                .getInstanceCount());
    }

    /**
     * Test method for obtaining a singleton bean in a multi-threaded
     * environment.
     */
    public void concurrentGetSingletonBean()
    {
        BeanA bean = (BeanA) context.getBean(REF_BEAN_A.getName());
        checkDataBean(bean.getTestData());
        threadData.add(bean);
    }

    /**
     * Initializes the test for obtaining a bean from a singleton provider.
     */
    private void initTestSingletonBean()
    {
        setUpDataBean(true);
        BeanProvider provider = new SingletonBeanProvider(
                new ConstructorBeanProvider(CTOR_BEAN_A), SET_DATA_INV);
        store.addBeanProvider(REF_BEAN_A.getName(), provider);
    }

    /**
     * Tests the factory bean provider when there is a dependency to another
     * bean controlled by a factory.
     */
    @Test
    public void testGetFactoryBean()
    {
        initTestFactoryBean(false);
        final int count = 12;
        Set<BeanA> beans = new HashSet<BeanA>();
        Set<ReflectionTestClass> dataBeans = new HashSet<ReflectionTestClass>();
        for (int i = 0; i < count; i++)
        {
            BeanA bean = (BeanA) context.getBean(REF_BEAN_A.getName());
            checkDataBean(bean.getTestData());
            assertTrue("No new bean instance", beans.add(bean));
            assertTrue("No new data bean instance", dataBeans.add(bean
                    .getTestData()));
        }
        assertEquals("Wrong number of instances created", count, BeanA
                .getInstanceCount());
    }

    /**
     * Tests the factory bean provider when there is a dependency to a singleton
     * bean.
     */
    @Test
    public void testGetFactoryBeanSingletonRef()
    {
        initTestFactoryBean(true);
        final int count = 17;
        Set<BeanA> beans = new HashSet<BeanA>();
        ReflectionTestClass dataBean = null;
        for (int i = 0; i < count; i++)
        {
            BeanA bean = (BeanA) context.getBean(REF_BEAN_A.getName());
            checkDataBean(bean.getTestData());
            assertTrue("No new bean instance", beans.add(bean));
            if (dataBean == null)
            {
                dataBean = bean.getTestData();
            }
            else
            {
                assertSame("Wrong instance for data bean", dataBean, bean
                        .getTestData());
            }
        }
        assertEquals("Wrong number of instances created", count, BeanA
                .getInstanceCount());
    }

    /**
     * Tests querying a factory bean with multiple threads.
     */
    @Test
    public void testConcurrentGetFactoryBean()
    {
        final int count = 11;
        initTestFactoryBean(false);
        performThreadTest("concurrentGetFactoryBean", count);
        assertEquals("Wrong number of objects in collection", 2 * count,
                threadData.size());
        assertEquals("Wrong number of bean instances created", count, BeanA
                .getInstanceCount());
    }

    /**
     * Test method for querying a factory bean with multiple threads.
     */
    public void concurrentGetFactoryBean()
    {
        BeanA bean = (BeanA) context.getBean(REF_BEAN_A.getName());
        threadData.add(bean);
        threadData.add(bean.getTestData());
        checkDataBean(bean.getTestData());
    }

    /**
     * Initializes tests for obtaining a bean from a factory provider.
     *
     * @param dataSingleton a flag whether the data bean should be a singleton
     */
    private void initTestFactoryBean(boolean dataSingleton)
    {
        setUpDataBean(dataSingleton);
        BeanProvider provider = new FactoryBeanProvider(
                new ConstructorBeanProvider(CTOR_BEAN_A), SET_DATA_INV);
        store.addBeanProvider(REF_BEAN_A.getName(), provider);
    }

    /**
     * Tests two singleton beans with a valid cyclic dependency.
     */
    @Test
    public void testGetSingletonCyclicDependency()
    {
        initGetSingletonCyclicDependency();
        BeanA beanA = (BeanA) context.getBean(REF_BEAN_A.getName());
        BeanB beanB = (BeanB) context.getBean(REF_BEAN_B.getName());
        checkDataBean(beanA.getTestData());
        assertSame("Wrong B reference", beanB, beanA.getRefB());
        assertSame("Wrong A reference", beanA, beanB.getRefA());
        assertEquals("Wrong number of A instances", 1, BeanA.getInstanceCount());
        assertEquals("Wrong number of B instances", 1, BeanB.getInstanceCount());
    }

    /**
     * Tests obtaining singleton beans with a valid cyclic dependency in
     * multiple threads.
     */
    @Test
    public void testConcurrentGetSingletonCyclicDependency()
    {
        initGetSingletonCyclicDependency();
        performThreadTest("concurrentGetSingletonCyclicDependency", 10);
        assertEquals("Wrong number of elements in set", 3, threadData.size());
        assertEquals("Wrong number of A instances", 1, BeanA.getInstanceCount());
        assertEquals("Wrong number of B instances", 1, BeanB.getInstanceCount());
    }

    /**
     * Tests method for concurrent access to singleton beans with a valid cyclic
     * dependency.
     */
    public void concurrentGetSingletonCyclicDependency()
    {
        BeanA beanA = (BeanA) context.getBean(REF_BEAN_A.getName());
        checkDataBean(beanA.getTestData());
        threadData.add(beanA);
        threadData.add(beanA.getRefB());
        threadData.add(beanA.getTestData());
    }

    /**
     * Initializes tests for singleton beans with an (allowed) cyclic
     * dependency.
     */
    private void initGetSingletonCyclicDependency()
    {
        setUpDataBean(true);
        ChainedInvocation cinv = new ChainedInvocation();
        cinv.addInvokable(SET_DATA_INV);
        cinv.addInvokable(new SetPropertyInvocation("refB", REF_BEAN_B));
        BeanProvider provider = new SingletonBeanProvider(
                new ConstructorBeanProvider(CTOR_BEAN_A), cinv);
        store.addBeanProvider(REF_BEAN_A.getName(), provider);
        provider = new SingletonBeanProvider(new ConstructorBeanProvider(
                CTOR_BEAN_B_REF));
        store.addBeanProvider(REF_BEAN_B.getName(), provider);
    }

    /**
     * Tests two singleton bean providers with an invalid cyclic dependency.
     * This should cause an exception.
     */
    @Test
    public void testGetSingletonCyclicDependencyInvalid()
    {
        BeanProvider provider = new SingletonBeanProvider(
                new ConstructorBeanProvider(CTOR_BEAN_A_REF));
        store.addBeanProvider(REF_BEAN_A.getName(), provider);
        provider = new SingletonBeanProvider(new ConstructorBeanProvider(
                CTOR_BEAN_B_REF));
        store.addBeanProvider(REF_BEAN_B.getName(), provider);
        try
        {
            context.getBean(BeanA.class);
            fail("Invalid cyclic dependency not detected!");
        }
        catch (InjectionException iex)
        {
            assertTrue("Wrong exception message: " + iex, iex.getMessage()
                    .indexOf("Unresolvable cyclic dependency") >= 0);
            assertTrue("Cyclic dependency not found in message: " + iex, iex
                    .getMessage().indexOf(REF_BEAN_B.toString()) >= 0);
        }
    }

    /**
     * Tests obtaining beans from a factory bean provider with a valid cyclic
     * dependency.
     */
    @Test
    public void testGetFactoryCyclicDependency()
    {
        initGetFactoryCyclicDependency();
        BeanA beanA = (BeanA) context.getBean(REF_BEAN_A.getName());
        BeanB beanB = (BeanB) context.getBean(REF_BEAN_B.getName());
        checkDataBean(beanA.getTestData());
        assertNotSame("Same A dependency", beanA, beanB.getRefA());
        assertNotSame("Same data bean", beanA.getTestData(), beanB.getRefA()
                .getTestData());
        checkDataBean(beanB.getRefA().getTestData());
        assertNotSame("Same B dependency", beanB, beanA.getRefB());
        assertEquals("Wrong number of A instances", 2, BeanA.getInstanceCount());
        assertEquals("Wrong number of B instances", 2, BeanB.getInstanceCount());
    }

    /**
     * Tests obtaining beans from a factory bean provider with a valid cyclic
     * dependency in multiple threads.
     */
    @Test
    public void testConcurrentGetFactoryCyclicDependency()
    {
        final int count = 27;
        initGetFactoryCyclicDependency();
        performThreadTest("concurrentGetFactoryCyclicDependency", count);
        assertEquals("Wrong number of elements in set", 3 * count, threadData
                .size());
        assertEquals("Wrong number of A instances", count, BeanA
                .getInstanceCount());
        assertEquals("Wrong number of B instances", count, BeanB
                .getInstanceCount());
    }

    /**
     * Tests method for obtaining factory beans with a valid cyclic dependency
     * in multiple threads.
     */
    public void concurrentGetFactoryCyclicDependency()
    {
        BeanB beanB = context.getBean(BeanB.class);
        threadData.add(beanB);
        threadData.add(beanB.getRefA());
        threadData.add(beanB.getRefA().getTestData());
        checkDataBean(beanB.getRefA().getTestData());
    }

    /**
     * Initializes tests for factory providers with a valid cyclic dependency.
     */
    private void initGetFactoryCyclicDependency()
    {
        setUpDataBean(false);
        ChainedInvocation cinv = new ChainedInvocation();
        cinv.addInvokable(SET_DATA_INV);
        cinv.addInvokable(new SetPropertyInvocation("refB", REF_BEAN_B));
        BeanProvider provider = new FactoryBeanProvider(
                new ConstructorBeanProvider(CTOR_BEAN_A), cinv);
        store.addBeanProvider(REF_BEAN_A.getName(), provider);
        provider = new FactoryBeanProvider(new ConstructorBeanProvider(
                new ConstructorInvocation(ClassDescription
                        .getInstance(BeanB.class), null)),
                new SetPropertyInvocation("refA", REF_BEAN_A));
        store.addBeanProvider(REF_BEAN_B.getName(), provider);
    }

     /**
      * Tests two factory providers when one provider's bean creation needs
      * the other provider and the other provider's initialization needs the
      * first one. This is a complicated, but valid scenario.
      */
    @Test
    public void testGetFactoryCylcicDependencyInInit()
    {
        BeanProvider provider = new FactoryBeanProvider(
                new ConstructorBeanProvider(CTOR_BEAN_A),
                new SetPropertyInvocation("refB", REF_BEAN_B));
        store.addBeanProvider(REF_BEAN_A.getName(), provider);
        provider = new FactoryBeanProvider(new ConstructorBeanProvider(
                CTOR_BEAN_B_REF));
        store.addBeanProvider(REF_BEAN_B.getName(), provider);
        BeanA beanA = (BeanA) context.getBean(REF_BEAN_A.getName());
        BeanB beanB = (BeanB) context.getBean(REF_BEAN_B.getName());
        assertSame("Wrong cyclic bean B ref", beanB, beanB.getRefA().getRefB());
        assertSame("Wrong cyclic bean A ref", beanA, beanA.getRefB().getRefA());
        assertNotSame("Same B beans", beanB, beanA.getRefB());
        assertNotSame("Same A beans", beanA, beanB.getRefA());
        assertEquals("Wrong number of created A instances", 2, BeanA
                .getInstanceCount());
        assertEquals("Wrong number of created B instances", 2, BeanB
                .getInstanceCount());
    }

    /**
     * Tests two factory bean providers that have an invalid cyclic dependency.
     * This should cause an exception.
     */
    @Test
    public void testGetFactoryCyclicDependencyInvalid()
    {
        BeanProvider provider = new FactoryBeanProvider(
                new ConstructorBeanProvider(CTOR_BEAN_A_REF));
        store.addBeanProvider(REF_BEAN_A.getName(), provider);
        provider = new FactoryBeanProvider(new ConstructorBeanProvider(
                CTOR_BEAN_B_REF));
        store.addBeanProvider(REF_BEAN_B.getName(), provider);
        try
        {
            context.getBean(REF_BEAN_B.getName());
            fail("Invalid cyclic dependency was not detected!");
        }
        catch (InjectionException iex)
        {
            assertTrue("Wrong exception message: " + iex, iex.getMessage()
                    .indexOf("Unresolvable cyclic dependency") >= 0);
            assertTrue("Cyclic dependency not found in message: " + iex, iex
                    .getMessage().indexOf(REF_BEAN_A.toString()) >= 0);
        }
    }

    /**
     * A bean class that is used for testing dependency resolving.
     */
    public static class BeanA
    {
        /** A counter for the number of created instances. */
        private static final AtomicInteger instances = new AtomicInteger();

        /** Stores a reference to bean B. */
        private BeanB refB;

        /** Stores a test object. */
        private ReflectionTestClass testData;

        public BeanA()
        {
            instanceCreated();
        }

        public BeanA(BeanB b)
        {
            this();
            refB = b;
        }

        public BeanB getRefB()
        {
            return refB;
        }

        public void setRefB(BeanB refB)
        {
            this.refB = refB;
        }

        public ReflectionTestClass getTestData()
        {
            return testData;
        }

        public void setTestData(ReflectionTestClass testData)
        {
            this.testData = testData;
        }

        /**
         * Returns the number of created instances.
         *
         * @return the number of created instances
         */
        public static int getInstanceCount()
        {
            return instances.get();
        }

        /**
         * Increments the instance counter.
         */
        private static void instanceCreated()
        {
            instances.incrementAndGet();
        }
    }

    /**
     * Another bean class that is used for testing dependency resolving. The
     * test bean classes have dependencies to each other.
     */
    public static class BeanB
    {
        /** A counter for the number of created instances. */
        private static final AtomicInteger instances = new AtomicInteger();

        /** Stores a reference to bean A. */
        private BeanA refA;

        public BeanB()
        {
            instanceCreated();
        }

        public BeanB(BeanA a)
        {
            this();
            refA = a;
        }

        public BeanA getRefA()
        {
            return refA;
        }

        public void setRefA(BeanA refA)
        {
            this.refA = refA;
        }

        /**
         * Returns the number of created instances.
         *
         * @return the number of created instances
         */
        public static int getInstanceCount()
        {
            return instances.get();
        }

        /**
         * Increments the instance counter.
         */
        private static void instanceCreated()
        {
            instances.incrementAndGet();
        }
    }

    /**
     * A thread class for executing tests that check concurrent access. The
     * thread is initialized with the name of a method to be executed.
     */
    class TestExecutorThread extends Thread
    {
        /** A latch for starting the threads synchronously.*/
        private final CountDownLatch startLatch;

        /** The name of the test method. */
        private final String methodName;

        /** The invocation helper for reflection operations. */
        private final InvocationHelper invocationHelper;

        /** Stores an exception if one occurred. */
        private volatile Exception exception;

        /**
         * Creates a new instance of {@code TestExecutorThread} and sets
         * the name of the test method.
         *
         * @param latch a latch for synchronizing the start of the threads
         * @param name the name of the method to invoke
         * @param invHlp the invocation helper object
         */
        public TestExecutorThread(CountDownLatch latch, String name,
                InvocationHelper invHlp)
        {
            startLatch = latch;
            methodName = name;
            invocationHelper = invHlp;
        }

        /**
         * Executes this thread. Calls the test method.
         */
        @Override
        public void run()
        {
            startLatch.countDown();

            try
            {
                startLatch.await();
                invocationHelper.invokeInstanceMethod(TestBeanProviders.this,
                        methodName, null, null);
            }
            catch (Exception ex)
            {
                exception = ex;
            }
        }
    }
}
