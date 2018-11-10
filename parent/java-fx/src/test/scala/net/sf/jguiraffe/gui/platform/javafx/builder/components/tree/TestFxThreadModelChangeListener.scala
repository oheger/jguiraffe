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
package net.sf.jguiraffe.gui.platform.javafx.builder.components.tree

import java.util.concurrent.CountDownLatch

import org.apache.commons.configuration.tree.ConfigurationNode
import org.easymock.EasyMock
import org.easymock.IAnswer
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import org.scalatest.junit.JUnitSuite
import org.scalatest.easymock.EasyMockSugar

import javafx.application.Platform
import net.sf.jguiraffe.gui.builder.components.model.TreeModelChangeListener
import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper

/**
 * Companion object for ''TestFxThreadModelChangeListener''.
 */
object TestFxThreadModelChangeListener {
  @BeforeClass def setUpOnce() {
    JavaFxTestHelper.initPlatform()
  }
}

/**
 * Test class for ''FxThreadModelChangeListener''.
 */
class TestFxThreadModelChangeListener extends JUnitSuite with EasyMockSugar {
  /**
   * Tests whether a change notification is correctly propagated.
   */
  @Test def testEventPropagation() {
    val wrappedListener = mock[TreeModelChangeListener]
    val node = mock[ConfigurationNode]
    val latch = new CountDownLatch(1)
    wrappedListener.treeModelChanged(node)
    EasyMock.expectLastCall().andAnswer(new IAnswer[Object] {
      def answer(): Object = {
        assertTrue("Not in FX thread", Platform.isFxApplicationThread)
        latch.countDown()
        null
      }
    })

    whenExecuting(wrappedListener) {
      val listener = new FxThreadModelChangeListener(wrappedListener)
      listener treeModelChanged node
      JavaFxTestHelper.await(latch)
    }
  }
}
