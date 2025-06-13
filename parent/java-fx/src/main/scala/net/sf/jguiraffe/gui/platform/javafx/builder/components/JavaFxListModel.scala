/*
 * Copyright 2006-2025 The JGUIraffe Team.
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
import scala.collection.mutable.ArrayBuffer

import javafx.collections.ObservableList
import net.sf.jguiraffe.gui.builder.components.model.ListModel

/**
 * An internally used implementation of the ''ListModel'' interface.
 *
 * This class manages two collection with data for lists or combo boxes. One
 * collection contains display objects and is directly backed by the
 * associated list control. The other collection contains corresponding
 * value objects used by the application. It is possible to add or remove
 * entries during runtime.
 *
 * @param displayList the collection with display objects
 * @param type the data type of the model items
 */
private class JavaFxListModel(val displayList: ObservableList[Object],
  @BeanProperty val `type`: Class[_])
  extends ListModel {
  /** A collection for storing value objects. */
  private val values = ArrayBuffer.empty[Object]

  /**
   * Initializes this model from the given ''ListModel''. The data of the
   * specified model is copied into this model.
   * @param model the ''ListModel'' to be used for initialization
   */
  def initFromModel(model: ListModel) {
    for (i <- 0 until model.size) {
      displayList add model.getDisplayObject(i)
      values += model.getValueObject(i)
    }
  }

  /**
   * Adds a new element to this model at the specified position.
   * @param index the position where to add this element
   * @param display the display object
   * @param value the value object
   */
  def insertItem(index: Int, display: Object, value: Object) {
    values.insert(index, value)
    displayList.add(index, display)
  }

  /**
   * Removes the item at the specified index.
   * @param index the index of the item to be removed
   */
  def removeItem(index: Int) {
    values.remove(index)
    displayList.remove(index)
  }

  def getDisplayObject(index: Int): Object = displayList.get(index)

  def getValueObject(index: Int): Object = values(index)

  def size: Int = displayList.size
}
