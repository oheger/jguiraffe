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
package net.sf.jguiraffe.gui.platform.javafx.builder.action

import javafx.event.ActionEvent

import net.sf.jguiraffe.gui.builder.action.{ActionData, ActionTask}
import net.sf.jguiraffe.gui.builder.event.{FormActionEvent, BuilderEvent}
import net.sf.jguiraffe.gui.platform.javafx.FetchAnswer
import org.easymock.EasyMock
import org.easymock.EasyMock.{eq => eqArgs}
import org.junit.Assert._
import org.junit.{Before, Test}
import org.scalatest.junit.JUnitSuite
import org.scalatest.easymock.EasyMockSugar

/**
 * Test class for ''JavaFxAction''.
 */
class TestJavaFxAction extends JUnitSuite with EasyMockSugar {
  /** A data object defining the properties of the action. */
  private var actionData: ActionData = _

  /** The action to be tested. */
  private var action: JavaFxAction = _

  @Before def setUp(): Unit = {
    actionData = mock[ActionData]
    action = new JavaFxAction(actionData)
  }

  /**
   * Tests whether the correct action name is returned.
   */
  @Test def testGetName(): Unit = {
    val ActionName = "MyTestAction"
    EasyMock.expect(actionData.getName).andReturn(ActionName)
    EasyMock replay actionData
    assertEquals("Wrong action name", ActionName, action.getName)
  }

  /**
   * Tests that actions are enabled per default.
   */
  @Test def testInitialEnabledState(): Unit = {
    assertTrue("Not enabled", action.isEnabled)
  }

  /**
   * Tests the initial checked state.
   */
  @Test def testInitialCheckedState(): Unit = {
    assertFalse("Checked", action.isChecked)
  }

  /**
   * Tests whether the enabled state can be queried.
   */
  @Test def testIsEnabled(): Unit = {
    val enabledProperty = action.enabled

    enabledProperty setValue true
    assertTrue("Wrong result (1)", action.isEnabled)
    enabledProperty set false
    assertFalse("Wrong result (2)", action.isEnabled)
  }

  /**
   * Tests whether the enabled state can be set.
   */
  @Test def testSetEnabled(): Unit = {
    val enabledProperty = action.enabled

    action setEnabled true
    assertTrue("Wrong value (1)", enabledProperty.get)
    action setEnabled false
    assertFalse("Wrong value (2)", enabledProperty.get)
  }

  /**
   * Tests whether the checked state can be queried.
   */
  @Test def testIsChecked(): Unit = {
    val checkedProperty = action.checked

    checkedProperty setValue true
    assertTrue("Wrong result (1)", action.isChecked)
    checkedProperty set false
    assertFalse("Wrong result (2)", action.isChecked)
  }

  /**
   * Tests whether the checked state can be set.
   */
  @Test def testSetChecked(): Unit = {
    val checkedProperty = action.checked

    action setChecked true
    assertTrue("Wrong value (1)", checkedProperty.get)
    action setChecked false
    assertFalse("Wrong value (2)", checkedProperty.get)
  }

  /**
   * Tests an execution of the action if the task is a Runnable.
   */
  @Test def testExecuteRunnableTask(): Unit = {
    val task = mock[Runnable]
    task.run()

    whenExecuting(task) {
      action setTask task
      action execute mock[BuilderEvent]
    }
  }

  /**
   * Tests an execution of the action if the task is an action task.
   */
  @Test def testExecuteActionTask(): Unit = {
    val task = mock[ActionTask]
    val event = mock[BuilderEvent]
    task.run(action, event)

    whenExecuting(task) {
      action setTask task
      action execute event
    }
  }

  /**
   * Tests whether an action can handle action events.
   */
  @Test def testHandleActionEvent(): Unit = {
    val ActionName = "MyTestAction"
    val task = mock[ActionTask]
    val answer = new FetchAnswer[AnyRef, FormActionEvent](argIdx = 1)
    task.run(eqArgs(action), EasyMock.anyObject(classOf[FormActionEvent]))
    EasyMock.expectLastCall() andAnswer answer
    EasyMock.expect(actionData.getName).andReturn(ActionName).anyTimes()

    val actionEvent = new ActionEvent
    whenExecuting(task, actionData) {
      action setTask task
      action handle actionEvent
    }

    val event = answer.value
    assertEquals("Wrong source", actionEvent, event.getSource)
    assertEquals("Wrong action command", ActionName, event.getCommand)
  }
}
