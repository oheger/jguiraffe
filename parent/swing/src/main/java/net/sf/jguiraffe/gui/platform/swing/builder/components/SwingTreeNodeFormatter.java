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
package net.sf.jguiraffe.gui.platform.swing.builder.components;

import org.apache.commons.configuration.tree.ConfigurationNode;

/**
 * <p>
 * An internally used helper class for formatting the nodes of a tree view.
 * </p>
 * <p>
 * The main task of this class is to return a string representation for the node
 * of a tree. Nodes are represented by {@code ConfigurationNode} objects
 * (although the Swing model operates on untyped objects internally). The
 * default format is just the name of the node.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: $
 */
class SwingTreeNodeFormatter
{
    /**
     * Returns the text to be displayed for the passed in tree node. This
     * implementation expects that the passed in node is a
     * {@code ConfigurationNode} and returns its name.
     *
     * @param node the tree node
     * @return the text representation for this node
     */
    public String textForNode(Object node)
    {
        return ((ConfigurationNode) node).getName();
    }
}
