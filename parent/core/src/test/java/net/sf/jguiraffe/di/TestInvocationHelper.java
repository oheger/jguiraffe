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
package net.sf.jguiraffe.di;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code InvocationHelper}.
 *
 * @author Oliver Heger
 * @version $Id: TestInvocationHelper.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestInvocationHelper
{
    /** Constant for the name of the overloaded initialize method. */
    private static final String METHOD_INIT = "initializeOverloaded";

    /** Constant for a test string. */
    private static final String STR_PARAM = "testStringParameterValue";

    /** Constant for a test integer. */
    private static final Integer INT_PARAM = 800;

    /** Constant for a test mode value. */
    private static final ReflectionTestClass.Mode MODE_PARAM =
            ReflectionTestClass.Mode.PRODUCTION;

    /** The instance to be tested. */
    private InvocationHelper helper;

    @Before
    public void setUp() throws Exception
    {
        helper = new InvocationHelper();
    }

    /**
     * Tests whether a default conversion helper instance is created.
     */
    @Test
    public void testGetConversionHelperDefault()
    {
        assertNotNull("No conversion helper", helper.getConversionHelper());
    }

    /**
     * Tests whether a conversion helper can be set when an instance is created.
     */
    @Test
    public void testGetConversionHelperInit()
    {
        ConversionHelper ch = new ConversionHelper();
        helper = new InvocationHelper(ch);
        assertSame("Wrong conversion helper", ch, helper.getConversionHelper());
    }

    /**
     * Tests findUniqueMethod() if the criteria select a single method.
     */
    @Test
    public void testFindUniqueMethodSpecific()
    {
        Class<?>[] paramTypes = new Class<?>[] {
                String.class, Integer.TYPE, ReflectionTestClass.Mode.class
        };
        Object[] args = new Object[] {
                STR_PARAM, INT_PARAM, MODE_PARAM
        };
        Method m =
                helper.findUniqueMethod(ReflectionTestClass.class, METHOD_INIT,
                        paramTypes, args);
        assertEquals("Wrong method name", METHOD_INIT, m.getName());
        assertTrue("Wrong parameters: " + m,
                Arrays.equals(paramTypes, m.getParameterTypes()));
    }

    /**
     * Tests findUniqueMethod() if a match can only be found if parameter types
     * are not matched exactly.
     */
    @Test
    public void testFindUniqueMethodAssignable()
    {
        Class<?>[] paramTypes =
                new Class<?>[] {
                        String.class, String.class, Integer.TYPE,
                        ReflectionTestClass.Mode.class
                };
        Object[] args = new Object[] {
                STR_PARAM, "anotherString", INT_PARAM, MODE_PARAM
        };
        Method m =
                helper.findUniqueMethod(ReflectionTestClass.class, METHOD_INIT,
                        paramTypes, args);
        assertEquals("Wrong method name", METHOD_INIT, m.getName());
        paramTypes[1] = Object.class;
        assertTrue("Wrong parameters: " + m,
                Arrays.equals(paramTypes, m.getParameterTypes()));
    }

    /**
     * Tests findUniqueMethod() if an exact match can only be achieved when the
     * types of the concrete arguments are inspected.
     */
    @Test
    public void testFindUniqueMethodArgumentTypes()
    {
        Class<?>[] paramTypes = new Class<?>[] {
                String.class, Integer.TYPE, null
        };
        Object[] args = new Object[] {
                STR_PARAM, INT_PARAM, MODE_PARAM
        };
        Method m =
                helper.findUniqueMethod(ReflectionTestClass.class, METHOD_INIT,
                        paramTypes, args);
        assertEquals("Wrong method name", METHOD_INIT, m.getName());
        paramTypes[2] = ReflectionTestClass.Mode.class;
        assertTrue("Wrong parameters: " + m,
                Arrays.equals(paramTypes, m.getParameterTypes()));
    }

    /**
     * Tests whether findUniqueMethod() can handle methods without arguments.
     */
    @Test
    public void testFindUniqueMethodVoid()
    {
        final String methodName = "getData";
        Class<?>[] paramTypes = new Class<?>[0];
        Method m =
                helper.findUniqueMethod(ReflectionTestClass.class, methodName,
                        paramTypes, null);
        assertEquals("Wrong method name", methodName, m.getName());
        assertEquals("Got parameters", 0, m.getParameterTypes().length);
    }

    /**
     * Tests whether findUniqueMethod() can deal with duplicate method
     * signatures that have different return types.
     */
    @Test
    public void testFindUniqueMethodCovariantDuplicates()
    {
        final String methodName = "append";
        Class<?>[] paramTypes = new Class<?>[] {
            String.class
        };
        Method m =
                helper.findUniqueMethod(StringBuilder.class, methodName,
                        paramTypes, new Object[] {
                            STR_PARAM
                        });
        assertEquals("Wrong method name", methodName, m.getName());
        assertTrue("Wrong parameter types",
                Arrays.equals(paramTypes, m.getParameterTypes()));
        assertEquals("Wrong return type", StringBuilder.class,
                m.getReturnType());
    }

    /**
     * Tests findUniqueMethod() if no arguments are provided. Then no results
     * should be found.
     */
    @Test(expected = InjectionException.class)
    public void testFindUniqueMethodNoArgs()
    {
        Class<?>[] paramTypes = new Class<?>[] {
                String.class, Integer.TYPE, ReflectionTestClass.Mode.class
        };
        helper.findUniqueMethod(ReflectionTestClass.class, METHOD_INIT,
                paramTypes, null);
    }

    /**
     * Tests findUniqueMethod() if the wrong number of concrete arguments is
     * passed in. No results should be found.
     */
    @Test(expected = InjectionException.class)
    public void testFindUniqueMethodWrongArgsNo()
    {
        Class<?>[] paramTypes = new Class<?>[] {
                String.class, Integer.TYPE, ReflectionTestClass.Mode.class
        };
        helper.findUniqueMethod(ReflectionTestClass.class, METHOD_INIT,
                paramTypes, new Object[0]);
    }

    /**
     * Tests findUniqueMethod() if for a primitive parameter a null value is
     * passed. This is not allowed.
     */
    @Test(expected = InjectionException.class)
    public void testFindUniqueMethodPrimitivumNull()
    {
        Class<?>[] paramTypes =
                new Class<?>[] {
                        String.class, Object.class, Integer.TYPE,
                        ReflectionTestClass.Mode.class
                };
        Object[] args = new Object[] {
                STR_PARAM, "anotherString", null, MODE_PARAM
        };
        helper.findUniqueMethod(ReflectionTestClass.class, METHOD_INIT,
                paramTypes, args);
    }

    /**
     * Tests findUniqueMethod() if the parameter list cannot be matched.
     */
    @Test(expected = InjectionException.class)
    public void testFindUniqueMethodNoMatch()
    {
        Class<?>[] paramTypes = new Class<?>[] {
            getClass()
        };
        Object[] args = new Object[] {
            this
        };
        helper.findUniqueMethod(ReflectionTestClass.class, METHOD_INIT,
                paramTypes, args);
    }

    /**
     * Tests findUniqueMethod() if there are too many matches.
     */
    @Test(expected = InjectionException.class)
    public void testFindUniqueMethodTooManyMatches()
    {
        Class<?>[] paramTypes = new Class<?>[] {
                String.class, Integer.TYPE, null
        };
        Object[] args = new Object[] {
                STR_PARAM, INT_PARAM, null
        };
        helper.findUniqueMethod(ReflectionTestClass.class, METHOD_INIT,
                paramTypes, args);
    }

    /**
     * Tests findUniqueConstructor() if the desired constructor is exactly
     * defined.
     */
    @Test
    public void testFindUniqueConstructorSpecific()
    {
        Class<?>[] paramTypes = new Class<?>[] {
                String.class, Integer.TYPE
        };
        Object[] args = new Object[] {
                STR_PARAM, INT_PARAM
        };
        Constructor<ReflectionTestClass> ctor =
                helper.findUniqueConstructor(ReflectionTestClass.class,
                        paramTypes, args);
        assertTrue("Wrong constructor: " + ctor,
                Arrays.equals(paramTypes, ctor.getParameterTypes()));
    }

    /**
     * Tests findUniqueConstructor() if the constructor can only be found if the
     * parameter types are not matched exactly.
     */
    @Test
    public void testFindUniqueConstructorAssignable()
    {
        Class<?>[] paramTypes = new Class<?>[] {
                getClass(), Integer.TYPE
        };
        Object[] args = new Object[] {
                this, INT_PARAM
        };
        Constructor<ReflectionTestClass> ctor =
                helper.findUniqueConstructor(ReflectionTestClass.class,
                        paramTypes, args);
        paramTypes[0] = Object.class;
        assertTrue("Wrong constructor: " + ctor,
                Arrays.equals(paramTypes, ctor.getParameterTypes()));
    }

    /**
     * Tests findUniqueConstructor() if the argument types need to be taken into
     * account in order to find a unique match.
     */
    @Test
    public void testFindUniqueConstructorArgumentTypes()
    {
        Class<?>[] paramTypes = new Class<?>[] {
                null, Integer.TYPE
        };
        Object[] args = new Object[] {
                MODE_PARAM, INT_PARAM
        };
        Constructor<ReflectionTestClass> ctor =
                helper.findUniqueConstructor(ReflectionTestClass.class,
                        paramTypes, args);
        paramTypes[0] = Object.class;
        assertTrue("Wrong constructor: " + ctor,
                Arrays.equals(paramTypes, ctor.getParameterTypes()));
    }

    /**
     * Tests whether the standard constructor can be found.
     */
    @Test
    public void testFindUniqueConstructorStd()
    {
        Class<?>[] paramTypes = new Class<?>[0];
        Constructor<ReflectionTestClass> ctor =
                helper.findUniqueConstructor(ReflectionTestClass.class,
                        paramTypes, null);
        assertEquals("Wrong constructor: " + ctor, 0,
                ctor.getParameterTypes().length);
    }

    /**
     * Tests findUniqueConstructor() if there are too many matches.
     */
    @Test(expected = InjectionException.class)
    public void testFindUniqueConstructorTooManyMatches()
    {
        Class<?>[] paramTypes = new Class<?>[] {
                null, Integer.TYPE
        };
        Object[] args = new Object[] {
                null, INT_PARAM
        };
        helper.findUniqueConstructor(ReflectionTestClass.class, paramTypes,
                args);
    }

    /**
     * Tests findUniqueConstructor() if no match can be found.
     */
    @Test(expected = InjectionException.class)
    public void testFindUniqueConstructorNoMatch()
    {
        Class<?>[] paramTypes = new Class<?>[] {
                Float.TYPE, Integer.TYPE
        };
        Object[] args = new Object[] {
                3.14f, INT_PARAM
        };
        helper.findUniqueConstructor(ReflectionTestClass.class, paramTypes,
                args);
    }

    /**
     * Tests that findUniqueConstructor() does not allow null arguments for
     * primitive parameters.
     */
    @Test(expected = InjectionException.class)
    public void testFindUniqueConstructorPrimitiveNull()
    {
        Class<?>[] paramTypes = new Class<?>[] {
                String.class, Integer.TYPE
        };
        Object[] args = new Object[] {
                STR_PARAM, null
        };
        helper.findUniqueConstructor(ReflectionTestClass.class, paramTypes,
                args);
    }

    /**
     * Tests the exception message produced by findUniqueConstructor().
     */
    @Test
    public void testFindUniqueConstructorExMsg()
    {
        Class<?>[] paramTypes = new Class<?>[] {
                null, Integer.TYPE
        };
        Object[] args = new Object[] {
                null, INT_PARAM
        };
        try
        {
            helper.findUniqueConstructor(ReflectionTestClass.class, paramTypes,
                    args);
            fail("Expected exception not thrown!");
        }
        catch (InjectionException iex)
        {
            String msg = iex.getMessage();
            assertTrue(
                    "Constructor not found in message: " + msg,
                    msg.indexOf(ReflectionTestClass.class.getName() + ".<init>(") > 0);
        }
    }

    /**
     * Tries to invoke a static method without a target class.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvokeStaticMethodNoTargetClass()
    {
        helper.invokeStaticMethod(null, "getInstance", null, new Object[] {
                STR_PARAM, INT_PARAM
        });
    }

    /**
     * Tries to invoke a non-static method through invokeStaticMethod().
     */
    @Test(expected = InjectionException.class)
    public void testInvokeStaticMethodNotStatic()
    {
        Class<?>[] paramTypes = new Class<?>[] {
                ReflectionTestClass.Mode.class, String.class, Integer.TYPE
        };
        Object[] args = new Object[] {
                MODE_PARAM, STR_PARAM, INT_PARAM
        };
        helper.invokeStaticMethod(ReflectionTestClass.class, METHOD_INIT,
                paramTypes, args);
    }

    /**
     * Tests a successful invocation of a static method.
     */
    @Test
    public void testInvokeStaticMethodSuccess()
    {
        ReflectionTestClass obj =
                (ReflectionTestClass) helper.invokeStaticMethod(
                        ReflectionTestClass.class, "getInstance", null,
                        new Object[] {
                                STR_PARAM, INT_PARAM
                        });
        assertEquals("Wrong string", STR_PARAM, obj.getStringProp());
        assertEquals("Wrong int property", INT_PARAM.intValue(),
                obj.getIntProp());
    }

    /**
     * Tries to invoke an instance method on a null instance.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvokeInstanceMethodNull()
    {
        helper.invokeInstanceMethod(null, METHOD_INIT, null, new Object[] {
                STR_PARAM, this, INT_PARAM, MODE_PARAM
        });
    }

    /**
     * Tests a successful invocation of an instance method.
     */
    @Test
    public void testInvokeInstanceMethodSuccess()
    {
        Class<?>[] paramTypes = new Class<?>[] {
                String.class, Integer.TYPE, String.class
        };
        Object[] args = new Object[] {
                STR_PARAM, INT_PARAM, MODE_PARAM.name()
        };
        ReflectionTestClass obj = new ReflectionTestClass();
        assertNull("Got a result",
                helper.invokeInstanceMethod(obj, METHOD_INIT, paramTypes, args));
        assertEquals("Wrong string property", STR_PARAM, obj.getStringProp());
        assertEquals("Wrong int property", INT_PARAM.intValue(),
                obj.getIntProp());
        assertEquals("Wrong data", MODE_PARAM.name(), obj.getData());
    }

    /**
     * Tests whether type conversion is performed when invoking a method.
     */
    @Test
    public void testInvokeMethodWithConversions()
    {
        Class<?>[] paramTypes = new Class<?>[] {
                String.class, Integer.TYPE, String.class
        };
        Object[] args = new Object[] {
                STR_PARAM, String.valueOf(INT_PARAM), MODE_PARAM.name()
        };
        ReflectionTestClass obj = new ReflectionTestClass();
        assertNull("Got a result",
                helper.invokeInstanceMethod(obj, METHOD_INIT, paramTypes, args));
        assertEquals("Wrong int property", INT_PARAM.intValue(),
                obj.getIntProp());
    }

    /**
     * Tests whether type conversions of Enum parameters are possible.
     */
    @Test
    public void testInvokeMethodWithConversionEnum()
    {
        Class<?>[] paramTypes = new Class<?>[] {
                ReflectionTestClass.Mode.class, String.class, Integer.TYPE
        };
        Object[] args = new Object[] {
                MODE_PARAM.name(), STR_PARAM, String.valueOf(INT_PARAM)
        };
        ReflectionTestClass obj = new ReflectionTestClass();
        assertNull("Got a result",
                helper.invokeInstanceMethod(obj, METHOD_INIT, paramTypes, args));
        assertEquals("Wrong mode", MODE_PARAM, obj.getMode());
    }

    /**
     * Tests whether the correct result is returned from a method invocation.
     */
    @Test
    public void testInvokeMethodResult()
    {
        ReflectionTestClass obj = new ReflectionTestClass();
        obj.setMode(MODE_PARAM);
        assertEquals("Wrong result", MODE_PARAM,
                helper.invokeMethod(null, obj, "getMode", null, null));
    }

    /**
     * Tries to invoke a method which cannot be found.
     */
    @Test(expected = InjectionException.class)
    public void testInvokeMethodNotFound()
    {
        helper.invokeMethod(ReflectionTestClass.class,
                new ReflectionTestClass(), "nonExistingMethod?",
                new Class<?>[0], null);
    }

    /**
     * Tests invokeMethod() if the target class is not compatible with the
     * instance.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvokeMethodInvalidClass()
    {
        Class<?>[] paramTypes = new Class<?>[] {
                String.class, Integer.TYPE, String.class
        };
        Object[] args = new Object[] {
                STR_PARAM, INT_PARAM, MODE_PARAM.name()
        };
        ReflectionTestClass obj = new ReflectionTestClass();
        helper.invokeMethod(getClass(), obj, METHOD_INIT, paramTypes, args);
    }

    /**
     * Tests invokeMethod() if a data type conversion is not possible.
     */
    @Test(expected = InjectionException.class)
    public void testInvokeMethodConversionNotPossible()
    {
        Class<?>[] paramTypes = new Class<?>[] {
                String.class, Integer.TYPE, String.class
        };
        Object[] args = new Object[] {
                STR_PARAM, this, MODE_PARAM.name()
        };
        ReflectionTestClass obj = new ReflectionTestClass();
        helper.invokeMethod(null, obj, METHOD_INIT, paramTypes, args);
    }

    /**
     * Tests convertArguments() if no conversion is required. In this case the
     * same array should be returned.
     */
    @Test
    public void testConvertArgumentsNoChange()
    {
        Class<?>[] types = new Class<?>[] {
                String.class, Integer.TYPE
        };
        Object[] args = new Object[] {
                STR_PARAM, INT_PARAM
        };
        assertSame("Array was changed", args,
                helper.convertArguments(types, args));
    }

    /**
     * Tries to invoke a constructor without a target class.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvokeConstructorNoTargetClass()
    {
        helper.invokeConstructor(null, new Class<?>[0], null);
    }

    /**
     * Tries to invoke a non existing constructor.
     */
    @Test(expected = InjectionException.class)
    public void testInvokeConstructorNotFound()
    {
        Class<?>[] paramTypes = new Class<?>[] {
                String.class, String.class, String.class
        };
        Object[] args = new Object[] {
                STR_PARAM, STR_PARAM, STR_PARAM
        };
        helper.invokeConstructor(ReflectionTestClass.class, paramTypes, args);
    }

    /**
     * Tests a successful invocation of a constructor.
     */
    @Test
    public void testInvokeConstructorSuccess()
    {
        Class<?>[] paramTypes = new Class<?>[] {
                String.class, Integer.TYPE
        };
        Object[] args = new Object[] {
                STR_PARAM, INT_PARAM
        };
        ReflectionTestClass obj =
                helper.invokeConstructor(ReflectionTestClass.class, paramTypes,
                        args);
        assertEquals("Wrong string property", STR_PARAM, obj.getStringProp());
        assertEquals("Wrong int property", INT_PARAM.intValue(),
                obj.getIntProp());
    }

    /**
     * Tests whether type conversions are performed when invoking a constructor.
     */
    @Test
    public void testInvokeConstructorWithConversion()
    {
        Class<?>[] paramTypes = new Class<?>[] {
                String.class, Integer.TYPE
        };
        Object[] args = new Object[] {
                STR_PARAM, String.valueOf(INT_PARAM)
        };
        ReflectionTestClass obj =
                helper.invokeConstructor(ReflectionTestClass.class, paramTypes,
                        args);
        assertEquals("Wrong int property", INT_PARAM.intValue(),
                obj.getIntProp());
    }

    /**
     * Tests whether a property can be set directly.
     */
    @Test
    public void testSetProperty()
    {
        ReflectionTestClass obj = new ReflectionTestClass();
        helper.setProperty(obj, "stringProp", STR_PARAM);
        assertEquals("Wrong string property", STR_PARAM, obj.getStringProp());
    }

    /**
     * Tests whether type conversion is performed when setting a property.
     */
    @Test
    public void testSetPropertyWithConversion()
    {
        ReflectionTestClass obj = new ReflectionTestClass();
        helper.setProperty(obj, "intProp", String.valueOf(INT_PARAM));
        assertEquals("Wrong int property", INT_PARAM.intValue(),
                obj.getIntProp());
    }

    /**
     * Tests whether a property can be set to null.
     */
    @Test
    public void testSetPropertyNullValue()
    {
        ReflectionTestClass obj = new ReflectionTestClass();
        obj.setStringProp(STR_PARAM);
        helper.setProperty(obj, "stringProp", null);
        assertNull("Property not changed", obj.getStringProp());
    }

    /**
     * Tests setProperty() if a conversion to an Enum class is involved.
     */
    @Test
    public void testSetPropertyEnumConversion()
    {
        ReflectionTestClass obj = new ReflectionTestClass();
        helper.setProperty(obj, "mode", MODE_PARAM.name());
        assertEquals("Wrong mode", MODE_PARAM, obj.getMode());
    }

    /**
     * Tries to set a read-only property.
     */
    @Test(expected = InjectionException.class)
    public void testSetPropertyReadOnly()
    {
        helper.setProperty(new ReflectionTestClass(), "readOnlyProperty",
                STR_PARAM);
    }

    /**
     * Tries to set a non existing property.
     */
    @Test(expected = InjectionException.class)
    public void testSetPropertNonExisting()
    {
        helper.setProperty(new ReflectionTestClass(), "nonExprop", STR_PARAM);
    }

    /**
     * Tries to set a property value if type conversion is not possible.
     */
    @Test(expected = InjectionException.class)
    public void testSetPropertyNonSupportedType()
    {
        helper.setProperty(new ReflectionTestClass(), "testCase", STR_PARAM);
    }

    /**
     * Tries to set a null property on a bean. This is an illegal argument.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetPropertyNull()
    {
        helper.setProperty(new ReflectionTestClass(), null, STR_PARAM);
    }

    /**
     * Tries to set a private property.
     */
    @Test(expected = InjectionException.class)
    public void testSetPropertyPrivate()
    {
        helper.setProperty(new ReflectionTestClass(), "privateProperty",
                STR_PARAM);
    }

    /**
     * Tests setPoperty() if the setter throws an exception. This should be
     * redirected.
     */
    @Test(expected = InjectionException.class)
    public void testSetPropertyException()
    {
        helper.setProperty(new ReflectionTestClass(), "exceptionProperty",
                STR_PARAM);
    }

    /**
     * Tries to set a property on a null bean.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetPropertyNullBean()
    {
        helper.setProperty(null, "intProp", INT_PARAM);
    }

    /**
     * Tries to set a primitive property to null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetPropertyPrimitiveNull()
    {
        helper.setProperty(new ReflectionTestClass(), "intProp", null);
    }
}
