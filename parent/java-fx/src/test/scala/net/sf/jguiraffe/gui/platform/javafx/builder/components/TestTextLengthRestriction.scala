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
package net.sf.jguiraffe.gui.platform.javafx.builder.components

import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import org.junit.Assert.assertEquals
import org.junit.{BeforeClass, Before, Test}
import org.scalatestplus.junit.JUnitSuite

import javafx.scene.control.TextField

object TestTextLengthRestriction {
  @BeforeClass def setUpOnce(): Unit = {
    JavaFxTestHelper.initPlatform()
  }
}

/**
 * Test class for ''TextLengthRestriction''.
 */
class TestTextLengthRestriction extends JUnitSuite {
  type RestrictedTextField = TextField with TextLengthRestriction

  /** Constant for a test text. */
  private val TestText = "0123456789"

  /** The text field to be tested. */
  private var field: RestrictedTextField = _

  @Before def setUp(): Unit = {
    field = new TextField with TextLengthRestriction
  }

  /**
   * Tests whether the restriction can be disabled.
   */
  @Test def testNoRestriction(): Unit = {
    field setText TestText
    field.insertText(0, "a")
    assertEquals("Wrong text", "a" + TestText, field.getText)
  }

  /**
   * Tests that no text can be added if the maximum length is reached.
   */
  @Test def testAppendTextFull(): Unit = {
    field setMaximumLength TestText.length
    field setText TestText
    field appendText TestText
    assertEquals("Text was appended", TestText, field.getText)
  }

  /**
   * Tests whether text can be appended if it fits into the length restriction.
   */
  @Test def testAppendSuccess(): Unit = {
    field setMaximumLength TestText.length
    field appendText TestText
    assertEquals("Wrong text", TestText, field.getText)
  }

  /**
   * Tests whether text is partly appended if some character fit into the
   * restriction.
   */
  @Test def testAppendPartly(): Unit = {
    field setMaximumLength 15
    field setText TestText
    field appendText TestText
    assertEquals("Wrong text", TestText + "01234", field.getText)
  }

  /**
   * Tests whether the restriction also works with the current selection.
   */
  @Test def testReplaceSelection(): Unit = {
    field setMaximumLength TestText.length
    field setText TestText
    field.selectRange(0, 2)
    field replaceSelection "abc"
    assertEquals("Wrong text", "ab" + TestText.substring(2), field.getText)
  }
}
