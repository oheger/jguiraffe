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

import javafx.beans.value.ChangeListener
import javafx.scene.control.TableView

import net.sf.jguiraffe.gui.builder.components.tags.table.{TableColumnRecalibrator, TableColumnWidthCalculator}

/**
 * A class for creating several components required by the implementation of
 * JavaFX tables.
 *
 * In order to create a table view and expose it via the ''JGUIraffe'' API, some
 * helper components have to be created and correctly plugged together. This
 * class defines corresponding methods for creating the single helper components.
 * It also simplifies testing whether a table handler has been created correctly.
 */
private class TableComponentFactory extends TableWidthExtractor {
  /**
   * The ''ColumnFactory'' for creating the single columns of the table.
   */
  val columnFactory = new ColumnFactory

  /**
   * Creates a row factory for a table that can be used to apply custom styles
   * for selection background and foreground colors.
   * @return the new row factory
   */
  def createRowFactory(): StyleAwareRowFactory[AnyRef] =
    new StyleAwareRowFactory[AnyRef] with CellSelectionExtractor

  /**
   * Creates a listener for reacting on changes of the table's width. This listener
   * is responsible for recalculating the widths of the table's columns whenever
   * it was resized.
   * @param calculator the ''TableColumnWidthCalculator''
   * @param table the table view
   * @return the table width change listener
   */
  def createTableWidthListener(calculator: TableColumnWidthCalculator,
                               table: TableView[AnyRef]): ChangeListener[java.lang.Number] =
    new TableViewWidthChangeListener(calculator, table)

  /**
   * Creates a special policy for resize operations on table columns. The policy
   * ensures that a recalibration is performed when the width of a column is changed.
   * @param recalibrator the ''TableColumnRecalibrator''
   * @return the resize policy
   */
  def createColumnResizePolicy(recalibrator: TableColumnRecalibrator):
  TableColumnRecalibrationResizePolicy =
    new TableColumnRecalibrationResizePolicy(recalibrator) with ColumnWidthExtractor with
      TableWidthExtractor
}
