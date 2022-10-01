/*
 * Copyright 2006-2022 The JGUIraffe Team.
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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ClassUtils;

/**
 * <p>
 * A helper class providing some more complex functionality related to
 * reflection.
 * </p>
 * <p>
 * This class builds on top of {@link ReflectionUtils} which implements
 * low-level utility methods for various operations related to reflection.
 * {@code InvocationHelper} in contrast focuses on some more advanced
 * operations. It deals with stuff like finding suitable methods and
 * constructors to be invoked, getting and setting properties, and data type
 * conversions (this is delegated to an instance of {@link ConversionHelper}
 * which is maintained by this class).
 * </p>
 * <p>
 * Implementation note: This class is thread-safe.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: InvocationHelper.java 205 2012-01-29 18:29:57Z oheger $
 */
public class InvocationHelper
{
    /** Constant for a format string for generating a method signature. */
    private static final String METHOD_SIGNATURE = "%s.%s(%s), arguments: %s";

    /** Constant for the name of a method representing a constructor. */
    private static final String CONSTR_METHOD_NAME = "<init>";

    /** Stores the associated {@code ConversionHelper} instance. */
    private final ConversionHelper conversionHelper;

    /**
     * Creates a new instance of {@code InvocationHelper}. A default
     * {@code ConversionHelper} instance is set.
     */
    public InvocationHelper()
    {
        this(null);
    }

    /**
     * Creates a new instance of {@code InvocationHelper} and initializes it
     * with the given {@code ConversionHelper} instance. If no helper object is
     * provided, a new default instance is created.
     *
     * @param convHlp the {@code ConversionHelper}
     */
    public InvocationHelper(ConversionHelper convHlp)
    {
        conversionHelper = (convHlp != null) ? convHlp : new ConversionHelper();
    }

    /**
     * Returns the {@code ConversionHelper} instance associated with this
     * object.
     *
     * @return the {@code ConversionHelper}
     */
    public ConversionHelper getConversionHelper()
    {
        return conversionHelper;
    }

    /**
     * Determines the method to be called given the name, the parameter types,
     * and the arguments. This method delegates to
     * {@link ReflectionUtils#findMethods(Class, String, Class[], boolean)} to
     * find a single method that matches the specified method signature. It also
     * takes the specified method arguments into account in order to find a
     * unique match. If this is not possible, an exception is thrown.
     *
     * @param targetClass the class on which to invoke the method
     * @param methodName the name of the method to be invoked
     * @param parameterTypes an array with the known parameter types (may be
     *        <b>null</b> or contain <b>null</b> entries representing wild
     *        cards)
     * @param args an array with the method arguments
     * @return the method to be invoked
     * @throws InjectionException if the method cannot be determined
     * @throws IllegalArgumentException if the target class is <b>null</b>
     */
    public Method findUniqueMethod(Class<?> targetClass, String methodName,
            Class<?>[] parameterTypes, Object[] args)
    {
        List<Method> methods =
                findMethods(targetClass, methodName, parameterTypes, args, true);
        if (methods.isEmpty())
        {
            // try again with relaxed type checking
            methods =
                    findMethods(targetClass, methodName, parameterTypes, args,
                            false);
            if (methods.isEmpty())
            {
                throw nonUniqueMethodException(methods, targetClass,
                        methodName, parameterTypes, args);
            }
        }

        if (methods.size() > 1)
        {
            // apply the types of the current call arguments
            Class<?>[] callTypes =
                    parameterTypesFromArguments(parameterTypes, args);
            methods =
                    findMethods(targetClass, methodName, callTypes, args, false);
        }

        if (methods.size() != 1)
        {
            throw nonUniqueMethodException(methods, targetClass, methodName,
                    parameterTypes, args);
        }

        return methods.get(0);
    }

    /**
     * Tries to match a single constructor of the specified target class given
     * the parameter types and the call arguments. This method works analogously
     * to {@link #findUniqueMethod(Class, String, Class[], Object[])}, but it
     * searches for a matching constructor.
     *
     * @param <T> the type of the target class
     * @param targetClass the class on which to invoke the method
     * @param parameterTypes an array with the known parameter types (may be
     *        <b>null</b> or contain <b>null</b> entries representing wild
     *        cards)
     * @param args an array with the call arguments
     * @return the unique constructor matching the specified criteria
     * @throws InjectionException if no unique constructor can be determined
     * @throws IllegalArgumentException if the target class is <b>null</b>
     */
    public <T> Constructor<T> findUniqueConstructor(Class<T> targetClass,
            Class<?>[] parameterTypes, Object[] args)
    {
        // This code is pretty similar to the one of findUniqueMethod().
        // Unfortunately, it cannot easily be generalized. This is because
        // Method and Constructor do not share a common super class that allows
        // access to the parameter types.
        List<Constructor<T>> constrs =
                findConstructors(targetClass, parameterTypes, args, true);
        if (constrs.isEmpty())
        {
            constrs =
                    findConstructors(targetClass, parameterTypes, args, false);
            if (constrs.isEmpty())
            {
                throw nonUniqueMethodException(constrs, targetClass,
                        CONSTR_METHOD_NAME, parameterTypes, args);
            }
        }

        if (constrs.size() > 1)
        {
            Class<?>[] callTypes =
                    parameterTypesFromArguments(parameterTypes, args);
            constrs = findConstructors(targetClass, callTypes, args, false);
        }

        if (constrs.size() != 1)
        {
            throw nonUniqueMethodException(constrs, targetClass,
                    CONSTR_METHOD_NAME, parameterTypes, args);
        }

        return constrs.get(0);
    }

    /**
     * Invokes the specified static method and returns its result.
     *
     * @param targetClass the target class on which to invoke the method (must
     *        not be <b>null</b>)
     * @param methodName the name of the method
     * @param parameterTypes an array with the known parameter types (may be
     *        <b>null</b> or contain <b>null</b> entries representing wild
     *        cards)
     * @param args an array with the method arguments
     * @return the result returned by the method
     * @throws InjectionException if an exception occurs
     * @throws IllegalArgumentException if the target class is undefined
     */
    public Object invokeStaticMethod(Class<?> targetClass, String methodName,
            Class<?>[] parameterTypes, Object[] args)
    {
        return invokeMethod(targetClass, null, methodName, parameterTypes, args);
    }

    /**
     * Invokes a method on the specified object. The object must not be
     * <b>null</b> because the target class is obtained from it. Then this
     * implementation delegates to
     * {@link #invokeMethod(Class, Object, String, Class[], Object[])}.
     *
     * @param instance the instance on which to invoke the method
     * @param methodName the name of the method to be invoked
     * @param parameterTypes an array with the known parameter types (may be
     *        <b>null</b> or contain <b>null</b> entries representing wild
     *        cards)
     * @param args an array with the method arguments
     * @return the result returned by the method
     * @throws InjectionException if an exception occurs
     * @throws IllegalArgumentException if the instance is undefined
     */
    public Object invokeInstanceMethod(Object instance, String methodName,
            Class<?>[] parameterTypes, Object[] args)
    {
        return invokeMethod(null, instance, methodName, parameterTypes, args);
    }

    /**
     * Invokes a method. This is the most generic form of invoking a method. It
     * works with both static and instance methods. This method delegates to
     * {@link #findUniqueMethod(Class, String, Class[], Object[])} to find the
     * method to be invoked. Then it performs necessary type conversions of the
     * parameters. Eventually, it invokes the method. Using this method it is
     * possible to invoke a specific method in a given class, even if the
     * parameter types are not or only partly known. All possible exceptions
     * (e.g. no unique method is found, the parameters cannot be converted,
     * reflection-related exceptions) are thrown as {@link InjectionException}
     * exceptions.
     *
     * @param targetClass the target class on which to invoke the method (may be
     *        <b>null</b> if an object instance is provided)
     * @param instance the instance on which to invoke the method (may be
     *        <b>null</b> for static methods)
     * @param methodName the name of the method to be invoked
     * @param parameterTypes an array with the known parameter types (may be
     *        <b>null</b> or contain <b>null</b> entries representing wild
     *        cards)
     * @param args an array with the method arguments
     * @return the result returned by the method
     * @throws InjectionException if an exception occurs
     * @throws IllegalArgumentException if a required parameter is missing or
     *         the specification of the method to be called is invalid
     */
    public Object invokeMethod(Class<?> targetClass, Object instance,
            String methodName, Class<?>[] parameterTypes, Object[] args)
    {
        Class<?> clsToInvoke = getClassToInvoke(targetClass, instance);
        Method method =
                findUniqueMethod(clsToInvoke, methodName, parameterTypes, args);
        Object[] convertedArgs =
                convertArguments(method.getParameterTypes(), args);
        return ReflectionUtils.invokeMethod(method, instance, convertedArgs);
    }

    /**
     * Invokes a constructor. This method delegates to
     * {@link #findUniqueConstructor(Class, Class[], Object[])} to obtain the
     * constructor to be invoked. Then it performs necessary type conversions of
     * the parameters. Eventually, it invokes the constructor and returns the
     * newly created instance. Using this method it is possible to invoke a
     * specific constructor of a given class, even if the parameter types are
     * not or only partly known. All possible exceptions (e.g. no unique method
     * is found, the parameters cannot be converted, reflection-related
     * exceptions) are thrown as {@link InjectionException} exceptions.
     *
     * @param <T> the type of the target class
     * @param targetClass the target class on which the constructor is to be
     *        invoked
     * @param parameterTypes an array with the known parameter types (may be
     *        <b>null</b> or contain <b>null</b> entries representing wild
     *        cards)
     * @param args an array with the method arguments
     * @return the newly created instance
     * @throws InjectionException if an exception occurs
     * @throws IllegalArgumentException if the specification of the constructor
     *         is invalid
     */
    public <T> T invokeConstructor(Class<T> targetClass,
            Class<?>[] parameterTypes, Object[] args)
    {
        Constructor<T> ctor =
                findUniqueConstructor(targetClass, parameterTypes, args);
        Object[] convertedArgs =
                convertArguments(ctor.getParameterTypes(), args);
        return ReflectionUtils.invokeConstructor(ctor, convertedArgs);
    }

    /**
     * Sets a property of the given bean. This method obtains the set method for
     * the property in question. If necessary, type conversion is performed.
     * Then the set method is invoked so that the new value of the property is
     * written. Occurring exceptions are redirected either as
     * {@link InjectionException} (if they are related to reflection operations)
     * or as {@code IllegalArgumentException} if they are related to the
     * parameters passed to this method.
     *
     * @param bean the bean on which to set the property
     * @param property the name of the property to be set
     * @param value the value of the property
     * @throws InjectionException if an error occurs related to reflection
     * @throws IllegalArgumentException if invalid arguments are passed in
     */
    public void setProperty(Object bean, String property, Object value)
    {
        Class<?> propertyType = getPropertyType(bean, property);
        Object convertedValue =
                getConversionHelper().convert(propertyType, value);
        ReflectionUtils.setProperty(bean, property, convertedValue);
    }

    /**
     * Performs necessary type conversions before invoking a method. This method
     * is called before a method or a constructor is invoked. The passed in
     * parameter types are the actual parameters of the method to be invoked,
     * the arguments are the ones passed by the caller. They may require a type
     * conversion. This implementation performs this conversion if necessary.
     *
     * @param parameterTypes the array with the parameter types
     * @param args the array with the call arguments
     * @return an array with the converted arguments
     * @throws InjectionException if a conversion fails
     */
    protected Object[] convertArguments(Class<?>[] parameterTypes, Object[] args)
    {
        assert arrayLength(parameterTypes) == arrayLength(args) : "Different array lengths!";
        Object[] results = null;

        for (int i = 0; i < parameterTypes.length; i++)
        {
            Object convValue =
                    getConversionHelper().convert(parameterTypes[i], args[i]);

            if (convValue != args[i])
            {
                if (results == null)
                {
                    // lazy create results array
                    results = new Object[args.length];
                    System.arraycopy(args, 0, results, 0, args.length);
                }
                results[i] = convValue;
            }
        }

        return (results != null) ? results : args;
    }

    /**
     * Helper method for finding methods that match given criteria. This method
     * delegates to {@link ReflectionUtils} for doing the lookup. Then it sorts
     * out the hits that are incompatible with the passed in parameters.
     * Finally, it deals with methods with identical signatures but different
     * return types.
     *
     * @param targetClass the target class
     * @param methodName the name of the method
     * @param parameterTypes an array with parameter types
     * @param args concrete parameters to be passed to the method
     * @param exactMatch a flag whether an exact match is to be performed
     * @return the list with found methods
     */
    private static List<Method> findMethods(Class<?> targetClass,
            String methodName, Class<?>[] parameterTypes, Object[] args,
            boolean exactMatch)
    {
        List<Method> methods =
                ReflectionUtils.findMethods(targetClass, methodName,
                        parameterTypes, exactMatch);

        for (Iterator<Method> it = methods.iterator(); it.hasNext();)
        {
            Method m = it.next();
            if (!checkArgumentsForCompatibility(m.getParameterTypes(), args))
            {
                it.remove();
            }
        }

        return ReflectionUtils.removeCovariantDuplicates(methods);
    }

    /**
     * Helper method for finding constructors that match given criteria. Works
     * analogously to
     * {@link #findMethods(Class, String, Class[], Object[], boolean)}, but
     * operates on constructors.
     *
     * @param <T> the type of the target class
     * @param targetClass the target class
     * @param parameterTypes an array with parameter types
     * @param args concrete parameters to be passed to the constructor
     * @param exactMatch a flag whether an exact match is to be performed
     * @return the list with found constructors
     */
    private static <T> List<Constructor<T>> findConstructors(
            Class<T> targetClass, Class<?>[] parameterTypes, Object[] args,
            boolean exactMatch)
    {
        List<Constructor<T>> constrs =
                ReflectionUtils.findConstructors(targetClass, parameterTypes,
                        exactMatch);

        for (Iterator<Constructor<T>> it = constrs.iterator(); it.hasNext();)
        {
            Constructor<T> c = it.next();
            if (!checkArgumentsForCompatibility(c.getParameterTypes(), args))
            {
                it.remove();
            }
        }

        return constrs;
    }

    /**
     * Tests whether the specified arguments are compatible with the given
     * method signature. This method mainly checks whether a null value is
     * assigned to a parameter of primitive type. Further type checks are not
     * performed because there may be type conversions later.
     *
     * @param parameterTypes the parameter types of the method
     * @param args the arguments to be passed to the method
     * @return a flag whether these arguments are compatible with the method
     */
    private static boolean checkArgumentsForCompatibility(
            Class<?>[] parameterTypes, Object[] args)
    {
        if (arrayLength(parameterTypes) != arrayLength(args))
        {
            // number of parameters does not match
            return false;
        }

        for (int i = 0; i < arrayLength(parameterTypes); i++)
        {
            if (args[i] == null && parameterTypes[i].isPrimitive())
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Derives parameter types from the concrete parameters. For each parameter
     * type whose class is not specified the corresponding type from the
     * argument is set - unless it is undefined, too.
     *
     * @param parameterTypes the array with parameter types
     * @param args the concrete call arguments
     * @return an array with enhanced parameter type information
     */
    private static Class<?>[] parameterTypesFromArguments(
            Class<?>[] parameterTypes, Object[] args)
    {
        if (arrayLength(args) == 0)
        {
            return parameterTypes;
        }

        Class<?>[] newTypes = new Class<?>[arrayLength(args)];
        System.arraycopy(parameterTypes, 0, newTypes, 0, newTypes.length);
        for (int i = 0; i < newTypes.length; i++)
        {
            if (newTypes[i] == null && args[i] != null)
            {
                newTypes[i] = args[i].getClass();
            }
        }

        return newTypes;
    }

    /**
     * Obtains the class to be invoked from the parameters of a method
     * invocation. If both a target class and an instance are provided, they are
     * checked for compatibility. If a target class was specified, it is used;
     * otherwise the class from the instance is obtained.
     *
     * @param targetClass the target class
     * @param instance the object instance
     * @return the class to be invoked
     * @throws IllegalArgumentException if the parameters are invalid
     */
    private static Class<?> getClassToInvoke(Class<?> targetClass,
            Object instance)
    {
        if (targetClass == null && instance == null)
        {
            throw new IllegalArgumentException(
                    "Neither target class nor instance provided!");
        }

        Class<?> clsToInvoke;
        if (targetClass != null)
        {
            if (instance != null
                    && !ClassUtils.isAssignable(instance.getClass(),
                            targetClass))
            {
                throw new IllegalArgumentException("Target class "
                        + targetClass + " is not compatible with instance "
                        + instance);
            }
            clsToInvoke = targetClass;
        }
        else
        {
            clsToInvoke = instance.getClass();
        }
        return clsToInvoke;
    }

    /**
     * Determines the type of the specified property.
     *
     * @param bean the bean
     * @param property the name of the property
     * @return the data type of this property
     * @throws IllegalArgumentException if parameters are invalid
     * @throws InjectionException if an error occurs
     */
    private static Class<?> getPropertyType(Object bean, String property)
    {
        Class<?> propertyType;
        try
        {
            propertyType = PropertyUtils.getPropertyType(bean, property);
        }
        catch (IllegalArgumentException iex)
        {
            throw iex;
        }
        catch (Exception ex)
        {
            // any other exception related to reflection is redirected
            throw new InjectionException(
                    "Error when determining type of property " + property
                            + " on bean " + bean, ex);
        }

        if (propertyType == null)
        {
            throw new InjectionException("Cannot determine type of property "
                    + property + " on bean " + bean);
        }
        return propertyType;
    }

    /**
     * Helper method for generating a string representation for a method
     * signature.
     *
     * @param targetClass the class the method belongs to
     * @param methodName the name of the method
     * @param paramTypes the array with parameter types
     * @param args the array with call arguments
     * @return a string representation of the method signature
     */
    private static String methodSignature(Class<?> targetClass,
            String methodName, Class<?>[] paramTypes, Object[] args)
    {
        return String.format(METHOD_SIGNATURE, targetClass.getName(),
                methodName, Arrays.toString(paramTypes), Arrays.toString(args));
    }

    /**
     * Helper method for generating a meaningful exception message if no unique
     * match for a method to be called can be found. This method checks whether
     * 0 or more than 1 matches were found and produces a corresponding error
     * message. The exception returned by this method also contains the
     * signature of the desired method.
     *
     * @param matches a list with the found matches
     * @param targetClass the class the method belongs to
     * @param methodName the name of the method
     * @param paramTypes the array with parameter types
     * @param args the array with call arguments
     * @return an exception reporting the error condition
     */
    private static InjectionException nonUniqueMethodException(
            List<? extends AccessibleObject> matches, Class<?> targetClass,
            String methodName, Class<?>[] paramTypes, Object[] args)
    {
        String msg =
                matches.isEmpty() ? "No match found for method %s"
                        : String.format(
                                "Multiple matches found for method %%s. Matches: %s",
                                matches.toString());
        return new InjectionException(String.format(msg,
                methodSignature(targetClass, methodName, paramTypes, args)));
    }

    /**
     * Null-safe method for determining the length of an array.
     *
     * @param ar the array in question
     * @return the length of this array
     */
    private static int arrayLength(Object[] ar)
    {
        return (ar != null) ? ar.length : 0;
    }
}
