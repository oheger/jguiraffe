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
package net.sf.jguiraffe.di.impl.providers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.ReflectionTestClass;
import net.sf.jguiraffe.di.impl.ClassDescription;
import net.sf.jguiraffe.di.impl.ConstructorInvocation;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for ConstructorBeanProvider.
 *
 * @author Oliver Heger
 * @version $Id: TestConstructorBeanProvider.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestConstructorBeanProvider
{
    /** The constructor invocation used for testing. */
    private ConstructorInvocation invocation;

    /** Stores the provider to be tested. */
    private ConstructorBeanProvider provider;

    @Before
    public void setUp() throws Exception
    {
        invocation =
                new ConstructorInvocation(
                        ClassDescription.getInstance(ReflectionTestClass.class
                                .getName()),
                        SimpleBeanProviderTestHelper
                                .getParameterClassDescriptions(),
                        SimpleBeanProviderTestHelper.getParameterDependencies());
        provider = new ConstructorBeanProvider(invocation);
    }

    /**
     * Tests whether the correct invocation is returned.
     */
    @Test
    public void testGetInvocation()
    {
        assertSame("Wrong invocation returned", invocation, provider
                .getInvocation());
    }

    /**
     * Tests whether the correct dependencies are returned.
     */
    @Test
    public void testGetDependencies()
    {
        Set<Dependency> dependencies = provider.getDependencies();
        Set<Dependency> invdeps = new HashSet<Dependency>(invocation
                .getParameterDependencies());
        assertEquals("Wrong number of dependencies", invdeps.size(),
                dependencies.size());
        for (Dependency d : invdeps)
        {
            assertTrue("Dependency not found " + d, dependencies.contains(d));
        }
    }

    /**
     * Tests querying the bean class.
     */
    @Test
    public void testGetBeanClass()
    {
        DependencyProvider depProvider = SimpleBeanProviderTestHelper
                .setUpDependencyProvider(true);
        assertEquals("Wrong bean class", ReflectionTestClass.class, provider
                .getBeanClass(depProvider));
        EasyMock.verify(depProvider);
    }

    /**
     * Tests obtaining the bean from the provider.
     */
    @Test
    public void testGetBean()
    {
        DependencyProvider depProvider = SimpleBeanProviderTestHelper
                .setUpDepProviderForGetBean(provider);
        SimpleBeanProviderTestHelper.checkTestInstance(provider
                .getBean(depProvider));
        EasyMock.verify(depProvider);
    }

    /**
     * Tests whether each invocation of getBean() creates a new instance.
     */
    @Test
    public void testGetBeanTwice()
    {
        DependencyProvider depProvider1 = SimpleBeanProviderTestHelper
                .setUpDepProviderForGetBean(provider);
        DependencyProvider depProvider2 = SimpleBeanProviderTestHelper
                .setUpDepProviderForGetBean(provider);
        ReflectionTestClass obj = (ReflectionTestClass) provider
                .getBean(depProvider1);
        assertNotSame("Same instance returned", obj, provider
                .getBean(depProvider2));
        EasyMock.verify(depProvider1, depProvider2);
    }

    /**
     * Tests invoking the default constructor.
     */
    @Test
    public void testGetBeanDefaultCtor()
    {
        DependencyProvider depProvider =
                SimpleBeanProviderTestHelper.setUpDependencyProvider(true);
        invocation =
                new ConstructorInvocation(
                        ClassDescription.getInstance(ReflectionTestClass.class),
                        null);
        provider = new ConstructorBeanProvider(invocation);
        ReflectionTestClass obj =
                (ReflectionTestClass) provider.getBean(depProvider);
        assertNull("String property is set", obj.getStringProp());
        assertEquals("Int property is set", 0, obj.getIntProp());
        EasyMock.verify(depProvider);
    }

    /**
     * Tests the constructor when a null invocation is passed in. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNullInvocation()
    {
        new ConstructorBeanProvider(null);
    }

    /**
     * Tests the string representation. We test whether information about the
     * constructor to be called can be found in the returned string.
     */
    @Test
    public void testToString()
    {
        String s = provider.toString();
        assertTrue("Constructor info not found in string " + s, s
                .indexOf(invocation.toString()) >= 0);
    }
}
