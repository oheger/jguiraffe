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
package net.sf.jguiraffe.di;

import java.util.Set;

/**
 * <p>
 * Definition of an interface for objects that provide access to bean
 * definitions.
 * </p>
 * <p>
 * A <code>BeanStore</code> maintains an arbitrary number of bean definitions,
 * which are identified by unique names. It is possible to list the names of all
 * available bean definitions, and to access a specific bean definition by its
 * name.
 * </p>
 * <p>
 * <code>BeanStore</code>s are hierarchical structures: they can have a
 * parent. A typical search algorithm an a {@code BeanStore}
 * will first query the local store, and then - if the specified bean
 * definition could not be found - delegate to its parent store. This allows for
 * complex scenarios where global objects can be defined on the application
 * level, while certain sub contexts have the opportunity of overriding some
 * definitions and define their own local data.
 * </p>
 * <p>
 * This interface is kept quite simple. It should be easy to implement it, e.g.
 * using a map or another context-like object.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: BeanStore.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface BeanStore
{
    /**
     * Returns the <code>BeanProvider</code> that is registered under the
     * given name or <b>null</b> if cannot be found.
     *
     * @param name the name of the <code>BeanProvider</code>
     * @return the provider registered under this name or <b>null</b>
     */
    BeanProvider getBeanProvider(String name);

    /**
     * Returns a set with the names of all <code>BeanProvider</code> objects
     * that are registered in this bean store.
     *
     * @return a set with the names of the contained <code>BeanProvider</code>s
     */
    Set<String> providerNames();

    /**
     * Returns a name for this bean store. The name is mainly used for grouping
     * and accessing bean stores. It does not have a direct impact on bean
     * creation or dependency injection.
     *
     * @return a name for this bean store
     */
    String getName();

    /**
     * Returns a reference to the parent <code>BeanStore</code>. Bean stores
     * can be organized in a hierarchical manner: if a bean provider cannot be
     * found in a specific bean store, the framework will also search its parent
     * bean store (and recursively the parent's parent, and so on). This makes
     * it possible to create different scopes of beans. The root store should
     * return <b>null</b>.
     *
     * @return the parent bean store or <b>null</b> if this is the root store
     */
    BeanStore getParent();

    /**
     * Returns a {@code ConversionHelper} object for performing type conversions
     * on the beans contained in this {@code BeanStore}. To the hierarchy of
     * {@code BeanStore} objects also {@code ConversionHelper} objects can be
     * added. This is useful when dealing with specialized beans for which
     * custom converters have to be provided. A {@code ConversionHelper} is
     * optional, so an implementation can return <b>null</b>. It is then up to
     * the caller whether it uses a default {@code ConversionHelper} instance or
     * tries to query the parent bean store.
     *
     * @return a {@code ConversionHelper} instance for performing type
     *         conversions related to the beans contained in this store
     */
    ConversionHelper getConversionHelper();
}
