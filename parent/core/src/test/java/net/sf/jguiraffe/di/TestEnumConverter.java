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
package net.sf.jguiraffe.di;

import static org.junit.Assert.assertEquals;

import org.apache.commons.beanutils.ConversionException;
import org.junit.Test;

/**
 * Test class for {@code EnumConverter}.
 *
 * @author Oliver Heger
 * @version $Id: TestEnumConverter.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestEnumConverter
{
    /**
     * Tests a successful conversion.
     */
    @Test
    public void testConvertSuccess()
    {
        for (ReflectionTestClass.Mode mode : ReflectionTestClass.Mode.values())
        {
            String value = mode.name();
            assertEquals(
                    "Wrong converted value",
                    mode,
                    EnumConverter.getInstance().convert(
                            ReflectionTestClass.Mode.class, value));
        }
    }

    /**
     * Tests convert() if the value is not an enumeration constant.
     */
    @Test(expected = ConversionException.class)
    public void testConvertInvalidConstant()
    {
        EnumConverter.getInstance().convert(ReflectionTestClass.Mode.class,
                this);
    }

    /**
     * Tests convert() if the value cannot be resolved to a constant.
     */
    @Test(expected = ConversionException.class)
    public void testConvertUnknownConstant()
    {
        EnumConverter.getInstance().convert(ReflectionTestClass.Mode.class,
                "non existing constant");
    }

    /**
     * Tests convert() if the target class is no enumeration class.
     */
    @Test(expected = ConversionException.class)
    public void testConvertNoEnumClass()
    {
        EnumConverter.getInstance().convert(getClass(),
                ReflectionTestClass.Mode.CRITICAL);
    }

    /**
     * Tests convert() for a null value.
     */
    @Test(expected = ConversionException.class)
    public void testConvertNullValue()
    {
        EnumConverter.getInstance().convert(ReflectionTestClass.Mode.class,
                null);
    }
}
