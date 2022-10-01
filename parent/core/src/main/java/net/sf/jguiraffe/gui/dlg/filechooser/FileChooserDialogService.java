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
package net.sf.jguiraffe.gui.dlg.filechooser;

/**
 * <p>
 * An interface for a service that allows the creation of standard dialogs for
 * choosing files and directories.
 * </p>
 * <p>
 * This interface provides a number of methods to generate dialogs related to
 * selecting files, e.g. for opening a single or multiple files, or for saving a
 * file. The dialogs can be configured using corresponding option objects.
 * </p>
 *
 * @since 1.4
 */
public interface FileChooserDialogService
{
    /**
     * Displays a dialog for selecting a single file to be opened. The dialog is
     * configured using the given options object. When the user closes the
     * dialog by selecting a file the callback defined in the options object is
     * invoked with this file.
     *
     * @param options an object with options to customize the dialog
     */
    void showOpenFileDialog(FileChooserOptions options);

    /**
     * Displays a dialog for selecting multiple files to be opened. The dialog
     * is configured using the given options object. When the user closes the
     * dialog via the OK button a list with the files selected is passed to the
     * callback defined in the options object.
     *
     * @param options an object with options to customize the dialog
     */
    void showOpenMultiFileDialog(MultiFileChooserOptions options);

    /**
     * Displays a dialog for selecting a file for a save operation. The dialog
     * is configured using the given options object. When the user closes the
     * dialog by selecting a file to be saved the callback defined in the
     * options object is invoked with this file.
     *
     * @param options an object with options to customize the dialog
     */
    void showSaveFileDialog(FileChooserOptions options);

    /**
     * Displays a dialog for selecting a directory. The dialog is configured
     * using the given options object. When the user closes the dialog by
     * selecting a directory the callback defined in the options object is
     * invoked with this directory.
     *
     * @param options an object with options to customize the dialog
     */
    void showChooseDirectoryDialog(DirectoryChooserOptions options);
}
