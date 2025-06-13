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
package net.sf.jguiraffe.gui.platform.javafx.builder.components

import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.TextInputControl

/**
 * A trait that can be mixed into a Java FX text component to restrict the
 * maximum text length.
 *
 * Per default, Java FX text components do not have a property for setting a
 * maximum text length. The user can enter an arbitrary number of characters
 * into a text field. If the data entered in the field has a length
 * restriction - for instance if it should be stored in a database columns
 * with a specific length -, validation has to be applied to ensure that the
 * length restriction is not violated. However, it would be handy to tell the
 * input field to only accept a given number of characters.
 *
 * This trait adds this feature to arbitrary Java FX text input controls
 * derived from ''TextInputControl''. It introduces a new property for setting
 * a maximum text length. If it is set to a value greater than 0, all user
 * input is intercepted and checked against the length restriction. It is
 * only processed if the restriction is not violated. This also works with
 * selected text and copy & paste.
 */
trait TextLengthRestriction extends TextInputControl {
  /** The property for storing the maximum text length. */
  val maximumLengthProperty: IntegerProperty = new SimpleIntegerProperty

  /**
   * Returns the current restriction of the maximum text length.
   * @return the maximum number of characters allowed for this text control
   */
  def getMaximumLength: Int = maximumLengthProperty.get

  /**
   * Sets the maximum length restriction. Values less than or equal zero
   * disable the restriction. Positive values are interpreted as the maximum
   * number of allowed characters.
   * @param len the maximum number of characters allowed for this text control
   */
  def setMaximumLength(len: Int) {
    maximumLengthProperty set len
  }

  /**
   * @inheritdoc This implementation allows the current text change only if
   * the maximum text length restriction is not violated. If necessary, only
   * parts of the new text are inserted. If the maximum text length is already
   * reached, no text can be added.
   */
  override abstract def replaceText(start: Int, end: Int, ntxt: String) {
    val replData = handleReplacement(start, end, ntxt)
    if (replData._1) {
      super.replaceText(start, end, replData._2)
    }
  }

  /**
   * @inheritdoc This implementation allows the change of the selection only if
   * the maximum text length restriction is not violated. If necessary, only
   * parts of the new text are inserted. If the maximum text length is already
   * reached, no text can be added.
   */
  override abstract def replaceSelection(ntxt: String) {
    val selRange = getSelection
    val replData = handleReplacement(selRange.getStart, selRange.getEnd, ntxt)
    if (replData._1) {
      super.replaceSelection(replData._2)
    }
  }

  /**
   * Handles a replacement operation of the current text in this text
   * component. This method is eventually called for all text changes. It
   * checks whether the current change causes the text length to grow. If so,
   * the maximum length restriction is applied. If necessary, the text to be
   * inserted is truncated, or the operation is canceled completely. The
   * return value is used by the calling methods to figure out how to proceed.
   * The boolean value determines whether the operation can continue (a value
   * of '''false''' means that it should be canceled). The String value is the
   * text to be added which may have been truncated if necessary.
   * @param start the start position of the text to be replaced
   * @param end the end position of the text to be replaced (excluding)
   * @param ntxt the replacement text for the specified range
   * @return a tuple with the continue flag and the text to be added
   */
  private def handleReplacement(start: Int, end: Int, ntxt: String): (Boolean, String) = {
    if (getMaximumLength <= 0) (true, ntxt)
    else {
      val delta = ntxt.length - (end - start)
      val capacity = getMaximumLength - getLength

      if (delta < capacity) {
        (true, ntxt)
      } else {
        val replCapacity = capacity + end - start
        if (replCapacity <= 0) {
          (false, ntxt)
        } else {
          (true, ntxt.substring(0, replCapacity))
        }
      }
    }
  }
}
