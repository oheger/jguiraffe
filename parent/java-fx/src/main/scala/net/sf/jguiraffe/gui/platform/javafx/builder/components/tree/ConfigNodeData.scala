/**
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
package net.sf.jguiraffe.gui.platform.javafx.builder.components.tree

import org.apache.commons.configuration.tree.ConfigurationNode

/**
 * A simple data class representing information about a tree node.
 *
 * This class is just a thin wrapper around a ''ConfigurationNode'' which
 * actually contains the data about a tree node. It defines a ''toString()''
 * method to construct the string which is displayed by the tree cell.
 *
 * @param node the underlying ''ConfigurationNode''
 */
private case class ConfigNodeData(node: ConfigurationNode) {
  override def toString = node.getName
}
