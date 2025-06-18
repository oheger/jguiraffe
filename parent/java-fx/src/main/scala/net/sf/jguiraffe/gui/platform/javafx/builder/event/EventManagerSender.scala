/*
 * Copyright 2006-2025 The JGUIraffe Team.
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

import net.sf.jguiraffe.gui.builder.event.FormEvent
import net.sf.jguiraffe.gui.builder.event.FormEventManager
import net.sf.jguiraffe.gui.builder.event.FormListenerType

/**
 * A specialized ''EventSender'' implementation which delivers events to
 * a ''FormEventManager'' instance.
 *
 * An instance of this class is used by event adapter implementations which
 * have to collaborate with the event manager. Such adapters basically
 * transform a Java FX event to a generic ''FormEvent''. The
 * ''FormEventManager'' can process such an event. It manages event listener
 * registrations itself and ensures that the event is passed to all listeners.
 *
 * @param manager the event manager
 * @param listenerType the concrete listener type of the managed event
 */
class EventManagerSender[E <: FormEvent](val manager: FormEventManager,
  val listenerType: FormListenerType) extends EventSender[E] {
  /**
   * @inheritdoc This implementation directly delegates to the event manager.
   */
  def fire(event: => E): Unit = {
    manager.fireEvent(event, listenerType)
  }
}
