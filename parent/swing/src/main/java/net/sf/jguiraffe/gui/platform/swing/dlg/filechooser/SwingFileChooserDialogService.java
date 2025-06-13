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
package net.sf.jguiraffe.gui.platform.swing.dlg.filechooser;

import java.awt.Component;
import java.io.File;
import java.util.Arrays;

import javax.swing.JFileChooser;

import net.sf.jguiraffe.gui.app.ApplicationContext;
import net.sf.jguiraffe.gui.dlg.AbstractDialogOptions;
import net.sf.jguiraffe.gui.dlg.filechooser.AbstractFileChooserOptions;
import net.sf.jguiraffe.gui.dlg.filechooser.DirectoryChooserOptions;
import net.sf.jguiraffe.gui.dlg.filechooser.FileChooserDialogService;
import net.sf.jguiraffe.gui.dlg.filechooser.FileChooserOptions;
import net.sf.jguiraffe.gui.dlg.filechooser.FileExtensionFilter;
import net.sf.jguiraffe.gui.dlg.filechooser.MultiFileChooserOptions;
import net.sf.jguiraffe.gui.platform.swing.builder.window.SwingWindow;

/**
 * <p>
 * The Swing-specific implementation of the file chooser dialog service.
 * </p>
 * <p>
 * This implementation uses the {@code JFileChooser} class of Swing to display
 * dialogs to choose files or directories.
 * </p>
 *
 * @since 1.4
 */
public class SwingFileChooserDialogService implements FileChooserDialogService
{
    /** Stores the application context. */
    private final ApplicationContext applicationContext;

    /**
     * Creates a new instance of {@code SwingFileChooserDialogService} and
     * initializes it with the given application context.
     *
     * @param appCtx the application context
     */
    public SwingFileChooserDialogService(ApplicationContext appCtx)
    {
        applicationContext = appCtx;
    }

    /**
     * Returns the {@code ApplicationContext} used by service.
     *
     * @return the {@code ApplicationContext}
     */
    public ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }

    public void showOpenFileDialog(FileChooserOptions options)
    {
        JFileChooser fileChooser =
                createFileChooserForFileSelection(options, false);
        if (fileChooser.showOpenDialog(
                getParentWindow()) == JFileChooser.APPROVE_OPTION)
        {
            handleDialogResult(options, fileChooser.getSelectedFile());
        }
        else
        {
            handleDialogCanceled(options);
        }
    }

    public void showOpenMultiFileDialog(MultiFileChooserOptions options)
    {
        JFileChooser fileChooser =
                createFileChooserForFileSelection(options, true);
        if (fileChooser.showOpenDialog(
                getParentWindow()) == JFileChooser.APPROVE_OPTION)
        {
            handleDialogResult(options,
                    Arrays.asList(fileChooser.getSelectedFiles()));
        }
        else
        {
            handleDialogCanceled(options);
        }
    }

    public void showSaveFileDialog(FileChooserOptions options)
    {
        JFileChooser fileChooser =
                createFileChooserForFileSelection(options, false);
        if (fileChooser.showSaveDialog(
                getParentWindow()) == JFileChooser.APPROVE_OPTION)
        {
            handleDialogResult(options, fileChooser.getSelectedFile());
        }
        else
        {
            handleDialogCanceled(options);
        }
    }

    public void showChooseDirectoryDialog(DirectoryChooserOptions options)
    {
        JFileChooser fileChooser =
                initInitialDirectory(createInitializedFileChooser(options),
                        options.getInitialDirectory());
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(
                getParentWindow()) == JFileChooser.APPROVE_OPTION)
        {
            handleDialogResult(options, fileChooser.getSelectedFile());
        }
        else
        {
            handleDialogCanceled(options);
        }
    }

    /**
     * Creates the {@code JFileChooser} object that is used to display the
     * dialogs constructed by this class.
     *
     * @return the {@code JFileChooser}
     */
    protected JFileChooser createFileChooser()
    {
        return new JFileChooser();
    }

    /**
     * Creates a {@code JFileChooser} object and initializes it with default
     * settings from the given options object.
     *
     * @param options the dialog options
     * @return the initialized {@code JFileChooser}
     */
    private JFileChooser createInitializedFileChooser(
            AbstractDialogOptions<?, ?> options)
    {
        JFileChooser fileChooser = createFileChooser();
        fileChooser
                .setDialogTitle(options.resolveTitle(getApplicationContext()));
        return fileChooser;
    }

    /**
     * Creates an instance of {@code JFileChooser} and initializes it for
     * selecting a file. This method takes care about the initialization of the
     * properties determined by an {@code AbstractFileChooserOptions} instance.
     *
     * @param options the options
     * @param multiSelection flag for multi file selection
     * @return the initialized {@code JFileChooser}
     */
    private JFileChooser createFileChooserForFileSelection(
            AbstractFileChooserOptions<?, ?> options, boolean multiSelection)
    {
        JFileChooser fileChooser =
                initInitialDirectory(createInitializedFileChooser(options),
                        options.getInitialDirectory());
        if (options.getCurrentFile() != null)
        {
            fileChooser.setSelectedFile(options.getCurrentFile());
        }
        fileChooser.setMultiSelectionEnabled(multiSelection);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        for (FileExtensionFilter filter : options.getFilters())
        {
            fileChooser.addChoosableFileFilter(SwingFileExtensionFilter
                    .fromExtensionFilter(filter, getApplicationContext()));
        }
        return fileChooser;
    }

    /**
     * Initializes a file chooser's initial directory if it is defined.
     *
     * @param fileChooser the file chooser
     * @param directory the directory (can be <strong>null</strong>)
     * @return the same file chooser reference
     */
    private JFileChooser initInitialDirectory(JFileChooser fileChooser,
            File directory)
    {
        if (directory != null)
        {
            fileChooser.setCurrentDirectory(directory);
        }
        return fileChooser;
    }

    /**
     * Returns the parent window from the application context.
     *
     * @return the parent window
     */
    private Component getParentWindow()
    {
        return ((SwingWindow) getApplicationContext().getMainWindow())
                .getComponent();
    }

    /**
     * Reacts on a dialog closed by OK by passing the given result to the
     * callback defined in the options.
     *
     * @param options the options
     * @param result the dialog result
     * @param <T> the type of the dialog result
     */
    private static <T> void handleDialogResult(
            AbstractDialogOptions<T, ?> options, T result)
    {
        options.getResultCallback().onDialogResult(result, null);
    }

    /**
     * Reacts on a canceled dialog by invoking the corresponding callback from
     * the given options.
     *
     * @param options the options
     */
    private static void handleDialogCanceled(
            AbstractDialogOptions<?, ?> options)
    {
        options.getCancelInvoker().run();
    }
}
