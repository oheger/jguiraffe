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

import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.control.TableView

import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import org.easymock.EasyMock
import org.junit.Assert._
import org.junit.runner.RunWith
import org.junit.{BeforeClass, Before, Test}
import org.powermock.api.easymock.PowerMock
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.scalatest.junit.JUnitSuite

object TestTableWidthExtractor {
  @BeforeClass def setUpOnce(): Unit = {
    JavaFxTestHelper.initPlatform()
  }
}

/**
 * Test class for ''TableWidthExtractor''.
 */
@RunWith(classOf[PowerMockRunner])
@PrepareForTest(Array(classOf[TableView[_]]))
class TestTableWidthExtractor extends JUnitSuite {
  /** The extractor to be tested. */
  private var extractor: TableWidthExtractor = _

  @Before def setUp(): Unit = {
    extractor = new TableWidthExtractor {}
  }

  /**
   * Tests whether the correct width property of a table is returned.
   */
  @Test def testTableWidthProperty(): Unit = {
    val table = new TableView[AnyRef]
    assertSame("Wrong width property", table.widthProperty, extractor tableWidthProperty table)
  }

  /**
   * Tests whether the width of a table can be determined.
   */
  @Test def testTableWidth(): Unit = {
    val table = PowerMock.createMock(classOf[TableView[AnyRef]])
    val width = 640.5
    val property = new SimpleDoubleProperty(width)
    EasyMock.expect(table.widthProperty).andReturn(property)
    PowerMock.replayAll()

    assertEquals("Wrong width", width, extractor.tableWidth(table), 0.001)
  }
}
