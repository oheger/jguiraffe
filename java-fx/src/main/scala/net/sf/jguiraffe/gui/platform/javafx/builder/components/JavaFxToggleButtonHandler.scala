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

import javafx.beans.value.ObservableValue
import javafx.scene.control.ToggleButton
import net.sf.jguiraffe.gui.platform.javafx.builder.event.ChangeEventSource

/**
 * A JavaFX ''ComponentHandler'' implementation for managing toggle button
 * components.
 *
 * This ''ComponentHandler'' class can deal with the JavaFX classes
 * ''ToggleButton'', and ''RadioButton''. Basic functionality is already
 * provided by the base class ''JavaFxButtonHandler''. The handler's data is
 * mapped to the toggle button's ''selected'' property. This property is also
 * used for supporting change listeners.
 *
 * Actually, this class is pretty similar to
 * [[net.sf.jguiraffe.gui.platform.javafx.builder.components.JavaFxCheckBoxHandler]].
 * Because in JavaFX check boxes and toggle buttons are in separate inheritance
 * trees it is not possible to use a single component handler implementation
 * for both.
 *
 * @param button the managed toggle button
 * @param actionCmd the optional action command
 */
private class JavaFxToggleButtonHandler(button: ToggleButton, actionCmd: String = null)
  extends JavaFxButtonHandler(button, actionCmd) with ChangeEventSource {
  override def getData = button.isSelected

  /**
   * @inheritdoc This implementation returns the button's ''selected''
   * property.
   */
  def observableValue: ObservableValue[_ <: AnyRef] = button.selectedProperty

  /**
   * @inheritdoc This implementation changes the selected state of the
   * managed toggle button.
   */
  override protected def setSelectedState(f: Boolean) {
    button setSelected f
  }
}
