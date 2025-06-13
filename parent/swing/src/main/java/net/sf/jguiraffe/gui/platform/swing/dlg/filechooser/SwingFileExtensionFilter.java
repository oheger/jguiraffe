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

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import net.sf.jguiraffe.gui.app.ApplicationContext;
import net.sf.jguiraffe.gui.dlg.filechooser.FileExtensionFilter;

/**
 * <p>
 * A specialized {@code FileFilter} implementation that implements the logic
 * represented by a {@code FileExtensionFilter} to Swing's file chooser dialog.
 * </p>
 */
class SwingFileExtensionFilter extends FileFilter
{
    /** The filter description. */
    private final String description;

    /** The set with file extensions accepted by this filter. */
    private final Set<String> acceptedExtensions;

    /** Flag whether all files should be accepted. */
    private final boolean acceptAll;

    /**
     * Creates a new instance of {@code SwingFileExtensionFilter}.
     *
     * @param desc the description of this filter
     * @param extensions a collection with the file extensions
     */
    public SwingFileExtensionFilter(String desc, Collection<String> extensions)
    {
        description = desc;
        acceptedExtensions = new HashSet<String>();
        for (String ext : extensions)
        {
            acceptedExtensions.add(ext.toLowerCase(Locale.ENGLISH));
        }
        acceptAll =
                acceptedExtensions.contains(FileExtensionFilter.EXT_ALL_FILES);
    }

    /**
     * Creates a new instance of {@code SwingFileExtensionFilter} that is
     * equivalent to the given extension filter.
     *
     * @param filter the extension filter
     * @param applicationContext the application context
     * @return the new {@code SwingFileExtensionFilter} object
     */
    public static SwingFileExtensionFilter fromExtensionFilter(
            FileExtensionFilter filter, ApplicationContext applicationContext)
    {
        return new SwingFileExtensionFilter(
                filter.getDescription().resolveText(applicationContext),
                filter.getExtensions());
    }

    /**
     * {@inheritDoc} This implementation extracts the extension from the given
     * file and checks whether it is contained in the set of accepted file
     * extensions. If this instance is the <em>accepts all</em> filter, of
     * course all files are accepted.
     */
    public boolean accept(File f)
    {
        if (acceptAll)
        {
            return true;
        }
        String extension = extractExtension(f);
        return acceptedExtensions.contains(extension);
    }

    public String getDescription()
    {
        return description;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof SwingFileExtensionFilter))
        {
            return false;
        }

        SwingFileExtensionFilter filter = (SwingFileExtensionFilter) o;

        if (getDescription() != null
                ? !getDescription().equals(filter.getDescription())
                : filter.getDescription() != null) {
            return false;
        }
        return acceptedExtensions.equals(filter.acceptedExtensions);
    }

    @Override
    public int hashCode()
    {
        int result = getDescription() != null ? getDescription().hashCode() : 0;
        result = 31 * result + acceptedExtensions.hashCode();
        return result;
    }

    /**
     * Extracts the extension from the given file in a form that it can be
     * compared against the set of accepted file extensions.
     *
     * @param file the file
     * @return the extension of this file
     */
    private static String extractExtension(File file)
    {
        String name = file.getName();
        int posExt = name.lastIndexOf('.');
        return (posExt >= 0)
                ? name.substring(posExt + 1).toLowerCase(Locale.ENGLISH)
                : "";
    }
}
