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

import javafx.beans.property.{BooleanProperty, SimpleBooleanProperty}
import javafx.event.ActionEvent
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control._
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent

import net.sf.jguiraffe.gui.builder.action.{ActionBuilder, ActionData, ActionManager, FormAction, PopupMenuHandler}
import net.sf.jguiraffe.gui.builder.components.tags.TextIconData
import net.sf.jguiraffe.gui.builder.components.{ComponentBuilderData, FormBuilderException}
import net.sf.jguiraffe.gui.forms.ComponentHandler
import net.sf.jguiraffe.gui.platform.javafx.common.ComponentUtils.as
import net.sf.jguiraffe.gui.platform.javafx.common._

/**
 * JavaFX-specific implementation of the ''ActionManager'' interface.
 *
 * @param buttonHandlerFactory a factory for creating button component handlers
 * @param toolTipFactory a factory for creating tool tips
 */
class JavaFxActionManager(val buttonHandlerFactory: ButtonHandlerFactory,
                          override val toolTipFactory: ToolTipFactory) extends ActionManager with
ToolTipCreationSupport {
  /**
   * Creates a new instance of ''JavaFxActionManager'' with default dependencies.
   * @param buttonHandlerFactory a factory for creating button component handlers
   * @return the newly created instance
   */
  def this(buttonHandlerFactory: ButtonHandlerFactory) = this(buttonHandlerFactory,
    new DefaultToolTipFactory)

  /**
   * @inheritdoc
   * This implementation returns JavaFX-specific action implementation.
   */
  def createAction(actionBuilder: ActionBuilder, actionData: ActionData): FormAction = {
    val action = new JavaFxAction(actionData)
    action setTask actionData.getTask
    action
  }

  /**
   * @inheritdoc
   * This implementation expects a ''JavaFxAction'' object to be passed in.
   * From there, the properties for initializing a new menu item are obtained.
   * There is also a property binding performed to connect properties of the
   * action with corresponding properties of the menu item.
   */
  def createMenuItem(actionBuilder: ActionBuilder, action: FormAction,
                     checked: Boolean, parent: Object): MenuItem = {
    val fxAction = as[JavaFxAction](action)
    addItemToMenu(parent, bindActionToMenuItem(fxAction, createInitializedMenuItem(fxAction
      .actionData, actionBuilder, checked)))
  }

  /**
   * @inheritdoc
   * This implementation either creates a ''MenuItem'' or a
   * ''CheckedMenuItem'' depending on the passed in ''checked'' flag. A special
   * ''ComponentHandler'' wrapping this menu item is returned.
   */
  def createMenuItem(actionBuilder: ActionBuilder,
                     actionData: ActionData, checked: Boolean,
                     parent: Object): ComponentHandler[_] = {
    val (item, property) = createInitializedMenuItem(actionData, actionBuilder, checked)
    new MenuItemComponentHandler(addItemToMenu(parent, item), property, actionData.getName)
  }

  /**
   * @inheritdoc
   * This implementation returns a JavaFX ''MenuBar'' object.
   */
  def createMenuBar(actionBuilder: ActionBuilder): Object = new MenuBar

  /**
   * @inheritdoc
   * This implementation creates a JavaFX ''Menu'' object and adds it to the
   * parent (which must be a ''MenuBar'').
   */
  def createMenu(actionBuilder: ActionBuilder, menu: Object,
                 data: TextIconData, parent: Object): Object = {
    if (menu == null) new Menu
    else {
      val fxMenu = as[Menu](menu)
      fxMenu setText ComponentUtils.mnemonicText(data.getCaption, data.getMnemonic)
      fxMenu setGraphic iconToImageView(data.getIcon)
      appendMenu(parent, fxMenu)
    }
  }

  /**
   * @inheritdoc This implementation returns a new JavaFX tool bar control.
   */
  def createToolbar(actionBuilder: ActionBuilder): ToolBar = new ToolBar

  /**
   * @inheritdoc This implementation creates a JavaFX button control; the
   *             concrete type depends on the ''checked'' flag. The action
   *             is connected connected to the corresponding properties of
   *             the new button.
   */
  def createToolbarButton(actionBuilder: ActionBuilder, action: FormAction,
                          checked: Boolean, parent: Object): ButtonBase = {
    val fxAction = ComponentUtils.as[JavaFxAction](action)
    bindActionToButton(fxAction, createInitializedButton(actionBuilder, fxAction.actionData,
      checked, parent, Some(fxAction.checked)))
  }

  /**
   * @inheritdoc This implementation creates a button and initializes it from
   *             the given action data object. The button is added to the
   *             parent which has to be a ''ToolBar''. The associated
   *             ''ButtonHandlerFactory'' is used to create a handler.
   */
  def createToolbarButton(actionBuilder: ActionBuilder,
                          data: ActionData, checked: Boolean,
                          parent: Object): ComponentHandler[_] = {
    buttonHandlerFactory.createButtonHandler(createInitializedButton(actionBuilder, data,
      checked, parent), data.getName)
  }

  /**
   * @inheritdoc
   * This implementation adds a separator menu item to the specified menu. The
   * menu is expected to be a ''Menu'' object.
   */
  def addMenuSeparator(actionBuilder: ActionBuilder, menu: Object): Unit = {
    addItemToMenu(menu, new SeparatorMenuItem)
  }

  /**
   * @inheritdoc This implementation adds a separator control to the specified
   *             toolbar. The toolbar is expected to be ''ToolBar'' object.
   */
  def addToolBarSeparator(actionBuilder: ActionBuilder, toolBar: Object): Unit = {
    addItemToToolbar(toolBar, new Separator(Orientation.VERTICAL))
  }

  /**
   * @inheritdoc This implementation registers a specialized mouse event listener
   *             at the passed in component (which has to be a ''Node'') which
   *             triggers the creation of a context menu when the correct mouse
   *             button is pressed.
   */
  def registerPopupMenuHandler(component: Object, handler: PopupMenuHandler,
                               compData: ComponentBuilderData): Unit = {
    val actionBuilder = (compData.getBeanContext getBean ActionBuilder.KEY_ACTION_BUILDER)
      .asInstanceOf[ActionBuilder]
    val node = as[Node](component)
    val listener = new ContextMenuEventListener(actionManager = this, actionBuilder = actionBuilder,
      handler = handler, compData = compData, component = node)
    node.addEventHandler(MouseEvent.MOUSE_CLICKED, listener)
  }

  /**
   * Converts an icon passed to a creation method to an ''ImageView''. If the
   * icon is defined, it is converted. Otherwise, result is '''null'''.
   * @param icon the icon
   * @return the ''ImageView'' to be set on a newly created control
   */
  private def iconToImageView(icon: AnyRef): ImageView =
    if (icon == null) null
    else as[ImageWrapper](icon).newImageView()

  /**
   * Determines the text for an action control taking an optional mnemonic
   * into account.
   * @param actionData the ''ActionData'' object
   * @return the text for the corresponding action control
   */
  private def textWithMnemonic(actionData: ActionData): String =
    ComponentUtils.mnemonicText(actionData.getText, actionData.getMnemonicKey.toChar)

  /**
   * Creates a ''MenuItem'' based on the content of the specified ''ActionData''
   * object. The concrete type of menu item depends on the ''checked''
   * parameter. The return value also contains the ''BooleanProperty''
   * associated with the checked state of the menu item.
   * @param actionData the ''ActionData''
   * @param actionBuilder the ''ActionBuilder''
   * @param checked flag whether a checked menu item is to be created
   * @return the new menu item and the property with its checked state
   */
  private def createInitializedMenuItem(actionData: ActionData, actionBuilder: ActionBuilder,
                                        checked: Boolean): (MenuItem,
    BooleanProperty) = {
    if (!checked) (initializeMenuItem(new MenuItem, actionData, actionBuilder),
      new SimpleBooleanProperty)
    else {
      val checkItem = new CheckMenuItem
      (initializeMenuItem(checkItem, actionData, actionBuilder), checkItem.selectedProperty)
    }
  }

  /**
   * Initializes a menu item from the properties of the given ''ActionData''
   * object.
   * @param item the item to be initialized
   * @param actionData the ''ActionData''
   * @param actionBuilder the ''ActionBuilder''
   * @return the initialized menu item
   */
  private def initializeMenuItem(item: MenuItem, actionData: ActionData,
                                 actionBuilder: ActionBuilder): MenuItem = {
    item setAccelerator AcceleratorConverter.convertAccelerator(actionData.getAccelerator)
    if (actionBuilder.isMenuIcon) {
      item setGraphic iconToImageView(actionData.getIcon)
    }
    item setText textWithMnemonic(actionData)
    item
  }

  /**
   * Binds the enabled property of the given action to the specified property.
   * This is interpreted as the ''disable'' property of a node.
   * @param action the action
   * @param property the property to be bound
   */
  private def bindActionToEnabledProperty(action: JavaFxAction, property: BooleanProperty): Unit = {
    property bind action.enabled.not()
  }

  /**
   * Binds the properties of an action to the corresponding properties of the
   * given menu item.
   * @param action the action
   * @param itemData a tuple consisting of the menu item and the property to be
   *                 bound to the action's ''checked'' property
   * @return the menu item
   */
  private def bindActionToMenuItem(action: JavaFxAction, itemData: (MenuItem,
    BooleanProperty)): MenuItem = {
    val (item, checkProperty) = itemData
    bindActionToEnabledProperty(action, item.disableProperty)
    checkProperty bind action.checked
    item.addEventHandler(ActionEvent.ACTION, action)
    item
  }

  /**
   * Appends a new menu to the given parent. The parent can be either a menu
   * bar or another menu.
   * @param parent the parent
   * @param fxMenu the menu to be appended
   * @return the appended menu item
   * @throws FormBuilderException if the parent is not supported
   */
  private def appendMenu(parent: Object, fxMenu: Menu): Menu = {
    parent match {
      case bar: MenuBar =>
        bar.getMenus add fxMenu

      case _ =>
        addItemToMenu(parent, fxMenu)
    }

    fxMenu
  }

  /**
   * Adds a menu item to its parent menu. The parent object must either be a JavaFX
   * menu or context menu.
   * @param parent the parent menu
   * @param item the item to be added
   * @return the menu item
   * @throws FormBuilderException if the parent is not supported
   */
  private def addItemToMenu(parent: Object, item: MenuItem): MenuItem = {
    val items = parent match {
      case m: Menu =>
        m.getItems
      case c: ContextMenu =>
        c.getItems
      case other =>
        throw new FormBuilderException("Unsupported parent menu: " + other)
    }
    items add item
    item
  }

  /**
   * Creates and initializes a toolbar button. The button is initialized based on the
   * passed in action data object. It is also added to the parent, which has to be a
   * ''ToolBar'' object.
   * @param actionBuilder the ''ActionBuilder''
   * @param data the data object with the action properties
   * @param checked the checked flag
   * @param parent the parent component
   * @param checkedProperty an optional checked property to be associated with a toggle button
   * @return the newly created button
   */
  private def createInitializedButton(actionBuilder: ActionBuilder, data: ActionData, checked:
  Boolean, parent: Object, checkedProperty: Option[BooleanProperty] = None): ButtonBase = {
    val button = createButtonControl(checked, checkedProperty)
    if (actionBuilder.isToolbarText) {
      button setText textWithMnemonic(data)
    }
    button setGraphic iconToImageView(data.getIcon)
    if (data.getToolTip != null) {
      addCreateToolTipRequest(actionBuilder.getContext, button, data.getToolTip)
    }
    addItemToToolbar(parent, button)
  }

  /**
   * Actually creates the button control to be added to the tool bar. If applicable,
   * the button's selected property is connected to the action.
   * @param checked the checked flag
   * @param checkedProperty an optional checked property to be associated with a toggle button
   * @return the newly created button
   */
  private def createButtonControl(checked: Boolean, checkedProperty: Option[BooleanProperty]):
  ButtonBase = {
    if (checked) {
      val toggleButton = new ToggleButton
      checkedProperty foreach (toggleButton.selectedProperty bind _)
      toggleButton
    } else new Button
  }

  /**
   * Adds the specified node to a toolbar object.
   * @param toolBar the toolbar (has to be of type ''ToolBar'')
   * @param item the item to be added
   * @return the item that was added
   */
  private def addItemToToolbar[T <: Node](toolBar: Object, item: T): T = {
    ComponentUtils.as[ToolBar](toolBar).getItems add item
    item
  }

  /**
   * Associates the specified action with the given button. This method deals
   * with the enabled property and the action listener. The checked property
   * is handled when creating the button.
   * @param action the action
   * @param button the button
   * @return the associated button
   */
  private def bindActionToButton(action: JavaFxAction, button: ButtonBase): ButtonBase = {
    bindActionToEnabledProperty(action, button.disableProperty)
    button.addEventHandler(ActionEvent.ACTION, action)
    button
  }
}
