/*
 * Copyright 2006-2015 The JGUIraffe Team.
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

import net.sf.jguiraffe.gui.builder.components.FormBuilderException

import scala.reflect.Manifest

/**
 * An object providing common functionality related to the creation and
 * manipulation of JavaFX components.
 *
 * This object defines multiple methods implementing standard functionality
 * which is needed for multiple manager implementations.
 */
object ComponentUtils {
  /** Constant for the mnemonic marker. */
  private val MnemonicMarker = '_'

  /**
   * Helper method for converting an object to the specified type. Because the
   * JGUIraffe library operates on abstract and generic objects type casts have
   * to be performed frequently. This helper method tries to cast the specified
   * object to the desired result type. If this fails, an exception is thrown.
   * @tparam T the result type
   * @param obj the object to be converted (must not be '''null''')
   * @return the converted object
   * @throws FormBuilderException if conversion fails
   */
  def as[T](obj: Any)(implicit m: Manifest[T]): T = {
    obj match {
      case t: T => t
      case _ =>
        throw new FormBuilderException("Wrong object! Expected " + m.toString +
          ", was: " + obj)
    }
  }

  /**
   * Adds a mnemonic marker to the specified text if possible. In Java FX, a
   * mnemonic character is specified by putting an underscore in front of it.
   * This method tries to find the given mnemonic character in the text. If
   * it is found (ignoring case), the text is modified to contain the
   * underscore.
   * @param txt the text to be modified
   * @param mnemonic the mnemonic character
   * @return the manipulated string
   */
  def mnemonicText(txt: String, mnemonic: Char): String = {
    if (txt == null) null
    else {
      val pos = indexOfMnemonic(txt, mnemonic)
      if (pos < 0) txt
      else txt.take(pos) + MnemonicMarker + txt.drop(pos)
    }
  }

  /**
   * Determines the position of the mnemonic character in the given string.
   * This method performs a search of the mnemonic character ignoring case.
   * If no such character is found, result is -1.
   * @param txt the text to be scanned
   * @param mnemonic the mnemonic character
   * @return the position of the mnemonic character or -1
   */
  private def indexOfMnemonic(txt: String, mnemonic: Char): Int = {
    val pos = txt indexOf mnemonic
    if (pos >= 0) pos
    else txt indexOf switchCase(mnemonic)
  }

  /**
   * Switches the case of the given character. If it is in lowercase, result is
   * the character in uppercase or vice versa.
   * @param c the character to be switched
   * @return the character with switched case
   */
  private def switchCase(c: Char): Char =
    if (c.isUpper) c.toLower
    else c.toUpper
}
