/*
 * Copyright 2006-2016 The JGUIraffe Team.
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

import javafx.scene.control.{TableColumn, TableCell}
import net.sf.jguiraffe.gui.builder.components.tags.table.TableFormController
import org.apache.commons.lang.StringUtils
import javafx.scene.input.{KeyCode, KeyEvent}
import scala.collection.mutable.ListBuffer
import net.sf.jguiraffe.gui.platform.javafx.builder.components.cell.EditableCell

/**
 * A specialized table cell implementation with extended edit capabilities.
 *
 * An instance is passed a
 * [[net.sf.jguiraffe.gui.builder.components.tags.table.TableFormController]]
 * and the index of the column. The controller is used to obtain the current value of the
 * cell and to store changed data - basically, it handles communication with the ''Form''
 * instance representing the current row. Note that the concrete type of objects stored
 * in the table does not matter because all properties are accessed via the form, i.e.
 * using reflection.
 *
 * @author Oliver Heger
 * @param formController the form controller
 * @param columnIndex the column index
 */
private class EditableTableCell(override val formController: TableFormController,
                                override val columnIndex: Int)
  extends TableCell[AnyRef, AnyRef]
  with EditableCell[AnyRef] with FormControllerCell[AnyRef, AnyRef] {
  /**
   * @inheritdoc
   * This implementation combines the default edit key handler with another
   * partial function which also takes the TAB key into account. A tab or
   * shift+tab navigates to the next or previous editable cell in the current
   * row.
   */
  override protected def editKeyHandler: KeyHandler =
    tableCellEditKeyHandler orElse super.editKeyHandler

  /**
   * @inheritdoc
   * This implementation returns the value of the cell obtained from
   * the ''TableFormController''.
   */
  override protected def stringRepresentation(): String = {
    val propValue = readCellValue
    if (propValue == null) StringUtils.EMPTY else propValue.toString
  }

  /**
   * @inheritdoc
   * This implementation passes the new text content of the cell to the
   * ''TableFormController''.
   */
  override protected def commitData(text: String, focusLost: Boolean) {
    commitEdit(getItem)
    writeCellValue(text)
  }

  private def tableCellEditKeyHandler: KeyHandler = {
    case ev: KeyEvent if ev.getCode == KeyCode.TAB =>
      performCommit(focusLost = false)
      nextEditableColumn(!ev.isShiftDown) foreach (getTableView.edit(getTableRow.getIndex, _))
  }

  /**
   * Searches for the next editable column in the current row (either in forward
   * or backward direction). This method is called in reaction on the TAB key
   * while the user edits a cell.
   * @param forward flag for the search direction
   * @return an ''Option'' for the next editable column
   */
  private def nextEditableColumn(forward: Boolean): Option[TableColumn[AnyRef, _]] = {
    import scala.collection.JavaConversions._
    val columns = ListBuffer.empty[TableColumn[AnyRef, _]]
    getTableView.getColumns foreach (columns ++= leafColumns(_))
    //There is no other column that supports editing.
    if (columns.size < 2) {
      return None
    }
    val currentIndex = columns.indexOf(getTableColumn)
    var nextIndex = currentIndex
    if (forward) {
      nextIndex += 1
      if (nextIndex > columns.size - 1) {
        nextIndex = 0
      }
    } else {
      nextIndex -= 1
      if (nextIndex < 0) {
        nextIndex = columns.size - 1
      }
    }
    Some(columns(nextIndex))
  }

  /**
   * Helper method for obtaining all columns on the lowest level. Columns can be
   * nested. This method returns a collection with all columns that do not
   * contain any sub columns.
   * @param root the current starting point of the search for columns
   * @return a collection with all leaf columns
   */
  private def leafColumns(root: TableColumn[AnyRef, _]): Seq[TableColumn[AnyRef, _]] = {
    val columns = ListBuffer.empty[TableColumn[AnyRef, _]]
    if (root.getColumns.isEmpty) {
      //We only want the leaves that are editable.
      if (root.isEditable) {
        columns += root
      }
    } else {
      import scala.collection.JavaConversions._
      root.getColumns foreach (columns ++= leafColumns(_))
    }
    columns
  }
}
