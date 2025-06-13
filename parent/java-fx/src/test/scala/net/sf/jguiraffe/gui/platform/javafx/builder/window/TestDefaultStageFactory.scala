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
package net.sf.jguiraffe.gui.platform.javafx.builder.window

import org.junit.Test
import org.junit.Assert._
import org.scalatest.junit.JUnitSuite

/**
  * Test class for ''DefaultStageFactory''. This class tests some corner cases.
  */
class TestDefaultStageFactory extends JUnitSuite {
  /**
    * Tests whether multiple instances of ''DefaultStageFactory'' can be
    * created. This is a problem because the JavaFX application can be
    * launched only once.
    */
  @Test def testMultipleInstances(): Unit = {
    val provider = new StyleSheetProvider("")
    val factory1 = DefaultStageFactory(provider)
    val factory2 = DefaultStageFactory(provider)

    assertNotEquals("Same primary stage", factory1.createStage(), factory2.createStage())
  }
}
