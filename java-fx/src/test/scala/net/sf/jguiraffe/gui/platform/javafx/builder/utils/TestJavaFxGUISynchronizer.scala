/*
 * Copyright 2006-2013 The JGUIraffe Team.
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

import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.scalatest.junit.JUnitSuite

import javafx.application.Platform
import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper.await
import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper.initPlatform
import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper.toRunnable

/**
 * Test class for ''JavaFxGUISynchronizer''.
 */
class TestJavaFxGUISynchronizer extends JUnitSuite {
  /** The synchronizer to be tested. */
  private var sync: JavaFxGUISynchronizer = _

  @Before def setUp() {
    sync = new JavaFxGUISynchronizer
  }

  /**
   * Tests isEventDispatchThread() if not called from the Java FX thread.
   */
  @Test def testIsEventDispatchThreadFalse() {
    assertFalse("Wrong result", sync.isEventDispatchThread)
  }

  /**
   * Tests isEventDispatchThread() if invoked from the Java FX thread.
   */
  @Test def testIsEventDispatchThreadTrue() {
    val result = new AtomicBoolean
    val latch = new CountDownLatch(1)
    Platform.runLater { () =>
      result.set(sync.isEventDispatchThread)
      latch.countDown()
    }
    await(latch)
    assertTrue("Wrong result", result.get)
  }

  /**
   * Tests an asynchronous invocation on the event dispatch thread.
   */
  @Test def testAsyncInvoke() {
    val invokeCount = new AtomicInteger
    val latch = new CountDownLatch(1)
    sync asyncInvoke { () =>
      invokeCount.incrementAndGet()
      assertTrue("Not in Java FX thread", Platform.isFxApplicationThread)
      latch.countDown()
    }
    await(latch)
    assertEquals("Wrong invocation count", 1, invokeCount.get)
  }

  /**
   * Tests a synchronous invocation if we are already on the Java FX thread.
   */
  @Test def testSyncInvokeFromFxThread() {
    val invokeCount = new AtomicInteger
    val latch = new CountDownLatch(1)
    Platform runLater { () =>
      sync syncInvoke { () =>
        invokeCount.incrementAndGet()
        assertTrue("Not in Java FX thread", Platform.isFxApplicationThread)
      }
      latch.countDown()
    }
    await(latch)
    assertEquals("Wrong invocation count", 1, invokeCount.get)
  }

  /**
   * Tests a synchronous invocation from another thread.
   */
  @Test def testSyncInvokeFromOtherThread() {
    val invokeCount = new AtomicInteger
    sync syncInvoke { () =>
      invokeCount.incrementAndGet()
      assertTrue("Not in Java FX thread", Platform.isFxApplicationThread)
    }
    assertEquals("Wrong invocation count", 1, invokeCount.get)
  }

  /**
   * Tests whether exceptions are handled on synchronous invocations.
   */
  @Test def testSyncInvokeWithException() {
    val invokeCount = new AtomicInteger
    sync syncInvoke { () =>
      invokeCount.incrementAndGet()
      throw new RuntimeException("Test exception!")
    }
    assertEquals("Wrong invocation count", 1, invokeCount.get)
  }
}

object TestJavaFxGUISynchronizer {
  @BeforeClass def setUpBeforeClass() {
    initPlatform
  }
}
