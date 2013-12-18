/*
 * Copyright 2006-2013 The JGUIraffe Team.
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

import org.apache.commons.configuration.tree.ConfigurationNode
import org.easymock.EasyMock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.EasyMockSugar

import javafx.scene.control.Label
import javafx.scene.control.Labeled
import javafx.scene.control.TextField
import javafx.scene.control.TreeItem
import net.sf.jguiraffe.gui.builder.components.model.TreeConfigurationChangeHandler

/**
 * Companion object for ''TestConfigCellController''
 */
object TestConfigCellController {
  /** Constant for a test node name. */
  private val NodeName = "This is my test node name!"

  /** A mock configuration node with a well-defined name. */
  private var Node: ConfigurationNode = _

  /** An item referring to a node. */
  private var Item: ConfigNodeData = _

  @BeforeClass def setUpBeforeClass() {
    Node = EasyMock.createMock(classOf[ConfigurationNode])
    EasyMock.expect(Node.getName).andReturn(NodeName).anyTimes()
    EasyMock.replay(Node)
    Item = ConfigNodeData(Node)
  }

  /**
   * Creates a ''CellData'' structure with default values.
   * @param lab the ''Labeled'' object to put into the data object
   * @return the data object
   */
  private def createCellData(lab: Labeled) = CellData(data = Item,
    treeItem = new TreeItem, view = lab, new TextField)
}

/**
 * Test class for ''ConfigCellController''.
 *
 * Note: It is pretty difficult to test the actual cell implementation.
 * Therefore, logic has been extracted into the controller class which is
 * more test-friendly.
 */
class TestConfigCellController extends JUnitSuite with EasyMockSugar {
  /** A mock for a change handler. */
  private var changeHandler: TreeConfigurationChangeHandler = _

  /** The controller to be tested. */
  private var controller: ConfigCellController = _

  import TestConfigCellController._

  @Before def setUp() {
    changeHandler = mock[TreeConfigurationChangeHandler]
    controller = new ConfigCellController(changeHandler)
  }

  /**
   * Tests whether an edit operation can be started.
   */
  @Test def testHandleStartEdit() {
    val view = new Label("someText")
    val cellData = createCellData(view)
    controller handleStartEdit cellData
    assertEquals("Wrong text in field", NodeName, cellData.editField.getText)
    assertNull("Text not cleared", view.getText)
    assertEquals("Wrong graphics", cellData.editField, view.getGraphic)
    assertTrue("No selection", cellData.editField.getSelection.getLength > 0)
  }

  /**
   * Tests startEdit() if no item data is available. In this case, the text for
   * the edit field has to be adapted.
   */
  @Test def testHandleStartEditNoData() {
    val view = new Label("someText")
    val cellData = createCellData(view).copy(data = null)
    controller handleStartEdit cellData
    assertEquals("Wrong content of edit field", "", cellData.editField.getText)
  }

  /**
   * Tests whether an edit operation can be canceled.
   */
  @Test def testHandleCancelEdit() {
    val graphics = mock[javafx.scene.Node]
    val view = new Label("A text to be reset")
    val cellData = createCellData(view)
    cellData.treeItem setGraphic graphics
    controller handleCancelEdit cellData
    assertSame("Wrong graphic", graphics, view.getGraphic)
    assertEquals("Wrong text", NodeName, view.getText)
  }

  /**
   * Tests whether an item can be updated to empty data.
   */
  @Test def testHandleUpdateItemEmpty() {
    val view = new Label
    view setText "Some text"
    view setGraphic mock[javafx.scene.Node]
    controller.handleUpdateItem(createCellData(view), true, true)
    assertNull("Got a text", view.getText)
    assertNull("Got a graphic", view.getGraphic)
  }

  /**
   * Tests handleUpdateItem() if editing mode is active.
   */
  @Test def testHandleUpdateItemEditing() {
    val view = new Label("someText")
    val cellData = createCellData(view)
    controller.handleUpdateItem(cellData, false, true)
    assertEquals("Wrong text in field", NodeName, cellData.editField.getText)
    assertNull("Text not cleared", view.getText)
    assertEquals("Wrong graphics", cellData.editField, view.getGraphic)
  }

  /**
   * Tests handleUpdateItem() if new data is to be set.
   */
  @Test def testHandleUpdateItemNewData() {
    val graphics = mock[javafx.scene.Node]
    val view = new Label("A text to be reset")
    val cellData = createCellData(view)
    cellData.treeItem setGraphic graphics
    controller.handleUpdateItem(cellData, false, false)
    assertSame("Wrong graphic", graphics, view.getGraphic)
    assertEquals("Wrong text", NodeName, view.getText)
  }

  /**
   * Tests committing an edit operation.
   */
  @Test def testHandleCommitEdit() {
    EasyMock.expect(changeHandler.changeNodeName(Node, NodeName)).andReturn(true)
    val cellData = createCellData(new Label)
    cellData.editField setText NodeName
    whenExecuting(changeHandler) {
      assertEquals("Wrong updated item", Item, controller.handleCommitEdit(cellData))
    }
  }
}
