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
package net.sf.jguiraffe.resources.impl.bundle;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import net.sf.jguiraffe.resources.ResourceGroup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * A specialized implementation of the {@code ResourceGroup} interface that
 * is backed by a {@code java.util.ResourceBundle}.
 * </p>
 * <p>
 * The methods required by the {@code ResourceGroup} interface are
 * delegated to the internally managed resource bundle. During construction this
 * bundle is loaded for the specified base name and locale.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: BundleResourceGroup.java 211 2012-07-10 19:49:13Z oheger $
 */
class BundleResourceGroup implements ResourceGroup
{
    /** The logger. */
    private final Log log = LogFactory.getLog(getClass());

    /** Stores the bundle this implementation is based on. */
    private final ResourceBundle bundle;

    /** Stores the name of this group. */
    private final String name;

    /** Stores the locale of this group. */
    private final Locale locale;

    /**
     * Creates a new instance of {@code BundleResourceGroup} and
     * initializes it.
     *
     * @param name the group's name
     * @param locale the locale
     * @param cl the class loader for resolving the bundle
     * @throws java.util.MissingResourceException if the bundle cannot be found
     */
    public BundleResourceGroup(String name, Locale locale, ClassLoader cl)
    {
        this.name = name;
        this.locale = locale;
        bundle = initBundle(name, locale, cl);
    }

    /**
     * Returns the bundle that is wrapped by this resource group.
     *
     * @return the underlying bundle
     */
    public ResourceBundle getBundle()
    {
        return bundle;
    }

    /**
     * Returns the name of this resource group.
     *
     * @return the name of this group
     */
    public Object getName()
    {
        return name;
    }

    /**
     * Returns a set with all keys contained in this resource group.
     *
     * @return a set with the defined keys
     */
    public Set<Object> getKeys()
    {
        Set<Object> result = new HashSet<Object>();
        for (Enumeration<String> en = getBundle().getKeys(); en
                .hasMoreElements();)
        {
            result.add(en.nextElement());
        }

        return result;
    }

    /**
     * Returns the locale of this group.
     *
     * @return the locale
     */
    public Locale getLocale()
    {
        return locale;
    }

    /**
     * Returns the resource for the specified key.
     *
     * @param key the key
     * @return the resource for this key
     * @throws java.util.MissingResourceException if this key is unknown
     * @throws IllegalArgumentException for a <b>null</b> key
     */
    public Object getResource(Object key)
    {
        if (key == null)
        {
            throw new IllegalArgumentException("Resource key must not be null!");
        }

        return getBundle().getObject(key.toString());
    }

    /**
     * Initializes the specified resource bundle. This method tries to load the
     * underlying resource bundle. First the provided class loader is used. If
     * this fails, a 2nd attempt is made with the class loader which loaded this
     * class.
     *
     * @param name the bundle's base name
     * @param locale the locale
     * @param cl the class loader for resolving the bundle
     * @return the bundle instance
     * @throws java.util.MissingResourceException if the bundle cannot be found
     */
    private ResourceBundle initBundle(String name, Locale locale, ClassLoader cl)
    {
        try
        {
            return ResourceBundle.getBundle(name, locale, cl);
        }
        catch (MissingResourceException mrex)
        {
            if (log.isInfoEnabled())
            {
                log.info("Could not load resource bundle '" + name
                        + "' using default class loader.");
            }
            return ResourceBundle.getBundle(name, locale);
        }
    }
}
