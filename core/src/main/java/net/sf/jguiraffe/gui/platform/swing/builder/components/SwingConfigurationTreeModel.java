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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.lang.ObjectUtils;

/**
 * <p>
 * A specialized implementation of <code>TreeModel</code> that obtains its data
 * from a <code>Configuration</code> object.
 * </p>
 * <p>
 * This is a fully functional implementation of Swing's <code>TreeModel</code>
 * interface. The content of the model is obtained from the nodes stored in a
 * hierarchical configuration. The tree will display the keys of the
 * configuration properties, i.e. the names of the nodes.
 * </p>
 * <p>
 * The structure-related methods of the <code>TreeModel</code> interface (e.g.
 * <code>getRoot()</code> or <code>getChild()</code>) are implemented by
 * directly forwarding to methods provided by the <code>ConfigurationNode</code>
 * interface. All of these methods expect that a passed in node (which is of
 * type <code>Object</code> in the <code>TreeModel</code> interface) can be cast
 * into a <code>ConfigurationNode</code>.
 * </p>
 * <p>
 * The model also registers itself as event listener at the underlying
 * configuration and tries to map configuration change events to corresponding
 * model change events. This is not always possible because configuration change
 * events often do not contain enough information for such a mapping. If the
 * mapping is not possible, a very generic structure changed event is fired.
 * </p>
 * <p>
 * As is true for most Swing objects, this class is not thread-safe. It is
 * possible to manipulate the underlying configuration in a separate thread,
 * which will cause change events received by this model. These events are then
 * propagated to registered listeners in the event dispatch thread. However,
 * this implementation relies on the fact that only a single configuration
 * change event can be received at a time. (Because typical configuration
 * implementations cannot be updated concurrently this should not be a
 * limitation.)
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingConfigurationTreeModel.java 205 2012-01-29 18:29:57Z oheger $
 */
public class SwingConfigurationTreeModel implements TreeModel,
        ConfigurationListener
{
    /** Stores the underlying configuration object. */
    private final HierarchicalConfiguration configuration;

    /** A collection with the event listeners registered for this model. */
    private final Collection<TreeModelListener> listeners;

    /** Stores the configuration node affected by the current modification. */
    private volatile ConfigurationNode changedNode;

    /**
     * Creates a new instance of <code>SwingConfigurationTreeModel</code> and
     * initializes it with the given <code>HierarchicalConfiguration</code>
     * object.
     *
     * @param config the configuration (must not be <b>null</b>)
     * @throws IllegalArgumentException if an error occurs
     */
    public SwingConfigurationTreeModel(HierarchicalConfiguration config)
    {
        if (config == null)
        {
            throw new IllegalArgumentException(
                    "Configuration must not be null!");
        }

        configuration = config;
        listeners = new CopyOnWriteArrayList<TreeModelListener>();
        configuration.addConfigurationListener(this);
    }

    /**
     * Returns the configuration object that stores the data of this model.
     *
     * @return the underlying configuration object
     */
    public HierarchicalConfiguration getConfiguration()
    {
        return configuration;
    }

    /**
     * Adds an event listener to this model. The listener must not be
     * <b>null</b>.
     *
     * @param l the listener to add
     * @throws IllegalArgumentException if the listener is <b>null</b>
     */
    public void addTreeModelListener(TreeModelListener l)
    {
        if (l == null)
        {
            throw new IllegalArgumentException("Listener must not be null!");
        }

        listeners.add(l);
    }

    /**
     * Returns the child node of the specified node with the given index. This
     * implementation expects that the node is of type
     * <code>ConfigurationNode</code>.
     *
     * @param node the node
     * @param index the index
     * @return the child node with this index
     */
    public Object getChild(Object node, int index)
    {
        return ((ConfigurationNode) node).getChild(index);
    }

    /**
     * Returns the number of child nodes of the specified node. This
     * implementation expects that the node is of type
     * <code>ConfigurationNode</code>.
     *
     * @param node the node
     * @return the number of child nodes of this node
     */
    public int getChildCount(Object node)
    {
        return ((ConfigurationNode) node).getChildrenCount();
    }

    /**
     * Returns the index of the specified child node relative to its parent
     * node. This implementation expects that the node is of type
     * <code>ConfigurationNode</code>. If the node is no child of the specified
     * parent, -1 is returned. The parent and the child node can both be
     * <b>null</b>; then -1 is returned, too.
     *
     * @param parent the parent node
     * @param child the child node
     * @return the index of this child node or -1
     */
    public int getIndexOfChild(Object parent, Object child)
    {
        if (parent == null || child == null)
        {
            return -1;
        }

        int index = 0;
        for (Object o : ((ConfigurationNode) parent).getChildren())
        {
            if (o == child)
            {
                return index;
            }
            index++;
        }

        return -1;
    }

    /**
     * Returns the root node of this tree model. This is the root node of the
     * underlying configuration.
     *
     * @return the root node of this tree model
     */
    public Object getRoot()
    {
        return getConfiguration().getRootNode();
    }

    /**
     * Tests whether the passed in node is a leaf node. This implementation
     * expects that the passed in object is a <code>ConfigurationNode</code>. It
     * then checks whether it has children.
     *
     * @param node the node
     * @return a flag whether this is a leaf node
     */
    public boolean isLeaf(Object node)
    {
        return ((ConfigurationNode) node).getChildrenCount() == 0;
    }

    /**
     * Removes the specified event listener from this model.
     *
     * @param l the listener to be removed
     */
    public void removeTreeModelListener(TreeModelListener l)
    {
        listeners.remove(l);
    }

    /**
     * The value of a node was changed. This method is called if the user edited
     * a node in the tree control. This implementation will update the value of
     * the corresponding configuration node. Then it will fire a change event.
     *
     * @param path the path to the changed node
     * @param newValue the new value
     */
    public void valueForPathChanged(TreePath path, Object newValue)
    {
        ConfigurationNode node = (ConfigurationNode) path
                .getLastPathComponent();
        if (!ObjectUtils.equals(node.getValue(), newValue))
        {
            changeNodeName(node, String.valueOf(newValue));

            if (path.getPathCount() > 1)
            {
                // It is not the root node
                TreeModelEvent event = new TreeModelEvent(this, path
                        .getParentPath(), new int[] {
                    getIndexOfChild(node.getParentNode(), node)
                }, new Object[] {
                    node
                });

                for (TreeModelListener l : listeners)
                {
                    l.treeNodesChanged(event);
                }
            }

            else
            {
                // fire a generic structure changed event for the root node
                fireStructureChangedEvent();
            }
        }
    }

    /**
     * The underlying configuration has changed. This method tries to translate
     * the configuration event into a tree model event. This involves finding
     * the highest configuration node in the hierarchy affected by this event.
     * In most cases this will not be possible because the configuration event
     * typically won't contain enough information. Then a generic structure
     * changed event for the root node is fired.
     *
     * @param event the event
     */
    public void configurationChanged(ConfigurationEvent event)
    {
        if (event.isBeforeUpdate())
        {
            // try to determine the node affected by this event
            switch (event.getType())
            {
            case HierarchicalConfiguration.EVENT_CLEAR_PROPERTY:
            case HierarchicalConfiguration.EVENT_CLEAR_TREE:
            case HierarchicalConfiguration.EVENT_SET_PROPERTY:
                changedNode = findChangedNode(event);
                break;

            case HierarchicalConfiguration.EVENT_ADD_NODES:
                List<ConfigurationNode> nodes = nodesForKey(event
                        .getPropertyName());
                if (nodes.size() == 1)
                {
                    changedNode = nodes.get(0);
                }
                break;

            default:
                changedNode = null;
            }
        }

        else
        {
            // fire the event after the update
            fireStructureChangedEvent();
        }
    }

    /**
     * Fires a structure changed event.
     */
    private void fireStructureChangedEvent()
    {
        final TreeModelEvent event = createEvent();
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                for (TreeModelListener l : listeners)
                {
                    l.treeStructureChanged(event);
                }
            }
        });
    }

    /**
     * Creates a path for the specified configuration node. The path contains
     * all nodes up to the root node.
     *
     * @param node the start node for the path
     * @return the path
     */
    private Object[] createPath(ConfigurationNode node)
    {
        List<Object> pathElements = new ArrayList<Object>();
        ConfigurationNode nd = node;

        // iterate to the root node
        while (nd.getParentNode() != null)
        {
            pathElements.add(nd);
            nd = nd.getParentNode();
        }

        // Explicitly add the root node. This is a workaround for
        // inconsistencies
        // in the handling of parent nodes in hierarchical configurations
        pathElements.add(getRoot());

        // now reverse order and create an array
        Collections.reverse(pathElements);
        return pathElements.toArray();
    }

    /**
     * Creates an event reporting a change of this tree model. If a node
     * affected by the event is known, the event will refer to this node.
     * Otherwise the configuration's root node is used for the event.
     *
     * @return the event
     */
    private TreeModelEvent createEvent()
    {
        Object[] path = (changedNode != null) ? createPath(changedNode)
                : new Object[] {
                    getRoot()
                };
        return new TreeModelEvent(this, path);
    }

    /**
     * Obtains a list with the nodes referred to by the specified key. This
     * method queries the expression engine of the wrapped configuration to
     * resolve the key.
     *
     * @param key the configuration key
     * @return a list with the configuration nodes this key points to
     */
    @SuppressWarnings("unchecked")
    private List<ConfigurationNode> nodesForKey(String key)
    {
        return (List<ConfigurationNode>) getConfiguration()
                .getExpressionEngine().query(getConfiguration().getRoot(), key);
    }

    /**
     * Tries to determine the configuration node that is affected by a change
     * event. This method will obtain the configuration nodes the key points to.
     * If all these nodes have the same parent, this parent node will become the
     * changed node. Otherwise <b>null</b> is returned.
     *
     * @param event the configuration change event
     * @return the affected node or <b>null</b> if it cannot be determined
     */
    private ConfigurationNode findChangedNode(ConfigurationEvent event)
    {
        ConfigurationNode node = null;

        for (ConfigurationNode nd : nodesForKey(event.getPropertyName()))
        {
            if (node == null)
            {
                node = nd.getParentNode();
            }
            else
            {
                if (node != nd.getParentNode())
                {
                    return null;
                }
            }
        }

        return node;
    }

    /**
     * Changes the name of a configuration node. Nodes that have a parent
     * usually must not be changed. So this method first removes the parent,
     * then sets the new name, and finally restores the parent.
     *
     * @param node the node
     * @param newName the new name
     */
    private static void changeNodeName(ConfigurationNode node, String newName)
    {
        ConfigurationNode parent = node.getParentNode();
        node.setParentNode(null);
        node.setName(newName);
        node.setParentNode(parent);
    }
}
