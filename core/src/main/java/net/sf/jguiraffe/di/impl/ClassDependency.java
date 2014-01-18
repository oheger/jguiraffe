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
package net.sf.jguiraffe.di.impl;

import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.InjectionException;

/**
 * <p>
 * An implementation of the <code>Dependency</code> interface that allows to
 * define a dependency based on a bean class.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ClassDependency.java 205 2012-01-29 18:29:57Z oheger $
 */
public final class ClassDependency implements Dependency
{
    /** Stores the class description of this dependency. */
    private ClassDescription dependentClass;

    /**
     * Creates a new instance of <code>ClassDependency</code> and sets the
     * dependent class. Clients use the static <code>getInstance()</code>
     * method for obtaining instances of this class.
     *
     * @param cls the description of the dependent class
     */
    private ClassDependency(ClassDescription cls)
    {
        dependentClass = cls;
    }

    /**
     * Returns the description of the class of this dependency.
     *
     * @return the class of this dependency
     */
    public ClassDescription getDependentClass()
    {
        return dependentClass;
    }

    /**
     * Resolves this dependency on the specified bean store. This implementation
     * iterates over the beans defined in the store. If a bean provider is found
     * whose bean class is either the same or a sub class of the class defined
     * in this dependency, it is returned. Otherwise the parent bean store will
     * be searched recursively.
     *
     * @param store the bean store to be searched
     * @param depProvider the dependency provider
     * @return the found bean provider
     * @throws InjectionException if no fitting bean provider can be found
     */
    public BeanProvider resolve(BeanStore store, DependencyProvider depProvider)
    {
        if (store == null)
        {
            throw new InjectionException(
                    "Could not resolve dependency for class "
                            + getDependentClass());
        }

        Class<?> depClass = getDependentClass().getTargetClass(depProvider);
        for (String n : store.providerNames())
        {
            BeanProvider provider = store.getBeanProvider(n);
            if (depClass.isAssignableFrom(
                    provider.getBeanClass(depProvider)))
            {
                return provider;
            }
        }

        // recursive call for parent store
        return resolve(store.getParent(), depProvider);
    }

    /**
     * Compares this object with another one. Two class dependencies are
     * considered equal if and only if they refer to the same class.
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
        if (!(obj instanceof ClassDependency))
        {
            return false;
        }
        ClassDependency c = (ClassDependency) obj;
        return getDependentClass().equals(c.getDependentClass());
    }

    /**
     * Returns a hash code for this object.
     *
     * @return a hash code
     */
    @Override
    public int hashCode()
    {
        return getDependentClass().hashCode();
    }

    /**
     * Returns a string representation of this object. This string will contain
     * the name of the class referred to by this dependency.
     *
     * @return a string for this object
     */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder(getClass().getName());
        buf.append("[ class = ").append(getDependentClass());
        buf.append(" ]");
        return buf.toString();
    }

    /**
     * Returns a <code>ClassDependency</code> instance for the specified
     * class.
     *
     * @param cls the class of the dependency (must not be <b>null</b>)
     * @return an instance pointing to the specified class
     * @throws IllegalArgumentException if the class is <b>null</b>
     */
    public static ClassDependency getInstance(Class<?> cls)
    {
        return getInstance((cls != null) ? ClassDescription.getInstance(cls)
                : null);
    }

    /**
     * Returns a <code>ClassDependency</code> instance for the specified
     * <code>ClassDescription</code>.
     *
     * @param clsdsc the class description
     * @return an instance pointing to the specified class
     * @throws IllegalArgumentException if the class description is <b>null</b>
     */
    public static ClassDependency getInstance(ClassDescription clsdsc)
    {
        if (clsdsc == null)
        {
            throw new IllegalArgumentException(
                    "Dependent class must not be null!");
        }
        return new ClassDependency(clsdsc);
    }
}
