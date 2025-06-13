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

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;

import junit.framework.TestCase;

/**
 * Test class for FloatTransformer.
 *
 * @author Oliver Heger
 * @version $Id: TestFloatTransformer.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestFloatTransformer extends TestCase
{
    /** Constant for the delta for comparisons. */
    private static final float DELTA = 0.001f;

    /** The transformer to be tested. */
    private FloatTransformer transformer;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        transformer = new FloatTransformer();
    }

    /**
     * Tests converting a number to a float.
     */
    public void testConvert()
    {
        final float value = 123.45f;
        Double d = Double.valueOf(value);
        Float f = transformer.convert(d);
        assertEquals("Wrong float value", value, f.floatValue(), DELTA);
    }

    /**
     * Tests converting a number that is too small.
     */
    public void testConvertTooSmall()
    {
        checkConvertOutOfRange(Float.MIN_VALUE / 2.0);
    }

    /**
     * Tests converting a number that is too big.
     */
    public void testConvertTooBig()
    {
        checkConvertOutOfRange(Float.MAX_VALUE * 2.0);
    }

    /**
     * Tests converting a value to a float that is too big or small. This should
     * cause an exception.
     *
     * @param value the value
     */
    private void checkConvertOutOfRange(double value)
    {
        try
        {
            transformer.convert(value);
            fail("Value out of range was not detected!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tests fetching a configuration property.
     */
    public void testFetchProperty()
    {
        Configuration config = EasyMock.createMock(Configuration.class);
        final Float value = 100f;
        final Float defVal = 200f;
        final String property = "testProperty";
        EasyMock.expect(config.getFloat(property, defVal)).andReturn(value);
        EasyMock.replay(config);
        assertEquals("Wrong property value", value, transformer.fetchProperty(
                config, property, defVal));
        EasyMock.verify(config);
    }
}
