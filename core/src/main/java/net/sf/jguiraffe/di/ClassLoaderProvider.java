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
package net.sf.jguiraffe.di;

import java.util.Set;

/**
 * <p>
 * Definition of an interface for objects that act as a registry for class
 * loaders.
 * </p>
 * <p>
 * In complex environments different class loaders can play an important role.
 * Thus the dependency injection framework supports the registration of
 * arbitrary class loaders; they are assigned a symbolic name. When a class is
 * loaded, a class loader can be specified by the symbolic name it was
 * registered.
 * </p>
 * <p>
 * This interface provides access to the registered class loaders by their
 * symbolic name. It can be used by components that need to load classes per
 * reflection.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ClassLoaderProvider.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface ClassLoaderProvider
{
    /**
     * Constant for the reserved name of the context class loader. If this
     * constant is passed into the <code>getClassLoader()</code> method, the
     * context class loader will automatically be used.
     */
    String CONTEXT_CLASS_LOADER = "CONTEXT";

    /**
     * Returns a reference to the class loader with the specified symbolic name.
     * The passed in name can be <b>null</b>, then a default class loader will
     * be returned. The constant <code>CONTEXT_CLASS_LOADER</code> can be
     * passed in, too, for obtaining a reference to the context class loader. In
     * all other cases the parameter is interpreted as the symbolic name of a
     * registered class loader. If no class loader is found under this name, an
     * exception will be thrown.
     *
     * @param name the name of the desired class loader
     * @return the class loader for this name
     * @throws InjectionException if the class loader cannot be resolved
     */
    ClassLoader getClassLoader(String name);

    /**
     * Returns the class specified by the given name. This is a convenience
     * method that obtains the class loader specified by its symbolic name and
     * immediately uses it for loading a class. The same naming conventions
     * apply as described for the {@link #getClassLoader(String)} method.
     * Occurring exceptions are re-thrown as runtime exceptions.
     *
     * @param name the name of the class to be loaded (must not be <b>null</b>)
     * @param loaderRef the name, under which the desired class loader is
     *        registered (<b>null</b> for the default class loader)
     * @return the loaded class
     * @throws InjectionException if the class cannot be loaded
     */
    Class<?> loadClass(String name, String loaderRef);

    /**
     * Registers a <code>ClassLoader</code> under the given name. This class
     * loader can later be accessed using the <code>getClassLoader()</code>
     * method. If the class loader reference is <b>null</b>, the class loader
     * with the given name will be unregistered.
     *
     * @param name a symbolic name for the class loader (must not be <b>null</b>)
     * @param loader the class loader to be registered
     * @throws IllegalArgumentException if the name is undefined
     */
    void registerClassLoader(String name, ClassLoader loader);

    /**
     * Returns the name of the default class loader.
     *
     * @return the name of the default class loader
     * @see #setDefaultClassLoader(ClassLoader)
     */
    String getDefaultClassLoaderName();

    /**
     * Sets the name of the default class loader. It is possible to mark one of
     * the registered class loaders as default class loader by passing its name
     * to this method. Then this class loader will be used for all dynamic class
     * loading operations for which no specific class loader name is specified.
     * If this property was not set or the default class loader name is
     * <b>null</b>, an implementation is free to return an arbitrary class
     * loader - probably the one that loaded the implementation class.
     *
     * @param loaderName the name of the new default class loader
     */
    void setDefaultClassLoaderName(String loaderName);

    /**
     * Returns a set with the names of all registered class loaders.
     *
     * @return the names of all registered class loaders
     */
    Set<String> classLoaderNames();
}
