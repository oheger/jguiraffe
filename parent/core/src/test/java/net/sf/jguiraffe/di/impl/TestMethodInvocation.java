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
package net.sf.jguiraffe.di.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.InjectionException;
import net.sf.jguiraffe.di.InvocationHelper;
import net.sf.jguiraffe.di.ReflectionTestClass;
import net.sf.jguiraffe.di.impl.providers.ConstantBeanProvider;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for MethodInvocation.
 *
 * @author Oliver Heger
 * @version $Id: TestMethodInvocation.java 207 2012-02-09 07:30:13Z oheger $
 */
public class TestMethodInvocation extends AbstractInvocationTest
{
    /** Constant for the name of the method to be invoked. */
    private static final String METHOD_NAME = "initialize";

    /** Constant for the name of the static method to be invoked. */
    private static final String STATIC_METHOD = "getInstance";

    @Override
    protected Invocation createInvocation(ClassDescription targetClass,
            ClassDescription[] types, Dependency... values)
    {
        return new MethodInvocation(targetClass, METHOD_NAME, types, values);
    }

    /**
     * Tests the constructor that does not take a target class.
     */
    @Test
    public void testInitNoTargetClass()
    {
        MethodInvocation inv = new MethodInvocation(METHOD_NAME, PARAM_TYPES,
                paramDeps);
        assertNull("A target class is set", inv.getTargetClass());
        assertEquals("Wrong method name", METHOD_NAME, inv.getMethodName());
    }

    /**
     * Tests creating an instance without specifying a method name. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoMethodName()
    {
        new MethodInvocation(null, PARAM_TYPES, paramDeps);
    }

    /**
     * Tries constructing a static invocation without a target class. This
     * should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitStaticNoTargetClass()
    {
        new MethodInvocation(null, STATIC_METHOD, true, PARAM_TYPES, paramDeps);
    }

    /**
     * Tests a successful invocation when all parameter types are defined.
     */
    @Test
    public void testInvokeByParamTypes()
    {
        checkInvocation(new MethodInvocation(METHOD_NAME, PARAM_TYPES,
                paramDeps));
    }

    /**
     * Tests a successful invocation when not all parameter types are known.
     */
    @Test
    public void testInvokeByParamValues()
    {
        checkInvocation(new MethodInvocation(METHOD_NAME, null, paramDeps));
    }

    /**
     * Tests whether the target is ignored for non-static invocations.
     */
    @Test
    public void testInvokeIgnoreTargetClass()
    {
        checkInvocation(new MethodInvocation(ClassDescription
                .getInstance(String.class), METHOD_NAME, PARAM_TYPES, paramDeps));
    }

    /**
     * Tests whether the passed in instance is ignored for a static invocation
     * when all parameter types are known.
     */
    @Test
    public void testInvokeStaticWithInstanceParamTypes()
    {
        checkInvokeStaticWithInstance(PARAM_TYPES);
    }

    /**
     * Tests whether the passed in instance is ignored for a static invocation
     * when not all parameter types are known.
     */
    @Test
    public void testInvokeStaticWithInstanceParamValues()
    {
        checkInvokeStaticWithInstance(null);
    }

    /**
     * Tests whether type conversions are performed when invoking a method.
     */
    @Test
    public void testInvokeWithConversion()
    {
        Dependency[] args = new Dependency[2];
        args[0] = paramDeps[0];
        args[1] =
                ConstantBeanProvider.getInstance(String
                        .valueOf(PARAM_VALUES[1]));
        checkInvocation(new MethodInvocation(METHOD_NAME, PARAM_TYPES, args),
                args);
    }

    /**
     * Performs a static invocation and passes in an instance. The instance
     * should be ignored.
     *
     * @param paramTypes the parameter types of the invocation
     */
    private void checkInvokeStaticWithInstance(ClassDescription[] paramTypes)
    {
        MethodInvocation inv = new MethodInvocation(TARGET_CLASS,
                STATIC_METHOD, true, paramTypes, paramDeps);
        DependencyProvider depProvider = setUpDependencyProvider();
        checkTestInstance((ReflectionTestClass) inv.invoke(depProvider, this));
        EasyMock.verify(depProvider);
    }

    /**
     * Tests a successful method invocation with the given parameter
     * dependencies.
     *
     * @param inv the invocation object
     * @param parameters the parameter dependencies
     */
    private void checkInvocation(MethodInvocation inv, Dependency[] parameters)
    {
        ReflectionTestClass obj = new ReflectionTestClass("oldString");
        DependencyProvider depProvider = setUpDependencyProvider(parameters);
        assertEquals("Wrong return value of method", "oldString",
                inv.invoke(depProvider, obj));
        checkTestInstance(obj);
        EasyMock.verify(depProvider);
    }

    /**
     * Tests a successful method invocation with standard parameters.
     *
     * @param inv the invocation object
     */
    private void checkInvocation(MethodInvocation inv)
    {
        checkInvocation(inv, paramDeps);
    }

    /**
     * Tests whether the properties of a test instance are correctly set.
     *
     * @param obj the instance to be tested
     */
    private void checkTestInstance(ReflectionTestClass obj)
    {
        assertEquals("Wrong string property", PARAM_VALUES[0], obj
                .getStringProp());
        assertEquals("Wrong int property", PARAM_VALUES[1], obj.getIntProp());
    }

    /**
     * Tests a static invocation.
     */
    @Test
    public void testInvokeStaticParamTypes()
    {
        DependencyProvider depProvider = setUpDependencyProvider();
        MethodInvocation inv = new MethodInvocation(TARGET_CLASS,
                STATIC_METHOD, PARAM_TYPES, paramDeps);
        ReflectionTestClass obj = (ReflectionTestClass) inv.invoke(depProvider,
                null);
        checkTestInstance(obj);
        EasyMock.verify(depProvider);
    }

    /**
     * Tests a static invocation when the parameter types are not known.
     */
    @Test
    public void testInvokeStaticParamValues()
    {
        DependencyProvider depProvider = setUpDependencyProvider();
        MethodInvocation inv = new MethodInvocation(TARGET_CLASS,
                STATIC_METHOD, null, paramDeps);
        ReflectionTestClass obj = (ReflectionTestClass) inv.invoke(depProvider,
                null);
        checkTestInstance(obj);
        EasyMock.verify(depProvider);
    }

    /**
     * Tests a static invocation when no target class is specified. This should
     * cause an exception.
     */
    @Test
    public void testInvokeStaticNoTargetClass()
    {
        DependencyProvider depProvider = EasyMock
                .createMock(DependencyProvider.class);
        EasyMock.replay(depProvider);
        MethodInvocation inv = new MethodInvocation(STATIC_METHOD, PARAM_TYPES,
                paramDeps);
        try
        {
            inv.invoke(depProvider, null);
            fail("Could invoke static method without a target class!");
        }
        catch (InjectionException iex)
        {
            // ok
        }
        EasyMock.verify(depProvider);
    }

    /**
     * Tests invoking a method that does not have arguments.
     */
    @Test
    public void testInvokeNoArgs()
    {
        DependencyProvider depProvider =
                EasyMock.createMock(DependencyProvider.class);
        EasyMock.expect(depProvider.getInvocationHelper()).andReturn(
                new InvocationHelper());
        EasyMock.replay(depProvider);
        MethodInvocation inv = new MethodInvocation("getIntProp", null);
        ReflectionTestClass obj =
                new ReflectionTestClass((String) PARAM_VALUES[0],
                        ((Integer) PARAM_VALUES[1]).intValue());
        assertEquals("Wrong method return value", PARAM_VALUES[1],
                inv.invoke(depProvider, obj));
        EasyMock.verify(depProvider);
    }

    /**
     * Tests the invoke() method when no dependency provider is passed in. This
     * will cause an error.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvokeNoDepProvider()
    {
        MethodInvocation inv = new MethodInvocation(METHOD_NAME, PARAM_TYPES,
                paramDeps);
        ReflectionTestClass obj = new ReflectionTestClass();
        inv.invoke(null, obj);
    }

    /**
     * Tests whether a target dependency can be set.
     */
    @Test
    public void testInvokeWithTargetDependency()
    {
        Dependency target = EasyMock.createMock(Dependency.class);
        ReflectionTestClass obj = new ReflectionTestClass();
        DependencyProvider provider = setUpDependencyProvider(paramDeps, false);
        EasyMock.expect(provider.getDependentBean(target)).andReturn(obj);
        EasyMock.replay(target, provider);
        MethodInvocation inv =
                new MethodInvocation(null, target, METHOD_NAME, false, null,
                        paramDeps);
        inv.invoke(provider, null);
        checkTestInstance(obj);
        EasyMock.verify(provider);
    }

    /**
     * Tests whether the target dependency is taken into account when querying
     * all dependencies.
     */
    @Test
    public void testGetParameterDependenciesWithTargetDependency()
    {
        Dependency target = EasyMock.createMock(Dependency.class);
        EasyMock.replay(target);
        MethodInvocation inv =
                new MethodInvocation(null, target, METHOD_NAME, false, null,
                        paramDeps);
        List<Dependency> deps = inv.getParameterDependencies();
        assertEquals("Wrong number of dependencies", paramDeps.length + 1,
                deps.size());
        assertTrue("Target dependency not found", deps.contains(target));
    }

    /**
     * Tests the toString() method if a target class is defined.
     */
    @Test
    public void testToStringTargetClass()
    {
        Invocation inv = createInvocation(TARGET_CLASS, PARAM_TYPES, paramDeps);
        String s = inv.toString();
        assertTrue("Invocation info not found in string " + s, s.indexOf("[ "
                + TARGET_CLASS + '.' + METHOD_NAME + '(') >= 0);
    }

    /**
     * Checks the string representation when no target class is provided.
     */
    @Test
    public void testToStringNoTargetClass()
    {
        Invocation inv = createInvocation(null, PARAM_TYPES, paramDeps);
        String s = inv.toString();
        assertTrue("Invocation info not found in string " + s, s.indexOf("[ "
                + METHOD_NAME + '(') >= 0);
    }
}
