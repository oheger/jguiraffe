/*
 * Copyright 2006-2013 The JGUIraffe Team.
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.easymock.EasyMock;

import junit.framework.TestCase;

/**
 * Test class for ToStringTransformer.
 *
 * @author Oliver Heger
 * @version $Id: TestToStringTransformer.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestToStringTransformer extends TestCase
{
    /** Stores the transformer context. */
    private TransformerContext ctx;

    /** The transformer to be tested. */
    private ToStringTransformer transformer;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        transformer = new ToStringTransformer();
    }

    /**
     * Clears the test environment. If a mock for the transformer context has
     * been created, it is now verified.
     */
    @Override
    protected void tearDown() throws Exception
    {
        if (ctx != null)
        {
            EasyMock.verify(ctx);
        }
        super.tearDown();
    }

    /**
     * Creates a mock for a transformer context. This mock will return a fixed
     * locale. If a map with properties is provided, it will also be returned by
     * the <code>properties()</code> method.
     *
     * @param props the properties (can be <b>null</b>)
     * @return the initialized context mock object
     */
    private TransformerContext setUpContext(Map<String, Object> props)
    {
        ctx = EasyMock.createMock(TransformerContext.class);
        EasyMock.expect(ctx.getLocale()).andStubReturn(Locale.GERMANY);
        EasyMock.expect(ctx.properties()).andStubReturn(
                (props != null) ? props : new HashMap<String, Object>());
        EasyMock.replay(ctx);
        return ctx;
    }

    /**
     * Creates a mock for a transformer context that does not have any specific
     * properties.
     *
     * @return the mock for the context
     */
    private TransformerContext setUpContext()
    {
        return setUpContext(null);
    }

    /**
     * Tests defaults for a newly created instance.
     */
    public void testInit()
    {
        assertEquals("Wrong default date style", DateFormat.SHORT, transformer
                .getDateFormatStyle());
        assertEquals("Wrong minimum fraction digits", 0, transformer
                .getMinimumFractionDigits());
        assertEquals("Wrong maximum fraction digits", 2, transformer
                .getMaximumFractionDigits());
        assertFalse("Wrong grouping flag", transformer.isGroupingUsed());
    }

    /**
     * Tests transforming a null object.
     */
    public void testTransformNull() throws Exception
    {
        assertEquals("Wrong result for null", "", transformer.transform(null,
                setUpContext()));
    }

    /**
     * Helper method for creating a date object.
     *
     * @param year the year
     * @param month the month
     * @param day the day
     * @return the initialized date object
     */
    private Date createDate(int year, int month, int day)
    {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        return cal.getTime();
    }

    /**
     * Tests the default transformation of a date.
     */
    public void testTransformDate() throws Exception
    {
        Date dt = createDate(2008, Calendar.JANUARY, 11);
        assertEquals("Wrong formatted date", "11.01.08", transformer.transform(
                dt, setUpContext()));
    }

    /**
     * Tests transforming a date when a specific style is set.
     */
    public void testTransformDateStyle() throws Exception
    {
        transformer.setDateFormatStyle(DateFormat.MEDIUM);
        Date dt = createDate(2008, Calendar.JANUARY, 11);
        assertEquals("Wrong formatted date", "11.01.2008", transformer
                .transform(dt, setUpContext()));
    }

    /**
     * Tests transforming a date when the style is determined by properties.
     */
    public void testTransformDateStyleProperties() throws Exception
    {
        transformer.setDateFormatStyle(DateFormat.MEDIUM);
        Date dt = createDate(2008, Calendar.JANUARY, 11);
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(ToStringTransformer.PROP_DATE_FORMAT_STYLE, DateFormat.SHORT);
        assertEquals("Wrong formatted date", "11.01.08", transformer.transform(
                dt, setUpContext(props)));
    }

    /**
     * Tests transforming a float number.
     */
    public void testTransformFloat() throws Exception
    {
        assertEquals("Wrong formatted float", "1234,45", transformer.transform(
                new Float(1234.45f), setUpContext()));
    }

    /**
     * Tests transforming a double number.
     */
    public void testTransformDouble() throws Exception
    {
        assertEquals("Wrong formatted double", "1234,45", transformer
                .transform(new Double(1234.45), setUpContext()));
    }

    /**
     * Tests transforming a big decimal number.
     */
    public void testTransformDigDecimal() throws Exception
    {
        assertEquals("Wrong formatted big decimal", "1234,45", transformer
                .transform(new BigDecimal("1234.45"), setUpContext()));
    }

    /**
     * Tests the different properties for transforming a decimal number.
     */
    public void testTransformDecimalStyle() throws Exception
    {
        transformer.setGroupingUsed(true);
        transformer.setMaximumFractionDigits(3);
        transformer.setMinimumFractionDigits(1);
        TransformerContext ctx = setUpContext();
        assertEquals("Wrong transformation (1)", "1.234,0", transformer
                .transform(1234.0, ctx));
        assertEquals("Wrong transformation (2)", "345,123", transformer
                .transform(345.1230001, ctx));
    }

    /**
     * Tests whether properties specified at the context override the settings
     * of the transformer object for a decimal number.
     */
    public void testTransformDecimalStyleProperties() throws Exception
    {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(ToStringTransformer.PROP_MAXIMUM_FRACTION_DIGITS, 3);
        props.put(ToStringTransformer.PROP_MINIMUM_FRACTION_DIGITS, 1);
        props.put(ToStringTransformer.PROP_GROUPING_USED, Boolean.TRUE);
        TransformerContext ctx = setUpContext(props);
        assertEquals("Wrong transformation (1)", "1.234,0", transformer
                .transform(1234.0, ctx));
        assertEquals("Wrong transformation (2)", "345,123", transformer
                .transform(345.1230001, ctx));
    }

    /**
     * Tests transforming an integer number.
     */
    public void testTransformInteger() throws Exception
    {
        assertEquals("Wrong transformed integer", "1234", transformer
                .transform(1234, setUpContext()));
    }

    /**
     * Tests transforming a long number.
     */
    public void testTransformLong() throws Exception
    {
        assertEquals("Wrong transformed long", "56987542548", transformer
                .transform(56987542548L, setUpContext()));
    }

    /**
     * Tests transforming a big integer.
     */
    public void testTransformBigInteger() throws Exception
    {
        assertEquals("Wrong transformed big int", "3254565874878875474457",
                transformer.transform(new BigInteger("3254565874878875474457"),
                        setUpContext()));
    }

    /**
     * Tests transforming a short number.
     */
    public void testTransformShort() throws Exception
    {
        assertEquals("Wrong transformed short", "5012", transformer.transform(
                new Short((short) 5012), setUpContext()));
    }

    /**
     * Tests transforming a byte.
     */
    public void testTransformByte() throws Exception
    {
        assertEquals("Wrong transformed byte", "64", transformer.transform(Byte
                .valueOf((byte) 64), setUpContext()));
    }

    /**
     * Tests the properties that have impact on the transformation result for an
     * integer number.
     */
    public void testTransformIntegerStyle() throws Exception
    {
        transformer.setGroupingUsed(true);
        TransformerContext ctx = setUpContext();
        assertEquals("Wrong transformation (1)", "1.063.987", transformer
                .transform(1063987, ctx));
        assertEquals("Wrong transformation (2)", "100", transformer.transform(
                100, ctx));
    }

    /**
     * Tests whether properties specified at the context override the settings
     * of the transformer object for an integer number.
     */
    public void testTransformIntegerStyleProperties() throws Exception
    {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(ToStringTransformer.PROP_GROUPING_USED, Boolean.TRUE);
        TransformerContext ctx = setUpContext(props);
        assertEquals("Wrong transformation (1)", "1.063.987", transformer
                .transform(1063987, ctx));
        assertEquals("Wrong transformation (2)", "100", transformer.transform(
                100, ctx));
    }

    /**
     * Tests transforming arbitrary objects. All objects that cannot be dealt
     * with in a specific way are simply handled by invoking their
     * <code>toString()</code> method.
     */
    public void testTransformObject() throws Exception
    {
        StringBuilder buf = new StringBuilder("A test string");
        assertEquals("Wrong transformation", "A test string", transformer
                .transform(buf, setUpContext()));
    }
}
