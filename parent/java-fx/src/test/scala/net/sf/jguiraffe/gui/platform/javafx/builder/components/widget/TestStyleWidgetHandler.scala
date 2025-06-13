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

import javafx.beans.property.{SimpleStringProperty, StringProperty}

import net.sf.jguiraffe.gui.builder.components.Color
import net.sf.jguiraffe.gui.platform.javafx.builder.components.WidgetHandlerAdapter
import org.hamcrest.CoreMatchers.containsString
import org.junit.Assert._
import org.junit.{Before, Test}

/**
 * Test class for ''StyleWidgetHandler''.
 */
class TestStyleWidgetHandler {
  /** The style property used by the test handler. */
  private var styleProperty: StringProperty = _

  /** The handler to be tested. */
  private var handler: StyleWidgetHandler = _

  @Before def setUp(): Unit = {
    styleProperty = new SimpleStringProperty("-fx-unknown: 1;")
    handler = new WidgetHandlerAdapter with StyleWidgetHandler {
      override val style: StringProperty = styleProperty
    }
  }

  /**
   * Generates a style definition.
   * @param key the key
   * @param value the value
   * @return the complete style definition
   */
  private def generateStyle(key: String, value: String): String = s"$key: $value;"

  /**
   * Checks whether the style property contains the expected style definition.
   * @param key the key of the expected definition
   * @param value the value of the expected definition
   */
  private def checkStyle(key: String, value: String): Unit = {
    assertThat(styleProperty.get, containsString(generateStyle(key, value)))
  }

  /**
   * Writes an entry into the style property for the specified key.
   * @param key the key
   * @param value the value
   */
  private def initStyleProperty(key: String, value: String): Unit = {
    styleProperty setValue generateStyle(key, value)
  }

  /**
   * Tests whether the foreground color is correctly extracted.
   */
  @Test def testGetForegroundColor(): Unit = {
    val ColorValue = "fgcol"
    initStyleProperty(JavaFxStylesHandler.StyleForegroundColor, ColorValue)
    val color = handler.getForegroundColor
    assertEquals("Wrong color", ColorValue, color.getColorDefinition)
  }

  /**
   * Tests whether the foreground color can be set.
   */
  @Test def testSetForegroundColor(): Unit = {
    val ColorValue = "fgcol"
    handler setForegroundColor Color.newLogicInstance(ColorValue)
    checkStyle(JavaFxStylesHandler.StyleForegroundColor, ColorValue)
  }

  /**
   * Tests whether the background color is correctly extracted.
   */
  @Test def testGetBackgroundColor(): Unit = {
    val ColorValue = "bgcol"
    initStyleProperty(JavaFxStylesHandler.StyleBackgroundColor, ColorValue)
    val color = handler.getBackgroundColor
    assertEquals("Wrong color", ColorValue, color.getColorDefinition)
  }

  /**
   * Tests whether the background color can be set.
   */
  @Test def testSetBackgroundColor(): Unit = {
    val ColorValue = "backGround"
    handler setBackgroundColor Color.newLogicInstance(ColorValue)
    checkStyle(JavaFxStylesHandler.StyleBackgroundColor, ColorValue)
  }

  /**
   * Tests whether the font can be queried.
   */
  @Test def testGetFont(): Unit = {
    val FontFamily = "MyTestFont"
    initStyleProperty(JavaFxStylesHandler.StyleFontFamily, FontFamily)
    val font = handler.getFont
    assertEquals("Wrong font", FontFamily, font.family.get)
  }

  /**
   * Tests whether the font can be set.
   */
  @Test def testSetFont(): Unit = {
    val font = JavaFxFont(family = Some("TestFont"))
    handler setFont font
    checkStyle(JavaFxStylesHandler.StyleFontFamily, font.family.get)
  }
}
