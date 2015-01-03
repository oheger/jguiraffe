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
package net.sf.jguiraffe.gui.platform.javafx.builder

import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.control.ContentDisplay
import net.sf.jguiraffe.gui.builder.components.{Color, Orientation}
import net.sf.jguiraffe.gui.builder.components.model.TextIconAlignment
import net.sf.jguiraffe.gui.builder.components.tags.TabbedPaneTag
import net.sf.jguiraffe.gui.platform.javafx.builder.components.widget.{JavaFxFont,
JavaFxStylesHandler}

package object components {
  /**
   * A mapping between JavaFX ''Side'' literals and ''JGUIraffe Placement''
   * constants.
   */
  val PlacementMapping = Map(TabbedPaneTag.Placement.BOTTOM -> Side.BOTTOM,
    TabbedPaneTag.Placement.LEFT -> Side.LEFT,
    TabbedPaneTag.Placement.RIGHT -> Side.RIGHT,
    TabbedPaneTag.Placement.TOP -> Side.TOP)

  /**
   * Converts a ''TextIconAlignment'' enumeration value to the corresponding
   * Java FX ''ContentDisplay'' value.
   * @param al the alignment to be converted
   * @return the corresponding ''ContentDisplay'' value
   */
  def convertAlignment(al: TextIconAlignment): ContentDisplay = {
    al match {
      case TextIconAlignment.CENTER => ContentDisplay.CENTER
      case TextIconAlignment.RIGHT => ContentDisplay.RIGHT
      case _ => ContentDisplay.LEFT
    }
  }

  /**
   * Converts a ''ContentDisplay'' enumeration literal to the corresponding
   * ''JGUIraffe TextIconAlignment'' value.
   * @param cd the ''ContentDisplay'' to be converted
   * @return the corresponding ''TextIconAlignment'' value
   */
  def convertContentDisplay(cd: ContentDisplay): TextIconAlignment = {
    cd match {
      case ContentDisplay.RIGHT => TextIconAlignment.RIGHT
      case ContentDisplay.CENTER => TextIconAlignment.CENTER
      case _ => TextIconAlignment.LEFT
    }
  }

  /**
   * Converts a ''JGUIraffe Placement'' enumeration literal to the corresponding
   * ''Side'' value.
   * @param pl the ''Placement'' literal to be converted
   * @return the corresponding ''Side'' value
   */
  def convertPlacementToSide(pl: TabbedPaneTag.Placement): Side =
    PlacementMapping.getOrElse(pl, Side.TOP)

  /**
   * Converts the given JGUIraffe ''Orientation'' value to a JavaFX
   * ''Orientation'' value.
   * @param or the input orientation
   * @return the converted orientation
   */
  def convertOrientation(or: Orientation): javafx.geometry.Orientation =
    if (Orientation.VERTICAL == or) javafx.geometry.Orientation.VERTICAL
    else javafx.geometry.Orientation.HORIZONTAL

  /**
   * Initializes the specified node with the properties defined by the passed
   * in properties object.
   * @param node the node to be initialized
   * @param properties the properties to be set
   * @return the modified node object
   */
  def initNodeProperties(node: Node, properties: NodeProperties): Node = {
    val stylesHandler = new JavaFxStylesHandler
    if (properties.background != null) {
      stylesHandler setBackgroundColor properties.background
    }
    if (properties.foreground != null) {
      stylesHandler setForegroundColor properties.foreground
    }

    properties.font match {
      case f: JavaFxFont =>
        stylesHandler setFont f
      case _ => // ignore
    }

    val styleDef = stylesHandler.styles.toExternalForm()
    if (!styleDef.isEmpty) {
      node setStyle styleDef
    }
    node
  }
}

/**
 * A data class representing the properties defined by UI component tags that
 * can be applied to all kinds of nodes.
 * @param background the background color
 * @param foreground the foreground color
 * @param font the font
 */
private case class NodeProperties(background: Color, foreground: Color, font: AnyRef)
