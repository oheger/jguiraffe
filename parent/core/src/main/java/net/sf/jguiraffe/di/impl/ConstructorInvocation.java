/*
 * Copyright 2006-2016 The JGUIraffe Team.
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

import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;

/**
 * <p>
 * A class that represents a constructor invocation.
 * </p>
 * <p>
 * Instances of this class can be used for creating objects by invoking
 * constructors on their classes. For this purpose the parameters to be passed
 * to the constructor must be specified, which is done in form of an array of
 * {@link Dependency} objects. These dependencies can either refer to other
 * beans (they will then be resolved by the dependency injection framework), or
 * can contain constant values that will be directly passed to the constructor.
 * If the constructor's signature is known, the types of its arguments can be
 * defined. If this is not the case, the parameter types will be derived from
 * the current parameter values. The
 * {@link net.sf.jguiraffe.di.InvocationHelper InvocationHelper} class is used
 * to find the correct constructor to be invoked.
 * </p>
 * <p>
 * Objects of this class cannot be changed after they have been created. So they
 * can be shared between multiple clients.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ConstructorInvocation.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ConstructorInvocation extends Invocation implements Invokable
{
    /**
     * Constant for the constructor name, which is added to the string
     * representation.
     */
    static final String CONSTR_NAME = ".<init>";

    /**
     * Creates a new instance of <code>ConstructorInvocation</code> and
     * initializes it. Refer to the base class for a detailed explanation of the
     * arguments.
     *
     * @param targetClass the class, on which the method is to be invoked (must
     * not be <b>null</b>)
     * @param paramTypes an array with the parameter types
     * @param paramValues the current parameter values (defined as
     * <code>Dependency</code> objects); this array must not contain <b>null</b>
     * elements
     * @throws IllegalArgumentException if the length of the parameter types
     * array does not match the length of the parameter values array, or if the
     * values array contains <b>null</b> elements, of if the target class is
     * <b>null</b>
     * @see Invocation#Invocation(Class, Class[], Dependency...)
     */
    public ConstructorInvocation(ClassDescription targetClass,
            ClassDescription[] paramTypes, Dependency... paramValues)
    {
        super(targetClass, paramTypes, paramValues);
        if (targetClass == null)
        {
            throw new IllegalArgumentException("Target class must not be null!");
        }
    }

    /**
     * Invokes the corresponding constructor on the specified target class and
     * returns the newly created instance. The required dependencies are
     * resolved using the given <code>DependencyProvider</code>.
     *
     * @param depProvider the dependency provider (must not be <b>null</b>)
     * @return the newly created instance of the target class
     * @throws net.sf.jguiraffe.di.InjectionException if an error occurs
     * @throws IllegalArgumentException if the dependency provider is <b>null</b>
     */
    public Object invoke(DependencyProvider depProvider)
    {
        Object[] values = getResolvedParameters(depProvider);
        return depProvider.getInvocationHelper().invokeConstructor(
                getTargetClass().getTargetClass(depProvider),
                getParameterClasses(depProvider), values);
    }

    /**
     * Performs the invocation. This method is required by the {@link Invokable}
     * interface. It delegates to the method with the same name, ignoring the
     * <code>target</code> parameter (which is not needed for a constructor
     * invocation).
     *
     * @param depProvider the dependency provider (must not be <b>null</b>)
     * @param target the target of the invocation
     * @return the result of the invocation
     * @throws net.sf.jguiraffe.di.InjectionException if an error occurs
     * @throws IllegalArgumentException if the dependency provider is
     *         <b>null</b>
     */
    public Object invoke(DependencyProvider depProvider, Object target)
    {
        return invoke(depProvider);
    }

    /**
     * Adds additional information about this invocation to the string buffer.
     * This implementation adds a name for the constructor.
     *
     * @param buf the target buffer
     */
    @Override
    protected void invocationInfoToString(StringBuilder buf)
    {
        super.invocationInfoToString(buf);
        buf.append(CONSTR_NAME);
    }
}
