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
package net.sf.jguiraffe.di.impl;

import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.InjectionException;

/**
 * <p>
 * An implementation of the <code>Dependency</code> interface that allows to
 * define a dependency based on the name of a bean.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: NameDependency.java 205 2012-01-29 18:29:57Z oheger $
 */
public final class NameDependency implements Dependency
{
    /** Stores the name of the dependent bean. */
    private String name;

    /**
     * Creates a new instance of <code>NameDependency</code> and sets the name
     * of the dependent bean. Clients create new instances using the static
     * factory method.
     *
     * @param name the name of the dependent bean
     */
    private NameDependency(String name)
    {
        this.name = name;
    }

    /**
     * Returns the name of the dependent bean.
     *
     * @return the name of the bean this dependency refers to
     */
    public String getName()
    {
        return name;
    }

    /**
     * Resolves the named dependency from the specified bean store. This is done
     * recursively for the bean store's parent if necessary.
     *
     * @param store the bean store
     * @param depProvider the dependency provider (not used here)
     * @return the found bean provider
     * @throws InjectionException if the provider cannot be resolved
     */
    public BeanProvider resolve(BeanStore store, DependencyProvider depProvider)
    {
        if (store == null)
        {
            throw new InjectionException("Cannot resolve named dependency: "
                    + getName());
        }

        BeanProvider result = store.getBeanProvider(getName());
        return (result != null) ? result : resolve(store.getParent(),
                depProvider);
    }

    /**
     * Tests whether this object equals another one. Two objects of this class
     * are considered equal if and only if they refer to the same dependent
     * bean.
     *
     * @param obj the object to compare to
     * @return a flag whether the objects are equal
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (!(obj instanceof NameDependency))
        {
            return false;
        }

        NameDependency c = (NameDependency) obj;
        return (getName() == null) ? c.getName() == null : getName().equals(
                c.getName());
    }

    /**
     * Returns a hash code for this object.
     *
     * @return a hash code
     */
    @Override
    public int hashCode()
    {
        final int seed = 17;
        final int factor = 23;
        int result = seed;
        if (getName() != null)
        {
            result = factor * result + getName().hashCode();
        }
        return result;
    }

    /**
     * Returns a string representation of this object. This string especially
     * contains the name of the bean this dependency is about.
     *
     * @return a string for this object
     */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder(getClass().getName());
        buf.append("[ name = ").append(getName()).append(" ]");
        return buf.toString();
    }

    /**
     * Returns an instance of this class for the bean with the specified name.
     *
     * @param name the name of the dependent bean
     * @return an instance referring to the specified dependent bean
     */
    public static NameDependency getInstance(String name)
    {
        return new NameDependency(name);
    }
}
