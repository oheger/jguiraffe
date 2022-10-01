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
 * Definition of an interface for objects that are able to load resource groups.
 * </p>
 * <p>
 * A <code>ResourceLoader</code> is responsible for retrieving
 * <code>ResourceGroup</code> objects from a specific source. There will be
 * different implementations for different resource sources like resource
 * bundles, database tables, etc.
 * </p>
 * <p>
 * This interface defines only a single method that must somehow retrieve a
 * resource group for a given <code>Locale</code> specified by a name. If this
 * fails, an exception will be thrown.
 * </p>
 *
 * @see ResourceGroup
 *
 * @author Oliver Heger
 * @version $Id: ResourceLoader.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface ResourceLoader
{
    /**
     * Performs all necessary steps to retrieve the specified resource group.
     * This method will be called by a <code>ResourceManager</code> whenever
     * resource entries or groups are accessed.
     *
     * @param locale the <code>Locale</code>
     * @param name the name of the searched resource group
     * @return the resource group
     * @throws MissingResourceException if this group cannot be retrieved
     */
    ResourceGroup loadGroup(Locale locale, Object name)
            throws MissingResourceException;
}
