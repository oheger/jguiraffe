/*
 * Copyright 2006-2021 The JGUIraffe Team.
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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * A helper class for concrete tree view implementations that supports the
 * processing of change events fired by a configuration serving as tree model.
 * </p>
 * <p>
 * In <em>JGUIraffe</em>, for tree views instances of
 * {@code HierarchicalConfiguration} are used as data model. It is desired that
 * the tree view updates itself automatically if the underlying configuration
 * object is changed. This is possible because a change listener can be
 * registered at the configuration. This class implements such a change
 * listener. It reacts on configuration change events, detects the configuration
 * parent node affected by this change and passes it to an associated
 * {@link TreeModelChangeListener}. This {@code TreeModelChangeListener}
 * implementation can then update the underlying UI component.
 * </p>
 * <p>
 * However, it is not a trivial task to find out the exact configuration node
 * affected by a change. Configuration change events typically only contain the
 * key of the node affected by a change, and this key is not necessarily unique.
 * This implementation deals with this fact by trying to find a parent node
 * common to all nodes referenced by a key. So the goal is to invalidate only a
 * minimum part of the tree that has to be regenerated to be in sync with the
 * model configuration. In the worst case, when no specific parent node can be
 * determined, the root node used; this means that the whole tree component has
 * to be reconstructed.
 * </p>
 * <p>
 * One limitation of this implementation is that it is not fully thread-safe. It
 * expects that only a single change event from a configuration is received at a
 * given point in time. Typically, this should not be a problem because
 * configuration implementations do not support concurrent updates.
 * </p>
 * <p>
 * This class is mainly intended to be used internally by implementations for
 * specific UI toolkits. Therefore, it does not do any sophisticated parameter
 * checks.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: $
 * @since 1.3
 */
public class TreeConfigurationChangeHandler implements ConfigurationListener
{
    /** The associated configuration. */
    private final HierarchicalConfiguration configuration;

    /** The change listener to be notified. */
    private final TreeModelChangeListener modelChangeListener;

    /** The node affected by the most recent change event. */
    private ConfigurationNode changedNode;

    /**
     * Creates a new instance of {@code TreeConfigurationChangeHandler} and
     * initializes it with the given {@code HierarchicalConfiguration} and the
     * change listener. Note that this constructor does not register the object
     * as listener at the configuration; this has to be done manually.
     *
     * @param config the associated {@code HierarchicalConfiguration}
     * @param listener the change listener
     */
    public TreeConfigurationChangeHandler(HierarchicalConfiguration config,
            TreeModelChangeListener listener)
    {
        configuration = config;
        modelChangeListener = listener;
    }

    /**
     * Returns the {@code HierarchicalConfiguration} associated with this
     * object.
     *
     * @return the {@code HierarchicalConfiguration}
     */
    public HierarchicalConfiguration getConfiguration()
    {
        return configuration;
    }

    /**
     * Returns the {@code TreeModelChangeListener} associated with this object.
     *
     * @return the {@code TreeModelChangeListener}
     */
    public TreeModelChangeListener getModelChangeListener()
    {
        return modelChangeListener;
    }

    /**
     * Changes the name of a {@code ConfigurationNode}. This is a utility method
     * which is probably needed by each concrete tree model implementation.
     * Normally, the name of a configuration node cannot be changed if it is
     * part of a node hierarchy. Therefore, this method uses some tricks to
     * achieve this goal. The return value indicates if a change was done. If
     * the passed in new name equals the current name, nothing is changed, and
     * result is <b>false</b>.
     *
     * @param node the {@code ConfigurationNode} to be changed
     * @param newName the new name of this node
     * @return <b>true</b> if a name change was necessary, <b>false</b>
     *         otherwise
     */
    public boolean changeNodeName(ConfigurationNode node, String newName)
    {
        if (!StringUtils.equals(node.getName(), newName))
        {
            ConfigurationNode parent = node.getParentNode();
            node.setParentNode(null);
            node.setName(newName);
            node.setParentNode(parent);
            return true;
        }
        return false;
    }

    /**
     * Reacts on change events of the associated {@code Configuration}. This
     * implementation determines the root node of the sub tree affected by the
     * change. This node is then passed to the associated
     * {@code TreeModelChangeListener}.
     *
     * @param event the change event fired by the configuration
     */
    public void configurationChanged(ConfigurationEvent event)
    {
        if (event.isBeforeUpdate())
        {
            // These types have to be checked before an update because
            // afterwards the key may be invalid.
            switch (event.getType())
            {
            case HierarchicalConfiguration.EVENT_CLEAR_PROPERTY:
            case HierarchicalConfiguration.EVENT_CLEAR_TREE:
            case HierarchicalConfiguration.EVENT_SET_PROPERTY:
                changedNode = findAffectedNode(event.getPropertyName(), true);
                break;

            case HierarchicalConfiguration.EVENT_ADD_NODES:
                changedNode = findAffectedNode(event.getPropertyName(), false);
                break;

            default:
                changedNode = getConfiguration().getRootNode();
            }
        }

        else
        {
            // Some event types need to be checked after the update; only
            // then the key is valid.
            if (event.getType() == HierarchicalConfiguration.EVENT_ADD_PROPERTY)
            {
                changedNode = findAffectedNode(event.getPropertyName(), true);
            }

            notifyListener();
        }
    }

    /**
     * Tries to determine the least common parent node of the passed in nodes.
     * In worst case, this is the configuration's root node.
     *
     * @param nd1 node 1
     * @param nd2 node 2
     * @return the common parent node
     */
    ConfigurationNode findCommonParent(ConfigurationNode nd1,
            ConfigurationNode nd2)
    {
        Set<ConfigurationNode> path = new HashSet<ConfigurationNode>();
        for (ConfigurationNode current = nd2; current != null; current =
                current.getParentNode())
        {
            if (current == nd1)
            {
                return current;
            }
            path.add(current);
        }

        for (ConfigurationNode current = nd1.getParentNode(); current != null; current =
                current.getParentNode())
        {
            if (path.contains(current))
            {
                return current;
            }
        }

        return getConfiguration().getRootNode();
    }

    /**
     * Obtains the deepest node in the hierarchical structure affected by a
     * change event described by the given key. This method tries to resolve the
     * key. If there is only one hit, the affected node can be uniquely
     * identified. Otherwise, the least common parent node of all retrieved
     * nodes is searched for. In worst case, this is the configuration's root
     * node.
     *
     * @param key the key associated with a change event
     * @param parent a flag whether the parent node of the key is desired
     * @return the deepest node affected by the change
     */
    private ConfigurationNode findAffectedNode(String key, boolean parent)
    {
        List<ConfigurationNode> nodes = nodesForKey(key);
        if (nodes.isEmpty())
        {
            return getConfiguration().getRootNode();
        }

        Iterator<ConfigurationNode> it = nodes.iterator();
        ConfigurationNode current = resolveParent(it.next(), parent);
        while (it.hasNext() && current != getConfiguration().getRootNode())
        {
            current =
                    findCommonParent(current, resolveParent(it.next(), parent));
        }
        return current;
    }

    /**
     * Obtains a list with the nodes referred to by the specified key. This
     * method queries the expression engine of the wrapped configuration to
     * resolve the key.
     *
     * @param key the configuration key
     * @return a list with the configuration nodes this key points to
     */
    private List<ConfigurationNode> nodesForKey(String key)
    {
        return getConfiguration().getExpressionEngine().query(
                getConfiguration().getRootNode(), key);
    }

    /**
     * Notifies the associated listener about a change event. The deepest node
     * in the hierarchical structure affected by the change is passed.
     */
    private void notifyListener()
    {
        getModelChangeListener().treeModelChanged(changedNode);
    }

    /**
     * Resolves the specified node regarding the parent flag. If the parent flag
     * is set, the node's parent is returned (if any); otherwise, the node
     * itself is returned.
     *
     * @param nd the node in question
     * @param parent the parent flag
     * @return the resolved node
     */
    private static ConfigurationNode resolveParent(ConfigurationNode nd,
            boolean parent)
    {
        if (parent)
        {
            ConfigurationNode parentNode = nd.getParentNode();
            return (parentNode != null) ? parentNode : nd;
        }
        return nd;
    }
}
