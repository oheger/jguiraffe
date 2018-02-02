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
package net.sf.jguiraffe.di.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.di.BeanInitializer;
import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.ConversionHelper;
import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.InjectionException;
import net.sf.jguiraffe.di.InvocationHelper;
import net.sf.jguiraffe.di.ReflectionUtils;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for DefaultDependencyProvider.
 *
 * @author Oliver Heger
 * @version $Id: TestDefaultDependencyProvider.java 211 2012-07-10 19:49:13Z oheger $
 */
public class TestDefaultDependencyProvider extends
        AbstractDependentProviderTest
{
    /** Constant for the name of the initial test provider. */
    private static final String INIT_PROVIDER_NAME = "InitialProvider";

    /** Constant for the class used for class loader tests. */
    private static final Class<?> TEST_CLASS = ReflectionUtils.class;

    /** Constant for a class name to be loaded. */
    private static final String CLS_NAME = TEST_CLASS.getName();

    /** Constant for a dummy lock ID. */
    private static final Long LOCK_ID = 42L;

    /** The initial dependency. */
    private Dependency initDependency;

    /** Stores the bean context that is used as class loader provider. */
    private DefaultBeanContextTestImpl context;

    /** Stores the object to be tested. */
    private DefaultDependencyProvider depProvider;

    @Override
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        initDependency = NameDependency.getInstance(INIT_PROVIDER_NAME);
        context = new DefaultBeanContextTestImpl();
        depProvider = new DefaultDependencyProvider(context);
    }

    /**
     * Tests the initialize method when the initial dependency cannot be
     * resolved.
     */
    @Test(expected = InjectionException.class)
    public void testInitializeNoInitDep()
    {
        depProvider.initialize(initDependency, store);
    }

    /**
     * Helper method for initializing the dependency provider if there are no
     * dependencies to other beans.
     */
    private void prepareInitializeWithoutDependencies()
    {
        store.addBeanProvider(INIT_PROVIDER_NAME, createProviderMock(true));
        replayProviders();
        assertTrue("Init not successful", depProvider.initialize(
                initDependency, store));
    }

    /**
     * Tests the initialize method if the initial dependency does not have any
     * further dependencies.
     */
    @Test
    public void testInitializeWithoutDeps()
    {
        prepareInitializeWithoutDependencies();
        assertEquals("Wrong number of dependencies", 1, depProvider
                .getDependencyMap().size());
    }

    /**
     * Tests the initialize method if only simple dependencies are involved.
     */
    @Test
    public void testInitializeSimpleDeps()
    {
        final int count = 5;
        String[] depNames = new String[count];
        for (int i = 0; i < count; i++)
        {
            int idx = addProvider(createProviderMock(true));
            depNames[i] = getProviderName(idx);
        }
        addProvider(createProviderMock());
        store.addBeanProvider(INIT_PROVIDER_NAME, createProviderMock(true,
                depNames));
        replayProviders();
        assertTrue("Init not successful", depProvider.initialize(
                initDependency, store));
        assertEquals("Wrong number of dependencies", count + 1, depProvider
                .getDependencyMap().size());
    }

    /**
     * Tests the initialize method with complex dependencies.
     */
    @Test
    public void testInitializeComplexDeps()
    {
        int idx1 = createProviderChain(5, false);
        int idx2 = createProviderChain(8, false);
        int idx3 = addProvider(createProviderMock(true,
                getProviderName(idx1 + 2)));
        int idx4 = addProvider(createProviderMock(true,
                getProviderName(idx2 + 2)));
        store.addBeanProvider(INIT_PROVIDER_NAME, createProviderMock(true,
                getProviderName(idx3), getProviderName(idx4)));
        replayProviders();
        assertTrue("Init not successful", depProvider.initialize(
                initDependency, store));
        assertEquals("Wrong number of dependencies", 12, depProvider
                .getDependencyMap().size());
    }

    /**
     * Tests initialization with cyclic dependencies.
     */
    @Test
    public void testInitializeCyclic()
    {
        int idx = createProviderChain(10, true);
        for (int i = 0; i < 20; i++)
        {
            addProvider(createProviderMock());
        }
        store.addBeanProvider(INIT_PROVIDER_NAME, createProviderMock(true,
                getProviderName(idx), getProviderName(idx + 4)));
        replayProviders();
        assertTrue("Init not successful", depProvider.initialize(
                initDependency, store));
        assertEquals("Wrong number of dependencies", 11, depProvider
                .getDependencyMap().size());
    }

    /**
     * Tests the initialize() method when the initial dependency is already
     * locked.
     */
    @Test
    public void testInitializeLockInitDep()
    {
        BeanProvider p = createProviderMock();
        EasyMock.expect(p.getLockID()).andReturn(LOCK_ID);
        store.addBeanProvider(INIT_PROVIDER_NAME, p);
        replayProviders();
        assertFalse("Lock not detected", depProvider.initialize(initDependency,
                store));
        assertNull("Depenency map not null", depProvider.getDependencyMap());
    }

    /**
     * Tests the initialize() method when one of the dependencies is locked.
     */
    @Test
    public void testInitializeLock()
    {
        int idx = createProviderChain(10, false);
        BeanProvider p = createProviderMock();
        EasyMock.expect(p.getLockID()).andReturn(LOCK_ID);
        int idx2 = addProvider(p);
        store.addBeanProvider(INIT_PROVIDER_NAME, createProviderMock(true,
                getProviderName(idx), getProviderName(idx2)));
        replayProviders();
        assertFalse("Lock not detected", depProvider.initialize(initDependency,
                store));
        assertNull("Depenency map not null", depProvider.getDependencyMap());
    }

    /**
     * Tests initialization when a dependency cannot be resolved.
     */
    @Test(expected = InjectionException.class)
    public void testInitializeUnresolved()
    {
        int idx1 = createProviderChain(10, true);
        int idx2 = addProvider(createProviderMock(true, "unknownDependency"));
        store.addBeanProvider(INIT_PROVIDER_NAME, createProviderMock(true,
                getProviderName(idx1), getProviderName(idx2)));
        replayProviders();
        depProvider.initialize(initDependency, store);
    }

    /**
     * Helper method for creating some providers with dependencies. There will
     * also be some other providers that are no dependencies. The initial
     * dependency will have the index &lt;return value&gt; + count.
     *
     * @param count the number of dependent providers to create
     * @return the start index of the first provider
     */
    private int setUpProviders(int count)
    {
        createProviderChain(10, false);
        String[] depNames = new String[count];
        int index = 0;
        for (int i = 0; i < count; i++)
        {
            BeanProvider p = createProviderMock(true);
            int idx = addProvider(p);
            if (i == 0)
            {
                index = idx;
            }
            depNames[i] = getProviderName(idx);
        }
        BeanProvider initProvider = createProviderMock(true, depNames);
        store.addBeanProvider(INIT_PROVIDER_NAME, initProvider);
        return index;
    }

    /**
     * Tests the lock method.
     */
    @Test
    public void testLock()
    {
        final int count = 8;
        int idx = setUpProviders(count);
        for (BeanProvider p : getProviders(idx, idx + count - 1))
        {
            p.setLockID(LOCK_ID);
        }
        store.getBeanProvider(INIT_PROVIDER_NAME).setLockID(LOCK_ID);
        replayProviders();
        assertTrue("Init not successful", depProvider.initialize(
                initDependency, store));
        depProvider.lock(LOCK_ID);
    }

    /**
     * Tests querying dependent providers.
     */
    @Test
    public void testGetDependentProvider()
    {
        final int count = 12;
        int idx = setUpProviders(count);
        replayProviders();
        assertTrue("Init not successful", depProvider.initialize(
                initDependency, store));
        for (int i = 0; i < count; i++)
        {
            assertNotNull("Bean provider not found " + i, depProvider
                    .getDependentProvider(NameDependency
                            .getInstance(getProviderName(idx + i))));
        }
        assertNotNull("Init provider not found", depProvider
                .getDependentProvider(NameDependency
                        .getInstance(INIT_PROVIDER_NAME)));
    }

    /**
     * Tries to query an unknown dependent provider. This should cause an
     * exception.
     */
    @Test(expected = InjectionException.class)
    public void testGetDependentProviderUnknown()
    {
        final int count = 12;
        setUpProviders(count);
        int idx = addProvider(createProviderMock());
        replayProviders();
        assertTrue("Init not successful", depProvider.initialize(
                initDependency, store));
        depProvider.getDependentProvider(NameDependency
                .getInstance(getProviderName(idx)));
    }

    /**
     * Tests querying a dependent bean.
     */
    @Test
    public void testGetDependentBean()
    {
        final String beanPrefix = "Bean";
        final int count = 12;
        int idx = setUpProviders(count);
        int i = 0;
        for (BeanProvider p : getProviders(idx, idx + count - 1))
        {
            EasyMock.expect(p.getBean(depProvider)).andReturn(beanPrefix + i);
            i++;
        }
        EasyMock.expect(
                store.getBeanProvider(INIT_PROVIDER_NAME).getBean(depProvider))
                .andReturn(beanPrefix);
        replayProviders();
        assertTrue("Init not successful", depProvider.initialize(
                initDependency, store));
        for (i = 0; i < count; i++)
        {
            assertEquals("Wrong bean returned " + i, beanPrefix + i,
                    depProvider.getDependentBean(NameDependency
                            .getInstance(getProviderName(idx + i))));
        }
        assertEquals("Wrong bean for init provider", beanPrefix, depProvider
                .getDependentBean(NameDependency
                        .getInstance(INIT_PROVIDER_NAME)));
    }

    /**
     * Tests querying an unknown bean. This should cause an exception.
     */
    @Test(expected = InjectionException.class)
    public void testGetDependentBeanUnknown()
    {
        final int count = 12;
        setUpProviders(count);
        int idx = addProvider(createProviderMock());
        replayProviders();
        assertTrue("Init not successful", depProvider.initialize(
                initDependency, store));
        depProvider.getDependentBean(NameDependency
                .getInstance(getProviderName(idx)));
    }

    /**
     * Tests loading a class using the default class loader.
     */
    @Test
    public void testLoadClassDefault()
    {
        assertEquals("Class could not be loaded with default loader",
                TEST_CLASS, depProvider.loadClass(CLS_NAME, null));
    }

    /**
     * Tests loading a class using the context class loader.
     */
    @Test
    public void testLoadClassContextCL()
    {
        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader(
                    getClass().getClassLoader());
            assertEquals("Class could not be loaded with CCL", TEST_CLASS,
                    depProvider.loadClass(CLS_NAME,
                            DependencyProvider.CONTEXT_CLASS_LOADER));
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(ccl);
        }
    }

    /**
     * Tests loading a class using a registered class loader.
     */
    @Test
    public void testLoadClassRegistered()
    {
        final String myCL = "myClassLoader";
        context.getClassLoaderProvider().registerClassLoader(myCL, getClass().getClassLoader());
        assertEquals("Class could not be loaded with registered loader",
                TEST_CLASS, depProvider.loadClass(CLS_NAME, myCL));
    }

    /**
     * Tries loading an unknown class. Exceptions should be re-thrown as
     * InjectionExceptions.
     */
    @Test(expected = InjectionException.class)
    public void testLoadClassUnknown()
    {
        depProvider.loadClass("an.Unknown.Class", null);
    }

    /**
     * Tests loading a class with a class loader that is not registered. This
     * should also cause an exception.
     */
    @Test(expected = InjectionException.class)
    public void testLoadClassNonRegisteredLoader()
    {
        depProvider.loadClass("java.lang.Integer", "unregistered class loader!");
    }

    /**
     * Tests registering a new class loader under a symbolic name.
     */
    @Test
    public void testRegisterClassLoader()
    {
        final String name = "myClassLoader";
        assertTrue("Already class loaders registered", depProvider
                .classLoaderNames().isEmpty());
        depProvider.registerClassLoader(name, getClass().getClassLoader());
        assertEquals("Wrong number of registered class loaders", 1, depProvider
                .classLoaderNames().size());
        assertTrue("Name not found", depProvider.classLoaderNames().contains(
                name));
        assertEquals("Wrong class loader returned",
                getClass().getClassLoader(), depProvider.getClassLoader(name));
    }

    /**
     * Tests whether a default class loader name can be set.
     */
    @Test
    public void testSetDefaultClassLoader()
    {
        final String clName = "New default class loader name!";
        depProvider.setDefaultClassLoaderName(clName);
        assertEquals("New default class loader name was not set", clName,
                depProvider.getDefaultClassLoaderName());
        assertSame("Not set in context", clName, context
                .getClassLoaderProvider().getDefaultClassLoaderName());
    }

    /**
     * Tests obtaining the default class loader name.
     */
    @Test
    public void testGetDefaultClassLoader()
    {
        assertEquals("Wrong default class loader name", context
                .getClassLoaderProvider().getDefaultClassLoaderName(),
                depProvider.getDefaultClassLoaderName());
    }

    /**
     * Tests the isBeanAvailable() method.
     */
    @Test
    public void testIsBeanAvailable()
    {
        setUpProviders(1);
        BeanProvider p = store.getBeanProvider(INIT_PROVIDER_NAME);
        EasyMock.expect(p.isBeanAvailable()).andReturn(Boolean.TRUE);
        replayProviders();
        assertTrue("Init not successful", depProvider.initialize(
                initDependency, store));
        assertTrue("Init dependency not available", depProvider
                .isBeanAvailable(initDependency));
    }

    /**
     * Creates a bean initializer mock that expects to be invoked.
     *
     * @return the initialized mock object
     */
    private BeanInitializer setUpInitializerMock()
    {
        BeanInitializer mock = EasyMock.createMock(BeanInitializer.class);
        mock.initialize(depProvider);
        depProvider.addInitializer(mock);
        return mock;
    }

    /**
     * Helper method for creating a number of initializer mocks.
     *
     * @param count the number of mocks to create
     * @param initializers the collection to the store the objects
     */
    private void setUpInitializerMocks(int count,
            Collection<BeanInitializer> initializers)
    {
        for (int i = 0; i < count; i++)
        {
            initializers.add(setUpInitializerMock());
        }
    }

    /**
     * Tests dealing with bean initializers. This test adds some initializers
     * and tests whether they can be invoked.
     */
    @Test
    public void testAddInitializer()
    {
        BeanInitializer mockInit = setUpInitializerMock();
        EasyMock.replay(mockInit);
        depProvider.invokeInitializers();
        EasyMock.verify(mockInit);
    }

    /**
     * Tests the invokeInitializers() method when an initializer throws an
     * exception.
     */
    @Test
    public void testInvokeInitializersWithRuntimeException()
    {
        RuntimeException rex = new RuntimeException("My test exception");
        InjectionException iex = checkInvokeInitializersWithException(rex);
        assertEquals("Wrong root cause for exception", rex, iex.getCause());
    }

    /**
     * Tests invoking the initializers when an InjectionException is thrown.
     */
    @Test
    public void testInvokeInitializersWithInjectionException()
    {
        InjectionException iex = new InjectionException();
        assertSame("Wrong exception thrown", iex,
                checkInvokeInitializersWithException(iex));
    }

    /**
     * Checks invoking the registered initializers when one of them throws an
     * exception. The caught exception is returned.
     *
     * @param ex the exception to throw
     * @return the caught exception
     */
    private InjectionException checkInvokeInitializersWithException(Throwable ex)
    {
        final int count = 5;
        Collection<BeanInitializer> initializers = new ArrayList<BeanInitializer>(
                2 * count + 1);
        setUpInitializerMocks(count, initializers);
        BeanInitializer initEx = setUpInitializerMock();
        EasyMock.expectLastCall().andThrow(ex);
        initializers.add(initEx);
        depProvider.addInitializer(null);
        setUpInitializerMocks(count, initializers);
        EasyMock.replay(initializers.toArray());
        InjectionException result = null;
        try
        {
            depProvider.invokeInitializers();
            fail("No exception was thrown!");
        }
        catch (InjectionException iex)
        {
            result = iex;
        }
        return result;
    }

    /**
     * Tests the notification for newly created beans.
     */
    @Test
    public void testBeanCreated()
    {
        BeanProvider bp = EasyMock.createMock(BeanProvider.class);
        EasyMock.replay(bp);
        final Object bean = new Object();
        depProvider.beanCreated(bean, bp);
        EasyMock.verify(bp);
        assertEquals("Wrong created bean", bean, context.createdBean);
        assertEquals("Wrong bean provider", bp, context.createdBeanProvider);
        assertEquals("Wrong dependency provider", depProvider,
                context.createdDepProvider);
    }

    /**
     * Tests whether a creation bean context can be set.
     */
    @Test
    public void testSetCreationBeanContext()
    {
        BeanContext ctx = EasyMock.createMock(BeanContext.class);
        EasyMock.replay(ctx);
        depProvider.setCreationBeanContext(ctx);
        assertEquals("Wrong creation context", ctx, depProvider
                .getCreationBeanContext());
        EasyMock.verify(ctx);
    }

    /**
     * Tests querying the bean creation context if it was not initialized. In
     * this case the associated context should be returned.
     */
    @Test
    public void testSetCreationBeanContextNotInit()
    {
        assertEquals("Wrong default creation context", context, depProvider
                .getCreationBeanContext());
    }

    /**
     * Tests whether an invocation helper can be requested if there is no
     * specific conversion helper.
     */
    @Test
    public void testGetInvocationHelperStandardConversionHelper()
    {
        prepareInitializeWithoutDependencies();
        InvocationHelper invHlp = depProvider.getInvocationHelper();
        assertNotNull("No invocation helper", invHlp);
    }

    /**
     * Tests whether a custom conversion helper is passed to the invocation
     * helper.
     */
    @Test
    public void testGetInvocationHelperCustomConversionHelper()
    {
        ConversionHelper ch = new ConversionHelper();
        store.setConversionHelper(ch);
        prepareInitializeWithoutDependencies();
        assertSame("Wrong conversion helper", ch, depProvider
                .getInvocationHelper().getConversionHelper());
    }

    /**
     * A test implementation of DefaultBeanContext used for testing specific
     * method invocations by the dependency provider.
     */
    private static class DefaultBeanContextTestImpl extends DefaultBeanContext
    {
        /** The bean passed to beanCreated(). */
        Object createdBean;

        /** The bean provider passed to beanCreated(). */
        BeanProvider createdBeanProvider;

        /** The dependency provider passed to beanCreated(). */
        DefaultDependencyProvider createdDepProvider;

        /**
         * Records this invocation.
         */
        @Override
        void beanCreated(Object bean, BeanProvider beanProvider,
                DefaultDependencyProvider depProvider)
        {
            createdBean = bean;
            createdBeanProvider = beanProvider;
            createdDepProvider = depProvider;
        }
    }
}
