/*
 * Copyright 2006-2015 The JGUIraffe Team.
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

import javafx.scene.control.{TextField, TextInputControl}

import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import net.sf.jguiraffe.gui.platform.javafx.builder.event.ChangeEventSource
import org.junit.Assert.{assertEquals, assertFalse, assertSame, assertTrue}
import org.junit.{BeforeClass, Test}
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.EasyMockSugar

object TestJavaFxTextHandler {
  @BeforeClass def setUpOnce(): Unit = {
    JavaFxTestHelper.initPlatform()
  }
}

/**
 * Test class for ''JavaFxTextHandler''.
 */
class TestJavaFxTextHandler extends JUnitSuite with EasyMockSugar {
  /** Constant for a test text. */
  private val TestText = "Test text for JavaFxTextHandler test"

  /** The text input component (a mock or a real text field). */
  private var txtControl: TextInputControl = _

  /**
   * Creates the handler and initializes it with a mock text control.
   * @return the handler
   */
  private def createHandlerWithMock(): JavaFxTextHandler = {
    txtControl = mock[TextInputControl]
    new JavaFxTextHandler(txtControl)
  }

  /**
   * Creates the handler and initializes it with a real text control. The
   * control is already initialized with a test text.
   * @return the handler
   */
  private def createHandlerWithField(): JavaFxTextHandler = {
    txtControl = new TextField(TestText)
    new JavaFxTextHandler(txtControl)
  }

  /**
   * Tests whether the correct component is passed to the parent class.
   */
  @Test def testComponent() {
    val handler = createHandlerWithMock()
    assertSame("Wrong component", txtControl, handler.getComponent)
  }

  /**
   * Tests whether the correct data type is returned.
   */
  @Test def testGetType() {
    val handler = createHandlerWithMock()
    assertEquals("Wrong type", classOf[String], handler.getType)
  }

  /**
   * Tests whether control data can be queried from the handler.
   */
  @Test def testGetData() {
    val handler = createHandlerWithField()
    assertEquals("Wrong component data", TestText, handler.getData)
  }

  /**
   * Tests whether control data can be set via the handler.
   */
  @Test def testSetData() {
    val handler = createHandlerWithField()
    txtControl.clear()
    handler setData TestText
    assertEquals("Text was not set", TestText, txtControl.getText)
  }

  /**
   * Tests hasSelection() if the expected result is true.
   */
  @Test def testHasSelectionTrue() {
    val handler = createHandlerWithField()
    txtControl.selectRange(0, 5)
    assertTrue("No selection", handler.hasSelection)
  }

  /**
   * Tests hasSelection() if the expected result is false.
   */
  @Test def testHasSelectionFalse() {
    val handler = createHandlerWithField()
    assertFalse("Got a selection", handler.hasSelection)
  }

  /**
   * Tests whether the selection start can be queried.
   */
  @Test def testGetSelectionStart() {
    val handler = createHandlerWithField()
    txtControl.selectRange(5, 8)
    assertEquals("Wrong selection start", 5, handler.getSelectionStart)
  }

  /**
   * Tests whether the selection end can be queried.
   */
  @Test def testGetSelectionEnd() {
    val handler = createHandlerWithField()
    txtControl.selectRange(5, 8)
    assertEquals("Wrong selection end", 8, handler.getSelectionEnd)
  }

  /**
   * Tests whether the text selection can be changed.
   */
  @Test def testSelect() {
    val handler = createHandlerWithField()
    handler.select(1, 5)
    val selRange = txtControl.getSelection
    assertEquals("Wrong selection start", 1, selRange.getStart)
    assertEquals("Wrong selection end", 5, selRange.getEnd)
  }

  /**
   * Tests whether the complete text can be selected.
   */
  @Test def testSelectAll() {
    val handler = createHandlerWithField()
    handler.selectAll()
    assertEquals("Wrong selection", TestText, txtControl.getSelectedText)
  }

  /**
   * Tests whether the selection can be cleared.
   */
  @Test def testClearSelection() {
    val handler = createHandlerWithField()
    txtControl.selectRange(4, 10)
    handler.clearSelection()
    assertEquals("Still selected", 0, txtControl.getSelection.getLength)
  }

  /**
   * Tests whether the selected text can be queried.
   */
  @Test def testGetSelectedText() {
    val handler = createHandlerWithField()
    val start = 5
    val end = 10
    txtControl.selectRange(start, end)
    assertEquals("Wrong selected text", TestText.substring(start, end),
      handler.getSelectedText)
  }

  /**
   * Tests whether the selection can be replaced.
   */
  @Test def testReplaceSelectedText() {
    val ReplText = "REPLACE"
    val handler = createHandlerWithField()
    txtControl.selectRange(0, 1)
    handler replaceSelectedText ReplText
    assertEquals("Wrong resulting text", ReplText + TestText.substring(1),
      txtControl.getText)
  }

  /**
   * Tests whether text can be copied to the clip-board.
   */
  @Test def testCopy() {
    val handler = createHandlerWithMock()
    txtControl.copy()
    whenExecuting(txtControl) {
      handler.copy()
    }
  }

  /**
   * Tests a cut operation.
   */
  @Test def testCut() {
    val handler = createHandlerWithMock()
    txtControl.cut()
    whenExecuting(txtControl) {
      handler.cut()
    }
  }

  /**
   * Tests a paste operation.
   */
  @Test def testPaste() {
    val handler = createHandlerWithMock()
    txtControl.paste()
    whenExecuting(txtControl) {
      handler.paste()
    }
  }

  /**
   * Tests whether the correct property for registering change listeners is
   * returned.
   */
  @Test def testPropertyForChangeEvents() {
    val source: ChangeEventSource = createHandlerWithField()
    assertSame("Wrong change event source", txtControl.textProperty,
      source.observableValue)
  }
}
