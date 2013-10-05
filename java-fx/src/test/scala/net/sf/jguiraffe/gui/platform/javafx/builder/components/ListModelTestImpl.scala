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

import net.sf.jguiraffe.gui.builder.components.model.ListModel

/**
 * The companion object of ''ListModelTestImpl''.
 */
object ListModelTestImpl {
  /** A prefix used for display objects. */
  val DisplayPrefix = "Display_"

  /** A prefix used for value objects. */
  val ValuePrefix = "Value_"

  /** The default size of the model. */
  val DefaultSize = 16
}

/**
 * A test implementation of ''ListModel'' which returns predictable data.
 * Instances can be used by multiple tests related to list controls.
 * @param size the size of the model
 */
class ListModelTestImpl(override val size: Int = ListModelTestImpl.DefaultSize)
  extends ListModel {
  import ListModelTestImpl._

  def getType = classOf[String]

  def getDisplayObject(index: Int) = DisplayPrefix + index

  def getValueObject(index: Int) = ValuePrefix + index
}
