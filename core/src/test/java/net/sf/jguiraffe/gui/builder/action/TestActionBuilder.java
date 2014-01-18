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
package net.sf.jguiraffe.gui.builder.action;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import net.sf.jguiraffe.di.impl.SimpleBeanStoreImpl;
import net.sf.jguiraffe.di.impl.providers.ConstantBeanProvider;

import org.apache.commons.jelly.JellyContext;

import junit.framework.TestCase;

/**
 * Test class for ActionBuilder.
 *
 * @author Oliver Heger
 * @version $Id: TestActionBuilder.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestActionBuilder extends TestCase
{
    /** Constant for the names of the test actions. */
    private static final String ACTION_NAME = "TestAction";

    /** Constant for the number of test actions. */
    private static final int ACTION_COUNT = 12;

    /** Stores the action builder to be tested. */
    private ActionBuilder data;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        data = new ActionBuilder();
    }

    /**
     * Tests whether the action builder can be put into the Jelly context.
     */
    public void testPut()
    {
        JellyContext ctx = new JellyContext();
        data.put(ctx);
        assertSame("Wrong instance returned", data, ActionBuilder.get(ctx));
    }

    /**
     * Tries storing the action builder in a null context. This should cause an
     * exception.
     */
    public void testPutNullContext()
    {
        try
        {
            data.put(null);
            fail("Could store action builder in null context!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tests obtaining the action builder from a null context. Result should be
     * null.
     */
    public void testGetNullContext()
    {
        assertNull("Wrong result for null context", ActionBuilder.get(null));
    }

    /**
     * Tests obtaining the action builder from a context that does not contain
     * an instance. Result should be null.
     */
    public void testGetNoInstance()
    {
        assertNull("Wrong result for empty context", ActionBuilder
                .get(new JellyContext()));
    }

    /**
     * Creates an action store with some test actions.
     */
    private void setUpActionStore()
    {
        ActionStore parent = new ActionStore();
        ActionStore store = new ActionStore(parent);
        for (int i = 0, cnt = ACTION_COUNT / 2; i < cnt; i++)
        {
            parent.addAction(createAction(i));
            store.addAction(createAction(i + cnt));
        }
        data.setActionStore(store);
    }

    /**
     * Creates an action with the given name.
     *
     * @param idx the index of this action (will be used for creating a name)
     * @return the dummy action
     */
    private FormAction createAction(int idx)
    {
        return new FormActionImpl(ACTION_NAME + idx);
    }

    /**
     * Tests whether the passed in set contains all expected bean names.
     *
     * @param names the set with the names
     */
    private void checkBeanNames(Set<String> names)
    {
        for (int i = 0; i < ACTION_COUNT; i++)
        {
            assertTrue("Cannot find action: " + i,
                    names.contains(ActionBuilder.KEY_ACTION_PREFIX
                            + ACTION_NAME + i));
        }
        assertTrue("Cannot find action store", names
                .contains(ActionBuilder.KEY_ACTION_STORE));
    }

    /**
     * Tests the set with the names of the supported beans.
     */
    public void testBeanNames()
    {
        setUpActionStore();
        Set<String> names = new HashSet<String>();
        data.beanNames(names);
        checkBeanNames(names);
        assertEquals("Wrong number of beans", ACTION_COUNT + 1, names.size());
    }

    /**
     * Tests the beanNames() methods when no action store exists.
     */
    public void testBeanNamesNoStore()
    {
        Set<String> names = new HashSet<String>();
        data.beanNames(names);
        assertTrue("Names were found", names.isEmpty());
    }

    /**
     * Tests whether the actions can be obtained using the getBean() method.
     */
    public void testGetBeanActions()
    {
        setUpActionStore();
        for (int i = 0; i < ACTION_COUNT; i++)
        {
            FormAction action = (FormAction) data
                    .getBean(ActionBuilder.KEY_ACTION_PREFIX + ACTION_NAME + i);
            assertNotNull("No action found: " + i, action);
            assertEquals("Wrong action: " + i, ACTION_NAME + i, action
                    .getName());
        }
    }

    /**
     * Tests obtaining the other helper objects from the getBean() method.
     */
    public void testGetBeanOther()
    {
        setUpActionStore();
        assertSame("Wrong action store", data.getActionStore(), data
                .getBean(ActionBuilder.KEY_ACTION_STORE));
    }

    /**
     * Tests the getBean() method when no action store exists.
     */
    public void testGetBeanNoStore()
    {
        assertNull("Found action store", data
                .getBean(ActionBuilder.KEY_ACTION_STORE));
    }

    /**
     * Tests querying an unknown action through the getBean() method. This
     * should cause an exception.
     */
    public void testGetBeanUnknownAction()
    {
        setUpActionStore();
        try
        {
            data.getBean("action:unknownAction");
            fail("Could obtain unknown action!");
        }
        catch (NoSuchElementException nsex)
        {
            // ok
        }
    }

    /**
     * Tests getBean() when null is passed in.
     */
    public void testGetBeanNull()
    {
        assertNull("Wrong result for null bean name", data.getBean(null));
    }

    /**
     * Tests whether a bean store is correctly initialized.
     */
    public void testInitBeanStore()
    {
        setUpActionStore();
        SimpleBeanStoreImpl store = new SimpleBeanStoreImpl();
        data.initBeanStore(store);
        Set<String> names = store.providerNames();
        checkBeanNames(names);
        assertEquals("Wrong number of beans", ACTION_COUNT + 2, names.size());
        assertTrue("Action builder not found", names
                .contains(ActionBuilder.KEY_ACTION_BUILDER));
        ConstantBeanProvider provider = (ConstantBeanProvider) store
                .getBeanProvider(ActionBuilder.KEY_ACTION_BUILDER);
        assertSame("Wrong action builder instance", data, provider.getBean());
    }
}
