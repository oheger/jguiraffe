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
package net.sf.jguiraffe.gui.platform.javafx.builder.components.tree

import scala.collection.JavaConversions.mapAsScalaMap

import org.apache.commons.configuration.tree.ConfigurationNode

import javafx.scene.Node
import net.sf.jguiraffe.gui.builder.components.tags.TreeIconHandler

/**
 * The internal default implementation of the
 * [[net.sf.jguiraffe.gui.platform.javafx.builder.components.tree.NodeGraphicsHandler]]
 * trait.
 *
 * This class is initialized with a ''TreeIconHandler'' and a map of icons
 * grouped by names. When the icon for a configuration node is requested the
 * ''TreeIconHandler'' is consulted first. Then the icon with this name is
 * returned from the map. If no such icon is found, result is '''null'''.
 *
 * @param iconHandler the ''TreeIconHandler''
 * @param iconMap the map with all available icons by name
 */
private class NodeGraphicsHandlerImpl(val iconHandler: TreeIconHandler,
  iconMap: java.util.Map[String, Object]) extends NodeGraphicsHandler {
  /** The map with the graphic nodes. */
  private val graphics = createGraphicMap(iconMap)

  def graphicsFor(node: ConfigurationNode, expanded: Boolean, leaf: Boolean): Node = {
    val name = iconHandler.getIconName(node, expanded, leaf)
    graphics.getOrElse(name, null)
  }

  /**
   * Converts the passed in Java map with an unspecified icon type to a Scala
   * immutable map of the correct graphic type.
   * @param iconMap the Java map
   * @return the specific map
   */
  private def createGraphicMap(iconMap: java.util.Map[String, Object]): Map[String, Node] = {
    import collection.JavaConversions._
    iconMap.mapValues(_.asInstanceOf[Node]).toMap
  }
}
