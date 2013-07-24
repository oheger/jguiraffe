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

import org.easymock.EasyMock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.easymock.PowerMock
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.scalatest.junit.JUnitSuite
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.Node
import net.sf.jguiraffe.gui.builder.event.FormEventManager
import net.sf.jguiraffe.gui.builder.event.FormFocusEvent
import net.sf.jguiraffe.gui.builder.event.FormListenerType
import net.sf.jguiraffe.gui.forms.ComponentHandler
import net.sf.jguiraffe.gui.platform.javafx.FetchAnswer

/**
 * Test class for ''FocusEventAdapter''.
 */
@RunWith(classOf[PowerMockRunner])
@PrepareForTest(Array(classOf[Node], classOf[ReadOnlyBooleanProperty]))
class TestFocusEventAdapter extends JUnitSuite {
  /** Constant for a component name. */
  private val ComponentName = "MyTestComponent"

  /** A mock Node object. */
  private var node: Node = _

  /** A mock for the focus property. */
  private var focusProperty: ReadOnlyBooleanProperty = _

  @Before def setUp() {
    node = PowerMock.createMock(classOf[Node])
  }

  /**
   * Prepares the mock node to expect the registration of a change listener
   * at the focus property. An answer object is returned allowing access to
   * this change listener.
   * @return the answer object
   */
  private def prepareListenerReg(): FetchAnswer[Object, ChangeListener[java.lang.Boolean]] = {
    focusProperty = PowerMock.createMock(classOf[ReadOnlyBooleanProperty])
    EasyMock.expect(node.focusedProperty).andReturn(focusProperty).anyTimes()
    val a = new FetchAnswer[Object, ChangeListener[java.lang.Boolean]]
    focusProperty.addListener(EasyMock.anyObject(classOf[ChangeListener[java.lang.Boolean]]))
    EasyMock.expectLastCall().andAnswer(a)
    a
  }

  /**
   * Tests whether a correct default sender is created.
   */
  @Test def testDefaultSender() {
    val evMan = PowerMock.createMock(classOf[FormEventManager])
    val compHandler = PowerMock.createMock(classOf[ComponentHandler[_]])
    PowerMock.replayAll()
    val adapter = new FocusEventAdapter(evMan, compHandler, ComponentName)
    assertSame("Wrong component handler", compHandler, adapter.componentHandler)
    assertEquals("Wrong name", ComponentName, adapter.componentName)
    val sender = adapter.sender.asInstanceOf[EventManagerSender[FormFocusEvent]]
    assertSame("Wrong event manager", evMan, sender.manager)
    assertEquals("Wrong listener type", FormListenerType.FOCUS, sender.listenerType)
  }

  /**
   * Tests whether focus events are converted and transferred to listeners.
   */
  @Test def testEventTransformation() {
    val obsVal = PowerMock.createMock(classOf[ObservableValue[java.lang.Boolean]])
    val compHandler = PowerMock.createMock(classOf[ComponentHandler[_]])
    val a = prepareListenerReg()
    val sender = new TestSender
    PowerMock.replayAll()
    val adapter = new FocusEventAdapter(sender, compHandler, ComponentName)
    adapter.register(node)
    a.value.changed(obsVal, false, true)
    a.value.changed(obsVal, true, false)
    sender.checkEvent(adapter, FormFocusEvent.Type.FOCUS_GAINED)
    sender.checkEvent(adapter, FormFocusEvent.Type.FOCUS_LOST)
    sender.verify()
    PowerMock.verifyAll()
  }

  /**
   * Tests whether the adapter can be removed again as focus listener.
   */
  @Test def testUnregister() {
    val compHandler = PowerMock.createMock(classOf[ComponentHandler[_]])
    val sender = new TestSender
    val aReg = prepareListenerReg()
    val aUnreg = new FetchAnswer[Object, ChangeListener[java.lang.Boolean]]
    focusProperty.removeListener(EasyMock.anyObject(classOf[ChangeListener[java.lang.Boolean]]))
    EasyMock.expectLastCall().andAnswer(aUnreg)
    PowerMock.replayAll()
    val adapter = new FocusEventAdapter(sender, compHandler, ComponentName)
    adapter.register(node)
    adapter.unregister(node)
    assertSame("Wrong listener removed", aReg.value, aUnreg.value)
    PowerMock.verifyAll()
  }

  /**
   * A test sender implementation which stores all received events in an
   * internal list.
   */
  private class TestSender extends EventSender[FormFocusEvent] {
    /** The list with the events received. */
    private var events: List[FormFocusEvent] = Nil

    def fire(event: => FormFocusEvent) {
      events = events :+ event
    }

    /**
     * Checks the next received event.
     * @param adapter the adapter object
     * @param evType the expected event type
     */
    def checkEvent(adapter: FocusEventAdapter, evType: FormFocusEvent.Type) {
      val event = events.head
      events = events.tail
      assertSame("Wrong component handler", adapter.componentHandler,
        event.getHandler)
      assertEquals("Wrong component name", adapter.componentName,
        event.getName)
      assertEquals("Wrong event type", evType, event.getType)
    }

    /**
     * Verifies that no more events have been received.
     */
    def verify() {
      assertTrue("Got more events: " + events, events.isEmpty)
    }
  }
}
