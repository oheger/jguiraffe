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

import scala.collection.mutable.Map
import org.apache.commons.configuration.HierarchicalConfiguration
import org.apache.commons.configuration.tree.ConfigurationNode
import org.apache.commons.configuration.tree.DefaultConfigurationNode
import org.easymock.EasyMock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.EasyMockSugar
import javafx.scene.control.SelectionMode
import javafx.scene.control.TreeView
import net.sf.jguiraffe.gui.builder.components.model.TreeHandler
import net.sf.jguiraffe.gui.builder.components.model.TreeNodePath
import javafx.scene.control.TreeItem

/**
 * The companion object for ''JavaFxTreeHandler''.
 */
object TestJavaFxTreeHandler {
  /** An array with classes to be used as keys for configuration nodes. */
  private val Keys = Array(classOf[TreeView[_]], classOf[TreeItem[_]],
    classOf[JavaFxTreeHandler], classOf[ConfigNodeData],
    classOf[ConfigNodeTreeCell], classOf[TreeHandler],
    classOf[HierarchicalConfiguration])

  /** A mock for the graphic handler. */
  private var GraphicsHandler: NodeGraphicsHandler = _

  @BeforeClass def setUpOnce() {
    GraphicsHandler = EasyMock.createNiceMock(classOf[NodeGraphicsHandler])
    EasyMock.expect(GraphicsHandler.graphicsFor(
      EasyMock.anyObject(classOf[ConfigurationNode]),
      EasyMock.anyBoolean(), EasyMock.anyBoolean)).andReturn(null)
    EasyMock.replay(GraphicsHandler)
  }

  /**
   * Creates a test configuration that can serve as model for a tree view.
   * @return the test configuration
   */
  private def createConfiguration(): HierarchicalConfiguration = {
    val config = new HierarchicalConfiguration
    Keys foreach { c => config.addProperty(c.getName, c.getSimpleName) }
    config
  }

  /**
   * Creates a test tree view instance.
   * @param multiSelect flag whether multiple selection is enabled
   * @return the test tree view
   */
  private def createTree(multiSelect: Boolean = false): TreeView[ConfigNodeData] = {
    val tree = new TreeView[ConfigNodeData]
    if (multiSelect) {
      tree.getSelectionModel setSelectionMode SelectionMode.MULTIPLE
    }
    tree
  }

  /**
   * Creates a test handler instance.
   * @param multiSelect flag whether multiple selection is enabled
   * @return the test handler instance
   */
  private def createHandler(multiSelect: Boolean = false): JavaFxTreeHandler =
    new JavaFxTreeHandler(createTree(multiSelect), createConfiguration(),
      GraphicsHandler, Map.empty)

  /**
   * Creates a test handler instance and initializes its root node.
   * @param multiSelect flag whether multiple selection is enabled
   * @return the test handler instance
   */
  private def createInitializedHandler(multiSelect: Boolean = false): JavaFxTreeHandler = {
    val handler = createHandler(multiSelect)
    handler treeModelChanged handler.model.getRootNode
    handler
  }

  /**
   * Obtains the tree view component from the passed in handler.
   * @param handler the handler
   * @return the wrapped tree view
   */
  private def getTree(handler: JavaFxTreeHandler): TreeView[ConfigNodeData] =
    handler.component.asInstanceOf[TreeView[ConfigNodeData]]

  /**
   * Obtains a ''ConfigurationNode'' object for a specific key from the
   * handler's model.
   * @param handler the tree handler
   * @param key the desired key in the tree model
   * @return the corresponding configuration node
   */
  private def getNode(handler: JavaFxTreeHandler, key: String): ConfigurationNode = {
    handler.model.getExpressionEngine.query(handler.model.getRootNode, key).get(0)
  }

  /**
   * Obtains the ''ConfigurationNode'' object for a specific class serving as
   * key from the handler's model.
   * @param handler the tree handler
   * @param cls the target class for constructing the key
   * @return the corresponding configuration node
   */
  private def getNode(handler: JavaFxTreeHandler, cls: Class[_]): ConfigurationNode =
    getNode(handler, cls.getName)

  /**
   * Obtains a ''TreeNodePath'' object for a specific class serving as key
   * from the handler's model.
   * @param handler the tree handler
   * @param cls the target class for constructing the key
   * @return the ''TreeNodePath'' for this class
   */
  private def getPath(handler: JavaFxTreeHandler, cls: Class[_]): TreeNodePath =
    new TreeNodePath(getNode(handler, cls))

  /**
   * Creates a path to a target node which is not part of the test tree's
   * model.
   * @return the invalid path
   */
  private def createInvalidPath(): TreeNodePath = {
    val node: ConfigurationNode = new DefaultConfigurationNode
    val nodes = java.util.Collections.singletonList(node)
    new TreeNodePath(nodes)
  }

  /**
   * Helper method for testing whether the selection in the managed tree is the
   * same as the passed in paths.
   * @param handler the tree handler
   * @param expSel a sequence with the expected selected paths
   */
  private def checkSelection(handler: JavaFxTreeHandler, expSel: TreeNodePath*) {
    val selItems = getTree(handler).getSelectionModel.getSelectedItems
    assertEquals("Wrong number of selected items", expSel.size, selItems.size)
    expSel foreach { p =>
      val item = handler.itemMap(p.getTargetNode)
      assertTrue("Item not found: " + item, selItems.contains(item))
    }
  }
}

/**
 * Test class for ''JavaFxTreeHandler''.
 */
class TestJavaFxTreeHandler extends JUnitSuite with EasyMockSugar {
  import TestJavaFxTreeHandler._

  /**
   * Tests the handler's type if only single selection is active.
   */
  @Test def testGetTypeSingleSelection() {
    val handler = createHandler()
    assertEquals("Wrong type", classOf[TreeNodePath], handler.getType)
  }

  /**
   * Tests the handler's type if multiple selection is enabled.
   */
  @Test def testGetTypeMultiSelection() {
    val handler = createHandler(true)
    assertEquals("Wrong type", classOf[Array[TreeNodePath]], handler.getType)
  }

  /**
   * Tests whether the initial tree item structure is created by the first
   * model change event.
   */
  @Test def testTreeModelChangedInitial() {
    val handler = createHandler()
    handler treeModelChanged handler.model.getRootNode
    val rootItem = getTree(handler).getRoot.asInstanceOf[ConfigTreeItem]
    assertSame("Wrong associated node", handler.model.getRootNode, rootItem.node)
  }

  /**
   * Tests whether a complete change in the associated data model is detected
   * and a new tree item structure is created in this case.
   */
  @Test def testTreeModelChangedNewRootNode() {
    val handler = createHandler()
    handler treeModelChanged handler.model.getRootNode
    val oldRoot = getTree(handler).getRoot
    val newRootNode = new DefaultConfigurationNode("new")
    handler.model setRootNode newRootNode
    handler treeModelChanged newRootNode
    val rootItem = getTree(handler).getRoot.asInstanceOf[ConfigTreeItem]
    assertNotSame("Root item not changed", oldRoot, rootItem)
    assertSame("Wrong associated node", newRootNode, rootItem.node)
  }

  /**
   * Tests whether a change event on a node which is not yet initialized is
   * ignored.
   */
  @Test def testTreeModelChangedNotInitialized() {
    val handler = createHandler()
    handler treeModelChanged handler.model.getRootNode
    handler treeModelChanged getNode(handler, Keys(0))
    assertEquals("Initialization was done", 1, handler.itemMap.size)
  }

  /**
   * Tests that a sync operation is triggered on a model change event.
   */
  @Test def testTreeModelChangedResync() {
    val item = mock[ConfigTreeItem]
    item.resync()
    whenExecuting(item) {
      val handler = createHandler()
      val node = getNode(handler, Keys(1))
      handler treeModelChanged handler.model.getRootNode
      handler.itemMap += node -> item
      handler treeModelChanged node
    }
  }

  /**
   * Tests whether a single path can be selected.
   */
  @Test def testSetSelectedPath() {
    val handler = createInitializedHandler()
    val path = getPath(handler, Keys(3))
    handler setSelectedPath path
    checkSelection(handler, path)
  }

  /**
   * Tests whether multiple paths can be selected.
   */
  @Test def testSetSelectedPaths() {
    val handler = createInitializedHandler(true)
    val paths = (1 to 3) map { idx => getPath(handler, Keys(idx)) }
    handler setSelectedPaths paths.toArray
    checkSelection(handler, paths: _*)
  }

  /**
   * Tests whether the old selection is cleared when setting a new one.
   */
  @Test def testSetSelectionClearsOldSelection() {
    val handler = createInitializedHandler(true)
    val path1 = getPath(handler, Keys(1))
    val path2 = getPath(handler, Keys(Keys.length - 1))
    handler setSelectedPath path1
    handler setSelectedPath path2
    checkSelection(handler, path2)
  }

  /**
   * Tests whether a path can be added to an existing selection.
   */
  @Test def testAddSelectedPath() {
    val handler = createInitializedHandler(true)
    val path1 = getPath(handler, Keys(1))
    val path2 = getPath(handler, Keys(Keys.length - 1))
    handler setSelectedPath path1
    handler addSelectedPath path2
    checkSelection(handler, path1, path2)
  }

  /**
   * Tests whether the tree's single selection can be queried.
   */
  @Test def testGetSelectedPath() {
    val handler = createInitializedHandler()
    val path = getPath(handler, Keys(3))
    handler setSelectedPath path
    assertEquals("Wrong selected path", path, handler.getSelectedPath)
  }

  /**
   * Tests getSelectedPath() if the tree does not have a selection.
   */
  @Test def testGetSelectedPathNoSelection() {
    val handler = createInitializedHandler()
    assertNull("Got a selected path", handler.getSelectedPath)
  }

  /**
   * Tests whether all selected paths can be queried.
   */
  @Test def testGetSelectedPaths() {
    val handler = createInitializedHandler(true)
    val path1 = getPath(handler, Keys(0))
    val path2 = getPath(handler, Keys(2))
    handler setSelectedPath path1
    handler addSelectedPath path2
    val sel = handler.getSelectedPaths
    assertEquals("Wrong number of selected paths", 2, sel.size)
    assertTrue("Wrong selection", List(path1, path2) forall (sel.contains(_)))
  }

  /**
   * Tests querying the selected paths if there is no selection.
   */
  @Test def testGetSelectedPathsNoSelection() {
    val handler = createInitializedHandler(true)
    assertTrue("Got selected paths", handler.getSelectedPaths.isEmpty)
  }

  /**
   * Tests whether the tree's selection can be cleared.
   */
  @Test def testClearSelection() {
    val handler = createInitializedHandler(true)
    handler setSelectedPath getPath(handler, Keys(1))
    handler addSelectedPath getPath(handler, Keys(3))
    handler.clearSelection()
    assertTrue("Got a selection",
      getTree(handler).getSelectionModel.getSelectedItems.isEmpty)
    assertNull("Got a selected item",
      getTree(handler).getSelectionModel.getSelectedItem)
  }

  /**
   * Tries to select a node which does not belong to the tree. This should have
   * no effect.
   */
  @Test def testSetSelectedPathInvalid() {
    val path = createInvalidPath()
    val handler = createInitializedHandler()
    handler setSelectedPath path
    assertNull("Got a selected item",
      getTree(handler).getSelectionModel.getSelectedItem)
  }

  /**
   * Tests whether the handler's data can be queried if there is no selection.
   */
  @Test def testGetDataSingleNoSelection() {
    val handler = createInitializedHandler()
    assertNull("Got data", handler.getData)
  }

  /**
   * Tests whether the handler's data can be queried in multiple selection mode
   * if there is no selection.
   */
  @Test def testGetDataMultiNoSelection() {
    val handler = createInitializedHandler(true)
    assertNull("Got data", handler.getData)
  }

  /**
   * Tests getData() if only single selection is enabled.
   */
  @Test def testGetDataSingleSelection() {
    val handler = createInitializedHandler()
    val path = getPath(handler, Keys(2))
    handler setSelectedPath path
    assertEquals("Wrong data", path, handler.getData)
  }

  /**
   * Tests getData() if multiple selections are allowed.
   */
  @Test def testGetDataMultiSelection() {
    val handler = createInitializedHandler(true)
    val paths = Array(getPath(handler, Keys(0)), getPath(handler, Keys(3)))
    handler setSelectedPaths paths
    val data = handler.getData.asInstanceOf[Array[TreeNodePath]]
    assertEquals("Wrong number of selected paths", paths.size, data.size)
    assertTrue("No all paths found", data forall (paths contains))
  }

  /**
   * Tests whether a single selection can be set using setData().
   */
  @Test def testSetDataSinglePath() {
    val handler = createInitializedHandler()
    handler setSelectedPath getPath(handler, Keys(0))
    val path = getPath(handler, Keys(1))
    handler setData path
    checkSelection(handler, path)
  }

  /**
   * Tests whether multiple selected paths can be set using setData().
   */
  @Test def testSetDataMultiplePaths() {
    val handler = createInitializedHandler(true)
    handler setSelectedPath getPath(handler, Keys(0))
    val paths = Array(getPath(handler, Keys(1)), getPath(handler, Keys(2)))
    handler setData paths
    checkSelection(handler, paths: _*)
  }

  /**
   * Tests that all other input to setData() just clears the selection.
   */
  @Test def testSetDataOther() {
    val handler = createInitializedHandler(true)
    handler setSelectedPath getPath(handler, Keys(0))
    handler setData null
    checkSelection(handler)
  }

  /**
   * Tests whether a specific node can be expanded.
   */
  @Test def testExpand() {
    val handler = createInitializedHandler()
    val path = getPath(handler, Keys(4))
    handler expand path
    val item = handler.itemMap(path.getTargetNode)
    assertTrue("Not expanded", item.isExpanded)
    assertTrue("Parent not expanded", item.getParent.isExpanded)
  }

  /**
   * Tests expand() if the passed in path is invalid. We can only test that this
   * does not cause an exception.
   */
  @Test def testExpandInvalid() {
    val handler = createInitializedHandler()
    handler expand createInvalidPath()
    assertFalse("Root node expanded", getTree(handler).getRoot.isExpanded)
  }

  /**
   * Tests whether a specific node can be collapsed.
   */
  @Test def testCollapse() {
    val handler = createInitializedHandler()
    val path = getPath(handler, Keys(2))
    handler expand path
    handler collapse path
    val item = handler.itemMap(path.getTargetNode)
    assertFalse("Item is expanded", item.isExpanded)
    assertTrue("Parent was collapsed", item.getParent.isExpanded)
  }

  /**
   * Tests collapse() if the passed in path is not yet initialized. In this
   * case, no action is necessary.
   */
  @Test def testCollapseNotInitialized() {
    val handler = createInitializedHandler()
    val path = getPath(handler, Keys(2))
    handler collapse path
    assertTrue("Too many items initialized", handler.itemMap.size < 2)
  }

  /**
   * Tests collapse() if the passed in path is invalid. We can only test that
   * this does not cause an exception.
   */
  @Test def testCollapseInvalid() {
    val handler = createInitializedHandler()
    handler collapse createInvalidPath()
  }
}
