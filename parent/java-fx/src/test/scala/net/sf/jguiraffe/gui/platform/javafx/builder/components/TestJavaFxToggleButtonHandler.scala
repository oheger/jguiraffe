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
package net.sf.jguiraffe.gui.platform.javafx.builder.components

import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.{BeforeClass, Before, Test}
import org.scalatest.junit.JUnitSuite

import javafx.scene.control.ToggleButton

object TestJavaFxToggleButtonHandler {
  @BeforeClass def setUpOnce(): Unit = {
    JavaFxTestHelper.initPlatform()
  }
}

/**
 * Test class for ''JavaFxToggleButtonHandler''.
 */
class TestJavaFxToggleButtonHandler extends JUnitSuite {
  /** A test toggle button component. */
  private var button: ToggleButton = _

  /** The handler to be tested. */
  private var handler: JavaFxToggleButtonHandler = _

  @Before def setUp() {
    button = new ToggleButton
    handler = new JavaFxToggleButtonHandler(button)
  }

  /**
   * Tests getData() if the expected result is false.
   */
  @Test def testGetDataFalse() {
    assertEquals("Wrong result", java.lang.Boolean.FALSE, handler.getData)
  }

  /**
   * Tests getData() if the expected result is true.
   */
  @Test def testGetDataTrue() {
    button setSelected true
    assertEquals("Wrong result", java.lang.Boolean.TRUE, handler.getData)
  }

  /**
   * Tests whether the handler's data can be set.
   */
  @Test def testSetData() {
    handler setData true
    assertTrue("Not selected", button.isSelected)
    handler setData java.lang.Boolean.FALSE
    assertFalse("Still selected", button.isSelected)
  }

  /**
   * Tests setData() with null input.
   */
  @Test def testSetDataNull() {
    button setSelected true
    handler setData null
    assertFalse("Still selected", button.isSelected)
  }

  /**
   * Tests whether the correct property for change listener support is
   * returned.
   */
  @Test def testObservableValue() {
    assertEquals("Wrong property", button.selectedProperty,
      handler.observableValue)
  }

  /**
   * Tests the default action command managed by the handler.
   */
  @Test def testActionCommandNotSet() {
    assertNull("Got an action command", handler.actionCommand)
  }

  /**
   * Tests whether an action command can be passed to the constructor.
   */
  @Test def testActionCommandInitialized() {
    val Command = "TestToggleButtonActionCommand"
    handler = new JavaFxToggleButtonHandler(button, Command)
    assertEquals("Wrong action command", Command, handler.actionCommand)
  }
}
