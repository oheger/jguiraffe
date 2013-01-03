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
package net.sf.jguiraffe.gui.builder.components.tags;

/**
 * <p>
 * Definition of an interface to be implemented by tags that support setting of
 * properties.
 * </p>
 * <p>
 * This interface is evaluated by tags for setting properties. Such tags can be
 * placed inside the body of tags implementing the <code>PropertySupport</code>
 * interface. Through the method defined here the value of a property can be
 * set. It is up to a concrete implementation how the property is further
 * processed.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: PropertySupport.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface PropertySupport
{
    /**
     * Sets the specified property. This method will be called by one of the
     * tags that allow the definition of properties. An implementation can
     * choose how this property will be handled.
     *
     * @param name the name of the property
     * @param value the value of the property
     */
    void setProperty(String name, Object value);
}
