/*
 * Copyright 2006-2015 The JGUIraffe Team.
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

import javafx.scene.control.TableView

import net.sf.jguiraffe.gui.builder.components.tags.table.{TableColumnRecalibrator, TableColumnWidthCalculator}

import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import org.junit.Assert._
import org.junit.{BeforeClass, Before, Test}
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.EasyMockSugar

object TestTableComponentFactory {
  @BeforeClass def setUpOnce(): Unit = {
    JavaFxTestHelper.initPlatform()
  }
}

/**
 * Test class for ''TableComponentFactory''.
 */
class TestTableComponentFactory extends JUnitSuite with EasyMockSugar {
  /** The factory to be tested. */
  private var factory: TableComponentFactory = _

  @Before def setUp(): Unit = {
    factory = new TableComponentFactory
  }

  /**
   * Tests whether the column factory has been correctly initialized.
   */
  @Test def testColumnFactory(): Unit = {
    assertNotNull("No column factory", factory.columnFactory)
  }

  /**
   * Tests whether a correct row factory can be created.
   */
  @Test def testCreateRowFactory(): Unit = {
    assertNotNull("No row factory", factory.createRowFactory())
  }

  /**
   * Tests whether a change listener for the table width can be created.
   */
  @Test def testCreateTableWidthListener(): Unit = {
    val calculator = mock[TableColumnWidthCalculator]
    val table = mock[TableView[AnyRef]]

    val listener = factory.createTableWidthListener(calculator, table)
    val tabListener = listener.asInstanceOf[TableViewWidthChangeListener]
    assertSame("Calculator not set", calculator, tabListener.calculator)
    assertSame("Table not set", table, tabListener.table)
  }

  /**
   * Tests whether a correct resize policy is created.
   */
  @Test def testCreateColumnResizePolicy(): Unit = {
    val recalibrator = mock[TableColumnRecalibrator]

    val policy = factory createColumnResizePolicy recalibrator
    assertSame("Recalibrator not set", recalibrator, policy.recalibrator)
  }
}
