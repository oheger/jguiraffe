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
import org.junit.Assert.assertSame
import org.junit.{BeforeClass, Before, Test}

import javafx.scene.control.Slider

object TestJavaFxSliderHandler {
  @BeforeClass def setUpOnce(): Unit = {
    JavaFxTestHelper.initPlatform()
  }
}

/**
 * Test class for ''JavaFxSliderHandler''.
 */
class TestJavaFxSliderHandler {
  /** The slider component. */
  private var slider: Slider = _

  /** The handler to be tested. */
  private var handler: JavaFxSliderHandler = _

  @Before def setUp() {
    slider = new Slider
    slider setMin 0
    slider setMax 100
    handler = new JavaFxSliderHandler(slider)
  }

  /**
   * Tests whether the handler returns the correct data type.
   */
  @Test def testGetType() {
    assertEquals("Wrong type", classOf[Integer], handler.getType)
  }

  /**
   * Tests whether the correct property for managing change events is returned.
   */
  @Test def testChangeEventSupport() {
    assertSame("Wrong change event property", slider.valueProperty,
      handler.observableValue)
  }

  /**
   * Tests whether the correct value is returned when querying the handler's
   * data.
   */
  @Test def testGetData() {
    val value = 50
    slider setValue value
    assertEquals("Wrong value", value, handler.getData.intValue)
  }

  /**
   * Tests whether the handler's data can be set.
   */
  @Test def testSetData() {
    val value = 25
    handler setData value
    assertEquals("Wrong value", value, slider.getValue, .001)
  }

  /**
   * Tests setData() if the new value is null.
   */
  @Test def testSetDataNull() {
    val value = 25
    slider setValue value
    handler setData null
    assertEquals("Wrong value", value, slider.getValue, .001)
  }
}
