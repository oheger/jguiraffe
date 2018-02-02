/*
 * Copyright 2006-2018 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.platform.javafx.builder.event

import org.easymock.EasyMock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.easymock.PowerMock
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.EasyMockSugar
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.value.ChangeListener
import javafx.event.EventHandler
import javafx.stage.Stage
import javafx.stage.{Window => FxWindow}
import javafx.stage.{WindowEvent => FxWindowEvent}
import net.sf.jguiraffe.gui.builder.window.Window
import net.sf.jguiraffe.gui.builder.window.WindowEvent
import net.sf.jguiraffe.gui.builder.window.WindowListener
import net.sf.jguiraffe.gui.platform.javafx.FetchAnswer
import net.sf.jguiraffe.gui.platform.javafx.FetchAnswer.convertToOption

/**
 * Test class for ''WindowEventAdapter''.
 */
@RunWith(classOf[PowerMockRunner])
@PrepareForTest(Array(classOf[FxWindow], classOf[Stage],
  classOf[ReadOnlyBooleanProperty]))
class PMTestWindowEventAdapter extends JUnitSuite with EasyMockSugar {
  /**
   * Prepares a test for the adapter's event sender. This method creates a
   * test instance associated with an event listener list to which the given
   * mock listener has been added.
   * @param l the mock window listener
   * @param wnd a mock for the associated Window
   * @return a test adapter instance
   */
  private def prepareSenderTest(l: WindowListener, wnd: Window): WindowEventAdapter = {
    val listeners = new EventListenerList[WindowEvent, WindowListener]
    listeners += l
    WindowEventAdapter.createAdapter(wnd, listeners)
  }

  /**
   * Tests whether the sender can handle a window opened event.
   */
  @Test def testSenderOpenEvent() {
    val listener = mock[WindowListener]
    val wnd = mock[Window]
    val event = new WindowEvent(this, wnd, WindowEvent.Type.WINDOW_OPENED)
    listener.windowOpened(event)
    val adapter = prepareSenderTest(listener, wnd)
    whenExecuting(listener, wnd) {
      adapter.sender.fire(event)
    }
  }

  /**
   * Tests whether the sender can handle a window activated event.
   */
  @Test def testSenderActivateEvent() {
    val listener = mock[WindowListener]
    val wnd = mock[Window]
    val event = new WindowEvent(this, wnd, WindowEvent.Type.WINDOW_ACTIVATED)
    listener.windowActivated(event)
    val adapter = prepareSenderTest(listener, wnd)
    whenExecuting(listener, wnd) {
      adapter.sender.fire(event)
    }
  }

  /**
   * Tests whether the sender can handle a window deactivation event.
   */
  @Test def testSenderDeactivateEvent() {
    val listener = mock[WindowListener]
    val wnd = mock[Window]
    val event = new WindowEvent(this, wnd, WindowEvent.Type.WINDOW_DEACTIVATED)
    listener.windowDeactivated(event)
    val adapter = prepareSenderTest(listener, wnd)
    whenExecuting(listener, wnd) {
      adapter.sender.fire(event)
    }
  }

  /**
   * Tests whether the sender can handle a window closing event.
   */
  @Test def testSenderClosingEvent() {
    val listener = mock[WindowListener]
    val wnd = mock[Window]
    val event = new WindowEvent(this, wnd, WindowEvent.Type.WINDOW_CLOSING)
    listener.windowClosing(event)
    val adapter = prepareSenderTest(listener, wnd)
    whenExecuting(listener, wnd) {
      adapter.sender.fire(event)
    }
  }

  /**
   * Tests whether the sender can handle a window closed event.
   */
  @Test def testSenderClosedEvent() {
    val listener = mock[WindowListener]
    val wnd = mock[Window]
    val event = new WindowEvent(this, wnd, WindowEvent.Type.WINDOW_CLOSED)
    listener.windowClosed(event)
    val adapter = prepareSenderTest(listener, wnd)
    whenExecuting(listener, wnd) {
      adapter.sender.fire(event)
    }
  }

  /**
   * Tests whether the sender can handle an icon event.
   */
  @Test def testSenderIconifiedEvent() {
    val listener = mock[WindowListener]
    val wnd = mock[Window]
    val event = new WindowEvent(this, wnd, WindowEvent.Type.WINDOW_ICONIFIED)
    listener.windowIconified(event)
    val adapter = prepareSenderTest(listener, wnd)
    whenExecuting(listener, wnd) {
      adapter.sender.fire(event)
    }
  }

  /**
   * Tests whether the sender can handle a de-icon event.
   */
  @Test def testSenderDeiconifiedEvent() {
    val listener = mock[WindowListener]
    val wnd = mock[Window]
    val event = new WindowEvent(this, wnd, WindowEvent.Type.WINDOW_DEICONIFIED)
    listener.windowDeiconified(event)
    val adapter = prepareSenderTest(listener, wnd)
    whenExecuting(listener, wnd) {
      adapter.sender.fire(event)
    }
  }

  /**
   * Tests whether a window opened event can be generated.
   */
  @Test def testHandleEventOpened() {
    val wnd = mock[Window]
    val sender = new MockSender
    val adapter = new WindowEventAdapter(wnd, sender)
    val event = new FxWindowEvent(null, FxWindowEvent.WINDOW_SHOWN)
    adapter.handleEvent(event)
    sender.checkEvent(wnd, event, WindowEvent.Type.WINDOW_OPENED)
  }

  /**
    * Tests that only the first SHOWN event is mapped to an OPENED event.
    */
  @Test def testHandleMultipleShownEvents(): Unit = {
    val wnd = mock[Window]
    val sender = new MockSender
    val adapter = new WindowEventAdapter(wnd, sender)
    val event = new FxWindowEvent(null, FxWindowEvent.WINDOW_SHOWN)
    adapter.handleEvent(event)
    sender.checkEvent(wnd, event, WindowEvent.Type.WINDOW_OPENED)
    adapter.handleEvent(event)
    sender.checkNoEvent()
  }

  /**
   * Tests whether a window HIDING event is ignored.
   */
  @Test def testHandleEventClosing() {
    val wnd = mock[Window]
    val sender = new MockSender
    val adapter = new WindowEventAdapter(wnd, sender)
    val event = new FxWindowEvent(null, FxWindowEvent.WINDOW_HIDING)
    adapter.handleEvent(event)
    sender.checkNoEvent()
  }

  /**
   * Tests whether a window HIDDEN event is ignored.
   */
  @Test def testHandleEventClosed() {
    val wnd = mock[Window]
    val sender = new MockSender
    val adapter = new WindowEventAdapter(wnd, sender)
    val event = new FxWindowEvent(null, FxWindowEvent.WINDOW_HIDDEN)
    adapter.handleEvent(event)
    sender.checkNoEvent()
  }

  /**
   * Tests whether an event which is not transformed can be handled correctly.
   */
  @Test def testHandleEventUnsupported() {
    val wnd = mock[Window]
    val sender = new MockSender
    val adapter = new WindowEventAdapter(wnd, sender)
    val event = new FxWindowEvent(null, FxWindowEvent.WINDOW_CLOSE_REQUEST)
    adapter.handleEvent(event)
    sender.checkNoEvent()
  }

  /**
   * Tests whether the registered event handler works as expected.
   */
  @Test def testEventHandler() {
    val adapter = mock[WindowEventAdapter]
    val event = new FxWindowEvent(null, FxWindowEvent.WINDOW_SHOWING)
    adapter.handleEvent(event)
    whenExecuting(adapter) {
      val handler = WindowEventAdapter.createEventHandler(adapter)
      handler.handle(event)
    }
  }

  /**
   * Tests whether the focus listener works correctly.
   */
  @Test def testFocusListener() {
    val wnd = mock[Window]
    val sender = new MockSender
    val adapter = new WindowEventAdapter(wnd, sender)
    val listener = WindowEventAdapter.createFocusListener(adapter)
    listener.changed(null, false, true)
    sender.checkEvent(wnd, adapter, WindowEvent.Type.WINDOW_ACTIVATED)
    listener.changed(null, true, false)
    sender.checkEvent(wnd, adapter, WindowEvent.Type.WINDOW_DEACTIVATED)
  }

  /**
   * Tests whether the listener on the icon property works correctly.
   */
  @Test def testIconListener() {
    val wnd = mock[Window]
    val sender = new MockSender
    val adapter = new WindowEventAdapter(wnd, sender)
    val listener = WindowEventAdapter.createIconListener(adapter)
    listener.changed(null, false, true)
    sender.checkEvent(wnd, adapter, WindowEvent.Type.WINDOW_ICONIFIED)
    listener.changed(null, true, false)
    sender.checkEvent(wnd, adapter, WindowEvent.Type.WINDOW_DEICONIFIED)
  }

  /**
   * Tests the registration and unregistration of an adapter at a Java FX
   * window.
   */
  @Test def testApplyAndUnregister() {
    val fxWnd = PowerMock.createMock(classOf[Stage])
    val wnd = PowerMock.createMock(classOf[Window])
    val focusProp = PowerMock.createMock(classOf[ReadOnlyBooleanProperty])
    val iconProp = PowerMock.createMock(classOf[ReadOnlyBooleanProperty])
    val listeners = new EventListenerList[WindowEvent, WindowListener]

    val addHandlerAnswer =
      new FetchAnswer[Object, EventHandler[FxWindowEvent]](argIdx = 1)
    fxWnd.addEventHandler(EasyMock.eq(FxWindowEvent.ANY),
      EasyMock.anyObject(classOf[EventHandler[FxWindowEvent]]))
    EasyMock.expectLastCall().andAnswer(addHandlerAnswer)
    val removeHandlerAnswer =
      new FetchAnswer[Object, EventHandler[FxWindowEvent]](argIdx = 1)
    fxWnd.removeEventHandler(EasyMock.eq(FxWindowEvent.ANY),
      EasyMock.anyObject(classOf[EventHandler[FxWindowEvent]]))
    EasyMock.expectLastCall().andAnswer(removeHandlerAnswer)

    EasyMock.expect(fxWnd.focusedProperty()).andReturn(focusProp).anyTimes()
    val addFocusAnswer = new FetchAnswer[Object, ChangeListener[java.lang.Boolean]]
    focusProp.addListener(EasyMock.anyObject(classOf[ChangeListener[java.lang.Boolean]]))
    EasyMock.expectLastCall().andAnswer(addFocusAnswer)
    val removeFocusAnswer = new FetchAnswer[Object, ChangeListener[java.lang.Boolean]]
    focusProp.removeListener(EasyMock.anyObject(classOf[ChangeListener[java.lang.Boolean]]))
    EasyMock.expectLastCall().andAnswer(removeFocusAnswer)

    EasyMock.expect(fxWnd.iconifiedProperty()).andReturn(iconProp).anyTimes()
    val addIconAnswer = new FetchAnswer[Object, ChangeListener[java.lang.Boolean]]
    iconProp.addListener(EasyMock.anyObject(classOf[ChangeListener[java.lang.Boolean]]))
    EasyMock.expectLastCall().andAnswer(addIconAnswer)
    val removeIconAnswer = new FetchAnswer[Object, ChangeListener[java.lang.Boolean]]
    iconProp.removeListener(EasyMock.anyObject(classOf[ChangeListener[java.lang.Boolean]]))
    EasyMock.expectLastCall().andAnswer(removeIconAnswer)

    PowerMock.replayAll()
    val adapter = WindowEventAdapter(fxWnd, wnd, listeners)
    adapter.unregister(fxWnd)
    assertSame("Wrong event handler unregistered", addHandlerAnswer.get,
      removeHandlerAnswer.get)
    assertSame("Wrong focus listener unregistered", addFocusAnswer.get,
      removeFocusAnswer.get)
    assertSame("Wrong icon listener unregistered", addIconAnswer.get,
      removeIconAnswer.get)
    PowerMock.verifyAll()
  }
}

/**
 * A mock implementation of EventSender for window events.
 */
class MockSender extends EventSender[WindowEvent] {
  /** The event that was fired. */
  private var event: Option[WindowEvent] = None

  /**
   * Checks the received event.
   * @param wnd the window
   * @param src the source of the event
   * @param evtype the expected event type
   */
  def checkEvent(wnd: Window, src: Object, evtype: WindowEvent.Type) {
    val ev = event.get
    assertSame("Wrong window", wnd, ev.getSourceWindow)
    assertSame("Wrong source", src, ev.getSource)
    assertEquals("Wrong event type", evtype, ev.getType)
    event = None
  }

  /**
   * Checks that no event was received.
   */
  def checkNoEvent() {
    assertTrue("Got an event", event.isEmpty)
  }

  /**
   * Resets an already received event.
   */
  def reset() {
    event = None
  }

  /**
   * @inheritdoc This implementation just stores the event.
   */
  def fire(ev: => WindowEvent) {
    assertTrue("Too many events received", event.isEmpty)
    event = Some(ev)
  }
}
