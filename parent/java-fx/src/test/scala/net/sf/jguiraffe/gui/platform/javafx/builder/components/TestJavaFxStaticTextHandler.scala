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
package net.sf.jguiraffe.gui.platform.javafx.builder.components

import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import org.apache.commons.lang.StringUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.{Before, BeforeClass, Test}
import org.scalatest.junit.JUnitSuite
import javafx.scene.control.ContentDisplay
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView

import net.sf.jguiraffe.gui.builder.components.model.StaticTextData
import net.sf.jguiraffe.gui.builder.components.model.TextIconAlignment
import net.sf.jguiraffe.gui.builder.components.tags.StaticTextDataImpl
import net.sf.jguiraffe.gui.platform.javafx.common.ImageWrapper

object TestJavaFxStaticTextHandler {
  @BeforeClass def setUpOnce(): Unit = {
    JavaFxTestHelper.initPlatform()
  }
}

/**
 * Test class for ''JavaFxStaticTextHandler''.
 */
class TestJavaFxStaticTextHandler extends JUnitSuite {
  /** Constant for a test text. */
  private val Text = "TestLabelText"

  /** The wrapped label control. */
  private var label: Label = _

  /** The handler to be tested. */
  private var handler: JavaFxStaticTextHandler = _

  @Before def setUp() {
    label = new Label
    handler = new JavaFxStaticTextHandler(label)
  }

  /**
   * Creates an icon to be used with JavaFX components.
   * @return the icon
   */
  private def createIcon() =
    new ImageView(createImage())

  /**
    * Creates a test image.
    * @return the image
    */
  private def createImage(): Image =
    new Image("icon.jpg")

  /**
   * Tests whether the correct type is returned.
   */
  @Test def testGetType() {
    assertEquals("Wrong data type", classOf[StaticTextData], handler.getType)
  }

  /**
   * Tests whether the handler's data can be queried.
   */
  @Test def testGetData() {
    val icon = createIcon()
    label setGraphic icon
    label setText Text
    label setContentDisplay ContentDisplay.CENTER
    val data = handler.getData
    assertSame("Wrong icon", icon, data.getIcon)
    assertEquals("Wrong text", Text, data.getText)
    assertEquals("Wrong alignment", TextIconAlignment.CENTER, data.getAlignment)
  }

  /**
   * Tests getData() for the initial values of a label.
   */
  @Test def testGetDataNotSet() {
    val data = handler.getData
    assertNull("Got an icon", data.getIcon)
    assertTrue("Got text", StringUtils.isEmpty(data.getText))
    assertEquals("Wrong default alignment", TextIconAlignment.LEFT,
      data.getAlignment)
  }

  /**
   * Tests whether the handler's data can be set.
   */
  @Test def testSetData() {
    val data = new StaticTextDataImpl
    data setText Text
    data setIcon createIcon()
    data setAlignment TextIconAlignment.RIGHT
    handler setData data
    assertEquals("Wrong text", Text, label.getText)
    assertEquals("Wrong icon", data.getIcon, label.getGraphic)
    assertEquals("Wrong alignment", ContentDisplay.RIGHT, label.getContentDisplay)
  }

  /**
   * Tests setData() if no data is provided.
   */
  @Test def testSetDataUndefined() {
    label setText Text
    label setGraphic createIcon()
    label setContentDisplay ContentDisplay.RIGHT
    handler setData null
    assertTrue("Got text", StringUtils.isEmpty(label.getText))
    assertNull("Got an icon", label.getGraphic)
    assertEquals("Wrong content display", ContentDisplay.LEFT,
      label.getContentDisplay)
  }

  /**
    * Tests that an ImageWrapper passed to the setIcon() method is
    * handled correctly.
    */
  @Test def testSetIconImageWrapper(): Unit = {
    val image = createImage()
    val icon = ImageWrapper(image)
    handler setIcon icon
    assertEquals("Wrong icon", image, label.getGraphic.asInstanceOf[ImageView].getImage)
  }
}
