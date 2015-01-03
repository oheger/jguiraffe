/*
 * Copyright 2006-2015 The JGUIraffe Team.
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

import javafx.event.ActionEvent
import javafx.event.EventHandler
import net.sf.jguiraffe.gui.platform.javafx.builder.event.ActionEventSource

/**
 * A default implementation of the ''ActionEventSource'' trait for JavaFX
 * component handlers.
 *
 * This trait implements the methods for adding and removing event handlers
 * by delegating to the ''Node'' object wrapped by the component handler.
 * This should be a suitable default implementation for most controls.
 */
private trait ControlActionEventSource[T] extends JavaFxComponentHandler[T]
  with ActionEventSource {
  /**
   * @inheritdoc This implementation delegates to the component managed by the
   * associated component handler.
   */
  def addActionListener(handler: EventHandler[ActionEvent]) {
    component.addEventHandler(ActionEvent.ACTION, handler)
  }

  /**
   * @inheritdoc This implementation delegates to the component managed by the
   * associated component handler.
   */
  def removeActionListener(handler: EventHandler[ActionEvent]) {
    component.removeEventHandler(ActionEvent.ACTION, handler)
  }
}
