/*
 * Copyright 2006-2015 The JGUIraffe Team.
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

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.stage.Stage
import javafx.stage.{WindowEvent => FxWindowEvent}
import net.sf.jguiraffe.gui.builder.window.Window
import net.sf.jguiraffe.gui.builder.window.WindowEvent
import net.sf.jguiraffe.gui.builder.window.WindowListener

/**
 * An adapter implementation mapping Java FX Window events to corresponding
 * JGUIraffe events.
 *
 * Unfortunately, there is a mismatch between Java FX window events and
 * JGUIraffe window events. Therefore, no one to one mapping is possible.
 * The following mappings are implemented by this class:
 *
 * - WINDOW_SHOWN => WINDOW_OPENED
 * - WINDOW_HIDING => WINDOW_CLOSING
 * - WINDOW_HIDDEN => WINDOW_CLOSED
 *
 * Java FX does not define events for window activation or deactivation, and
 * events related to the window's current icon state. However, these events
 * correspond to changes on some properties of the window. So listeners
 * on the Java FX window's ''focused'' and ''iconified'' properties are
 * registered for generating such events.
 *
 * @param window the wrapped JGUIraffe window
 * @param sender the object for transporting events
 */
class WindowEventAdapter private[event] (val window: Window,
  val sender: EventSender[WindowEvent]) {
  /** The event handler serving this adapter. */
  @volatile private var eventHandler: EventHandler[FxWindowEvent] = _

  /** A listener for reacting on focus changes. */
  @volatile private var focusListener: ChangeListener[java.lang.Boolean] = _

  /** A listener for reacting on icon state changes. */
  @volatile private var iconListener: ChangeListener[java.lang.Boolean] = _

  /**
   * Maps the given Java FX event to a corresponding JGUIraffe event and
   * passes it to the event sender.
   * @param event the event to be converted
   */
  def handleEvent(event: FxWindowEvent) {
    event.getEventType match {
      case FxWindowEvent.WINDOW_SHOWN =>
        fire(event, WindowEvent.Type.WINDOW_OPENED)

      case FxWindowEvent.WINDOW_HIDDEN =>
        fire(event, WindowEvent.Type.WINDOW_CLOSED)

      case FxWindowEvent.WINDOW_HIDING =>
        fire(event, WindowEvent.Type.WINDOW_CLOSING)

      case _ =>
    }
  }

  /**
   * Removes this adapter from the specified window. This method removes
   * the event filter and the event handler that were created during
   * registration.
   * @param wnd the Java FX window
   */
  def unregister(wnd: Stage) {
    wnd.removeEventHandler(FxWindowEvent.ANY, eventHandler)
    wnd.focusedProperty().removeListener(focusListener)
    wnd.iconifiedProperty().removeListener(iconListener)
  }

  /**
   * Register all required event listeners at the given stage. This method
   * is called when the adapter is initialized.
   * @param wnd the window to register at
   */
  private def register(wnd: Stage) {
    eventHandler = WindowEventAdapter.createEventHandler(this)
    wnd.addEventHandler(FxWindowEvent.ANY, eventHandler)
    focusListener = WindowEventAdapter.createFocusListener(this)
    wnd.focusedProperty().addListener(focusListener)
    iconListener = WindowEventAdapter.createIconListener(this)
    wnd.iconifiedProperty().addListener(iconListener)
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

  /**
   * Transforms a focus changed event into a window event.
   * @param focusGained a flag whether this window gained the focus
   */
  private def handleFocusEvent(focusGained: Boolean) {
    sender fire new WindowEvent(this, window,
      if (focusGained) WindowEvent.Type.WINDOW_ACTIVATED
      else WindowEvent.Type.WINDOW_DEACTIVATED)
  }

  /**
   * Transforms a notification of the icon state property into a window event.
   * @param iconified a flag whether the icon state was entered
   */
  private def handleIconEvent(iconified: Boolean) {
    sender fire new WindowEvent(this, window,
      if (iconified) WindowEvent.Type.WINDOW_ICONIFIED
      else WindowEvent.Type.WINDOW_DEICONIFIED)
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
  def apply(fxwnd: Stage, wnd: Window,
    listeners: EventListenerList[WindowEvent, WindowListener]): WindowEventAdapter = {
    val adapter = createAdapter(wnd, listeners)
    adapter.register(fxwnd)
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
   * Creates the event handler for intercepting window events. This object is
   * responsible for events in the bubbling phase.
   * @param adapter the adapter to be served by the handler
   * @return the event handler
   */
  private[event] def createEventHandler(adapter: WindowEventAdapter): EventHandler[FxWindowEvent] =
    new EventHandler[FxWindowEvent] {
      def handle(event: FxWindowEvent) {
        adapter.handleEvent(event)
      }
    }

  /**
   * Creates a listener on the window's focused property for generating
   * activation events.
   * @param adapter the window event adapter
   * @return the listener
   */
  private[event] def createFocusListener(adapter: WindowEventAdapter): ChangeListener[java.lang.Boolean] =
    new ChangeListener[java.lang.Boolean] {
      def changed(valueObs: ObservableValue[_ <: java.lang.Boolean],
        oldValue: java.lang.Boolean,
        newValue: java.lang.Boolean) {
        adapter.handleFocusEvent(newValue)
      }
    }

  /**
   * Creates a listener on the window's iconified property for generating
   * events related to the icon state.
   * @param adapter the window event adapter
   * @return the listener
   */
  private[event] def createIconListener(adapter: WindowEventAdapter): ChangeListener[java.lang.Boolean] =
    new ChangeListener[java.lang.Boolean] {
      def changed(valueObs: ObservableValue[_ <: java.lang.Boolean],
        oldValue: java.lang.Boolean,
        newValue: java.lang.Boolean) {
        adapter.handleIconEvent(newValue)
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
      case WindowEvent.Type.WINDOW_ICONIFIED =>
        l.windowIconified(e)
      case WindowEvent.Type.WINDOW_DEICONIFIED =>
        l.windowDeiconified(e)
      case WindowEvent.Type.WINDOW_CLOSING =>
        l.windowClosing(e)
      case WindowEvent.Type.WINDOW_CLOSED =>
        l.windowClosed(e)
    }
  }
}
