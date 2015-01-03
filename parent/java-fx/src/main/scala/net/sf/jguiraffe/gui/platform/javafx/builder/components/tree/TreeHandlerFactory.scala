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
package net.sf.jguiraffe.gui.platform.javafx.builder.components.tree

import javafx.scene.control.{SelectionMode, TreeView}

import net.sf.jguiraffe.gui.builder.components.model.TreeConfigurationChangeHandler
import net.sf.jguiraffe.gui.builder.components.tags.TreeTag
import net.sf.jguiraffe.gui.forms.ComponentHandler
import net.sf.jguiraffe.gui.platform.javafx.common.FunctionCallback

/**
 * A factory class for creating component handlers for JavaFX tree view
 * components.
 *
 * This is the public entry point into the ''tree'' sub package. An instance
 * of this class is used by the JavaFX-specific ''ComponentManager''
 * implementation for processing tree view components. It creates fully
 * initialized components handlers for trees. Because this is not trivial -
 * multiple objects have to be created, initialized, and connected with each
 * other - a separate class was created for this purpose.
 */
class TreeHandlerFactory {
  def createTreeHandler(tag: TreeTag): ComponentHandler[Object] = {
    val tree = createTreeView(tag)
    val handler = new JavaFxTreeHandler(tree, tag.getName, tag.getTreeModel,
      createGraphicsHandler(tag), collection.mutable.Map.empty)
    val threadListener = new FxThreadModelChangeListener(handler)
    val changeHandler = new TreeConfigurationChangeHandler(tag.getTreeModel,
      threadListener)
    tag.getTreeModel addConfigurationListener changeHandler

    initCellFactory(tree, changeHandler)
    handler treeModelChanged tag.getTreeModel.getRootNode
    handler
  }

  /**
   * Creates the tree view component based on the properties of the given tag.
   * @param tag the tree tag
   * @return the tree view component
   */
  private def createTreeView(tag: TreeTag): TreeView[ConfigNodeData] = {
    val tree = new TreeView[ConfigNodeData]
    tree setEditable tag.isEditable
    tree.getSelectionModel setSelectionMode determineSelectionMode(tag)
    tree setShowRoot tag.isRootVisible
    tree
  }

  /**
   * Initializes the cell factory for the given tree view.
   * @param tree the tree view
   * @param changeHandler the change handler
   */
  private def initCellFactory(tree: TreeView[ConfigNodeData],
    changeHandler: TreeConfigurationChangeHandler) {
    tree setCellFactory FunctionCallback(tv => new ConfigNodeTreeCell(changeHandler))
  }

  /**
   * Determines the correct selection mode for the tree view.
   * @param tag the tree tag
   * @return the selection mode for the tree's selection model
   */
  private def determineSelectionMode(tag: TreeTag) =
    if (tag.isMultiSelection) SelectionMode.MULTIPLE
    else SelectionMode.SINGLE

  /**
   * Creates the graphic handler for the tree handler.
   * @param tag the tree tag
   * @return the graphic handler
   */
  private def createGraphicsHandler(tag: TreeTag): NodeGraphicsHandler =
    new NodeGraphicsHandlerImpl(tag.getResolvedIconHandler, tag.getIcons)
}
