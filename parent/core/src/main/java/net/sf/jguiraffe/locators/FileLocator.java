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
package net.sf.jguiraffe.locators;

import java.io.File;
import java.net.URL;

/**
 * <p>
 * A concrete <code>Locator</code> class that represents files.
 * </p>
 * <p>
 * Instances of this class can be initialized with either a <code>File</code>
 * object or with the name of a file. Based on this data the locator methods are
 * implemented in an appropriate way.
 * </p>
 * <p>
 * Note that this class does not check whether the passed in file exists. So if
 * an input stream is to be obtained for this file, it is possible that a file
 * not found exception gets thrown.
 * </p>
 * <p>
 * Instances of this class are created using the <code>getInstance()</code>
 * factory methods. They are immutable and thus can be shared between multiple
 * threads.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FileLocator.java 205 2012-01-29 18:29:57Z oheger $
 */
public final class FileLocator extends AbstractLocator
{
    /** Stores the represented file object. */
    private final File file;

    /**
     * Creates a new instance of <code>FileLocator</code> and initializes it
     * with the represented file. For creating an instance clients have to call
     * the <code>getInstance()</code> factory methods.
     *
     * @param file the file
     */
    private FileLocator(File file)
    {
        this.file = file;
    }

    /**
     * Returns a <code>FileLocator</code> instance for the specified file.
     *
     * @param file the file (must not be <b>null</b>)
     * @return the <code>FileLocator</code> instance for this file
     * @throws IllegalArgumentException if the file is <b>null</b>
     */
    public static FileLocator getInstance(File file)
    {
        if (file == null)
        {
            throw new IllegalArgumentException("File must not be null!");
        }

        return new FileLocator(file);
    }

    /**
     * Returns a <code>FileLocator</code> instance for the specified file name.
     *
     * @param fileName the file name (must not be <b>null</b>)
     * @return the <code>FileLocator</code> instance for this file
     * @throws IllegalArgumentException if the file name is <b>null</b>
     */
    public static FileLocator getInstance(String fileName)
    {
        if (fileName == null)
        {
            throw new IllegalArgumentException("File name must not be null!");
        }

        return new FileLocator(new File(fileName));
    }

    /**
     * Returns the URL for the represented file.
     *
     * @return the URL
     * @throws LocatorException if an error occurs
     */
    public URL getURL()
    {
        return LocatorUtils.fileURL(getFile());
    }

    /**
     * Returns the represented file resource.
     *
     * @return the file
     */
    @Override
    public File getFile()
    {
        return file;
    }

    /**
     * Returns the name of the represented file. This is an absolute file path.
     *
     * @return the file name
     */
    public String getFileName()
    {
        return getFile().getAbsolutePath();
    }

    /**
     * Compares this object with another one. Two instances of this class are
     * considered equal if and only if they point to the same file.
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
        if (!(obj instanceof FileLocator))
        {
            return false;
        }

        FileLocator c = (FileLocator) obj;
        return getFile().equals(c.getFile());
    }

    /**
     * Returns a hash code for this object.
     *
     * @return a hash code
     */
    @Override
    public int hashCode()
    {
        return getFile().hashCode();
    }

    /**
     * Returns a string representation for this object. This string will at
     * least contain the path to the file as returned by
     * <code>getFileName()</code>.
     *
     * @return a string for this object
     */
    @Override
    public String toString()
    {
        return LocatorUtils.locatorToString(this, "file = " + getFileName());
    }
}
