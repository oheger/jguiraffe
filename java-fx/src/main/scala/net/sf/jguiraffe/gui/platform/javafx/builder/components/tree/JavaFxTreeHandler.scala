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

import scala.beans.BeanProperty
import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.mutable.Map

import org.apache.commons.configuration.HierarchicalConfiguration
import org.apache.commons.configuration.tree.ConfigurationNode

import javafx.scene.control.SelectionMode
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import net.sf.jguiraffe.gui.builder.components.model.TreeExpansionListener
import net.sf.jguiraffe.gui.builder.components.model.TreeHandler
import net.sf.jguiraffe.gui.builder.components.model.TreeModelChangeListener
import net.sf.jguiraffe.gui.builder.components.model.TreeNodePath
import net.sf.jguiraffe.gui.builder.components.model.TreePreExpansionListener
import net.sf.jguiraffe.gui.platform.javafx.builder.components.JavaFxComponentHandler

/**
 * A specialized ''ComponentHandler'' implementation for JavaFX tree view
 * components.
 *
 * This class wraps a JavaFX ''TreeView'' component. It implements typical
 * ''ComponentHandler'' functionality plus additional features defined by
 * the ''TreeHandler'' interface.
 *
 * @param tree the managed tree view
 * @param model the configuration object serving as the tree's data model
 * @param graphicHandler the object providing access to graphics for tree items
 * @param itemMap a map for accessing all tree items
 */
private class JavaFxTreeHandler(tree: TreeView[ConfigNodeData],
  @BeanProperty val model: HierarchicalConfiguration,
  val graphicHandler: NodeGraphicsHandler,
  val itemMap: Map[ConfigurationNode, ConfigTreeItem])
  extends JavaFxComponentHandler[Object](tree)
  with TreeModelChangeListener with TreeHandler {
  /** Flag whether multiple selection is supported. */
  private val multiSelection =
    tree.getSelectionModel.getSelectionMode == SelectionMode.MULTIPLE

  /** The data type of this handler. It depends on the selection mode. */
  @BeanProperty val `type` = if (multiSelection) classOf[Array[TreeNodePath]]
  else classOf[TreeNodePath]

  /** The current root node of the model configuration. */
  private var currentRootNode: ConfigurationNode = _

  /**
   * @inheritdoc This implementation, depending on the selection mode, either
   * delegates to ''getSelectedPath()'' or ''getSelectedPaths()''. In the latter
   * case, in contrast to ''getSelectedPaths()'', result is '''null''' if there
   * is no selection.
   */
  def getData: Object = {
    if (multiSelection) {
      val paths = getSelectedPaths
      if (paths.isEmpty) null
      else paths
    } else getSelectedPath
  }

  /**
   * @inheritdoc This implementation clears the selection and then evaluates
   * the passed in data object. If it is a ''TreeNodePath'' or an array
   * thereof, the tree's selection is set. Otherwise, it is just ignored.
   */
  def setData(data: Object) {
    clearSelection()
    data match {
      case paths: Array[TreeNodePath] =>
        setSelectedPaths(paths)

      case path: TreeNodePath =>
        setSelectedPath(path)

      case _ => // ignore
    }
  }

  /**
   * @inheritdoc This implementation synchronizes tree items with the
   * underlying configuration node structure if necessary. It also checks
   * whether the model was changed completely (i.e. the root node was changed);
   * in this case, a new tree item structure has to be set up.
   * '''Note''': This implementation assumes that it is called in
   * the FX event thread! So there must be an external component ensuring this.
   */
  def treeModelChanged(node: ConfigurationNode) {
    if (currentRootNode ne model.getRootNode()) {
      setUpTreeItemStructure()
    } else {
      itemMap.get(node) foreach (_.resync())
    }
  }

  /**
   * @inheritdoc This implementation transforms the tree's current selection
   * into a ''TreeNodePath''. If there is no selection, result is '''null'''.
   */
  def getSelectedPath: TreeNodePath = {
    val selItem = tree.getSelectionModel.getSelectedItem
    if (selItem != null) new TreeNodePath(selItem.getValue.node)
    else null
  }

  def setSelectedPath(path: TreeNodePath) {
    clearSelection()
    addSelectedPath(path)
  }

  /**
   * @inheritdoc This implementation transforms the tree's current selection
   * into a (potential empty) array of ''TreeNodePath'' objects.
   */
  def getSelectedPaths: Array[TreeNodePath] = {
    import collection.JavaConversions._
    val paths = for (item <- tree.getSelectionModel.getSelectedItems)
      yield new TreeNodePath(item.getValue.node)
    paths.toArray
  }

  def setSelectedPaths(paths: Array[TreeNodePath]) {
    clearSelection()
    for (p <- paths) {
      addSelectedPath(p)
    }
  }

  /**
   * @inheritdoc This implementation ensures that the target node to be
   * selected has already be initialized. Then the corresponding tree item is
   * added to the list of selected items.
   */
  def addSelectedPath(path: TreeNodePath) {
    ensureInitialized(path.getTargetNode) foreach {
      tree.getSelectionModel select _
    }
  }

  def clearSelection() {
    tree.getSelectionModel.clearSelection()
  }

  def addExpansionListener(l: TreeExpansionListener) {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def removeExpansionListener(l: TreeExpansionListener) {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def addPreExpansionListener(l: TreePreExpansionListener) {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def removePreExpansionListener(l: TreePreExpansionListener) {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  /**
   * @inheritdoc This implementation ensures that the whole path starting with
   * the passed in target node gets expanded.
   */
  def expand(path: TreeNodePath) {
    ensureInitialized(path.getTargetNode) foreach (expandPath)
  }

  /**
   * @inheritdoc This implementation determines the tree item corresponding to
   * the passed in path. If this is successful, the expanded flag is set to
   * '''false'''. Other nodes on this path are not affected.
   */
  def collapse(path: TreeNodePath) {
    itemMap.get(path.getTargetNode) foreach (_.setExpanded(false))
  }

  /**
   * Initializes the tree's item structure. This method sets a new root node
   * for the managed tree view.
   */
  private def setUpTreeItemStructure() {
    tree setRoot createRootItem()
  }

  /**
   * Creates a new root item for the managed tree.
   * @return the newly created root item
   */
  private def createRootItem(): ConfigTreeItem = {
    currentRootNode = model.getRootNode
    new ConfigTreeItem(currentRootNode, graphicHandler, itemMap)
  }

  /**
   * Ensures that the full path to a configuration node has been initialized.
   * Because the model of the managed tree is lazily initialized it can happen
   * that clients wants to perform operations on nodes which have not yet been
   * created. In this case, the full path to such a target node has to be
   * initialized. This is done by this method. It returns the tree item
   * corresponding to the passed in node or ''None'' if it cannot be
   * determined (this means that the passed in node does not belong to the
   * managed tree).
   * @param node the target node
   * @return an option with the corresponding tree item
   */
  private def ensureInitialized(node: ConfigurationNode): Option[ConfigTreeItem] = {
    if (node == null) None
    else {
      itemMap.get(node) orElse (ininitializeParentPath(node))
    }
  }

  /**
   * Tries to initialize the path to the parent of the passed in node. This
   * method is used internally by ''ensureInitialized()'' if a specific tree
   * item does not exist yet.
   * @param node the target node
   * @return an option with the corresponding tree item
   */
  private def ininitializeParentPath(node: ConfigurationNode): Option[ConfigTreeItem] = {
    val optItem = ensureInitialized(node.getParentNode)
    optItem foreach (_.getChildren) // initializes the child items
    itemMap.get(node)
  }

  /**
   * Expands the path from the given tree item up to the root item.
   * @param item the item to start with
   */
  private def expandPath(item: TreeItem[_]) {
    if (item != null) {
      item setExpanded true
      expandPath(item.getParent)
    }
  }
}
