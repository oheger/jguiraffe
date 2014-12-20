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
package net.sf.jguiraffe.gui.platform.javafx.builder.components

import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.{FlowPane, Pane, StackPane}

import net.sf.jguiraffe.gui.builder.components.tags.PanelTag
import org.junit.Assert._
import org.junit.{Before, Test}
import org.scalatest.junit.JUnitSuite

/**
 * Companion object.
 */
object TestBorderPanelFactory {
  /** Constant for a test title. */
  private val Title = "Test title"

  /**
   * Checks whether the given node has been assigned the specified CSS class.
   * @param node the node to be checked
   * @param cssClass the expected CSS class
   * @return the checked node
   */
  private def checkStyle(node: Node, cssClass: String): Node = {
    assertTrue("Style class not found", node.getStyleClass contains cssClass)
    node
  }

  /**
   * Checks whether the content node has been assigned the expected CSS class.
   * @param content the content node to be checked
   */
  private def checkContentStyle(content: Node) {
    checkStyle(content, "bordered-panel-content")
  }

  /**
   * Checks whether the given child node is contained in the given pane.
   * @param pane the pane
   * @param child the child node in question
   * @return the child node
   */
  private def childContained(pane: Pane, child: Node): Node = {
    assertTrue("Child not found", pane.getChildren contains child)
    child
  }
}

/**
 * Test class for ''BorderPanelFactory''.
 */
class TestBorderPanelFactory extends JUnitSuite {

  import net.sf.jguiraffe.gui.platform.javafx.builder.components.TestBorderPanelFactory._

  /** The factory to be tested. */
  private var factory: BorderPanelFactory = _

  @Before def setUp(): Unit = {
    factory = new BorderPanelFactory
  }

  /**
   * Tests whether a panel with only a border is correctly created.
   */
  @Test def testCreateBorderPanelBorderOnly(): Unit = {
    val tag = new PanelTag
    val content = new FlowPane
    tag setBorder true

    val pane = factory.createBorderPanel(tag, content)
    checkContentStyle(childContained(pane, content))
    checkStyle(pane, "bordered-panel-border")
    assertEquals("Wrong number of children", 1, pane.getChildren.size)
  }

  /**
   * Helper method for testing the creation of a panel with a title.
   * @param withBorder flag whether a border should be available
   * @param expectedPanelStyle the expected style of the panel
   */
  private def checkCreateBorderPanelWithTitle(withBorder: Boolean, expectedPanelStyle: String):
  Unit = {
    val tag = new PanelTag
    val content = new FlowPane
    tag setText Title
    tag setBorder withBorder

    val pane = factory.createBorderPanel(tag, content)
    checkContentStyle(childContained(pane, content))
    checkStyle(pane, expectedPanelStyle)
    assertEquals("Wrong number of children", 2, pane.getChildren.size)
    val label = pane.getChildren.get(0).asInstanceOf[Label]
    assertEquals("Wrong caption", " " + Title + " ", label.getText)
    checkStyle(label, "bordered-panel-title")
    assertEquals("Wrong alignment for title", Pos.TOP_CENTER, StackPane.getAlignment(label))
  }

  /**
   * Tests whether a panel with only a title can be created.
   */
  @Test def testCreateBorderPanelTitleOnly(): Unit = {
    checkCreateBorderPanelWithTitle(withBorder = false, "bordered-panel-no-border")
  }

  /**
   * Tests whether a panel with both a title and a border can be created.
   */
  @Test def testCreateBorderPaneWithTitleAndBorder(): Unit = {
    checkCreateBorderPanelWithTitle(withBorder = true, "bordered-panel-border")
  }

  /**
   * Tests that no transformer function is returned if no decorations are needed.
   */
  @Test def testGetPaneTransformerNoDecorations(): Unit = {
    assertFalse("Got a transformer function", factory.getPaneTransformer(new PanelTag).isDefined)
  }

  /**
   * Checks whether a decorating transformer function is correctly created.
   * @param tag the panel tag to be used as input
   * @param expStyleClass the expected style class of the resulting panel
   */
  private def checkDecoratedPaneTransformer(tag: PanelTag, expStyleClass: String): Unit = {
    val content = new FlowPane
    val transformer = factory.getPaneTransformer(tag).get
    val pane = transformer(content)
    checkContentStyle(childContained(pane, content))
    checkStyle(pane, expStyleClass)
  }

  /**
   * Tests the pane transformer function if a border is declared.
   */
  @Test def testGetPaneTransformerBorder(): Unit = {
    val tag = new PanelTag
    tag setBorder true

    checkDecoratedPaneTransformer(tag, "bordered-panel-border")
  }

  /**
   * Tests the pane transformer function if a title is declared.
   */
  @Test def testGetPaneTransformerTitle(): Unit = {
    val tag = new PanelTag
    tag setText Title

    checkDecoratedPaneTransformer(tag, "bordered-panel-no-border")
  }
}
