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
package net.sf.jguiraffe.gui.platform.javafx.builder.action

import javafx.scene.Node
import javafx.scene.input.MouseEvent

import net.sf.jguiraffe.gui.builder.action.{AbstractPopupMenuBuilder, ActionBuilder, ActionManager}

/**
 * A specialized ''PopupMenuBuilder'' implementation for JavaFX context menus.
 *
 * This class creates JavaFX context menus. The main functionality is inherited
 * from the base class; only the actual ''create()'' method has to be
 * implemented.
 *
 * @param actionManager the associated ''ActionManager''
 * @param actionBuilder the current ''ActionBuilder''
 * @param node the node associated with the menu
 * @param event the triggering mouse event
 */
private class JavaFxPopupMenuBuilder(actionManager: ActionManager, actionBuilder: ActionBuilder,
                                     val node: Node, val event: MouseEvent) extends
AbstractPopupMenuBuilder(actionManager, actionBuilder) {
  this: ContextMenuCreator =>

  /** The menu to be constructed by this builder. */
  private lazy val contextMenu = createContextMenu()

  override protected[action] def getMenuUnderConstruction: AnyRef = contextMenu

  /**
   * @inheritdoc This implementation displays the context menu that has been
   *             constructed using this builder.
   */
  override def create(): AnyRef = {
    contextMenu.show(node, event.getScreenX, event.getScreenY)
    contextMenu
  }
}
