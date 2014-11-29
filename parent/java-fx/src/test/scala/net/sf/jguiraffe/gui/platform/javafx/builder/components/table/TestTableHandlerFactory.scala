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

import javafx.beans.property.StringProperty
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.scene.control.{TableColumn, TableView}

import net.sf.jguiraffe.gui.builder.components.tags.table.{TableColumnRecalibrator, TableColumnWidthCalculator, TableFormController}
import org.easymock.EasyMock
import org.junit.Assert._
import org.junit.{Before, Test}
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.EasyMockSugar

object TestTableHandlerFactory {
  /** The number of columns of the test table. */
  private val ColumnCount = 8

  /** The content of the data model for the test handler. */
  private val ModelData = Array[AnyRef]("Item1", "Item2", "Other Item", "Another item")

  /**
   * Returns an array with test columns for the table.
   * @return the array with test columns
   */
  private def createColumns(): Array[TableColumn[AnyRef, AnyRef]] = {
    ((1 to ColumnCount) map (i => new TableColumn[AnyRef, AnyRef](s"Column $i"))).toArray
  }

  /**
   * Creates the model for the table handler. This is just a list with arbitrary
   * data items.
   * @return the test model
   */
  private def createModel(): java.util.List[AnyRef] = {
    java.util.Arrays.asList(ModelData: _*)
  }
}

/**
 * Test class for ''TableHandlerFactory''.
 */
class TestTableHandlerFactory extends JUnitSuite with EasyMockSugar {

  import net.sf.jguiraffe.gui.platform.javafx.builder.components.table.TestTableHandlerFactory._

  /** A mock column factory. */
  private var mockColumnFactory: ColumnFactory = _

  /** A mock row factory. */
  private var mockRowFactory: StyleAwareRowFactory[AnyRef] = _

  /** A mock table width listener. */
  private var mockTableWidthListener: TableViewWidthChangeListener = _

  /** A mock resize policy. */
  private var mockResizePolicy: TableColumnRecalibrationResizePolicy = _

  /** A mock table width calculator. */
  private var mockWidthCalculator: TableColumnWidthCalculator = _

  /** A mock column recalibrator. */
  private var mockRecalibrator: TableColumnRecalibrator = _

  /** A mock observable for the table width. */
  private var mockWidthObservable: ObservableValue[java.lang.Number] = _

  @Before def setUp(): Unit = {
    mockColumnFactory = mock[ColumnFactory]
    mockRowFactory = mock[StyleAwareRowFactory[AnyRef]]
    mockTableWidthListener = mock[TableViewWidthChangeListener]
    mockResizePolicy = niceMock[TableColumnRecalibrationResizePolicy]
    mockWidthCalculator = mock[TableColumnWidthCalculator]
    mockRecalibrator = mock[TableColumnRecalibrator]
    mockWidthObservable = mock[ObservableValue[java.lang.Number]]
  }

  /**
   * Tests whether a default component factory is created.
   */
  @Test def testDefaultComponentFactory(): Unit = {
    val factory = new TableHandlerFactory
    assertNotNull("No default component factory", factory.componentFactory)
  }

  /**
   * Helper method for testing the creation of a table handler.
   * @param editable flag whether the table should be editable
   */
  private def checkCreateTableHandler(editable: Boolean): Unit = {
    val controller: TableFormController = createFormController(editable)
    val columns = createColumns()
    prepareColumnFactory(controller, columns)
    val styleProperty = prepareRowFactory()
    prepareWidthListener()

    val componentFactory = new MockTableComponentFactory
    val factory = new TableHandlerFactory(componentFactory)

    whenExecuting(mockColumnFactory, mockRowFactory, mockTableWidthListener, mockResizePolicy,
      mockWidthCalculator, mockRecalibrator, mockWidthObservable, controller) {
      val handler = factory.createTableHandler(controller).asInstanceOf[JavaFxTableHandler]
      val table = handler.getComponent.asInstanceOf[TableView[AnyRef]]

      assertEquals("Wrong editable flag", editable, table.isEditable)
      checkColumns(columns, table)
      assertEquals("Wrong data model", createModel(), handler.model)
      assertEquals("Wrong number of rows", ModelData.length, table.getItems.size)
      for (i <- 0 until ModelData.length) {
        assertEquals(s"Wrong item at $i", ModelData(i), table.getItems.get(i))
      }
      assertSame("Wrong passed table view", table, componentFactory.passedTableView)
      assertSame("Wrong resize policy", mockResizePolicy, table.getColumnResizePolicy)
      assertSame("Wrong row factory", mockRowFactory, table.getRowFactory)
      assertSame("Wrong style property", mockRowFactory.styleProperty, handler.selectionStyles)
    }
  }

  /**
   * Tests whether a table handler can be created and is fully initialized.
   */
  @Test def testCreateTableHandler(): Unit = {
    checkCreateTableHandler(editable = false)
  }

  /**
   * Tests whether the editable flag for the table is correctly processed.
   */
  @Test def testCreateTableHandlerForEditableTable(): Unit = {
    checkCreateTableHandler(editable = true)
  }

  /**
   * Creates a mock for the table form controller.
   * @param editable a flag whether the table is supposed to be editable
   * @return the controller mock
   */
  private def createFormController(editable: Boolean): TableFormController = {
    val controller = mock[TableFormController]
    EasyMock.expect(controller.getColumnWidthCalculator).andReturn(mockWidthCalculator).anyTimes()
    EasyMock.expect(controller.getColumnRecalibrator).andReturn(mockRecalibrator).anyTimes()
    EasyMock.expect(controller.getColumnCount).andReturn(ColumnCount).anyTimes()
    EasyMock.expect(controller.getDataModel).andReturn(createModel()).anyTimes()
    EasyMock.expect(controller.isTableEditable).andReturn(editable).anyTimes()
    controller
  }

  /**
   * Prepares the mock for the column factory to expect the creation of all columns.
   * @param controller the form controller
   * @param columns the columns to be returned by the factory
   */
  private def prepareColumnFactory(controller: TableFormController,
                                   columns: Array[TableColumn[AnyRef, AnyRef]]): Unit = {
    for (i <- 0 until ColumnCount) {
      EasyMock.expect(mockColumnFactory.createColumn(controller, i)).andReturn(columns(i))
      EasyMock.expect(mockResizePolicy installWidthChangeListener columns(i)).andReturn(null)
    }
  }

  /**
   * Checks whether the expected columns were created.
   * @param columns an array with the columns
   * @param table the table view
   */
  private def checkColumns(columns: Array[TableColumn[AnyRef, AnyRef]], table: TableView[AnyRef]) {
    assertEquals("Wrong number of columns", ColumnCount, table.getColumns.size)
    for (i <- 0 until ColumnCount) {
      assertEquals(s"Wrong column at $i", columns(i), table.getColumns.get(i))
    }
  }

  /**
   * Prepares the mock for the row factory.
   * @return the mock row factory
   */
  private def prepareRowFactory(): StringProperty = {
    val property = mock[StringProperty]
    EasyMock.expect(mockRowFactory.styleProperty).andReturn(property).anyTimes()
    property
  }

  /**
   * Prepares the mock for the width listener.
   */
  private def prepareWidthListener(): Unit = {
    mockWidthObservable addListener mockTableWidthListener
  }

  /**
   * A mock implementation of a component factory. This implementation returns the
   * mock objects defined as member fields in the enclosing test class.
   */
  private class MockTableComponentFactory extends TableComponentFactory {
    /** Stores the table view passed to factory methods. */
    var passedTableView: TableView[_] = _

    override val columnFactory: ColumnFactory = mockColumnFactory

    override def createRowFactory(): StyleAwareRowFactory[AnyRef] = mockRowFactory

    override def createTableWidthListener(calculator: TableColumnWidthCalculator,
                                          table: TableView[AnyRef]): ChangeListener[Number] = {
      storePassedInTable(table)
      assertSame("Wrong width calculator", mockWidthCalculator, calculator)
      mockTableWidthListener
    }

    override def createColumnResizePolicy(recalibrator: TableColumnRecalibrator):
    TableColumnRecalibrationResizePolicy = {
      assertSame("Wrong recalibrator", mockRecalibrator, recalibrator)
      mockResizePolicy
    }

    override def tableWidthProperty(table: TableView[_]): ObservableValue[Number] = {
      storePassedInTable(table)
      mockWidthObservable
    }

    /**
     * Stores the table passed to a factory method. It has to be ensured that always the
     * same table object is passed. This is verified by this helper method.
     * @param table the table to be stored
     */
    private def storePassedInTable(table: TableView[_]): Unit = {
      if (passedTableView == null) passedTableView = table
      else assertSame("Wrong table passed in", passedTableView, table)
    }
  }

}