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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * <p>
 * A helper class for locating resources.
 * </p>
 * <p>
 * This class provides functionality for locating resources like configuration
 * files, which can be specified in multiple ways:
 * </p>
 * <p>
 * <ul>
 * <li>as absolute URLs.</li>
 * <li>as absolute or relative files.</li>
 * <li>as resources in the application's class path.</li>
 * <li>through {@link Locator} objects.</li>
 * </ul>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: LocatorUtils.java 211 2012-07-10 19:49:13Z oheger $
 */
public final class LocatorUtils
{
    /** Constant for the slash as prefix for resources.*/
    private static final String SLASH = "/";

    /** Constant for the buffer size for string generation.*/
    private static final int BUF_SIZE = 20;

    /**
     * Private constructor, so no instance can be created.
     */
    private LocatorUtils()
    {
    }

    /**
     * Returns the URL for the given file. This is a convenience method that
     * takes care for transforming the file to an URI and the URI eventually to
     * a URL. If a <b>null</b> parameter is passed in, result will also be
     * <b>null</b>.
     *
     * @param file the file to be transformed
     * @return the corresponding URL
     * @throws LocatorException if the transformation fails
     */
    public static URL fileURL(File file)
    {
        if (file == null)
        {
            return null;
        }

        try
        {
            return file.toURI().toURL();
        }
        catch (MalformedURLException mex)
        {
            throw new LocatorException(mex);
        }
    }

    /**
     * Tries to locate a resource in the class path using the specified class
     * loader. If the {@code ClassLoader} parameter is not <b>null</b>, this
     * class loader is tried first. If this fails, this method tries the
     * context, the default, and the system class loader (in this order) to find
     * the resource in the class path. Search stops as soon as the resource is
     * found.
     *
     * @param resource the name of the resource
     * @param cl the class loader to use for looking up the resource (can be
     *        <b>null</b>)
     * @return the URL to the resource or <b>null</b> if it cannot be resolved
     * @since 1.2
     */
    public static URL locateResource(String resource, ClassLoader cl)
    {
        if (resource == null)
        {
            return null;
        }

        URL result = null;
        if (cl != null)
        {
            result = cl.getResource(resource);
        }

        if (result == null)
        {
            ClassLoader ctxCL = Thread.currentThread().getContextClassLoader();
            if (ctxCL != null)
            {
                result = ctxCL.getResource(resource);
            }
        }

        if (result == null)
        {
            result = LocatorUtils.class.getResource(resource);
        }

        if (result == null)
        {
            result = ClassLoader.getSystemResource(resource);
        }

        if (result == null && !resource.startsWith(SLASH))
        {
            result = locateResource(SLASH + resource, cl);
        }
        return result;
    }

    /**
     * Tries to locate a resource in the class path. No specific class loader is
     * provided, so this method tries the context, the default, and the system
     * class loader (in this order) to find the resource in the class path.
     *
     * @param resource the name of the resource
     * @return the URL to the resource or <b>null</b> if it cannot be resolved
     * @see #locateResource(String, ClassLoader)
     */
    public static URL locateResource(String resource)
    {
        return locateResource(resource, null);
    }

    /**
     * Locates a file using a string, which can be either a full URL or a file
     * name. Some variants are tried until a valid resource can be found.
     *
     * @param url the URL of the file to be located
     * @return the full URL to this file or <b>null</b> if it cannot be found
     */
    public static URL locateURL(String url)
    {
        if (url != null)
        {
            try
            {
                return new URL(url);
            }
            catch (MalformedURLException mex)
            {
                // no valid URL, check if it is a file
                File f = new File(url);
                if (f.exists())
                {
                    return fileURL(f);
                }
            }
        }

        return null;
    }

    /**
     * Locates a resource either from a URL or a class path resource, returning
     * <b>null</b> if the resource cannot be resolved. This method combines the
     * methods {@code locateURL()} and {@code locateResource()}. If a URL is
     * defined, it is tried to be resolved. If this fails and a resource name is
     * defined, this name is tried to be resolved. If both parameters are
     * defined and valid, the URL takes precedence. If both attempts fail, the
     * result is <b>null</b>. If a {@code ClassLoader} is specified, it is used
     * during class path lookup as described at
     * {@link #locateResource(String, ClassLoader)}.
     *
     * @param url specifies a URL
     * @param name specifies a resource name
     * @param cl an optional class loader for the class path lookup
     * @return the resolved URL or <b>null</b> if the resource cannot be
     *         resolved
     * @since 1.2
     */
    public static URL locate(String url, String name, ClassLoader cl)
    {
        URL result = locateURL(url);
        if (result == null)
        {
            result = locateResource(name, cl);
        }

        return result;
    }

    /**
     * Locates a resource either from a URL or a class path resource (using a
     * default class loader), returning <b>null</b> if the resource cannot be
     * resolved.
     *
     * @param url specifies a URL
     * @param name specifies a resource name
     * @return the resolved URL or <b>null</b>
     * @see #locate(String, String, ClassLoader)
     */
    public static URL locate(String url, String name)
    {
        return locate(url, name, null);
    }

    /**
     * Locates a resource either from a URL or a class path resource, throwing
     * an exception if the resource cannot be resolved. This is analogous to
     * {@code locate()}, but a failing lookup causes a {@code LocatorException}.
     *
     * @param url specifies a URL
     * @param name specifies a resource name
     * @param cl an optional class loader for the class path lookup
     * @return the resolved URL
     * @throws LocatorException if the resource cannot be resolved
     * @since 1.2
     */
    public static URL locateEx(String url, String name, ClassLoader cl)
    {
        URL result = locate(url, name, cl);
        if (result == null)
        {
            throw new LocatorException("Cannot resolve resource: URL = " + url
                    + ", resource name = " + name);
        }
        return result;
    }

    /**
     * Locates a resource either from a URL or a class path resource (using a
     * default class loader), throwing an exception if the resource cannot be
     * resolved.
     *
     * @param url specifies a URL
     * @param name specifies a resource name
     * @return the resolved URL
     * @throws LocatorException if the resource cannot be resolved
     * @see #locateEx(String, String, ClassLoader)
     */
    public static URL locateEx(String url, String name)
    {
        return locateEx(url, name, null);
    }

    /**
     * Obtains an input stream for the specified locator. This method will query
     * the different methods of the locator until a result is found. From this
     * result a stream will be created. The locator's methods are invoked in the
     * following order (until a non <b>null</b> result is obtained):
     * <ol>
     * <li>{@code getInputStream()}</li>
     * <li>{@code getFile()}</li>
     * <li>{@code getURL()}</li>
     * </ol>
     *
     * @param locator the locator
     * @return the input stream for this locator
     * @throws IOException if an IO error occurs
     * @throws LocatorException if the locator throws an exception or no valid
     * values are returned
     */
    public static InputStream openStream(Locator locator) throws IOException
    {
        if (locator == null)
        {
            throw new LocatorException("Locator must not be null!");
        }

        InputStream stream = locator.getInputStream();
        if (stream != null)
        {
            return stream;
        }
        else
        {
            File file = locator.getFile();
            if (file != null)
            {
                return new FileInputStream(file);
            }
            else
            {
                URL url = locator.getURL();
                if (url == null)
                {
                    throw new LocatorException("Locator returns only null!");
                }
                return url.openStream();
            }
        }
    }

    /**
     * Creates a string representation of a {@code Locator} object. This
     * string contains the fully qualified class name of the
     * {@code Locator} (<em>class</em>), its identity hash code (
     * <em>hash</em>), and the data passed to this method (<em>data</em>, which
     * is locator specific). It has the following form: {@code class@hash[ data
     * ]}. The concrete {@code Locator} implementations in this package use
     * this method in the implementation of their {@code toString()}
     * method.
     *
     * @param locator the {@code Locator} to be transformed to a string
     *        (must not be <b>null</b>)
     * @param locatorData the data of this locator
     * @return a string representation for this {@code Locator}
     * @throws IllegalArgumentException if the locator is <b>null</b>
     */
    public static String locatorToString(Locator locator, String locatorData)
    {
        if (locator == null)
        {
            throw new IllegalArgumentException("Locator must not be null!");
        }

        String clsName = locator.getClass().getName();
        String data = (locatorData != null) ? locatorData : "";
        StringBuilder buf = new StringBuilder(BUF_SIZE + clsName.length()
                + data.length());
        buf.append(clsName);
        buf.append('@').append(System.identityHashCode(locator));
        buf.append("[ ").append(data).append(" ]");

        return buf.toString();
    }

    /**
     * Extracts the data of the string representation of a {@code Locator}.
     * Strings created by the {@code locatorToString()} method contain some
     * information specific to the {@code Locator} object involved,
     * especially its identity hash code. This complicates things, for instance
     * in unit tests, when locators that are equal should produce equal string
     * representations. In such cases this method can be used. It produces a
     * string containing only the class name (not fully qualified) and the data
     * of the locator. The relevant parts are extracted from the given string,
     * which must conform to the format produced by the
     * {@code locatorToString()} method.
     *
     * @param locatorString the string for the locator (as generated by
     *        {@code locatorToString()})
     * @return the data string extracted
     * @throws IllegalArgumentException if the passed in string is <b>null</b>
     *         or does not conform to the expected format
     * @see #locatorToString(Locator, String)
     */
    public static String locatorToDataString(String locatorString)
    {
        if (locatorString == null)
        {
            throw new IllegalArgumentException(
                    "Locator string must not be null!");
        }

        StringBuilder buf = new StringBuilder();
        int posAt = find(locatorString, '@', 0);
        int idx = posAt - 1;
        while (idx > 0 && locatorString.charAt(idx) != '.')
        {
            idx--;
        }
        if (idx >= 0 && locatorString.charAt(idx) == '.')
        {
            idx++; // skip first dot
        }

        buf.append(locatorString.substring(idx, posAt));
        buf.append(locatorString.substring(find(locatorString, '[', posAt)));

        return buf.toString();
    }

    /**
     * Returns the data string for the specified {@code Locator}. This is a
     * short cut of {@code locatorToDataString(locator.toString()}. The
     * {@code toString()} implementation of the locator must produce
     * strings conforming to the format of {@code locatorToString()}.
     *
     * @param locator the locator to be transformed into a string
     * @return the data string for this locator
     * @throws IllegalArgumentException if the locator is <b>null</b> or has an
     *         invalid string representation
     */
    public static String locatorToDataString(Locator locator)
    {
        if (locator == null)
        {
            throw new IllegalArgumentException("Locator must not be null!");
        }

        return locatorToDataString(locator.toString());
    }

    /**
     * Searches in the given string for the specified character starting at the
     * start position. If the character cannot be found, an exception is thrown.
     *
     * @param s the string
     * @param c the character to search for
     * @param start the start index
     * @return the position of the found character
     * @throws IllegalArgumentException if the character cannot be found
     */
    private static int find(String s, char c, int start)
    {
        int pos = s.indexOf(c, start);
        if (pos < 0)
        {
            throw new IllegalArgumentException("Cannot find '" + c + "' in "
                    + s);
        }
        return pos;
    }
}
