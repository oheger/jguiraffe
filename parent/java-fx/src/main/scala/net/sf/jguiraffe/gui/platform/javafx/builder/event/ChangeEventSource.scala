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
package net.sf.jguiraffe.gui.platform.javafx.builder.event

import javafx.beans.value.ObservableValue

/**
 * A trait for objects supporting the registration of change listeners.
 *
 * The JGUIraffe Java FX implementation uses change notifications of specific
 * key properties to simulate the missing change events. In order to implement
 * a generic mechanism for registering change listeners, an object has to
 * provide the key property. This is exactly the contract enforced by this
 * trait.
 */
trait ChangeEventSource {
  /**
   * Returns the observable value to which a change listener can be added or
   * removed.
   */
  def observableValue: ObservableValue[_ <: AnyRef]
}
