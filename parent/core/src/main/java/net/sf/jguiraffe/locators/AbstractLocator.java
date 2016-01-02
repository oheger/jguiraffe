/*
 * Copyright 2006-2016 The JGUIraffe Team.
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
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>
 * An adapter class for implementing concrete <code>Locator</code> classes.
 * </p>
 * <p>
 * This class implements the <code>Locator</code> interface and provides dummy
 * implementations for most of the methods defined in this interface. It can
 * serve as a starting point for the implementation of custom
 * <code>Locator</code> classes; then the developer only needs to deal with
 * the methods that are really required.
 * </p>
 * <p>
 * Because the <code>getURL()</code> method must be implemented in every
 * locator, no dummy implementation for this method is provided.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: AbstractLocator.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class AbstractLocator implements Locator
{
    /**
     * Dummy implementation of this interface method. Always returns <b>null</b>.
     *
     * @return a file object for the represented resource
     * @throws LocatorException if an error occurs
     */
    public File getFile() throws LocatorException
    {
        return null;
    }

    /**
     * Dummy implementation of this interface method. Always returns <b>null</b>.
     *
     * @return an input stream for the represented resource
     * @throws IOException if an IO error occurs
     * @throws LocatorException if an internal error occurs
     */
    public InputStream getInputStream() throws IOException, LocatorException
    {
        return null;
    }
}
