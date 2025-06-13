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
package net.sf.jguiraffe.gui.platform.javafx.builder.action

import javafx.beans.property.SimpleBooleanProperty
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.control.MenuItem

import org.easymock.EasyMock
import org.junit.Assert._
import org.junit.{Before, Test}
import org.scalatest.junit.JUnitSuite
import org.scalatest.easymock.EasyMockSugar

/**
 * Test class for ''MenuItemComponentHandler''.
 */
class TestMenuItemComponentHandler extends JUnitSuite with EasyMockSugar {
  /** The wrapped menu item. */
  private var menuItem: MenuItem = _

  /** The property with the handler's data. */
  private var property: SimpleBooleanProperty = _

  /** The handler to be tested. */
  private var handler: MenuItemComponentHandler = _

  @Before def setUp(): Unit = {
    menuItem = new MenuItem
    property = new SimpleBooleanProperty
    handler = new MenuItemComponentHandler(menuItem, property, "MyAction")
  }

  /**
   * Tests whether the correct component is returned.
   */
  @Test def testGetComponent(): Unit = {
    assertSame("Wrong component", menuItem, handler.getComponent)
  }

  /**
   * Tests whether the correct outer component is returned.
   */
  @Test def testGetOuterComponent(): Unit = {
    assertSame("Wrong outer component", menuItem, handler.getOuterComponent)
  }

  /**
   * Tests the handler's type.
   */
  @Test def testGetType(): Unit = {
    assertEquals("Wrong type", classOf[java.lang.Boolean], handler.getType)
  }

  /**
   * Tests whether the handler's data can be queried.
   */
  @Test def testGetData(): Unit = {
    assertFalse("Wrong data (1)", handler.getData)
    property set true
    assertTrue("Wrong data (2)", handler.getData)
  }

  /**
   * Tests whether data can be set.
   */
  @Test def testSetData(): Unit = {
    handler setData true
    assertTrue("Data not set (1)", property.get)
    handler setData false
    assertFalse("Data not set (2)", property.get)
  }

  /**
   * Tests whether null input can be handled by setData().
   */
  @Test def testSetDataNull(): Unit = {
    handler setData null
    assertFalse("Wrong value", property.get)
    assertFalse("Wrong data", handler.getData)
  }

  /**
   * Tests whether the enabled state can be queried.
   */
  @Test def testIsEnabled(): Unit = {
    assertTrue("Wrong enabled state (1)", handler.isEnabled)
    menuItem setDisable true
    assertFalse("Wrong enabled state (2)", handler.isEnabled)
  }

  /**
   * Tests whether the enabled state can be set.
   */
  @Test def testSetEnabled(): Unit = {
    handler setEnabled false
    assertTrue("Wrong disabled state (1)", menuItem.isDisable)
    handler setEnabled true
    assertFalse("Wrong disabled state (2)", menuItem.isDisable)
  }

  /**
   * Tests whether an action listener can be added.
   */
  @Test def testAddActionListener(): Unit = {
    val listener = mock[EventHandler[ActionEvent]]
    listener handle EasyMock.anyObject(classOf[ActionEvent])

    whenExecuting(listener) {
      handler addActionListener listener
      menuItem.fire()
    }
  }

  /**
   * Tests whether an action listener can be removed.
   */
  @Test def testRemoveActionListener(): Unit = {
    val listener = mock[EventHandler[ActionEvent]]

    whenExecuting(listener) {
      handler addActionListener listener
      handler removeActionListener listener
      menuItem.fire()
    }
  }
}
