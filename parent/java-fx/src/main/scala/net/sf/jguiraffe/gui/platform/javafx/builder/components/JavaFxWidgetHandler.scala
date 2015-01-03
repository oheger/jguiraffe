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
import javafx.scene.control.Control

import net.sf.jguiraffe.gui.builder.components.{Color, WidgetHandler}
import net.sf.jguiraffe.gui.platform.javafx.builder.components.widget.{JavaFxFont, JavaFxStylesHandler, Styles}
import net.sf.jguiraffe.gui.platform.javafx.common.ToolTipFactory
import org.apache.commons.logging.LogFactory

import scala.beans.BeanProperty

/**
 * Java FX-specific implementation of the ''WidgetHandler'' interface.
 *
 * An instance of this class is constructed with a Java FX ''Node'' instance.
 * The interface methods are implemented in a way that they delegate to this
 * ''Node''.
 *
 * The creation of tool tips is delegated to a
 * [[ToolTipFactory]]
 * object which is also passed to the constructor. This allows applications to
 * customize tool tips.
 *
 * @param widget the underlying Java FX widget
 * @param toolTipFactory the factory object for tool tips
 */
private class JavaFxWidgetHandler(@BeanProperty val widget: Node,
    val toolTipFactory: ToolTipFactory) extends WidgetHandler {
  /** The styles handler used by this object. */
  lazy val stylesHandler = createStylesHandler()

  def isVisible: Boolean = widget.isVisible

  def setVisible(f: Boolean) {
    widget.setVisible(f)
  }

  /**
   * @inheritdoc The background color is obtained from the wrapped node's
   * styles.
   */
  def getBackgroundColor: Color = stylesHandler.getBackgroundColor

  /**
   * @inheritdoc This implementation passes the color to the styles handler.
   * Then all styles are transformed to text and passed to the wrapped
   * node.
   */
  def setBackgroundColor(c: Color) {
    stylesHandler setBackgroundColor c
    updateWidgetStyles()
  }

  /**
   * @inheritdoc The foreground color is obtained from the wrapped node's
   * styles.
   */
  def getForegroundColor: Color = stylesHandler.getForegroundColor

  /**
   * @inheritdoc This implementation passes the color to the styles handler.
   * Then all styles are transformed to text and passed to the wrapped
   * node.
   */
  def setForegroundColor(c: Color) {
    stylesHandler setForegroundColor c
    updateWidgetStyles()
  }

  /**
   * @inheritdoc Tool tips are supported only for widgets derived from the
   * ''Control'' class. For other widgets, '''null''' is returned.
   */
  def getToolTip: String = {
    widget match {
      case ctrl: Control =>
        extractToolTipText(ctrl)
      case _ => null
    }
  }

  /**
   * @inheritdoc This implementation checks whether the wrapped widget
   * supports tool tips. If not, this call is ignored. Otherwise, the tool
   * tip is set either by creating a new ''Tooltip'' object or by setting
   * it to '''null'''.
   */
  def setToolTip(tip: String) {
    widget match {
      case ctrl: Control =>
        setToolTipText(ctrl, tip)
      case _ =>
        LogFactory.getLog(classOf[JavaFxWidgetHandler])
          .warn("Widget does not support tool tips: " + widget)
    }
  }

  /**
   * @inheritdoc The node's font is dynamically created based on its styles
   * definitions.
   */
  def getFont: JavaFxFont = stylesHandler.getFont

  /**
   * @inheritdoc This implementation expects that the argument is of type
   * ''JavaFxFont''. It applies the data of the font object to the wrapped
   * node's styles definition,
   */
  def setFont(font: Object) {
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
    val styles = Styles(widget.getStyle)
    new JavaFxStylesHandler(styles)
  }

  /**
   * Updates the styles of the wrapped widget. This method is called whenever
   * a property was changed that is implemented through CSS styles.
   */
  private def updateWidgetStyles() {
    widget.setStyle(stylesHandler.styles.toExternalForm())
  }

  /**
   * Obtains the text of the given control's tool tip. Result is '''null'''
   * if no tool tip is set.
   */
  private def extractToolTipText(ctrl: Control): String = {
    val tip = ctrl.getTooltip()
    if (tip != null) tip.getText
    else null
  }

  /**
   * Sets the text of the given control's tool tip. Safely handles '''null'''
   * input.
   * @param ctrl the control
   * @param txt the text of the tool tip (which may be '''null''')
   */
  private def setToolTipText(ctrl: Control, txt: String) {
    ctrl.setTooltip(if (txt == null) null
    else toolTipFactory.createToolTip(txt))
  }
}
