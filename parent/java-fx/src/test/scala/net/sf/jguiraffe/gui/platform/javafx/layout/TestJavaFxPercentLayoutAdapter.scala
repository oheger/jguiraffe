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
package net.sf.jguiraffe.gui.platform.javafx.layout

import java.awt.Rectangle

import org.easymock.EasyMock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.easymock.PowerMock
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.EasyMockSugar

import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.text.Text
import net.sf.jguiraffe.gui.layout.UnitSizeHandler

/**
 * Test class for ''JavaFxPercentLayoutAdapter''.
 */
@RunWith(classOf[PowerMockRunner])
@PrepareForTest(Array(classOf[Node]))
class TestJavaFxPercentLayoutAdapter extends JUnitSuite with EasyMockSugar {
  /** The number of children. */
  private val ComponentCount = 3

  /** A list with mock nodes representing components. */
  private var components: Array[Node] = _

  /** An array with test constraints. */
  private var constraints: Array[Object] = _

  /** The adapter to be tested. */
  private var adapter: JavaFxPercentLayoutAdapter = _

  @Before def setUp() {
    components = new Array(ComponentCount)
    constraints = new Array(ComponentCount)
    for (i <- 0 until ComponentCount) {
      components(i) = new Text("Text " + i)
      constraints(i) = "TestConstraint" + i
    }
    adapter = new JavaFxPercentLayoutAdapter(components, constraints)
  }

  /**
   * Tests whether the number of components can be determined.
   */
  @Test def testGetComponentCount() {
    assertEquals("Wrong number of components", ComponentCount,
      adapter.getComponentCount)
  }

  /**
   * Tests whether a specific component can be queried.
   */
  @Test def testGetComponent() {
    var idx = 0
    for (c <- components) {
      assertEquals("Wrong component at " + idx, c, adapter.getComponent(idx))
      idx += 1
    }
  }

  /**
   * Tests whether a specific constraints object can be queried.
   */
  @Test def testGetConstraints() {
    var idx = 0
    for (c <- constraints) {
      assertEquals("Wrong constraints at " + idx, c, adapter.getConstraints(idx))
      idx += 1
    }
  }

  /**
   * Creates a mock node with the specified content bias.
   * @param bias the content bias
   * @return the mock node
   */
  private def nodeWithBias(bias: Orientation): Node = {
    val node = mock[Node]
    EasyMock.expect(node.getContentBias).andReturn(bias)
    node
  }

  /**
   * Tests whether the preferred component width can be obtained for nodes with
   * horizontal bias.
   */
  @Test def testGetPreferredComponentWidthHorizontalBias() {
    val node = nodeWithBias(Orientation.HORIZONTAL)
    EasyMock.expect(node.prefWidth(-1)).andReturn(100)
    whenExecuting(node) {
      assertEquals("Wrong result", 100, adapter.getPreferredComponentSize(node, false))
    }
  }

  /**
   * Tests whether the preferred component width can be obtained for nodes with
   * no content bias.
   */
  @Test def testGetPreferredComponentWidthNoBias() {
    val node = nodeWithBias(null)
    EasyMock.expect(node.prefWidth(-1)).andReturn(100)
    whenExecuting(node) {
      assertEquals("Wrong result", 100, adapter.getPreferredComponentSize(node, false))
    }
  }

  /**
   * Tests whether the preferred component width can be obtained for nodes with
   * vertical bias.
   */
  @Test def testGetPreferredComponentWidthVerticalBias() {
    val node = nodeWithBias(Orientation.VERTICAL)
    val height = 50.0
    EasyMock.expect(node.prefHeight(-1)).andReturn(height)
    EasyMock.expect(node.prefWidth(height)).andReturn(100)
    whenExecuting(node) {
      assertEquals("Wrong result", 100, adapter.getPreferredComponentSize(node, false))
    }
  }

  /**
   * Tests whether the preferred component height can be obtained for nodes with
   * vertical bias.
   */
  @Test def testGetPreferredComponentHeightVerticalBias() {
    val node = nodeWithBias(Orientation.VERTICAL)
    EasyMock.expect(node.prefHeight(-1)).andReturn(100)
    whenExecuting(node) {
      assertEquals("Wrong result", 100, adapter.getPreferredComponentSize(node, true))
    }
  }

  /**
   * Tests whether the preferred component height can be obtained for nodes with
   * no bias.
   */
  @Test def testGetPreferredComponentHeightNoBias() {
    val node = nodeWithBias(null)
    EasyMock.expect(node.prefHeight(-1)).andReturn(100)
    whenExecuting(node) {
      assertEquals("Wrong result", 100, adapter.getPreferredComponentSize(node, true))
    }
  }

  /**
   * Tests whether the preferred component height can be obtained for nodes with
   * horizontal bias.
   */
  @Test def testGetPreferredComponentHeightHorizontalBias() {
    val node = nodeWithBias(Orientation.HORIZONTAL)
    val width = 150.0
    EasyMock.expect(node.prefWidth(-1)).andReturn(width)
    EasyMock.expect(node.prefHeight(width)).andReturn(100)
    whenExecuting(node) {
      assertEquals("Wrong result", 100, adapter.getPreferredComponentSize(node, true))
    }
  }

  /**
   * Tests whether the minimum component width can be obtained for nodes with
   * horizontal bias.
   */
  @Test def testGetMinimumComponentWidthHorizontalBias() {
    val node = nodeWithBias(Orientation.HORIZONTAL)
    EasyMock.expect(node.minWidth(-1)).andReturn(100)
    whenExecuting(node) {
      assertEquals("Wrong result", 100, adapter.getMinimumComponentSize(node, false))
    }
  }

  /**
   * Tests whether the minimum component width can be obtained for nodes with
   * no bias.
   */
  @Test def testGetMinimumComponentWidthNoBias() {
    val node = nodeWithBias(null)
    EasyMock.expect(node.minWidth(-1)).andReturn(100)
    whenExecuting(node) {
      assertEquals("Wrong result", 100, adapter.getMinimumComponentSize(node, false))
    }
  }

  /**
   * Tests whether the minimum component width can be obtained for nodes with
   * vertical bias.
   */
  @Test def testGetMinimumComponentWidthVerticalBias() {
    val node = nodeWithBias(Orientation.VERTICAL)
    val height = 50.0
    EasyMock.expect(node.minHeight(-1)).andReturn(height)
    EasyMock.expect(node.minWidth(height)).andReturn(100)
    whenExecuting(node) {
      assertEquals("Wrong result", 100, adapter.getMinimumComponentSize(node, false))
    }
  }

  /**
   * Tests whether the minimum component height can be obtained for nodes with
   * vertical bias.
   */
  @Test def testGetMinimumComponentHeightVerticalBias() {
    val node = nodeWithBias(Orientation.VERTICAL)
    EasyMock.expect(node.minHeight(-1)).andReturn(100)
    whenExecuting(node) {
      assertEquals("Wrong result", 100, adapter.getMinimumComponentSize(node, true))
    }
  }

  /**
   * Tests whether the minimum component height can be obtained for nodes with
   * no bias.
   */
  @Test def testGetMinimumComponentHeightNoBias() {
    val node = nodeWithBias(null)
    EasyMock.expect(node.minHeight(-1)).andReturn(100)
    whenExecuting(node) {
      assertEquals("Wrong result", 100, adapter.getMinimumComponentSize(node, true))
    }
  }

  /**
   * Tests whether the minimum component height can be obtained for nodes with
   * horizontal bias.
   */
  @Test def testGetMinimumComponentHeightHorizontalBias() {
    val node = nodeWithBias(Orientation.HORIZONTAL)
    val width = 150.0
    EasyMock.expect(node.minWidth(-1)).andReturn(width)
    EasyMock.expect(node.minHeight(width)).andReturn(100)
    whenExecuting(node) {
      assertEquals("Wrong result", 100, adapter.getMinimumComponentSize(node, true))
    }
  }

  /**
   * Tests whether a component's bounds can be set.
   */
  @Test def testSetBounds() {
    val node = PowerMock.createMock(classOf[Node])
    val rect = new Rectangle(10, 20, 100, 80)
    node.setLayoutX(rect.x)
    node.setLayoutY(rect.y)
    node.resize(rect.width, rect.height)
    PowerMock.replayAll()
    adapter.setComponentBounds(node, rect)
    PowerMock.verifyAll()
  }

  /**
   * Tests the size handler returned by the adapter.
   */
  @Test def testGetSizeHandler() {
    val sizeHandler = adapter.getSizeHandler
    assertTrue("Wrong size handler", sizeHandler.isInstanceOf[JavaFxUnitSizeHandler])
    assertSame("Multiple instances", sizeHandler, adapter.getSizeHandler)
  }

  /**
   * Tests whether a size handler can be passed to the constructor.
   */
  @Test def testGetSizeHandlerDefined() {
    val sizeHandler = PowerMock.createMock(classOf[UnitSizeHandler])
    adapter = new JavaFxPercentLayoutAdapter(components, constraints,
      Some(sizeHandler))
    assertSame("Wrong size handler", sizeHandler, adapter.getSizeHandler)
  }
}
