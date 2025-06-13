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

import javafx.scene.control.ContextMenu

/**
 * A trait for creating a JavaFX context menu.
 *
 * This trait is used internally for obtaining a new context menu instance.
 * Per default, simply a ''ContextMenu'' object is created and returned.
 * Test classes can inject a mock implementation.
 */
private trait ContextMenuCreator {
  /**
   * Creates a new ''ContextMenu'' instance. This implementation just creates
   * a new instance without any special initialization.
   * @return the newly created context menu instance
   */
  def createContextMenu(): ContextMenu = new ContextMenu
}
