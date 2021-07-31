/*
 * Copyright 2006-2021 The JGUIraffe Team.
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

import javafx.scene.control.MenuItem

import org.junit.Assert._
import org.junit.{Before, Test}
import org.scalatest.junit.JUnitSuite

/**
 * Test class for ''MenuItemWidgetHandler''.
 */
class TestMenuItemWidgetHandler extends JUnitSuite {
  /** The wrapped menu item. */
  private var item: MenuItem = _

  /** The handler to be tested. */
  private var handler: MenuItemWidgetHandler = _

  @Before def setUp(): Unit = {
    item = new MenuItem
    handler = new MenuItemWidgetHandler(item)
  }

  /**
   * Tests whether the correct widget is returned.
   */
  @Test def testGetWidget(): Unit = {
    assertSame("Wrong widget", item, handler.getWidget)
  }

  /**
   * Tests whether a tool tip can be queried. Menu items do not support tool
   * tips; therefore, result should be null.
   */
  @Test def testGetToolTip(): Unit = {
    assertNull("Got a tool tip", handler.getToolTip)
  }

  /**
   * Tests whether the visible state can be queried.
   */
  @Test def testIsVisible(): Unit = {
    item setVisible true
    assertTrue("Wrong visible state (1)", handler.isVisible)
    item setVisible false
    assertFalse("Wrong visible state (2)", handler.isVisible)
  }

  /**
   * Tests whether the visible state can be changed.
   */
  @Test def testSetVisible(): Unit = {
    handler setVisible true
    assertTrue("Wrong visible state (1)", item.isVisible)
    handler setVisible false
    assertFalse("Wrong visible state (2)", item.isVisible)
  }

  /**
   * Tests whether the correct style property is used.
   */
  @Test def testStyleProperty(): Unit = {
    assertSame("Wrong style property", item.styleProperty, handler.style)
  }
}
