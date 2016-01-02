/*
 * Copyright 2006-2016 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.components.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.configuration.tree.DefaultConfigurationNode;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code TreeConfigurationChangeHandler}.
 *
 * @author Oliver Heger
 * @version $Id: $
 */
public class TestTreeConfigurationChangeHandler
{
    /** The model change listener. */
    private TreeModelChangeListenerTestImpl listener;

    /** The configuration. */
    private HierarchicalConfiguration config;

    /** The handler to be tested. */
    private TreeConfigurationChangeHandler handler;

    @Before
    public void setUp() throws Exception
    {
        listener = new TreeModelChangeListenerTestImpl();
        config = setUpConfig();
        handler = new TreeConfigurationChangeHandler(config, listener);
        config.addConfigurationListener(handler);
    }

    /**
     * Creates a configuration containing some test data. The configuration
     * describes database tables with some columns.
     *
     * @return the test configuration
     */
    private static HierarchicalConfiguration setUpConfig()
    {
        HierarchicalConfiguration c = new HierarchicalConfiguration();
        c.addProperty("tables.table.name", "users");
        c.addProperty("tables.table(-1).name", "docs");
        c.addProperty("tables.table(0).fields.field(-1).name", "usrID");
        c.addProperty("tables.table(0).fields.field(-1).name", "usrName");
        c.addProperty("tables.table(1).fields.field(-1).name", "docID");
        c.addProperty("tables.table(1).fields.field(-1).name", "name");
        c.addProperty("tables.table(1).fields.field(-1).name", "content");
        c.addProperty("data.mode", "test");
        return c;
    }

    /**
     * Checks whether the expected changed node was sent to the listener.
     *
     * @param nodeName the expected name of the changed node
     */
    private void checkChangedNode(String nodeName)
    {
        assertEquals("Wrong changed node", nodeName, listener.getChangedNode()
                .getName());
    }

    /**
     * Tests whether changes related to an added property are handled correctly
     * if the key can be uniquely determined.
     */
    @Test
    public void testAddPropertyUnique()
    {
        config.addProperty("data.version", "1.0");
        checkChangedNode("data");
    }

    /**
     * Tests whether changes related to an added property are handled correctly
     * if there are multiple nodes matching the key.
     */
    @Test
    public void testAddPropertyAmbiguous()
    {
        config.addProperty("tables.table.name", "roles");
        checkChangedNode("tables");
    }

    /**
     * Tests a change notification caused by an added property if the key cannot
     * be matched to an existing node.
     */
    @Test
    public void testAddPropertyNoMatch()
    {
        config.addProperty("tables.table(0).fields.field(-1).name", "firstName");
        assertEquals("Wrong changed node", config.getRootNode(),
                listener.getChangedNode());
    }

    /**
     * Tests a change notification caused by an add nodes operation if the key
     * is unique.
     */
    @Test
    public void testAddNodesUnique()
    {
        DefaultConfigurationNode nd1 =
                new DefaultConfigurationNode("version", "1.0");
        DefaultConfigurationNode nd2 =
                new DefaultConfigurationNode("date", "2013-12-08");
        config.addNodes("data", Arrays.asList(nd1, nd2));
        checkChangedNode("data");
    }

    /**
     * Tests a change notification caused by an add nodes operation if there are
     * multiple matching nodes.
     */
    @Test
    public void testAddNodesAmbiguous()
    {
        DefaultConfigurationNode nd1 =
                new DefaultConfigurationNode("field", "col1");
        DefaultConfigurationNode nd2 =
                new DefaultConfigurationNode("field", "col2");
        config.addNodes("tables.table.fields", Arrays.asList(nd1, nd2));
        checkChangedNode("tables");
    }

    /**
     * Tests a change event caused by a clear property operation.
     */
    @Test
    public void testClearProperty()
    {
        config.clearProperty("data.mode");
        checkChangedNode("data");
    }

    /**
     * Tests a change event caused by a clear tree operation.
     */
    @Test
    public void testClearTree()
    {
        config.clearTree("data.mode");
        checkChangedNode("data");
    }

    /**
     * Tests a change event caused by a setProperty() operation.
     */
    @Test
    public void testSetProperty()
    {
        config.setProperty("data.mode", "production");
        checkChangedNode("data");
    }

    /**
     * Tests the handling of another event which is not explicitly supported.
     */
    @Test
    public void testOtherEvent()
    {
        config.clear();
        assertEquals("Wrong changed node", config.getRootNode(),
                listener.getChangedNode());
    }

    /**
     * Tests findCommonParentNode() for the corner case that the nodes do not
     * belong to the name node structure. This should not happen in practice,
     * but we want to be on the safe side.
     */
    @Test
    public void testFindCommonParentNodeDisjunct()
    {
        ConfigurationNode nd1 = config.getRootNode().getChild(0).getChild(0);
        ConfigurationNode nd2 =
                new DefaultConfigurationNode("AnotherNode", "?");
        assertEquals("Wrong common parent (1)", config.getRootNode(),
                handler.findCommonParent(nd1, nd2));
        assertEquals("Wrong common parent (2)", config.getRootNode(),
                handler.findCommonParent(nd2, nd1));
    }

    /**
     * Obtains the single configuration node referenced by the passed in key. If
     * the key does not reference a single node, this method fails.
     *
     * @param key the key
     * @return the node referenced by this key
     */
    private ConfigurationNode fetchNode(String key)
    {
        List<ConfigurationNode> nodes =
                config.getExpressionEngine().query(config.getRootNode(), key);
        assertEquals("Unexpected number of nodes", 1, nodes.size());
        return nodes.get(0);
    }

    /**
     * Returns a node which can be used for testing the changeNodeName() method.
     *
     * @return the test node
     */
    private ConfigurationNode nodeNameTestNode()
    {
        return fetchNode("tables.table(0)");
    }

    /**
     * Tests whether the name of a node can be changed.
     */
    @Test
    public void testChangeNodeNameOtherName()
    {
        ConfigurationNode node = nodeNameTestNode();
        String newName = "db-table";
        assertTrue("Wrong result", handler.changeNodeName(node, newName));
        assertEquals("Name was not changed", newName, node.getName());
        assertNotNull("No parent node", node.getParentNode());
    }

    /**
     * Tests changeNodeName() if the same name is passed in.
     */
    @Test
    public void testChangedNodeNameSameName()
    {
        ConfigurationNode node = nodeNameTestNode();
        String oldName = node.getName();
        assertFalse("Wrong result", handler.changeNodeName(node, oldName));
        assertEquals("Name was changed", oldName, node.getName());
    }

    /**
     * A test listener implementation which just records the received nodes.
     */
    private static class TreeModelChangeListenerTestImpl implements
            TreeModelChangeListener
    {
        /** The node affected by a change event. */
        private ConfigurationNode changedNode;

        /**
         * Returns the node affected by a change.
         *
         * @return the changed node
         */
        public ConfigurationNode getChangedNode()
        {
            return changedNode;
        }

        /**
         * Sets the node affected by a change.
         *
         * @param changedNode the changed node
         */
        public void setChangedNode(ConfigurationNode changedNode)
        {
            this.changedNode = changedNode;
        }

        /**
         * Records the changed node. It is expected that only a single change
         * notification is received.
         *
         * @param node the changed node
         */
        public void treeModelChanged(ConfigurationNode node)
        {
            if (getChangedNode() != null)
            {
                fail("Too many changed nodes reported!");
            }
            setChangedNode(node);
        }
    }
}
