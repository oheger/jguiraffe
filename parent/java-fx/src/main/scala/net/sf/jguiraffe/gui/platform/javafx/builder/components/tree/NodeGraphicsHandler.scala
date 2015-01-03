/*
 * Copyright 2006-2015 The JGUIraffe Team.
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

import org.apache.commons.configuration.tree.ConfigurationNode

import javafx.scene.Node

/**
 * A trait which encapsulates querying icons for a specific tree node.
 *
 * This trait is similar in purpose to the ''TreeIconHandler'' interface of
 * JGUIraffe core. However, it uses an API tailored towards JavaFX.
 * Components responsible for the implementation of JavaFX tree views are
 * associated with an implementation of this trait in order to obtain the
 * graphics for the single nodes based on their current state.
 */
trait NodeGraphicsHandler {
  /**
   * Returns the graphics node to be used for the tree node representing the
   * given ''ConfigurationNode''. The state of the tree node is passed in so
   * that an implementation can return different graphics based on it.
   * @param node the ''ConfigurationNode'' representing the tree node
   * @param expanded flag whether the tree node is expanded
   * @param leaf flag whether the tree node is a leaf or not
   */
  def graphicsFor(node: ConfigurationNode, expanded: Boolean, leaf: Boolean): Node
}
