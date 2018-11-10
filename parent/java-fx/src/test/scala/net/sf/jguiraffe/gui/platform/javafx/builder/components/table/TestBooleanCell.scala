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

import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.{TableColumn, TableView}

import net.sf.jguiraffe.gui.builder.components.tags.table.TableFormController
import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import org.easymock.EasyMock
import org.junit.Assert._
import org.junit.{BeforeClass, Before, Test}
import org.scalatest.junit.JUnitSuite
import org.scalatest.easymock.EasyMockSugar

object TestBooleanCell {
  @BeforeClass def setUpOnce(): Unit = {
    JavaFxTestHelper.initPlatform()
  }
}

/**
 * Test class for ''BooleanCell''.
 */
class TestBooleanCell extends JUnitSuite with EasyMockSugar {
  /** Constant for the column index. */
  private val ColumnIndex = 2

  /** Constant for the index of the cell. */
  private val CellIndex = 8

  /** The table view associated with the test cell. */
  private var table: TableView[AnyRef] = _

  /** A mock table form controller. */
  private var controller: TableFormController = _

  /** The cell to be tested. */
  private var cell: BooleanCell[Boolean, AnyRef] = _

  @Before def setUp() {
    controller = mock[TableFormController]
    table = new TableView
    cell = new BooleanCell(controller, ColumnIndex)
    cell updateIndex CellIndex
    cell updateTableView table
    cell updateTableColumn new TableColumn[AnyRef, AnyRef]
  }

  private def cellValue: SimpleBooleanProperty = {
    val callbackValue = cell.getSelectedStateCallback call CellIndex
    callbackValue match {
      case prop: SimpleBooleanProperty =>
        prop
      case other =>
        fail("Unexpected property: " + other)
    }
  }

  /**
   * Prepares the controller mock for a get value query.
   * @param result the result to be returned
   */
  private def expectGetValue(result: AnyRef) {
    controller selectCurrentRow CellIndex
    EasyMock.expect(controller.getColumnValue(ColumnIndex)).andReturn(result)
  }

  /**
   * Tests whether the cell's value can be externally updated.
   */
  @Test def testUpdateItem() {
    expectGetValue(java.lang.Boolean.TRUE)
    whenExecuting(controller) {
      cell.updateItem(this, empty = false)
      assertTrue("Wrong value", cellValue.getValue)
    }
  }

  /**
   * Tests updateItem() if an empty value is passed in.
   */
  @Test def testUpdateItemEmpty() {
    whenExecuting(controller) {
      cell.updateItem(this, empty = true)
      assertFalse("Wrong value", cellValue.getValue)
    }
  }

  /**
   * Tests whether non-boolean values returned from the controller are handled
   * correctly.
   */
  @Test def testUpdateItemInvalidValue() {
    expectGetValue(this)
    whenExecuting(controller) {
      cell.updateItem(this, empty = false)
      assertFalse("Wrong value", cellValue.getValue)
    }
  }

  /**
   * Tests whether an edit operation on the cell is passed to the model.
   */
  @Test def testEditCell() {
    controller.selectCurrentRow(CellIndex)
    controller.setColumnValue(table, ColumnIndex, java.lang.Boolean.TRUE)
    whenExecuting(controller) {
      cellValue set java.lang.Boolean.TRUE
    }
  }
}
