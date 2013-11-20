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
package net.sf.jguiraffe.gui.platform.javafx.builder.window

import org.easymock.EasyMock
import org.easymock.IAnswer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.easymock.PowerMock
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.scalatest.junit.JUnitSuite
import javafx.application.Platform
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.scene.Group
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.FlowPane
import javafx.scene.text.Text
import javafx.stage.Stage
import javafx.stage.{ WindowEvent => FxWindowEvent }
import net.sf.jguiraffe.gui.builder.event.FormMouseEvent
import net.sf.jguiraffe.gui.builder.event.FormMouseListener
import net.sf.jguiraffe.gui.builder.window.Window
import net.sf.jguiraffe.gui.builder.window.WindowClosingStrategy
import net.sf.jguiraffe.gui.builder.window.WindowEvent
import net.sf.jguiraffe.gui.builder.window.WindowListener
import net.sf.jguiraffe.gui.platform.javafx.FetchAnswer
import net.sf.jguiraffe.gui.platform.javafx.FetchAnswer.convertToOption
import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import net.sf.jguiraffe.gui.platform.javafx.builder.event.EventListenerList
import net.sf.jguiraffe.gui.platform.javafx.layout.ContainerWrapper
import net.sf.jguiraffe.gui.layout.UnitSizeHandler

/**
 * Test class for ''JavaFxWindow''.
 */
@RunWith(classOf[PowerMockRunner])
@PrepareForTest(Array(classOf[Stage], classOf[ReadOnlyBooleanProperty],
  classOf[MouseEvent], classOf[Scene]))
class TestJavaFxWindow extends JUnitSuite {
  /** A mock for the wrapped stage. */
  private var stage: Stage = _

  /** A mock for the window listeners. */
  private var windowListeners: EventListenerList[WindowEvent, WindowListener] = _

  /** A mock for the mouse listeners. */
  private var mouseListeners: EventListenerList[FormMouseEvent, FormMouseListener] = _

  @Before def setUp() {
    stage = PowerMock.createMock(classOf[Stage])
  }

  /**
   * Creates a test window object.
   * @return the test window
   */
  private def createWindow(): JavaFxWindow = {
    windowListeners = PowerMock.createMock(
      classOf[EventListenerList[WindowEvent, WindowListener]])
    mouseListeners = PowerMock.createMock(
      classOf[EventListenerList[FormMouseEvent, FormMouseListener]])
    new JavaFxWindow(stage, windowListeners, mouseListeners, new ContainerWrapper)
  }

  /**
   * Tests whether the X position can be queried.
   */
  @Test def testGetXPos() {
    val x = 3.1415
    EasyMock.expect(stage.getX).andReturn(x)
    val wnd = createWindow()
    PowerMock.replayAll()
    assertEquals("Wrong X position", x.toInt, wnd.getXPos)
    PowerMock.verifyAll()
  }

  /**
   * Tests whether the Y position can be queried.
   */
  @Test def testGetYPos() {
    val y = 100.0
    EasyMock.expect(stage.getY).andReturn(y)
    val wnd = createWindow()
    PowerMock.replayAll()
    assertEquals("Wrong Y position", y.toInt, wnd.getYPos)
    PowerMock.verifyAll()
  }

  /**
   * Tests whether the window's width can be queried.
   */
  @Test def testGetWidth() {
    val width = 640.4
    EasyMock.expect(stage.getWidth).andReturn(width)
    val wnd = createWindow()
    PowerMock.replayAll()
    assertEquals("Wrong width", width.toInt, wnd.getWidth)
    PowerMock.verifyAll()
  }

  /**
   * Tests whether the window's height can be queried.
   */
  @Test def testGetHeight() {
    val height = 480.0
    EasyMock.expect(stage.getHeight).andReturn(height)
    val wnd = createWindow()
    PowerMock.replayAll()
    assertEquals("Wrong height", height.toInt, wnd.getHeight)
    PowerMock.verifyAll()
  }

  /**
   * Tests whether bounds can be set.
   */
  @Test def testSetBounds() {
    val x = 100
    val y = 150
    val width = 641
    val height = 333
    stage.setX(x)
    stage.setY(y)
    stage.setWidth(width)
    stage.setHeight(height)
    val wnd = createWindow()
    PowerMock.replayAll()
    wnd.setBounds(x, y, width, height)
    PowerMock.verifyAll()
  }

  /**
   * Tests whether the window's title can be set.
   */
  @Test def testGetTitle() {
    val title = "WindowTitle"
    EasyMock.expect(stage.getTitle()).andReturn(title)
    val wnd = createWindow()
    PowerMock.replayAll()
    assertEquals("Wrong title", title, wnd.getTitle)
    PowerMock.verifyAll()
  }

  /**
   * Tests whether the window's title can be set.
   */
  @Test def testSetTitle() {
    val title = "MyWindow'sTitle"
    stage.setTitle(title)
    val wnd = createWindow()
    PowerMock.replayAll()
    wnd.setTitle(title)
    PowerMock.verifyAll()
  }

  /**
   * Tests whether the visible property can be queried.
   */
  @Test def testIsVisible() {
    EasyMock.expect(stage.isShowing).andReturn(true)
    EasyMock.expect(stage.isShowing).andReturn(false)
    val wnd = createWindow()
    PowerMock.replayAll()
    assertTrue("Wrong result (1)", wnd.isVisible)
    assertFalse("Wrong result (2)", wnd.isVisible)
    PowerMock.verifyAll()
  }

  /**
   * Tests whether the window can be made visible.
   */
  @Test def testSetVisibleTrue() {
    stage.show()
    val wnd = createWindow()
    PowerMock.replayAll()
    wnd.setVisible(true)
    PowerMock.verifyAll()
  }

  /**
   * Tests whether the window can be made invisible.
   */
  @Test def testSetVisibleFalse() {
    stage.hide()
    val wnd = createWindow()
    PowerMock.replayAll()
    wnd.setVisible(false)
    PowerMock.verifyAll()
  }

  /**
   * Tests whether the parent window can be set and queried.
   */
  @Test def testParentWindow() {
    val parent = PowerMock.createMock(classOf[Window])
    val wnd = createWindow()
    PowerMock.replayAll()
    assertNull("Got a parent window initially", wnd.getParentWindow)
    wnd.setParentWindow(parent)
    assertSame("Wrong parent window", parent, wnd.getParentWindow)
  }

  /**
   * Tests whether the window's controller object can be set and queried.
   */
  @Test def testController() {
    val wnd = createWindow()
    assertNull("Got a controller", wnd.windowController)
    wnd.windowController = this
    assertSame("Controller was not set", this, wnd.getWindowController)
  }

  /**
   * Tests whether a window can be opened. This is expected to be a blocking
   * call on the Java FX thread.
   */
  @Test def testOpen() {
    val scene = PowerMock.createMock(classOf[Scene])
    EasyMock.expect(stage.getScene).andReturn(scene)
    val rootAnswer = new FetchAnswer[Object, Parent]
    scene.setRoot(EasyMock.anyObject(classOf[Parent]))
    EasyMock.expectLastCall().andAnswer(rootAnswer)
    stage.show()
    EasyMock.expectLastCall().andAnswer(new IAnswer[Object] {
      def answer(): Object = {
        assertTrue("Not in dispatch thread", Platform.isFxApplicationThread())
        null
      }
    })
    val comp = new Text("Test")
    val wnd = createWindow()
    wnd.rootContainer.addComponent(comp, null)
    PowerMock.replayAll()
    wnd.open()
    PowerMock.verifyAll()
    val rootPane = rootAnswer.get.asInstanceOf[FlowPane]
    assertTrue("Root pane not initialized", rootPane.getChildren.contains(comp))
  }

  /**
   * Tests whether a default closing strategy is set.
   */
  @Test def testDefaultClosingStrategy() {
    val wnd = createWindow()
    val strat = wnd.getWindowClosingStrategy
    assertTrue("Cannot close", strat.canClose(wnd))
  }

  /**
   * Tests a forced close operation.
   */
  @Test def testCloseForce() {
    val strat = PowerMock.createMock(classOf[WindowClosingStrategy])
    val wnd = createWindow()
    stage.close()
    PowerMock.replayAll()
    wnd.setWindowClosingStrategy(strat)
    assertTrue("Wrong result", wnd.close(true))
    assertTrue("Closing not permitted", wnd.closingPermitted)
  }

  /**
   * Tests a close operation allowed by the strategy.
   */
  @Test def testCloseStrategyAllows() {
    val strat = PowerMock.createMock(classOf[WindowClosingStrategy])
    val wnd = createWindow()
    EasyMock.expect(strat.canClose(wnd)).andReturn(true)
    stage.close()
    PowerMock.replayAll()
    wnd.setWindowClosingStrategy(strat)
    assertTrue("Wrong result", wnd.close(false))
    assertTrue("Closing not permitted", wnd.closingPermitted)
    PowerMock.verifyAll()
  }

  /**
   * Tests a close operation that is vetoed by the closing strategy.
   */
  @Test def testCloseStrategyForbids() {
    val strat = PowerMock.createMock(classOf[WindowClosingStrategy])
    val wnd = createWindow()
    EasyMock.expect(strat.canClose(wnd)).andReturn(false)
    wnd.setWindowClosingStrategy(strat)
    PowerMock.replayAll()
    assertFalse("Wrong result", wnd.close(false))
    assertFalse("Closing not permitted", wnd.closingPermitted)
    PowerMock.verifyAll()
  }

  /**
   * Prepares a test of the apply() method. Adds some expectations to the window
   * mock for event listener registrations.
   * @param withListeners if '''true''' mocks are prepared for default event
   * listener registrations
   */
  private def prepareApplyTest(withListeners: Boolean = false) {
    val propFocus = PowerMock.createNiceMock(classOf[ReadOnlyBooleanProperty])
    val propIcon = PowerMock.createNiceMock(classOf[ReadOnlyBooleanProperty])
    EasyMock.expect(stage.focusedProperty()).andReturn(propFocus).anyTimes()
    EasyMock.expect(stage.iconifiedProperty()).andReturn(propIcon).anyTimes()

    if (withListeners) {
      expectWindowListener()
      expectMouseListener()
      expectClosingListener()
    }
  }

  /**
   * Expects a window listener to be registered at the mock window.
   */
  private def expectWindowListener() {
    stage.addEventHandler(EasyMock.eq(FxWindowEvent.ANY),
      EasyMock.anyObject(classOf[EventHandler[FxWindowEvent]]))
  }

  /**
   * Expects a mouse listener to be registered at the mock window.
   */
  private def expectMouseListener() {
    stage.addEventHandler(EasyMock.eq(MouseEvent.ANY),
      EasyMock.anyObject(classOf[EventHandler[MouseEvent]]))
  }

  /**
   * Expects a listener for window closing requests to be added to the mock
   * window.
   */
  private def expectClosingListener() {
    stage.addEventFilter(EasyMock.eq(FxWindowEvent.WINDOW_CLOSE_REQUEST),
      EasyMock.anyObject(classOf[EventHandler[FxWindowEvent]]))
  }

  /**
   * Tests whether window listeners can be added to the window.
   */
  @Test def testWindowListenerRegistration() {
    val wndListener = PowerMock.createMock(classOf[WindowListener])
    prepareApplyTest()
    expectWindowListener()
    val answer = new FetchAnswer[Object, EventHandler[FxWindowEvent]](argIdx = 1)
    EasyMock.expectLastCall().andAnswer(answer)
    expectMouseListener()
    expectClosingListener()
    val eventAnswer = new FetchAnswer[Object, WindowEvent]
    wndListener.windowOpened(EasyMock.anyObject(classOf[WindowEvent]))
    EasyMock.expectLastCall().andAnswer(eventAnswer)
    PowerMock.replayAll()
    val wnd = JavaFxWindow(stage)
    wnd.addWindowListener(wndListener)
    answer.get.handle(new FxWindowEvent(stage, FxWindowEvent.WINDOW_SHOWN))
    wnd.removeWindowListener(wndListener)
    answer.get.handle(new FxWindowEvent(stage, FxWindowEvent.WINDOW_HIDING))
    PowerMock.verifyAll()
    assertEquals("Wrong event type", WindowEvent.Type.WINDOW_OPENED,
      eventAnswer.get.getType())
  }

  /**
   * Tests whether mouse listeners can be added to the window.
   */
  @Test def testMouseListenerRegistration() {
    val mouseListener = PowerMock.createMock(classOf[FormMouseListener])
    val event = PowerMock.createNiceMock(classOf[MouseEvent])
    prepareApplyTest()
    expectWindowListener()
    val listenerAnswer = new FetchAnswer[Object, EventHandler[MouseEvent]](argIdx = 1)
    expectMouseListener()
    EasyMock.expectLastCall().andAnswer(listenerAnswer)
    expectClosingListener()
    val evType: EventType[MouseEvent] = MouseEvent.MOUSE_CLICKED
    event.getEventType()
    EasyMock.expectLastCall().andReturn(evType).anyTimes()
    EasyMock.expect(event.getButton()).andReturn(MouseButton.PRIMARY).anyTimes()
    val eventAnswer = new FetchAnswer[Object, FormMouseEvent]
    mouseListener.mouseClicked(EasyMock.anyObject(classOf[FormMouseEvent]))
    EasyMock.expectLastCall().andAnswer(eventAnswer)
    PowerMock.replayAll()
    val wnd = JavaFxWindow(stage)
    wnd.addMouseListener(mouseListener)
    listenerAnswer.get.handle(event)
    wnd.removeMouseListener(mouseListener)
    listenerAnswer.get.handle(event)
    PowerMock.verifyAll()
    assertEquals("Wrong event type", FormMouseEvent.Type.MOUSE_CLICKED,
      eventAnswer.get.getType())
  }

  /**
   * Tests whether the closing listener forbids closing the window if the
   * closing strategy did not allow closing.
   */
  @Test def testClosingListenerVeto() {
    val event = PowerMock.createMock(classOf[FxWindowEvent])
    prepareApplyTest()
    expectWindowListener()
    expectMouseListener()
    val listenerAnswer =
      new FetchAnswer[Object, EventHandler[FxWindowEvent]](argIdx = 1)
    expectClosingListener()
    EasyMock.expectLastCall().andAnswer(listenerAnswer)
    event.getEventType()
    EasyMock.expectLastCall().andReturn(FxWindowEvent.WINDOW_CLOSE_REQUEST).anyTimes()
    event.consume()
    PowerMock.replayAll()
    val wnd = JavaFxWindow(stage)
    listenerAnswer.get.handle(event)
    PowerMock.verifyAll()
  }

  /**
   * Tests the closing listener if the window can be closed.
   */
  @Test def testClosingListenerAllow() {
    val event = PowerMock.createMock(classOf[FxWindowEvent])
    prepareApplyTest()
    expectWindowListener()
    expectMouseListener()
    val listenerAnswer =
      new FetchAnswer[Object, EventHandler[FxWindowEvent]](argIdx = 1)
    expectClosingListener()
    EasyMock.expectLastCall().andAnswer(listenerAnswer)
    event.getEventType()
    EasyMock.expectLastCall().andReturn(FxWindowEvent.WINDOW_CLOSE_REQUEST).anyTimes()
    stage.close()
    PowerMock.replayAll()
    val wnd = JavaFxWindow(stage)
    assertTrue("Wrong result", wnd.close(false))
    listenerAnswer.get.handle(event)
    PowerMock.verifyAll()
  }

  /**
   * Tests whether the root container can be queried.
   */
  @Test def testGetRootContainerDefault() {
    prepareApplyTest(true)
    PowerMock.replayAll()
    val wnd = JavaFxWindow(stage)
    assertTrue("Wrong root container",
      wnd.getRootContainer.isInstanceOf[ContainerWrapper])
  }

  /**
   * Tests whether a size handler can be passed to the window which is then
   * propagated to the root container.
   */
  @Test def testGetRootContainerWithSizeHandler() {
    val sizeHandler = PowerMock.createMock(classOf[UnitSizeHandler])
    prepareApplyTest(true)
    PowerMock.replayAll()
    val wnd = JavaFxWindow(stage, Some(sizeHandler))
    val root = wnd.getRootContainer
    assertSame("Wrong size handler", sizeHandler, root.sizeHandler.get)
  }
}

object TestJavaFxWindow {
  @BeforeClass def setUpBeforeClass() {
    JavaFxTestHelper.initPlatform()
  }
}
