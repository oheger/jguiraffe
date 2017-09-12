/*
 * Copyright 2006-2017 The JGUIraffe Team.
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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import net.sf.jguiraffe.di.Dependency;

import org.junit.Test;

/**
 * Test class for PropertiesBeanProvider.
 *
 * @author Oliver Heger
 * @version $Id: TestPropertiesBeanProvider.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestPropertiesBeanProvider
{
    /** Constant for the prefix for key dependencies. */
    private static final String KEY_PREFIX = "key";

    /** Constant for the prefix for value dependencies. */
    private static final String VAL_PREFIX = "value";

    /** Constant for the number of dependencies. */
    private static final int DEP_COUNT = 16;

    /**
     * Creates a collection with test dependencies.
     *
     * @param prefix the prefix for the dependencies
     * @return the collection with constant test dependencies
     */
    private Collection<Dependency> setUpDependencies(String prefix)
    {
        List<Dependency> res = new ArrayList<Dependency>(DEP_COUNT);
        for (int i = 0; i < DEP_COUNT; i++)
        {
            res.add(ConstantBeanProvider.getInstance(prefix + i));
        }
        return res;
    }

    /**
     * Tests a collection with dependencies. The method expects constant
     * dependencies that are sorted by its index.
     *
     * @param deps the dependencies to test
     * @param prefix the expected prefix
     */
    private void checkDependencies(Collection<Dependency> deps, String prefix)
    {
        int index = 0;
        for (Dependency d : deps)
        {
            ConstantBeanProvider provider = (ConstantBeanProvider) d;
            assertEquals("Wrong value at " + index, prefix + index, provider
                    .getBean());
            index++;
        }
    }

    /**
     * Tests creating an instance without specifying key dependencies. This
     * should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoKeys()
    {
        new PropertiesBeanProvider(null, setUpDependencies(VAL_PREFIX));
    }

    /**
     * Tests creating an instance without specifying value dependencies. This
     * should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoValues()
    {
        new PropertiesBeanProvider(setUpDependencies(KEY_PREFIX), null);
    }

    /**
     * Tests creating an instance when the collection have different sizes. This
     * should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitDifferentSizes()
    {
        Collection<Dependency> keyDeps = setUpDependencies(KEY_PREFIX);
        keyDeps.add(ConstantBeanProvider.getInstance(KEY_PREFIX));
        new PropertiesBeanProvider(keyDeps, setUpDependencies(VAL_PREFIX));
    }

    /**
     * Tests creating an instance.
     */
    @Test
    public void testInit()
    {
        PropertiesBeanProvider provider = new PropertiesBeanProvider(
                setUpDependencies(KEY_PREFIX), setUpDependencies(VAL_PREFIX));
        checkDependencies(provider.getKeyDependencies(), KEY_PREFIX);
        checkDependencies(provider.getValueDependencies(), VAL_PREFIX);
        assertFalse("Ordered flag is set", provider.isOrdered());
    }

    /**
     * Tests creating a map.
     */
    @Test
    public void testCreateMap()
    {
        PropertiesBeanProvider provider = new PropertiesBeanProvider(
                setUpDependencies(KEY_PREFIX), setUpDependencies(VAL_PREFIX));
        Properties props = (Properties) provider.createMap();
        assertTrue("Properties not empty", props.isEmpty());
    }
}
