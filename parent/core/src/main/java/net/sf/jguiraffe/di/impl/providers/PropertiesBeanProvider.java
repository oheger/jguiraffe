/*
 * Copyright 2006-2015 The JGUIraffe Team.
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
package net.sf.jguiraffe.di.impl.providers;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import net.sf.jguiraffe.di.Dependency;

/**
 * <p>
 * A specialized <code>BeanProvider</code> implementation for creating a
 * <code>java.util.Properties</code> object.
 * </p>
 * <p>
 * This class works analogously to <code>{@link MapBeanProvider}</code>, but it
 * creates a specialized map: a <code>Properties</code> object. Because the
 * major part of the functionality required is already implemented by the super
 * class this implementation can be very simple. It merely has to override the
 * <code>createMap()</code> method to return a <code>Properties</code> instance.
 * </p>
 * <p>
 * This class stands in a similar relation to <code>MapBeanProvider</code> as
 * <code>java.util.Properties</code> stands to its ancestor
 * <code>java.util.HashMap</code>: <code>Properties</code> operates on string
 * keys and values, but through the methods inherited from its base class it is
 * possible to store data of other types as well. The same is true for this
 * <code>BeanProvider</code> implementation. It accepts any kind of dependencies
 * for keys and values and does not perform a type check. So it lies in the
 * responsibility of the user to populate the <code>Properties</code> object
 * only with valid keys and values.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: PropertiesBeanProvider.java 205 2012-01-29 18:29:57Z oheger $
 */
public class PropertiesBeanProvider extends MapBeanProvider
{
    /**
     * Creates a new instance of <code>PropertiesBeanProvider</code> and
     * initializes it with the dependencies for keys and values. Note that the
     * <code>ordered</code> flag supported by the super class does not make
     * sense in this context.
     *
     * @param keyDeps the dependencies for the property keys (must not be
     *        <b>null</b>)
     * @param valDeps the dependencies for the property values (must not be
     *        <b>null</b>)
     * @throws IllegalArgumentException if either <code>keyDeps</code> or
     *         <code>valDeps</code> is <b>null</b> or the sizes of the
     *         collections are different
     */
    public PropertiesBeanProvider(Collection<Dependency> keyDeps,
            Collection<Dependency> valDeps)
    {
        super(keyDeps, valDeps, false);
    }

    /**
     * Creates the map managed by this bean provider. This implementation
     * constructs a new <code>Properties</code> object.
     *
     * @return the map managed by this bean provider
     */
    @Override
    protected Map<Object, Object> createMap()
    {
        return new Properties();
    }
}
