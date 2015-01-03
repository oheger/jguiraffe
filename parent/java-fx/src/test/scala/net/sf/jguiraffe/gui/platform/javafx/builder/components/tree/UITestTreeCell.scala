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
package net.sf.jguiraffe.gui.platform.javafx.builder.components.tree

import org.loadui.testfx.GuiTest
import javafx.scene.{Node, Parent}
import org.apache.commons.configuration.HierarchicalConfiguration
import javafx.scene.control.{ContentDisplay, TreeItem, TreeView}
import org.apache.commons.configuration.tree.ConfigurationNode
import net.sf.jguiraffe.gui.builder.components.model.{TreeModelChangeListener,
TreeConfigurationChangeHandler}
import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import org.junit.{Ignore, Test}
import org.junit.Assert._
import javafx.scene.input.KeyCode

/**
 * UI test class for testing functionality of the custom tree cell implementation.
 * This functionality, especially the support for edit operations, is very hard to
 * test using standard unit tests.
 */
class UITestTreeCell extends GuiTest {

  import GuiTest._

  /** Constant for the configuration sub tree. */
  private val Prefix = "preferences"

  /** Constant for a test setting accessed by the tests. */
  private val Setting = "color"

  /** The configuration serving as tree model. */
  private var treeModel: HierarchicalConfiguration = _

  /**
   * Sets up this test case and returns the UI control to be tested.
   * This is a tree view containing cells of the test class.
   * @return the test table
   */
  override def getRootNode: Parent = {
    treeModel = createConfiguration()
    createTreeView(treeModel)
  }

  /**
   * Creates a configuration instance serving as tree model.
   */
  private def createConfiguration(): HierarchicalConfiguration = {
    val config = new HierarchicalConfiguration
    config.getRootNode setName Prefix
    config.addProperty("user1.color", "blue")
    config.addProperty("user1.music", "rock")
    config.addProperty("user1.movie", "Science Fiction")
    config.addProperty("user2.color", "red")
    config
  }

  /**
   * Creates the tree view to be tested.
   * @param config the configuration acting as tree model
   * @return the test tree view
   */
  private def createTreeView(config: HierarchicalConfiguration): TreeView[ConfigNodeData] = {
    val tree = new TreeView[ConfigNodeData](createRootItem(config))
    tree setEditable true
    val changeHandler = new TreeConfigurationChangeHandler(config, new TreeModelChangeListener {
      override def treeModelChanged(node: ConfigurationNode) {}
    })
    tree setCellFactory JavaFxTestHelper.functionToCallback(f => new ConfigNodeTreeCell(changeHandler))
    tree
  }

  /**
   * Creates the tree item for the tree's root node.
   * @param config the configuration acting as tree model
   * @return the root item of the tree
   */
  private def createRootItem(config: HierarchicalConfiguration): TreeItem[ConfigNodeData] =
    new ConfigTreeItem(config.getRootNode, createGraphicsHandler(), scala.collection.mutable.Map
      .empty)

  /**
   * Creates a dummy graphics handler. We do not use any icons in this test, so the
   * handler always returns '''null'''.
   * @return the graphics handler to be used for the test
   */
  private def createGraphicsHandler(): NodeGraphicsHandler =
    new NodeGraphicsHandler {
      override def graphicsFor(node: ConfigurationNode, expanded: Boolean,
                               leaf: Boolean): Node = null
    }

  /**
   * Helper method for expanding the initial tree structure.
   */
  private def expandTree() {
    doubleClick(Prefix)
    doubleClick("user1")
  }

  /**
   * Returns the cell with the given content or fails if it cannot be found.
   * @param content the content of the cell
   * @return the cell with this content
   */
  private def findCell(content: String): ConfigNodeTreeCell = {
    find[Node](content) match {
      case cell: ConfigNodeTreeCell =>
        cell
      case elem =>
        captureScreenshot()
        throw new AssertionError("Unexpected UI element: " + elem)
    }
  }

  /**
   * Starts edit mode in the given cell.
   * @param label the label of the cell to be edited
   */
  private def startEdit(label: String) {
    val cell = findCell(label)
    click(cell).click(cell)
  }

  /**
   * Tests whether the expected data is displayed in the cells.
   */
  @Test def testDisplay() {
    expandTree()
    findCell("user2")
    findCell("music")
  }

  /**
   * Tests whether a cell can be edited.
   */
  @Test def testEditCell() {
    expandTree()
    startEdit(Setting)
    val newContent = "mood"
    `type`(newContent)
    push(KeyCode.ENTER)
    findCell(newContent)
    val node = treeModel.getRootNode.getChild(0).getChild(0)
    assertEquals("Model not changed", newContent, node.getName)
  }

  /**
   * Tests whether an edit operation can be canceled.
   */
  @Test def testCancelEdit() {
    expandTree()
    startEdit(Setting)
    `type`("esc")
    push(KeyCode.ESCAPE)
    findCell(Setting)
    assertTrue("Key not found", treeModel containsKey "user1." + Setting)
  }

  /**
   * Tests whether a focus lost event is handled during an edit operation.
   */
  @Test def testEditWithFocusLost() {
    expandTree()
    startEdit(Setting)
    val newContent = "x"
    `type`(newContent)
    click("music")
    val cell = findCell(newContent)
    assertEquals("Wrong display", ContentDisplay.TEXT_ONLY, cell.getContentDisplay)
  }
}
