/*
 * Copyright 2006-2014 The JGUIraffe Team.
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

import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.{SelectionMode, TableView}

import net.sf.jguiraffe.gui.builder.components.Color
import org.junit.Assert._
import org.junit.Test
import org.scalatest.junit.JUnitSuite

import scala.beans.BeanProperty

object TestJavaFxTableHandler {
  /** Constant for the component name. */
  private val Name = "TestTableHandler"

  /** Constant for the number of rows in the test data. */
  private val Rows = 16

  /** Constant for a style definition for the foreground color. */
  private val StyleForegroundColor = "-fx-text-fill: #FF80C0;"

  /** Constant for a style definition for the background color. */
  private val StyleBackgroundColor = "-fx-background-color: #AA99FF;"

  /** Constant for a test foreground color. */
  private val ForegroundColor = Color.newLogicInstance("#FF80C0")

  /** Constant for a test background color. */
  private val BackgroundColor = Color.newLogicInstance("#AA99FF")

  /**
   * Creates the table view control and sets the correct selection mode.
   * @param multiSelect flag for multiple selection support
   * @return the table view component
   */
  private def createTable(multiSelect: Boolean = false): TableView[AnyRef] = {
    val table = new TableView[AnyRef]
    if (multiSelect) {
      table.getSelectionModel setSelectionMode SelectionMode.MULTIPLE
    }
    table
  }

  /**
   * Creates the data collection acting as table model. The model contains
   * ''TableData'' objects with sequential indices.
   * @return the list with the data of the table
   */
  private def createModel(): java.util.List[AnyRef] = {
    val data = new java.util.ArrayList[AnyRef](Rows)
    (1 to Rows) foreach (i => data add new TableData(i))
    data
  }

  /**
   * Obtains the table view from the passed in handler.
   * @param handler the handler
   * @return the managed table view
   */
  private def tableFrom(handler: JavaFxTableHandler): TableView[_] =
    handler.getComponent.asInstanceOf[TableView[_]]

  /**
   * Creates a test handler instance.
   * @param multiSelect a flag whether multiple selection is supported
   * @param model the list to be used as table model
   * @return the test handler
   */
  private def createHandler(multiSelect: Boolean = false, model: java.util.List[AnyRef] =
  createModel()): JavaFxTableHandler =
    new JavaFxTableHandler(createTable(multiSelect), Name, model, new SimpleStringProperty)

  /**
   * Creates a test handler and initializes its table model.
   * @param multiSelect a flag whether multiple selection is supported
   * @param model the list to be used as table model
   * @return the initialized test handler
   */
  private def createInitializedHandler(multiSelect: Boolean = false,
                                       model: java.util.List[AnyRef] = createModel()):
  JavaFxTableHandler = {
    val handler = createHandler(multiSelect, model)
    handler tableDataChanged()
    handler
  }

  /**
   * Checks whether the expected indices are selected in the table.
   * @param handler the handler to be checked
   * @param expected the expected selected indices
   */
  private def checkSelectedIndices(handler: JavaFxTableHandler, expected: Array[Int]): Unit = {
    val selIndices = tableFrom(handler).getSelectionModel.getSelectedIndices
    assertEquals("Wrong number of selected indices", expected.size, selIndices.size)
    val indicesCollection = java.util.Arrays.asList(expected map Integer.valueOf: _*)
    assertTrue(s"Wrong selected indices: $selIndices", selIndices.containsAll(indicesCollection))
  }

  /**
   * Checks the item collection of the handler's table against its model collection.
   * @param handler the handler
   */
  private def checkTableItems(handler: JavaFxTableHandler): Unit = {
    val items = tableFrom(handler).getItems
    assertEquals("Wrong number of items", handler.getModel.size, items.size)
    val itModel = handler.getModel.iterator()
    val itItems = items.iterator()
    while (itModel.hasNext) {
      assertEquals("Wrong element", itModel.next(), itItems.next())
    }
  }
}

/**
 * Test class for ''JavaFxTableHandler''.
 */
class TestJavaFxTableHandler extends JUnitSuite {

  import net.sf.jguiraffe.gui.platform.javafx.builder.components.table.TestJavaFxTableHandler._

  /**
   * Tests whether the correct type is returned for a table with single selection.
   */
  @Test def testTypeSingleSelection(): Unit = {
    assertEquals("Wrong type", Integer.TYPE, createHandler().getType)
  }

  /**
   * Tests whether the correct type is returned for a table with multiple selection.
   */
  @Test def testTypeMultipleSelection(): Unit = {
    assertEquals("Wrong type", classOf[Array[Int]], createHandler(multiSelect = true).getType)
  }

  /**
   * Tests whether the table's model can be initialized.
   */
  @Test def testInitializationOfTableModel(): Unit = {
    val model = createModel()
    val handler = createInitializedHandler(model = model)
    assertSame("Wrong model list", model, handler.getModel)
    val items = tableFrom(handler).getItems
    assertEquals("Wrong number of items", Rows, items.size)
    assertTrue("Wrong items: " + items, items.containsAll(model))
  }

  /**
   * Tests whether the selected index can be queried.
   */
  @Test def testGetSelectedIndex(): Unit = {
    val Index = Rows / 2
    val handler = createInitializedHandler(multiSelect = false)
    tableFrom(handler).getSelectionModel select Index

    assertEquals("Wrong selected index", Index, handler.getSelectedIndex)
    checkSelectedIndices(handler, Array(Index))
  }

  /**
   * Tests whether the selected index can be set.
   */
  @Test def testSetSelectedIndex(): Unit = {
    val Index = 2
    val handler = createInitializedHandler(multiSelect = false)

    handler setSelectedIndex Index
    assertEquals("Wrong selected index", Index, tableFrom(handler).getSelectionModel
      .getSelectedIndex)
  }

  /**
   * Tests that multiple calls to select an index always remove the existing selection.
   */
  @Test def testSelectionIsClearedBeforeAnUpdate(): Unit = {
    val Index = 1
    val handler = createInitializedHandler(multiSelect = true)

    handler setSelectedIndex (Rows - 1)
    handler setSelectedIndex Index
    val selectedIndices = tableFrom(handler).getSelectionModel.getSelectedIndices
    assertEquals("Wrong number of selected indices", 1, selectedIndices.size)
    assertEquals("Wrong selected index", Index, selectedIndices.get(0))
  }

  /**
   * Tests whether the selected indices can be queried.
   */
  @Test def testGetSelectedIndices(): Unit = {
    val indices = Array(0, 2, 5, Rows - 1)
    val handler = createInitializedHandler(multiSelect = true)
    tableFrom(handler).getSelectionModel.selectIndices(indices(0), indices.tail: _*)

    assertTrue("Wrong array with indices", java.util.Arrays.equals(indices,
      handler.getSelectedIndices))
  }

  /**
   * Tests whether an array of indices can be set.
   */
  @Test def testSetSelectedIndices(): Unit = {
    val indices = Array(1, 3, 4, Rows - 1)
    val handler = createInitializedHandler(multiSelect = true)

    handler setSelectedIndices indices
    checkSelectedIndices(handler, indices)
  }

  /**
   * Tests that the old selection is cleared before another array of indices is selected.
   */
  @Test def testSelectionIsClearedBeforeSelectingMultipleIndices(): Unit = {
    val indices = Array(1, 3, 4, Rows - 1)
    val handler = createInitializedHandler(multiSelect = true)
    tableFrom(handler).getSelectionModel select 2

    handler setSelectedIndices indices
    checkSelectedIndices(handler, indices)
  }

  /**
   * Tests whether the selection can be cleared.
   */
  @Test def testClearSelection(): Unit = {
    val handler = createInitializedHandler(multiSelect = true)
    tableFrom(handler).getSelectionModel.selectIndices(0, 2, 3, 4, Rows - 2, Rows - 1)

    handler.clearSelection()
    assertEquals("Wrong selected index", -1, handler.getSelectedIndex)
    checkSelectedIndices(handler, Array.empty[Int])
  }

  /**
   * Tests getData() in case of single selection support.
   */
  @Test def testGetDataSingleSelection(): Unit = {
    val Index = Rows / 2
    val handler = createInitializedHandler(multiSelect = false)
    tableFrom(handler).getSelectionModel select Index

    assertEquals("Wrong selected index", Integer.valueOf(Index), handler.getData)
  }

  /**
   * Tests getData() if multiple selection mode is enabled.
   */
  @Test def testGetDataMultipleSelection(): Unit = {
    val handler = createInitializedHandler(multiSelect = true)
    tableFrom(handler).getSelectionModel selectIndices(1, 2, 3, 4)

    val data = handler.getData.asInstanceOf[Array[Int]]
    assertTrue(s"Wrong selected indices: ${data.mkString(",")}", java.util.Arrays.equals(Array(1,
      2, 3, 4), data))
  }

  /**
   * Tests whether a single index can be passed to setData().
   */
  @Test def testSetDataSingleIndex(): Unit = {
    val Index = 4
    val handler = createInitializedHandler(multiSelect = false)
    tableFrom(handler).getSelectionModel select (Index + 1)

    handler setData Integer.valueOf(Index)
    assertEquals("Wrong selected index", Index, tableFrom(handler).getSelectionModel
      .getSelectedIndex)
    checkSelectedIndices(handler, Array(Index))
  }

  /**
   * Tests whether an array of indices can be passed to setData().
   */
  @Test def testSetDataMultipleIndices(): Unit = {
    val Indices = Array(0, 2, 4, 6)
    val handler = createInitializedHandler(multiSelect = true)
    tableFrom(handler).getSelectionModel select (Rows - 1)

    handler setData Indices
    checkSelectedIndices(handler, Indices)
  }

  /**
   * Tests setData() for other input.
   */
  @Test def testSetDataUnsupported(): Unit = {
    val handler = createInitializedHandler(multiSelect = true)
    tableFrom(handler).getSelectionModel select (Rows - 1)

    handler setData "This is a test"
    checkSelectedIndices(handler, Array.empty[Int])
  }

  /**
   * Tests whether the table's item collection is cleared before new content gets added.
   */
  @Test def testTableDataChangedReplacesOldContent(): Unit = {
    val model = createModel()
    val handler = createInitializedHandler(model = model)
    model.clear()
    model addAll java.util.Arrays.asList(new TableData(11), new TableData(47))

    handler.tableDataChanged()
    checkTableItems(handler)
  }

  /**
   * Tests whether notifications about inserted rows are processed correctly.
   */
  @Test def testRowsInserted(): Unit = {
    val Index = 3
    val model = createModel()
    val handler = createInitializedHandler(model = model)
    model.add(Index, new TableData(47))
    model.add(Index + 1, new TableData(11))

    handler.rowsInserted(Index, Index + 1)
    checkTableItems(handler)
  }

  /**
   * Tests whether notifications about deleted rows are processed correctly.
   */
  @Test def testRowsDeleted(): Unit = {
    val Index = 5
    val model = createModel()
    val handler = createInitializedHandler(model = model)
    model.remove(Index)
    model.remove(Index)

    handler.rowsDeleted(Index, Index + 1)
    checkTableItems(handler)
  }

  /**
   * Tests whether notifications about updated rows are processed correctly.
   */
  @Test def testRowsUpdated(): Unit = {
    val Index = 7
    val model = createModel()
    val handler = createInitializedHandler(model = model)
    model.set(Index, new TableData(100))
    model.set(Index + 1, new TableData(200))

    handler.rowsUpdated(Index, Index + 1)
    checkTableItems(handler)
  }

  /**
   * Tests whether the selection background color can be obtained.
   */
  @Test def testGetSelectionBackground(): Unit = {
    val handler = createHandler()
    handler.selectionStyles setValue StyleBackgroundColor

    assertEquals("Wrong selection background", BackgroundColor, handler.getSelectionBackground)
  }

  /**
   * Tests whether the selection foreground color can be obtained.
   */
  @Test def testGetSelectionForeground(): Unit = {
    val handler = createHandler()
    handler.selectionStyles setValue StyleForegroundColor

    assertEquals("Wrong selection foreground", ForegroundColor, handler.getSelectionForeground)
  }

  /**
   * Tests whether the selection background color can be set.
   */
  @Test def testSetSelectionBackground(): Unit = {
    val handler = createHandler()
    handler setSelectionBackground BackgroundColor

    assertEquals("Wrong styles", StyleBackgroundColor, handler.selectionStyles.get.trim)
  }

  /**
   * Tests whether the selection foreground color can be set.
   */
  @Test def testSetSelectionForeground(): Unit = {
    val handler = createHandler()
    handler setSelectionForeground ForegroundColor

    assertEquals("Wrong styles", StyleForegroundColor, handler.selectionStyles.get.trim)
  }

  /**
   * Tests whether multiple styles for selected rows can be set.
   */
  @Test def testMultipleSelectionStyles(): Unit = {
    val handler = createHandler()
    handler setSelectionForeground ForegroundColor
    handler setSelectionBackground BackgroundColor

    assertTrue("Foreground style not found", handler.selectionStyles.get contains
      StyleForegroundColor)
    assertTrue("Background style not found", handler.selectionStyles.get contains
      StyleBackgroundColor)
  }
}

/**
 * A simple data class used for populating the test table.
 * @param index a numeric index identifying an instance
 */
class TableData(@BeanProperty var index: Int)
