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
package net.sf.jguiraffe.gui.platform.swing.builder.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.configuration.tree.DefaultConfigurationNode;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for SwingConfigurationTreeModel.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingConfigurationTreeModel.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingConfigurationTreeModel
{
    /** Constant for the name of the root element. */
    private static final String ELEM_TABLES = "tables";

    /** Constant for the name of a table element. */
    private static final String ELEM_TABLE = "table";

    /** Constant for the name of a field element. */
    private static final String ELEM_FIELD = "field";

    /** An array with the numbers of fields for the test tables. */
    private static final int[] FIELD_COUNT = {
            4, 5, 6
    };

    /** Stores the configuration. */
    private HierarchicalConfiguration config;

    /** The root node of the test nodes. */
    private ConfigurationNode root;

    /** The model to be tested. */
    private SwingConfigurationTreeModel model;

    /**
     * Fills some test data into the configuration and creates the tree model.
     * The test data consists of a root node representing a tables element. It
     * has children representing a single table each. The tables are named with
     * the prefix <code>ELEM_TABLE</code> and a numeric index. Each table
     * element has a number of field elements as children that are also named
     * using a prefix (<code>ELEM_FIELD</code>) and a numeric index.
     */
    @Before
    public void setUp() throws Exception
    {
        root = new DefaultConfigurationNode(ELEM_TABLES);
        for (int i = 0; i < FIELD_COUNT.length; i++)
        {
            DefaultConfigurationNode ndTable = new DefaultConfigurationNode(
                    ELEM_TABLE, ELEM_TABLE + i);
            for (int j = 0; j < FIELD_COUNT[i]; j++)
            {
                ndTable.addChild(new DefaultConfigurationNode(ELEM_FIELD,
                        ELEM_FIELD + j));
            }
            root.addChild(ndTable);
        }
        config = new HierarchicalConfiguration();
        DefaultConfigurationNode top = new DefaultConfigurationNode();
        top.addChild(root);
        config.setRootNode(top);
        model = new SwingConfigurationTreeModel(config);
    }

    /**
     * Tests creating an instance without a configuration. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoConfig()
    {
        new SwingConfigurationTreeModel(null);
    }

    /**
     * Tests querying a child node.
     */
    @Test
    public void testGetChild()
    {
        for (int i = 0; i < FIELD_COUNT.length; i++)
        {
            Object child = model.getChild(root, i);
            ConfigurationNode node = (ConfigurationNode) child;
            assertEquals("Wrong node name", ELEM_TABLE, node.getName());
            assertEquals("Wrong node value", ELEM_TABLE + i, node.getValue());
        }
    }

    /**
     * Tests querying the number of child nodes of a node.
     */
    @Test
    public void testGetChildCount()
    {
        assertEquals("Wrong children of root node", FIELD_COUNT.length, model
                .getChildCount(root));
        for (int i = 0; i < FIELD_COUNT.length; i++)
        {
            assertEquals("Wrong number of children for table " + i,
                    FIELD_COUNT[i], model.getChildCount(root.getChild(i)));
        }
    }

    /**
     * Tests querying the index of a child node.
     */
    @Test
    public void testGetIndexOfChild()
    {
        for (int i = 0; i < FIELD_COUNT.length; i++)
        {
            ConfigurationNode node = root.getChild(i);
            assertEquals("Wrong index for table " + i, i, model
                    .getIndexOfChild(root, node));
            for (int j = 0; j < FIELD_COUNT[i]; j++)
            {
                assertEquals("Wrong index for field " + j, j, model
                        .getIndexOfChild(node, node.getChild(j)));
            }
        }
    }

    /**
     * Tests querying the index when the child does not belong to this parent.
     */
    @Test
    public void testGetIndexNoChild()
    {
        ConfigurationNode node = root.getChild(0);
        ConfigurationNode child = root.getChild(1).getChild(0);
        assertEquals("Wrong result for non child", -1, model.getIndexOfChild(
                node, child));
    }

    /**
     * Tests querying the index when the parent node is null.
     */
    @Test
    public void testGetIndexNullParent()
    {
        assertEquals("Wrong index for null parent", -1, model.getIndexOfChild(
                null, root.getChild(1)));
    }

    /**
     * Tests querying the index when the child node is null.
     */
    @Test
    public void testGetIndexNullChild()
    {
        assertEquals("Wrong index for null child", -1, model.getIndexOfChild(
                config.getRootNode(), null));
    }

    /**
     * Tests querying the root object of the model.
     */
    @Test
    public void testGetRoot()
    {
        assertEquals("Wrong root object", config.getRootNode(), model.getRoot());
    }

    /**
     * Tests checking whether a node is a leaf node.
     */
    @Test
    public void testIsLeaf()
    {
        assertFalse("Root is a leaf", model.isLeaf(root));
        for (Object child : root.getChildren())
        {
            assertFalse("Table node is leaf", model.isLeaf(child));
            for (Object field : ((ConfigurationNode) child).getChildren())
            {
                assertTrue("Field node is no leaf", model.isLeaf(field));
            }
        }
    }

    /**
     * Tests whether the path stored in the given event is consistent.
     *
     * @param e the event
     * @return the last path component
     */
    private ConfigurationNode checkEventPath(TreeModelEvent e)
    {
        Object[] path = e.getPath();
        assertTrue("No path", path.length > 0);
        for (int i = path.length - 1; i > 1; i--)
        {
            ConfigurationNode node = (ConfigurationNode) path[i];
            assertEquals("Wrong parent for path component " + i, node
                    .getParentNode(), path[i - 1]);
        }
        assertTrue("Wrong path root", path[0] == model.getRoot());
        assertEquals("Wrong event source", model, e.getSource());
        return (ConfigurationNode) path[path.length - 1];
    }

    /**
     * Tries adding a null listener. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddTreeModelListenerNull()
    {
        model.addTreeModelListener(null);
    }

    /**
     * Helper method for checking an event of an arbitrary type received by the
     * test listener.
     *
     * @param data the event data object
     * @param expNode the expected node in the path
     * @param expType the expected event type
     */
    private void checkEventWithType(EventData data, ConfigurationNode expNode,
            EventType expType)
    {
        ConfigurationNode node = checkEventPath(data.event);
        assertEquals("Wrong affected node", expNode, node);
        assertEquals("Wrong event type", expType, data.type);
    }

    /**
     * Helper method for checking an event received by the test listener.
     *
     * @param data the event data object
     * @param expNode the expected node in the path
     */
    private void checkEvent(EventData data, ConfigurationNode expNode)
    {
        checkEventWithType(data, expNode, EventType.STRUCTURE_CHANGED);
    }

    /**
     * Helper method for checking the number of events received by a listener.
     * This method assumes the standard for tests, i.e. only one event is
     * expected.
     *
     * @param l the listener
     * @return the event data for the single event
     */
    private EventData checkEventCount(TreeModelListenerTestImpl l)
    {
        List<EventData> events = l.getEvents();
        assertEquals("Wrong number of events", 1, events.size());
        return events.get(0);
    }

    /**
     * Helper method for checking the events received by the test listener. This
     * method assumes that only a single event is expected that refers to the
     * specified node.
     *
     * @param l the listener
     * @param expNode the expected node
     */
    private void checkListener(TreeModelListenerTestImpl l,
            ConfigurationNode expNode)
    {
        checkEvent(checkEventCount(l), expNode);
    }

    /**
     * Helper method for checking an event that is fired when no specific
     * information is available. In this case a structure changed event with the
     * root node is fired.
     *
     * @param data the event data object
     */
    private void checkUnspecifcEvent(EventData data)
    {
        checkEvent(data, (ConfigurationNode) model.getRoot());
    }

    /**
     * Helper method for checking the events received by a test listener when
     * only a single generic event is expected.
     *
     * @param l the listener
     */
    private void checkListenerUnspecific(TreeModelListenerTestImpl l)
    {
        checkUnspecifcEvent(checkEventCount(l));
    }

    /**
     * Tests whether nodes inserted events are fired.
     */
    @Test
    public void testEventNodesInserted()
    {
        TreeModelListenerTestImpl l = new TreeModelListenerTestImpl();
        model.addTreeModelListener(l);
        config.addProperty("tables.table(-1)", "newTable");
        checkListenerUnspecific(l);
    }

    /**
     * Tests the event fired for a clear property operation when the affected
     * node can be determined.
     */
    @Test
    public void testEventClearPropertySpecific()
    {
        TreeModelListenerTestImpl l = new TreeModelListenerTestImpl();
        model.addTreeModelListener(l);
        config.clearProperty("tables.table(0).field(0)");
        checkListener(l, root.getChild(0));
    }

    /**
     * Tests the event fired for a clear property operation if the affected
     * node cannot be determined.
     */
    @Test
    public void testEventClearPropertyUnspecific()
    {
        TreeModelListenerTestImpl l = new TreeModelListenerTestImpl();
        model.addTreeModelListener(l);
        config.clearProperty("tables.table.field(0)");
        checkListener(l, root);
    }

    /**
     * Tests the event fired for a clear tree operation when the affected node
     * can be determined.
     */
    @Test
    public void testEventClearTreeSpecific()
    {
        TreeModelListenerTestImpl l = new TreeModelListenerTestImpl();
        model.addTreeModelListener(l);
        config.clearTree("tables.table(0).field(0)");
        checkListener(l, root.getChild(0));
    }

    /**
     * Tests the event fired for a clear tree operation when the affected node
     * cannot be determined.
     */
    @Test
    public void testEventClearTreeUnspecific()
    {
        TreeModelListenerTestImpl l = new TreeModelListenerTestImpl();
        model.addTreeModelListener(l);
        config.clearTree("tables.table.field(0)");
        checkListener(l, root);
    }

    /**
     * Tests the event fired for a set property operation when the affected node
     * can be determined.
     */
    @Test
    public void testEventSetPropertySpecific()
    {
        TreeModelListenerTestImpl l = new TreeModelListenerTestImpl();
        model.addTreeModelListener(l);
        config.setProperty("tables.table(0).field(0)", "newValue");
        checkListener(l, root.getChild(0));
    }

    /**
     * Tests the event fired for a set property operation when the affected node
     * cannot be determined.
     */
    @Test
    public void testEventSetPropertyUnspecific()
    {
        TreeModelListenerTestImpl l = new TreeModelListenerTestImpl();
        model.addTreeModelListener(l);
        config.setProperty("tables.table.name.test.new.property", "newValue");
        checkListenerUnspecific(l);
    }

    /**
     * Tests the event fired for an add nodes operation when the affected node
     * can be determined.
     */
    @Test
    public void testEventAddNodesSpecific()
    {
        TreeModelListenerTestImpl l = new TreeModelListenerTestImpl();
        model.addTreeModelListener(l);
        ConfigurationNode newNode = new DefaultConfigurationNode(ELEM_FIELD,
                "newField");
        config.addNodes("tables.table(0)", Collections.singletonList(newNode));
        checkListener(l, root.getChild(0));
    }

    /**
     * Tests the event fired for an add nodes operation when the affected node
     * cannot be determined.
     */
    @Test
    public void testEventAddNodesUnspecific()
    {
        TreeModelListenerTestImpl l = new TreeModelListenerTestImpl();
        model.addTreeModelListener(l);
        ConfigurationNode newNode = new DefaultConfigurationNode(ELEM_FIELD,
                "newField");
        config.addNodes("tables.table.name.test.new.property", Collections
                .singletonList(newNode));
        checkListenerUnspecific(l);
    }

    /**
     * Tests receiving multiple events. We need to check that information for
     * the first event is reset before the second is sent out.
     */
    @Test
    public void testMultipleEvents()
    {
        TreeModelListenerTestImpl l = new TreeModelListenerTestImpl();
        model.addTreeModelListener(l);
        config.clearProperty("tables.table(0).field(0)");
        config.clearProperty("tables.table.field(0)");
        List<EventData> events = l.getEvents();
        assertEquals("Wrong number of events", 2, events.size());
        assertEquals("Wrong event type 1", EventType.STRUCTURE_CHANGED, events
                .get(0).type);
        checkEvent(events.get(1), root);
    }

    /**
     * Tests removing an event listener.
     */
    @Test
    public void testRemoveTreeModelListener()
    {
        TreeModelListenerTestImpl l = new TreeModelListenerTestImpl();
        model.addTreeModelListener(l);
        config.clearProperty("tables.table(0).field(0)");
        l.getEvents();
        model.removeTreeModelListener(l);
        config.clearProperty("tables.table.field(0)");
        List<EventData> events = l.getEvents();
        assertEquals("Wrong number of events", 1, events.size());
    }

    /**
     * Tests editing a normal node.
     */
    @Test
    public void testValueForPathChangedNormalNode() throws Exception
    {
        TreeModelListenerTestImpl l = new TreeModelListenerTestImpl();
        model.addTreeModelListener(l);
        final Object newValue = "NewNameForThisNode";
        final int changeIdx = 1;
        final Object[] nodes = {
                model.getRoot(), root, root.getChild(changeIdx)
        };
        SwingUtilities.invokeAndWait(new Runnable()
        {
            public void run()
            {
                model.valueForPathChanged(new TreePath(nodes), newValue);
            }
        });
        assertEquals("Node value not changed", newValue, root.getChild(
                changeIdx).getName());
        List<EventData> events = l.getEvents();
        assertEquals("Wrong number of events", 1, events.size());
        checkEventWithType(events.get(0), root, EventType.NODES_CHANGED);
        int[] indices = events.get(0).event.getChildIndices();
        assertEquals("Wrong number of indices", 1, indices.length);
        assertEquals("Wrong index", changeIdx, indices[0]);
        Object[] children = events.get(0).event.getChildren();
        assertEquals("Wrong number of children", 1, children.length);
        assertEquals("Wrong child", root.getChild(changeIdx), children[0]);
    }

    /**
     * Tests editing a node if nothing has changed.
     */
    @Test
    public void testValueForPathChangedNoChange()
    {
        TreeModelListenerTestImpl l = new TreeModelListenerTestImpl();
        model.addTreeModelListener(l);
        ConfigurationNode node = root.getChild(0);
        Object[] nodes = {
                model.getRoot(), root, node
        };
        model.valueForPathChanged(new TreePath(nodes), node.getValue());
        List<EventData> events = l.getEvents();
        assertTrue("Received events", events.isEmpty());
    }

    /**
     * Tests the model's reaction on editing the root node.
     */
    @Test
    public void testValueForPathChangedRootNode()
    {
        TreeModelListenerTestImpl l = new TreeModelListenerTestImpl();
        model.addTreeModelListener(l);
        final Object newValue = "NewNameForRoot";
        model.valueForPathChanged(new TreePath(model.getRoot()), newValue);
        assertEquals("Node value not changed", newValue,
                ((ConfigurationNode) model.getRoot()).getName());
        checkListenerUnspecific(l);
    }

    /**
     * An enumeration for recording the type of received tree model events.
     */
    private static enum EventType {
        NODES_CHANGED, NODES_INSERTED, NODES_REMOVED, STRUCTURE_CHANGED
    }

    /**
     * A simple data class for storing information about an event received by a
     * tree model listener.
     */
    private static class EventData
    {
        /** The event. */
        public TreeModelEvent event;

        /** The event type. */
        public EventType type;

        public EventData(TreeModelEvent e, EventType t)
        {
            event = e;
            type = t;
        }
    }

    /**
     * A test implementation of the TreeModelListener interface used for testing
     * whether the expected events are received.
     */
    private static class TreeModelListenerTestImpl implements TreeModelListener
    {
        /** A collection with the events received. */
        private final Collection<EventData> events = Collections
                .synchronizedCollection(new LinkedList<EventData>());

        public void treeNodesChanged(TreeModelEvent e)
        {
            addEvent(e, EventType.NODES_CHANGED);
        }

        public void treeNodesInserted(TreeModelEvent e)
        {
            addEvent(e, EventType.NODES_INSERTED);
        }

        public void treeNodesRemoved(TreeModelEvent e)
        {
            addEvent(e, EventType.NODES_REMOVED);
        }

        public void treeStructureChanged(TreeModelEvent e)
        {
            addEvent(e, EventType.STRUCTURE_CHANGED);
        }

        /**
         * Returns a collection with the events received by this listener. Note
         * that the events have to be retrieved in the event dispatch thread to
         * avoid race conditions.
         *
         * @return the collection with the events
         */
        public List<EventData> getEvents()
        {
            final List<EventData> result = new LinkedList<EventData>();
            try
            {
                SwingUtilities.invokeAndWait(new Runnable()
                {
                    public void run()
                    {
                        result.addAll(events);
                    }
                });
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Error when obtaining events!");
            }
            return result;
        }

        /**
         * Adds an event that was passed to this listener to the internal list.
         * Also checks whether we are in the event dispatch thread.
         *
         * @param e the event
         * @param type the event type
         */
        private void addEvent(TreeModelEvent e, EventType type)
        {
            assertTrue("Not in event dispatch thread", SwingUtilities
                    .isEventDispatchThread());
            events.add(new EventData(e, type));
        }
    }
}
