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

import static org.junit.Assert.*;

import java.io.File;

import net.sf.jguiraffe.gui.dlg.DialogResultCallback;
import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for {@code DirectoryChooserOptions}.
 */
public class TestDirectoryChooserOptions
{
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

        DirectoryChooserOptions options =
                new DirectoryChooserOptions(callback, data);
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

        DirectoryChooserOptions options = new DirectoryChooserOptions(callback);
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

        DirectoryChooserOptions options =
                new DirectoryChooserOptions(createResultCallbackMock())
                        .setTitle("Select the dir")
                        .setInitialDirectory(initDir);
        assertEquals("Wrong initial directory", initDir,
                options.getInitialDirectory());
    }
}
