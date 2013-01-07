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
 * Definition of an interface that allows access to the beans managed by the
 * dependency injection framework.
 * </p>
 * <p>
 * This interface serves as the top-level entry point into the dependency
 * injection framework. It allows querying beans from a specified
 * <code>{@link BeanStore}</code> or a default <code>BeanStore</code> by
 * name or by type. Listing the available beans is also possible.
 * </p>
 * <p>
 * When objects are dynamically created, class loader issues have to be taken
 * into account. A <code>BeanContext</code> allows the registration of class
 * loaders by name. In the configuration, it can then be specified, which bean
 * is to be created using which class loader. For the context class loader there
 * is a special constant.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: BeanContext.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface BeanContext
{
    /**
     * Returns the bean with the specified name from the default
     * <code>BeanStore</code>.
     *
     * @param name the name of the bean to be retrieved
     * @return the bean with this name
     * @throws InjectionException if the bean cannot be resolved or its creation
     * or initialization causes an error
     */
    Object getBean(String name);

    /**
     * Returns the bean with the specified name from the given
     * <code>BeanStore</code>. This method can be used if a child bean store
     * of the default bean store is to be accessed.
     *
     * @param name the name of the bean to be retrieved
     * @param store the <code>BeanStore</code> to query
     * @return the bean with this name
     * @throws InjectionException if the bean cannot be resolved or its creation
     * or initialization causes an error
     * @throws IllegalArgumentException if the passed in bean store is <b>null</b>
     */
    Object getBean(String name, BeanStore store);

    /**
     * Returns the bean with the specified class from the default
     * <code>BeanStore</code>. An implementation of this method has to
     * iterate over the beans defined in the bean store to find out, whether the
     * specified class is assignable from their class. Then the first fitting
     * bean will be returned. So it is for instance possible to query for an
     * interface and find a bean that is a concrete implementation of this
     * interface. If there are multiple bean definitions that are compatible
     * with this class, it is up to a concrete implementation of this interface
     * to choose one. Defining beans based on a class (or interface) allows to
     * abstract from the concrete naming of beans and focuses more on the beans'
     * semantics.
     *
     * @param <T> the type of the bean to be fetched
     * @param beanCls the class of the bean to be fetched
     * @return the bean with this class
     * @throws InjectionException if the bean cannot be resolved or its creation
     * or initialization causes an error
     */
    <T> T getBean(Class<T> beanCls);

    /**
     * Returns the bean with the specified class from the given
     * <code>BeanStore</code>. Works like the overloaded method, but operates
     * on the given bean store.
     *
     * @param <T> the type of the bean to be fetched
     * @param beanCls the class of the bean to be fetched
     * @param store the <code>BeanStore</code> to use
     * @return the bean with this class
     * @throws InjectionException if the bean cannot be resolved or its creation
     * or initialization causes an error
     * @throws IllegalArgumentException if the passed in bean store is <b>null</b>
     */
    <T> T getBean(Class<T> beanCls, BeanStore store);

    /**
     * Returns a set with the names of all beans defined in the default
     * <code>BeanStore</code>.
     *
     * @return a set with the names of the defined beans
     */
    Set<String> beanNames();

    /**
     * Returns a set with the names of all beans defined in the given
     * <code>BeanStore</code>. This list will also contain beans that are
     * defined in one of the bean store's parents.
     *
     * @param store the bean store (must not be <b>null</b>)
     * @return a set with the names of the defined beans
     * @throws IllegalArgumentException if the passed in bean store is <b>null</b>
     */
    Set<String> beanNames(BeanStore store);

    /**
     * Returns a set with the classes of all beans defined in the default
     * <code>BeanStore</code> (or one of its parents).
     *
     * @return a set with the classes of the defined beans
     */
    Set<Class<?>> beanClasses();

    /**
     * Returns a set with the classes of all beans defined in the given
     * <code>BeanStore</code>. This list will also contain beans that are
     * defined in one of the bean store's parents.
     *
     * @param store the bean store (must not be <b>null</b>)
     * @return a set with the classes of the defined beans
     * @throws IllegalArgumentException if the passed in bean store is <b>null</b>
     */
    Set<Class<?>> beanClasses(BeanStore store);

    /**
     * Checks whether the default <code>BeanStore</code> contains a bean with
     * the given name. If necessary the bean store's parents will also be
     * searched.
     *
     * @param name the name of the searched bean
     * @return a flag whether this bean can be found
     */
    boolean containsBean(String name);

    /**
     * Checks whether the specified <code>BeanStore</code> contains a bean
     * with the given name. If necessary the bean store's parents will also be
     * searched.
     *
     * @param name the name of the searched bean
     * @param store the bean store
     * @return a flag whether this bean can be found
     */
    boolean containsBean(String name, BeanStore store);

    /**
     * Checks whether the default <code>BeanStore</code> contains a bean with
     * the given class. If necessary the bean store's parents will also be
     * searched.
     *
     * @param beanClass the class of the searched bean
     * @return a flag whether this bean can be found
     */
    boolean containsBean(Class<?> beanClass);

    /**
     * Checks whether the specified <code>BeanStore</code> contains a bean
     * with the given class. If necessary the bean store's parents will also be
     * searched.
     *
     * @param beanClass the class of the searched bean
     * @param store the bean store
     * @return a flag whether this bean can be found
     */
    boolean containsBean(Class<?> beanClass, BeanStore store);

    /**
     * Returns the default bean store.
     *
     * @return the default bean store
     */
    BeanStore getDefaultBeanStore();

    /**
     * Sets the default bean store. This bean store is used as starting point
     * for all lookup-operations if no specific bean store is specified.
     *
     * @param store the new default bean store
     */
    void setDefaultBeanStore(BeanStore store);

    /**
     * Returns the name of the bean that is managed by the specified {@code
     * BeanProvider}, starting search with the default {@code BeanStore}.
     *
     * @param beanProvider the {@code BeanProvider}
     * @return the name, under which this {@code BeanProvider} is registered in
     *         one of the accessible bean stores or <b>null</b> if it cannot be
     *         resolved
     * @see #beanNameFor(BeanProvider, BeanStore)
     */
    String beanNameFor(BeanProvider beanProvider);

    /**
     * Returns the name of the bean that is managed by the specified {@code
     * BeanProvider}, starting search in the specified {@code BeanStore}. This
     * method can be used for performing a "reverse lookup": when a bean
     * provider is known, but the corresponding bean name is searched. A use
     * case would be the processing of a {@link BeanCreationEvent}. The event
     * object contains the {@code BeanProvider} that created the bean, but the
     * name of this bean is not part of the event.
     *
     * @param beanProvider the {@code BeanProvider}
     * @param store the {@code BeanStore}
     * @return the name, under which this {@code BeanProvider} is registered in
     *         one of the accessible bean stores or <b>null</b> if it cannot be
     *         resolved
     */
    String beanNameFor(BeanProvider beanProvider, BeanStore store);

    /**
     * Adds a new {@code BeanCreationListener} to this context. This listener
     * will receive notifications about newly created beans.
     *
     * @param l the listener to be added (must not be <b>null</b>)
     * @throws IllegalArgumentException if the listener is <b>null</b>
     */
    void addBeanCreationListener(BeanCreationListener l);

    /**
     * Removes the specified {@code BeanCreationListener} from this context. If
     * the listener is not registered, this method has no effect.
     *
     * @param l the listener to remove
     */
    void removeBeanCreationListener(BeanCreationListener l);

    /**
     * Closes this bean context. This method should be called when the context
     * is no more needed. An implementation can free resources or perform other
     * clean up. Note that the {@link BeanStore} associated with this context is
     * not closed by this method (this is because a {@code BeanStore} could be
     * shared by multiple contexts, and there is not necessary a 1:1
     * relationship between a {@code BeanStore} and a {@code BeanContext}).
     */
    void close();

    /**
     * Returns the {@code ClassLoaderProvider} used by this context.
     *
     * @return the {@code ClassLoaderProvider}
     */
    ClassLoaderProvider getClassLoaderProvider();

    /**
     * Sets the {@code ClassLoaderProvider} to be used by this context. This
     * object is needed when classes have to be loaded through reflection.
     *
     * @param clp the {@code ClassLoaderProvider}
     */
    void setClassLoaderProvider(ClassLoaderProvider clp);
}
