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

import scala.beans.BeanProperty

import javafx.scene.Node
import javafx.scene.control.Label
import net.sf.jguiraffe.gui.builder.components.model.StaticTextData
import net.sf.jguiraffe.gui.builder.components.model.StaticTextHandler
import net.sf.jguiraffe.gui.builder.components.model.TextIconAlignment
import net.sf.jguiraffe.gui.builder.components.tags.StaticTextDataImpl

/**
 * The JavaFX-specific implementation of a ''ComponentHandler'' for a
 * ''StaticText'' component.
 *
 * This implementation wraps a JavaFX ''Label'' component. It allows setting
 * the label's text and image via a ''StaticTextData'' object; this is also
 * the data type of this ''ComponentHandler''. In addition, the
 * ''StaticTextHandler'' interface is implemented. Through the methods defined
 * by this interface different different properties of the wrapped label can
 * be manipulated in isolation.
 *
 * @param label the wrapped ''Label'' component
 */
private class JavaFxStaticTextHandler(label: Label)
  extends JavaFxComponentHandler[StaticTextData](label) with StaticTextHandler {
  @BeanProperty val `type` = classOf[StaticTextData]

  def getData: StaticTextData = {
    val data = new StaticTextDataImpl
    data setText getText
    data setIcon getIcon
    data setAlignment getAlignment
    data
  }

  def setData(data: StaticTextData) {
    val actData = if (data != null) data else new StaticTextDataImpl
    setText(actData.getText)
    setIcon(actData.getIcon)
    setAlignment(actData.getAlignment)
  }

  def getText: String = label.getText

  def setText(txt: String) {
    label setText txt
  }

  def getIcon: Node = label.getGraphic

  /**
   * @inheritdoc This implementation checks whether the passed in icon object
   * is a JavaFX ''Node''. If so, it is passed to the wrapped label.
   * Otherwise, the label's icon is set to '''null'''.
   */
  def setIcon(icon: Object) {
    val fxIcon = icon match {
      case nd: Node => nd
      case _ => null
    }
    label setGraphic fxIcon
  }

  def getAlignment: TextIconAlignment =
    convertContentDisplay(label.getContentDisplay)

  def setAlignment(al: TextIconAlignment) {
    label setContentDisplay (convertAlignment(al))
  }
}
