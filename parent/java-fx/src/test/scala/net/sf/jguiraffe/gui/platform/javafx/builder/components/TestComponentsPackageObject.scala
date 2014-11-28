/**
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

import org.junit.Assert.assertEquals
import org.junit.Test
import org.scalatest.junit.JUnitSuite

import javafx.geometry.Side
import javafx.scene.control.ContentDisplay
import net.sf.jguiraffe.gui.builder.components.model.TextIconAlignment
import net.sf.jguiraffe.gui.builder.components.tags.TabbedPaneTag

/**
 * Test class for the package object of the ''components'' package.
 */
class TestComponentsPackageObject extends JUnitSuite {
  /**
   * Tests whether alignment values can be converted to their JavaFX
   * counterparts.
   */
  @Test def testConvertAlignment() {
    assert(ContentDisplay.LEFT === convertAlignment(TextIconAlignment.LEFT))
    assert(ContentDisplay.RIGHT === convertAlignment(TextIconAlignment.RIGHT))
    assert(ContentDisplay.CENTER === convertAlignment(TextIconAlignment.CENTER))
  }

  /**
   * Tests whether ContentDisplay values can be converted to their JGUIraffe
   * counterparts.
   */
  @Test def testConvertContentDisplay() {
    assert(TextIconAlignment.LEFT === convertContentDisplay(ContentDisplay.LEFT))
    assert(TextIconAlignment.RIGHT === convertContentDisplay(ContentDisplay.RIGHT))
    assert(TextIconAlignment.CENTER === convertContentDisplay(ContentDisplay.CENTER))
  }

  /**
   * Tests whether Placement values can be converted to Side literals.
   */
  @Test def testConvertPlacementToSide() {
    assert(Side.RIGHT === convertPlacementToSide(TabbedPaneTag.Placement.RIGHT))
    assert(Side.LEFT === convertPlacementToSide(TabbedPaneTag.Placement.LEFT))
    assert(Side.BOTTOM === convertPlacementToSide(TabbedPaneTag.Placement.BOTTOM))
    assert(Side.TOP === convertPlacementToSide(TabbedPaneTag.Placement.TOP))
  }

  /**
   * Tests whether a reasonable default value is used when converting Placement
   * values to Side literals.
   */
  @Test def testConvertSideToPlacementDefault() {
    assertEquals("Wrong default", Side.TOP, convertPlacementToSide(null))
  }

  /**
   * Tests whether orientation values are correctly converted.
   */
  @Test def testConvertOrientation() {
    assert(javafx.geometry.Orientation.VERTICAL ==
      convertOrientation(net.sf.jguiraffe.gui.builder.components.Orientation.VERTICAL))
    assert(javafx.geometry.Orientation.HORIZONTAL ==
      convertOrientation(net.sf.jguiraffe.gui.builder.components.Orientation.HORIZONTAL))
  }
}
