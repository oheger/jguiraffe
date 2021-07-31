/*
 * Copyright 2006-2021 The JGUIraffe Team.
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

import java.util.Set;

import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.impl.HelperInvocations;
import net.sf.jguiraffe.di.impl.Invokable;

/**
 * <p>
 * A specialized life-cycle supporting {@link BeanProvider} implementation for
 * creating <em>singleton</em> beans.
 * </p>
 * <p>
 * When the {@code getBean()} method of an instance of this class is called for
 * the first time, all dependencies are resolved, and a new bean instance is
 * created (using the specified creation bean provider). Following calls to
 * {@code getBean()} always return this same bean instance. In fact, this
 * provider then behaves like a constant bean provider: it does not declare any
 * dependencies any more and does not need any synchronization support (i.e. the
 * {@code getLockID()} method will always return <b>null</b>). This means that
 * providers of this type can be efficiently used in a multi-threaded
 * environment once they are initialized.
 * </p>
 * <p>
 * Instances of this class can also be initialized with an {@link Invokable} to
 * be called when the bean is no more needed. This {@link Invokable} is
 * triggered by the {@code shutdown()} method. This mechanism makes it possible
 * to perform cleanup when a bean store is closed.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SingletonBeanProvider.java 208 2012-02-11 20:57:33Z oheger $
 */
public class SingletonBeanProvider extends LifeCycleBeanProvider
{
    /** Stores the invokable to be called when the provider is shut down.*/
    private final Invokable shutdownHandler;

    /**
     * Creates a new instance of {@code SingletonBeanProvider} and initializes
     * it with the bean provider for creating a bean instance and {@code
     * Invokable} objects for initializing and releasing the bean managed by
     * this provider.
     *
     * @param createProvider the bean provider used for creating a new bean
     *        instance (must not be <b>null</b>)
     * @param initInv the (optional) invocation object for performing
     *        initialization
     * @param shutdownInv the (optional) {@code Invokable} object to be called
     *        on shutdown
     * @throws IllegalArgumentException if the bean provider is undefined
     */
    public SingletonBeanProvider(BeanProvider createProvider,
            Invokable initInv, Invokable shutdownInv)
    {
        super(createProvider, initInv);
        shutdownHandler = shutdownInv;
    }

    /**
     * Creates a new instance of {@code SingletonBeanProvider} and
     * initializes it with the bean provider for creating a bean instance and
     * the invocation object for performing initialization.
     *
     * @param createProvider the bean provider used for creating a new bean
     * instance (must not be <b>null</b>)
     * @param initinv the (optional) invocation object for performing
     * initialization
     * @throws IllegalArgumentException if the bean provider is undefined
     */
    public SingletonBeanProvider(BeanProvider createProvider, Invokable initinv)
    {
        super(createProvider, initinv);
        shutdownHandler = null;
    }

    /**
     * Creates a new instance of {@code SingletonBeanProvider} and
     * initializes it with the bean provider for creating a bean instance.
     *
     * @param createProvider the bean provider used for creating a new bean
     * instance (must not be <b>null</b>)
     * @throws IllegalArgumentException if the bean provider is undefined
     */
    public SingletonBeanProvider(BeanProvider createProvider)
    {
        super(createProvider);
        shutdownHandler = null;
    }

    /**
     * Returns the {@code Invokable} object that is called when this provider is
     * shut down. This can be <b>null</b> if no shutdown handler was set.
     *
     * @return the {@code Invokable} to be called on shutdown
     */
    public Invokable getShutdownHandler()
    {
        return shutdownHandler;
    }

    /**
     * Returns the bean managed by this bean provider. This implementation will
     * create a bean on first access and then always return this instance.
     *
     * @param dependencyProvider the dependency provider
     * @return the bean managed by this provider
     * @throws net.sf.jguiraffe.di.InjectionException if an error occurs
     */
    public Object getBean(DependencyProvider dependencyProvider)
    {
        return fetchBean(dependencyProvider);
    }

    /**
     * Returns the dependencies of this bean provider. If already a bean has
     * been created, this implementation returns <b>null</b> because there is
     * no need any more for resolving dependencies.
     *
     * @return the dependencies of this bean provider
     */
    @Override
    public Set<Dependency> getDependencies()
    {
        return hasBean() ? null : super.getDependencies();
    }

    /**
     * Returns the ID of the locking transaction. As long as no bean has been
     * created yet, this implementation behaves like the method of the base
     * class. After that it will always return <b>null</b>, because from now on
     * there is no need for synchronization any more.
     *
     * @return the ID of the locking transaction
     */
    @Override
    public Long getLockID()
    {
        return hasBean() ? null : super.getLockID();
    }

    /**
     * Tells this provider that it is no more needed. This implementation
     * invokes the shutdown handler if a bean was already created.
     *
     * @param depProvider the {@code DependencyProvider}
     */
    @Override
    public void shutdown(DependencyProvider depProvider)
    {
        if (hasBean())
        {
            fetchShutdownInvokable()
                    .invoke(depProvider, fetchBean(depProvider));
        }
    }

    /**
     * Fetches the {@code Invokable} for shutdown. This method never returns
     * <b>null</b>. If a shutdown invokable is set, it is returned. Otherwise a
     * dummy object is returned.
     *
     * @return the {@code Invokable} to be called on shutdown
     */
    Invokable fetchShutdownInvokable()
    {
        return (getShutdownHandler() != null) ? getShutdownHandler()
                : HelperInvocations.NULL_INVOCATION;
    }
}
