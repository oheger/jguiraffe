/*
 * Copyright 2006-2021 The JGUIraffe Team.
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
import org.junit.{BeforeClass, Before, Test}
import org.junit.runner.RunWith
import org.powermock.api.easymock.PowerMock
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.scalatest.junit.JUnitSuite

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Label
import net.sf.jguiraffe.gui.builder.components.model.StaticTextData
import net.sf.jguiraffe.gui.platform.javafx.builder.event.ActionEventSource

object PMTestControlActionEventSource {
  @BeforeClass def setUpOnce(): Unit = {
    JavaFxTestHelper.initPlatform()
  }
}

/**
 * Test class for ''ControlActionEventSource''.
 */
@RunWith(classOf[PowerMockRunner])
@PrepareForTest(Array(classOf[Label]))
class PMTestControlActionEventSource extends JUnitSuite {
  /** The mock for the label. */
  private var label: Label = _

  /** The object to be tested. */
  private var handler: ActionEventSource = _

  @Before def setUp() {
    label = PowerMock.createMock(classOf[Label])
    handler = new JavaFxStaticTextHandler(label) with ControlActionEventSource[StaticTextData]
  }

  /**
   * Tests whether an action listener can be added.
   */
  @Test def testAddActionListener() {
    val listener = PowerMock.createMock(classOf[EventHandler[ActionEvent]])
    label.addEventHandler(ActionEvent.ACTION, listener)
    PowerMock.replayAll()
    handler addActionListener listener
    PowerMock.verifyAll()
  }

  /**
   * Tests whether an action listener can be removed.
   */
  @Test def testRemoveActionListener() {
    val listener = PowerMock.createMock(classOf[EventHandler[ActionEvent]])
    label.removeEventHandler(ActionEvent.ACTION, listener)
    PowerMock.replayAll()
    handler removeActionListener listener
    PowerMock.verifyAll()
  }
}
