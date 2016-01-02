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
package net.sf.jguiraffe.resources.impl.bundle;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.impl.DefaultClassLoaderProvider;
import net.sf.jguiraffe.resources.ResourceGroup;
import net.sf.jguiraffe.resources.ResourceLoader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * A specialized implementation of the {@code ResourceLoader} interface that
 * make use of the default Java resource bundles.
 * </p>
 * <p>
 * This class uses the passed in {@code Locale} and resource group name (which
 * is interpreted as string) to find a corresponding resource bundle. For this
 * bundle a wrapping object is returned that implements the
 * {@link de.olix.gen.resources.ResourceGroup ResourceGroup} interface.
 * </p>
 * <p>
 * When creating an instance a {@link ClassLoaderProvider} and a class loader
 * name can be provided. If set, the resource bundle is loaded using the class
 * loader specified this way.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: BundleResourceLoader.java 211 2012-07-10 19:49:13Z oheger $
 */
public class BundleResourceLoader implements ResourceLoader
{
    /** Constant for the initial key buffer size. */
    private static final int BUF_SIZE = 64;

    /** Constant for the key separator character. */
    private static final char KEY_SEPRATOR = '_';

    /** The logger. */
    private final Log log = LogFactory.getLog(getClass());

    /** A cache for the so far created resource groups. */
    private final ConcurrentMap<Object, ResourceGroup> groups;

    /** The class loader provider for resolving class loaders. */
    private final ClassLoaderProvider classLoaderProvider;

    /** The name of the class loader for loading resource bundles. */
    private final String classLoaderName;

    /**
     * Creates a new instance of {@code BundleResourceLoader} without a special
     * {@code ClassLoaderProvider}; a default one is created.
     */
    public BundleResourceLoader()
    {
        this(null, null);
    }

    /**
     * Creates a new instance of {@code BundleResourceLoader} and initializes it
     * with the given class loader provider and the class loader name.
     *
     * @param clp the {@code ClassLoaderProvider}; can be <b>null</b>, then a
     *        default instance is created
     * @param clName the name of the class loader to use; can be <b>null</b>,
     *        then the default class loader of the {@code ClassLoaderProvider}
     *        is used
     * @since 1.2
     */
    public BundleResourceLoader(ClassLoaderProvider clp, String clName)
    {
        classLoaderName = clName;
        classLoaderProvider =
                (clp != null) ? clp : new DefaultClassLoaderProvider();
        groups = new ConcurrentHashMap<Object, ResourceGroup>();
    }

    /**
     * Returns the {@code ClassLoaderProvider} used by this object. Note: This
     * method never returns <b>null</b>; if no class loader provider was passed
     * to the constructor, a default instance is returned.
     *
     * @return the {@code ClassLoaderProvider}
     * @since 1.2
     */
    public ClassLoaderProvider getClassLoaderProvider()
    {
        return classLoaderProvider;
    }

    /**
     * Returns the name of the class loader used for resolving resource bundles.
     *
     * @return the class loader name (may be <b>null</b>)
     * @since 1.2
     */
    public String getClassLoaderName()
    {
        return classLoaderName;
    }

    /**
     * Loads the specified resource group from a resource bundle.
     *
     * @param locale the {@code Locale}
     * @param name the group's name
     * @return the found resource group
     * @throws MissingResourceException if the group cannot be found
     */
    public ResourceGroup loadGroup(Locale locale, Object name)
    {
        if (name == null)
        {
            throw new IllegalArgumentException(
                    "Resource group name must not be null!");
        }

        String key = keyForGroup(locale, name);
        ResourceGroup group = groups.get(key);
        if (group == null)
        {
            group = createGroup(name.toString(), locale);
            ResourceGroup group2 = groups.putIfAbsent(key, group);
            if (group2 != null)
            {
                group = group2;
            }
        }

        return group;
    }

    /**
     * Constructs a key for the given combination of a locale and a group name.
     * This key is used for storing a group in the cache after it was created.
     *
     * @param locale the locale
     * @param name the group's name
     * @return a key for this group
     */
    protected String keyForGroup(Locale locale, Object name)
    {
        StringBuffer buf = new StringBuffer(BUF_SIZE);
        buf.append(name).append(KEY_SEPRATOR);
        if (locale != null)
        {
            buf.append(locale);
        }
        return buf.toString();
    }

    /**
     * Creates a {@code ResourceGroup} object from the specified bundle.
     *
     * @param locale the {@code Locale}
     * @param name the group's name
     * @return the new resource group
     * @throws MissingResourceException if the bundle cannot be found
     */
    protected ResourceGroup createGroup(String name, Locale locale)
            throws MissingResourceException
    {
        if (log.isInfoEnabled())
        {
            log.info("Loading resource group " + name + " for locale " + locale);
        }
        return new BundleResourceGroup(name, locale, getClassLoaderProvider()
                .getClassLoader(getClassLoaderName()));
    }
}
