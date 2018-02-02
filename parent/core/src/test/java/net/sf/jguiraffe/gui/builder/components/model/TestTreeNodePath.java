/*
 * Copyright 2006-2018 The JGUIraffe Team.
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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.jguiraffe.JGuiraffeTestHelper;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.configuration.tree.DefaultConfigurationKey;
import org.apache.commons.configuration.tree.DefaultConfigurationNode;
import org.apache.commons.configuration.tree.DefaultExpressionEngine;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class for TreeNodePath.
 *
 * @author Oliver Heger
 * @version $Id: TestTreeNodePath.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestTreeNodePath
{
    /** An array with the names of the test tables. */
    private static final String[] TABLE_NAMES = {
            "users", "documents"
    };

    /** An array with the names of the table fields. */
    private static final String[][] TABLE_FIELDS = {
            {
                    "uid", "ucode", "firstName", "lastName", "lastLogin"
            },
            {
                    "docid", "docName", "description", "createdAt", "authorID",
                    "lastModified"
            }
    };

    /** Constant for the prefix path of the test key. */
    private static final String PATH_PREFIX = "tables(0).table(1)";

    /** Constant for the name of a child node. */
    private static final String CHILD_NAME = "field";

    /** Constant for the test key. */
    private static final String TEST_KEY = PATH_PREFIX + "." + CHILD_NAME
            + "(2)";

    /** Stores the root of the test node hierarchy. */
    private static ConfigurationNode root;

    /**
     * Creates the test node hierarchy.
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        root = new HierarchicalConfiguration.Node();
        ConfigurationNode ndTabs = new DefaultConfigurationNode("tables");
        root.addChild(ndTabs);
        for (int i = 0; i < TABLE_NAMES.length; i++)
        {
            ConfigurationNode ndTab = new DefaultConfigurationNode("table",
                    TABLE_NAMES[i]);
            ndTabs.addChild(ndTab);
            for (int j = 0; j < TABLE_FIELDS[i].length; j++)
            {
                ConfigurationNode ndField = new DefaultConfigurationNode(
                        CHILD_NAME, TABLE_FIELDS[i][j]);
                ndTab.addChild(ndField);
            }
        }
    }

    /**
     * Resolves the specified key and returns the corresponding node.
     *
     * @param key the key
     * @return the node for this key
     */
    private static ConfigurationNode nodeForKey(String key)
    {
        HierarchicalConfiguration config = new HierarchicalConfiguration();
        config.setRootNode(root);
        List<?> nodes = config.getExpressionEngine().query(root, key);
        assertEquals("Not unique key", 1, nodes.size());
        return (ConfigurationNode) nodes.get(0);
    }

    /**
     * Tests creating an instance with a null node. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoNode()
    {
        new TreeNodePath((ConfigurationNode) null);
    }

    /**
     * Tests creating an instance with a null node collection. This should cause
     * an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoCollection()
    {
        new TreeNodePath((Collection<ConfigurationNode>) null);
    }

    /**
     * Tests creating a path from a configuration node.
     */
    @Test
    public void testInitNode()
    {
        ConfigurationNode node = nodeForKey(TEST_KEY);
        TreeNodePath path = new TreeNodePath(node);
        assertEquals("Wrong target node", node, path.getTargetNode());
    }

    /**
     * Tests creating a path from a collection of nodes.
     */
    @Test
    public void testInitCollection()
    {
        ConfigurationNode node = nodeForKey(TEST_KEY);
        ConfigurationNode p1 = node.getParentNode();
        ConfigurationNode p2 = p1.getParentNode();
        ConfigurationNode p3 = p2.getParentNode();
        List<ConfigurationNode> nodes = new ArrayList<ConfigurationNode>();
        nodes.add(p3);
        nodes.add(p2);
        nodes.add(p1);
        nodes.add(node);
        TreeNodePath path = new TreeNodePath(nodes);
        assertEquals("Wrong target node", node, path.getTargetNode());
    }

    /**
     * Tests querying the size.
     */
    @Test
    public void testSize()
    {
        TreeNodePath path = new TreeNodePath(nodeForKey(TEST_KEY));
        assertEquals("Wrong size", 4, path.size());
    }

    /**
     * Tests querying the nodes of the path.
     */
    @Test
    public void testGetNodes()
    {
        TreeNodePath path = new TreeNodePath(nodeForKey(TEST_KEY));
        List<ConfigurationNode> nodes = path.getNodes();
        assertEquals("Wrong number of nodes", 4, nodes.size());
        assertEquals("Wrong root node", root, nodes.get(0));
        ConfigurationNode nd = nodes.get(1);
        assertEquals("Wrong node 1", "tables", nd.getName());
        nd = nodes.get(2);
        assertEquals("Wrong node 2", TABLE_NAMES[1], nd.getValue());
        nd = nodes.get(3);
        assertEquals("Wrong node 3", TABLE_FIELDS[1][2], nd.getValue());
    }

    /**
     * Tries to modify the list of nodes. This should cause an exception.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetNodesModify()
    {
        TreeNodePath path = new TreeNodePath(nodeForKey(TEST_KEY));
        path.getNodes().clear();
    }

    /**
     * Tests querying node names along the path.
     */
    @Test
    public void testGetNodeName()
    {
        TreeNodePath path = new TreeNodePath(nodeForKey(TEST_KEY));
        assertEquals("Wrong name 0", "tables", path.getNodeName(0));
        assertEquals("Wrong name 1", "table", path.getNodeName(1));
        assertEquals("Wrong name 2", "field", path.getNodeName(2));
    }

    /**
     * Tests the getNodeName() method when an invalid index is provided.
     */
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testGetNodeNameInvalidIndex()
    {
        TreeNodePath path = new TreeNodePath(nodeForKey(TEST_KEY));
        path.getNodeName(3);
    }

    /**
     * Tests querying node indices along the path.
     */
    @Test
    public void testGetNodeIndex()
    {
        TreeNodePath path = new TreeNodePath(nodeForKey(TEST_KEY));
        assertEquals("Wrong index 0", 0, path.getNodeIndex(0));
        assertEquals("Wrong index 1", 1, path.getNodeIndex(1));
        assertEquals("Wrong index 2", 2, path.getNodeIndex(2));
    }

    /**
     * Tests the getNodeIndex() method when an invalid index is provided.
     */
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testGetNodeIndexInvalidIndex()
    {
        TreeNodePath path = new TreeNodePath(nodeForKey(TEST_KEY));
        path.getNodeIndex(3);
    }

    /**
     * Tests the equals() implementation.
     */
    @Test
    public void testEquals()
    {
        TreeNodePath p1 = new TreeNodePath(root.getChild(0).getChild(1));
        JGuiraffeTestHelper.checkEquals(p1, p1, true);
        TreeNodePath p2 = new TreeNodePath(root.getChild(0).getChild(0));
        JGuiraffeTestHelper.checkEquals(p1, p2, false);
        p2 = new TreeNodePath(root.getChild(0).getChild(1));
        JGuiraffeTestHelper.checkEquals(p1, p2, true);
    }

    /**
     * Tests the equals() method when invalid objects are passed in.
     */
    @Test
    public void testEqualsInvalid()
    {
        JGuiraffeTestHelper.testTrivialEquals(new TreeNodePath(root.getChild(0)
                .getChild(1)));
    }

    /**
     * Tests transforming a path to a configuration key.
     */
    @Test
    public void testPathToKey()
    {
        TreeNodePath path = new TreeNodePath(nodeForKey(TEST_KEY));
        HierarchicalConfiguration config = new HierarchicalConfiguration();
        DefaultConfigurationKey key = new DefaultConfigurationKey(
                (DefaultExpressionEngine) config.getExpressionEngine());
        path.pathToKey(key);
        assertEquals("Wrong key", TEST_KEY, key.toString());
    }

    /**
     * Tests the pathToKey() method when a null key is passed in. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testPathToKeyNullKey()
    {
        TreeNodePath path = new TreeNodePath(nodeForKey(TEST_KEY));
        path.pathToKey(null);
    }

    /**
     * Tests the string representation of a path object.
     */
    @Test
    public void testToString()
    {
        TreeNodePath path = new TreeNodePath(nodeForKey(TEST_KEY));
        String s = path.toString();
        String p = TEST_KEY.replace('.', '/');
        p = p.replace('(', '[');
        p = p.replace(')', ']');
        assertTrue("Expected path not found: " + s, s.indexOf(p) >= 0);
    }

    /**
     * Helper method for testing whether a path object contains the expected
     * nodes.
     *
     * @param expected a list with the expected nodes
     * @param path the path to check
     */
    private void checkPathNodes(List<ConfigurationNode> expected,
            TreeNodePath path)
    {
        List<ConfigurationNode> actual = path.getNodes();
        assertEquals("Wrong size of path", expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++)
        {
            assertSame("Wrong node at " + i, expected.get(i), actual.get(i));
        }
    }

    /**
     * Tests whether the parent path can be obtained.
     */
    @Test
    public void testParentPath()
    {
        TreeNodePath path = new TreeNodePath(nodeForKey(TEST_KEY));
        TreeNodePath parent = path.parentPath();
        List<ConfigurationNode> nodes1 = path.getNodes();
        checkPathNodes(nodes1.subList(0, nodes1.size() - 1), parent);
        assertSame("Wrong parent node", path.getNodes().get(path.size() - 2),
                parent.getTargetNode());
    }

    /**
     * Tries to query the parent path of the root path. This should cause an
     * exception.
     */
    @Test(expected = IllegalStateException.class)
    public void testParentPathRoot()
    {
        TreeNodePath path = new TreeNodePath(root);
        path.parentPath();
    }

    /**
     * Tests whether a node can be appended to a path.
     */
    @Test
    public void testAppendNode()
    {
        TreeNodePath path = new TreeNodePath(nodeForKey(PATH_PREFIX));
        ConfigurationNode child =
                (ConfigurationNode) path.getTargetNode().getChildren().get(0);
        TreeNodePath path2 = path.append(child);
        assertSame("Wrong target node", child, path2.getTargetNode());
        List<ConfigurationNode> nodes =
                new ArrayList<ConfigurationNode>(path.getNodes());
        nodes.add(child);
        checkPathNodes(nodes, path2);
    }

    /**
     * Tries to append a null node to a path.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAppendNodeNull()
    {
        TreeNodePath path = new TreeNodePath(nodeForKey(PATH_PREFIX));
        path.append((ConfigurationNode) null);
    }

    /**
     * Tries to append a configuration node to a path which is not a child node
     * of the current target node.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAppendNodeNoChild()
    {
        TreeNodePath path = new TreeNodePath(nodeForKey(PATH_PREFIX));
        path.append(new DefaultConfigurationNode());
    }

    /**
     * Tests whether a child node can be appended to a path.
     */
    @Test
    public void testAppendChild()
    {
        TreeNodePath path = new TreeNodePath(nodeForKey(PATH_PREFIX));
        TreeNodePath path2 = path.append(CHILD_NAME, 1);
        List<ConfigurationNode> nodes =
                new ArrayList<ConfigurationNode>(path.getNodes());
        ConfigurationNode nd = nodeForKey(PATH_PREFIX);
        nodes.add((ConfigurationNode) nd.getChildren(CHILD_NAME).get(1));
        checkPathNodes(nodes, path2);
    }

    /**
     * Tests whether the first child node is used if no index is provided.
     */
    @Test
    public void testAppendChildDefaultIdx()
    {
        TreeNodePath path = new TreeNodePath(nodeForKey(PATH_PREFIX));
        TreeNodePath path2 = path.append(CHILD_NAME);
        assertEquals("Wrong node added", TABLE_FIELDS[1][0], path2
                .getTargetNode().getValue());
    }

    /**
     * Tries to add a null child to a path.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAppendChildNull()
    {
        TreeNodePath path = new TreeNodePath(nodeForKey(PATH_PREFIX));
        path.append((String) null);
    }

    /**
     * Tries to add an unknown child node to a path.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAppendChildUnknown()
    {
        TreeNodePath path = new TreeNodePath(nodeForKey(PATH_PREFIX));
        path.append("an unknown child!");
    }

    /**
     * Tries to add a child with an invalid index to a path.
     */
    @Test(expected = IndexOutOfBoundsException.class)
    public void testAppendChildInvalidIndex()
    {
        TreeNodePath path = new TreeNodePath(nodeForKey(PATH_PREFIX));
        path.append(CHILD_NAME, TABLE_FIELDS[1].length + 1);
    }
}
