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
package net.sf.jguiraffe.gui.platform.javafx.builder.components.table

import javafx.scene.control.ContentDisplay
import javafx.scene.image.{Image, ImageView}

import net.sf.jguiraffe.gui.builder.components.tags.table.TableFormController
import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import net.sf.jguiraffe.gui.platform.javafx.common.ImageWrapper
import org.easymock.EasyMock
import org.junit.Assert._
import org.junit.{BeforeClass, Before, Test}
import org.scalatest.junit.JUnitSuite
import org.scalatest.easymock.EasyMockSugar

object TestIconCell {
  @BeforeClass def setUpOnce(): Unit = {
    JavaFxTestHelper.initPlatform()
  }
}

/**
 * Test class for ''IconCell''.
 */
class TestIconCell extends JUnitSuite with EasyMockSugar {
  /** Constant for the column Index. */
  private val ColumnIndex = 8

  /** Constant for the row index. */
  private val RowIndex = 4

  /** A mock for the form controller. */
  private var formController: TableFormController = _

  /** The cell to be tested. */
  private var cell: IconCell[AnyRef] = _

  @Before def setUp() {
    formController = mock[TableFormController]
    cell = new IconCell(formController, ColumnIndex)
    cell updateIndex RowIndex
  }

  /**
   * Checks whether the cell contains the expected image.
   * @param expImg the expected image
   */
  private def checkImage(expImg: Image) {
    val imageView = cell.getGraphic.asInstanceOf[ImageView]
    assertEquals("Wrong image", expImg, imageView.getImage)
  }

  /**
   * Prepares the controller mock for a get value query.
   * @param result the result to be returned
   */
  private def expectGetValue(result: AnyRef) {
    formController selectCurrentRow RowIndex
    EasyMock.expect(formController.getColumnValue(ColumnIndex)).andReturn(result)
  }

  /**
   * Tests whether the UI-related properties are set correctly.
   */
  @Test def testUIInitialization() {
    assertEquals("Wrong content display", ContentDisplay.GRAPHIC_ONLY, cell.getContentDisplay)
    assertTrue("Wrong graphic component", cell.getGraphic.isInstanceOf[ImageView])
  }

  /**
   * Tests whether an empty value can be set for the cell.
   */
  @Test def testUpdateItemEmpty() {
    whenExecuting(formController) {
      cell.updateItem(null, empty = true)
    }
    checkImage(null)
  }

  /**
   * Tests whether an update of the item state is handled correctly.
   */
  @Test def testUpdateItemNewImage() {
    val image = mock[Image]
    val wrapper = mock[ImageWrapper]
    EasyMock.expect(wrapper.image).andReturn(image).anyTimes()
    expectGetValue(wrapper)

    whenExecuting(image, wrapper, formController) {
      cell.updateItem(this, empty = false)
      checkImage(image)
      assertEquals("Wrong item", this, cell.getItem)
    }
  }

  /**
   * Tests updateItem() if the cell contains an invalid value from which no image
   * can be extracted.
   */
  @Test def testUpdateItemInvalidObject() {
    expectGetValue(this)
    whenExecuting(formController) {
      cell.updateItem(this, empty = false)
      checkImage(null)
      assertEquals("Wrong item", this, cell.getItem)
    }
  }
}
