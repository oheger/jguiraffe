/*
 * Copyright 2006-2022 The JGUIraffe Team.
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

import java.util.EnumSet
import javafx.event.EventHandler
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import net.sf.jguiraffe.gui.builder.event.FormEventManager
import net.sf.jguiraffe.gui.builder.event.FormListenerType
import net.sf.jguiraffe.gui.builder.event.FormMouseEvent
import net.sf.jguiraffe.gui.builder.event.FormMouseListener
import net.sf.jguiraffe.gui.builder.event.Modifiers
import net.sf.jguiraffe.gui.forms.ComponentHandler

/**
 * An adapter implementation mapping Java FX mouse events to corresponding
 * JGUIraffe events.
 *
 * @param sender the object for sending events
 * @param componentHandler the optional handler of the component this adapter
 * is associated with
 * @param componentName the optional name of the component this adapter is
 * associated with
 */
class MouseEventAdapter private (val sender: EventSender[FormMouseEvent],
  val componentHandler: ComponentHandler[_],
  val componentName: String) extends EventHandler[MouseEvent] {
  /**
   * Processes the specified Java FX event. This implementation converts the
   * event to a JGUIraffe event and passes it to the current event sender.
   * @param event the event to be processed
   */
  override def handle(event: MouseEvent) {
    event.getEventType match {
      case MouseEvent.MOUSE_ENTERED =>
        fire(event, FormMouseEvent.Type.MOUSE_ENTERED)

      case MouseEvent.MOUSE_EXITED =>
        fire(event, FormMouseEvent.Type.MOUSE_EXITED)

      case MouseEvent.MOUSE_PRESSED =>
        fire(event, FormMouseEvent.Type.MOUSE_PRESSED)

      case MouseEvent.MOUSE_RELEASED =>
        fire(event, FormMouseEvent.Type.MOUSE_RELEASED)

      case MouseEvent.MOUSE_CLICKED =>
        val evType = if (event.getClickCount() < 2) FormMouseEvent.Type.MOUSE_CLICKED
        else FormMouseEvent.Type.MOUSE_DOUBLE_CLICKED
        fire(event, evType)

      case _ => // ignore some unsupported types
    }
  }

  /**
   * Creates a new JGUIraffe mouse event based on the passed in Java FX event.
   * @param event the original Java FX event
   * @param eventtype the target event type
   * @return the converted event
   */
  private[event] def convertEvent(event: MouseEvent,
    eventtype: FormMouseEvent.Type): FormMouseEvent =
    new FormMouseEvent(event, componentHandler, componentName, eventtype,
      event.getX.toInt, event.getY.toInt,
      MouseEventAdapter.convertMouseButton(event.getButton),
      convertModifiers(event))

  /**
   * Converts the specified Java FX event to a JGUIraffe event and passes it
   * to the event sender.
   * @param event the original Java FX event
   * @param eventtype the type of the converted event
   */
  private def fire(event: MouseEvent, eventtype: FormMouseEvent.Type) {
    sender fire convertEvent(event, eventtype)
  }

  /**
   * Converts the modifiers of the source event to a corresponding set.
   * @param event the original event
   * @return the set with modifier constants
   */
  private def convertModifiers(event: MouseEvent): java.util.Set[Modifiers] = {
    val set = EnumSet.noneOf(classOf[Modifiers])
    if (event.isAltDown) {
      set.add(Modifiers.ALT)
    }
    if (event.isControlDown) {
      set.add(Modifiers.CONTROL)
    }
    if (event.isMetaDown) {
      set.add(Modifiers.META)
    }
    if (event.isShiftDown) {
      set.add(Modifiers.SHIFT)
    }
    set
  }
}

/**
 * The companion object of ''MouseEventAdapter''.
 */
object MouseEventAdapter {
  /** A map for converting mouse button constants. */
  private final val ButtonMap = createMouseButtonMap()

  /**
   * Creates a new instance of ''MouseEventAdapter'' which sends events to a
   * ''FormEventManager'' instance.
   * @param evMan the target event manager
   * @param compHandler the optional ''ComponentHandler'' the adapter is
   * associated with
   * @param compName the name of the associated component (if any)
   * @return the newly created instance
   */
  def apply(evMan: FormEventManager, compHandler: ComponentHandler[_],
    compName: String): MouseEventAdapter = {
    new MouseEventAdapter(sender = new EventManagerSender(evMan, FormListenerType.MOUSE),
      componentHandler = compHandler, componentName = compName)
  }

  /**
   * Creates a new instance of ''MouseEventAdapter'' which sends events to the
   * specified list of event listeners.
   * @param listeners the event listeners
   * @param compHandler the optional ''ComponentHandler'' the adapter is
   * associated with
   * @param compName the name of the associated component (if any)
   * @return the newly created instance
   */
  def apply(listeners: EventListenerList[FormMouseEvent, FormMouseListener],
    compHandler: ComponentHandler[_] = null,
    compName: String = null): MouseEventAdapter = {
    new MouseEventAdapter(sender = createEventSender(listeners),
      componentHandler = compHandler, componentName = compName)
  }

  /**
   * Creates the sender object which passes events to registered listeners.
   * @param listeners the list with event listeners
   * @return the sender object
   */
  private def createEventSender(
    listeners: EventListenerList[FormMouseEvent, FormMouseListener]): EventSender[FormMouseEvent] = {
    new EventSender[FormMouseEvent] {
      def fire(event: => FormMouseEvent) {
        listeners.fire(event, callMouseListener)
      }
    }
  }

  /**
   * Passes the given mouse event to the correct listener method.
   * @param l the mouse listener to call
   * @param e the mouse event
   */
  private def callMouseListener(l: FormMouseListener, e: FormMouseEvent) {
    e.getType() match {
      case FormMouseEvent.Type.MOUSE_CLICKED =>
        l.mouseClicked(e)
      case FormMouseEvent.Type.MOUSE_DOUBLE_CLICKED =>
        l.mouseDoubleClicked(e)
      case FormMouseEvent.Type.MOUSE_ENTERED =>
        l.mouseEntered(e)
      case FormMouseEvent.Type.MOUSE_EXITED =>
        l.mouseExited(e)
      case FormMouseEvent.Type.MOUSE_PRESSED =>
        l.mousePressed(e)
      case FormMouseEvent.Type.MOUSE_RELEASED =>
        l.mouseReleased(e)
    }
  }

  /**
   * Converts the given Java FX mouse button constant to the corresponding
   * JGUIraffe code.
   * @param orgBtn the Java FX mouse button constant
   * @return the corresponding JGUIraffe code
   */
  private def convertMouseButton(orgBtn: MouseButton): Int =
    ButtonMap(orgBtn)

  /**
   * Creates a map associating Java FX constants for mouse buttons with
   * JGUIraffe identifiers.
   * @return the conversion map
   */
  private def createMouseButtonMap() =
    Map(MouseButton.NONE -> FormMouseEvent.NO_BUTTON,
      MouseButton.PRIMARY -> FormMouseEvent.BUTTON1,
      MouseButton.MIDDLE -> FormMouseEvent.BUTTON2,
      MouseButton.SECONDARY -> FormMouseEvent.BUTTON3)
}
