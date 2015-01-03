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
package net.sf.jguiraffe.di.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.ConversionHelper;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for DefaultBeanStore.
 *
 * @author Oliver Heger
 * @version $Id: TestDefaultBeanStore.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestDefaultBeanStore
{
    /** Constant for the parent bean store. */
    private static final BeanStore PARENT_STORE = new DefaultBeanStore();

    /** Constant for the name of the bean store. */
    private static final String STORE_NAME = "TestBeanStore";

    /** Constant for the name of a bean provider. */
    private static final String PROVIDER_NAME = "provider";

    /** Stores the bean store to be tested. */
    private DefaultBeanStore store;

    @Before
    public void setUp() throws Exception
    {
        store = new DefaultBeanStore(STORE_NAME, PARENT_STORE);
    }

    /**
     * Creates a provider mock object.
     *
     * @return the provider mock
     */
    private BeanProvider createProvider()
    {
        BeanProvider mock = EasyMock.createMock(BeanProvider.class);
        EasyMock.replay(mock);
        return mock;
    }

    /**
     * Tests the default constructor.
     */
    @Test
    public void testInitDefault()
    {
        assertNull("A parent is set", PARENT_STORE.getParent());
        assertNull("A name is set", PARENT_STORE.getName());
        assertTrue("Providers not empty", PARENT_STORE.providerNames()
                .isEmpty());
    }

    /**
     * Tests a newly created bean store.
     */
    @Test
    public void testInit()
    {
        assertEquals("Wrong name of store", STORE_NAME, store.getName());
        assertSame("Wrong parent", PARENT_STORE, store.getParent());
        assertTrue("Providers not empty", store.providerNames().isEmpty());
        assertNull("Provider found", store.getBeanProvider(PROVIDER_NAME));
        assertNull("Got a conversion helper", store.getConversionHelper());
    }

    /**
     * Tests adding bean providers to the test store.
     */
    @Test
    public void testAddBeanProvider()
    {
        final int count = 11;
        for (int i = 0; i < count; i++)
        {
            store.addBeanProvider(PROVIDER_NAME + i, createProvider());
        }
        Set<String> names = store.providerNames();
        assertEquals("Wrong number of names", count, names.size());
        for (int i = 0; i < count; i++)
        {
            String name = PROVIDER_NAME + i;
            assertTrue("Name not found " + name, names.contains(name));
            assertNotNull("Provider not found " + name, store
                    .getBeanProvider(name));
        }
    }

    /**
     * Tests adding a null provider. This is not allowed.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddBeanProviderNullProvider()
    {
        store.addBeanProvider(PROVIDER_NAME, null);
    }

    /**
     * Tests adding a provider with a null name. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddBeanProviderNullName()
    {
        store.addBeanProvider(null, createProvider());
    }

    /**
     * Tests querying an existing bean provider.
     */
    @Test
    public void testGetBeanProviderExisting()
    {
        BeanProvider prov = createProvider();
        store.addBeanProvider(PROVIDER_NAME, prov);
        assertSame("Wrong bean provider returned", prov, store
                .getBeanProvider(PROVIDER_NAME));
    }

    /**
     * Tests querying a non existing bean provider.
     */
    @Test
    public void testGetBeanProviderNonExisting()
    {
        store.addBeanProvider(PROVIDER_NAME + "test", createProvider());
        assertNull("Wrong result for non existing provider", store
                .getBeanProvider(PROVIDER_NAME));
    }

    /**
     * Tests removing an existing bean provider.
     */
    @Test
    public void testRemoveBeanProviderExisting()
    {
        final int count = 12;
        BeanProvider[] providers = new BeanProvider[count];
        for (int i = 0; i < count; i++)
        {
            providers[i] = createProvider();
            store.addBeanProvider(PROVIDER_NAME + i, providers[i]);
        }
        for (int i = 0; i < count; i++)
        {
            assertSame("Wrong removed provider returned", providers[i], store
                    .removeBeanProvider(PROVIDER_NAME + i));
            assertNull("Provider still found", store
                    .getBeanProvider(PROVIDER_NAME + i));
            assertEquals("Wrong size of providers", count - i - 1, store
                    .providerNames().size());
        }
    }

    /**
     * Tests removing a non existing bean provider.
     */
    @Test
    public void testRemoveBeanProviderNonExisting()
    {
        final int count = 5;
        for (int i = 0; i < count; i++)
        {
            store.addBeanProvider(PROVIDER_NAME + i, createProvider());
        }
        assertNull("Wrong result for removing non existent provider", store
                .removeBeanProvider(PROVIDER_NAME));
        assertEquals("Wrong number of providers", count, store.providerNames()
                .size());
    }

    /**
     * Tests removing all bean providers.
     */
    @Test
    public void testClear()
    {
        for (int i = 0; i < 20; i++)
        {
            store.addBeanProvider(PROVIDER_NAME + i, createProvider());
        }
        store.clear();
        assertEquals("Still providers found", 0, store.providerNames().size());
    }

    /**
     * Tests the string representation of a default bean store.
     */
    @Test
    public void testToString()
    {
        final int providerCount = 5;
        BeanProvider[] providers = new BeanProvider[providerCount];
        for (int i = 0; i < providerCount; i++)
        {
            providers[i] = createProvider();
            store.addBeanProvider(PROVIDER_NAME + i, providers[i]);
        }

        String s = store.toString();
        assertTrue("Store name not found: " + s, s.indexOf(STORE_NAME) >= 0);
        for (int i = 0; i < providerCount; i++)
        {
            assertTrue("Provider name " + i + " not found: " + s, s
                    .indexOf(PROVIDER_NAME + i) >= 0);
            assertTrue("Provider string " + i + " not found: " + s, s
                    .indexOf(providers[i].toString()) >= 0);
        }
        EasyMock.verify((Object[]) providers);
    }

    /**
     * Tests adding an anonymous bean provider.
     */
    @Test
    public void testAddAnonymousProvider()
    {
        BeanProvider p = createProvider();
        final int index = 42;
        String name = store.addAnonymousBeanProvider(index, p);
        assertTrue("Wrong name of provider", name.endsWith(String
                .valueOf(index)));
        assertEquals("Bean provider not found", p, store.getBeanProvider(name));
        EasyMock.verify(p);
    }

    /**
     * Tries adding an anonymous provider that is null. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddAnonymousProviderNull()
    {
        store.addAnonymousBeanProvider(1, null);
    }

    /**
     * Tests querying the names of registered providers if anonymous ones are
     * involved.
     */
    @Test
    public void testProviderNamesWithAnonymous()
    {
        final int count = 10;
        for (int i = 0; i < count; i++)
        {
            store.addBeanProvider(PROVIDER_NAME + i, createProvider());
            store.addAnonymousBeanProvider(i, createProvider());
        }
        Set<String> names = store.providerNames();
        assertEquals("Wrong number of names", count, names.size());
        for (int i = 0; i < count; i++)
        {
            assertTrue("Provider not found: " + i, names.contains(PROVIDER_NAME
                    + i));
        }
    }

    /**
     * Tests fetchConversionHelper() for null input if no instance has to be
     * created.
     */
    @Test
    public void testFetchConversionHelperNullNoCreate()
    {
        assertNull("Got an instance",
                DefaultBeanStore.fetchConversionHelper(null, false));
    }

    /**
     * Tests fetchConversionHelper() for null input if a default instance has to
     * be created.
     */
    @Test
    public void testFetchConversionHelperNullCreate()
    {
        assertNotNull("No instance",
                DefaultBeanStore.fetchConversionHelper(null, true));
    }

    /**
     * Tests fetchConversionHelper() if the passed in store has a helper.
     */
    @Test
    public void testFetchConversionHelperFoundDirectly()
    {
        ConversionHelper ch = new ConversionHelper();
        store.setConversionHelper(ch);
        assertSame("Wrong helper", ch,
                DefaultBeanStore.fetchConversionHelper(store, true));
    }

    /**
     * Tests fetchConversionHelper() if the helper is found somewhere in the
     * hierarchy.
     */
    @Test
    public void testFetchConversionHelperFoundInParents()
    {
        BeanStore parent1 = EasyMock.createMock(BeanStore.class);
        BeanStore parent2 = EasyMock.createMock(BeanStore.class);
        ConversionHelper ch = new ConversionHelper();
        EasyMock.expect(parent1.getConversionHelper()).andReturn(null);
        EasyMock.expect(parent1.getParent()).andReturn(parent2);
        EasyMock.expect(parent2.getConversionHelper()).andReturn(ch);
        EasyMock.replay(parent1, parent2);
        store.setParent(parent1);
        assertSame("Wrong helper", ch,
                DefaultBeanStore.fetchConversionHelper(store, true));
        EasyMock.verify(parent1, parent2);
    }

    /**
     * Tests fetchConversionHelper() if no helper can be found in the hierarchy.
     */
    @Test
    public void testFetchConversionHelperNotFound()
    {
        BeanStore parent1 = EasyMock.createMock(BeanStore.class);
        BeanStore parent2 = EasyMock.createMock(BeanStore.class);
        EasyMock.expect(parent1.getConversionHelper()).andReturn(null);
        EasyMock.expect(parent1.getParent()).andReturn(parent2);
        EasyMock.expect(parent2.getConversionHelper()).andReturn(null);
        EasyMock.expect(parent2.getParent()).andReturn(null);
        EasyMock.replay(parent1, parent2);
        store.setParent(parent1);
        assertNull("Got a helper",
                DefaultBeanStore.fetchConversionHelper(store, false));
        EasyMock.verify(parent1, parent2);
    }
}
