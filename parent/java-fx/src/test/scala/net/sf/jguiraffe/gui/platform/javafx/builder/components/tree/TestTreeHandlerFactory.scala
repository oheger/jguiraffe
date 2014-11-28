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

import javafx.scene.Node
import javafx.scene.control.{SelectionMode, TreeView}

import net.sf.jguiraffe.gui.builder.components.model.TreeConfigurationChangeHandler
import net.sf.jguiraffe.gui.builder.components.tags.{TreeIconHandler, TreeTag}
import net.sf.jguiraffe.gui.forms.ComponentHandler
import org.apache.commons.configuration.HierarchicalConfiguration
import org.apache.commons.configuration.tree.ConfigurationNode
import org.easymock.EasyMock
import org.junit.Assert.{assertEquals, assertFalse, assertSame, assertTrue}
import org.junit.{Before, Test}
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.EasyMockSugar

/**
 * The companion object for ''TestTreeHandlerFactory''.
 */
object TestTreeHandlerFactory {
  /** Constant for a component name. */
  private val Name = "TestTreeViewComponent"

  /** Constant for an icon name. */
  private val IconName = "icon1"

  /**
   * Helper method for obtaining the JavaFX tree view from the given component
   * handler.
   * @param handler the component handler
   * @return the managed tree view
   */
  private def tree(handler: ComponentHandler[Object]): TreeView[ConfigNodeData] =
    handler.getComponent.asInstanceOf[TreeView[ConfigNodeData]]

  /**
   * Helper method for converting a generic component handler to a JavaFX
   * tree handler.
   * @param handler the generic handler
   * @return the converted tree handler
   */
  private def treeHandler(handler: ComponentHandler[Object]): JavaFxTreeHandler =
    handler.asInstanceOf[JavaFxTreeHandler]
}

/**
 * Test class for ''TreeHandlerFactory''.
 */
class TestTreeHandlerFactory extends JUnitSuite with EasyMockSugar {
  import net.sf.jguiraffe.gui.platform.javafx.builder.components.tree.TestTreeHandlerFactory._

  /** The tag defining the tree to be created. */
  private var tag: TreeTag = _

  /** The factory to be tested. */
  private var factory: TreeHandlerFactory = _

  @Before def setUp() {
    tag = setUpTag()
    factory = new TreeHandlerFactory
  }

  /**
   * Creates a fully initialized tree tag instance.
   * @return the tag
   */
  private def setUpTag(): TreeTag = {
    val iconHandler = mock[TreeIconHandler]
    EasyMock.expect(
      iconHandler.getIconName(EasyMock.anyObject(classOf[ConfigurationNode]),
        EasyMock.anyBoolean(), EasyMock.anyBoolean()))
      .andReturn(IconName).anyTimes()
    EasyMock.replay(iconHandler)
    val tag = new TreeTag {
      override val getResolvedIconHandler = iconHandler
    }
    tag setTreeModel new HierarchicalConfiguration
    tag setName Name
    tag
  }

  /**
   * Checks whether the tree's selection model is correctly initialized.
   * @param multiFlag the multiple selection flag for the tag
   * @param expMode the expected selection mode for the model
   */
  private def checkSelectionMode(multiFlag: Boolean, expMode: SelectionMode) {
    tag setMultiSelection multiFlag
    val handler = factory.createTreeHandler(tag)
    val treeview = tree(handler)
    assertEquals("Wrong selection mode", expMode,
      treeview.getSelectionModel.getSelectionMode)
  }

  /**
   * Tests whether the tree's selection model is correctly initialized if
   * single selection mode is set.
   */
  @Test def testSingleSelectionMode() {
    checkSelectionMode(multiFlag = false, SelectionMode.SINGLE)
  }

  /**
   * Tests whether the tree's selection model is correctly initialized if
   * multiple selection is enabled.
   */
  @Test def testMultipleSelectionMode() {
    checkSelectionMode(multiFlag = true, SelectionMode.MULTIPLE)
  }

  /**
   * Checks whether the tree's edit flag is correctly set.
   */
  private def checkEditable(editFlag: Boolean) {
    tag setEditable editFlag
    assertEquals("Wrong edit flag", editFlag,
      tree(factory.createTreeHandler(tag)).isEditable)
  }

  /**
   * Tests the tree's edit flag if the tree cannot be edited.
   */
  @Test def testEditableFalse() {
    checkEditable(editFlag = false)
  }

  /**
   * Tests the tree's edit flag if the tree can be edited.
   */
  @Test def testEditableTrue() {
    checkEditable(editFlag = true)
  }

  /**
   * Checks whether the visibility of the root node can be specified.
   * @param rootVisible the visibility flag for the root node
   */
  private def checkShowRoot(rootVisible: Boolean) {
    tag setRootVisible rootVisible
    assertEquals("Wrong root visibility flag", rootVisible,
        tree(factory.createTreeHandler(tag)).isShowRoot)
  }

  /**
   * Tests whether the root node can be hidden.
   */
  @Test def testShowRootFalse() {
    checkShowRoot(rootVisible = false)
  }

  /**
   * Tests whether the root node can be displayed.
   */
  @Test def testShowRootTrue() {
    checkShowRoot(rootVisible = true)
  }

  /**
   * Tests whether the correct cell factory is set.
   */
  @Test def testCellFactory() {
    val treeview = tree(factory.createTreeHandler(tag))
    val cellFactory = treeview.getCellFactory
    cellFactory.call(treeview) match {
      case cell : ConfigNodeTreeCell =>
        val handler = cell.changeHandler
        assertEquals("Wrong configuration", tag.getTreeModel, handler.getConfiguration)
        assertTrue("Wrong change listener",
          handler.getModelChangeListener.isInstanceOf[FxThreadModelChangeListener])
      case other =>
        fail("Unexpected cell: " + other)
    }
  }

  /**
   * Tests whether the tree handler is sent an initial change event so it
   * initializes the item structure.
   */
  @Test def testInitialChangeNotification() {
    val handler = factory.createTreeHandler(tag)
    val th = treeHandler(handler)
    assertFalse("Item map not initialized", th.itemMap.isEmpty)
    val rootItem = th.itemMap(tag.getTreeModel.getRootNode)
    assertEquals("Wrong root item", rootItem, tree(handler).getRoot)
  }

  /**
   * Tests whether the correct component name is set for the handler.
   */
  @Test def testComponentName() {
    val handler = treeHandler(factory.createTreeHandler(tag))
    assertEquals("Wrong name", Name, handler.name)
  }

  /**
   * Tests whether a correct graphics handler is set.
   */
  @Test def testGraphicsHandler() {
    val icon1 = mock[Node]
    val icon2 = mock[Node]
    val node = mock[ConfigurationNode]
    tag addIcon (IconName, icon1)
    tag addIcon ("icon2", icon2)
    val handler = treeHandler(factory.createTreeHandler(tag))
    val gh = handler.graphicHandler.asInstanceOf[NodeGraphicsHandlerImpl]
    assertSame("Wrong icon handler", tag.getResolvedIconHandler, gh.iconHandler)
    assertSame("Wrong icon", icon1, gh.graphicsFor(node, expanded = false, leaf = false))
  }

  /**
   * Tests whether correct listeners have been registered at the configuration
   * serving as data model.
   */
  @Test def testConfigurationListener() {
    import scala.collection.JavaConversions._
    val handler = factory.createTreeHandler(tag)
    val listener = tag.getTreeModel.getConfigurationListeners
      .find(_.isInstanceOf[TreeConfigurationChangeHandler])
    val changeHandler = listener.get.asInstanceOf[TreeConfigurationChangeHandler]
    assertSame("Wrong configuration", tag.getTreeModel,
      changeHandler.getConfiguration)
    changeHandler.getModelChangeListener match {
      case l: FxThreadModelChangeListener =>
        assertSame("Wrong change listener", handler, l.listener)
      case other => fail("Unexpected listener: " + other)
    }
  }
}
