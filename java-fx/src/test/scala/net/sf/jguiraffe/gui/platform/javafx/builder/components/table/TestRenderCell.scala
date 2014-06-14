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
import javafx.scene.control.{ContentDisplay, Label, TableColumn, TableView}

import net.sf.jguiraffe.gui.builder.components.tags.table.TableFormController
import org.junit.Assert._
import org.junit.{Before, Test}
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.EasyMockSugar

/**
 * Test class for ''RenderCell''.
 */
class TestRenderCell extends JUnitSuite with EasyMockSugar {
  /** Constant for the row index of the test cell. */
  private val RowIndex = 4

  /** The mock for the form controller. */
  private var formController: TableFormController = _

  /** The renderer component. */
  private var rendererComponent: Node = _

  /** The cell to be tested. */
  private var cell: RenderCell = _

  @Before def setUp() {
    formController = mock[TableFormController]
    rendererComponent = new Label("Renderer")
    cell = new RenderCell(formController, rendererComponent)
    cell updateIndex RowIndex
    cell updateTableColumn new TableColumn[AnyRef, AnyRef]
    cell updateTableView new TableView[AnyRef]
  }

  /**
   * Tests whether the correct render component has been installed.
   */
  @Test def testRenderComponent() {
    assertEquals("Wrong content display", ContentDisplay.GRAPHIC_ONLY, cell.getContentDisplay)
    assertSame("Wrong graphic", rendererComponent, cell.getGraphic)
  }

  /**
   * Tests updateItem() for an empty cell.
   */
  @Test def testUpdateItemEmpty() {
    whenExecuting(formController) {
      cell.updateItem(this, empty = true)
      assertSame("Item not updated", this, cell.getItem)
    }
  }

  /**
   * Tests updateItem() if a new value is passed in.
   */
  @Test def testUpdateItemNewValue() {
    formController selectCurrentRow RowIndex
    whenExecuting(formController) {
      cell.updateItem(this, empty = false)
      assertSame("Item not updated", this, cell.getItem)
    }
  }
}
