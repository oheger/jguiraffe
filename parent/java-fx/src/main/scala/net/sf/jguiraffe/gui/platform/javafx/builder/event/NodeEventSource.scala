/*
 * Copyright 2006-2018 The JGUIraffe Team.
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

import javafx.scene.Node

/**
 * A trait for objects supporting the registration of event listeners
 * for generic node events.
 *
 * Java FX ''Node'' objects define a bunch of properties to which event
 * listeners can be added. This trait enables all of these properties by
 * just defining a method for returning a ''Node'' object. Event listener
 * registration code can then access the appropriate properties.
 */
trait NodeEventSource {
  /**
   * Returns the ''Node'' which is the source of events. At this object
   * listeners have to be registered.
   * @return the ''Node'' acting as event source
   */
  def sourceNode: Node
}
