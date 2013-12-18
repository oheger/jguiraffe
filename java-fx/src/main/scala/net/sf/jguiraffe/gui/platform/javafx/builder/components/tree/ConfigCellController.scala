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
package net.sf.jguiraffe.gui.platform.javafx.builder.components.tree

import org.apache.commons.lang.StringUtils

import javafx.scene.control.Labeled
import javafx.scene.control.TextField
import javafx.scene.control.TreeItem
import net.sf.jguiraffe.gui.builder.components.model.TreeConfigurationChangeHandler

/**
 * A data class describing the current status of a tree cell.
 *
 * A tree cell passes an object of this class to its controller when an update
 * operation has to be executed. The object contains all information required
 * by the controller to perform the update and to change the state of the cell.
 *
 * @param data the ''ConfigNodeData'' object pointing to the current node
 * @param treeItem the ''TreeItem'' object for the current tree node
 * @param view the ''Labeled'' object for the graphical representation of the
 * cell
 * @param editField the text field serving as cell editor
 */
private case class CellData(data: ConfigNodeData,
  treeItem: TreeItem[ConfigNodeData],
  view: Labeled,
  editField: TextField)

/**
 * A helper class for managing updates of a ''TreeCell''.
 *
 * Each ''TreeCell'' in a JGUIraffe tree has a reference of an instance of
 * this class. The controller contains logic for updating a cell and changing
 * its visual representation when the user edits the owning tree.
 *
 * Testing functionality of a JavaFX ''TreeCell'' implementation is very hard.
 * Therefore, logic was extracted into this helper controller class. The basic
 * idea is that a cell object delegates calls related to editing its content
 * to the controller instance passing in all required information about its
 * current state. The controller can then react accordingly.
 *
 * @param changeHandler the ''TreeConfigurationChangeHandler''
 */
private class ConfigCellController(changeHandler: TreeConfigurationChangeHandler) {
  /**
   * Handles the start of an edit operation. This method is called when the
   * user wants to edit the content of a tree node.
   * @param cellData the data object describing the affected cell
   */
  def handleStartEdit(cellData: CellData) {
    initEditField(cellData)
    cellData.editField.selectAll()
    cellData.editField.requestFocus()
  }

  /**
   * Handles a cancellation of an edit operation. This method is called when
   * the user aborts editing of a tree node. Then it has to be reset to its
   * original state.
   * @param cellData the data object describing the affected cell
   */
  def handleCancelEdit(cellData: CellData) {
    resetView(cellData)
  }

  /**
   * Handles an update of the content of a node. This method is called when the
   * value of a node changes, probably due to a committed edit operation.
   * @param cellData the data object describing the affected cell
   * @param empty a flag whether the new value is empty
   * @param editing a flag whether the cell is currently edited
   */
  def handleUpdateItem(cellData: CellData, empty: Boolean, editing: Boolean) {
    if (empty) {
      cellData.view setText null
      cellData.view setGraphic null
    } else if (editing) {
      initEditField(cellData)
    } else {
      resetView(cellData)
    }
  }

  /**
   * Handles a completed edit operation. The text entered into the edit field
   * becomes the new node name.
   * @param cellData the data object describing the affected cell
   * @return the new data item for the calling cell
   */
  def handleCommitEdit(cellData: CellData): ConfigNodeData = {
    changeHandler.changeNodeName(cellData.data.node, cellData.editField.getText)
    cellData.data
  }

  /**
   * Resets the visual properties to the information stored in the given
   * ''CellData'' object.
   * @param cellData the current state of the cell
   */
  private def resetView(cellData: CellData) {
    cellData.view setText itemString(cellData)
    cellData.view setGraphic cellData.treeItem.getGraphic
  }

  /**
   * Initializes the edit field from the data in the given ''CellData'' object.
   * @param cellData the ''CellData''
   */
  private def initEditField(cellData: CellData) {
    cellData.view setText null
    cellData.view setGraphic cellData.editField
    cellData.editField setText itemString(cellData)
  }

  /**
   * Returns a string for the specified item data. The data can be '''null''',
   * thus the effort.
   * @param cellData the ''CellData'' object of the associated cell
   * @return a string representation of the current item data
   */
  private def itemString(cellData: CellData): String =
    if (cellData.data != null) cellData.data.toString
    else StringUtils.EMPTY
}
