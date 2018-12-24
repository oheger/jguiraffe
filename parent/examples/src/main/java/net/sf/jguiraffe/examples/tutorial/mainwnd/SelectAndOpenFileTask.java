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
package net.sf.jguiraffe.examples.tutorial.mainwnd;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import net.sf.jguiraffe.gui.app.TextResource;
import net.sf.jguiraffe.gui.dlg.DialogResultCallback;
import net.sf.jguiraffe.gui.dlg.filechooser.FileChooserDialogService;
import net.sf.jguiraffe.gui.dlg.filechooser.FileChooserOptions;
import net.sf.jguiraffe.gui.dlg.filechooser.FileExtensionFilter;

/**
 * <p>
 * A task class for selecting a file and opening it.
 * </p>
 * <p>
 * This task displays a file chooser dialog to select a file. If the user closes
 * this dialog with OK, the selected file is opened via the desktop mechanism.
 * </p>
 */
public class SelectAndOpenFileTask implements Runnable
{
    /** Filter to display all files. */
    private static final FileExtensionFilter FILTER_ALL =
            new FileExtensionFilter(
                    TextResource.fromResourceID("fc_filter_all"),
                    FileExtensionFilter.EXT_ALL_FILES);

    /** Filter to display image files. */
    private static final FileExtensionFilter FILTER_IMAGES =
            new FileExtensionFilter(
                    TextResource.fromResourceID("fc_filter_images"), "jpg",
                    "jpeg", "gif", "bmp", "png");

    /** Filter to display audio files. */
    private static final FileExtensionFilter FILTER_AUDIO =
            new FileExtensionFilter(
                    TextResource.fromResourceID("fc_filter_audio"), "mp3",
                    "ogg", "wav", "au");

    /** Filter to display text files. */
    private static final FileExtensionFilter FILTER_TEXT =
            new FileExtensionFilter(
                    TextResource.fromResourceID("fc_filter_text"), "txt", "doc",
                    "pdf");

    /** The list of special file extension filters. */
    private static final List<FileExtensionFilter> FILTERS =
            Arrays.asList(FILTER_ALL, FILTER_IMAGES, FILTER_AUDIO, FILTER_TEXT);

    /** The service for displaying file chooser dialogs. */
    private final FileChooserDialogService fileChooserService;

    /** The main window controller. */
    private final MainWndController controller;

    /**
     * Creates a new instance of {@code SelectAndOpenFileTask} and initializes
     * it with a reference to the file chooser service.
     *
     * @param service the file chooser service
     * @param ctrl the main window controller
     */
    public SelectAndOpenFileTask(FileChooserDialogService service,
            MainWndController ctrl)
    {
        fileChooserService = service;
        controller = ctrl;
    }

    @Override
    public void run()
    {
        DialogResultCallback<File, Void> callback =
                new DialogResultCallback<File, Void>()
                {
                    @Override
                    public void onDialogResult(File result, Void data)
                    {
                        openDialogResult(result);
                    }
                };
        FileChooserOptions options =
                new FileChooserOptions(callback).setTitleResource("fc_title")
                        .setFilters(FILTERS).setInitialDirectory(
                                new File(System.getProperty("java.io.tmpdir")));
        fileChooserService.showOpenFileDialog(options);
    }

    /**
     * Opens the file that has been selected in the file chooser dialog.
     *
     * @param file the result file
     */
    private void openDialogResult(final File file)
    {
        OpenDesktopTask openTask = new OpenDesktopTask(controller)
        {
            @Override
            public File getSelectedFile()
            {
                return file;
            }
        };
        openTask.run();
    }
}
