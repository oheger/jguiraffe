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
import org.junit.Assert.assertSame
import org.junit.{BeforeClass, Before, Test}

import javafx.scene.control.ProgressBar

object TestJavaFxProgressBarHandler {
  @BeforeClass def setUpOnce(): Unit = {
    JavaFxTestHelper.initPlatform()
  }
}

/**
 * Test class for ''JavaFxProgressBarHandler''.
 */
class TestJavaFxProgressBarHandler {
  /** The progress bar control. */
  private var progressBar: ProgressBar = _

  /** The handler to be tested. */
  private var handler: JavaFxProgressBarHandler = _

  @Before def setUp() {
    progressBar = new ProgressBar
    handler = new JavaFxProgressBarHandler(progressBar, 0, 100)
  }

  /**
   * Tests whether the correct data type is returned for the handler.
   */
  @Test def testType() {
    assertEquals("Wrong type", classOf[Integer], handler.getType)
  }

  /**
   * Tests whether the correct property for change notifications is returned.
   */
  @Test def testChangeEventSupport() {
    assertSame("Wrong change event property", progressBar.progressProperty,
      handler.observableValue)
  }

  /**
   * Tests whether the correct current progress value can be queried.
   */
  @Test def testGetValue() {
    def checkValue(p: Double, expVal: Int) {
      progressBar setProgress p
      assertEquals("Wrong value", expVal, handler.getValue)
    }

    checkValue(0, 0)
    checkValue(0.25, 25)
    checkValue(0.5, 50)
    checkValue(0.75, 75)
    checkValue(1, 100)
  }

  /**
   * Tests whether the progress bar's value can be set directly.
   */
  @Test def testSetValue() {
    def checkSetValue(value: Int, expProgr: Double) {
      handler setValue value
      assertEquals("Wrong progress", expProgr, progressBar.getProgress, .001)
    }

    checkSetValue(0, 0)
    checkSetValue(25, .25)
    checkSetValue(50, .5)
    checkSetValue(75, .75)
    checkSetValue(100, 1)
  }

  /**
   * Tests whether the handler's data can be queried.
   */
  @Test def testGetData() {
    progressBar setProgress .5
    assertEquals("Wrong value", Integer.valueOf(50), handler.getData)
  }

  /**
   * Tests whether the handler's data can be set to a valid value.
   */
  @Test def testSetData() {
    handler setData Integer.valueOf(50)
    assertEquals("Wrong progress value", .5, progressBar.getProgress, .001)
  }

  /**
   * Tests whether setData() can deal with null values.
   */
  @Test def testSetDataNull() {
    val progress = .2
    progressBar setProgress progress
    handler setData null
    assertEquals("Progress was changed", progress, progressBar.getProgress, .001)
  }

  /**
   * Tests the handling of the progressText property. This property is not
   * supported by JavaFX, so the value passed to the property is just recorded.
   */
  @Test def testProgressText() {
    val text = "Test progress text"
    handler setProgressText text
    assertEquals("Wrong progress text", text, handler.getProgressText)
  }
}
