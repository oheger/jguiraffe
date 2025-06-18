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
package net.sf.jguiraffe.gui.platform.javafx.builder.components

import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import org.junit.Assert.assertEquals
import org.junit.{BeforeClass, Before, Test}
import org.scalatestplus.junit.JUnitSuite

import javafx.scene.control.Tab
import javafx.scene.control.TabPane

object TestJavaFxTabPaneHandler {
  @BeforeClass def setUpOnce(): Unit = {
    JavaFxTestHelper.initPlatform()
  }
}

/**
 * Test class for ''JavaFxTabPaneHandler''.
 */
class TestJavaFxTabPaneHandler extends JUnitSuite {
  /** The tab pane. */
  private var tabPane: TabPane = _

  /** The handler to be tested. */
  private var handler: JavaFxTabPaneHandler = _

  @Before def setUp(): Unit = {
    def createTab(idx: Int): Tab = {
      val tab = new Tab
      tab setText ("Tab" + idx)
      tab
    }

    tabPane = new TabPane
    tabPane.getTabs().addAll(createTab(1), createTab(2), createTab(3))
    handler = new JavaFxTabPaneHandler(tabPane)
  }

  /**
   * Tests whether the correct data type is returned.
   */
  @Test def testGetType(): Unit = {
    assert(classOf[Integer] === handler.getType)
  }

  /**
   * Tests whether the handler's data can be set.
   */
  @Test def testSetData(): Unit = {
    handler setData 1
    assertEquals("Wrong selected index", 1,
      tabPane.getSelectionModel.getSelectedIndex)
  }

  /**
   * Tests setData() with null input.
   */
  @Test def testSetDataNull(): Unit = {
    tabPane.getSelectionModel.select(2)
    handler setData null
    assertEquals("Selected index was changed", 2,
      tabPane.getSelectionModel.getSelectedIndex)
  }

  /**
   * Tests whether the handler's data can be queried.
   */
  @Test def testGetData(): Unit = {
    tabPane.getSelectionModel.select(1)
    assertEquals("Wrong selected index", 1, handler.getData.intValue())
  }

  /**
   * Tests whether the correct property for change listener support is
   * returned.
   */
  @Test def testObservableValue(): Unit = {
    assertEquals("Wrong property", tabPane.getSelectionModel.selectedIndexProperty,
      handler.observableValue)
  }
}
