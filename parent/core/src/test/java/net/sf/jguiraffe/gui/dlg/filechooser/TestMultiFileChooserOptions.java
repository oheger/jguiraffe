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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Test;

import net.sf.jguiraffe.gui.dlg.DialogResultCallback;

/**
 * Test class for {@code MultiFileChooserOptions}.
 */
public class TestMultiFileChooserOptions
{
    /**
     * Creates a mock for a result callback.
     *
     * @return the mock result callback
     */
    private static DialogResultCallback<List<File>, String> createResultCallbackMock()
    {
        @SuppressWarnings("unchecked")
        DialogResultCallback<List<File>, String> callback =
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
        final List<File> resultFiles = Arrays.asList(new File("myResult1.dat"),
                new File("myOtherResult.txt"));
        final String data = "some context data";
        DialogResultCallback<List<File>, String> callback =
                createResultCallbackMock();
        callback.onDialogResult(resultFiles, data);
        EasyMock.replay(callback);

        MultiFileChooserOptions options =
                new MultiFileChooserOptions(callback, data);
        options.getResultCallback().onDialogResult(resultFiles, null);
        EasyMock.verify(callback);
    }

    /**
     * Tests whether the result callback (without a data object) is passed to
     * the super constructor.
     */
    @Test
    public void testResultCallbackIsPassedToSuper()
    {
        final List<File> resultFiles =
                Collections.singletonList(new File("resultWithoutData.txt"));
        DialogResultCallback<List<File>, String> callback =
                createResultCallbackMock();
        callback.onDialogResult(resultFiles, null);
        EasyMock.replay(callback);

        MultiFileChooserOptions options = new MultiFileChooserOptions(callback);
        options.getResultCallback().onDialogResult(resultFiles, null);
        EasyMock.verify(callback);
    }

    /**
     * Tests whether the correct self reference is returned.
     */
    @Test
    public void testSelf()
    {
        MultiFileChooserOptions options =
                new MultiFileChooserOptions(createResultCallbackMock())
                        .setTitle("myDialog").setCurrentFilterIndex(1);
        assertEquals("Wrong filter index", 1, options.getCurrentFilterIndex());
    }
}
