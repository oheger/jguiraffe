/*
 * Copyright 2006-2013 The JGUIraffe Team.
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

import org.junit.Test
import org.scalatest.junit.JUnitSuite

import javafx.scene.control.ContentDisplay
import net.sf.jguiraffe.gui.builder.components.model.TextIconAlignment

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
}
