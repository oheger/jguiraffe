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

import java.util.ArrayList
import java.util.Collection

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.scalatestplus.junit.JUnitSuite

/**
 * Test class for ''EventListenerList''.
 */
class TestEventListenerList extends JUnitSuite {
  /** A counter for generating 'events'. */
  private var counter = 0

  /** The object to be tested. */
  private var sender: EventListenerList[Int, Collection[Int]] = _

  @Before def setUp(): Unit = {
    sender = new EventListenerList
  }

  /**
   * A method simulating the invocation of an event listener. The numeric
   * event value is added to the listener collection.
   * @param listener simulates the listener
   * @param event the event value
   */
  private def call(listener: Collection[Int], event: Int): Unit = {
    listener add event
  }

  /**
   * Generates a new event value. This implementation just increments a counter.
   * @return the new event value
   */
  private def nextEvent() = {
    counter += 1
    counter
  }

  /**
   * Tests whether an event can be fired and propagated to a listener.
   */
  @Test def testFire(): Unit = {
    val listener = new ArrayList[Int]
    sender.addListener(listener)
    sender.fire(nextEvent(), call)
    assertEquals("Wrong number of events", 1, listener.size)
    assertEquals("Wrong event", 1, listener.get(0))
  }

  /**
   * Tests whether listeners can be added and removed.
   */
  @Test def testAddAndRemoveListeners(): Unit = {
    val l1 = new ArrayList[Int]
    val l2 = new ArrayList[Int]
    sender += l1
    sender += l2
    sender.fire(nextEvent(), call)
    sender -= l2
    sender.fire(nextEvent(), call)
    assertEquals("Wrong event count for l2", 1, l2.size)
    assertEquals("Wrong event count for l1", 2, l1.size)
    assertEquals("Wrong event", 2, l1.get(1))
  }

  /**
   * Tests that a fire() operation has no effect if no listener is registered.
   */
  @Test def testFireNoListeners(): Unit = {
    sender.addListener(null)
    sender.fire(nextEvent(), call)
    assertEquals("Event was created", 0, counter)
  }
}
