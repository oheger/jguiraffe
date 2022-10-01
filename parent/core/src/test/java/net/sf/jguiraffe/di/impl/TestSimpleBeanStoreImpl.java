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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.ConversionHelper;
import net.sf.jguiraffe.di.impl.providers.ConstantBeanProvider;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for SimpleBeanStoreImpl.
 *
 * @author Oliver Heger
 * @version $Id: TestSimpleBeanStoreImpl.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSimpleBeanStoreImpl
{
    /** Constant for the name of a test bean. */
    private static final String BEAN_NAME = "TestBean";

    /** Stores the bean store to be tested. */
    private SimpleBeanStoreImpl store;

    @Before
    public void setUp() throws Exception
    {
        store = new SimpleBeanStoreImpl();
    }

    /**
     * Tests a newly created object.
     */
    @Test
    public void testInit()
    {
        assertNull("A parent is set", store.getParent());
        assertNull("A name is set", store.getName());
        assertTrue("Already beans available", store.providerNames().isEmpty());
        assertNull("Got a ConversionHelper", store.getConversionHelper());
    }

    /**
     * Tests whether constructor arguments are correctly stored.
     */
    @Test
    public void testInitArguments()
    {
        BeanStore parent = EasyMock.createMock(BeanStore.class);
        EasyMock.replay(parent);
        final String name = "MyStore";
        store = new SimpleBeanStoreImpl(name, parent);
        assertEquals("Wrong parent store", parent, store.getParent());
        assertEquals("Wrong name", name, store.getName());
        EasyMock.verify(parent);
    }

    /**
     * Tests whether a conversion helper can be set.
     */
    @Test
    public void testSetConversionHelper()
    {
        ConversionHelper ch = new ConversionHelper();
        store.setConversionHelper(ch);
        assertSame("Wrong conversion helper", ch, store.getConversionHelper());
    }

    /**
     * Tests adding a bean when the name is null. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddBeanNullName()
    {
        store.addBean(null, this);
    }

    /**
     * Tests adding a null bean. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddBeanNullBean()
    {
        store.addBean(BEAN_NAME, null);
    }

    /**
     * Tests adding a bean.
     */
    @Test
    public void testAddBean()
    {
        store.addBean(BEAN_NAME, this);
        Set<String> names = store.providerNames();
        assertEquals("Wrong number of beans", 1, names.size());
        assertTrue("Bean not found", names.contains(BEAN_NAME));
        ConstantBeanProvider provider = (ConstantBeanProvider) store
                .getBeanProvider(BEAN_NAME);
        assertNotNull("No provider for test bean", provider);
        assertSame("Wrong bean returned", this, provider.getBean());
    }

    /**
     * Tests removing a bean from the store.
     */
    @Test
    public void testRemoveBean()
    {
        store.addBean(BEAN_NAME, this);
        assertSame("Wrong result of removeBean", this, store
                .removeBean(BEAN_NAME));
        assertTrue("Bean name still found", store.providerNames().isEmpty());
    }

    /**
     * Tests removing a non-existing bean.
     */
    @Test
    public void testRemoveBeanNonExisting()
    {
        assertNull("Wrong result for removing non existing bean", store
                .removeBean(BEAN_NAME));
    }

    /**
     * Tests requesting the names of the available providers.
     */
    @Test
    public void testProviderNames()
    {
        final int count = 10;
        for (int i = 0; i < count; i++)
        {
            store.addBean(BEAN_NAME + i, Integer.valueOf(i));
        }
        store.addBeanContributor(new SimpleBeanStoreImpl.BeanContributor()
        {
            public void beanNames(Set<String> names)
            {
                // return some test bean names
                for (int i = 0; i < count; i++)
                {
                    names.add(BEAN_NAME + (count + i));
                }
            }

            public Object getBean(String name)
            {
                // should not be called
                throw new UnsupportedOperationException("Not implemented!");
            }

        });

        Set<String> names = store.providerNames();
        assertEquals("Wrong number of names", 2 * count, names.size());
        for (int i = 0; i < 2 * count; i++)
        {
            String name = BEAN_NAME + i;
            assertTrue("Bean name not found: " + name, names.contains(name));
        }
    }

    /**
     * Tests the getBeanProvider() method when the bean can be retrieved from
     * the internal map.
     */
    @Test
    public void testGetBeanProviderFromStore()
    {
        SimpleBeanStoreImpl.BeanContributor contr = EasyMock
                .createMock(SimpleBeanStoreImpl.BeanContributor.class);
        EasyMock.replay(contr);
        store.addBean(BEAN_NAME, this);
        store.addBeanContributor(contr);
        ConstantBeanProvider provider = (ConstantBeanProvider) store
                .getBeanProvider(BEAN_NAME);
        assertSame("Wrong bean returned", this, provider.getBean());
        EasyMock.verify(contr);
    }

    /**
     * Tests the getBeanProvider() method when the bean must be obtained from a
     * contributor.
     */
    @Test
    public void testGetBeanProviderFromContributor()
    {
        SimpleBeanStoreImpl.BeanContributor contr1 = EasyMock
                .createMock(SimpleBeanStoreImpl.BeanContributor.class);
        SimpleBeanStoreImpl.BeanContributor contr2 = EasyMock
                .createMock(SimpleBeanStoreImpl.BeanContributor.class);
        SimpleBeanStoreImpl.BeanContributor contr3 = EasyMock
                .createMock(SimpleBeanStoreImpl.BeanContributor.class);
        EasyMock.expect(contr1.getBean(BEAN_NAME)).andReturn(null);
        EasyMock.expect(contr2.getBean(BEAN_NAME)).andReturn(this);
        EasyMock.replay(contr1, contr2, contr3);
        store.addBeanContributor(contr1);
        store.addBeanContributor(contr2);
        store.addBeanContributor(contr3);
        ConstantBeanProvider provider = (ConstantBeanProvider) store
                .getBeanProvider(BEAN_NAME);
        assertSame("Wrong bean returned", this, provider.getBean());
        EasyMock.verify(contr1, contr2, contr3);
    }

    /**
     * Tests querying an unknown bean provider.
     */
    @Test
    public void testGetBeanProviderUnknown()
    {
        SimpleBeanStoreImpl.BeanContributor contr = EasyMock
                .createMock(SimpleBeanStoreImpl.BeanContributor.class);
        EasyMock.expect(contr.getBean(BEAN_NAME)).andReturn(null);
        EasyMock.replay(contr);
        store.addBeanContributor(contr);
        assertNull("Wrong result for unknown provider", store
                .getBeanProvider(BEAN_NAME));
        EasyMock.verify(contr);
    }

    /**
     * Tests removing a bean contributor.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testRemoveBeanContributor()
    {
        SimpleBeanStoreImpl.BeanContributor contr = EasyMock
                .createMock(SimpleBeanStoreImpl.BeanContributor.class);
        contr.beanNames((Set<String>) EasyMock.anyObject());
        EasyMock.expect(contr.getBean(BEAN_NAME)).andReturn(null);
        EasyMock.replay(contr);
        store.addBeanContributor(contr);
        assertTrue("Found provider names (1)", store.providerNames().isEmpty());
        assertNull("Found provider (1)", store.getBeanProvider(BEAN_NAME));
        store.removeBeanContributor(contr);
        assertTrue("Found provider names (2)", store.providerNames().isEmpty());
        assertNull("Found provider (2)", store.getBeanProvider(BEAN_NAME));
        EasyMock.verify(contr);
    }

    /**
     * Tries adding a null bean contributor. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddBeanContributorNull()
    {
        store.addBeanContributor(null);
    }
}
