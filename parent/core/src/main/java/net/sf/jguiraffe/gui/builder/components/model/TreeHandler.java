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

import net.sf.jguiraffe.gui.forms.ComponentHandler;

import org.apache.commons.configuration.HierarchicalConfiguration;

/**
 * <p>
 * A specialized <code>ComponentHandler</code> interface for dealing with tree
 * components.
 * </p>
 * <p>
 * A tree component provides some enhanced functionality not covered by the
 * default methods defined in the <code>ComponentHandler</code> interface.
 * Therefore this interface is available for accessing this special
 * functionality. New methods have been introduced for dealing for instance with
 * querying the tree's selection or registering special event handlers.
 * </p>
 * <p>
 * Note that this <code>ComponentHandler</code> is of type <code>Object</code>.
 * This is due to the fact that a tree supports both single and multiple
 * selections. In the former case the handler's data is an object of type
 * <code>{@link TreeNodePath}</code>. In the latter case it is an array of this
 * type.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TreeHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface TreeHandler extends ComponentHandler<Object>
{
    /**
     * Returns the path to the selected node. This method can be used for trees
     * supporting single selection only. The <code>TreeNodePath</code> object
     * returned points to the selected node. If nothing is selected, result will
     * be <b>null</b>.
     *
     * @return the path to the selected node
     */
    TreeNodePath getSelectedPath();

    /**
     * Sets a single selected node. Using this method the selection of the tree
     * is set to exactly one node.
     *
     * @param path the path to the selected node (must not be <b>null</b>)
     */
    void setSelectedPath(TreeNodePath path);

    /**
     * Returns an array with the paths to all selected nodes. This method can be
     * used if multiple selection is active for querying all selected nodes at
     * once. If nothing is selected, an empty array is returned.
     *
     * @return an array with the paths of all selected nodes
     */
    TreeNodePath[] getSelectedPaths();

    /**
     * Adds the specified path to the selection of the tree. With this method
     * the selection can be extended. For this to work the tree must support
     * multiple selection.
     *
     * @param path the path pointing to the node which should be added to the
     *        selection (must not be <b>null</b>)
     */
    void addSelectedPath(TreeNodePath path);

    /**
     * Removes the selection. After calling this method no node is selected any
     * more.
     */
    void clearSelection();

    /**
     * Adds a <code>TreeExpansionListener</code> to this tree component. This
     * listener will be notified whenever a node of this tree is expanded or
     * collapsed.
     *
     * @param l the listener to add (must not be <b>null</b>)
     * @throws IllegalArgumentException if the listener is <b>null</b>
     */
    void addExpansionListener(TreeExpansionListener l);

    /**
     * Removes the specified expansion listener from this tree component.
     *
     * @param l the listener to remove
     */
    void removeExpansionListener(TreeExpansionListener l);

    /**
     * Adds a <code>TreePreExpansionListener</code> to this tree component. This
     * listener will be notified whenever a tree node is about to be expanded or
     * collapsed and has the opportunity to forbid this operation.
     *
     * @param l the listener to add (must not be <b>null</b>)
     * @throws IllegalArgumentException if the listener is <b>null</b>
     */
    void addPreExpansionListener(TreePreExpansionListener l);

    /**
     * Removes the specified <code>TreePreExpansionListener</code> from this
     * tree component.
     *
     * @param l the listener to remove
     */
    void removePreExpansionListener(TreePreExpansionListener l);

    /**
     * Expands the node specified by the given path.
     *
     * @param path the path
     */
    void expand(TreeNodePath path);

    /**
     * Collapses the node specified by the given path.
     *
     * @param path the path
     */
    void collapse(TreeNodePath path);

    /**
     * Returns the tree's data model. This is a hierarchical configuration
     * object. By manipulating this configuration the tree's content can be
     * changed.
     *
     * @return the configuration that serves as data model for this tree
     */
    HierarchicalConfiguration getModel();
}
