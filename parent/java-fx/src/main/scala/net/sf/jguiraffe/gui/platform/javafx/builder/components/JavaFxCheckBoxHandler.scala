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

import javafx.beans.value.ObservableValue
import javafx.scene.control.CheckBox
import net.sf.jguiraffe.gui.platform.javafx.builder.event.ChangeEventSource

/**
 * A JavaFX-specific ''ComponentHandler'' implementation for managing
 * ''CheckBox'' controls.
 *
 * A JavaFX ''CheckBox'' can be handled in a similar way as a button, therefore
 * this class extends ''JavaFxButtonHandler''. The main difference is that
 * the boolean data value of the ''ComponentHandler'' can now be mapped to the
 * check box's ''selected'' property. This property is also used for
 * supporting change listeners.
 *
 * @param checkBox the wrapped ''CheckBox'' control
 */
private class JavaFxCheckBoxHandler(checkBox: CheckBox)
  extends JavaFxButtonHandler(checkBox) with ChangeEventSource {
  override def getData = checkBox.isSelected

  /**
   * @inheritdoc This implementation returns the check box's ''selected''
   * property.
   */
  def observableValue: ObservableValue[_ <: AnyRef] = checkBox.selectedProperty

  /**
   * @inheritdoc This implementation sets the selected state of the managed
   * check box.
   */
  override protected def setSelectedState(f: Boolean) {
    checkBox.setSelected(f)
  }
}
