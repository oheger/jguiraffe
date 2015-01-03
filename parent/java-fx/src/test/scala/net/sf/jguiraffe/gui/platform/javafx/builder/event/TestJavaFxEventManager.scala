/*
 * Copyright 2006-2015 The JGUIraffe Team.
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
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import net.sf.jguiraffe.gui.builder.event.FormEventManager
import net.sf.jguiraffe.gui.builder.event.FormFocusEvent
import net.sf.jguiraffe.gui.builder.event.FormListenerType
import net.sf.jguiraffe.gui.forms.ComponentHandler
import net.sf.jguiraffe.gui.platform.javafx.FetchAnswer
import net.sf.jguiraffe.gui.platform.javafx.FetchAnswer.convertToOption

/**
 * Test class for ''JavaFxEventManager''.
 */
@RunWith(classOf[PowerMockRunner])
@PrepareForTest(Array(classOf[Node]))
class TestJavaFxEventManager extends JUnitSuite {
  /** Constant for a test component name. */
  private val CompName = "JavaFxEventManagerTestComponent"

  /** A mock for the form event manager. */
  private var formEvMan: FormEventManager = _

  /** The event manager to be tested. */
  private var evMan: JavaFxEventManager = _

  @Before def setUp() {
    formEvMan = PowerMock.createMock(classOf[FormEventManager])
    evMan = new JavaFxEventManager
  }

  /**
   * Checks whether an event sender is correctly configured.
   * @param sender the sender
   * @param lType the expected listener type
   */
  private def checkSender(sender: EventSender[_], lType: FormListenerType) {
    sender match {
      case evManSender: EventManagerSender[_] =>
        assertSame("Wrong event manager", formEvMan, evManSender.manager)
        assertEquals("Wrong listener type", lType, evManSender.listenerType)
      case _ =>
        fail("Unexpected sender: " + sender)
    }
  }

  /**
   * Performs registration checks for a component handler and a given listener
   * type. A listener is registered and unregistered.
   * @param compHandler the ''ComponentHandler''
   * @param lType the listener type
   */
  private def doRegistration(compHandler: ComponentHandler[_],
    lType: FormListenerType) {
    evMan.registerListener(CompName, compHandler, formEvMan, lType)
    evMan.unregisterListener(CompName, compHandler, formEvMan, lType)
  }

  /**
   * Tests whether action listeners are handled correctly.
   */
  @Test def testActionListenerRegistration() {
    val compHandler = PowerMock.createMock(classOf[CompHandlerWithActionSource])
    val aRegister = new FetchAnswer[AnyRef, EventHandler[ActionEvent]]
    val aUnRegister = new FetchAnswer[AnyRef, EventHandler[ActionEvent]]
    val Command = "TestActionCommand"
    compHandler.addActionListener(
      EasyMock.anyObject(classOf[EventHandler[ActionEvent]]))
    EasyMock.expectLastCall().andAnswer(aRegister)
    compHandler.removeActionListener(
      EasyMock.anyObject(classOf[EventHandler[ActionEvent]]))
    EasyMock.expectLastCall().andAnswer(aUnRegister)
    EasyMock.expect(compHandler.actionCommand).andReturn(Command)
    PowerMock.replayAll()

    doRegistration(compHandler, FormListenerType.ACTION)
    val listener = aRegister.get.asInstanceOf[ActionEventAdapter]
    checkSender(listener.sender, FormListenerType.ACTION)
    assertSame("Wrong component handler", compHandler, listener.componentHandler)
    assertEquals("Wrong component name", CompName, listener.componentName)
    assertEquals("Wrong action command", Command, listener.command)
    assertSame("Wrong object unregistered", listener, aUnRegister.get)
    PowerMock.verifyAll()
  }

  /**
   * Tests whether listener registration handles the case that the desired
   * listener interface is not supported. The registration should be ignored.
   */
  @Test def testActionListenerNoActionSource() {
    val compHandler = PowerMock.createMock(classOf[ComponentHandler[AnyRef]])
    PowerMock.replayAll()

    evMan.registerListener(CompName, compHandler, formEvMan,
      FormListenerType.ACTION)
  }

  /**
   * Tests that a request to unregister an unknown listener is ignored.
   */
  @Test def testUnregisterUnknownListener() {
    val compHandler = PowerMock.createMock(classOf[CompHandlerWithActionSource])
    PowerMock.replayAll()

    evMan.unregisterListener(CompName, compHandler, formEvMan,
      FormListenerType.ACTION)
  }

  /**
   * Tests whether change listeners are handled correctly.
   */
  @Test def testChangeListenerRegistration() {
    val compHandler = PowerMock.createMock(classOf[CompHandlerWithChangeSource])
    val obsValue = PowerMock.createMock(classOf[ObservableValue[AnyRef]])
    compHandler.observableValue
    EasyMock.expectLastCall().andReturn(obsValue).anyTimes()
    val aRegister = new FetchAnswer[AnyRef, ChangeListener[AnyRef]]
    val aUnregister = new FetchAnswer[AnyRef, ChangeListener[AnyRef]]
    obsValue.addListener(EasyMock.anyObject(classOf[ChangeListener[AnyRef]]))
    EasyMock.expectLastCall().andAnswer(aRegister)
    obsValue.removeListener(EasyMock.anyObject(classOf[ChangeListener[AnyRef]]))
    EasyMock.expectLastCall().andAnswer(aUnregister)
    PowerMock.replayAll()

    doRegistration(compHandler, FormListenerType.CHANGE)
    val listener = aRegister.get.asInstanceOf[ChangeEventAdapter]
    assertSame("Wrong component handler", compHandler, listener.componentHandler)
    assertEquals("Wrong component name", CompName, listener.componentName)
    checkSender(listener.sender, FormListenerType.CHANGE)
    assertSame("Wrong unregistered listener", listener, aUnregister.get)
    PowerMock.verifyAll()
  }

  /**
   * Tests whether focus listeners are handled correctly.
   */
  @Test def testFocusListenerRegistration() {
    /**
     * Helper method for testing a received event.
     * @param ans the answer with the event
     * @param handler the expected handler
     * @param expType the expected event type
     */
    def checkFocusEvent(ans: FetchAnswer[_, FormFocusEvent],
      handler: ComponentHandler[_], expType: FormFocusEvent.Type) {
      assertSame("Wrong handler", handler, ans.get.getHandler)
      assertEquals("Wrong component name", CompName, ans.get.getName)
      assertEquals("Wrong event type", expType, ans.get.getType)
    }

    val compHandler = PowerMock.createMock(classOf[CompHandlerWithNodeSource])
    val node = PowerMock.createMock(classOf[Node])
    val focusProp = PowerMock.createMock(classOf[ReadOnlyBooleanProperty])
    EasyMock.expect(compHandler.sourceNode).andReturn(node).anyTimes()
    EasyMock.expect(node.focusedProperty).andReturn(focusProp).anyTimes()
    val aRegister = new FetchAnswer[AnyRef, ChangeListener[java.lang.Boolean]]
    val aUnregister = new FetchAnswer[AnyRef, ChangeListener[java.lang.Boolean]]
    focusProp.addListener(EasyMock.anyObject(classOf[ChangeListener[AnyRef]]))
    EasyMock.expectLastCall().andAnswer(aRegister)
    focusProp.removeListener(EasyMock.anyObject(classOf[ChangeListener[AnyRef]]))
    EasyMock.expectLastCall().andAnswer(aUnregister)
    val aFocusGainedEvent = new FetchAnswer[AnyRef, FormFocusEvent]
    val aFocusLostEvent = new FetchAnswer[AnyRef, FormFocusEvent]
    formEvMan.fireEvent(EasyMock.anyObject(classOf[FormFocusEvent]),
      EasyMock.eq(FormListenerType.FOCUS))
    EasyMock.expectLastCall().andAnswer(aFocusGainedEvent)
    formEvMan.fireEvent(EasyMock.anyObject(classOf[FormFocusEvent]),
      EasyMock.eq(FormListenerType.FOCUS))
    EasyMock.expectLastCall().andAnswer(aFocusLostEvent)
    PowerMock.replayAll()

    doRegistration(compHandler, FormListenerType.FOCUS)
    val listener = aRegister.get
    listener.changed(focusProp, java.lang.Boolean.FALSE, java.lang.Boolean.TRUE)
    listener.changed(focusProp, java.lang.Boolean.TRUE, java.lang.Boolean.FALSE)

    checkFocusEvent(aFocusGainedEvent, compHandler,
      FormFocusEvent.Type.FOCUS_GAINED)
    checkFocusEvent(aFocusLostEvent, compHandler,
      FormFocusEvent.Type.FOCUS_LOST)
    assertSame("Wrong unregistered listener", listener, aUnregister.get)
    PowerMock.verifyAll()
  }

  /**
   * Tests whether mouse listeners are handled correctly.
   */
  @Test def testMouseListenerRegistration() {
    val compHandler = PowerMock.createMock(classOf[CompHandlerWithNodeSource])
    val node = PowerMock.createMock(classOf[Node])
    EasyMock.expect(compHandler.sourceNode).andReturn(node).anyTimes()
    val aRegister = new FetchAnswer[AnyRef, EventHandler[MouseEvent]](argIdx = 1)
    val aUnregister = new FetchAnswer[AnyRef, EventHandler[MouseEvent]](argIdx = 1)
    node.addEventHandler(EasyMock.eq(MouseEvent.ANY),
      EasyMock.anyObject(classOf[EventHandler[MouseEvent]]))
    EasyMock.expectLastCall().andAnswer(aRegister)
    node.removeEventHandler(EasyMock.eq(MouseEvent.ANY),
      EasyMock.anyObject(classOf[EventHandler[MouseEvent]]))
    EasyMock.expectLastCall().andAnswer(aUnregister)
    PowerMock.replayAll()

    doRegistration(compHandler, FormListenerType.MOUSE)
    val listener = aRegister.get.asInstanceOf[MouseEventAdapter]
    assertSame("Wrong component handler", compHandler, listener.componentHandler)
    assertEquals("Wrong component name", CompName, listener.componentName)
    checkSender(listener.sender, FormListenerType.MOUSE)
    assertSame("Wrong unregistered listener", listener, aUnregister.get)
    PowerMock.verifyAll()
  }
}

/**
 * A combined trait for a component handler and an action event source needed
 * for mock creation.
 */
private trait CompHandlerWithActionSource
  extends ComponentHandler[AnyRef] with ActionEventSource

/**
 * A combined trait for a component handler and a change event source needed
 * for mock creation.
 */
private trait CompHandlerWithChangeSource
  extends ComponentHandler[AnyRef] with ChangeEventSource

/**
 * A combined trait for a component handler and a node event source needed
 * for mock creation.
 */
private trait CompHandlerWithNodeSource
  extends ComponentHandler[AnyRef] with NodeEventSource