/*
 * Copyright 2006-2022 The JGUIraffe Team.
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

import javafx.scene.image.Image

import org.junit.Assert._
import org.junit.{Before, Test}
import org.scalatest.junit.JUnitSuite
import org.scalatest.easymock.EasyMockSugar

/**
 * Test class for ''ImageWrapper''.
 */
class TestImageWrapper extends JUnitSuite with EasyMockSugar {
  /** A mock for the image. */
  private var image: Image = _

  /** The wrapper to be tested. */
  private var wrapper: ImageWrapper = _

  @Before def setUp() {
    image = mock[Image]
    wrapper = ImageWrapper(image)
  }

  /**
   * Tests whether a new ImageView can be created from the wrapper.
   */
  @Test def testImageView() {
    val iv = wrapper.newImageView()
    assertSame("Wrong image", image, iv.getImage)
  }

  /**
   * Tests that each invocation of newImageView() creates a new instance.
   */
  @Test def testImageViewNewInstance() {
    val iv = wrapper.newImageView()
    assertNotSame("Same instance", iv, wrapper.newImageView())
  }
}
