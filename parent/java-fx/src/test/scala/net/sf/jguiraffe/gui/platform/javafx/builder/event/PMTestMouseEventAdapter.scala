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

import javafx.event.EventType
import javafx.scene.input.{MouseButton, MouseEvent}

import net.sf.jguiraffe.gui.builder.event.{FormEventManager, FormListenerType, FormMouseEvent, FormMouseListener, Modifiers}
import net.sf.jguiraffe.gui.forms.ComponentHandler
import net.sf.jguiraffe.gui.platform.javafx.FetchAnswer
import org.easymock.EasyMock
import org.junit.Assert._
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.easymock.PowerMock
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.scalatestplus.junit.JUnitSuite
import org.scalatestplus.easymock.EasyMockSugar

/**
 * Test class for ''MouseEventAdapter''.
 */
@RunWith(classOf[PowerMockRunner])
@PrepareForTest(Array(classOf[MouseEvent]))
class PMTestMouseEventAdapter extends JUnitSuite with EasyMockSugar {
  /** A default component name. */
  private val ComponentName = "SomeTestComponentName"

  /** A default x coordinate for a mouse event. */
  private val EventX = 128

  /** A default y coordinate for a mouse event. */
  private val EventY = 222

  /**
   * Tests whether an instance can be created which serves a form event
   * manager.
   */
  @Test def testCreateForEventManager(): Unit = {
    val evMan = mock[FormEventManager]
    val compHandler = mock[ComponentHandler[_]]
    val adapter = MouseEventAdapter(evMan, compHandler, ComponentName)
    assertEquals("Wrong component name", ComponentName, adapter.componentName)
    assertSame("Wrong component handler", compHandler, adapter.componentHandler)
    val sender = adapter.sender.asInstanceOf[EventManagerSender[FormMouseEvent]]
    assertSame("Wrong event manager", evMan, sender.manager)
    assertEquals("Wrong listener type", FormListenerType.MOUSE, sender.listenerType)
  }

  /**
   * Creates a mock four a mouse event with some default properties.
   * @param evtype the event type
   * @param btn the mouse button that was pressed
   * @return the mock for the event
   */
  private def createEvent(evtype: EventType[_ <: MouseEvent],
    btn: MouseButton = MouseButton.PRIMARY): MouseEvent = {
    val event = PowerMock.createNiceMock(classOf[MouseEvent])
    event.getEventType
    EasyMock.expectLastCall().andReturn(evtype).anyTimes()
    EasyMock.expect(event.getButton).andReturn(btn).anyTimes()
    EasyMock.expect(event.getX).andReturn(EventX).anyTimes()
    EasyMock.expect(event.getY).andReturn(EventY).anyTimes()
    event
  }

  /**
   * Tests a converted mouse event if there are no modifiers.
   */
  @Test def testConvertEventNoModifiers(): Unit = {
    val event = createEvent(MouseEvent.MOUSE_CLICKED)
    val evMan = PowerMock.createMock(classOf[FormEventManager])
    PowerMock.replayAll()
    val adapter = MouseEventAdapter(evMan, null, ComponentName)
    val convEvent = adapter.convertEvent(event, FormMouseEvent.Type.MOUSE_CLICKED)
    assertTrue("Got modifiers", convEvent.getModifiers.isEmpty)
  }

  /**
   * Tests whether modifiers of a mouse event are converted correctly.
   */
  @Test def testConvertEventWithModifiers(): Unit = {
    val evMan = PowerMock.createMock(classOf[FormEventManager])
    val event = createEvent(MouseEvent.MOUSE_CLICKED)
    EasyMock.expect(event.isAltDown).andReturn(true)
    EasyMock.expect(event.isShiftDown).andReturn(true)
    EasyMock.expect(event.isControlDown).andReturn(true)
    EasyMock.expect(event.isMetaDown).andReturn(true)
    PowerMock.replayAll()
    val adapter = MouseEventAdapter(evMan, null, ComponentName)
    val convEvent = adapter.convertEvent(event, FormMouseEvent.Type.MOUSE_CLICKED)
    val modifiers = convEvent.getModifiers
    assertTrue("No Alt modifier", modifiers.contains(Modifiers.ALT))
    assertTrue("No Shift modifier", modifiers.contains(Modifiers.SHIFT))
    assertTrue("No Meta modifier", modifiers.contains(Modifiers.META))
    assertTrue("No Control modifier", modifiers.contains(Modifiers.CONTROL))
  }

  /**
   * Helper method for testing whether the pressed mouse buttons in a mouse
   * event are correctly detected.
   */
  private def checkMouseButton(orgBtn: MouseButton, expResult: Int): Unit = {
    val evMan = PowerMock.createMock(classOf[FormEventManager])
    val event = createEvent(MouseEvent.MOUSE_CLICKED, orgBtn)
    PowerMock.replayAll()
    val adapter = MouseEventAdapter(evMan, null, ComponentName)
    val convEvent = adapter.convertEvent(event, FormMouseEvent.Type.MOUSE_CLICKED)
    assertEquals("Wrong converted button", expResult, convEvent.getButton)
  }

  /**
   * Tests whether a mouse event with no button pressed is correctly converted.
   */
  @Test def testConvertEventNoButton(): Unit = {
    checkMouseButton(MouseButton.NONE, FormMouseEvent.NO_BUTTON)
  }

  /**
   * Tests whether the left button is detected when converting a mouse event.
   */
  @Test def testConvertEventWithLeftButton(): Unit = {
    checkMouseButton(MouseButton.PRIMARY, FormMouseEvent.BUTTON1)
  }

  /**
   * Tests whether the right button is detected when converting a mouse event.
   */
  @Test def testConvertEventWithRightButton(): Unit = {
    checkMouseButton(MouseButton.SECONDARY, FormMouseEvent.BUTTON3)
  }

  /**
   * Tests whether the middle button is detected when converting a mouse event.
   */
  @Test def testConvertEventWithMiddleButton(): Unit = {
    checkMouseButton(MouseButton.MIDDLE, FormMouseEvent.BUTTON2)
  }

  /**
   * Creates an answer object which expects a mouse event as method argument
   * and stores it in the global option variable.
   * @return the answer object
   */
  private def eventAnswer(): FetchAnswer[Object, FormMouseEvent] =
    new FetchAnswer

  /**
   * Creates an answer object for processing an event and registers it to the
   * last mock invocation.
   * @return the answer object which can be used for obtaining the event
   */
  private def registerEventAnswer(): FetchAnswer[Object, FormMouseEvent] = {
    val a = eventAnswer()
    EasyMock.expectLastCall().andAnswer(a)
    a
  }

  /**
   * Performs a test whether a mouse event is passed to a listener. This
   * method expects a mock listener which is already prepared to expect a
   * specific listener method to be invoked, Then firing of the given event
   * is simulated. It is checked whether a converted event was received with
   * the expected standard properties.
   * @param listener the mock for the listener
   * @param event the event to be simulated
   * @param a the answer for retrieving the event parameter object
   */
  private def checkMouseListener(listener: FormMouseListener, event: MouseEvent,
      a: FetchAnswer[_, FormMouseEvent]): Unit = {
    val compHandler = PowerMock.createMock(classOf[ComponentHandler[_]])
    val listeners = new EventListenerList[FormMouseEvent, FormMouseListener]
    listeners += listener
    val adapter = MouseEventAdapter(listeners, compHandler, ComponentName)
    PowerMock.replayAll()
    adapter handle event
    assertFalse("No event received", a.isEmpty)
    val ev = a.value
    assertSame("Wrong component handler", compHandler, ev.getHandler)
    assertEquals("Wrong component name", ComponentName, ev.getName)
    assertSame("Wrong event source", event, ev.getSource)
    assertEquals("Wrong X", EventX, ev.getX)
    assertEquals("Wrong Y", EventY, ev.getY)
    PowerMock.verifyAll()
  }

  /**
   * Convenience method for creating a mock mouse event listener.
   * @return the mock for the listener
   */
  private def mockListener(): FormMouseListener =
    PowerMock.createMock(classOf[FormMouseListener])

  /**
   * Tests whether a mouse entered event can be processed.
   */
  @Test def testMouseEntered(): Unit = {
    val listener = mockListener()
    listener.mouseEntered(EasyMock.anyObject(classOf[FormMouseEvent]))
    val a = registerEventAnswer()
    checkMouseListener(listener, createEvent(MouseEvent.MOUSE_ENTERED), a)
  }

  /**
   * Tests whether a mouse exited event can be processed.
   */
  @Test def testMouseExisted(): Unit = {
    val listener = mockListener()
    listener.mouseExited(EasyMock.anyObject(classOf[FormMouseEvent]))
    val a = registerEventAnswer()
    checkMouseListener(listener, createEvent(MouseEvent.MOUSE_EXITED), a)
  }

  /**
   * Tests whether a mouse pressed event can be processed.
   */
  @Test def testMousePressed(): Unit = {
    val listener = mockListener()
    listener.mousePressed(EasyMock.anyObject(classOf[FormMouseEvent]))
    val a = registerEventAnswer()
    checkMouseListener(listener, createEvent(MouseEvent.MOUSE_PRESSED), a)
  }

  /**
   * Tests whether a mouse released event can be processed.
   */
  @Test def testMouseReleased(): Unit = {
    val listener = mockListener()
    listener.mouseReleased(EasyMock.anyObject(classOf[FormMouseEvent]))
    val a = registerEventAnswer()
    checkMouseListener(listener, createEvent(MouseEvent.MOUSE_RELEASED), a)
  }

  /**
   * Tests whether a mouse click event can be processed.
   */
  @Test def testMouseClick(): Unit = {
    val listener = mockListener()
    listener.mouseClicked(EasyMock.anyObject(classOf[FormMouseEvent]))
    val a = registerEventAnswer()
    val event = createEvent(MouseEvent.MOUSE_CLICKED)
    EasyMock.expect(event.getClickCount).andReturn(1)
    checkMouseListener(listener, event, a)
  }

  /**
   * Tests whether a mouse double click event can be processed.
   */
  @Test def testMouseDoubleClick(): Unit = {
    val listener = mockListener()
    listener.mouseDoubleClicked(EasyMock.anyObject(classOf[FormMouseEvent]))
    val a = registerEventAnswer()
    val event = createEvent(MouseEvent.MOUSE_CLICKED)
    EasyMock.expect(event.getClickCount).andReturn(3)
    checkMouseListener(listener, event, a)
  }
}
