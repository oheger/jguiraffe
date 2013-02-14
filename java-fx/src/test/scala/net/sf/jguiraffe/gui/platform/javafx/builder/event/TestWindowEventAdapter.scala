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
package net.sf.jguiraffe.gui.platform.javafx.builder.event

import org.easymock.EasyMock
import org.easymock.IAnswer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.easymock.PowerMock
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.EasyMockSugar

import javafx.event.EventHandler
import javafx.stage.{Window => FxWindow}
import javafx.stage.{WindowEvent => FxWindowEvent}
import net.sf.jguiraffe.gui.builder.window.Window
import net.sf.jguiraffe.gui.builder.window.WindowEvent
import net.sf.jguiraffe.gui.builder.window.WindowListener

/**
 * Test class for ''WindowEventAdapter''.
 */
@RunWith(classOf[PowerMockRunner])
@PrepareForTest(Array(classOf[FxWindow]))
class TestWindowEventAdapter extends JUnitSuite with EasyMockSugar {
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
   * Tests whether the sender can handle a window deactvation event.
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
   * Tests whether a window opened event can be generated.
   */
  @Test def testHandleEventOpened() {
    val wnd = mock[Window]
    val sender = new MockSender
    val adapter = new WindowEventAdapter(wnd, sender)
    val event = new FxWindowEvent(null, FxWindowEvent.WINDOW_SHOWN)
    adapter.handleEvent(event, true)
    sender.checkEvent(wnd, event, WindowEvent.Type.WINDOW_OPENED)
    adapter.handleEvent(event, false)
  }

  /**
   * Tests whether a window activated event can be generated.
   */
  @Test def testHandleEventActivated() {
    val wnd = mock[Window]
    val sender = new MockSender
    val adapter = new WindowEventAdapter(wnd, sender)
    val event = new FxWindowEvent(null, FxWindowEvent.WINDOW_SHOWN)
    adapter.handleEvent(event, true)
    sender.reset()
    adapter.handleEvent(event, true)
    sender.checkEvent(wnd, event, WindowEvent.Type.WINDOW_ACTIVATED)
  }

  /**
   * Tests whether a window deactivated event can be generated.
   */
  @Test def testHandleEventDeactivated() {
    val wnd = mock[Window]
    val sender = new MockSender
    val adapter = new WindowEventAdapter(wnd, sender)
    val event = new FxWindowEvent(null, FxWindowEvent.WINDOW_HIDDEN)
    adapter.handleEvent(event, true)
    sender.checkEvent(wnd, event, WindowEvent.Type.WINDOW_DEACTIVATED)
  }

  /**
   * Tests whether a window closing event can be generated.
   */
  @Test def testHandleEventClosing() {
    val wnd = mock[Window]
    val sender = new MockSender
    val adapter = new WindowEventAdapter(wnd, sender)
    val event = new FxWindowEvent(null, FxWindowEvent.WINDOW_CLOSE_REQUEST)
    adapter.handleEvent(event, false)
    sender.checkEvent(wnd, event, WindowEvent.Type.WINDOW_CLOSING)
  }

  /**
   * Tests whether a window closed event can be generated.
   */
  @Test def testHandleEventClosed() {
    val wnd = mock[Window]
    val sender = new MockSender
    val adapter = new WindowEventAdapter(wnd, sender)
    val event = new FxWindowEvent(null, FxWindowEvent.WINDOW_CLOSE_REQUEST)
    adapter.handleEvent(event, true)
    sender.checkEvent(wnd, event, WindowEvent.Type.WINDOW_CLOSED)
  }

  /**
   * Tests that a window hidden event is suppressed if it is produced by an
   * event filter.
   */
  @Test def testHiddenEventFilter() {
    val wnd = mock[Window]
    val sender = new MockSender
    val adapter = new WindowEventAdapter(wnd, sender)
    val event = new FxWindowEvent(null, FxWindowEvent.WINDOW_HIDDEN)
    adapter.handleEvent(event, false)
    sender.checkNoEvent()
  }

  /**
   * Tests whether an event filter works as expected.
   */
  @Test def testEventFilter() {
    val adapter = mock[WindowEventAdapter]
    val event = new FxWindowEvent(null, FxWindowEvent.WINDOW_SHOWING)
    adapter.handleEvent(event, false)
    whenExecuting(adapter) {
      val filter = WindowEventAdapter.createEventFilter(adapter)
      filter.handle(event)
    }
  }

  /**
   * Tests whether an event handler works as expected.
   */
  @Test def testEventHandler() {
    val adapter = mock[WindowEventAdapter]
    val event = new FxWindowEvent(null, FxWindowEvent.WINDOW_SHOWING)
    adapter.handleEvent(event, true)
    whenExecuting(adapter) {
      val handler = WindowEventAdapter.createEventHandler(adapter)
      handler.handle(event)
    }
  }

  /**
   * Tests the registration and unregistration of an adapter at a Java FX
   * window.
   */
  @Test def testApplyAndUnregister() {
    val fxWnd = PowerMock.createMock(classOf[FxWindow])
    val wnd = PowerMock.createMock(classOf[Window])
    val listeners = new EventListenerList[WindowEvent, WindowListener]
    var eventFilter: EventHandler[FxWindowEvent] = null
    var eventHandler: EventHandler[FxWindowEvent] = null
    fxWnd.addEventFilter(EasyMock.eq(FxWindowEvent.ANY),
      EasyMock.anyObject(classOf[EventHandler[FxWindowEvent]]))
    EasyMock.expectLastCall().andAnswer(new IAnswer[Object] {
      def answer(): Object = {
        eventFilter = EasyMock.getCurrentArguments()(1)
          .asInstanceOf[EventHandler[FxWindowEvent]]
        null
      }
    })
    fxWnd.removeEventFilter(EasyMock.eq(FxWindowEvent.ANY),
      EasyMock.anyObject(classOf[EventHandler[FxWindowEvent]]))
    EasyMock.expectLastCall().andAnswer(new IAnswer[Object] {
      def answer(): Object = {
        assertSame("Wrong filter removed", eventFilter,
          EasyMock.getCurrentArguments()(1))
        null
      }
    })
    fxWnd.addEventHandler(EasyMock.eq(FxWindowEvent.ANY),
      EasyMock.anyObject(classOf[EventHandler[FxWindowEvent]]))
    EasyMock.expectLastCall().andAnswer(new IAnswer[Object] {
      def answer(): Object = {
        eventHandler = EasyMock.getCurrentArguments()(1)
          .asInstanceOf[EventHandler[FxWindowEvent]]
        null
      }
    })
    fxWnd.removeEventHandler(EasyMock.eq(FxWindowEvent.ANY),
      EasyMock.anyObject(classOf[EventHandler[FxWindowEvent]]))
    EasyMock.expectLastCall().andAnswer(new IAnswer[Object] {
      def answer(): Object = {
        assertSame("Wrong handler removed", eventHandler,
          EasyMock.getCurrentArguments()(1))
        null
      }
    })
    PowerMock.replayAll()

    val adapter = WindowEventAdapter(fxWnd, wnd, listeners)
    adapter.unregister(fxWnd)
    assertNotNull("No event filter", eventFilter)
    assertNotNull("No event handler", eventHandler)
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
    assertSame("Wrong window", wnd, ev.getSourceWindow())
    assertSame("Wrong source", src, ev.getSource())
    assertEquals("Wrong event type", evtype, ev.getType())
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
