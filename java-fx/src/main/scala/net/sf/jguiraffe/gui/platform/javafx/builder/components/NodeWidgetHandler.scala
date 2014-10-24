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
package net.sf.jguiraffe.gui.platform.javafx.builder.components

import javafx.scene.Node

import org.apache.commons.logging.LogFactory

import scala.beans.BeanProperty

/**
 * A ''WidgetHandler'' implementation for plain JavaFX nodes.
 *
 * This class is used for widgets which are not derived from the
 * ''Control'' class. Its functionality is limited; for instance, there is no
 * support for tooltips. Other functionality is directly mapped to methods of
 * the wrapped ''Node''.
 */
private class NodeWidgetHandler(@BeanProperty val widget: Node) extends StyleWidgetHandler {
  override val style = widget.styleProperty

  override def isVisible: Boolean = widget.isVisible

  override def setVisible(f: Boolean): Unit = widget setVisible f

  /**
   * @inheritdoc This implementation always returns '''null'''. Plain nodes do
   *             not support tool tips.
   */
  override def getToolTip: String = null

  /**
   * @inheritdoc This is a dummy implementation. The passed in tool tip text is
   *             ignored.
   */
  override def setToolTip(tip: String): Unit = {
    LogFactory.getLog(classOf[NodeWidgetHandler])
      .warn("Widget does not support tool tips: " + widget)
  }
}
