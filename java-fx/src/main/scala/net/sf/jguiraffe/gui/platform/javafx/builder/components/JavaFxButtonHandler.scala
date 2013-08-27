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
package net.sf.jguiraffe.gui.platform.javafx.builder.components

import scala.beans.BeanProperty

import javafx.scene.control.ButtonBase

/**
 * The ''ComponentHandler'' implementation for JavaFX command buttons.
 *
 * Although buttons are handled as input components, JavaFX command buttons do
 * not really maintain a state. The JavaFX API does not allow querying the
 * button's pressed state which could serve as the button's state. Therefore,
 * in this handler implementation the methods for getting and setting component
 * data are just dummies.
 *
 * The main functionality of a button handler is its support for action
 * listeners. This is achieved by mixing in the ''ControlActionEventSource''
 * trait.
 *
 * Note that this class serves as a base class for handlers for other
 * button-like components like toggle buttons, or check boxes.
 *
 * @param button the ''ButtonBase'' control wrapped by this handler
 */
private class JavaFxButtonHandler(button: ButtonBase)
  extends JavaFxComponentHandler[java.lang.Boolean](button)
  with ControlActionEventSource[java.lang.Boolean] {
  @BeanProperty val `type` = classOf[java.lang.Boolean]

  def getData: java.lang.Boolean = java.lang.Boolean.FALSE

  def setData(f: java.lang.Boolean) {
    setSelectedState(f != null && f.booleanValue)
  }

  /**
   * Sets the wrapped button's selected state. This method is called by
   * ''setData()'' with the converted ''Boolean'' value. (''setData() has
   * already handled '''null''' input.) Derived classes should override this
   * method to actually change the wrapped component's state. This base
   * implementation is just an empty dummy.
   * @param f the new selected state of the managed component
   */
  protected def setSelectedState(f: Boolean) {
  }
}
