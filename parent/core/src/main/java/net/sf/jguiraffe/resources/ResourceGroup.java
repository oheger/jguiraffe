/*
 * Copyright 2006-2018 The JGUIraffe Team.
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
import java.util.Set;

/**
 * <p>
 * Definition of an interface for resource groups.
 * </p>
 * <p>
 * Resources can be organized in logical groups. Each group allows access to the
 * resources it contains and to a collection of all available keys. The
 * resources of a group all belong to the same <code>Locale</code>.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ResourceGroup.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface ResourceGroup
{
    /**
     * Returns the name of this resource group. It is up to a concrete
     * implementation what this name means in practice.
     *
     * @return the name of this resource group
     */
    Object getName();

    /**
     * Returns a collection with the resource keys defined in this resource
     * group. The keys in this collection can be passed to the
     * <code>getResource()</code> method.
     *
     * @return the collection with defined resource keys
     */
    Set<Object> getKeys();

    /**
     * Returns the <code>Locale</code> of this resource group
     *
     * @return the Locale
     */
    Locale getLocale();

    /**
     * Returns the resource with the specified key. This is the main method for
     * accessing resources in this group.
     *
     * @param key the resource key
     * @return the resource with this key
     * @throws MissingResourceException if the resource cannot be found
     */
    Object getResource(Object key) throws MissingResourceException;
}
