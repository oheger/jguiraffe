/*
 * Copyright 2006-2017 The JGUIraffe Team.
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

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import org.scalatest.mock.EasyMockSugar
import net.sf.jguiraffe.gui.builder.event.FormEventManager
import net.sf.jguiraffe.gui.forms.ComponentHandler
import net.sf.jguiraffe.gui.builder.event.FormListenerType
import net.sf.jguiraffe.gui.builder.event.FormActionEvent
import javafx.event.ActionEvent

/**
 * Test class for ''ActionEventAdapter''.
 */
class TestActionEventAdapter extends JUnitSuite with EasyMockSugar {
  /** The component name. */
  private val ComponentName = "testComponent"

  /** Constant for a command. */
  private val Command = "Just do it!"

  /**
   * Tests whether a correct event sender is created.
   */
  @Test def testEventSender() {
    val evMan = mock[FormEventManager]
    val compHandler = mock[ComponentHandler[_]]
    val adapter = new ActionEventAdapter(evMan, compHandler, ComponentName,
      Command)
    val sender = adapter.sender.asInstanceOf[EventManagerSender[FormActionEvent]]
    assertSame("Wrong event manager", evMan, sender.manager)
    assertEquals("Wrong listener type", FormListenerType.ACTION, sender.listenerType)
  }

  /**
   * Tests whether an event is correctly handled and transformed.
   */
  @Test def testHandleEvent() {
    val compHandler = mock[ComponentHandler[_]]
    val orgEvent = new ActionEvent
    val sender = new MockSender
    val adapter = new ActionEventAdapter(sender, compHandler, ComponentName,
      Command)
    adapter handle orgEvent
    val recEvent = sender.optEvent.get
    assertSame("Wrong source", orgEvent, recEvent.getSource)
    assertSame("Wrong component handler", compHandler, recEvent.getHandler)
    assertEquals("Wrong component name", ComponentName, recEvent.getName)
    assertEquals("Wrong command", Command, recEvent.getCommand)
  }

  /**
   * A mock sender implementation which allows obtaining the fired event.
   */
  class MockSender extends EventSender[FormActionEvent] {
    var optEvent: Option[FormActionEvent] = None

    def fire(event: => FormActionEvent) {
      assertFalse("Too many events received", optEvent.isDefined)
      optEvent = Some(event)
    }
  }
}
