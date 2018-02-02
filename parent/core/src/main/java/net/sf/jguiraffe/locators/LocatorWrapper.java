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
package net.sf.jguiraffe.locators;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * <p>
 * A wrapper implementation for locators.
 * </p>
 * <p>
 * Most concrete <code>Locator</code> implementations in this package cannot be
 * extended. (They do not have public constructors, instances can only be
 * created through static factory methods.) If a custom <code>Locator</code>
 * implementation is to be created that can benefit from an existing locator,
 * <em>composition</em> can be used instead of inheritance. To simplify this,
 * this wrapper class is introduced.
 * </p>
 * <p>
 * <code>LocatorWrapper</code> is an implementation of the <code>Locator</code>
 * interface that is based on an underlying <code>Locator</code> object. All
 * methods are implemented to delegate to this wrapped object. This way
 * inheritance can be emulated by implementing desired functionality before or
 * after delegating to the wrapped <code>Locator</code> (or skipping the call to
 * this object at all).
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: LocatorWrapper.java 205 2012-01-29 18:29:57Z oheger $
 */
public class LocatorWrapper implements Locator
{
    /** Stores the underlying locator object. */
    private final Locator wrappedLocator;

    /**
     * Creates a new instance of <code>LocatorWrapper</code> and initializes it
     * with the given wrapped locator. Per default, all methods of this class
     * delegate to this object. It must not be <b>null</b>.
     *
     * @param wrapped the wrapped locator (must not be <b>null</b>)
     * @throws IllegalArgumentException if the wrapped locator is <b>null</b>
     */
    public LocatorWrapper(Locator wrapped)
    {
        if (wrapped == null)
        {
            throw new IllegalArgumentException(
                    "Wrapped locator must not be null!");
        }

        wrappedLocator = wrapped;
    }

    /**
     * Returns the wrapped <code>Locator</code>. This is the object this
     * instance will delegate to.
     *
     * @return the wrapped <code>Locator</code>
     */
    public Locator getWrappedLocator()
    {
        return wrappedLocator;
    }

    /**
     * Returns the file this locator points to. This implementation delegates to
     * the wrapped <code>Locator</code>.
     *
     * @return the file
     */
    public File getFile()
    {
        return getWrappedLocator().getFile();
    }

    /**
     * Returns the input stream this locator points to. This implementation
     * delegates to the wrapped <code>Locator</code>.
     *
     * @return the input stream
     * @throws IOException if an error occurs
     */
    public InputStream getInputStream() throws IOException
    {
        return getWrappedLocator().getInputStream();
    }

    /**
     * Returns the URL this locator points to. This implementation delegates to
     * the wrapped <code>Locator</code>.
     *
     * @return the URL
     */
    public URL getURL()
    {
        return getWrappedLocator().getURL();
    }
}
