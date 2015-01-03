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

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.InjectionException;
import net.sf.jguiraffe.di.ReflectionUtils;

/**
 * <p>
 * A default implementation of the {@code ClassLoaderProvider} interface.
 * </p>
 * <p>
 * This is a straight-forward (but thread-safe) implementation of all the
 * methods defined by the {@code ClassLoaderProvider} interface. Class loaders
 * registered at this object are stored in a map. A default class loader name
 * can be set. If none has been set, the class loader that loaded this class is
 * returned as default class loader.
 * </p>
 * <p>
 * Per default, classes of the library (starting with the prefix
 * {@code net.sf.jguiraffe}) are always loaded by the class loader which loaded
 * this class. This makes sense for instance in an OSGi environment: it
 * allows access even to internal implementation classes. This behavior can be
 * disabled by setting the {@code handleInternalClasses} property to
 * <b>false</b>. Then the selected class loader is used to load all classes.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: DefaultClassLoaderProvider.java 211 2012-07-10 19:49:13Z oheger $
 */
public class DefaultClassLoaderProvider implements ClassLoaderProvider
{
    /** The prefix for class names which belong to this library. */
    private static final String LIBRARY_CLASS_PREFIX = "net.sf.jguiraffe.";

    /** A map with the class loaders registered at this object. */
    private final Map<String, ClassLoader> mapLoaders;

    /** Stores the default class loader name. */
    private volatile String defaultLoaderName;

    /** A flag whether internal classes should be treated in a special way. */
    private final boolean handleInternalClasses;

    /**
     * Creates a new instance of {@code DefaultClassLoaderProvider}.
     */
    public DefaultClassLoaderProvider()
    {
        this(true);
    }

    /**
     * Creates a new instance of {@code DefaultClassLoaderProvider} and sets the
     * flag whether internal classes should be handled in a special way.
     *
     * @param fHandleInternalClasses the value of the flag
     * @since 1.2
     */
    public DefaultClassLoaderProvider(boolean fHandleInternalClasses)
    {
        mapLoaders = new ConcurrentHashMap<String, ClassLoader>();
        handleInternalClasses = fHandleInternalClasses;
    }

    /**
     * Returns a flag whether internal library classes are handled in a special
     * way by this class loader provider. If this property is <b>true</b>, all
     * classes belonging to this library are loaded by the class loader which
     * also loaded this class.
     *
     * @return <b>true</b> if library classes are handled in a special way,
     *         <b>false</b> otherwise
     * @since 1.2
     */
    public boolean isHandleInternalClasses()
    {
        return handleInternalClasses;
    }

    /**
     * Returns a set with the names of the class loaders that have been
     * registered at this object. All these names can be passed into the {@code
     * getClassLoader()} method.
     *
     * @return a set with the names of the class loaders registered at this
     *         object
     */
    public Set<String> classLoaderNames()
    {
        return Collections.unmodifiableSet(mapLoaders.keySet());
    }

    /**
     * Returns the class loader specified by the given symbolic name. This
     * method supports all variants: the default class loader (in this case the
     * name is <b>null</b>), the context class loader, and a registered class
     * loader.
     *
     * @param name the name of the class loader
     * @return the corresponding class loader
     * @throws InjectionException if the class loader cannot be resolved
     */
    public ClassLoader getClassLoader(String name)
    {
        String loaderName = (name != null) ? name : getDefaultClassLoaderName();
        if (loaderName == null)
        {
            return getClass().getClassLoader();
        }

        if (CONTEXT_CLASS_LOADER.equals(loaderName))
        {
            return Thread.currentThread().getContextClassLoader();
        }

        ClassLoader cl = mapLoaders.get(loaderName);
        if (cl == null)
        {
            throw new InjectionException("ClassLoader is not registered: "
                    + name);
        }
        return cl;
    }

    /**
     * Returns the name of the default class loader. Result can be <b>null</b>
     * if no default class loader name has been set so far.
     *
     * @return the name of the default class loader
     */
    public String getDefaultClassLoaderName()
    {
        return defaultLoaderName;
    }

    /**
     * Loads the class with the specified name using the class loader identified
     * by the given symbolic reference.
     *
     * @param name the class of the name to be loaded
     * @param loaderRef determines the class loader to be used
     * @return the loaded class
     * @throws InjectionException if the class cannot be loaded
     */
    public Class<?> loadClass(String name, String loaderRef)
    {
        return ReflectionUtils.loadClass(name,
                determineClassLoader(name, loaderRef));
    }

    /**
     * Allows to register a class loader under a symbolic name.
     *
     * @param name the name of the class loader (must not be <b>null</b>)
     * @param loader the class loader to be registered; can be <b>null</b>, then
     *        the class loader with the given name will be removed
     * @throws IllegalArgumentException if the name is <b>null</b>
     */
    public void registerClassLoader(String name, ClassLoader loader)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("Name must not be null!");
        }

        if (loader == null)
        {
            mapLoaders.remove(name);
        }
        else
        {
            mapLoaders.put(name, loader);
        }
    }

    /**
     * Sets the name of the default class loader. Applications can here specify
     * the name of a class loader they have registered or the reserved name
     * {@link ClassLoaderProvider#CONTEXT_CLASS_LOADER} for the context class
     * loader. A value of <b>null</b> causes {@link #getClassLoader(String)} to
     * fall back to the class loader which loaded this class.
     *
     * @param loaderName the new default class loader
     */
    public void setDefaultClassLoaderName(String loaderName)
    {
        defaultLoaderName = loaderName;
    }

    /**
     * Obtains the class loader for loading the specified class.
     *
     * @param clsName the class name
     * @param loaderRef the reference to the class loader
     * @return the class loader for loading this class
     * @throws IllegalArgumentException if the class name is <b>null</b>
     */
    private ClassLoader determineClassLoader(String clsName, String loaderRef)
    {
        return (isHandleInternalClasses() && clsName != null && clsName
                .startsWith(LIBRARY_CLASS_PREFIX)) ? getClass()
                .getClassLoader() : getClassLoader(loaderRef);
    }
}
