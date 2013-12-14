/*
 * Copyright 2006-2013 The JGUIraffe Team.
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

import java.awt.Container;
import java.util.HashMap;

import javax.swing.JTextField;
import javax.swing.JTree;

import net.sf.jguiraffe.gui.builder.components.tags.TreeIconHandler;

import org.apache.commons.configuration.tree.ConfigurationNode;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class for {@code SwingTreeCellEditor}.
 *
 * @author Oliver Heger
 * @version $Id: $
 */
public class TestSwingTreeCellEditor
{
    /** Constant for a test node name. */
    private static final String NODE_NAME = "TestNodeName";

    /** A test node. */
    private static ConfigurationNode node;

    /** A mock node formatter. */
    private static SwingTreeNodeFormatter formatter;

    /** The editor to be tested. */
    private SwingTreeCellEditor editor;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        node = EasyMock.createMock(ConfigurationNode.class);
        formatter = EasyMock.createMock(SwingTreeNodeFormatter.class);
        EasyMock.expect(formatter.textForNode(node)).andReturn(NODE_NAME)
                .anyTimes();
        EasyMock.replay(node, formatter);
    }

    @Before
    public void setUp() throws Exception
    {
        TreeIconHandler handler =
                EasyMock.createNiceMock(TreeIconHandler.class);
        EasyMock.replay(handler);
        SwingTreeCellRenderer renderer =
                new SwingTreeCellRenderer(handler,
                        new HashMap<String, Object>(), formatter);
        editor = new SwingTreeCellEditor(new JTree(), renderer);
    }

    /**
     * Tests whether the editor component is initialized with the correct text.
     */
    @Test
    public void testGetTreeCellEditorComponent()
    {
        Container container =
                (Container) editor.getTreeCellEditorComponent(new JTree(),
                        node, false, false, true, 0);
        JTextField comp = (JTextField) container.getComponents()[0];
        assertEquals("Wrong text", NODE_NAME, comp.getText());
    }
}
