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
package net.sf.jguiraffe.gui.platform.javafx

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

import org.junit.Assert.fail

import javafx.embed.swing.JFXPanel
import javax.swing.SwingUtilities

/**
 * An object providing some utility methods for unit tests for Java FX
 * functionality.
 */
object JavaFxTestHelper {
  /** Constant for the default timeout. */
  private final val Timeout = 5000

  /**
   * Helper method for waiting for a latch using a timeout. If the latch is
   * not triggered within the timeout, this method causes the test to fail.
   * @param latch the latch to wait for
   */
  def await(latch: CountDownLatch) {
    if(!latch.await(Timeout, TimeUnit.MILLISECONDS)) {
      fail("Timeout when waiting for latch!")
    }
  }

  /**
   * A conversion method which wraps a function into a Runnable.
   * @param f the function
   * @return the Runnable which executes this function
   */
  implicit def toRunnable(f: () => Unit): Runnable = {
    new Runnable() {
      def run() {
        f()
      }
    }
  }

  /**
   * Initializes the Java FX platform. This method typically has to be invoked
   * by a unit test class before Java FX classes are used.
   */
  def initPlatform() {
    val latch = new CountDownLatch(1)
    SwingUtilities.invokeLater { () =>
      new JFXPanel
      latch.countDown()
    }
    await(latch)
  }
}
