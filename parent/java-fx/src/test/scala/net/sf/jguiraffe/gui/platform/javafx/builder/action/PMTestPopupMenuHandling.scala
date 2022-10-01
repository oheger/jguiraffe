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

import javafx.event.EventType
import javafx.scene.Node
import javafx.scene.control.{ContextMenu, Label}
import javafx.scene.input.{MouseButton, MouseEvent}

import net.sf.jguiraffe.di.BeanContext
import net.sf.jguiraffe.gui.builder.action.{ActionBuilder, ActionManager, PopupMenuHandler}
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData
import net.sf.jguiraffe.gui.platform.javafx.common.ButtonHandlerFactory
import net.sf.jguiraffe.gui.platform.javafx.{FetchAnswer, JavaFxTestHelper}
import org.easymock.EasyMock
import org.easymock.EasyMock.{eq => argEq}
import org.junit.Assert._
import org.junit.runner.RunWith
import org.junit.{BeforeClass, Test}
import org.powermock.api.easymock.PowerMock
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.scalatest.junit.JUnitSuite

/**
 * Companion object.
 */
object PMTestPopupMenuHandling {
  /** A default x coordinate for a mouse event. */
  private val EventX = 128

  /** A default y coordinate for a mouse event. */
  private val EventY = 222

  @BeforeClass def setUpOnce(): Unit = {
    JavaFxTestHelper.initPlatform()
  }

  /**
   * Creates a mock four a mouse event with some default properties.
   * @param eventType the event type
   * @param btn the mouse button that was pressed
   * @return the mock for the event
   */
  private def createEvent(eventType: EventType[MouseEvent] = MouseEvent.MOUSE_CLICKED,
                          btn: MouseButton = MouseButton.PRIMARY): MouseEvent = {
    val event = PowerMock.createNiceMock(classOf[MouseEvent])
    event.getEventType
    EasyMock.expectLastCall().andReturn(eventType).anyTimes()
    EasyMock.expect(event.getButton).andReturn(btn).anyTimes()
    EasyMock.expect(event.getScreenX).andReturn(EventX).anyTimes()
    EasyMock.expect(event.getScreenY).andReturn(EventY).anyTimes()
    event
  }
}

/**
 * A test class for the functionality related to popup menu support.
 *
 * This functionality is implemented by a couple of internal classes. Testing
 * requires support by PowerMock to gain access to final methods in the JavaFX
 * API. Therefore, these tests were extracted into their own test class.
 */
@RunWith(classOf[PowerMockRunner])
@PrepareForTest(Array(classOf[MouseEvent], classOf[Node]))
class PMTestPopupMenuHandling extends JUnitSuite {

  import net.sf.jguiraffe.gui.platform.javafx.builder.action.PMTestPopupMenuHandling._

  /**
   * Tests whether a correct context menu is created.
   */
  @Test def testContextMenuCreator(): Unit = {
    val creator = new ContextMenuCreator {}
    val menu = JavaFxTestHelper.invokeInFxThread { f => creator.createContextMenu()}
    assertTrue("Got items", menu.getItems.isEmpty)
  }

  /**
   * Tests whether a menu builder is correctly initialized.
   */
  @Test def testMenuBuilderInitialization(): Unit = {
    val actionManager = PowerMock.createMock(classOf[ActionManager])
    val actionBuilder = PowerMock.createMock(classOf[ActionBuilder])
    val menuBuilder = new JavaFxPopupMenuBuilder(actionManager, actionBuilder, new Label,
      createEvent()) with MockContextMenuCreator

    assertSame("Wrong action manager", actionManager, menuBuilder.getActionManager)
    assertSame("Wrong action builder", actionBuilder, menuBuilder.getActionBuilder)
  }

  /**
   * Tests whether the menu builder returns the correct menu under construction.
   */
  @Test def testMenuBuilderMenuUnderConstruction(): Unit = {
    val actionManager = PowerMock.createMock(classOf[ActionManager])
    val actionBuilder = PowerMock.createMock(classOf[ActionBuilder])
    val menuBuilder = new JavaFxPopupMenuBuilder(actionManager, actionBuilder, new Label,
      createEvent()) with MockContextMenuCreator
    PowerMock.replayAll()

    assertSame("Wrong menu under construction", menuBuilder.mockMenu, menuBuilder
      .getMenuUnderConstruction)
  }

  /**
   * Tests the create() method of the popup menu builder.
   */
  @Test def testMenuBuilderCreate(): Unit = {
    val node = new Label
    val menuBuilder = new JavaFxPopupMenuBuilder(PowerMock.createMock(classOf[ActionManager]),
      PowerMock.createMock(classOf[ActionBuilder]), node, createEvent()) with MockContextMenuCreator
    menuBuilder.mockMenu.show(node, EventX, EventY)
    PowerMock.replayAll()

    assertSame("Wrong menu", menuBuilder.mockMenu, menuBuilder.create())
    PowerMock.verify(menuBuilder.mockMenu)
  }

  /**
   * Tests that the menu event listener ignores click events with the wrong
   * mouse button.
   */
  @Test def testMenuEventListenerWrongButton(): Unit = {
    val actionManager = PowerMock.createMock(classOf[ActionManager])
    val actionBuilder = PowerMock.createMock(classOf[ActionBuilder])
    val handler = PowerMock.createMock(classOf[PopupMenuHandler])
    val compData = PowerMock.createMock(classOf[ComponentBuilderData])
    PowerMock.replayAll()

    val listener = new ContextMenuEventListener(actionManager, actionBuilder, handler, compData,
      null)
    listener handle createEvent(btn = MouseButton.PRIMARY)
  }

  /**
   * Tests that the mouse listener triggers the display of the context menu if
   * the correct mouse button is clicked.
   */
  @Test def testMenuEventListenerCorrectButton(): Unit = {
    val actionManager = PowerMock.createMock(classOf[ActionManager])
    val actionBuilder = PowerMock.createMock(classOf[ActionBuilder])
    val handler = PowerMock.createMock(classOf[PopupMenuHandler])
    val compData = PowerMock.createMock(classOf[ComponentBuilderData])
    val node = PowerMock.createMock(classOf[Node])
    val answer = new FetchAnswer[Unit, JavaFxPopupMenuBuilder]
    handler.constructPopup(EasyMock.anyObject(classOf[JavaFxPopupMenuBuilder]), argEq(compData))
    EasyMock.expectLastCall() andAnswer answer
    val event = createEvent(btn = MouseButton.SECONDARY)
    PowerMock.replayAll()

    val listener = new ContextMenuEventListener(actionManager, actionBuilder, handler, compData,
      node)
    listener handle event
    PowerMock.verify(handler)

    val builder = answer.get
    assertEquals("Wrong action manager", actionManager, builder.getActionManager)
    assertEquals("Wrong action builder", actionBuilder, builder.getActionBuilder)
    assertEquals("Wrong node", node, builder.node)
    assertEquals("Wrong event", event, builder.event)
  }

  /**
   * Tests whether a popup menu handler can be registered for a node.
   */
  @Test def testRegisterPopupMenuHandler(): Unit = {
    val node = PowerMock.createMock(classOf[Node])
    val actionBuilder = PowerMock.createMock(classOf[ActionBuilder])
    val handler = PowerMock.createMock(classOf[PopupMenuHandler])
    val beanContext = PowerMock.createMock(classOf[BeanContext])
    val compData = new ComponentBuilderData
    EasyMock.expect(beanContext.getBean(ActionBuilder.KEY_ACTION_BUILDER)).andReturn(actionBuilder)
    val answer = new FetchAnswer[Unit, ContextMenuEventListener](argIdx = 1)
    node.addEventHandler(argEq(MouseEvent.MOUSE_CLICKED), EasyMock.anyObject
      (classOf[ContextMenuEventListener]))
    EasyMock.expectLastCall() andAnswer answer
    PowerMock.replayAll()
    compData setBeanContext beanContext

    val actionManager = new JavaFxActionManager(PowerMock.createMock(classOf[ButtonHandlerFactory]))
    actionManager.registerPopupMenuHandler(node, handler, compData)
    PowerMock.verify(node)

    val listener = answer.get
    assertEquals("Wrong action manager", actionManager, listener.actionManager)
    assertEquals("Wrong action builder", actionBuilder, listener.actionBuilder)
    assertEquals("Wrong component", node, listener.component)
    assertEquals("Wrong component builder data", compData, listener.compData)
    assertEquals("Wrong handler", handler, listener.handler)
  }
}

/**
 * A mock implementation of a context menu creator.
 *
 * This implementation has a mock context menu which is returned on each call.
 * It also checks that it is called at most once.
 */
private trait MockContextMenuCreator extends ContextMenuCreator {
  /** The mock context menu. */
  val mockMenu = PowerMock.createMock(classOf[ContextMenu])

  /** A counter for the invocations. */
  var createContextMenuCount = 0

  /**
   * Creates a new ''ContextMenu'' instance. This implementation just creates
   * a new instance without any special initialization.
   * @return the newly created context menu instance
   */
  override def createContextMenu(): ContextMenu = {
    assertTrue("Too many invocations", createContextMenuCount < 1)
    createContextMenuCount += 1
    mockMenu
  }
}
