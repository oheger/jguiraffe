/**
 * Copyright 2006-2014 The JGUIraffe Team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.jguiraffe.gui.platform.javafx.builder.components.table

import javafx.scene.control.{TableColumn, TextField, ContentDisplay, TableCell}
import net.sf.jguiraffe.gui.builder.components.tags.table.TableFormController
import org.apache.commons.lang.StringUtils
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.input.{KeyCode, KeyEvent}
import javafx.beans.value.{ObservableValue, ChangeListener}
import scala.collection.mutable.ListBuffer

/**
 * A specialized table cell implementation with extended edit capabilities.
 *
 * An instance is passed a
 * [[net.sf.jguiraffe.gui.builder.components.tags.table.TableFormController]]
 * and the index of the column. The controller is used to obtain the current value of the
 * cell and to store changed data - basically, it handles communication with the ''Form''
 * instance representing the current row. Note that the concrete type of objects stored
 * in the table does not matter because all properties are accessed via the form, i.e.
 * using reflection.
 *
 * @author Oliver Heger
 * @param formController the form controller
 * @param columnIndex the column index
 */
private class EditableTableCell(val formController: TableFormController,
                                val columnIndex: Int) extends TableCell[AnyRef, AnyRef] {
  /** The text field acting as editor. */
  private var textField: Option[TextField] = None

  /**
   * Notifies this object that the user wants to edit this cell.
   * The cell is updated correspondingly, the text field serving as cell editor
   * is created if necessary and initialized.
   */
  override def startEdit() {
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
  override def cancelEdit() {
    super.cancelEdit()
    setText(stringRepresentation())
    setContentDisplay(ContentDisplay.TEXT_ONLY)
    textField = None
  }

  /**
   * Sets the content to be displayed in this cell.
   * @param item the current data item
   * @param empty a flag whether this cell is empty
   */
  override def updateItem(item: AnyRef, empty: Boolean) {
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
   * Returns the current value of this cell as a string.
   * @return a string for the current value of this cell
   */
  protected def stringRepresentation(): String = {
    selectCurrentRow()
    val propValue = formController.getColumnValue(columnIndex)
    if (propValue == null) StringUtils.EMPTY else propValue.toString
  }

  /**
   * Requests a commit of an edit operation. The modified content of the cell is
   * passed as string. This implementation ensures that it is written back into
   * the form representing the current row.
   * @param text the new text content of this cell
   * @param focusLost a flag whether the focus was lost
   */
  protected def commitData(text: String, focusLost: Boolean) {
    commitEdit(getItem)
    selectCurrentRow()
    formController.setColumnValue(null, columnIndex, text)
  }

  /**
   * Creates the text field to be used as cell editor. Several listeners have
   * to be registered at the field to ensure that it behaves correctly.
   */
  private def createTextField(): TextField = {
    val field = new TextField(stringRepresentation())
    field.setMinWidth(getWidth - getGraphicTextGap * 2)
    field.setOnKeyPressed(new EventHandler[KeyEvent]() {
      @Override
      def handle(t: KeyEvent) {
        t.getCode match {
          case KeyCode.ENTER =>
            performCommit(focusLost = false)
          case KeyCode.ESCAPE =>
            cancelEdit()
          case KeyCode.TAB =>
            performCommit(focusLost = false)
            nextEditableColumn(!t.isShiftDown) foreach (getTableView.edit(getTableRow.getIndex, _))
          case _ =>
        }
      }
    })
    field.focusedProperty().addListener(new ChangeListener[java.lang.Boolean] {
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
    })

    field
  }

  /**
   * Triggers a commit operation. This method is called when the user wants to
   * complete an edit operation. It ensures that the updated cell data is
   * written back into the underlying table model.
   * @param focusLost a flag whether the commit is caused by a lost focus
   */
  private def performCommit(focusLost: Boolean) {
    textField foreach (t => commitData(t.getText, focusLost))
    setText(stringRepresentation())
  }

  /**
   * Selects the current row for the form controller. This method is always called
   * before data of this cell can be accessed.
   */
  private def selectCurrentRow() {
    formController selectCurrentRow getIndex
  }

  /**
   * Searches for the next editable column in the current row (either in forward
   * or backward direction). This method is called in reaction on the TAB key
   * while the user edits a cell.
   * @param forward flag for the search direction
   * @return an ''Option'' for the next editable column
   */
  private def nextEditableColumn(forward: Boolean): Option[TableColumn[AnyRef, _]] = {
    import scala.collection.JavaConversions._
    val columns = ListBuffer.empty[TableColumn[AnyRef, _]]
    getTableView.getColumns foreach (columns ++= leafColumns(_))
    //There is no other column that supports editing.
    if (columns.size < 2) {
      return None
    }
    val currentIndex = columns.indexOf(getTableColumn)
    var nextIndex = currentIndex
    if (forward) {
      nextIndex += 1
      if (nextIndex > columns.size - 1) {
        nextIndex = 0
      }
    } else {
      nextIndex -= 1
      if (nextIndex < 0) {
        nextIndex = columns.size - 1
      }
    }
    Some(columns(nextIndex))
  }

  /**
   * Helper method for obtaining all columns on the lowest level. Columns can be
   * nested. This method returns a collection with all columns that do not
   * contain any sub columns.
   * @param root the current starting point of the search for columns
   * @return a collection with all leaf columns
   */
  private def leafColumns(root: TableColumn[AnyRef, _]): Seq[TableColumn[AnyRef, _]] = {
    val columns = ListBuffer.empty[TableColumn[AnyRef, _]]
    if (root.getColumns.isEmpty) {
      //We only want the leaves that are editable.
      if (root.isEditable) {
        columns += root
      }
    } else {
      import scala.collection.JavaConversions._
      root.getColumns foreach (columns ++= leafColumns(_))
    }
    columns
  }
}
