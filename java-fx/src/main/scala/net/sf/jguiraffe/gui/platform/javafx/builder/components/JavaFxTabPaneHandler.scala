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
package net.sf.jguiraffe.gui.platform.javafx.builder.components

import scala.beans.BeanProperty

import javafx.scene.control.TabPane
import net.sf.jguiraffe.gui.platform.javafx.builder.event.ChangeEventSource

/**
 * A specialized ''ComponentHandler'' implementation for a JavaFX ''TabPane''
 * control.
 *
 * This handler implementation is used for modeling tab panes in JavaFX.
 * The data of the handler is the index of the currently selected tab. Change
 * listeners are supported; they are triggered when the selected index is
 * changed.
 */
private class JavaFxTabPaneHandler(tabPane: TabPane)
  extends JavaFxComponentHandler[java.lang.Integer](tabPane)
  with ChangeEventSource {
  @BeanProperty val `type` = classOf[Integer]

  def getData: java.lang.Integer =
    tabPane.getSelectionModel.selectedIndexProperty.get()

  def setData(idx: java.lang.Integer) {
    if (idx != null) {
      tabPane.getSelectionModel.select(idx)
    }
  }

  /**
   * @inheritdoc This implementation returns the property of the selection
   * model responsible for storing the selected index.
   */
  def observableValue = tabPane.getSelectionModel.selectedIndexProperty
}
