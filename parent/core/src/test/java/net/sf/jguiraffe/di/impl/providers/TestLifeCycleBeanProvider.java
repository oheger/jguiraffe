/*
 * Copyright 2006-2025 The JGUIraffe Team.
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.di.BeanInitializer;
import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.InjectionException;
import net.sf.jguiraffe.di.InvocationHelper;
import net.sf.jguiraffe.di.ReflectionTestClass;
import net.sf.jguiraffe.di.impl.Invokable;
import net.sf.jguiraffe.di.impl.MethodInvocation;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for LifeCycleBeanProvider. The basic functionality of this
 * abstract base class is tested.
 *
 * @author Oliver Heger
 * @version $Id: TestLifeCycleBeanProvider.java 207 2012-02-09 07:30:13Z oheger $
 */
public class TestLifeCycleBeanProvider
{
    /** Constant for the bean returned by the tested provider. */
    private static final Object TEST_BEAN = new Object();

    /** Constant for the name of a test class loader. */
    private static final String CL_NAME = "myClassLoader";

    /** Stores all mock objects created for a test. */
    private Collection<Object> mocks;

    /** Stores the provider to be tested. */
    private LifeCycleBeanProviderTestImpl provider;

    /**
     * Creates a mock object of the specified class. The mock is also added to
     * an internal list. The <code>verify()</code> method verifies all mocks.
     *
     * @param mockCls the class of the mock object
     * @return the mock
     */
    protected <T> T createMock(Class<T> mockCls)
    {
        T mock = EasyMock.createMock(mockCls);
        if (mocks == null)
        {
            mocks = new LinkedList<Object>();
        }
        mocks.add(mock);
        return mock;
    }

    /**
     * Verifies all mock objects that have been created using the
     * <code>createMock()</code> method.
     */
    protected void verify()
    {
        EasyMock.verify(mocks.toArray());
    }

    /**
     * Tests creating a new instance.
     */
    @Test
    public void testInit()
    {
        BeanProvider mockProvider = createMock(BeanProvider.class);
        Invokable mockInv = createMock(Invokable.class);
        EasyMock.replay(mockProvider, mockInv);
        LifeCycleBeanProviderTestImpl provider = new LifeCycleBeanProviderTestImpl(
                mockProvider, mockInv);
        assertEquals("Wrong creator set", mockProvider, provider
                .getBeanCreator());
        assertEquals("Wrong initializer set", mockInv, provider
                .getBeanInitializer());
        verify();
    }

    /**
     * Tests the constructor that only takes a creator provider.
     */
    @Test
    public void testInitProviderOnly()
    {
        BeanProvider mockProvider = createMock(BeanProvider.class);
        EasyMock.replay(mockProvider);
        LifeCycleBeanProviderTestImpl provider = new LifeCycleBeanProviderTestImpl(
                mockProvider);
        assertEquals("Wrong creator set", mockProvider, provider
                .getBeanCreator());
        assertNotNull("No invocation is set", provider.getBeanInitializer());
        verify();
    }

    /**
     * Tests the dependencies returned by the default bean initializer.
     */
    @Test
    public void testDefaultBeanInitializerGetParameterDependencies()
    {
        provider = new LifeCycleBeanProviderTestImpl(EasyMock
                .createNiceMock(BeanProvider.class));
        assertTrue("Initializer has dependencies", provider
                .getBeanInitializer().getParameterDependencies().isEmpty());
    }

    /**
     * Tests invoking the default bean initializer. This should be a null
     * operation. We can only test that the objects passed to this method are
     * not touched.
     */
    @Test
    public void testDefaultBeanInitializerInvoke()
    {
        provider =
                new LifeCycleBeanProviderTestImpl(
                        EasyMock.createNiceMock(BeanProvider.class));
        Invokable initializer = provider.getBeanInitializer();
        DependencyProvider depProvider = createMock(DependencyProvider.class);
        EasyMock.replay(depProvider);
        assertSame("Wrong result of invocation", TEST_BEAN,
                initializer.invoke(depProvider, TEST_BEAN));
        verify();
    }

    /**
     * Tries to create a provider with a null creator. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNullProvider()
    {
        new LifeCycleBeanProviderTestImpl(null);
    }

    /**
     * Tests manipulating the lock ID.
     */
    @Test
    public void testLockID()
    {
        BeanProvider mockProvider = createMock(BeanProvider.class);
        EasyMock.replay(mockProvider);
        final Long lockID = 42L;
        LifeCycleBeanProviderTestImpl provider = new LifeCycleBeanProviderTestImpl(
                mockProvider);
        assertNull("Wrong lock ID after init", provider.getLockID());
        provider.setLockID(lockID);
        assertEquals("Lock ID was not set", lockID, provider.getLockID());
        verify();
    }

    /**
     * Helper method for creating a list with dependency mock objects.
     *
     * @param count the number of dependencies to create
     * @return the list with the initialized dependency mocks
     */
    private List<Dependency> setUpDependencyList(int count)
    {
        List<Dependency> lst = new ArrayList<Dependency>(count);
        for (int i = 0; i < count; i++)
        {
            Dependency mock = createMock(Dependency.class);
            EasyMock.replay(mock);
            lst.add(mock);
        }
        return lst;
    }

    /**
     * Tests querying the dependencies of the bean provider.
     */
    @Test
    public void testGetDependencies()
    {
        BeanProvider mockCreator = createMock(BeanProvider.class);
        Invokable mockInit = createMock(Invokable.class);
        List<Dependency> lstDep1 = setUpDependencyList(5);
        List<Dependency> lstDep2 = setUpDependencyList(7);
        Set<Dependency> setDep = new HashSet<Dependency>(lstDep1);
        setDep.add(lstDep2.get(0));
        EasyMock.expect(mockCreator.getDependencies()).andReturn(setDep);
        EasyMock.expect(mockInit.getParameterDependencies()).andReturn(lstDep2);
        EasyMock.replay(mockCreator, mockInit);
        LifeCycleBeanProviderTestImpl provider = new LifeCycleBeanProviderTestImpl(
                mockCreator, mockInit);
        Set<Dependency> setProviderDeps = provider.getDependencies();
        assertEquals("Wrong number of dependencies", lstDep1.size()
                + lstDep2.size(), setProviderDeps.size());
        assertTrue("Not all dependencies of creator found", setProviderDeps
                .containsAll(lstDep1));
        assertTrue("Not all dependencies of initializer found", setProviderDeps
                .containsAll(lstDep2));
        verify();
    }

    /**
     * Tests querying the dependencies when only a creator is initialized.
     */
    @Test
    public void testGetDependenciesCreatorOnly()
    {
        BeanProvider mockCreator = createMock(BeanProvider.class);
        Set<Dependency> setDep = new HashSet<Dependency>(
                setUpDependencyList(11));
        EasyMock.expect(mockCreator.getDependencies()).andReturn(setDep);
        EasyMock.replay(mockCreator);
        LifeCycleBeanProviderTestImpl provider = new LifeCycleBeanProviderTestImpl(
                mockCreator);
        assertSame("Wrong dependencies returned", setDep, provider
                .getDependencies());
        verify();
    }

    /**
     * Tests whether a null collection of dependencies returned by the creator
     * is handled correctly.
     */
    @Test
    public void testGetDependenciesNullFromCreator()
    {
        BeanProvider mockCreator = createMock(BeanProvider.class);
        Invokable mockInit = createMock(Invokable.class);
        List<Dependency> lstDeps = setUpDependencyList(5);
        EasyMock.expect(mockInit.getParameterDependencies()).andReturn(lstDeps);
        EasyMock.expect(mockCreator.getDependencies()).andReturn(null);
        EasyMock.replay(mockCreator, mockInit);
        LifeCycleBeanProviderTestImpl provider =
                new LifeCycleBeanProviderTestImpl(mockCreator, mockInit);
        Set<Dependency> setProviderDeps = provider.getDependencies();
        assertEquals("Wrong number of dependencies", lstDeps.size(),
                setProviderDeps.size());
        assertTrue("Wrong dependencies", setProviderDeps.containsAll(lstDeps));
        verify();
    }

    /**
     * Tests querying the bean class of the provider.
     */
    @Test
    public void testGetBeanClass()
    {
        DependencyProvider depProvider = createMock(DependencyProvider.class);
        BeanProvider mockCreator = createMock(BeanProvider.class);
        mockCreator.getBeanClass(depProvider);
        EasyMock.expectLastCall().andReturn(ReflectionTestClass.class);
        EasyMock.replay(depProvider, mockCreator);
        LifeCycleBeanProviderTestImpl provider = new LifeCycleBeanProviderTestImpl(
                mockCreator);
        assertEquals("Wrong bean class", ReflectionTestClass.class, provider
                .getBeanClass(depProvider));
        verify();
    }

    /**
     * Tests creating a bean instance in the most simple case. All required
     * dependencies are available.
     */
    @Test
    public void testCreateBeanAllAvailable()
    {
        DependencyProvider dependencyProvider = createMock(DependencyProvider.class);
        BeanProvider mockCreator = createMock(BeanProvider.class);
        Dependency mockDependency = createMock(Dependency.class);
        Invokable mockInit = createMock(Invokable.class);
        final Object bean = new Object();
        final Object beanInit = new Object();
        List<Dependency> deps = new ArrayList<Dependency>(1);
        deps.add(mockDependency);
        EasyMock.expect(
                mockCreator.getBean((DependencyProvider) EasyMock.anyObject()))
                .andReturn(bean);
        EasyMock.expect(mockInit.getParameterDependencies()).andReturn(deps);
        EasyMock.expect(dependencyProvider.isBeanAvailable(mockDependency))
                .andReturn(Boolean.TRUE);
        EasyMock.expect(mockInit.invoke(dependencyProvider, bean)).andReturn(
                beanInit);
        LifeCycleBeanProviderTestImpl provider = new LifeCycleBeanProviderTestImpl(
                mockCreator, mockInit);
        dependencyProvider.beanCreated(beanInit, provider);
        EasyMock.replay(dependencyProvider, mockCreator, mockInit,
                mockDependency);
        assertSame("Wrong bean returned", beanInit, provider
                .createBean(dependencyProvider));
        verify();
    }

    /**
     * Performs a createBean() test when some of the required dependencies are
     * unavailable.
     */
    private void performCreateBeanUnavailableDepTest()
    {
        DependencyProvider dependencyProvider = createMock(DependencyProvider.class);
        BeanProvider mockCreator = createMock(BeanProvider.class);
        Dependency mockDep1 = createMock(Dependency.class);
        Dependency mockDep2 = createMock(Dependency.class);
        Invokable mockInit = createMock(Invokable.class);
        provider = new LifeCycleBeanProviderTestImpl(mockCreator, mockInit);
        List<Dependency> deps = new ArrayList<Dependency>(2);
        deps.add(mockDep1);
        deps.add(mockDep2);
        EasyMock.expect(
                mockCreator.getBean((DependencyProvider) EasyMock.anyObject()))
                .andReturn(TEST_BEAN);
        EasyMock.expect(mockInit.getParameterDependencies()).andReturn(deps);
        EasyMock.expect(dependencyProvider.isBeanAvailable(mockDep1))
                .andReturn(Boolean.TRUE);
        EasyMock.expect(dependencyProvider.isBeanAvailable(mockDep2))
                .andReturn(Boolean.FALSE);
        dependencyProvider.addInitializer(provider);
        EasyMock.replay(dependencyProvider, mockCreator, mockInit, mockDep1,
                mockDep2);
        assertSame("Wrong bean returned", TEST_BEAN, provider
                .createBean(dependencyProvider));
        verify();
    }

    /**
     * Tests creating a bean instance when some of the dependencies required for
     * initialization are not available. In this case initialization has to be
     * postponed.
     */
    @Test
    public void testCreateBeanSomeDepsNotAvailable()
    {
        performCreateBeanUnavailableDepTest();
        verify();
    }

    /**
     * Tests a reentrant call to createBean() when some of the dependencies are
     * unavailable.
     */
    @Test
    public void testCreateBeanSomeDepsNotAvailableReentrant()
    {
        DependencyProvider dependencyProvider = createMock(DependencyProvider.class);
        EasyMock.replay(dependencyProvider);
        performCreateBeanUnavailableDepTest();
        assertSame("Wrong bean returned", TEST_BEAN, provider
                .createBean(dependencyProvider));
        verify();
    }

    /**
     * A helper method for performing a test of the fetchBean() operation.
     */
    private void performFetchBeanTest()
    {
        DependencyProvider dependencyProvider = createMock(DependencyProvider.class);
        BeanProvider mockCreator = createMock(BeanProvider.class);
        provider = new LifeCycleBeanProviderTestImpl(mockCreator);
        EasyMock.expect(
                mockCreator.getBean((DependencyProvider) EasyMock.anyObject()))
                .andReturn(TEST_BEAN);
        dependencyProvider.beanCreated(TEST_BEAN, provider);
        EasyMock.replay(dependencyProvider, mockCreator);
        assertSame("Wrong bean returned", TEST_BEAN, provider
                .fetchBean(dependencyProvider));
    }

    /**
     * Tests the fetchBean() method in the most basic case.
     */
    @Test
    public void testFetchBean()
    {
        performFetchBeanTest();
        verify();
    }

    /**
     * Tests whether a 2nd call to fetchBean() returns the same bean.
     */
    @Test
    public void testFetchBeanCached()
    {
        DependencyProvider dependencyProvider = createMock(DependencyProvider.class);
        EasyMock.replay(dependencyProvider);
        performFetchBeanTest();
        assertSame("Bean is not cached", TEST_BEAN, provider
                .fetchBean(dependencyProvider));
        verify();
    }

    /**
     * Tests the hasBean() method for a new bean provider.
     */
    @Test
    public void testHasBean()
    {
        BeanProvider mockCreator = createMock(BeanProvider.class);
        EasyMock.replay(mockCreator);
        provider = new LifeCycleBeanProviderTestImpl(mockCreator);
        assertFalse("Provider has already a bean", provider.hasBean());
        verify();
    }

    /**
     * Tests the hasBean() method after a bean was fetched.
     */
    @Test
    public void testHasBeanAfterFetch()
    {
        performFetchBeanTest();
        assertTrue("Provider does not have a bean", provider.hasBean());
        verify();
    }

    /**
     * Tests the hasBean() method after successful creation, but postponed
     * initialization.
     */
    @Test
    public void testHasBeanDependenciesNotAvailable()
    {
        performCreateBeanUnavailableDepTest();
        assertFalse("Provider already has a bean", provider.hasBean());
        verify();
    }

    /**
     * Tests reseting a bean provider.
     */
    @Test
    public void testResetBean()
    {
        performFetchBeanTest();
        provider.resetBean();
        assertFalse("Bean still present", provider.hasBean());
        verify();
    }

    /**
     * Tests whether a reentrant call to createBean() is possible.
     */
    @Test
    public void testCreateBeanReentrant()
    {
        DependencyProvider depProvider = createMock(DependencyProvider.class);
        BeanProvider mockCreator = createMock(BeanProvider.class);
        EasyMock.expect(
                mockCreator.getBean((DependencyProvider) EasyMock.anyObject()))
                .andReturn(TEST_BEAN);
        Invokable inv = new Invokable()
        {
            public List<Dependency> getParameterDependencies()
            {
                return null;
            }

            public Object invoke(DependencyProvider depProvider, Object target)
            {
                // performs the reentrant call
                Object bean = provider.fetchBean(depProvider);
                assertSame("Wrong bean returned by reentrant call", TEST_BEAN,
                        bean);
                return bean;
            }

        };
        provider = new LifeCycleBeanProviderTestImpl(mockCreator, inv);
        depProvider.beanCreated(TEST_BEAN, provider);
        EasyMock.replay(depProvider, mockCreator);
        assertSame("Wrong bean returned by fetchBean()", TEST_BEAN, provider
                .fetchBean(depProvider));
        verify();
    }

    /**
     * Tests an unresolvable reentrant call to fetchBean()/createBean(). This
     * should cause an exception.
     */
    @Test
    public void testCreateBeanReentrantUnresolvable()
    {
        DependencyProvider depProvider = createMock(DependencyProvider.class);
        EasyMock.replay(depProvider);
        BeanProvider reentrantCreator = new SimpleBeanProvider()
        {
            public Object getBean(DependencyProvider dependencyProvider)
            {
                // Contains the reentrant call
                return provider.fetchBean(dependencyProvider);
            }

            public Class<?> getBeanClass(DependencyProvider dependencyProvider)
            {
                return TEST_BEAN.getClass();
            }
        };
        provider = new LifeCycleBeanProviderTestImpl(reentrantCreator);
        try
        {
            provider.fetchBean(depProvider);
            fail("Unresolvable dependency not detected!");
        }
        catch (InjectionException iex)
        {
            // ok
        }
        verify();
    }

    /**
     * Tests whether the managed bean is available for a newly created provider.
     */
    @Test
    public void testIsBeanAvailableInit()
    {
        provider = new LifeCycleBeanProviderTestImpl(ConstantBeanProvider
                .getInstance(TEST_BEAN));
        assertTrue("Bean not available after init", provider.isBeanAvailable());
    }

    /**
     * Tests whether the managed bean is available after a successful creation.
     */
    @Test
    public void testIsBeanAvailableCreated()
    {
        performFetchBeanTest();
        assertTrue("Bean not available after creating an instance", provider
                .isBeanAvailable());
    }

    /**
     * Tests whether the managed is available while it is created. This should
     * not be the case.
     */
    @Test
    public void testIsBeanAvailableReentrant()
    {
        DependencyProvider depProvider = createMock(DependencyProvider.class);
        BeanProvider reentrantChecker = new SimpleBeanProvider()
        {
            public Object getBean(DependencyProvider dependencyProvider)
            {
                // performs the check while the bean is created
                assertFalse("Bean available during creation", provider
                        .isBeanAvailable());
                return TEST_BEAN;
            }

            public Class<?> getBeanClass(DependencyProvider dependencyProvider)
            {
                return TEST_BEAN.getClass();
            }
        };
        provider = new LifeCycleBeanProviderTestImpl(reentrantChecker);
        depProvider.beanCreated(TEST_BEAN, provider);
        EasyMock.replay(depProvider);
        assertEquals("Wrong bean returned", TEST_BEAN, provider
                .fetchBean(depProvider));
        verify();
    }

    /**
     * Tests the toString() implementation. We test whether the creator and the
     * initializer can be found in the returned string.
     */
    @Test
    public void testToString()
    {
        BeanProvider creator = ConstantBeanProvider
                .getInstance(new ReflectionTestClass());
        Invokable initializer = new MethodInvocation("initialize", null,
                ConstantBeanProvider.getInstance("a string"),
                ConstantBeanProvider.getInstance(42));
        provider = new LifeCycleBeanProviderTestImpl(creator, initializer);
        String s = provider.toString();
        assertTrue("Creator not found in string " + s, s.indexOf("creator = "
                + creator) >= 0);
        assertTrue("Initializer not found in string " + s, s
                .indexOf("initializer = " + initializer) >= 0);
    }

    /**
     * Tests the string representation when no initializer is provided.
     */
    @Test
    public void testToStringCreatorOnly()
    {
        BeanProvider creator = ConstantBeanProvider.getInstance(TEST_BEAN);
        provider = new LifeCycleBeanProviderTestImpl(creator);
        String s = provider.toString();
        assertTrue("Wrong initializer contained in string " + s, s
                .indexOf("initializer = IDENTITY_INVOCATION") > 0);
    }

    /**
     * Helper method for obtaining a diagnostic dependency provider.
     *
     * @param wrappedProvider the wrapped dependency provider
     * @return the dependency provider
     */
    private DependencyProvider setUpDiagnosticDepProvider(
            DependencyProvider wrappedProvider)
    {
        BeanProvider creator = createMock(BeanProvider.class);
        EasyMock.replay(creator);
        return new LifeCycleBeanProviderTestImpl(creator)
                .createDiagnosticDependencyProvider(wrappedProvider);
    }

    /**
     * Tests the loadClass() implementation of the diagnostic bean provider.
     */
    @Test
    public void testDiagnosticDependencyProviderLoadClass()
    {
        DependencyProvider depProvider = createMock(DependencyProvider.class);
        depProvider.loadClass(getClass().getName(), null);
        EasyMock.expectLastCall().andReturn(getClass());
        EasyMock.replay(depProvider);
        DependencyProvider diagDepProvider = setUpDiagnosticDepProvider(depProvider);
        assertEquals("Wrong class returned", getClass(), diagDepProvider
                .loadClass(getClass().getName(), null));
        verify();
    }

    /**
     * Tests the isBeanAvailable() implementation of the diagnostic bean
     * provider.
     */
    @Test
    public void testDiagnosticDependencyProviderIsBeanAvailable()
    {
        DependencyProvider wrappedProvider = createMock(DependencyProvider.class);
        Dependency dep = createMock(Dependency.class);
        EasyMock.expect(wrappedProvider.isBeanAvailable(dep)).andReturn(
                Boolean.TRUE);
        EasyMock.replay(wrappedProvider, dep);
        DependencyProvider diagDepProvider = setUpDiagnosticDepProvider(wrappedProvider);
        assertTrue("Bean not available", diagDepProvider.isBeanAvailable(dep));
        verify();
    }

    /**
     * Tests the addInitializer() implementation of the diagnostic bean
     * provider.
     */
    @Test
    public void testDiagnosticDependencyProviderAddInitializer()
    {
        DependencyProvider wrappedProvider = createMock(DependencyProvider.class);
        BeanInitializer init = createMock(BeanInitializer.class);
        wrappedProvider.addInitializer(init);
        EasyMock.replay(wrappedProvider, init);
        DependencyProvider diagDepProvider = setUpDiagnosticDepProvider(wrappedProvider);
        diagDepProvider.addInitializer(init);
        verify();
    }

    /**
     * Tests the beanCreated() implementation of the diagnostic bean provider.
     */
    @Test
    public void testDiagnosticDependencyProviderBeanCreated()
    {
        DependencyProvider wrappedProvider = createMock(DependencyProvider.class);
        BeanProvider beanProvider = createMock(BeanProvider.class);
        final Object bean = new Object();
        wrappedProvider.beanCreated(bean, beanProvider);
        EasyMock.replay(wrappedProvider, beanProvider);
        DependencyProvider diagDepProvider = setUpDiagnosticDepProvider(wrappedProvider);
        diagDepProvider.beanCreated(bean, beanProvider);
        verify();
    }

    /**
     * Tests the setCreationBeanContext() of the diagnostic bean provider.
     */
    @Test
    public void testDiagnosticDependencyProviderSetCreationBeanContext()
    {
        DependencyProvider wrappedProvider = createMock(DependencyProvider.class);
        BeanContext context = createMock(BeanContext.class);
        wrappedProvider.setCreationBeanContext(context);
        EasyMock.replay(wrappedProvider, context);
        DependencyProvider diagDepProvider = setUpDiagnosticDepProvider(wrappedProvider);
        diagDepProvider.setCreationBeanContext(context);
        verify();
    }

    /**
     * Tests the classLoaderNames() implementation of the diagnostic bean
     * provider.
     */
    @Test
    public void testDiagnosticDependencyProviderClassLoaderNames()
    {
        DependencyProvider wrappedProvider = createMock(DependencyProvider.class);
        Set<String> names = new HashSet<String>();
        names.add("test1");
        names.add("test2");
        Set<String> refNames = new HashSet<String>(names);
        EasyMock.expect(wrappedProvider.classLoaderNames()).andReturn(names);
        EasyMock.replay(wrappedProvider);
        DependencyProvider diagDepProvider = setUpDiagnosticDepProvider(wrappedProvider);
        Set<String> names2 = diagDepProvider.classLoaderNames();
        assertSame("Wrong set returned", names, names2);
        assertEquals("Set was modified", refNames, names2);
        verify();
    }

    /**
     * Tests the getClassLoader() implementation of the diagnostic bean
     * provider.
     */
    @Test
    public void testDiagnosticDependencyProviderGetClassLoader()
    {
        DependencyProvider wrappedProvider = createMock(DependencyProvider.class);
        EasyMock.expect(wrappedProvider.getClassLoader(CL_NAME)).andReturn(
                getClass().getClassLoader());
        EasyMock.replay(wrappedProvider);
        assertEquals("Wrong class loader returned",
                getClass().getClassLoader(), setUpDiagnosticDepProvider(
                        wrappedProvider).getClassLoader(CL_NAME));
        verify();
    }

    /**
     * Tests the getDefaultClassLoaderName() implementation of the diagnostic
     * bean provider.
     */
    @Test
    public void testDiagnosticDependencyProviderGetDefaultClassLoaderName()
    {
        DependencyProvider wrappedProvider =
                createMock(DependencyProvider.class);
        EasyMock.expect(wrappedProvider.getDefaultClassLoaderName()).andReturn(
                CL_NAME);
        EasyMock.replay(wrappedProvider);
        assertEquals("Wrong default class loader name", CL_NAME,
                setUpDiagnosticDepProvider(wrappedProvider)
                        .getDefaultClassLoaderName());
        verify();
    }

    /**
     * Tests the getDefaultClassLoaderName() implementation of the diagnostic
     * bean provider.
     */
    @Test
    public void testDiagnosticDependencyProviderSetDefaultClassLoaderName()
    {
        DependencyProvider wrappedProvider =
                createMock(DependencyProvider.class);
        wrappedProvider.setDefaultClassLoaderName(CL_NAME);
        EasyMock.replay(wrappedProvider);
        setUpDiagnosticDepProvider(wrappedProvider).setDefaultClassLoaderName(
                CL_NAME);
        verify();
    }

    /**
     * Tests the registerClassLoader() implementation of the diagnostic bean
     * provider.
     */
    @Test
    public void testDiagnosticDependencyProviderRegisterClassLoader()
    {
        DependencyProvider wrappedProvider = createMock(DependencyProvider.class);
        ClassLoader cl = getClass().getClassLoader();
        wrappedProvider.registerClassLoader(CL_NAME, cl);
        EasyMock.replay(wrappedProvider);
        setUpDiagnosticDepProvider(wrappedProvider).registerClassLoader(CL_NAME,
                cl);
        verify();
    }

    /**
     * Tests the getInvocationHelper() implementation of the diagnostic bean
     * provider.
     */
    @Test
    public void testDiagnosticDependencyProviderGetInvocationHelper()
    {
        DependencyProvider wrappedProvider =
                createMock(DependencyProvider.class);
        InvocationHelper invHlp = new InvocationHelper();
        EasyMock.expect(wrappedProvider.getInvocationHelper())
                .andReturn(invHlp);
        EasyMock.replay(wrappedProvider);
        DependencyProvider dp = setUpDiagnosticDepProvider(wrappedProvider);
        assertSame("Wrong invocation helper", invHlp, dp.getInvocationHelper());
        verify();
    }

    /**
     * A concrete subclass of LifeCycleBeanProvider used for testing.
     */
    static class LifeCycleBeanProviderTestImpl extends LifeCycleBeanProvider
    {
        public LifeCycleBeanProviderTestImpl(BeanProvider createProvider,
                Invokable initinv)
        {
            super(createProvider, initinv);
        }

        public LifeCycleBeanProviderTestImpl(BeanProvider createProvider)
        {
            super(createProvider);
        }

        public Object getBean(DependencyProvider dependencyProvider)
        {
            // just a dummy that won't be called
            throw new UnsupportedOperationException("Not yet implemented!");
        }
    }
}
