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
package net.sf.jguiraffe.gui.builder.window;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.jguiraffe.di.impl.SimpleBeanStoreImpl;
import net.sf.jguiraffe.di.impl.providers.ConstantBeanProvider;

import org.apache.commons.jelly.JellyContext;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code WindowBuilderData}.
 *
 * @author Oliver Heger
 * @version $Id: TestWindowBuilderData.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestWindowBuilderData
{
    /** Constant for the name of a test window. */
    private static final String WND_NAME = "testWindow";

    /** Constant for the number of test windows. */
    private static final int COUNT = 12;

    /** Stores the instance to be tested. */
    private WindowBuilderData data;

    @Before
    public void setUp() throws Exception
    {
        data = new WindowBuilderData();
    }

    /**
     * Tests whether the window builder can be put into the Jelly context.
     */
    @Test
    public void testPut()
    {
        JellyContext ctx = new JellyContext();
        data.put(ctx);
        assertSame("Wrong instance returned", data, WindowBuilderData.get(ctx));
    }

    /**
     * Tries storing the window builder in a null context. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testPutNullContext()
    {
        data.put(null);
    }

    /**
     * Tests obtaining the window builder from a null context. Result should be
     * null.
     */
    @Test
    public void testGetNullContext()
    {
        assertNull("Wrong result for null context", WindowBuilderData.get(null));
    }

    /**
     * Tests obtaining the window builder from a context that does not contain
     * an instance. Result should be null.
     */
    @Test
    public void testGetNoInstance()
    {
        assertNull("Wrong result for empty context", WindowBuilderData
                .get(new JellyContext()));
    }

    /**
     * Tests whether the expected bean names are returned.
     *
     * @param names the set with the names
     */
    private void checkBeanNames(Set<String> names)
    {
        final String[] expectedNames =
        { WindowBuilderData.KEY_CURRENT_WINDOW,
                WindowBuilderData.KEY_FORM_BEAN,
                WindowBuilderData.KEY_PARENT_WINDOW };
        for (String n : expectedNames)
        {
            assertTrue("Bean name not found: " + n, names.contains(n));
        }
    }

    /**
     * Initializes the properties of the test object with object references.
     */
    private void initAssociatedObjects()
    {
        data.setFormBean(new Object());
        data.setParentWindow(EasyMock.createNiceMock(Window.class));
        data.setResultWindow(EasyMock.createNiceMock(Window.class));
    }

    /**
     * Tests whether the correct bean names are returned if all involved objects
     * are present.
     */
    @Test
    public void testBeanNamesWithObjectRefs()
    {
        initAssociatedObjects();
        Set<String> names = new HashSet<String>();
        data.beanNames(names);
        checkBeanNames(names);
    }

    /**
     * Tests beanNames() if object references are not set.
     */
    @Test
    public void testBeanNamesNoObjectRefs()
    {
        Set<String> names = new HashSet<String>();
        data.beanNames(names);
        assertTrue("Got bean names", names.isEmpty());
    }

    /**
     * Helper method for creating and initializing a bean store with data from
     * the window builder data object.
     *
     * @return the bean store
     */
    private SimpleBeanStoreImpl setUpStore()
    {
        SimpleBeanStoreImpl store = new SimpleBeanStoreImpl();
        data.initBeanStore(store);
        return store;
    }

    /**
     * Helper method for obtaining a bean from a bean store.
     *
     * @param store the store
     * @param key the name of the bean
     * @return the found bean
     */
    private Object getBean(SimpleBeanStoreImpl store, String key)
    {
        ConstantBeanProvider provider = (ConstantBeanProvider) store
                .getBeanProvider(key);
        return provider.getBean();
    }

    /**
     * Tests whether the initialized bean store contains the correct provider
     * names.
     */
    @Test
    public void testInitBeanStoreProviderNames()
    {
        initAssociatedObjects();
        SimpleBeanStoreImpl store = setUpStore();
        checkBeanNames(store.providerNames());
        assertTrue("Instance key not found", store.providerNames().contains(
                WindowBuilderData.KEY_WINDOW_BUILDER_DATA));
    }

    /**
     * Tests whether the form bean can be obtained from the initialized bean
     * store.
     */
    @Test
    public void testInitBeanStoreGetFormBean()
    {
        final Object formBean = "MyTestFormBean";
        data.setFormBean(formBean);
        SimpleBeanStoreImpl store = setUpStore();
        assertSame("Wrong form bean", formBean, getBean(store,
                WindowBuilderData.KEY_FORM_BEAN));
    }

    /**
     * Tests whether the result window can be obtained from the initialized bean
     * store.
     */
    @Test
    public void testInitBeanStoreGetResultWindow()
    {
        final Window resultWindow = new WindowImpl();
        data.setResultWindow(resultWindow);
        SimpleBeanStoreImpl store = setUpStore();
        assertSame("Wrong result window", resultWindow, getBean(store,
                WindowBuilderData.KEY_CURRENT_WINDOW));
    }

    /**
     * Tests whether the parent window can be obtained from the initialized bean
     * store.
     */
    @Test
    public void testInitBeanStoreGetParentWindow()
    {
        final Window parentWindow = new WindowImpl();
        data.setParentWindow(parentWindow);
        SimpleBeanStoreImpl store = setUpStore();
        assertSame("Wrong parent window", parentWindow, getBean(store,
                WindowBuilderData.KEY_PARENT_WINDOW));
    }

    /**
     * Tests whether the window builder data instance can be obtained from the
     * initialized bean store.
     */
    @Test
    public void testInitBeanStoreGetWindowBuilderData()
    {
        assertSame("Wrong window builder data", data, getBean(setUpStore(),
                WindowBuilderData.KEY_WINDOW_BUILDER_DATA));
    }

    /**
     * Tests whether a named window can be added.
     */
    @Test
    public void testPutWindow()
    {
        Window window = EasyMock.createMock(Window.class);
        EasyMock.replay(window);
        data.putWindow(WND_NAME, window);
        assertSame("Wrong window", window, data.getWindow(WND_NAME));
        assertSame("Wrong result window", window, data.getResultWindow());
    }

    /**
     * Tests whether putWindow() works with a window without a name.
     */
    @Test
    public void testPutWindowNoName()
    {
        Window window = EasyMock.createMock(Window.class);
        EasyMock.replay(window);
        data.putWindow(null, window);
        assertSame("Wrong result window", window, data.getResultWindow());
    }

    /**
     * Adds the given number of test windows to the data object.
     * @param count the number of windows to add
     * @return the mock windows created by this method
     */
    private List<Window> putWindows(int count)
    {
        List<Window> result = new ArrayList<Window>(count);
        for(int i = 0; i < count; i++)
        {
            Window w = EasyMock.createMock(Window.class);
            EasyMock.replay(w);
            result.add(w);
            data.putWindow(WND_NAME + i, w);
        }
        return result;
    }

    /**
     * Tests the behavior of the data object if multiple windows are added.
     */
    @Test
    public void testPutWindowMulti()
    {
        List<Window> windows = putWindows(COUNT);
        for (int i = 0; i < windows.size(); i++)
        {
            assertSame("Wrong window at " + i, windows.get(i), data
                    .getWindow(WND_NAME + i));
        }
        assertSame("Wrong result window", windows.get(windows.size() - 1), data
                .getResultWindow());
    }

    /**
     * Tests whether the names of windows are in the set of available bean
     * names.
     */
    @Test
    public void testBeanNamesNamedWindows()
    {
        putWindows(COUNT);
        Set<String> names = new HashSet<String>();
        data.beanNames(names);
        for (int i = 0; i < COUNT; i++)
        {
            String windowName = WND_NAME + i;
            assertTrue("Window bean not found: " + windowName, names
                    .contains(WindowBuilderData.WINDOW_PREFIX + windowName));
        }
    }

    /**
     * Tests whether beans for named windows can be queried.
     */
    @Test
    public void testInitBeanStoreNamedWindows()
    {
        List<Window> windows = putWindows(COUNT);
        SimpleBeanStoreImpl store = setUpStore();
        for (int i = 0; i < COUNT; i++)
        {
            assertSame("Wrong window at " + i, windows.get(i), getBean(store,
                    WindowBuilderData.WINDOW_PREFIX + WND_NAME + i));
        }
    }

    /**
     * Tries to access the context before it becomes available.
     */
    @Test(expected = IllegalStateException.class)
    public void testGetContextNotAvailable()
    {
        data.getContext();
    }

    /**
     * Tests whether an existing Jelly context can be queried.
     */
    @Test
    public void testGetContextAvailable()
    {
        JellyContext context = new JellyContext();
        data.put(context);
        assertSame("Wrong context", context, data.getContext());
    }
}
