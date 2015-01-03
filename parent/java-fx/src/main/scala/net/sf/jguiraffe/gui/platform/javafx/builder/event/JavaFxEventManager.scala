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
package net.sf.jguiraffe.gui.platform.javafx.builder.event

import org.apache.commons.logging.LogFactory

import javafx.beans.value.ChangeListener
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import net.sf.jguiraffe.gui.builder.event.FormEventManager
import net.sf.jguiraffe.gui.builder.event.FormListenerType
import net.sf.jguiraffe.gui.builder.event.PlatformEventManager
import net.sf.jguiraffe.gui.forms.ComponentHandler

/**
 * The Java FX-specific implementation of the ''PlatformEventManager''
 * interface.
 *
 * This class implements basic functionality for registering default event
 * listeners at Java FX components. It expects that the passed in
 * ''ComponentHandler'' objects implement special event source interfaces
 * for the event types they support. The methods defined by these interfaces
 * are used to perform the actual listener registration. The listeners used
 * by this class are special adapter implementations that transform Java FX
 * events to the generic JGUIraffe event classes.
 *
 * Node that this class can only safely be used when collaborating with
 * [[net.sf.jguiraffe.gui.builder.event.FormEventManager]]. The implementation
 * relies on certain guarantees given by ''FormEventManager'' regarding
 * thread-safety or event multiplexing.
 */
class JavaFxEventManager extends PlatformEventManager {
  /** The logger. */
  private val log = LogFactory.getLog(getClass)

  /** The mapping between listener types and registration objects. */
  private val registrations = createRegistrationMap()

  /**
   * @inheritdoc This implementation determines a special handler object for
   * the registration of the specified listener type and delegates to it.
   */
  def registerListener(name: String, handler: ComponentHandler[_],
    eventManager: FormEventManager, listenerType: FormListenerType) {
    registrations(listenerType).registerListener(name, handler, eventManager)
  }

  /**
   * @inheritdoc This implementation determines a special handler object for
   * the registration of the specified listener type and delegates to it.
   */
  def unregisterListener(name: String, handler: ComponentHandler[_],
    eventManager: FormEventManager, listenerType: FormListenerType) {
    registrations(listenerType).unregisterListener(name, handler)
  }

  /**
   * Creates a map for obtaining ''ListenerRegistration'' objects for a given
   * ''FormListenerType''.
   * @return the map
   */
  private def createRegistrationMap(): Map[FormListenerType, ListenerRegistration[_]] =
    Map(FormListenerType.ACTION -> new ActionListenerRegistration,
      FormListenerType.CHANGE -> new ChangeListenerRegistration,
      FormListenerType.FOCUS -> new FocusListenerRegistration,
      FormListenerType.MOUSE -> new MouseListenerRegistration)

  /**
   * A base class of a class hierarchy for managing event listener registrations
   * of a specific type.
   *
   * This class already provides base functionality for dealing with event
   * listeners and for managing registered listeners. Concrete subclasses have
   * to implement the creation of a corresponding event adapter and the
   * actual listener registration.
   *
   * @tparam L the type of the listeners managed by this class
   */
  private abstract class ListenerRegistration[L] {
    /** The map with registered listeners. */
    private val listeners = scala.collection.mutable.Map.empty[String, L]

    /**
     * Performs an event listener registration for the specified component on
     * behalf of the event manager. This method lets concrete subclasses
     * perform the registration; the resulting listener is stored so that it
     * can be removed again later.
     * @param name the name of the component
     * @param handler the component handler
     * @param eventManager the event manager
     */
    def registerListener(name: String, handler: ComponentHandler[_],
      eventManager: FormEventManager) {
      val optListener = createAndRegisterListener(handler, name, eventManager)
      optListener foreach (listeners += name -> _)
    }

    /**
     * Removes the specified event listener. This method checks whether a
     * listener has been registered for this component. If so, the
     * ''removeListener()'' method is called.
     * @param name the name of the component
     * @param handler the component handler
     */
    def unregisterListener(name: String, handler: ComponentHandler[_]) {
      listeners.get(name).foreach(removeListener(handler, _))
    }

    /**
     * Tries to do a cast from the given component handler to an event source.
     * This method can be used to find out whether a handler supports a specific
     * event source interface. An option with the result is returned.
     * @tparam T the type of the event source to be checked for
     * @param handler the handler to be checked
     * @return an ''Option'' with the result of the type cast
     */
    protected def eventSource[T](handler: ComponentHandler[_])(implicit m: Manifest[T]): Option[T] = {
      handler match {
        case evSrc: T =>
          Some(evSrc)
        case _ =>
          log.warn("Cannot cast " + handler + " to " + m.erasure)
          None
      }
    }

    /**
     * Creates an event adapter of the supported type and registers it at
     * the given component handler if possible. If the passed in handler does
     * not support the expected event listener interface, no registration is
     * done, and result is ''None''.
     * @param handler the component handler
     * @param name the component name
     * @param eventManager the ''FormEventManager''
     * @return an ''Option'' with the registered listener
     */
    protected def createAndRegisterListener(handler: ComponentHandler[_],
      name: String, eventManager: FormEventManager): Option[L]

    /**
     * Removes the given listener from the specified component handler.
     * This method is called when an event handler is to be unregistered.
     */
    protected def removeListener(handler: ComponentHandler[_], l: L)
  }

  /**
   * A concrete ''ListenerRegistration'' class responsible for action listeners.
   */
  private class ActionListenerRegistration
    extends ListenerRegistration[EventHandler[ActionEvent]] {
    protected def createAndRegisterListener(handler: ComponentHandler[_],
      name: String, eventManager: FormEventManager): Option[EventHandler[ActionEvent]] = {
      val source = eventSource[ActionEventSource](handler)
      source map { s =>
        val adapter = new ActionEventAdapter(eventManager, handler, name,
            s.actionCommand)
        s.addActionListener(adapter)
        adapter
      }
    }

    protected def removeListener(handler: ComponentHandler[_],
      l: EventHandler[ActionEvent]) {
      eventSource[ActionEventSource](handler) foreach (_.removeActionListener(l))
    }
  }

  /**
   * A concrete ''ListenerRegistration'' class responsible for change listeners.
   */
  private class ChangeListenerRegistration
    extends ListenerRegistration[ChangeListener[AnyRef]] {
    protected def createAndRegisterListener(handler: ComponentHandler[_],
      name: String, eventManager: FormEventManager): Option[ChangeListener[AnyRef]] = {
      val source = eventSource[ChangeEventSource](handler)
      source map { s =>
        val adapter = new ChangeEventAdapter(eventManager, handler, name)
        s.observableValue.addListener(adapter)
        adapter
      }
    }

    protected def removeListener(handler: ComponentHandler[_],
      l: ChangeListener[AnyRef]) {
      val source = eventSource[ChangeEventSource](handler)
      source foreach (_.observableValue.removeListener(l))
    }
  }

  /**
   * A concrete ''ListenerRegistration'' class responsible for focus listeners.
   */
  private class FocusListenerRegistration
    extends ListenerRegistration[FocusEventAdapter] {
    protected def createAndRegisterListener(handler: ComponentHandler[_],
      name: String, eventManager: FormEventManager): Option[FocusEventAdapter] = {
      val source = eventSource[NodeEventSource](handler)
      source map { s =>
        val adapter = new FocusEventAdapter(eventManager, handler, name)
        adapter register s.sourceNode
        adapter
      }
    }

    protected def removeListener(handler: ComponentHandler[_],
      adapter: FocusEventAdapter) {
      val source = eventSource[NodeEventSource](handler)
      source foreach (s => adapter.unregister(s.sourceNode))
    }
  }

  /**
   * A concrete ''ListenerRegistration'' class responsible for mouse listeners.
   */
  private class MouseListenerRegistration
    extends ListenerRegistration[EventHandler[MouseEvent]] {
    protected def createAndRegisterListener(handler: ComponentHandler[_],
      name: String, eventManager: FormEventManager): Option[EventHandler[MouseEvent]] = {
      val source = eventSource[NodeEventSource](handler)
      source map { s =>
        val adapter = MouseEventAdapter(eventManager, handler, name)
        s.sourceNode.addEventHandler(MouseEvent.ANY, adapter)
        adapter
      }
    }

    protected def removeListener(handler: ComponentHandler[_],
      l: EventHandler[MouseEvent]) {
      val source = eventSource[NodeEventSource](handler)
      source foreach (_.sourceNode.removeEventHandler(MouseEvent.ANY, l))
    }
  }
}
