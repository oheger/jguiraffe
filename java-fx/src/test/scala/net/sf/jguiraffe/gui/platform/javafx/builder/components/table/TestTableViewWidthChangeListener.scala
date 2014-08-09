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

import javafx.scene.control.{TableColumn, TableView}

import net.sf.jguiraffe.gui.builder.components.tags.table.TableColumnWidthCalculator
import org.easymock.EasyMock
import org.junit.Assert._
import org.junit.Test
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.EasyMockSugar

/**
 * Test class for ''TableViewWidthChangeListener''.
 */
class TestTableViewWidthChangeListener extends JUnitSuite with EasyMockSugar {
  /**
   * Tests whether a change of the table width is correctly processed.
   */
  @Test def testTableWidthChanged(): Unit = {
    val controller = mock[TableColumnWidthCalculator]
    val table = new TableView[AnyRef]
    val col1 = new TableColumn[AnyRef, AnyRef]
    val col2 = new TableColumn[AnyRef, AnyRef]
    table.getColumns.addAll(col1, col2)
    val Delta = 0.001
    val widths = Array(100, 200)
    val Size = 300
    EasyMock.expect(controller.calculateWidths(Size)).andReturn(widths)

    val listener = new TableViewWidthChangeListener(controller, table)
    whenExecuting(controller) {
      listener.changed(null, 200, Size)
      assertEquals("Wrong column width (1)", widths(0), col1.getPrefWidth, Delta)
      assertEquals("Wrong column width (2)", widths(1), col2.getPrefWidth, Delta)
    }
  }
}
