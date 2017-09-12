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
package net.sf.jguiraffe.gui.platform.swing.builder.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.sf.jguiraffe.gui.builder.components.model.TreeExpandVetoException;
import net.sf.jguiraffe.gui.builder.components.model.TreeExpansionEvent;
import net.sf.jguiraffe.gui.builder.components.model.TreeNodePath;
import net.sf.jguiraffe.gui.builder.components.model.TreePreExpansionListener;
import net.sf.jguiraffe.gui.platform.swing.builder.event.ChangeListener;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for SwingTreeComponentHandler.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingTreeComponentHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingTreeComponentHandler
{
    /** Constant for the root property key. */
    private static final String PCKG_ROOT = "net.sf.jguiraffe";

    /** Constant for the application class. */
    private static final String KEY_APP = PCKG_ROOT + ".Application";

    /** Constant for the main class. */
    private static final String KEY_MAIN = PCKG_ROOT + ".Main";

    /** Constant for the name of the component. */
    private static final String NAME = "MyTree";

    /** Constant for the preferred scroll width. */
    private static final int SCROLL_WIDTH = 250;

    /** Constant for the preferred scroll height. */
    private static final int SCROLL_HEIGHT = 400;

    /** Stores the model for the tree. */
    private SwingConfigurationTreeModel model;

    /** Stores the tree. */
    private JTree tree;

    /** The handler to be tested. */
    private SwingTreeComponentHandler handler;

    @Before
    public void setUp() throws Exception
    {
        model = new SwingConfigurationTreeModel(setUpConfig());
        tree = new JTree(model);
        handler = new SwingTreeComponentHandler(tree, model, NAME,
                SCROLL_WIDTH, SCROLL_HEIGHT);
    }

    /**
     * Creates the configuration with some test properties.
     *
     * @return the configuration for the tree model
     */
    private HierarchicalConfiguration setUpConfig()
    {
        HierarchicalConfiguration config = new HierarchicalConfiguration();
        config.addProperty(PCKG_ROOT, Boolean.TRUE);
        config.addProperty(KEY_APP, Boolean.TRUE);
        config.addProperty(KEY_MAIN, Boolean.TRUE);
        return config;
    }

    /**
     * Returns a tree node path for the given configuration key.
     *
     * @param key the key
     * @return the node path for this key
     */
    private TreeNodePath nodePath(String key)
    {
        List<?> nodes = model.getConfiguration().getExpressionEngine().query(
                model.getConfiguration().getRootNode(), key);
        assertEquals("Wrong number of nodes for key " + key, 1, nodes.size());
        return new TreeNodePath((ConfigurationNode) nodes.get(0));
    }

    /**
     * Returns a tree path for the given configuration key.
     *
     * @param key the key
     * @return the path for this key
     */
    private TreePath treePath(String key)
    {
        TreeNodePath nodePath = nodePath(key);
        return new TreePath(nodePath.getNodes().toArray());
    }

    /**
     * Tests whether the specified path equals the given key.
     *
     * @param path the path
     * @param key the key
     */
    private boolean comparePath(TreePath path, String key)
    {
        TreeNodePath nodePath = nodePath(key);
        return path.getLastPathComponent().equals(nodePath.getTargetNode());
    }

    /**
     * Tests whether the correct outer component is returned. This must be a
     * scroll pane.
     */
    @Test
    public void testGetOuterComponent()
    {
        JScrollPane pane = (JScrollPane) handler.getOuterComponent();
        assertEquals("Tree not in scroll pane", tree, pane.getViewport()
                .getView());
        Dimension d = pane.getPreferredSize();
        assertEquals("Wrong scroll width", SCROLL_WIDTH, d.width);
        assertEquals("Wrong scroll height", SCROLL_HEIGHT, d.height);
    }

    /**
     * Tests whether the correct tree component is returned.
     */
    @Test
    public void testGetTree()
    {
        assertEquals("Wrong tree", tree, handler.getTree());
    }

    /**
     * Tests whether the correct model (i.e. the configuration) is returned.
     */
    @Test
    public void testGetModel()
    {
        assertEquals("Wrong configuration as tree model", model
                .getConfiguration(), handler.getModel());
    }

    /**
     * Tests querying the type for a single selection tree.
     */
    @Test
    public void testGetTypeSingleSelection()
    {
        tree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        assertEquals("Wrong type", TreeNodePath.class, handler.getType());
    }

    /**
     * Tests querying the type of a contiguous selection.
     */
    @Test
    public void testGetTypeContiguousSelection()
    {
        tree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
        assertEquals("Wrong type", TreeNodePath[].class, handler.getType());
    }

    /**
     * Tests querying the type of a discontiguous selection.
     */
    @Test
    public void testGetTypeDiscontiguousSelection()
    {
        tree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        assertEquals("Wrong type", TreeNodePath[].class, handler.getType());
    }

    /**
     * Tests clearing the selection.
     */
    @Test
    public void testClearSelection()
    {
        tree.setSelectionPath(treePath(KEY_APP));
        assertNotNull("No path selected", tree.getSelectionPath());
        handler.clearSelection();
        assertNull("Selection not cleared", tree.getSelectionPath());
    }

    /**
     * Tests querying the selected path.
     */
    @Test
    public void testGetSelectedPath()
    {
        tree.setSelectionPath(treePath(KEY_APP));
        assertEquals("Wrong selected path", nodePath(KEY_APP), handler
                .getSelectedPath());
    }

    /**
     * Tests the getSelectedPath() method when nothing is selected.
     */
    @Test
    public void testGetSelectedPathNoSelection()
    {
        tree.clearSelection();
        assertNull("Wrong selected path", handler.getSelectedPath());
    }

    /**
     * Helper method for testing whether the expected selected paths are
     * returned by the handler.
     *
     * @param expPaths the expected paths
     */
    private void checkSelectedNodePaths(TreeNodePath... expPaths)
    {
        TreeNodePath[] paths = handler.getSelectedPaths();
        assertEquals("Wrong number of selected paths", expPaths.length,
                paths.length);
        Set<TreeNodePath> pathSet = new HashSet<TreeNodePath>(Arrays
                .asList(paths));
        for (TreeNodePath p : expPaths)
        {
            assertTrue("Path not found " + p, pathSet.contains(p));
        }
    }

    /**
     * Tests querying the selected paths for a multiple selection.
     */
    @Test
    public void testGetSelectedPaths()
    {
        tree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        tree.addSelectionPath(treePath(KEY_APP));
        tree.addSelectionPath(treePath(KEY_MAIN));
        checkSelectedNodePaths(nodePath(KEY_APP), nodePath(KEY_MAIN));
    }

    /**
     * Tests querying the selected paths when nothing is selected.
     */
    @Test
    public void testGetSelectedPathsNoSelection()
    {
        tree.clearSelection();
        TreeNodePath[] paths = handler.getSelectedPaths();
        assertEquals("Wrong number of paths", 0, paths.length);
    }

    /**
     * Tests setting a selected path.
     */
    @Test
    public void testSetSelectedPath()
    {
        handler.setSelectedPath(nodePath(KEY_APP));
        assertTrue("Wrong selected path", comparePath(tree.getSelectionPath(),
                KEY_APP));
    }

    /**
     * Tries to set a null selected path. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetSelectedPathNull()
    {
        handler.setSelectedPath(null);
    }

    /**
     * Tests whether the specified array of tree paths contains exactly the
     * expected paths.
     *
     * @param selPaths the selected tree paths
     * @param keys the keys of the expected paths
     */
    private void checkSelectedTreePaths(TreePath[] selPaths, String... keys)
    {
        assertEquals("Wrong number of selected paths", keys.length,
                selPaths.length);

        for (String key : keys)
        {
            boolean found = false;

            for (TreePath p : selPaths)
            {
                if (comparePath(p, key))
                {
                    found = true;
                    break;
                }
            }

            assertTrue("Key not selected: " + key, found);
        }
    }

    /**
     * Tests whether the tree contains the specified selected paths. We do not
     * guarantee any order in which the paths are returned, so comparing the
     * paths is a bit tricky.
     *
     * @param keys the keys of the expected selected paths
     */
    private void checkSelectedTreePaths(String... keys)
    {
        TreePath[] selPaths = tree.getSelectionPaths();
        checkSelectedTreePaths(selPaths, keys);
    }

    /**
     * Tests adding a selected path.
     */
    @Test
    public void testAddSelectedPath()
    {
        handler.addSelectedPath(nodePath(KEY_APP));
        checkSelectedTreePaths(KEY_APP);
    }

    /**
     * Tests adding multiple selected paths.
     */
    @Test
    public void testAddSelectedPathMulti()
    {
        tree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        handler.addSelectedPath(nodePath(KEY_APP));
        handler.addSelectedPath(nodePath(KEY_MAIN));
        checkSelectedTreePaths(KEY_APP, KEY_MAIN);
    }

    /**
     * Tests calling addSelectedPath() with a null path. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddSelectedPathNull()
    {
        handler.addSelectedPath(null);
    }

    /**
     * Tests setting the data to null. This should clear the selection.
     */
    @Test
    public void testSetDataNull()
    {
        tree.setSelectionPath(treePath(KEY_APP));
        handler.setData(null);
        assertNull("Selection not cleared", tree.getSelectionPath());
    }

    /**
     * Tests setting the data of the tree to a single path.
     */
    @Test
    public void testSetDataSinglePath()
    {
        handler.setData(nodePath(KEY_APP));
        checkSelectedTreePaths(KEY_APP);
    }

    /**
     * Tests setting the data to an array of selected paths.
     */
    @Test
    public void testSetDataMultiPaths()
    {
        tree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        TreeNodePath[] paths = new TreeNodePath[] {
                nodePath(KEY_APP), nodePath(KEY_MAIN)
        };
        handler.setData(paths);
        checkSelectedTreePaths(KEY_APP, KEY_MAIN);
    }

    /**
     * Tests setting the data to an invalid object. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetDataInvalid()
    {
        handler.setData(this);
    }

    /**
     * Tests querying the handler's data in single selection mode.
     */
    @Test
    public void testGetDataSingle()
    {
        tree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setSelectionPath(treePath(KEY_APP));
        assertEquals("Wrong data", nodePath(KEY_APP), handler.getData());
    }

    /**
     * Tests querying the handler's data in single selection mode when nothing
     * is selected.
     */
    @Test
    public void testGetDataSingleNoSel()
    {
        tree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.clearSelection();
        assertNull("Found a selection", handler.getData());
    }

    /**
     * Tests querying the handler's data in multiple selection mode.
     */
    @Test
    public void testGetDataMulti()
    {
        tree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        tree.addSelectionPath(treePath(KEY_APP));
        tree.addSelectionPath(treePath(KEY_MAIN));
        TreeNodePath[] paths = (TreeNodePath[]) handler.getData();
        Set<TreeNodePath> pathSet = new HashSet<TreeNodePath>(Arrays
                .asList(paths));
        assertEquals("Wrong number of paths", 2, pathSet.size());
        assertTrue("Key 1 not found", pathSet.contains(nodePath(KEY_APP)));
        assertTrue("Key 2 not found", pathSet.contains(nodePath(KEY_MAIN)));
    }

    /**
     * Tests querying the handler's data in multiple selection mode when nothing
     * is selected.
     */
    @Test
    public void testGetDataMultiNoSel()
    {
        tree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        tree.clearSelection();
        assertNull("Found a selection", handler.getData());
    }

    /**
     * Tests expanding a node.
     */
    @Test
    public void testExpand()
    {
        TreePath p = treePath(PCKG_ROOT);
        assertFalse("Already expanded", tree.isExpanded(p));
        handler.expand(nodePath(PCKG_ROOT));
        assertTrue("Not expanded", tree.isExpanded(p));
    }

    /**
     * Tests expanding a null path. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testExpandNull()
    {
        handler.expand(null);
    }

    /**
     * Tests collapsing a node.
     */
    @Test
    public void testCollapse()
    {
        TreePath p = treePath(PCKG_ROOT);
        tree.expandPath(p);
        handler.collapse(nodePath(PCKG_ROOT));
        assertTrue("Not collapsed", tree.isCollapsed(p));
    }

    /**
     * Tests collapsing a null path. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCollapseNull()
    {
        handler.collapse(null);
    }

    /**
     * Tests adding a change listener for the tree.
     */
    @Test
    public void testAddChangeListener()
    {
        ChangeListener l = EasyMock.createMock(ChangeListener.class);
        EasyMock.replay(l);
        handler.addChangeListener(l);
        TreeSelectionListener[] listeners = tree.getTreeSelectionListeners();
        boolean found = false;
        for (TreeSelectionListener lst : listeners)
        {
            if (lst == handler)
            {
                found = true;
                break;
            }
        }
        assertTrue("Not registered as change listener", found);
        EasyMock.verify(l);
    }

    /**
     * Tests whether a tree selection event is propagated to the registered
     * change listener.
     */
    @Test
    public void testValueChanged()
    {
        ChangeListener l = EasyMock.createMock(ChangeListener.class);
        TreeSelectionEvent event = new TreeSelectionEvent(tree,
                treePath(KEY_APP), false, null, null);
        l.componentChanged(event);
        EasyMock.replay(l);
        handler.addChangeListener(l);
        handler.valueChanged(event);
        EasyMock.verify(l);
    }

    /**
     * Tests removing a change listener.
     */
    @Test
    public void testRemoveChangeListener()
    {
        ChangeListener l = EasyMock.createMock(ChangeListener.class);
        EasyMock.replay(l);
        handler.addChangeListener(l);
        handler.removeChangeListener(l);
        TreeSelectionEvent event = new TreeSelectionEvent(tree,
                treePath(KEY_APP), false, null, null);
        handler.valueChanged(event);
        EasyMock.verify(l);
    }

    /**
     * Tests whether the handler registered itself as tree expansion listener.
     */
    @Test
    public void testInitExpansionListener()
    {
        TreeExpansionListener[] listeners = tree.getTreeExpansionListeners();
        boolean found = false;
        for (TreeExpansionListener l : listeners)
        {
            if (l == handler)
            {
                found = true;
                break;
            }
        }
        assertTrue("Not registered as tree expansion listener", found);
    }

    /**
     * Tests whether expansion events are correctly fired.
     */
    @Test
    public void testAddExpansionListenerExpand()
    {
        TreeExpansionListenerTestImpl l = new TreeExpansionListenerTestImpl();
        handler.addExpansionListener(l);
        handler.treeExpanded(new javax.swing.event.TreeExpansionEvent(tree,
                treePath(KEY_APP)));
        l.checkEvent(KEY_APP, TreeExpansionEvent.Type.NODE_EXPAND);
    }

    /**
     * Tests whether a collapse event is correctly fired.
     */
    @Test
    public void testAddExpansionListenerCollapse()
    {
        TreeExpansionListenerTestImpl l = new TreeExpansionListenerTestImpl();
        handler.addExpansionListener(l);
        handler.treeCollapsed(new javax.swing.event.TreeExpansionEvent(tree,
                treePath(KEY_APP)));
        l.checkEvent(KEY_APP, TreeExpansionEvent.Type.NODE_COLLAPSE);
    }

    /**
     * Tries adding a null expansion listener. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddExpansionListenerNull()
    {
        handler.addExpansionListener(null);
    }

    /**
     * Tests removing an expansion listener.
     */
    @Test
    public void testRemoveExpansionListener()
    {
        TreeExpansionListenerTestImpl l = new TreeExpansionListenerTestImpl();
        handler.addExpansionListener(l);
        handler.removeExpansionListener(l);
        handler.treeExpanded(new javax.swing.event.TreeExpansionEvent(tree,
                treePath(KEY_APP)));
        assertNull("An event was received", l.event);
    }

    /**
     * Tests whether the handler registered itself as tree will expansion
     * listener.
     */
    @Test
    public void testInitWillExpansionListener()
    {
        TreeWillExpandListener[] listeners = tree.getTreeWillExpandListeners();
        boolean found = false;
        for (TreeWillExpandListener l : listeners)
        {
            if (l == handler)
            {
                found = true;
                break;
            }
        }
        assertTrue("Not registered as tree expansion listener", found);
    }

    /**
     * Tests whether pre-expand events are fired correctly.
     */
    @Test
    public void testAddPreExpansionListenerExpand() throws ExpandVetoException
    {
        TreePreExpansionListenerTestImpl l = new TreePreExpansionListenerTestImpl();
        handler.addPreExpansionListener(l);
        handler.treeWillExpand(new javax.swing.event.TreeExpansionEvent(tree,
                treePath(KEY_APP)));
        l.checkEvent(KEY_APP, TreeExpansionEvent.Type.NODE_EXPAND);
    }

    /**
     * Tests whether pre-collapse events are fired correctly.
     */
    @Test
    public void testAddPreExpansionListenerCollapse()
            throws ExpandVetoException
    {
        TreePreExpansionListenerTestImpl l = new TreePreExpansionListenerTestImpl();
        handler.addPreExpansionListener(l);
        handler.treeWillCollapse(new javax.swing.event.TreeExpansionEvent(tree,
                treePath(KEY_APP)));
        l.checkEvent(KEY_APP, TreeExpansionEvent.Type.NODE_COLLAPSE);
    }

    /**
     * Tests whether expansion veto events are processed correctly.
     */
    @Test
    public void testAddPreExpansionListenerExpandVeto()
    {
        TreePreExpansionListenerTestImpl l = new TreePreExpansionListenerTestImpl();
        final String msg = "Not allowed for this test!";
        javax.swing.event.TreeExpansionEvent event = new javax.swing.event.TreeExpansionEvent(
                tree, treePath(KEY_APP));
        TreeExpandVetoException veto = new TreeExpandVetoException(
                new TreeExpansionEvent(this, handler, NAME,
                        TreeExpansionEvent.Type.NODE_EXPAND, nodePath(KEY_APP)),
                msg);
        l.veto = veto;
        handler.addPreExpansionListener(l);
        try
        {
            handler.treeWillExpand(event);
            fail("Veto exception not detected!");
        }
        catch (ExpandVetoException evex)
        {
            assertEquals("Wrong error message", msg, evex.getMessage());
        }
    }

    /**
     * Tries adding a null pre-expansion listener. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddPreExpansionListenerNull()
    {
        handler.addPreExpansionListener(null);
    }

    /**
     * Tests removing a pre-expansion listener.
     */
    @Test
    public void testRemovePreExpansionListener() throws ExpandVetoException
    {
        TreePreExpansionListenerTestImpl l = new TreePreExpansionListenerTestImpl();
        handler.addPreExpansionListener(l);
        handler.removePreExpansionListener(l);
        handler.treeWillExpand(new javax.swing.event.TreeExpansionEvent(tree,
                treePath(KEY_APP)));
        assertNull("An event was received", l.event);
    }

    /**
     * A base class for event listener test implementations.
     */
    private class TestListenerBase
    {
        /** Stores the event received by this listener. */
        TreeExpansionEvent event;

        /**
         * Verifies an event received by this listener.
         *
         * @param handler the component handler for the tree
         * @param key the key of the expected path
         * @param type the expected event type
         */
        public void checkEvent(String key, TreeExpansionEvent.Type type)
        {
            assertNotNull("No event received", event);
            assertEquals("Wrong component handler", handler, event.getHandler());
            assertEquals("Wrong name", NAME, event.getName());
            javax.swing.event.TreeExpansionEvent orgEvent = (javax.swing.event.TreeExpansionEvent) event
                    .getSource();
            assertEquals("Wrong source of org event", tree, orgEvent
                    .getSource());
            assertEquals("Wrong event type", type, event.getType());
            assertEquals("Wrong path", nodePath(key), event.getPath());
        }

        /**
         * Stores an event that was received by this listener. We expect that
         * only a single event will be received. So we check whether there is
         * already another one.
         *
         * @param e the event
         */
        protected void initEvent(TreeExpansionEvent e)
        {
            assertNull("Too many events received", event);
            event = e;
        }
    }

    /**
     * An event listener implementation used for testing whether the correct
     * TreeExpansionEvent events are fired.
     */
    private class TreeExpansionListenerTestImpl extends TestListenerBase
            implements
            net.sf.jguiraffe.gui.builder.components.model.TreeExpansionListener
    {
        public void expansionStateChanged(TreeExpansionEvent e)
        {
            initEvent(e);
        }
    }

    /**
     * An event listener implementation used for testing whether the correct pre
     * expansion events are fired.
     */
    private class TreePreExpansionListenerTestImpl extends TestListenerBase
            implements TreePreExpansionListener
    {
        TreeExpandVetoException veto;

        /**
         * Records the event. If a veto exception is set, it is thrown.
         */
        public void beforeExpansionStateChange(TreeExpansionEvent event)
                throws TreeExpandVetoException
        {
            initEvent(event);
            if (veto != null)
            {
                throw veto;
            }
        }
    }
}
