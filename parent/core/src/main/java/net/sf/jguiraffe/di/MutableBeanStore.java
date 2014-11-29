/*
 * Copyright 2006-2014 The JGUIraffe Team.
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
 * Definition of an interface for a <code>BeanStore</code> that can be
 * manipulated.
 * </p>
 * <p>
 * This interface inherits from the basic <code>BeanStore</code> interface and
 * adds a set of methods to it that allow for changes of the store, i.e. adding/
 * removing bean providers or cleaning the whole store.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: MutableBeanStore.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface MutableBeanStore extends BeanStore
{
    /**
     * Adds the specified <code>BeanProvider</code> to this bean store under
     * the given name.
     *
     * @param name the name of the bean provider (must not be <b>null</b>)
     * @param provider the <code>BeanProvider</code> to be registered
     * @throws IllegalArgumentException if the name or the provider is <b>null</b>
     */
    void addBeanProvider(String name, BeanProvider provider);

    /**
     * Adds an <em>anonymous</em> <code>BeanProvider</code> to this
     * <code>BeanStore</code>. It is possible to define beans in the context
     * of a dependency tag (e.g. a <code>&lt;param&gt;</code> tag); then this
     * bean is only visible in this narrow context and cannot be accessed from
     * elsewhere. This makes sense for beans that are used only once and are not
     * shared between different components; then defining the bean directly at
     * the point where it is used is more readable. Such beans do not have a
     * real name, but an internal name is generated by this method, which is
     * treated specially. For instance, internal names won't be returned by the
     * <code>providerNames()</code> methods. The passed in index parameter is
     * used for generating a unique name for the provider. It is in the
     * responsibility of the caller to pass in unique numbers.
     *
     * @param index the index of the <code>BeanProvider</code>
     * @param provider the <code>BeanProvider</code> to be registered (must
     *        not be <b>null</b>)
     * @return the generated name for this <code>BeanProvider</code>
     * @throws IllegalArgumentException if the <code>BeanProvider</code> is
     *         <b>null</b>
     */
    String addAnonymousBeanProvider(int index, BeanProvider provider);

    /**
     * Removes the <code>BeanProvider</code> with the specified name from this
     * bean store. If this provider cannot be found, this operation has no
     * effect.
     *
     * @param name the name of the provider to remove
     * @return a reference to the removed provider or <b>null</b> if it could
     * not be found
     */
    BeanProvider removeBeanProvider(String name);

    /**
     * Removes all <code>BeanProvider</code>s from this bean store.
     */
    void clear();

    /**
     * Sets the name of this bean store.
     *
     * @param n the new name
     */
    void setName(String n);

    /**
     * Sets the parent for this bean store.
     *
     * @param p the parent
     */
    void setParent(BeanStore p);
}