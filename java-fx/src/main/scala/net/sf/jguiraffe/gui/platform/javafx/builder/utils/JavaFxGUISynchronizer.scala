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

import javafx.application.Platform
import net.sf.jguiraffe.gui.builder.utils.GUISynchronizer

/**
 * The Java FX-specific implementation of the ''GUISynchronizer'' interface.
 *
 * This implementation mainly delegates to methods of the Java FX ''Platform''
 * class. Functionality missing in this class, but required by the interface
 * has to be implemented manually, e.g. synchronous executions on the Java FX
 * thread.
 */
class JavaFxGUISynchronizer extends GUISynchronizer {
  /**
   * @inheritdoc This implementation directly delegates to the ''Platform''
   * class.
   */
  def asyncInvoke(r: Runnable) {
    Platform runLater r
  }

  /**
   * @inheritdoc This implementation has to simulate synchronous invocations
   * because they are not supported per default by Java FX. Therefore, an
   * asynchronous invocation is performed, and synchronization is used to
   * wait for its completion.
   */
  def syncInvoke(r: Runnable) {
    JavaFxGUISynchronizer syncJavaFxInvocation (r.run _)
  }

  /**
   * @inheritdoc This implementation uses the Java FX ''Platform'' class to
   * find out whether we are on the Java FX application thread.
   */
  def isEventDispatchThread: Boolean = JavaFxGUISynchronizer.isJavaFxThread
}

/**
 * The companion object for ''JavaFxGUISynchronizer''.
 */
object JavaFxGUISynchronizer {
  /**
   * Returns a flag whether the current thread is the Java FX application
   * thread.
   * @return '''true''' if the current thread is the Java FX application
   * thread, '''false''' otherwise
   */
  def isJavaFxThread = Platform.isFxApplicationThread

  /**
   * Invokes the specified function on the Java FX thread and waits for its
   * completion. This method can be called from any thread. It simulates a
   * synchronous invocation on the Java FX thread by performing an asynchronous
   * one and waiting for its completion.
   * @param f the function to be invoked
   */
  def syncJavaFxInvocation(f: () => Unit) {
    if (isJavaFxThread) {
      f() // invoke directly
    } else {
      invokeAndWait(f)
    }
  }

  /**
   * Simulates a synchronous invocation of the given function.
   * @param f the function to be invoked
   */
  private def invokeAndWait(f: () => Unit) {
    val latch = new CountDownLatch(1)
    val r = new Runnable {
      def run() {
        try {
          f()
        } finally {
          latch.countDown()
        }
      }
    }

    Platform runLater r
    latch.await()
  }
}
