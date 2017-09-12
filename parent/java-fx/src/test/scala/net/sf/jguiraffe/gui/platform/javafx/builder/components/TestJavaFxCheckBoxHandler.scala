/*
 * Copyright 2006-2017 The JGUIraffe Team.
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
import org.junit.Assert.assertTrue
import org.junit.{BeforeClass, Before, Test}
import org.scalatest.junit.JUnitSuite

import javafx.scene.control.CheckBox

object TestJavaFxCheckBoxHandler {
  @BeforeClass def setUpOnce(): Unit = {
    JavaFxTestHelper.initPlatform()
  }
}

/**
 * Test class for ''JavaFxCheckBoxHandler''.
 */
class TestJavaFxCheckBoxHandler extends JUnitSuite {
  /** The check box control. */
  private var checkBox: CheckBox = _

  /** The handler to be tested. */
  private var handler: JavaFxCheckBoxHandler = _

  @Before def setUp() {
    checkBox = new CheckBox
    handler = new JavaFxCheckBoxHandler(checkBox)
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
    checkBox.setSelected(true)
    assertEquals("Wrong result", java.lang.Boolean.TRUE, handler.getData)
  }

  /**
   * Tests whether the check box's selected state can be manipulated via the
   * handler.
   */
  @Test def testSetData() {
    handler setData java.lang.Boolean.TRUE
    assertTrue("Not selected", checkBox.isSelected)
    handler setData java.lang.Boolean.FALSE
    assertFalse("Still selected", checkBox.isSelected)
  }

  /**
   * Tests whether setData() can handle null input.
   */
  @Test def testSetDataNull() {
    checkBox setSelected true
    handler setData null
    assertFalse("Still selected", checkBox.isSelected)
  }

  /**
   * Tests whether the correct property for change listener support is returned.
   */
  @Test def testObservableValue() {
    assertEquals("Wrong property", checkBox.selectedProperty,
      handler.observableValue)
  }
}
