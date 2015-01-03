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
package net.sf.jguiraffe.di;

/**
 * <p>
 * Definition of an interface that provides access to dependencies defined by a
 * {@link BeanProvider}.
 * </p>
 * <p>
 * This interface is used by a {@link BeanProvider}
 * implementation for resolving its dependencies to other beans. An
 * implementation of this interface is passed to the bean provider's
 * <code>getBean()</code> method, allowing access to the required
 * dependencies. By extending the <code>ClassLoaderProvider</code> interface
 * functionality for dealing with dynamic class loading is also available through
 * this interface.
 * </p>
 * <p>
 * Note that this interface is used internally by the framework. Clients
 * probably won't have to deal with it directly. It is only of importance for
 * custom implementations of the {@link BeanProvider} interface.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: DependencyProvider.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface DependencyProvider extends ClassLoaderProvider
{
    /**
     * Returns the dependency bean for the specified dependency. This dependency
     * must have been part of the collection of dependencies returned by the
     * bean provider's <code>getDependencies()</code> method.
     *
     * @param dependency the dependency pointing to the bean in question
     * @return the bean for this dependency
     * @throws InjectionException if the bean cannot be resolved
     */
    Object getDependentBean(Dependency dependency);

    /**
     * Checks whether the bean specified by the given <code>Dependency</code>
     * is currently available. This method can be used by complex bean providers
     * to find out whether an initialization of their managed bean is now
     * possible. Because of cyclic dependencies it may be the case that a
     * required dependency cannot be resolved now. The affected bean provider
     * can then register itself as an <em>initializer</em> and try again after
     * the creation phase has completed.
     *
     * @param dependency the dependency pointing to the bean in question
     * @return a flag whether this bean is currently available
     * @throws InjectionException if the dependency cannot be resolved
     * @see #addInitializer(BeanInitializer)
     */
    boolean isBeanAvailable(Dependency dependency);

    /**
     * Registers a <code>BeanInitializer</code>. This object will be called
     * at the end of the transaction when all beans have been created. This
     * gives complex bean providers an opportunity of trying some
     * initializations again that were not possible before because of cyclic
     * dependencies. A concrete implementation has to ensure that the
     * initializers registered here are always called before the current
     * transaction ends - no matter whether it succeeds or fails.
     *
     * @param initializer the initializer to register
     */
    void addInitializer(BeanInitializer initializer);

    /**
     * Notifies this {@code DependencyProvider} about the creation of a bean.
     * This method has to be called by {@code BeanProvider} implementations when
     * a new bean has been completely created. It allows the framework to
     * perform some post processing, e.g. enhanced initialization of the new
     * bean or notification of context listeners.
     *
     * @param bean the newly created bean
     * @param provider the {@code BeanProvider} responsible for this bean
     */
    void beanCreated(Object bean, BeanProvider provider);

    /**
     * Sets the {@link BeanContext} that is responsible for a
     * {@link BeanCreationEvent} notification. This method can be used by
     * implementations of the {@code BeanContext} interface that wrap other
     * contexts, e.g. a combined bean context. Such wrapping contexts usually
     * delegate to other contexts when a bean is requested. If such a request
     * causes a new bean to be created, the corresponding {@code
     * BeanCreationEvent} per default has the wrapped context as its source, and
     * also a {@link BeanContextClient} object will be initialized with this
     * context. However, it may be appropriate to set the wrapping context as
     * source. This can be achieved by registering a
     * {@link BeanCreationListener} at the wrapped contexts. In the event
     * handling method the {@code setCreationBeanContext()} can be called with
     * the wrapping context as parameter. Then correct creation context is known
     * and can also be passed to a {@link BeanContextClient}.
     *
     * @param context the {@code BeanContext} responsible for a bean creation
     */
    void setCreationBeanContext(BeanContext context);

    /**
     * Returns a reference to the current {@link InvocationHelper} object. This
     * helper object can be used by bean providers for more advanced operations
     * related to reflection, e.g. method invocations or data type conversions.
     * Especially the data type conversion facilities provided by
     * {@link InvocationHelper} may be of interest. The object returned by an
     * implementation should be a central instance, i.e. the same instance that
     * has been used for registering custom converter implementations.
     *
     * @return the current {@code InvocationHelper} object
     */
    InvocationHelper getInvocationHelper();
}
