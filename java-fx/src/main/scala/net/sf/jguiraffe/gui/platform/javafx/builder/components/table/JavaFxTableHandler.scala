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
package net.sf.jguiraffe.gui.platform.javafx.builder.components.table

import javafx.scene.control.{SelectionMode, TableView}

import net.sf.jguiraffe.gui.builder.components.Color
import net.sf.jguiraffe.gui.builder.components.model.TableHandler
import net.sf.jguiraffe.gui.platform.javafx.builder.components.JavaFxComponentHandler

import scala.beans.BeanProperty

/**
 * A specialized ''ComponentHandler'' implementation for JavaFX table view
 * components.
 *
 * This handler implements the typical functionality based on a table component.
 * The handler's data is the index of the selected row (in case of single
 * selection) or an array of indices of selected rows (in case of multiple
 * selection support).
 *
 * In addition, the methods defined by the ''TableHandler'' interface are
 * implemented. Among others, this means that a list serving as table model has
 * to be mapped to the items collection used by the underlying table view
 * component. Because these are actually different collections updates on the
 * model collection are not per se visible in the table view. It is mandatory to
 * call the corresponding update methods offered by the interface. These are
 * implemented by copying data objects from the model collection to the
 * table's item collection. (Therefore, it is crucial that correct indices are
 * provided so that the correct objects are updated, inserted, or deleted).
 *
 * @param table the manged table view component
 * @param name the component name
 * @param model the list serving as table model
 */
private class JavaFxTableHandler(table: TableView[AnyRef], val name: String,
                                 @BeanProperty val model: java.util.List[AnyRef])
  extends JavaFxComponentHandler[Object](table) with TableHandler {
  /** A flag whether the table supports multiple selection. */
  private val multipleSelection =
    table.getSelectionModel.getSelectionMode == SelectionMode.MULTIPLE

  /** The type of this handler. It depends in the table's selection mode. */
  @BeanProperty val `type` = if (multipleSelection) classOf[Array[Int]]
  else Integer.TYPE

  override def getSelectedIndex: Int = table.getSelectionModel.getSelectedIndex

  override def clearSelection(): Unit = {
    table.getSelectionModel.clearSelection()
  }

  override def getSelectedIndices: Array[Int] = {
    val indices = table.getSelectionModel.getSelectedIndices
    indices.toArray(new Array[Integer](indices.size)) map (_.intValue)
  }

  override def setSelectedIndices(rowIndices: Array[Int]): Unit = {
    clearSelection()
    rowIndices foreach table.getSelectionModel.select
  }

  override def setSelectedIndex(rowIdx: Int): Unit = {
    clearSelection()
    table.getSelectionModel select rowIdx
  }

  /**
   * @inheritdoc
   * The return value of this method depends on the current selection mode:
   * If multiple selections are supported, result is an array of ints for the
   * selected indices. Otherwise, a single Integer is returned.
   */
  override def getData: AnyRef =
    if (multipleSelection) getSelectedIndices
    else Integer.valueOf(table.getSelectionModel.getSelectedIndex)

  /**
   * @inheritdoc
   * This method accepts the following input:
   * $ - A ''java.lang.Integer'' object is interpreted as single index to be selected
   * $ - An array of int is interpreted as the indices to be selected
   * All other input causes the current selection to be cleared.
   */
  override def setData(data: Object): Unit = {
    data match {
      case idx: Integer =>
        setSelectedIndex(idx.intValue)
      case indices: Array[Int] =>
        setSelectedIndices(indices)
      case _ =>
        clearSelection()
    }
  }

  /**
   * @inheritdoc
   * This implementation adds the specified range of the model collection to
   * the items list of the table view.
   */
  override def rowsInserted(startIdx: Int, endIdx: Int): Unit = {
    table.getItems.addAll(startIdx, model.subList(startIdx, endIdx + 1))
  }

  /**
   * @inheritdoc
   * This implementation replaces the elements in the given range from the
   * table's item collection with the corresponding entries of the model
   * collection.
   */
  override def rowsUpdated(startIdx: Int, endIdx: Int): Unit = {
    val items = table.getItems
    for (i <- startIdx to endIdx) {
      items.set(i, model get i)
    }
  }

  /**
   * @inheritdoc
   * This implementation removes the specified range from the table's items
   * collection.
   */
  override def rowsDeleted(startIdx: Int, endIdx: Int): Unit = {
    val items = table.getItems
    for (i <- startIdx to endIdx) {
      items remove startIdx
    }
  }

  /**
   * @inheritdoc
   * This implementation makes the table's item collection an exact copy
   * of the model collection.
   */
  override def tableDataChanged(): Unit = {
    table.getItems.clear()
    table.getItems addAll(0, model)
  }

  override def getSelectionBackground: Color = {
    null
  }

  override def setSelectionBackground(c: Color): Unit = {
    null
  }

  override def setSelectionForeground(c: Color): Unit = {
    null
  }

  override def getSelectionForeground: Color = {
    null
  }
}
