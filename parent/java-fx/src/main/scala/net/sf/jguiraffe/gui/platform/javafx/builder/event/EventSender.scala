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

/**
 * A trait defining a generic event sender interface.
 *
 * The idea behind this trait is that it can be used by adapter implementations
 * converting between platform-specific events and generic events. Such
 * adapters somehow receive events and then have to propagate them to
 * registered listeners. The way listeners are organized is specific to a
 * concrete implementation.
 *
 * There are mainly two different flavors of event listeners/receivers in
 * JGUIraffe:
 * - classic event listeners can be added to a component in an unlimited
 * number
 * - the ''FormEventManager'' class can act as event receiver; it provides an
 * implementation of event multi-plexing
 *
 * This trait should be generic enough to support both flavors of event
 * listeners. Concrete implementations have to ensure that events are
 * correctly passed.
 *
 * @param E the type of events handled by this object
 */
trait EventSender[E] {
  /**
   * Fires an event. The event is passed to the method, a concrete
   * implementation has to do whatever is necessary to deliver it to the
   * correct receivers. Note that the event is a by-name parameter; an
   * implementation should evaluate it once only if listeners are available for
   * this event. If there are no listeners, the event must not be accessed.
   * That way a conversion of the event is only done if there are actually
   * receivers.
   * @param event the event to be fired
   */
  def fire(event: => E): Unit
}
