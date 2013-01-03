/*
 * Copyright 2006-2012 The JGUIraffe Team.
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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.di.BeanContextClient;
import net.sf.jguiraffe.di.BeanCreationListener;
import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.InjectionException;

/**
 * <p>
 * A default implementation of the <code>BeanContext</code> interface.
 * </p>
 * <p>
 * This class allows full access to all beans defined in a hierarchy of
 * {@link BeanStore}s. Dependencies are dynamically resolved and injected.
 * </p>
 * <p>
 * The class is thread-safe. When operating on a bean store hierarchy it
 * implements transactional behavior as described in the documentation to the
 * {@link BeanStore} interface, i.e. if two threads try to access a
 * {@link BeanProvider} concurrently, one of the will be suspended until the
 * other one has resolved all of its dependencies. This enables concurrent,
 * synchronized access to bean stores in a read-only manner. However the bean
 * stores should not be written at the same time.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: DefaultBeanContext.java 205 2012-01-29 18:29:57Z oheger $
 */
public class DefaultBeanContext implements BeanContext
{
    /** A counter for generating transaction IDs. */
    private static final AtomicLong TX_COUNTER = new AtomicLong();

    /** Stores the default bean store. */
    private volatile BeanStore defaultBeanStore;

    /** An object with the bean creation listeners registered at this context.*/
    private final BeanCreationListenerSupport creationListeners;

    /**
     * Stores an internally used dependency provider that is independent on a
     * specific transaction. This provider will be used e.g. for resolving the
     * classes of bean providers.
     */
    private final DependencyProvider internalDependencyProvider;

    /** Holds a reference to the class loader provider used by this context.*/
    private volatile ClassLoaderProvider classLoaderProvider;

    /**
     * Creates a new instance of {@code DefaultBeanContext}.
     */
    public DefaultBeanContext()
    {
        this(null);
    }

    /**
     * Creates a new instance of {@code DefaultBeanContext} and sets the default
     * bean store. This parameter is optional. If no {@code BeanStore} is
     * provided, a default store must be set later or only methods can be used
     * that expect a store as argument.
     *
     * @param defStore the default bean store
     */
    public DefaultBeanContext(BeanStore defStore)
    {
        internalDependencyProvider = new DefaultDependencyProvider(this);
        creationListeners = new BeanCreationListenerSupport(this);
        classLoaderProvider = new DefaultClassLoaderProvider();
        setDefaultBeanStore(defStore);
    }

    /**
     * Returns the {@code ClassLoaderProvider} used by this bean context.
     *
     * @return the {@code ClassLoaderProvider}
     * @see #setClassLoaderProvider(ClassLoaderProvider)
     */
    public ClassLoaderProvider getClassLoaderProvider()
    {
        return classLoaderProvider;
    }

    /**
     * Sets the {@code ClassLoaderProvider} to be used by this bean context. The
     * {@code ClassLoaderProvider} is needed when dependencies to beans are to
     * be resolved that specified by class names. When a new {@code
     * DefaultBeanContext} instance is created, a default {@code
     * ClassLoaderProvider} is set. It is then possible to change this object
     * using this method.
     *
     * @param classLoaderProvider the new {@code ClassLoaderProvider} (must not
     *        be <b>null</b>)
     * @throws IllegalArgumentException if the parameter is <b>null</b>
     */
    public void setClassLoaderProvider(ClassLoaderProvider classLoaderProvider)
    {
        if (classLoaderProvider == null)
        {
            throw new IllegalArgumentException(
                    "Class loader provider must not be null!");
        }
        this.classLoaderProvider = classLoaderProvider;
    }

    /**
     * Obtains a list of the classes of the beans defined in the default bean
     * store.
     *
     * @return the classes of the defined beans
     */
    public Set<Class<?>> beanClasses()
    {
        return beanClasses(getDefaultBeanStore());
    }

    /**
     * Obtains a list of the classes of the beans defined in the given store and
     * its parents.
     *
     * @param store the store to start from
     * @return the classes of the defined beans
     */
    public Set<Class<?>> beanClasses(BeanStore store)
    {
        Set<Class<?>> result = new HashSet<Class<?>>();
        fetchBeanClasses(store, result);
        return result;
    }

    /**
     * Returns a set with the names of the beans defined in the default bean
     * store.
     *
     * @return the names of the defined beans
     */
    public Set<String> beanNames()
    {
        return beanNames(getDefaultBeanStore());
    }

    /**
     * Returns a set with the names of the beans defined in the given bean store
     * (or its parent).
     *
     * @param store the bean store
     * @return a set with the names of the defined beans
     */
    public Set<String> beanNames(BeanStore store)
    {
        Set<String> names = new HashSet<String>();
        fetchBeanNames(store, names);
        return names;
    }

    /**
     * Tests whether a bean with the given name can be found in the default
     * store.
     *
     * @param name the name of the bean
     * @return a flag whether this bean can be found
     */
    public boolean containsBean(String name)
    {
        return containsBean(name, getDefaultBeanStore());
    }

    /**
     * Tests whether a bean with the given name can be found in the specified
     * bean store. <b>null</b> can be specified for both the name and the bean
     * store; result will then be <b>false</b>.
     *
     * @param name the name of the bean
     * @param store the bean store
     * @return a flag whether this bean can be found in this store
     */
    public boolean containsBean(String name, BeanStore store)
    {
        return (name != null) ? containsDependency(NameDependency
                .getInstance(name), store) : false;
    }

    /**
     * Tests whether a bean with the given bean class can be found in the
     * default bean store.
     *
     * @param beanClass the class of the bean
     * @return a flag whether this bean can be found
     */
    public boolean containsBean(Class<?> beanClass)
    {
        return containsBean(beanClass, getDefaultBeanStore());
    }

    /**
     * Tests whether a bean with the given class can be found in the specified
     * bean store. <b>null</b> can be specified for both the name and the bean
     * store; result will then be <b>false</b>.
     *
     * @param beanClass the class of the bean
     * @param store the bean store
     * @return a flag whether this bean can be found in this store
     */
    public boolean containsBean(Class<?> beanClass, BeanStore store)
    {
        return (beanClass != null) ? containsDependency(ClassDependency
                .getInstance(beanClass), store) : false;
    }

    /**
     * Returns the bean with the specified name from the default store.
     *
     * @param name the name of the bean to retrieve
     * @return the bean with this name
     * @throws InjectionException if an error occurs
     */
    public Object getBean(String name)
    {
        return getBean(name, getDefaultBeanStore());
    }

    /**
     * Returns the bean with the specified name from the given bean store.
     *
     * @param name the name of the bean to retrieve
     * @param store the bean store
     * @return the bean with this name
     * @throws InjectionException if an error occurs
     */
    public Object getBean(String name, BeanStore store)
    {
        if (name == null)
        {
            throw new InjectionException("Bean name must not be null!");
        }
        return getBean(NameDependency.getInstance(name), store);
    }

    /**
     * Returns the bean with the specified class from the default bean store.
     *
     * @param <T> the type of the bean to be retrieved
     * @param beanCls the class of the bean to be retrieved
     * @return the bean with this class
     * @throws InjectionException if an error occurs
     */
    public <T> T getBean(Class<T> beanCls)
    {
        return getBean(beanCls, getDefaultBeanStore());
    }

    /**
     * Returns the bean with the specified class from the given bean store.
     *
     * @param <T> the type of the bean to be retrieved
     * @param beanCls the class of the bean to be retrieved
     * @param store the bean store
     * @return the bean with this class
     * @throws InjectionException if an error occurs
     */
    public <T> T getBean(Class<T> beanCls, BeanStore store)
    {
        if (beanCls == null)
        {
            throw new InjectionException("Bean class must not be null!");
        }
        return beanCls
                .cast(getBean(ClassDependency.getInstance(beanCls), store));
    }

    /**
     * Returns the default bean store.
     *
     * @return the default bean store
     */
    public BeanStore getDefaultBeanStore()
    {
        return defaultBeanStore;
    }

    /**
     * Searches for the specified {@code BeanProvider} in the accessible bean
     * stores (starting with the default bean store) and the returns the name,
     * under which it is registered.
     *
     * @param beanProvider the {@code BeanProvider} in question
     * @return the corresponding bean name or <b>null</b> if it cannot be
     *         resolved
     */
    public String beanNameFor(BeanProvider beanProvider)
    {
        return beanNameFor(beanProvider, getDefaultBeanStore());
    }

    /**
     * Searches for the specified {@code BeanProvider} in the accessible bean
     * stores (starting with the specified bean store) and the returns the name,
     * under which it is registered.
     *
     * @param beanProvider the {@code BeanProvider} in question
     * @param store the {@code BeanStore}
     * @return the corresponding bean name or <b>null</b> if it cannot be
     *         resolved
     */
    public String beanNameFor(BeanProvider beanProvider, BeanStore store)
    {
        if (store == null)
        {
            return null;
        }

        else
        {
            for (String s : store.providerNames())
            {
                if (beanProvider == store.getBeanProvider(s))
                {
                    return s;
                }
            }

            return beanNameFor(beanProvider, store.getParent());
        }
    }

    /**
     * Closes this {@code BeanContext}. This is just an empty dummy
     * implementation. There are no resources to be freed.
     */
    public void close()
    {
    }

    /**
     * Sets the default bean store.
     *
     * @param store the new default bean store
     */
    public void setDefaultBeanStore(BeanStore store)
    {
        defaultBeanStore = store;
    }

    /**
     * Adds the specified {@code BeanCreationListener} to this context.
     *
     * @param l the listener to add (must not be <b>null</b>)
     * @throws IllegalArgumentException if the listener is <b>null</b>
     */
    public void addBeanCreationListener(BeanCreationListener l)
    {
        creationListeners.addBeanCreationListener(l);
    }

    /**
     * Removes the specified {@code BeanCreationListener} from this context.
     *
     * @param l the listener to be removed
     */
    public void removeBeanCreationListener(BeanCreationListener l)
    {
        creationListeners.removeBeanCreationListener(l);
    }

    /**
     * Obtains the bean from the {@link BeanProvider} specified by the given
     * {@link Dependency}. This method is called by the other
     * <code>getBean()</code> methods. It does the real work.
     *
     * @param dependency the dependency to be resolved
     * @param store the current store
     * @return the bean managed by the specified provider
     * @throws InjectionException if an error occurs while resolving the
     *         dependency
     */
    protected Object getBean(Dependency dependency, BeanStore store)
    {
        BeanStore root = fetchRootStore(store);
        DefaultDependencyProvider depProvider = new DefaultDependencyProvider(this);

        synchronized (root)
        {
            try
            {
                while (!depProvider.initialize(dependency, store))
                {
                    // wait until we can lock all dependent providers
                    waitForTx(root);
                }
            }
            catch (InterruptedException iex)
            {
                throw new InjectionException(iex);
            }

            // lock the whole dependency graph
            depProvider.lock(nextTxID());
        }

        try
        {
            return depProvider.getDependentBean(dependency);
        }
        finally
        {
            try
            {
                // perform postponed initialization
                depProvider.invokeInitializers();
            }

            finally
            {
                synchronized (root)
                {
                    // unlock dependency graph
                    depProvider.lock(null);
                    // resume a waiting transaction
                    root.notify();
                }
            }
        }
    }

    /**
     * Waits at the specified bean store until the current transaction finishes.
     * This method is called when a transaction affects a bean provider that is
     * already locked by another transaction. In this case this other
     * transaction has to finish first. This implementation calls
     * <code>wait()</code> on the given bean store.
     *
     * @param root the root bean store
     * @throws InterruptedException if the thread is interrupted
     */
    protected void waitForTx(BeanStore root) throws InterruptedException
    {
        root.wait();
    }

    /**
     * Returns the next transaction ID. Each starting transaction is assigned a
     * new ID.
     *
     * @return the next transaction ID
     */
    static Long nextTxID()
    {
        return TX_COUNTER.incrementAndGet();
    }

    /**
     * Resets the counter for transaction IDs. This method is mainly used for
     * testing purposes.
     */
    static void resetTxID()
    {
        TX_COUNTER.set(0);
    }

    /**
     * Returns the internally used dependency provider.
     *
     * @return the internal dependency provider
     */
    DependencyProvider getInternalDependencyProvider()
    {
        return internalDependencyProvider;
    }

    /**
     * Notifies this context about the creation of a bean. This method is called
     * by the dependency provider when a {@code BeanProvider} indicates the
     * creation of a new bean. It performs some post processing of the bean (for
     * instance, it checks whether the bean implements certain interfaces
     * evaluated by the framework) and notifies the {@code BeanCreationListener}
     * s registered at this context.
     *
     * @param bean the bean that was created
     * @param beanProvider the responsible {@code BeanProvider}
     * @param depProvider the current {@code DependencyProvider}
     */
    void beanCreated(Object bean, BeanProvider beanProvider,
            DefaultDependencyProvider depProvider)
    {
        depProvider.setCreationBeanContext(this);
        creationListeners
                .fireBeanCreationEvent(beanProvider, depProvider, bean);

        if (bean instanceof BeanContextClient)
        {
            ((BeanContextClient) bean).setBeanContext(depProvider
                    .getCreationBeanContext());
        }
    }

    /**
     * Helper method for collecting the classes of the defined beans.
     *
     * @param store the current bean store
     * @param clsSet the set where the classes are stored
     */
    private void fetchBeanClasses(BeanStore store, Set<Class<?>> clsSet)
    {
        if (store != null)
        {
            for (String n : store.providerNames())
            {
                BeanProvider p = store.getBeanProvider(n);
                clsSet.add(p.getBeanClass(getInternalDependencyProvider()));
            }
            fetchBeanClasses(store.getParent(), clsSet);
        }
    }

    /**
     * Helper method for collecting the names of the defined beans.
     *
     * @param store the current bean store
     * @param names the set, in which to store the names
     */
    private void fetchBeanNames(BeanStore store, Set<String> names)
    {
        if (store != null)
        {
            names.addAll(store.providerNames());
            fetchBeanNames(store.getParent(), names);
        }
    }

    /**
     * Tries to resolve the given dependency in the specified bean store.
     *
     * @param dep the dependency to resolve
     * @param store the store
     * @return a flag whether the dependency could be resolved
     */
    private boolean containsDependency(Dependency dep, BeanStore store)
    {
        if (store == null)
        {
            return false;
        }

        try
        {
            dep.resolve(store, getInternalDependencyProvider());
            return true;
        }
        catch (InjectionException iex)
        {
            return false;
        }
    }

    /**
     * Obtains the root store of the given bean store. Navigates through the
     * hierarchy of parent stores until the root is reached. If the passed in
     * store is already <b>null</b>, an exception is thrown.
     *
     * @param store the current store
     * @return the root store of this hierarchy
     * @throws InjectionException if the store is <b>null</b>
     */
    private BeanStore fetchRootStore(BeanStore store)
    {
        BeanStore current = store;
        BeanStore result = null;

        while (current != null)
        {
            result = current;
            current = current.getParent();
        }

        if (result == null)
        {
            throw new InjectionException("Store must not be null!");
        }
        return result;
    }
}
