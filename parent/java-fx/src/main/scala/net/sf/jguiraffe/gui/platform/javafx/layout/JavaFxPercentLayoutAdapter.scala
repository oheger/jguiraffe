/*
 * Copyright 2006-2016 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.platform.javafx.layout

import java.awt.Rectangle

import javafx.geometry.Orientation
import javafx.scene.Node
import net.sf.jguiraffe.gui.layout.PercentLayoutPlatformAdapter
import net.sf.jguiraffe.gui.layout.UnitSizeHandler

/**
 * The Java FX-based implementation of the ''PercentLayoutPlatformAdapter''
 * interface.
 *
 * This implementation operates on Java FX nodes and provides methods for
 * querying their sizes and setting their bounds.
 *
 * @param components a list with the nodes managed by the associated container
 * @param constraints an array with constraints for the managed components
 * @param sizeHandler an Option defining the ''UnitSizeHandler'' to be used
 */
class JavaFxPercentLayoutAdapter(components: Array[Node],
  constraints: Array[Object], sizeHandler: Option[UnitSizeHandler] = None)
  extends PercentLayoutPlatformAdapter {
  /** The number of components managed by the associated container. */
  val getComponentCount = constraints.length

  /** The size handler used for Java FX percent layouts. */
  val getSizeHandler = sizeHandler.getOrElse(new JavaFxUnitSizeHandler)

  def getComponent(index: Int): Object = components(index)

  def getConstraints(index: Int): Object = constraints(index)

  def getMinimumComponentSize(component: Object, vert: Boolean): Int = {
    val node = asNode(component)
    getSize(node, vert, node.minWidth, node.minHeight)
  }

  def getPreferredComponentSize(component: Object, vert: Boolean): Int = {
    val node = asNode(component)
    // Note: The preferred width seems to be slightly too small; therefore, it
    // is increased by 1 pixel as a workaround. See BUG-21.
    getSize(node, vert, node.prefWidth(_) + 1, node.prefHeight)
  }

  def setComponentBounds(component: Object, bounds: Rectangle) {
    val node = asNode(component)
    node.setLayoutX(bounds.x)
    node.setLayoutY(bounds.y)
    node.resize(bounds.width, bounds.height)
  }

  /**
   * Determines the height of a node based on the passed in functions. This
   * method takes the node's content bias into account. If necessary, the
   * node's width is determined first.
   * @param node the node in question
   * @param fHeight a function for obtaining the node's height
   * @param fWidth a function for obtaining the node's width
   * @return the height of this node
   */
  private def getHeight(node: Node, fHeight: Double => Double,
    fWidth: Double => Double): Int = {
    val bias = node.getContentBias
    if (bias == null || bias == Orientation.VERTICAL) {
      fHeight(-1).toInt
    } else {
      val width = fWidth(-1)
      fHeight(width).toInt
    }
  }

  /**
   * Determines the width of a node based on the passed in functions. This
   * method takes the node's content bias into account. If necessary, the
   * node's height is determined first.
   * @param node the node in question
   * @param fWidth a function for obtaining the node's width
   * @param fHeight a function for obtaining the node's height
   * @return the width of this node
   */
  private def getWidth(node: Node, fWidth: Double => Double,
    fHeight: Double => Double): Int = {
    val bias = node.getContentBias
    if (bias == null || bias == Orientation.HORIZONTAL) {
      fWidth(-1).toInt
    } else {
      val height = fHeight(-1)
      fWidth(height).toInt
    }
  }

  /**
   * Determines the size of a component based on the passed in functions and the
   * flag for vertical or horizontal.
   * @param node the node in question
   * @param vert flag for height or width
   * @param fWidth the function for querying the width
   * @param fHeight the function for querying the height
   * @return the desired size of this component
   */
  private def getSize(node: Node, vert: Boolean, fWidth: Double => Double,
    fHeight: Double => Double): Int = {
    if (vert) {
      getHeight(node, fHeight, fWidth)
    } else {
      getWidth(node, fWidth, fHeight)
    }
  }

  /**
   * Helper method for casting the component object to a node.
   * @param component the component to be converted
   * @return the resulting node object
   */
  private def asNode(component: Object): Node = component.asInstanceOf[Node]
}
