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
package net.sf.jguiraffe.di.impl.providers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;

/**
 * <p>
 * A specialized <code>BeanProvider</code> implementation for creating beans of
 * type <code>java.util.Map</code>.
 * </p>
 * <p>
 * With this bean provider implementation either a
 * <code>java.util.HashMap</code> or a <code>java.util.LinkedHashMap</code> can
 * be created and populated. The mechanisms used here are very similar to the
 * ones used by {@link CollectionBeanProvider}: an instance is
 * initialized with two lists of {@link Dependency} objects - one
 * list represents the key, the other list represents the values of the map. The
 * <code>getDependencies()</code> method returns a union of all these
 * dependencies. When the <code>Map</code> bean is to be created, a new instance
 * of the correct <code>Map</code> class is created. Then the dependencies for
 * the keys and values are resolved, and the corresponding objects are added to
 * the map.
 * </p>
 * <p>
 * Bean providers of this type are intended to be used together with a
 * {@link LifeCycleBeanProvider} that controls the creation of new
 * instances.
 * </p>
 * <p>
 * Implementation note: Objects of this class are immutable and thus can be
 * shared between multiple threads.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: MapBeanProvider.java 205 2012-01-29 18:29:57Z oheger $
 */
public class MapBeanProvider extends SimpleBeanProvider
{
    /** A collection with the dependencies representing the keys of the map. */
    private final Collection<Dependency> keyDependencies;

    /** A collection with the dependencies representing the values of the map. */
    private final Collection<Dependency> valueDependencies;

    /** Stores the ordered flag. */
    private final boolean ordered;

    /**
     * Creates a new instance of <code>MapBeanProvider</code> and initializes
     * it.
     *
     * @param keyDeps the dependencies for the keys of the map (must not be
     *        <b>null</b>)
     * @param valDeps the dependencies for the values of the map (must not be
     *        <b>null</b>)
     * @param ordered a flag whether the order of the dependencies should be
     *        maintained
     * @throws IllegalArgumentException if either <code>keyDeps</code> or
     *         <code>valDeps</code> is <b>null</b> or the sizes of the
     *         collections are different
     */
    public MapBeanProvider(Collection<Dependency> keyDeps,
            Collection<Dependency> valDeps, boolean ordered)
    {
        if (keyDeps == null)
        {
            throw new IllegalArgumentException(
                    "Key dependencies must not be null!");
        }
        if (valDeps == null)
        {
            throw new IllegalArgumentException(
                    "Value dependencies must not be null!");
        }
        if (keyDeps.size() != valDeps.size())
        {
            throw new IllegalArgumentException(
                    "Different number of key and value dependencies!");
        }

        keyDependencies = new ArrayList<Dependency>(keyDeps);
        valueDependencies = new ArrayList<Dependency>(valDeps);
        this.ordered = ordered;
    }

    /**
     * Returns the dependencies for the keys of the map.
     *
     * @return the key dependencies
     */
    public Collection<Dependency> getKeyDependencies()
    {
        return Collections.unmodifiableCollection(keyDependencies);
    }

    /**
     * Returns the dependencies for the values of the map.
     *
     * @return the value dependencies
     */
    public Collection<Dependency> getValueDependencies()
    {
        return Collections.unmodifiableCollection(valueDependencies);
    }

    /**
     * Returns the ordered flag. This property determines the type of the map
     * created by this provider.
     *
     * @return the ordered flag
     */
    public boolean isOrdered()
    {
        return ordered;
    }

    /**
     * Returns the bean managed by this provider. This implementation calls
     * <code>createMap()</code> to create the map. Then the dependencies for
     * keys and values are resolved and added to the map.
     *
     * @param dependencyProvider the dependency provider
     * @return the managed bean
     */
    public Object getBean(DependencyProvider dependencyProvider)
    {
        Map<Object, Object> map = createMap();
        Iterator<Dependency> itVal = valueDependencies.iterator();
        for (Dependency keyDep : keyDependencies)
        {
            Object key = dependencyProvider.getDependentBean(keyDep);
            Object val = dependencyProvider.getDependentBean(itVal.next());
            map.put(key, val);
        }

        return map;
    }

    /**
     * Returns the class of the managed bean. This implementation simply returns
     * the unspecific <code>java.util.Map</code> class.
     *
     * @param dependencyProvider the dependency provider
     * @return the class of the bean managed by this provider
     */
    public Class<?> getBeanClass(DependencyProvider dependencyProvider)
    {
        return Map.class;
    }

    /**
     * Returns the dependencies of this bean provider. This implementation
     * returns a union of all key and value dependencies.
     *
     * @return a set with all dependencies of this bean provider
     */
    @Override
    public Set<Dependency> getDependencies()
    {
        Set<Dependency> deps = new HashSet<Dependency>();
        deps.addAll(keyDependencies);
        deps.addAll(valueDependencies);
        return deps;
    }

    /**
     * Creates the map managed by this provider. This implementation checks the
     * <code>ordered</code> property and either creates a hash map or a linked
     * hash map.
     *
     * @return the map
     */
    protected Map<Object, Object> createMap()
    {
        return isOrdered() ? new LinkedHashMap<Object, Object>()
                : new HashMap<Object, Object>();
    }
}
