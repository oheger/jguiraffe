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
package net.sf.jguiraffe.gui.platform.javafx.common

import javafx.beans.property.{ObjectProperty, SimpleObjectProperty}
import javafx.scene.control.{Label, Tooltip}

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData
import net.sf.jguiraffe.gui.forms.ComponentStoreImpl
import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import org.apache.commons.jelly.{JellyContext, Tag}
import org.easymock.EasyMock
import org.junit.Assert._
import org.junit.{BeforeClass, Before, Test}
import org.scalatest.junit.JUnitSuite
import org.scalatest.easymock.EasyMockSugar

object TestToolTipCreationSupport {
  @BeforeClass def setUpOnce(): Unit = {
    JavaFxTestHelper.initPlatform()
  }
}

/**
 * Test class for ''ToolTipCreationSupport''.
 */
class TestToolTipCreationSupport extends JUnitSuite with EasyMockSugar {
  /** A string for a tool tip. */
  private val ToolTip = "This is a helpful tooltip."

  /** A mock for the tooltip factory. */
  private var factory: ToolTipFactory = _

  /** The object to be tested. */
  private var support: ToolTipCreationSupport = _

  @Before def setUp(): Unit = {
    factory = mock[ToolTipFactory]
    EasyMock.replay(factory)
    support = new ToolTipCreationSupport {
      override val toolTipFactory = factory
    }
  }

  /**
   * Creates and initializes a Jelly context which can be used for testing the
   * processing of tool tip requests.
   * @return the context
   */
  private def prepareContext(): JellyContext = {
    val context = new JellyContext
    val builderData = new ComponentBuilderData
    builderData put context
    builderData pushComponentStore new ComponentStoreImpl
    context
  }

  /**
   * Checks the tooltip creation request produced by a test case.
   * @param context the ''JellyContext''
   * @param property the expected property
   */
  private def checkRequest(context: JellyContext, property: ObjectProperty[Tooltip]) {
    val callBack = ToolTipCreationCallBack.getInstance(context, null)
    assertSame("Wrong factory", factory, callBack.toolTipFactory)
    assertEquals("Wrong number of requests", 1, callBack.requests.size)
    val request = callBack.requests.head
    assertEquals("Wrong property", property, request.prop)
    assertEquals("Wrong text", ToolTip, request.tip)
  }

  /**
   * Tests whether a tool tip creation request can be initiated if the Jelly
   * context and a property are specified.
   */
  @Test def testToolTipRequestForContextAndProperty(): Unit = {
    val property = new SimpleObjectProperty[Tooltip]
    val context = prepareContext()
    support.addCreateToolTipRequest(context, property, ToolTip)

    checkRequest(context, property)
  }

  /**
   * Tests whether a tool tip creation request can be initiated if the Jelly
   * context and a control are provided.
   */
  @Test def testToolTipRequestForContextAndControl(): Unit = {
    val control = new Label
    val context = prepareContext()
    support.addCreateToolTipRequest(context, control, ToolTip)

    checkRequest(context, control.tooltipProperty)
  }

  /**
   * Creates and initializes a tag which can be used for testing the processing
   * of requests.
   * @return the initialized tag
   */
  private def prepareTag(): Tag = {
    val tag = mock[Tag]
    EasyMock.expect(tag.getContext).andReturn(prepareContext()).anyTimes()
    EasyMock.replay(tag)
    tag
  }

  /**
   * Tests whether a tool tip creation request can be initiated if a tag and a
   * property are provided.
   */
  @Test def testToolTipRequestForTagAndProperty(): Unit = {
    val property = new SimpleObjectProperty[Tooltip]
    val tag = prepareTag()
    support.addCreateToolTipRequest(tag, property, ToolTip)

    checkRequest(tag.getContext, property)
  }

  /**
   * Tests whether a tool tip creation request can be initiated if a tag and a
   * control are provided.
   */
  @Test def testToolTipRequestForTagAndControl(): Unit = {
    val control = new Label
    val tag = prepareTag()
    support.addCreateToolTipRequest(tag, control, ToolTip)

    checkRequest(tag.getContext, control.tooltipProperty)
  }
}
