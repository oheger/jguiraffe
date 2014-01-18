/**
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
package net.sf.jguiraffe.gui.platform.javafx.builder.components.tree

import org.easymock.EasyMock
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.EasyMockSugar

import javafx.scene.input.KeyCode

/**
 * Test class for ''ConfigNodeTreeCell''.
 */
class TestConfigNodeTreeCell extends JUnitSuite with EasyMockSugar {
  /** A mock for the cell controller. */
  private var ctrl: ConfigCellController = _

  /** The cell to be tested. */
  private var cell: ConfigNodeTreeCell = _

  @Before def setUp() {
    ctrl = mock[ConfigCellController]
    cell = new ConfigNodeTreeCell(ctrl)
  }

  /**
   * Tests the cell data object created by the cell.
   */
  @Test def testCellData() {
    val cd = cell.cellData
    assertSame("Wrong view", cell, cd.view)
    assertNotNull("No edit field", cd.editField)
  }

  /**
   * Tests whether the edit field is cached once created.
   */
  @Test def testEditFieldCached() {
    val cd = cell.cellData
    val cd2 = cell.cellData
    assertSame("Multiple edit fields", cd.editField, cd2.editField)
  }

  /**
   * Tests whether an edit operation can be started.
   */
  @Test def testStartEdit() {
    val cd = cell.cellData
    ctrl handleStartEdit cd
    whenExecuting(ctrl) {
      cell.startEdit()
    }
  }

  /**
   * Tests the cancellation of an edit operation.
   */
  @Test def testCancelEdit() {
    val cd = cell.cellData
    ctrl handleCancelEdit cd
    whenExecuting(ctrl) {
      cell.cancelEdit()
    }
  }

  /**
   * Tests whether a cancel operation can be triggered in the edit field
   * using a key code.
   */
  @Test def testEditFieldCancel() {
    val cd = cell.cellData
    ctrl handleCancelEdit cd
    whenExecuting(ctrl) {
      cell handleEditFieldKeyCode KeyCode.ESCAPE
    }
  }

  /**
   * Tests whether other keys than the special ones are ignored by the key
   * listener on the edit field. We can only test that no exception occurs.
   */
  @Test def testEditFieldOtherKey() {
    cell handleEditFieldKeyCode KeyCode.A
  }

  /**
   * Tests whether an edit operation can be committed via a key code in the
   * edit field.
   */
  @Test def testEditFieldCommit() {
    val newData = mock[ConfigNodeData]
    val cd = cell.cellData
    EasyMock.expect(ctrl.handleCommitEdit(cd)).andReturn(newData)
    whenExecuting(ctrl) {
      cell handleEditFieldKeyCode KeyCode.ENTER
    }
  }

  /**
   * Tests whether the current item can be updated.
   */
  @Test def testUpdateItem() {
    val newData = mock[ConfigNodeData]
    val cd = cell.cellData.copy(data = newData)
    ctrl.handleUpdateItem(cd, false, false)
    whenExecuting(ctrl) {
      cell.updateItem(newData, false)
    }
  }
}
