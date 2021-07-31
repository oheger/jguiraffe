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
package net.sf.jguiraffe.gui.builder.di;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.InvocationHelper;
import net.sf.jguiraffe.di.impl.DefaultBeanStore;
import net.sf.jguiraffe.di.impl.providers.ConstantBeanProvider;

import org.apache.commons.jelly.JellyContext;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for DIBuilderData.
 *
 * @author Oliver Heger
 * @version $Id: TestDIBuilderData.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestDIBuilderData
{
    /** Constant for the name of a bean store. */
    private static final String STORE_NAME = "myStore";

    /** Stores the builder data object to be tested. */
    private DIBuilderData builderData;

    @Before
    public void setUp() throws Exception
    {
        builderData = new DIBuilderData();
    }

    /**
     * Tests querying the class loader provider when none has been set.
     */
    @Test
    public void testGetClassLoaderProviderUndefined()
    {
        ClassLoaderProvider clp = builderData.getClassLoaderProvider();
        assertNotNull("No ClassLoaderProvider returned", clp);
        assertTrue("CLP contains registered loaders", clp.classLoaderNames()
                .isEmpty());
    }

    /**
     * Tests querying the class loader provider when one has been set.
     */
    @Test
    public void testGetClassLoaderProviderDefined()
    {
        ClassLoaderProvider clp = EasyMock
                .createMock(ClassLoaderProvider.class);
        EasyMock.replay(clp);
        builderData.setClassLoaderProvider(clp);
        assertSame("Wrong ClassLoaderProvider returned", clp, builderData
                .getClassLoaderProvider());
        EasyMock.verify(clp);
    }

    /**
     * Tests the get() method when the context does not contain an instance.
     */
    @Test
    public void testGetUndefined()
    {
        assertNull("Instance found", DIBuilderData.get(new JellyContext()));
    }

    /**
     * Tests the get() method when an instance was put into the context.
     */
    @Test
    public void testGetAfterPut()
    {
        JellyContext context = new JellyContext();
        builderData.put(context);
        assertSame("Wrong instance returned", builderData, DIBuilderData
                .get(context));
    }

    /**
     * Tests the return values of bean store related methods for a newly created
     * instance.
     */
    @Test
    public void testBeanStoresAfterInit()
    {
        assertNotNull("No root bean store set", builderData.getRootBeanStore());
        assertTrue("Already bean stores available", builderData
                .getBeanStoreNames().isEmpty());
        assertFalse("Found a bean store", builderData.hasBeanStore("test"));
    }

    /**
     * Tests adding a new bean store to the root store.
     */
    @Test
    public void testAddBeanStoreRoot()
    {
        builderData.addBeanStore(STORE_NAME, null);
        assertTrue("Bean store not present", builderData
                .hasBeanStore(STORE_NAME));
        assertEquals("Wrong number of bean stores", 1, builderData
                .getBeanStoreNames().size());
        assertTrue("Bean store name not found", builderData.getBeanStoreNames()
                .contains(STORE_NAME));
        BeanStore store = builderData.getBeanStore(STORE_NAME);
        assertNotNull("Bean store not found", store);
        assertSame("Wrong parent of bean store",
                builderData.getRootBeanStore(), store.getParent());
        assertEquals("Wrong name of bean store", STORE_NAME, store.getName());
    }

    /**
     * Tests adding a new bean store to an existing parent.
     */
    @Test
    public void testAddBeanStoreParent()
    {
        final String parentName = "storeParent";
        builderData.addBeanStore(parentName, null);
        BeanStore parent = builderData.getBeanStore(parentName);
        builderData.addBeanStore(STORE_NAME, parentName);
        Set<String> storeNames = builderData.getBeanStoreNames();
        assertEquals("Wrong number of bean stores", 2, storeNames.size());
        assertTrue("Parent store name not found", storeNames
                .contains(parentName));
        assertTrue("Store name not found", storeNames.contains(STORE_NAME));
        BeanStore store = builderData.getBeanStore(STORE_NAME);
        assertSame("Wrong parent store", parent, store.getParent());
    }

    /**
     * Tries adding a bean store with an undefined name. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddBeanStoreNullName()
    {
        builderData.addBeanStore(null, null);
    }

    /**
     * Tests adding a bean store with an existing name. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddBeanStoreNameDuplicate()
    {
        builderData.addBeanStore(STORE_NAME, null);
        builderData.addBeanStore(STORE_NAME, STORE_NAME);
    }

    /**
     * Tries adding a bean store to a non existing parent. This should cause an
     * exception.
     */
    @Test(expected = NoSuchElementException.class)
    public void testAddBeanStoreNonExistingParent()
    {
        builderData.addBeanStore(STORE_NAME, "non existing parent store");
    }

    /**
     * Tests initializing the root bean store.
     */
    @Test
    public void testInitRootStore()
    {
        DefaultBeanStore root = new DefaultBeanStore();
        builderData.initRootBeanStore(root);
        assertSame("Root store not set", root, builderData.getRootBeanStore());
        assertSame("Root store not accessible through alias", root, builderData
                .getBeanStore(null));
    }

    /**
     * Tests accessing the root bean store.
     */
    @Test
    public void testGetRootStore()
    {
        BeanStore store = builderData.getBeanStore(null);
        assertSame("Wrong root bean store", store, builderData
                .getRootBeanStore());
    }

    /**
     * Tests accessing a non existing bean store. This should cause an
     * exception.
     */
    @Test(expected = NoSuchElementException.class)
    public void testGetBeanStoreNonExisting()
    {
        builderData.getBeanStore(STORE_NAME);
    }

    /**
     * Tests the hasBeanStore() method applied to the root store.
     */
    @Test
    public void testHasBeanStoreRoot()
    {
        assertTrue("Root store not existing", builderData.hasBeanStore(null));
    }

    /**
     * Tests adding a bean provider to a bean store.
     */
    @Test
    public void testAddBeanProvider()
    {
        final String beanName = "myBean";
        builderData.addBeanStore(STORE_NAME, null);
        BeanProvider provider = ConstantBeanProvider.getInstance(42);
        builderData.addBeanProvider(STORE_NAME, beanName, provider);
        BeanStore store = builderData.getBeanStore(STORE_NAME);
        assertSame("Bean provider was not added", provider, store
                .getBeanProvider(beanName));
    }

    /**
     * Tries to add a bean provider with an undefined bean name. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddBeanProviderNullBeanName()
    {
        builderData.addBeanStore(STORE_NAME, null);
        BeanProvider provider = ConstantBeanProvider.getInstance(42);
        builderData.addBeanProvider(STORE_NAME, null, provider);
    }

    /**
     * Tries to add a null provider to a store. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddBeanProviderNullProvider()
    {
        builderData.addBeanStore(STORE_NAME, null);
        builderData.addBeanProvider(STORE_NAME, "myBean", null);
    }

    /**
     * Tries to add a bean provider to a non existing store. This should cause
     * an exception.
     */
    @Test(expected = NoSuchElementException.class)
    public void testAddBeanProviderNonExistingStore()
    {
        builderData.addBeanStore(STORE_NAME, null);
        builderData.addBeanProvider("unknownStore", "myBean",
                ConstantBeanProvider.getInstance(1));
    }

    /**
     * Tests adding anonymous bean providers.
     */
    @Test
    public void testAddAnonymousBeanProvider()
    {
        final int count = 12;
        builderData.addBeanStore(STORE_NAME, null);
        BeanProvider[] providers = new BeanProvider[count];
        String[] names = new String[count];
        Set<String> namesSet = new HashSet<String>();
        for (int i = 0; i < count; i++)
        {
            providers[i] = ConstantBeanProvider.getInstance(i);
            names[i] = builderData.addAnonymousBeanProvider(STORE_NAME,
                    providers[i]);
            assertTrue("Name not unique: " + names[i], namesSet.add(names[i]));
        }
        BeanStore store = builderData.getBeanStore(STORE_NAME);
        for (int i = 0; i < count; i++)
        {
            assertEquals("Wrong provider at " + i, providers[i], store
                    .getBeanProvider(names[i]));
        }
    }

    /**
     * Tests adding a null anonymous bean provider. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddAnonymousBeanProviderNull()
    {
        builderData.addBeanStore(STORE_NAME, null);
        builderData.addAnonymousBeanProvider(STORE_NAME, null);
    }

    /**
     * Tests whether a default invocation helper instance is created.
     */
    @Test
    public void testGetInvocationHelperDefault()
    {
        assertNotNull("No default invocation helper",
                builderData.getInvocationHelper());
    }

    /**
     * Tests whether an invocation helper can be set explicitly.
     */
    @Test
    public void testSetInvocationHelperExplicit()
    {
        InvocationHelper ih = new InvocationHelper();
        builderData.setInvocationHelper(ih);
        assertSame("Wrong invocation helper", ih,
                builderData.getInvocationHelper());
    }

    /**
     * Tests whether the current conversion helper is passed to the new root
     * bean store.
     */
    @Test
    public void testCreateRootBeanStoreConversionHelper()
    {
        BeanStore root = builderData.getBeanStore(null);
        assertSame("ConversionHelper not passed", builderData
                .getInvocationHelper().getConversionHelper(),
                root.getConversionHelper());
    }
}
