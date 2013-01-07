/*
 * Copyright 2006-2013 The JGUIraffe Team.
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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.di.BeanInitializer;
import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.InjectionException;
import net.sf.jguiraffe.di.InvocationHelper;
import net.sf.jguiraffe.di.ReflectionTestClass;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link RestrictedDependencyProvider}.
 *
 * @author Oliver Heger
 * @version $Id: TestRestrictedDependencyProvider.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestRestrictedDependencyProvider
{
    /** Constant for a test class loader name. */
    private static final String CL_NAME = "TestClassLoader";

    /** Constant for a test class loader. */
    private static final ClassLoader LOADER = new ClassLoader()
    {
    };

    /** A mock for the class loader provider. */
    private ClassLoaderProvider clp;

    /** The invocation helper object. */
    private InvocationHelper invHelper;

    /** The dependency provider to be tested. */
    private RestrictedDependencyProvider depProvider;

    @Before
    public void setUp() throws Exception
    {
        clp = EasyMock.createMock(ClassLoaderProvider.class);
        invHelper = new InvocationHelper();
        depProvider = new RestrictedDependencyProvider(clp, invHelper);
    }

    /**
     * Tests querying the class loader provider.
     */
    @Test
    public void testGetClassLoaderProvider()
    {
        assertEquals("Wrong CLP", clp, depProvider.getClassLoaderProvider());
    }

    /**
     * Tries to create an instance without a CLP.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoCLP()
    {
        new RestrictedDependencyProvider(null, invHelper);
    }

    /**
     * Tries to create an instance without an invocation helper. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoInvHelper()
    {
        new RestrictedDependencyProvider(clp, null);
    }

    /**
     * Tests whether the invocation helper can be queried.
     */
    @Test
    public void testGetInvocationHelper()
    {
        assertSame("Wrong invocation helper", invHelper,
                depProvider.getInvocationHelper());
    }

    /**
     * Tests the implementation of classLoaderNames().
     */
    @Test
    public void testClassLoaderNames()
    {
        Set<String> names = new HashSet<String>();
        names.add(CL_NAME);
        EasyMock.expect(clp.classLoaderNames()).andReturn(names);
        EasyMock.replay(clp);
        assertSame("Wrong set returned", names, depProvider.classLoaderNames());
        EasyMock.verify(clp);
    }

    /**
     * Tests querying a specific class loader.
     */
    @Test
    public void testGetClassLoader()
    {
        EasyMock.expect(clp.getClassLoader(CL_NAME)).andReturn(LOADER);
        EasyMock.replay(clp);
        assertEquals("Wrong class loader", LOADER, depProvider
                .getClassLoader(CL_NAME));
        EasyMock.verify(clp);
    }

    /**
     * Tests whether the default class loader name can be queried.
     */
    @Test
    public void testGetDefaultClassLoaderName()
    {
        EasyMock.expect(clp.getDefaultClassLoaderName()).andReturn(CL_NAME);
        EasyMock.replay(clp);
        assertEquals("Wrong default class loader name", CL_NAME, depProvider
                .getDefaultClassLoaderName());
        EasyMock.verify(clp);
    }

    /**
     * Tests loading a class.
     */
    @Test
    public void testLoadClass()
    {
        clp.loadClass(getClass().getName(), CL_NAME);
        EasyMock.expectLastCall().andReturn(getClass());
        EasyMock.replay(clp);
        assertEquals("Wrong class", getClass(), depProvider.loadClass(
                getClass().getName(), CL_NAME));
        EasyMock.verify(clp);
    }

    /**
     * Tests registering a class loader.
     */
    @Test
    public void testRegisterClassLoader()
    {
        clp.registerClassLoader(CL_NAME, LOADER);
        EasyMock.replay(clp);
        depProvider.registerClassLoader(CL_NAME, LOADER);
        EasyMock.verify(clp);
    }

    /**
     * Tests whether the name of a default class loader can be set.
     */
    @Test
    public void testSetDefaultClassLoaderName()
    {
        clp.setDefaultClassLoaderName(CL_NAME);
        EasyMock.replay(clp);
        depProvider.setDefaultClassLoaderName(CL_NAME);
        EasyMock.verify(clp);
    }

    /**
     * Tests adding an initializer. This is not supported.
     */
    @Test
    public void testAddInitializer()
    {
        BeanInitializer ini = EasyMock.createMock(BeanInitializer.class);
        EasyMock.replay(ini);
        try
        {
            depProvider.addInitializer(ini);
            fail("Could add initializer!");
        }
        catch (UnsupportedOperationException uoex)
        {
            EasyMock.verify(ini);
        }
    }

    /**
     * Tests the beanCreated() implementation. This is not supported.
     */
    @Test
    public void testBeanCreated()
    {
        BeanProvider provider = EasyMock.createMock(BeanProvider.class);
        EasyMock.replay(provider);
        try
        {
            depProvider.beanCreated(this, provider);
            fail("beanCreated() was successful!");
        }
        catch (UnsupportedOperationException uoex)
        {
            EasyMock.verify(provider);
        }
    }

    /**
     * Tests querying another bean when the dependency can be resolved.
     */
    @Test
    public void testGetDependentBean()
    {
        Dependency dependency = EasyMock.createMock(Dependency.class);
        BeanProvider provider = EasyMock.createMock(BeanProvider.class);
        final Object bean = new Object();
        EasyMock.expect(dependency.resolve(null, depProvider)).andReturn(
                provider);
        EasyMock.expect(provider.getBean(depProvider)).andReturn(bean);
        EasyMock.replay(dependency, provider);
        assertEquals("Wrong bean", bean, depProvider
                .getDependentBean(dependency));
        EasyMock.verify(dependency, provider);
    }

    /**
     * Tests querying a dependency when the dependency cannot deal with a null
     * store.
     */
    @Test(expected = InjectionException.class)
    public void testGetDependentBeanNPE()
    {
        Dependency dependency = EasyMock.createMock(Dependency.class);
        EasyMock.expect(dependency.resolve(null, depProvider)).andThrow(
                new NullPointerException("Test exception!"));
        EasyMock.replay(dependency);
        depProvider.getDependentBean(dependency);
    }

    /**
     * Tests querying whether a bean is available. This is not supported.
     */
    @Test
    public void testIsBeanAvailable()
    {
        Dependency dependency = EasyMock.createMock(Dependency.class);
        EasyMock.replay(dependency);
        try
        {
            depProvider.isBeanAvailable(dependency);
            fail("Could query the status of a dependency bean!");
        }
        catch (UnsupportedOperationException uoex)
        {
            EasyMock.verify(dependency);
        }
    }

    /**
     * Tests setting the responsible context. This is not supported.
     */
    @Test
    public void testSetCreationBeanContext()
    {
        BeanContext context = EasyMock.createMock(BeanContext.class);
        EasyMock.replay(context);
        try
        {
            depProvider.setCreationBeanContext(context);
            fail("Could set the creation context!");
        }
        catch (UnsupportedOperationException uoex)
        {
            EasyMock.verify(context);
        }
    }

    /**
     * Tests whether a script with a set of Invokables can be executed using a
     * restricted dependency provider.
     */
    @Test
    public void testExecuteScript()
    {
        DefaultClassLoaderProvider clp = new DefaultClassLoaderProvider();
        depProvider = new RestrictedDependencyProvider(clp, invHelper);
        final int i1 = 21;
        final int i2 = 2;
        ReflectionTestClass data = new ReflectionTestClass();
        data.setIntProp(i1);
        data.setStringProp(String.valueOf(i2));
        ChainedInvocation inv = new ChainedInvocation();
        inv.addInvokable(new MethodInvocation("getIntProp", null), "intProp");
        inv.addInvokable(new MethodInvocation(ClassDescription
                .getInstance("java.math.BigInteger"), "valueOf", true,
                new ClassDescription[] {
                    ClassDescription.getInstance(Long.TYPE)
                }, inv.getChainDependency("intProp")), "i1");
        inv
                .addInvokable(new MethodInvocation("getStringProp", null),
                        "strProp");
        inv.addInvokable(new ConstructorInvocation(ClassDescription
                .getInstance("java.math.BigInteger"), new ClassDescription[] {
            ClassDescription.getInstance("java.lang.String")
        }, inv.getChainDependency("strProp")), "i2");
        inv.addInvokable(new MethodInvocation("multiply", null, inv
                .getChainDependency("i2")), "product", "i1");
        inv.addInvokable(new SetPropertyInvocation("data", inv
                .getChainDependency("product")));
        inv.invoke(depProvider, data);
        assertEquals("Wrong data property", BigInteger.valueOf(i1 * i2), data
                .getData());
    }
}
