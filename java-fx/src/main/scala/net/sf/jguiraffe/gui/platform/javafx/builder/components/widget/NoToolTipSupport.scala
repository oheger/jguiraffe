/*
 * Copyright 2006-2014 The JGUIraffe Team.
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

import net.sf.jguiraffe.gui.builder.components.WidgetHandler
import org.apache.commons.logging.LogFactory

/**
 * A simple trait to be mixed into widget handler implementations which do not
 * support tool tips.
 *
 * This trait has dummy implementations for the methods dealing with tool tips.
 */
private trait NoToolTipSupport extends WidgetHandler {
  /**
   * @inheritdoc This implementation always returns '''null'''.
   */
  override def getToolTip: String = null

  /**
   * @inheritdoc This is a dummy implementation. The passed in tool tip text is
   *             ignored.
   */
  override def setToolTip(tip: String): Unit = {
    LogFactory.getLog(classOf[NoToolTipSupport])
      .warn("Widget does not support tool tips: " + this)
  }
}
