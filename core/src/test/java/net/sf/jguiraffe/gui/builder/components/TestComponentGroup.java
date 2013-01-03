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
package net.sf.jguiraffe.gui.builder.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.forms.ComponentHandlerImpl;
import net.sf.jguiraffe.gui.forms.TransformerContextImpl;
import net.sf.jguiraffe.gui.forms.bind.BeanBindingStrategy;

import org.apache.commons.jelly.JellyContext;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code ComponentGroup}.
 *
 * @author Oliver Heger
 * @version $Id: TestComponentGroup.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestComponentGroup
{
    private static final String GROUP_PREFIX = "group/";

    /** Constant for the name of the test group. */
    private static final String GROUP_NAME = "testGroup";

    /** Constant for the prefix for a component name. */
    private static final String COMP_NAME = "testComponent";

    /** Constant for the number of test components. */
    private static final int COMP_COUNT = 4;

    /** The group to be tested. */
    private ComponentGroup group;

    /** The Jelly context. */
    private JellyContext context;

    @Before
    public void setUp() throws Exception
    {
        group = new ComponentGroup();
        context = new JellyContext();
    }

    /**
     * Tests a new group instance.
     */
    @Test
    public void testNewInstance()
    {
        assertTrue("Group not empty", group.getComponentNames().isEmpty());
    }

    /**
     * Tests adding new components to the group.
     */
    @Test
    public void testAddComponents()
    {
        final int count = 10;
        for (int i = 1; i < count; i++)
        {
            group.addComponent(COMP_NAME + i);
        }

        int idx = 1;
        for (String name : group.getComponentNames())
        {
            assertEquals("Wrong name at " + idx, COMP_NAME + idx, name);
            idx++;
        }
        assertEquals("Wrong number of components", count, idx);
    }

    /**
     * Tests storing groups in the context and retrieving them.
     */
    @Test
    public void testFromContext()
    {
        ComponentGroup.storeGroup(context, GROUP_NAME, group);
        ComponentGroup g = ComponentGroup.fromContext(context, GROUP_NAME);
        assertSame("Wrong group from context", group, g);
    }

    /**
     * Tests querying a group from the context that cannot be found. This should
     * cause an exception.
     */
    @Test(expected = NoSuchElementException.class)
    public void testFromContextNotFound()
    {
        ComponentGroup.fromContext(context, "test");
    }

    /**
     * Tests fromContext() if no context is passed in. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFromNullContext()
    {
        ComponentGroup.fromContext(null, GROUP_NAME);
    }

    /**
     * Tests the groupExists() method.
     */
    @Test
    public void testGroupExists()
    {
        assertFalse("Group already exists", ComponentGroup.groupExists(context,
                GROUP_NAME));
        ComponentGroup.storeGroup(context, GROUP_NAME, group);
        assertTrue("Group not stored", ComponentGroup.groupExists(context,
                GROUP_NAME));
    }

    /**
     * Tests whether a group can be removed by storing a null group.
     */
    @Test
    public void testStoreGroupNull()
    {
        ComponentGroup.storeGroup(context, GROUP_NAME, group);
        assertTrue("Group not found", ComponentGroup.groupExists(context,
                GROUP_NAME));
        ComponentGroup.storeGroup(context, GROUP_NAME, null);
        assertFalse("Group not removed", ComponentGroup.groupExists(context,
                "testGroup"));
    }

    /**
     * Tries to store a group in a null context. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testStoreGroupNullCtx()
    {
        ComponentGroup.storeGroup(null, GROUP_NAME, group);
    }

    /**
     * Tests whether a group can be created successfully.
     */
    @Test
    public void testCreateGroupSuccess() throws FormBuilderException
    {
        ComponentGroup grp = ComponentGroup.createGroup(context, GROUP_NAME);
        assertNotNull("No group created", grp);
        assertSame("Group not stored in context", grp, context
                .getVariable(GROUP_PREFIX + GROUP_NAME));
        assertTrue("Got components", grp.getComponentNames().isEmpty());
    }

    /**
     * Tries to create a group without a name. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateGroupNull() throws FormBuilderException
    {
        ComponentGroup.createGroup(context, null);
    }

    /**
     * Tries to create a group in a null context. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateGroupNullCtx() throws FormBuilderException
    {
        ComponentGroup.createGroup(null, GROUP_NAME);
    }

    /**
     * Tries to create a group that already exists. This should cause an
     * exception.
     */
    @Test(expected = FormBuilderException.class)
    public void testCreateGroupExisting() throws FormBuilderException
    {
        ComponentGroup.createGroup(context, GROUP_NAME);
        ComponentGroup.createGroup(context, GROUP_NAME);
    }

    /**
     * Creates some test components and stores them in a builder data object.
     * The components are also added to the group.
     *
     * @return the component builder data object
     */
    private ComponentBuilderData createComponents()
    {
        ComponentBuilderData data = new ComponentBuilderData();
        data.initializeForm(new TransformerContextImpl(),
                new BeanBindingStrategy());
        for (int i = 0; i < COMP_COUNT; i++)
        {
            String compName = COMP_NAME + i;
            ComponentHandlerImpl ch = new ComponentHandlerImpl();
            ch.setComponent(Integer.valueOf(i));
            data.storeComponentHandler(compName, ch);
            group.addComponent(compName);
        }
        return data;
    }

    /**
     * Tests whether a map with all components can be requested.
     */
    @Test
    public void testGetComponents() throws FormBuilderException
    {
        ComponentBuilderData data = createComponents();
        Map<String, Object> comps = group.getComponents(data);
        assertEquals("Wrong number of components", COMP_COUNT, comps.size());
        Iterator<Map.Entry<String, Object>> it = comps.entrySet().iterator();
        for (int i = 0; i < COMP_COUNT; i++)
        {
            Map.Entry<String, Object> e = it.next();
            assertEquals("Wrong name", COMP_NAME + i, e.getKey());
            assertEquals("Wrong object", Integer.valueOf(i), e.getValue());
        }
        assertFalse("Too many elements", it.hasNext());
    }

    /**
     * Tries to call getComponents() without a builder data object. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetComponentsNullBuilderData() throws FormBuilderException
    {
        createComponents();
        group.getComponents(null);
    }

    /**
     * Tests getComponents() if a component name cannot be resolved. This should
     * cause an exception.
     */
    @Test(expected = FormBuilderException.class)
    public void testGetComponentsUnknownName() throws FormBuilderException
    {
        ComponentBuilderData data = createComponents();
        group.addComponent("unknownComponent");
        group.getComponents(data);
    }

    /**
     * Tests whether a map with all component handlers can be queried.
     */
    @Test
    public void testGetComponentHandlers() throws FormBuilderException
    {
        ComponentBuilderData data = createComponents();
        Map<String, ComponentHandler<?>> handlers = group
                .getComponentHandlers(data);
        assertEquals("Wrong number of elements", COMP_COUNT, handlers.size());
        Iterator<Map.Entry<String, ComponentHandler<?>>> it = handlers
                .entrySet().iterator();
        for (int i = 0; i < COMP_COUNT; i++)
        {
            Map.Entry<String, ComponentHandler<?>> e = it.next();
            assertEquals("Wrong name", COMP_NAME + i, e.getKey());
            assertEquals("Wrong object", Integer.valueOf(i), e.getValue()
                    .getComponent());
        }
        assertFalse("Too many elements", it.hasNext());
    }

    /**
     * Tries to call getComponentHandlers() without a builder data object. This
     * should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetComponentHandlersNullBuilderData()
            throws FormBuilderException
    {
        createComponents();
        group.getComponentHandlers(null);
    }

    /**
     * Tests getComponentHandlers() if a component name cannot be resolved. This
     * should cause an exception.
     */
    @Test(expected = FormBuilderException.class)
    public void testGetComponentHandlersUnknownName()
            throws FormBuilderException
    {
        ComponentBuilderData data = createComponents();
        group.addComponent("unknownComponent");
        group.getComponentHandlers(data);
    }

    /**
     * Tests whether the group can be obtained from a bean context.
     */
    @Test
    public void testFromBeanContext()
    {
        BeanContext beanCtx = EasyMock.createMock(BeanContext.class);
        EasyMock.expect(beanCtx.getBean(GROUP_PREFIX + GROUP_NAME))
                .andReturn(group);
        EasyMock.replay(beanCtx);
        assertSame("Wrong group", group, ComponentGroup.fromBeanContext(
                beanCtx, GROUP_NAME));
        EasyMock.verify(beanCtx);
    }

    /**
     * Tries to obtain a group from a null bean context.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFromBeanContextNull()
    {
        ComponentGroup.fromBeanContext(null, GROUP_NAME);
    }

    /**
     * Tests whether the existence of a group in a bean context can be checked.
     */
    @Test
    public void testGroupExistsInBeanContext()
    {
        BeanContext beanCtx = EasyMock.createMock(BeanContext.class);
        EasyMock.expect(beanCtx.containsBean(GROUP_PREFIX + GROUP_NAME))
                .andReturn(Boolean.TRUE);
        EasyMock.expect(beanCtx.containsBean(GROUP_PREFIX + GROUP_NAME))
                .andReturn(Boolean.FALSE);
        EasyMock.replay(beanCtx);
        assertTrue("Wrong result (1)", ComponentGroup.groupExistsInBeanContext(
                beanCtx, GROUP_NAME));
        assertFalse("Wrong result (2)", ComponentGroup
                .groupExistsInBeanContext(beanCtx, GROUP_NAME));
        EasyMock.verify(beanCtx);
    }

    /**
     * Tests the behavior of groupExistsInBeanContext() if a null context is
     * passed in.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGroupExistsInBeanContextNull()
    {
        ComponentGroup.groupExistsInBeanContext(null, GROUP_NAME);
    }

    /**
     * Helper method for testing the enabled state of the test components.
     *
     * @param data the component builder data object
     * @param expected the expected enabled state
     */
    private static void checkEnabled(ComponentBuilderData data, boolean expected)
    {
        for (int i = 0; i < COMP_COUNT; i++)
        {
            ComponentHandler<?> ch = data.getComponentHandler(COMP_NAME + i);
            assertEquals("Wrong enabled state at " + i, expected, ch
                    .isEnabled());
        }
    }

    /**
     * Tests whether a group can be enabled and disabled.
     */
    @Test
    public void testEnableGroup() throws FormBuilderException
    {
        ComponentBuilderData data = createComponents();
        group.enableGroup(data, true);
        checkEnabled(data, true);
        group.enableGroup(data, false);
        checkEnabled(data, false);
    }
}
