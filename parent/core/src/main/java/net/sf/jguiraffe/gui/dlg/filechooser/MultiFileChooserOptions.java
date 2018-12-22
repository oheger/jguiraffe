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
package net.sf.jguiraffe.gui.dlg.filechooser;

import java.io.File;
import java.util.List;

import net.sf.jguiraffe.gui.dlg.DialogResultCallback;

/**
 * <p>
 * A class for defining the options of a multi file chooser dialog.
 * </p>
 * <p>
 * Instances of this class can be passed to a file chooser service to configure
 * the behavior of a dialog for selecting multiple files to be loaded. The class
 * supports the same properties as {@link FileChooserOptions}, but its result
 * type is a list of files (reflecting the fact that the user can select
 * multiple files).
 * </p>
 * <p>
 * Implementation note: Instances are not thread-safe.
 * </p>
 *
 * @since 1.4
 */
public class MultiFileChooserOptions
        extends AbstractFileChooserOptions<List<File>, MultiFileChooserOptions>
{
    /**
     * Creates a new instance of {@code MultiFileChooserOptions} and sets the
     * callback to be notified with the dialog result and an additional data
     * object to be passed to the callback.
     *
     * @param resultCallback the result callback
     * @param data the data object for the callback
     * @param <D> the type of the data object
     * @throws IllegalArgumentException if the result callback is
     *         <strong>null</strong>
     */
    public <D> MultiFileChooserOptions(
            DialogResultCallback<List<File>, D> resultCallback, D data)
    {
        super(resultCallback, data);
    }

    /**
     * Creates a new instance of {@code MultiFileChooserOptions} and sets the
     * callback to be notified with the dialog result.
     *
     * @param resultCallback the result callback
     * @throws IllegalArgumentException if the result callback is
     *         <strong>null</strong>
     */
    public MultiFileChooserOptions(
            DialogResultCallback<List<File>, ?> resultCallback)
    {
        super(resultCallback);
    }

    protected MultiFileChooserOptions getSelf()
    {
        return this;
    }
}
