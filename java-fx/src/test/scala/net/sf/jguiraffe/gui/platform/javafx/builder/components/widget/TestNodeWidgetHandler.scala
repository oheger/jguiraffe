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
package net.sf.jguiraffe.gui.platform.javafx.builder.components.widget

import javafx.scene.control.Label

import org.junit.Assert._
import org.junit.{Before, Test}
import org.scalatest.junit.JUnitSuite

/**
 * Test class for ''NodeWidgetHandler''.
 */
class TestNodeWidgetHandler extends JUnitSuite {
  /** The node used for the tests. */
  private var widget: Label = _

  /** The handler to be tested. */
  private var handler: NodeWidgetHandler = _

  @Before def setUp(): Unit = {
    widget = new Label
    handler = new NodeWidgetHandler(widget)
  }

  /**
   * Tests whether the correct widget is returned.
   */
  @Test def testGetWidget(): Unit = {
    assertSame("Wrong widget", widget, handler.getWidget)
  }

  /**
   * Tests whether the correct style property is used.
   */
  @Test def testStyleProperty(): Unit = {
    assertSame("Wrong style property", widget.styleProperty, handler.style)
  }

  /**
   * Tests whether the visible property can be queried.
   */
  @Test def testIsVisible(): Unit = {
    widget setVisible true
    assertTrue("Not visible", handler.isVisible)

    widget setVisible false
    assertFalse("Still visible", handler.isVisible)
  }

  /**
   * Tests whether the visible state can be modified.
   */
  @Test def testSetVisible(): Unit = {
    handler setVisible true
    assertTrue("Not visible", widget.isVisible)

    handler setVisible false
    assertFalse("Still visible", widget.isVisible)
  }

  /**
   * Tests whether the tool tip can be queried. Tool tips are not supported,
   * so result is always null.
   */
  @Test def testGetToolTip(): Unit = {
    assertNull("Got a tool tip", handler.getToolTip)
  }

  /**
   * Tests setToolTip(). We can also test that the label's tool tip is not
   * changed.
   */
  @Test def testSetToolTip(): Unit = {
    handler setToolTip "Some tool tip"
    assertNull("Got a tool tip", widget.getTooltip)
  }
}
