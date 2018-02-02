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

import javafx.beans.value.ObservableValue
import javafx.scene.control.TableView

/**
 * A trait for extracting the width of a table view.
 *
 * This trait is analogous to
 * [[net.sf.jguiraffe.gui.platform.javafx.builder.components.table.ColumnWidthExtractor]].
 * It provides functionality for accessing the width property of a table and
 * reading its value. This is mainly useful for unit tests because there is no
 * easy way to manipulate or mock the width of a table.
 */
trait TableWidthExtractor {
  /**
   * Determines the width property of the specified table.
   * @param table the table
   * @return the width property of this table
   */
  def tableWidthProperty(table: TableView[_]): ObservableValue[Number] = table.widthProperty

  /**
   * Determines the width of the specified table.
   * @param table the table
   * @return the width of this table
   */
  def tableWidth(table: TableView[_]): Double = tableWidthProperty(table).getValue.doubleValue
}
