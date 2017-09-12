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

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import net.sf.jguiraffe.gui.builder.event.FormChangeEvent
import net.sf.jguiraffe.gui.builder.event.FormEventManager
import net.sf.jguiraffe.gui.builder.event.FormListenerType
import net.sf.jguiraffe.gui.forms.ComponentHandler

/**
 * An adapter implementation mapping Java FX change notifications to JGUIraffe
 * [[net.sf.jguiraffe.gui.builder.event.FormChangeEvent]] objects.
 *
 * Change events in JGUIraffe are pretty basic. They just transport the
 * information that the state of a monitored component has changed. An
 * additional step is necessary to inspect the component's properties and to
 * find out what actually changed.
 *
 * In Java FX component state is typically modeled using properties which
 * allow the registration of change listeners. Therefore, this adapter
 * implements a generic change listener. Thus it can be added to an
 * arbitrary property. Whenever the property changes its value, a corresponding
 * JGUIraffe change event is generated and passed to the configured sender.
 * Per default, a sender is used which targets the current ''FormEventManager''
 * because this is the standard component responsible for the registration of
 * change listeners.
 *
 * @param sender the object for sending events
 * @param componentHandler the associated component handler
 * @param componentName the name of the associated component
 */
private class ChangeEventAdapter(val sender: EventSender[FormChangeEvent],
  val componentHandler: ComponentHandler[_], val componentName: String)
  extends ChangeListener[AnyRef] {
  /**
   * Creates a new instance of ''ChangeEventAdapter'' which sends events to the
   * specified ''FormEventManager'' object.
   * @param evMan the ''FormEventManager''
   * @param compHandler the associated component handler
   * @param compName the name of the associated component
   */
  def this(evMan: FormEventManager, compHandler: ComponentHandler[_],
    compName: String) = this(new EventManagerSender[FormChangeEvent](evMan,
    FormListenerType.CHANGE), compHandler, compName)

  /**
   * @inheritdoc This implementation generates a change event and passes it to
   * the sender.
   */
  override def changed(obsValue: ObservableValue[_ <: AnyRef], oldValue: AnyRef,
    newValue: AnyRef) {
    sender fire (new FormChangeEvent(obsValue, componentHandler, componentName))
  }
}
