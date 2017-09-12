/*
 * Copyright 2006-2017 The JGUIraffe Team.
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

import net.sf.jguiraffe.gui.builder.components.{Color, WidgetHandler}

/**
 * A simple adapter class for a ''WidgetHandler'' providing dummy implementations
 * for all methods.
 *
 * This class is used to simplify tests for traits derived from ''WidgetHandler''.
 * Test objects can be created by mixing the traits under test into this base
 * class.
 */
class WidgetHandlerAdapter extends WidgetHandler {
  override def getWidget: AnyRef = unexpected()

  override def isVisible: Boolean = unexpected()

  override def getBackgroundColor: Color = unexpected()

  override def getForegroundColor: Color = unexpected()

  override def getFont: AnyRef = unexpected()

  override def setVisible(f: Boolean): Unit = unexpected()

  override def getToolTip: String = unexpected()

  override def setFont(font: Object): Unit = unexpected()

  override def setToolTip(tip: String): Unit = unexpected()

  override def setBackgroundColor(c: Color): Unit = unexpected()

  override def setForegroundColor(c: Color): Unit = unexpected()

  private def unexpected(): Nothing = throw new AssertionError("Unexpected method call!")
}
