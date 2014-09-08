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
 * be a container consisting of multiple components. For each cell instance
 * created for the table a separate UI component has to be created. The
 * [[net.sf.jguiraffe.gui.platform.javafx.builder.components.table.CellComponentManager]]
 * passed to the constructor takes care of this. Therefore, this cell
 * implementation can be pretty straight-forward. It just has to register itself
 * at the ''CellComponentManager'' and install the provided
 * renderer component as graphic for the cell. Whenever the cell value changes
 * the ''updateItem()'' method is called. Here the current row of the
 * table has to be selected, and the cell has to set itself as the current,
 * active cell at the ''CellComponentManager''; this causes the fields of the
 * renderer form to be connected to the cell-specific form. Thus, the graphical
 * renderer components are updated.
 *
 * @param formController the ''TableFormController''
 * @param cellComponentManager the ''CellComponentManager''
 */
private class RenderCell(val formController: TableFormController,
                         val cellComponentManager: CellComponentManager)
  extends TableCell[AnyRef, AnyRef] {
  /** The visual representation for this cell. */
  private val uiNode = initCellUI()

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
      cellComponentManager selectCell this
      setGraphic(uiNode)
    } else {
      setGraphic(null)
    }
  }

  /**
   * Initializes the UI of this cell. Obtains the renderer component
   * which is obtained from the ''CellComponentManager''.
   * @return the UI component to be used for this cell
   */
  private def initCellUI(): Node = {
    setContentDisplay(ContentDisplay.GRAPHIC_ONLY)
    cellComponentManager registerCell this
  }
}
