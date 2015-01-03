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
package net.sf.jguiraffe.gui.builder.components.tags;

import org.apache.commons.configuration.tree.ConfigurationNode;

/**
 * <p>
 * Definition of an interface for components that determine the icons displayed
 * by a tree component.
 * </p>
 * <p>
 * When a tree is constructed an arbitrary number of named icons can be
 * specified using nested <code>{@link TreeIconTag}</code> tags. When rendering
 * the tree the <code>TreeIconHandler</code> is queried for each node to be
 * displayed. It is passed in the current tree node and some flags describing
 * its state (whether it is expanded or a leaf node). The return value is the
 * name of an icon that was defined on construction time of the tree. The
 * corresponding icon will then be displayed for this node.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TreeIconHandler.java 205 2012-01-29 18:29:57Z oheger $
 * @see TreeTag
 * @see TreeIconTag
 */
public interface TreeIconHandler
{
    /**
     * Returns the name of the icon for the specified node. The name must match
     * exactly the name of an icon specified when the tree was constructed.
     *
     * @param node the current node
     * @param expanded a flag whether this node is expanded
     * @param leaf a flag whether this node is a leaf node
     * @return the name of the icon to be displayed for this node
     */
    String getIconName(ConfigurationNode node, boolean expanded, boolean leaf);
}
