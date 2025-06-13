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
package net.sf.jguiraffe.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for LongTransformer.
 *
 * @author Oliver Heger
 * @version $Id: TestLongTransformer.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestLongTransformer extends AbstractTransformerTest
{
    /** The transformer to be tested. */
    private LongTransformer transformer;

    @Before
    public void setUp() throws Exception
    {
        transformer = new LongTransformer();
    }

    /**
     * Tests the convert() method when the passed in object is already a Long.
     */
    @Test
    public void testConvertLong()
    {
        Long l = 123456789L;
        assertEquals("Wrong long returned", l, transformer.convert(l));
    }

    /**
     * Tests the convert() method when a type conversion is required.
     */
    @Test
    public void testConvertTypeConversion()
    {
        Long l = transformer.convert(12345.1);
        assertEquals("Wrong long value", 12345L, l.longValue());
    }

    /**
     * Tests converting a number to a long that is too big. This should cause an
     * exception.
     */
    @Test
    public void testConvertTooBig()
    {
        double d = Long.MAX_VALUE;
        d *= 2;
        checkConvertLongOutOfRange(d);
    }

    /**
     * Tests converting a number to a long that is too small. This should cause
     * an exception.
     */
    @Test
    public void testConvertTooSmall()
    {
        double d = Long.MIN_VALUE;
        d *= 2;
        checkConvertLongOutOfRange(d);
    }

    /**
     * Tests the convert() method when the passed in number does not fit in the
     * value range of a long. This should cause an exception.
     *
     * @param n the number to convert
     */
    private void checkConvertLongOutOfRange(Number n)
    {
        try
        {
            transformer.convert(n);
            fail("Value out of range was not detected!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tests fetching a property from a configuration.
     */
    @Test
    public void testFetchProperty()
    {
        Configuration config = EasyMock.createMock(Configuration.class);
        final String prop = "testProperty";
        final Long defValue = 41L;
        final Long value = 42L;
        EasyMock.expect(config.getLong(prop, defValue)).andReturn(value);
        EasyMock.replay(config);
        assertEquals("Wrong property value", value, transformer.fetchProperty(
                config, prop, defValue));
        EasyMock.verify(config);
    }

    /**
     * Tests the transform() method for a long number.
     */
    @Test
    public void testTransform() throws Exception
    {
        setUpContextMock(new HashMap<String, Object>());
        replayMocks();
        Long value = (Long) transformer.transform("1.000.000", context);
        assertEquals("Wrong value", 1000000L, value.longValue());
        verifyMocks();
    }
}
