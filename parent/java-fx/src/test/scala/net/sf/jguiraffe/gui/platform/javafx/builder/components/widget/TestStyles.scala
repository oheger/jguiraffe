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

import org.junit.Assert.{assertEquals, assertFalse, assertTrue}
import org.junit.Test
import org.scalatestplus.junit.JUnitSuite

/**
 * Test class for ''Styles''.
 */
class TestStyles extends JUnitSuite {
  /** Constant for a textual styles definition. */
  val StylesDefinition = """-fx-font: 16px   "Serif;Test";
        -fx-background-color :    #AA99FF;
        -fx-unknown: 18.5%_blupp http://test.com?check=x;"""

  /**
   * Tests whether an empty instance can be created.
   */
  @Test def testInitEmpty() {
    val styles = Styles()
    assertTrue("Got styles", styles.styleKeys.isEmpty)
  }

  /**
   * Tests whether an instance can be created from a styles definition.
   */
  @Test def testInitFromDefinition() {
    val styles = Styles(StylesDefinition)
    assertEquals("Wrong number of styles", 3, styles.styleKeys.size)
  }

  /**
   * Tests whether the value of a style can be queried.
   */
  @Test def testQueryStyle() {
    val styles = Styles(StylesDefinition)
    assertEquals("Wrong style value (1)", "#AA99FF",
      styles("-fx-background-color").get)
    assertEquals("Wrong style value (2)", "18.5%_blupp http://test.com?check=x",
      styles("-fx-unknown").get)
  }

  /**
   * Tests whether style sheet values with a string literal are parsed
   * correctly and can be queried.
   */
  @Test def testQueryStyleWithStringLiteral() {
    val styles = Styles(StylesDefinition)
    assertEquals("Wrong style value", "16px \"Serif;Test\"", styles("-fx-font").get)
  }

  /**
   * Tests whether styles can be converted to a textual representation.
   */
  @Test def testToExternalForm() {
    val styles = Styles(StylesDefinition)
    checkExternalForm(styles, """-fx-font: 16px "Serif;Test";""",
      "-fx-background-color: #AA99FF;",
      "-fx-unknown: 18.5%_blupp http://test.com?check=x;")
  }

  /**
   * Checks whether the external form of the given Styles object contains all
   * the given styles definitions. Because toExternalForm() returns the
   * styles in random order the test is a bit more tricky.
   * @param styles the Styles object to check
   * @param expDefs all the style definitions to check
   */
  private def checkExternalForm(styles: Styles, expDefs: String*) {
    val ext = styles.toExternalForm()
    for (sd <- expDefs) {
      assertTrue("Definition " + sd + " not found in " + ext, ext contains sd)
    }
  }

  /**
   * Tests whether style definitions can be removed.
   */
  @Test def testRemoveStyles() {
    val styles = Styles(StylesDefinition)
    styles -= "-fx-font"
    styles -= "-fx-unknown"
    assertEquals("Wrong remaining styles", "-fx-background-color: #AA99FF;\n",
      styles.toExternalForm())
  }

  /**
   * Tests whether new styles can be added.
   */
  @Test def testAddStyles() {
    val styles = Styles()
    styles += ("-fx-new", "true")
    assertEquals("Wrong styles", "-fx-new: true;\n", styles.toExternalForm)
  }

  /**
   * Tests whether updateStyle() can be used to add/override styles.
   */
  @Test def testUpdateStyleAdd() {
    val styles = Styles()
    styles += ("-fx-new", "perhaps")
    styles.updateStyle("-fx-new", Some("true"))
    styles.updateStyle("-fx-new2", Some("absolutely"))
    assertEquals("New style not added", "absolutely", styles("-fx-new2").get)
    assertEquals("Style not updated", "true", styles("-fx-new").get)
  }

  /**
   * Tests whether updateStyle() can be used to remove a style.
   */
  @Test def testUpdateStyleRemove() {
    val styles = Styles(StylesDefinition)
    styles.updateStyle("-fx-unknown", None)
    assertFalse("Style not removed", styles.styleKeys.contains("-fx-unknown"))
  }

  /**
   * Tests the string representation.
   */
  @Test def testToString() {
    val styles = Styles(StylesDefinition)
    val s = styles.toString()
    assertTrue("Wrong string representation: " + s, s.contains(styles.toExternalForm()))
  }

  /**
   * Tests whether parsing errors are handled.
   */
  @Test def testParseError() {
    val styles = Styles("-fx-test: strange: value = ?")
    assertTrue("Got styles", styles.styleKeys.isEmpty)
  }
}
