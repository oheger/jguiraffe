/*
 * Copyright 2006-2025 The JGUIraffe Team.
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

import javafx.scene.Group
import javafx.scene.control.{Button, MenuBar}
import javafx.scene.image.Image
import javafx.scene.text.Text
import javafx.stage.{Modality, Stage}
import net.sf.jguiraffe.gui.builder.components.{ComponentBuilderData, FormBuilderException}
import net.sf.jguiraffe.gui.builder.window.{Window, WindowBuilderData, WindowData}
import net.sf.jguiraffe.gui.forms.bind.BeanBindingStrategy
import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import net.sf.jguiraffe.gui.platform.javafx.common.ImageWrapper
import net.sf.jguiraffe.gui.platform.javafx.layout.{ContainerWrapper, JavaFxUnitSizeHandler}
import net.sf.jguiraffe.transform.TransformerContext
import org.apache.commons.jelly.JellyContext
import org.easymock.EasyMock
import org.junit.Assert._
import org.junit.{Before, BeforeClass, Test}
import org.scalatestplus.junit.JUnitSuite
import org.scalatestplus.easymock.EasyMockSugar

import scala.beans.{BeanProperty, BooleanBeanProperty}

/**
 * Test class for ''JavaFxWindowManager''.
 */
class TestJavaFxWindowManager extends JUnitSuite with EasyMockSugar {
  /** The manager to be tested. */
  private var manager: JavaFxWindowManager = _

  @Before def setUp(): Unit = {
    manager = TestJavaFxWindowManager.initializedManager
  }

  /**
   * Extracts the wrapped stage from the given window object.
   * @param wnd the window
   * @return the wrapped stage
   */
  private def extractStage(wnd: Window) =
    wnd.asInstanceOf[JavaFxWindow].stage

  /**
    * Creates a test image.
    * @return the image
    */
  private def createImage(): Image =
  new Image("icon.jpg")

  /**
   * Tests whether an initial and another stage can be created.
   */
  @Test def testCreateStages(): Unit = {
    val builderData = createWindowBuilderData()
    val primaryStage = manager.createFrame(builderData, new WindowDataImpl, null)
    assertSame("Different window", primaryStage,
      manager.createFrame(new WindowBuilderData, new WindowDataImpl, primaryStage))
    builderData.setParentWindow(primaryStage)
    val frame = manager.createFrame(builderData, new WindowDataImpl, null)
    manager.createFrame(builderData, new WindowDataImpl, frame)
    assertNotSame("Single window instance", primaryStage, frame)
    assertSame("Wrong parent", primaryStage, frame.getParentWindow)
    assertNotSame("Got same stages", extractStage(primaryStage), extractStage(frame))
  }

  /**
   * Creates a window builder data object with default settings.
   * @return the builder data object
   */
  private def createWindowBuilderData(): WindowBuilderData = {
    val data = new WindowBuilderData
    val context = new JellyContext
    data put context
    JavaFxUnitSizeHandler.fromContext(context) // creates the handler
    data
  }

  /**
   * Tests whether the stage's scene is initialized and whether controls can
   * be added.
   */
  @Test def testInitScene(): Unit = {
    val builderData = createWindowBuilderData()
    val windowData = new WindowDataImpl
    val wndNew = manager.createFrame(builderData, windowData, null)
      .asInstanceOf[JavaFxWindow]
    val scene = wndNew.stage.getScene
    assertTrue("Wrong scene root", scene.getRoot.isInstanceOf[Group])
    val rootContainer = wndNew.getRootContainer
    rootContainer.addComponent(new Text("Hello"), null)
    val pane = rootContainer.createContainer()
    assertEquals("Container not populated", 1, pane.getChildren.size())
    assertEquals("Wrong number of style sheets", TestJavaFxWindowManager.StyleSheets.size, scene
      .getStylesheets.size)
    assertTrue("Wrong style sheets", TestJavaFxWindowManager.StyleSheets forall scene
      .getStylesheets.contains)
  }

  /**
   * Tests whether the window title is set.
   */
  @Test def testInitTitle(): Unit = {
    val title = "A window title"
    val builderData = createWindowBuilderData()
    val windowData = WindowDataImpl(title = title)
    val wndNew = manager.createFrame(builderData, windowData, null)
    val wnd = manager.createFrame(builderData, windowData, wndNew)
    assertEquals("Wrong title", title, wnd.getTitle)
  }

  /**
   * Tests whether the window controller is set.
   */
  @Test def testInitController(): Unit = {
    val builderData = createWindowBuilderData()
    val windowData = WindowDataImpl(controller = this)
    val wndNew = manager.createFrame(builderData, windowData, null)
    val wnd = manager.createFrame(builderData, windowData, wndNew)
    assertEquals("Wrong controller", this, wnd.getWindowController)
  }

  /**
   * Tests whether the window's bounds are correctly initialized.
   */
  @Test def testInitBounds(): Unit = {
    val builderData = createWindowBuilderData()
    val windowData = WindowDataImpl(xPos = 10, yPos = 20, width = 300,
      height = 200)
    val wndNew = manager.createFrame(builderData, windowData, null)
    val wnd = manager.createFrame(builderData, windowData, wndNew)
    assertEquals("Wrong xPos", windowData.xPos, wnd.getXPos)
    assertEquals("Wrong yPos", windowData.yPos, wnd.getYPos)
    assertEquals("Wrong width", windowData.width, wnd.getWidth)
    assertEquals("Wrong height", windowData.height, wnd.getHeight)
  }

  /**
    * Tests whether an icon for the window is processed correctly.
    */
  @Test def testInitIcon(): Unit = {
    val image = createImage()
    val builderData = createWindowBuilderData()
    val windowData = WindowDataImpl(icon = ImageWrapper(image))
    val wndNew = manager.createFrame(builderData, windowData, null)
    val wnd = manager.createFrame(builderData, windowData, wndNew).asInstanceOf[JavaFxWindow]
    assertEquals("Wrong number of icons", 1, wnd.stage.getIcons.size())
    assertEquals("Wrong icon", image, wnd.stage.getIcons.get(0))
  }

  /**
   * Helper method for testing that the auto close flag is set correctly.
   * @param autoClose the auto close flag to be set
   */
  private def checkAutoCloseFlag(autoClose: Boolean): Unit = {
    val builderData = createWindowBuilderData()
    val windowData = WindowDataImpl(autoClose = autoClose)
    val wnd = manager.createFrame(builderData, windowData, null).asInstanceOf[JavaFxWindow]
    assertEquals("Wrong autoClose flag", autoClose, wnd.autoClose)
  }

  /**
   * Tests whether an autoClose flag set to true is handled correctly.
   */
  @Test def testAutoCloseTrue(): Unit = {
    checkAutoCloseFlag(autoClose = true)
  }

  /**
   * Tests whether an autoClose flag set to false is handled correctly.
   */
  @Test def testAutoCloseFalse(): Unit = {
    checkAutoCloseFlag(autoClose = false)
  }

  /**
    * Helper method for testing that the closeable flag is set correctly.
    * @param closeable the closeable flag to be set
    */
  private def checkCloseableFlag(closeable: Boolean): Unit = {
    val builderData = createWindowBuilderData()
    val windowData = WindowDataImpl(closable = closeable)
    val wnd = manager.createFrame(builderData, windowData, null).asInstanceOf[JavaFxWindow]
    assertEquals("Wrong closeable flag", closeable, wnd.closeable)
  }

  /**
    * Tests whether a closeable flag set to true is handled correctly.
    */
  @Test def testCloseableTrue(): Unit = {
    checkCloseableFlag(closeable = true)
  }

  /**
    * Tests whether a closeable flag set to false is handled correctly.
    */
  @Test def testCloseableFalse(): Unit = {
    checkCloseableFlag(closeable = false)
  }

  /**
   * Helper method for creating a window with the specified menu bar.
   * @param menuBar the menu bar
   * @return the window
   */
  private def createWindowWithMenuBar(menuBar: AnyRef): Window = {
    val builderData = createWindowBuilderData()
    val windowData = WindowDataImpl(menuBar = menuBar)
    val wndNew = manager.createFrame(builderData, windowData, null)
    manager.createFrame(builderData, windowData, wndNew)
  }

  /**
   * Obtains the root container from the specified window and casts it to the
   * given target type.
   * @param window the window
   * @return the root container
   */
  private def extractRootContainer(window: Window): WindowRootContainerWrapper =
    window.getRootContainer.asInstanceOf[WindowRootContainerWrapper]

  /**
   * Tests whether a menu bar is correctly initialized.
   */
  @Test def testInitMenuBar(): Unit = {
    val menuBar = new MenuBar
    assertEquals("Wrong menu bar", menuBar, extractRootContainer(createWindowWithMenuBar(menuBar)
    ).menuBar.get)
  }

  /**
   * Tests whether a missing menu bar is handled correctly.
   */
  @Test def testInitNoMenuBar(): Unit = {
    assertFalse("Got a menu bar", extractRootContainer(createWindowWithMenuBar(null)).menuBar
      .isDefined)
  }

  /**
   * Tests whether an invalid menu bar is handled correctly.
   */
  @Test(expected = classOf[FormBuilderException]) def testInitInvalidMenuBar(): Unit = {
    createWindowWithMenuBar(this)
  }

  /**
   * Tests the modality of a new frame window.
   */
  @Test def testCreateFrameModality(): Unit = {
    val builderData = createWindowBuilderData()
    val windowData = new WindowDataImpl
    val wndNew = manager.createFrame(builderData, windowData, null)
    val wnd = manager.createFrame(builderData, windowData, wndNew).asInstanceOf[JavaFxWindow]
    assertEquals("Wrong modality", Modality.NONE, wnd.stage.getModality)
  }

  /**
   * Tests whether the size handler is extracted from the Jelly context and
   * passed to the window's root container.
   */
  @Test def testSizeHandlerInRootContainer(): Unit = {
    val builderData = createWindowBuilderData()
    val windowData = new WindowDataImpl
    val wndNew = manager.createFrame(builderData, windowData, null)
    val root = wndNew.getRootContainer.asInstanceOf[ContainerWrapper]
    assertSame("Wrong size handler",
      JavaFxUnitSizeHandler.fromContext(builderData.getContext),
      root.sizeHandler.get)
  }

  /**
   * Helper method for testing the creation of a dialog.
   * @param modal the modal flag
   * @param expModality the expected modality value
   */
  private def checkCreateDialog(modal: Boolean, expModality: Modality): Unit = {
    val builderData = createWindowBuilderData()
    val windowData = WindowDataImpl(title = "some title")
    val wndNew = manager.createDialog(builderData, windowData, modal, null)
    val wnd = manager.createDialog(builderData, windowData, modal,
      wndNew).asInstanceOf[JavaFxWindow]
    assertEquals("Wrong modality", expModality, wnd.stage.getModality)
    assertEquals("Not initialized", "some title", wnd.getTitle)
  }

  /**
   * Tests whether a non-modal dialog window can be created.
   */
  @Test def testCreateDialogNonModal(): Unit = {
    checkCreateDialog(modal = false, Modality.NONE)
  }

  /**
   * Tests whether a modal dialog window can be created.
   */
  @Test def testCreateDialogModal(): Unit = {
    checkCreateDialog(modal = true, Modality.APPLICATION_MODAL)
  }

  /**
    * Tests whether a cancel button is correctly initialized.
    */
  @Test def testCreateDialogWithCancelButton(): Unit = {
    val builderData = createWindowBuilderData()
    val windowData = WindowDataImpl(title = "Esc", closeOnEsc = true)
    val CancelButtonName = "myCancelButton"
    val cancelButton = new Button
    val wndNew = manager.createFrame(builderData, windowData, null)
    windowData.getComponentBuilderData.storeComponent(CancelButtonName, cancelButton)
    windowData.getComponentBuilderData setCancelButtonName CancelButtonName

    manager.createFrame(builderData, windowData, wndNew)
    assertTrue("Cancel button not initialized", cancelButton.isCancelButton)
  }

  /**
    * Tests handling of a cancel button if a window must not be closed via ESC.
    */
  @Test def testNoCancelButtonIsSetIfCloseOnEscIsDisabled(): Unit = {
    val builderData = createWindowBuilderData()
    val windowData = WindowDataImpl(title = "Esc")
    val CancelButtonName = "cancelButtonToBeIgnored"
    val cancelButton = new Button
    val wndNew = manager.createDialog(builderData, windowData, modal = true, wnd = null)
    windowData.getComponentBuilderData.storeComponent(CancelButtonName, cancelButton)
    windowData.getComponentBuilderData setCancelButtonName CancelButtonName

    manager.createDialog(builderData, windowData, modal = true, wndNew)
    assertFalse("Cancel button flag set", cancelButton.isCancelButton)
  }

  /**
   * Tests the creation of an internal frame.
   */
  @Test def testCreateInternalFrame(): Unit = {
    val builderData = createWindowBuilderData()
    val windowData = WindowDataImpl(title = "some title")
    val wndNew = manager.createInternalFrame(builderData, windowData, null)
    val wnd = manager.createInternalFrame(builderData, windowData,
      wndNew).asInstanceOf[JavaFxWindow]
    assertEquals("Wrong modality", Modality.NONE, wnd.stage.getModality)
    assertEquals("Not initialized", "some title", wnd.getTitle)
  }

  /**
    * Tests that a custom stage factory can be installed.
    */
  @Test def testCustomizeStageCreation(): Unit = {
    val stageFactory = mock[StageFactory]
    val stage = JavaFxTestHelper.invokeInFxThread[Stage] { _ => new Stage }
    EasyMock.expect(stageFactory.createStage()).andReturn(stage)

    whenExecuting(stageFactory) {
      val manager = new JavaFxWindowManager(TestJavaFxWindowManager.createStyleSheetProvider(),
        stageFactory)
      val builderData = createWindowBuilderData()
      val window = manager.createFrame(builderData, new WindowDataImpl, null)
      assertSame("Wrong stage", stage, extractStage(window))
    }
  }

  /**
    * Creates a default ''ComponentBuilderData'' object that can be used by
    * tests.
    *
    * @return the initialized builder data object
    */
  private def createComponentBuilderData(): ComponentBuilderData = {
    val data = new ComponentBuilderData
    val transCtx = niceMock[TransformerContext]
    data.initializeForm(transCtx, new BeanBindingStrategy)
    data
  }

  /**
   * A test implementation of the WindowData interface.
   */
  private case class WindowDataImpl(
    @BeanProperty componentBuilderData: ComponentBuilderData = createComponentBuilderData(),
    @BeanProperty controller: Object = null,
    @BeanProperty height: Int = WindowData.UNDEFINED,
    @BeanProperty icon: Object = null,
    @BeanProperty menuBar: Object = null,
    @BeanProperty title: String = null,
    @BeanProperty width: Int = WindowData.UNDEFINED,
    @BeanProperty xPos: Int = WindowData.UNDEFINED,
    @BeanProperty yPos: Int = WindowData.UNDEFINED,
    @BooleanBeanProperty autoClose: Boolean = false,
    @BooleanBeanProperty center: Boolean = false,
    @BooleanBeanProperty closable: Boolean = false,
    @BooleanBeanProperty closeOnEsc: Boolean = false,
    @BooleanBeanProperty iconifiable: Boolean = false,
    @BooleanBeanProperty maximizable: Boolean = false,
    @BooleanBeanProperty resizable: Boolean = false)
    extends WindowData
}

object TestJavaFxWindowManager {
  /** A set with style sheet URLs to be added to newly created Scene objects. */
  val StyleSheets: Set[String] = Set("style1.css", "otherStyle.css")

  /**
   * The test instance. It has to be created once because it initializes the
   * Java FX platform. If multiple instances are created, exceptions are thrown.
   */
  private var initializedManager: JavaFxWindowManager = _

  @BeforeClass def setUpBeforeClass(): Unit = {
    val provider = createStyleSheetProvider()
    initializedManager = new JavaFxWindowManager(provider)
  }

  /**
    * Creates a new style sheet provider.
    * @return the new provider
    */
  private def createStyleSheetProvider(): StyleSheetProvider =
    new StyleSheetProvider(StyleSheets mkString ",", null)
}
