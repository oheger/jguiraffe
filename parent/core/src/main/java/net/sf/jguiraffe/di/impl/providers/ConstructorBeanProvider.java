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
package net.sf.jguiraffe.di.impl.providers;

import java.util.HashSet;
import java.util.Set;

import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.impl.ConstructorInvocation;

/**
 * <p>
 * A simple bean provider that creates new bean instances by invoking a
 * constructor.
 * </p>
 * <p>
 * This <code>BeanProvider</code> class is initialized with a
 * {@link ConstructorInvocation} object. The
 * <code>getBean()</code> method will trigger this invocation object and
 * return the newly created instance.
 * </p>
 * <p>
 * The <code>ConstructorInvocation</code> also determines the dependencies of
 * this bean provider: these are the parameters to be passed to the constructor,
 * which can be arbitrary other beans defined in the current bean store.
 * </p>
 * <p>
 * A <code>ConstructorBeanProvider</code> is intended to be used together with
 * another life-cycle-aware bean provider. It will then be used for creating new
 * bean instances while its owning bean provider is responsible for further
 * initialization and life-cycle support.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ConstructorBeanProvider.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ConstructorBeanProvider extends SimpleBeanProvider
{
    /** Stores the invocation object. */
    private ConstructorInvocation invocation;

    /**
     * Creates a new instance of <code>ConstructorBeanProvider</code> and
     * initializes it with the constructor invocation to be called. This object
     * specifies the constructor to be invoked.
     *
     * @param ctorinv the <code>ConstructorInvocation</code> (must not be
     * <b>null</b>)
     * @throws IllegalArgumentException if the constructor invocation is
     * undefined
     */
    public ConstructorBeanProvider(ConstructorInvocation ctorinv)
    {
        if (ctorinv == null)
        {
            throw new IllegalArgumentException(
                    "Constructor invocation must not be null!");
        }
        invocation = ctorinv;
    }

    /**
     * Returns the <code>ConstructorInvocation</code> object that determines
     * the constructor to be invoked.
     *
     * @return the used constructor invocation
     */
    public ConstructorInvocation getInvocation()
    {
        return invocation;
    }

    /**
     * Returns the bean managed by this provider. This implementation will
     * invoke the specified constructor for creating the new bean instance.
     *
     * @param dependencyProvider the dependency provider
     * @return the newly created bean instance
     * @throws net.sf.jguiraffe.di.InjectionException if an error occurs when
     *         creating the bean
     */
    public Object getBean(DependencyProvider dependencyProvider)
    {
        return getInvocation().invoke(dependencyProvider);
    }

    /**
     * Returns the class of the managed bean. This class is also determined by
     * the constructor invocation.
     *
     * @param dependencyProvider the dependency provider
     * @return the class of the managed bean
     */
    public Class<?> getBeanClass(DependencyProvider dependencyProvider)
    {
        return getInvocation().getTargetClass().getTargetClass(
                dependencyProvider);
    }

    /**
     * Returns the dependencies of this bean provider. This implementation
     * returns the dependencies required for the constructor invocation (i.e.
     * the parameters to be passed to the constructor).
     *
     * @return a set with the dependencies of this bean provider
     */
    @Override
    public Set<Dependency> getDependencies()
    {
        return new HashSet<Dependency>(getInvocation()
                .getParameterDependencies());
    }

    /**
     * Returns a string representation of this object. This string also contains
     * information about the constructor that will be invoked.
     *
     * @return a string for this object
     */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder(getClass().getName());
        buf.append('@').append(System.identityHashCode(this));
        buf.append("[ ctor = ");
        buf.append(getInvocation()).append(" ]");
        return buf.toString();
    }
}
