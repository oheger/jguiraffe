/*
 * Copyright 2006-2016 The JGUIraffe Team.
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

import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.{BeforeClass, Before, Test}
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.EasyMockSugar

import javafx.scene.control.ButtonBase

object TestJavaFxButtonHandler {
  @BeforeClass def setUpOnce(): Unit = {
    JavaFxTestHelper.initPlatform()
  }
}

/**
 * Test class for ''JavaFxButtonHandler''.
 */
class TestJavaFxButtonHandler extends JUnitSuite with EasyMockSugar {
  /** The mock for the button control. */
  private var button: ButtonBase = _

  /** The handler to be tested. */
  private var handler: JavaFxButtonHandler = _

  @Before def setUp() {
    button = mock[ButtonBase]
    handler = new JavaFxButtonHandler(button)
  }

  /**
   * Tests whether the handler supports adding action listeners.
   */
  @Test def testActionListenerSupport() {
    assertTrue("No control action listener support",
      handler.isInstanceOf[ControlActionEventSource[java.lang.Boolean]])
  }

  /**
   * Tests whether the correct data type is returned.
   */
  @Test def testGetType() {
    assertEquals("Wrong data type", classOf[java.lang.Boolean], handler.getType)
  }

  /**
   * Tests the dummy getData() implementation. Here we can only test that the
   * button control is not manipulated.
   */
  @Test def testGetData() {
    whenExecuting(button) {
      assertEquals("Wrong result", java.lang.Boolean.FALSE, handler.getData)
    }
  }

  /**
   * Tests the dummy setData() implementation. Here we can only test that the
   * button control is not touched.
   */
  @Test def testSetData() {
    whenExecuting(button) {
      handler setData java.lang.Boolean.TRUE
    }
  }

  /**
   * Tests the action command of the action event source if it has not been
   * initialized.
   */
  @Test def testDefaultActionCommand() {
    assertNull("Got an action command", handler.actionCommand)
  }

  /**
   * Tests whether an action command can be passed to the constructor.
   */
  @Test def testConfiguredActionCommand() {
    val Command = "TestButtonActionCommand"
    handler = new JavaFxButtonHandler(button, Command)
    assertEquals("Wrong action command", Command, handler.actionCommand)
  }
}
