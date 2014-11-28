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

import scala.beans.BeanProperty

import javafx.scene.control.ProgressBar
import net.sf.jguiraffe.gui.builder.components.model.ProgressBarHandler
import net.sf.jguiraffe.gui.platform.javafx.builder.event.ChangeEventSource

/**
 * The JavaFX ''ComponentHandler'' implementation for progress bar components.
 *
 * This handler can be used to display a progress bar. The current value of the
 * bar can be set as an integer value - the borders for this value are passed
 * to the handler at construction time; it is scaled to the double values
 * between 0 and 1 as accepted by the JavaFX ''ProgressBar'' component.
 *
 * Change events are supported; they are generated when the progress bar's
 * value is changed.
 *
 * JavaFX does not support setting a string value for the current progress.
 * Therefore, the ''progressText'' property required by the
 * ''ProgressBarHandler'' interface is implemented as a dummy.
 *
 * @param progressBar the underlying ''ProgressBar'' component
 * @param min the minimum value of the progress bar
 * @param max the maximum value of the progress bar
 */
private class JavaFxProgressBarHandler(progressBar: ProgressBar,
  val min: Int, val max: Int) extends JavaFxComponentHandler[Integer](progressBar)
  with ProgressBarHandler with ChangeEventSource {
  /** The data type of this handler. */
  @BeanProperty val `type` = classOf[Integer]

  /** The property used for generating change events. */
  override val observableValue = progressBar.progressProperty

  /**
   * Stores the progress text. JavaFX does not support an additional text
   * property. Therefore, the value passed to this property is only stored in
   * an internal member field.
   */
  @BeanProperty var progressText: String = _

  /**
   * @inheritdoc This implementation just delegates to ''getValue''.
   */
  def getData: Integer = getValue

  /**
   * @inheritdoc This implementation delegates to ''setValue()''. If the
   * passed in value is '''null''', this call has no effect.
   */
  def setData(value: Integer) {
    if (value != null) {
      setValue(value.intValue)
    }
  }

  /**
   * @inheritdoc This implementation obtains the current progress value from
   * the ''ProgressBar'' and maps it to the current range as an integer.
   */
  def getValue: Int = min + (progressBar.getProgress * (max - min)).toInt

  /**
   * @inheritdoc This implementation expects that the passed in value is in
   * the range determined by the ''min'' and ''max'' parameters. It maps the
   * value to a percentage value in the interval [0, 1].
   */
  def setValue(v: Int) {
    val progress = (v - min).toDouble / (max - min).toDouble
    progressBar setProgress progress
  }
}
