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

import javafx.scene.control.Tooltip

/**
 * Definition of a trait for the creation of new tool tip objects.
 *
 * In Java FX tool tips are in the responsibility of the ''Tooltip'' class.
 * This class offers some enhanced capabilities, so an application may
 * wish to use or customize them. This can be achieved by creating a custom
 * implementation of this trait and passing it to the
 * [[net.sf.jguiraffe.gui.platform.javafx.builder.components.JavaFxComponentManager]]
 * when it is constructed.
 *
 * In the default configuration the component manager uses a default
 * ''ToolTipFactory'' implementation which produces very basic tool tips.
 */
trait ToolTipFactory {
  /**
   * Creates a ''Tooltip'' instance for the specified text. This method is
   * called in the Java FX application thread. A concrete implementation can
   * create a ''Tooltip'' instance based on the given text and customize it as
   * desired.
   * @param text the text of the tool tip
   * @return the newly created ''Tooltip'' instance
   */
  def createToolTip(text: String): Tooltip
}
