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

import javafx.scene.control.{TableColumn, TableView}

import net.sf.jguiraffe.gui.builder.components.tags.table.TableColumnRecalibrator
import org.easymock.EasyMock
import org.junit.Assert._
import org.junit.{Before, Test}
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.EasyMockSugar

/**
 * Test class for ''TableColumnRecalibrationResizePolicy''.
 */
class TestTableColumnRecalibrationResizePolicy extends JUnitSuite with EasyMockSugar {
  /** An array with the names of the test columns. */
  private val ColumnNames = Array("Icon", "First name", "Last name", "External")

  /** An array with column widths. */
  private val ColumnWidths = Array(40.0, 100.0, 150.0, 60.0)

  /** Constant for the precision in asserts with doubles. */
  private val Precision = 0.0001

  /** A mock for the object performing the recalibration. */
  private var recalibrator: TableColumnRecalibrator = _

  /** The table view with the columns to be resized. */
  private var table: TableView[AnyRef] = _

  /** The policy to be tested. */
  private var policy: TableColumnRecalibrationResizePolicy with MockColumnWidthExtractor = _

  @Before def setUp() {
    recalibrator = mock[TableColumnRecalibrator]
    policy = new TableColumnRecalibrationResizePolicy(recalibrator) with MockColumnWidthExtractor
    table = createTable()
  }

  /**
   * Creates the test table view and initializes its test columns.
   * @return the table view
   */
  private def createTable(): TableView[AnyRef] = {
    val table = new TableView[AnyRef]
    (ColumnNames zip ColumnWidths) foreach { data =>
      val column = new TableColumn[AnyRef, AnyRef](data._1)
      column setPrefWidth data._2
      policy initWidth column -> data._2
      table.getColumns add column
    }
    table
  }

  /**
   * Returns the test column with the given index.
   * @param index the index
   * @return the test column with this index
   */
  private def column(index: Int): TableColumn[AnyRef, _] = table.getColumns.get(index)

  /**
   * Creates a ''ResizeFeatures'' object for the test table with the given content.
   * @param column the column affected by the operation
   * @param delta the delta
   * @return the ''ResizeFeatures'' object
   */
  private def resizeFeatures(column: TableColumn[AnyRef, _], delta: Double) =
    new TableView.ResizeFeatures[AnyRef](table, column, delta)

  /**
   * Checks whether the test columns have the expected preferred widths after a
   * resize operation.
   * @param expected an array with the expected widths
   */
  private def checkWidths(expected: Array[Double]) {
    for (i <- 0 until expected.size) {
      assertEquals(s"Wrong preferred with for column $i", expected(i),
        table.getColumns.get(i).getPrefWidth, Precision)
    }
  }

  /**
   * Invokes a resize call and checks the resulting column widths.
   * @param features the resize features for the call
   * @param expected the expected column widths
   * @return the result of the call
   */
  private def callAndCheckWidths(features: TableView.ResizeFeatures[_],
                                 expected: Array[Double]): Boolean = {
    val result = policy call features
    checkWidths(expected)
    result
  }

  /**
   * Tests that an undefined column in the resize features is ignored.
   */
  @Test def testIgnoreUndefinedColumn() {
    assertFalse("Wrong result", callAndCheckWidths(resizeFeatures(null, 0), ColumnWidths))
  }

  /**
   * Prepares the recalibrator mock to expect an invocation.
   * @param widths the expected widths
   */
  private def expectRecalibrate(widths: Array[Double]) {
    recalibrator recalibrate EasyMock.aryEq(widths map (math.round(_).toInt))
  }

  /**
   * Prepares the mock for the recalibrator to expect an invocation with an arbitrary
   * array.
   */
  private def expectRecalibrateCall() {
    recalibrator recalibrate EasyMock.anyObject(classOf[Array[Int]])
  }

  /**
   * Tests a successful resize operation if the full delta can be applied to the last
   * column.
   */
  @Test def testSuccessfulResizeOfSingleColumn() {
    val delta = 5
    val widths = Array(ColumnWidths(0), ColumnWidths(1), ColumnWidths(2) + delta,
      ColumnWidths(3) - delta)
    expectRecalibrate(widths)

    whenExecuting(recalibrator) {
      assertTrue("Wrong result", callAndCheckWidths(resizeFeatures(column(2), delta),
        widths))
    }
  }

  /**
   * Tests a failed resize operation due to minimum size restrictions.
   */
  @Test def testFailedResizeMinimum() {
    val lastIndex = ColumnNames.size - 1
    column(lastIndex) setMinWidth (ColumnWidths(lastIndex) - 5)

    whenExecuting(recalibrator) {
      assertFalse("Wrong result", callAndCheckWidths(resizeFeatures(column(lastIndex - 1), 10),
        ColumnWidths))
    }
  }

  /**
   * Tests a failed resize operation due to maximum size restrictions.
   */
  @Test def testFailedResizeMaximum() {
    val lastIndex = ColumnNames.size - 1
    column(lastIndex) setMaxWidth (ColumnWidths(lastIndex) + 5)

    whenExecuting(recalibrator) {
      assertFalse("Wrong result", callAndCheckWidths(resizeFeatures(column(lastIndex - 1), -10),
        ColumnWidths))
    }
  }

  /**
   * Tests whether the width change of one column can be distributed over multiple
   * columns to the right of the affected column.
   */
  @Test def testSuccessfulResizeOverMultipleColumns() {
    val lastIndex = ColumnNames.size - 1
    column(lastIndex) setMinWidth (ColumnWidths(lastIndex) - 4)
    val widths = Array(ColumnWidths(0), ColumnWidths(1) + 10, ColumnWidths(2) - 6,
      ColumnWidths(3) - 4)
    expectRecalibrate(widths)

    whenExecuting(recalibrator) {
      assertTrue("Wrong result", callAndCheckWidths(resizeFeatures(column(lastIndex - 2), 10),
        widths))
    }
  }

  /**
   * Tests whether a resize operation of the last column can be handled.
   */
  @Test def testSuccessfulResizeOfLastColumn() {
    val lastIndex = ColumnNames.size - 1
    val delta = -16
    val widths = Array(ColumnWidths(0) - delta, ColumnWidths(1), ColumnWidths(2),
      ColumnWidths(3) + delta)
    expectRecalibrate(widths)

    whenExecuting(recalibrator) {
      assertTrue("Wrong result", callAndCheckWidths(resizeFeatures(column(lastIndex), delta),
        widths))
    }
  }

  /**
   * Tests whether a change notification of a table column in correctly processed.
   */
  @Test def testColumnWidthChanged() {
    val delta = -8
    val ColIdx = 1
    val widths = Array(ColumnWidths(0), ColumnWidths(1), ColumnWidths(2), ColumnWidths(3) - delta)
    expectRecalibrate(widths)

    whenExecuting(recalibrator) {
      policy.columnWidthChanged(column(ColIdx), ColumnWidths(ColIdx) - delta, ColumnWidths(ColIdx))
      checkWidths(widths)
    }
  }

  /**
   * Helper method for testing whether the correct number of column width changed
   * notifications is ignored after a resize operation. (During a resize column
   * widths are changed which lead to update events as well; those have to be
   * ignored).
   * @param widths an array with the expected column widths
   * @param delta the delta
   * @param ignoredCalls the number of calls to be ignored
   */
  private def checkIgnoreColumnWidthChangedAfterResize(widths: Array[Double], delta: Double,
                                                       ignoredCalls: Int) {
    expectRecalibrate(widths)
    expectRecalibrateCall()

    whenExecuting(recalibrator) {
      assertTrue("Wrong result", callAndCheckWidths(resizeFeatures(column(1), delta),
        widths))
      for (i <- 1 to ignoredCalls) {
        policy.columnWidthChanged(column(1), ColumnWidths(1), ColumnWidths(1) + delta)
      }
    }
  }

  /**
   * Tests that column width changed notifications are ignored after columns
   * have been updated in a resize operation.
   */
  @Test def testIgnoreColumnWidthChangedAfterResize() {
    val delta = 5
    val widths = Array(ColumnWidths(0), ColumnWidths(1) + delta, ColumnWidths(2),
      ColumnWidths(3) - delta)
    checkIgnoreColumnWidthChangedAfterResize(widths, delta, 3)
  }

  /**
   * Tests that column width change notifications are ignored after resize operations
   * that affect multiple columns.
   */
  @Test def testIgnoreColumnWidthChangedAfterResizeMultipleColumns() {
    val lastIndex = ColumnNames.size - 1
    column(lastIndex) setMinWidth (ColumnWidths(lastIndex) - 4)
    val widths = Array(ColumnWidths(0), ColumnWidths(1) + 10, ColumnWidths(2) - 6,
      ColumnWidths(3) - 4)
    checkIgnoreColumnWidthChangedAfterResize(widths, 10, 4)
  }

  /**
   * Tests that column width change notifications are ignored after resize operations
   * that affect multiple columns, but some columns have not been changed.
   */
  @Test def testIgnoreColumnWidthChangedAfterResizeColumnsNotChanged() {
    val lastIndex = ColumnNames.size - 1
    val delta = 10
    column(lastIndex) setMinWidth ColumnWidths(lastIndex)
    val widths = Array(ColumnWidths(0), ColumnWidths(1) + delta, ColumnWidths(2) - delta,
      ColumnWidths(3))
    checkIgnoreColumnWidthChangedAfterResize(widths, delta, 3)
  }

  /**
   * Tests that no column width updates are ignored after a failed resize operation.
   */
  @Test def testNoColumnWidthUpdatesIgnoredAfterFailedResize() {
    val lastIndex = ColumnNames.size - 1
    val delta = 10
    column(lastIndex) setMinWidth ColumnWidths(lastIndex)
    expectRecalibrateCall()

    whenExecuting(recalibrator) {
      assertFalse("Wrong result", callAndCheckWidths(resizeFeatures(column(lastIndex - 1), delta),
        ColumnWidths))
      policy.columnWidthChanged(column(1), ColumnWidths(1), ColumnWidths(1) + delta)
    }
  }

  /**
   * A mock implementation of the ''ColumnWidthExtractor'' trait.
   *
   * This implementation allows setting the widths for columns directly. The
   * explicitly set widths are returned.
   */
  private trait MockColumnWidthExtractor extends ColumnWidthExtractor {
    /** A map for storing column widths. */
    private val columnWidths = collection.mutable.Map.empty[TableColumn[_, _], Double]

    /**
     * Initializes the width of a specific column.
     * @param pair a tuple of a column and its associated with
     */
    def initWidth(pair: (TableColumn[_, _], Double)) {
      columnWidths += pair
    }

    /**
     * @inheritdoc This implementation returns a width which must have been set
     *             explicitly before.
     */
    override def columnWidth(column: TableColumn[_, _]): Double = columnWidths(column)
  }

}
