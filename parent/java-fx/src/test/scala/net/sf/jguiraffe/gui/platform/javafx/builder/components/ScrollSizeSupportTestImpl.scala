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
package net.sf.jguiraffe.gui.platform.javafx.builder.components

import org.easymock.EasyMock
import org.easymock.EasyMock.{eq => eqObj}
import org.easymock.IAnswer
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame

import net.sf.jguiraffe.gui.builder.components.Composite
import net.sf.jguiraffe.gui.builder.components.tags.FormBaseTag
import net.sf.jguiraffe.gui.builder.components.tags.ScrollSizeSupport
import net.sf.jguiraffe.gui.layout.NumberWithUnit
import net.sf.jguiraffe.gui.layout.UnitSizeHandler

/**
 * Definition of a trait which simplifies testing of tags with support for a
 * preferred scroll size.
 *
 * This trait allows returning specific values for the preferred scroll size.
 * The values are actually mock objects. It is possible to verify that the
 * correct pixel values have been extracted.
 */
trait ScrollSizeSupportTestImpl extends ScrollSizeSupport {
  this: FormBaseTag =>

  /** The preferred scroll size in X direction (as pixels). */
  val xScrollSize: Int

  /** The preferred scroll size in Y direction (as pixels). */
  val yScrollSize: Int

  /** A mock for the container. */
  val container = new Object

  /** The mock for the Composite wrapping the container. */
  val composite = createComposite()

  /** The answer for the X scroll size. */
  private var xAnswer: SizeAnswer = _

  /** The answer for the Y scroll size. */
  private var yAnswer: SizeAnswer = _

  /** A mock for the scroll size in X direction. */
  private var scrollWidth: NumberWithUnit = _

  /** A mock for the scroll size in Y direction. */
  private var scrollHeight: NumberWithUnit = _

  /**
   * Verifies that the mocks for the scroll size have been used in the
   * expected way.
   * @param expSizeHandler the expected size handler
   */
  def verify(expSizeHandler: UnitSizeHandler) {
    assertNotNull("X scroll size not queried", scrollWidth)
    assertNotNull("Y scroll size not queried", scrollHeight)
    EasyMock.verify(scrollWidth, scrollHeight)
    xAnswer.verify(expSizeHandler, container)
    yAnswer.verify(expSizeHandler, container)
  }

  /**
   * Overrides the inherited method to always return the mock container.
   */
  override def findContainer() = composite

  /**
   * @inheritdoc This implementation returns the mock scroll width object.
   */
  override def getPreferredScrollWidth = {
    if (scrollWidth == null) {
      xAnswer = new SizeAnswer(xScrollSize)
      scrollWidth = createScrollSizeMock(xAnswer, y = false)
    }
    scrollWidth
  }

  /**
   * @inheritdoc This implementation returns the mock scroll height object.
   */
  override def getPreferredScrollHeight = {
    if (scrollHeight == null) {
      yAnswer = new SizeAnswer(yScrollSize)
      scrollHeight = createScrollSizeMock(yAnswer, y = true)
    }
    scrollHeight
  }

  /**
   * Creates a mock for a ''NumberWithUnit'' which expects an invocation of its
   * toPixel() method handled by the passed in answer.
   * @param answer the answer
   * @param y the direction flag
   * @return the newly created mock object
   */
  private def createScrollSizeMock(answer: IAnswer[Int], y: Boolean): NumberWithUnit = {
    val result = EasyMock.createMock(classOf[NumberWithUnit])
    EasyMock.expect(result.toPixel(EasyMock.anyObject(classOf[UnitSizeHandler]),
      EasyMock.anyObject(), eqObj(y))).andAnswer(answer)
    EasyMock.replay(result)
    result
  }

  /**
   * Returns a mock ''Composite'' object wrapping the test container.
   * @return the mock ''Composite''
   */
  private def createComposite(): Composite = {
    val comp = EasyMock.createMock(classOf[Composite])
    EasyMock.expect(comp.getContainer).andReturn(container).anyTimes()
    EasyMock.replay(comp)
    comp
  }
}

/**
 * A specialized ''ScrollSizeSupport'' implementation which returns an
 * unsupported scroll size.
 */
trait ScrollSizeSupportUndefined extends ScrollSizeSupportTestImpl {
  this: FormBaseTag =>

  val xScrollSize = 0
  val yScrollSize = 0
}

/**
 * A specialized ''ScrollSizeSupport'' implementation which returns specific
 * values for the scroll size.
 */
trait ScrollSizeSupportSpecific extends ScrollSizeSupportTestImpl {
  this: FormBaseTag =>

  val xScrollSize = 150
  val yScrollSize = 100
}

/**
 * A specialized IAnswer implementation for testing whether the correct size
 * is extracted from a ''NumberWithUnit'' object.
 *
 * @param size the size to be returned by the answer
 */
private class SizeAnswer(size: Int) extends IAnswer[Int] {
  /** Stores the size handler passed to the NumberWithUnit. */
  private var sizeHandler: Any = _

  /** Stores the container passed to the NumberWithUnit. */
  private var container: Any = _

  def answer(): Int = {
    val args = EasyMock.getCurrentArguments
    sizeHandler = args(0)
    container = args(1)
    size
  }

  /**
   * Verifies that the expected data was passed to the NumberWithUnit instance.
   * @param expSizeHandler the expected ''UnitSizeHandler''
   * @param expContainer the expected container object
   */
  def verify(expSizeHandler: UnitSizeHandler, expContainer: Any) {
    assertSame("Wrong size handler", expSizeHandler, sizeHandler)
    assertSame("Wrong container", expContainer, container)
  }
}
