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
package net.sf.jguiraffe.gui.builder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.impl.DefaultBeanStore;

import org.apache.commons.jelly.JellyContext;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for JellyContextBeanStore.
 *
 * @author Oliver Heger
 * @version $Id: TestJellyContextBeanStore.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestJellyContextBeanStore
{
    /** Constant for the number of variables in the test. */
    private static final int VAR_COUNT = 12;

    /** Constant for the prefix of variables. */
    private static final String VAR_PREFIX = "var";

    /** Stores the Jelly context. */
    private JellyContext context;

    /** Stores the parent store. */
    private BeanStore parentStore;

    /** The bean store to be tested. */
    private JellyContextBeanStore store;

    @Before
    public void setUp() throws Exception
    {
        context = setUpContext();
        parentStore = new DefaultBeanStore();
        store = new JellyContextBeanStore(context, parentStore);
    }

    /**
     * Creates a Jelly context to be used for testing and populates it with some
     * variable values.
     *
     * @return the initialized context
     */
    private JellyContext setUpContext()
    {
        JellyContext ctx = new JellyContext();
        for (int i = 0; i < VAR_COUNT; i++)
        {
            ctx.setVariable(VAR_PREFIX + i, Integer.valueOf(i));
        }
        return ctx;
    }

    /**
     * Tests whether a default store name is set.
     */
    @Test
    public void testGetDefaultStoreName()
    {
        assertEquals("No default name set", JellyContextBeanStore.DEFAULT_NAME,
                store.getName());
    }

    /**
     * Tests whether the parent is correctly set.
     */
    @Test
    public void testGetParent()
    {
        assertSame("Wrong parent set", parentStore, store.getParent());
    }

    /**
     * Tests whether the expected provider names are returned.
     */
    @Test
    public void testProviderNames()
    {
        Set<String> names = store.providerNames();
        assertTrue("Wrong number of provider names", names.size() >= VAR_COUNT);
        for (int i = 0; i < VAR_COUNT; i++)
        {
            assertTrue("Provider name not found: " + i, names
                    .contains(VAR_PREFIX + i));
        }
    }

    /**
     * Tests querying (existing) bean providers.
     */
    @Test
    public void testGetBeanProvider()
    {
        DependencyProvider depProvider = EasyMock
                .createMock(DependencyProvider.class);
        EasyMock.replay(depProvider);
        for (int i = 0; i < VAR_COUNT; i++)
        {
            BeanProvider provider = store.getBeanProvider(VAR_PREFIX + i);
            assertNotNull("Bean provider not found: " + i, provider);
            Object bean = provider.getBean(depProvider);
            assertEquals("Wrong bean of provider " + i, Integer.valueOf(i),
                    bean);
        }
        EasyMock.verify(depProvider);
    }

    /**
     * Tests querying a non existing bean provider. Result should be null.
     */
    @Test
    public void testGetBeanProviderNonExisting()
    {
        assertNull("Wrong result for non existing bean provider", store
                .getBeanProvider("nonExistingProvider"));
    }

    /**
     * Tests whether a store name is correctly set.
     */
    @Test
    public void testInitWithName()
    {
        final String storeName = "MySpecialStoreName";
        store = new JellyContextBeanStore(context, parentStore, storeName);
        assertEquals("Store name was not set", storeName, store.getName());
    }

    /**
     * Tests the conversion helper returned by the bean store. There should not
     * be one.
     */
    @Test
    public void testGetConversionHelper()
    {
        assertNull("Got a conversion helper", store.getConversionHelper());
    }

    /**
     * Tries creating an instance without passing a Jelly context. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNullContext()
    {
        new JellyContextBeanStore(null, parentStore);
    }
}
