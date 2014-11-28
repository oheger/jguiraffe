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
package net.sf.jguiraffe.di.impl.providers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.di.BeanInitializer;
import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.InjectionException;
import net.sf.jguiraffe.di.InvocationHelper;
import net.sf.jguiraffe.di.impl.HelperInvocations;
import net.sf.jguiraffe.di.impl.Invokable;

/**
 * <p>
 * An abstract base class for {@code BeanProvider} implementations with
 * life-cycle support.
 * </p>
 * <p>
 * A {@code LifeCycleBeanProvider} has the following properties:
 * <ul>
 * <li>A (usually simple) {@link BeanProvider} for actually
 * creating an instance of the managed bean.</li>
 * <li>An optional {@link Invokable} object for initializing the
 * newly created bean instance.</li>
 * </ul>
 * </p>
 * <p>
 * This base class provides basic functionality for the creation and
 * initialization of beans. It also implements the methods related to life-cycle
 * support of the {@code BeanProvider} interface in a meaningful way.
 * There are two methods that are intended to be called by concrete sub classes:
 * {@code createBean()} and {@code fetchBean()}.
 * </p>
 * <p>
 * {@code createBean()}, as its name implies, creates a new instance of
 * the managed bean class. This is done by invoking the
 * {@code BeanProvider} for creating new beans. After that the
 * {@code Invokable} object is called on the newly created bean. The
 * creation of a bean through the bean provider may cause an endless loop if
 * there are cyclic dependencies (e.g. bean A needs bean B as a constructor
 * argument and vice verse). Such cycles are detected and lead to a
 * {@code InjectionException} exception being thrown.
 * </p>
 * <p>
 * The {@code fetchBean()} method checks whether a bean instance has
 * already been created. If this is the case, it is directly returned. Otherwise
 * {@code createBean()} is called for creating a new instance. Depending
 * on their semantics derived classes decide, which of these methods to call.
 * This decision must be implemented in the {@code getBean()} method; all
 * other methods defined by the {@code BeanProvider} interface are
 * already implemented by this base class.
 * </p>
 * <p>
 * Implementation note: This class is intended to be used together with a
 * correct implementation of the {@code BeanContext} interface. It is not
 * thread-safe by itself, but if the bean context handles transactions properly,
 * it can be used in an environment with multiple threads accessing the bean
 * context concurrently.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: LifeCycleBeanProvider.java 208 2012-02-11 20:57:33Z oheger $
 */
public abstract class LifeCycleBeanProvider implements BeanProvider,
        BeanInitializer
{
    /** Stores the bean provider for creating a bean. */
    private final BeanProvider beanCreator;

    /** Stores the invocation object for initializing the bean. */
    private final Invokable beanInitializer;

    /**
     * Stores the currently processed dependency. This field is used for
     * producing meaningful error messages in case of cyclic dependencies, which
     * cannot be resolved.
     */
    private Dependency currentDependency;

    /** Stores the latest created bean. */
    private Object bean;

    /** Stores the lock ID of the current transaction. */
    private Long lockID;

    /** A flag whether a bean is currently initialized. */
    private boolean initializing;

    /** A flag whether a bean is currently created. */
    private boolean creating;

    /** A flag whether a bean instance has been successfully created. */
    private volatile boolean instanceCreated;

    /**
     * Creates a new instance of {@code LifeCycleBeanProvider} and
     * initializes it with the {@code BeanProvider} for creating the bean
     * instance and an {@code Invokable} for initializing it.
     *
     * @param createProvider the bean provider for creating a bean instance
     * (must not be <b>null</b>)
     * @param initinv an optional invocation object with initialization code for
     * the newly created bean
     * @throws IllegalArgumentException if the bean provider is <b>null</b>
     */
    protected LifeCycleBeanProvider(BeanProvider createProvider,
            Invokable initinv)
    {
        if (createProvider == null)
        {
            throw new IllegalArgumentException(
                    "Creation bean provider must not be null!");
        }
        beanCreator = createProvider;
        beanInitializer =
                (initinv != null) ? initinv
                        : HelperInvocations.IDENTITY_INVOCATION;
    }

    /**
     * Creates a new instance of {@code LifeCycleBeanProvider} and
     * initializes it with the {@code BeanProvider} for creating the bean
     * instance.
     *
     * @param createProvider the bean provider for creating a bean instance
     * (must not be <b>null</b>)
     * @throws IllegalArgumentException if the bean provider is <b>null</b>
     */
    protected LifeCycleBeanProvider(BeanProvider createProvider)
    {
        this(createProvider, null);
    }

    /**
     * Returns the {@code BeanProvider} that is responsible for creating
     * a new bean instance.
     *
     * @return the bean provider for creating new bean instances
     */
    public BeanProvider getBeanCreator()
    {
        return beanCreator;
    }

    /**
     * Returns the {@code Invokable} object responsible for initializing
     * the newly created bean. This method never returns <b>null</b>. If no
     * initializer was set, a default initializer object (that does not have any
     * effect) is returned.
     *
     * @return the invocation object used for initialization
     */
    public Invokable getBeanInitializer()
    {
        return beanInitializer;
    }

    /**
     * Returns the class of the bean managed by this provider. This class is
     * determined by the {@code BeanProvider} for creating new bean
     * instances.
     *
     * @param dependencyProvider the dependency provider
     * @return the class of the managed bean
     * @throws InjectionException if an error occurs determining the class
     */
    public Class<?> getBeanClass(DependencyProvider dependencyProvider)
    {
        return getBeanCreator().getBeanClass(dependencyProvider);
    }

    /**
     * Returns the dependencies of this bean provider. This implementation
     * obtains the dependencies of the creation bean provider and the invocation
     * object for initialization and returns a union.
     *
     * @return the dependencies of this bean provider
     */
    public Set<Dependency> getDependencies()
    {
        List<Dependency> initDeps =
                getBeanInitializer().getParameterDependencies();
        Set<Dependency> creatorDeps = getBeanCreator().getDependencies();

        if (initDeps.isEmpty())
        {
            return creatorDeps;
        }
        else
        {
            Set<Dependency> result = new HashSet<Dependency>();
            if (creatorDeps != null)
            {
                result.addAll(creatorDeps);
            }
            result.addAll(initDeps);
            return result;
        }
    }

    /**
     * Returns the ID of the locking transaction. If this method returns a non
     * <b>null</b> value, this bean provider must not be used by a concurrent
     * transaction.
     *
     * @return the ID of the locking transaction
     */
    public Long getLockID()
    {
        return lockID;
    }

    /**
     * Sets the ID of the locking transaction. This indicates that this bean
     * provider is blocked by the specified transaction.
     *
     * @param lid the ID of the locking transaction
     */
    public void setLockID(Long lid)
    {
        lockID = lid;
    }

    /**
     * Returns a flag whether the bean managed by this provider is available.
     * This implementation checks whether the bean is currently created. If this
     * is the case, it is not available.
     *
     * @return a flag whether the managed bean is available
     */
    public boolean isBeanAvailable()
    {
        return !creating;
    }

    /**
     * Notifies this {@code BeanProvider} that it is no longer needed. This is
     * just an empty dummy implementation. Derived classes that support shutdown
     * handling have to override it.
     *
     * @param depProvider the {@code DependencyProvider}
     */
    public void shutdown(DependencyProvider depProvider)
    {
    }

    /**
     * Performs initialization. This method is called by the dependency provider
     * if initialization has to be delayed because of cyclic dependencies. It
     * invokes the initializer.
     *
     * @param dependencyProvider the dependency provider
     */
    public void initialize(DependencyProvider dependencyProvider)
    {
        try
        {
            Object initBean =
                    fetchInitializedBeanInstance(bean, dependencyProvider);
            bean = initBean;
            instanceCreated = true;
        }
        finally
        {
            initializing = false;
        }
    }

    /**
     * Returns a string representation of this object. This string also contains
     * information about the bean provider used for creating the bean and the
     * invocation object for the initialization.
     *
     * @return a string for this object
     */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append(getClass());
        buf.append('@').append(System.identityHashCode(this));
        buf.append("[ creator = ").append(getBeanCreator());
        buf.append(" initializer = ").append(getBeanInitializer());
        buf.append(']');
        return buf.toString();
    }

    /**
     * Creates and initializes a new bean instance. This method is reentrant,
     * which is necessary if there are cycles in the dependencies. In this case
     * a bean that is only partly initialized may be returned. It is also
     * possible that the cycles cannot be resolved, then an exception is thrown.
     *
     * @param dependencyProvider the dependency provider
     * @return the newly created bean instance
     * @throws InjectionException if an error occurs
     */
    protected Object createBean(DependencyProvider dependencyProvider)
    {
        if (creating)
        {
            // a disallowed reentrant call
            throw new InjectionException("Unresolvable cyclic dependency "
                    + currentDependency + " in bean provider " + this);
        }

        if (!initializing)
        {
            // no reentrant call
            creating = true;
            initializing = true;
            boolean canInit = true;
            try
            {
                bean = doCreateBean(createDiagnosticDependencyProvider(dependencyProvider));
                creating = false;

                // Can initialization be directly performed?
                canInit = canInitialize(dependencyProvider);
                if (canInit)
                {
                    bean =
                            fetchInitializedBeanInstance(bean,
                                    dependencyProvider);
                }
                else
                {
                    // No, postpone it
                    dependencyProvider.addInitializer(this);
                }

                if (canInit)
                {
                    instanceCreated = true;
                }
            }
            finally
            {
                creating = false;
                if (canInit)
                {
                    initializing = false;
                }
            }
        }

        return bean;
    }

    /**
     * Returns the bean instance created by this provider. If no instance has
     * been created yet, this is done now by invoking {@code createBean()}.
     * Otherwise the bean instance is directly returned. If the dependencies
     * contain cyclic references, it is possible that a bean instance is
     * returned, which has not yet been fully initialized. Cycles that cannot be
     * resolved cause an exception.
     *
     * @param dependencyProvider the dependency provider
     * @return the bean instance managed by this provider
     * @throws InjectionException if an error occurs
     */
    protected Object fetchBean(DependencyProvider dependencyProvider)
    {
        return (bean != null) ? bean : createBean(dependencyProvider);
    }

    /**
     * Checks whether initialization of the bean is now possible. This method
     * tests whether all dependencies required for the bean's initialization are
     * currently available.
     *
     * @param dependencyProvider the dependency provider
     * @return a flag whether initialization can now be performed
     */
    protected boolean canInitialize(DependencyProvider dependencyProvider)
    {
        List<Dependency> initDeps = getBeanInitializer()
                .getParameterDependencies();
        if (initDeps != null)
        {
            for (Dependency d : initDeps)
            {
                if (!dependencyProvider.isBeanAvailable(d))
                {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns a flag whether a bean instance has already been created. This can
     * be used by derived classes for adapting their behavior. This
     * implementation returns <b>true</b> if and only if a bean instance has
     * been created and fully initialized.
     *
     * @return a flag whether a bean instance has been created
     */
    protected boolean hasBean()
    {
        return instanceCreated;
    }

    /**
     * Resets any so far created bean. This method can be called by derived
     * classes to reset this bean provider. In a following call to
     * {@code fetchBean()} a completely new bean will be created.
     */
    protected void resetBean()
    {
        bean = null;
        instanceCreated = false;
    }

    /**
     * Creates a new bean instance. This method is called by
     * {@code createBean()} for actually creating the bean.
     *
     * @param dependencyProvider the dependency provider
     * @return the new bean instance
     * @throws InjectionException if an error occurs
     */
    protected Object doCreateBean(DependencyProvider dependencyProvider)
    {
        return getBeanCreator().getBean(
                new DiagnosticDependencyProvider(dependencyProvider));
    }

    /**
     * Initializes a newly created bean instance. This method is called by
     * {@code createBean()} for each new bean instance. This implementation
     * invokes the initializer and notifies the {@code DependencyProvider} about
     * the creation of a new bean.
     *
     * @param bean the bean to initialize
     * @param dependencyProvider the dependency provider
     * @throws InjectionException if an error occurs
     * @deprecated This method is not called any more during bean creation;
     * instead {@link #fetchInitializedBeanInstance(Object, DependencyProvider)}
     * is invoked
     */
    @Deprecated
    protected void initBean(Object bean, DependencyProvider dependencyProvider)
    {
        getBeanInitializer().invoke(dependencyProvider, bean);
        dependencyProvider.beanCreated(bean, this);
    }

    /**
     * Returns the initialized bean instance. This method is called by
     * {@code createBean()} for each new bean instance. Its purpose is to
     * execute the initializer script on the bean. This implementation invokes
     * the initializer and notifies the {@code DependencyProvider} about the
     * creation of a new bean. Note that the bean returned by the initializer
     * script is the result of this method. Thus it is possible that a different
     * bean than passed to the method becomes the managed bean of this provider.
     * However, if the initializer returns <b>null</b>, the passed in bean is
     * returned.
     *
     * @param bean the bean to initialize
     * @param dependencyProvider the dependency provider
     * @return the bean instance
     * @throws InjectionException if an error occurs
     * @since 1.1
     */
    protected Object fetchInitializedBeanInstance(Object bean,
            DependencyProvider dependencyProvider)
    {
        Object initBean = getBeanInitializer().invoke(dependencyProvider, bean);
        dependencyProvider.beanCreated(initBean, this);
        return (initBean != null) ? initBean : bean;
    }

    /**
     * Creates a specialized dependency provider with the ability of generating
     * meaningful error messages in case of cyclic dependencies. This method is
     * called by {@code createBean()} before the creator is invoked.
     *
     * @param wrappedProvider the dependency provider to be wrapped
     * @return the diagnostic dependency provider
     */
    DependencyProvider createDiagnosticDependencyProvider(
            DependencyProvider wrappedProvider)
    {
        return new DiagnosticDependencyProvider(wrappedProvider);
    }

    /**
     * A specialized {@code DependencyProvider} implementation that is
     * used for generating meaningful error messages for cyclic dependencies.
     * This implementation stores the currently processed dependency in a member
     * field. If later a reentrant call to {@code createBean()} happens,
     * we are able to determine, which dependency caused the problem.
     */
    private class DiagnosticDependencyProvider implements DependencyProvider
    {
        /** Stores the wrapped dependency provider. */
        private final DependencyProvider wrappedProvider;

        /**
         * Creates a new instance of {@code DiagnosticDependencyProvider}
         * and sets the dependency provider to be wrapped.
         *
         * @param d the wrapped dependency provider
         */
        public DiagnosticDependencyProvider(DependencyProvider d)
        {
            wrappedProvider = d;
        }

        /**
         * Returns the bean for the given dependency. This implementation saves
         * the dependency in an internal field and then delegates to the wrapped
         * provider.
         *
         * @param dependency the dependency
         * @return the dependent bean
         * @throws InjectionException if an error occurs
         */
        public Object getDependentBean(Dependency dependency)
        {
            currentDependency = dependency;
            return wrappedProvider.getDependentBean(dependency);
        }

        /**
         * Loads the specified class. This implementation just delegates to the
         * wrapped provider.
         *
         * @param name the name of the class
         * @param loaderRef the name of the class loader to use
         * @return the resolved class
         * @throws InjectionException if an error occurs
         */
        public Class<?> loadClass(String name, String loaderRef)
        {
            return wrappedProvider.loadClass(name, loaderRef);
        }

        /**
         * Adds an initializer. This implementation just delegates to the
         * wrapped provider.
         *
         * @param initializer the initializer to add
         */
        public void addInitializer(BeanInitializer initializer)
        {
            wrappedProvider.addInitializer(initializer);
        }

        /**
         * Checks whether a dependency is currently available. This
         * implementation just delegates to the wrapped provider.
         *
         * @param dependency the dependency in question
         * @return a flag whether this dependency is available
         */
        public boolean isBeanAvailable(Dependency dependency)
        {
            return wrappedProvider.isBeanAvailable(dependency);
        }

        /**
         * Returns a set with the names of the registered class loaders. This
         * implementation just delegates to the wrapped provider.
         *
         * @return a set with the names of the registered class loaders
         */
        public Set<String> classLoaderNames()
        {
            return wrappedProvider.classLoaderNames();
        }

        /**
         * Returns the class loader that was registered under the given symbolic
         * name. This implementation just delegates to the wrapped provider.
         *
         * @param name the name of the class loader
         * @return the class loader for this name
         * @throws InjectionException if no such class loader can be found
         */
        public ClassLoader getClassLoader(String name)
        {
            return wrappedProvider.getClassLoader(name);
        }

        /**
         * Returns the default class loader name. This implementation just
         * delegates to the wrapped provider.
         *
         * @return the name of the default class loader
         */
        public String getDefaultClassLoaderName()
        {
            return wrappedProvider.getDefaultClassLoaderName();
        }

        /**
         * Registers a class loader under a symbolic name. This implementation
         * just delegates to the wrapped provider.
         *
         * @param name the symbolic name
         * @param loader the class loader
         */
        public void registerClassLoader(String name, ClassLoader loader)
        {
            wrappedProvider.registerClassLoader(name, loader);
        }

        /**
         * Sets the name of the default class loader. This implementation just
         * delegates to the wrapped provider.
         *
         * @param loaderName the new default class loader name
         */
        public void setDefaultClassLoaderName(String loaderName)
        {
            wrappedProvider.setDefaultClassLoaderName(loaderName);
        }

        /**
         * A new bean was created. This implementation just delegates to the
         * wrapped provider.
         *
         * @param bean the new bean
         * @param provider the responsible bean provider
         */
        public void beanCreated(Object bean, BeanProvider provider)
        {
            wrappedProvider.beanCreated(bean, provider);
        }

        /**
         * Sets the bean context responsible for a bean creation. This
         * implementation just delegates to the wrapped provider.
         *
         * @param context the responsible bean context
         */
        public void setCreationBeanContext(BeanContext context)
        {
            wrappedProvider.setCreationBeanContext(context);
        }

        /**
         * Returns the current {@code InvocationHelper} object. This
         * implementation just delegates to the wrapped provider.
         *
         * @return the {@code InvocationHelper}
         */
        public InvocationHelper getInvocationHelper()
        {
            return wrappedProvider.getInvocationHelper();
        }
    }
}
