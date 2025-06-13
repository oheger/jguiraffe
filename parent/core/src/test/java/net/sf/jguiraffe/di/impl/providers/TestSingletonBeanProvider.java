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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Set;

import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.InvocationHelper;
import net.sf.jguiraffe.di.ReflectionTestClass;
import net.sf.jguiraffe.di.impl.ClassDescription;
import net.sf.jguiraffe.di.impl.ConstructorInvocation;
import net.sf.jguiraffe.di.impl.HelperInvocations;
import net.sf.jguiraffe.di.impl.Invokable;
import net.sf.jguiraffe.di.impl.NameDependency;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for SingletonBeanProvider.
 *
 * @author Oliver Heger
 * @version $Id: TestSingletonBeanProvider.java 208 2012-02-11 20:57:33Z oheger $
 */
public class TestSingletonBeanProvider
{
    /** Constant for the name of the string bean. */
    private static final String STR_BEAN_NAME = "stringBean";

    /** Constant for the name of the int bean. */
    private static final String INT_BEAN_NAME = "integerBean";

    /** Stores the provider to be tested. */
    private SingletonBeanProvider provider;

    /**
     * Initializes the fixture. Creates a singleton bean provider that uses a
     * constructor bean provider as creator.
     */
    @Before
    public void setUp() throws Exception
    {
        provider = new SingletonBeanProvider(setUpCreationProvider());
    }

    /**
     * Creates a bean provider for creating new bean instances. This is a
     * constructor bean provider that creates an object of the reflection test
     * class.
     *
     * @return the new provider
     */
    private BeanProvider setUpCreationProvider()
    {
        ConstructorInvocation inv =
                new ConstructorInvocation(ClassDescription
                        .getInstance(ReflectionTestClass.class),
                        SimpleBeanProviderTestHelper
                                .getParameterClassDescriptions(),
                        NameDependency.getInstance(STR_BEAN_NAME),
                        NameDependency.getInstance(INT_BEAN_NAME));
        return new ConstructorBeanProvider(inv);
    }

    /**
     * Creates a dependency provider mock that can resolve the parameter
     * dependencies of the creation provider.
     *
     * @return the initialized mock object
     */
    private DependencyProvider setUpDependencyProvider()
    {
        DependencyProvider mock = EasyMock.createMock(DependencyProvider.class);
        EasyMock.expect(
                mock.getDependentBean(NameDependency.getInstance(STR_BEAN_NAME)))
                .andReturn(SimpleBeanProviderTestHelper.STR_VALUE);
        EasyMock.expect(
                mock.getDependentBean(NameDependency.getInstance(INT_BEAN_NAME)))
                .andReturn(SimpleBeanProviderTestHelper.INT_VALUE);
        mock.beanCreated(EasyMock.anyObject(), EasyMock.eq(provider));
        EasyMock.expect(mock.getInvocationHelper())
                .andReturn(new InvocationHelper()).anyTimes();
        EasyMock.replay(mock);
        return mock;
    }

    /**
     * Tests querying the provider's bean. Checks whether the correct bean is
     * returned and returns the dependency provider for verification.
     *
     * @return the used dependency provider
     */
    private DependencyProvider performGetBeanTest()
    {
        DependencyProvider depProvider = setUpDependencyProvider();
        ReflectionTestClass obj = (ReflectionTestClass) provider
                .getBean(depProvider);
        assertEquals("Wrong string property",
                SimpleBeanProviderTestHelper.STR_VALUE, obj.getStringProp());
        assertEquals("Wrong int property",
                SimpleBeanProviderTestHelper.INT_VALUE, obj.getIntProp());
        return depProvider;
    }

    /**
     * Tests obtaining the provider's bean.
     */
    @Test
    public void testGetBean()
    {
        EasyMock.verify(performGetBeanTest());
    }

    /**
     * Tests whether always the same bean is returned by getBean().
     */
    @Test
    public void testGetBeanCached()
    {
        DependencyProvider depProvider = performGetBeanTest();
        ReflectionTestClass obj = (ReflectionTestClass) provider
                .getBean(depProvider);
        for (int i = 0; i < 10; i++)
        {
            assertSame("Different bean instance returned", obj, provider
                    .getBean(depProvider));
        }
        EasyMock.verify(depProvider);
    }

    /**
     * Tests the lock ID handling before a bean was requested.
     */
    @Test
    public void testGetLockIDBeforeGetBean()
    {
        final Long lockID = 45L;
        provider.setLockID(lockID);
        assertEquals("Wrong lockID", lockID, provider.getLockID());
    }

    /**
     * Tests the lock ID handling after a bean was requested. Now the
     * getLockID() should always return null.
     */
    @Test
    public void testGetLockIDAfterGetBean()
    {
        DependencyProvider depProvider = performGetBeanTest();
        provider.setLockID(42L);
        assertNull("Lock ID is not null", provider.getLockID());
        EasyMock.verify(depProvider);
    }

    /**
     * Tests querying the dependencies before a bean was requested.
     */
    @Test
    public void testGetDependenciesBeforeGetBean()
    {
        Set<Dependency> deps = provider.getDependencies();
        assertNotNull("No dependencies returned", deps);
        assertEquals("Wrong dependencies returned", provider.getBeanCreator()
                .getDependencies(), deps);
    }

    /**
     * Tests querying the dependencies after a bean was requested. From now on
     * getDependencies() should return null.
     */
    @Test
    public void testGetDependenciesAfterGetBean()
    {
        DependencyProvider depProvider = performGetBeanTest();
        assertNull("Still dependencies returned", provider.getDependencies());
        EasyMock.verify(depProvider);
    }

    /**
     * Tests that per default no shutdown handler is set.
     */
    @Test
    public void testGetShutdownHandler()
    {
        assertNull("A shutdown handler is set", provider.getShutdownHandler());
    }

    /**
     * Tests querying the shutdown invokable if none is set. In this case a
     * dummy should be returned.
     */
    @Test
    public void testFetchShutdownInvokableUndefined()
    {
        assertSame("Wrong dummy shutdown invokable",
                HelperInvocations.NULL_INVOCATION,
                provider.fetchShutdownInvokable());
    }

    /**
     * Tests that on shutdown() the shutdown handler is called.
     */
    @Test
    public void testShutdownInvokable()
    {
        Invokable shutdownHandler = EasyMock.createMock(Invokable.class);
        DependencyProvider depShutdown = EasyMock
                .createMock(DependencyProvider.class);
        provider = new SingletonBeanProvider(setUpCreationProvider(), null,
                shutdownHandler);
        DependencyProvider dp = performGetBeanTest();
        Object bean = provider.getBean(dp);
        EasyMock.expect(shutdownHandler.invoke(depShutdown, bean)).andReturn(
                null);
        EasyMock.replay(depShutdown, shutdownHandler);
        provider.shutdown(depShutdown);
        EasyMock.verify(depShutdown, shutdownHandler);
    }

    /**
     * Tests the shutdown() method when no bean was created. In this case the
     * shutdown handler must not be called.
     */
    @Test
    public void testShutdownInvokableNoBean()
    {
        Invokable shutdownHandler = EasyMock.createMock(Invokable.class);
        DependencyProvider depShutdown = EasyMock
                .createMock(DependencyProvider.class);
        provider = new SingletonBeanProvider(setUpCreationProvider(), null,
                shutdownHandler);
        EasyMock.replay(depShutdown, shutdownHandler);
        provider.shutdown(depShutdown);
        EasyMock.verify(depShutdown, shutdownHandler);
    }
}
