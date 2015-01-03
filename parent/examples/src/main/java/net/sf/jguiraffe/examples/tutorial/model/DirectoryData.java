/*
 * Copyright 2006-2015 The JGUIraffe Team.
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
package net.sf.jguiraffe.examples.tutorial.model;

import java.io.File;
import java.util.List;

/**
 * <p>
 * A data class for storing information about a directory.
 * </p>
 * <p>
 * Objects of this class are used by the file system browser to keep track about
 * the directories already scanned. They are stored in the model of the tree
 * view. Whenever a node in the tree is selected the corresponding {@code
 * DirectoryData} object is determined. From this object the content of the
 * directory can be obtained. It is also possible to find out whether this
 * directory has already been read.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: DirectoryData.java 205 2012-01-29 18:29:57Z oheger $
 */
public class DirectoryData
{
    /** Stores the file representing the directory. */
    private final File directory;

    /** A list with the content of this directory. */
    private List<FileData> content;

    /**
     * Creates a new instance of {@code DirectoryData} and initializes it with
     * the {@code File} object representing the associated directory.
     *
     * @param dir the directory
     */
    public DirectoryData(File dir)
    {
        directory = dir;
    }

    /**
     * Returns a list with the content of this directory.
     *
     * @return a list with the content of this directory
     */
    public List<FileData> getContent()
    {
        return content;
    }

    /**
     * Sets the list with the content of this directory.
     *
     * @param content the list with the content
     */
    public void setContent(List<FileData> content)
    {
        this.content = content;
    }

    /**
     * Returns the {@code File} object representing the associated directory.
     *
     * @return the directory
     */
    public File getDirectory()
    {
        return directory;
    }

    /**
     * Returns a flag whether this directory has already been initialized.
     * Initialized means that the content of the directory is available.
     *
     * @return a flag whether this directory has been initialized
     */
    public boolean isInitialized()
    {
        return content != null;
    }

    /**
     * Returns a string representation for this object. This string contains the
     * directory object itself and its content as well (if it is available
     * already).
     *
     * @return a string for this object
     */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append("DirectoryData [ directory = ").append(getDirectory());
        if (isInitialized())
        {
            buf.append(", content = ").append(content);
        }
        buf.append(" ]");
        return buf.toString();
    }
}
