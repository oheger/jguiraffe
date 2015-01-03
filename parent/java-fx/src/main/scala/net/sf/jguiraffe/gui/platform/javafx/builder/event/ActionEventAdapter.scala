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

import javafx.event.ActionEvent
import javafx.event.EventHandler
import net.sf.jguiraffe.gui.builder.event.FormActionEvent
import net.sf.jguiraffe.gui.builder.event.FormEventManager
import net.sf.jguiraffe.gui.builder.event.FormListenerType
import net.sf.jguiraffe.gui.forms.ComponentHandler

/**
 * An adapter implementation mapping Java FX action events to equivalent
 * JGUIraffe action events.
 *
 * Action events are always component-specific. It is up to a concrete
 * implementation of ''ComponentHandler'' how a listener is registered at this
 * component.
 *
 * Action listeners are only added via the event manager. Therefore, this
 * class is used internally only by the Java FX-specific implementation of
 * the [[net.sf.jguiraffe.gui.builder.event.PlatformEventManager]] trait.
 *
 * @param sender the object for sending events
 * @param componentHandler the associated component handler
 * @param componentName the name of the associated component
 * @param command an optional command string
 */
private class ActionEventAdapter(val sender: EventSender[FormActionEvent],
  val componentHandler: ComponentHandler[_], val componentName: String,
  val command: String) extends EventHandler[ActionEvent] {
  /**
   * Creates a new instance of ''ActionEventAdapter'' using a sender which
   * passes events directly to the specified ''FormEventManager''.
   * @param evMan the ''FormEventManager'' as receiver of events
   * @param compHandler the associated component handler
   * @param compName the name of the associated component
   * @param cmd an optional command string
   */
  def this(evMan: FormEventManager, compHandler: ComponentHandler[_],
    compName: String, cmd: String) =
    this(new EventManagerSender[FormActionEvent](evMan, FormListenerType.ACTION),
      compHandler, compName, cmd)

  /**
   * Processes the specified Java FX action event. The event is converted to a
   * ''FormActionEvent'' and passed to the associated sender.
   * @param event the action event to be handled
   */
  override def handle(event: ActionEvent) {
    sender fire (convertEvent(event))
  }

  /**
   * Converts the specified Java FX action event into a JGUIraffe
   * ''FormActionEvent''.
   * @param event the original Java FX event
   * @return the converted event
   */
  private def convertEvent(event: ActionEvent): FormActionEvent =
    new FormActionEvent(event, componentHandler, componentName, command)
}
