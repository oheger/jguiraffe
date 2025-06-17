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
package net.sf.jguiraffe.gui.platform.javafx.builder.components

import javafx.scene.control.Label

import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import org.junit.Assert.{assertFalse, assertSame, assertTrue}
import org.junit.{Before, BeforeClass, Test}
import org.scalatestplus.junit.JUnitSuite

object TestJavaFxComponentHandler {
  @BeforeClass def setUpOnce(): Unit = {
    JavaFxTestHelper.initPlatform()
  }
}

/**
 * Test class for ''JavaFxComponentHandler''.
 */
class TestJavaFxComponentHandler extends JUnitSuite {
  /** The mock control wrapped by the handler. */
  private var control: Label = _

  /** The test component handler. */
  private var handler: JavaFxComponentHandlerTestImpl = _

  @Before def setUp() {
    control = new Label
    handler = new JavaFxComponentHandlerTestImpl(control)
  }

  /**
   * Tests whether the enabled state can be queried if it is true.
   */
  @Test def testIsEnabledTrue() {
    control setDisable false

    assertTrue("Wrong result", handler.isEnabled)
  }

  @Test def testIsEnabledFalse(): Unit = {
    control setDisable true

    assertFalse("Wrong result", handler.isEnabled)
  }

  /**
   * Tests whether the enabled state can be changed to true.
   */
  @Test def testSetEnabledTrue() {
    handler setEnabled true
    assertFalse("Not enabled", control.isDisable)
  }

  /**
   * Tests whether the enabled state can be changed to false.
   */
  @Test def testSetEnabledFalse() {
    handler setEnabled false
    assertTrue("Not disabled", control.isDisable)
  }

  /**
   * Tests whether the correct component is returned by the field.
   */
  @Test def testGetComponentField() {
    assertSame("Wrong component (field)", control, handler.component)
  }

  /**
   * Tests whether the correct component is returned by the get method.
   */
  @Test def testGetComponentGetter() {
    assertSame("Wrong component (getter)", control, handler.getComponent)
  }

  /**
   * Tests whether the correct outer component is returned.
   */
  @Test def testGetOuterComponent() {
    assertSame("Wrong outer component", control, handler.getOuterComponent)
  }

  /**
   * Tests whether the correct source node is returned.
   */
  @Test def testSourceNode() {
    assertSame("Wrong source node", control, handler.sourceNode)
  }

  /**
   * A test implementation of a concrete component handler.
   */
  private class JavaFxComponentHandlerTestImpl(c: Label)
    extends JavaFxComponentHandler[AnyRef](c) {
    def getData: AnyRef = {
      throw new UnsupportedOperationException("Unexpected method call!")
    }

    def setData(data: AnyRef) {
      throw new UnsupportedOperationException("Unexpected method call!")
    }

    def getType: Class[_] = {
      throw new UnsupportedOperationException("Unexpected method call!")
    }
  }
}
