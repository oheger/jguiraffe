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

import javafx.scene.control._
import javafx.scene.image.{Image, ImageView}
import javafx.scene.input.KeyCombination

import net.sf.jguiraffe.gui.builder.action.{Accelerator, ActionBuilder, ActionData, ActionTask}
import net.sf.jguiraffe.gui.builder.components.FormBuilderException
import net.sf.jguiraffe.gui.builder.components.tags.TextIconData
import net.sf.jguiraffe.gui.builder.event.{FormActionEvent, Keys, Modifiers}
import net.sf.jguiraffe.gui.platform.javafx.common.{ButtonHandlerFactory, ImageWrapper}
import org.easymock.EasyMock
import org.easymock.EasyMock.{eq => eqArg}
import org.junit.Assert._
import org.junit.{Before, BeforeClass, Test}
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.EasyMockSugar

/**
 * Companion object.
 */
object TestJavaFxActionManager {
  /** Constant for an accelerator used within tests. */
  private val TestAccelerator = Accelerator.getInstance(Keys.F3, java.util.Collections.singleton
    (Modifiers.CONTROL))

  /** Constant for the text of the test action. */
  private val ActionText = "Execute"

  /** Constant for the action text with mnemonic character. */
  private val ActionTextWithMnemonic = "E_xecute"

  /** The image associated with menus or buttons. */
  private var image: Image = _

  /** The icon used by tests. */
  private var icon: ImageWrapper = _

  /** The expected key combination. */
  private var keyCombination: KeyCombination = _

  @BeforeClass def setUpOnce(): Unit = {
    image = new Image(classOf[TestJavaFxActionManager].getResource("/icon.jpg").toExternalForm)
    icon = new ImageWrapper(image)
    keyCombination = KeyCombination valueOf "CTRL+F3"
  }

  /**
   * Creates a default action builder object.
   * @return the ''ActionBuilder''
   */
  private def createActionBuilder(): ActionBuilder = {
    val builder = new ActionBuilder
    builder setMenuIcon true
    builder setToolbarText true
    builder
  }

  /**
   * Creates an action data object with default settings.
   * @return the data object
   */
  private def createActionData(): ActionDataImpl =
    ActionDataImpl(getName = "TestAction", getText = ActionText,
      getMnemonicKey = 'x', getAccelerator = TestAccelerator, getIcon = icon,
      getTask = EasyMock.createMock(classOf[Runnable]))

  /**
   * Creates an action with default settings.
   * @return the action
   */
  private def createAction(): JavaFxAction =
    new JavaFxAction(createActionData())

  /**
   * Checks whether the correct basic properties for a menu item have been
   * initialized.
   * @param data the object with the expected properties
   * @param item the item to be checked
   */
  private def checkMenuItemBasicProperties(data: ActionDataImpl, item: MenuItem): Unit = {
    val expText = if (data.getMnemonicKey > 0) ActionTextWithMnemonic
    else ActionText
    assertEquals("Wrong text", expText, item.getText)
    assertEquals("Wrong icon", image, item.getGraphic.asInstanceOf[ImageView].getImage)
  }

  /**
   * Checks the most important properties of a menu item.
   * @param data the object with the expected properties
   * @param item the item to be checked
   */
  private def checkMenuItem(data: ActionDataImpl, item: MenuItem): Unit = {
    checkMenuItemBasicProperties(data, item)
    assertEquals("Wrong accelerator", keyCombination, item.getAccelerator)
  }
}

/**
 * Test class for ''JavaFxActionManager''.
 */
class TestJavaFxActionManager extends JUnitSuite with EasyMockSugar {

  import net.sf.jguiraffe.gui.platform.javafx.builder.action.TestJavaFxActionManager._

  /** The action builder. */
  private var actionBuilder: ActionBuilder = _

  /** A button handler factory used by the test manager. */
  private var buttonHandlerFactory: ButtonHandlerFactory = _

  /** The manager to be tested. */
  private var manager: JavaFxActionManager = _

  @Before def setUp(): Unit = {
    actionBuilder = createActionBuilder()
    buttonHandlerFactory = mock[ButtonHandlerFactory]
    manager = new JavaFxActionManager(buttonHandlerFactory)
  }

  /**
   * Tests whether an action can be created.
   */
  @Test def testCreateAction(): Unit = {
    val data = createActionData()
    val action = manager.createAction(actionBuilder, data).asInstanceOf[JavaFxAction]
    assertSame("Wrong action data", data, action.actionData)
    assertSame("Wrong action task", data.getTask, action.getTask)
  }

  /**
   * Tests whether a menu bar can be created.
   */
  @Test def testCreateMenuBar(): Unit = {
    assertTrue("Wrong menu bar", manager.createMenuBar(actionBuilder).isInstanceOf[MenuBar])
  }

  /**
   * Tests whether a menu can be created.
   */
  @Test def testCreateMenuCreationPhase(): Unit = {
    assertTrue(manager.createMenu(actionBuilder, null, null, null).isInstanceOf[Menu])
  }

  /**
   * Tests whether a menu is correctly initialized.
   */
  @Test def testCreateMenuInitializationPhase(): Unit = {
    val menu = new Menu
    val menuBar = new MenuBar
    val data = new TextIconData(null)
    val actData = createActionData().copy(getMnemonicKey = 0)
    data setText actData.getText
    data setIcon actData.getIcon

    assertSame("Wrong result", menu, manager.createMenu(actionBuilder, menu, data, menuBar))
    checkMenuItemBasicProperties(actData, menu)
    assertEquals("Wrong number of menus", 1, menuBar.getMenus.size)
    assertEquals("Not added to menu bar", menu, menuBar.getMenus.get(0))
  }

  /**
   * Tests that an undefined icon is handled correctly.
   */
  @Test def testCreateMenuNoIcon(): Unit = {
    val menu = new Menu
    val menuBar = new MenuBar
    val data = new TextIconData(null)
    data setText ActionText

    manager.createMenu(actionBuilder, menu, data, menuBar)
    assertNull("Got an icon", menu.getGraphic)
  }

  /**
   * Tests whether a mnemonic is set on a newly created menu.
   */
  @Test def testCreateMenuWithMnemonic(): Unit = {
    val menu = new Menu
    val menuBar = new MenuBar
    val data = new TextIconData(null)
    val actData = createActionData()
    data setText actData.getText
    data setIcon actData.getIcon
    data setMnemonicKey String.valueOf(actData.getMnemonicKey.toChar)

    manager.createMenu(actionBuilder, menu, data, menuBar)
    checkMenuItemBasicProperties(actData, menu)
  }

  /**
   * Tests whether a menu can be added to another menu.
   */
  @Test def testCreateMenuNested(): Unit = {
    val menu = new Menu
    val parentMenu = new Menu
    val data = new TextIconData(null)
    data setText ActionText

    manager.createMenu(actionBuilder, menu, data, parentMenu)
    assertEquals("Wrong number of items", 1, parentMenu.getItems.size)
    assertEquals("Wrong item", menu, parentMenu.getItems.get(0))
  }

  /**
   * Tests whether an invalid parent is detected when creating a menu.
   */
  @Test(expected = classOf[FormBuilderException]) def testCreateMenuInvalidParent(): Unit = {
    val menu = new Menu
    val data = new TextIconData(null)
    data setText ActionText

    manager.createMenu(actionBuilder, menu, data, this)
  }

  /**
   * Helper method for testing the creation of a menu item from a data object.
   * @param checked flag whether a checked item is to be created
   * @return the created component handler
   */
  private def checkCreateMenuItemFromData(checked: Boolean): MenuItemComponentHandler = {
    val actData = createActionData()
    val menu = new Menu

    val itemHandler = manager.createMenuItem(actionBuilder, actData, checked,
      menu).asInstanceOf[MenuItemComponentHandler]
    val item = itemHandler.item
    checkMenuItem(actData, item)
    assertEquals("Wrong number of items", 1, menu.getItems.size)
    assertEquals("Wrong menu item", item, menu.getItems.get(0))
    assertEquals("Wrong action command", actData.getName, itemHandler.actionCommand)
    itemHandler
  }

  /**
   * Tests whether a menu item can created from a data object.
   */
  @Test def testCreateMenuItemFromData(): Unit = {
    checkCreateMenuItemFromData(checked = false)
  }

  /**
   * Tests whether a check menu item can be created from a data object.
   */
  @Test def testCreateCheckMenuItemFromData(): Unit = {
    val itemHandler = checkCreateMenuItemFromData(checked = true)
    val checkItem = itemHandler.item.asInstanceOf[CheckMenuItem]
    assertSame("Wrong property", checkItem.selectedProperty, itemHandler.property)
  }

  /**
   * Tests that the action builder's flag for suppressing icons in menu items is
   * taken into account.
   */
  @Test def testCreateMenuItemSuppressIcon(): Unit = {
    actionBuilder setMenuIcon false
    val item = manager.createMenuItem(actionBuilder, createAction(), checked = false, new Menu)

    assertNull("Got an icon", item.getGraphic)
  }

  /**
   * Creates a menu item for the specified action and performs some basic
   * tests.
   * @param action the action
   * @param checked the checked flag
   * @return the newly created menu item
   */
  private def createAndCheckMenuItemFromAction(action: JavaFxAction, checked: Boolean): MenuItem = {
    val menu = new Menu
    val item = manager.createMenuItem(actionBuilder, action, checked, menu)
    checkMenuItem(action.actionData.asInstanceOf[ActionDataImpl], item)
    assertEquals("Wrong number of items", 1, menu.getItems.size)
    assertEquals("Wrong menu item", item, menu.getItems.get(0))
    item
  }

  /**
   * Tests whether a menu item can be created from an action.
   */
  @Test def testCreateMenuItemFromAction(): Unit = {
    createAndCheckMenuItemFromAction(createAction(), checked = false)
  }

  /**
   * Tests that the enabled property of a menu item can be controlled via the
   * associated action.
   */
  @Test def testActionEnabledPropertyAssociatedWithMenuItem(): Unit = {
    val action = createAction()
    val item = createAndCheckMenuItemFromAction(action, checked = false)

    action setEnabled false
    assertTrue("Enabled state not set", item.isDisable)
  }

  /**
   * Tests whether a check menu item can be created from an action.
   */
  @Test def testCreateCheckMenuItemFromAction(): Unit = {
    assertTrue("Not a check menu item", createAndCheckMenuItemFromAction(createAction(),
      checked = true).isInstanceOf[CheckMenuItem])
  }

  /**
   * Tests whether the checked property of a check menu item can be controlled
   * via the associated action.
   */
  @Test def testActionCheckedPropertyAssociatedWithCheckMenuItem(): Unit = {
    val action = createAction()
    val checkItem = createAndCheckMenuItemFromAction(action,
      checked = true).asInstanceOf[CheckMenuItem]

    assertFalse("Already checked", checkItem.isSelected)
    action setChecked true
    assertTrue("Not checked", checkItem.isSelected)
  }

  /**
   * Tests whether an action gets executed when the menu item is triggered.
   */
  @Test def testActionPerformedByMenuItem(): Unit = {
    val task = mock[ActionTask]
    val action = createAction()
    task.run(eqArg(action), EasyMock.anyObject(classOf[FormActionEvent]))
    action setTask task

    whenExecuting(task) {
      val item = createAndCheckMenuItemFromAction(action, checked = false)
      item.fire()
    }
  }

  /**
   * Tests whether a separator can be added to a menu.
   */
  @Test def testAddMenuSeparator(): Unit = {
    val menu = new Menu

    manager.addMenuSeparator(actionBuilder, menu)
    assertEquals("Wrong number of menu items", 1, menu.getItems.size)
    assertTrue("Wrong separator item", menu.getItems.get(0).isInstanceOf[SeparatorMenuItem])
  }

  /**
   * Tests whether a tool bar component can be created.
   */
  @Test def testCreateToolBar(): Unit = {
    assertTrue("Got tool bar items", manager.createToolbar(actionBuilder).getItems.isEmpty)
  }
}

/**
 * A simple implementation of the ''ActionData'' interface.
 */
private case class ActionDataImpl(getName: String, getText: String,
                                  getToolTip: String = null, getMnemonicKey: Int,
                                  getAccelerator: Accelerator, getIcon: AnyRef,
                                  getTask: AnyRef) extends ActionData
