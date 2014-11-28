/*
 * Copyright 2006-2014 The JGUIraffe Team.
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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>
 * An abstract base class for <code>Locator</code> implementations that mainly
 * operate on streams.
 * </p>
 * <p>
 * The <code>Locator</code> interface requires that the <code>getURL()</code>
 * method has to be implemented in a meaningful way. However, there are some use
 * cases where data is available only as a stream (e.g. in some binary form),
 * and it is not obvious how a URL can be constructed to point to this data.
 * This class is designed to support such use cases.
 * </p>
 * <p>
 * The basic idea is that this class provides a specialized implementation of
 * the abstract <code>java.net.URLStreamHandler</code> class. This
 * implementation can create <code>java.net.URLConnection</code> objects whose
 * <code>getInputStream()</code> method returns the input stream provided by the
 * concrete <code>Locator</code> implementation.
 * </p>
 * <p>
 * Concrete subclasses have to implement the <code>getInputStream()</code>
 * method to return the stream they point to. They also have to provide an
 * implementation of the <code>createURL()</code> method. This method is passed
 * a <code>URLStreamHandler</code> object as described above. An implementation
 * of <code>createURL()</code> can create a URL with an arbitrary protocol and
 * other components as it sees fit. As long as the <code>URLStreamHandler</code>
 * is used when creating the URL it is guaranteed that calls to
 * <code>openConnection()</code> or <code>openStream()</code> on the URL object
 * return the stream of the <code>Locator</code>.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: AbstractStreamLocator.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class AbstractStreamLocator extends AbstractLocator
{
    /** Stores the URL managed by this locator. */
    private final AtomicReference<URL> urlReference;

    /**
     * Creates a new instance of <code>AbstractStreamLocator</code>.
     */
    protected AbstractStreamLocator()
    {
        urlReference = new AtomicReference<URL>();
    }

    /**
     * Returns the <code>URL</code> managed by this locator. On first access
     * this implementation delegates to <code>createURL()</code> for creating
     * the URL. Then the URL is cached and directly returned on subsequent
     * requests. Note that a non-blocking algorithm is used for lazily creating
     * the URL. So on concurrent access it may happen that
     * <code>createURL()</code> is invoked multiple times. However, it is
     * guaranteed that this method always returns the same <code>URL</code>.
     *
     * @return the <code>URL</code> managed by this locator
     */
    public URL getURL()
    {
        URL url = urlReference.get();

        if (url == null)
        {
            // The URL has not been created yet, so do it now.
            try
            {
                url = createURL(new StreamLocatorURLStreamHandler());
            }
            catch (MalformedURLException mex)
            {
                throw new LocatorException(mex);
            }
            if (!urlReference.compareAndSet(null, url))
            {
                // In the meantime the URL was created by another thread.
                url = urlReference.get();
            }
        }

        return url;
    }

    /**
     * Creates the URL managed by this locator. This method is called by
     * <code>getURL()</code> to initialize the URL. An implementation is free to
     * create whatever URL it likes, but it should use the specified
     * <code>URLStreamHandler</code>. This handler ensures that reading from the
     * URL is delegated to the stream managed by this locator.
     *
     * @param streamHandler the stream handler to use when creating the URL
     * @return the URL to be returned by <code>getURL()</code>
     * @throws MalformedURLException if the URL cannot be created
     */
    protected abstract URL createURL(URLStreamHandler streamHandler)
            throws MalformedURLException;

    /**
     * A specialized URLConnection implementation that always returns the stream
     * managed by this locator as input stream. This allows this locator class
     * to return a URL that can be used in the usual way, but actually points to
     * the data managed by this locator.
     */
    private class StreamURLConnection extends URLConnection
    {
        /**
         * Creates a new instance of <code>StreamURLConnection</code>.
         *
         * @param url the URL
         */
        public StreamURLConnection(URL url)
        {
            super(url);
        }

        /**
         * Dummy implementation of this method. We do not make any connection,
         * but always return the stream managed by this locator.
         *
         * @throws IOException if an error occurs
         */
        @Override
        public void connect() throws IOException
        {
        }

        /**
         * Returns the input stream for this URL. This is actually the stream of
         * the locator.
         *
         * @return an input stream for reading from this URL
         * @throws IOException if an error occurs
         */
        @Override
        public InputStream getInputStream() throws IOException
        {
            return AbstractStreamLocator.this.getInputStream();
        }
    }

    /**
     * A specialized URLStreamHandler implementation. This implementation
     * creates URLConnection objects for URLs that access the input stream
     * managed by this locator.
     */
    private class StreamLocatorURLStreamHandler extends URLStreamHandler
    {
        /**
         * Returns a connection object for the specified URL. This
         * implementation returns the fake connection class that always accesses
         * this locator's input stream.
         *
         * @param u the URL
         * @return a connection for this URL
         * @throws IOException if an error occurs
         */
        @Override
        protected URLConnection openConnection(URL u) throws IOException
        {
            return new StreamURLConnection(u);
        }
    }
}
