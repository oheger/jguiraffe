/*
 * Copyright 2006-2017 The JGUIraffe Team.
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
import org.junit.Assert.{assertEquals, assertFalse, assertTrue}
import org.junit.{Before, Test}
import org.scalatest.junit.JUnitSuite

/**
 * Test class for ''JavaFxStylesHandler''.
 */
class TestJavaFxStylesHandler extends JUnitSuite {
  /** The underlying styles object. */
  private var styles: Styles = _

  /** The handler to be tested. */
  private var handler: JavaFxStylesHandler = _

  @Before def setUp() {
    styles = Styles()
    handler = new JavaFxStylesHandler(styles)
  }

  /**
   * Tests whether default styles are set if no object is passed in.
   */
  @Test def testInitDefaultStyles() {
    handler = new JavaFxStylesHandler
    assertTrue("No empty styles", handler.styles.styleKeys.isEmpty)
  }

  /**
   * Tests whether a representation object for a font can be obtained.
   */
  @Test def testGetFont() {
    styles += ("-fx-font-family", "FontFamily")
    styles += ("-fx-font-size", "FontSize")
    styles += ("-fx-font-style", "FontStyle")
    styles += ("-fx-font-weight", "FontWeight")
    styles += ("-fx-font", "Font")
    val font = handler.getFont()
    assertEquals("Wrong family", "FontFamily", font.family.get)
    assertEquals("Wrong size", "FontSize", font.size.get)
    assertEquals("Wrong style", "FontStyle", font.style.get)
    assertEquals("Wrong weight", "FontWeight", font.weight.get)
    assertEquals("Wrong font definition", "Font", font.fontDef.get)
  }

  /**
   * Tests whether defined attributes of a font are stored in the styles object.
   */
  @Test def testSetFontDefined() {
    val font = JavaFxFont(family = Some("Family"), size = Some("Size"),
      style = Some("Style"), weight = Some("Weight"),
      fontDef = Some("Def"))
    handler setFont font
    assertEquals("Wrong family", "Family", styles("-fx-font-family").get)
    assertEquals("Wrong size", "Size", styles("-fx-font-size").get)
    assertEquals("Wrong style", "Style", styles("-fx-font-style").get)
    assertEquals("Wrong weight", "Weight", styles("-fx-font-weight").get)
    assertEquals("Wrong fond definition", "Def", styles("-fx-font").get)
  }

  /**
   * Tests whether missing font attributes are removed from the styles object.
   */
  @Test def testSetFontUndefined() {
    val font = JavaFxFont()
    styles += ("-fx-font", "someFont")
    styles += ("other", "test")
    handler setFont font
    assertEquals("Wrong number of style definitions", 1, styles.styleKeys.size)
    assertFalse("Wrong style removed", styles.styleKeys.contains("-fx-font"))
  }

  /**
   * Tests whether the background color can be queried.
   */
  @Test def testGetBackgroundColor() {
    styles += ("-fx-background-color", "bgcol")
    val col = handler.getBackgroundColor()
    assertTrue("Not a logic color", col.isLogicColor)
    assertEquals("Wrong color definition", "bgcol", col.getColorDefinition)
  }

  /**
   * Tests the background color returned if the style sheet is undefined.
   */
  @Test def testGetBackgroundColorUndefined() {
    assert(Color.UNDEFINED === handler.getBackgroundColor())
  }

  /**
   * Tests whether an undefined background color causes the removal of the
   * style definition.
   */
  @Test def testSetBackgroundColorUndefined() {
    styles += ("-fx-background-color", "someValue")
    handler setBackgroundColor Color.UNDEFINED
    assertTrue("Style not removed", styles.styleKeys.isEmpty)
  }

  /**
   * Tests whether a logic background color can be set.
   */
  @Test def testSetBackgroundColorLogic() {
    val color = Color.newLogicInstance("coldef")
    handler setBackgroundColor color
    assertEquals("Wrong style", color.getColorDefinition,
      styles("-fx-background-color").get)
  }

  /**
   * Tests whether an RGB color is correctly converted when set as background
   * color.
   */
  @Test def testSetBackgroundColorRGB() {
    val color = Color.newRGBInstance(0x80, 0xC0, 0xFF)
    handler setBackgroundColor color
    assertEquals("Wrong style", "#80c0ff", styles("-fx-background-color").get)
  }

  /**
   * Tests whether the foreground color can be queried.
   */
  @Test def testGetForegroundColor() {
    styles += ("-fx-text-fill", "fgcol")
    val col = handler.getForegroundColor()
    assertTrue("Not a logic color", col.isLogicColor)
    assertEquals("Wrong color definition", "fgcol", col.getColorDefinition)
  }

  /**
   * Tests whether the foreground color can be set. (The same mechanism is
   * used as for the background color, so we do not have to test all
   * special cases.)
   */
  @Test def testSetForegroundColor() {
    val color = Color.newLogicInstance("coldef")
    handler setForegroundColor color
    assertEquals("Wrong style", color.getColorDefinition,
      styles("-fx-text-fill").get)
  }
}
