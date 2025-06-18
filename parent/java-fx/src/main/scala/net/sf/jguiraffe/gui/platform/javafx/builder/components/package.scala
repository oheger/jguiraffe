/*
 * Copyright 2006-2025 The JGUIraffe Team.
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

import net.sf.jguiraffe.gui.builder.components.model.TextIconAlignment
import net.sf.jguiraffe.gui.builder.components.tags.{ComponentBaseTag, TabbedPaneTag}
import net.sf.jguiraffe.gui.builder.components.{Color, Orientation}
import net.sf.jguiraffe.gui.platform.javafx.builder.components.widget.{Styles, JavaFxFont,
JavaFxStylesHandler}
import org.apache.commons.lang.StringUtils

package object components {
  /**
   * A mapping between JavaFX ''Side'' literals and ''JGUIraffe Placement''
   * constants.
   */
  val PlacementMapping: Map[TabbedPaneTag.Placement, Side] =
    Map(TabbedPaneTag.Placement.BOTTOM -> Side.BOTTOM,
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
    * Tries to convert the specified object into a font.
    * @param ft the object to be converted
    * @return an option with the resulting ''JavaFxFont''
    */
  def toFont(ft: AnyRef): Option[JavaFxFont] =
    Option(ft) flatMap { font =>
      font match {
        case f: JavaFxFont => Some(f)
        case _ => None
      }
    }

  /**
   * Initializes the specified node with the properties defined by the passed
   * in properties object.
   * @param node the node to be initialized
   * @param properties the properties to be set
   * @return the modified node object
   */
  def initNodeProperties(node: Node, properties: NodeProperties): Node = {
    val styles = Styles(node.getStyle)
    val stylesHandler = new JavaFxStylesHandler(styles)
    properties.background foreach stylesHandler.setBackgroundColor
    properties.foreground foreach stylesHandler.setForegroundColor
    properties.font foreach stylesHandler.setFont
    properties.id foreach node.setId

    val styleDef = stylesHandler.styles.toExternalForm()
    if (!styleDef.isEmpty) {
      node setStyle styleDef
    }
    node
  }

  /**
    * Creates a ''NodeProperties'' object from the data of the given tag.
    * @param tag the tag
    * @return the corresponding node properties
    */
  private [builder] def extractNodeProperties(tag: ComponentBaseTag): NodeProperties = {
    NodeProperties(background = Option(tag.getBackgroundColor),
      foreground = Option(tag.getForegroundColor), font = toFont(tag.getFont),
      id = extractNodeID(tag))
  }

  /**
    * Obtains the node ID from the given tag if it is defined.
    * @param tag the tag
    * @return an option for the node ID
    */
  private def extractNodeID(tag: ComponentBaseTag): Option[String] =
    if (StringUtils.isEmpty(tag.getName)) None
    else Some(tag.getName)
}


/**
  * A data class representing the properties defined by UI component tags that
  * can be applied to all kinds of nodes.
  *
  * All properties are optional.
  *
  * @param background the background color
  * @param foreground the foreground color
  * @param font the font
  * @param id the node ID
  */
private case class NodeProperties(background: Option[Color], foreground: Option[Color], font:
Option[JavaFxFont], id: Option[String] = None)

