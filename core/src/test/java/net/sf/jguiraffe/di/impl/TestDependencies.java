/*
 * Copyright 2006-2014 The JGUIraffe Team.
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

import org.easymock.EasyMock;

import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.InjectionException;
import net.sf.jguiraffe.di.impl.providers.ConstantBeanProvider;
import junit.framework.TestCase;

/**
 * Test class for implementations of the Dependency interface.
 *
 * @author Oliver Heger
 * @version $Id: TestDependencies.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestDependencies extends TestCase
{
    /** Constant for the name of the test bean. */
    private static final String BEAN_NAME = "MyTestBean";

    /** Constant for the prefix used for dummy beans. */
    private static final String NAME_PREFIX = "DummyBean";

    /** Constant for the name of a bean store. */
    private static final String STORE_PREFIX = "DummyStore";

    /** Constant for the number of dummy beans stored in a context. */
    private static final int BEAN_COUNT = 10;

    /** Constant for the number of dummy stores. */
    private static final int STORE_COUNT = 3;

    /** A bean store for testing. */
    private DefaultBeanStore store;

    /** The parent bean store of the hierarchy. */
    private DefaultBeanStore parentStore;

    /** The bean provider with the dependent bean. */
    private BeanProvider dependentProvider;

    /** Stores a mock for a dependency provider.*/
    private DependencyProvider dependencyProvider;

    /**
     * Sets up the test environment. Creates a hierarchy of bean stores and
     * populates them with dummy bean providers.
     */
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        parentStore = new DefaultBeanStore();
        DefaultBeanStore currentStore = parentStore;
        for (int i = 0; i < STORE_COUNT; i++)
        {
            DefaultBeanStore st = createStore();
            st.setName(STORE_PREFIX + i);
            st.setParent(currentStore);
            currentStore = st;
        }
        store = new DefaultBeanStore("TestStore", currentStore);
        dependentProvider = ConstantBeanProvider
                .getInstance(new MyBeanSubClass());
    }

    /**
     * Cleans the test environment. If a dependency provider mock has been
     * created, verify() is called for it.
     */
    @Override
    protected void tearDown() throws Exception
    {
        if (dependencyProvider != null)
        {
            EasyMock.verify(dependencyProvider);
        }
        super.tearDown();
    }

    /**
     * Returns a mock for a dependency provider. This mock does not expect any
     * calls.
     *
     * @return a dependency provider mock object
     */
    private DependencyProvider getDependencyProvider()
    {
        if (dependencyProvider == null)
        {
            dependencyProvider = EasyMock.createMock(DependencyProvider.class);
            EasyMock.replay(dependencyProvider);
        }
        return dependencyProvider;
    }

    /**
     * Creates a bean store and populates it with dummy beans.
     *
     * @return the bean store
     */
    private DefaultBeanStore createStore()
    {
        DefaultBeanStore store = new DefaultBeanStore();
        for (int i = 0; i < BEAN_COUNT; i++)
        {
            store.addBeanProvider(NAME_PREFIX + i, ConstantBeanProvider
                    .getInstance(i));
        }
        return store;
    }

    /**
     * Helper method for checking the equals() and hashCode() implementations.
     *
     * @param o1 object 1 to compare
     * @param o2 object 2 to compare
     * @param expected the expected outcome of the comparison
     */
    private void checkEquals(Object o1, Object o2, boolean expected)
    {
        assertEquals("Wrong comparison result", expected, o1.equals(o2));
        if (o2 != null)
        {
            assertEquals("Not symmetric", expected, o2.equals(o1));
        }
        if (expected)
        {
            assertEquals("Different hash codes", o1.hashCode(), o2.hashCode());
        }
    }

    /**
     * Tests resolving a dependency by name that can be found in the current
     * store.
     */
    public void testResolveByNameDirectly()
    {
        store.addBeanProvider(BEAN_NAME, dependentProvider);
        assertSame("Name dependency cannot be resolved in same store",
                dependentProvider, NameDependency.getInstance(BEAN_NAME)
                        .resolve(store, getDependencyProvider()));
    }

    /**
     * Tests resolving a dependency by name that can be found in a parent store.
     */
    public void testResolveByNameParent()
    {
        parentStore.addBeanProvider(BEAN_NAME, dependentProvider);
        assertSame("Name dependency cannot be resolved in parent store",
                dependentProvider, NameDependency.getInstance(BEAN_NAME)
                        .resolve(store, getDependencyProvider()));
    }

    /**
     * Tests whether the hierarchy of stores is searched in the correct order.
     */
    public void testResolveByNameCurrentFirst()
    {
        store.addBeanProvider(BEAN_NAME, dependentProvider);
        parentStore.addBeanProvider(BEAN_NAME, ConstantBeanProvider
                .getInstance("Another bean"));
        assertSame("Name dependency not searched in correct order",
                dependentProvider, NameDependency.getInstance(BEAN_NAME)
                        .resolve(store, getDependencyProvider()));
    }

    /**
     * Tests resolving a non existing name dependency.
     */
    public void testResolveByNameNonEx()
    {
        try
        {
            NameDependency.getInstance(BEAN_NAME).resolve(store,
                    getDependencyProvider());
            fail("Could resolve non existing dependency!");
        }
        catch (InjectionException iex)
        {
            // ok
        }
    }

    /**
     * Tests the equals() method for name dependencies.
     */
    public void testNameDependencyEquals()
    {
        NameDependency d1 = NameDependency.getInstance(BEAN_NAME);
        checkEquals(d1, d1, true);
        NameDependency d2 = NameDependency.getInstance(null);
        checkEquals(d2, d2, true);
        checkEquals(d1, d2, false);
        d2 = NameDependency.getInstance(BEAN_NAME.toLowerCase());
        checkEquals(d1, d2, false);
        d2 = NameDependency.getInstance(BEAN_NAME);
        checkEquals(d1, d2, true);
        checkEquals(d1, null, false);
        checkEquals(d1, "Wrong class", false);
    }

    /**
     * Tests the toString() implementation for name dependencies. We test
     * whether the name is contained in the string.
     */
    public void testNameDependencyToString()
    {
        NameDependency d = NameDependency.getInstance(BEAN_NAME);
        String s = d.toString();
        assertTrue("Bean name not found in string " + s,
                s.indexOf(BEAN_NAME) >= 0);
    }

    /**
     * Tests resolving a dependency by class that can be found directly in the
     * current store.
     */
    public void testResolveByClassDirectly()
    {
        store.addBeanProvider(BEAN_NAME, dependentProvider);
        assertSame("Class dependency cannot be resolved in same store",
                dependentProvider, ClassDependency.getInstance(
                        MyBeanClass.class).resolve(store,
                        getDependencyProvider()));
    }

    /**
     * Tests resolving a dependency by class when a provider with a sub class
     * can be found in the same store.
     */
    public void testResolveByClassDirectlySubClass()
    {
        store.addBeanProvider(BEAN_NAME, dependentProvider);
        assertSame(
                "Class dependency cannot be resolved for sub class in same store",
                dependentProvider, ClassDependency.getInstance(
                        MyBeanClass.class).resolve(store,
                        getDependencyProvider()));
    }

    /**
     * Tests resolving a dependency by class that lives in a parent store.
     */
    public void testResolveByClassParent()
    {
        parentStore.addBeanProvider(BEAN_NAME, dependentProvider);
        assertSame("Class dependency cannot be resolved in parent store",
                dependentProvider, ClassDependency.getInstance(
                        MyBeanClass.class).resolve(store,
                        getDependencyProvider()));
    }

    /**
     * Tests whether the current store is searched first when resolving class
     * dependencies.
     */
    public void testResolveByClassCurrentFirst()
    {
        store.addBeanProvider(BEAN_NAME, dependentProvider);
        parentStore.addBeanProvider("AnotherTestBean", ConstantBeanProvider
                .getInstance(new MyBeanClass()));
        assertSame("Incorrect search order for class dependency",
                dependentProvider, ClassDependency.getInstance(
                        MyBeanClass.class).resolve(store,
                        getDependencyProvider()));
    }

    /**
     * Tests resolving a class dependency that is defined using a class
     * description.
     */
    public void testResolveByClassWithDescription()
    {
        DependencyProvider depProvider = EasyMock
                .createMock(DependencyProvider.class);
        final String loaderName = "TestClassLoader";
        depProvider.loadClass(MyBeanClass.class.getName(), loaderName);
        EasyMock.expectLastCall().andReturn(MyBeanClass.class);
        EasyMock.replay(depProvider);
        store.addBeanProvider(BEAN_NAME, dependentProvider);
        ClassDescription cd = ClassDescription.getInstance(MyBeanClass.class
                .getName(), loaderName);
        ClassDependency dep = ClassDependency.getInstance(cd);
        assertSame("Wrong bean provider", dependentProvider, dep.resolve(store,
                depProvider));
        EasyMock.verify(depProvider);
    }

    /**
     * Tests resolving a class dependency that does not exist. This should cause
     * an exception.
     */
    public void testResolveByClassNonEx()
    {
        store.addBeanProvider(BEAN_NAME, ConstantBeanProvider
                .getInstance(new MyBeanClass()));
        try
        {
            ClassDependency.getInstance(MyBeanSubClass.class).resolve(store,
                    getDependencyProvider());
            fail("Could resolve non existing class dependency!");
        }
        catch (InjectionException iex)
        {
            // ok
        }
    }

    /**
     * Tests requesting an instance for a class dependency with a null class.
     * This should cause an exception.
     */
    public void testClassDependencyNullInstance()
    {
        try
        {
            ClassDependency.getInstance((Class<?>) null);
            fail("Could obtain class dependency for a null class!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tests requesting an instance for a class dependency with a null
     * description. This should cause an exception.
     */
    public void testClassDependencyNullDescInstance()
    {
        try
        {
            ClassDependency.getInstance((ClassDescription) null);
            fail("Could obtain class dependency for a null description!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tests the equals() and hashCode() implementations for class dependencies.
     */
    public void testClassDependencyEquals()
    {
        ClassDependency d1 = ClassDependency.getInstance(MyBeanClass.class);
        checkEquals(d1, d1, true);
        ClassDependency d2 = ClassDependency.getInstance(MyBeanSubClass.class);
        checkEquals(d1, d2, false);
        d2 = ClassDependency.getInstance(MyBeanClass.class);
        checkEquals(d1, d2, true);
        checkEquals(d1, null, false);
        checkEquals(d1, "Invalid Object", false);
    }

    /**
     * Tests the toString() implementation of class dependencies. We only test
     * whether the name of the dependent class can be found in the string.
     */
    public void testClassDependencyToString()
    {
        ClassDependency d = ClassDependency.getInstance(MyBeanClass.class);
        String s = d.toString();
        assertTrue("Class not found in string " + s, s
                .indexOf(MyBeanClass.class.getName()) >= 0);
    }

    /**
     * A dummy class used for testing class dependencies.
     */
    public static class MyBeanClass
    {
    }

    /**
     * Another dummy class for testing whether derived classes are also found.
     */
    public static class MyBeanSubClass extends MyBeanClass
    {
    }
}
