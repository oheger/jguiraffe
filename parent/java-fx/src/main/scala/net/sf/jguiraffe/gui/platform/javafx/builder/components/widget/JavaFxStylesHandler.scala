/*
 * Copyright 2006-2021 The JGUIraffe Team.
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

import net.sf.jguiraffe.gui.builder.components.Color
import net.sf.jguiraffe.gui.platform.javafx.builder.components.widget

/**
 * A class for managing the styles of a Java FX node.
 *
 * This class adds a kind of semantic layer on top of the
 * [[widget.Styles]] class.
 * It assigns concrete objects to different style sheet definitions, e.g. fonts
 * or colors. It can be used to set and to query these objects.
 *
 * The ''Styles'' object to operate on can be passed to the constructor.
 * Alternatively, a new empty object is created.
 *
 * @param styles the ''Styles'' object associated with this instance
 */
private[components] class JavaFxStylesHandler(val styles: Styles = Styles()) {
  /**
   * Returns the background color defined by the associated style sheet. If
   * the style sheet does not define a background color, result is a special
   * ''Color'' instance representing an undefined color.
   * @return the background color
   */
  def getBackgroundColor(): Color =
    styleToColor(JavaFxStylesHandler.StyleBackgroundColor)

  /**
   * Sets the background color in the associated style sheet. The corresponding
   * style definition is set to the value of the color. If the color is
   * undefined, the style definition is removed.
   * @param col the new background color
   */
  def setBackgroundColor(col: Color) {
    setColor(JavaFxStylesHandler.StyleBackgroundColor, col)
  }

  /**
   * Returns the foreground color defined by the associated style sheet. If
   * the style sheet does not define a foreground color, result is a special
   * ''Color'' instance representing an undefined color.
   * @return the foreground color
   */
  def getForegroundColor(): Color =
    styleToColor(JavaFxStylesHandler.StyleForegroundColor)

  /**
   * Sets the foreground color in the associated style sheet. The corresponding
   * style definition is set to the value of the color. If the color is
   * undefined, the style definition is removed.
   * @param col the new foreground color
   */
  def setForegroundColor(col: Color) {
    setColor(JavaFxStylesHandler.StyleForegroundColor, col)
  }

  /**
   * Returns a ''JavaFxFont'' object with all font-related properties of the
   * associated style sheet. It may well be the case that all properties of
   * the returned font object are undefined.
   * @return a ''Font'' object with the corresponding styles definitions
   */
  def getFont(): JavaFxFont =
    JavaFxFont(family = styles(JavaFxStylesHandler.StyleFontFamily),
      size = styles(JavaFxStylesHandler.StyleFontSize),
      style = styles(JavaFxStylesHandler.StyleFontStyle),
      weight = styles(JavaFxStylesHandler.StyleFontWeight),
      fontDef = styles(JavaFxStylesHandler.StyleFontDef))

  /**
   * Sets all font-related properties in the associated style sheet to the
   * values defined by the given ''JavaFxFont'' object. Undefined properties
   * in the font object are removed from the style sheet.
   * @param font the object with font-related information
   */
  def setFont(font: JavaFxFont) {
    styles.updateStyle(JavaFxStylesHandler.StyleFontFamily, font.family)
    styles.updateStyle(JavaFxStylesHandler.StyleFontSize, font.size)
    styles.updateStyle(JavaFxStylesHandler.StyleFontStyle, font.style)
    styles.updateStyle(JavaFxStylesHandler.StyleFontWeight, font.weight)
    styles.updateStyle(JavaFxStylesHandler.StyleFontDef, font.fontDef)
  }

  /**
   * Sets a color. The value of the color is written into the corresponding
   * style definition. If the color is undefined, the key is removed from
   * the stye sheet.
   * @param key the key of the style definition
   * @param col the new color
   */
  private def setColor(key: String, col: Color) {
    if (Color.UNDEFINED == col) {
      styles -= key
    } else {
      styles += (key, JavaFxStylesHandler.colorToStyle(col))
    }
  }

  /**
   * Obtains the style definition for the given key and converts it to a
   * ''Color'' object. If the style is defined, its content is used as logic
   * color definition. Otherwise, an undefined color instance is returned.
   */
  private def styleToColor(key: String): Color =
    styles(key).map(Color.newLogicInstance(_)).getOrElse(Color.UNDEFINED)
}

/**
 * The companion object for ''JavaFxStylesHandler''.
 */
private object JavaFxStylesHandler {
  /** Constant for the font family style. */
  val StyleFontFamily = "-fx-font-family"

  /** Constant for the font size style. */
  val StyleFontSize = "-fx-font-size"

  /** Constant for the font style style. */
  val StyleFontStyle = "-fx-font-style"

  /** Constant for the font weight style. */
  val StyleFontWeight = "-fx-font-weight"

  /** Constant for the style for the whole font definition. */
  val StyleFontDef = "-fx-font"

  /** Constant for the style of the background color. */
  val StyleBackgroundColor = "-fx-background-color"

  /** Constant for the style of the foreground color. */
  val StyleForegroundColor = "-fx-text-fill"

  /**
   * Returns the style definition for the specified color. For logic colors
   * the color's definition is directly returned. RGB colors are converted to
   * a corresponding style definition.
   * @param col the color to be converted
   * @return the style definition for this color
   */
  private def colorToStyle(col: Color): String =
    if (col.isLogicColor) col.getColorDefinition
    else formatRGBColor(col)

  /**
   * Converts the specified RGB color to a string format which can be used
   * as style definition. The resulting string has the format `#RRGGBB`
   * using hexadecimal values.
   * @param col the color to be formatted
   * @return the resulting format string
   */
  private def formatRGBColor(col: Color): String =
    f"#${col.getRed}%02x${col.getGreen}%02x${col.getBlue}%02x"
}
