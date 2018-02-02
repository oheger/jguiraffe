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
package net.sf.jguiraffe.gui.platform.javafx.builder.utils

import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.control.{Button, Label}
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.{Node, Scene}
import javafx.stage.{Stage, Window => FxWindow}

import net.sf.jguiraffe.di.{BeanContext, BeanContextClient}
import net.sf.jguiraffe.gui.app.ApplicationContext
import net.sf.jguiraffe.gui.builder.utils.MessageOutput
import net.sf.jguiraffe.gui.builder.window.{Window, WindowUtils}
import net.sf.jguiraffe.gui.layout.{NumberWithUnit, ButtonLayout, PercentLayoutBase}
import net.sf.jguiraffe.gui.platform.javafx.layout.{ContainerWrapper, JavaFxPercentLayoutAdapter, PercentLayoutPane}

/**
 * An abstract base class for the JavaFX-specific ''MessageOutput''
 * implementation.
 *
 * This base implements all the functionality for displaying message boxes of
 * different types. To achieve this, it relies on certain providers for
 * obtaining the various elements for constructing message boxes. Which
 * concrete providers to be used has to be defined for concrete sub classes by
 * mixing in the corresponding traits.
 *
 * A concrete subclass is typically defined in the bean context of a
 * ''JGUIraffe'' application. Note that the required dependencies cannot be
 * injected directly because this would lead to cyclic references (the
 * ''MessageOutput'' implementation is passed to the ''ApplicationContext''
 * object at creation time; the ''ApplicationContext'' is also needed by this
 * object.)
 *
 * Implementation note: This class is not thread-safe. It is expected to be
 * used only in the JavaFX thread.
 *
 * @param maximumTextWidth the maximum width of the label for displaying the
 *                         message text; if the text is wider, it is wrapped
 */
class JavaFxMessageOutputBase(val maximumTextWidth: Double) extends MessageOutput with
BeanContextClient {
  this: MessageOutputStageProvider with MessageOutputButtonProvider with
    MessageOutputIconProvider =>

  /** The bean context. */
  private var beanContext: BeanContext = _

  /** The application context. It is obtained from the bean context. */
  private lazy val applicationContext = beanContext.getBean(classOf[ApplicationContext])

  /**
   * The code to be returned by a ''show()'' invocation.
   *
   * Note that it is no problem to store this value globally as a member field.
   * The ''show()'' method can only be called in the JavaFX thread. There can
   * only be a single invocation at a given point in time.
   */
  private var returnCode: Int = 0

  /**
   * Initializes the bean context. This method is called by the injection
   * framework.
   * @param context the ''BeanContext''
   */
  override def setBeanContext(context: BeanContext): Unit = {
    beanContext = context
  }

  override def show(parent: Window, message: scala.Any, title: String, messageType: Int,
                    buttonType: Int): Int = {
    val stage = createMessageStage(parent)
    val icon = messageIcon(messageType)

    val label = new Label(String.valueOf(message))
    label setWrapText true
    label setMaxWidth maximumTextWidth

    val pane = new BorderPane
    pane setCenter label
    pane setBottom createButtonPane(buttonType, stage)
    icon foreach (pane setLeft new ImageView(_))

    val scene = new Scene(pane)
    stage setScene scene
    initStage(stage, title)

    returnCode = initializeReturnCodeForClose(buttonType)
    stage.showAndWait()
    currentReturnCode
  }

  /**
   * Returns the current return code. This is mainly used for testing purposes.
   * @return the current return code
   */
  private [utils] def currentReturnCode = returnCode

  /**
   * Creates the stage in which the message is to be displayed. It is obtained from
   * a ''MessageOutputStageProvider''. The correct parent window has to be
   * determined: either is was passed in or it has to be obtained from the
   * ''ApplicationContext''.
   * @param parent the parent window passed to ''show()''
   * @return the stage for the message
   */
  private def createMessageStage(parent: Window): Stage = {
    val parentStage: FxWindow = fetchParentWindow(parent)
    createStage(parentStage)
  }

  /**
   * Obtains the parent JavaFX window for the message dialog. If a non-null
   * window was passed in, the JavaFX window is obtained from this. Otherwise,
   * the main window of the application is used.
   * @param parent the parent window passed to ''show()''
   * @return the parent window
   */
  private def fetchParentWindow(parent: Window): FxWindow = {
    val parentWindow = if (parent != null) parent
    else applicationContext.getMainWindow
    (WindowUtils getPlatformWindow parentWindow).asInstanceOf[FxWindow]
  }

  /**
   * Initializes the message stage before it is displayed.
   * @param stage the stage
   * @param title the message title
   * @return the initialized stage
   */
  private def initStage(stage: Stage, title: String): Stage = {
    stage setTitle title
    stage.sizeToScene()
    stage.centerOnScreen()
    stage setResizable false
    stage
  }

  /**
   * Creates a ''PercentLayoutPane'' for the passed in ''PercentLayoutBase'' and the
   * given content.
   * @param components the components to be added to the pane
   * @param constraints the constraints for the components
   * @param layout the underlying ''PercentLayoutBase''
   * @return the newly created ''PercentLayoutPane''
   */
  private def createPercentLayoutPane(components: Array[Node], constraints: Array[AnyRef],
                                      layout: PercentLayoutBase): PercentLayoutPane = {
    val layoutAdapter = new JavaFxPercentLayoutAdapter(components, constraints)
    layout setPlatformAdapter layoutAdapter
    val pane = new PercentLayoutPane(layout, new ContainerWrapper)
    components foreach pane.getChildren.add
    pane
  }

  /**
   * Creates the pane containing the buttons for closing the dialog.
   * @param buttonType the button type constant
   * @param stage the dialog window
   * @return the button pane
   */
  private def createButtonPane(buttonType: Int, stage: Stage): PercentLayoutPane = {
    val buttons = createButtons(buttonType, stage)
    val buttonConstraints = new Array[AnyRef](buttons.size)
    val buttonLayout = new ButtonLayout
    buttonLayout setGap new NumberWithUnit(18)
    val btnPane = createPercentLayoutPane(buttons, buttonConstraints, buttonLayout)
    btnPane
  }

  /**
   * Returns an array with the buttons to be contained in the current message
   * dialog based on the given button type.
   * @param buttonType the button type constant
   * @param stage the dialog window
   * @return an array with the corresponding buttons
   */
  private def createButtons(buttonType: Int, stage: Stage): Array[Node] = {
    buttonType match {
      case MessageOutput.BTN_YES_NO_CANCEL =>
        Array[Node](fetchYesButton(stage), fetchNoButton(stage), fetchCancelButton(stage))
      case MessageOutput.BTN_OK_CANCEL =>
        Array[Node](fetchOkButton(stage), fetchCancelButton(stage))
      case MessageOutput.BTN_YES_NO =>
        Array[Node](fetchYesButton(stage), fetchNoButton(stage))
      case _ => Array[Node](fetchOkButton(stage))
    }
  }

  /**
   * Initializes the specified button before it is added to the message dialog.
   * This method adds an event listener so that the message window is closed
   * with the correct result when this button is clicked.
   * @param button the button to be initialized
   * @param btnReturnCode the return code for this button
   * @param stage the stage for the message dialog
   * @return the initialized button
   */
  private def initButton(button: Button, btnReturnCode: Int, stage: Stage): Button = {
    button setOnAction new EventHandler[ActionEvent] {
      override def handle(event: ActionEvent): Unit = {
        returnCode = btnReturnCode
        stage.close()
      }
    }
    button
  }

  /**
   * Obtains the initialized Yes button.
   * @param stage the stage for the message dialog
   * @return the button
   */
  private def fetchYesButton(stage: Stage): Button =
    initButton(yesButton(applicationContext), MessageOutput.RET_YES, stage)

  /**
   * Obtains the initialized No button.
   * @param stage the stage for the message dialog
   * @return the button
   */
  private def fetchNoButton(stage: Stage): Button =
    initButton(noButton(applicationContext), MessageOutput.RET_NO, stage)

  /**
   * Obtains the initialized Cancel button.
   * @param stage the stage for the message dialog
   * @return the button
   */
  private def fetchCancelButton(stage: Stage): Button =
    initButton(cancelButton(applicationContext), MessageOutput.RET_CANCEL, stage)

  /**
   * Obtains the initialized Ok button.
   * @param stage the stage for the message dialog
   * @return the button
   */
  private def fetchOkButton(stage: Stage): Button =
    initButton(okButton(applicationContext), MessageOutput.RET_OK, stage)

  /**
   * Determines the code to be returned if the user closes the message dialog with
   * the [X] button. This is equivalent with pressing ESC. The code to be returned
   * in this case depends on the button type.
   * @param buttonType the button type
   * @return the code to be returned if the user closes the message window
   */
  private def initializeReturnCodeForClose(buttonType: Int): Int =
    buttonType match {
      case MessageOutput.BTN_OK_CANCEL => MessageOutput.RET_CANCEL
      case MessageOutput.BTN_YES_NO_CANCEL => MessageOutput.RET_CANCEL
      case MessageOutput.BTN_YES_NO => MessageOutput.RET_NO
      case _ => MessageOutput.RET_OK
    }
}
