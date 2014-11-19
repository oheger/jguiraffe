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
package net.sf.jguiraffe.gui.platform.javafx.builder.action

import javafx.event.EventType
import javafx.scene.control.{ContextMenu, Label}
import javafx.scene.input.{MouseButton, MouseEvent}

import net.sf.jguiraffe.gui.builder.action.{ActionBuilder, ActionManager}
import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import org.easymock.EasyMock
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
object TestPopupMenuHandling {
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
@PrepareForTest(Array(classOf[MouseEvent]))
class TestPopupMenuHandling extends JUnitSuite {

  import net.sf.jguiraffe.gui.platform.javafx.builder.action.TestPopupMenuHandling._

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
