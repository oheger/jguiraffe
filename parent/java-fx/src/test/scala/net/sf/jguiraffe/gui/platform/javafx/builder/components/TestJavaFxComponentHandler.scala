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

import org.easymock.EasyMock
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.easymock.PowerMock
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.scalatest.junit.JUnitSuite

import javafx.scene.control.Control

/**
 * Test class for ''JavaFxComponentHandler''.
 */
@RunWith(classOf[PowerMockRunner])
@PrepareForTest(Array(classOf[Control]))
class TestJavaFxComponentHandler extends JUnitSuite {
  /** The mock control wrapped by the handler. */
  private var control: Control = _

  /** The test component handler. */
  private var handler: JavaFxComponentHandlerTestImpl = _

  @Before def setUp() {
    control = PowerMock.createMock(classOf[Control])
    handler = new JavaFxComponentHandlerTestImpl(control)
  }

  /**
   * Tests whether the enabled state can be queried.
   */
  @Test def testIsEnabled() {
    EasyMock.expect(control.isDisabled).andReturn(true)
    EasyMock.expect(control.isDisabled).andReturn(false)
    PowerMock.replayAll()

    assertFalse("Not enabled", handler.isEnabled)
    assertTrue("Still enabled", handler.isEnabled)
    PowerMock.verifyAll()
  }

  /**
   * Tests whether the enabled state can be changed to true.
   */
  @Test def testSetEnabledTrue() {
    control setDisable false
    PowerMock.replayAll()

    handler setEnabled true
    PowerMock.verifyAll()
  }

  /**
   * Tests whether the enabled state can be changed to false.
   */
  @Test def testSetEnabledFalse() {
    control setDisable true
    PowerMock.replayAll()

    handler setEnabled false
    PowerMock.verifyAll()
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
  private class JavaFxComponentHandlerTestImpl(c: Control)
    extends JavaFxComponentHandler[AnyRef](c) {
    def getData(): AnyRef = {
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
