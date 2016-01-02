/*
 * Copyright 2006-2016 The JGUIraffe Team.
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

import javafx.beans.value.ObservableValue
import javafx.scene.control.{TableColumn, TableView}

import net.sf.jguiraffe.gui.builder.components.tags.table.TableColumnRecalibrator
import net.sf.jguiraffe.gui.platform.javafx.{JavaFxTestHelper, FetchAnswer}
import org.easymock.EasyMock
import org.junit.Assert._
import org.junit.{BeforeClass, Before, Test}
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.EasyMockSugar

object TestTableColumnRecalibrationResizePolicy {
  @BeforeClass def setUpOnce(): Unit = {
    JavaFxTestHelper.initPlatform()
  }
}

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

  /** Constant for the width of the test table. */
  private val TableWidth = 400.0

  /** A mock for the object performing the recalibration. */
  private var recalibrator: TableColumnRecalibrator = _

  /** The table view with the columns to be resized. */
  private var table: TableView[AnyRef] = _

  /** The policy to be tested. */
  private var policy: TableColumnRecalibrationResizePolicy with MockColumnWidthExtractor with
    MockTableWidthExtractor = _

  @Before def setUp() {
    recalibrator = mock[TableColumnRecalibrator]
    policy = new TableColumnRecalibrationResizePolicy(recalibrator)
      with MockColumnWidthExtractor with MockTableWidthExtractor
    table = createTable()
    initTableWidth()
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
   * Performs an initial invocation of the policy which should initialize the
   * internally tracked table width.
   */
  private def initTableWidth(): Unit = {
    policy.definedTableWidth = TableWidth
    policy call resizeFeatures(null, 0)
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
   * Tests whether the width of the managed table is tracked.
   */
  @Test def testCurrentColumnWidth() {
    assertEquals("Wrong table width", TableWidth, policy.currentTableWidth, Precision)
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
   * Tests that the width of the managed table is checked before a column change
   * event is processed.
   */
  @Test def testIgnoreColumnWidthChangeTableResized() {
    policy.definedTableWidth = TableWidth - 100
    whenExecuting(recalibrator) {
      policy.columnWidthChanged(column(0), 100, 150)
    }
  }

  /**
   * Tests that initial column change events are ignored. In this case, the
   * table is not yet fully constructed and has no width.
   */
  @Test def testIgnoreColumnWidthChangeDuringTableSetup() {
    policy.definedTableWidth = 0
    policy call resizeFeatures(null, 0)
    whenExecuting(recalibrator) {
      policy.columnWidthChanged(column(0), 0, 50)
    }
  }

  /**
   * Tests whether a width change listener for a column can be installed.
   */
  @Test def testInstallWidthChangeListener() {
    val obsValue = mock[ObservableValue[Number]]
    val answer = new FetchAnswer[Unit, TableColumnWidthChangeListener]
    obsValue.addListener(EasyMock.anyObject(classOf[TableColumnWidthChangeListener]))
    EasyMock.expectLastCall().andAnswer(answer)
    policy initWidthProperty(column(0), obsValue)

    whenExecuting(obsValue) {
      val listener = policy installWidthChangeListener column(0)
      assertSame("Wrong column", column(0), listener.column)
      assertSame("Wrong policy", policy, listener.policy)
      assertSame("Listener not registered", listener, answer.get)
    }
  }

  /**
   * A mock implementation of the ''ColumnWidthExtractor'' trait.
   *
   * This implementation allows setting the widths and width properties for
   * columns directly. The explicitly set objects are returned.
   */
  private trait MockColumnWidthExtractor extends ColumnWidthExtractor {
    /** A map for storing column widths. */
    private val columnWidths = collection.mutable.Map.empty[TableColumn[_, _], Double]

    /** A map for storing column width properties. */
    private val widthProperties = collection.mutable.Map.empty[TableColumn[_, _],
      ObservableValue[Number]]

    /**
     * Initializes the width of a specific column.
     * @param pair a tuple of a column and its associated with
     */
    def initWidth(pair: (TableColumn[_, _], Double)) {
      columnWidths += pair
    }

    /**
     * Initializes the width property of a specific column.
     * @param pair a tuple of a column and its associated with property
     */
    def initWidthProperty(pair: (TableColumn[_, _], ObservableValue[Number])) {
      widthProperties += pair
    }

    /**
     * @inheritdoc This implementation returns the width property associated with
     *             the specified column. It must have been initialized before.
     */
    override def widthProperty(column: TableColumn[_, _]): ObservableValue[Number] =
      widthProperties(column)

    /**
     * @inheritdoc This implementation returns a width which must have been set
     *             explicitly before.
     */
    override def columnWidth(column: TableColumn[_, _]): Double = columnWidths(column)
  }

  /**
   * A mock implementation of the ''TableWidthExtractor'' trait which allows
   * specifying a specific width for the test table.
   */
  private trait MockTableWidthExtractor extends TableWidthExtractor {
    /** The width of the test table. */
    var definedTableWidth = 0.0

    /**
     * @inheritdoc This implementation returns the width defined by the member
     *             field. It is also checked whether the passed in table is
     *             the test table.
     */
    override def tableWidth(testTable: TableView[_]): Double = {
      assertEquals("Wrong table", table, testTable)
      definedTableWidth
    }
  }
}
