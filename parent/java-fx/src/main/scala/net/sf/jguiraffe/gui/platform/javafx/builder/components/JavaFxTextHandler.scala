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

import scala.beans.BeanProperty

import javafx.beans.value.ObservableValue
import javafx.scene.control.TextInputControl
import net.sf.jguiraffe.gui.builder.components.model.TextHandler
import net.sf.jguiraffe.gui.platform.javafx.builder.event.ChangeEventSource

/**
 * A specialized ''ComponentHandler'' implementation for Java FX text controls.
 *
 * This handler class can be used for managing various Java FX text controls
 * like text fields and text areas. Access to the text data stored in the
 * control is provided. In addition, the ''TextHandler'' interface is
 * implemented which supports enhanced functionality related to text
 * processing.
 *
 * The implementation of most methods is pretty trivial. In most cases, it can
 * be directly delegated to the wrapped text control.
 *
 * @param txtControl the wrapped Java FX text control
 */
private class JavaFxTextHandler(txtControl: TextInputControl)
  extends JavaFxComponentHandler[String](txtControl)
  with TextHandler with ChangeEventSource {
  /** The type of this handler. We deal with String data. */
  @BeanProperty val `type` = classOf[String]

  def getData: String = txtControl.getText

  def setData(txt: String) {
    txtControl setText txt
  }

  def hasSelection: Boolean = txtControl.getSelection.getLength > 0

  def getSelectionStart: Int = txtControl.getSelection.getStart

  def getSelectionEnd: Int = txtControl.getSelection.getEnd

  def select(start: Int, end: Int) {
    txtControl.selectRange(start, end)
  }

  def selectAll() {
    txtControl.selectAll()
  }

  def clearSelection() {
    txtControl.deselect()
  }

  def getSelectedText: String = txtControl.getSelectedText

  def replaceSelectedText(text: String) {
    txtControl.replaceSelection(text)
  }

  def copy() {
    txtControl.copy()
  }

  def cut() {
    txtControl.cut()
  }

  def paste() {
    txtControl.paste()
  }

  /**
   * @inheritdoc This implementation returns the text property. So it is
   * possible to react on every change of the text stored in the wrapped
   * control.
   */
  def observableValue: ObservableValue[_ <: AnyRef] = txtControl.textProperty
}
