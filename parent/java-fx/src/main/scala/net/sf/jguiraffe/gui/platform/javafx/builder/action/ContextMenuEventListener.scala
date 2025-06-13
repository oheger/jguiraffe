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

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.input.{MouseButton, MouseEvent}

import net.sf.jguiraffe.gui.builder.action.{ActionBuilder, ActionManager, PopupMenuHandler}
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData

/**
 * An event listener implementation used by the Java FX action manager to
 * associate context menus with controls.
 *
 * This listener is registered for mouse click events on scene nodes that have
 * an associated context menu. When a click with the correct button is received
 * the associated [[net.sf.jguiraffe.gui.builder.action.PopupMenuHandler]] is
 * invoked so that a context menu can be defined and displayed.
 *
 * @param actionManager the ''ActionManager'' to be delegated to
 * @param actionBuilder the current ''ActionBuilder'' data object
 * @param handler the ''PopupMenuHandler'' to be invoked
 * @param compData the ''ComponentBuilderData'' object
 */
private class ContextMenuEventListener(val actionManager: ActionManager, val actionBuilder:
ActionBuilder, val handler: PopupMenuHandler, val compData: ComponentBuilderData,
                                       val component: Node)
  extends EventHandler[MouseEvent] {
  /**
   * @inheritdoc This implementation checks whether the event
   */
  override def handle(event: MouseEvent): Unit = {
    if (event.getButton == MouseButton.SECONDARY) {
      val builder = new JavaFxPopupMenuBuilder(actionManager, actionBuilder, component, event)
        with ContextMenuCreator
      handler.constructPopup(builder, compData)
    }
  }
}
