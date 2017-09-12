/*
 * Copyright 2006-2017 The JGUIraffe Team.
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.Arrays;

/**
 * <p>
 * A specialized {@link Locator} implementation that provides access to data
 * stored in memory as a byte array.
 * </p>
 * <p>
 * This locator can be initialized either with a byte array or with a string.
 * This data is kept in memory. The locator returns a stream or an URL providing
 * access to exactly this data.
 * </p>
 * <p>
 * This class is especially useful for providing access to small amounts of data
 * to clients that can deal with locators. It can also be of value for unit
 * tests supporting an easy way of defining test data.
 * </p>
 * <p>
 * Instances are created using one of the static <code>getInstance()</code>
 * factory methods. This class is thread-safe and can be used concurrently by
 * multiple threads. Each invocation of <code>getInputStream()</code> returns a
 * new stream instance initialized with the data of this locator.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ByteArrayLocator.java 205 2012-01-29 18:29:57Z oheger $
 */
public final class ByteArrayLocator extends AbstractStreamLocator
{
    /** Constant for the maximum number of elements printed by toString(). */
    static final int MAX_ELEMENTS = 50;

    /** Constant for the protocol used for the URL. */
    private static final String PROTOCOL = "data";

    /** Constant for the length factor for calculating the string buffer size. */
    private static final int LENGTH_FACTOR = 5;

    /** Constant for the static size of the string buffer. */
    private static final int BUF_SIZE = 8;

    /** Stores the data of this locator. */
    private final byte[] data;

    /**
     * Creates a new instance of <code>ByteArrayLocator</code> and initializes
     * it with the data array. Clients use the static factory methods for
     * creating instances.
     *
     * @param d the data array
     */
    private ByteArrayLocator(byte[] d)
    {
        data = d;
    }

    /**
     * Creates an instance of <code>ByteArrayLocator</code> that is initialized
     * with the specified data.
     *
     * @param data the data for the locator (must not be <b>null</b>)
     * @return the locator instance pointing to this data
     * @throws IllegalArgumentException if the data is <b>null</b>
     */
    public static ByteArrayLocator getInstance(byte[] data)
    {
        if (data == null)
        {
            throw new IllegalArgumentException("Data must not be null!");
        }

        return new ByteArrayLocator(data.clone());
    }

    /**
     * Creates an instance of <code>ByteArrayLocator</code> that is initialized
     * with the data of the specified string.
     *
     * @param data the data for the locator (must not be <b>null</b>)
     * @return the locator instance pointing to this data
     * @throws IllegalArgumentException if the data is <b>null</b>
     */
    public static ByteArrayLocator getInstance(String data)
    {
        if (data == null)
        {
            throw new IllegalArgumentException("Data must not be null!");
        }

        return new ByteArrayLocator(data.getBytes());
    }

    /**
     * Returns an input stream for the data of this locator. This implementation
     * creates a stream that allows reading the data this locator points to in
     * memory.
     *
     * @return the input stream for this locator
     * @throws IOException if an error occurs
     */
    @Override
    public InputStream getInputStream() throws IOException
    {
        return new ByteArrayInputStream(data);
    }

    /**
     * Compares this object with another one. Two instances of this class are
     * considered equal if and only if the data arrays they point to are equal.
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
        if (!(obj instanceof ByteArrayLocator))
        {
            return false;
        }

        ByteArrayLocator c = (ByteArrayLocator) obj;
        return Arrays.equals(data, c.data);
    }

    /**
     * Returns a hash code for this object.
     *
     * @return a hash code for this object
     */
    @Override
    public int hashCode()
    {
        return Arrays.hashCode(data);
    }

    /**
     * Returns a string representation for this object. This string will contain
     * (at least parts of) the data this locator points to. If the data array
     * contains too many elements, only an excerpt of the data is printed out.
     *
     * @return a string for this object
     */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder(Math.min(MAX_ELEMENTS,
                data.length)
                * LENGTH_FACTOR + BUF_SIZE);
        buf.append("data = ");

        // Obtain the data to be dumped. If the data array is small enough,
        // dump it complete; otherwise fetch a subset.
        byte[] d;
        if (data.length <= MAX_ELEMENTS)
        {
            d = data;
        }
        else
        {
            d = new byte[MAX_ELEMENTS];
            System.arraycopy(data, 0, d, 0, MAX_ELEMENTS);
        }
        buf.append(Arrays.toString(d));
        if (d != data)
        {
            buf.append("...");
        }

        return LocatorUtils.locatorToString(this, buf.toString());
    }

    /**
     * Creates a URL representing the data of this locator. This implementation
     * creates a URL with the protocol 'data' and a hash value of the data as
     * host.
     *
     * @param streamHandler the stream handler to use for this URL
     * @return the URL representing the data of this locator
     * @throws MalformedURLException if the URL cannot be created
     */
    @Override
    protected URL createURL(URLStreamHandler streamHandler)
            throws MalformedURLException
    {
        return new URL(PROTOCOL, String.valueOf(Arrays.hashCode(data)), -1, "",
                streamHandler);
    }
}
