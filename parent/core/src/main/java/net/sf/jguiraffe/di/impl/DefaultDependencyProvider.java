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
package net.sf.jguiraffe.di.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.di.BeanInitializer;
import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.InjectionException;
import net.sf.jguiraffe.di.InvocationHelper;

/**
 * <p>
 * An internally used implementation of the <code>DependencyProvider</code>
 * interface.
 * </p>
 * <p>
 * An instance of this class is created by the default {@link BeanContext}
 * implementation at the beginning of a transaction. Its task is to obtain and
 * cache all dependencies of the currently requested bean. This can fail if a
 * dependency cannot be resolved or one of the dependent bean providers is
 * already locked by another transaction. In this case the transaction has to be
 * suspended.
 * </p>
 * <p>
 * If an instance could be successfully initialized, it allows access to all
 * dependent bean providers - and no more. So the
 * <code>getDependentBean()</code> method can be implemented in a meaningful
 * way.
 * </p>
 * <p>
 * This class works closely together with the default bean context
 * implementation. From there it also obtains a map with the registered class
 * loaders. Instances are confined to a single thread, so there is no need of
 * being thread-safe.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: DefaultDependencyProvider.java 205 2012-01-29 18:29:57Z oheger $
 */
class DefaultDependencyProvider implements DependencyProvider
{
    /**
     * Holds a reference to the associated bean context implementation.
     */
    private final DefaultBeanContext beanContext;

    /** Stores the resolved dependencies. */
    private Map<Dependency, BeanProvider> dependencyMap;

    /** A list with the registered bean initializers. */
    private List<BeanInitializer> initializers;

    /** The context that is responsible for a bean creation event. */
    private BeanContext creationBeanContext;

    /** The invocation helper used by this instance. */
    private InvocationHelper invocationHelper;

    /**
     * Creates a new instance of {@code DefaultDependencyProvider} and
     * initializes it with the bean context implementation it assists.
     *
     * @param context the associated bean context
     */
    public DefaultDependencyProvider(DefaultBeanContext context)
    {
        beanContext = context;
    }

    /**
     * Returns the {@code BeanContext} that is responsible for a bean creation.
     *
     * @return the {@code BeanContext} responsible for a bean creation
     */
    public BeanContext getCreationBeanContext()
    {
        return (creationBeanContext != null) ? creationBeanContext
                : getBeanContext();
    }

    /**
     * Sets the {@code BeanContext} that is responsible for a bean creation.
     * This method is intended to be called by a bean creation listener to set
     * the correct context when there is a complex structure of combined and
     * wrapped bean contexts.
     *
     * @param creationBeanContext the creation bean context
     */
    public void setCreationBeanContext(BeanContext creationBeanContext)
    {
        this.creationBeanContext = creationBeanContext;
    }

    /**
     * Initializes this object. This method resolves the specified dependency
     * and, recursively, all dependencies it depends on. The found bean
     * providers are stored in an internal data structure. If a dependency
     * cannot be resolved, a <code>InjectionException</code> exception is
     * thrown. The return value indicates whether all found bean providers are
     * unlocked. If a locked provider is found, the method is aborted, and
     * <b>false</b> is returned.
     *
     * @param dependency the initial dependency to resolve
     * @param store the starting bean store
     * @return a flag whether all dependencies can be locked
     * @throws InjectionException if a dependency cannot be resolved
     */
    public boolean initialize(Dependency dependency, BeanStore store)
    {
        Map<Dependency, BeanProvider> depMap = new HashMap<Dependency, BeanProvider>();
        Queue<Dependency> q = new LinkedList<Dependency>();
        q.add(dependency);

        while (!q.isEmpty())
        {
            Dependency d = q.remove();
            if (!depMap.containsKey(d))
            {
                // not yet processed => resolve this dependency
                BeanProvider provider = d.resolve(store, this);
                if (provider.getLockID() != null)
                {
                    // already locked, initialization fails
                    return false;
                }
                depMap.put(d, provider);

                // Process the dependencies of this provider
                Set<Dependency> dependencies = provider.getDependencies();
                if (dependencies != null)
                {
                    q.addAll(dependencies);
                }
            }
        }

        dependencyMap = depMap;
        initInvocationHelper(store);
        return true;
    }

    /**
     * Returns the associated bean context implementation.
     *
     * @return the bean context
     */
    public DefaultBeanContext getBeanContext()
    {
        return beanContext;
    }

    /**
     * Marks all resolved dependencies as locked. This method can be called
     * after a successful invocation of <code>initialize()</code>. It sets the
     * lock IDs for all involved <code>BeanProvider</code>s, so that they cannot
     * take part in another concurrent transaction. A second call to this method
     * with the argument <b>null</b> releases all locks.
     *
     * @param lockID the lock ID
     */
    public void lock(Long lockID)
    {
        for (BeanProvider p : getDependencyMap().values())
        {
            p.setLockID(lockID);
        }
    }

    /**
     * Returns the bean provider specified by the given dependency. This
     * dependency must belong to the set of dependencies fetched during the
     * <code>initialize()</code> method.
     *
     * @param dependency the dependency
     * @return the bean provider specified by this dependency
     * @throws InjectionException if the dependency cannot be resolved
     */
    public BeanProvider getDependentProvider(Dependency dependency)
    {
        BeanProvider result = dependencyMap.get(dependency);
        if (result == null)
        {
            throw new InjectionException(
                    "Invalid dependency! This dependency does not belong to "
                            + "the current transaction: " + dependency);
        }
        return result;
    }

    /**
     * Returns the bean of the bean provider specified by the given dependency.
     * This dependency must belong to the set of dependencies fetched during the
     * <code>initialize()</code> method.
     *
     * @param dependency the dependency
     * @return the bean from the bean provider specified by this dependency
     * @throws InjectionException if the dependency cannot be resolved
     */
    public Object getDependentBean(Dependency dependency)
    {
        return getDependentProvider(dependency).getBean(this);
    }

    /**
     * Returns a set with the names of all registered class loaders. This
     * implementation delegates to the internal <code>ClassLoaderProvider</code>
     * .
     *
     * @return a set with the names of the registered class loaders
     */
    public Set<String> classLoaderNames()
    {
        return getCLP().classLoaderNames();
    }

    /**
     * Returns the class loader with the given symbolic name. This
     * implementation delegates to the internal <code>ClassLoaderProvider</code>
     * .
     *
     * @param name the name of the class loader
     * @return the class loader with this name
     * @throws InjectionException if the class loader cannot be resolved
     */
    public ClassLoader getClassLoader(String name)
    {
        return getCLP().getClassLoader(name);
    }

    /**
     * Returns the default class loader name. This implementation delegates to
     * the internal <code>ClassLoaderProvider</code>.
     *
     * @return the default class loader name
     */
    public String getDefaultClassLoaderName()
    {
        return getCLP().getDefaultClassLoaderName();
    }

    /**
     * Registers a class loader under a symbolic name. This implementation
     * delegates to the internal <code>ClassLoaderProvider</code>.
     *
     * @param name the symbolic name for the class loader
     * @param loader the class loader to register
     */
    public void registerClassLoader(String name, ClassLoader loader)
    {
        getCLP().registerClassLoader(name, loader);
    }

    /**
     * Sets the name of the default class loader. This implementation delegates
     * to the internal <code>ClassLoaderProvider</code>.
     *
     * @param loaderName the new default class loader name
     */
    public void setDefaultClassLoaderName(String loaderName)
    {
        getCLP().setDefaultClassLoaderName(loaderName);
    }

    /**
     * Loads the class with the specified name using the class loader identified
     * by the given symbolic reference. This implementation delegates to the
     * internal <code>ClassLoaderProvider</code>.
     *
     * @param name the class of the name to be loaded
     * @param loaderRef determines the class loader to be used
     * @return the loaded class
     * @throws InjectionException if the class cannot be loaded
     */
    public Class<?> loadClass(String name, String loaderRef)
    {
        return getCLP().loadClass(name, loaderRef);
    }

    /**
     * Adds a bean initializer. This initializer will be invoked by the
     * <code>invokeInitializers()</code> method.
     *
     * @param initializer the initializer to be added (may be <b>null</b>, then
     *        this operation has no effect)
     */
    public void addInitializer(BeanInitializer initializer)
    {
        if (initializer != null)
        {
            if (initializers == null)
            {
                initializers = new LinkedList<BeanInitializer>();
            }
            initializers.add(initializer);
        }
    }

    /**
     * Invokes all registered bean initializers. If an initializer throws an
     * exception, it is caught and saved. The remaining initializers will be
     * invoked. After that the exception is re-thrown.
     *
     * @throws InjectionException if an error occurs
     */
    public void invokeInitializers()
    {
        if (initializers != null)
        {
            InjectionException thrownEx = null;

            for (BeanInitializer init : initializers)
            {
                try
                {
                    init.initialize(this);
                }
                catch (InjectionException iex)
                {
                    if (thrownEx == null)
                    {
                        thrownEx = iex;
                    }
                }
                catch (Exception ex)
                {
                    if (thrownEx == null)
                    {
                        thrownEx = new InjectionException(ex);
                    }
                }
            }

            if (thrownEx != null)
            {
                throw thrownEx;
            }
        }
    }

    /**
     * Tests whether a bean is available.
     *
     * @param dependency the dependency
     * @return a flag whether this dependency can be currently resolved
     */
    public boolean isBeanAvailable(Dependency dependency)
    {
        return getDependentProvider(dependency).isBeanAvailable();
    }

    /**
     * Returns the {@code InvocationHelper} object. This implementation obtains
     * the helper object from the associated bean context.
     *
     * @return the {@code InvocationHelper} object
     */
    public InvocationHelper getInvocationHelper()
    {
        return invocationHelper;
    }

    /**
     * Notifies this dependency provider about the creation of a new bean. This
     * method is called by a {@code BeanProvider} when it has to create a bean
     * to satisfy the current request.
     *
     * @param bean the new bean
     * @param provider the {@code BeanProvider} responsible for the creation
     */
    public void beanCreated(Object bean, BeanProvider provider)
    {
        getBeanContext().beanCreated(bean, provider, this);
    }

    /**
     * Returns a map with the resolved dependencies.
     *
     * @return the internal dependency map
     */
    Map<Dependency, BeanProvider> getDependencyMap()
    {
        return dependencyMap;
    }

    /**
     * Convenience method for obtaining the class loader provider from the
     * associated bean context.
     *
     * @return the class loader provider
     */
    private ClassLoaderProvider getCLP()
    {
        return getBeanContext().getClassLoaderProvider();
    }

    /**
     * Initializes the internal {@link InvocationHelper}. This method is called
     * after a successful initialization.
     *
     * @param store the current bean store
     */
    private void initInvocationHelper(BeanStore store)
    {
        invocationHelper =
                new InvocationHelper(DefaultBeanStore.fetchConversionHelper(
                        store, true));
    }
}
