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
package net.sf.jguiraffe.gui.platform.javafx.builder.components.widget

import net.sf.jguiraffe.gui.platform.javafx.builder.components.WidgetHandlerAdapter
import org.junit.Assert._
import org.junit.{Before, Test}

/**
 * Test class for ''NoToolTipSupport''.
 */
class TestNoToolTipSupport {
  /** The handler to be tested. */
  private var handler: NoToolTipSupport = _

  @Before def setUp(): Unit = {
    handler = new WidgetHandlerAdapter with NoToolTipSupport
  }

  /**
   * Tests whether the tool tip can be queried. Tool tips are not supported,
   * so result is always null.
   */
  @Test def testGetToolTip(): Unit = {
    assertNull("Got a tool tip", handler.getToolTip)
  }

  /**
   * Tests setToolTip(). We can only test that nothing is changed.
   */
  @Test def testSetToolTip(): Unit = {
    handler setToolTip "Some tool tip"
  }
}
