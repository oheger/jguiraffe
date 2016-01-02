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
package net.sf.jguiraffe.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for DoubleTransformer and AbstractDecimalTransformer.
 *
 * @author Oliver Heger
 * @version $Id: TestDoubleTransformer.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestDoubleTransformer extends AbstractTransformerTest
{
    /** Constant for the delta for comparisons. */
    private static final double DELTA = 0.0001;

    /** Stores the transformer to be tested. */
    private DoubleTransformer transformer;

    @Before
    public void setUp() throws Exception
    {
        transformer = new DoubleTransformer();
    }

    /**
     * Tests whether a suitable format object is created.
     */
    @Test
    public void testCreateFormat() throws ParseException
    {
        NumberFormat fmt = transformer.createFormat(LOCALE);
        Number n = fmt.parse("1,25");
        assertEquals("Wrong parsed number", 1.25, n.doubleValue(), DELTA);
    }

    /**
     * Tests the convert() method.
     */
    @Test
    public void testConvert()
    {
        final int value = 100;
        BigDecimal bd = new BigDecimal(value);
        Double d = transformer.convert(bd);
        assertEquals("Wrong double value", value, d.doubleValue(), DELTA);
    }

    /**
     * Tests converting a float.
     */
    @Test
    public void testConvertFloat()
    {
        final float value = 12345.5f;
        Double d = transformer.convert(value);
        assertEquals("Wrong double value", (double) value, d.doubleValue(),
                DELTA);
    }

    /**
     * Tests converting a number, which is too big.
     */
    @Test
    public void testConvertTooBig()
    {
        checkConvertOutOfRange(Double.MAX_VALUE, "2");
    }

    /**
     * Tests converting a number, which is too small.
     */
    @Test
    public void testConvertTooSmall()
    {
        checkConvertOutOfRange(Double.MIN_VALUE, "0.5");
    }

    /**
     * Tries converting a number that does not fit into the value range of a
     * double. This should cause an exception.
     *
     * @param val the double value
     * @param factor the multiplication factor
     */
    private void checkConvertOutOfRange(double val, String factor)
    {
        BigDecimal bd = new BigDecimal(val);
        bd = bd.multiply(new BigDecimal(factor));
        try
        {
            transformer.convert(bd);
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
    @Test
    public void testFetchProperty()
    {
        Configuration config = EasyMock.createMock(Configuration.class);
        final Double value = Double.valueOf(100);
        final Double defVal = Double.valueOf(200);
        final String property = "testProperty";
        EasyMock.expect(config.getDouble(property, defVal)).andReturn(value);
        EasyMock.replay(config);
        assertEquals("Wrong configuration property", value, transformer
                .fetchProperty(config, property, defVal));
        EasyMock.verify(config);
    }
}
