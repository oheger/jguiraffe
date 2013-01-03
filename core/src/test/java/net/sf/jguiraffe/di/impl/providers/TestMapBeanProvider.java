/*
 * Copyright 2006-2012 The JGUIraffe Team.
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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class of MapBeanProvider.
 *
 * @author Oliver Heger
 * @version $Id: TestMapBeanProvider.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestMapBeanProvider
{
    /** Constant for the number of dependencies. */
    private static final int DEP_COUNT = 12;

    /** Constant for the prefix of a key dependency. */
    private static final String KEY_PREFIX = "key";

    /**
     * Returns the key dependency with the given index. This is a constant bean
     * of type string whose value consists of a prefix and the index.
     *
     * @param idx the index
     * @return the key dependency for this index
     */
    private static ConstantBeanProvider keyDependency(int idx)
    {
        return ConstantBeanProvider.getInstance(KEY_PREFIX + idx);
    }

    /**
     * Returns the value dependency with the given index. This is a constant
     * bean of type integer whose value is the index.
     *
     * @param idx the index
     * @return the value dependency for this index
     */
    private static ConstantBeanProvider valDependency(int idx)
    {
        return ConstantBeanProvider.getInstance(idx);
    }

    /**
     * Returns a collection with the key dependencies.
     *
     * @return the key dependencies
     */
    private static Collection<Dependency> keyDependencies()
    {
        Collection<Dependency> result = new ArrayList<Dependency>(DEP_COUNT);
        for (int i = 0; i < DEP_COUNT; i++)
        {
            result.add(keyDependency(i));
        }
        return result;
    }

    /**
     * Returns a collection with the value dependencies.
     *
     * @return the value dependencies
     */
    private static Collection<Dependency> valDependencies()
    {
        Collection<Dependency> result = new ArrayList<Dependency>(DEP_COUNT);
        for (int i = 0; i < DEP_COUNT; i++)
        {
            result.add(valDependency(i));
        }
        return result;
    }

    /**
     * Creates a provider with default values.
     *
     * @return the provider
     */
    private MapBeanProvider setUpProvider()
    {
        return new MapBeanProvider(keyDependencies(), valDependencies(), false);

    }

    /**
     * Tests creating an instance when no key dependencies are provided. This
     * should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoKeyDeps()
    {
        new MapBeanProvider(null, valDependencies(), false);
    }

    /**
     * Tests creating an instance when no value dependencies are provided. This
     * should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoValDeps()
    {
        new MapBeanProvider(keyDependencies(), null, false);
    }

    /**
     * Tests creating an instance when the numbers of the key and value
     * dependencies differ. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitDifferentSizes()
    {
        Collection<Dependency> keyDeps = keyDependencies();
        keyDeps.add(keyDependency(DEP_COUNT));
        new MapBeanProvider(keyDeps, valDependencies(), false);
    }

    /**
     * Compares two dependency collections.
     *
     * @param expected the expected dependencies
     * @param actual the actual values
     */
    private void checkDependencies(Collection<Dependency> expected,
            Collection<Dependency> actual)
    {
        Set<Object> values = new HashSet<Object>();
        assertEquals("Wrong size", expected.size(), actual.size());
        for (Dependency d : expected)
        {
            values.add(((ConstantBeanProvider) d).getBean());
        }
        for (Dependency d : actual)
        {
            assertTrue("Dependency not found: " + d, values
                    .contains(((ConstantBeanProvider) d).getBean()));
        }
    }

    /**
     * Tests whether the key dependencies are correctly set.
     */
    @Test
    public void testInitKeyDependencies()
    {
        MapBeanProvider provider = setUpProvider();
        checkDependencies(keyDependencies(), provider.getKeyDependencies());
    }

    /**
     * Tests whether the value dependencies are correctly set.
     */
    @Test
    public void testInitValDependencies()
    {
        MapBeanProvider provider = setUpProvider();
        checkDependencies(valDependencies(), provider.getValueDependencies());
    }

    /**
     * Tests that the key dependencies are immutable.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetKeyDependenciesModify()
    {
        MapBeanProvider provider = setUpProvider();
        provider.getKeyDependencies().clear();
    }

    /**
     * Tests that the value dependencies are immutable.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetValueDependenciesModify()
    {
        MapBeanProvider provider = setUpProvider();
        provider.getValueDependencies().clear();
    }

    /**
     * Tests querying the bean class.
     */
    @Test
    public void testGetBeanClass()
    {
        DependencyProvider depProvider = EasyMock
                .createMock(DependencyProvider.class);
        EasyMock.replay(depProvider);
        assertEquals("Wrong bean class", Map.class, setUpProvider()
                .getBeanClass(depProvider));
        EasyMock.verify(depProvider);
    }

    /**
     * Tests querying the dependencies of the bean provider.
     */
    @Test
    public void testGetDependencies()
    {
        MapBeanProvider provider = setUpProvider();
        Set<Dependency> deps = provider.getDependencies();
        Collection<Dependency> expDeps = new ArrayList<Dependency>(
                2 * DEP_COUNT);
        expDeps.addAll(keyDependencies());
        expDeps.addAll(valDependencies());
        checkDependencies(expDeps, deps);
    }

    /**
     * Prepares a dependency provider mock to expect a call to getBean().
     *
     * @param depProvider the provider mock
     * @param keyDeps the key dependencies
     * @param valDeps the value dependencies
     */
    private void prepareDependencyProvider(DependencyProvider depProvider,
            Collection<Dependency> keyDeps, Collection<Dependency> valDeps)
    {
        Collection<Dependency> allDeps = new ArrayList<Dependency>(
                2 * DEP_COUNT);
        allDeps.addAll(keyDeps);
        allDeps.addAll(valDeps);
        for (Dependency d : allDeps)
        {
            ConstantBeanProvider cp = (ConstantBeanProvider) d;
            EasyMock.expect(depProvider.getDependentBean(d)).andReturn(
                    cp.getBean());
        }
    }

    /**
     * Helper method for testing the getBean() implementation.
     *
     * @param ordered the ordered flag
     * @return the map bean created by the provider
     */
    private Map<?, ?> checkGetBean(boolean ordered)
    {
        Collection<Dependency> keyDeps = keyDependencies();
        Collection<Dependency> valDeps = valDependencies();
        MapBeanProvider provider = new MapBeanProvider(keyDeps, valDeps,
                ordered);
        DependencyProvider depProvider = EasyMock
                .createMock(DependencyProvider.class);
        prepareDependencyProvider(depProvider, keyDeps, valDeps);
        EasyMock.replay(depProvider);
        Map<?, ?> map = (Map<?, ?>) provider.getBean(depProvider);
        EasyMock.verify(depProvider);
        Set<?> keys = map.keySet();
        Iterator<Dependency> itVal = valDeps.iterator();
        for (Dependency d : keyDeps)
        {
            Object key = ((ConstantBeanProvider) d).getBean();
            assertTrue("Key not found: " + d, keys.contains(key));
            assertEquals("Wrong value for " + d, ((ConstantBeanProvider) itVal
                    .next()).getBean(), map.get(key));
        }
        return map;
    }

    /**
     * Tests querying the bean from a default map provider.
     */
    @Test
    public void testGetBean()
    {
        Map<?, ?> map = checkGetBean(false);
        assertTrue("Wrong class of map: " + map, map instanceof HashMap<?, ?>);
    }

    /**
     * Tests querying the bean from a provider with the ordered flag set.
     */
    @Test
    public void testGetBeanOrdered()
    {
        Map<?, ?> map = checkGetBean(true);
        assertTrue("Wrong class of map: " + map, map instanceof LinkedHashMap<?, ?>);
        int idx = 0;
        for (Object o : map.keySet())
        {
            assertEquals("Wrong key at " + idx, keyDependency(idx++).getBean(),
                    o);
        }
    }

    /**
     * Tests querying the bean multiple times. Each call to getBean() should
     * cause a new map to be created.
     */
    @Test
    public void testGetBeanTwice()
    {
        Collection<Dependency> keyDeps = keyDependencies();
        Collection<Dependency> valDeps = valDependencies();
        DependencyProvider depProvider = EasyMock
                .createMock(DependencyProvider.class);
        prepareDependencyProvider(depProvider, keyDeps, valDeps);
        prepareDependencyProvider(depProvider, keyDeps, valDeps);
        EasyMock.replay(depProvider);
        MapBeanProvider provider = new MapBeanProvider(keyDeps, valDeps, false);
        Map<?, ?> map1 = (Map<?, ?>) provider.getBean(depProvider);
        Map<?, ?> map2 = (Map<?, ?>) provider.getBean(depProvider);
        assertNotSame("Only one instance created", map1, map2);
        assertEquals("Wrong size of map 1", DEP_COUNT, map1.size());
        assertEquals("Wrong size of map 2", DEP_COUNT, map2.size());
        EasyMock.verify(depProvider);
    }
}
