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

import javafx.scene.control.TableColumn

import org.junit.{Before, Test}
import org.scalatestplus.junit.JUnitSuite
import org.scalatestplus.easymock.EasyMockSugar

/**
 * Test class for ''TableColumnWidthChangeListener''.
 */
class TestTableColumnWidthChangeListener extends JUnitSuite with EasyMockSugar {
  /** The associated column. */
  private var column: TableColumn[_, _] = _

  /** The mock resize policy. */
  private var policy: TableColumnRecalibrationResizePolicy = _

  /** The change listener to be tested. */
  private var listener: TableColumnWidthChangeListener = _

  @Before def setUp() {
    column = new TableColumn[AnyRef, AnyRef]
    policy = mock[TableColumnRecalibrationResizePolicy]
    listener = new TableColumnWidthChangeListener(policy, column)
  }

  /**
   * Tests whether a change notification is correctly propagated.
   */
  @Test def testColumnWidthChanged(): Unit = {
    val OldWidth = 100.0
    val NewWidth = 120.0
    policy.columnWidthChanged(column, OldWidth, NewWidth)

    whenExecuting(policy) {
      listener.changed(null, OldWidth, NewWidth)
    }
  }
}
