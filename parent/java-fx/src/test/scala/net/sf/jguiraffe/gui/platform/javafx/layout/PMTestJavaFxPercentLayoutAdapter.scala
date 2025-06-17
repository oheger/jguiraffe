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
package net.sf.jguiraffe.gui.platform.javafx.layout

import java.awt.Rectangle

import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.text.Text
import net.sf.jguiraffe.gui.layout.UnitSizeHandler
import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import org.easymock.EasyMock
import org.junit.Assert.{assertEquals, assertSame, assertTrue}
import org.junit.runner.RunWith
import org.junit.{Before, BeforeClass, Test}
import org.powermock.api.easymock.PowerMock
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.scalatestplus.easymock.EasyMockSugar
import org.scalatestplus.junit.JUnitSuite

/**
 * Test class for ''JavaFxPercentLayoutAdapter''.
 */
@RunWith(classOf[PowerMockRunner])
@PrepareForTest(Array(classOf[Node]))
class PMTestJavaFxPercentLayoutAdapter extends JUnitSuite with EasyMockSugar {
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
    * Initializes the bias property of the given node mock.
    *
    * @param node the node
    * @param bias the content bias
    * @tparam T the concrete node type
    * @return the updated node mock
    */
  private def initBias[T <: Node](node: T, bias: Orientation): T = {
    EasyMock.expect(node.getContentBias).andReturn(bias)
    node
  }

  /**
   * Creates a mock node with the specified content bias.
   * @param bias the content bias
   * @return the mock node
   */
  private def nodeWithBias(bias: Orientation): Node =
    initBias(mock[Node], bias)

  /**
   * Tests whether the preferred component width can be obtained for nodes with
   * horizontal bias.
   */
  @Test def testGetPreferredComponentWidthHorizontalBias() {
    val Width = 100
    val node = nodeWithBias(Orientation.HORIZONTAL)
    EasyMock.expect(node.prefWidth(-1)).andReturn(Width)
    whenExecuting(node) {
      assertEquals("Wrong result", Width, adapter.getPreferredComponentSize(node, vert = false))
    }
  }

  /**
    * Tests whether the preferred width of Labeled components with horizontal
    * bias is adjusted.
    */
  @Test def testGetPreferredLabeledWidthHorizontalBias(): Unit = {
    val Width = 128
    val labeled = new Label
    labeled.setWrapText(true)
    labeled.setPrefWidth(Width)
    assertEquals("Wrong result", Width + 1,
      adapter.getPreferredComponentSize(labeled, vert = false))
  }

  /**
   * Tests whether the preferred component width can be obtained for nodes with
   * no content bias.
   */
  @Test def testGetPreferredComponentWidthNoBias() {
    val node = nodeWithBias(null)
    EasyMock.expect(node.prefWidth(-1)).andReturn(100)
    whenExecuting(node) {
      assertEquals("Wrong result", 100, adapter.getPreferredComponentSize(node, vert = false))
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
      assertEquals("Wrong result", 100, adapter.getPreferredComponentSize(node, vert = false))
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
      assertEquals("Wrong result", 100, adapter.getPreferredComponentSize(node, vert = true))
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
      assertEquals("Wrong result", 100, adapter.getPreferredComponentSize(node, vert = true))
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
      assertEquals("Wrong result", 100, adapter.getPreferredComponentSize(node, vert = true))
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
      assertEquals("Wrong result", 100, adapter.getMinimumComponentSize(node, vert = false))
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
      assertEquals("Wrong result", 100, adapter.getMinimumComponentSize(node, vert = false))
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
      assertEquals("Wrong result", 100, adapter.getMinimumComponentSize(node, vert = false))
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
      assertEquals("Wrong result", 100, adapter.getMinimumComponentSize(node, vert = true))
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
      assertEquals("Wrong result", 100, adapter.getMinimumComponentSize(node, vert = true))
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
      assertEquals("Wrong result", 100, adapter.getMinimumComponentSize(node, vert = true))
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

object PMTestJavaFxPercentLayoutAdapter {
  @BeforeClass def setUpBeforeClass(): Unit = {
    JavaFxTestHelper.initPlatform()
  }
}
