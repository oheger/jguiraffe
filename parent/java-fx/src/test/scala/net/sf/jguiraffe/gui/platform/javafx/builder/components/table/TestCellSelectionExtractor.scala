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

import javafx.scene.control.TableRow

import org.junit.{Before, Test}
import org.scalatest.junit.JUnitSuite

/**
 * Test class for ''CellSelectionExtractor''.
 */
class TestCellSelectionExtractor extends JUnitSuite {
  /** The extractor to be tested. */
  private var extractor: CellSelectionExtractor = _

  @Before def setUp(): Unit = {
    extractor = new CellSelectionExtractor {}
  }

  /**
   * Tests whether the correct property is returned.
   */
  @Test def testSelectedProperty(): Unit = {
    val row = new TableRow[AnyRef]
    assert(row.selectedProperty === extractor.selectedProperty(row))
  }
}
