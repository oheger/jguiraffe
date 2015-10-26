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

import javafx.scene.control.Label

import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import org.easymock.EasyMock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.{BeforeClass, Before, Test}
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.EasyMockSugar

import javafx.scene.Node
import javafx.scene.layout.{StackPane, BorderPane, FlowPane, Pane}
import javafx.scene.text.Font
import javafx.scene.text.Text
import net.sf.jguiraffe.gui.builder.components.FormBuilderException
import net.sf.jguiraffe.gui.layout.PercentLayout
import net.sf.jguiraffe.gui.layout.UnitSizeHandler

object TestContainerWrapper {
  @BeforeClass def setUpOnce(): Unit = {
    JavaFxTestHelper.initPlatform()
  }
}

/**
 * Test class for ''ContainerWrapper''.
 */
class TestContainerWrapper extends JUnitSuite with EasyMockSugar {
  /** The wrapper to be tested. */
  private var wrapper: ContainerWrapper = _

  @Before def setUp() {
    wrapper = new ContainerWrapper
  }

  /**
   * Checks whether the given container has the expected children.
   * @param pane the container to be checked
   * @param expChildren the expected children
   */
  private def checkChildren(pane: Pane, expChildren: Node*) {
    assertEquals("Wrong number of children", expChildren.size,
      pane.getChildren.size)
    assertTrue("Child not found", expChildren.forall(pane.getChildren.contains(_)))
  }

  /**
   * Tests the default size handler.
   */
  @Test def testDefaultSizeHandler() {
    assertFalse("Got a size handler", wrapper.sizeHandler.isDefined)
  }

  /**
   * Tests addComponent() if the component is itself a wrapper.
   */
  @Test def testAddComponentWrapper() {
    val comp = mock[ContainerWrapper]
    val pane = new BorderPane
    EasyMock.expect(comp.createContainer()).andReturn(pane)
    whenExecuting(comp) {
      wrapper.addComponent(comp, null)
      val createdPane = wrapper.createContainer()
      assertTrue("Wrong pane class", createdPane.isInstanceOf[FlowPane])
      checkChildren(createdPane, pane)
    }
  }

  /**
   * Tests addComponent() if a plain node is added.
   */
  @Test def testAddComponentNode() {
    val node = new Text("Test")
    wrapper.addComponent(node, null)
    checkChildren(wrapper.createContainer(), node)
  }

  /**
   * Tests whether an unsupported component is handled correctly by
   * addComponent().
   */
  @Test(expected = classOf[FormBuilderException])
  def testAddComponentUnsupported() {
    wrapper.addComponent(this, null)
  }

  /**
   * Tests whether a default font is used if none is set.
   */
  @Test def testContainerFontUndefined() {
    assertFalse("Got an initial font", wrapper.font.isDefined)
    assertEquals("Wrong container default font", Font.getDefault,
      wrapper.getContainerFont)
  }

  /**
   * Tests whether the container can be assigned a font.
   */
  @Test def testContainerFontDefined() {
    val font = new Font(24)
    wrapper.font = Some(font)
    assertSame("Wrong container font", font, wrapper.getContainerFont)
  }

  /**
   * Tests fromObject() if a type cast is possible.
   */
  @Test def testFromObjectValid() {
    assertSame("Wrong result", wrapper, ContainerWrapper.fromObject(wrapper))
  }

  /**
   * Tests fromObject() for invalid input.
   */
  @Test(expected = classOf[IllegalArgumentException])
  def testFromObjectInvalid() {
    ContainerWrapper.fromObject(this)
  }

  /**
   * Tests fromObject() for null input.
   */
  @Test(expected = classOf[IllegalArgumentException])
  def testFromObjectNull() {
    ContainerWrapper.fromObject(null)
  }

  /**
   * Helper method for testing whether a percent layout container can be
   * created.
   * @return the percent layout adapter created for the layout object
   */
  private def checkCreatePercentLayoutContainer(): JavaFxPercentLayoutAdapter = {
    val comp1 = new Text("Text1")
    val comp2 = new Text("Text2")
    val constr1 = "Constraint1"
    val constr2 = "Constraint2"
    wrapper.addComponent(comp1, constr1)
    wrapper.addComponent(comp2, constr2)
    val layout = new PercentLayout(4, 2)
    wrapper initLayout layout
    val pane = wrapper.createContainer().asInstanceOf[PercentLayoutPane]
    checkChildren(pane, comp1, comp2)
    val adapter = layout.getPlatformAdapter.asInstanceOf[JavaFxPercentLayoutAdapter]
    assertEquals("Wrong number of components", 2, adapter.getComponentCount)
    assertEquals("Wrong constraints (1)", constr1, adapter.getConstraints(0))
    assertEquals("Wrong constraints (2)", constr2, adapter.getConstraints(1))
    assertEquals("Component not found", comp2, adapter.getComponent(1))
    assertSame("Wrong layout", layout, pane.percentLayout)
    assertSame("Wrong container wrapper", wrapper, pane.wrapper)
    adapter
  }

  /**
   * Tests whether a correct pane is created when a percent layout object is
   * set.
   */
  @Test def testCreatePercentLayoutContainer() {
    val adapter = checkCreatePercentLayoutContainer()
    assertTrue("Wrong size handler",
      adapter.getSizeHandler.isInstanceOf[JavaFxUnitSizeHandler])
  }

  /**
   * Tests whether a size handler is correctly passed when creating a percent
   * layout pane.
   */
  @Test def testCreatePercentLayoutContainerWithSizeHandler() {
    val sizeHandler = mock[UnitSizeHandler]
    wrapper = new ContainerWrapper(Some(sizeHandler))
    val adapter = checkCreatePercentLayoutContainer()
    assertSame("Size handler not passed", sizeHandler, adapter.getSizeHandler)
  }

  /**
   * Tests whether a node can be obtained which is directly passed in.
   */
  @Test def testObtainPossiblyWrappedNodeNode() {
    val node = new Label("test")
    assertSame("Wrong result", node, ContainerWrapper.obtainPossiblyWrappedNode(node))
  }

  /**
   * Tests whether a node can be obtained from a container wrapper.
   */
  @Test def testObtainPossiblyWrappedNodeContainer() {
    val node = new Text("Test")
    wrapper.addComponent(node, null)
    val pane = ContainerWrapper.obtainPossiblyWrappedNode(wrapper).asInstanceOf[Pane]
    checkChildren(pane, node)
  }

  /**
   * Tests whether invalid input is handled correctly by obtainPossiblyWrappedNode().
   */
  @Test(expected = classOf[FormBuilderException])
  def testObtainPossiblyWrappedNodeOther() {
    ContainerWrapper.obtainPossiblyWrappedNode(this)
  }

  /**
   * Tests whether a list with the components of a wrapper can be obtained.
   */
  @Test def testGetComponents() {
    val node1 = new Label("c1")
    val node2 = new Text("c2")
    wrapper.addComponent(node1, null)
    wrapper.addComponent(node2, "a constraint")

    val components = wrapper.getComponents
    assertEquals("Wrong components", List(node1, node2), components)
  }

  /**
   * Tests whether a transformer function is called with the created pane.
   */
  @Test def testPaneTransformer(): Unit = {
    val f: ContainerWrapper.PaneTransformer = p => {
      val pane = new StackPane
      pane.getChildren add p
      pane
    }

    wrapper = new ContainerWrapper(paneTransformer = Some(f))
    val node = new Label("test")
    wrapper.addComponent(node, null)

    val resultPane = wrapper.createContainer()
    assertTrue("Wrong result pane", resultPane.isInstanceOf[StackPane])
    assertEquals("Wrong number of children", 1, resultPane.getChildren.size)
    val containerPane = resultPane.getChildren.get(0).asInstanceOf[Pane]
    checkChildren(containerPane, node)
  }
}
