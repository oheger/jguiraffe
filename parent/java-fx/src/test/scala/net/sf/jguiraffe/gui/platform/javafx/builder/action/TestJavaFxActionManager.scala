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
package net.sf.jguiraffe.gui.platform.javafx.builder.action

import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control._
import javafx.scene.image.{Image, ImageView}
import javafx.scene.input.KeyCombination

import net.sf.jguiraffe.gui.builder.action._
import net.sf.jguiraffe.gui.builder.components.FormBuilderException
import net.sf.jguiraffe.gui.builder.components.tags.TextIconData
import net.sf.jguiraffe.gui.builder.event.{FormActionEvent, Keys, Modifiers}
import net.sf.jguiraffe.gui.forms.ComponentHandler
import net.sf.jguiraffe.gui.platform.javafx.{JavaFxTestHelper, FetchAnswer}
import net.sf.jguiraffe.gui.platform.javafx.common.{ButtonHandlerFactory, DefaultToolTipFactory, ImageWrapper, MockToolTipCreationSupport}
import org.apache.commons.jelly.JellyContext
import org.apache.commons.lang.StringUtils
import org.easymock.EasyMock
import org.easymock.EasyMock.{eq => eqArg}
import org.junit.Assert._
import org.junit.{Before, BeforeClass, Test}
import org.scalatestplus.junit.JUnitSuite
import org.scalatestplus.easymock.EasyMockSugar

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

  /** Constant for a tool tip for an action. */
  private val ActionToolTip = "This action does something interesting."

  /** The image associated with menus or buttons. */
  private var image: Image = _

  /** The icon used by tests. */
  private var icon: ImageWrapper = _

  /** The expected key combination. */
  private var keyCombination: KeyCombination = _

  @BeforeClass def setUpOnce(): Unit = {
    JavaFxTestHelper.initPlatform()
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
    val context = new JellyContext
    builder put context
    builder
  }

  /**
   * Creates an action data object with default settings.
   * @return the data object
   */
  private def createActionData(): ActionDataImpl = {
    val runnable: Runnable = EasyMock.createMock(classOf[Runnable])
    ActionDataImpl(getName = "TestAction", getText = ActionText,
      getMnemonicKey = 'x', getAccelerator = TestAccelerator, getIcon = icon,
      getToolTip = ActionToolTip,
      getTask = runnable)
  }

  /**
   * Creates an action with default settings.
   * @return the action
   */
  private def createAction(): JavaFxAction =
    new JavaFxAction(createActionData())

  /**
   * Creates a JavaFX context menu. This has to be done in the event dispatch thread.
   * @return the newly created context menu
   */
  private def createContextMenu(): ContextMenu =
    JavaFxTestHelper.invokeInFxThread(f => new ContextMenu)

  /**
   * Determines the expected text for an action based on the presence of a mnemonic.
   * @param data the action data
   * @return the expected text for this action
   */
  private def expectedActionText(data: ActionData): String =
    if (data.getMnemonicKey > 0) ActionTextWithMnemonic
    else ActionText

  /**
   * Checks the image assigned to an action component.
   * @param graphic the node representing the image
   */
  private def checkImage(graphic: Node): Unit = {
    assertEquals("Wrong icon", image, graphic.asInstanceOf[ImageView].getImage)
  }

  /**
   * Checks whether the correct basic properties for a menu item have been
   * initialized.
   * @param data the object with the expected properties
   * @param item the item to be checked
   */
  private def checkMenuItemBasicProperties(data: ActionDataImpl, item: MenuItem): Unit = {
    assertEquals("Wrong text", expectedActionText(data), item.getText)
    checkImage(item.getGraphic)
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

  /**
   * Checks the most important properties of a (tool bar) button.
   * @param data the object with the expected properties
   * @param button the button to be checked
   * @return the very same button
   */
  private def checkButtonBasicProperties(data: ActionData, button: ButtonBase): ButtonBase = {
    assertEquals("Wrong text", expectedActionText(data), button.getText)
    checkImage(button.getGraphic)
    button
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
  private var manager: JavaFxActionManager with MockToolTipCreationSupport = _

  @Before def setUp(): Unit = {
    actionBuilder = createActionBuilder()
    buttonHandlerFactory = mock[ButtonHandlerFactory]
    manager = new JavaFxActionManager(buttonHandlerFactory) with MockToolTipCreationSupport
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
   * Creates a mock for an action task and prepares it to expect its
   * execution.
   * @param action the action
   * @return the task mock
   */
  private def prepareActionTask(action: FormAction): ActionTask = {
    val task = mock[ActionTask]
    task.run(eqArg(action), EasyMock.anyObject(classOf[FormActionEvent]))
    action setTask task
    task
  }

  /**
   * Tests whether an action gets executed when the menu item is triggered.
   */
  @Test def testActionPerformedByMenuItem(): Unit = {
    val action = createAction()

    whenExecuting(prepareActionTask(action)) {
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

  /**
   * Tests whether a correct default tool tip factory is created.
   */
  @Test def testDefaultToolTipFactory(): Unit = {
    assertTrue("Wrong tool tip factory", manager.toolTipFactory.isInstanceOf[DefaultToolTipFactory])
  }

  /**
   * Checks whether a tool bar button has the expected properties. This method
   * also checks whether a tool tip was created.
   * @param data the data object with the expected properties
   * @param button the button to be checked
   * @return the checked button
   */
  private def checkButton(data: ActionData, button: ButtonBase): ButtonBase = {
    assertFalse("Too many tooltip requests", manager.verifyToolTipCreationRequest(actionBuilder
      .getContext, button, ActionToolTip))
    checkButtonBasicProperties(data, button)
  }

  /**
   * Helper method for testing whether a tool bar button can be created from a
   * data object.
   * @param checked the checked flag
   * @return the newly created button
   */
  private def checkCreateToolbarButtonFromData(checked: Boolean): ButtonBase = {
    val buttonHandler = mock[ComponentHandler[java.lang.Boolean]]
    val answer = new FetchAnswer[ComponentHandler[java.lang.Boolean],
      ButtonBase](retVal = buttonHandler)
    val parent = new ToolBar
    val data = createActionData()
    EasyMock.expect(buttonHandlerFactory.createButtonHandler(EasyMock.anyObject(classOf[ButtonBase]),
      eqArg(data.getName))).andAnswer(answer)

    whenExecuting(buttonHandler, buttonHandlerFactory) {
      assertSame("Wrong handler", buttonHandler, manager.createToolbarButton(actionBuilder, data,
        checked, parent))
      assertEquals("Wrong number of items in tool bar", 1, parent.getItems.size)
      assertEquals("Wrong button", checkButton(data, answer.get), parent.getItems.get(0))
    }
    answer.get
  }

  /**
   * Tests whether a toolbar button can be created from a data object.
   */
  @Test def testCreateToolbarButtonFromData(): Unit = {
    assertEquals("Wrong button class", classOf[Button], checkCreateToolbarButtonFromData(checked
      = false).getClass)
  }

  /**
   * Tests whether a toggle button can be created for the tool bar.
   */
  @Test def testCreateCheckedToolbarButtonFromData(): Unit = {
    assertTrue("Wrong toggle button", checkCreateToolbarButtonFromData(checked = true)
      .isInstanceOf[ToggleButton])
  }

  /**
   * Tests that an undefined tool tip is handled correctly for tool bar buttons.
   */
  @Test def testCreateToolbarButtonNoToolTip(): Unit = {
    val buttonHandler = mock[ComponentHandler[java.lang.Boolean]]
    EasyMock.expect(buttonHandlerFactory.createButtonHandler(EasyMock.anyObject
      (classOf[ButtonBase]),
      EasyMock.anyObject(classOf[String]))).andReturn(buttonHandler)
    val data = createActionData().copy(getToolTip = null)

    whenExecuting(buttonHandler, buttonHandlerFactory) {
      manager.createToolbarButton(actionBuilder, data, checked = false, new ToolBar)
    }
    manager.verifyNoInteraction()
  }

  /**
   * Helper method for testing the creation of a tool bar button based on an action.
   * @param checked the checked flag
   * @return the newly created button
   */
  private def checkCreateToolbarButtonFromAction(checked: Boolean): ButtonBase = {
    val action = createAction()
    val toolBar = new ToolBar

    val button = checkButton(action.actionData, manager.createToolbarButton(actionBuilder,
      action, checked, toolBar))
    assertEquals("Wrong number of items in toolbar", 1, toolBar.getItems.size)
    assertEquals("Wrong item", button, toolBar.getItems.get(0))
    button
  }

  /**
   * Tests whether a toolbar button can be created from an action.
   */
  @Test def testCreateToolbarButtonFromAction(): Unit = {
    assertTrue("Wrong button class", checkCreateToolbarButtonFromAction(checked = false)
      .isInstanceOf[Button])
  }

  /**
   * Tests whether a toggle toolbar button can be created from an action.
   */
  @Test def testCreateToggleToolbarButtonFromAction(): Unit = {
    assertTrue("Wrong button class", checkCreateToolbarButtonFromAction(checked = true)
      .isInstanceOf[ToggleButton])
  }

  /**
   * Tests whether the enabled property of an action is associated with the tool bar
   * button that is created for this action.
   */
  @Test def testActionEnabledPropertyAssociatedWithButton(): Unit = {
    val action = createAction()
    val button = manager.createToolbarButton(actionBuilder, action, checked = false, new ToolBar)

    assertFalse("Not enabled (1)", button.isDisable)
    action setEnabled false
    assertTrue("Not disabled", button.isDisable)
    action setEnabled true
    assertFalse("Not enabled (2)", button.isDisable)
  }

  /**
   * Tests whether the checked property of an action is associated with the tool bar
   * toggle button that is created for this action.
   */
  @Test def testActionCheckedPropertyAssociatedWithButton(): Unit = {
    val action = createAction()
    val button = manager.createToolbarButton(actionBuilder, action, checked = true, new ToolBar).asInstanceOf[ToggleButton]

    assertFalse("Already selected", button.isSelected)
    action setChecked true
    assertTrue("Not selected", button.isSelected)
    action setChecked false
    assertFalse("Still selected", button.isSelected)
  }

  /**
   * Tests whether an action used for creating a toolbar button is executed
   * when the button is clicked.
   */
  @Test def testActionPerformedByButton(): Unit = {
    val action = createAction()

    whenExecuting(prepareActionTask(action)) {
      val button = manager.createToolbarButton(actionBuilder, action, checked = false, new ToolBar)
      button.fire()
    }
  }

  /**
   * Tests whether the text of a button is suppressed if the corresponding
   * property is set in the ActionBuilder.
   */
  @Test def testCreateToolbarButtonSuppressText(): Unit = {
    actionBuilder setToolbarText false
    val button = manager.createToolbarButton(actionBuilder, createAction(), checked = false, new
        ToolBar)

    assertTrue("Got button text", StringUtils.isEmpty(button.getText))
  }

  /**
   * Tests whether a separator can be added to a toolbar.
   */
  @Test def testAddToolbarSeparator(): Unit = {
    val toolBar = new ToolBar
    manager.addToolBarSeparator(actionBuilder, toolBar)

    assertEquals("Wrong number of items", 1, toolBar.getItems.size)
    val separator = toolBar.getItems.get(0).asInstanceOf[Separator]
    assertEquals("Wrong orientation", Orientation.VERTICAL, separator.getOrientation)
  }

  /**
   * Tests that the action manager supports context menus when adding items.
   */
  @Test def testAddItemToContextMenu(): Unit = {
    val parent = createContextMenu()
    val item = manager.createMenuItem(actionBuilder, createAction(), checked = false, parent)

    assertEquals("Wrong number of items", 1, parent.getItems.size)
    assertEquals("Wrong menu item", item, parent.getItems.get(0))
  }

  /**
   * Tests whether a menu can be added to a context menu.
   */
  @Test def testAddMenuToContextMenu(): Unit = {
    val parent = createContextMenu()
    val menu = new Menu
    val tiData = new TextIconData(null)
    tiData setText ActionText
    manager.createMenu(actionBuilder, menu, tiData, parent)

    assertEquals("Wrong number of items", 1, parent.getItems.size)
    assertEquals("Wrong item", menu, parent.getItems.get(0))
  }

  /**
   * Tests that an unsupported parent menu is handled correctly.
   */
  @Test(expected = classOf[FormBuilderException]) def testAddItemToUnsupportedParent(): Unit = {
    manager.createMenuItem(actionBuilder, createAction(), checked = false, this)
  }
}

/**
 * A simple implementation of the ''ActionData'' interface.
 */
private case class ActionDataImpl(getName: String, getText: String,
                                  getToolTip: String = null, getMnemonicKey: Int,
                                  getAccelerator: Accelerator, getIcon: AnyRef,
                                  getTask: AnyRef) extends ActionData
