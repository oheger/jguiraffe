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

import javafx.event.EventHandler
import javafx.stage.{Window => FxWindow}
import javafx.stage.{WindowEvent => FxWindowEvent}
import net.sf.jguiraffe.gui.builder.window.Window
import net.sf.jguiraffe.gui.builder.window.WindowEvent
import net.sf.jguiraffe.gui.builder.window.WindowListener

/**
 * An adapter implementation mapping Java FX Window events to corresponding
 * JGUIraffe events.
 *
 * @param window the wrapped JGUIraffe window
 * @param sender the object for transporting events
 */
class WindowEventAdapter private[event] (val window: Window,
  val sender: EventSender[WindowEvent]) {
  /** The event filter serving this adapter. */
  @volatile private var eventFilter: EventHandler[FxWindowEvent] = _

  /** The event handler serving this adapter. */
  @volatile private var eventHandler: EventHandler[FxWindowEvent] = _

  /** A flag whether this is the initial activation. */
  private var opened = false

  /**
   * Maps the given Java FX event to a corresponding JGUIraffe event and
   * passes it to the event sender.
   * @param event the event to be converted
   * @param bubbling '''true''' if the event was received in bubbling phase,
   * '''false''' otherwise
   */
  def handleEvent(event: FxWindowEvent, bubbling: Boolean) {
    if (bubbling) {
      event.getEventType match {
        case FxWindowEvent.WINDOW_SHOWN =>
          val evtype = if (opened) WindowEvent.Type.WINDOW_ACTIVATED
          else {
            opened = true
            WindowEvent.Type.WINDOW_OPENED
          }
          fire(event, evtype)

        case FxWindowEvent.WINDOW_HIDDEN =>
          fire(event, WindowEvent.Type.WINDOW_DEACTIVATED)

        case FxWindowEvent.WINDOW_CLOSE_REQUEST =>
          fire(event, WindowEvent.Type.WINDOW_CLOSED)

        case _ =>
      }
    } else {
      if (FxWindowEvent.WINDOW_CLOSE_REQUEST == event.getEventType) {
        fire(event, WindowEvent.Type.WINDOW_CLOSING)
      }
    }
  }

  /**
   * Removes this adapter from the specified window. This method removes
   * the event filter and the event handler that were created during
   * registration.
   * @param wnd the Java FX window
   */
  def unregister(wnd: FxWindow) {
    wnd.removeEventFilter(FxWindowEvent.ANY, eventFilter)
    wnd.removeEventHandler(FxWindowEvent.ANY, eventHandler)
  }

  /**
   * Creates a new JGUIraffe event based on the given Java FX event.
   * @param srcEvent the original FX event
   * @param evtype the type of the resulting event
   * @return the newly created JGUIraffe event
   */
  private def convertEvent(srcEvent: FxWindowEvent, evtype: WindowEvent.Type) =
    new WindowEvent(srcEvent, window, evtype)

  /**
   * Converts the given source event to a JGUIraffe event and passes it to
   * the registered listeners.
   * @param srcEvent the original FX event
   * @param evtype the type of the resulting event
   */
  private def fire(srcEvent: FxWindowEvent, evtype: WindowEvent.Type) {
    sender fire convertEvent(srcEvent, evtype)
  }
}

/**
 * The companion object of ''WindowEventAdapter''.
 */
object WindowEventAdapter {
  /**
   * Creates a new ''WindowEventAdapter'' object converting window events for
   * the specified Java FX window. The adapter is fully initialized and
   * registered as listener at the FX window.
   * @param fxwnd the Java FX window
   * @param wnd the JGUIraffe window
   * @param listeners the list with event listeners; here JGUIraffe window
   * listeners can be registered
   */
  def apply(fxwnd: FxWindow, wnd: Window,
    listeners: EventListenerList[WindowEvent, WindowListener]): WindowEventAdapter = {
    val adapter = createAdapter(wnd, listeners)
    val filter = createEventFilter(adapter)
    fxwnd.addEventFilter(FxWindowEvent.ANY, filter)
    val handler = createEventHandler(adapter)
    fxwnd.addEventHandler(FxWindowEvent.ANY, handler)
    adapter.eventFilter = filter
    adapter.eventHandler = handler
    adapter
  }

  /**
   * Creates a new instance of ''WindowAdapter'' which is associated with the
   * given event listener list. The new instance is not yet registered at a
   * Java FX ''Window'' object however.
   * @param wnd the ''Window'' acting as the source of events
   * @param listeners the object managing the adapter's event listeners
   */
  def createAdapter(wnd: Window,
    listeners: EventListenerList[WindowEvent, WindowListener]): WindowEventAdapter = {
    new WindowEventAdapter(wnd, createEventSender(listeners))
  }

  /**
   * Creates the event filter for intercepting window events. This object is
   * responsible for events in the capturing phase.
   * @param adapter the adapter to be served by the filter
   * @return the event filter
   */
  private[event] def createEventFilter(adapter: WindowEventAdapter) =
    new EventHandler[FxWindowEvent] {
      def handle(event: FxWindowEvent) {
        adapter.handleEvent(event, false)
      }
    }

  /**
   * Creates the event handler for intercepting window events. This object is
   * responsible for events in the bubbling phase.
   * @param adapter the adapter to be served by the handler
   * @return the event handler
   */
  private[event] def createEventHandler(adapter: WindowEventAdapter) =
    new EventHandler[FxWindowEvent] {
      def handle(event: FxWindowEvent) {
        adapter.handleEvent(event, true)
      }
    }

  /**
   * Creates the sender object which passes events to registered listeners.
   * @param listeners the list with event listeners
   * @return the sender object
   */
  private def createEventSender(
    listeners: EventListenerList[WindowEvent, WindowListener]): EventSender[WindowEvent] = {
    new EventSender[WindowEvent] {
      def fire(event: => WindowEvent) {
        listeners.fire(event, callWindowListener)
      }
    }
  }

  /**
   * Invokes the correct method of the event list based on the window's event
   * type.
   * @param l the window listener
   * @param e the window event
   */
  private def callWindowListener(l: WindowListener, e: WindowEvent) {
    e.getType match {
      case WindowEvent.Type.WINDOW_OPENED =>
        l.windowOpened(e)
      case WindowEvent.Type.WINDOW_ACTIVATED =>
        l.windowActivated(e)
      case WindowEvent.Type.WINDOW_DEACTIVATED =>
        l.windowDeactivated(e)
      case WindowEvent.Type.WINDOW_CLOSING =>
        l.windowClosing(e)
      case WindowEvent.Type.WINDOW_CLOSED =>
        l.windowClosed(e)
    }
  }
}
