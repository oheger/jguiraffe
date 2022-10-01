/*
 * Copyright 2006-2022 The JGUIraffe Team.
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

import javafx.scene.control.Control
import net.sf.jguiraffe.gui.forms.ComponentHandler
import net.sf.jguiraffe.gui.platform.javafx.builder.event.NodeEventSource

/**
 * A base class for Java FX-specific ''ComponentHandler'' implementations.
 *
 * This class provides basic functionality for managing a Java FX component
 * based on the ''Control'' class. It maintains a ''Control'' reference and
 * implements methods that directly operate on this reference.
 *
 * Concrete subclasses have to deal with data transfer to and from the
 * managed control instance.
 *
 * @tparam T the data type supported by this handler
 * @param component the ''Control'' managed by this handler
 */
private abstract class JavaFxComponentHandler[T](@BeanProperty val component: Control)
  extends ComponentHandler[T] with NodeEventSource {
  /** Per default, the outer component is the managed control. */
  val getOuterComponent = component

  /** The source node for events is always the managed control. */
  val sourceNode = component

  /**
   * @inheritdoc This implementation queries the managed control's
   * ''disabled'' property.
   */
  def isEnabled: Boolean = !component.isDisabled

  /**
   * @inheritdoc This implementation sets the ''disable'' property on the
   * managed control.
   */
  def setEnabled(f: Boolean) {
    component setDisable !f
  }
}
