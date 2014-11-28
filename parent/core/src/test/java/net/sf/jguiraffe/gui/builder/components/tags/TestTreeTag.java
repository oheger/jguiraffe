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
package net.sf.jguiraffe.gui.builder.components.tags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.ComponentManagerImpl;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.forms.ComponentHandlerImpl;
import net.sf.jguiraffe.gui.layout.NumberWithUnit;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.tree.DefaultConfigurationNode;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for TreeTag.
 *
 * @author Oliver Heger
 * @version $Id: TestTreeTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestTreeTag
{
    /** Constant for the name of a bean. */
    private static final String BEAN_NAME = "myBean";

    /** The component builder data. */
    private ComponentBuilderData builderData;

    /** The tag to be tested. */
    private TreeTag tag;

    @Before
    public void setUp() throws Exception
    {
        tag = new TreeTag();
        JellyContext context = new JellyContext();
        tag.setContext(context);
        builderData = new ComponentBuilderData();
        builderData.put(context);
        builderData.setComponentManager(new ComponentManagerImpl());
    }

    /**
     * Creates a bean context mock and installs it at the builder data object.
     *
     * @return the bean context mock
     */
    private BeanContext setUpBeanContext()
    {
        BeanContext ctx = EasyMock.createMock(BeanContext.class);
        builderData.setBeanContext(ctx);
        return ctx;
    }

    /**
     * Convenience method for querying the component manager.
     *
     * @return the component manager
     */
    private ComponentManager getComponentManager()
    {
        return builderData.getComponentManager();
    }

    /**
     * Tests a newly created instance and the default values for attributes.
     */
    @Test
    public void testInit()
    {
        assertTrue("Wrong default for noField", tag.isNoField());
        assertTrue("Wrong default for rootVisible", tag.isRootVisible());
        assertFalse("Wrong default for editable", tag.isEditable());
        assertFalse("Wrong default for multiSelection", tag.isMultiSelection());
        assertNull("Model name is set", tag.getModel());
        assertNull("Model is set", tag.getTreeModel());
        assertNull("Icon handler is set", tag.getIconHandler());
        assertTrue("Icons defined", tag.getIcons().isEmpty());
    }

    /**
     * Tests creating the component handler when the create flag is true.
     */
    @Test
    public void testCreateComponentHandlerCreate() throws FormBuilderException,
            JellyTagException
    {
        assertNull("Wrong result for create = true", tag
                .createComponentHandler(getComponentManager(), true));
    }

    /**
     * Helper method for checking whether a correct component handler was
     * created.
     *
     * @param expected the expected string representing the handler
     */
    private void checkComponentHandler(String expected)
    {
        try
        {
            tag.createComponentHandler(getComponentManager(), true);
            ComponentHandlerImpl ch = (ComponentHandlerImpl) tag
                    .createComponentHandler(getComponentManager(), false);
            assertEquals("Wrong component handler", expected, ch.getComponent()
                    .toString());
        }
        catch (Exception ex)
        {
            fail("Unexpected exception: " + ex);
        }
    }

    /**
     * Tests creating a component handler when all required data is present.
     */
    @Test
    public void testCreateComponentHandlerSuccess()
            throws FormBuilderException, JellyTagException
    {
        HierarchicalConfiguration model = new HierarchicalConfiguration();
        tag.setTreeModel(model);
        tag.setRootVisible(false);
        tag.setMultiSelection(true);
        tag.setEditable(true);
        checkComponentHandler("TREE [ EDITABLE = true ROOTVISIBLE = false MULTISELECT ]");
        assertEquals("Wrong model", model, tag.getTreeModel());
        assertEquals("Wrong default scroll width", NumberWithUnit.ZERO, tag
                .getPreferredScrollWidth());
        assertEquals("Wrong default scroll height", NumberWithUnit.ZERO, tag
                .getPreferredScrollHeight());
    }

    /**
     * Tests creating a component handler when the model has to be obtained from
     * the current bean context.
     */
    @Test
    public void testCreateComponentHandlerModelFromContext()
    {
        BeanContext ctx = setUpBeanContext();
        HierarchicalConfiguration model = new HierarchicalConfiguration();
        EasyMock.expect(ctx.getBean(BEAN_NAME)).andReturn(model);
        EasyMock.replay(ctx);
        tag.setModel(BEAN_NAME);
        checkComponentHandler("TREE [ MODEL = myBean EDITABLE = false ROOTVISIBLE = true ]");
        assertEquals("Wrong model", model, tag.getTreeModel());
        EasyMock.verify(ctx);
    }

    /**
     * Tests whether the scroll size is taken into account.
     */
    @Test
    public void testCreateComponentHandlerScrollSize()
    {
        final NumberWithUnit scrollWidth = new NumberWithUnit(10);
        final NumberWithUnit scrollHeight = new NumberWithUnit(25);
        HierarchicalConfiguration model = new HierarchicalConfiguration();
        tag.setTreeModel(model);
        tag.setScrollWidth(scrollWidth.toUnitString());
        tag.setScrollHeight(scrollHeight.toUnitString());
        checkComponentHandler("TREE [ EDITABLE = false ROOTVISIBLE = true SCROLLWIDTH = "
                + scrollWidth + " SCROLLHEIGHT = " + scrollHeight + " ]");
    }

    /**
     * Tests processing a tag when no model is defined. This should cause an
     * exception.
     */
    @Test(expected = JellyTagException.class)
    public void testCreateComponentHandlerNoModel()
            throws FormBuilderException, JellyTagException
    {
        tag.createComponentHandler(getComponentManager(), false);
    }

    /**
     * Tests querying the icon handler if a custom handler is defined.
     */
    @Test
    public void testGetResolvedIconHandlerByName() throws FormBuilderException,
            JellyTagException
    {
        TreeIconHandler iconHandler = EasyMock
                .createMock(TreeIconHandler.class);
        BeanContext ctx = setUpBeanContext();
        EasyMock.expect(ctx.getBean(BEAN_NAME)).andReturn(iconHandler);
        EasyMock.replay(ctx, iconHandler);
        tag.setIconHandler(BEAN_NAME);
        tag.processBeforeBody();
        assertEquals("Wrong icon handler", iconHandler, tag
                .getResolvedIconHandler());
        EasyMock.verify(ctx, iconHandler);
    }

    /**
     * Tests the icon name returned by the default icon handler for leaf nodes.
     */
    @Test
    public void testGetResolvedIconHandlerDefaultLeaf()
            throws FormBuilderException, JellyTagException
    {
        tag.processBeforeBody();
        assertEquals("Wrong icon name", TreeTag.ICON_LEAF, tag
                .getResolvedIconHandler().getIconName(
                        new DefaultConfigurationNode(), false, true));
    }

    /**
     * Tests the icon name returned by the default icon handler for leaf nodes
     * that are also expanded. This combination should normally not be possible,
     * but should try to be on the safe side.
     */
    @Test
    public void testGetResolvedIconHandlerDefaultLeafExpanded()
            throws FormBuilderException, JellyTagException
    {
        tag.processBeforeBody();
        assertEquals("Wrong icon name", TreeTag.ICON_LEAF, tag
                .getResolvedIconHandler().getIconName(
                        new DefaultConfigurationNode(), true, true));
    }

    /**
     * Tests the icon name returned by the default icon handler for expanded
     * branch nodes.
     */
    @Test
    public void testGetResolvedIconHandlerDefaultBranchExpanded()
            throws FormBuilderException, JellyTagException
    {
        tag.processBeforeBody();
        assertEquals("Wrong icon name", TreeTag.ICON_BRANCH_EXPANDED, tag
                .getResolvedIconHandler().getIconName(
                        new DefaultConfigurationNode(), true, false));
    }

    /**
     * Tests the icon name returned by the default icon handler for collapsed
     * branch nodes.
     */
    @Test
    public void testGetResolvedIconHandlerDefaultBranchCollapsed()
            throws FormBuilderException, JellyTagException
    {
        tag.processBeforeBody();
        assertEquals("Wrong icon name", TreeTag.ICON_BRANCH_COLLAPSED, tag
                .getResolvedIconHandler().getIconName(
                        new DefaultConfigurationNode(), false, false));
    }

    /**
     * Tests adding icons.
     */
    @Test
    public void testAddIcon()
    {
        final int count = 20;
        final String namePrefix = "iconName";
        final String iconPrefix = "ICON";
        for (int i = 0; i < count; i++)
        {
            tag.addIcon(namePrefix + i, iconPrefix + i);
        }
        Map<String, Object> icons = tag.getIcons();
        assertEquals("Wrong number of icons", count, icons.size());
        for (int i = 0; i < count; i++)
        {
            assertEquals("Wrong icon for key " + i, iconPrefix + i, icons
                    .get(namePrefix + i));
        }
    }

    /**
     * Tests adding an icon when no name is provided. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddIconNoName()
    {
        tag.addIcon(null, BEAN_NAME);
    }

    /**
     * Tests adding an icon when the icon is null. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddIconNoIcon()
    {
        tag.addIcon(BEAN_NAME, null);
    }

    /**
     * Tests modifying the map with the icons. This should not be possible.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetIconsModify()
    {
        tag.addIcon(BEAN_NAME, BEAN_NAME);
        tag.getIcons().clear();
    }

    /**
     * Tests whether an invalid scroll size specification is detected.
     */
    @Test(expected = FormBuilderException.class)
    public void testInvalidScrollSize() throws FormBuilderException,
            JellyTagException
    {
        tag.setTreeModel(new HierarchicalConfiguration());
        tag.setScrollHeight("invalid scroll height!");
        tag.createComponentHandler(getComponentManager(), true);
    }
}
