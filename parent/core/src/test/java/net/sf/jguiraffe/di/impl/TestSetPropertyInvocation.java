/*
 * Copyright 2006-2022 The JGUIraffe Team.
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
import static org.junit.Assert.assertSame;
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
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for SetPropertyInvocation. Note that this class cannot be derived
 * from {@link AbstractInvocationTest} because some of the
 * default properties of invocations are treated differently by this class.
 *
 * @author Oliver Heger
 * @version $Id: TestSetPropertyInvocation.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSetPropertyInvocation
{
    /** Constant for the name of the property to set. */
    private static final String PROP_NAME = "intProp";

    /** Constant for the property's value. */
    private static final int PROP_VALUE = 42;

    /** Stores the dependency for the property value. */
    private ConstantBeanProvider valueDependency;

    /** Stores the invocation to be tested. */
    private SetPropertyInvocation invocation;

    @Before
    public void setUp() throws Exception
    {
        valueDependency = ConstantBeanProvider.getInstance(PROP_VALUE);
        invocation = new SetPropertyInvocation(PROP_NAME, valueDependency);
    }

    /**
     * Tests whether the correct parameter dependencies are returned.
     */
    @Test
    public void testGetParameterDependencies()
    {
        List<Dependency> deps = invocation.getParameterDependencies();
        assertEquals("Wrong number of dependencies", 1, deps.size());
        assertSame("Wrong dependency", valueDependency, deps.get(0));
    }

    /**
     * Tests whether the correct property name is returned.
     */
    @Test
    public void testGetPropertyName()
    {
        assertEquals("Wrong property name", PROP_NAME, invocation
                .getPropertyName());
    }

    /**
     * Tries to create an invocation without a property name. This should cause
     * an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNullPropertyName()
    {
        new SetPropertyInvocation(null, valueDependency);
    }

    /**
     * Tries to create an invocation without a property value. This should cause
     * an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNullPropertyValue()
    {
        new SetPropertyInvocation(PROP_NAME, null);
    }

    /**
     * Checks an invocation. Tests whether the property is correctly set on the
     * target object.
     */
    private void checkInvoke()
    {
        DependencyProvider depProvider =
                EasyMock.createMock(DependencyProvider.class);
        EasyMock.expect(depProvider.getDependentBean(valueDependency))
                .andReturn(valueDependency.getBean());
        EasyMock.expect(depProvider.getInvocationHelper()).andReturn(
                new InvocationHelper());
        EasyMock.replay(depProvider);
        ReflectionTestClass target = new ReflectionTestClass();
        assertNull("Wrong result of invoke()",
                invocation.invoke(depProvider, target));
        assertEquals("Wrong value of property", PROP_VALUE, target.getIntProp());
        EasyMock.verify(depProvider);
    }

    /**
     * Tests the invoke() method.
     */
    @Test
    public void testInvoke()
    {
        checkInvoke();
    }

    /**
     * Tests invocation when the property value has to be converted.
     */
    @Test
    public void testInvokeConvert()
    {
        valueDependency = ConstantBeanProvider.getInstance(String
                .valueOf(PROP_VALUE));
        invocation = new SetPropertyInvocation(PROP_NAME, valueDependency);
        checkInvoke();
    }

    /**
     * Tries to call invoke() with a null target. This should cause an
     * exception.
     */
    @Test
    public void testInvokeNullTarget()
    {
        DependencyProvider depProvider = EasyMock
                .createMock(DependencyProvider.class);
        EasyMock.replay(depProvider);
        try
        {
            invocation.invoke(depProvider, null);
            fail("Could call invoke without a target!");
        }
        catch (InjectionException iex)
        {
            EasyMock.verify(depProvider);
        }
    }

    /**
     * Tries to call invoke() with a null dependency provider. This should cause
     * an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvokeNullDepProvider()
    {
        invocation.invoke(null, new ReflectionTestClass());
    }

    /**
     * Tests the string representation for a property invocation.
     */
    @Test
    public void testToString()
    {
        String s = invocation.toString();
        assertTrue("Property name not found: " + s, s.indexOf(PROP_NAME) >= 0);
        assertTrue("Value dependency not found: " + s, s
                .indexOf(valueDependency.toString()) >= 0);
    }
}
