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
package net.sf.jguiraffe.gui.platform.javafx.common

import java.util.concurrent.atomic.AtomicReference

import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper.toRunnable
import org.junit.Assert._
import org.junit.{Before, BeforeClass, Test}
import org.scalatest.junit.JUnitSuite

/**
 * Test class for ''DefaultToolTipFactory''.
 */
class TestDefaultToolTipFactory extends JUnitSuite {
  /** The factory to be tested. */
  private var factory: DefaultToolTipFactory = _

  @Before def setUp() {
    factory = new DefaultToolTipFactory
  }

  /**
   * Tests whether a tool tip can be created.
   */
  @Test def testCreateToolTip() {
    val myText = "Text for a test tool tip"
    val refTip = new AtomicReference[String]
    JavaFxTestHelper.runInFxThread { () =>
      val tip = factory createToolTip myText
      refTip set tip.getText
    }
    assertEquals("Wrong tool tip text", myText, refTip.get)
  }
}

object TestDefaultToolTipFactory {
  @BeforeClass def setUpBeforeClass() {
    JavaFxTestHelper.initPlatform()
  }
}
