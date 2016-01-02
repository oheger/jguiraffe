/*
 * Copyright 2006-2016 The JGUIraffe Team.
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

import javafx.scene.control.TreeCell

import net.sf.jguiraffe.gui.builder.components.model.TreeConfigurationChangeHandler
import net.sf.jguiraffe.gui.platform.javafx.builder.components.cell.EditableCell

/**
 * An internally used helper class serving as visual representation of tree
 * nodes supporting node editing facilities.
 *
 * This is a specialized implementation of a JavaFX ''TreeCell''. It
 * collaborates with a
 * [[net.sf.jguiraffe.gui.builder.components.model.TreeConfigurationChangeHandler]]
 * in order to handle update operations - if a user edits a tree node, the
 * underlying ''ConfigurationNode'' is updated. By mixing in ''EditableCell''
 * the major part of the functionality for making the cell editable is already in
 * place.
 *
 * @param changeHandler the ''TreeConfigurationChangeHandler''
 */
private class ConfigNodeTreeCell(val changeHandler: TreeConfigurationChangeHandler)
  extends TreeCell[ConfigNodeData] with EditableCell[ConfigNodeData] {
  /**
   * @inheritdoc
   * This implementation returns a string representation of the underlying
   * ''ConfigNodeData'' object. This is the name of the configuration node
   * represented by this cell.
   */
  override protected def stringRepresentation(): String = getItem.toString

  /**
   * Requests a commit of an edit operation. This method is called when an edit
   * operation is complete, and the text entered by the user has to be written
   * into the underlying data model. The modified content of the cell is
   * passed as string.
   * @param text the new text content of this cell
   * @param focusLost a flag whether the focus was lost
   */
  override protected def commitData(text: String, focusLost: Boolean) {
    changeHandler.changeNodeName(getItem.node, text)
  }
}
