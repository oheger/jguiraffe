/*
 * Copyright 2006-2013 The JGUIraffe Team.
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

import org.scalatest.junit.JUnitSuite
import javafx.scene.Node
import org.junit.Before
import org.scalatest.mock.EasyMockSugar
import org.junit.Test
import org.junit.Assert._
import org.easymock.EasyMock
import org.junit.runner.RunWith
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.api.easymock.PowerMock
import net.sf.jguiraffe.gui.builder.components.Color
import javafx.scene.control.Control
import javafx.scene.control.Tooltip
import org.junit.BeforeClass
import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import net.sf.jguiraffe.gui.platform.javafx.builder.utils.JavaFxGUISynchronizer
import net.sf.jguiraffe.gui.platform.javafx.FetchAnswer

/**
 * Test class for ''JavaFxWidgetHandler''.
 */
@RunWith(classOf[PowerMockRunner])
@PrepareForTest(Array(classOf[Node], classOf[Control]))
class TestJavaFxWidgetHandler extends JUnitSuite {
  /** Constant for a test color. */
  private val TestColor = Color.newLogicInstance("someTestColor")

  /** Constant for a test styles definition. */
  private val StylesDef = "A test styles definition."

  /** Constant for a tool tip text. */
  private val TestToolTip = "My Test Tool Tip"

  /** A mock for the wrapped Java FX node. */
  private var node: Node = _

  /** The handler to be tested.*/
  private var widgetHandler: JavaFxWidgetHandlerTestImpl = _

  @Before def setUp() {
    node = PowerMock.createMock(classOf[Node])
    widgetHandler = new JavaFxWidgetHandlerTestImpl(node)
  }

  /**
   * Tests whether the correct wrapped widget is returned.
   */
  @Test def testGetWidget() {
    assertSame("Wrong wrapped widget", node, widgetHandler.getWidget)
  }

  /**
   * Tests the implementation of isVisisble().
   */
  @Test def testIsVisible() {
    EasyMock.expect(node.isVisible).andReturn(true)
    EasyMock.expect(node.isVisible()).andReturn(false)
    PowerMock.replayAll()
    assertTrue("Wrong result (1)", widgetHandler.isVisible)
    assertFalse("Wrong result (2)", widgetHandler.isVisible)
    PowerMock.verifyAll()
  }

  /**
   * Tests whether the widget's visibility can be changed.
   */
  @Test def testSetVisible() {
    node.setVisible(false)
    node.setVisible(true)
    PowerMock.replayAll()
    widgetHandler.setVisible(false)
    widgetHandler.setVisible(true)
    PowerMock.verifyAll()
  }

  /**
   * Creates a mock for a styles handler and installs it at the test object.
   * @return the styles handler mock
   */
  private def installMockStylesHandler(): JavaFxStylesHandler = {
    val stylesHandler = PowerMock.createMock(classOf[JavaFxStylesHandler])
    val styles = PowerMock.createMock(classOf[Styles])
    EasyMock.expect(stylesHandler.styles).andReturn(styles).anyTimes()
    EasyMock.expect(styles.toExternalForm()).andReturn(StylesDef).anyTimes()
    widgetHandler.mockStylesHandler = stylesHandler
    stylesHandler
  }

  /**
   * Prepares the mock for the wrapped widget to expect an update of its
   * styles definitions.
   */
  private def expectStylesUpdate() {
    node setStyle StylesDef
  }

  /**
   * Tests that a correct styles handler is used.
   */
  @Test def testStylesHandler() {
    val styleDef = "-fx-test: Style1; -fx-test2: Style2;"
    EasyMock.expect(node.getStyle).andReturn(styleDef)
    PowerMock.replayAll()
    val styles = widgetHandler.stylesHandler.styles
    assertEquals("Wrong number of styles", 2, styles.styleKeys.size)
    assertEquals("Wrong style (1)", "Style1", styles("-fx-test").get)
    assertEquals("Wrong style (2)", "Style2", styles("-fx-test2").get)
    PowerMock.verifyAll()
  }

  /**
   * Tests whether the node's background color can be set.
   */
  @Test def testSetBackgroundColor() {
    val stylesHandler = installMockStylesHandler()
    stylesHandler.setBackgroundColor(TestColor)
    expectStylesUpdate()
    PowerMock.replayAll()
    widgetHandler setBackgroundColor TestColor
    PowerMock.verifyAll()
  }

  /**
   * Tests whether the node's foreground color can be set.
   */
  @Test def testSetForegroundColor() {
    val stylesHandler = installMockStylesHandler()
    stylesHandler.setForegroundColor(TestColor)
    expectStylesUpdate()
    PowerMock.replayAll()
    widgetHandler setForegroundColor TestColor
    PowerMock.verifyAll()
  }

  /**
   * Tests whether the widget's background color can be queried.
   */
  @Test def testGetBackgroundColor() {
    val stylesHandler = installMockStylesHandler()
    EasyMock.expect(stylesHandler.getBackgroundColor).andReturn(TestColor)
    PowerMock.replayAll()
    assertEquals("Wrong color", TestColor, widgetHandler.getBackgroundColor)
    PowerMock.verifyAll()
  }

  /**
   * Tests whether the widget's foreground color can be queried.
   */
  @Test def testGetForegroundColor() {
    val stylesHandler = installMockStylesHandler()
    EasyMock.expect(stylesHandler.getForegroundColor).andReturn(TestColor)
    PowerMock.replayAll()
    assertEquals("Wrong color", TestColor, widgetHandler.getForegroundColor)
    PowerMock.verifyAll()
  }

  /**
   * Tests whether a new font can be set.
   */
  @Test def testSetFont() {
    val stylesHandler = installMockStylesHandler()
    val font = JavaFxFont()
    stylesHandler setFont font
    expectStylesUpdate()
    PowerMock.replayAll()
    widgetHandler setFont font
    PowerMock.verifyAll()
  }

  /**
   * Tests whether the current font can be queried.
   */
  @Test def testGetFont() {
    val stylesHandler = installMockStylesHandler()
    val font = JavaFxFont(family = Some("MyTestFont"))
    EasyMock.expect(stylesHandler.getFont).andReturn(font)
    PowerMock.replayAll()
    assertEquals("Wrong font", font, widgetHandler.getFont)
    PowerMock.verifyAll()
  }

  /**
   * Tests whether a tool tip can be queried if the wrapped node does not
   * support this.
   */
  @Test def testGetToolTipUnsupported() {
    assertNull("Got a tool tip", widgetHandler.getToolTip)
  }

  /**
   * Tests whether setToolTip() can deal with widgets that do not support tool
   * tips.
   */
  @Test def testSetTooltipUnsupported() {
    PowerMock.replayAll()
    widgetHandler setToolTip "Some tool tip"
  }

  /**
   * Tests whether a tool tip can be obtained from a control.
   */
  @Test def testGetToolTipSupported() {
    val ctrl = PowerMock.createMock(classOf[Control])
    JavaFxGUISynchronizer.syncJavaFxInvocation { () =>
      val tip = new Tooltip(TestToolTip)
      EasyMock.expect(ctrl.getTooltip()).andReturn(tip)
      PowerMock.replayAll()
      val whandler = new JavaFxWidgetHandler(ctrl)
      assertEquals("Wrong tool tip", TestToolTip, whandler.getToolTip)
    }
    PowerMock.verifyAll()
  }

  /**
   * Tests getToolTip() if the widget supports tool tips, but none was set.
   */
  @Test def testGetToolTipSupportedNotSet() {
    val ctrl = PowerMock.createMock(classOf[Control])
    EasyMock.expect(ctrl.getTooltip()).andReturn(null)
    PowerMock.replayAll()
    val whandler = new JavaFxWidgetHandler(ctrl)
    assertNull("Got a tool tip", widgetHandler.getToolTip)
  }

  /**
   * Tests whether a tool tip can be set if the widget supports this.
   */
  @Test def testSetToolTipSupported() {
    val ctrl = PowerMock.createMock(classOf[Control])
    val answer = new FetchAnswer[Object, Tooltip]
    ctrl.setTooltip(EasyMock.anyObject(classOf[Tooltip]))
    EasyMock.expectLastCall().andAnswer(answer)
    PowerMock.replayAll()
    JavaFxGUISynchronizer.syncJavaFxInvocation { () =>
      val whandler = new JavaFxWidgetHandler(ctrl)
      whandler setToolTip TestToolTip
      assertEquals("Wrong tool tip", TestToolTip, answer.get.getText)
    }
    PowerMock.verifyAll()
  }

  /**
   * Tests whether a null tool tip can be set.
   */
  @Test def testSetToolTipSupportedNull() {
    val ctrl = PowerMock.createMock(classOf[Control])
    ctrl setTooltip null
    PowerMock.replayAll()
    val whandler = new JavaFxWidgetHandler(ctrl)
    whandler setToolTip null
    PowerMock.verifyAll()
  }

  /**
   * A test implementation of JavaFxWidgetHandler which allows mocking the
   * styles handler.
   */
  private class JavaFxWidgetHandlerTestImpl(nd: Node)
    extends JavaFxWidgetHandler(nd) {
    /** A mock styles handler. */
    var mockStylesHandler: JavaFxStylesHandler = _

    /**
     * Either returns the mock styles handler or calls the inherited method.
     */
    override def createStylesHandler() =
      if (mockStylesHandler != null) mockStylesHandler
      else super.createStylesHandler()
  }
}

object TestJavaFxWidgetHandler {
  @BeforeClass def setUpBeforeClass() {
    JavaFxTestHelper.initPlatform()
  }
}
