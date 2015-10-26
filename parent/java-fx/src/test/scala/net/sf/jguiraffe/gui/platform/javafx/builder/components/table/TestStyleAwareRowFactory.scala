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

import javafx.beans.property.{BooleanProperty, ReadOnlyBooleanProperty, SimpleBooleanProperty}
import javafx.scene.control.{Cell, TableRow, TableView}

import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import org.junit.Assert._
import org.junit.{BeforeClass, Before, Test}
import org.scalatest.junit.JUnitSuite

object TestStyleAwareRowFactory {
  @BeforeClass def setUpOnce(): Unit = {
    JavaFxTestHelper.initPlatform()
  }
}

/**
 * Test class for ''StyleAwareRowFactory''.
 */
class TestStyleAwareRowFactory extends JUnitSuite {
  /** A constant for a test style definition. */
  private val Style = "-fx-background-color: #CCDDEE;"

  /** The test property for manipulating the selection. */
  private var testSelectedProperty: BooleanProperty = _

  /** The factory to be tested. */
  private var factory: StyleAwareRowFactory[AnyRef] = _

  @Before def setUp(): Unit = {
    testSelectedProperty = new SimpleBooleanProperty(false)
    factory = new StyleAwareRowFactory[AnyRef] with MockSelectionExtractor
  }

  /**
   * Creates a row using the test factory.
   * @return the table row
   */
  private def createRow(): TableRow[AnyRef] = {
    factory.styleProperty set Style
    val table = new TableView[AnyRef]
    factory call table
  }

  /**
   * Tests the style if the row is not selected.
   */
  @Test def testStyleNotSelected(): Unit = {
    val row = createRow()
    assertFalse("Selected", row.isSelected)
    assertEquals("Got styles", "", row.getStyle)
  }

  /**
   * Tests whether the style is changed if the row is selected.
   */
  @Test def testStyleSelected(): Unit = {
    val row = createRow()
    testSelectedProperty set true
    assertEquals("Wrong style", Style, row.getStyle)
  }

  /**
   * A mock implementation of ''CellSelectionExtractor'' allowing to use a
   * test property.
   */
  private trait MockSelectionExtractor extends CellSelectionExtractor {
    override def selectedProperty(cell: Cell[_]): ReadOnlyBooleanProperty = testSelectedProperty
  }

}
