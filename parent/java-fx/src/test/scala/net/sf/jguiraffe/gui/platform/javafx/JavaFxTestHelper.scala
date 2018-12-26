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
package net.sf.jguiraffe.gui.platform.javafx

import java.util.concurrent.{CountDownLatch, SynchronousQueue, TimeUnit}

import javafx.application.Platform
import javafx.beans.property.ReadOnlyProperty
import javafx.embed.swing.JFXPanel
import javafx.stage.Stage
import javafx.util.Callback
import javax.swing.SwingUtilities
import net.sf.jguiraffe.gui.builder.window.{Window, WindowWrapper}
import org.easymock.EasyMock
import org.junit.Assert._

import scala.language.implicitConversions

/**
 * An object providing some utility methods for unit tests for Java FX
 * functionality.
 */
object JavaFxTestHelper {
  /** Constant for the default timeout. */
  private final val Timeout = 5000

  /**
    * A trait combining a JGUIraffe window with a window wrapper. This is used
    * to create mock JavaFX windows.
    */
  trait WrappingWindow extends Window with WindowWrapper

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
    () => {
      f()
    }
  }

  /**
   * Executes the passed in ''Runnable'' in the Java FX thread and waits for
   * its execution (with a timeout). This method can be used to test Java FX
   * functionality which requires to be executed in the Java FX thread.
   * Because the call is blocking no further synchronization is needed.
   * @param r the runnable to be executed
   */
  def runInFxThread(r: Runnable) {
    val latch = new CountDownLatch(1)
    Platform.runLater(() => {
      r.run()
      latch.countDown()
    })
    await(latch)
  }

  /**
   * Executes the passed in function in the Java FX thread, waits for its
   * execution (with a timeout), and returns the result. This method can be
   * used instead of ''runInFxThread()'' if results are produced.
   * @param f the function to be executed
   * @tparam T the return type of the function
   * @return the value returned by the function
   */
  def invokeInFxThread[T](f: Unit => T): T = {
    val syncQueue = new SynchronousQueue[T]
    Platform runLater (() => {
      syncQueue put f()
    })
    val result = syncQueue.poll(Timeout, TimeUnit.MILLISECONDS)
    assertNotNull("No value retrieved", result)
    result
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

  /**
   * Reads the current value of a property in the JavaFX thread.
   * @tparam T the type of the property
   * @param prop the property to be read
   */
  def readProperty[T](prop: ReadOnlyProperty[T]): T = {
    val latch = new CountDownLatch(1)
    var value: T = null.asInstanceOf[T]
    runInFxThread { () =>
      value = prop.getValue
      latch.countDown()
    }
    await(latch)
    value
  }

  /**
   * Helper method for converting a function to a callback object.
   * Note that we do not use an implicit conversion here. This is due to the
   * fact that the compiler cannot infer the correct type arguments. Therefore,
   * it is easier to use a function call.
   * @param f the function to which the generated callback has to delegate
   * @tparam P the parameter type
   * @tparam R the return type
   * @return a ''Callback'' object executing the passed in function
   */
  def functionToCallback[P, R](f: P => R): Callback[P, R] = {
    param: P => f(param)
  }

  /**
    * Creates a mock for a JavaFX ''Window'' object that wraps a specific
    * ''Stage''. This is useful for tests that somehow have to access the
    * application's main window.
    *
    * @param mainWnd optional ''Stage'' to be wrapped
    * @return the mock for the JavaFX ''Window''
    */
  def createJavaFxWindowMock(mainWnd: Stage = EasyMock.createMock(classOf[Stage])):
  WrappingWindow = {
    val fxMainWnd = EasyMock.createMock(classOf[WrappingWindow])
    EasyMock.expect(fxMainWnd.getWrappedWindow).andReturn(mainWnd).anyTimes()
    EasyMock.replay(fxMainWnd)
    fxMainWnd
  }
}
