/*
 * Copyright 2006-2014 The JGUIraffe Team.
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

import java.awt.Dimension
import java.awt.Rectangle

import org.easymock.EasyMock
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.EasyMockSugar

import javafx.geometry.Insets
import net.sf.jguiraffe.gui.layout.PercentLayoutBase

/**
 * Test class for ''PercentLayoutPane''.
 */
class TestPercentLayoutPane extends JUnitSuite with EasyMockSugar {
  /** Constant for top insets. */
  private val TopInsets = 5

  /** Constant for left insets. */
  private val LeftInsets = 4

  /** Constant for bottom insets. */
  private val BottomInsets = 7

  /** Constant for right insets. */
  private val RightInsets = 8

  /** Constant for the container's insets. */
  private val Insets = new Insets(TopInsets, RightInsets, BottomInsets, LeftInsets)

  /** Constant for a size value. */
  private val Size = new Dimension(200, 100)

  /** The mock for the layout base object. */
  private var layout: PercentLayoutBase = _

  /** The mock for the wrapped container. */
  private var container: ContainerWrapper = _

  /** The pane to be tested. */
  private var pane: PercentLayoutPane = _

  @Before def setUp() {
    layout = mock[PercentLayoutBase]
    container = mock[ContainerWrapper]
    EasyMock.replay(container)
    pane = new PercentLayoutPane(layout, container) {
      setWidth(Size.width)
      setHeight(Size.height)

      override def getInsets() = Insets
    }
  }

  /**
   * Tests whether the minimum width of the layout can be calculated.
   */
  @Test def testComputeMinWidth() {
    EasyMock.expect(layout.calcMinimumLayoutSize(container)).andReturn(Size)
    whenExecuting(layout) {
      assertEquals("Wrong minimum width", Size.width + LeftInsets + RightInsets,
        pane.computeMinWidth(0).toInt)
    }
  }

  /**
   * Tests whether the minimum height of the layout can be calculated.
   */
  @Test def testComputeMinHeight() {
    EasyMock.expect(layout.calcMinimumLayoutSize(container)).andReturn(Size)
    whenExecuting(layout) {
      assertEquals("Wrong minimum height", Size.height + TopInsets + BottomInsets,
        pane.computeMinHeight(100).toInt)
    }
  }

  /**
   * Tests whether the preferred width of the layout can be calculated.
   */
  @Test def testComputePrefWidth() {
    EasyMock.expect(layout.calcPreferredLayoutSize(container)).andReturn(Size)
    whenExecuting(layout) {
      assertEquals("Wrong preferred width", Size.width + LeftInsets + RightInsets,
        pane.computePrefWidth(-1).toInt)
    }
  }

  /**
   * Tests whether the preferred height of the layout can be calculated.
   */
  @Test def testComputePrefHeight() {
    EasyMock.expect(layout.calcPreferredLayoutSize(container)).andReturn(Size)
    whenExecuting(layout) {
      assertEquals("Wrong preferred height", Size.height + TopInsets + BottomInsets,
        pane.computePrefHeight(2).toInt)
    }
  }

  /**
   * Tests whether the layout can be applied to the child components.
   */
  @Test def tetLayoutChildren() {
    layout.performLayout(container, new Rectangle(LeftInsets, TopInsets,
      RightInsets, BottomInsets), Size)
    whenExecuting(layout) {
      pane.layoutChildren()
    }
  }
}
