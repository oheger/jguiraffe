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
package net.sf.jguiraffe.gui.platform.javafx.builder.utils

import java.util.concurrent.SynchronousQueue
import javafx.application.Platform
import javafx.scene.Parent
import javafx.scene.control.{Button, Label}
import javafx.scene.image.{Image, ImageView}
import javafx.stage.{Stage, Window => FxWindow}

import net.sf.jguiraffe.di.BeanContext
import net.sf.jguiraffe.gui.app.ApplicationContext
import net.sf.jguiraffe.gui.builder.utils.MessageOutput
import net.sf.jguiraffe.gui.builder.window.{Window, WindowWrapper}
import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import net.sf.jguiraffe.gui.platform.javafx.layout.PercentLayoutPane
import org.easymock.EasyMock
import org.junit.Assert._
import org.junit.{BeforeClass, Before, Test}
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.EasyMockSugar

/**
 * Companion object.
 */
object TestJavaFxMessageOutputBase {
  /** Constant for a test message. */
  private val Message = "Some test message"

  /** Constant for a test title. */
  private val Title = "A test title"

  /** Constant for a test icon. */
  private var testIcon: Image = _

  /** Constant for the maximum text width. */
  private val TextWidth = 333.0

  @BeforeClass def setUpOnce(): Unit = {
    JavaFxTestHelper.initPlatform()
    testIcon = new Image(classOf[TestJavaFxMessageOutputBase].getResource("/icon.jpg")
      .toExternalForm)
  }

  def Icon: Image = testIcon
}

/**
 * Test class for ''JavaFxMessageOutputBase''.
 */
class TestJavaFxMessageOutputBase extends JUnitSuite with EasyMockSugar {

  import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper._
  import net.sf.jguiraffe.gui.platform.javafx.builder.utils.TestJavaFxMessageOutputBase._

  /** A mock for the application context. */
  private var applicationContext: ApplicationContext = _

  /** The output object to be tested. */
  private var output: JavaFxMessageOutputBase with StageProviderTestImpl with
    ButtonProviderTestImpl with IconProviderTestImpl = _

  @Before def setUp(): Unit = {
    applicationContext = mock[ApplicationContext]
    val beanContext = mock[BeanContext]
    EasyMock.expect(beanContext.getBean(classOf[ApplicationContext]))
      .andReturn(applicationContext).anyTimes()
    EasyMock.replay(beanContext)

    JavaFxTestHelper.runInFxThread { () =>
        output = new JavaFxMessageOutputBase(TextWidth) with StageProviderTestImpl with
          ButtonProviderTestImpl with IconProviderTestImpl
    }
    output setBeanContext beanContext
  }

  /**
   * Helper method for invoking the show() method on the test object passing in some
   * test data and the provided values.
   * @param parent the parent window
   * @param messageType the message type
   * @param buttonType the button type
   */
  private def invokeShow(parent: Window, messageType: Int, buttonType: Int): Unit = {
    output.expectedMessageType = messageType
    JavaFxTestHelper.runInFxThread { () =>
      assertTrue("Wrong result", output.show(parent, Message, Title, messageType, buttonType) >= 0)
    }
  }

  /**
   * Creates a JGUIraffe window wrapping the parent stage.
   * @return the parent window
   */
  private def createParent(): Window = {
    val wrapper = mock[WrappingWindow]
    val parentStage = mock[Stage]
    EasyMock.expect(wrapper.getWrappedWindow).andReturn(parentStage).anyTimes()
    EasyMock.replay(wrapper, parentStage)
    output.expectedParent = Some(parentStage)
    wrapper
  }

  /**
   * Searches for a component of the specified class in the content of the test
   * stage. This method can be used to check whether all expected UI elements
   * have been created correctly. Note: We do not actually check the correct
   * layout. Rather, we only check whether the expected UI controls are there.
   * @param compClass the class of the component to be retrieved
   * @tparam T the component type
   * @return the found component
   */
  private def findComponent[T](compClass: Class[T]): T =
    findComponentInParent(output.stage.getScene.getRoot, compClass).get

  /**
   * Recursively searches for a component of a certain class in the components of
   * a parent.
   * @param parent the ''Parent''
   * @param compClass the class of the component to be retrieved
   * @tparam T the component type
   * @return an option of the found component
   */
  private def findComponentInParent[T](parent: Parent, compClass: Class[T]): Option[T] = {
    val iterator = parent.getChildrenUnmodifiable.iterator()
    var component: Option[T] = None

    while (component.isEmpty && iterator.hasNext) {
      val c = iterator.next()
      if (compClass isInstance c) {
        component = Some(compClass.cast(c))
      } else {
        if (c.isInstanceOf[Parent]) {
          component = findComponentInParent(c.asInstanceOf[Parent], compClass)
        }
      }
    }

    component
  }

  /**
   * Tests that the stage containing the message is correctly initialized.
   */
  @Test def testStageInitialization(): Unit = {
    invokeShow(createParent(), MessageOutput.MESSAGE_INFO, MessageOutput.BTN_OK)

    assertEquals("Wrong title", Title, output.stage.getTitle)
    assertFalse("Stage is resizable", output.stage.isResizable)
  }

  /**
   * Tests whether the main window is set as  parent stage if none is provided.
   */
  @Test def testStageInitializationMainWindowAsParent(): Unit = {
    val mainWindow = createParent()
    EasyMock.expect(applicationContext.getMainWindow).andReturn(mainWindow).anyTimes()

    whenExecuting(applicationContext) {
      invokeShow(null, MessageOutput.MESSAGE_INFO, MessageOutput.BTN_OK)
    }
  }

  /**
   * Tests whether the message stage is correctly displayed.
   */
  @Test def testStageDisplaying(): Unit = {
    invokeShow(createParent(), MessageOutput.MESSAGE_INFO, MessageOutput.BTN_OK)

    assertEquals("Not sized", 1, output.stage.sizeToSceneCalls)
    assertEquals("Not centered", 1, output.stage.centerOnScreenCalls)
    assertEquals("Not displayed", 1, output.stage.showAndWaitCalls)
    assertEquals("Closed", 0, output.stage.closeCalls)
  }

  /**
   * Checks whether the message UI contains the expected buttons.
   * @param expButtons an array with the expected buttons
   */
  private def checkButtons(expButtons: Button*): Unit = {
    import scala.collection.JavaConversions._
    val buttonPane = findComponent(classOf[PercentLayoutPane])
    val buttons = buttonPane.getChildrenUnmodifiable filter (_.isInstanceOf[Button])
    assertEquals("Wrong buttons", expButtons.toSeq, buttons.toSeq)
  }

  /**
   * Helper method for testing whether correct buttons are created for a given
   * button type.
   * @param buttonType the button type
   * @param expButtons the expected buttons for this type
   */
  private def checkButtonsInMessage(buttonType: Int, expButtons: Button*): Unit = {
    invokeShow(createParent(), MessageOutput.MESSAGE_INFO, buttonType)
    checkButtons(expButtons: _*)
  }

  /**
   * Tests a message containing only an OK button.
   */
  @Test def testMessageOkButton(): Unit = {
    checkButtonsInMessage(MessageOutput.BTN_OK, output.btnOk)
  }

  /**
   * Tests a message with the OK and Cancel buttons.
   */
  @Test def testMessageOkCancelButton(): Unit = {
    checkButtonsInMessage(MessageOutput.BTN_OK_CANCEL, output.btnOk, output.btnCancel)
  }

  /**
   * Tests a message with the Yes and No buttons.
   */
  @Test def testMessageYesNoButton(): Unit = {
    checkButtonsInMessage(MessageOutput.BTN_YES_NO, output.btnYes, output.btnNo)
  }

  /**
   * Tests a message with 3 result buttons.
   */
  @Test def testMessageYesNoCancelButton(): Unit = {
    checkButtonsInMessage(MessageOutput.BTN_YES_NO_CANCEL, output.btnYes, output.btnNo,
      output.btnCancel)
  }

  /**
   * Helper method for testing whether the correct result is returned based on
   * the clicked button.
   * @param buttonType the button type constant
   * @param btn the button clicked by the user
   * @param expResult the expected result
   */
  private def checkResult(buttonType: Int, btn: Button, expResult: Int): Unit = {
    val parent = createParent()
    invokeShow(parent, MessageOutput.MESSAGE_INFO, buttonType)
    val syncQueue = new SynchronousQueue[Integer]
    Platform.runLater(new Runnable {
      override def run(): Unit = {
        btn.fire()
        syncQueue put output.currentReturnCode
      }
    })
    assertEquals("Wrong result", expResult, syncQueue.take().intValue)
    assertEquals("Stage not closed", 1, output.stage.closeCalls)
  }

  /**
   * Tests whether the correct result is returned if the yes button is clicked.
   */
  @Test def testResultYes(): Unit = {
    checkResult(MessageOutput.BTN_YES_NO_CANCEL, output.btnYes, MessageOutput.RET_YES)
  }

  /**
   * Tests whether the correct result is returned if the no button is clicked.
   */
  @Test def testResultNo(): Unit = {
    checkResult(MessageOutput.BTN_YES_NO_CANCEL, output.btnNo, MessageOutput.RET_NO)
  }

  /**
   * Tests whether the correct result is returned if the cancel button is clicked.
   */
  @Test def testResultCancel(): Unit = {
    checkResult(MessageOutput.BTN_YES_NO_CANCEL, output.btnCancel, MessageOutput.RET_CANCEL)
  }

  /**
   * Tests whether the correct result is returned if the OK button is clicked.
   */
  @Test def testResultOk(): Unit = {
    checkResult(MessageOutput.BTN_OK, output.btnOk, MessageOutput.RET_OK)
  }

  /**
   * Tests whether the expected icon is displayed.
   */
  @Test def testMessageIcon(): Unit = {
    invokeShow(createParent(), MessageOutput.MESSAGE_ERROR, MessageOutput.BTN_OK)
    val image = findComponent(classOf[ImageView])
    assertSame("Wrong image", Icon, image.getImage)
  }

  /**
   * Tests a message with does not have an icon.
   */
  @Test def testMessageNoIcon(): Unit = {
    invokeShow(createParent(), MessageOutput.MESSAGE_PLAIN, MessageOutput.BTN_OK)
    assertFalse("Got an icon", findComponentInParent(output.stage.getScene.getRoot,
      classOf[ImageView]).isDefined)
  }

  /**
   * Tests whether the label for the message is correctly initialized.
   */
  @Test def testMessageLabel(): Unit = {
    invokeShow(createParent(), MessageOutput.MESSAGE_QUESTION, MessageOutput.BTN_OK)
    val label = findComponent(classOf[Label])
    assertEquals("Wrong text", Message, label.getText)
    assertTrue("No word wrapping", label.isWrapText)
    assertEquals("Wrong maximum width", TextWidth, label.getMaxWidth, .001)
  }

  /**
   * Tests whether a ''JavaFxMessageOutput'' object can be constructed with a
   * default maximum text width.
   */
  @Test def testMessageOutputDefaultTextWidth(): Unit = {
    val msgOutput = new JavaFxMessageOutput
    assertEquals("Wrong default text width", JavaFxMessageOutput.DefaultMaximumTextWidth,
      msgOutput.maximumTextWidth, .001)
  }

  /**
   * Helper method for testing the result produced by a normal close operation
   * on the message window.
   * @param buttonType the button type
   * @param expResult the expected result
   */
  private def checkResultForClose(buttonType: Int, expResult: Int): Unit = {
    val syncQueue = new SynchronousQueue[Integer]
    output.expectedMessageType = MessageOutput.MESSAGE_QUESTION
    Platform.runLater(new Runnable {
      override def run(): Unit = {
        syncQueue.put(Integer valueOf output.show(createParent(), Message, Title,
          MessageOutput.MESSAGE_QUESTION, buttonType))
      }
    })
    assertEquals("Wrong result", expResult, syncQueue.take().intValue())
  }

  /**
   * Tests the result for a closed window if only the OK button is present.
   */
  @Test def testResultForCloseOk(): Unit = {
    checkResultForClose(MessageOutput.BTN_OK, MessageOutput.RET_OK)
  }

  /**
   * Tests the result for a closed window with OK and Cancel buttons.
   */
  @Test def testResultForCloseOkCancel(): Unit = {
    checkResultForClose(MessageOutput.BTN_OK_CANCEL, MessageOutput.RET_CANCEL)
  }

  /**
   * Tests the result for a closed window with Yes, No, and Cancel buttons.
   */
  @Test def testResultForCloseYesNoCancel(): Unit = {
    checkResultForClose(MessageOutput.BTN_YES_NO_CANCEL, MessageOutput.RET_CANCEL)
  }

  /**
   * Tests the result for a closed window with Yes and No buttons.
   */
  @Test def testResultForCloseYesNo(): Unit = {
    checkResultForClose(MessageOutput.BTN_YES_NO, MessageOutput.RET_NO)
  }

  /**
   * Tests the result for a closed window if an invalid button type is provided.
   */
  @Test def testResultForCloseInvalidButtons(): Unit = {
    checkResultForClose(-1, MessageOutput.RET_OK)
  }

  /**
   * A combined trait for a window and a window wrapper used for creating mock
   * objects.
   */
  private trait WrappingWindow extends Window with WindowWrapper

  /**
   * A test ''Stage'' implementation used for testing the communication of the
   * test object with the stage containing the message dialog.
   */
  private class StageTestImpl extends Stage {
    /** The number of close() calls. */
    var closeCalls = 0

    /** The number of showAndWait() calls. */
    var showAndWaitCalls = 0

    /** The number of sizeToScene() calls. */
    var sizeToSceneCalls = 0

    /** The number of centerOnScreen() calls. */
    var centerOnScreenCalls = 0

    override def close(): Unit = {
      closeCalls += 1
    }

    override def showAndWait(): Unit = {
      showAndWaitCalls += 1
    }

    override def sizeToScene(): Unit = {
      sizeToSceneCalls += 1
    }

    override def centerOnScreen(): Unit = {
      centerOnScreenCalls += 1
    }
  }

  /**
   * A test implementation of the stage provider. This implementation always
   * returns the test stage. It also checks whether the expected parent
   * window is passed in.
   */
  private trait StageProviderTestImpl extends MessageOutputStageProvider {
    /** The stage that is returned by this provider. */
    val stage = new StageTestImpl

    /** A parent window to be checked. */
    var expectedParent: Option[FxWindow] = None

    /**
     * Creates a new ''Stage'' as a child window of the specified parent.
     * @param parent the parent window
     * @return the new ''Stage''
     */
    override def createStage(parent: FxWindow): Stage = {
      expectedParent foreach (_ == parent)
      stage
    }
  }

  /**
   * A test implementation of a button provider. This implementation operates
   * on a fix set of buttons which can be queried at any time.
   */
  private trait ButtonProviderTestImpl extends MessageOutputButtonProvider {
    /** The ok button. */
    val btnOk = new Button

    /** The cancel button. */
    val btnCancel = new Button

    /** The yes button. */
    val btnYes = new Button

    /** The no button. */
    val btnNo = new Button

    override def okButton(context: ApplicationContext): Button =
      getButtonAndCheckCtx(context, btnOk)

    override def cancelButton(context: ApplicationContext): Button =
      getButtonAndCheckCtx(context, btnCancel)

    override def yesButton(context: ApplicationContext): Button =
      getButtonAndCheckCtx(context, btnYes)

    override def noButton(context: ApplicationContext): Button =
      getButtonAndCheckCtx(context, btnNo)

    /**
     * Checks whether the correct application context is passed. If so, the provided
     * button is returned.
     * @param context the application context
     * @param btn the button to be returned
     * @return the button
     */
    private def getButtonAndCheckCtx(context: ApplicationContext, btn: Button): Button = {
      assertEquals("Wrong application context", applicationContext, context)
      btn
    }
  }

  /**
   * A test implementation for an icon provider. This implementation returns a
   * predefined icon. (For the type plain no icon is returned.) It also checks
   * whether the expected message code is passed.
   */
  private trait IconProviderTestImpl extends MessageOutputIconProvider {
    /** The expected message type. */
    var expectedMessageType: Int = -1

    override def messageIcon(messageType: Int): Option[Image] = {
      assertEquals("Wrong message type", expectedMessageType, messageType)
      messageType match {
        case MessageOutput.MESSAGE_PLAIN =>
          None
        case _ =>
          Some(TestJavaFxMessageOutputBase.Icon)
      }
    }
  }

}
