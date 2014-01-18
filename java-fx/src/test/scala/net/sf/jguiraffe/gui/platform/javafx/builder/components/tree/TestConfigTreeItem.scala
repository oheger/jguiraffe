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

import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.mutable.Map

import org.apache.commons.configuration.tree.ConfigurationNode
import org.apache.commons.configuration.tree.DefaultConfigurationNode
import org.easymock.EasyMock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.scalatest.junit.JUnitSuite

import javafx.scene.Node

/**
 * The companion object for ''ConfigTreeItem''.
 */
object TestConfigTreeItem {
  /** A test graphics handler. */
  val GraphicsHandler = new NodeGraphicsHandlerTestImpl

  /** Constant for the name of the test node. */
  private val NodeName = "TestNode"

  /** Constant for the name prefix of a child node. */
  private val ChildNode = "ChildNode"

  /** Constant for the default number of child nodes. */
  private val ChildCount = 4

  /**
   * Creates a default test configuration node (without children).
   * @return the test node
   */
  private def createNode(): ConfigurationNode =
    new DefaultConfigurationNode(NodeName)

  /**
   * Adds default child nodes to the given parent node.
   * @param node the parent node
   * @return a reference to the passed in parent node
   */
  private def addChildNodes(node: ConfigurationNode): ConfigurationNode = {
    (1 to ChildCount) map {
      idx => new DefaultConfigurationNode(s"$ChildNode$idx")
    } foreach (node addChild _)
    node
  }

  /**
   * Creates a default test configuration with test children.
   * @return the test node
   */
  private def createNodeWithChildren(): ConfigurationNode =
    addChildNodes(createNode())

  /**
   * Checks whether the given item has the correct graphic.
   * @param item the item to check
   * @param expanded the expanded flag
   * @param leaf the leaf flag
   */
  private def checkGraphic(item: ConfigTreeItem, expanded: Boolean, leaf: Boolean) {
    assertSame("Wrong graphic", GraphicsHandler.graphicsFor(item.node,
      expanded, leaf), item.getGraphic)
  }
}

/**
 * Test class for ''ConfigTreeItem''.
 */
class TestConfigTreeItem extends JUnitSuite {
  import TestConfigTreeItem._

  /** The map storing all tree items. */
  private var itemMap: Map[ConfigurationNode, ConfigTreeItem] = _

  @Before def setUp() {
    itemMap = Map.empty
  }

  /**
   * Tests whether the item is added to the central map.
   */
  @Test def testAddToItemMap() {
    val node = createNode()
    val item = new ConfigTreeItem(node, GraphicsHandler, itemMap)
    assertEquals("Wrong number of items", 1, itemMap.size)
    assertSame("Wrong item", item, itemMap(node))
  }

  /**
   * Tests whether the correct graphic is set for a leaf node.
   */
  @Test def testGraphicLeaf() {
    val item = new ConfigTreeItem(createNode(), GraphicsHandler, itemMap)
    checkGraphic(item, false, true)
  }

  /**
   * Tests whether the correct graphic is set for a non-leaf node.
   */
  @Test def testGraphicNoLeaf() {
    val item = new ConfigTreeItem(createNodeWithChildren(), GraphicsHandler, itemMap)
    checkGraphic(item, false, false)
  }

  /**
   * Tests isLeaf if the expected result is true.
   */
  @Test def testIsLeafTrue() {
    val item = new ConfigTreeItem(createNode(), GraphicsHandler, itemMap)
    assertTrue("Not a leaf", item.isLeaf)
  }

  /**
   * Tests isLeaf if the expected result is false.
   */
  @Test def testIsLeafFalse() {
    val item = new ConfigTreeItem(createNodeWithChildren(), GraphicsHandler, itemMap)
    assertFalse("A leaf", item.isLeaf)
  }

  /**
   * Tests whether the leaf flag is cached after its initialization.
   */
  @Test def testIsLeafCached() {
    val node = createNode()
    val item = new ConfigTreeItem(node, GraphicsHandler, itemMap)
    assertTrue("Not a leaf", item.isLeaf)
    addChildNodes(node)
    assertTrue("No longer a leaf", item.isLeaf)
  }

  /**
   * Tests whether the item's children can be accessed.
   */
  @Test def testGetChildren() {
    val node = createNodeWithChildren()
    val item = new ConfigTreeItem(node, GraphicsHandler, itemMap)
    val children = item.getChildren
    assertEquals("Wrong number of children", ChildCount, children.size)
    node.getChildren foreach { c =>
      val childItem = itemMap(c)
      assertSame("Wrong parent", item, childItem.getParent)
      assertTrue("Child not found", children.contains(childItem))
    }
  }

  /**
   * Tests whether the item's children are cached after initialization.
   */
  @Test def testGetChildrenCached() {
    val node = createNode()
    val item = new ConfigTreeItem(node, GraphicsHandler, itemMap)
    item.getChildren
    addChildNodes(node)
    assertTrue("Children were changed", item.getChildren.isEmpty)
  }

  /**
   * Tests a sync operation on an uninitialized item. This should not be
   * propagated to child nodes.
   */
  @Test def testResyncUninitialized() {
    val node = new DefaultConfigurationNode(NodeName) {
      override def getChildren() = {
        throw new UnsupportedOperationException("Unexpected method call!")
      }
    }
    val item = new ConfigTreeItem(node, GraphicsHandler, itemMap)
    item.resync()
  }

  /**
   * Tests whether a sync operation changes the value of the tree item object.
   */
  @Test def testResyncNewNodeData() {
    val node = createNode()
    val item = new ConfigTreeItem(node, GraphicsHandler, itemMap)
    val value = item.getValue
    node setName (NodeName + "other")
    item.resync()
    assertNotSame("Value was not changed", value, item.getValue)
    assertSame("Wrong configuration node", node, item.getValue.node)
  }

  /**
   * Tests whether a sync operation can change the item's graphic.
   */
  @Test def testResyncNewGraphic() {
    val node = createNode()
    val item = new ConfigTreeItem(node, GraphicsHandler, itemMap)
    addChildNodes(node)
    item.resync()
    checkGraphic(item, false, false)
  }

  /**
   * Tests whether a new child node is detected by a sync operation.
   */
  @Test def testResyncNewChild() {
    val node = createNodeWithChildren()
    val item = new ConfigTreeItem(node, GraphicsHandler, itemMap)
    item.getChildren // initialize
    val newChild = new DefaultConfigurationNode(ChildNode + "_NEW")
    node addChild newChild

    item.resync()
    val children = item.getChildren
    assertEquals("Wrong number of child nodes", ChildCount + 1, children.size)
    val newItem = children(ChildCount)
    assertSame("Wrong node", newChild, newItem.getValue.node)
    assertSame("Wrong parent", item, newItem.getParent)
    assertSame("Map not updated", newItem, itemMap(newChild))
  }

  /**
   * Tests whether a removed child is detected by a sync operation.
   */
  @Test def testResyncRemovedChild() {
    val node = createNodeWithChildren()
    val item = new ConfigTreeItem(node, GraphicsHandler, itemMap)
    val removedItem = item.getChildren.get(0)
    val removedChild = node.getChild(0)
    node removeChild removedChild

    item.resync()
    val children = item.getChildren
    assertEquals("Wrong number of child nodes", ChildCount - 1, children.size)
    assertFalse("Still found removed item", children.contains(removedItem))
    assertFalse("Still in map", itemMap.contains(removedChild))
  }

  /**
   * Tests whether a sync operation is propagated to child nodes.
   */
  @Test def testResyncRecursively() {
    val node = createNodeWithChildren()
    val item = new ConfigTreeItem(node, GraphicsHandler, itemMap)
    val checkItem = item.getChildren.get(0)
    val oldValue = checkItem.getValue

    item.resync()
    assertNotSame("Value was not changed", oldValue, checkItem.getValue)
  }

  /**
   * Tests removeFromItemMap() if the node has not yet been initialized. This
   * should not have any effect on child items.
   */
  @Test def testRemoveFromItemMapUninitialized() {
    val node = createNodeWithChildren()
    val child = node.getChild(0)
    val item = new ConfigTreeItem(node, GraphicsHandler, itemMap)
    val childItem = new ConfigTreeItem(child, GraphicsHandler, itemMap)
    item.removeFromItemMap()
    assertEquals("Child item not found", childItem, itemMap(child))
  }

  /**
   * Tests whether removeFromItemMap() also removes child node entries.
   */
  @Test def testRemoveFromItemMapRecursively() {
    val node = createNodeWithChildren()
    val item = new ConfigTreeItem(node, GraphicsHandler, itemMap)
    item.getChildren // initialize
    assertTrue("Not enough entries in itemMap", itemMap.size > 1)
    item.removeFromItemMap()
    assertTrue("Still got items", itemMap.isEmpty)
  }
}

/**
 * A test implementation of the graphics handler trait.
 *
 * This implementation returns unique mock objects for each configuration node
 * and state combination. This makes it possible to verify whether correct
 * graphics were set.
 */
class NodeGraphicsHandlerTestImpl extends NodeGraphicsHandler {
  private var graphics = Map.empty[ConfigurationNode, Array[Node]]

  def graphicsFor(node: ConfigurationNode, expanded: Boolean, leaf: Boolean): Node = {
    if (!graphics.contains(node)) {
      graphics += node -> (new Array[Node](4))
    }
    val mockNodes = graphics(node)
    val idx = stateIndex(expanded, leaf)
    if (mockNodes(idx) == null) {
      mockNodes(idx) = EasyMock.createMock(classOf[Node])
      EasyMock.replay(mockNodes(idx))
    }
    mockNodes(idx)
  }

  /**
   * Determines an index based on the given state flags.
   * @param expanded the expanded flag
   * @param leaf the leaf flag
   * @return the index of this state in the array of mock nodes
   */
  private def stateIndex(expanded: Boolean, leaf: Boolean): Int = {
    def flagIndex(f: Boolean): Int = if (f) 1 else 0

    (flagIndex(expanded) << 1) + flagIndex(leaf)
  }
}
