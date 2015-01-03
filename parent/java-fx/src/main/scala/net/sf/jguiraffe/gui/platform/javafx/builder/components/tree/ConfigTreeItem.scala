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

import javafx.collections.{FXCollections, ObservableList}
import javafx.scene.control.TreeItem

import org.apache.commons.configuration.tree.ConfigurationNode

import scala.collection.JavaConversions.{asScalaBuffer, asScalaSet}
import scala.collection.mutable

/**
 * A specialized ''TreeItem'' implementation for managing tree nodes which are
 * represented by ''ConfigurationNode'' objects.
 *
 * An instance of this class is initialized with the ''ConfigurationNode''
 * object it represents. It displays this node's name. The children (if any)
 * are lazily initialized, i.e. on first access to the node when it gets
 * expanded.
 *
 * There is also support for updates of the underlying configuration node
 * structure. When a change on the associated ''ConfigurationNode'' is
 * detected this object's data is again synchronized with the source node.
 *
 * @param node the underlying ''ConfigurationNode''
 * @param graphicsHandler the object providing the graphics for tree items
 * @param itemMap a map storing the existing tree items for all configuration
 * nodes of the configuration serving as tree data model
 */
private class ConfigTreeItem(val node: ConfigurationNode,
  val graphicsHandler: NodeGraphicsHandler,
  itemMap: mutable.Map[ConfigurationNode, ConfigTreeItem])
  extends TreeItem[ConfigNodeData](ConfigNodeData(node)) {
  /** A flag whether the children have been initialized. */
  private var childrenInitialized = false

  // Add this instance to the global map of tree items
  itemMap += node -> this
  updateGraphic()

  /**
   * @inheritdoc The purpose of this implementation is to determine the leaf
   *             status even if the children have not yet been initialized.
   *             In this case, the underlying ''ConfigurationNode'' is accessed.
   *             If the children have already been resolved, we can safely
   *             delegate to the inherited method.
   */
  override def isLeaf: Boolean = {
    if (!childrenInitialized) node.getChildrenCount <= 0
    else super.isLeaf
  }

  /**
   * @inheritdoc This implementation constructs the list of children on first
   * access. For each child node of the underlying ''ConfigurationNode'' a new
   * child tree item is created.
   */
  override def getChildren: ObservableList[TreeItem[ConfigNodeData]] = {
    if (!childrenInitialized) {
      childrenInitialized = true
      super.getChildren.setAll(buildChildren())
    }
    super.getChildren
  }

  /**
   * Synchronizes this item with its underlying node. This method is called
   * when a change of the underlying node was detected. It iterates over all
   * children of the underlying ''ConfigurationNode'' and performs a
   * synchronization with the child tree items: new items are created for new
   * child configuration nodes, items for no longer existing configuration
   * nodes are removed. Value and graphic of this item are updated, too.
   */
  def resync() {
    setValue(ConfigNodeData(node))
    updateGraphic()
    if (childrenInitialized) {
      resyncChildren()
    }
  }

  /**
   * Removes this tree item and all its children from the central mapping from
   * configuration nodes to tree items. This is necessary when nodes in the
   * hierarchical configuration structure were removed.
   */
  private[tree] def removeFromItemMap() {
    itemMap -= node
    if (childrenInitialized) {
      super.getChildren foreach (_.asInstanceOf[ConfigTreeItem].removeFromItemMap())
    }
  }

  /**
   * Creates a list with tree item objects representing the child nodes of the
   * underlying ''ConfigurationNode''.
   * @return a list with the new child items of this item
   */
  private def buildChildren(): ObservableList[TreeItem[ConfigNodeData]] = {
    val children = createChildrenCollection()
    node.getChildren foreach (appendNewChild(children, _))
    children
  }

  /**
   * Synchronizes the child items of this item with the child nodes of the
   * underlying ''ConfigurationNode''.
   */
  private def resyncChildren() {
    val childSet = new java.util.HashSet(getChildren())
    val newChildren = createChildrenCollection()

    node.getChildren foreach { c =>
      val optItem = itemMap.get(c)
      if (optItem.isDefined) {
        childSet remove optItem.get
        optItem.get.resync()
        newChildren add optItem.get
      } else {
        appendNewChild(newChildren, c)
      }
    }

    super.getChildren setAll newChildren
    childSet foreach (_.asInstanceOf[ConfigTreeItem].removeFromItemMap())
  }

  /**
   * Creates a new observable collection for the child items of this item.
   * @return the new collection
   */
  private def createChildrenCollection() =
    FXCollections.observableArrayList[TreeItem[ConfigNodeData]]()

  /**
   * Creates a new child item and adds it to the given list of child items.
   * @param children the list with child item
   * @param child the configuration node for the new child item
   * @return the new child item
   */
  private def appendNewChild(children: ObservableList[TreeItem[ConfigNodeData]],
    child: ConfigurationNode): ConfigTreeItem = {
    val childItem = new ConfigTreeItem(child, graphicsHandler, itemMap)
    children add childItem
    childItem
  }

  /**
   * Sets the correct graphic based on this tree item's state. The graphic is
   * obtained from the ''GraphicsHandler''.
   */
  private def updateGraphic() {
    setGraphic(graphicsHandler.graphicsFor(node, isExpanded,
      node.getChildrenCount == 0))
  }
}
