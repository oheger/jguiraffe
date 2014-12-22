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
package net.sf.jguiraffe.gui.platform.javafx.builder.components

import javafx.application.Platform
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.SplitPane
import net.sf.jguiraffe.gui.builder.components.FormBuilderException
import net.sf.jguiraffe.gui.builder.components.tags.SplitterTag
import net.sf.jguiraffe.gui.platform.javafx.layout.ContainerWrapper

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

/**
 * A trait for creating ''SplitPane'' components.
 *
 * An object implementing this trait is associated with the
 * [[net.sf.jguiraffe.gui.platform.javafx.builder.components.JavaFxComponentManager]];
 * it is invoked whenever a ''SplitPane'' is requested. All properties of the
 * new ''SplitPane'' are defined by the passed in tag.
 *
 * An implementation of this trait also has to ensure that a ''SplitPane'' behaves
 * correctly when it is resized, i.e. additional size is correctly distributed
 * on the managed components.
 */
trait SplitPaneFactory {
  /**
   * Creates and initializes a ''SplitPane'' component based on the properties
   * defined by the passed in tag.
   * @param tag the tag defining the ''SplitPane'' component
   * @return the newly created ''SplitPane''
   */
  def createSplitPane(tag: SplitterTag): SplitPane
}

private object SplitPaneFactoryImpl {
  /**
   * Extracts the correct size property from the given split pane taking the
   * orientation into account.
   * @param split the split pane
   * @return the size property of this split pane
   */
  private def fetchSizeProperty(split: SplitPane): ReadOnlyDoubleProperty =
    if (Orientation.HORIZONTAL == split.getOrientation) split.widthProperty
    else split.heightProperty

  /**
   * Extracts the property for the split pane's divider position.
   * @param split the split pane
   * @return the position property
   */
  private def fetchPosProperty(split: SplitPane): DoubleProperty =
    split.getDividers.get(0).positionProperty
}

/**
 * A default implementation of the ''SplitPaneFactory'' trait.
 *
 * This implementation creates a ''SplitPane'' based on the passed in tag.
 * Note: The functions that can be passed to the constructor are mainly used
 * to improve testability. If a ''SplitPane'' would be easy to mock, they
 * would not be necessary.
 *
 * @param funcSizeProp a function for obtaining the size property from a pane
 * @param funcPosProp a function for obtaining the position property form a pane
 */
private class SplitPaneFactoryImpl(
  val funcSizeProp: SplitPane => ReadOnlyDoubleProperty = SplitPaneFactoryImpl.fetchSizeProperty,
  val funcPosProp: SplitPane => DoubleProperty = SplitPaneFactoryImpl.fetchPosProperty)
  extends SplitPaneFactory {
  def createSplitPane(tag: SplitterTag): SplitPane = {
    val split = createAndInitSplitPane(tag)
    val listener = createResizeListener(tag, split)
    funcSizeProp(split) addListener listener
    split
  }

  /**
   * Creates and initializes a split pane from the passed in tag.
   * @param tag the tag
   * @return the split pane
   */
  private def createAndInitSplitPane(tag: SplitterTag): SplitPane = {
    val split = new SplitPane
    split setOrientation determineOrientation(tag)
    split.getItems.addAll(toNode(tag.getFirstComponent),
      toNode(tag.getSecondComponent))
    split
  }

  /**
   * Determines the orientation for the split pane. This method maps the
   * logic constants used by JGUIraffe to JavaFX-specific values.
   * @param tag the splitter tag
   * @return the orientation of the split pane
   */
  private def determineOrientation(tag: SplitterTag): Orientation =
    convertOrientation(tag.getSplitterOrientation)

  /**
   * Converts a child component for the split pane to a Node. This requires a
   * special treatment for ''ContainerWrapper'' objects.
   * @param comp the component to be converted
   * @return the converted component
   * @throws FormBuilderException if the component is not supported
   */
  private def toNode(comp: Any): Node =
    comp match {
      case nd: Node =>
        nd

      case wrap: ContainerWrapper =>
        wrap.createContainer()

      case c =>
        throw new FormBuilderException("Unsupported child component: " + c)
    }

  /**
   * Creates a resize listener for controlling the given split pane's size
   * changes.
   * @param tag the tag defining the split pane
   * @param split the split pane
   * @return the resize listener
   */
  private def createResizeListener(tag: SplitterTag, split: SplitPane): SplitPaneResizeListener =
    new SplitPaneResizeListener(tag.getPos, tag.getResizeWeight,
      funcPosProp(split))
}
