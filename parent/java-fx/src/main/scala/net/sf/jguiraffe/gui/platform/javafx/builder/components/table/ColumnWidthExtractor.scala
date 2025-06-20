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
package net.sf.jguiraffe.gui.platform.javafx.builder.components.table

import javafx.beans.value.ObservableValue
import javafx.scene.control.TableColumn

/**
 * A trait for extracting the width of a table column.
 *
 * This trait is mainly used for testing purposes. Because the ''width'' property
 * of a table column is final it is pretty hard to mock it. Therefore, objects
 * having to access column widths can make use of this trait. Specific widths
 * for testing purposes can then be injected by using specific test
 * implementations.
 */
private trait ColumnWidthExtractor {
  /**
   * Obtains the property with the width from the specified column.
   * @param column the column
   * @return the property storing the column's width
   */
  def widthProperty(column: TableColumn[_, _]): ObservableValue[Number] = column.widthProperty

  /**
   * Extracts the width of the specified column. This implementation returns
   * the value of the property returned by ''widthProperty()''.
   * @param column the column
   * @return the width of this column
   */
  def columnWidth(column: TableColumn[_, _]): Double = widthProperty(column).getValue.doubleValue
}
