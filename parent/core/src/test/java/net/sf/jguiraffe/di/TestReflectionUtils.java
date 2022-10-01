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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.ClassUtils;
import org.junit.Test;

/**
 * Test class for ReflectionUtils.
 *
 * @author Oliver Heger
 * @version $Id: TestReflectionUtils.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestReflectionUtils
{
    /** Constant for the name of the overloaded initialize method. */
    private static final String METHOD_INIT = "initializeOverloaded";

    /**
     * Tests whether a parameter is assignable when the involved classes are
     * equal.
     */
    @Test
    public void testIsParamAssignableEquals()
    {
        assertTrue("Reference not assignable", ReflectionUtils
                .isParamAssignable(getClass(), this));
        assertTrue("Wrapper not assignable", ReflectionUtils.isParamAssignable(
                Integer.class, 42));
    }

    /**
     * Tests whether a null value can be assigned to a reference parameter.
     */
    @Test
    public void testIsParamAssignableNullReference()
    {
        assertTrue("Null parameter not assignable", ReflectionUtils
                .isParamAssignable(getClass(), null));
    }

    /**
     * Tests that a null value cannot be assigned to a primitive parameter.
     */
    @Test
    public void testIsParamAssignableNullPrimitive()
    {
        assertFalse("Null parameter assignable to primitive", ReflectionUtils
                .isParamAssignable(Integer.TYPE, null));
    }

    /**
     * Tests assigning wrapper objects to primitive parameters.
     */
    @Test
    public void testIsParamAssignablePrimitive()
    {
        assertTrue("Boolean parameter not assignable", ReflectionUtils
                .isParamAssignable(Boolean.TYPE, Boolean.TRUE));
        assertTrue("Short parameter not assignable", ReflectionUtils
                .isParamAssignable(Short.TYPE, Short.valueOf((short) 10)));
        assertTrue("Byte parameter not assignable", ReflectionUtils
                .isParamAssignable(Byte.TYPE, Byte.valueOf((byte) 5)));
        assertTrue("Char parameter not assignable", ReflectionUtils
                .isParamAssignable(Character.TYPE, 'c'));
        assertTrue("Integer parameter not assignable", ReflectionUtils
                .isParamAssignable(Integer.TYPE, Integer.valueOf(42)));
        assertTrue("Long parameter not assignable", ReflectionUtils
                .isParamAssignable(Long.TYPE, Long.valueOf(100L)));
        assertTrue("Float parameter not assignable", ReflectionUtils
                .isParamAssignable(Float.TYPE, Float.valueOf(3.14f)));
        assertTrue("Double parameter not assignable", ReflectionUtils
                .isParamAssignable(Double.TYPE, Double.valueOf(3.1415)));
    }

    /**
     * Tests assigning a subtype to a parameter.
     */
    @Test
    public void testIsParamAssignableSubtype()
    {
        assertTrue("Subtype not assignable", ReflectionUtils.isParamAssignable(
                Collection.class, new ArrayList<Object>()));
    }

    /**
     * Tests a widening conversion when assigning a wrapper parameter value.
     */
    @Test
    public void testIsParamAssignableWidening()
    {
        assertTrue("Int not assignable to long", ReflectionUtils
                .isParamAssignable(Long.TYPE, Integer.valueOf(42)));
        assertTrue("Byte not assignable to int", ReflectionUtils
                .isParamAssignable(Integer.TYPE, Byte.valueOf((byte) 4)));
        assertTrue("Float not assignable to double", ReflectionUtils
                .isParamAssignable(Double.TYPE, Float.valueOf(3.14f)));
        assertFalse("Long assignable to int", ReflectionUtils
                .isParamAssignable(Integer.TYPE, Long.valueOf(100)));
    }

    /**
     * Tests parameter assignment with a null parameter class. This should cause
     * an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testIsParamAssignableNullClass()
    {
        ReflectionUtils.isParamAssignable(null, "Test");
    }

    /**
     * Tests the matchParameterTypes() method when all types are defined.
     */
    @Test
    public void testMatchParameterTypesDefinedTypesCompatible()
    {
        Class<?>[] paramTypes =
        { String.class, Integer.TYPE };
        Object[] values =
        { "test1", "test2" };
        assertTrue("Wrong result for compatible defined types", ReflectionUtils
                .matchParameterTypes(paramTypes, paramTypes, values));
    }

    /**
     * Tests the matchParameterTypes() method when a defined type is
     * incompatible.
     */
    @Test
    public void testMatchParameterTypesDefinedTypesIncompatible()
    {
        Class<?>[] methodParams =
        { Collection.class, String.class };
        Class<?>[] defParams =
        { ArrayList.class, null };
        Object[] values =
        { new ArrayList<Object>(), "Test" };
        assertFalse("Wrong result for incompatible defined types",
                ReflectionUtils.matchParameterTypes(methodParams, defParams,
                        values));
    }

    /**
     * Tests the matchParameterTypes() method when the value types are
     * compatible.
     */
    @Test
    public void testMatchParameterTypesCompatibleValues()
    {
        Class<?>[] methodParams =
        { Collection.class, String.class, String.class };
        Class<?>[] defParams =
        { null, null, null };
        Object[] values =
        { new ArrayList<Object>(), "Test", null };
        assertTrue("Wrong result for compatible defined types", ReflectionUtils
                .matchParameterTypes(methodParams, defParams, values));
    }

    /**
     * Tests the matchParameterTypes() method when there is an incompatible
     * value object.
     */
    @Test
    public void testMatchParameterTypesIncompatibleValues()
    {
        Class<?>[] methodParams =
        { Collection.class, String.class, String.class };
        Class<?>[] defParams =
        { null, null, null };
        Object[] values =
        { new ArrayList<Object>(), "Test", Integer.valueOf(42) };
        assertFalse("Wrong result for incompatible defined types",
                ReflectionUtils.matchParameterTypes(methodParams, defParams,
                        values));
    }

    /**
     * Tests the matchParameterTypes() method when the number of parameters is
     * different.
     */
    @Test
    public void testMatchParameterTypesDifferentParameterCount()
    {
        Class<?>[] methodParams =
        { Collection.class, String.class, String.class };
        Class<?>[] defParams =
        { null, null };
        Object[] values =
        { new ArrayList<Object>(), "Test" };
        assertFalse("Wrong result for different parameter count",
                ReflectionUtils.matchParameterTypes(methodParams, defParams,
                        values));
    }

    /**
     * Tests matching parameter types when an unboxing and a widening conversion
     * is needed.
     */
    @Test
    public void testMatchParameterTypesCompatibleValuesWidening()
    {
        Class<?>[] methodParams = {
            Integer.TYPE
        };
        Class<?>[] defParams = {
            null
        };
        Object[] values = {
            Byte.valueOf((byte) 5)
        };
        assertTrue("Wrong result for widening conversion", ReflectionUtils
                .matchParameterTypes(methodParams, defParams, values));
    }

    /**
     * Tries to find a method on a null target class.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFindMethodsNoTargetClass()
    {
        ReflectionUtils.findMethods(null, METHOD_INIT, new Class<?>[] {
                String.class, Integer.TYPE, ReflectionTestClass.Mode.class
        }, true);
    }

    /**
     * Helper method for obtaining a method.
     *
     * @param targetClass the target class (default: ReflectionTestClass)
     * @param name the method name (default: the overloaded initialize method)
     * @param signature a string with the signature
     * @return the method
     */
    private static Method fetchMethod(Class<?> targetClass, String name,
            String signature)
    {
        try
        {
            Class<?>[] classes = parameterTypesFromSignature(signature);
            Class<?> target =
                    (targetClass != null) ? targetClass
                            : ReflectionTestClass.class;
            String method = (name != null) ? name : METHOD_INIT;
            return target.getMethod(method, classes);
        }
        catch (Exception ex)
        {
            // handle all reflection exception the same way
            fail("Unexpected exception: " + ex);
            return null;
        }
    }

    /**
     * Helper method for obtaining a constructor.
     *
     * @param <T> the type of the involved class
     * @param targetClass the target class (default: ReflectionTestClass)
     * @param signature a string with the signature
     * @return the constructor found
     */
    private static <T> Constructor<T> fetchConstructor(Class<T> targetClass,
            String signature)
    {
        try
        {
            Class<?>[] classes = parameterTypesFromSignature(signature);
            Class<?> target =
                    (targetClass != null) ? targetClass
                            : ReflectionTestClass.class;
            @SuppressWarnings("unchecked")
            Constructor<T> result =
                    (Constructor<T>) target.getConstructor(classes);
            return result;
        }
        catch (Exception ex)
        {
            // handle all reflection exception the same way
            fail("Unexpected exception: " + ex);
            return null;
        }
    }

    /**
     * Helper method for extracting the parameter types from a signature string.
     * The signature is a comma-separated list of class names. Abbreviations
     * related to the test class are supported.
     *
     * @param signature the signature string
     * @return an array with the corresponding parameter types
     * @throws ClassNotFoundException if a class cannot be found
     */
    private static Class<?>[] parameterTypesFromSignature(String signature)
            throws ClassNotFoundException
    {
        String[] classNames = signature.split(",");
        Class<?>[] classes = new Class<?>[classNames.length];
        int idx = 0;
        for (String cn : classNames)
        {
            Class<?> cls;
            if ("Mode".equals(cn))
            {
                cls = ReflectionTestClass.Mode.class;
            }
            else if ("String".equals(cn))
            {
                cls = String.class;
            }
            else
            {
                cls = ClassUtils.getClass(cn);
            }
            classes[idx++] = cls;
        }
        return classes;
    }

    /**
     * Helper method for testing whether all expected methods were found.
     *
     * @param expected the expected methods
     * @param actual the actual methods
     */
    private void checkFoundMethods(
            Collection<? extends AccessibleObject> expected,
            Collection<? extends AccessibleObject> actual)
    {
        assertEquals("Wrong number of methods", expected.size(), actual.size());
        assertTrue("Different methods: " + actual, expected.containsAll(actual));
    }

    /**
     * Tests findMethods() if no array with parameter types is provided.
     */
    @Test
    public void testFindMethodsNoSignature()
    {
        List<Method> methods =
                ReflectionUtils.findMethods(ReflectionTestClass.class,
                        METHOD_INIT, null, true);
        Collection<Method> expected = new ArrayList<Method>();
        expected.add(fetchMethod(null, null, "String,int,Mode"));
        expected.add(fetchMethod(null, null, "String,int,String"));
        expected.add(fetchMethod(null, null, "Mode,String,int"));
        expected.add(fetchMethod(null, null, "String,java.lang.Object,int,Mode"));
        checkFoundMethods(expected, methods);
    }

    /**
     * Tests findMethods() if no method name is provided.
     */
    @Test
    public void testFindMethodsNoName()
    {
        List<Method> methods =
                ReflectionUtils.findMethods(ReflectionTestClass.class, null,
                        new Class<?>[] {
                                String.class, Object.class, Integer.TYPE,
                                ReflectionTestClass.Mode.class
                        }, true);
        checkFoundMethods(Collections.singleton(fetchMethod(null, null,
                "String,java.lang.Object,int,Mode")), methods);
    }

    /**
     * Tests findMethods() if all parameter types are specified exactly.
     */
    @Test
    public void testFindMethodsSpecific()
    {
        List<Method> methods =
                ReflectionUtils.findMethods(ReflectionTestClass.class,
                        METHOD_INIT, new Class<?>[] {
                                String.class, Integer.TYPE,
                                ReflectionTestClass.Mode.class
                        }, true);
        checkFoundMethods(Collections.singleton(fetchMethod(null, null,
                "String,int,Mode")), methods);
    }

    /**
     * Tests whether wild cards can be used for parameter types.
     */
    @Test
    public void testFindMethodsParamsWildCard()
    {
        List<Method> methods =
                ReflectionUtils.findMethods(ReflectionTestClass.class,
                        METHOD_INIT, new Class<?>[] {
                                String.class, Integer.TYPE, null
                        }, true);
        Collection<Method> expected = new ArrayList<Method>();
        expected.add(fetchMethod(null, null, "String,int,Mode"));
        expected.add(fetchMethod(null, null, "String,int,String"));
        checkFoundMethods(expected, methods);
    }

    /**
     * Tests whether it is possible to find super class parameters.
     */
    @Test
    public void testFindMethodsAssignableClasses()
    {
        List<Method> methods =
                ReflectionUtils.findMethods(ReflectionTestClass.class,
                        METHOD_INIT, new Class<?>[] {
                                String.class, getClass(), Integer.TYPE,
                                ReflectionTestClass.Mode.class
                        }, false);
        checkFoundMethods(Collections.singleton(fetchMethod(null, null,
                "String,java.lang.Object,int,Mode")), methods);
    }

    /**
     * Tests searching for methods if exact matches are required.
     */
    @Test
    public void testFindMethodsExactMatch()
    {
        List<Method> methods =
                ReflectionUtils.findMethods(ReflectionTestClass.class,
                        METHOD_INIT, new Class<?>[] {
                                String.class, getClass(), Integer.TYPE,
                                ReflectionTestClass.Mode.class
                        }, true);
        assertTrue("Found methods: " + methods, methods.isEmpty());
    }

    /**
     * Tests findMethods() with primitive types and widening comparisons.
     */
    @Test
    public void testFindMethodsPrimitiveBoxing()
    {
        List<Method> methods =
                ReflectionUtils.findMethods(ReflectionTestClass.class,
                        METHOD_INIT, new Class<?>[] {
                                String.class, Integer.class,
                                ReflectionTestClass.Mode.class
                        }, true);
        checkFoundMethods(Collections.singleton(fetchMethod(null, null,
                "String,int,Mode")), methods);
    }

    /**
     * Tries to find constructors on a null target class.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFindConstructorsNoTargetClass()
    {
        ReflectionUtils.findConstructors(null, null, true);
    }

    /**
     * Tests whether a specific constructor can be found.
     */
    @Test
    public void testFindConstructorsSpecific()
    {
        List<Constructor<ReflectionTestClass>> constr =
                ReflectionUtils.findConstructors(ReflectionTestClass.class,
                        new Class<?>[] {
                                String.class, Integer.TYPE
                        }, true);
        checkFoundMethods(Collections.singleton(fetchConstructor(
                ReflectionTestClass.class, "String,int")), constr);
    }

    /**
     * Tests whether the standard constructor can be found.
     */
    @Test
    public void testFindConstructorsStandard()
    {
        List<Constructor<ReflectionTestClass>> constr =
                ReflectionUtils.findConstructors(ReflectionTestClass.class,
                        new Class<?>[0], true);
        assertEquals("Wrong number of constructors", 1, constr.size());
        Constructor<ReflectionTestClass> c = constr.get(0);
        assertEquals("Wrong constructor", 0, c.getParameterTypes().length);
    }

    /**
     * Tests invoking a method successfully.
     */
    @Test
    public void testInvokeMethodSuccessful() throws NoSuchMethodException
    {
        ReflectionTestClass o = new ReflectionTestClass("Test", 10);
        Method m = ReflectionTestClass.class.getMethod("getStringProp");
        assertEquals("Wrong return value", "Test", ReflectionUtils
                .invokeMethod(m, o));
    }

    /**
     * Tests passing in parameter to a method.
     */
    @Test
    public void testInvokeMethodWithParameters() throws NoSuchMethodException
    {
        ReflectionTestClass o = new ReflectionTestClass();
        Method m = ReflectionTestClass.class.getMethod("setStringProp", String.class);
        assertNull("Wrong return value of void method", ReflectionUtils
                .invokeMethod(m, o, "Test"));
        assertEquals("Property not set", "Test", o.getStringProp());
    }

    /**
     * Tests invoking a method that will throw an exception.
     */
    @Test(expected = InjectionException.class)
    public void testInvokeMethodWithException() throws NoSuchMethodException
    {
        ReflectionTestClass o = new ReflectionTestClass();
        Method m = ReflectionTestClass.class.getMethod("methodThatThrowsAnException");
        ReflectionUtils.invokeMethod(m, o);
    }

    /**
     * Tries to invoke a null method. That should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvokeMethodNull()
    {
        ReflectionUtils.invokeMethod(null, new ReflectionTestClass(), "Test");
    }

    /**
     * Tests invoking a ctor.
     */
    @Test
    public void testInovkeConstructorSuccessful() throws NoSuchMethodException
    {
        Class<?>[] types =
        { String.class, Integer.TYPE };
        Object[] values =
        { "Test", Integer.valueOf(10) };
        Constructor<ReflectionTestClass> c = ReflectionTestClass.class.getConstructor(types);
        ReflectionTestClass obj = ReflectionUtils.invokeConstructor(c, values);
        assertEquals("Wrong string property", "Test", obj.getStringProp());
        assertEquals("Wrong int property", 10, obj.getIntProp());
    }

    /**
     * Tests an invocation of a ctor that will cause an error. We try to invoke
     * a private ctor.
     */
    @Test(expected = InjectionException.class)
    public void testInvokeConstructorError() throws NoSuchMethodException
    {
        Constructor<ReflectionTestClass> c = ReflectionTestClass.class
                .getDeclaredConstructor(Integer.TYPE);
        ReflectionUtils.invokeConstructor(c, Integer.valueOf(10));
    }

    /**
     * Tries to invoke a null ctor. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvokeConstructorNull()
    {
        ReflectionUtils.invokeConstructor(null, "Test");
    }

    /**
     * Tests setting properties on a bean directly.
     */
    @Test
    public void testSetProperty()
    {
        ReflectionTestClass obj = new ReflectionTestClass();
        ReflectionUtils.setProperty(obj, "stringProp", "Test");
        ReflectionUtils.setProperty(obj, "intProp", Integer.valueOf(42));
        assertEquals("String was not set", "Test", obj.getStringProp());
        assertEquals("Number was not set", 42, obj.getIntProp());
    }

    /**
     * Tries to set a read-only property. This should cause an exception.
     */
    @Test(expected = InjectionException.class)
    public void testSetPropertyReadOnly()
    {
        ReflectionTestClass obj = new ReflectionTestClass();
        ReflectionUtils.setProperty(obj, "readOnlyProperty", "Test");
    }

    /**
     * Tries to set a non existing property. This should cause an exception.
     */
    @Test(expected = InjectionException.class)
    public void testSetPropertyNonExisting()
    {
        ReflectionUtils.setProperty(new ReflectionTestClass(), "nonExProp", 42);
    }

    /**
     * Tests setting a null property. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetPropertyNull()
    {
        ReflectionUtils.setProperty(new ReflectionTestClass(), null, 10);
    }

    /**
     * Tries setting a property when the bean is null. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetPropertyNullBean()
    {
        ReflectionUtils.setProperty(null, "test", "TestValue");
    }

    /**
     * Tests setting a property when the value is incompatible.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetPropertyInvalidValue()
    {
        ReflectionUtils.setProperty(new ReflectionTestClass(), "intProp", "Text");
    }

    /**
     * Tests setting a private property. This should cause an exception.
     */
    @Test(expected = InjectionException.class)
    public void testSetPropertyPrivate()
    {
        ReflectionUtils.setProperty(new ReflectionTestClass(),
                "privateProperty", "Test");
    }

    /**
     * Tests setting a property when the setter throws an exception. This should
     * be redirected.
     */
    @Test(expected = InjectionException.class)
    public void testSetPropertyException()
    {
        ReflectionUtils.setProperty(new ReflectionTestClass(),
                "exceptionProperty", "Test");
    }

    /**
     * Tests accessing properties.
     */
    @Test
    public void testGetProperty()
    {
        ReflectionTestClass obj = new ReflectionTestClass("Test", 10);
        assertEquals("Wrong string property", "Test", ReflectionUtils
                .getProperty(obj, "stringProp"));
        assertEquals("Wrong int property", Integer.valueOf(10), ReflectionUtils
                .getProperty(obj, "intProp"));
    }

    /**
     * Tries accessing a non existing property. This should fail of course.
     */
    @Test(expected = InjectionException.class)
    public void testGetPropertyNonExisting()
    {
        ReflectionUtils.getProperty(new ReflectionTestClass(), "nonExProp");
    }

    /**
     * Tries accessing a null property. This is an invalid input parameter.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetPropertyNull()
    {
        ReflectionUtils.getProperty(new ReflectionTestClass(), null);
    }

    /**
     * Tests whether a class can be loaded.
     */
    @Test
    public void testLoadClass()
    {
        Class<?> cls = ReflectionUtils.loadClass(InjectionException.class
                .getName(), getClass().getClassLoader());
        assertEquals("Class was not correctly loaded",
                InjectionException.class, cls);
    }

    /**
     * Tests whether a primitive class can be loaded.
     */
    @Test
    public void testLoadClassPrimitive()
    {
        Class<?> cls = ReflectionUtils.loadClass("int", getClass()
                .getClassLoader());
        assertEquals("Wrong class", Integer.TYPE, cls);
    }

    /**
     * Tries loading a non existing class. This should cause an exception.
     */
    @Test(expected = InjectionException.class)
    public void testLoadClassUnknown()
    {
        ReflectionUtils.loadClass("an unknown.class!", getClass()
                .getClassLoader());
    }

    /**
     * Tries loading a class with a null name. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLoadClassNullName()
    {
        ReflectionUtils.loadClass(null, getClass().getClassLoader());
    }

    /**
     * Tries loading a class without specifying a class loader. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLoadClassNullLoader()
    {
        ReflectionUtils.loadClass(getClass().getName(), null);
    }

    /**
     * Tries to call removeCovariantDuplicates() with null input.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRemoveCovariantDuplicatesNull()
    {
        ReflectionUtils.removeCovariantDuplicates(null);
    }

    /**
     * Tests removeCovariantDuplicates() if the passed in list contains null
     * elements.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRemoveCovariantDuplicatesNullElement()
    {
        List<Method> methods =
                ReflectionUtils.findMethods(ReflectionUtils.class, null, null,
                        false);
        methods.add(null);
        ReflectionUtils.removeCovariantDuplicates(methods);
    }

    /**
     * Tests removeCovariantDuplicates() with a list with only a single element.
     * It cannot contain duplicates, so it should be returned immediately.
     */
    @Test
    public void testRemoveCovariantDuplicatesSmallList()
    {
        List<Method> list =
                Collections.singletonList(fetchMethod(null, null,
                        "String,int,String"));
        List<Method> copy = new ArrayList<Method>(list);
        assertSame("Wrong result list", list,
                ReflectionUtils.removeCovariantDuplicates(list));
        assertEquals("List was modified", copy, list);
    }

    /**
     * Tests removeCovariantDuplicates() if there are no duplicates.
     */
    @Test
    public void testRemoveCovariantDuplicatesNoDuplicates()
    {
        List<Method> list =
                ReflectionUtils.findMethods(ReflectionTestClass.class,
                        METHOD_INIT, null, false);
        List<Method> copy = new ArrayList<Method>(list);
        assertSame("Wrong result list", list,
                ReflectionUtils.removeCovariantDuplicates(list));
        assertEquals("List was modified", copy, list);
    }

    /**
     * Tests removeCovariantDuplicates() if there are actually duplicates.
     */
    @Test
    public void testRemoveCovariantDuplicatesWithDuplicates()
    {
        final String methodName = "append";
        final Class<?>[] paramTypes = new Class<?>[] {
            String.class
        };
        List<Method> list =
                ReflectionUtils.findMethods(StringBuilder.class, methodName,
                        paramTypes, true);
        assertTrue("Not enough matches found: " + list, list.size() > 1);
        List<Method> result = ReflectionUtils.removeCovariantDuplicates(list);
        assertEquals("Wrong number of elements", 1, result.size());
        Method m = result.get(0);
        assertEquals("Wrong method name", methodName, m.getName());
        assertTrue("Wrong parameters",
                Arrays.equals(paramTypes, m.getParameterTypes()));
        assertEquals("Wrong result type", StringBuilder.class,
                m.getReturnType());
    }
}
