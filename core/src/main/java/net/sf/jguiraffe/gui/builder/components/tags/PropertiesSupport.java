/*
 * Copyright 2006-2013 The JGUIraffe Team.
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

import java.util.Map;

/**
 * <p>
 * Definition of an interface to be implemented by tags that support a
 * properties object.
 * </p>
 * <p>
 * This interface is evaluated by the <code>{@link PropertiesTag}</code> tag
 * handler class. Through the single method defined here a map with properties
 * can be set. The idea is that the <code>PropertiesTag</code> creates such a
 * map. By tags in the body of the <code>PropertiesTag</code> the map is
 * populated. Finally the resulting map is passed to a target implementing this
 * interface.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: PropertiesSupport.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface PropertiesSupport
{
    /**
     * Sets a map with properties. It is up to a concrete implementation what it
     * does with this map.
     *
     * @param props the maps with the properties
     */
    void setProperties(Map<String, Object> props);
}
