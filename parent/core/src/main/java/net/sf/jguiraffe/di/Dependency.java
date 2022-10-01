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
package net.sf.jguiraffe.di;

/**
 * <p>
 * Definition of an interface for describing a dependency to another
 * <code>{@link BeanProvider}</code> in an abstract way.
 * </p>
 * <p>
 * <code>BeanProvider</code>s often depend on other providers, e.g. some
 * properties of the bean to be created need to be initialized with other beans.
 * There are different ways of defining such dependencies: by specifying the
 * name of the dependent bean, by specifying its class, etc. This interface
 * provides a generic way of defining dependencies. It has a
 * <code>resolve()</code> method that can be implemented in a suitable way.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: Dependency.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface Dependency
{
    /**
     * Resolves this dependency starting from the specified
     * <code>BeanStore</code>. A concrete implementation has to search for the
     * <code>BeanProvider</code> it depends on. If necessary, the given bean
     * store's parent has to be searched recursively. The <code>DependencyProvider</code>
     * can be used for querying further information about the current context,
     * e.g. for resolving classes using predefined class loaders.
     *
     * @param store the current bean store
     * @param depProvider the dependency provider
     * @return the resolved bean
     * @throws InjectionException if resolving of the dependency fails
     */
    BeanProvider resolve(BeanStore store, DependencyProvider depProvider);
}
