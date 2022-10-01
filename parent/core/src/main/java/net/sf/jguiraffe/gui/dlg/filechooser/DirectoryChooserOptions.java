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

import java.io.File;

import net.sf.jguiraffe.gui.dlg.AbstractDialogOptions;
import net.sf.jguiraffe.gui.dlg.DialogResultCallback;

/**
 * <p>
 * A class for defining the options of a directory chooser dialog.
 * </p>
 * <p>
 * Instances of this class can be passed to a file chooser service to configure
 * the behavior of a directory chooser dialog. The class offers a fluent API to
 * set the single properties.
 * </p>
 * <p>
 * Implementation note: Instances are not thread-safe.
 * </p>
 *
 * @since 1.4
 */
public class DirectoryChooserOptions
        extends AbstractDialogOptions<File, DirectoryChooserOptions>
{
    /** Stores the initial directory. */
    private File initialDirectory;

    /**
     * Creates a new instance of {@code DirectoryChooserOptions} and sets the
     * callback to be notified with the dialog result and an additional data
     * object to be passed to the callback.
     *
     * @param resultCallback the result callback
     * @param data the data object for the callback
     * @param <D> the type of the data object
     * @throws IllegalArgumentException if the result callback is
     *         <strong>null</strong>
     */
    public <D> DirectoryChooserOptions(
            DialogResultCallback<File, D> resultCallback, D data)
    {
        super(resultCallback, data);
    }

    /**
     * Creates a new instance of {@code DirectoryChooserOptions} and sets the
     * callback to be notified with the dialog result.
     *
     * @param resultCallback the result callback
     * @throws IllegalArgumentException if the result callback is
     *         <strong>null</strong>
     */
    public DirectoryChooserOptions(DialogResultCallback<File, ?> resultCallback)
    {
        super(resultCallback);
    }

    /**
     * Allows setting the initial directory for the directory chooser dialog.
     *
     * @param directory the initial directory
     * @return this object
     */
    public DirectoryChooserOptions setInitialDirectory(File directory)
    {
        this.initialDirectory = directory;
        return getSelf();
    }

    /**
     * Returns the initial directory for the directory chooser dialog. Result
     * can be <strong>null</strong> if no such directory has been set.
     *
     * @return the initial directory
     */
    public File getInitialDirectory()
    {
        return initialDirectory;
    }

    protected DirectoryChooserOptions getSelf()
    {
        return this;
    }
}
