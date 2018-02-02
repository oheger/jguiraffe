/*
 * Copyright 2006-2018 The JGUIraffe Team.
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

import net.sf.jguiraffe.di.ClassLoaderProvider;

/**
 * <p>
 * A helper class for defining references to classes.
 * </p>
 * <p>
 * Handling of classes can become quite complex because of issues with different
 * class loaders. Because of that the dependency injection framework supports
 * multiple ways of defining classes:
 * <ul>
 * <li>by directly specifying <code>Class</code> objects; this can be used
 * when the class is already known at compile time</li>
 * <li>by providing only the class name; the class will then be resolved using
 * a default class loader</li>
 * <li>by providing a class name and a symbolic name for a class loader; this
 * works together with the class loader registration mechanism supported by
 * {@link net.sf.jguiraffe.di.BeanContext BeanContext}</li>
 * </ul>
 * </p>
 * <p>
 * Instances of this class encapsulate the different ways of specifying a class.
 * They can be initialized with different variants of class descriptions. The
 * <code>getTargetClass()</code> method can be used for obtaining a reference
 * to the wrapped class. Instances are thread-safe and can be shared between
 * multiple components. New instances are created using the static factory
 * methods.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ClassDescription.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ClassDescription
{
    /** Stores the resolved class. */
    private Class<?> targetClass;

    /** Stores the name of the target class. */
    private final String targetClassName;

    /** Stores the name of the class loader. */
    private final String classLoaderName;

    /**
     * Creates a new instance of <code>ClassDescription</code> and initializes
     * it. Clients call the static factory methods for creating new instances.
     *
     * @param cls the target class
     * @param clsName the name of the target class
     * @param loaderName the name of the class loader
     */
    ClassDescription(Class<?> cls, String clsName, String loaderName)
    {
        targetClass = cls;
        targetClassName = clsName;
        classLoaderName = loaderName;
    }

    /**
     * Returns the target class of this description. The passed in
     * {@code ClassLoaderProvider} is used for resolving the class if necessary.
     * This implementation will cache the <code>Class</code> objects when they
     * have been resolved.
     *
     * @param clProvider the {@code ClassLoaderProvider}
     * @return the target class of this description instance
     * @throws net.sf.jguiraffe.di.InjectionException if the class cannot be
     *         resolved
     * @throws IllegalArgumentException if the {@code ClassLoaderProvider} is
     *         needed, but is <b>null</b>
     */
    public synchronized Class<?> getTargetClass(ClassLoaderProvider clProvider)
    {
        if (targetClass == null)
        {
            if (clProvider == null)
            {
                throw new IllegalArgumentException(
                        "ClassLoaderProvider must not be null!");
            }
            targetClass = clProvider.loadClass(getTargetClassName(),
                    getClassLoaderName());
        }
        return targetClass;
    }

    /**
     * Returns the name of the target class of this description.
     *
     * @return the name of the target class
     */
    public String getTargetClassName()
    {
        return targetClassName;
    }

    /**
     * Returns the symbolic name of the class loader for resolving the class.
     * This can be <b>null</b> if the default class loader is to be used.
     *
     * @return the name of the class loader to use
     */
    public String getClassLoaderName()
    {
        return classLoaderName;
    }

    /**
     * Tests the passed in object for equality. Two objects of this class are
     * considered equal if they have the same target class name and class loader
     * name.
     *
     * @param obj the object to compare to
     * @return a flag whether the objects are equal
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof ClassDescription))
        {
            return false;
        }

        ClassDescription c = (ClassDescription) obj;
        return getTargetClassName().equals(c.getTargetClassName())
                && ((getClassLoaderName() == null) ? c.getClassLoaderName() == null
                        : getClassLoaderName().equals(c.getClassLoaderName()));
    }

    /**
     * Returns a hash code for this object.
     *
     * @return a hash code
     */
    @Override
    public int hashCode()
    {
        final int seed = 47;
        final int factor = 17;

        int result = seed;
        result = factor * result + getTargetClassName().hashCode();
        if (getClassLoaderName() != null)
        {
            result = factor * result + getClassLoaderName().hashCode();
        }
        return result;
    }

    /**
     * Returns a string representation of this object. This string will contain
     * the name of the target class. If a class loader name is specified, this
     * name will also be contained in the resulting string.
     *
     * @return a string for this object
     */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder(getClass().getName());
        buf.append('@').append(System.identityHashCode(this));
        buf.append("[ className = ").append(getTargetClassName());
        if (getClassLoaderName() != null)
        {
            buf.append(" classLoader = ").append(getClassLoaderName());
        }
        buf.append(" ]");
        return buf.toString();
    }

    /**
     * Returns an instance for the specified class. This method can be used when
     * the target class is already known at compile time.
     *
     * @param cls the target class
     * @return the description instance for this class
     */
    public static ClassDescription getInstance(Class<?> cls)
    {
        if (cls == null)
        {
            throw new IllegalArgumentException("Class must not be null!");
        }
        return new ClassDescription(cls, cls.getName(), null);
    }

    /**
     * Returns an instance for the specified class name and class loader name.
     * The target class will be resolved using reflection. It is loaded from the
     * class loader with the given symbolic name.
     *
     * @param clsName the name of the class
     * @param clsLoaderName the symbolic name of the class loader
     * @return the description instance for this class
     */
    public static ClassDescription getInstance(String clsName,
            String clsLoaderName)
    {
        if (clsName == null)
        {
            throw new IllegalArgumentException("Class name must not be null!");
        }
        return new ClassDescription(null, clsName, clsLoaderName);
    }

    /**
     * Returns an instance for the specified class name that will be resolved
     * using the default class loader.
     *
     * @param clsName the name of the class
     * @return the description instance for this class
     */
    public static ClassDescription getInstance(String clsName)
    {
        return getInstance(clsName, null);
    }
}
