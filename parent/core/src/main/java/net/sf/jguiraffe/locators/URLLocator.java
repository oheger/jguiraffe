/*
 * Copyright 2006-2021 The JGUIraffe Team.
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

import java.net.MalformedURLException;
import java.net.URL;

/**
 * <p>
 * A specialized {@link Locator} implementation that operates on URLs.
 * </p>
 * <p>
 * This is a straight forward implementation of the <code>Locator</code>
 * interface that is based on a URL. New instances can be created using one of
 * the <code>getInstance()</code> methods by either passing in a URL or its
 * string representation. The implementation of the <code>getURL()</code> method
 * then directly returns this URL. Other methods defined in the
 * <code>Locator</code> interface are implemented as empty stubs only.
 * </p>
 * <p>
 * Instances of this class are immutable and thus can be shared between multiple
 * threads.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: URLLocator.java 205 2012-01-29 18:29:57Z oheger $
 */
public final class URLLocator extends AbstractLocator
{
    /** Stores the underlying URL. */
    private final URL url;

    /**
     * Creates a new instance of <code>URLLocator</code> and initializes it with
     * the underlying URL. Clients use the static factory methods for creating
     * instances.
     *
     * @param u the URL
     */
    private URLLocator(URL u)
    {
        url = u;
    }

    /**
     * Returns a <code>URLLocator</code> instance for the specified URL.
     *
     * @param url the URL (must not be <b>null</b>)
     * @return the <code>URLLocator</code> instance for this URL
     * @throws IllegalArgumentException if the URL is <b>null</b>
     */
    public static URLLocator getInstance(URL url)
    {
        if (url == null)
        {
            throw new IllegalArgumentException("URL must not be null!");
        }

        return new URLLocator(url);
    }

    /**
     * Returns a <code>URLLocator</code> instance for the URL specified as
     * string. This method converts the given string into a URL and returns a
     * corresponding <code>URLLocator</code>.
     *
     * @param sUrl the URL as string
     * @return the <code>URLLocator</code> for this URL
     * @throws LocatorException if the string cannot be transformed into a URL
     * @throws IllegalArgumentException if the string is <b>null</b>
     */
    public static URLLocator getInstance(String sUrl)
    {
        if (sUrl == null)
        {
            throw new IllegalArgumentException("URL must not be null!");
        }

        try
        {
            return new URLLocator(new URL(sUrl));
        }
        catch (MalformedURLException mex)
        {
            throw new LocatorException(mex);
        }
    }

    /**
     * Returns the URL represented by this locator. This is simply the URL that
     * was specified when this instance was created.
     *
     * @return the URL represented by this locator
     */
    public URL getURL()
    {
        return url;
    }

    /**
     * Compares this object with another one. Two instance of this class are
     * equal if an only if they refer to the same URL.
     *
     * @param obj the object to compare to
     * @return a flag whether these objects are equal
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof URLLocator))
        {
            return false;
        }

        URLLocator c = (URLLocator) obj;
        return getURL().toExternalForm().equals(c.getURL().toExternalForm());
    }

    /**
     * Returns a hash code for this object.
     *
     * @return a hash code
     */
    @Override
    public int hashCode()
    {
        return getURL().toExternalForm().hashCode();
    }

    /**
     * Returns a string representation of this object. This string will contain
     * the URL underlying this locator.
     *
     * @return a string for this object
     */
    @Override
    public String toString()
    {
        return LocatorUtils.locatorToString(this, "url = " + getURL());
    }
}
