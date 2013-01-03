/*
 * Copyright 2006-2012 The JGUIraffe Team.
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
package net.sf.jguiraffe.examples.tutorial.mainwnd;

import java.io.File;

import net.sf.jguiraffe.gui.builder.components.model.ListModel;

/**
 * <p>
 * A {@code ListModel} implementation for the roots of the available file
 * systems.
 * </p>
 * <p>
 * In the main window there is a combobox that allows the user to select a file
 * system whose content is then displayed by the file system browser. This class
 * is the underlying model of this combobox. At construction time it determines
 * the available file system roots. The methods defined by the {@code ListModel}
 * interface are implemented on these root objects. As values the corresponding
 * {@code java.io.File} objects are returned. As display names the file names
 * are used.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FileSystemListModel.java 205 2012-01-29 18:29:57Z oheger $
 */
public class FileSystemListModel implements ListModel
{
    /** Stores the available root objects. */
    private final File[] roots;

    /**
     * Creates a new instance of {@code FileSystemListModel}.
     */
    public FileSystemListModel()
    {
        roots = File.listRoots();
    }

    /**
     * Returns the display object at the given index. This implementation
     * returns the name of the {@code java.io.File} representing the file system
     * root at the given index.
     *
     * @param index the index
     * @return the display object for this model element
     */
    @Override
    public Object getDisplayObject(int index)
    {
        return roots[index].getPath();
    }

    /**
     * Returns the data type of the model elements. For this model
     * implementation {@code java.io.File} is the data type.
     *
     * @return the data type of this model
     */
    @Override
    public Class<?> getType()
    {
        return File.class;
    }

    /**
     * Returns the value object at the given index. This implementation returns
     * the {@code java.io.File} object representing the file system root at the
     * given index.
     *
     * @param index the index
     * @return the value object at this index
     */
    @Override
    public Object getValueObject(int index)
    {
        return roots[index];
    }

    /**
     * Returns the size of this model. This implementation returns the number of
     * file system roots available.
     *
     * @return the size of this model
     */
    @Override
    public int size()
    {
        return roots.length;
    }
}
