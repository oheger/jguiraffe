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

import javafx.scene.control.ComboBox
import net.sf.jguiraffe.gui.builder.components.tags.ListModelUtils
import net.sf.jguiraffe.gui.platform.javafx.builder.event.ChangeEventSource

/**
 * A specialized ''ComponentHandler'' implementation for JavaFX combo boxes.
 *
 * This handler implementation supports both editable and non editable combo
 * boxes.
 *
 * @param combo the managed combo box control
 */
private class JavaFxComboBoxHandler(combo: ComboBox[Object])
  extends JavaFxComponentHandler[Object](combo) with ListModelSupport
  with ChangeEventSource {
  protected val displayList = combo.getItems

  def getType: Class[_] = getListModel.getType

  /**
   * @inheritdoc This implementation checks whether a value is set in the
   * combo box which exists as display object in the list model. If so, the
   * corresponding value object is returned. Otherwise, the object is
   * transformed to a value object. '''Null''' values are returned directly.
   */
  def getData: Object = {
    val value = combo.getValue
    if (value == null) null
    else {
      val idx = ListModelUtils.getDisplayIndex(getListModel, value)
      if (idx != ListModelUtils.IDX_UNDEFINED) getListModel.getValueObject(idx)
      else editableModel.toValue(value)
    }
  }

  /**
   * @inheritdoc This implementation checks whether the object to set is
   * contained as value object in the list model. If so, the corresponding
   * display object is set. Otherwise, it is transformed to a display object.
   * '''null''' values are again treated in a special way.
   */
  def setData(data: Object) {
    val newValue = if (data == null) null
    else {
      val idx = ListModelUtils.getIndex(getListModel, data)
      if (idx != ListModelUtils.IDX_UNDEFINED) getListModel.getDisplayObject(idx)
      else editableModel.toDisplay(data)
    }
    combo setValue newValue
  }

  /**
   * @inheritdoc This implementation returns the combo box's value property.
   */
  def observableValue = combo.valueProperty
}
