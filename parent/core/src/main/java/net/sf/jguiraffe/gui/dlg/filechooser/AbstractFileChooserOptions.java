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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sf.jguiraffe.gui.dlg.AbstractDialogOptions;
import net.sf.jguiraffe.gui.dlg.DialogResultCallback;

/**
 * <p>
 * An abstract base class for the options of file chooser dialogs.
 * </p>
 * <p>
 * The concrete option classes for file chooser dialogs distinguish only in the
 * result type: a single {@code File} for the normal dialog and a collection of
 * files for the multi dialog. Therefore, this base class already defines all
 * properties, but leaves the result type open.
 * </p>
 *
 * @param <T> the result type of the dialog
 * @param <S> the self type
 * @since 1.4
 */
public abstract class AbstractFileChooserOptions<T, S extends AbstractFileChooserOptions<T, S>>
        extends AbstractDialogOptions<T, S>
{
    /** The initial directory. */
    private File initialDirectory;

    /** The current file to be selected initially. */
    private File currentFile;

    /** The file filters that can be selected by the user. */
    private List<FileExtensionFilter> filters = Collections.emptyList();

    /** The index of the current filter. */
    private int currentFilterIndex;

    /**
     * Creates a new instance of {@code AbstractFileChooserOptions} and sets the
     * callback to be notified with the dialog result and an additional data
     * object to be passed to the callback.
     *
     * @param resultCallback the result callback
     * @param data the data object for the callback
     * @param <D> the type of the data object
     * @throws IllegalArgumentException if the result callback is
     *         <strong>null</strong>
     */
    protected <D> AbstractFileChooserOptions(
            DialogResultCallback<T, D> resultCallback, D data)
    {
        super(resultCallback, data);
    }

    /**
     * Creates a new instance of {@code AbstractFileChooserOptions} and sets the
     * callback to be notified with the dialog result.
     *
     * @param resultCallback the result callback
     * @throws IllegalArgumentException if the result callback is
     *         <strong>null</strong>
     */
    protected AbstractFileChooserOptions(
            DialogResultCallback<T, ?> resultCallback)
    {
        super(resultCallback);
    }

    /**
     * Allows setting the initial directory for the file chooser dialog. When
     * the dialog opens, the files in this directory are displayed.
     *
     * @param directory the initial directory
     * @return this object
     */
    public S setInitialDirectory(File directory)
    {
        this.initialDirectory = directory;
        return getSelf();
    }

    /**
     * Returns the initial directory for the file chooser dialog. Result can be
     * <strong>null</strong> if no such directory has been set.
     *
     * @return the initial directory
     */
    public File getInitialDirectory()
    {
        return initialDirectory;
    }

    /**
     * Allows pre-selecting a specific file. When the dialog opens this file is
     * already selected, but the user can select another one.
     *
     * @param file the file that is currently selected
     * @return this object
     */
    public S setCurrentFile(File file)
    {
        this.currentFile = file;
        return getSelf();
    }

    /**
     * Returns the current file to be selected initially. Result can be
     * <strong>null</strong> if no such file has been set.
     *
     * @return the currently selected file
     */
    public File getCurrentFile()
    {
        return currentFile;
    }

    /**
     * Sets the filters for the file types to be displayed. With this option the
     * user is given the possibility to restrict the display of files to certain
     * predefined types. Note that a filter for all files is not added
     * automatically; this must be done by the application.
     *
     * @param filters the list with file filters
     * @return this object
     */
    public S setFilters(List<FileExtensionFilter> filters)
    {
        this.filters = Collections
                .unmodifiableList(new ArrayList<FileExtensionFilter>(filters));
        return getSelf();
    }

    /**
     * Sets the filters for the file types to be displayed as var args. This
     * method is analogous to {@link #setFilters(List)}, but can be called with
     * an arbitrary number of filter objects.
     *
     * @param filters the file filters
     * @return this object
     */
    public S setFilters(FileExtensionFilter... filters)
    {
        return setFilters(Arrays.asList(filters));
    }

    /**
     * Sets the index of the filter to be active when the dialog is opened. Per
     * default, this is the first filter in the list of filters; but with this
     * property a different filter can be selected. If the index passed to this
     * method is out of range for the list of filters, no filter is selected
     * explicitly. (The behavior then depends on the UI library.)
     *
     * @param index the index of the current filter
     * @return this object
     */
    public S setCurrentFilterIndex(int index)
    {
        this.currentFilterIndex = index;
        return getSelf();
    }

    /**
     * Returns the index of the current filter when the dialog is opened.
     *
     * @return the index of the current filter
     */
    public int getCurrentFilterIndex()
    {
        return currentFilterIndex;
    }

    /**
     * Returns the current filter. This is the filter selected by the
     * {@code currentFilterIndex} property. If the current filter index is out
     * of range, this method returns <strong>null</strong>.
     *
     * @return the current filter or <strong>null</strong>
     * @see #setCurrentFilterIndex(int)
     */
    public FileExtensionFilter getCurrentFilter()
    {
        int index = getCurrentFilterIndex();
        return (index >= 0 && index < getFilters().size())
                ? getFilters().get(index)
                : null;
    }

    /**
     * Returns a (unmodifiable) list with predefined file filters. If no filters
     * have been set, the list is empty.
     *
     * @return a list with file filters
     */
    public List<FileExtensionFilter> getFilters()
    {
        return filters;
    }
}
