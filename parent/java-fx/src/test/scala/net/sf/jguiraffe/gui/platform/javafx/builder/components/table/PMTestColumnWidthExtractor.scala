/*
 * Copyright 2006-2025 The JGUIraffe Team.
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

import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.control.TableColumn

import org.easymock.EasyMock
import org.junit.Assert._
import org.junit.runner.RunWith
import org.junit.{Before, Test}
import org.powermock.api.easymock.PowerMock
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.scalatest.junit.JUnitSuite

/**
 * Test class for ''ColumnWidthExtractor''.
 */
@RunWith(classOf[PowerMockRunner])
@PrepareForTest(Array(classOf[TableColumn[_, _]]))
class PMTestColumnWidthExtractor extends JUnitSuite {
  /** The test extractor. */
  private var extractor: ColumnWidthExtractor = _

  @Before def setUp() {
    extractor = new ColumnWidthExtractor {}
  }

  /**
   * Tests whether the correct width property is returned.
   */
  @Test def testWidthProperty() {
    val column = new TableColumn[AnyRef, AnyRef]
    assertSame("Wrong width property", column.widthProperty, extractor widthProperty column)
  }

  /**
   * Tests whether the width of a column can be extracted.
   */
  @Test def testColumnWidth() {
    val column = PowerMock.createMock(classOf[TableColumn[_, _]])
    val width = 100.5
    val property = new SimpleDoubleProperty(width)
    EasyMock.expect(column.widthProperty).andReturn(property)
    PowerMock.replayAll()

    assertEquals("Wrong result", width, extractor columnWidth column, .001)
  }
}
