/*
 * Copyright 2006-2022 The JGUIraffe Team.
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

import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.scene.control.TableView

import net.sf.jguiraffe.gui.builder.components.tags.table.TableColumnWidthCalculator

/**
 * Companion object for ''TableViewWidthChangeListener''.
 */
object TableViewWidthChangeListener {
  /**
   * Constant defining the border width of a table view. This value has to be
   * subtracted when calculating the new column widths; otherwise a
   * horizontal scrollbar is displayed. The value has been determined
   * empirically.
   */
  val BorderWidth = 2
}

/**
 * A specialized change listener implementation for reacting on changes of the
 * width of a table view component.
 *
 * This class is part of the mechanism which controls the widths of the
 * columns in a table. It is associated with a
 * [[net.sf.jguiraffe.gui.builder.components.tags.table.TableColumnWidthController]].
 * When ever the table's width is changed the width controller is asked to
 * calculate the widths of all columns according to their properties. These new
 * width values are then applied to the table's columns.
 *
 * @param calculator the object for calculating the widths of table columns
 * @param table the table to be monitored
 */
private class TableViewWidthChangeListener(val calculator: TableColumnWidthCalculator,
                                           val table: TableView[_])
  extends ChangeListener[java.lang.Number] {
  import TableViewWidthChangeListener._

  override def changed(obsValue: ObservableValue[_ <: Number], oldValue: Number,
                       newValue: Number): Unit = {
    val widths = calculator calculateWidths (newValue.intValue - BorderWidth)
    for (i <- 0 until widths.size) {
      table.getColumns.get(i) setPrefWidth widths(i)
    }
  }
}
