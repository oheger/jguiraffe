/*
 * Copyright 2006-2015 The JGUIraffe Team.
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
import java.util.concurrent.atomic.{AtomicReference, AtomicInteger}

import org.apache.commons.logging.LogFactory

import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
import javafx.stage.Stage
import net.sf.jguiraffe.gui.platform.javafx.builder.utils.JavaFxGUISynchronizer

/**
  * The default ''StageFactory'' implementation.
  *
  * This class implements the creation of JavaFX ''Stage'' objects which can be
  * assigned to JGUIraffe windows. It integrates window creation with the
  * Java FX application framework: It fires up a simple application just to
  * obtain the primary stage. This stage is stored and returned as the first
  * stage object queried by a client. Further invocations of the factory result
  * in newly created ''Stage'' objects.
  *
  * New instances can be created using the ''apply()'' method of the companion
  * object. Here the dummy JavaFX application is started, and the primary stage
  * is obtained. Alternatively, the primary stage can be obtained using a
  * custom approach. Then instances can be created using the constructor and
  * passing in the primary stage.
  *
  * @param primaryStage an option for the primary stage of this application
  * @since 1.3.1
  */
class DefaultStageFactory(primaryStage: Option[Stage]) extends StageFactory {
  /**
   * A counter for determining whether the primary stage is queried or a new
   * one has to be created.
   */
  private final val stageCount = new AtomicInteger

  DefaultStageFactory.log.info("Instance of StageFactory created.")

  /**
   * Creates a new ''Stage'' object. This method is called for each stage
   * required by this application. On first invocation it returns the
   * primary stage; for succeeding calls new ''Stage'' objects are created.
   * @return the newly created ''Stage'' object
   */
  def createStage(): Stage = {
    if (stageCount.getAndIncrement == 0 && primaryStage.isDefined) primaryStage.get
    else {
      JavaFxGUISynchronizer.syncJavaFxInvocation {() =>
        DefaultStageFactory.StageQueue.add(Some(createSubStage()))
      }
      DefaultStageFactory.StageQueue.take().get
    }
  }

  /**
    * Creates another ''Stage''. This method is called for further invocations
    * of ''createStage()'' after the primary stage has been returned. It can be
    * overridden by derived classes to adapt the stage creation process. This
    * default implementation creates a new ''Stage'' object via its constructor
    * and sets its scene to a new ''Scene'' object created by
    * ''createScene()''.
    * @return the new ''Stage''
    */
  protected def createSubStage(): Stage = {
    val stage = new Stage
    DefaultStageFactory.initScene(stage)
    stage
  }
}

/**
 * The companion object of ''StageFactory''.
 */
object DefaultStageFactory {
  /** The logger. */
  private final val log = LogFactory.getLog(getClass)

  /**
   * A queue for passing the primary stage from the setup application to the
   * thread creating the ''StageFactory''.
   */
  private val StageQueue = new ArrayBlockingQueue[Option[Stage]](3)

  /**
   * Stores the ''StyleSheetProvider''. This field is accessed from multiple
   * threads, so it has to be thread-safe.
   */
  private val refStyleSheetProvider = new AtomicReference[StyleSheetProvider]

  /**
   * Creates a new ''StageFactory'' instance. This method must be called
   * exactly once. It launches a fake Java FX application and retrieves the
   * primary stage. With this primary stage a new ''StageFactory'' instance is
   * created and returned.
   * @param styleSheetProvider the ''StyleSheetProvider''; all style sheets
   *                           defined by this object are added to newly
   *                           created ''Scene'' objects
   * @return the newly created ''StageFactory'' instance
   */
  def apply(styleSheetProvider: StyleSheetProvider): DefaultStageFactory = {
    initStyleSheetProvider(styleSheetProvider)
    new DefaultStageFactory(createPrimaryStage())
  }

  /**
    * Creates the primary ''Stage'' for this application. This is done by
    * starting a dummy JavaFX application and obtaining the stage from it.
    * This may fail if the application has already been started; in this
    * case, result is ''None''.
    * @return an option for the new primary stage
    */
  def createPrimaryStage(): Option[Stage] = {
    val setupThread = new Thread {
      override def run() {
        try {
          Application.launch(classOf[SetupApplication])
        } catch {
          case e: IllegalStateException =>
            log.warn("Multiple instances of DefaultStageFactory created!")
            StageQueue.add(None)
        }
      }
    }
    setupThread.start()

    StageQueue.take()
  }

  /**
    * Initializes the provider for stylesheets. This method is called by
    * ''apply()''. If a ''DefaultStageFactory'' is created using a different
    * approach, it has to be invoked manually.
    * @param styleSheetProvider the style sheet provider
    */
  def initStyleSheetProvider(styleSheetProvider: StyleSheetProvider): Unit = {
    refStyleSheetProvider set styleSheetProvider
  }

  /**
   * Initializes the Scene object of the specified stage.
   * @param stage the stage
   */
  private def initScene(stage: Stage) {
    stage.setScene(createScene())
  }

  /**
   * Creates a new ''Scene'' object and initializes it. This method also adds
   * all style sheets defined by the associated ''StyleSheetProvider'' to the
   * new scene.
   * @return the new ''Scene''
   */
  def createScene(): Scene = {
    val scene = new Scene(new Group)
    styleSheetURLs foreach scene.getStylesheets.add
    scene
  }

  /**
    * Returns a set with the style sheet URLs obtained from the
    * ''StyleSheetProvider''.
    * @return a set with the global style sheet URLs
    */
  def styleSheetURLs: Set[String] =
    refStyleSheetProvider.get.styleSheetURLs

  /**
   * A specialized Java FX application whose job is to obtain this application's
   * primary stage. An instance of this class is launched in a background
   * thread. When its ''start()'' method gets invoked it just passes the
   * provided primary stage to the ''StageFactory'' object by using a blocking
   * queue.
   */
  class SetupApplication extends Application {
    override def start(primaryStage: Stage) {
      LogFactory.getLog(classOf[DefaultStageFactory])
        .info("Java FX setup application starting up.")
      initScene(primaryStage)
      StageQueue.add(Some(primaryStage))
    }
  }
}
