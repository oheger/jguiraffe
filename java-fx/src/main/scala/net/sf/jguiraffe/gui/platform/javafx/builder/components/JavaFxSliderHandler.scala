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

import scala.beans.BeanProperty

import javafx.scene.control.Slider
import net.sf.jguiraffe.gui.platform.javafx.builder.event.ChangeEventSource

/**
 * A ''ComponentHandler'' implementation wrapping a JavaFX ''Slider''
 * component.
 *
 * This handler implementation is pretty straight-forward: It maps the
 * handler's data to the slider's ''value'' property (here a conversion between
 * double and integer is required). Change events are supported; they are
 * generated when the slider's value is changed.
 *
 * @param slider the wrapped ''Slider'' component
 */
private class JavaFxSliderHandler(slider: Slider)
  extends JavaFxComponentHandler[Integer](slider) with ChangeEventSource {
  @BeanProperty val `type` = classOf[Integer]

  override val observableValue = slider.valueProperty

  /**
   * @inheritdoc This implementation returns the slider's current value
   * converted to an integer.
   */
  def getData: Integer = slider.getValue.toInt

  /**
   * @inheritdoc This implementation sets the slider's value property. If the
   * argument is '''null''', this call has no effect.
   */
  def setData(value: Integer) {
    if (value != null) {
      slider setValue value.doubleValue
    }
  }
}
