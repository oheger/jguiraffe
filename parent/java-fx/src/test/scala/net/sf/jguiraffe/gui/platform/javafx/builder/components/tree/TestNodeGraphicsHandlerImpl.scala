/*
 * Copyright 2006-2022 The JGUIraffe Team.
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.scalatest.junit.JUnitSuite
import org.scalatest.easymock.EasyMockSugar

import javafx.scene.Node
import net.sf.jguiraffe.gui.builder.components.tags.TreeIconHandler

/**
 * The companion object for ''TestNodeGraphicsHandlerImpl''
 */
object TestNodeGraphicsHandlerImpl {
  /** A default icon name. */
  private val IconName = "TestIcon"

  /** A mock for a configuration node. */
  private var ConfigNode: ConfigurationNode = _

  /** A mock for a graphic. */
  private var Graphic: Node = _

  @BeforeClass def setUpBeforeClass() {
    ConfigNode = EasyMock.createMock(classOf[ConfigurationNode])
    Graphic = EasyMock.createMock(classOf[Node])
    EasyMock.replay(ConfigNode, Graphic)
  }

  /**
   * Returns a map with icons.
   * @return the map with icons
   */
  private def iconMap(): java.util.Map[String, Object] = {
    val icons = new java.util.HashMap[String, Object]
    icons.put(IconName, Graphic)
    icons
  }
}

/**
 * Test class for ''NodeGraphicsHandlerImpl''.
 */
class TestNodeGraphicsHandlerImpl extends JUnitSuite with EasyMockSugar {
  import TestNodeGraphicsHandlerImpl._

  /** A mock for the tree icon handler. */
  private var iconHandler: TreeIconHandler = _

  /** The handler to be tested. */
  private var handler: NodeGraphicsHandlerImpl = _

  @Before def setUp() {
    iconHandler = mock[TreeIconHandler]
    handler = new NodeGraphicsHandlerImpl(iconHandler, iconMap())
  }

  /**
   * Tests whether an existing icon can be retrieved.
   */
  @Test def testIconExisting1() {
    EasyMock.expect(iconHandler.getIconName(ConfigNode, false, false)).andReturn(IconName)
    whenExecuting(iconHandler) {
      assertSame("Wrong graphic", Graphic, handler.graphicsFor(ConfigNode, false, false))
    }
  }

  /**
   * Tests whether an existing icon can be retrieved, but with a different
   * flag combination.
   */
  @Test def testIconExisting2() {
    EasyMock.expect(iconHandler.getIconName(ConfigNode, true, true)).andReturn(IconName)
    whenExecuting(iconHandler) {
      assertSame("Wrong graphic", Graphic, handler.graphicsFor(ConfigNode, true, true))
    }
  }

  /**
   * Tests whether a non-existing name is handled gracefully.
   */
  @Test def testIconNonExisting() {
    EasyMock.expect(iconHandler.getIconName(ConfigNode, true, true))
      .andReturn(IconName + "_NonExisting")
    whenExecuting(iconHandler) {
      assertNull("Got a graphic", handler.graphicsFor(ConfigNode, true, true))
    }
  }
}
