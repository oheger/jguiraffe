/*
 * Copyright 2006-2017 The JGUIraffe Team.
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

import javafx.scene.control.TableCell

import net.sf.jguiraffe.gui.builder.components.tags.table.TableFormController

/**
 * A trait for table cell implementations that use a
 * [[net.sf.jguiraffe.gui.builder.components.tags.table.TableFormController]].
 *
 * This trait provides basic functionality for obtaining the current value of
 * the cell from the controller or updating it. It can be mixed into concrete
 * table cell implementations.
 *
 * @tparam S the type of elements contained in the current column
 * @tparam T the element type of the table view
 */
trait FormControllerCell[S, T] extends TableCell[S, T] {
  /** The form controller. */
  val formController: TableFormController

  /** The column index of this cell. */
  val columnIndex: Int

  /**
   * Reads the current value of this cell from the ''TableFormController''.
   * @return the current value of this cell
   */
  protected def readCellValue: AnyRef = {
    selectCurrentRow()
    formController.getColumnValue(columnIndex)
  }

  /**
   * Updates the value of this cell. The specified new value is passed to the
   * ''TableFormController''.
   * @param value the new value for this cell
   */
  protected def writeCellValue(value: AnyRef) {
    selectCurrentRow()
    formController.setColumnValue(getTableView, columnIndex, value)
  }

  /**
   * Selects the current row for the form controller. This method is always called
   * before data of this cell can be accessed.
   */
  private def selectCurrentRow() {
    formController selectCurrentRow getIndex
  }
}
