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
package net.sf.jguiraffe.gui.platform.javafx.builder.components.widget

import javafx.beans.property.ObjectProperty
import javafx.scene.control.Tooltip

import net.sf.jguiraffe.gui.builder.components.WidgetHandler
import net.sf.jguiraffe.gui.platform.javafx.common.ToolTipFactory

/**
 * A trait for the implementation of ''WidgetHandler''s which provides
 * functionality for getting and setting tool tips.
 *
 * This trait can be mixed in for handler implementations whose wrapped widget
 * supports tool tips and thus has a corresponding property. A concrete
 * implementation has to provide this property so that this trait can fulfill
 * its tasks. In addition, a
 * [[net.sf.jguiraffe.gui.platform.javafx.common.ToolTipFactory]] has to be
 * provided; this is used for creating ''Tooltip'' objects for the plain texts
 * passed to the handler.
 */
private[components] trait ToolTipWidgetHandler extends WidgetHandler {
  /**
   * The property of the wrapped widget for storing the tool tip objects.
   */
  val toolTipProperty: ObjectProperty[Tooltip]

  /**
   * The factory for creating new tool tips.
   */
  val toolTipFactory: ToolTipFactory

  /**
   * @inheritdoc This implementation obtains the ''Tooltip'' from the
   *             configured property. If it is defined, its text is extracted.
   */
  override def getToolTip: String = {
    val tip = toolTipProperty.get
    if (tip == null) null
    else tip.getText
  }

  /**
   * @inheritdoc This implementation uses the configured ''ToolTipFactory''
   *             to create a tool tip object based on the passed in text. A
   *             '''null''' text leads to an undefined tool tip.
   */
  override def setToolTip(tip: String): Unit = {
    toolTipProperty set createToolTip(tip)
  }

  /**
   * Creates a ''Tooltip'' object for the specified text using the tool tip
   * factory.
   * @param text the tool tip text (may be '''null''')
   * @return the tool tip object
   */
  private def createToolTip(text: String): Tooltip =
    if (text == null) null
    else toolTipFactory createToolTip text
}
