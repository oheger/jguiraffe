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

import javafx.scene.Node
import javafx.scene.control.{ContentDisplay, TableCell}

import net.sf.jguiraffe.gui.builder.components.tags.table.TableFormController

/**
 * A specialized ''TableCell'' implementation that renders a complete form.
 *
 * It is possible to define a renderer component for a table column. This can
 * be a container consisting of multiple components. The component has been
 * set up during the builder operation, and and fields it contains have been
 * connected to the row render form of the table. Therefore, this table
 * implementation can be pretty straight-forward. It just has to install the
 * renderer component as graphic for the cell. Whenever the cell value changes
 * the ''updateItem()'' method is called. Here just the current row of the
 * table has to be selected; this causes the fields of the renderer form to
 * be initialized and updates the graphical renderer components directly.
 *
 * @param formController the ''TableFormController''
 * @param renderComponent the component to be used as cell renderer
 */
private class RenderCell(val formController: TableFormController, val renderComponent: Node)
  extends TableCell[AnyRef, AnyRef] {
  initCellUI()

  /**
   * @inheritdoc
   * Notifies this cell that its content has changed. This implementation just selects
   * the current row in the form controller. This causes the renderer form to be
   * initialized which in turn transfers the current values into the UI components.
   */
  override def updateItem(item: AnyRef, empty: Boolean) {
    super.updateItem(item, empty)
    if (!empty) {
      formController selectCurrentRow getIndex
    }
  }

  /**
   * Initializes the UI of this cell. Installs the renderer component.
   */
  private def initCellUI() {
    setGraphic(renderComponent)
    setContentDisplay(ContentDisplay.GRAPHIC_ONLY)
  }
}
