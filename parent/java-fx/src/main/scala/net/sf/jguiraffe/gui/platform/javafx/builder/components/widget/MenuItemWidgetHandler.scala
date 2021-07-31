/*
 * Copyright 2006-2021 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.platform.javafx.builder.components.widget

import javafx.scene.control.MenuItem

/**
 * A specialized ''WidgetHandler'' implementation for JavaFX menu items.
 *
 * In JavaFX menu items are somewhat special because they do not extend the
 * ''Node'' class. Therefore, other base classes for JavaFX-specific widget
 * handlers cannot be extended in order to deal with menu items.
 *
 * This is a specialized implementation tailored towards menu items, but it
 * uses functionality provided by special traits.
 *
 * @param item the menu item wrapped by this handler
 */
private[components] class MenuItemWidgetHandler(item: MenuItem) extends StyleWidgetHandler with
NoToolTipSupport {
  override val getWidget = item

  override def isVisible: Boolean = item.isVisible

  override def setVisible(f: Boolean): Unit = item setVisible f

  override val style = item.styleProperty
}
