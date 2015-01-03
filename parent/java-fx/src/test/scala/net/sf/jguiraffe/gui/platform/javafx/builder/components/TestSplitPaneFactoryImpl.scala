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

import javafx.beans.property.{DoubleProperty, ReadOnlyDoubleProperty, SimpleDoubleProperty}
import javafx.geometry.Orientation
import javafx.scene.control.{Label, SplitPane, TextField}
import javafx.scene.layout.Pane

import net.sf.jguiraffe.gui.builder.components.tags.SplitterTag
import net.sf.jguiraffe.gui.builder.components.{FormBuilderException, Orientation => JGOrientation}
import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import net.sf.jguiraffe.gui.platform.javafx.layout.ContainerWrapper
import org.easymock.EasyMock
import org.junit.Assert.{assertEquals, assertSame, assertTrue}
import org.junit.{BeforeClass, Test}
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.EasyMockSugar

import scala.collection.mutable

/**
 * Companion object for ''TestSplitPaneFactoryImpl''.
 */
object TestSplitPaneFactoryImpl {
  @BeforeClass def setUpOnce() {
    JavaFxTestHelper.initPlatform()
  }
}

/**
 * Test class for ''SplitPaneFactoryImpl''.
 */
class TestSplitPaneFactoryImpl extends JUnitSuite with EasyMockSugar {
  /**
   * Tests whether the correct property is returned by the default function
   * for extracting the size property if orientation is vertical.
   */
  @Test def testDefaultSizePropertyFunctionVertical() {
    val split = new SplitPane
    split setOrientation Orientation.VERTICAL
    val factory = new SplitPaneFactoryImpl
    assertSame("Wrong property", split.heightProperty, factory.funcSizeProp(split))
  }

  /**
   * Tests whether the correct property is returned by the default function
   * for extracting the size property if orientation is horizontal.
   */
  @Test def testDefaultSizePropertyFunctionHorizontal() {
    val split = new SplitPane
    split setOrientation Orientation.HORIZONTAL
    val factory = new SplitPaneFactoryImpl
    assertSame("Wrong property", split.widthProperty, factory.funcSizeProp(split))
  }

  /**
   * Tests whether the correct property is returned by the default function for
   * extracting the position property.
   */
  @Test def testDefaultPosPropertyFunction() {
    val split = new SplitPane
    split.getItems.addAll(new TextField, new TextField)
    val factory = new SplitPaneFactoryImpl
    assertSame("Wrong property", split.getDividers.get(0).positionProperty,
      factory.funcPosProp(split))
  }

  /**
   * Creates a test function for extracting the position property from a split
   * pane.
   * @param panes the collection for storing the passed in split pane
   * @param prop the property to be returned by the function
   * @return the test function
   */
  private def createPosFunction(panes: mutable.Set[SplitPane],
    prop: DoubleProperty): SplitPane => DoubleProperty = {
    p =>
      panes += p
      prop
  }

  /**
   * Creates a test function for extracting the size property from a split
   * pane.
   * @param panes the collection for storing the passed in split pane
   * @param expOrient the expected orientation of the split pane
   * @param prop the property to be returned by the function
   * @return the test function
   */
  private def createSizeFunction(panes: mutable.Set[SplitPane], expOrient: Orientation,
    prop: ReadOnlyDoubleProperty): SplitPane => ReadOnlyDoubleProperty = {
    p =>
      panes += p
      assertEquals("Wrong orientation", expOrient, p.getOrientation)
      prop
  }

  /**
   * Creates a test property.
   * @return the test property
   */
  private def createProperty(): DoubleProperty = new SimpleDoubleProperty

  /**
   * Creates a tag for defining a split pane.
   * @param orient the orientation of the split pane
   * @return the tag
   */
  private def createTag(orient: JGOrientation): SplitterTag = {
    val comp1 = new Label("Test label")
    createTag(orient, comp1)
  }

  /**
   * Creates a tag for defining a split pane allowing the first split
   * component to be defined.
   * @param orient the orientation of the split pane
   * @param comp1 the first component of the split pane
   * @return the tag
   */
  private def createTag(orient: JGOrientation, comp1: Object): SplitterTag = {
    val comp2 = new TextField
    new SplitterTag {
      override val getSplitterOrientation = orient
      override val getFirstComponent = comp1
      override val getSecondComponent = comp2
    }
  }

  /**
   * Tests whether the correct items have been added to the split pane.
   */
  @Test def testSplitPaneItems() {
    val tag = createTag(JGOrientation.HORIZONTAL)
    val factory = new SplitPaneFactoryImpl
    val split = factory.createSplitPane(tag)
    assertEquals("Wrong number of items", 2, split.getItems.size)
    assertTrue("First component not found",
      split.getItems.contains(tag.getFirstComponent))
    assertTrue("2nd component not found",
      split.getItems.contains(tag.getSecondComponent))
  }

  /**
   * Tests whether the items of a split pane are correctly initialized if one
   * component is a container.
   */
  @Test def testSplitPaneItemsContainer() {
    val container = mock[ContainerWrapper]
    val pane = new Pane
    EasyMock.expect(container.createContainer()).andReturn(pane)
    val tag = createTag(JGOrientation.VERTICAL, container)

    whenExecuting(container) {
      val factory = new SplitPaneFactoryImpl
      val split = factory.createSplitPane(tag)
      assertSame("Wrong container component", pane, split.getItems.get(0))
    }
  }

  /**
   * Tests whether an unsupported split pane component is detected.
   */
  @Test(expected = classOf[FormBuilderException])
  def testSplitPaneItemsUnsupported() {
    val tag = createTag(JGOrientation.HORIZONTAL, "unsupported")
    val factory = new SplitPaneFactoryImpl
    factory.createSplitPane(tag)
  }

  /**
   * Helper method for testing whether a split pane has the correct orientation.
   * @param expOrient the expected orientation of the split pane
   * @param tagOrient the orientation of the tag
   */
  private def checkSplitPaneOrientation(expOrient: Orientation,
    tagOrient: JGOrientation) {
    val splitPanes = mutable.Set.empty[SplitPane]
    val factory = new SplitPaneFactoryImpl(
      funcSizeProp = createSizeFunction(splitPanes, expOrient, createProperty()))
    val split = factory.createSplitPane(createTag(tagOrient))
    assertEquals("Wrong orientation", expOrient, split.getOrientation)
  }

  /**
   * Tests whether the split pane's orientation is correctly initialized if it
   * is vertical.
   */
  @Test def testSplitPaneOrientationVertical() {
    checkSplitPaneOrientation(Orientation.VERTICAL, JGOrientation.VERTICAL)
  }

  /**
   * Tests whether the split pane's orientation is correctly initialized if it
   * is horizontal.
   */
  @Test def testSplitPaneOrientationHorizontal() {
    checkSplitPaneOrientation(Orientation.HORIZONTAL, JGOrientation.HORIZONTAL)
  }

  /**
   * Tests whether the correct split pane object is passed to the property
   * extraction functions.
   */
  @Test def testSplitPanePassedToPropertyFunctions() {
    val splitPanes = mutable.Set.empty[SplitPane]
    val factory = new SplitPaneFactoryImpl(
      funcSizeProp = createSizeFunction(splitPanes, Orientation.VERTICAL, createProperty()),
      funcPosProp = createPosFunction(splitPanes, createProperty()))
    splitPanes += factory.createSplitPane(createTag(JGOrientation.VERTICAL))
    assertEquals("Multiple split panes", 1, splitPanes.size)
  }

  /**
   * Tests whether a size handler is installed which can set the initial
   * position.
   */
  @Test def testSizeHandlerPosition() {
    val splitPanes = mutable.Set.empty[SplitPane]
    val propPos = createProperty()
    val propSize = createProperty()
    val tag = createTag(JGOrientation.VERTICAL)
    tag setPos 100
    val factory = new SplitPaneFactoryImpl(
      funcSizeProp = createSizeFunction(splitPanes, Orientation.VERTICAL, propSize),
      funcPosProp = createPosFunction(splitPanes, propPos))

    factory.createSplitPane(tag)
    propSize set 1000
    val pos = JavaFxTestHelper.readProperty(propPos)
    assertEquals("Wrong value", .1, pos.doubleValue, .001)
  }

  /**
   * Tests whether a size handler is installed with the correct resize weight.
   */
  @Test def testSizeHandlerResizeWeight() {
    val splitPanes = mutable.Set.empty[SplitPane]
    val propPos = createProperty()
    val propSize = createProperty()
    val tag = createTag(JGOrientation.VERTICAL)
    tag setResizeWeight 1
    val factory = new SplitPaneFactoryImpl(
      funcSizeProp = createSizeFunction(splitPanes, Orientation.VERTICAL, propSize),
      funcPosProp = createPosFunction(splitPanes, propPos))

    factory.createSplitPane(tag)
    propPos set .5
    propSize set 100
    propSize set 200
    val pos = JavaFxTestHelper.readProperty(propPos)
    assertEquals("Wrong value", .75, pos.doubleValue, .001)
  }
}
