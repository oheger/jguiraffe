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
package net.sf.jguiraffe.gui.platform.javafx.builder.components.widget

import javafx.beans.property.{ObjectProperty, SimpleObjectProperty}
import javafx.scene.control.Tooltip

import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper._
import net.sf.jguiraffe.gui.platform.javafx.builder.components.WidgetHandlerAdapter
import net.sf.jguiraffe.gui.platform.javafx.common.ToolTipFactory
import org.easymock.EasyMock
import org.junit.Assert._
import org.junit.{Before, BeforeClass, Test}
import org.scalatestplus.junit.JUnitSuite
import org.scalatestplus.easymock.EasyMockSugar

/**
 * Companion object.
 */
object TestToolTipWidgetHandler {
  @BeforeClass def setUpOnce(): Unit = {
    initPlatform()
  }
}

/**
 * Test class for ''ToolTipWidgetHandler''.
 */
class TestToolTipWidgetHandler extends JUnitSuite with EasyMockSugar {
  /** Constant for a test tool tip. */
  private val TestToolTip = "This is a test tool tip."

  /** The property for storing tool tips.*/
  private var tipProperty: ObjectProperty[Tooltip] = _

  /** A mock for the tool tip factory.*/
  private var factory: ToolTipFactory = _

  /** The handler to be tested. */
  private var handler: ToolTipWidgetHandler = _

  @Before def setUp(): Unit = {
    tipProperty = new SimpleObjectProperty[Tooltip]
    factory = mock[ToolTipFactory]

    handler = new WidgetHandlerAdapter with ToolTipWidgetHandler {
      override val toolTipFactory: ToolTipFactory = factory
      override val toolTipProperty: ObjectProperty[Tooltip] = tipProperty
    }
  }

  /**
   * Tests whether the tool tip can be set to null.
   */
  @Test def testSetToolTipNull(): Unit = {
    whenExecuting(factory) {
      handler setToolTip null
      assertNull("Got a tool tip", tipProperty.get)
    }
  }

  /**
   * Tests whether a defined tool tip can be set.
   */
  @Test def testSetToolTipDefined(): Unit = {
    val tip = mock[Tooltip]
    EasyMock.expect(factory.createToolTip(TestToolTip)).andReturn(tip)

    whenExecuting(factory, tip) {
      handler setToolTip TestToolTip
      assertSame("Wrong tool tip set", tip, tipProperty.get)
    }
  }

  /**
   * Tests whether a tool tip text can be queried if no tip is defined.
   */
  @Test def testGetToolTipUndefined(): Unit = {
    assertNull("Got a tool tip text", handler.getToolTip)
  }

  /**
   * Creates a tool tip with the test text.
   * @return the tool tip
   */
  private def createToolTip(): Tooltip = {
    var tip: Tooltip = null
    JavaFxTestHelper.runInFxThread { () =>
      tip = new Tooltip(TestToolTip)
    }
    tip
  }

  /**
   * Tests whether the correct tool tip text can be extracted.
   */
  @Test def testGetToolTipDefined(): Unit = {
    tipProperty set createToolTip()

    assertEquals("Wrong tool tip text", TestToolTip, handler.getToolTip)
  }
}
