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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code ConversionHelper}.
 *
 * @author Oliver Heger
 * @version $Id: TestConversionHelper.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestConversionHelper
{
    /** Constant for a test string. */
    private static final String STR_PARAM = "testStringParameterValue";

    /** Constant for a test integer. */
    private static final Integer INT_PARAM = 800;

    /** The instance to be tested. */
    private ConversionHelper helper;

    @Before
    public void setUp() throws Exception
    {
        helper = new ConversionHelper();
    }

    /**
     * Tests whether a parent is set if the instance was created using the
     * standard constructor.
     */
    @Test
    public void testGetParentDefault()
    {
        assertNull("Got a parent", helper.getParent());
    }

    /**
     * Tests whether a parent can be queried that was set when creating the
     * instance.
     */
    @Test
    public void testGetParent()
    {
        ConversionHelper child = new ConversionHelper(helper);
        assertSame("Wrong parent", helper, child.getParent());
    }

    /**
     * Tries to register a converter without a target class.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRegisterConverterNoTargetClass()
    {
        helper.registerConverter(EasyMock.createNiceMock(Converter.class), null);
    }

    /**
     * Tries to register a null converter.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRegisterConverterNoConverter()
    {
        helper.registerConverter(null, ReflectionTestClass.class);
    }

    /**
     * Tries to register a base class converter without a target class.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRegisterBaseConverterNoTargetClass()
    {
        helper.registerBaseClassConverter(
                EasyMock.createNiceMock(Converter.class), null);
    }

    /**
     * Tries to register a null base class converter.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRegisterBaseConverterNoConverter()
    {
        helper.registerBaseClassConverter(null, ReflectionTestClass.class);
    }

    /**
     * Tries to convert an object to a null target class.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConvertNoTargetClass()
    {
        helper.convert(null, STR_PARAM);
    }

    /**
     * Tests convert() if no matching converter can be found.
     */
    @Test(expected = InjectionException.class)
    public void testConvertUnknownClass()
    {
        helper.convert(getClass(), STR_PARAM);
    }

    /**
     * Tests whether a null value can be converted.
     */
    @Test
    public void testConvertNullValue()
    {
        assertNull("Wrong result", helper.convert(getClass(), null));
    }

    /**
     * Tests convert() if the value is already an instance of the target class.
     */
    @Test
    public void testConvertNoConversionNeeded()
    {
        assertSame("Wrong result", this, helper.convert(getClass(), this));
    }

    /**
     * Tests convert() if a custom converter is involved.
     */
    @Test
    public void testConvertRegisteredConverter()
    {
        Converter conv = EasyMock.createMock(Converter.class);
        Collection<?> col = EasyMock.createMock(Collection.class);
        EasyMock.expect(conv.convert(Collection.class, STR_PARAM)).andReturn(
                col);
        EasyMock.replay(conv, col);
        helper.registerConverter(conv, Collection.class);
        assertSame("Wrong converted object", col,
                helper.convert(Collection.class, STR_PARAM));
        EasyMock.verify(conv, col);
    }

    /**
     * Tests convert() if a base class converter has to do the job.
     */
    @Test
    public void testConvertRegisteredBaseConverter()
    {
        Converter conv = EasyMock.createMock(Converter.class);
        List<?> list = EasyMock.createMock(List.class);
        EasyMock.expect(conv.convert(List.class, STR_PARAM)).andReturn(list);
        EasyMock.replay(conv, list);
        helper.registerBaseClassConverter(conv, Collection.class);
        assertSame("Wrong converted object", list,
                helper.convert(List.class, STR_PARAM));
        EasyMock.verify(conv, list);
    }

    /**
     * Tests convert() if both a specific and a base class converter are
     * involved. The specific converter should be preferred.
     */
    @Test
    public void testConvertBaseAndSpecificConverter()
    {
        Converter convBase = EasyMock.createMock(Converter.class);
        Converter convSpec = EasyMock.createMock(Converter.class);
        List<?> list = EasyMock.createMock(List.class);
        EasyMock.expect(convSpec.convert(List.class, STR_PARAM))
                .andReturn(list);
        EasyMock.replay(convBase, convSpec, list);
        helper.registerBaseClassConverter(convBase, Collection.class);
        helper.registerConverter(convSpec, List.class);
        assertSame("Wrong converted object", list,
                helper.convert(List.class, STR_PARAM));
        EasyMock.verify(convBase, convSpec, list);
    }

    /**
     * Tests whether the order of base class converters is taken into account.
     */
    @Test
    public void testConvertBaseConvertersOrder()
    {
        Converter convCol = EasyMock.createMock(Converter.class);
        Converter convList = EasyMock.createMock(Converter.class);
        List<?> list = EasyMock.createMock(List.class);
        EasyMock.expect(convList.convert(List.class, STR_PARAM))
                .andReturn(list);
        EasyMock.replay(convCol, convList, list);
        helper.registerBaseClassConverter(convCol, Collection.class);
        helper.registerBaseClassConverter(convList, List.class);
        assertSame("Wrong converted object", list,
                helper.convert(List.class, STR_PARAM));
        EasyMock.verify(convCol, convList, list);
    }

    /**
     * Tests whether ConversionExceptions are handled correctly.
     */
    @Test
    public void testConvertEx()
    {
        Converter conv = EasyMock.createMock(Converter.class);
        ConversionException cex = new ConversionException(STR_PARAM);
        EasyMock.expect(conv.convert(Collection.class, STR_PARAM))
                .andThrow(cex);
        EasyMock.replay(conv);
        helper.registerConverter(conv, Collection.class);
        try
        {
            helper.convert(Collection.class, STR_PARAM);
            fail("ConversionException not detected!");
        }
        catch (InjectionException iex)
        {
            assertEquals("Wrong root cause", cex, iex.getCause());
        }
        EasyMock.verify(conv);
    }

    /**
     * Tests a standard conversion.
     */
    @Test
    public void testConvertStandard()
    {
        assertEquals("Wrong result", INT_PARAM,
                helper.convert(Integer.class, String.valueOf(INT_PARAM)));
    }

    /**
     * Tests a standard conversion to a primitive data type.
     */
    @Test
    public void testConvertStandardPrimitiveType()
    {
        assertEquals("Wrong result", INT_PARAM,
                helper.convert(Integer.TYPE, String.valueOf(INT_PARAM)));
    }

    /**
     * Tests a conversion that involves an enumeration class.
     */
    @Test
    public void testConvertEnum()
    {
        assertEquals("Wrong result", ReflectionTestClass.Mode.CRITICAL,
                helper.convert(ReflectionTestClass.Mode.class,
                        ReflectionTestClass.Mode.CRITICAL.name()));
    }

    /**
     * Tests a conversion with an enumeration class if a parent instance is set.
     * In this case the converter should be obtained from the parent.
     */
    @Test
    public void testConvertEnumWithParent()
    {
        ConversionHelper child = new ConversionHelper(helper);
        assertEquals("Wrong result", ReflectionTestClass.Mode.DEVELOPMENT,
                child.convert(ReflectionTestClass.Mode.class,
                        ReflectionTestClass.Mode.DEVELOPMENT.name()));
    }

    /**
     * Tests a conversion if the converter has to be obtained from the parent.
     */
    @Test
    public void testConvertFromParent()
    {
        helper.registerConverter(new Converter()
        {
            public Object convert(@SuppressWarnings("rawtypes") Class type,
                    Object value)
            {
                return TestConversionHelper.this;
            }
        }, getClass());
        ConversionHelper child = new ConversionHelper(helper);
        assertSame("Wrong conversion", this,
                child.convert(getClass(), STR_PARAM));
    }
}
