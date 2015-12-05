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

import javafx.scene.Group
import javafx.scene.control.MenuBar
import javafx.scene.text.Text
import javafx.stage.{Stage, Modality}

import net.sf.jguiraffe.gui.builder.components.{ComponentBuilderData, FormBuilderException}
import net.sf.jguiraffe.gui.builder.window.{Window, WindowBuilderData, WindowData}
import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import net.sf.jguiraffe.gui.platform.javafx.layout.{ContainerWrapper, JavaFxUnitSizeHandler}
import org.apache.commons.jelly.JellyContext
import org.easymock.EasyMock
import org.junit.Assert._
import org.junit.{Before, BeforeClass, Test}
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.EasyMockSugar

import scala.beans.{BeanProperty, BooleanBeanProperty}

/**
 * Test class for ''JavaFxWindowManager''.
 */
class TestJavaFxWindowManager extends JUnitSuite with EasyMockSugar {
  /** The manager to be tested. */
  private var manager: JavaFxWindowManager = _

  @Before def setUp() {
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
   * Tests whether an initial and another stage can be created.
   */
  @Test def testCreateStages() {
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
  @Test def testInitScene() {
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
  @Test def testInitTitle() {
    val title = "A window title"
    val builderData = createWindowBuilderData()
    val windowData = new WindowDataImpl(title = title)
    val wndNew = manager.createFrame(builderData, windowData, null)
    val wnd = manager.createFrame(builderData, windowData, wndNew)
    assertEquals("Wrong title", title, wnd.getTitle)
  }

  /**
   * Tests whether the window controller is set.
   */
  @Test def testInitController() {
    val builderData = createWindowBuilderData()
    val windowData = new WindowDataImpl(controller = this)
    val wndNew = manager.createFrame(builderData, windowData, null)
    val wnd = manager.createFrame(builderData, windowData, wndNew)
    assertEquals("Wrong controller", this, wnd.getWindowController)
  }

  /**
   * Tests whether the window's bounds are correctly initialized.
   */
  @Test def testInitBounds() {
    val builderData = createWindowBuilderData()
    val windowData = new WindowDataImpl(xPos = 10, yPos = 20, width = 300,
      height = 200)
    val wndNew = manager.createFrame(builderData, windowData, null)
    val wnd = manager.createFrame(builderData, windowData, wndNew)
    assertEquals("Wrong xPos", windowData.xPos, wnd.getXPos)
    assertEquals("Wrong yPos", windowData.yPos, wnd.getYPos)
    assertEquals("Wrong width", windowData.width, wnd.getWidth)
    assertEquals("Wrong height", windowData.height, wnd.getHeight)
  }

  /**
   * Helper method for testing that the auto close flag is set correctly.
   * @param autoClose the auto close flag to be set
   */
  private def checkAutoCloseFlag(autoClose: Boolean): Unit = {
    val builderData = createWindowBuilderData()
    val windowData = new WindowDataImpl(autoClose = autoClose)
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
   * Helper method for creating a window with the specified menu bar.
   * @param menuBar the menu bar
   * @return the window
   */
  private def createWindowWithMenuBar(menuBar: AnyRef): Window = {
    val builderData = createWindowBuilderData()
    val windowData = new WindowDataImpl(menuBar = menuBar)
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
  @Test def testCreateFrameModality() {
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
  @Test def testSizeHandlerInRootContainer() {
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
  private def checkCreateDialog(modal: Boolean, expModality: Modality) {
    val builderData = createWindowBuilderData()
    val windowData = new WindowDataImpl(title = "some title")
    val wndNew = manager.createDialog(builderData, windowData, modal, null)
    val wnd = manager.createDialog(builderData, windowData, modal,
      wndNew).asInstanceOf[JavaFxWindow]
    assertEquals("Wrong modality", expModality, wnd.stage.getModality)
    assertEquals("Not initialized", "some title", wnd.getTitle)
  }

  /**
   * Tests whether a non-modal dialog window can be created.
   */
  @Test def testCreateDialogNonModal() {
    checkCreateDialog(modal = false, Modality.NONE)
  }

  /**
   * Tests whether a modal dialog window can be created.
   */
  @Test def testCreateDialogModal() {
    checkCreateDialog(modal = true, Modality.APPLICATION_MODAL)
  }

  /**
   * Tests the creation of an internal frame.
   */
  @Test def testCreateInternalFrame() {
    val builderData = createWindowBuilderData()
    val windowData = new WindowDataImpl(title = "some title")
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
   * A test implementation of the WindowData interface.
   */
  private case class WindowDataImpl(
    @BeanProperty componentBuilderData: ComponentBuilderData = null,
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
  val StyleSheets = Set("style1.css", "otherStyle.css")

  /**
   * The test instance. It has to be created once because it initializes the
   * Java FX platform. If multiple instances are created, exceptions are thrown.
   */
  private var initializedManager: JavaFxWindowManager = _

  @BeforeClass def setUpBeforeClass() {
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
