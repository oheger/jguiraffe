/*
 * Copyright 2006-2017 The JGUIraffe Team.
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

import javafx.beans.property.BooleanProperty
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.control.MenuItem

import net.sf.jguiraffe.gui.forms.ComponentHandler
import net.sf.jguiraffe.gui.platform.javafx.builder.event.ActionEventSource

/**
 * An internally used ''ComponentHandler'' implementation which wraps a JavaFX
 * ''MenuItem''.
 *
 * Instances of this class are returned by the action manager's
 * ''createMenuItem()'' method which is passed an action data object. Rather
 * than returning the newly created menu item as an untyped object, the
 * ''ActionManager'' interface requires the method to return a
 * ''ComponentHandler''. This allows client code to manipulate the menu item
 * via the ''ComponentHandler'' interface and also to register event listeners.
 *
 * Unfortunately, it is not possible to reuse the ''ComponentHandler''
 * implementation for JavaFX controls because ''MenuItem'' does not extend the
 * ''Node'' base class. Therefore, a full implementation of the handler
 * interface is required.
 *
 * This handler implementation deals with normal menu items and checked items.
 * Its data is the selected state of the menu item. This data is simply mapped
 * to a boolean property passed to the constructor. The creating instance is
 * responsible for setting the correct property.
 *
 * @param item the wrapped menu item
 * @param property the property storing the data of this handler
 * @param actionCommand the command to be published for action events
 */
private class MenuItemComponentHandler(val item: MenuItem, val property: BooleanProperty,
                                       override val actionCommand: String)
  extends ComponentHandler[java.lang.Boolean] with ActionEventSource {
  override val getComponent = item

  override val getOuterComponent = item

  val getType = classOf[java.lang.Boolean]

  /**
   * @inheritdoc
   * This implementation returns the value of the associated boolean property.
   */
  override def getData: java.lang.Boolean = property.get

  /**
   * @inheritdoc
   * This implementation writes the passed in data into the associated property.
   */
  override def setData(data: java.lang.Boolean): Unit = {
    property setValue data
  }

  /**
   * @inheritdoc
   * This implementation returns the negation of the menu item's disabled
   * property.
   */
  override def isEnabled: Boolean = !item.isDisable

  /**
   * @inheritdoc
   * This implementation sets the menu item's disabled state to the negation of
   * the passed in flag.
   */
  override def setEnabled(f: Boolean): Unit = item setDisable !f

  /**
   * @inheritdoc
   * This implementation adds the specified handler to the wrapped menu item.
   */
  override def addActionListener(handler: EventHandler[ActionEvent]): Unit =
    item.addEventHandler(ActionEvent.ACTION, handler)

  /**
   * @inheritdoc
   * This implementation removes the specified handler from the wrapped menu
   * item.
   */
  override def removeActionListener(handler: EventHandler[ActionEvent]): Unit =
    item.removeEventHandler(ActionEvent.ACTION, handler)
}
