/*
 * Copyright 2006-2018 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.platform.javafx.builder.utils

import javafx.scene.control.Button

import net.sf.jguiraffe.gui.app.ApplicationContext

/**
 * A trait for obtaining the different buttons for message windows.
 *
 * The default message dialogs produced by a ''MessageOutput'' object can contain
 * a number of different buttons. All requests for buttons are handled by this
 * trait. Thus it is possible to inject specific button implementations.
 *
 * This base implementation produces normal JavaFX buttons whose titles are obtained
 * from a default resource bundle shipped with the ''JGUIraffe JavaFX'' library.
 */
trait MessageOutputButtonProvider {
  /** The name of the resource bundle with message resources. */
  val MessageResourceGroup = "messageresources"

  /** The resource key for for the title of the OK button. */
  final val KeyOkButton = "BTN_OK"

  /** The resource key for the title of the CANCEL button. */
  final val KeyCancelButton = "BTN_CANCEL"

  /** The resource key for the title of the YES button. */
  final val KeyYesButton = "BTN_YES"

  /** The resource key for the title of the NO button. */
  final val KeyNoButton = "BTN_NO"

  /**
   * Returns the OK button.
   * @param context the application context
   * @return the OK button
   */
  def okButton(context: ApplicationContext): Button = createButton(context, KeyOkButton)

  /**
   * Returns the CANCEL button.
   * @param context the application context
   * @return the CANCEL button
   */
  def cancelButton(context: ApplicationContext): Button = {
    val button = createButton(context, KeyCancelButton)
    button setCancelButton true
    button
  }

  /**
   * Returns the YES button.
   * @param context the application context
   * @return the YES button
   */
  def yesButton(context: ApplicationContext): Button = createButton(context, KeyYesButton)

  /**
   * Returns the NO button.
   * @param context the application context
   * @return the NO button
   */
  def noButton(context: ApplicationContext): Button = createButton(context, KeyNoButton)

  /**
   * Helper method for creating and initializing a button with a caption defined by the
   * passed in resource key. This method is called by the base implementations of the
   * methods for returning specific buttons. It implements the default button
   * creation and initializing logic.
   * @param context the application context (for resolving the resource key)
   * @param resKey the resource key for the button caption
   * @return the newly created button object
   */
  protected def createButton(context: ApplicationContext, resKey: String): Button =
    new Button(context.getResourceText(MessageResourceGroup, resKey))
}
