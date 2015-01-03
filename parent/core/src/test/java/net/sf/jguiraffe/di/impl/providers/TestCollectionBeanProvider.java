/*
 * Copyright 2006-2015 The JGUIraffe Team.
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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for CollectionBeanProvider. Concrete implementations are tested as
 * well.
 *
 * @author Oliver Heger
 * @version $Id: TestCollectionBeanProvider.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestCollectionBeanProvider
{
    /** Constant for the number of test dependencies. */
    private static final int DEP_COUNT = 12;

    /**
     * Creates a collection with mock dependencies.
     *
     * @return the collection with the mocks
     */
    private Collection<Dependency> setUpDependencies()
    {
        Collection<Dependency> deps = new ArrayList<Dependency>(DEP_COUNT);
        for (int i = 0; i < DEP_COUNT; i++)
        {
            Dependency dep = EasyMock.createMock(Dependency.class);
            deps.add(dep);
        }
        return deps;
    }

    /**
     * Creates a test provider instance with a collection of mock dependencies.
     *
     * @return the provider
     */
    private CollectionBeanProviderTestImpl setUpProvider()
    {
        return new CollectionBeanProviderTestImpl(setUpDependencies());
    }

    /**
     * Tests creating an instance with a null dependency collection. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNull()
    {
        new CollectionBeanProviderTestImpl(null);
    }

    /**
     * Helper method for checking the element dependencies.
     *
     * @param provider the provider
     * @param deps the expected dependencies
     */
    private void checkElementDependencies(CollectionBeanProvider provider,
            Collection<Dependency> deps)
    {
        Collection<Dependency> providerDeps = provider.getElementDependencies();
        assertEquals("Wrong number of dependencies", deps.size(), providerDeps
                .size());
        Iterator<Dependency> it = providerDeps.iterator();
        for (Dependency d : deps)
        {
            assertEquals("Wrong dependency", d, it.next());
        }
    }

    /**
     * Tests querying the dependencies for the collection elements.
     */
    @Test
    public void testGetElementDependencies()
    {
        Collection<Dependency> deps = setUpDependencies();
        CollectionBeanProviderTestImpl provider = new CollectionBeanProviderTestImpl(
                deps);
        checkElementDependencies(provider, deps);
    }

    /**
     * Tries to modify the dependency collection. This should not be allowed.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetElementDependenciesModify()
    {
        Collection<Dependency> deps = setUpDependencies();
        CollectionBeanProviderTestImpl provider = new CollectionBeanProviderTestImpl(
                deps);
        provider.getElementDependencies().clear();
    }

    /**
     * Tests querying the class of the bean.
     */
    @Test
    public void testGetBeanClass()
    {
        CollectionBeanProviderTestImpl provider = setUpProvider();
        DependencyProvider depProvider = EasyMock
                .createMock(DependencyProvider.class);
        EasyMock.replay(depProvider);
        assertEquals("Wrong bean class", Collection.class, provider
                .getBeanClass(depProvider));
        EasyMock.verify(depProvider);
    }

    /**
     * Tests querying the dependencies.
     */
    @Test
    public void testGetDependencies()
    {
        Collection<Dependency> deps = setUpDependencies();
        CollectionBeanProviderTestImpl provider = new CollectionBeanProviderTestImpl(
                deps);
        Set<Dependency> providerDeps = provider.getDependencies();
        assertEquals("Wrong number of dependencies", deps.size(), providerDeps
                .size());
        for (Dependency d : deps)
        {
            assertTrue("Dependency not found: " + d, providerDeps.contains(d));
        }
    }

    /**
     * Initializes a dependency provider mock to expect the dependencies of the
     * passed in collection. The dependencies are "resolved" by integer objects.
     *
     * @param depProvider the dependency provider mock
     * @param deps the dependencies
     */
    private void setUpDependencyProvider(DependencyProvider depProvider,
            Collection<Dependency> deps)
    {
        int index = 0;
        for (Dependency d : deps)
        {
            EasyMock.expect(depProvider.getDependentBean(d)).andReturn(index);
            index++;
        }
    }

    /**
     * Creates a dependency provider mock and initializes it to expect the
     * dependencies in the collection to be resolved.
     *
     * @param deps the collection with the dependencies
     * @return the dependency provider mock (not replayed)
     */
    private DependencyProvider setUpDependencyProvider(
            Collection<Dependency> deps)
    {
        DependencyProvider depProvider = EasyMock
                .createMock(DependencyProvider.class);
        setUpDependencyProvider(depProvider, deps);
        return depProvider;
    }

    /**
     * Tests querying the bean from the provider.
     */
    @Test
    public void testGetBean()
    {
        Collection<Dependency> deps = setUpDependencies();
        DependencyProvider depProvider = setUpDependencyProvider(deps);
        EasyMock.replay(depProvider);
        CollectionBeanProviderTestImpl provider = new CollectionBeanProviderTestImpl(
                deps);
        assertSame("Wrong bean", provider.col, provider.getBean(depProvider));
        assertEquals("Wrong number of elements", DEP_COUNT, provider.col.size());
        int index = 0;
        for (Object o : provider.col)
        {
            assertEquals("Wrong element at " + index, Integer.valueOf(index), o);
            index++;
        }
        EasyMock.verify(depProvider);
    }

    /**
     * Tests querying the bean multiple time. Each call to getBean() should
     * cause createCollection() to be called and the collection to be populated.
     */
    @Test
    public void testGetBeanTwice()
    {
        Collection<Dependency> deps = setUpDependencies();
        DependencyProvider depProvider = setUpDependencyProvider(deps);
        setUpDependencyProvider(depProvider, deps);
        EasyMock.replay(depProvider);
        CollectionBeanProviderTestImpl provider = new CollectionBeanProviderTestImpl(
                deps);
        assertSame("Wrong bean (1)", provider.col, provider
                .getBean(depProvider));
        assertSame("Wrong bean (2)", provider.col, provider
                .getBean(depProvider));
        assertEquals("Wrong number of elements", 2 * DEP_COUNT, provider.col
                .size());
        EasyMock.verify(depProvider);
    }

    /**
     * Tests whether ListBeanProvider creates a correct collection.
     */
    @Test
    public void testListBeanProviderCreateCollection()
    {
        ListBeanProvider provider = new ListBeanProvider(setUpDependencies());
        Collection<Object> col = provider.createCollection(DEP_COUNT);
        assertTrue("Wrong collection class", col instanceof ArrayList<?>);
        assertTrue("Collection not empty", col.isEmpty());
    }

    /**
     * Tests whether the dependencies are correctly passed to the super class.
     */
    @Test
    public void testListBeanProviderGetElementDependencies()
    {
        Collection<Dependency> deps = setUpDependencies();
        ListBeanProvider provider = new ListBeanProvider(deps);
        checkElementDependencies(provider, deps);
    }

    /**
     * Tests whether the dependencies of a set provider are correctly passed to
     * the super class.
     */
    @Test
    public void testSetBeanProviderGetElementDependencies()
    {
        Collection<Dependency> deps = setUpDependencies();
        SetBeanProvider provider = new SetBeanProvider(deps, false);
        checkElementDependencies(provider, deps);
    }

    /**
     * Tests the collection created by SetBeanProvider if ordered is false.
     */
    @Test
    public void testSetBeanProviderCreateCollectionNotOrdered()
    {
        SetBeanProvider provider = new SetBeanProvider(setUpDependencies(),
                false);
        assertFalse("Wrong ordered property", provider.isOrdered());
        Collection<Object> col = provider.createCollection(DEP_COUNT);
        assertTrue("Wrong collection class", col instanceof HashSet<?>);
        assertTrue("Collection not empty", col.isEmpty());
    }

    /**
     * Tests the collection created by SetBeanProvider if ordered is true.
     */
    @Test
    public void testSetBeanProviderCreateCollectionOrdered()
    {
        SetBeanProvider provider = new SetBeanProvider(setUpDependencies(),
                true);
        assertTrue("Wrong ordered property", provider.isOrdered());
        Collection<Object> col = provider.createCollection(DEP_COUNT);
        assertTrue("Wrong collection class", col instanceof LinkedHashSet<?>);
        assertTrue("Collection not empty", col.isEmpty());
    }

    /**
     * A concrete test implementation of CollectionBeanProvider. This
     * implementation simply returns a fixed collection in createCollection().
     */
    private static class CollectionBeanProviderTestImpl extends
            CollectionBeanProvider
    {
        /** The collection used by this test implementation. */
        Collection<Object> col = new ArrayList<Object>();

        public CollectionBeanProviderTestImpl(Collection<Dependency> deps)
        {
            super(deps);
        }

        @Override
        protected Collection<Object> createCollection(int size)
        {
            return col;
        }
    }
}
