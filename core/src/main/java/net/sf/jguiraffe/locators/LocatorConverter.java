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

import java.util.Locale;

import net.sf.jguiraffe.di.ClassLoaderProvider;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

/**
 * <p>
 * A specialized {@code Converter} implementation dealing with {@link Locator}
 * objects.
 * </p>
 * <p>
 * This converter implementation is able to transform text representations of
 * locators into concrete {@link Locator} instances. This is pretty convenient,
 * especially for bean declarations; here a compact text representation for
 * locators is much more readable than a verbose declaration of a factory method
 * invocation.
 * </p>
 * <p>
 * This converter supports most of the standard {@link Locator} implementations
 * provided by this package. A text representation of a locator starts with a
 * prefix, followed by a colon. Then the specific data of the concrete
 * {@link Locator} subclass is appended. The following table lists the supported
 * prefixes with their meaning and examples:
 * <table border="1">
 * <tr>
 * <th>Prefix</th>
 * <th>Description</th>
 * <th>Example</th>
 * </tr>
 * <tr>
 * <td valign="top">classpath</td>
 * <td>Creates a {@link ClassPathLocator} instance. The data is interpreted as a
 * resource name which is looked up on the current class path. Optionally, a
 * class loader name can be provided separated by a semicolon. If this is used,
 * the corresponding class loader is obtained from the
 * {@link ClassLoaderProvider}; otherwise, the default class loader is used.</td>
 * <td>classpath:myresource.properties<br>
 * classpath:myresource.properties;myClassLoader</td>
 * </tr>
 * <tr>
 * <td valign="top">file</td>
 * <td>Creates a {@link FileLocator} instance. The data is interpreted as a
 * relative or absolute file name. It is directly passed to the
 * {@link FileLocator} instance to be created.</td>
 * <td>file:target/data.txt</td>
 * </tr>
 * <tr>
 * <td valign="top">url</td>
 * <td>Creates a {@link URLLocator} instance. The data is interpreted as a URL
 * in text form. It is directly passed to the {@link URLLocator} instance to be
 * created.</td>
 * <td>url:http://www.mydomain.com/image.jpg</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * Prefixes are not case sensitive. If an unknown prefix is encountered, a
 * {@code ConversionException} is thrown. An instance of this class is
 * registered as base class converter by the form builder per default. Using the
 * data type conversion mechanism provided by the dependency injection
 * framework, it is possible to add further converters for custom
 * {@link Locator} implementations.
 * </p>
 * <p>
 * This class does not have any internal state. Thus an instance can be shared
 * between multiple components and called concurrently.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: LocatorConverter.java 213 2012-07-14 19:40:51Z oheger $
 */
public class LocatorConverter implements Converter
{
    /**
     * Constant for the prefix separator. This character is used to separate the
     * prefix which identifies the type of the locator from its data.
     */
    public static final char PREFIX_SEPARATOR = ':';

    /**
     * The separator for a class loader name. This character is evaluated for
     * locator declarations of type classpath.
     */
    private static final char CL_SEPARATOR = ';';

    /** The current class loader provider. */
    private final ClassLoaderProvider classLoaderProvider;

    /**
     * Creates a new instance of {@code LocatorConverter} and initializes it
     * with the given {@code ClassLoaderProvider}. Class loaders for class path
     * locators are obtained from this provider.
     *
     * @param clp the {@code ClassLoaderProvider}
     * @since 1.2
     */
    public LocatorConverter(ClassLoaderProvider clp)
    {
        classLoaderProvider = clp;
    }

    /**
     * Creates a new instance of {@code LocatorConverter} without a class loader
     * provider. A converter constructed this way cannot resolve any class
     * loader names. This constructor exists for reasons of backwards
     * compatibility. It is recommended to always provide a
     * {@code ClassLoaderProvider}.
     */
    public LocatorConverter()
    {
        this(null);
    }

    /**
     * Returns the {@code ClassLoaderProvider} used by this converter. This
     * object is used to determine class loaders when locators for class path
     * resources are to be created. Result may be <b>null</b> if no
     * {@code ClassLoaderProvider} has been set.
     *
     * @return the current {@code ClassLoaderProvider}
     * @since 1.2
     */
    public ClassLoaderProvider getClassLoaderProvider()
    {
        return classLoaderProvider;
    }

    /**
     * Tries to convert the specified object to a {@link Locator}. The
     * conversion is based on prefixes as described in the class comment.
     *
     * @param type the target class
     * @param value the object to be converted (must not be <b>null</b>)
     * @return the converted object; this is a concrete implementation of the
     *         {@link Locator} interface
     * @throws ConversionException if conversion is not possible
     */
    public Object convert(@SuppressWarnings("rawtypes") Class type, Object value)
    {
        if (value == null)
        {
            throw new ConversionException("Cannot convert null Locator!");
        }

        return convertLocator(String.valueOf(value));
    }

    /**
     * Obtains a {@code LocatorRepresentation} instance which corresponds to the
     * specified prefix extracted from the textual representation of a locator.
     * If this is not possible, an exception is thrown.
     *
     * @param locatorPrefix the prefix identifying the concrete locator type
     * @return the corresponding {@code LocatorRepresentation} instance
     * @throws ConversionException if no fitting instance can be found
     */
    private LocatorRepresentation getRepresentation(String locatorPrefix)
    {
        try
        {
            return LocatorRepresentation.valueOf(locatorPrefix
                    .toUpperCase(Locale.ENGLISH));
        }
        catch (IllegalArgumentException iex)
        {
            throw new ConversionException("Failed conversion to a Locator! "
                    + "Unknown prefix: " + locatorPrefix);
        }
    }

    /**
     * Converts the given textual representation of a locator to a concrete
     * {@link Locator} instance.
     *
     * @param rep the string-based representation of the locator
     * @return the corresponding {@link Locator} instance
     * @throws ConversionException if conversion is not possible
     */
    private Locator convertLocator(String rep)
    {
        int pos = rep.indexOf(PREFIX_SEPARATOR);
        if (pos < 0)
        {
            throw new ConversionException(
                    "Invalid syntax of a Locator representation: '" + rep
                            + "'! Cannot find a prefix for the Locator type.");
        }
        if (pos == rep.length() - 1)
        {
            throw new ConversionException("No data provided for locator: "
                    + rep);
        }

        LocatorRepresentation locRep = getRepresentation(rep.substring(0, pos));
        try
        {
            return locRep.createLocator(rep.substring(pos + 1),
                    getClassLoaderProvider());
        }
        catch (LocatorException lex)
        {
            throw new ConversionException("Conversion to Locator failed for "
                    + rep, lex);
        }
    }

    /**
     * An internally used enumeration class for the text representations of
     * locators. This class is used to identify the desired locator type and to
     * create a corresponding instance.
     */
    private static enum LocatorRepresentation
    {
        /** Constant for a class path locator. */
        CLASSPATH
        {
            /**
             * Creates a {@link ClassPathLocator}. The passed in data is
             * interpreted as the resource name. If it contains a class loader
             * name (separated by a semicolon), this class loader is obtained
             * from the given {@code ClassLoaderProvider} and passed to the
             * newly created {@code Locator}.
             *
             * @param data data for the locator
             * @param clp the {@code ClassLoaderProvider}
             * @return the new locator instance
             */
            @Override
            public Locator createLocator(String data, ClassLoaderProvider clp)
            {
                String resourceName;
                String clName;
                int posCLName = data.indexOf(CL_SEPARATOR);
                if (posCLName > 0)
                {
                    resourceName = data.substring(0, posCLName);
                    clName = data.substring(posCLName + 1);
                }
                else
                {
                    resourceName = data;
                    clName = null;
                }
                ClassLoader cl =
                        (clp != null) ? clp.getClassLoader(clName) : null;

                return ClassPathLocator.getInstance(resourceName, cl);
            }
        },

        /** Constant for a file locator. */
        FILE
        {
            /**
             * Creates a {@link FileLocator}. The passed in data is interpreted
             * as the file name.
             *
             * @param data data for the locator
             * @param clp the {@code ClassLoaderProvider}
             * @return the new locator instance
             */
            @Override
            public Locator createLocator(String data, ClassLoaderProvider clp)
            {
                return FileLocator.getInstance(data);
            }
        },

        /** Constant for a URL locator. */
        URL
        {
            /**
             * Creates a {@link URLLocator}. The passed in data is interpreted
             * as the text representation of the URL.
             *
             * @param data data for the locator
             * @param clp the {@code ClassLoaderProvider}
             * @return the new locator instance
             */
            @Override
            public Locator createLocator(String data, ClassLoaderProvider clp)
            {
                return URLLocator.getInstance(data);
            }
        };

        /**
         * Creates the concrete {@link Locator} implementation represented by
         * this object. The passed in data was read from the specification
         * string. It is provided to the locator.
         *
         * @param data the data for the new locator
         * @param clp the provider for class loaders
         * @return the locator instance
         */
        public abstract Locator createLocator(String data,
                ClassLoaderProvider clp);
    }
}
