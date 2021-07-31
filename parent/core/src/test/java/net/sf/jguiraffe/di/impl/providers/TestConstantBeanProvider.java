/*
 * Copyright 2006-2021 The JGUIraffe Team.
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.InjectionException;
import net.sf.jguiraffe.di.InvocationHelper;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for ConstantBeanProvider.
 *
 * @author Oliver Heger
 * @version $Id: TestConstantBeanProvider.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestConstantBeanProvider
{
    /** The bean managed by the provider. */
    private static final Integer VALUE = 42;

    /**
     * Creates a default provider instance initialized with a test value.
     *
     * @return the provider instance
     */
    private ConstantBeanProvider setUpProvider()
    {
        return ConstantBeanProvider.getInstance(VALUE);
    }

    /**
     * Tests whether the dependency can be resolved to the expected object.
     *
     * @param dep the dependency to test
     * @param expected the expected bean
     * @param conversion a flag whether a type conversion should be expected
     */
    private void checkDependency(ConstantBeanProvider dep, Object expected,
            boolean conversion)
    {
        BeanStore store = EasyMock.createMock(BeanStore.class);
        DependencyProvider depProvider =
                EasyMock.createMock(DependencyProvider.class);
        if (conversion)
        {
            EasyMock.expect(depProvider.getInvocationHelper()).andReturn(
                    new InvocationHelper());
        }
        EasyMock.replay(store, depProvider);
        BeanProvider provider = dep.resolve(store, depProvider);
        assertNull("Provider has further dependencies",
                provider.getDependencies());
        assertEquals("Wrong bean returned", expected,
                provider.getBean(depProvider));
        EasyMock.verify(store, depProvider);
    }

    /**
     * Tests whether the correct bean is returned if no conversion is required.
     */
    @Test
    public void testGetBeanNoConversion()
    {
        checkDependency(setUpProvider(), VALUE, false);
    }

    /**
     * Tests getBean() if a type conversion is required.
     */
    @Test
    public void testGetBeanConversion()
    {
        ConstantBeanProvider dep =
                ConstantBeanProvider.getInstance(Integer.class,
                        String.valueOf(VALUE));
        checkDependency(dep, VALUE, true);
    }

    /**
     * Tests whether a type conversion is performed only once.
     */
    @Test
    public void testGetBeanConversionCached()
    {
        ConstantBeanProvider dep =
                ConstantBeanProvider.getInstance(Integer.class,
                        String.valueOf(VALUE));
        checkDependency(dep, VALUE, true);
        checkDependency(dep, VALUE, false);
    }

    /**
     * Tests the getBean() method which does not expect a dependency provider.
     */
    @Test
    public void testGetBeanNoDepProvider()
    {
        String data = String.valueOf(VALUE);
        ConstantBeanProvider provider =
                ConstantBeanProvider.getInstance(Integer.class, data);
        assertEquals("Wrong bean", data, provider.getBean());
    }

    /**
     * Tests querying the dependencies.
     */
    @Test
    public void testGetDependencies()
    {
        assertNull("Dependencies not null", setUpProvider().getDependencies());
    }

    /**
     * Tests querying the lock ID for a newly created instance.
     */
    @Test
    public void testGetLockIDAfterCreate()
    {
        assertNull("Lock ID after creation not null", setUpProvider()
                .getLockID());
    }

    /**
     * Tests querying the lock ID after it was set.
     */
    @Test
    public void testGetLockIDAfterSet()
    {
        ConstantBeanProvider provider = setUpProvider();
        provider.setLockID(100L);
        assertNull("Lock ID not null after setting", provider.getLockID());
    }

    /**
     * Tests whether the managed bean is available. This should be the case.
     */
    @Test
    public void testBeanAvailable()
    {
        assertTrue("Bean not available", setUpProvider().isBeanAvailable());
    }

    /**
     * Tests invoking the shutdown() method. This should have no effect. We can
     * only test that no exception is thrown and the parameter object is not
     * touched.
     */
    @Test
    public void testShutdown()
    {
        ConstantBeanProvider provider = setUpProvider();
        DependencyProvider depProvider =
                EasyMock.createMock(DependencyProvider.class);
        EasyMock.replay(depProvider);
        provider.shutdown(depProvider);
        EasyMock.verify(depProvider);
        assertEquals("Bean was changed", VALUE, provider.getBean());
    }

    /**
     * Tests whether the bean class can be queried if no conversion class is
     * set.
     */
    @Test
    public void testGetBeanClass()
    {
        DependencyProvider depProvider =
                EasyMock.createMock(DependencyProvider.class);
        EasyMock.replay(depProvider);
        assertEquals("Wrong class of bean", Integer.class, setUpProvider()
                .getBeanClass(depProvider));
        EasyMock.verify(depProvider);
    }

    /**
     * Tests whether the bean class can be queried if the bean is null, and no
     * conversion class is set.
     */
    @Test
    public void testGetBeanClassNull()
    {
        DependencyProvider depProvider =
                EasyMock.createMock(DependencyProvider.class);
        EasyMock.replay(depProvider);
        ConstantBeanProvider provider = ConstantBeanProvider.getInstance(null);
        assertEquals("Wrong class for null bean", Object.class,
                provider.getBeanClass(depProvider));
        EasyMock.verify(depProvider);
    }

    /**
     * Tests whether the bean class can be queried if a conversion class is set.
     */
    @Test
    public void testGetBeanClassConversionClass()
    {
        DependencyProvider depProvider =
                EasyMock.createMock(DependencyProvider.class);
        EasyMock.replay(depProvider);
        ConstantBeanProvider provider =
                ConstantBeanProvider.getInstance(Integer.class,
                        String.valueOf(VALUE));
        assertEquals("Wrong class for null bean", Integer.class,
                provider.getBeanClass(depProvider));
        EasyMock.verify(depProvider);
    }

    /**
     * Tests whether the bean class can be queried if it is set explicitly, but
     * the bean itself is null.
     */
    @Test
    public void testGetBeanClassNullConversionClass()
    {
        DependencyProvider depProvider =
                EasyMock.createMock(DependencyProvider.class);
        EasyMock.replay(depProvider);
        ConstantBeanProvider provider =
                ConstantBeanProvider.getInstance(getClass(), null);
        assertEquals("Wrong bean class", getClass(),
                provider.getBeanClass(depProvider));
        EasyMock.verify(depProvider);
    }

    /**
     * Tests a failed conversion to the specified target class.
     */
    @Test(expected = InjectionException.class)
    public void testGetInstanceConvertFailed()
    {
        ConstantBeanProvider dep =
                ConstantBeanProvider
                        .getInstance(getClass(), "A strange object");
        checkDependency(dep, null, true);
    }

    /**
     * Tests the getInstance() method when a null class is passed in. In this
     * case the class argument is completely ignored.
     */
    @Test
    public void testGetInstanceNullClass()
    {
        checkDependency(ConstantBeanProvider.getInstance(null, "42"), "42",
                false);
    }

    /**
     * Tests the getInstance() method when a null value is specified. In this
     * case no conversion will be performed, even if a class is specified.
     */
    @Test
    public void testGetInstanceNullObject()
    {
        checkDependency(ConstantBeanProvider.getInstance(Integer.class, null),
                null, false);
    }

    /**
     * Tests multiple accesses to a null bean.
     */
    @Test
    public void testGetBeanNullObjectCached()
    {
        ConstantBeanProvider dep =
                ConstantBeanProvider.getInstance(getClass(), null);
        checkDependency(dep, null, false);
        checkDependency(dep, null, false);
    }

    /**
     * Tests the toString() implementation. Here we check whether the value of
     * the dependency is contained in the returned string.
     */
    @Test
    public void testToString()
    {
        String str = setUpProvider().toString();
        assertTrue("Value not found in string " + str,
                str.indexOf(VALUE.toString()) >= 0);
    }
}
