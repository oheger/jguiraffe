/*
 * Copyright 2006-2022 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.platform.javafx.builder.components.cell

import javafx.scene.control.{ContentDisplay, TextField, IndexedCell}
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.input.{KeyCode, KeyEvent}
import javafx.beans.value.{ObservableValue, ChangeListener}

/**
 * A trait providing functionality for editable cells.
 *
 * This trait can be mixed into custom implementations of JavaFX cells, e.g. for
 * trees or tables. It implements the major part of the functionality for making
 * the cell editable. This includes providing a text field acting as editor
 * control. The text field is accordingly configured to react on typical key
 * events for committing or canceling the edit operation.
 *
 * A concrete sub class has to implement a method for persisting a successful
 * edit operation and a method which returns a string representing the current
 * content of the cell.
 *
 * It is also possible to enhance the functionality for reacting on keys in the
 * edit text field. For this purpose a partial function can be returned by the
 * ''editKeyHandler()'' method.
 *
 * @tparam T the type of the content of the cell
 */
trait EditableCell[T] extends IndexedCell[T] {
  /** Type definition of the key handler function. */
  type KeyHandler = PartialFunction[KeyEvent, Unit]

  /** Stores the key handler function used by the edit text field. */
  private lazy val keyHandlerFunction: KeyHandler = editKeyHandler

  /** The text field acting as editor. */
  private var textField: Option[TextField] = None

  /**
   * Notifies this object that the user wants to edit this cell.
   * The cell is updated correspondingly, the text field serving as cell editor
   * is created if necessary and initialized.
   */
  override abstract def startEdit() {
    super.startEdit()
    if (!textField.isDefined) {
      textField = Some(createTextField())
    }
    setGraphic(textField.get)
    setContentDisplay(ContentDisplay.GRAPHIC_ONLY)
    Platform.runLater(new Runnable() {
      @Override
      def run() {
        textField.get.selectAll()
        textField.get.requestFocus()
      }
    })
  }

  /**
   * Cancels the current edit operation. The cell is restored with the original
   * content.
   */
  override abstract def cancelEdit() {
    textField = None
    super.cancelEdit()
    setText(stringRepresentation())
    setContentDisplay(ContentDisplay.TEXT_ONLY)
  }

  /**
   * Sets the content to be displayed in this cell.
   * @param item the current data item
   * @param empty a flag whether this cell is empty
   */
  override abstract def updateItem(item: T, empty: Boolean) {
    super.updateItem(item, empty)
    if (empty) {
      setText(null)
      setGraphic(null)
    } else {
      if (isEditing && textField.isDefined) {
        textField.get setText stringRepresentation()
        setGraphic(textField.get)
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY)
      } else {
        setText(stringRepresentation())
        setContentDisplay(ContentDisplay.TEXT_ONLY)
      }
    }
  }

  /**
   * Returns a key handler function for processing user input in the edit text
   * field. This function can process specific key codes (e.g. ENTER or ESC)
   * and react accordingly. This base implementation handles events for
   * committing and canceling the current edit operation.
   * @return the function for handling user input in the edit field
   */
  protected def editKeyHandler: KeyHandler = {
    case ev: KeyEvent if ev.getCode == KeyCode.ENTER =>
      performCommit(focusLost = false)
    case ev: KeyEvent if ev.getCode == KeyCode.ESCAPE =>
      cancelEdit()
  }

  /**
   * Triggers a commit operation. This method is called when the user wants to
   * complete an edit operation. It ensures that the updated cell data is
   * written back into the underlying table model.
   * @param focusLost a flag whether the commit is caused by a lost focus
   */
  protected def performCommit(focusLost: Boolean) {
    textField foreach (t => commitData(t.getText, focusLost))
    setText(stringRepresentation())
  }

  /**
   * Returns the current value of this cell as a string. Concrete sub classes
   * have to provide a suitable string representation of their current
   * content.
   * @return a string for the current value of this cell
   */
  protected def stringRepresentation(): String

  /**
   * Requests a commit of an edit operation. This method is called when an edit
   * operation is complete, and the text entered by the user has to be written
   * into the underlying data model. The modified content of the cell is
   * passed as string.
   * @param text the new text content of this cell
   * @param focusLost a flag whether the focus was lost
   */
  protected def commitData(text: String, focusLost: Boolean): Unit

  /**
   * Creates the text field to be used as cell editor. Several listeners have
   * to be registered at the field to ensure that it behaves correctly.
   */
  private def createTextField(): TextField = {
    val field = new TextField(stringRepresentation())
    field setMinWidth (getWidth - getGraphicTextGap * 2)
    field setOnKeyPressed new EventHandler[KeyEvent]() {
      @Override
      def handle(event: KeyEvent) {
        if (keyHandlerFunction.isDefinedAt(event)) {
          keyHandlerFunction.apply(event)
        }
      }
    }
    field.focusedProperty addListener new ChangeListener[java.lang.Boolean] {
      @Override
      def changed(observable: ObservableValue[_ <: java.lang.Boolean],
                  oldValue: java.lang.Boolean, newValue: java.lang.Boolean) {
        //This focus listener fires at the end of cell editing when focus is lost
        //and when enter is pressed (because that causes the text field to lose focus).
        //The problem is that if enter is pressed then cancelEdit is called before this
        //listener runs and therefore the text field has been cleaned up. If the
        //text field is null we don't commit the edit. This has the useful side effect
        //of stopping the double commit.
        if (!newValue && textField.isDefined) {
          performCommit(focusLost = true)
        }
      }
    }

    field
  }
}
