/*
 * Copyright 2006-2018 The JGUIraffe Team.
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

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.{TableCell, TableColumn}

import net.sf.jguiraffe.gui.builder.components.tags.table.{ColumnClass, TableFormController}
import net.sf.jguiraffe.gui.platform.javafx.common.FunctionCallback

/**
 * A factory class for creating the columns of a table view defined by a
 * [[net.sf.jguiraffe.gui.builder.components.tags.table.TableFormController]].
 *
 * The purpose of this class is to create fully initialized table columns.
 * The initialization of a column depends on multiple factors:
 * * whether a renderer component is defined for the column
 * * the logic data type of the column (if any)
 * * whether the column can be edited
 *
 * All this information for a given column is available from a
 * ''TableFormController'' object. Therefore, this class defines a method which
 * expects such a controller and the index of the column in question and returns
 * the corresponding ''TableColumn''.
 */
private class ColumnFactory {
  def createColumn(controller: TableFormController, columnIndex: Int): TableColumn[AnyRef,
    AnyRef] = {
    if (controller.hasRenderer(columnIndex)) {
      createColumnWithCallbacks(controller, columnIndex, editable = false) { f =>
        new RenderCell(controller, obtainCellComponentManager(controller, columnIndex))
      }
    } else {
      controller installTransformersForColumnType columnIndex
      val cellCallback: TableColumn[AnyRef, AnyRef] => TableCell[AnyRef, AnyRef] =
        controller.getLogicDataClass(columnIndex) match {
          case ColumnClass.BOOLEAN =>
            f => new BooleanCell[AnyRef, AnyRef](controller, columnIndex)
          case ColumnClass.ICON =>
            f => new IconCell[AnyRef](controller, columnIndex)
          case _ =>
            f => new EditableTableCell(controller, columnIndex)
        }
      createColumnWithCallbacks(controller, columnIndex,
        editable = controller.isColumnEditable(columnIndex))(cellCallback)
    }
  }

  /**
   * Creates a column with basic properties and installs callbacks for the
   * column value and the cell.
   * @param controller the ''TableFormController''
   * @param columnIndex the index of the column
   * @param editable the editable flag for this column
   * @param cellCallback the function for creating the cell
   * @return the newly created column
   */
  private def createColumnWithCallbacks(controller: TableFormController,
                                        columnIndex: Int, editable: Boolean)
                                       (cellCallback: TableColumn[AnyRef,
                                         AnyRef] => TableCell[AnyRef,
                                         AnyRef]): TableColumn[AnyRef, AnyRef] = {
    val column = new TableColumn[AnyRef, AnyRef](controller.getColumnName(columnIndex))
    column setCellValueFactory FunctionCallback(f => new SimpleObjectProperty(f.getValue))
    column setCellFactory FunctionCallback(cellCallback)
    column setEditable editable
    column
  }

  /**
   * Obtains the ''CellComponentManager'' required for a ''RenderCell'' from the
   * given ''TableFormController''. The component manager is stored as renderer
   * component for the given column. A corresponding type cast has to be performed.
   * @param controller the ''TableFormController''
   * @param columnIndex the column index
   * @return the ''CellComponentManager''
   */
  private def obtainCellComponentManager(controller: TableFormController,
                                         columnIndex: Int): CellComponentManager = {
    controller.getColumnRenderer(columnIndex).asInstanceOf[CellComponentManager]
  }
}
