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
package net.sf.jguiraffe.gui.platform.javafx.builder.window

import javafx.scene.control.MenuBar
import javafx.stage.Modality
import javafx.stage.Stage

import net.sf.jguiraffe.gui.builder.window.Window
import net.sf.jguiraffe.gui.builder.window.WindowBuilderData
import net.sf.jguiraffe.gui.builder.window.WindowData
import net.sf.jguiraffe.gui.builder.window.WindowManager
import net.sf.jguiraffe.gui.platform.javafx.common.{ComponentUtils, ImageWrapper}
import net.sf.jguiraffe.gui.platform.javafx.layout.JavaFxUnitSizeHandler

/**
 * The Java FX-based implementation of the ''WindowManager'' interface.
 *
 * This class creates ''Stage'' instances wrapped by Java FX-specific
 * implementations of the ''Window'' interface. Stages can be used for all
 * types of windows supported by the JGUIraffe library. Note that internal
 * frames are not really supported; they are simulated by regular stages which
 * are parents of the top-level stage.
 *
 * Because CSS style sheets are managed by ''Scene'' objects and these scenes
 * are created together with stages, this class is also responsible for adding
 * custom CSS style sheet URLs. For this purpose, an instance of
 * [[StyleSheetProvider]] has to be passed to the constructor. All style sheet
 * URLs defined by this instance are added to all newly created scenes.
 *
 * @param styleSheetProvider the ''StyleSheetProvider''
 * @param factory the ''StageFactory'' to be used
 */
class JavaFxWindowManager(val styleSheetProvider: StyleSheetProvider, factory: StageFactory)
  extends WindowManager {
  /**
    * Creates a new instance of ''JavaFxWindowManager'' with the given style
    * sheet provider. A default stage factory is used.
    * @param provider the ''StyleSheetProvider''
    */
  def this(provider: StyleSheetProvider) = this(provider, DefaultStageFactory(provider))

  /**
    * Returns the factory for creating new stages.
    * @return the ''StageFactory''
    * @since 1.3.1
    */
  def stageFactory: StageFactory = factory

  /**
   * @inheritdoc This implementation creates a normal ''Stage'' object with no
   * modality. The stage's parent window is determined from the passed in
   * ''WindowBuilderData'' object; if defined, the newly created window is a
   * child window, otherwise a top-level window.
   */
  def createFrame(builderData: WindowBuilderData, data: WindowData,
    wnd: Window): Window = {
    if (wnd == null) {
      createWindow(builderData, data)
    } else {
      initWindowProperties(data, wnd)
      wnd
    }
  }

  /**
   * @inheritdoc This implementation works like ''createFrame()'', but the
   * stage's modality is set correspondingly to the ''modal'' argument.
   */
  def createDialog(builderData: WindowBuilderData, data: WindowData,
    modal: Boolean, wnd: Window): Window = {
    val resultWnd = createFrame(builderData, data, wnd)
    if (modal && wnd == null) {
      extractStage(resultWnd).initModality(Modality.APPLICATION_MODAL)
    }
    resultWnd
  }

  /**
   * @inheritdoc This implementation behaves the same as ''createFrame()'' as
   * internal frames are not supported by Java FX. So calling this method only
   * makes sense when a parent window is provided in the ''WindowBuilderData''
   * object.
   */
  def createInternalFrame(builderData: WindowBuilderData, data: WindowData,
    wnd: Window): Window = createFrame(builderData, data, wnd)

  /**
   * Creates a new ''Window'' object that wraps a ''Stage''. The stage is
   * obtained from the ''StageFactory''. If an owner is specified, it is
   * set.
   * @param builderData the builder data object
   * @param data the data object with the window's properties
   * @return the newly created ''Window'' object
   */
  private def createWindow(builderData: WindowBuilderData, data: WindowData): Window = {
    val stage = stageFactory.createStage()
    val sizeHandler = JavaFxUnitSizeHandler.fromContext(builderData.getContext)
    val wnd = JavaFxWindow(stage, sizeHandler = Some(sizeHandler), autoClose = data.isAutoClose,
      closeable = data.isClosable)
    if (builderData.getParentWindow != null) {
      stage.initOwner(extractStage(builderData.getParentWindow))
      wnd.parentWindow = builderData.getParentWindow
    }
    wnd
  }

  /**
   * Initializes the properties of the specified window.
   * @param data the data object with the properties for the window
   * @param wnd the window to be initialized
   */
  private def initWindowProperties(data: WindowData, wnd: Window) {
    val fxwnd = asFxWindow(wnd)
    wnd.setTitle(data.getTitle)
    fxwnd.windowController = data.getController
    initWindowBounds(data, fxwnd.stage)
    initMenuBar(fxwnd, data.getMenuBar)
    initIcon(fxwnd, data)
  }

  /**
   * Initializes the window's menu bar if one is defined.
   *
   * @param window the window
   * @param menuBar the optional menu bar
   */
  def initMenuBar(window: JavaFxWindow, menuBar: AnyRef): Unit = {
    if (menuBar != null) {
      window.rootContainer.menuBar = Some(ComponentUtils.as[MenuBar](menuBar))
    }
  }

  /**
   * Initializes the stage's bounds from the given data object. All properties
   * defined in the ''WindowData'' object are set.
   * @param data the data object
   * @param stage the stage to be initialized
   */
  private def initWindowBounds(data: WindowData, stage: Stage) {
    if (defined(data.getXPos)) {
      stage.setX(data.getXPos)
    }
    if (defined(data.getYPos)) {
      stage.setY(data.getYPos)
    }
    if (defined(data.getWidth)) {
      stage.setWidth(data.getWidth)
    }
    if (defined(data.getHeight)) {
      stage.setHeight(data.getHeight)
    }
  }

  /**
    * Initializes the window's icon from the given data object.
    * @param window the window
    * @param data the data object
    */
  private def initIcon(window: JavaFxWindow, data: WindowData): Unit = {
    data.getIcon match {
      case ImageWrapper(image) =>
        window.stage.getIcons add image
      case _ =>
    }
  }

  /**
   * Convenience method for casting a window object to a Java FX window.
   * @param wnd the input window
   * @return the window cast to a Java FX window
   */
  private def asFxWindow(wnd: Window): JavaFxWindow = wnd.asInstanceOf[JavaFxWindow]

  /**
   * Convenience method for obtaining the stage wrapped by the passed in window.
   * The window must represent a Java FX window. It is cast, and its stage is
   * returned.
   * @param wnd the input window
   * @return the stage wrapped by this window
   */
  private def extractStage(wnd: Window): Stage = asFxWindow(wnd).stage

  /**
   * Helper method for testing whether a bounds value is defined.
   * @param value the value to be checked
   * @return '''true''' if this value is defined, '''false''' otherwise
   */
  private def defined(value: Int): Boolean = value != WindowData.UNDEFINED
}
