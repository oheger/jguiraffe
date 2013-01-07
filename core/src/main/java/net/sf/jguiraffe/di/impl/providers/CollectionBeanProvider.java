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
package net.sf.jguiraffe.di.impl.providers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;

/**
 * <p>
 * An abstract base class for <code>BeanProvider</code> implementations that
 * create beans derived from collections.
 * </p>
 * <p>
 * This bean provider base implementation can be used when collection beans are
 * to be created. What makes collections a bit special is the fact that their
 * elements are also dependencies, which can reference other beans. So this bean
 * provider has to ensure that all dependencies are registered correctly, so
 * they can be resolved when the collection is created.
 * </p>
 * <p>
 * This class implements the major part of the functionality required for
 * creating collection beans. It is initialized with a collection of
 * dependencies representing the elements of the final collection. These can be
 * either constant dependencies or references to other beans. In the
 * <code>getBean()</code> implementation a collection of the correct type is
 * created, the element dependencies are resolved, and the resulting objects are
 * added to the collection.
 * </p>
 * <p>
 * A concrete subclass is responsible for creating a collection of a specific
 * type. For this purpose the <code>createCollection()</code> method has to be
 * defined. It is invoked by <code>getBean()</code> for obtaining a new
 * instance of the collection class supported.
 * </p>
 * <p>
 * Bean providers of this type are intended to be used together with a
 * <code>{@link LifeCycleBeanProvider}</code> that controls the creation of
 * new instances.
 * </p>
 * <p>
 * Implementation note: Objects of this class are immutable and thus can be
 * shared between multiple threads.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: CollectionBeanProvider.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class CollectionBeanProvider extends SimpleBeanProvider
{
    /** Stores the dependencies of this bean provider. */
    private final Collection<Dependency> dependencies;

    /**
     * Creates a new instance of <code>CollectionBeanProvider</code> and
     * initializes it with a collection with the dependencies of the elements.
     *
     * @param deps a collection with the element dependencies (must not be
     *        <b>null</b>)
     * @throws IllegalArgumentException if the collection is <b>null</b>
     */
    protected CollectionBeanProvider(Collection<Dependency> deps)
    {
        if (deps == null)
        {
            throw new IllegalArgumentException(
                    "Dependency collection must not be null!");
        }

        dependencies = new ArrayList<Dependency>(deps);
    }

    /**
     * Returns a collection with the dependencies of the collection elements.
     * From these dependencies the collection will be populated. Note that the
     * collection returned by this method is immutable.
     *
     * @return a collection with the dependencies of the elements
     */
    public Collection<Dependency> getElementDependencies()
    {
        return Collections.unmodifiableCollection(dependencies);
    }

    /**
     * Returns the bean managed by this provider. This implementation creates a
     * new collection by calling <code>createCollection()</code>. Then the
     * dependencies for the elements are resolved and added to the collection.
     *
     * @param dependencyProvider the dependency provider
     * @return the bean managed by this provider
     */
    public Object getBean(DependencyProvider dependencyProvider)
    {
        Collection<Object> col = createCollection(dependencies.size());
        for (Dependency dep : dependencies)
        {
            col.add(dependencyProvider.getDependentBean(dep));
        }

        return col;
    }

    /**
     * Returns the class of the bean created by this provider. This
     * implementation simply returns <code>Collection.class</code>. We expect
     * that collection beans are not so specific that they are queried by class.
     *
     * @param dependencyProvider the dependency provider
     * @return the class of the bean created by this provider
     */
    public Class<?> getBeanClass(DependencyProvider dependencyProvider)
    {
        return Collection.class;
    }

    /**
     * Returns a set with the dependencies of this bean provider. This
     * implementation creates a new set from the collection with the
     * dependencies for the elements.
     *
     * @return the dependencies of this bean provider
     */
    @Override
    public Set<Dependency> getDependencies()
    {
        return new HashSet<Dependency>(dependencies);
    }

    /**
     * Creates the collection. This method is invoked whenever a new bean
     * instance is to be created. Derived classes must here return a new
     * instance of the collection supported.
     *
     * @param size the initial size of the new collection; here the number of
     *        elements is passed in
     * @return the collection
     */
    protected abstract Collection<Object> createCollection(int size);
}
