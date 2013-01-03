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
package net.sf.jguiraffe.gui.platform.swing.builder.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Component;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import net.sf.jguiraffe.gui.builder.components.tags.TreeIconHandler;

import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.configuration.tree.DefaultConfigurationNode;
import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class for SwingTreeCellRenderer.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingTreeCellRenderer.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingTreeCellRenderer
{
    /** Constant for the name of the test node. */
    private static final String NODE_NAME = "TestNode";

    /** Constant for a test icon name. */
    private static final String ICON_NAME = "MyIcon";

    /** Constant for the resource name of the test icon. */
    private static final String ICON_RESOURCE = "/icon.gif";

    /** Stores the test icon. */
    private static Icon icon;

    @BeforeClass
    public static void beforeClass()
    {
        icon = loadIcon();
    }

    /**
     * Creates a test configuration node.
     *
     * @return the node
     */
    private ConfigurationNode setUpNode()
    {
        return new DefaultConfigurationNode(NODE_NAME);
    }

    /**
     * Loads the test icon.
     *
     * @return the test icon
     */
    private static Icon loadIcon()
    {
        URL iconUrl = TestSwingTreeCellRenderer.class
                .getResource(ICON_RESOURCE);
        assertNotNull("Test icon not found", iconUrl);
        return new ImageIcon(iconUrl);
    }

    /**
     * Creates a map that contains the test icon.
     *
     * @return the map with the icon
     */
    private Map<String, Object> setUpIconMap()
    {
        Map<String, Object> icons = new HashMap<String, Object>();
        icons.put(ICON_NAME, icon);
        return icons;
    }

    /**
     * Creates a mock for an icon handler and initializes it to expect a request
     * for an icon.
     *
     * @param node the configuration node
     * @return the mock
     */
    private TreeIconHandler setUpIconHandler(ConfigurationNode node)
    {
        TreeIconHandler handler = EasyMock.createMock(TreeIconHandler.class);
        EasyMock.expect(handler.getIconName(node, false, true)).andReturn(
                ICON_NAME);
        EasyMock.replay(handler);
        return handler;
    }

    /**
     * Helper method for invoking the renderer.
     *
     * @param renderer the renderer
     * @param node the current node
     * @return the renderer component
     */
    private JLabel fetchComponent(TreeCellRenderer renderer,
            ConfigurationNode node)
    {
        Component c = renderer.getTreeCellRendererComponent(new JTree(), node,
                false, false, true, 1, false);
        assertTrue("Wrong renderer component: " + c, c instanceof JLabel);
        return (JLabel) c;
    }

    /**
     * Tests creating an instance without an icon handler. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoHandler()
    {
        new SwingTreeCellRenderer(null, new HashMap<String, Object>());
    }

    /**
     * Tests creating an instance without an icon map. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoIconMap()
    {
        new SwingTreeCellRenderer(EasyMock
                .createNiceMock(TreeIconHandler.class), null);
    }

    /**
     * Tests querying the renderer when the icon handler returns the name of a
     * known icon.
     */
    @Test
    public void testGetTreeCellRendererComponentCustomIcon()
    {
        ConfigurationNode node = setUpNode();
        TreeIconHandler handler = setUpIconHandler(node);
        SwingTreeCellRenderer renderer = new SwingTreeCellRenderer(handler,
                setUpIconMap());
        JLabel comp = fetchComponent(renderer, node);
        assertEquals("Wrong text", NODE_NAME, comp.getText());
        assertEquals("Wrong icon", icon, comp.getIcon());
        EasyMock.verify(handler);
    }

    /**
     * Tests querying the renderer when the default icon is to be used.
     */
    @Test
    public void testGetTreeCellRendererComponentDefaultIcon()
    {
        ConfigurationNode node = setUpNode();
        TreeIconHandler handler = setUpIconHandler(node);
        SwingTreeCellRenderer renderer = new SwingTreeCellRenderer(handler,
                new HashMap<String, Object>());
        JLabel comp = fetchComponent(renderer, node);
        assertEquals("Wrong text", NODE_NAME, comp.getText());
        DefaultTreeCellRenderer r2 = new DefaultTreeCellRenderer();
        JLabel comp2 = fetchComponent(r2, node);
        assertEquals("Wrong icon", comp2.getIcon(), comp.getIcon());
    }
}
