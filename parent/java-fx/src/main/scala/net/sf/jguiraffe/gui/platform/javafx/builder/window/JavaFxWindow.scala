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

import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import javafx.stage.Stage

import net.sf.jguiraffe.gui.builder.event.{FormMouseEvent, FormMouseListener}
import net.sf.jguiraffe.gui.builder.window._
import net.sf.jguiraffe.gui.layout.UnitSizeHandler
import net.sf.jguiraffe.gui.platform.javafx.builder.event.{EventListenerList, MouseEventAdapter, WindowEventAdapter}
import net.sf.jguiraffe.gui.platform.javafx.builder.utils.JavaFxGUISynchronizer

import scala.beans.BeanProperty

/**
 * The Java FX-based implementation of the ''Window'' interface.
 *
 * This class wraps a JavaFX ''Stage'' object. Most methods of the JGUIraffe
 * ''Window'' interface are delegated to this stage.
 *
 * Note that most methods must be called in the Java FX thread if not stated
 * otherwise. No additional thread-safety layer is created by this class.
 *
 * The window's root container is a ''ContainerWrapper'' object passed to
 * the constructor. This wrapper can be populated with the window's content
 * and initialized with a layout. When the window is displayed it is asked to
 * create a corresponding ''Pane'' which* is then added to the wrapped stage's
 * scene.
 *
 * @param stage the wrapped ''Stage'' object
 * @param windowListeners the object for registering window listeners
 * @param mouseListeners the object for registering mouse listeners
 * @param rootContainer the root container for this window
 */
private class JavaFxWindow private[window] (val stage: Stage,
  windowListeners: EventListenerList[WindowEvent, WindowListener],
  mouseListeners: EventListenerList[FormMouseEvent, FormMouseListener],
  @BeanProperty val rootContainer: WindowRootContainerWrapper)
  extends Window with WindowWrapper {
  /** The underlying wrapped window. */
  override val getWrappedWindow = stage

  /** The parent window of this window. */
  @BeanProperty var parentWindow: Window = _

  /** The controller object for this window. */
  @BeanProperty var windowController: Object = _

  /**
   * A flag whether this window can now be closed. This is used
   * internally to ensure that the window closing strategy is taken into
   * account.
   */
  private[window] var closingPermitted: Boolean = false

  /** The window closing strategy used by this window. */
  private var windowClosingStrategy: WindowClosingStrategy = _

  def isVisible: Boolean = stage.isShowing

  /**
   * @inheritdoc This implementation calls the stage's ''show()'' or
   * ''hide()'' methods depending on the parameter.
   */
  def setVisible(f: Boolean) {
    if (f) stage.show()
    else stage.hide()
  }

  /**
   * @inheritdoc This method can be called from an arbitrary thread. It
   * opens the window in the Java FX application thread and waits until it
   * is open.
   */
  def open() {
    JavaFxGUISynchronizer.syncJavaFxInvocation { () =>
      stage.getScene.setRoot(rootContainer.createContainer())
      stage.show()
    }
  }

  /**
   * @inheritdoc This implementation asks the associated closing strategy
   * if the window can be closed (if the ''force'' flag is not set). If
   * closing is allowed, the associated window is closed.
   */
  def close(force: Boolean): Boolean = {
    closingPermitted = force || getWindowClosingStrategy.canClose(this)
    if (closingPermitted) {
      stage.close()
      true
    } else {
      false
    }
  }

  def getXPos: Int = stage.getX.toInt

  def getYPos: Int = stage.getY.toInt

  def getWidth: Int = stage.getWidth.toInt

  def getHeight: Int = stage.getHeight.toInt

  def setBounds(x: Int, y: Int, w: Int, h: Int) {
    stage.setX(x)
    stage.setY(y)
    stage.setWidth(w)
    stage.setHeight(h)
  }

  def getTitle: String = stage.getTitle

  def setTitle(title: String) {
    stage.setTitle(title)
  }

  def addWindowListener(l: WindowListener) {
    windowListeners += l
  }

  def removeWindowListener(l: WindowListener) {
    windowListeners -= l
  }

  /**
   * @inheritdoc This implementation never returns '''null'''. If no strategy
   * has been set so far, a default strategy is returned which always permits
   * closing.
   */
  def getWindowClosingStrategy: WindowClosingStrategy =
    if (windowClosingStrategy != null) windowClosingStrategy
    else InvariantWindowClosingStrategy.DEFAULT_INSTANCE

  def setWindowClosingStrategy(wcs: WindowClosingStrategy) = {
    windowClosingStrategy = wcs
  }

  def addMouseListener(l: FormMouseListener) {
    mouseListeners += l
  }

  def removeMouseListener(l: FormMouseListener) {
    mouseListeners -= l
  }
}

/**
 * The companion object for ''JavaFxWindow''.
 */
private object JavaFxWindow {
  /**
   * Creates a new instance of ''JavaFxWindow'' which wraps the specified
   * ''Stage'' object. Optionally, a size handler can be provided which is then
   * passed to the window's root container.
   * @param stage the ''Stage'' to be wrapped
   * @return the fully initialized ''JavaFxWindow'' object
   */
  def apply(stage: Stage, sizeHandler: Option[UnitSizeHandler] = None): JavaFxWindow = {
    val wndListeners = new EventListenerList[WindowEvent, WindowListener]
    val mouseListeners = new EventListenerList[FormMouseEvent, FormMouseListener]
    val root = new WindowRootContainerWrapper(sizeHandler)
    val wnd = new JavaFxWindow(stage, wndListeners, mouseListeners, root)

    WindowEventAdapter(stage, wnd, wndListeners)
    val mouseAdapter = MouseEventAdapter(mouseListeners)
    stage.addEventHandler(MouseEvent.ANY, mouseAdapter)
    registerClosingListener(stage, wnd)

    wnd
  }

  /**
   * Registers a window closing listener which monitors the window's closing
   * process. Closing is only possible if permitted by the closing strategy.
   * @param stage the stage
   * @param wnd the window wrapper
   */
  private def registerClosingListener(stage: Stage, wnd: JavaFxWindow) {
    stage.addEventFilter(javafx.stage.WindowEvent.WINDOW_CLOSE_REQUEST,
      new EventHandler[javafx.stage.WindowEvent] {
        def handle(e: javafx.stage.WindowEvent) {
          if (!wnd.closingPermitted) {
            e.consume()
          }
        }
      })
  }
}