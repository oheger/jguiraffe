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
package net.sf.jguiraffe.gui.platform.javafx.builder.window

import javafx.scene.control.MenuBar
import javafx.scene.layout.{BorderPane, Pane}

import net.sf.jguiraffe.gui.layout.UnitSizeHandler
import net.sf.jguiraffe.gui.platform.javafx.layout.ContainerWrapper

/**
 * A specialized ''ContainerWrapper'' implementation to be used for the root
 * pane of windows.
 *
 * This class is used to define the content of a window. Its purpose is to
 * handle elements associated with the window (e.g. the menu bar) which are not
 * part of the normal content. These elements are stored separately. When the
 * root pane is constructed and additional elements are defined, the actual
 * content is embedded into another pane containing these special elements.
 *
 * The main functionality is provided by the base class. This class overrides
 * the creation of the represented pane.
 *
 * @param sizeHandler an optional ''UnitSizeHandler''
 */
private class WindowRootContainerWrapper(sizeHandler: Option[UnitSizeHandler] = None)
  extends ContainerWrapper(sizeHandler) {
  /** The menu bar of the associated window. */
  var menuBar: Option[MenuBar] = None

  /**
   * @inheritdoc
   * This implementation determines the pane to be created based on the
   * presence of a menu bar. If a menu bar is provided, a border pane is
   * created with the menu bar in the top and the actual container in the
   * center. Otherwise, the inherited method is called.
   */
  override def createContainer(): Pane = {
    (menuBar map createBorderPane) getOrElse super.createContainer()
  }

  /**
   * Creates the border pane to be returned if a menu bar is defined.
   * @param mainMenu the menu bar
   * @return the border pane
   */
  private def createBorderPane(mainMenu: MenuBar): BorderPane = {
    val pane = new BorderPane
    pane setTop mainMenu
    pane setCenter super.createContainer()
    pane
  }
}
