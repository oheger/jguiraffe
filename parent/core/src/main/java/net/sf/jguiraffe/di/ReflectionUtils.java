/*
 * Copyright 2006-2025 The JGUIraffe Team.
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ClassUtils;

/**
 * <p>
 * An utility class that provides some functionality related to reflection and
 * dependency injection.
 * </p>
 * <p>
 * This class implements some basic functionality that is needed by other parts
 * of the dependency injection package. It especially deals with row reflection
 * calls and exception handling. It is not intended to be used directly by
 * applications using this framework. It will be called under the hood.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ReflectionUtils.java 205 2012-01-29 18:29:57Z oheger $
 */
public final class ReflectionUtils
{
    /**
     * A private constructor, so that no instances can be created.
     */
    private ReflectionUtils()
    {
        // empty
    }

    /**
     * Loads the class with the specified name using the given class loader.
     * This is a thin wrapper over the <code>Class.forName()</code> method.
     * <code>ClassNotFoundException</code> exceptions are caught and re-thrown
     * as <code>InjectionException</code> exceptions.
     *
     * @param className the name of the class to be loaded
     * @param loader the class loader to use
     * @return the loaded class
     * @throws IllegalArgumentException if the class name or the class loader is
     * undefined
     * @throws InjectionException if the class cannot be resolved
     */
    public static Class<?> loadClass(String className, ClassLoader loader)
    {
        if (className == null)
        {
            throw new IllegalArgumentException("Class name must not be null!");
        }
        if (loader == null)
        {
            throw new IllegalArgumentException("Class loader must not be null!");
        }

        try
        {
            return ClassUtils.getClass(loader, className);
        }
        catch (ClassNotFoundException cnfex)
        {
            throw new InjectionException(cnfex);
        }
    }

    /**
     * Helper method for invoking a method using reflection. This method catches
     * the variety of possible exceptions and re-throws them as runtime
     * exceptions.
     *
     * @param method the method to be invoked
     * @param target the target object
     * @param args the arguments of the method
     * @return the return value of the method
     * @throws InjectionException if invoking the method causes an error
     * @throws IllegalArgumentException if the method object is <b>null</b>
     */
    public static Object invokeMethod(Method method, Object target,
            Object... args)
    {
        if (method == null)
        {
            throw new IllegalArgumentException("Method must not be null!");
        }

        try
        {
            return method.invoke(target, args);
        }
        catch (Exception ex)
        {
            // catch all related exceptions
            throw new InjectionException("Exception when invoking method "
                    + method.getName(), ex);
        }
    }

    /**
     * Creates an object by invoking the specified constructor with the given
     * arguments. Like <code>invokeMethod()</code>, this is a helper method
     * that deals with all possible exceptions and redirects them as
     * <code>InjectionException</code>s.
     *
     * @param <T> the type of the constructor
     * @param ctor the constructor to be invoked (must not be <b>null</b>)
     * @param args the arguments to be passed to the constructor
     * @return the newly created instance
     * @throws InjectionException if construction of the object fails
     * @throws IllegalArgumentException if the constructor object is <b>null</b>
     */
    public static <T> T invokeConstructor(Constructor<T> ctor, Object... args)
    {
        if (ctor == null)
        {
            throw new IllegalArgumentException("Constructor must not be null!");
        }

        try
        {
            return ctor.newInstance(args);
        }
        catch (Exception ex)
        {
            // redirect all exceptions
            throw new InjectionException("Exception when invoking constructor "
                    + ctor, ex);
        }
    }

    /**
     * Finds all methods matching the given search criteria. With this method a
     * list of the methods of the specified target class can be obtained that
     * have the given name and are compatible with the given parameter types.
     * Wild cards are supported as follows:
     * <ul>
     * <li>The method name can be <b>null</b>, then only parameter types are
     * compared.</li>
     * <li>Each element of the {@code paramTypes} can be <b>null</b>, then
     * arbitrary parameter types are accepted at this place.</li>
     * <li>The whole {@code paramTypes} array can be <b>null</b>, then all
     * methods with the given name and arbitrary signature are accepted.</li>
     * </ul>
     * The {@code exactTypeMatch} parameter controls how parameter types are
     * compared: If set to <b>true</b> the parameter types must match exactly;
     * otherwise, the parameters of the method in the target class can be super
     * classes of the provided parameter types. So this method allows searching
     * for methods of a given class in a flexible way including the following
     * use cases:
     * <ul>
     * <li>Search for all methods with a given name: just pass in <b>null</b>
     * for the parameter types array.</li>
     * <li>Search for all methods with a given signature: pass in <b>null</b>
     * for the method name and specify a corresponding array with parameter
     * types.</li>
     * <li>Search for methods with a given name and a partly known signature:
     * here the name of the method and a type array with all known types set has
     * to be passed in.</li>
     * </ul>
     *
     * @param targetClass the target class (must not be <b>null</b>)
     * @param methodName the name of the method to be searched for
     * @param paramTypes an array with the parameter types
     * @param exactTypeMatch a flag whether parameter types should be matched
     *        exactly
     * @return a list with methods matching the search criteria
     * @throws IllegalArgumentException if the target class is <b>null</b>
     */
    public static List<Method> findMethods(Class<?> targetClass,
            String methodName, Class<?>[] paramTypes, boolean exactTypeMatch)
    {
        checkTargetClass(targetClass);
        List<Method> methods = new LinkedList<Method>();

        for (Method m : targetClass.getMethods())
        {
            if (methodName == null || methodName.equals(m.getName()))
            {
                if (isSignatureCompatible(m.getParameterTypes(), paramTypes,
                        exactTypeMatch))
                {
                    methods.add(m);
                }
            }
        }

        return methods;
    }

    /**
     * Removes duplicate methods from the specified list. When calling
     * {@link #findMethods(Class, String, Class[], boolean)} it is possible that
     * the resulting list contains multiple methods with the same name and
     * signature, but with a different result type. This is the case for
     * instance if a class overrides a super class method with a covariant
     * return type. With this method such duplicates can be eliminated. It
     * searches for methods with the same name and signature and drops all of
     * them except for the one with the most specific return type. The result of
     * the method depends on the operations performed: If no duplicates have
     * been found, the same list is returned without changes. Otherwise, a new
     * list is created and returned.
     *
     * @param methods the list with methods to be checked (must not be
     *        <b>null</b>
     * @return a list with duplicates removed
     * @throws IllegalArgumentException if the passed in list is <b>null</b> or
     *         contain <b>null</b> elements
     */
    public static List<Method> removeCovariantDuplicates(List<Method> methods)
    {
        if (methods == null)
        {
            throw new IllegalArgumentException("Method list must not be null!");
        }
        if (methods.size() < 1)
        {
            // there can't be duplicates
            return methods;
        }

        Map<Signature, Method> methodMap = new HashMap<Signature, Method>();
        for (Method m : methods)
        {
            Signature sig = new Signature(m);
            Method m2 = methodMap.get(sig);
            if (m2 == null
                    || m2.getReturnType().isAssignableFrom(m.getReturnType()))
            {
                methodMap.put(sig, m);
            }
        }

        if (methodMap.size() == methods.size())
        {
            // no changes, return input list
            return methods;
        }
        else
        {
            return new ArrayList<Method>(methodMap.values());
        }
    }

    /**
     * Finds all constructors matching the specified search criteria. This
     * method works like {@link #findMethods(Class, String, Class[], boolean)},
     * but deals with constructors of the target class.
     *
     * @param <T> the type of the constructor
     * @param targetClass the target class (must not be <b>null</b>)
     * @param paramTypes an array with the parameter types
     * @param exactTypeMatch a flag whether parameter types should be matched
     *        exactly
     * @return a list with constructors matching the search criteria
     * @throws IllegalArgumentException if the target class is <b>null</b>
     */
    public static <T> List<Constructor<T>> findConstructors(
            Class<T> targetClass, Class<?>[] paramTypes, boolean exactTypeMatch)
    {
        checkTargetClass(targetClass);
        List<Constructor<T>> constr = new LinkedList<Constructor<T>>();

        for (Constructor<?> c : targetClass.getConstructors())
        {
            if (isSignatureCompatible(c.getParameterTypes(), paramTypes,
                    exactTypeMatch))
            {
                // should be a constructor of the target class
                @SuppressWarnings("unchecked")
                Constructor<T> ctor = (Constructor<T>) c;
                constr.add(ctor);
            }
        }

        return constr;
    }

    /**
     * Returns the value of the specified property from the given bean.
     *
     * @param bean the bean
     * @param name the name of the property to retrieve
     * @return the value of this property
     * @throws InjectionException if accessing the property fails
     * @throws IllegalArgumentException if invalid parameters are specified
     */
    public static Object getProperty(Object bean, String name)
    {
        try
        {
            return PropertyUtils.getProperty(bean, name);
        }
        catch (IllegalArgumentException iex)
        {
            throw iex;
        }
        catch (Exception ex)
        {
            // redirect all reflection exceptions
            throw new InjectionException("Error when accessing property "
                    + name, ex);
        }
    }

    /**
     * Sets the value of a property for the specified bean. The given value will
     * be directly written into the property, without performing any type
     * conversions. Occurring exceptions will be re-thrown as
     * <code>InjectionException</code>s.
     *
     * @param bean the bean, on which to set the property
     * @param name the name of the property to be set
     * @param value the new value of the property
     * @throws InjectionException if an error occurs when setting the property
     * @throws IllegalArgumentException if invalid parameters are passed in
     */
    public static void setProperty(Object bean, String name, Object value)
    {
        try
        {
            PropertyUtils.setProperty(bean, name, value);
        }
        catch (IllegalArgumentException iex)
        {
            throw iex;
        }
        catch (Exception ex)
        {
            // handle all reflection exceptions the same way
            throw new InjectionException("Error when setting property " + name,
                    ex);
        }
    }

    /**
     * Tests whether the specified actual parameter value can be assigned to a
     * parameter of the given type.
     *
     * @param paramClass the parameter type
     * @param param the current parameter value
     * @return a flag whether this assignment is possible
     */
    static boolean isParamAssignable(Class<?> paramClass, Object param)
    {
        if (paramClass == null)
        {
            throw new IllegalArgumentException(
                    "Parameter class must not be null!");
        }

        if (param == null && paramClass.isPrimitive())
        {
            // cannot assign null to a primitive parameter
            return false;
        }
        Class<?> valueClass = (param != null) ? param.getClass() : null;
        return isParameterCompatible(paramClass, valueClass, false);
    }

    /**
     * Tests whether the specified value objects match the given parameter
     * types. With this method the signature of a method can be tested for
     * compatibility with a set of parameter objects.
     *
     * @param parameterTypes the parameter types to be checked
     * @param definedTypes an array with defined parameter types; all defined
     * elements in this array must be exactly the same as in the parameterTypes
     * array
     * @param values an array with the current parameter values
     * @return a flag whether the method signature is compatible with the
     * parameter values
     */
    static boolean matchParameterTypes(Class<?>[] parameterTypes,
            Class<?>[] definedTypes, Object[] values)
    {
        assert parameterTypes != null && definedTypes != null && values != null
            : "Input parameters are null";
        assert definedTypes.length == values.length : "Invalid array lengths";

        if (parameterTypes.length != values.length)
        {
            return false;
        }
        for (int i = 0; i < values.length; i++)
        {
            if (definedTypes[i] != null)
            {
                if (!definedTypes[i].equals(parameterTypes[i]))
                {
                    return false;
                }
            }
            else
            {
                if (!isParamAssignable(parameterTypes[i], values[i]))
                {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Tests whether a method signature matches search criteria. This method is
     * used by methods for finding specific methods or constructors. All wild
     * cards supported by search criteria for signatures are supported.
     *
     * @param methodTypes the parameter types of a method in question
     * @param callTypes the search criteria for method types
     * @param exactMatch the exact match flag
     * @return a flag whether the parameters match the criteria
     */
    private static boolean isSignatureCompatible(Class<?>[] methodTypes,
            Class<?>[] callTypes, boolean exactMatch)
    {
        if (callTypes == null)
        {
            // wild card for whole signature
            return true;
        }

        if (methodTypes.length != callTypes.length)
        {
            return false;
        }

        for (int i = 0; i < methodTypes.length; i++)
        {
            if (!isParameterCompatible(methodTypes[i], callTypes[i], exactMatch))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Tests whether an argument class is compatible with the parameter class of
     * a method. This method compares the classes either exactly or checks
     * whether they are assignment compatible. Primitive types and wrappers are
     * also handled correctly.
     *
     * @param methodParamCls the class of the method parameter
     * @param argCls the class of the argument
     * @param exactMatch the exact match flag
     * @return a flag whether the parameter is compatible
     */
    private static boolean isParameterCompatible(Class<?> methodParamCls,
            Class<?> argCls, boolean exactMatch)
    {
        if (argCls == null)
        {
            return true;
        }

        if (compareParameterClasses(methodParamCls, argCls, exactMatch))
        {
            return true;
        }

        // Check for unboxing conversions of wrapper types
        if (methodParamCls.isPrimitive())
        {
            Class<?> unboxedCls = wrapperToPrimitive(argCls);
            if (unboxedCls != null
                    && ClassUtils.isAssignable(unboxedCls, methodParamCls))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Helper method for comparing the classes of a method parameter. Depending
     * on the exact match flag either a strict comparison or a test for
     * assignment compatibility is performed.
     *
     * @param methodParamCls the class of the method parameter
     * @param argCls the class of the argument
     * @param exactMatch the exact match flag
     * @return a flag whether the classes are compatible
     */
    private static boolean compareParameterClasses(Class<?> methodParamCls,
            Class<?> argCls, boolean exactMatch)
    {
        return exactMatch ? methodParamCls.equals(argCls) : ClassUtils
                .isAssignable(argCls, methodParamCls);
    }

    /**
     * Tests whether the target class parameter is valid. Throws an exception if
     * not.
     *
     * @param targetClass the target class
     */
    private static void checkTargetClass(Class<?> targetClass)
    {
        if (targetClass == null)
        {
            throw new IllegalArgumentException("Target class must not be null!");
        }
    }

    /**
     * Returns the corresponding primitive type to a wrapper class. If the
     * passed in class is not a wrapper class, <b>null</b> is returned.
     *
     * @param wrapperCls the wrapper class
     * @return the corresponding primitive type
     */
    private static Class<?> wrapperToPrimitive(Class<?> wrapperCls)
    {
        return ClassUtils.wrapperToPrimitive(wrapperCls);
    }

    /**
     * An internally used helper class representing a method signature.
     */
    private static class Signature
    {
        /** The name of the method. */
        private final String methodName;

        /** The parameter types of the method. */
        private final Class<?>[] parameterTypes;

        /**
         * Creates a new instance of {@code Signature} and initializes it from
         * the given {@code Method} instance.
         *
         * @param m the represented method instance
         * @throws IllegalArgumentException if the method is <b>null</b>
         */
        public Signature(Method m)
        {
            if (m == null)
            {
                throw new IllegalArgumentException("Method must not be null!");
            }
            methodName = m.getName();
            parameterTypes = m.getParameterTypes();
        }

        /**
         * Returns a hash code for this object. The result is based on the
         * method name and the parameter types.
         *
         * @return a hash code for this object
         */
        @Override
        public int hashCode()
        {
            final int factor = 31;
            final int seed = 17;

            int result = seed;
            result = result * factor + methodName.hashCode();
            result = result * factor + Arrays.hashCode(parameterTypes);
            return result;
        }

        /**
         * Checks whether this object equals another one. Two instances are
         * considered equals if the method name and the parameter types are
         * equal.
         *
         * @param obj the object to compare to
         * @return a flag whether these objects are equal
         */
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (!(obj instanceof Signature))
            {
                return false;
            }

            Signature c = (Signature) obj;
            return methodName.equals(c.methodName)
                    && Arrays.equals(parameterTypes, c.parameterTypes);
        }
    }
}
