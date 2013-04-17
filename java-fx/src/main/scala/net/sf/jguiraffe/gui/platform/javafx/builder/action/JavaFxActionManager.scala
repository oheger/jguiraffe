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
package net.sf.jguiraffe.gui.platform.javafx.builder.action

import net.sf.jguiraffe.gui.builder.action.ActionBuilder
import net.sf.jguiraffe.gui.builder.action.ActionData
import net.sf.jguiraffe.gui.builder.action.ActionManager
import net.sf.jguiraffe.gui.builder.action.FormAction
import net.sf.jguiraffe.gui.builder.action.PopupMenuHandler
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData
import net.sf.jguiraffe.gui.builder.components.tags.TextIconData
import net.sf.jguiraffe.gui.forms.ComponentHandler

class JavaFxActionManager extends ActionManager {
  def createAction(actionBuilder: ActionBuilder, actionData: ActionData): FormAction = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def createMenuItem(actionBuilder: ActionBuilder, action: FormAction,
    checked: Boolean, parent: Object): FormAction = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def createMenuItem(actionBuilder: ActionBuilder,
    actionData: ActionData, checked: Boolean, parent: Object): ComponentHandler[_] = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def createMenuBar(actionBuilder: ActionBuilder): Object = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def createMenu(actionBuilder: ActionBuilder, menu: Object,
    data: TextIconData, parent: Object): Object = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def createToolbar(actionBuilder: ActionBuilder): Object = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def createToolbarButton(actionBuilder: ActionBuilder, action: FormAction,
    checked: Boolean, parent: Object): Object = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def createToolbarButton(actionBuilder: ActionBuilder,
    data: ActionData, checked: Boolean, parent: Object): ComponentHandler[_] = {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def addMenuSeparator(actionBuilder: ActionBuilder, menu: Object) {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def addToolBarSeparator(actionBuilder: ActionBuilder, toolBar: Object) {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  def registerPopupMenuHandler(component: Object, handler: PopupMenuHandler,
    compData: ComponentBuilderData) {
    //TODO implementation
    throw new UnsupportedOperationException("Not yet implemented!");
  }
}
