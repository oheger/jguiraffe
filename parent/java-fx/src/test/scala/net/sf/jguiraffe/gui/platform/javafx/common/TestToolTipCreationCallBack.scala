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
package net.sf.jguiraffe.gui.platform.javafx.common

import java.util.concurrent.CountDownLatch
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.{Control, TextField, Tooltip}

import net.sf.jguiraffe.gui.builder.components.tags.LabelTag
import net.sf.jguiraffe.gui.builder.components.{ComponentBuilderCallBack, ComponentBuilderData}
import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import org.apache.commons.jelly.JellyContext
import org.junit.Assert.{assertEquals, assertSame}
import org.junit.{BeforeClass, Test}
import org.scalatestplus.junit.JUnitSuite
import org.scalatestplus.easymock.EasyMockSugar

/**
 * Test class for ''ToolTipCreationCallBack''.
 */
class TestToolTipCreationCallBack extends JUnitSuite with EasyMockSugar {
  /**
   * Tests whether getInstance() creates a new instance which is also registered
   * as call back.
   */
  @Test def testGetInstanceFirstAccess(): Unit = {
    val factory = mock[ToolTipFactory]
    val context = new JellyContext
    val tag = new LabelTag
    tag setContext context
    val data = new ComponentBuilderDataCallBackAccess
    data put context
    val callBack = ToolTipCreationCallBack.getInstance(tag, factory)
    assertSame("Wrong tool tip factory", factory, callBack.toolTipFactory)
    assertEquals("Wrong number of call backs", 1, data.addedCallBacks.size)
    assertSame("Not added as call back", callBack, data.addedCallBacks.head)
  }

  /**
   * Tests whether a single instance is assigned with a Jelly context.
   */
  @Test def testGetInstanceCached(): Unit = {
    val factory = mock[ToolTipFactory]
    val context = new JellyContext
    val tag = new LabelTag
    tag setContext context
    val data = new ComponentBuilderDataCallBackAccess
    data put context
    val callBack = ToolTipCreationCallBack.getInstance(tag, factory)
    assertSame("Multiple instances", callBack,
      ToolTipCreationCallBack.getInstance(tag, factory))
    assertEquals("Wrong number of call backs", 1, data.addedCallBacks.size)
  }

  /**
   * Tests whether requested tool tips are actually created.
   */
  @Test def testProcessToolTipRequests(): Unit = {
    val TipPrefix = "tip"

    def testTip(comp: Control, expIdx: Int): Unit = {
      assertEquals("Wrong tool tip text", TipPrefix + expIdx,
        comp.getTooltip().getText())
    }

    val latch = new CountDownLatch(1)
    val callBack = new ToolTipCreationCallBack(new DefaultToolTipFactory) {
      override def createAndAssignToolTips(): Unit = {
        super.createAndAssignToolTips()
        latch.countDown()
      }
    }

    val comp1 = new TextField
    val comp2 = new TextField
    val prop = new SimpleObjectProperty[Tooltip]
    callBack.addCreateToolTipRequest(comp1, "tip1")
    callBack.addCreateToolTipRequest(comp2, "tip2")
    callBack.addCreateToolTipRequest(prop, "tip3")
    callBack.callBack(new ComponentBuilderData, this)

    JavaFxTestHelper.await(latch)
    testTip(comp1, 1)
    testTip(comp2, 2)
    assertEquals("Wrong tip in property", "tip3", prop.get.getText)
  }
}

object TestToolTipCreationCallBack {
  @BeforeClass def setUpBeforeClass(): Unit = {
    JavaFxTestHelper.initPlatform()
  }
}

/**
 * A specialized ''ComponentBuilderData'' implementation providing access to
 * the call backs added to it.
 */
class ComponentBuilderDataCallBackAccess extends ComponentBuilderData {
  /** A list with call backs added to this instance. */
  var addedCallBacks = List.empty[ComponentBuilderCallBack]

  /**
   * @inheritdoc This implementation also adds the passed in call back to the
   * accessible call back list.
   */
  override def addCallBack(callBack: ComponentBuilderCallBack, param: Any): Unit = {
    addedCallBacks = callBack :: addedCallBacks
  }
}
