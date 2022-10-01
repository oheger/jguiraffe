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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sf.jguiraffe.gui.app.TextResource;

/**
 * <p>
 * A class representing an extension filter that can be used together with the
 * file chooser service.
 * </p>
 * <p>
 * When defining the options to show a file chooser dialog an arbitrary number
 * of objects of this class can be specified. Each filter represents a specific
 * file type and can be configured with the corresponding file extensions, such
 * as {@code "Image files"; "gif", "jpeg", "jpg", "png"}. The user is displayed
 * a list with all pre-configured extension filters; when selecting a specific
 * one, only the files matched by the filter are visible. (When providing a file
 * extension do not prepend a separator character or a wildcard, such as
 * {@code *.txt}; just use the plain extension {@code txt}).
 * </p>
 * <p>
 * The description of the filter is displayed to the user. It can be defined as
 * a {@code TextResource} object, i.e. either as plain text or via a resource
 * ID.
 * </p>
 * <p>
 * Instances of this class are immutable.
 * </p>
 *
 * @since 1.4
 */
public final class FileExtensionFilter
{
    /**
     * Constant for a special extension with the meaning that all files should
     * be matched. This extension should be specified for an "All files"
     * filter.
     */
    public static final String EXT_ALL_FILES = "*";

    /** Stores the resource for the filter description. */
    private final TextResource description;

    /** Stores the file extensions for this filter. */
    private final List<String> extensions;

    /**
     * Creates a new instance of {@code FileExtensionFilter} and sets the
     * resource for a description and a collection with the file extensions to
     * be matched by this filter.
     *
     * @param descResource the resource for the filter description
     * @param extList a list with file extensions
     */
    public FileExtensionFilter(TextResource descResource, List<String> extList)
    {
        description = descResource;
        extensions =
                Collections.unmodifiableList(new ArrayList<>(extList));
    }

    /**
     * Creates a new instance of {@code FileExtensionFilter} and sets the
     * resource for a description and the single file extensions to be matched
     * by this filter.
     *
     * @param descResource the resource for the filter description
     * @param extensions the file extensions
     */
    public FileExtensionFilter(TextResource descResource, String... extensions)
    {
        this(descResource, Arrays.asList(extensions));
    }

    /**
     * Returns the {@code TextResource} for the filter description.
     *
     * @return the resource for the filter description
     */
    public TextResource getDescription()
    {
        return description;
    }

    /**
     * Returns a (unmodifiable) list with the file extensions assigned to this
     * filter.
     *
     * @return a collection with file extensions
     */
    public List<String> getExtensions()
    {
        return extensions;
    }

    /**
     * {@inheritDoc} Two instances of this class are considered equals if all of
     * their properties are equal.
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof FileExtensionFilter))
        {
            return false;
        }

        FileExtensionFilter filter = (FileExtensionFilter) o;

        if (!getDescription().equals(filter.getDescription())) {
            return false;
        }
        return getExtensions().equals(filter.getExtensions());
    }

    @Override
    public int hashCode()
    {
        int result = getDescription().hashCode();
        result = 31 * result + getExtensions().hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return "FileExtensionFilter{" + "description=" + description
                + ", extensions=" + extensions + '}';
    }
}
