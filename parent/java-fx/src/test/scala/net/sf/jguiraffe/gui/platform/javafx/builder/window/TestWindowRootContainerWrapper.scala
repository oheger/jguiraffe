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
package net.sf.jguiraffe.gui.platform.javafx.builder.window

import javafx.scene.control.{Label, MenuBar}
import javafx.scene.layout.{BorderPane, FlowPane}

import net.sf.jguiraffe.gui.layout.UnitSizeHandler
import org.junit.Assert._
import org.junit.{Before, Test}
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.EasyMockSugar

/**
 * Test class for ''WindowRootContainerWrapper''.
 */
class TestWindowRootContainerWrapper extends JUnitSuite with EasyMockSugar {
  /** A mock for the size handler. */
  private var sizeHandler: UnitSizeHandler = _

  @Before def setUp(): Unit = {
    sizeHandler = mock[UnitSizeHandler]
  }

  /**
   * Tests whether the size handler can be accessed.
   */
  @Test def testSizeHandlerPassedToSuperClass(): Unit = {
    val container = new WindowRootContainerWrapper(Some(sizeHandler))
    assertSame("Wrong size handler", sizeHandler, container.sizeHandler.get)
  }

  /**
   * Tests the created pane if no menu is provided.
   */
  @Test def testCreatePaneNoMenu(): Unit = {
    val ctrl = new Label
    val container = new WindowRootContainerWrapper(Some(sizeHandler))
    container.addComponent(ctrl, null)

    val pane = container.createContainer()
    assertTrue("Wrong pane: " + pane, pane.isInstanceOf[FlowPane])
    assertTrue("Control not contained", pane.getChildren.contains(ctrl))
  }

  /**
   * Tests whether a border pane is created and correctly initialized if a menu
   * bar has been specified.
   */
  @Test def testCreatePaneWithMenu(): Unit = {
    val menuBar = new MenuBar
    val ctrl = new Label
    val container = new WindowRootContainerWrapper(Some(sizeHandler))
    container.menuBar = Some(menuBar)
    container.addComponent(ctrl, null)

    val pane = container.createContainer().asInstanceOf[BorderPane]
    assertEquals("Menu bar not added correctly", menuBar, pane.getTop)
    val content = pane.getCenter.asInstanceOf[FlowPane]
    assertTrue("Control not contained", content.getChildren.contains(ctrl))
  }
}
