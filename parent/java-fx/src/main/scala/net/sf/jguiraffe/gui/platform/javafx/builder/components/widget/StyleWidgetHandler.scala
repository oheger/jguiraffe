/*
 * Copyright 2006-2025 The JGUIraffe Team.
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

import javafx.beans.property.StringProperty

import net.sf.jguiraffe.gui.builder.components.{Color, WidgetHandler}

/**
 * A trait implementing basic functionality for JavaFX widget handlers related
 * to CSS styles.
 *
 * The problem with widget handlers in JavaFX is that there is no single base
 * class for widgets; rather, multiple concrete object types - not connected in
 * an inheritance hierarchy - have to be supported.
 *
 * This trait provides common functionality for all kind of widget handlers
 * based on CSS styles. Concrete subclasses have to provide access to the
 * actual widget's style property.
 */
private[components] trait StyleWidgetHandler extends WidgetHandler {
  /** The styles handler used by this object. */
  lazy val stylesHandler = createStylesHandler()

  /** The property defining the style of the wrapped widget. */
  val style: StringProperty

  /**
   * @inheritdoc The background color is obtained from the wrapped node's
   *             styles.
   */
  override def getBackgroundColor: Color = stylesHandler.getBackgroundColor

  /**
   * @inheritdoc This implementation passes the color to the styles handler.
   *             Then all styles are transformed to text and passed to the wrapped
   *             node.
   */
  override def setBackgroundColor(c: Color): Unit = {
    stylesHandler setBackgroundColor c
    updateWidgetStyles()
  }

  /**
   * @inheritdoc The foreground color is obtained from the wrapped node's
   *             styles.
   */
  override def getForegroundColor: Color = stylesHandler.getForegroundColor()

  /**
   * @inheritdoc This implementation passes the color to the styles handler.
   *             Then all styles are transformed to text and passed to the wrapped
   *             node.
   */
  override def setForegroundColor(c: Color): Unit = {
    stylesHandler setForegroundColor c
    updateWidgetStyles()
  }

  /**
   * @inheritdoc The node's font is dynamically created based on its styles
   *             definitions.
   */
  override def getFont: JavaFxFont = stylesHandler.getFont

  /**
   * @inheritdoc This implementation expects that the argument is of type
   *             ''JavaFxFont''. It applies the data of the font object to the wrapped
   *             node's styles definition,
   */
  override def setFont(font: Object): Unit = {
    stylesHandler.setFont(font.asInstanceOf[JavaFxFont])
    updateWidgetStyles()
  }

  /**
   * Creates the object for managing the wrapped widget's styles. This
   * object is created lazily on first access to properties which read or
   * update style definitions.
   * @return the style handler to be used by this object
   */
  private[components] def createStylesHandler(): JavaFxStylesHandler = {
    val styles = Styles(style.get)
    new JavaFxStylesHandler(styles)
  }

  /**
   * Updates the styles of the wrapped widget. This method is called whenever
   * a property was changed that is implemented through CSS styles.
   */
  private def updateWidgetStyles(): Unit = {
    style set stylesHandler.styles.toExternalForm()
  }
}
