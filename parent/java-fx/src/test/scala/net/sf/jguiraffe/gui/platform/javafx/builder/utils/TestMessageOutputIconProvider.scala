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
package net.sf.jguiraffe.gui.platform.javafx.builder.utils

import javafx.scene.image.Image

import net.sf.jguiraffe.gui.builder.utils.MessageOutput
import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import org.junit.Assert._
import org.junit.{BeforeClass, Before, Test}
import org.scalatest.junit.JUnitSuite

object TestMessageOutputIconProvider {
  @BeforeClass def setUpOnce(): Unit = {
    JavaFxTestHelper.initPlatform()
  }
}

/**
 * Test class for ''MessageOutputIconProvider''.
 */
class TestMessageOutputIconProvider extends JUnitSuite {
  /** The icon provider to be tested. */
  private var iconProvider: MessageOutputIconProvider = _

  @Before def setUp(): Unit = {
    iconProvider = new MessageOutputIconProvider {}
  }

  /**
   * Helper method for obtaining an image of the specified type and testing
   * whether it is defined.
   * @param messageType the message type
   * @return the image for this message type
   */
  private def checkIcon(messageType: Int): Image = (iconProvider messageIcon messageType).get

  /**
   * Returns a set with all supported icons from the test provider.
   * @return the set with all icons
   */
  private def fetchAllIcons(): Set[Image] =
    Set(checkIcon(MessageOutput.MESSAGE_INFO),
      checkIcon(MessageOutput.MESSAGE_QUESTION),
      checkIcon(MessageOutput.MESSAGE_WARNING), checkIcon(MessageOutput.MESSAGE_ERROR))

  /**
   * Tests that different icons for all types are returned.
   */
  @Test def testAllIconsDifferent(): Unit = {
    val icons = fetchAllIcons()
    assertEquals("Wrong number of distinct icons", 4, icons.size)
  }

  /**
   * Tests whether an unknown message type is handled correctly.
   */
  @Test def testUnknownMessageType(): Unit = {
    assertFalse("Got an icon", iconProvider.messageIcon(1000).isDefined)
  }

  /**
   * Tests whether images are cached.
   */
  @Test def testIconsCached(): Unit = {
    val icons = fetchAllIcons()
    val icons2 = fetchAllIcons()
    val iconsCombined = icons ++ icons2
    assertEquals("Got different icons", icons.size, iconsCombined.size)
  }
}
