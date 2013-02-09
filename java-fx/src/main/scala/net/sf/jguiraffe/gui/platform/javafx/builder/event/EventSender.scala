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

/**
 * A trait defining a generic event sender interface.
 *
 * The idea behind this trait is that it can be used by adapter implementations
 * converting between platform-specific events and generic events. Such
 * adapters somehow receive events and then have to propagate them to
 * registered listeners. The way listeners are organized is specific to a
 * concrete implementation.
 *
 * @param E the type of events handled by this object
 * @param L the type of event listeners handled by this object
 */
trait EventSender[E, L] {
  /**
   * Fires an event. A concrete implementation iterates over all registered
   * event listeners and invokes the passed in function on each listener.
   * Note that the event is a by-name parameter; an implementation should
   * evaluate it once only if listeners are available for this event.
   * @param event the event to be fired
   * @param f the function for invoking the event listener
   */
  def fire(event: => E, f: (L, E) => Unit): Unit
}
