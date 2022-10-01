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
package net.sf.jguiraffe.locators;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * <p>
 * Definition of the base <code>Locator</code> interface.
 * </p>
 * <p>
 * A <em>Locator</em> is an object that points to a resource to be loaded by
 * an application. There are many ways how such a resource can be specified,
 * including
 * <ul>
 * <li>plain strings representing file paths or URLs</li>
 * <li><code>File</code> objects</li>
 * <li><code>URL</code> objects</li>
 * </ul>
 * and lots of more.
 * </p>
 * <p>
 * Typically to be fully compatible with all kinds of clients and to be
 * convenient to use a service class that needs to load resources has to deal
 * with all these various ways. As an implementor of such services classes you
 * find yourself often writing similar code for converting <code>File</code>s
 * to <code>URL</code>s, strings to both or vice versa.
 * </p>
 * <p>
 * To make the handling of resources easier the <code>Locator</code> interface
 * was introduced. A <em>Locator</em> provides an abstract view on a resource
 * specification. Concrete implementations will deal with specific ways of
 * describing resources, e.g. as URLs or files or loaded from the class path. So
 * a service class need not deal with conversion between the different formats
 * any longer, but just queries the provided locator.
 * </p>
 * <p>
 * The <code>Locator</code> interface defines some methods for returning a
 * pointer to the represented resource, but the only method that must be
 * implemented is the <code>getURL()</code> method. So URLs are the native
 * format. If a stream to the represented resource is to be opened, this task
 * should be delegated to the <code>{@link LocatorUtils}</code> class, which
 * contains static utility methods for exactly this purpose. These methods will
 * test, which of the locator's methods return defined values and then use those
 * to open the stream.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: Locator.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface Locator
{
    /**
     * Returns a URL to the resource represented by this locator. This method
     * must return a non <b>null</b> value.
     *
     * @return a URL for the represented resource
     * @throws LocatorException if an internal error occurs while determining
     * the URL
     */
    URL getURL();

    /**
     * Returns a file for the resource represented by this locator. This is an
     * optional method that can return <b>null</b> if a file makes no sense for
     * the represented resource.
     *
     * @return a <code>File</code> object for the represented resource
     * @throws LocatorException if an internal error occurs
     */
    File getFile();

    /**
     * Returns an input stream for the represented resource. This method is
     * called first when a stream to the locator is to be obtained. It is an
     * optional method that can return <b>null</b>. In this case the
     * <code>getFile()</code> and, last but not least, the
     * <code>getURL()</code> methods will be tried.
     *
     * @return an input stream to the represented resource
     * @throws LocatorException if an internal error occurs
     * @throws IOException if an IO error occurs when opening the stream
     */
    InputStream getInputStream() throws IOException;
}
