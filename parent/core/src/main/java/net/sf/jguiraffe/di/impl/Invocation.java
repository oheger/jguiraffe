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

import java.util.Arrays;
import java.util.List;

import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;

/**
 * <p>
 * A base class for (method or constructor) invocations.
 * </p>
 * <p>
 * This class allows the definition of important data that is required for
 * invoking a method using reflection. Especially the parameter types and the
 * parameter values can be specified. The parameter values are provided as
 * {@link Dependency} objects, so they can refer to other beans defined in a
 * {@code BeanStore}.
 * </p>
 * <p>
 * The main use case for {@code Invocation} objects is the creation and
 * initialization of beans performed by the dependency injection framework: At
 * first a bean has to be created by invoking one of its constructors. After
 * that some initialization methods may be called. In both cases the parameters
 * for the calls have to be specified (which can be either constant values or
 * references to other beans).
 * </p>
 * <p>
 * This base provides common functionality related to the management of the
 * invocation parameters. There will be concrete sub classes implementing
 * specific invocations of methods or constructors.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: Invocation.java 207 2012-02-09 07:30:13Z oheger $
 */
public class Invocation
{
    /** Constant for the initial string buffer size. */
    private static final int BUF_SIZE = 128;

    /** Stores the parameter types. */
    private final ClassDescription[] parameterTypes;

    /** Stores a list with the dependencies for the parameters. */
    private final List<Dependency> parameterDependencies;

    /** Stores the target class of the invocation. */
    private final ClassDescription targetClass;

    /**
     * Creates a new instance of {@code Invocation} and initializes it
     * with information about the call parameters. To perform an invocation the
     * class has to know the current parameter values and (at least partly) the
     * data types of these values. From this information the signature of the
     * method to call is derived. The array with the parameter types can have
     * <b>null</b> elements if the corresponding parameter types are unknown;
     * it can even be <b>null</b> at all if no information about data types is
     * available. If it is not <b>null</b>, its length must be the same as the
     * length of the array with the parameter values.
     *
     * @param targetClass description of the class, on which the method is to be
     * invoked
     * @param paramTypes an array with the parameter type descriptions
     * @param paramValues the current parameter values (defined as
     * {@code Dependency} objects); this array must not contain <b>null</b>
     * elements
     * @throws IllegalArgumentException if the length of the parameter types
     * array does not match the length of the parameter values array or if the
     * values array contains <b>null</b> elements
     */
    protected Invocation(ClassDescription targetClass,
            ClassDescription[] paramTypes, Dependency... paramValues)
    {
        parameterTypes = initParameterTypes(paramTypes, paramValues);
        parameterDependencies = initDependencies(paramValues);
        this.targetClass = targetClass;
    }

    /**
     * Returns the target class of this invocation. This is the class, on which
     * the method or constructor is to be invoked. A target class may not be
     * required for all cases, e.g. for non-static method invocations it can be
     * determined from the target instance.
     *
     * @return the target class of the invocation
     */
    public ClassDescription getTargetClass()
    {
        return targetClass;
    }

    /**
     * Returns an array with the types of the parameters of the invocation. Note
     * that the array returned here is never <b>null</b>, even if <b>null</b>
     * was passed to the constructor. The returned array may contain <b>null</b>
     * elements for parameters where the type information is lacking.
     *
     * @return an array with the parameter types
     */
    public ClassDescription[] getParameterTypes()
    {
        return parameterTypes.clone();
    }

    /**
     * Returns the {@code Dependency} objects defining the current
     * parameter values.
     *
     * @return a list with the {@code Dependency} objects for the
     * parameter values
     */
    public List<Dependency> getParameterDependencies()
    {
        return parameterDependencies;
    }

    /**
     * Returns a flag whether the data types of all method parameters are known.
     * If this is the case, the signature of the method to be called can be
     * exactly specified. Otherwise the signature has to be derived from the
     * data types of the current parameters.
     *
     * @return a flag whether information about the parameter types is complete
     */
    public boolean isTypeInfoComplete()
    {
        for (ClassDescription type : parameterTypes)
        {
            if (type == null)
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns an array with the resolved parameters. This method iterates over
     * the parameter dependencies and tries to resolve them using the specified
     * {@code DependencyProvider}. An array with the resulting beans is
     * returned. If no parameters are specified (i.e. for invocations of methods
     * that do not have arguments), the return value is <b>null</b>.
     *
     * @param depProvider the dependency provider (must not be <b>null</b>)
     * @return an array with the resolved parameter values (can be <b>null</b>)
     * @throws net.sf.jguiraffe.di.InjectionException if a dependency cannot be
     *         resolved
     * @throws IllegalArgumentException if the passed in dependency provider is
     *         <b>null</b>
     */
    public Object[] getResolvedParameters(DependencyProvider depProvider)
    {
        if (parameterDependencies.isEmpty())
        {
            return null;
        }
        checkDependencyProvider(depProvider);

        Object[] values = new Object[parameterDependencies.size()];
        int idx = 0;
        for (Dependency d : parameterDependencies)
        {
            values[idx++] = depProvider.getDependentBean(d);
        }
        return values;
    }

    /**
     * Returns an array with the concrete parameter classes. This method
     * converts the internally stored {@code ClassDescription} objects into
     * {@code Class} objects.
     *
     * @param depProvider the dependency provider for resolving the classes
     * @return an array with the parameter classes
     * @throws net.sf.jguiraffe.di.InjectionException if a class cannot be
     *         resolved
     */
    public Class<?>[] getParameterClasses(DependencyProvider depProvider)
    {
        ClassDescription[] descs = parameterTypes;
        Class<?>[] result = new Class<?>[descs.length];
        for (int i = 0; i < descs.length; i++)
        {
            if (descs[i] != null)
            {
                result[i] = descs[i].getTargetClass(depProvider);
            }
        }

        return result;
    }

    /**
     * Returns a string representation of this object. The returned string will
     * contain information about this invocation (including the class name, the
     * target class name, the parameters, and further information provided by
     * sub classes). This implementation will print the concrete class name,
     * followed by an opening square bracket. Then
     * {@code invocationInfoToString()}, and
     * {@code parametersToString()} are called. Finally a closing square
     * bracket is output.
     *
     * @return a string for this object
     */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder(BUF_SIZE);
        buf.append(getClass().getName());
        buf.append('@').append(System.identityHashCode(this));
        buf.append("[ ");
        invocationInfoToString(buf);
        parametersToString(buf);
        buf.append(']');
        return buf.toString();
    }

    /**
     * Creates a string with additional information about this invocation. This
     * method is called by the default {@code toString()} implementation.
     * It adds the target class to the buffer if it is defined.
     *
     * @param buf the target buffer
     */
    protected void invocationInfoToString(StringBuilder buf)
    {
        if (getTargetClass() != null)
        {
            buf.append(getTargetClass());
        }
    }

    /**
     * Creates a string representation of the current parameter values. This
     * implementation iterates over all parameter dependencies and invokes their
     * {@code toString()} method. It is called by the
     * {@code toString()} method.
     *
     * @param buf the target buffer
     */
    protected void parametersToString(StringBuilder buf)
    {
        buf.append('(');
        boolean first = true;
        for (Dependency d : parameterDependencies)
        {
            if (first)
            {
                first = false;
            }
            else
            {
                buf.append(", ");
            }
            buf.append(d);
        }
        buf.append(')');
    }

    /**
     * Checks whether a valid {@code DependencyProvider} has been specified.
     *
     * @param depProvider the provider to be checked
     * @throws IllegalArgumentException if the {@code DependencyProvider} is
     *         undefined
     * @since 1.1
     */
    protected static void checkDependencyProvider(DependencyProvider depProvider)
    {
        if (depProvider == null)
        {
            throw new IllegalArgumentException(
                    "Dependency provider must not be null!");
        }
    }

    /**
     * Initializes the array with the parameter types. <b>null</b> may have
     * been passed in, which has to be converted to a valid value.
     *
     * @param types the array with the parameter types
     * @param deps the parameter dependencies
     * @return the final array with the types of the parameters
     * @throws IllegalArgumentException if the lengths of the arrays are
     * incompatible
     */
    private ClassDescription[] initParameterTypes(ClassDescription[] types,
            Dependency... deps)
    {
        if (types == null)
        {
            return new ClassDescription[deps.length];
        }
        else
        {
            if (types.length != deps.length)
            {
                throw new IllegalArgumentException(
                        "If the parameter types array is defined, "
                                + "its length must be the same as the values array!");
            }

            ClassDescription[] result = new ClassDescription[types.length];
            System.arraycopy(types, 0, result, 0, types.length);
            return result;
        }
    }

    /**
     * Initializes the list with the dependencies based on the constructor
     * argument. The dependencies are also checked for validity.
     *
     * @param deps the dependencies
     * @return the list with the dependencies
     * @throws IllegalArgumentException if an undefined dependency is specified
     */
    private List<Dependency> initDependencies(Dependency... deps)
    {
        for (Dependency d : deps)
        {
            if (d == null)
            {
                throw new IllegalArgumentException(
                        "Parameter dependency must not be null!");
            }
        }
        return Arrays.asList(deps);
    }
}
