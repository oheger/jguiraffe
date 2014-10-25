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
package net.sf.jguiraffe.gui.platform.javafx.builder.components

import javafx.beans.property.{ObjectProperty, SimpleObjectProperty}
import javafx.scene.control.Tooltip

import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper._
import net.sf.jguiraffe.gui.builder.components.Color
import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import net.sf.jguiraffe.gui.platform.javafx.common.ToolTipFactory
import org.easymock.EasyMock
import org.junit.{BeforeClass, Test, Before}
import org.junit.Assert._
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.EasyMockSugar

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

    handler = new ToolTipWidgetHandler {
      override val toolTipFactory: ToolTipFactory = factory
      override val toolTipProperty: ObjectProperty[Tooltip] = tipProperty

      override def isVisible: Boolean = unexpected()

      override def getBackgroundColor: Color = unexpected()

      override def getForegroundColor: Color = unexpected()

      override def getFont: AnyRef = unexpected()

      override def setVisible(f: Boolean): Unit = unexpected()

      override def setFont(font: scala.Any): Unit = unexpected()

      override def getWidget: AnyRef = unexpected()

      override def setBackgroundColor(c: Color): Unit = unexpected()

      override def setForegroundColor(c: Color): Unit = unexpected()

      private def unexpected(): Nothing = throw new AssertionError("Unexpected method call!")
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
