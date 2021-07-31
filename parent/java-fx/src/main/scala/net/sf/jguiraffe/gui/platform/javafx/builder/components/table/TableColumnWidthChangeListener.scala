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

import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.scene.control.TableColumn

/**
 * A specialized change listener for the column width property which triggers
 * a recalibration.
 *
 * Most column width change notifications are processed by the resize policy.
 * However, there are a few that are missed - mainly auto-fit operations.
 * Therefore, a specialized change listener is registered at a table's columns
 * that can notify the resize policy in such cases. Because each and every
 * change of a column's width triggers this listener the policy is itself
 * responsible for filtering out unnecessary notifications. For this reason,
 * this class can be pretty straight-forward.
 *
 * @param policy the resize policy to be notified
 * @param column the associated column
 */
private class TableColumnWidthChangeListener(val policy: TableColumnRecalibrationResizePolicy,
                                             val column: TableColumn[_,
                                               _]) extends ChangeListener[java.lang.Number] {
  /**
   * @inheritdoc
   * This implementation just triggers the policy to notify it about the width
   * change of the managed column.
   */
  override def changed(obsValue: ObservableValue[_ <: Number], oldValue: Number, newValue: Number) {
    policy.columnWidthChanged(column, oldValue.doubleValue, newValue.doubleValue)
  }
}
