/*
 * Copyright 2006-2021 The JGUIraffe Team.
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
import javafx.scene.Node
import net.sf.jguiraffe.gui.builder.event.FormEventManager
import net.sf.jguiraffe.gui.builder.event.FormFocusEvent
import net.sf.jguiraffe.gui.builder.event.FormListenerType
import net.sf.jguiraffe.gui.forms.ComponentHandler

/**
 * An adapter implementation for generating JGUIraffe focus events for Java
 * FX components.
 *
 * In Java FX, there is not a direct match for focus events as used by the
 * JGUIraffe library. Rather, each node has a focus property which can be
 * monitored. This is what this adapter class does. It registers a change
 * listener at a node's focus property and generates corresponding focus
 * gained and focus lost events.
 *
 * @param sender the event sender
 * @param componentHandler the handler of the associated component
 * @param componentName the name of the associated component
 */
private class FocusEventAdapter(val sender: EventSender[FormFocusEvent],
  val componentHandler: ComponentHandler[_], val componentName: String) {
  /**
   * A change listener which will monitor the value of the focus property.
   */
  private val changeListener = new ChangeListener[java.lang.Boolean] {
    def changed(valueObs: ObservableValue[_ <: java.lang.Boolean],
      oldValue: java.lang.Boolean,
      newValue: java.lang.Boolean) {
      sender.fire(createEvent(newValue.booleanValue()))
    }
  }

  /**
   * Creates a new instance of ''FocusEventAdapter'' with a sender which targets
   * the specified ''FormEventManager''.
   * @param evMan the event manager
   * @param compHandler the handler of the associated component
   * @param compName the component name
   */
  def this(evMan: FormEventManager, compHandler: ComponentHandler[_],
    compName: String) =
    this(new EventManagerSender[FormFocusEvent](evMan, FormListenerType.FOCUS),
      compHandler, compName)

  /**
   * Removes this adapter from the specified node. No focus events will be
   * generated for this node any longer.
   * @param node the node
   */
  def unregister(node: Node) {
    node.focusedProperty().removeListener(changeListener)
  }

  /**
   * Registers this instance as focus listener at the specified node.
   * @param node the node
   */
  def register(node: Node) {
    node.focusedProperty().addListener(changeListener)
  }

  /**
   * Creates a new ''FormFocusEvent'' instance to be sent to registered
   * listeners. The event type is determined by the boolean argument.
   * @param gainedFocus flag whether the monitored component just gained or
   * lost focus
   * @return the newly created event object
   */
  private def createEvent(gainedFocus: Boolean): FormFocusEvent =
    new FormFocusEvent(this, componentHandler, componentName,
      if (gainedFocus) FormFocusEvent.Type.FOCUS_GAINED
      else FormFocusEvent.Type.FOCUS_LOST)
}
