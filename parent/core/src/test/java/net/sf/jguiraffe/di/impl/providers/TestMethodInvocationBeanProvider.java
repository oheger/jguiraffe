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

import java.util.Set;

import junit.framework.TestCase;
import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.ReflectionTestClass;
import net.sf.jguiraffe.di.impl.ClassDescription;
import net.sf.jguiraffe.di.impl.MethodInvocation;

import org.easymock.EasyMock;

/**
 * Test class for MethodInvocationBeanProvider.
 *
 * @author Oliver Heger
 * @version $Id: TestMethodInvocationBeanProvider.java 86 2007-09-01 14:40:21Z
 * oheger $
 */
public class TestMethodInvocationBeanProvider extends TestCase
{
    /** Constant for the name of the static factory method. */
    private static final String STATIC_MEHTOD = "getInstance";

    /** Constant for the name of the init method. */
    private static final String INIT_METHOD = "initialize";

    /**
     * Stores the dependency to the bean instance, on which to invoke a method.
     * This field is initialized by the <code>setUpInitProvider()</code>
     * method.n
     */
    private ConstantBeanProvider targetDependency;

    /**
     * Creates a provider object for calling a static factory method. This
     * provider does not have a dependency for obtaining a bean instance, on
     * which to invoke the method.
     *
     * @return the provider
     */
    private MethodInvocationBeanProvider setUpStaticProvider()
    {
        MethodInvocation inv = new MethodInvocation(ClassDescription
                .getInstance(ReflectionTestClass.class), STATIC_MEHTOD, null,
                SimpleBeanProviderTestHelper.getParameterDependencies());
        return new MethodInvocationBeanProvider(inv);
    }

    /**
     * This is a short form of <code>setUpInitProvider(null);.
     *
     * @return the provider
     */
    private MethodInvocationBeanProvider setUpInitProvider()
    {
        return setUpInitProvider(null);
    }

    /**
     * Creates a provider object for calling an initialization method on another
     * bean. This provider has an additional dependency that resolves to an
     * instance of the ReflectionTestClass class.
     *
     * @param beanClsDsc an optional description of the class of the managed
     * bean
     * @return the provider
     */
    private MethodInvocationBeanProvider setUpInitProvider(
            ClassDescription beanClsDsc)
    {
        MethodInvocation inv = new MethodInvocation(INIT_METHOD, null,
                SimpleBeanProviderTestHelper.getParameterDependencies());
        targetDependency = ConstantBeanProvider
                .getInstance(new ReflectionTestClass());
        return (beanClsDsc != null) ? new MethodInvocationBeanProvider(
                targetDependency, inv, beanClsDsc)
                : new MethodInvocationBeanProvider(targetDependency, inv);
    }

    /**
     * Tests whether the parameter dependencies can be found in the dependencies
     * returned by the provider.
     *
     * @param deps a set with the dependencies
     */
    private void checkParameterDependencies(Set<Dependency> deps)
    {
        for (Object value : SimpleBeanProviderTestHelper.PARAM_VALUES)
        {
            boolean found = false;
            for (Dependency d : deps)
            {
                assertTrue("Wrong dependency type: " + d,
                        d instanceof ConstantBeanProvider);
                ConstantBeanProvider cd = (ConstantBeanProvider) d;
                if (value.equals(cd.getBean()))
                {
                    found = true;
                    break;
                }
            }
            assertTrue("Param dependency not found: " + value, found);
        }
    }

    /**
     * Tests querying the dependencies for a static method invocation.
     */
    public void testGetDependenciesStatic()
    {
        MethodInvocationBeanProvider provider = setUpStaticProvider();
        Set<Dependency> deps = provider.getDependencies();
        assertEquals("Wrong number of dependencies",
                SimpleBeanProviderTestHelper.PARAM_VALUES.length, deps.size());
        checkParameterDependencies(deps);
    }

    /**
     * Tests querying the dependencies for an invocation on an instance.
     */
    public void testGetDependenciesInit()
    {
        MethodInvocationBeanProvider provider = setUpInitProvider();
        Set<Dependency> deps = provider.getDependencies();
        assertEquals("Wrong number of dependencies",
                SimpleBeanProviderTestHelper.PARAM_VALUES.length + 1, deps
                        .size());
        checkParameterDependencies(deps);
        assertTrue("Bean dependency not found", deps.contains(targetDependency));
    }

    /**
     * Tests querying the bean class for a static invocation.
     */
    public void testGetBeanClassStatic()
    {
        DependencyProvider depProvider = SimpleBeanProviderTestHelper
                .setUpDependencyProvider(true);
        MethodInvocationBeanProvider provider = setUpStaticProvider();
        assertEquals("Wrong bean class", ReflectionTestClass.class, provider
                .getBeanClass(depProvider));
        EasyMock.verify(depProvider);
    }

    /**
     * Tests querying the bean class for an invocation on an instance. Currently
     * the bean class is determined only by accessing the method invocation
     * object. So in this case the result will be null.
     */
    public void testGetBeanClassInit()
    {
        DependencyProvider depProvider = EasyMock
                .createMock(DependencyProvider.class);
        EasyMock.replay(depProvider);
        MethodInvocationBeanProvider provider = setUpInitProvider();
        assertNull("Wrong bean class", provider.getBeanClass(depProvider));
        EasyMock.verify(depProvider);
    }

    /**
     * Tests querying the bean class when it is explicitly defined.
     */
    public void testGetBeanClassExplicit()
    {
        DependencyProvider depProvider = EasyMock
                .createMock(DependencyProvider.class);
        EasyMock.replay(depProvider);
        ClassDescription cd = ClassDescription
                .getInstance(ReflectionTestClass.class);
        MethodInvocationBeanProvider provider = setUpInitProvider(cd);
        assertEquals("Wrong bean class", ReflectionTestClass.class, provider
                .getBeanClass(depProvider));
        EasyMock.verify(depProvider);
    }

    /**
     * Tests querying the provider's bean for a static invocation.
     */
    public void testGetBeanStatic()
    {
        MethodInvocationBeanProvider provider = setUpStaticProvider();
        DependencyProvider depProvider = SimpleBeanProviderTestHelper
                .setUpDepProviderForGetBean(provider);
        SimpleBeanProviderTestHelper.checkTestInstance(provider
                .getBean(depProvider));
        EasyMock.verify(depProvider);
    }

    /**
     * Tests querying the provider's bean for an invocation on an instance.
     */
    public void testGetBeanInit()
    {
        MethodInvocationBeanProvider provider = setUpInitProvider();
        DependencyProvider depProvider = SimpleBeanProviderTestHelper
                .setUpDepProviderForGetBean(provider);
        Object bean = provider.getBean(depProvider);
        assertSame("Wrong bean returned", targetDependency.getBean(), bean);
        SimpleBeanProviderTestHelper.checkTestInstance(bean);
        EasyMock.verify(depProvider);
    }

    /**
     * Tests the string representation of a static invocation. We check whether
     * information about the method to call can be found.
     */
    public void testToStringStatic()
    {
        MethodInvocationBeanProvider provider = setUpStaticProvider();
        String s = provider.toString();
        assertTrue("Cannot find information about the method invocation: " + s,
                s.indexOf(provider.getInvocation().toString()) >= 0);
        assertTrue("Target dependency is specified: " + s, s
                .indexOf("target =") < 0);
    }

    /**
     * Tests the string representation for an invocation on an instance. This
     * time we also check whether the target dependency is printed.
     */
    public void testToStringInit()
    {
        MethodInvocationBeanProvider provider = setUpInitProvider();
        String s = provider.toString();
        assertTrue("Cannot find information about the method invocation: " + s,
                s.indexOf(provider.getInvocation().toString()) >= 0);
        assertTrue("Target dependency not found: " + s, s.indexOf("target = "
                + targetDependency) >= 0);
    }

    /**
     * Tests creating an instance when a null invocation is specified. This
     * should cause an exception.
     */
    public void testInitNullInvocation()
    {
        try
        {
            new MethodInvocationBeanProvider(null);
            fail("Could create instance with null invocation!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }
}
