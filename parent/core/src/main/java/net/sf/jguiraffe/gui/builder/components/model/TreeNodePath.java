/*
 * Copyright 2006-2015 The JGUIraffe Team.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.configuration.tree.DefaultConfigurationKey;

/**
 * <p>
 * A class that represents a path in a tree component.
 * </p>
 * <p>
 * Objects of this class are used for uniquely identifying specific nodes in a
 * tree. They contain the path from a target node to the root node. This is used
 * for instance to describe the selected node(s) in a tree.
 * </p>
 * <p>
 * With the methods provided by this class the path can be queried as a list of
 * <code>ConfigurationNode</code> objects. (Theoretically the end node of the
 * path would already be a unique description of a path because there is only a
 * single way to the root node; by navigating through the parent nodes an
 * application can construct this path. However, if this information is provided
 * explicitly as a list, it is much easier for an application to deal with
 * paths.)
 * </p>
 * <p>
 * It is also possible to query the single names and indices of the nodes
 * comprising the path. This is especially useful for constructing a
 * configuration key representing the path. This key can then be used for
 * querying or manipulating the <code>Configuration</code> object that serves as
 * data store for the tree's model.
 * </p>
 * <p>
 * When an instance of this class is created it is fully initialized with the
 * nodes that belong to the represented path. These references cannot be changed
 * later any more. However, the nodes themselves are not copied because this
 * could be expensive. Therefore the class is not immutable. It can be used by
 * multiple threads only under the precondition that the configuration nodes
 * involved do not change. The main use case however is that a path is obtained
 * and processed by a single event handler in the event dispatch thread.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TreeNodePath.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TreeNodePath
{
    /** Stores a list with the nodes in the path. */
    private final List<ConfigurationNode> nodes;

    /** An array with the names of the nodes in the path. */
    private final String[] nodeNames;

    /** An array with the indices of the nodes in the path. */
    private final int[] indices;

    /**
     * Creates a new instance of <code>TreeNodePath</code> and initializes it
     * with the target node. From this node the path to the root will be
     * constructed.
     *
     * @param target the target node (must not be <b>null</b>)
     * @throws IllegalArgumentException if the target node is <b>null</b>
     */
    public TreeNodePath(ConfigurationNode target)
    {
        if (target == null)
        {
            throw new IllegalArgumentException("Target node must not be null!");
        }

        nodes = initPath(target);
        nodeNames = initNodeNames(nodes);
        indices = initNodeIndices(nodes);
    }

    /**
     * Creates a new instance of <code>TreeNodePath</code> and initializes it
     * with the nodes comprising the path.
     *
     * @param pathNodes the collection with the nodes of the path (must not be
     *        <b>null</b>)
     * @throws IllegalArgumentException if the collection is <b>null</b>
     */
    public TreeNodePath(Collection<ConfigurationNode> pathNodes)
    {
        if (pathNodes == null)
        {
            throw new IllegalArgumentException(
                    "Collection with the nodes must not be null!");
        }

        nodes = new ArrayList<ConfigurationNode>(pathNodes);
        nodeNames = initNodeNames(nodes);
        indices = initNodeIndices(nodes);
    }

    /**
     * Returns a list with the nodes comprising the path. The root node of the
     * tree is at index 0. The target node of this path is at the highest index.
     * Note that the list returned by this method is immutable.
     *
     * @return a list with the nodes of this path
     */
    public List<ConfigurationNode> getNodes()
    {
        return Collections.unmodifiableList(nodes);
    }

    /**
     * Returns the length of this path. This is the number of nodes contained in
     * this path from the target node to the root node.
     *
     * @return the length of this path
     */
    public int size()
    {
        return nodes.size();
    }

    /**
     * <p>
     * Returns the name of the path node with the given index. The purpose of
     * this method is constructing unique configuration keys by iterating over
     * all node names and indices. Because the root node is not part of a
     * configuration key it is not taken into account by this method. Thus the
     * index 0 will not return the name of the root node, but the name of the
     * next node in the path below the root node. The last node of the path has
     * then the index <code>size() - 2</code>. The following code fragment shows
     * how this method and <code>getNodeIndex()</code> can be used for
     * constructing a string representation of this path:
     * </p>
     * <p>
     *
     * <pre>
     * TreeNodePath path = ...;
     * StringBuilder buf = new StringBuilder();
     * for (int i = 0; i &lt; path.size() - 1; i++) {
     *   if (i &gt; 0) {
     *     buf.append('/');  // separator for node names
     *   }
     *   buf.append(path.getNodeName(i);
     *   buf.append('[').append(path.getNodeIndex(i)).append(']');
     * }
     * </pre>
     *
     * </p>
     *
     * @param index the index of the desired node
     * @return the name of this node
     * @throws ArrayIndexOutOfBoundsException if the index is invalid
     */
    public String getNodeName(int index)
    {
        return nodeNames[index];
    }

    /**
     * Returns the index of the path node at the specified position in the path.
     * Analogously to <code>getNodeName()</code> this method is intended for
     * constructing unique keys for paths that can be used for accessing the
     * underlying <code>Configuration</code> object. Because a configuration
     * node can have multiple child nodes with the same name indices are
     * required for making keys unique. The index returned by this method is non
     * 0 only if there are multiple child nodes with the same name. In this case
     * it determines, which of these child nodes is the one that belongs to this
     * path.
     *
     * @param index the index of the desired node (in the path)
     * @return a unique index for this node for constructing a unique key
     * @throws ArrayIndexOutOfBoundsException if the index is invalid
     * @see #getNodeName(int)
     */
    public int getNodeIndex(int index)
    {
        return indices[index];
    }

    /**
     * Returns the target node of this path. This is the last component in the
     * path.
     *
     * @return the target node of this path
     */
    public ConfigurationNode getTargetNode()
    {
        return nodes.get(nodes.size() - 1);
    }

    /**
     * Appends this path to the given <code>ConfigurationKey</code>. This
     * implementation will create a unique key that corresponds to the path
     * represented. The node names and the indices along the path are appended
     * to the given configuration key.
     *
     * @param key the key (must not be <b>null</b>)
     * @throws IllegalArgumentException if the key is <b>null</b>
     */
    public void pathToKey(DefaultConfigurationKey key)
    {
        if (key == null)
        {
            throw new IllegalArgumentException("Key must not be null!");
        }

        for (int i = 0; i < size() - 1; i++)
        {
            key.append(getNodeName(i));
            key.appendIndex(getNodeIndex(i));
        }
    }

    /**
     * Returns the parent path of this {@code TreeNodePath}. This means that the
     * target node of the {@code TreeNodePath} returned by this method is the
     * parent node of this {@code TreeNodePath}'s target node. This {@code
     * TreeNodePath} must have at least a size of 2, otherwise an exception is
     * thrown.
     *
     * @return the parent path of this {@code TreeNodePath}
     * @throws IllegalStateException if this path already represents the root
     *         node
     */
    public TreeNodePath parentPath()
    {
        if (size() <= 1)
        {
            throw new IllegalStateException(
                    "Cannot obtain parent path for root node!");
        }

        return new TreeNodePath(nodes.get(size() - 2));
    }

    /**
     * Returns a {@code TreeNodePath} object that was created by appending the
     * specified {@code ConfigurationNode} to this path. The new node becomes
     * the target node of the new {@code TreeNodePath} object. This method is
     * useful when navigating through a tree structure. The passed in node must
     * be a child node of the current target node.
     *
     * @param node the node to be appended to the path
     * @return the new {@code TreeNodePath} extended by the node
     * @throws IllegalArgumentException if the passed in node is <b>null</b> or
     *         not a child node of the target node
     */
    public TreeNodePath append(ConfigurationNode node)
    {
        if (node == null)
        {
            throw new IllegalArgumentException("Node must not be null!");
        }
        if (node.getParentNode() != getTargetNode())
        {
            throw new IllegalArgumentException(
                    "Node is not a child of the target node!");
        }

        List<ConfigurationNode> newNodes =
                new ArrayList<ConfigurationNode>(size() + 1);
        newNodes.addAll(nodes);
        newNodes.add(node);
        return new TreeNodePath(newNodes);
    }

    /**
     * Returns a {@code TreeNodePath} object that was created by appending the
     * specified child node of the current target node to this path. This method
     * determines the child node with the given name and index. It then creates
     * a new {@code TreeNodePath} object with this node as target node.
     *
     * @param childName the name of the child node to be appended
     * @param index the index of the node (in case there are multiple children
     *        with the same name)
     * @return the new {@code TreeNodePath} extended by the child node
     * @throws IllegalArgumentException if no child node with this name can be
     *         found
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public TreeNodePath append(String childName, int index)
    {
        if (childName == null)
        {
            throw new IllegalArgumentException(
                    "Name of child node must not be null!");
        }
        if (getTargetNode().getChildrenCount(childName) < 1)
        {
            throw new IllegalArgumentException("Cannot find child with name "
                    + childName);
        }

        return append((ConfigurationNode) getTargetNode()
                .getChildren(childName).get(index));
    }

    /**
     * Returns a {@code TreeNodePath} object that was created by appending the
     * first child node of the current target node with the given name to this path.
     * This is a short cut of {@code append(childName, 0)}.
     * @param childName the name of the child node to be appended
     * @return the new {@code TreeNodePath} extended by the child node
     * @throws IllegalArgumentException if no child node with this name can be found
     */
    public TreeNodePath append(String childName)
    {
        return append(childName, 0);
    }

    /**
     * Tests whether two objects are equal. Two path objects are considered
     * equal if and only if they refer to the same target node.
     *
     * @param obj the object to test
     * @return a flag whether these objects are equal
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (!(obj instanceof TreeNodePath))
        {
            return false;
        }

        TreeNodePath c = (TreeNodePath) obj;
        return getTargetNode().equals(c.getTargetNode());
    }

    /**
     * Returns a hash code for this object. The hash code is obtained from the
     * target node.
     *
     * @return a hash code
     */
    @Override
    public int hashCode()
    {
        return getTargetNode().hashCode();
    }

    /**
     * Returns a string representation for this object. This string will contain
     * a representation of the path using '/' as path separator and square
     * brackets for indices.
     *
     * @return a string for this object
     */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append(getClass().getName()).append('@');
        buf.append(System.identityHashCode(this));
        buf.append("[ ");

        for (int i = 0; i < size() - 1; i++)
        {
            if (i > 0)
            {
                buf.append('/');
            }
            buf.append(getNodeName(i));
            buf.append('[').append(getNodeIndex(i)).append(']');
        }

        buf.append(" ]");
        return buf.toString();
    }

    /**
     * Obtains the complete path from the specified target node. This method
     * navigates through the parent nodes until it finds the root node.
     *
     * @param node the target node
     * @return a list with the nodes of this path
     */
    private List<ConfigurationNode> initPath(ConfigurationNode node)
    {
        List<ConfigurationNode> nodes = new ArrayList<ConfigurationNode>();
        ConfigurationNode nd = node;

        while (nd != null)
        {
            nodes.add(nd);
            nd = nd.getParentNode();
        }

        Collections.reverse(nodes);
        return nodes;
    }

    /**
     * Extracts the array with the node names from the given node list.
     *
     * @param nodes the list with the nodes
     * @return an array with the node names
     */
    private String[] initNodeNames(List<ConfigurationNode> nodes)
    {
        String[] names = new String[nodes.size() - 1];
        for (int i = 0; i < names.length; i++)
        {
            names[i] = nodes.get(i + 1).getName();
        }
        return names;
    }

    /**
     * Determines the indices for the nodes that belong to the path.
     *
     * @param nodes the list with the nodes
     * @return an array with the indices of the nodes
     */
    private int[] initNodeIndices(List<ConfigurationNode> nodes)
    {
        int[] indices = new int[nodes.size() - 1];
        for (int i = 0; i < indices.length; i++)
        {
            indices[i] = indexForChild(nodes.get(i + 1));
        }
        return indices;
    }

    /**
     * Determines the index of the specified child node.
     *
     * @param nd the node
     * @return the index of this node relative to its parent
     */
    private static int indexForChild(ConfigurationNode nd)
    {
        ConfigurationNode parent = nd.getParentNode();
        assert parent != null : "No parent node!";

        List<?> children = parent.getChildren(nd.getName());
        int index = 0;
        for (Object c : children)
        {
            if (c == nd)
            {
                break;
            }
            index++;
        }

        return index;
    }
}
