/*
 * Copyright 2006-2021 The JGUIraffe Team.
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
 * Unfortunately, this is not trivial. There are some updates of the widths of
 * columns which do not trigger the policy - mainly auto-fit operations when the
 * user double-clicks a column header and the column's width is automatically
 * adjusted to its content. To properly react on such changes, listeners have
 * to be attached to the width properties of all columns in the managed table
 * view. However, then some notifications received by these listeners have to be
 * ignored as they interfere with the resizing logic implemented here.
 *
 * @param recalibrator the ''TableColumnRecalibrator''
 */
private class TableColumnRecalibrationResizePolicy(val recalibrator: TableColumnRecalibrator)
  extends Callback[TableView.ResizeFeatures[_], java.lang.Boolean] {
  this: ColumnWidthExtractor with TableWidthExtractor =>
  /** The number of pending width change notifications. */
  private var pendingWidthChangeNotifications = 0

  /** The width of the managed table. */
  private var managedTableWidth = 0.0

  override def call(resizeFeatures: TableView.ResizeFeatures[_]): java.lang.Boolean = {
    managedTableWidth = tableWidth(resizeFeatures.getTable)
    if (resizeFeatures.getColumn == null) false
    else handleResize(resizeFeatures, updateAffectedColumn = true)
  }

  /**
   * Notifies this object that the width of a column has changed. This method is
   * intended to be used for auto-fit operations which are not handled per default
   * by the resize policy. However, it is also called in reaction of width changes
   * caused by the policy itself. In those cases, it has to be skipped.
   * @param column the affected column
   * @param oldWidth the old width of the column
   * @param newWidth the new width of the column
   */
  def columnWidthChanged(column: TableColumn[_, _], oldWidth: Double, newWidth: Double): Unit = {
    if (shouldProcessColumnChange(column)) {
      handleResize(createResizeFeatures(column, oldWidth, newWidth),
        updateAffectedColumn = false)
    }
  }

  /**
   * Creates a specialized change listener for updates of a column's width
   * property and registers it at the specified column.
   * @param column the column to be observed
   * @return the change listener
   */
  def installWidthChangeListener(column: TableColumn[_, _]): TableColumnWidthChangeListener = {
    val listener = new TableColumnWidthChangeListener(this, column)
    widthProperty(column) addListener listener
    listener
  }

  /**
   * Returns the current value of the internally stored width of the managed
   * table. This field is updated when the policy is invoked.
   * @return the current width of the managed table
   */
  def currentTableWidth: Double = managedTableWidth

  /**
   * Performs a resize operation. This method is called when the ''ResizeFeatures''
   * object contains valid data.
   * @param resizeFeatures the ''ResizeFeatures'' object
   * @param updateAffectedColumn a flag whether the target column is to be updated itself; in
   *                             some scenarios when this method is called,
   *                             the column width has already been changed; then no update must
   *                             be performed
   * @return a flag whether the operation was successful
   */
  private def handleResize(resizeFeatures: TableView.ResizeFeatures[_],
                           updateAffectedColumn: Boolean): java.lang.Boolean = {
    val updater = new ColumnWidthUpdater(resizeFeatures)
    if (updater.prepareUpdate()) {
      pendingWidthChangeNotifications = updater.changedColumns
      if (updateAffectedColumn) {
        pendingWidthChangeNotifications += 1
        resizeFeatures.getColumn setPrefWidth (columnWidth(resizeFeatures.getColumn) +
          resizeFeatures.getDelta)
      }
      recalibrator recalibrate updater.performUpdate()
      true
    } else false
  }

  /**
   * Checks whether a column width change notification should be processed.
   * There are some criteria which have to be taken into account:
   * $ - If columns have been resized by this policy, corresponding change
   * notifications are expected; they have to be ignored
   * $ - Column change notifications are received when the managed table's width
   * is changing. In this case, they have to be ignored because they are handled
   * by the change listener for the table width.
   * $ - When the table is constructed initial change notifications are sent
   * which also have to be ignored.
   * @param column the column affected by the change
   * @return a flag whether a column change notification is to be processed
   */
  private def shouldProcessColumnChange(column: TableColumn[_, _]): Boolean = {
    if (pendingWidthChangeNotifications > 0) {
      pendingWidthChangeNotifications -= 1
      false
    } else {
      currentTableWidth == tableWidth(column.getTableView) && currentTableWidth != 0
    }
  }

  /**
   * Creates a ''ResizeFeatures'' object from the given parameters.
   * @param column the affected column
   * @param oldWidth the old column width
   * @param newWidth the new column width
   * @tparam S the type of the column
   * @return the ''ResizeFeatures''
   */
  private def createResizeFeatures[S](column: TableColumn[S, _], oldWidth: Double,
                                      newWidth: Double): TableView.ResizeFeatures[S] =
    new TableView.ResizeFeatures[S](column.getTableView, column, newWidth - oldWidth)

  /**
   * An internally used helper class which updates the widths of the single columns
   * in the affected table.
   *
   * This class provides some methods for determining the columns affected by the
   * current change and performing the actual updates.
   * @param resizeFeatures the object with resize information
   */
  private class ColumnWidthUpdater(resizeFeatures: TableView.ResizeFeatures[_]) {
    /** Contains the number of columns that have been changed. */
    var changedColumns = 0

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
          if (changedWidths(index) != colWidth) {
            remainingDelta -= colWidth - changedWidths(index)
            changedColumns += 1
          }
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
    private def roundedColumnWidths(): Array[Int] = {
      import scala.jdk.CollectionConverters._
      (tableColumns.asScala map ((col: TableColumn[_, _]) => math.round(col.getWidth).toInt)).toArray
    }

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
