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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.InjectionException;

/**
 * <p>
 * A class that represents a method invocation.
 * </p>
 * <p>
 * This class stores all data, which is needed for invoking a method; i.e. the
 * method name, the target class, optional information about the data types of
 * the method parameters, and the current parameter values to be passed to the
 * method.
 * </p>
 * <p>
 * Once initialized, an instance is immutable. So it can easily be shared
 * between multiple components and threads without having to care about
 * synchronization issues. The {@code invoke()} method actually executes
 * the corresponding method.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: MethodInvocation.java 207 2012-02-09 07:30:13Z oheger $
 */
public class MethodInvocation extends Invocation implements Invokable
{
    /** Stores the target dependency for this invocation. */
    private final Dependency targetDependency;

    /** Stores the name of the method to invoke. */
    private final String methodName;

    /** Stores a flag whether this is a static method invocation. */
    private final boolean staticInvocation;

    /**
     * Creates a new instance of {@code MethodInvocation} for non-static
     * method invocations. This constructor sets the target class to <b>null</b>,
     * so that it can only be derived from the target instance (which prohibits
     * static method invocations).
     *
     * @param methodName the name of the method to be invoked (must not be
     * <b>null</b>)
     * @param paramTypes an array with the parameter types
     * @param paramValues the current parameter values (defined as
     * {@code Dependency} objects); this array must not contain <b>null</b>
     * elements
     * @throws IllegalArgumentException if the length of the parameter types
     * array does not match the length of the parameter values array, or if the
     * values array contains <b>null</b> elements, or if the method name is
     * undefined
     */
    public MethodInvocation(String methodName, ClassDescription[] paramTypes,
            Dependency... paramValues)
    {
        this(null, methodName, paramTypes, paramValues);
    }

    /**
     * Creates a new instance of {@code MethodInvocation} and initializes
     * it with information about the method to invoke and the target class.
     *
     * @param targetClass the class, on which the method is to be invoked
     * @param methodName the name of the method to be invoked (must not be
     * <b>null</b>)
     * @param paramTypes an array with the parameter types
     * @param paramValues the current parameter values (defined as
     * {@code Dependency} objects); this array must not contain <b>null</b>
     * elements
     * @throws IllegalArgumentException if the length of the parameter types
     * array does not match the length of the parameter values array, or if the
     * values array contains <b>null</b> elements, or if the method name is
     * undefined
     * @see Invocation#Invocation(Class, Class[], Dependency...)
     */
    public MethodInvocation(ClassDescription targetClass, String methodName,
            ClassDescription[] paramTypes, Dependency... paramValues)
    {
        this(targetClass, methodName, false, paramTypes, paramValues);
    }

    /**
     * Creates a new instance of {@code MethodInvocation} and initializes most
     * of the properties. This constructor is appropriate for static or
     * non-static invocations which are performed on the target object passed to
     * the {@code invoke()} method.
     *
     * @param targetClass the class, on which the method is to be invoked
     * @param methodName the name of the method to be invoked (must not be
     * <b>null</b>)
     * @param isStatic determines whether a static method is to be invoked
     * @param paramTypes an array with the parameter types
     * @param paramValues the current parameter values (defined as
     * {@code Dependency} objects); this array must not contain <b>null</b>
     * elements
     * @throws IllegalArgumentException if the length of the parameter types
     * array does not match the length of the parameter values array, or if the
     * values array contains <b>null</b> elements, or if the method name is
     * undefined, or if the static flag is <b>true</b>, but no target class is
     * defined
     * @see Invocation#Invocation(Class, Class[], Dependency...)
     */
    public MethodInvocation(ClassDescription targetClass, String methodName,
            boolean isStatic, ClassDescription[] paramTypes,
            Dependency... paramValues)
    {
        this(targetClass, null, methodName, isStatic, paramTypes, paramValues);
    }

    /**
     * Creates a new instance of {@code MethodInvocation} and fully initializes
     * it. This constructor takes all information required for arbitrary method
     * invocations. It is especially possible to define a dependency for the
     * target object. If set, this dependency is resolved during invocation; a
     * target object is then ignored. Refer to the base class for a detailed
     * explanation of the arguments.
     *
     * @param targetClass the class, on which the method is to be invoked
     * @param targetDep an optional {@code Dependency} to the target bean on
     *        which the method should be invoked
     * @param methodName the name of the method to be invoked (must not be
     *        <b>null</b>)
     * @param isStatic determines whether a static method is to be invoked
     * @param paramTypes an array with the parameter types
     * @param paramValues the current parameter values (defined as
     *        {@code Dependency} objects); this array must not contain
     *        <b>null</b> elements
     * @throws IllegalArgumentException if the length of the parameter types
     *         array does not match the length of the parameter values array, or
     *         if the values array contains <b>null</b> elements, or if the
     *         method name is undefined, or if the static flag is <b>true</b>,
     *         but no target class is defined
     * @see Invocation#Invocation(Class, Class[], Dependency...)
     * @since 1.1
     */
    public MethodInvocation(ClassDescription targetClass, Dependency targetDep,
            String methodName, boolean isStatic, ClassDescription[] paramTypes,
            Dependency... paramValues)
    {
        super(targetClass, paramTypes, paramValues);
        if (methodName == null)
        {
            throw new IllegalArgumentException("Method name must not be null!");
        }
        if (targetClass == null && isStatic)
        {
            throw new IllegalArgumentException(
                    "Need a target class for a static invocation!");
        }

        targetDependency = targetDep;
        this.methodName = methodName;
        staticInvocation = isStatic;
    }

    /**
     * Returns the name of the method to be invoked.
     *
     * @return the method name
     */
    public String getMethodName()
    {
        return methodName;
    }

    /**
     * Returns the static flag. This flag indicates whether a static method is
     * to be invoked.
     *
     * @return the static invocation flag
     */
    public boolean isStaticInvocation()
    {
        return staticInvocation;
    }

    /**
     * Returns the target {@code Dependency} of this {@code MethodInvocation}.
     * This dependency defines the bean on which the method is to be invoked. If
     * there is no target dependency, result is <b>null</b>.
     *
     * @return the target {@code Dependency}
     * @since 1.1
     */
    public Dependency getTargetDependency()
    {
        return targetDependency;
    }

    /**
     * {@inheritDoc} This implementation adds the dependency to the invocation
     * target if it exists.
     */
    @Override
    public List<Dependency> getParameterDependencies()
    {
        List<Dependency> deps = super.getParameterDependencies();
        Dependency depTarget = getTargetDependency();
        if (depTarget == null)
        {
            return deps;
        }

        List<Dependency> result = new ArrayList<Dependency>(deps.size() + 1);
        result.addAll(deps);
        result.add(depTarget);
        return Collections.unmodifiableList(result);
    }

    /**
     * Invokes the corresponding method on the specified target instance. The
     * method's result is returned. The behavior of this method depends on the
     * {@link #isStaticInvocation()} flag. If it is set, a passed in target
     * object is ignored and a static method invocation on the target class is
     * performed. Otherwise, if a non <b>null</b> target object is passed in,
     * the target class is derived from this instance (an eventually set target
     * class is ignored).
     *
     * @param depProvider the dependency provider for resolving the parameters
     *        (must not be <b>null</b>)
     * @param target the target instance, on which to invoke the method
     * @return the method's return value
     * @throws InjectionException in case of an error
     * @throws IllegalArgumentException if the dependency provider is
     *         <b>null</b>
     */
    public Object invoke(DependencyProvider depProvider, Object target)
    {
        Object targetObject = resolveTarget(depProvider, target);
        if (targetObject == null && getTargetClass() == null)
        {
            throw new InjectionException(
                    "Target class and instance must not both be null!");
        }

        Object[] values = getResolvedParameters(depProvider);
        if (isStaticInvocation() || targetObject == null)
        {
            return depProvider.getInvocationHelper().invokeStaticMethod(
                    getTargetClass().getTargetClass(depProvider),
                    getMethodName(), getParameterClasses(depProvider), values);
        }
        else
        {
            return depProvider.getInvocationHelper().invokeInstanceMethod(
                    targetObject, getMethodName(),
                    getParameterClasses(depProvider), values);
        }
    }

    /**
     * Creates a string with additional information about this invocation. This
     * implementation will output the method name.
     *
     * @param buf the target buffer
     */
    @Override
    protected void invocationInfoToString(StringBuilder buf)
    {
        super.invocationInfoToString(buf);
        if (getTargetClass() != null)
        {
            buf.append('.');
        }
        buf.append(getMethodName());
    }

    /**
     * Resolves the target object of this method invocation. If a target
     * dependency is set, it is resolved, and the resulting object is used as
     * target.
     *
     * @param depProvider the {@code DependencyProvider}
     * @param target the target object passed to {@code invoke()}
     * @return the target object of the invocation
     * @throws IllegalArgumentException if the {@code DependencyProvider} is
     *         <b>null</b>
     */
    private Object resolveTarget(DependencyProvider depProvider, Object target)
    {
        Dependency dep = getTargetDependency();
        if (dep == null)
        {
            return target;
        }

        checkDependencyProvider(depProvider);
        return depProvider.getDependentBean(dep);
    }
}
