/*
 * Copyright 2006-2025 The JGUIraffe Team.
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.ConversionHelper;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for {@code CombinedBeanStore}.
 *
 * @author Oliver Heger
 * @version $Id: TestCombinedBeanStore.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestCombinedBeanStore
{
    /** Constant for a provider name. */
    private static final String PROVIDER_NAME = "testProvider";

    /** Constant for the number of child bean stores. */
    private static final int CHILD_COUNT = 3;

    /**
     * Creates an array with mock bean stores that can be used as children.
     *
     * @return the array with the mock stores
     */
    private static BeanStore[] createMockStores()
    {
        BeanStore[] result = new BeanStore[CHILD_COUNT];
        for (int i = 0; i < CHILD_COUNT; i++)
        {
            result[i] = EasyMock.createMock(BeanStore.class);
        }
        return result;
    }

    /**
     * Helper method for replaying the given bean store mock objects.
     *
     * @param beanStores the mocks to be replayed
     */
    private static void replay(BeanStore... beanStores)
    {
        EasyMock.replay((Object[]) beanStores);
    }

    /**
     * Helper method for verifying the given bean store mock objects.
     *
     * @param beanStores the mocks to be verified
     */
    private static void verify(BeanStore... beanStores)
    {
        EasyMock.verify((Object[]) beanStores);
    }

    /**
     * Tries to create an instance with a null array. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNullArray()
    {
        new CombinedBeanStore((BeanStore[]) null);
    }

    /**
     * Tries to create an instance if one of the child stores is null. This
     * should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNullChild()
    {
        BeanStore[] children = {
                new DefaultBeanStore(), null
        };
        new CombinedBeanStore(children);
    }

    /**
     * Tests a successful initialization.
     */
    @Test
    public void testInit()
    {
        BeanStore[] children = createMockStores();
        replay(children);
        CombinedBeanStore store = new CombinedBeanStore(children);
        assertEquals("Wrong size", CHILD_COUNT, store.size());
        for (int i = 0; i < CHILD_COUNT; i++)
        {
            assertEquals("Wrong child store at " + i, children[i], store
                    .getChildStore(i));
        }
        verify(children);
    }

    /**
     * Tests whether a defensive copy of the array passed to the constructor is
     * created.
     */
    @Test
    public void testInitDefensiveCopy()
    {
        BeanStore[] children = createMockStores();
        replay(children);
        CombinedBeanStore store = new CombinedBeanStore(children);
        BeanStore c = children[0];
        children[0] = null;
        assertEquals("Child was modified", c, store.getChildStore(0));
    }

    /**
     * Tries to access a child store with an invalid index.
     */
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testGetChildStoreInvalidIndex()
    {
        CombinedBeanStore store = new CombinedBeanStore(createMockStores());
        store.getChildStore(CHILD_COUNT + 10);
    }

    /**
     * Tests whether the name of the bean store is correctly maintained.
     */
    @Test
    public void testGetNameSpecified()
    {
        final String name = "TestBeanStore";
        CombinedBeanStore store = new CombinedBeanStore(name,
                createMockStores());
        assertEquals("Wrong name", name, store.getName());
    }

    /**
     * Tests whether names for bean stores can be generated.
     */
    @Test
    public void testGetNameGenerated()
    {
        final String prefix = "jguiraffe.CombinedBeanStore_";
        final int count = 100;
        int index = 0;
        for (int i = 0; i < count; i++)
        {
            BeanStore mockStore = EasyMock.createMock(BeanStore.class);
            CombinedBeanStore store = new CombinedBeanStore(mockStore);
            String name = store.getName();
            assertTrue("Wrong name prefix: " + name, name.startsWith(prefix));
            int currentIndex = Integer
                    .parseInt(name.substring(prefix.length()));
            assertTrue("Index too small: " + currentIndex + " <= " + index,
                    currentIndex > index);
            index = currentIndex;
        }
    }

    /**
     * Tests getParent() if no child store has a parent.
     */
    @Test
    public void testGetParentNone()
    {
        BeanStore[] children = createMockStores();
        for (int i = 0; i < children.length; i++)
        {
            EasyMock.expect(children[i].getParent()).andReturn(null);
        }
        replay(children);
        CombinedBeanStore store = new CombinedBeanStore(children);
        assertNull("Got a parent", store.getParent());
        verify(children);
    }

    /**
     * Tests getParent() if there is exactly a single one.
     */
    @Test
    public void testGetParentSingle()
    {
        BeanStore[] children = createMockStores();
        BeanStore parent = EasyMock.createMock(BeanStore.class);
        int parentIdx = children.length / 2;
        for (int i = 0; i < children.length; i++)
        {
            EasyMock.expect(children[i].getParent()).andReturn(
                    (i == parentIdx) ? parent : null);
        }
        replay(children);
        EasyMock.replay(parent);
        CombinedBeanStore store = new CombinedBeanStore(children);
        assertEquals("Wrong parent", parent, store.getParent());
        verify(children);
        EasyMock.verify(parent);
    }

    /**
     * Tests getParent() if there are multiple parents.
     */
    @Test
    public void testGetParentMultiple()
    {
        BeanStore[] children = createMockStores();
        List<BeanStore> parents = new ArrayList<BeanStore>();
        for (int i = 0; i < children.length; i++)
        {
            BeanStore parent;
            if (i % 2 == 0)
            {
                parent = EasyMock.createMock(BeanStore.class);
                parents.add(parent);
            }
            else
            {
                parent = null;
            }
            EasyMock.expect(children[i].getParent()).andReturn(parent);
        }
        replay(children);
        EasyMock.replay(parents.toArray());
        CombinedBeanStore store = new CombinedBeanStore(children);
        CombinedBeanStore parent = (CombinedBeanStore) store.getParent();
        assertEquals("Wrong number of parents", parents.size(), parent.size());
        for (int i = 0; i < parents.size(); i++)
        {
            assertEquals("Wrong parent at " + i, parents.get(i), parent
                    .getChildStore(i));
        }
        verify(children);
        EasyMock.verify(parents.toArray());
    }

    /**
     * Tests whether the names of all providers can be obtained.
     */
    @Test
    public void testProviderNames()
    {
        final int providerCount = 5;
        BeanStore[] children = createMockStores();
        for (int i = 0; i < children.length; i++)
        {
            Set<String> storeNames = new HashSet<String>();
            for (int j = i * providerCount; j <= (i + 1) * providerCount; j++)
            {
                storeNames.add(PROVIDER_NAME + j);
            }
            EasyMock.expect(children[i].providerNames()).andReturn(storeNames);
        }
        replay(children);
        CombinedBeanStore store = new CombinedBeanStore(children);
        Set<String> allNames = store.providerNames();
        assertEquals("Wrong number of names", children.length * providerCount
                + 1, allNames.size());
        for (int i = 0; i < allNames.size(); i++)
        {
            String name = PROVIDER_NAME + i;
            assertTrue("Provider name not found: " + name, allNames
                    .contains(name));
        }
        verify(children);
    }

    /**
     * Tests whether an existing bean provider can be retrieved.
     */
    @Test
    public void testGetBeanProviderExisting()
    {
        BeanStore[] children = createMockStores();
        BeanProvider provider = EasyMock.createMock(BeanProvider.class);
        EasyMock.expect(children[0].getBeanProvider(PROVIDER_NAME)).andReturn(
                null);
        EasyMock.expect(children[1].getBeanProvider(PROVIDER_NAME)).andReturn(
                provider);
        replay(children);
        EasyMock.replay(provider);
        CombinedBeanStore store = new CombinedBeanStore(children);
        assertEquals("Wrong bean provider", provider, store
                .getBeanProvider(PROVIDER_NAME));
        verify(children);
        EasyMock.verify(provider);
    }

    /**
     * Tests getBeanProvider() if none of the child stores contains the
     * provider.
     */
    @Test
    public void testGetBeanProviderNonExisting()
    {
        BeanStore[] children = createMockStores();
        for (BeanStore st : children)
        {
            EasyMock.expect(st.getBeanProvider(PROVIDER_NAME)).andReturn(null);
        }
        replay(children);
        CombinedBeanStore store = new CombinedBeanStore(children);
        assertNull("Got a provider", store.getBeanProvider(PROVIDER_NAME));
        verify(children);
    }

    /**
     * Tests the string representation of the combined store.
     */
    @Test
    public void testToString()
    {
        BeanStore[] children = createMockStores();
        replay(children);
        CombinedBeanStore store = new CombinedBeanStore("myStore", children);
        String s = store.toString();
        assertTrue("Name not found: " + s, s.indexOf("name = "
                + store.getName()) > 0);
        assertTrue("Children not found: " + s, s.indexOf("childStores = "
                + Arrays.toString(children)) > 0);
    }

    /**
     * Tests getConversionHelper() if a helper can be found.
     */
    @Test
    public void testGetConversionHelperFound()
    {
        BeanStore[] children = createMockStores();
        ConversionHelper ch = new ConversionHelper();
        EasyMock.expect(children[0].getConversionHelper()).andReturn(null);
        EasyMock.expect(children[1].getConversionHelper()).andReturn(ch);
        replay(children);
        CombinedBeanStore store = new CombinedBeanStore(children);
        assertSame("Wrong conversion helper", ch, store.getConversionHelper());
        verify(children);
    }

    /**
     * Tests getConversionHelper() if none of the child stores defines a helper.
     */
    @Test
    public void testGetConversionHelperNotFound()
    {
        BeanStore[] children = createMockStores();
        for (BeanStore st : children)
        {
            EasyMock.expect(st.getConversionHelper()).andReturn(null);
        }
        replay(children);
        CombinedBeanStore store = new CombinedBeanStore(children);
        assertNull("Got a conversion helper", store.getConversionHelper());
        verify(children);
    }
}
