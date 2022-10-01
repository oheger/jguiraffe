/*
 * Copyright 2006-2022 The JGUIraffe Team.
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

import javafx.scene.control.TableColumn
import javafx.scene.control.TableColumn.CellDataFeatures

import net.sf.jguiraffe.gui.builder.components.tags.table.{ColumnClass, TableFormController}
import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import org.easymock.EasyMock
import org.junit.Assert._
import org.junit.{BeforeClass, Before, Test}
import org.scalatest.junit.JUnitSuite
import org.scalatest.easymock.EasyMockSugar

object TestColumnFactory {
  /** Constant for a column name. */
  private val ColumnName = "MyTestColumn"

  @BeforeClass def setUpOnce(): Unit = {
    JavaFxTestHelper.initPlatform()
  }

  /**
   * Checks basic properties of a newly created column.
   * @param col the column to be checked
   * @return the same column
   */
  private def checkColumn(col: TableColumn[AnyRef, AnyRef]): TableColumn[AnyRef, AnyRef] = {
    assertEquals("Wrong column name", ColumnName, col.getText)
    checkValueFactory(col)
  }

  /**
   * Checks whether a correct value factory has been set for the given column.
   * @param col the column to be tested
   * @return the same column
   */
  private def checkValueFactory(col: TableColumn[AnyRef, AnyRef]): TableColumn[AnyRef, AnyRef] = {
    val features: CellDataFeatures[AnyRef, AnyRef] = EasyMock.createMock(classOf[CellDataFeatures[AnyRef, AnyRef]])
    EasyMock.expect(features.getValue).andReturn(this)
    EasyMock.replay(features)

    val prop = col.getCellValueFactory call features
    assertEquals("Wrong value", this, prop.getValue)
    col
  }

  /**
   * Extracts the cell of the specified column and checks its type.
   * @param col the column
   * @param m the manifest
   * @tparam T the expect type of the cell
   * @return the table cell
   */
  private def extractCell[T](col: TableColumn[AnyRef, AnyRef])(implicit m: Manifest[T]): T = {
    col.getCellFactory call col match {
      case cell: T => cell
      case other =>
        throw new IllegalStateException("Unexpected table cell: " + other)
    }
  }
}

/**
 * Test class for ''ColumnFactory''.
 */
class TestColumnFactory extends JUnitSuite with EasyMockSugar {

  import net.sf.jguiraffe.gui.platform.javafx.builder.components.table.TestColumnFactory._

  /** The mock controller. */
  private var controller: TableFormController = _

  /** The factory to be tested. */
  private var factory: ColumnFactory = _

  @Before def setUp() {
    controller = mock[TableFormController]
    factory = new ColumnFactory
  }

  /**
   * Prepares the mock controller to return the corresponding parameters for a
   * column.
   * @param colIdx the index of the column in question
   * @param renderer the renderer to be returned for this column
   * @param columnClass the logic class to be returned for this column
   * @param editable flag whether this column should be editable
   */
  private def prepareController(colIdx: Int, renderer: AnyRef= null,
                                columnClass: ColumnClass = ColumnClass.STRING,
                                editable: Boolean = false) {
    EasyMock.expect(controller.hasRenderer(colIdx)).andReturn(renderer != null).anyTimes()
    EasyMock.expect(controller.getColumnRenderer(colIdx)).andReturn(renderer).anyTimes()
    EasyMock.expect(controller.getLogicDataClass(colIdx)).andReturn(columnClass).anyTimes()
    EasyMock.expect(controller.isColumnEditable(colIdx)).andReturn(editable).anyTimes()
    EasyMock.expect(controller.getColumnName(colIdx)).andReturn(ColumnName).anyTimes()
  }

  /**
   * Prepares the mock controller to expect that transformers for the column type
   * are to be installed.
   * @param colIdx the column index
   */
  private def expectInstallTransformers(colIdx: Int) {
    EasyMock.expect(controller.installTransformersForColumnType(colIdx)).andReturn(true)
  }

  /**
   * Creates a new table column using the test form controller and performs some
   * checks of basic properties.
   * @param colIdx the column index
   * @param expEditFlag the expected editable flag of the column
   * @return the newly created column
   */
  private def createAndCheckColumn(colIdx: Int, expEditFlag: Boolean): TableColumn[AnyRef,
    AnyRef] = {
    val column = checkColumn(factory.createColumn(controller, colIdx))
    assertEquals("Wrong editable flag", expEditFlag, column.isEditable)
    column
  }

  /**
   * Checks whether a cell of type ''FormControllerCell'' has been correctly
   * initialized.
   * @param cell the cell to be checked
   * @param colIdx the expected column index
   */
  private def checkFormControllerCell(cell: FormControllerCell[_, _], colIdx: Int) {
    assertSame("Wrong form controller", controller, cell.formController)
    assertEquals("Wrong column index", colIdx, cell.columnIndex)
  }

  /**
   * Tests whether a column with a renderer is correctly initialized.
   */
  @Test def testColumnWithRenderer() {
    val ColIdx = 1
    val renderer = niceMock[CellComponentManager]
    prepareController(ColIdx, renderer = renderer)

    whenExecuting(controller) {
      val column = createAndCheckColumn(ColIdx, expEditFlag = false)
      val cell = extractCell[RenderCell](column)
      assertSame("Wrong component manager", renderer, cell.cellComponentManager)
    }
  }

  /**
   * Helper method for testing whether a column that does not have any special
   * type is created correctly.
   * @param editable a flag whether the column is editable
   */
  private def checkColumnStandardType(editable: Boolean) {
    val ColIdx = 0
    prepareController(ColIdx, editable = editable)
    expectInstallTransformers(ColIdx)

    whenExecuting(controller) {
      val column = createAndCheckColumn(ColIdx, expEditFlag = editable)
      checkFormControllerCell(extractCell[EditableTableCell](column), ColIdx)
    }
  }

  /**
   * Tests whether a correct column for a standard type is created which is not
   * editable.
   */
  @Test def testColumnStandardTypeNotEditable() {
    checkColumnStandardType(editable = false)
  }

  /**
   * Tests whether a correct column for a standard type is created which can
   * be edited.
   */
  @Test def testColumnStandardTypeEditable() {
    checkColumnStandardType(editable = true)
  }

  /**
   * Tests whether a column of type boolean can be created.
   */
  @Test def testColumnBoolean() {
    val ColIdx = 2
    prepareController(ColIdx, columnClass = ColumnClass.BOOLEAN)
    expectInstallTransformers(ColIdx)

    whenExecuting(controller) {
      val column = createAndCheckColumn(ColIdx, expEditFlag = false)
      checkFormControllerCell(extractCell[BooleanCell[AnyRef, AnyRef]](column), ColIdx)
    }
  }

  /**
   * Tests whether a column of type icon can be created.
   */
  @Test def testColumnIcon() {
    val ColIdx = 3
    prepareController(ColIdx, columnClass = ColumnClass.ICON)
    expectInstallTransformers(ColIdx)

    whenExecuting(controller) {
      val column = createAndCheckColumn(ColIdx, expEditFlag = false)
      checkFormControllerCell(extractCell[IconCell[AnyRef]](column), ColIdx)
    }
  }
}
