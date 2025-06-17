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
package net.sf.jguiraffe.gui.platform.javafx.common

import javafx.scene.Node
import javafx.scene.control.Label

import net.sf.jguiraffe.gui.builder.components.FormBuilderException
import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import org.junit.Assert._
import org.junit.{BeforeClass, Test}
import org.scalatestplus.junit.JUnitSuite

object TestComponentUtils {
  @BeforeClass def setUpOnce(): Unit = {
    JavaFxTestHelper.initPlatform()
  }
}

/**
 * Test class for ''ComponentUtils''.
 */
class TestComponentUtils extends JUnitSuite {
  /**
   * Tests a successful type conversion.
   */
  @Test def testAsSuccessful(): Unit = {
    val node: AnyRef = new Label
    val asNode: Node = ComponentUtils.as[Node](node)
    assertNotNull("No result", asNode)
  }

  /**
   * Tests a failed type conversion.
   */
  @Test(expected = classOf[FormBuilderException]) def testAsFailed(): Unit = {
    ComponentUtils.as[Node](this)
  }

  /**
   * Tests a type conversion with null input.
   */
  @Test(expected = classOf[FormBuilderException]) def testAsNull(): Unit = {
    ComponentUtils.as[Node](null)
  }

  /**
   * Tests whether mnemonicText() can handle null input.
   */
  @Test def testMnemonicTextNull() {
    assertNull("Wrong result", ComponentUtils.mnemonicText(null, 'x'))
  }

  /**
   * Tests whether a text with a mnemonic is correctly manipulated.
   */
  @Test def testMnemonicTextFound() {
    assertEquals("Wrong result (1)", "A _Test", ComponentUtils.mnemonicText("A Test", 'T'))
    assertEquals("Wrong result (2)", "_Test", ComponentUtils.mnemonicText("Test", 'T'))
    assertEquals("Wrong result (3)", "ab_c", ComponentUtils.mnemonicText("abc", 'c'))
    assertEquals("Wrong result (4)", "_a", ComponentUtils.mnemonicText("a", 'a'))
  }

  /**
   * Tests whether whether case is ignored when searching for mnemonics.
   */
  @Test def testMnemonicTextCase() {
    assertEquals("Wrong result (1)", "a_bc", ComponentUtils.mnemonicText("abc", 'B'))
    assertEquals("Wrong result (2)", "A_BC", ComponentUtils.mnemonicText("ABC", 'b'))
  }

  /**
   * Tests mnemonicText() if the mnemonic cannot be found.
   */
  @Test def testMnemonicTextNotFound() {
    assertEquals("Wrong result (1)", "check", ComponentUtils.mnemonicText("check", 'z'))
    assertEquals("Wrong result (2)", "", ComponentUtils.mnemonicText("", 'a'))
  }
}
