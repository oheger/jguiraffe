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

import javafx.scene.control.ListView
import net.sf.jguiraffe.gui.builder.components.tags.ListModelUtils
import net.sf.jguiraffe.gui.platform.javafx.builder.event.ChangeEventSource

/**
 * A specialized ''ComponentHandler'' implementation for JavaFX list view
 * components.
 *
 * This class manages a ''ListView'' control in single selection mode.
 * Its data is of type ''Object'', i.e. the object from the list model
 * whose index is selected in the ''ListView'' control. Change events are
 * supported and mapped to selection changes.
 *
 * @param listView the ''ListView'' control to be managed
 */
private class JavaFxListViewHandler(listView: ListView[Object])
  extends JavaFxComponentHandler[Object](listView) with ListModelSupport
  with ChangeEventSource {
  protected val displayList = listView.getItems

  override val observableValue = listView.getSelectionModel.selectedIndexProperty

  /**
   * @inheritdoc This implementation obtains the selected index from the
   * managed list box. Then the corresponding value object from the list
   * model is returned.
   */
  def getData: Object = ListModelUtils.getValue(getListModel,
    listView.getSelectionModel.getSelectedIndex)

  /**
   * @inheritdoc This implementation determines the index of the passed in
   * object. This index - if defined - is then set as selected index.
   */
  def setData(data: Object) {
    listView.getSelectionModel.clearSelection()

    if (data != null) {
      val idx = ListModelUtils.getIndex(getListModel, data)
      if (idx != ListModelUtils.IDX_UNDEFINED) {
        listView.getSelectionModel.select(idx)
      }
    }
  }

  /**
   * @inheritdoc This implementation returns the type from the list model.
   */
  def getType: Class[_] = getListModel.getType
}
