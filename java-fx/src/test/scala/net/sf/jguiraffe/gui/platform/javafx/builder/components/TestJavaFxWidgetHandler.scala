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

import org.easymock.EasyMock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.easymock.PowerMock
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.scalatest.junit.JUnitSuite
import javafx.scene.Node
import javafx.scene.control.Control
import javafx.scene.control.Tooltip
import net.sf.jguiraffe.gui.builder.components.Color

/**
 * Test class for ''JavaFxWidgetHandler''.
 */
@RunWith(classOf[PowerMockRunner])
@PrepareForTest(Array(classOf[Node], classOf[Control], classOf[Tooltip]))
class TestJavaFxWidgetHandler extends JUnitSuite {
  /** Constant for a test color. */
  private val TestColor = Color.newLogicInstance("someTestColor")

  /** Constant for a test styles definition. */
  private val StylesDef = "A test styles definition."

  /** Constant for a tool tip text. */
  private val TestToolTip = "My Test Tool Tip"

  /** A mock for the wrapped Java FX node. */
  private var node: Node = _

  /** A mock tool tip factory. */
  private var toolTipFactory: ToolTipFactory = _

  /** The handler to be tested.*/
  private var widgetHandler: JavaFxWidgetHandlerTestImpl = _

  @Before def setUp() {
    node = PowerMock.createMock(classOf[Node])
    toolTipFactory = PowerMock.createMock(classOf[ToolTipFactory])
    widgetHandler = new JavaFxWidgetHandlerTestImpl(node, toolTipFactory)
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
    val tip = PowerMock.createMock(classOf[Tooltip])
    EasyMock.expect(ctrl.getTooltip()).andReturn(tip)
    EasyMock.expect(tip.getText).andReturn(TestToolTip)
    PowerMock.replayAll()
    val whandler = new JavaFxWidgetHandler(ctrl, toolTipFactory)
    assertEquals("Wrong tool tip", TestToolTip, whandler.getToolTip)
    PowerMock.verifyAll()
  }

  /**
   * Tests getToolTip() if the widget supports tool tips, but none was set.
   */
  @Test def testGetToolTipSupportedNotSet() {
    val ctrl = PowerMock.createMock(classOf[Control])
    EasyMock.expect(ctrl.getTooltip()).andReturn(null)
    PowerMock.replayAll()
    val whandler = new JavaFxWidgetHandler(ctrl, toolTipFactory)
    assertNull("Got a tool tip", widgetHandler.getToolTip)
  }

  /**
   * Tests whether a tool tip can be set if the widget supports this.
   */
  @Test def testSetToolTipSupported() {
    val ctrl = PowerMock.createMock(classOf[Control])
    val tip = PowerMock.createMock(classOf[Tooltip])
    EasyMock.expect(toolTipFactory.createToolTip(TestToolTip)).andReturn(tip)
    ctrl setTooltip tip
    PowerMock.replayAll()
    val whandler = new JavaFxWidgetHandler(ctrl, toolTipFactory)
    whandler setToolTip TestToolTip
    PowerMock.verifyAll()
  }

  /**
   * Tests whether a null tool tip can be set.
   */
  @Test def testSetToolTipSupportedNull() {
    val ctrl = PowerMock.createMock(classOf[Control])
    ctrl setTooltip null
    PowerMock.replayAll()
    val whandler = new JavaFxWidgetHandler(ctrl, toolTipFactory)
    whandler setToolTip null
    PowerMock.verifyAll()
  }

  /**
   * A test implementation of JavaFxWidgetHandler which allows mocking the
   * styles handler.
   */
  private class JavaFxWidgetHandlerTestImpl(nd: Node, toolTipFactory: ToolTipFactory)
    extends JavaFxWidgetHandler(nd, toolTipFactory) {
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
