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
package net.sf.jguiraffe.gui.platform.javafx.builder.utils

import javafx.scene.image.Image

import net.sf.jguiraffe.gui.builder.utils.MessageOutput

/**
 * A trait for obtaining the different icons for message dialogs of different
 * types.
 *
 * Each message dialog type (information, question, warning, error) is
 * associated with a specific icon. The concrete icons to be used can be
 * queried via this trait. This makes it possible to override the default
 * icons with custom ones.
 */
trait MessageOutputIconProvider {
  /** Name of the info icon. */
  private val NameInfoIcon = "icon_info.gif"

  /** Name of the question icon. */
  private val NameQuestionIcon = "icon_question.gif"

  /** Name of the warning icon. */
  private val NameWarningIcon = "icon_warning.gif"

  /** Name of the error icon. */
  private val NameErrorIcon = "icon_error.gif"

  /** The image for an information message. */
  lazy val iconInfo = loadIconFromResources(NameInfoIcon)

  /** The image for a confirmation/question message. */
  lazy val iconQuestion = loadIconFromResources(NameQuestionIcon)

  /** The image for a warning message. */
  lazy val iconWarning = loadIconFromResources(NameWarningIcon)

  /** The image for an error message. */
  lazy val iconError = loadIconFromResources(NameErrorIcon)

  /**
   * Returns the image for the specified message type. The return value is
   * actually an option. It is possible that specific message types should not
   * have their own icon. In this case, a concrete implementation can
   * return ''None''. This base implementation returns a set of default icons
   * for the pre-defined message types.
   * @param messageType the message type (a constant defined by ''MessageOutput'')
   * @return an option with the image for this message type
   */
  def messageIcon(messageType: Int): Option[Image] = {
    messageType match {
      case MessageOutput.MESSAGE_INFO =>
        Some(iconInfo)
      case MessageOutput.MESSAGE_QUESTION =>
        Some(iconQuestion)
      case MessageOutput.MESSAGE_WARNING =>
        Some(iconWarning)
      case MessageOutput.MESSAGE_ERROR =>
        Some(iconError)
      case _ => None
    }
  }

  /**
   * Loads an icon with the specified name from the class path. It is assumed
   * that this icon is located in the root of the class path and that it
   * actually exists.
   * @param name the name of the icon to be loaded
   * @return the ''Image'' that was loaded
   */
  protected def loadIconFromResources(name: String): Image = {
    val url = getClass.getResource("/" + name)
    new Image(url.toExternalForm)
  }
}
