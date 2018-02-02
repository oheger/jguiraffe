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

import javafx.event.EventHandler
import javafx.event.ActionEvent

/**
 * A trait for objects that support the registration of Java FX action
 * listeners.
 *
 * Action listeners are supported by various Java FX components. However,
 * there is no inheritance hierarchy or generic mechanism for their
 * registration. Therefore, this trait is introduced. It is intended to be
 * mixed in by concrete ''ComponentHandler'' implementations which support
 * action listeners.
 */
trait ActionEventSource {
  /**
   * Adds the specified handler for action events.
   * @param handler the event handler to be added to this object
   */
  def addActionListener(handler: EventHandler[ActionEvent]): Unit

  /**
   * Removes the specified handler for action events.
   * @param handler the event handler to be removed from this object
   */
  def removeActionListener(handler: EventHandler[ActionEvent]): Unit

  /**
   * Returns a command string for the action events to be generated for this
   * source. JavaFX does not support commands in action events. So the
   * command to be stored in JGUIraffe action events has to be provided by the
   * source itself.
   * @return the action command string for generated action events
   */
  def actionCommand: String = null
}
