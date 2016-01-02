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

import java.awt.Dimension
import java.awt.Rectangle

import javafx.scene.layout.Pane
import net.sf.jguiraffe.gui.layout.PercentLayoutBase

/**
 * A specialized Java FX Pane implementation which uses a percent layout to
 * layout its children.
 *
 * This class implements typical layout methods of a Java FX pane in a way
 * that the passed in ''PercentLayoutBase'' object is used to calculate
 * component sizes and perform the layout.
 *
 * @param percentLayout the percent layout object actually handling the layout
 * @param wrapper the wrapper object for the associated container
 */
class PercentLayoutPane(val percentLayout: PercentLayoutBase,
  val wrapper: ContainerWrapper) extends Pane {
  /**
   * @inheritdoc This implementation delegates to the associated
   * ''PercentLayoutBase'' object for the size calculation.
   */
  protected[layout] override def computeMinHeight(width: Double): Double =
    computeMinSize().height

  /**
   * @inheritdoc This implementation delegates to the associated
   * ''PercentLayoutBase'' object for the size calculation.
   */
  protected[layout] override def computeMinWidth(height: Double): Double =
    computeMinSize().width

  /**
   * @inheritdoc This implementation delegates to the associated
   * ''PercentLayoutBase'' object for the size calculation.
   */
  protected[layout] override def computePrefHeight(width: Double): Double =
    computePrefSize().height

  /**
   * @inheritdoc This implementation delegates to the associated
   * ''PercentLayoutBase'' object for the size calculation.
   */
  protected[layout] override def computePrefWidth(height: Double): Double =
    computePrefSize().width

  /**
   * @inheritdoc This implementation determines the current insets and size
   * of this pane. Then it delegates to the associated ''PercentLayoutBase''
   * object to apply the layout based on this data.
   */
  protected[layout] override def layoutChildren() {
    val insets = getInsets
    percentLayout.performLayout(wrapper, new Rectangle(insets.getLeft.toInt,
      insets.getTop.toInt, insets.getRight.toInt, insets.getBottom.toInt),
      new Dimension(getWidth.toInt, getHeight.toInt))
  }

  /**
   * Calculates the minimum layout size.
   * @return the minimum layout size
   */
  private def computeMinSize(): Dimension =
    addInsets(percentLayout.calcMinimumLayoutSize(wrapper))

  /**
   * Calculates the preferred layout size.
   * @return the preferred layout size
   */
  private def computePrefSize(): Dimension =
    addInsets(percentLayout.calcPreferredLayoutSize(wrapper))

  /**
   * Adds the insets of the associated container to the given size.
   * @param size the size to be manipulated
   * @return the manipulated size
   */
  private def addInsets(size: Dimension): Dimension = {
    val insets = getInsets
    size.width += (insets.getLeft + insets.getRight).toInt
    size.height += (insets.getTop + insets.getBottom).toInt
    size
  }
}
