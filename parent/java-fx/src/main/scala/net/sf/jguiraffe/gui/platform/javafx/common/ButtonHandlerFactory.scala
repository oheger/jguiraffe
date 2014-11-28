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
package net.sf.jguiraffe.gui.platform.javafx.common

import javafx.scene.control.ButtonBase

import net.sf.jguiraffe.gui.forms.ComponentHandler

/**
 * A trait describing a factory object for
 * [[net.sf.jguiraffe.gui.forms.ComponentHandler]] instances for JavaFX buttons.
 *
 * Component handlers for buttons have to be created in multiple places: when
 * constructing the UI of a form, but also for a tool bar. This trait allows
 * reusing the code which implements this functionality without introducing a
 * tight coupling.
 */
trait ButtonHandlerFactory {
  /**
   * Creates a ''ComponentHandler'' for interacting with the specified button.
   * The handler is of type ''Boolean''; its data corresponds to the selected
   * state of the button.
   * @param button the button to be wrapped by the handler
   * @param command a command string to be used when registering action listeners at the
   *                component handler
   * @return a ''ComponentHandler'' for this button
   */
  def createButtonHandler(button: ButtonBase, command: String): ComponentHandler[java.lang.Boolean]
}
