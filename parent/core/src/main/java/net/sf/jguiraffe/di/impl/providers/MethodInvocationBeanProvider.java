/*
 * Copyright 2006-2017 The JGUIraffe Team.
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
import net.sf.jguiraffe.di.impl.ClassDescription;
import net.sf.jguiraffe.di.impl.MethodInvocation;

/**
 * <p>
 * A specialized <code>BeanProvider</code> that creates beans by invoking a
 * method.
 * </p>
 * <p>
 * This <code>BeanProvider</code> implementation is initialized with a
 * {@link MethodInvocation} object describing the method to be invoked. Optional
 * a {@link Dependency} can be provided for the instance, on which the method is
 * to be invoked (if no such dependency is specified, the method to be invoked
 * must be static). This class is intended for the following use cases:
 * <ul>
 * <li>It can be used for obtaining instances that are not created using a
 * constructor, but by invoking a static factory method. In this case the passed
 * in <code>MethodInvocation</code> must refer to this factory method and must
 * provide the parameters for this invocation.</li>
 * <li>Another use case is the handling of <em>factory classes</em>: Some
 * objects are not directly created, but a method on a specific factory class is
 * used for this purpose. An example could be a <code>Connection</code> object
 * that is obtained from a <code>DataSource</code>. In this case the factory is
 * defined as a separate bean, and a dependency to this bean is specified to
 * this bean provider class. The <code>MethodInvocation</code> defines the
 * method of this factory bean that has to be called for obtaining an instance.</li>
 * </ul>
 * </p>
 * <p>
 * As is true for other <code>SimpleBeanProvider</code>s, this provider is
 * intended to be used together with a life-cycle-aware
 * <code>BeanProvider</code>. It does not provide any life-cycle support on its
 * own.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: MethodInvocationBeanProvider.java 205 2012-01-29 18:29:57Z oheger $
 */
public class MethodInvocationBeanProvider extends SimpleBeanProvider
{
    /** Stores the method invocation object. */
    private MethodInvocation invocation;

    /** Stores the target dependency. */
    private Dependency targetDependency;

    /** Stores a class description for the managed bean. */
    private ClassDescription beanClassDescription;

    /**
     * Creates a new instance of <code>MethodInvocationBeanProvider</code> and
     * initializes it with the dependency to the target object (on which the
     * method is to be invoked) and the description of the method invocation.
     *
     * @param targetBean the dependency to the target bean (can be <b>null</b>)
     * @param methodInv the description of the method to invoke (must not be
     * <b>null</b>)
     * @throws IllegalArgumentException if the <code>MethodInvocation</code>
     * is undefined
     */
    public MethodInvocationBeanProvider(Dependency targetBean,
            MethodInvocation methodInv)
    {
        this(targetBean, methodInv, null);
    }

    /**
     * Creates a new instance of <code>MethodInvocationBeanProvider</code> and
     * initializes it with the description of the method to invoke. No
     * dependency for the target instance is provided, so a static method must
     * be specified.
     *
     * @param methodInv the description of the method to invoke (must not be
     * <b>null</b>)
     * @throws IllegalArgumentException if the <code>MethodInvocation</code>
     * is undefined
     */
    public MethodInvocationBeanProvider(MethodInvocation methodInv)
    {
        this(null, methodInv);
    }

    /**
     * Creates a new instance of <code>MethodInvocationBeanProvider</code> and
     * initializes it with the dependency to the target object (on which the
     * method is to be invoked), the description of the method invocation, and
     * the class of the managed bean. Depending on the used dependency and/or
     * the method invocation, it is not always possible to determine the class
     * of the managed bean. With this constructor it can be explicitly set.
     *
     * @param targetBean the dependency to the target bean (can be <b>null</b>)
     * @param methodInv the description of the method to invoke (must not be
     * <b>null</b>)
     * @param beanClsDsc a description of the class of the managed bean (can be
     * <b>null</b>)
     * @throws IllegalArgumentException if the <code>MethodInvocation</code>
     * is undefined
     */
    public MethodInvocationBeanProvider(Dependency targetBean,
            MethodInvocation methodInv, ClassDescription beanClsDsc)
    {
        if (methodInv == null)
        {
            throw new IllegalArgumentException(
                    "Method invocation must not be null!");
        }
        invocation = methodInv;
        targetDependency = targetBean;
        beanClassDescription = beanClsDsc;
    }

    /**
     * Returns the dependency to the target bean. This can be <b>null</b> if
     * none is provided.
     *
     * @return the dependency to the target bean
     */
    public Dependency getTargetDependency()
    {
        return targetDependency;
    }

    /**
     * Returns the <code>MethodInvocation</code> for the method to be invoked.
     *
     * @return the method invocation
     */
    public MethodInvocation getInvocation()
    {
        return invocation;
    }

    /**
     * Returns the description of the class of the managed bean. If a class
     * description was explicitly set in the constructor, this description is
     * returned. Otherwise this implementation tries to obtain the bean class
     * from the {@link MethodInvocation} object owned by this provider. Because
     * a class is optional for a method invocation, result can be <b>null</b>.
     * To avoid this, a valid {@link ClassDescription} should always be set
     * either on the <code>MethodInvocation</code> or when an instance of this
     * class is constructed.
     *
     * @return a class description of the managed bean
     */
    public ClassDescription getBeanClassDescription()
    {
        return (beanClassDescription != null) ? beanClassDescription
                : getInvocation().getTargetClass();
    }

    /**
     * Returns the bean managed by this provider. If a target dependency is set,
     * the corresponding bean will be fetched from the specified dependency
     * provider and used as target instance for the method invocation. Otherwise
     * the method is invoked on a <b>null</b> instance, so this has to be a
     * static method.
     *
     * @param dependencyProvider the dependency provider
     * @return the bean managed by this provider
     */
    public Object getBean(DependencyProvider dependencyProvider)
    {
        Object target = (getTargetDependency() != null) ? dependencyProvider
                .getDependentBean(getTargetDependency()) : null;
        Object result = getInvocation().invoke(dependencyProvider, target);
        return (target != null) ? target : result;
    }

    /**
     * Returns the bean class of the bean managed by this provider. This
     * implementation delegates to <code>getBeanClassDescription()</code> for
     * obtaining the description of the bean class. If this is successful, the
     * class is resolved; otherwise result is <b>null</b>.
     *
     * @param dependencyProvider the dependency provider
     * @return the class of the managed bean
     * @see #getBeanClassDescription()
     */
    public Class<?> getBeanClass(DependencyProvider dependencyProvider)
    {
        ClassDescription cdesc = getBeanClassDescription();
        return (cdesc != null) ? cdesc.getTargetClass(dependencyProvider)
                : null;
    }

    /**
     * Returns the dependencies of this bean provider. These are the parameter
     * dependencies of the <code>MethodInvocation</code> object. If a
     * dependency for the target instance is provided, it will also be contained
     * in the returned set.
     *
     * @return a set with the dependencies of this provider
     */
    @Override
    public Set<Dependency> getDependencies()
    {
        Set<Dependency> result = new HashSet<Dependency>(getInvocation()
                .getParameterDependencies());
        if (getTargetDependency() != null)
        {
            result.add(getTargetDependency());
        }
        return result;
    }

    /**
     * Returns a string representation for this object. This string will contain
     * information about the method invoked by this bean provider. If a target
     * dependency is provided, it will also be output.
     *
     * @return a string for this object
     */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder(getClass().getName());
        buf.append('@').append(System.identityHashCode(this));
        buf.append("[ method = ").append(getInvocation());
        if (getTargetDependency() != null)
        {
            buf.append(" target = ").append(getTargetDependency());
        }
        return buf.toString();
    }
}
