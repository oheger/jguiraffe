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
package net.sf.jguiraffe.gui.platform.javafx.dlg.filechooser

import javafx.stage.{DirectoryChooser, FileChooser, Window}
import net.sf.jguiraffe.gui.app.ApplicationContext
import net.sf.jguiraffe.gui.builder.window.WindowUtils
import net.sf.jguiraffe.gui.dlg.AbstractDialogOptions
import net.sf.jguiraffe.gui.dlg.filechooser._

/**
  * The JavaFX specific implementation of a service for displaying file
  * selection dialogs.
  *
  * This class maps the methods of the [[FileChooserDialogService]] interface
  * to invocations of the JavaFX ''FileChooser'' and
  * ''DirectoryChooser'' classes.
  *
  * @param applicationContext the current ''ApplicationContext''
  * @param fileChooserCreator a function for creating a file chooser
  * @param dirChooserCreator  a function for creating a directory chooser
  */
class JavaFxFileChooserDialogService(val applicationContext: ApplicationContext,
                                     val fileChooserCreator: () => FileChooser,
                                     val dirChooserCreator: () => DirectoryChooser) extends
  FileChooserDialogService {
  /** A reference to the application's main window. */
  private lazy val mainWindow = WindowUtils.getPlatformWindow(applicationContext.getMainWindow)
    .asInstanceOf[Window]

  /**
    * Creates a new instance of ''JavaFxFileChooserDialogService'' with default
    * functions for creating chooser objects.
    *
    * @param applicationContext the current ''ApplicationContext''
    * @return the new instance
    */
  def this(applicationContext: ApplicationContext) = this(applicationContext,
    () => new FileChooser, () => new DirectoryChooser)

  import collection.JavaConverters._

  override def showOpenFileDialog(options: FileChooserOptions): Unit = {
    showFileSelectionDialog(options)(_.showOpenDialog(_))
  }

  override def showOpenMultiFileDialog(options: MultiFileChooserOptions): Unit = {
    showFileSelectionDialog(options)(_.showOpenMultipleDialog(_))
  }

  override def showSaveFileDialog(options: FileChooserOptions): Unit = {
    showFileSelectionDialog(options)(_.showSaveDialog(_))
  }

  override def showChooseDirectoryDialog(options: DirectoryChooserOptions): Unit = {
    val chooser = dirChooserCreator()
    initIfPresent(options.resolveTitle(applicationContext))(chooser.setTitle)
    initIfPresent(options.getInitialDirectory)(chooser.setInitialDirectory)
    val directory = chooser.showDialog(mainWindow)
    invokeResultCallback(options, directory)
  }

  /**
    * Generic function to initialize a ''FileChooser'' based on the options
    * provided, to display it, and to propagate the dialog's result. The steps
    * to initialize a file chooser and process its result are always the same;
    * only the operation to be invoked on the ''FileChooser'' instance depends
    * on the current use case. This function handles the common logic and
    * extracts the invocation of the file chooser to a function.
    *
    * @param options the options defining the chooser dialog
    * @param f       the function to invoke the chooser
    * @tparam T the type of the dialog result
    */
  private def showFileSelectionDialog[T](options: AbstractFileChooserOptions[T, _])
                                        (f: (FileChooser, Window) => T): Unit = {
    val chooser = fileChooserCreator()
    initIfPresent(options.resolveTitle(applicationContext))(chooser.setTitle)
    initIfPresent(options.getInitialDirectory)(chooser.setInitialDirectory)
    initIfPresent(options.getCurrentFile)(f => chooser.setInitialFileName(f.getName))
    options.getFilters.asScala foreach { filter =>
      chooser.getExtensionFilters.add(convertFilter(filter))
    }
    if (options.getCurrentFilterIndex < chooser.getExtensionFilters.size() &&
      options.getCurrentFilterIndex >= 0) {
      chooser.setSelectedExtensionFilter(
        chooser.getExtensionFilters.get(options.getCurrentFilterIndex))
    }
    val result = f(chooser, mainWindow)
    invokeResultCallback(options, result)
  }

  /**
    * Convenience method for initializing a property only if it is not null.
    * The passed in property is checked whether it is null. If this is not the
    * case, the given initialization function is called.
    *
    * @param prop the property
    * @param f    the initialization function
    * @tparam T the type of the property
    */
  private def initIfPresent[T <: AnyRef](prop: T)(f: T => Unit): Unit =
    if (prop != null) f(prop)

  /**
    * Handles the result of a dialog invocation and invokes either the result
    * callback or the canceled callback.
    *
    * @param options the dialog options
    * @param result  the result; '''null''' means a cancellation
    * @tparam T the type of the dialog result
    */
  private def invokeResultCallback[T](options: AbstractDialogOptions[T, _], result: T): Unit = {
    if (result != null)
      options.getResultCallback.onDialogResult(result, null)
    else options.getCancelInvoker.run()
  }

  /**
    * Maps a filter defined in dialog options to an ''ExtensionFilter'' used by
    * the ''FileChooser'' class.
    *
    * @param filter the filter from the options
    * @return the filter for the file chooser
    */
  private def convertFilter(filter: FileExtensionFilter): FileChooser.ExtensionFilter = {
    val description = filter.getDescription.resolveText(applicationContext)
    val extensions = filter.getExtensions.asScala.map(e => "*." + e).asJava
    val extensionFilter = new FileChooser.ExtensionFilter(description, extensions)
    extensionFilter
  }
}
