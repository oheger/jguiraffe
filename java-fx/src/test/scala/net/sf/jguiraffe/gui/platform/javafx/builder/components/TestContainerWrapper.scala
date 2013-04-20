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
package net.sf.jguiraffe.gui.platform.javafx.builder.components

import org.easymock.EasyMock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.EasyMockSugar

import javafx.scene.Node
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import net.sf.jguiraffe.gui.builder.components.FormBuilderException

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
}
