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
package net.sf.jguiraffe.resources;

import java.util.Locale;
import java.util.MissingResourceException;

/**
 * <p>
 * Definition of an interface for accessing resources in a generic way.
 * </p>
 * <p>
 * Through this interface applications can access a resource manager object that
 * is able to provide access to resource items or whole resource groups. This
 * service is completely independent on the way the resources of this
 * application are stored. The physical resource access is performed by a
 * <code>ResourceLoader</code> object that is associated with this class.
 * </p>
 * <p>
 * Resources supported by this library are always organized in logical resource
 * groups. This allows for a logic structure. When resources are accessed a
 * group name must always be provided.
 * </p>
 * <p>
 * This interface defines all needed methods for accessing resources. A single
 * item can be retrieved as object or, for convenience purpose, as string. It is
 * also possible to retrieve a whole resource group.
 * </p>
 *
 * @see ResourceGroup
 * @see ResourceLoader
 *
 * @author Oliver Heger
 * @version $Id: ResourceManager.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface ResourceManager
{
    /**
     * Returns the resource for the given <code>Locale</code>, resource
     * group, and key.
     *
     * @param locale the <code>Locale</code>
     * @param group the name of the resource group the resource belongs to
     * @param key the resource key
     * @return the specified resource
     * @throws MissingResourceException if the resource cannot be found
     */
    Object getResource(Locale locale, Object group, Object key)
            throws MissingResourceException;

    /**
     * Returns the text resource for the given combination of a
     * <code>Locale</code>, resource group, and resource key. This is a
     * convenience method if the resource is known to be a text resource.
     *
     * @param locale the <code>Locale</code>
     * @param group the name of the resource group the resource belongs to
     * @param key the resource key
     * @return the specified resource
     * @throws MissingResourceException if the resource cannot be found
     */
    String getText(Locale locale, Object group, Object key)
            throws MissingResourceException;

    /**
     * Returns the <code>ResourceLoader</code> that is associated with this
     * resource manager.
     *
     * @return the associated <code>ResourceLoader</code>
     */
    ResourceLoader getResourceLoader();

    /**
     * Sets the <code>ResourceLoader</code> for this resource manager. This
     * loader is then used to retrieve resource groups.
     *
     * @param resourceLoader the <code>ResourceLoader</code> to be used
     */
    void setResourceLoader(ResourceLoader resourceLoader);

    /**
     * Returns the resource group for the specified <code>Locale</code> with
     * the given name. The returned object can be used to retrieve all resources
     * that belong to this group at once.
     *
     * @param locale the <code>Locale</code>
     * @param group the name of the resource group
     * @return the found resource group
     * @throws MissingResourceException if the resource group cannot be found
     */
    ResourceGroup getResourceGroup(Locale locale, Object group)
            throws MissingResourceException;

    /**
     * Returns the name of the default resource group.
     *
     * @return the default resource group's name
     */
    Object getDefaultResourceGroup();

    /**
     * Sets the name of the default resource group. If a resource is queried
     * with an undefined group name, this default group will be used.
     *
     * @param grp the name of the default resource group
     */
    void setDefaultResourceGroup(Object grp);
}
