/**
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
package net.sf.jguiraffe.gui.platform.javafx.builder.window

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.atomic.AtomicInteger

import org.apache.commons.logging.LogFactory

import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
import javafx.stage.Stage
import net.sf.jguiraffe.gui.platform.javafx.builder.utils.JavaFxGUISynchronizer

/**
 * A class for creating ''Stage'' objects.
 *
 * This internal helper class implements the integration into the Java FX
 * application framework. It fires up a simple application just to obtain
 * the primary stage. This stage is stored and returned as the first stage
 * object queried by a client. Further invocations of the factory result in
 * newly created ''Stage'' objects.
 *
 * @param primaryStage the primary stage of this application
 */
private class StageFactory(primaryStage: Stage) {
  /** The logger. */
  private final val log = LogFactory.getLog(getClass)

  /**
   * A counter for determining whether the primary stage is queried or a new
   * one has to be created.
   */
  private final val stageCount = new AtomicInteger

  log.info("Instance of StageFactory created.")

  /**
   * Creates a new ''Stage'' object. This method is called for each stage
   * required by this application. On first invocation it returns the
   * primary stage; for succeeding calls new ''Stage'' objects are created.
   * @return the newly created ''Stage'' object
   */
  def createStage(): Stage = {
    if (stageCount.getAndIncrement() == 0) primaryStage
    else {
      JavaFxGUISynchronizer.syncJavaFxInvocation {() =>
        val stage = new Stage
        StageFactory.initScene(stage)
        StageFactory.StageQueue.add(stage)
      }
      StageFactory.StageQueue.take()
    }
  }
}

/**
 * The companion object of ''StageFactory''.
 */
private object StageFactory {
  /**
   * A queue for passing the primary stage from the setup application to the
   * thread creating the ''StageFactory''.
   */
  private val StageQueue = new ArrayBlockingQueue[Stage](3)

  /**
   * Creates a new ''StageFactory'' instance. This method must be called
   * exactly once. It launches a fake Java FX application and retrieves the
   * primary stage. With this primary stage a new ''StageFactory'' instance is
   * created and returned.
   * @return the newly created ''StageFactory'' instance
   */
  def apply(): StageFactory = {
    val setupThread = new Thread {
      override def run() {
        Application.launch(classOf[SetupApplication])
      }
    }
    setupThread.start()

    val primaryStage = StageQueue.take()
    new StageFactory(primaryStage)
  }

  /**
   * Initializes the Scene object of the specified stage.
   * @param stage the stage
   */
  private def initScene(stage: Stage) {
    stage.setScene(new Scene(new Group))
  }

  /**
   * A specialized Java FX application whose job is to obtain this application's
   * primary stage. An instance of this class is launched in a background
   * thread. When its ''start()'' method gets invoked it just passes the
   * provided primary stage to the ''StageFactory'' object by using a blocking
   * queue.
   */
  class SetupApplication extends Application {
    override def start(primaryStage: Stage) {
      LogFactory.getLog(classOf[StageFactory])
        .info("Java FX setup application starting up.")
      initScene(primaryStage)
      StageQueue.add(primaryStage)
    }
  }
}
