/**
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
package net.sf.jguiraffe.gui.platform.javafx.builder.components.tree

import javafx.event.EventHandler
import javafx.scene.control.TextField
import javafx.scene.control.TreeCell
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent

/**
 * An internally used helper class serving as visual representation of tree
 * nodes supporting node editing facilities.
 *
 * This is a specialized implementation of a JavaFX ''TreeCell''. It
 * collaborates with a
 * [[net.sf.jguiraffe.gui.platform.javafx.builder.components.tree.ConfigCellController]]
 * in order to handle update operations - if a user edits a tree node, the
 * underlying ''ConfigurationNode'' is updated.
 *
 * @param controller the ''ConfigCellController'
 */
private class ConfigNodeTreeCell(controller: ConfigCellController)
  extends TreeCell[ConfigNodeData] {
  /** The edit field associated with this cell. */
  private lazy val editField = createEditField()

  /**
   * The user is going to edit this tree node. This implementation delegates to
   * the associated ''ConfigCellController''.
   */
  override def startEdit() {
    super.startEdit()
    controller handleStartEdit cellData
  }

  /**
   * The user cancels the current edit operation. This implementation delegates
   * to the associated ''ConfigCellController''.
   */
  override def cancelEdit() {
    super.cancelEdit()
    controller handleCancelEdit cellData
  }

  /**
   * Updates the item of this cell. This method is called when the associated
   * tree node is changed. This implementation delegates to the associated
   * ''ConfigCellController''.
   * @param nd the new data item
   * @param empty flag whether the item is empty
   */
  override def updateItem(nd: ConfigNodeData, empty: Boolean) {
    super.updateItem(nd, empty)
    controller.handleUpdateItem(cellData, empty, isEditing)
  }

  /**
   * Returns a ''CellData'' object describing the current state of this cell.
   * @return a current ''CellData'' object
   */
  def cellData = CellData(view = this, editField = this.editField,
    data = getItem, treeItem = getTreeItem)

  /**
   * Handles special key codes in the edit text field that affect the current
   * edit operation. This method checks for the Escape or the Enter keys which
   * cancel or commit the edit operation respectively.
   * @param code the key code to be processed
   */
  def handleEditFieldKeyCode(code: KeyCode) {
    code match {
      case KeyCode.ESCAPE =>
        cancelEdit()

      case KeyCode.ENTER =>
        commitEdit(controller.handleCommitEdit(cellData))

      case _ =>
        // ignore all other keys
    }
  }

  /**
   * Creates a text field that is used when the user wants to edit this cell.
   * The text field handles some special key codes for committing or aborting
   * the edit operation.
   * @return the newly created text field
   */
  private def createEditField(): TextField = {
    val field = new TextField
    field setOnKeyReleased (new EventHandler[KeyEvent] {
      def handle(event: KeyEvent) {
        handleEditFieldKeyCode(event.getCode)
      }
    })
    field
  }
}
