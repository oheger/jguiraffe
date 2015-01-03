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
package net.sf.jguiraffe.gui.platform.javafx.builder.components.tree

import org.apache.commons.configuration.tree.ConfigurationNode
import org.easymock.EasyMock
import org.junit.Test
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.EasyMockSugar

/**
 * Test class for ''ConfigNodeData''.
 */
class TestConfigNodeData extends JUnitSuite with EasyMockSugar {
  /**
   * Tests the string representation of a node object.
   */
  @Test def testToString() {
    val node = mock[ConfigurationNode]
    val NodeName = "MyTestNode"
    EasyMock.expect(node.getName).andReturn(NodeName).anyTimes()

    whenExecuting(node) {
      val nodeData = ConfigNodeData(node)
      assert(NodeName === nodeData.toString)
    }
  }
}
