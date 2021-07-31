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

import javafx.beans.binding.Bindings
import javafx.beans.property.{SimpleStringProperty, StringProperty}
import javafx.scene.control.{TableRow, TableView}
import javafx.util.Callback

import org.apache.commons.lang.StringUtils

/**
 * This is a specialized row factory for table views that handles specific styles to be
 * applied on selected rows.
 *
 * ''JGUIraffe'' supports setting the foreground and background colors for selected
 * rows in a table view explicitly. This is surprisingly difficult to realize with
 * JavaFX because the default way here is to use (static) CSS. In order to apply
 * colors programmatically, a component's ''style'' property has to be changed
 * dynamically. This implementation does exactly this for a table row which is in fact
 * a ''Cell'' instance.
 *
 * What this class does is to create a binding between a table row's ''selected''
 * property and a string property allowing the definition of specific styles. If
 * the row is selected, the style is set to the content of this text property;
 * otherwise, it is set to an empty string. The text property for the styles
 * definition is passed to the
 * [[net.sf.jguiraffe.gui.platform.javafx.builder.components.table.JavaFxTableHandler]]
 * object. Here corresponding styles are added when selection colors are modified.
 */
private class StyleAwareRowFactory[S] extends Callback[TableView[S], TableRow[S]] {
  this: CellSelectionExtractor =>
  /** The property for setting the style definition. */
  val styleProperty: StringProperty = new SimpleStringProperty(StringUtils.EMPTY)

  /** The property containing an empty style definition. */
  private val emptyStyle = new SimpleStringProperty(StringUtils.EMPTY)

  override def call(tableView: TableView[S]): TableRow[S] =
    bindStyleProperty(new TableRow[S])

  /**
   * Binds the style property of the given row to a string property based on the
   * current value of its selected property.
   * @param row the row to be manipulated
   * @return the same row
   */
  private def bindStyleProperty(row: TableRow[S]): TableRow[S] = {
    row.styleProperty() bind Bindings.when(selectedProperty(row))
      .then(styleProperty)
      .otherwise(emptyStyle)
    row
  }
}
