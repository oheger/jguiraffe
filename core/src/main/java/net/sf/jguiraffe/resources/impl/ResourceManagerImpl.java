/*
 * Copyright 2006-2012 The JGUIraffe Team.
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
package net.sf.jguiraffe.resources.impl;

import java.util.Locale;
import java.util.MissingResourceException;

import net.sf.jguiraffe.resources.ResourceGroup;
import net.sf.jguiraffe.resources.ResourceLoader;
import net.sf.jguiraffe.resources.ResourceManager;

/**
 * <p>
 * A default implementation of the <code>ResourceManager</code> interface.
 * </p>
 * <p>
 * This class provides a fully functional <code>ResourceManager</code>
 * implementation that can be used as is. There is usually no need to subclass
 * this class or use a different implementation.
 * </p>
 * <p>
 * The class uses the associated <code>ResourceLoader</code> to retrieve
 * requested resources or resource groups. No caching is performed, this can be
 * done in the <code>ResourceLoader</code>.
 * </p>
 * <p>Implementation note: This class is thread-safe.</p>
 *
 * @author Oliver Heger
 * @version $Id: ResourceManagerImpl.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ResourceManagerImpl implements ResourceManager
{
    /** Stores the associated resource loader object. */
    private volatile ResourceLoader resourceLoader;

    /** Stores the name of the default resource group. */
    private volatile Object defaultResourceGroup;

    /**
     * Creates a new instance of <code>ResourceManagerImpl</code>.
     */
    public ResourceManagerImpl()
    {
        this(null);
    }

    /**
     * Creates a new instance of <code>ResourceManagerImpl</code> and
     * initializes the associated resource loader.
     *
     * @param loader the resource loader to use
     */
    public ResourceManagerImpl(ResourceLoader loader)
    {
        setResourceLoader(loader);
    }

    /**
     * Returns the specified resource.
     *
     * @param locale the <code>Locale</code>
     * @param group the owning resource group's name
     * @param key the resource key
     * @return the found resource
     * @throws MissingResourceException if the resource cannot be found
     */
    public Object getResource(Locale locale, Object group, Object key)
    {
        return getResourceGroup(locale,
                (group != null) ? group : getDefaultResourceGroup())
                .getResource(key);
    }

    /**
     * Returns the specified resource group.
     *
     * @param locale the <code>Locale</code> of the group
     * @param group the group's name
     * @return the specified resource group
     * @throws MissingResourceException if the group cannot be found
     */
    public ResourceGroup getResourceGroup(Locale locale, Object group)
    {
        return fetchLoader().loadGroup(locale, group);
    }

    /**
     * Returns the text of the specified resource.
     *
     * @param locale the <code>Locale</code>
     * @param group the name of the resource group
     * @param key the resource key
     * @return the text of the specified resource
     * @throws MissingResourceException if the resource cannot be found
     */
    public String getText(Locale locale, Object group, Object key)
            throws MissingResourceException
    {
        return getResource(locale, group, key).toString();
    }

    /**
     * Returns the associated <code>ResourceLoader</code> object.
     *
     * @return the <code>ResourceLoader</code>
     */
    public ResourceLoader getResourceLoader()
    {
        return resourceLoader;
    }

    /**
     * Sets the <code>ResourceLoader</code> to use. Requests for resource
     * groups are delegated to this object.
     *
     * @param resourceLoader the <code>ResourceLoader</code> to use
     */
    public void setResourceLoader(ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Fetches the resource loader. This method is called whenever access to a
     * resource loader is needed. It checks if a resource loader is defined and
     * if not, throws an exception.
     *
     * @return the resource loader to use
     */
    protected ResourceLoader fetchLoader()
    {
        ResourceLoader loader = getResourceLoader();
        if (loader == null)
        {
            throw new IllegalStateException("No ResourceLoader defined!");
        }
        return getResourceLoader();
    }

    /**
     * Returns the name of the default resource group.
     *
     * @return the name of the default resource group
     */
    public Object getDefaultResourceGroup()
    {
        return defaultResourceGroup;
    }

    /**
     * Sets the name of the default resource group.
     *
     * @param grp the name of the default resource group
     */
    public void setDefaultResourceGroup(Object grp)
    {
        defaultResourceGroup = grp;
    }
}
