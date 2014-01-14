/*
 * Copyright 2006-2013 The JGUIraffe Team.
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

import javafx.application.Platform
import javafx.beans.property.DoubleProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue

/**
 * A special ''ChangeListener'' implementation which ensures that the position
 * of an associated ''SplitPane'' is updated accordingly when the pane's
 * size is changed.
 *
 * Splitter components in Swing support a property called ''reseizeWeight''
 * which controls how additional space becoming available gets distributed
 * over the components managed by the splitter. JavaFX does not support such a
 * feature out of the box. Thus this listener class is used to implement it.
 * Basically, the listener gets invoked whenever the size of the managed
 * ''SplitPane'' is changed. It then updates the divider position based on the
 * current ''reseizeWeight'' factory.
 *
 * The ''reseizeWeight'' property can be a value between 0 and 1 (including).
 * The meaning is the same as for the Swing implementation: 0 means that
 * only the right/bottom component is affected by a size change; a value of
 * 1 causes only the left/top component to be changed. All other values
 * change the sizes of both components according to the factor.
 *
 * @param initialPosition the initial position (in pixels); a value of 0 means
 * that no initial position is set; in this case, JavaFX determines the
 * initial divider position
 * @param reseizeWeight the factor for resizing as described above
 * @param positionProperty the property with the divider position to be updated
 */
private class SplitPaneResizeListener(val initialPosition: Int,
  val reseizeWeight: Double, val positionProperty: DoubleProperty)
  extends ChangeListener[Number] {
  /**
   * @inheritdoc Reacts on size changes of a ''SplitPane''.
   */
  override def changed(obs: ObservableValue[_ <: Number], oldVal: Number, newVal: Number) {
    if (initialUpdate(oldVal, newVal)) {
      handleInitialUpdate(newVal)
    } else {
      handleResize(oldVal, newVal)
    }
  }

  /**
   * Tests whether the current change is the initial update of the monitored
   * property. In this case, the monitored split pane was assigned a size for
   * the first time, and the initial divider position (if defined) has to be
   * set.
   * @param oldVal the old value of the monitored property
   * @param newVal the new value of the monitored property
   * @return a flag whether this is the initial update
   */
  private def initialUpdate(oldVal: Number, newVal: Number): Boolean =
    newVal.doubleValue > 0 && oldVal.intValue == 0

  /**
   * Handle the initial update of the monitored property. This method sets the
   * position property to the initial value if defined.
   * @param newVal the new value of the monitored property
   */
  private def handleInitialUpdate(newVal: Number) {
    if (initialPosition > 0) {
      updatePosition(initialPosition / newVal.doubleValue)
    }
  }

  /**
   * Handles a change in the monitored property. Depending on the
   * ''reseizeWeight'' property, the additional space is distributed on the
   * components of the associated split pane.
   */
  private def handleResize(oldVal: Number, newVal: Number) {
    val diff = newVal.doubleValue - oldVal.doubleValue
    val pos = positionProperty.get
    val sizeLeftOld = oldVal.doubleValue * pos
    val sizeLeftNew = sizeLeftOld + reseizeWeight * diff
    updatePosition(sizeLeftNew / newVal.doubleValue)
  }

  /**
   * Updates the position property with the specified value. This happens later
   * in the JavaFX thread to ensure that there is no interference with
   * adjustments done by the split pane.
   * @param pos the new position value to be set
   */
  private def updatePosition(pos: Double) {
    Platform.runLater(new Runnable {
      def run() {
        positionProperty set pos
      }
    })
  }
}
