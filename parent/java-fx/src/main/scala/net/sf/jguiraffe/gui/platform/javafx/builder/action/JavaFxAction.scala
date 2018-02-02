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
package net.sf.jguiraffe.gui.platform.javafx.builder.action

import javafx.beans.property.{BooleanProperty, SimpleBooleanProperty}
import javafx.event.{ActionEvent, EventHandler}

import net.sf.jguiraffe.gui.builder.action.{ActionData, ActionHelper, FormAction}
import net.sf.jguiraffe.gui.builder.event.{FormActionEvent, BuilderEvent}

import scala.beans.BeanProperty

/**
 * A JavaFx-specific implementation of the ''FormAction'' interface.
 *
 * This is a simple (not thread-safe) implementation of an action to be used
 * together with JavaFx menu and toolbar components. For some of the properties
 * that trigger state changes in associated components JavaFx properties are
 * used. This makes it possible to handle updates automatically via property
 * bindings.
 *
 * In addition, this class is also a handler for action events. Thus, it can be
 * directly connected to JavaFX controls which can trigger events of this type.
 *
 * @param actionData an object defining the properties of this action
 */
private class JavaFxAction(val actionData: ActionData) extends FormAction with
EventHandler[ActionEvent] {
  /**
   * The property for storing the enabled state of this action.
   */
  val enabled: BooleanProperty = new SimpleBooleanProperty(true)

  /**
   * The property for storing the checked state of this action.
   */
  val checked: BooleanProperty = new SimpleBooleanProperty

  /** Stores the task of this action. */
  @BeanProperty var task: AnyRef = _

  override def isEnabled: Boolean = enabled.get

  override def setEnabled(f: Boolean): Unit = enabled set f

  override def isChecked: Boolean = checked.get

  override def setChecked(f: Boolean): Unit = checked set f

  override def execute(event: BuilderEvent): Unit =
    ActionHelper.invokeActionTask(task, this, event)

  override def getName: String = actionData.getName

  /**
   * @inheritdoc
   * This implementation executes this action with a ''FormActionEvent''
   * created based on the current ''ActionEvent''.
   */
  override def handle(event: ActionEvent): Unit = {
    execute(new FormActionEvent(event, null, null, getName))
  }
}
