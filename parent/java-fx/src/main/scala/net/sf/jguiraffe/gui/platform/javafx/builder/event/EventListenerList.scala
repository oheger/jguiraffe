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

import java.util.concurrent.atomic.AtomicReference

/**
 * A class managing a list of event listeners.
 *
 * This class provides methods for adding and removing event listeners of
 * the managed type. These methods can be called from an arbitrary thread.
 * When an event is fired, all listeners registered at this point of time
 * are invoked.
 */
class EventListenerList[E, L <: AnyRef] {
  /** Stores the list with registered listeners. */
  private final val listeners = new AtomicReference[List[L]](Nil)

  /**
   * Adds the given event listener to this object. If a '''null''' listener
   * is passed in, this method does not have any effect.
   * @param l the listener to be added
   */
  def addListener(l: L) {
    if (l != null) {
      var done = false
      do {
        val oldListeners = listeners.get
        val newListeners = l :: oldListeners
        done = listeners.compareAndSet(oldListeners, newListeners)
      } while (!done)
    }
  }

  /**
   * Adds the given listener to this object; this is a short cut for
   * '''addListener(l)'''.
   * @param l the listener to be added
   */
  def +=(l: L) {
    addListener(l)
  }

  /**
   * Removes the given event listener from this object.
   * @param l the listener to be removed
   */
  def removeListener(l: L) {
    var done = false
    do {
      val oldListeners = listeners.get
      val newListeners = oldListeners filter (_.ne(l))
      done = listeners.compareAndSet(oldListeners, newListeners)
    } while (!done)
  }

  /**
   * Removes the given event listener from this object; this is a short cut for
   * '''removeListener(l)'''.
   * @param l the listener to be removed
   */
  def -=(l: L) {
    removeListener(l)
  }

  /**
   * @inheritdoc This implementation iterates over all currently registered
   * event listeners and invokes the passed in function on each. The event is
   * only accessed if listeners are available; because it is created lazily
   * no instance needs to be created if there are no listeners.
   */
  def fire(event: => E, f: (L, E) => Unit) {
    val list = listeners.get
    if (!list.isEmpty) {
      val evObj = event
      list foreach (f(_, evObj))
    }
  }
}
