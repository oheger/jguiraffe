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

import org.junit.Before
import org.junit.Test
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.EasyMockSugar

import net.sf.jguiraffe.gui.builder.event.FormChangeEvent
import net.sf.jguiraffe.gui.builder.event.FormEventManager
import net.sf.jguiraffe.gui.builder.event.FormListenerType

/**
 * Test class for ''EventManagerSender''.
 */
class TestEventManagerSender extends JUnitSuite with EasyMockSugar {
  /** The concrete listener type. */
  private val ListenerType = FormListenerType.CHANGE

  /** A mock for the form event manager. */
  private var eventManager: FormEventManager = _

  @Before def setUp() {
    eventManager = mock[FormEventManager]
  }

  /**
   * Tests whether an event can be fired to the event manager.
   */
  @Test def testFire() {
    val event = new FormChangeEvent(this, null, "test")
    eventManager.fireEvent(event, ListenerType)
    whenExecuting(eventManager) {
      val sender = new EventManagerSender[FormChangeEvent](eventManager, ListenerType)
      sender.fire(event)
    }
  }
}
