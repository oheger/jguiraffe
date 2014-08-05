/**
 * Copyright 2006-2014 The JGUIraffe Team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.jguiraffe.gui.platform.javafx.builder.components.table

import javafx.scene.control.{TableColumn, TableView}
import javafx.util.Callback

import net.sf.jguiraffe.gui.builder.components.tags.table.TableColumnRecalibrator

/**
 * A specialized resize policy for the columns of a table view that supports
 * columns with a percent width.
 *
 * The main purpose of this policy implementation is to notify the
 * [[net.sf.jguiraffe.gui.builder.components.tags.table.TableColumnWidthController]]
 * whenever the size of a column is changed by the user. Then the controller
 * has to perform a recalibration to ensure that a resize operation of the
 * table yields correctly adapted column widths.
 *
 * @param recalibrator the ''TableColumnRecalibrator''
 */
private class TableColumnRecalibrationResizePolicy(val recalibrator: TableColumnRecalibrator)
  extends Callback[TableView.ResizeFeatures[_], java.lang.Boolean] {
  this: ColumnWidthExtractor =>

  import scala.collection.JavaConversions._

  override def call(resizeFeatures: TableView.ResizeFeatures[_]): java.lang.Boolean =
    if (resizeFeatures.getColumn == null) false
    else handleResize(resizeFeatures)

  /**
   * Performs a resize operation. This method is called when the ''ResizeFeatures''
   * object contains valid data.
   * @param resizeFeatures the ''ResizeFeatures'' object
   * @return a flag whether the operation was successful
   */
  private def handleResize(resizeFeatures: TableView.ResizeFeatures[_]): java.lang.Boolean = {
    val updater = new ColumnWidthUpdater(resizeFeatures)
    if (updater.prepareUpdate()) {
      resizeFeatures.getColumn setPrefWidth (columnWidth(resizeFeatures.getColumn) +
        resizeFeatures.getDelta)
      recalibrator recalibrate updater.performUpdate()
      true
    } else false
  }

  /**
   * An internally used helper class which updates the widths of the single columns
   * in the affected table.
   *
   * This class provides some methods for determining the columns affected by the
   * current change and performing the actual updates.
   * @param resizeFeatures the object with resize information
   */
  private class ColumnWidthUpdater(resizeFeatures: TableView.ResizeFeatures[_]) {
    /** The collection with the columns of the affected table. */
    private val tableColumns = resizeFeatures.getTable.getColumns

    /** A flag whether iterations have to be done in backwards direction. */
    private val backwards = determineDirection()

    /** The start index for an iteration. */
    private val startIndex = if (backwards) 0 else tableColumns.size - 1

    /** The increment value during an iteration. */
    private val increment = if (backwards) 1 else -1

    /** An array with the updated column widths. */
    private val changedWidths = new Array[Double](tableColumns.size)

    /** The remaining delta to be applied to columns. */
    private var remainingDelta = resizeFeatures.getDelta

    /** The number of columns affected by this change. */
    private var affectedColumns = 0

    /**
     * Prepares an update operation and checks whether this is possible. This
     * method tries to apply the delta to the possible columns in the table.
     * The return value indicates whether this was successful.
     * @return a flag whether this resize operation can be performed
     */
    def prepareUpdate(): Boolean = {
      var index = startIndex
      var found = false
      while (!found && remainingDelta != 0) {
        val column = columnAt(index)
        if (column == resizeFeatures.getColumn) found = true
        else {
          val colWidth = columnWidth(column)
          changedWidths(index) = math.min(column.getMaxWidth, math.max(column.getMinWidth,
            colWidth - remainingDelta))
          remainingDelta -= colWidth - changedWidths(index)
          affectedColumns += 1
          index += increment
        }
      }

      remainingDelta == 0
    }

    /**
     * Changes the column widths according to the values determined during the
     * preparation phase. An array with the new (rounded) column widths is
     * returned. This can be used to perform a recalibration.
     * @return an array with the new column widths
     */
    def performUpdate(): Array[Int] = {
      var index = startIndex
      while (affectedColumns > 0) {
        columnAt(index) setPrefWidth changedWidths(index)
        index += increment
        affectedColumns -= 1
      }

      roundedColumnWidths()
    }

    /**
     * Returns an array with the rounded widths of the table's columns.
     * @return an array with the current column widths (rounded)
     */
    private def roundedColumnWidths(): Array[Int] =
      (tableColumns map ((col: TableColumn[_, _]) => math.round(col.getWidth).toInt)).toArray

    /**
     * Helper method for obtaining a column with a specific index.
     * @param index the index
     * @return the column with this index
     */
    private def columnAt(index: Int): TableColumn[_, _] =
      tableColumns.get(index)

    /**
     * Determines the direction of this update operation. If the last column is
     * affected, iterations over the columns have to be done in reverse direction.
     * @return a flag whether reverse iterations are required
     */
    private def determineDirection(): Boolean =
      resizeFeatures.getColumn eq tableColumns.get(tableColumns.size - 1)
  }

}
