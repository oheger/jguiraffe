/*
 * Copyright 2006-2021 The JGUIraffe Team.
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

import static org.junit.Assert.assertNotNull;

import java.awt.Component;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import net.sf.jguiraffe.gui.app.TextResource;
import net.sf.jguiraffe.gui.dlg.DialogCanceledCallback;
import net.sf.jguiraffe.gui.dlg.filechooser.FileChooserOptions;
import net.sf.jguiraffe.gui.dlg.filechooser.FileExtensionFilter;
import net.sf.jguiraffe.gui.dlg.filechooser.MultiFileChooserOptions;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import net.sf.jguiraffe.gui.app.ApplicationContext;
import net.sf.jguiraffe.gui.dlg.DialogResultCallback;
import net.sf.jguiraffe.gui.dlg.filechooser.DirectoryChooserOptions;
import net.sf.jguiraffe.gui.platform.swing.builder.window.SwingWindow;

/**
 * Test class for {@code SwingFileChooserDialogService}.
 */
public class TestSwingFileChooserDialogService
{
    /** The text for the title. */
    private static final String TITLE = "Choose a file";

    /** The context data for the result callback. */
    private static final String DATA = "dialog context data";

    /** A test extension filter. */
    private static final FileExtensionFilter FILTER_IMAGES =
            new FileExtensionFilter(TextResource.fromText("images"), "jpg",
                    "gif");

    /** Another extension filter. */
    private static final FileExtensionFilter FILTER_MUSIC =
            new FileExtensionFilter(TextResource.fromText("music"), "mp3",
                    "ogg");

    /** Mock for the application context. */
    private ApplicationContext applicationContext;

    /** The component representing the main window. */
    private Component mainWindowComponent;

    /** Mock for the main application window. */
    private SwingWindow mainWindow;

    /** Mock for the underlying file chooser. */
    private JFileChooser fileChooser;

    /** The instance to be tested. */
    private SwingFileChooserDialogServiceTestImpl dialogService;

    @Before
    public void setUp()
    {
        applicationContext = EasyMock.createMock(ApplicationContext.class);
        mainWindow = EasyMock.createNiceMock(SwingWindow.class);
        fileChooser = EasyMock.createMock(JFileChooser.class);
        mainWindowComponent = new JFrame();
        EasyMock.expect(applicationContext.getMainWindow())
                .andReturn(mainWindow);
        EasyMock.expect(mainWindow.getComponent())
                .andReturn(mainWindowComponent);
        fileChooser.setDialogTitle(TITLE);
        dialogService =
                new SwingFileChooserDialogServiceTestImpl(applicationContext);
    }

    /**
     * Creates a mock for a dialog result callback.
     *
     * @param <T> the type of the result
     * @return the mock
     */
    private static <T> DialogResultCallback<T, String> createResultCallback()
    {
        @SuppressWarnings("unchecked")
        DialogResultCallback<T, String> callback =
                EasyMock.createMock(DialogResultCallback.class);
        return callback;
    }

    /**
     * Creates a mock for a dialog canceled callback.
     *
     * @return the callback mock
     */
    private static DialogCanceledCallback<String> createCanceledCallback()
    {
        @SuppressWarnings("unchecked")
        DialogCanceledCallback<String> callback =
                EasyMock.createMock(DialogCanceledCallback.class);
        return callback;
    }

    /**
     * Replays the mocks involved in this test and the given additional mocks.
     *
     * @param mocks additional mocks to be replayed
     */
    private void replayMocks(Object... mocks)
    {
        EasyMock.replay(applicationContext, mainWindow, fileChooser);
        if (mocks.length > 0)
        {
            EasyMock.replay(mocks);
        }
    }

    /**
     * Tests whether a correct file chooser dialog is created.
     */
    @Test
    public void testCorrectFileChooserIsCreated()
    {
        SwingFileChooserDialogService service =
                new SwingFileChooserDialogService(applicationContext);

        assertNotNull("No file chooser", service.createFileChooser());
    }

    /**
     * Tests whether a directory chooser dialog can be opened.
     */
    @Test
    public void testDirectoryChooserDialogCanBeOpened()
    {
        File initialDirectory = new File("initial");
        File result = new File("result");
        DialogResultCallback<File, String> resultCallback =
                createResultCallback();
        resultCallback.onDialogResult(result, DATA);
        fileChooser.setCurrentDirectory(initialDirectory);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        EasyMock.expect(fileChooser.getSelectedFile()).andReturn(result);
        EasyMock.expect(fileChooser.showOpenDialog(mainWindowComponent))
                .andReturn(JFileChooser.APPROVE_OPTION);
        replayMocks(resultCallback);
        DirectoryChooserOptions options =
                new DirectoryChooserOptions(resultCallback, DATA)
                        .setTitle(TITLE).setInitialDirectory(initialDirectory);

        dialogService.showChooseDirectoryDialog(options);
        EasyMock.verify(resultCallback, fileChooser);
    }

    /**
     * Expects that the file filters have been applied to the file chooser.
     */
    private void expectFileFilters()
    {
        expectFileFilter(FILTER_IMAGES);
        expectFileFilter(FILTER_MUSIC);
    }

    /**
     * Expects that the given file filter is applied to the file chooser.
     *
     * @param filter the filter
     */
    private void expectFileFilter(FileExtensionFilter filter)
    {
        SwingFileExtensionFilter swingFilter = SwingFileExtensionFilter
                .fromExtensionFilter(filter, applicationContext);
        fileChooser.addChoosableFileFilter(swingFilter);
    }

    /**
     * Tests whether a canceled directory dialog is handled correctly.
     */
    @Test
    public void testDirectoryChooserCanceled()
    {
        DialogResultCallback<File, String> resultCallback =
                createResultCallback();
        DialogCanceledCallback<String> canceledCallback =
                createCanceledCallback();
        canceledCallback.onDialogCanceled(DATA);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        EasyMock.expect(fileChooser.showOpenDialog(mainWindowComponent))
                .andReturn(JFileChooser.CANCEL_OPTION);
        replayMocks(resultCallback, canceledCallback);
        DirectoryChooserOptions options =
                new DirectoryChooserOptions(resultCallback).setTitle(TITLE)
                        .setCanceledCallback(canceledCallback, DATA);

        dialogService.showChooseDirectoryDialog(options);
        EasyMock.verify(resultCallback, canceledCallback, fileChooser);
    }

    /**
     * Tests whether a dialog to open a single file can be displayed.
     */
    @Test
    public void testOpenSingleFileDialog()
    {
        File initialDirectory = new File("initialDir");
        File currentFile = new File("current.txt");
        File result = new File("resultFile");
        DialogResultCallback<File, String> resultCallback =
                createResultCallback();
        resultCallback.onDialogResult(result, DATA);
        fileChooser.setCurrentDirectory(initialDirectory);
        fileChooser.setSelectedFile(currentFile);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setMultiSelectionEnabled(false);
        expectFileFilters();
        EasyMock.expect(fileChooser.getSelectedFile()).andReturn(result);
        EasyMock.expect(fileChooser.showOpenDialog(mainWindowComponent))
                .andReturn(JFileChooser.APPROVE_OPTION);
        replayMocks(resultCallback);
        FileChooserOptions options =
                new FileChooserOptions(resultCallback, DATA).setTitle(TITLE)
                        .setInitialDirectory(initialDirectory)
                        .setCurrentFile(currentFile)
                        .setFilters(FILTER_IMAGES, FILTER_MUSIC);

        dialogService.showOpenFileDialog(options);
        EasyMock.verify(resultCallback, fileChooser);
    }

    /**
     * Tests whether a cancellation of the open single file dialog is handled.
     */
    @Test
    public void testOpenSingleFileDialogCanceled()
    {
        DialogResultCallback<File, String> resultCallback =
                createResultCallback();
        DialogCanceledCallback<String> canceledCallback =
                createCanceledCallback();
        canceledCallback.onDialogCanceled(DATA);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setMultiSelectionEnabled(false);
        EasyMock.expect(fileChooser.showOpenDialog(mainWindowComponent))
                .andReturn(JFileChooser.CANCEL_OPTION);
        FileChooserOptions options = new FileChooserOptions(resultCallback)
                .setCanceledCallback(canceledCallback, DATA).setTitle(TITLE);
        replayMocks(resultCallback, canceledCallback);

        dialogService.showOpenFileDialog(options);
        EasyMock.verify(canceledCallback, fileChooser);
    }

    /**
     * Tests whether a dialog to open multiple files can be displayed.
     */
    @Test
    public void testOpenMultiFileDialog()
    {
        File initialDirectory = new File("initialDir");
        File currentFile = new File("current.txt");
        File result1 = new File("resultFile1.txt");
        File result2 = new File("resultFile2.doc");
        DialogResultCallback<List<File>, String> resultCallback =
                createResultCallback();
        resultCallback.onDialogResult(Arrays.asList(result1, result2), DATA);
        fileChooser.setCurrentDirectory(initialDirectory);
        fileChooser.setSelectedFile(currentFile);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setMultiSelectionEnabled(true);
        expectFileFilters();
        EasyMock.expect(fileChooser.getSelectedFiles()).andReturn(new File[] {
                result1, result2
        });
        EasyMock.expect(fileChooser.showOpenDialog(mainWindowComponent))
                .andReturn(JFileChooser.APPROVE_OPTION);
        replayMocks(resultCallback);
        MultiFileChooserOptions options =
                new MultiFileChooserOptions(resultCallback, DATA)
                        .setTitle(TITLE).setInitialDirectory(initialDirectory)
                        .setCurrentFile(currentFile)
                        .setFilters(FILTER_IMAGES, FILTER_MUSIC);

        dialogService.showOpenMultiFileDialog(options);
        EasyMock.verify(resultCallback, fileChooser);
    }

    /**
     * Tests whether a cancellation of a multi file open dialog is handled
     * correctly.
     */
    @Test
    public void testOpenMultiFileDialogCanceled()
    {
        DialogResultCallback<List<File>, String> resultCallback =
                createResultCallback();
        DialogCanceledCallback<String> canceledCallback =
                createCanceledCallback();
        canceledCallback.onDialogCanceled(DATA);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setMultiSelectionEnabled(true);
        EasyMock.expect(fileChooser.showOpenDialog(mainWindowComponent))
                .andReturn(JFileChooser.CANCEL_OPTION);
        MultiFileChooserOptions options =
                new MultiFileChooserOptions(resultCallback)
                        .setCanceledCallback(canceledCallback, DATA)
                        .setTitle(TITLE);
        replayMocks(resultCallback, canceledCallback);

        dialogService.showOpenMultiFileDialog(options);
        EasyMock.verify(canceledCallback, fileChooser);
    }

    /**
     * Tests whether a dialog to select a file for saving can be displayed.
     */
    @Test
    public void testShowSaveDialog()
    {
        File initialDirectory = new File("initialDir");
        File currentFile = new File("current.txt");
        File result = new File("resultFile.sav");
        DialogResultCallback<File, String> resultCallback =
                createResultCallback();
        resultCallback.onDialogResult(result, DATA);
        fileChooser.setCurrentDirectory(initialDirectory);
        fileChooser.setSelectedFile(currentFile);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setMultiSelectionEnabled(false);
        expectFileFilters();
        EasyMock.expect(fileChooser.getSelectedFile()).andReturn(result);
        EasyMock.expect(fileChooser.showSaveDialog(mainWindowComponent))
                .andReturn(JFileChooser.APPROVE_OPTION);
        replayMocks(resultCallback);
        FileChooserOptions options =
                new FileChooserOptions(resultCallback, DATA).setTitle(TITLE)
                        .setInitialDirectory(initialDirectory)
                        .setCurrentFile(currentFile)
                        .setFilters(FILTER_IMAGES, FILTER_MUSIC);

        dialogService.showSaveFileDialog(options);
        EasyMock.verify(resultCallback, fileChooser);
    }

    /**
     * Tests whether the cancellation of the file save dialog is handled.
     */
    @Test
    public void testShowSaveDialogCanceled()
    {
        DialogResultCallback<File, String> resultCallback =
                createResultCallback();
        DialogCanceledCallback<String> canceledCallback =
                createCanceledCallback();
        canceledCallback.onDialogCanceled(DATA);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setMultiSelectionEnabled(false);
        EasyMock.expect(fileChooser.showSaveDialog(mainWindowComponent))
                .andReturn(JFileChooser.CANCEL_OPTION);
        FileChooserOptions options = new FileChooserOptions(resultCallback)
                .setCanceledCallback(canceledCallback, DATA).setTitle(TITLE);
        replayMocks(resultCallback, canceledCallback);

        dialogService.showSaveFileDialog(options);
        EasyMock.verify(canceledCallback, fileChooser);
    }

    /**
     * A test implementation of the dialog service. This implementation operates
     * on a mock file chooser, so that the initializations on this object can be
     * verified.
     */
    private class SwingFileChooserDialogServiceTestImpl
            extends SwingFileChooserDialogService
    {
        public SwingFileChooserDialogServiceTestImpl(ApplicationContext appCtx)
        {
            super(appCtx);
        }

        @Override
        protected JFileChooser createFileChooser()
        {
            return fileChooser;
        }
    }
}
