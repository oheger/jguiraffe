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
package net.sf.jguiraffe.gui.platform.javafx.builder.components

import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.{Pane, StackPane}

import net.sf.jguiraffe.gui.builder.components.Color
import net.sf.jguiraffe.gui.builder.components.tags.PanelTag
import net.sf.jguiraffe.gui.platform.javafx.builder.NodeProperties
import net.sf.jguiraffe.gui.platform.javafx.layout.ContainerWrapper

/**
 * Companion object for ''BorderPanelFactory''.
 */
private object BorderPanelFactory {
  /** CSS style class for the border panel with a border. */
  val StylePanelWithBorder = "bordered-panel-border"

  /** CSS style class for the border panel without a border. */
  val StylePanelNoBorder = "bordered-panel-no-border"

  /** CSS style class for the content within the border pane. */
  val StylePanelContent = "bordered-panel-content"

  /** CSS style class for the label representing the title of the panel. */
  val StylePanelTitle = "bordered-panel-title"

  /** The default background color for title labels. */
  val DefaultBackgroundColor = Color.newLogicInstance("-fx-control-inner-background")

  /**
   * Adds the given style sheet class to the given node.
   * @param node the node
   * @param styleClass the style class to be added
   * @return the modified node
   */
  private def addStyle(node: Node, styleClass: String): Node = {
    node.getStyleClass add styleClass
    node
  }

  /**
   * Adds a label for the title of the panel.
   * @param pane the panel
   * @param tag the tag defining the panel
   */
  private def addTitleLabel(pane: StackPane, tag: PanelTag) {
    val title = createTitleLabel(tag)
    pane.getChildren add addStyle(title, StylePanelTitle)
    StackPane.setAlignment(title, Pos.TOP_CENTER)
  }

  /**
   * Creates the label control to be used as title for a panel.
   * @param tag the tag defining the panel
   * @return the title label
   */
  private def createTitleLabel(tag: PanelTag): Node = {
    val title = new Label(s" ${tag.getTextData.getCaption} ")
    val properties = NodeProperties(foreground = Option(tag.getColor),
      font = toFont(tag.getTitleFont), background = Option(titleLabelBackgroundColor(tag)))
    initNodeProperties(title, properties)
  }

  /**
   * Determines the background color for the title label. If a color for the
   * owning panel is defined, this one is used. Otherwise, the default background
   * color is used.
   * @param tag the tag defining the panel
   * @return the color to be used as background for the title label
   */
  def titleLabelBackgroundColor(tag: PanelTag): Color =
    if (tag.getBackgroundColor != null) tag.getBackgroundColor
    else DefaultBackgroundColor
}

/**
 * A factory class for creating panels with a border and/or a title.
 *
 * This class is used by [[JavaFxComponentManager]] for creating panel objects
 * based on a ''PanelTag''. It allows creating special panels that embed other
 * panels and add an optional border or title around them.
 */
private class BorderPanelFactory {

  import net.sf.jguiraffe.gui.platform.javafx.builder.components.BorderPanelFactory._

  /**
   * Creates a ''Pane'' that wraps the specified content node with the
   * decorations defined by the given panel tag.
   * @param tag the panel tag
   * @param content the content node
   * @return the newly created panel
   */
  def createBorderPanel(tag: PanelTag, content: Node): Pane = {
    addStyle(content, StylePanelContent)
    val pane = new StackPane
    if (tag.getTextData.isDefined) {
      addTitleLabel(pane, tag)
    }
    addStyle(pane, if (tag.isBorder) StylePanelWithBorder else StylePanelNoBorder)
    pane.getChildren add content

    pane
  }

  /**
   * Returns an ''Option'' for a ''PaneTransformer'' which applies this factory
   * to a panel created for the specified tag. If the tag declares a border or a
   * title, the transformer function returned by this method will create a border
   * panel with appropriate attributes that wraps the passed in panel. Otherwise,
   * no transformation is necessary, and result is ''None''.
   * @param tag the panel tag
   * @return a transformer function for decorating the resulting panel
   */
  def getPaneTransformer(tag: PanelTag): Option[ContainerWrapper.PaneTransformer] =
    if (tag.isBorder || tag.getTextData.isDefined) Some(createBorderPanel(tag, _))
    else None
}
