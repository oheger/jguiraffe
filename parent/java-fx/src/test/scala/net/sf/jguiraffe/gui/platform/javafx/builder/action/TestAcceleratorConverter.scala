/*
 * Copyright 2006-2018 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.platform.javafx.builder.action

import java.util
import javafx.scene.input.KeyCombination

import net.sf.jguiraffe.gui.builder.action.Accelerator
import net.sf.jguiraffe.gui.builder.event.{Keys, Modifiers}
import org.junit.Assert._
import org.junit.Test
import org.scalatest.junit.JUnitSuite

/**
 * Test class for ''AcceleratorConverter''.
 */
class TestAcceleratorConverter extends JUnitSuite {
  /**
   * Tests whether null input is handled correctly.
   */
  @Test def testConvertNull(): Unit = {
    assertNull("Got a KeyCombination", AcceleratorConverter convertAccelerator null)
  }

  /**
   * Tests a conversion if the accelerator contains a character code.
   */
  @Test def testConvertCharacter(): Unit = {
    val acc = Accelerator.getInstance(Character.valueOf('x'), null)
    assertEquals("Wrong result", KeyCombination.valueOf("'x'"),
      AcceleratorConverter convertAccelerator acc)
  }

  /**
   * Tests a conversion for an accelerator containing a special key code.
   * All special key codes defined by the library are checked.
   */
  @Test def testConvertSpecialKey(): Unit = {
    val keys = Keys.values()
    val expectedCodes = Array("BACK_SPACE", "DELETE", "DOWN", "END", "ENTER",
      "ESCAPE", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10",
      "F11", "F12", "F13", "F14", "F15", "F16", "HOME", "INSERT", "LEFT",
      "PAGE_DOWN", "PAGE_UP", "PRINTSCREEN", "RIGHT", "SPACE", "TAB", "UP")

    assertEquals("Wrong number of expected codes", keys.size, expectedCodes.size)
    for (i <- 0 until keys.size) {
      val acc = Accelerator.getInstance(keys(i), null)
      assertEquals("Wrong result", KeyCombination.valueOf(expectedCodes(i)),
        AcceleratorConverter convertAccelerator acc)
    }
  }

  /**
   * Tests whether the supported modifiers are handled correctly.
   */
  @Test def testConvertModifier(): Unit = {
    val modifiers = Modifiers.values()
    val expectedModifiers = Array("Alt", "Alt", "Ctrl", "Meta", "Shift")

    assertEquals("Wrong number of expected modifiers", modifiers.size, expectedModifiers.size)
    for (i <- 0 until modifiers.size) {
      val acc = Accelerator.getInstance(Keys.F1, java.util.Collections.singleton(modifiers(i)))
      assertEquals("Wrong result", KeyCombination.valueOf(expectedModifiers(i) + "+F1"),
        AcceleratorConverter convertAccelerator acc)
    }
  }

  /**
   * Tests whether multiple modifiers are supported.
   */
  @Test def testConvertMultipleModifiers(): Unit = {
    val acc = Accelerator.getInstance(Keys.F10, new util.HashSet[Modifiers](java.util.Arrays
      .asList(Modifiers.SHIFT, Modifiers.CONTROL)))
    assertEquals("Wrong result", KeyCombination valueOf "Shift+Ctrl+F10",
      AcceleratorConverter convertAccelerator acc)
  }
}
