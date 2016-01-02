/*
 * Copyright 2006-2016 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.platform.javafx.layout

import java.util.concurrent.CountDownLatch

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.scalatest.junit.JUnitSuite

import javafx.scene.text.Font
import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper

/**
 * Test class for ''JavaFxUnitSizeHandler''. Unfortunately, this class is
 * hard to unit test because it accesses Java FX functionality which can
 * hardly be mocked. Therefore, we can merely invoke the methods and check
 * whether results seem sensible.
 */
class TestJavaFxUnitSizeHandler extends JUnitSuite {
  /** The handler to be tested.*/
  private var handler: JavaFxUnitSizeHandler = _

  @Before def setUp() {
    handler = new JavaFxUnitSizeHandler
  }

  /**
   * Tests whether the screen resolution can be queried.
   */
  @Test def testGetScreenResolution() {
    assertTrue("Not a valid screen resolution", handler.getScreenResolution() > 0)
  }

  /**
   * Helper method for querying the size of a container's font from the size
   * handler.
   * @param container the container with the font in question
   * @return a tuple with the font's width and height
   */
  private def queryFontSize(container: ContainerWrapper) = {
    val width = handler.getFontSize(container, y = false)
    val height = handler.getFontSize(container, y = true)
    (width, height)
  }

  /**
   * Tests whether font sizes can be queried. This test also checks whether
   * the handler is thread-safe and correctly uses the Java FX thread.
   */
  @Test def testGetFontSizeConcurrently() {
    val ThreadCount = 16
    val container1 = new ContainerWrapper
    val container2 = new ContainerWrapper
    val latch = new CountDownLatch(1)

    def createAndStartThread(idx: Int): QueryFontSizeThread = {
      val cont = if (idx % 2 == 0) container1
      else container2
      val thread = new QueryFontSizeThread(latch = latch, container = cont)
      thread.start()
      thread
    }

    container1.fontInitializer = Some(TestJavaFxUnitSizeHandler.createFontInitializer(Font.getDefault))
    container2.fontInitializer = Some(TestJavaFxUnitSizeHandler.createFontInitializer(new Font(32)))
    val threads =
      for (i <- 0 until ThreadCount) yield createAndStartThread(i)

    latch.countDown()
    val sizes1 = queryFontSize(container1)
    val sizes2 = queryFontSize(container2)
    assertTrue("Width too small", sizes1._1 > 0)
    assertTrue("Height too small", sizes1._2 > 0)
    assertTrue("Strange widths", sizes1._1 < sizes2._1)
    assertTrue("Strange heights", sizes1._2 < sizes2._2)
    assertTrue("Strange width to height ratio", sizes1._1 / sizes1._2 <= 3)
    for (t <- threads) {
      t.join()
      val expSizes = if (t.container == container1) sizes1
      else sizes2
      assertEquals("Wrong font sizes", expSizes, t.fontSize)
    }
  }

  /**
   * A test thread class for concurrently accessing a size handler querying
   * font sizes.
   * @param container the container whose font size is to be checked
   * @param latch the count down latch for synchronizing thread start up
   */
  private class QueryFontSizeThread(val container: ContainerWrapper,
    latch: CountDownLatch) extends Thread {
    @volatile var fontSize: (Double, Double) = _

    override def run() {
      latch.await()
      fontSize = queryFontSize(container)
    }
  }
}

object TestJavaFxUnitSizeHandler {
  @BeforeClass def setUpBeforeClass() {
    JavaFxTestHelper.initPlatform()
  }

  /**
    * Creates an initializer for the specified font.
    * @param font the font
    * @return the font initializer
    */
  private def createFontInitializer(font: Font): ContainerWrapper.TextFontInitializer =
    text => {
      text setFont font
      text
    }
}
