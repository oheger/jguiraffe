/*
 * Copyright 2006-2022 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.platform.javafx.dlg.filechooser

import java.io.File

import javafx.collections.FXCollections
import javafx.stage.{DirectoryChooser, FileChooser, Stage}
import net.sf.jguiraffe.gui.app.{ApplicationContext, TextResource}
import net.sf.jguiraffe.gui.builder.window.{Window, WindowWrapper}
import net.sf.jguiraffe.gui.dlg.filechooser.{DirectoryChooserOptions, FileChooserOptions, FileExtensionFilter, MultiFileChooserOptions}
import net.sf.jguiraffe.gui.dlg.{DialogCanceledCallback, DialogResultCallback}
import net.sf.jguiraffe.gui.platform.javafx.JavaFxTestHelper
import org.easymock.EasyMock
import org.junit.Assert._
import org.junit.runner.RunWith
import org.junit.{Before, Test}
import org.powermock.api.easymock.PowerMock
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.scalatest.easymock.EasyMockSugar
import org.scalatest.junit.JUnitSuite

import scala.reflect.ClassTag

trait WrappingWindow extends Window with WindowWrapper

object PMTestJavaFxFileChooserDialogService {
  /** A string representing data to be passed to callbacks. */
  private val ContextData = "Dialog Context Data"

  /** Suffix for a text resource indicating a resolved resource key. */
  private val ResolvedKey = "_resolved"

  /** Resource key for the dialog title. */
  private val KeyTitle = "DialogTitle"

  /** A test filter for images. */
  private val FilterImages =
    new FileExtensionFilter(TextResource.fromResourceID("pictures"), "gif", "jpg")

  /** A test filter for music. */
  private val FilterMusic =
    new FileExtensionFilter(TextResource.fromResourceID("music"), "mp3", "ogg")

  /**
    * Simulates a resource resolve operation.
    *
    * @param key the resource key
    * @return the resolved resource text
    */
  private def resource(key: AnyRef): String = key + ResolvedKey

  /**
    * Checks a filter passed to a file chooser.
    *
    * @param expected the expected filter definition
    * @param actual   the actual filter to be checked
    */
  private def checkFilter(expected: FileExtensionFilter,
                          actual: FileChooser.ExtensionFilter): Unit = {
    assertEquals("Wrong filter name", resource(expected.getDescription.getResourceID),
      actual.getDescription)
    import collection.JavaConverters._
    val expExtensions = expected.getExtensions.asScala.map(e => "*." + e).asJava
    assertEquals("Wrong extensions", expExtensions, actual.getExtensions)
  }

  /**
    * Checks whether the given chooser object has been added the expected
    * extension filters.
    *
    * @param chooser the chooser
    * @param filters the expected filters
    */
  private def checkExtensionFilters(chooser: FileChooser, filters: FileExtensionFilter*): Unit = {
    assertEquals("Wrong number of filters", filters.size,
      chooser.getExtensionFilters.size())
    val itFilter = chooser.getExtensionFilters.iterator()
    filters foreach { f => checkFilter(f, itFilter.next()) }
  }
}

/**
  * Test class for ''JavaFxFileChooserDialogService''.
  */
@RunWith(classOf[PowerMockRunner])
@PrepareForTest(Array(classOf[FileChooser], classOf[DirectoryChooser]))
class PMTestJavaFxFileChooserDialogService extends JUnitSuite with EasyMockSugar {

  import PMTestJavaFxFileChooserDialogService._

  /** Mock for the application context. */
  private var applicationContext: ApplicationContext = _

  /** Mock for the application's main window. */
  private var mainWindow: Stage = _

  @Before def setUp(): Unit = {
    mainWindow = mock[Stage]
    applicationContext = createApplicationContextMock(mainWindow)
  }

  /**
    * Creates a mock for the application context and initializes it to
    * support resource resolving. A resource ID is resolved by appending a
    * special suffix.
    *
    * @param mainWnd the application's main window
    * @return the initialized mock for the application context
    */
  private def createApplicationContextMock(mainWnd: Stage): ApplicationContext = {
    val appCtx = mock[ApplicationContext]
    EasyMock.expect(appCtx.getResourceText(EasyMock.anyObject(classOf[String])))
      .andAnswer(() => {
        val resId = EasyMock.getCurrentArguments()(0)
        resId + ResolvedKey
      }).anyTimes()
    val fxMainWnd = JavaFxTestHelper.createJavaFxWindowMock(mainWnd)
    EasyMock.expect(appCtx.getMainWindow).andReturn(fxMainWnd)
    EasyMock.replay(appCtx)
    appCtx
  }

  /**
    * Creates a mock for a result callback of the given type.
    *
    * @param c the class tag
    * @tparam R the type of the result
    * @return the mock for the result callback
    */
  private def createResultCallback[R](implicit c: ClassTag[R]): DialogResultCallback[R, String] =
    mock[DialogResultCallback[R, String]]

  /**
    * Creates a mock for a canceled callback.
    *
    * @return the mock for the callback
    */
  private def createCancelCallback(): DialogCanceledCallback[String] =
    mock[DialogCanceledCallback[String]]

  /**
    * Tests the default function for creating a file chooser.
    */
  @Test def testDefaultFileChooserCreatorFunction(): Unit = {
    val service = new JavaFxFileChooserDialogService(applicationContext)

    assertSame("Wrong application context", applicationContext,
      service.applicationContext)
    val chooser = service.fileChooserCreator()
    assertNotNull("No file chooser", chooser)
  }

  /**
    * Tests the default function for creating a directory chooser.
    */
  @Test def testDefaultDirectoryChooserCreatorFunction(): Unit = {
    val service = new JavaFxFileChooserDialogService(applicationContext)

    val chooser = service.dirChooserCreator()
    assertNotNull("No directory chooser", chooser)
  }

  /**
    * Tests whether a directory chooser dialog can be used to select a
    * directory.
    */
  @Test def testDirectoryChooserCanBeDisplayed(): Unit = {
    val ResultDir = new File("selectedDir")
    val InitDir = new File("initDir")
    val resultCallback = createResultCallback[File]
    resultCallback.onDialogResult(ResultDir, ContextData)
    val chooser = PowerMock.createMock(classOf[DirectoryChooser])
    chooser.setTitle(resource(KeyTitle))
    chooser.setInitialDirectory(InitDir)
    EasyMock.expect(chooser.showDialog(mainWindow)).andReturn(ResultDir)
    EasyMock.replay(chooser, resultCallback)
    val options = new DirectoryChooserOptions(resultCallback, ContextData)
      .setTitleResource(KeyTitle)
      .setInitialDirectory(InitDir)
    val service = new JavaFxFileChooserDialogService(applicationContext,
      null, () => chooser)

    service.showChooseDirectoryDialog(options)
    EasyMock.verify(resultCallback, chooser)
  }

  /**
    * Tests whether a directory chooser dialog with a minimum number of
    * properties can be displayed that is canceled.
    */
  @Test def testDirectoryChooserCanceled(): Unit = {
    val resultCallback = createResultCallback[File]
    val canceledCallback = createCancelCallback()
    canceledCallback.onDialogCanceled(ContextData)
    val chooser = PowerMock.createMock(classOf[DirectoryChooser])
    EasyMock.expect(chooser.showDialog(mainWindow)).andReturn(null)
    EasyMock.replay(resultCallback, canceledCallback, chooser)
    val options = new DirectoryChooserOptions(resultCallback)
    val service = new JavaFxFileChooserDialogService(applicationContext,
      null, () => chooser)

    service.showChooseDirectoryDialog(options)
    EasyMock.verify(resultCallback, chooser)
  }

  /**
    * Creates a mock for a file chooser that is prepared for some default
    * operations.
    *
    * @return the initialized mock
    */
  private def createFileChooserMock(): FileChooser = {
    val chooser = PowerMock.createMock(classOf[FileChooser])
    val filterList = FXCollections.observableArrayList[FileChooser.ExtensionFilter]
    EasyMock.expect(chooser.getExtensionFilters).andReturn(filterList).anyTimes()
    chooser
  }

  /**
    * Prepares the given mock for a file chooser to expect a selection of an
    * extension filter.
    *
    * @param chooser the mock file chooser
    * @param filter  the expected filter
    * @return the same chooser
    */
  private def expectSelectedFilter(chooser: FileChooser, filter: FileExtensionFilter):
  FileChooser = {
    chooser.setSelectedExtensionFilter(EasyMock.anyObject())
    EasyMock.expectLastCall().andAnswer(() => {
      val selFilter = EasyMock.getCurrentArguments()(0).asInstanceOf[FileChooser.ExtensionFilter]
      checkFilter(filter, selFilter)
      null
    })
    chooser
  }

  /**
    * Tests whether a dialog to open a single file can be displayed.
    */
  @Test def testSingleFileChooserCanBeDisplayed(): Unit = {
    val ResultFile = new File("selectedFile.txt")
    val InitDir = new File("initDir")
    val InitFile = new File("initFile.dat")
    val resultCallback = createResultCallback[File]
    resultCallback.onDialogResult(ResultFile, ContextData)
    val chooser = expectSelectedFilter(createFileChooserMock(), FilterMusic)
    chooser.setTitle(resource(KeyTitle))
    chooser.setInitialDirectory(InitDir)
    chooser.setInitialFileName(InitFile.getName)
    EasyMock.expect(chooser.showOpenDialog(mainWindow)).andReturn(ResultFile)
    EasyMock.replay(resultCallback, chooser)
    val options = new FileChooserOptions(resultCallback, ContextData)
      .setCurrentFile(InitFile)
      .setCurrentFilterIndex(1)
      .setFilters(FilterImages, FilterMusic)
      .setInitialDirectory(InitDir)
      .setTitleResource(KeyTitle)
    val service = new JavaFxFileChooserDialogService(applicationContext,
      () => chooser, null)

    service.showOpenFileDialog(options)
    EasyMock.verify(resultCallback, chooser)
    checkExtensionFilters(chooser, FilterImages, FilterMusic)
  }

  /**
    * Tests whether a file chooser dialog that is canceled is handled
    * correctly.
    */
  @Test def testSingleFileChooserCanceled(): Unit = {
    val resultCallback = createResultCallback[File]
    val canceledCallback = createCancelCallback()
    canceledCallback.onDialogCanceled(ContextData)
    val chooser = createFileChooserMock()
    EasyMock.expect(chooser.showOpenDialog(mainWindow)).andReturn(null)
    EasyMock.replay(resultCallback, canceledCallback, chooser)
    val options = new FileChooserOptions(resultCallback)
      .setCanceledCallback(canceledCallback, ContextData)
      .setFilters(FilterMusic)
      .setCurrentFilterIndex(1)
    val service = new JavaFxFileChooserDialogService(applicationContext,
      () => chooser, null)

    service.showOpenFileDialog(options)
    EasyMock.verify(canceledCallback, chooser)
  }

  /**
    * Tests whether multiple files can be selected by a dialog.
    */
  @Test def testMultiFileChooserCanBeDisplayed(): Unit = {
    val ResultFiles = java.util.Arrays.asList(new File("res1.txt"),
      new File("res2.doc"))
    val resultCallback = createResultCallback[java.util.List[File]]
    resultCallback.onDialogResult(ResultFiles, ContextData)
    val chooser = createFileChooserMock()
    chooser.setTitle(resource(KeyTitle))
    EasyMock.expect(chooser.showOpenMultipleDialog(mainWindow)).andReturn(ResultFiles)
    EasyMock.replay(resultCallback, chooser)
    val options = new MultiFileChooserOptions(resultCallback, ContextData)
      .setTitleResource(KeyTitle)
      .setFilters(FilterMusic)
      .setCurrentFilterIndex(-1)
    val service = new JavaFxFileChooserDialogService(applicationContext,
      () => chooser, null)

    service.showOpenMultiFileDialog(options)
    EasyMock.verify(resultCallback, chooser)
    checkExtensionFilters(chooser, FilterMusic)
  }

  /**
    * Tests whether a dialog for saving a file can be displayed.
    */
  @Test def testFileSaveDialogCanBeDisplayed(): Unit = {
    val ResultFile = new File("save.dat")
    val resultCallback = createResultCallback[File]
    resultCallback.onDialogResult(ResultFile, ContextData)
    val canceledCallback = createCancelCallback()
    val chooser = createFileChooserMock()
    EasyMock.expect(chooser.showSaveDialog(mainWindow)).andReturn(ResultFile)
    EasyMock.replay(resultCallback, canceledCallback, chooser)
    val options = new FileChooserOptions(resultCallback, ContextData)
    val service = new JavaFxFileChooserDialogService(applicationContext,
      () => chooser, null)

    service.showSaveFileDialog(options)
    EasyMock.verify(resultCallback, chooser)
    checkExtensionFilters(chooser)
  }
}
