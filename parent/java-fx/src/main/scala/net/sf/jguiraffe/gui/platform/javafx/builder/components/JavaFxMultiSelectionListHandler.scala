/*
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

import scala.Array.canBuildFrom

import javafx.beans.binding.Bindings
import javafx.scene.control.ListView
import javafx.scene.control.SelectionMode
import net.sf.jguiraffe.gui.builder.components.tags.ListModelUtils
import net.sf.jguiraffe.gui.platform.javafx.builder.event.ChangeEventSource

/**
 * A specialized ''ComponentHandler'' implementation for JavaFX list views
 * with multiple selection support.
 *
 * This class manages a ''ListView'' control supporting multiple selected
 * entries. The handler's data is an array of model objects that are selected.
 * Change events are fired if the selection is changed.
 */
private class JavaFxMultiSelectionListHandler(listView: ListView[Object])
  extends JavaFxComponentHandler[Object](listView) with ListModelSupport
  with ChangeEventSource {
  override protected val displayList = listView.getItems

  /**
   * @inheritdoc This implementation combines change events with the number of
   * elements in the collection of selected indices.
   */
  override val observableValue =
    Bindings.size(listView.getSelectionModel.getSelectedIndices)

  // set multiple selection support
  listView.getSelectionModel.setSelectionMode(SelectionMode.MULTIPLE)

  /**
   * @inheritdoc This implementation queries the selected indices from the
   * managed list view. The result is converted to an array model values.
   */
  def getData: Object = {
    val selection = listView.getSelectionModel.getSelectedIndices
    if (selection.isEmpty) null
    else {
      val selArray = new Array[Integer](selection.size)
      selection.toArray(selArray)
      ListModelUtils.getValues(getListModel, selArray map { _.intValue() })
    }
  }

  /**
   * @inheritdoc This implementation converts the passed in array of model
   * objects to their corresponding indices in the model. Then these indices
   * are selected.
   */
  def setData(data: Object) {
    listView.getSelectionModel.clearSelection()
    data match {
      case arr: Array[Object] =>
        val indices = ListModelUtils.getIndices(getListModel, arr)
        if (!indices.isEmpty) {
          listView.getSelectionModel.selectIndices(indices.head, indices.drop(1): _*)
        }
      case _ => // null or other type
    }
  }

  def getType = getListModel.getType
}
