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

import java.util.concurrent.CountDownLatch

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.scalatest.junit.JUnitSuite

import javafx.beans.property.DoubleProperty
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.SimpleDoubleProperty
import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper.await
import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper.initPlatform
import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper.toRunnable

/**
 * Companion object for ''TestSplitPaneResizeListener''.
 */
object TestSplitPaneResizeListener {
  import JavaFxTestHelper._
  @BeforeClass def setUpOnce() {
    initPlatform()
  }

  /**
   * Reads the current value of a property in the JavaFX thread.
   * @tparam T the type of the property
   * @param prop the property to be read
   */
  private def readProperty[T](prop: ReadOnlyProperty[T]): T = {
    val latch = new CountDownLatch(1)
    var value: T = null.asInstanceOf[T]
    JavaFxTestHelper.runInFxThread { () =>
      value = prop.getValue
      latch.countDown()
    }
    await(latch)
    value
  }

  /**
   * Creates a property for storing double values.
   * @return the property
   */
  private def createProperty(): DoubleProperty = new SimpleDoubleProperty

  /**
   * Creates a property for double values and registers the listener at it.
   * @param l the listener
   * @return the property
   */
  private def createMonitoredProperty(l: SplitPaneResizeListener): DoubleProperty = {
    val prop = createProperty()
    prop addListener l
    prop
  }
}

/**
 * Test class for ''SplitPaneResizeListener''.
 */
class TestSplitPaneResizeListener extends JUnitSuite {
  import TestSplitPaneResizeListener._

  /** The property to be updated by the test listener. */
  private var position: DoubleProperty = _

  @Before def setUp() {
    position = createProperty()
  }

  /**
   * Checks whether the position property contains the correct value.
   * @param exp the expected value
   */
  private def assertPosition(exp: Double) {
    assertEquals("Wrong value", exp, readProperty(position).doubleValue, .001)
  }

  /**
   * Tests whether the listener can initialize the position once.
   */
  @Test def testInitalPosition() {
    val listener = new SplitPaneResizeListener(100, 0, position)
    val size = createMonitoredProperty(listener)
    size set 1000
    assertPosition(.1)
  }

  /**
   * Tests the initial update of the monitored property if no position is
   * defined.
   */
  @Test def testInitialPositionUndefined() {
    val listener = new SplitPaneResizeListener(0, 0, position)
    val size = createMonitoredProperty(listener)
    size set 1000
    assertPosition(0)
  }

  /**
   * Tests a size update if the resize weight is 0. In this case, the left/top
   * component keeps its size.
   */
  @Test def testSizeUpdateResizeWeightZero() {
    val listener = new SplitPaneResizeListener(0, 0, position)
    val size = createMonitoredProperty(listener)
    position set .4
    size set 100
    size set 200
    assertPosition(.2)
  }

  /**
   * Tests a size update if the resize weight is 1. In this case, the
   * right/bottom component keeps its size.
   */
  @Test def testSizeUpdateResizeWeightOne() {
    val listener = new SplitPaneResizeListener(0, 1, position)
    val size = createMonitoredProperty(listener)
    position set .5
    size set 100
    size set 200
    assertPosition(.75)
  }

  /**
   * Tests a size update if the additional size has to be distributed on both
   * components.
   */
  @Test def testSizeUpdateResizeWeightDistributed() {
    val listener = new SplitPaneResizeListener(0, .25, position)
    val size = createMonitoredProperty(listener)
    position set .5
    size set 100
    size set 200
    assertPosition(.375)
  }
}
