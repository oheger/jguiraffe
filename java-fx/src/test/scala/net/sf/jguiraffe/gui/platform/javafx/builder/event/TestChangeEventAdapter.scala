/**
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

import org.junit.Test
import org.junit.Assert._
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.EasyMockSugar
import net.sf.jguiraffe.gui.builder.event.FormEventManager
import net.sf.jguiraffe.gui.forms.ComponentHandler
import net.sf.jguiraffe.gui.builder.event.FormChangeEvent
import net.sf.jguiraffe.gui.builder.event.FormListenerType
import javafx.beans.property.StringProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue

/**
 * Test class for ''ChangeEventAdapter''.
 */
class TestChangeEventAdapter extends JUnitSuite with EasyMockSugar {
  /** Constant for a component name. */
  private val ComponentName = "TestChangeComponent"

  /**
   * Tests whether a sender is created referencing the current event manager.
   */
  @Test def testDefaultSender() {
    val evMan = mock[FormEventManager]
    val compHandler = mock[ComponentHandler[_]]
    val adapter = new ChangeEventAdapter(evMan, compHandler, ComponentName)
    val sender = adapter.sender.asInstanceOf[EventManagerSender[FormChangeEvent]]
    assertSame("Wrong event manager", evMan, sender.manager)
    assertEquals("Wrong listener type", FormListenerType.CHANGE, sender.listenerType)
  }

  /**
   * Tests whether a change event is correctly processed.
   */
  @Test def testConvertEvent() {
    val compHandler = mock[ComponentHandler[_]]
    val sender = new MockSender
    val obsValue = new SimpleStringProperty
    val adapter = new ChangeEventAdapter(sender, compHandler, ComponentName)
    obsValue addListener adapter

    obsValue set "Some value"
    val event = sender.optEvent.get
    assertSame("Wrong component handler", compHandler, event.getHandler)
    assertEquals("Wrong component name", ComponentName, event.getName)
    assertSame("Wrong source", obsValue, event.getSource)
  }

  /**
   * A mock sender for checking the generated change event.
   */
  class MockSender extends EventSender[FormChangeEvent] {
    /** Stores the fired event. */
    var optEvent: Option[FormChangeEvent] = None

    def fire(event: => FormChangeEvent) {
      assertFalse("Too many events fired", optEvent.isDefined)
      optEvent = Some(event)
    }
  }
}
