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

import java.util.Set;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.di.BeanInitializer;
import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.InjectionException;
import net.sf.jguiraffe.di.InvocationHelper;

/**
 * <p>
 * An implementation of the {@code DependencyProvider} interface, which does not
 * support external dependencies.
 * </p>
 * <p>
 * This class allows the execution of simple {@link Invokable} objects, which do
 * not require access to other beans stored in a {@code BeanContext}. The
 * methods defined by the {@link ClassLoaderProvider} interface are fully
 * implemented (by delegating to a wrapped {@link ClassLoaderProvider}). But
 * other methods for dealing with dependencies throw an exception. This makes it
 * possible to execute simple invocation scripts (e.g. defined by a
 * {@link ChainedInvocation}), including access to local variables. However,
 * other bean providers cannot be accessed.
 * </p>
 * <p>
 * A use case for this class is the execution of a shutdown script by a bean
 * provider that is no longer needed. At the time the shutdown script is invoked
 * the corresponding {@code BeanContext} (or {@code BeanStore}) may already be
 * partly destroyed, so access to other beans is not safe. Nevertheless
 * arbitrary methods on the affected bean can be invoked.
 * </p>
 * <p>
 * When creating an instance of this class a {@link ClassLoaderProvider} object
 * must be provided. All methods dealing with classes and class loaders are
 * passed to this object. The other methods are implemented by simply throwing
 * an exception.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: RestrictedDependencyProvider.java 205 2012-01-29 18:29:57Z oheger $
 */
public class RestrictedDependencyProvider implements DependencyProvider
{
    /** Stores the wrapped {@code ClassLoaderProvider}. */
    private final ClassLoaderProvider classLoaderProvider;

    /** Stores the invocation helper. */
    private final InvocationHelper invocationHelper;

    /**
     * Creates a new instance of {@code RestrictedDependencyProvider} and
     * initializes it with the specified {@code ClassLoaderProvider} and the
     * {@code InvocationHelper}.
     *
     * @param clp the {@code ClassLoaderProvider} (must not be <b>null</b>)
     * @param invHlp the {@code InvocationHelper} (must not be <b>null</b>)
     * @throws IllegalArgumentException if a required parameter is <b>null</b>
     */
    public RestrictedDependencyProvider(ClassLoaderProvider clp,
            InvocationHelper invHlp)
    {
        if (clp == null)
        {
            throw new IllegalArgumentException(
                    "ClassLoaderProvider must not be null!");
        }
        if (invHlp == null)
        {
            throw new IllegalArgumentException(
                    "InvocationHelper must not be null!");
        }

        classLoaderProvider = clp;
        invocationHelper = invHlp;
    }

    /**
     * Returns the wrapped {@code ClassLoaderProvider}.
     *
     * @return the {@code ClassLoaderProvider}
     */
    public ClassLoaderProvider getClassLoaderProvider()
    {
        return classLoaderProvider;
    }

    /**
     * Adds a {@code BeanInitializer}. This implementation just throws an
     * exception.
     *
     * @param initializer the initializer
     */
    public void addInitializer(BeanInitializer initializer)
    {
        throw new UnsupportedOperationException(
                "BeanInitializers are not supported by this dependency provider!");
    }

    /**
     * Notifies this dependency provider about a newly created bean. This
     * implementation just throws an exception.
     *
     * @param bean the new bean
     * @param provider the responsible bean provider
     */
    public void beanCreated(Object bean, BeanProvider provider)
    {
        throw new UnsupportedOperationException(
                "This method is not supported by this dependency provider!");
    }

    /**
     * Returns a dependent bean. This implementation invokes the passed in
     * {@code Dependency} with a <b>null</b> bean store. If a bean provider is
     * returned, this provider's bean is queried. This way certain {@code
     * Dependency} implementations can be served, while others cause an
     * exception.
     *
     * @param dependency the dependency to resolve
     * @return the corresponding bean
     * @throws InjectionException if the {@code Dependency} cannot be resolved
     */
    public Object getDependentBean(Dependency dependency)
    {
        try
        {
            BeanProvider bp = dependency.resolve(null, this);
            return bp.getBean(this);
        }
        catch (NullPointerException npex)
        {
            throw new InjectionException(
                    "This dependency provider does not allow access to "
                            + dependency, npex);
        }
    }

    /**
     * Checks whether a bean is available. This implementation just throws an
     * exception.
     *
     * @param dependency the dependency to the bean
     * @return a flag whether this bean is available
     */
    public boolean isBeanAvailable(Dependency dependency)
    {
        throw new UnsupportedOperationException(
                "This method is not supported by this dependency provider!");
    }

    /**
     * Sets the context that is responsible for a bean creation event. This
     * implementation just throws an exception.
     *
     * @param context the context
     */
    public void setCreationBeanContext(BeanContext context)
    {
        throw new UnsupportedOperationException(
                "This method is not supported by this dependency provider!");
    }

    /**
     * Returns a set with the names of all class loaders registered at this
     * object. This implementation delegates to the wrapped {@code
     * ClassLoaderProvider}.
     *
     * @return a set with the names of the known class loaders
     */
    public Set<String> classLoaderNames()
    {
        return getClassLoaderProvider().classLoaderNames();
    }

    /**
     * Returns the class loader that was registered under the given name. This
     * implementation delegates to the wrapped {@code ClassLoaderProvider}.
     *
     * @param name the name of the class loader
     * @return the corresponding class loader
     * @throws InjectionException if the name is unknown
     */
    public ClassLoader getClassLoader(String name)
    {
        return getClassLoaderProvider().getClassLoader(name);
    }

    /**
     * Returns the name of the default class loader. This implementation
     * delegates to the wrapped {@code ClassLoaderProvider}.
     *
     * @return the default class loader
     */
    public String getDefaultClassLoaderName()
    {
        return getClassLoaderProvider().getDefaultClassLoaderName();
    }

    /**
     * Loads a class using the specified class loader. This implementation
     * delegates to the wrapped {@code ClassLoaderProvider}.
     *
     * @param name the name of the class to load
     * @param loaderRef the name of the class loader
     * @return the corresponding class
     * @throws InjectionException if an error occurs
     */
    public Class<?> loadClass(String name, String loaderRef)
    {
        return getClassLoaderProvider().loadClass(name, loaderRef);
    }

    /**
     * Registers a class loader under a name. This implementation delegates to
     * the wrapped {@code ClassLoaderProvider}.
     *
     * @param name the name
     * @param loader the class loader
     */
    public void registerClassLoader(String name, ClassLoader loader)
    {
        getClassLoaderProvider().registerClassLoader(name, loader);
    }

    /**
     * Sets the name of the default class loader. This implementation delegates
     * to the wrapped {@code ClassLoaderProvider}.
     *
     * @param loaderName the new default class loader name
     */
    public void setDefaultClassLoaderName(String loaderName)
    {
        getClassLoaderProvider().setDefaultClassLoaderName(loaderName);
    }

    /**
     * Returns the {@code InvocationHelper} object. This implementation returns
     * the object that was passed to the constructor.
     *
     * @return the {@code InvocationHelper} object
     */
    public InvocationHelper getInvocationHelper()
    {
        return invocationHelper;
    }
}
