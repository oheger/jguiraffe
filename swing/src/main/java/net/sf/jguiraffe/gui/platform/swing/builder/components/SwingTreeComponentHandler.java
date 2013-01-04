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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.sf.jguiraffe.gui.builder.components.model.TreeExpandVetoException;
import net.sf.jguiraffe.gui.builder.components.model.TreeExpansionListener;
import net.sf.jguiraffe.gui.builder.components.model.TreeHandler;
import net.sf.jguiraffe.gui.builder.components.model.TreeNodePath;
import net.sf.jguiraffe.gui.builder.components.model.TreePreExpansionListener;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;

/**
 * <p>
 * The Swing-specific implementation of a component handler for a tree.
 * </p>
 * <p>
 * This class wraps a <code>javax.swing.JTree</code> component and implements
 * the methods required for Swing component handlers in a suitable way. The
 * following functionality is provided:
 * <ul>
 * <li>The data handling depends on the tree's selection model. If the tree
 * supports single selection only, the handler's data type is
 * {@link TreeNodePath} (i.e. a generic way of describing a single
 * node in the tree). Otherwise the type is an array of
 * {@link TreeNodePath} storing the paths to all selected nodes.
 * The <code>getData()</code> and <code>setData()</code> methods work
 * correspondingly.</li>
 * <li>Methods are available for directly querying and manipulating the tree's
 * selection. These methods are defined by the platform-neutral
 * {@link TreeHandler} interface.</li>
 * <li>The data model of the tree can be queried in form of a
 * <code>HierarchicalConfiguration</code> object. Through this object the tree's
 * data can be directly read or manipulated.</li>
 * <li>Support for different kinds of event listeners. Platform-neutral change
 * listeners can register at handlers of this type. They will then be notified
 * whenever the tree's selection changes. Listeners can also be registered for
 * node expansion or collapse events.</li>
 * <li>A scroll pane for the table is automatically created and maintained.</li>
 * </ul>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingTreeComponentHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
class SwingTreeComponentHandler extends SwingComponentHandler<Object> implements
        TreeHandler, TreeSelectionListener,
        javax.swing.event.TreeExpansionListener, TreeWillExpandListener
{
    /** Stores the model of the tree. */
    private final SwingConfigurationTreeModel model;

    /** A list for managing event listeners. */
    private final EventListenerList listenerList;

    /** Stores the scroll pane, which is the outer component. */
    private final JScrollPane scrollPane;

    /** Stores the name of this component. */
    private final String name;

    /**
     * Creates a new instance of <code>SwingTreeComponentHandler</code> and
     * initializes it.
     *
     * @param tree the tree component wrapped by this handler
     * @param model the model for the tree
     * @param name the name of this component
     * @param scrollWidth the preferred scroll width (&lt;= 0 for undefined)
     * @param scrollHeight the preferred scroll height (&lt;= 0 for undefined)
     */
    public SwingTreeComponentHandler(JTree tree,
            SwingConfigurationTreeModel model, String name, int scrollWidth,
            int scrollHeight)
    {
        super(tree);
        this.model = model;
        this.name = name;

        scrollPane = SwingComponentUtils.scrollPaneFor(tree, scrollWidth,
                scrollHeight);
        listenerList = new EventListenerList();
        tree.addTreeExpansionListener(this);
        tree.addTreeWillExpandListener(this);
    }

    /**
     * Returns the tree wrapped by this handler.
     *
     * @return the underlying tree
     */
    public JTree getTree()
    {
        return (JTree) getComponent();
    }

    /**
     * Adds an expansion listener to this component. This listener will be
     * notified whenever the expansion state of a node changes.
     *
     * @param l the listener to add (must not be <b>null</b>)
     * @throws IllegalArgumentException if the listener is <b>null</b>
     */
    public void addExpansionListener(TreeExpansionListener l)
    {
        if (l == null)
        {
            throw new IllegalArgumentException(
                    "Event listener must not be null!");
        }
        listenerList.add(TreeExpansionListener.class, l);
    }

    /**
     * Adds a pre-expansion listener to this component. This listener will be
     * notified whenever a node's expansion state is about to change. It can
     * even veto against this change.
     *
     * @param l the listener to add (must not be <b>null</b>)
     * @throws IllegalArgumentException if the listener is <b>null</b>
     */
    public void addPreExpansionListener(TreePreExpansionListener l)
    {
        if (l == null)
        {
            throw new IllegalArgumentException(
                    "Event listener must not be null!");
        }
        listenerList.add(TreePreExpansionListener.class, l);
    }

    /**
     * Adds the given path to the selection of this tree.
     *
     * @param path the path to add (must not be <b>null</b>)
     * @throws IllegalArgumentException if the path is <b>null</b>
     */
    public void addSelectedPath(TreeNodePath path)
    {
        if (path == null)
        {
            throw new IllegalArgumentException(
                    "Path to add to selection must not be null!");
        }

        getTree().addSelectionPath(treePathFromNodePath(path));
    }

    /**
     * Clears the selection of the tree.
     */
    public void clearSelection()
    {
        getTree().clearSelection();
    }

    /**
     * Collapses the specified path.
     *
     * @param path the path to collapse (must not be <b>null</b>)
     * @throws IllegalArgumentException if the path is <b>null</b>
     */
    public void collapse(TreeNodePath path)
    {
        if (path == null)
        {
            throw new IllegalArgumentException(
                    "Path to collapse must not be null!");
        }

        getTree().collapsePath(treePathFromNodePath(path));
    }

    /**
     * Expands the specified path.
     *
     * @param path the path to expand (must not be <b>null</b>)
     * @throws IllegalArgumentException if the path is <b>null</b>
     */
    public void expand(TreeNodePath path)
    {
        if (path == null)
        {
            throw new IllegalArgumentException(
                    "Path to expand must not be null!");
        }

        getTree().expandPath(treePathFromNodePath(path));
    }

    /**
     * Returns the configuration that serves as the model for the tree. This
     * object is obtained from the Swing-specific tree model implementation.
     *
     * @return the configuration serving as tree model
     */
    public HierarchicalConfiguration getModel()
    {
        return model.getConfiguration();
    }

    /**
     * Returns the selected path. If nothing is selected, <b>null</b> will be
     * returned.
     *
     * @return the selected path
     */
    public TreeNodePath getSelectedPath()
    {
        TreePath path = getTree().getSelectionPath();
        return (path == null) ? null : nodePathFromTreePath(path);
    }

    /**
     * Returns an array with all selected paths. If nothing is selected, an
     * empty array is returned.
     *
     * @return an array with the selected paths
     */
    public TreeNodePath[] getSelectedPaths()
    {
        TreePath[] paths = getTree().getSelectionPaths();
        if (paths == null)
        {
            return new TreeNodePath[0];
        }

        TreeNodePath[] result = new TreeNodePath[paths.length];
        for (int i = 0; i < result.length; i++)
        {
            result[i] = nodePathFromTreePath(paths[i]);
        }

        return result;
    }

    /**
     * Removes the specified expansion listener from this component.
     *
     * @param l the listener to remove
     */
    public void removeExpansionListener(TreeExpansionListener l)
    {
        listenerList.remove(TreeExpansionListener.class, l);
    }

    /**
     * Removes the specified pre-expansion listener from this component.
     *
     * @param l the listener to remove
     */
    public void removePreExpansionListener(TreePreExpansionListener l)
    {
        listenerList.remove(TreePreExpansionListener.class, l);
    }

    /**
     * Selects a path.
     *
     * @param path the path to select (must not be <b>null</b>)
     * @throws IllegalArgumentException if the path is <b>null</b>
     */
    public void setSelectedPath(TreeNodePath path)
    {
        if (path == null)
        {
            throw new IllegalArgumentException(
                    "Path to select must not be null!");
        }

        getTree().setSelectionPath(treePathFromNodePath(path));
    }

    /**
     * Returns the data of this handler. This corresponds to the tree's
     * selection. The return value depends on the tree's selection model: if
     * multiple selection is supported, it is an array of
     * <code>{@link TreeNodePath}</code>. For single selection it is a single
     * <code>{@link TreeNodePath}</code> object. In either case, if nothing is
     * selected, result is <b>null</b>.
     *
     * @return the data of this handler
     */
    public Object getData()
    {
        if (getTree().getSelectionCount() == 0)
        {
            return null;
        }

        if (isMultiSelection())
        {
            TreePath[] paths = getTree().getSelectionPaths();
            TreeNodePath[] result = new TreeNodePath[paths.length];
            for (int i = 0; i < paths.length; i++)
            {
                result[i] = nodePathFromTreePath(paths[i]);
            }
            return result;
        }

        else
        {
            return nodePathFromTreePath(getTree().getSelectionPath());
        }
    }

    /**
     * Returns the outer component. This is the scroll pane that has the tree
     * component as view component.
     *
     * @return the outer component
     */
    @Override
    public Object getOuterComponent()
    {
        return scrollPane;
    }

    /**
     * Returns the type of this handler. Depending on the selection mode this is
     * either <code>TreeNodePath</code> or an array of this class.
     *
     * @return the type of this handler
     */
    public Class<?> getType()
    {
        return isMultiSelection() ? TreeNodePath[].class : TreeNodePath.class;
    }

    /**
     * Sets the data of this handler. This implementation accepts the following
     * data objects:
     * <ul>
     * <li><b>null</b> will clear the tree's selection.</li>
     * <li>A single <code>{@link TreeNodePath}</code> object will become the
     * selected path of the tree.</li>
     * <li>An array of <code>{@link TreeNodePath}</code> objects can be passed
     * in; then these paths will all be selected. (For this to work the tree
     * must support multiple selection.)</li>
     * </ul>
     * All other objects will cause an exception.
     *
     * @param data the data for the tree component
     * @throws IllegalArgumentException if the data is invalid
     */
    public void setData(Object data)
    {
        if (data == null)
        {
            clearSelection();
        }
        else if (data instanceof TreeNodePath)
        {
            setSelectedPath((TreeNodePath) data);
        }

        else if (data instanceof TreeNodePath[])
        {
            TreeNodePath[] nodePaths = (TreeNodePath[]) data;
            TreePath[] treePaths = new TreePath[nodePaths.length];
            for (int i = 0; i < nodePaths.length; i++)
            {
                treePaths[i] = treePathFromNodePath(nodePaths[i]);
            }
            getTree().setSelectionPaths(treePaths);
        }

        else
        {
            throw new IllegalArgumentException(
                    "Invalid data for tree handler: " + data);
        }
    }

    /**
     * The selection of the wrapped tree component has changed. This event is
     * propagated to the registered change listeners.
     *
     * @param e the event
     */
    public void valueChanged(TreeSelectionEvent e)
    {
        fireChangeEvent(e);
    }

    /**
     * A node in the wrapped tree was collapsed. Notify the registered expansion
     * listeners.
     *
     * @param event the original expansion event
     */
    public void treeCollapsed(TreeExpansionEvent event)
    {
        fireExpansionEvent(
                event,
                net.sf.jguiraffe.gui.builder.components.model.TreeExpansionEvent
                .Type.NODE_COLLAPSE);
    }

    /**
     * A node in the wrapped tree was expanded. Notify the registered expansion
     * listeners.
     *
     * @param event the original expansion event
     */
    public void treeExpanded(TreeExpansionEvent event)
    {
        fireExpansionEvent(
                event,
                net.sf.jguiraffe.gui.builder.components.model.TreeExpansionEvent
                .Type.NODE_EXPAND);
    }

    /**
     * A note in the wrapped tree will be collapsed. The pre-expansion listeners
     * registered at this component will be notified.
     *
     * @param event the original event
     * @throws ExpandVetoException if a listener forbids this operation
     */
    public void treeWillCollapse(TreeExpansionEvent event)
            throws ExpandVetoException
    {
        firePreExpansionEvent(
                event,
                net.sf.jguiraffe.gui.builder.components.model.TreeExpansionEvent
                .Type.NODE_COLLAPSE);
    }

    /**
     * A note in the wrapped tree will be expanded. The pre-expansion listeners
     * registered at this component will be notified.
     *
     * @param event the original event
     * @throws ExpandVetoException if a listener forbids this operation
     */
    public void treeWillExpand(TreeExpansionEvent event)
            throws ExpandVetoException
    {
        firePreExpansionEvent(
                event,
                net.sf.jguiraffe.gui.builder.components.model.TreeExpansionEvent
                .Type.NODE_EXPAND);
    }

    /**
     * Returns a flag whether multiple selection is supported. This information
     * is obtained from the tree's selection model.
     *
     * @return the multiple selection flag
     */
    protected boolean isMultiSelection()
    {
        return getTree().getSelectionModel().getSelectionMode()
            != TreeSelectionModel.SINGLE_TREE_SELECTION;
    }

    /**
     * Converts a tree path to a tree node path.
     *
     * @param treePath the tree path
     * @return the node path
     */
    protected TreeNodePath nodePathFromTreePath(TreePath treePath)
    {
        Object[] nodes = treePath.getPath();
        List<ConfigurationNode> ndLst = new ArrayList<ConfigurationNode>(
                nodes.length);
        for (Object nd : nodes)
        {
            ndLst.add((ConfigurationNode) nd);
        }

        return new TreeNodePath(ndLst);
    }

    /**
     * Converts a node path to a tree path.
     *
     * @param nodePath the node path
     * @return the corresponding tree path
     */
    protected TreePath treePathFromNodePath(TreeNodePath nodePath)
    {
        return new TreePath(nodePath.getNodes().toArray());
    }

    /**
     * Converts a Swing <code>TreeExpansionEvent</code> to a platform-neutral
     * expansion event.
     *
     * @param event the Swing-specific event
     * @param type the type of the event (expand or collapse)
     * @return the platform-neutral event
     */
    protected net.sf.jguiraffe.gui.builder.components.model.TreeExpansionEvent convertEvent(
            TreeExpansionEvent event,
            net.sf.jguiraffe.gui.builder.components.model.TreeExpansionEvent.Type type)
    {
        return new net.sf.jguiraffe.gui.builder.components.model.TreeExpansionEvent(
                event, this, name, type, nodePathFromTreePath(event.getPath()));
    }

    /**
     * Notifies all registered event listeners about a change in the expansion
     * state of a node.
     *
     * @param event the original expansion event
     * @param type the type of the event (expand or collapse)
     */
    protected void fireExpansionEvent(
            TreeExpansionEvent event,
            net.sf.jguiraffe.gui.builder.components.model.TreeExpansionEvent.Type type)
    {
        net.sf.jguiraffe.gui.builder.components.model.TreeExpansionEvent e = null;
        Object[] listeners = listenerList.getListenerList();

        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == TreeExpansionListener.class)
            {
                if (e == null)
                {
                    e = convertEvent(event, type);
                }
                ((TreeExpansionListener) listeners[i + 1])
                        .expansionStateChanged(e);
            }
        }
    }

    /**
     * Notifies all registered event listeners that a node of the tree is about
     * to change its expansion state. The listeners can veto against this
     * action.
     *
     * @param event the original expansion event
     * @param type the type of the event (expand or collapse)
     * @throws ExpandVetoException if at least one listeners vetos against this
     *         operation
     */
    protected void firePreExpansionEvent(
            TreeExpansionEvent event,
            net.sf.jguiraffe.gui.builder.components.model.TreeExpansionEvent.Type type)
            throws ExpandVetoException
    {
        net.sf.jguiraffe.gui.builder.components.model.TreeExpansionEvent e = null;
        Object[] listeners = listenerList.getListenerList();

        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == TreePreExpansionListener.class)
            {
                if (e == null)
                {
                    e = convertEvent(event, type);
                }
                try
                {
                    ((TreePreExpansionListener) listeners[i + 1])
                            .beforeExpansionStateChange(e);
                }
                catch (TreeExpandVetoException tevex)
                {
                    throw new ExpandVetoException(event, tevex.getMessage());
                }
            }
        }
    }

    /**
     * Registers this component handler as change listener at the wrapped
     * component. This implementation will add a listener for selection changes
     * of the wrapped tree component.
     */
    @Override
    protected void registerChangeListener()
    {
        getTree().addTreeSelectionListener(this);
    }

    /**
     * Stops listening for change events. This implementation will unregister
     * itself as tree selection listener.
     */
    @Override
    protected void unregisterChangeListener()
    {
        getTree().removeTreeSelectionListener(this);
    }
}
