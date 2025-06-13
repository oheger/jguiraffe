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
package net.sf.jguiraffe.gui.dlg.filechooser;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.jguiraffe.gui.app.TextResource;
import net.sf.jguiraffe.gui.dlg.DialogResultCallback;
import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for {@code FileChooserOptions}.
 */
public class TestFileChooserOptions
{
    /** A test extension filter. */
    private static final FileExtensionFilter FILTER_IMAGES =
            new FileExtensionFilter(TextResource.fromText("images"), "gif",
                    "jpg", "png");

    /** Another test extension filter. */
    private static final FileExtensionFilter FILTER_MUSIC =
            new FileExtensionFilter(TextResource.fromText("songs"), "mp3",
                    "ogg");

    /**
     * Creates a mock for a result callback.
     *
     * @return the mock result callback
     */
    private static DialogResultCallback<File, String> createResultCallbackMock()
    {
        @SuppressWarnings("unchecked")
        DialogResultCallback<File, String> callback =
                EasyMock.createMock(DialogResultCallback.class);
        return callback;
    }

    /**
     * Tests that the callback and the data object are correctly passed to the
     * super constructor.
     */
    @Test
    public void testResultCallbackAndDataObjectArePassedToSuper()
    {
        final File resultFile = new File("myResult.dat");
        final String data = "some context data";
        DialogResultCallback<File, String> callback =
                createResultCallbackMock();
        callback.onDialogResult(resultFile, data);
        EasyMock.replay(callback);

        FileChooserOptions options = new FileChooserOptions(callback, data);
        options.getResultCallback().onDialogResult(resultFile, null);
        EasyMock.verify(callback);
    }

    /**
     * Tests whether the result callback (without a data object) is passed to
     * the super constructor.
     */
    @Test
    public void testResultCallbackIsPassedToSuper()
    {
        final File resultFile = new File("resultWithoutData.txt");
        DialogResultCallback<File, String> callback =
                createResultCallbackMock();
        callback.onDialogResult(resultFile, null);
        EasyMock.replay(callback);

        FileChooserOptions options = new FileChooserOptions(callback);
        options.getResultCallback().onDialogResult(resultFile, null);
        EasyMock.verify(callback);
    }

    /**
     * Tests whether the directory can be initialized.
     */
    @Test
    public void testInitialDirectoryCanBeSet()
    {
        final File initDir = new File("start-dir");

        FileChooserOptions options =
                new FileChooserOptions(createResultCallbackMock())
                        .setTitle("Select the dir")
                        .setInitialDirectory(initDir);
        assertEquals("Wrong initial directory", initDir,
                options.getInitialDirectory());
    }

    /**
     * Tests whether the dialog can be configured with a file to be selected.
     */
    @Test
    public void testCurrentFileCanBeSet()
    {
        File current = new File("iAmSelected.txt");

        FileChooserOptions options =
                new FileChooserOptions(createResultCallbackMock())
                        .setCurrentFile(current);
        assertEquals("Wrong current file", current, options.getCurrentFile());
    }

    /**
     * Tests that extension filters can be set as collection.
     */
    @Test
    public void testCollectionOfFiltersCanBeSet()
    {
        List<FileExtensionFilter> filters =
                Arrays.asList(FILTER_IMAGES, FILTER_MUSIC);

        FileChooserOptions options =
                new FileChooserOptions(createResultCallbackMock())
                        .setFilters(filters);
        assertEquals("Wrong filters", filters, options.getFilters());
    }

    /**
     * Tests whether a defensive copy from the collection of filters is made.
     */
    @Test
    public void testCollectionOfFiltersIsCopied()
    {
        List<FileExtensionFilter> orgFilters =
                Arrays.asList(FILTER_IMAGES, FILTER_MUSIC);
        List<FileExtensionFilter> filters =
                new ArrayList<FileExtensionFilter>(orgFilters);
        FileChooserOptions options =
                new FileChooserOptions(createResultCallbackMock())
                        .setFilters(filters);

        filters.remove(1);
        assertEquals("Wrong filters", orgFilters, options.getFilters());
    }

    /**
     * Tests the var args method for setting filters.
     */
    @Test
    public void testFiltersCanBeSetAsVarArgs()
    {
        List<FileExtensionFilter> expFilters =
                Arrays.asList(FILTER_IMAGES, FILTER_MUSIC);

        FileChooserOptions options =
                new FileChooserOptions(createResultCallbackMock())
                        .setFilters(FILTER_IMAGES, FILTER_MUSIC);
        assertEquals("Wrong filters", expFilters, options.getFilters());
    }

    /**
     * Tests that the list returned by getFilters() cannot be modified.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetFiltersReturnsUnmodifiableList()
    {
        FileChooserOptions options =
                new FileChooserOptions(createResultCallbackMock())
                        .setFilters(FILTER_IMAGES, FILTER_MUSIC);

        options.getFilters().remove(1);
    }

    /**
     * Tests the getFilters() method if no filters have been set.
     */
    @Test
    public void testEmptyFiltersAreReturnedIfNotInitialized()
    {
        FileChooserOptions options =
                new FileChooserOptions(createResultCallbackMock());

        assertTrue("Got filters", options.getFilters().isEmpty());
    }

    /**
     * Tests whether getFilters() returns an unmodifiable list before filters
     * have been set.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testInitialListOfFiltersIsUnmodifiable()
    {
        FileChooserOptions options =
                new FileChooserOptions(createResultCallbackMock());

        options.getFilters().add(FILTER_MUSIC);
    }

    /**
     * Tests whether the index of the current filter can be set.
     */
    @Test
    public void testCurrentFilterIndexCanBeSet()
    {
        final int filterIdx = 1;
        List<FileExtensionFilter> filters =
                Arrays.asList(FILTER_IMAGES, FILTER_MUSIC);

        FileChooserOptions options =
                new FileChooserOptions(createResultCallbackMock())
                        .setFilters(filters).setCurrentFilterIndex(filterIdx);
        assertEquals("Wrong current filter index", filterIdx,
                options.getCurrentFilterIndex());
        assertEquals("Wrong current filter", filters.get(filterIdx),
                options.getCurrentFilter());
    }

    /**
     * Tests that the default current filter index is 0.
     */
    @Test
    public void testFirstFilterIsCurrentPerDefault()
    {
        FileChooserOptions options =
                new FileChooserOptions(createResultCallbackMock())
                        .setFilters(FILTER_MUSIC, FILTER_IMAGES);

        assertEquals("Wrong current filter", FILTER_MUSIC,
                options.getCurrentFilter());
    }

    /**
     * Tests getFilter() if the current filter index is invalid.
     */
    @Test
    public void testInvalidFilterIndexIsHandled()
    {
        FileChooserOptions options =
                new FileChooserOptions(createResultCallbackMock());

        assertNull("Got a current filter", options.getCurrentFilter());
    }

    /**
     * Tests that also a negative filter index is handled.
     */
    @Test
    public void testNegativeFilterIndexIsHandled()
    {
        FileChooserOptions options =
                new FileChooserOptions(createResultCallbackMock())
                        .setFilters(FILTER_IMAGES).setCurrentFilterIndex(-1);

        assertNull("Got a current filter", options.getCurrentFilter());
    }
}
