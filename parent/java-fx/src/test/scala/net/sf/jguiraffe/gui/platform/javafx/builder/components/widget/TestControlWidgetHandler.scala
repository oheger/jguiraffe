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

import javafx.scene.control.{Control, Label, Tooltip}

import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import net.sf.jguiraffe.gui.platform.javafx.common.ToolTipFactory
import org.easymock.EasyMock
import org.junit.Assert._
import org.junit.{BeforeClass, Before, Test}
import org.scalatest.junit.JUnitSuite
import org.scalatest.easymock.EasyMockSugar

object TestControlWidgetHandler {
  @BeforeClass def setUpOnce(): Unit = {
    JavaFxTestHelper.initPlatform()
  }
}

/**
 * Test class for ''ControlWidgetHandler''.
 */
class TestControlWidgetHandler extends JUnitSuite with EasyMockSugar {
  /** A mock for the tool tip factory. */
  private var tipFactory: ToolTipFactory = _

  /** The wrapped control. */
  private var control: Control = _

  /** The handler to be tested. */
  private var handler: ControlWidgetHandler = _

  @Before def setUp(): Unit = {
    tipFactory = mock[ToolTipFactory]
    control = new Label
    handler = new ControlWidgetHandler(control, tipFactory)
  }

  /**
   * Tests whether a tool tip can be set via this handler. This tests the whole
   * functionality the handler should provide.
   */
  @Test def testSetToolTip(): Unit = {
    val tip = mock[Tooltip]
    val TipText = "Some Tooltip"
    EasyMock.expect(tipFactory.createToolTip(TipText)).andReturn(tip)

    whenExecuting(tip, tipFactory) {
      handler setToolTip TipText
      assertSame("Tool tip not set", tip, control.getTooltip)
    }
  }
}
