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
package net.sf.jguiraffe.gui.platform.javafx.builder.utils

import java.util.concurrent.atomic.AtomicReference
import javafx.stage.{Modality, Stage, Window}

import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper._
import org.junit.Assert._
import org.junit.{Before, BeforeClass, Test}
import org.scalatestplus.junit.JUnitSuite
import org.scalatestplus.easymock.EasyMockSugar

object TestMessageOutputStageProvider {
  @BeforeClass def setUpOnce(): Unit = {
    initPlatform()
  }
}

/**
 * Test class for ''MessageOutputStageProvider''.
 */
class TestMessageOutputStageProvider extends JUnitSuite with EasyMockSugar {
  /** The test provider. */
  private var provider: MessageOutputStageProvider = _

  @Before def setUp(): Unit = {
    provider = new MessageOutputStageProvider {}
  }

  /**
   * Tests whether a stage can be created.
   */
  @Test def testCreateStage(): Unit = {
    val parent = mock[Window]
    whenExecuting(parent) {
      val refStage = new AtomicReference[Stage]
      JavaFxTestHelper.runInFxThread { () =>
        refStage set (provider createStage parent)
      }
      val stage = refStage.get()
      assertSame("Wrong owner", parent, stage.getOwner)
      assertEquals("Wrong modality", Modality.APPLICATION_MODAL, stage.getModality)
    }
  }
}
