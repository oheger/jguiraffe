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
package net.sf.jguiraffe.examples.tutorial.model;

import java.io.File;
import java.util.Date;

/**
 * <p>
 * A simple data class representing a file (or sub directory) in a directory.
 * </p>
 * <p>
 * Objects of this type are used by the model of the table control. Each
 * instance represents one row in the table. An instance wraps a {@code File}
 * object and provides bean-style get methods for it. (The API of {@code File}
 * does not always conform to the Java Beans standard.)
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FileData.java 205 2012-01-29 18:29:57Z oheger $
 */
public class FileData implements Comparable<FileData>
{
    /** Stores the wrapped file. */
    private final File file;

    /** The icon to be displayed. */
    private final Object icon;

    /** The file name. */
    private final String name;

    /**
     * Creates a new instance of {@code FileData} for the specified {@code File}
     * object.
     *
     * @param f the {@code File}
     * @param icon the icon for this {@code File}
     */
    public FileData(File f, Object icon)
    {
        file = f;
        this.icon = icon;
        name = f.getName();
    }

    /**
     * Creates a new instance of {@code FileData} that does not represent a
     * file. Objects of this type are used as placeholders if no real data is
     * available, e.g. if data has to be loaded first.
     *
     * @param name the name to be displayed
     * @param icon an icon
     */
    public FileData(String name, Object icon)
    {
        file = null;
        this.icon = icon;
        this.name = name;
    }

    /**
     * Returns the associated {@code File} object. If this object represents a
     * special entry which is not associated with a file, result is <b>null</b>.
     *
     * @return the associated {@code File} object
     */
    public File getFile()
    {
        return file;
    }

    /**
     * Returns the icon of this file.
     *
     * @return the icon
     */
    public Object getIcon()
    {
        return icon;
    }

    /**
     * Returns the name of this file.
     *
     * @return the file name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the date this file was modified.
     *
     * @return the last modification date (can be <b>null</b>)
     */
    public Date getLastModified()
    {
        return (file != null) ? new Date(file.lastModified()) : null;
    }

    /**
     * Returns the size of this file.
     *
     * @return the size
     */
    public long getSize()
    {
        return (file == null || !file.isFile()) ? 0L : file.length();
    }

    /**
     * Compares this object with another one. Two instances of {@code FileData}
     * are equal if they refer to the same file.
     *
     * @param obj the object to compare to
     * @return a flag whether the objects are equal
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof FileData))
        {
            return false;
        }

        FileData c = (FileData) obj;
        return ((file == null) ? c.file == null : file.equals(c.file));
    }

    /**
     * Returns a hash code for this object.
     *
     * @return a hash code
     */
    @Override
    public int hashCode()
    {
        return (file == null) ? 17 : file.hashCode();
    }

    /**
     * Returns a string representation for this object. This string contains the
     * name and the file stored in this instance.
     *
     * @return a string for this object
     */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append("FileData [ name = ").append(name);
        buf.append(", file = ").append(file);
        buf.append(" ]");
        return buf.toString();
    }

    /**
     * A special {@code compareTo()} implementation. Files are sorted
     * alphabetically by their names. Directories are sorted before plain files.
     *
     * @param o the other {@code FileData} object
     * @return a value indicating the order of these objects
     */
    @Override
    public int compareTo(FileData o)
    {
        if (file == null)
        {
            return (o.file == null) ? 0 : -1;
        }
        if (o.file == null)
        {
            return 1;
        }

        boolean dir1 = file.isDirectory();
        boolean dir2 = o.file.isDirectory();
        if (dir1 != dir2)
        {
            return dir1 ? -1 : 1;
        }
        return getName().compareTo(o.getName());
    }
}
