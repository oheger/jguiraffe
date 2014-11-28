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
package net.sf.jguiraffe.gui.platform.javafx.common

import javafx.scene.control.Tooltip

/**
 * A default implementation of ''ToolTipFactory''.
 *
 * This class creates very basic ''Tooltip'' instances that are just
 * initialized with the passed in text. It is used per default by
 * the standard manager implementations which create components
 * if no custom factory is provided.
 */
class DefaultToolTipFactory extends ToolTipFactory {
  /**
   * @inheritdoc This implementation creates a new instance of ''Tooltip''
   * with the specified text and returns it.
   */
  def createToolTip(text: String): Tooltip = new Tooltip(text)
}
