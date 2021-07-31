/*
 * Copyright 2006-2021 The JGUIraffe Team.
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for DateTransformerBase.
 *
 * @author Oliver Heger
 * @version $Id: TestDateTransformerBase.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestDateTransformerBase extends AbstractTransformerTest
{
    /** Stores the transformer to test. */
    private DateTransformerBaseTestImpl transformer;

    @Before
    public void setUp() throws Exception
    {
        transformer = new DateTransformerBaseTestImpl();
    }

    /**
     * Helper method for comparing the components of a date.
     *
     * @param dt the date
     * @param year the expected year
     * @param month the expected month
     * @param day the expected day
     * @param hour the expected hour
     * @param min the expected minute
     * @param sec the expected seconds
     * @param millis the expected milliseconds
     */
    private void checkDate(Date dt, int year, int month, int day, int hour,
            int min, int sec, int millis)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        assertEquals("Wrong year", year, cal.get(Calendar.YEAR));
        assertEquals("Wrong month", month, cal.get(Calendar.MONTH));
        assertEquals("Wrong day", day, cal.get(Calendar.DATE));
        assertEquals("Wrong hour", hour, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals("Wrong minute", min, cal.get(Calendar.MINUTE));
        assertEquals("Wrong seconds", sec, cal.get(Calendar.SECOND));
        assertEquals("Wrong millis", millis, cal.get(Calendar.MILLISECOND));
    }

    /**
     * Tests the default values for properties.
     */
    @Test
    public void testPropertyDefaults()
    {
        assertEquals("Wrong style", DateFormat.SHORT, transformer
                .getStyle());
        assertFalse("Wrong lenient flag", transformer.isLenient());
        assertFalse("Wrong after flag", transformer.isAfter());
        assertFalse("Wrong before flag", transformer.isBefore());
        assertFalse("Wrong equal flag", transformer.isEqual());
        assertNull("Reference date already set", transformer.getReferenceDate());
    }

    /**
     * Tests transforming a valid SQL date string.
     */
    @Test
    public void testTransformSqlDateValidDate()
    {
        Date date = transformer.transformSqlDate("2008-01-25");
        checkDate(date, 2008, Calendar.JANUARY, 25, 0, 0, 0, 0);
    }

    /**
     * Tests transforming a valid SQL time string.
     */
    @Test
    public void testTransformSqlDateValidTime()
    {
        Date date = transformer.transformSqlDate("21:05:48");
        checkDate(date, 1970, Calendar.JANUARY, 1, 21, 05, 48, 0);
    }

    /**
     * Tests transforming a valid SQL time stamp string.
     */
    @Test
    public void testTransformSqlDateValidTimeStamp()
    {
        Date date = transformer.transformSqlDate("2008-01-25 21:10:30.123");
        checkDate(date, 2008, Calendar.JANUARY, 25, 21, 10, 30, 123);
    }

    /**
     * Tests transforming an invalid SQL date. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testTransformSqlDateInvalid()
    {
        transformer.transformSqlDate("2008-01-25-21");
    }

    /**
     * Tests querying the reference date when it has been explicitly set.
     */
    @Test
    public void testGetReferenceDatePropertySet()
    {
        Configuration config = EasyMock.createMock(Configuration.class);
        EasyMock.expect(
                config.getString(DateTransformerBase.PROP_REFERENCE_DATE))
                .andReturn(null);
        EasyMock.replay(config);
        transformer.setReferenceDate("2008-01-25");
        Date date = transformer.getReferenceDateProperty(config);
        checkDate(date, 2008, Calendar.JANUARY, 25, 0, 0, 0, 0);
        EasyMock.verify(config);
    }

    /**
     * Tests querying the reference date when it can be obtained from the
     * configuration.
     */
    @Test
    public void testGetReferenceDatePropertyConfig()
    {
        Configuration config = EasyMock.createMock(Configuration.class);
        EasyMock.expect(
                config.getString(DateTransformerBase.PROP_REFERENCE_DATE))
                .andReturn("2008-01-25");
        EasyMock.replay(config);
        transformer.setReferenceDate("2011-11-11");
        Date date = transformer.getReferenceDateProperty(config);
        checkDate(date, 2008, Calendar.JANUARY, 25, 0, 0, 0, 0);
        EasyMock.verify(config);
    }

    /**
     * Tests querying the reference date when it has not been set. In this case
     * the current date should be used.
     */
    @Test
    public void testGetReferenceDatePropertyUndefined()
    {
        Configuration config = EasyMock.createMock(Configuration.class);
        EasyMock.expect(
                config.getString(DateTransformerBase.PROP_REFERENCE_DATE))
                .andReturn(null);
        EasyMock.replay(config);
        Date date = transformer.getReferenceDateProperty(config);
        Calendar cal = Calendar.getInstance();
        checkDate(date, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal
                .get(Calendar.DATE), 0, 0, 0, 0);
        EasyMock.verify(config);
    }

    /**
     * Tests setting the reference date to null.
     */
    @Test
    public void testSetReferenceDateNull()
    {
        transformer.setReferenceDate("2008-01-25");
        transformer.setReferenceDate(null);
        testGetReferenceDatePropertyUndefined();
    }

    /**
     * Tests the isValid() method for a valid date.
     */
    @Test
    public void testIsValidValidDate()
    {
        setUpContextMock(new HashMap<String, Object>());
        replayMocks();
        ValidationResult vr = transformer.isValid("26.01.08", context);
        assertTrue("Date not valid", vr.isValid());
        verifyMocks();
    }

    /**
     * Tests the isValid() method for an only partly valid date. This should
     * fail per default.
     */
    @Test
    public void testIsValidPartlyValidDate()
    {
        setUpContextMock(new HashMap<String, Object>());
        final String testDate = "27.01.08x";
        expectError(ValidationMessageConstants.ERR_INVALID_DATE, testDate);
        replayMocks();
        ValidationResult vr = transformer.isValid(testDate, context);
        checkError(ValidationMessageConstants.ERR_INVALID_DATE, vr);
        verifyMocks();
    }

    /**
     * Tests parsing a lenient date when the lenient mode is turned off (which
     * it is per default).
     */
    @Test
    public void testIsValidLenientFalse()
    {
        setUpContextMock(new HashMap<String, Object>());
        final String lenientTestDate = "32.01.2008";
        expectError(ValidationMessageConstants.ERR_INVALID_DATE,
                lenientTestDate);
        replayMocks();
        checkError(ValidationMessageConstants.ERR_INVALID_DATE, transformer
                .isValid(lenientTestDate, context));
        verifyMocks();
    }

    /**
     * Tests parsing a lenient date when lenient mode is enabled.
     */
    @Test
    public void testIsValidLenientTrue()
    {
        setUpContextMock(new HashMap<String, Object>());
        final String lenientTestDate = "32.01.2008";
        replayMocks();
        transformer.setLenient(true);
        assertTrue("Lenient mode has no effect", transformer.isValid(
                lenientTestDate, context).isValid());
        verifyMocks();
    }

    /**
     * Tests parsing a lenient date when lenient mode is enabled in the
     * context's properties.
     */
    @Test
    public void testIsValidLenientProperties()
    {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(DateTransformerBase.PROP_LENIENT, Boolean.TRUE);
        setUpContextMock(props);
        replayMocks();
        final String lenientTestDate = "32.01.2008";
        assertTrue("Lenient mode from properties has no effect", transformer
                .isValid(lenientTestDate, context).isValid());
        verifyMocks();
    }

    /**
     * Tests validating a null date. This should be valid.
     */
    @Test
    public void testIsValidNull()
    {
        setUpContextMock(new HashMap<String, Object>());
        replayMocks();
        assertTrue("Wrong result for null date", transformer.isValid(null,
                context).isValid());
        verifyMocks();
    }

    /**
     * Tests validating an empty date. This should be valid.
     */
    @Test
    public void testIsValidEmpty()
    {
        setUpContextMock(new HashMap<String, Object>());
        replayMocks();
        assertTrue("Wrong result for null date", transformer.isValid("",
                context).isValid());
        verifyMocks();
    }

    /**
     * Tests the after flag with a valid date.
     */
    @Test
    public void testIsValidAfter()
    {
        transformer.setReferenceDate("2008-01-28");
        transformer.setAfter(true);
        setUpContextMock(new HashMap<String, Object>());
        replayMocks();
        assertTrue("Wrong result for valid date after", transformer.isValid(
                "29.01.08", context).isValid());
        verifyMocks();
    }

    /**
     * Tests the after flag when the date is too small.
     */
    @Test
    public void testIsValidAfterErr()
    {
        transformer.setReferenceDate("2008-01-28");
        transformer.setAfter(true);
        setUpContextMock(new HashMap<String, Object>());
        expectError(ValidationMessageConstants.ERR_DATE_AFTER, "28.01.08");
        replayMocks();
        checkError(ValidationMessageConstants.ERR_DATE_AFTER, transformer
                .isValid("28.01.08", context));
        verifyMocks();
    }

    /**
     * Tests a valid date when the after and equal flags are set.
     */
    @Test
    public void testIsValidAfterEqual()
    {
        transformer.setReferenceDate("2008-01-28");
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(DateTransformerBase.PROP_AFTER, Boolean.TRUE);
        transformer.setEqual(true);
        setUpContextMock(props);
        replayMocks();
        assertTrue("Wrong result for valid date after/equal", transformer
                .isValid("28.01.08", context).isValid());
        verifyMocks();
    }

    /**
     * Tests the after and equal flags when the date is too small.
     */
    @Test
    public void testIsValidAfterEqualErr()
    {
        transformer.setReferenceDate("2008-01-28");
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(DateTransformerBase.PROP_EQUAL, Boolean.TRUE);
        transformer.setAfter(true);
        setUpContextMock(props);
        expectError(ValidationMessageConstants.ERR_DATE_AFTER_EQUAL, "28.01.08");
        replayMocks();
        checkError(ValidationMessageConstants.ERR_DATE_AFTER_EQUAL, transformer
                .isValid("27.01.08", context));
        verifyMocks();
    }

    /**
     * Tests the before flag with a valid date.
     */
    @Test
    public void testIsValidBefore()
    {
        transformer.setReferenceDate("2008-01-28");
        transformer.setBefore(true);
        setUpContextMock(new HashMap<String, Object>());
        replayMocks();
        assertTrue("Wrong result for valid date before", transformer.isValid(
                "27.01.08", context).isValid());
        verifyMocks();
    }

    /**
     * Tests the before flag when the date is too big.
     */
    @Test
    public void testIsValidBeforeErr()
    {
        transformer.setReferenceDate("2008-01-28");
        transformer.setBefore(true);
        setUpContextMock(new HashMap<String, Object>());
        expectError(ValidationMessageConstants.ERR_DATE_BEFORE, "28.01.08");
        replayMocks();
        checkError(ValidationMessageConstants.ERR_DATE_BEFORE, transformer
                .isValid("28.01.08", context));
        verifyMocks();
    }

    /**
     * Tests a valid date when the before and equal flags are set.
     */
    @Test
    public void testIsValidBeforeEqual()
    {
        transformer.setReferenceDate("2008-01-28");
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(DateTransformerBase.PROP_BEFORE, Boolean.TRUE);
        transformer.setEqual(true);
        setUpContextMock(props);
        replayMocks();
        assertTrue("Wrong result for valid date before/equal", transformer
                .isValid("28.01.08", context).isValid());
        verifyMocks();
    }

    /**
     * Tests the before and equal flags when the date is too big.
     */
    @Test
    public void testIsValidBeforeEqualErr()
    {
        transformer.setReferenceDate("2008-01-28");
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(DateTransformerBase.PROP_EQUAL, Boolean.TRUE);
        transformer.setBefore(true);
        setUpContextMock(props);
        expectError(ValidationMessageConstants.ERR_DATE_BEFORE_EQUAL,
                "28.01.08");
        replayMocks();
        checkError(ValidationMessageConstants.ERR_DATE_BEFORE_EQUAL,
                transformer.isValid("29.01.08", context));
        verifyMocks();
    }

    /**
     * Tests transforming a valid date.
     */
    @Test
    public void testTransform() throws Exception
    {
        setUpContextMock(new HashMap<String, Object>());
        replayMocks();
        Date dt = (Date) transformer.transform("29.01.08", context);
        checkDate(dt, 2008, Calendar.JANUARY, 29, 0, 0, 0, 0);
        verifyMocks();
    }

    /**
     * Tests transforming an invalid date. This should cause an exception.
     */
    @Test(expected = ParseException.class)
    public void testTransformInvalidDate() throws Exception
    {
        setUpContextMock(new HashMap<String, Object>());
        replayMocks();
        transformer.transform("32.01.08", context);
    }

    /**
     * Tests whether the lenient flag is taken into account by the transform()
     * method.
     */
    @Test
    public void testTransformLenient() throws Exception
    {
        setUpContextMock(new HashMap<String, Object>());
        replayMocks();
        transformer.setLenient(true);
        Date dt = (Date) transformer.transform("32.01.08", context);
        checkDate(dt, 2008, Calendar.FEBRUARY, 1, 0, 0, 0, 0);
        verifyMocks();
    }

    /**
     * Tests whether the lenient flag is taken into account when set as a
     * property.
     */
    @Test
    public void testTransformLenientProps() throws Exception
    {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(DateTransformerBase.PROP_LENIENT, Boolean.TRUE);
        setUpContextMock(props);
        replayMocks();
        Date dt = (Date) transformer.transform("32.01.08", context);
        checkDate(dt, 2008, Calendar.FEBRUARY, 1, 0, 0, 0, 0);
        verifyMocks();
    }

    /**
     * Tests transforming a date object to a string.
     */
    @Test
    public void testTransformDate() throws Exception
    {
        setUpContextMock(new HashMap<String, Object>());
        replayMocks();
        Calendar cal = Calendar.getInstance();
        cal.set(2008, Calendar.JANUARY, 31);
        assertEquals("Wrong date string", "31.01.08", transformer.transform(cal
                .getTime(), context));
        verifyMocks();
    }

    /**
     * Tests transforming a date object to a string obtaining the style from the
     * properties.
     */
    @Test
    public void testTransformDateStyleProps() throws Exception
    {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(DateTransformerBase.PROP_STYLE, DateFormat.MEDIUM);
        setUpContextMock(props);
        replayMocks();
        Calendar cal = Calendar.getInstance();
        cal.set(2008, Calendar.JANUARY, 31);
        assertEquals("Wrong date string", "31.01.2008", transformer.transform(
                cal.getTime(), context));
        verifyMocks();
    }

    /**
     * Tests transforming a null object. Result should be null.
     */
    @Test
    public void testTransformNull() throws Exception
    {
        setUpContextMock(new HashMap<String, Object>());
        replayMocks();
        assertNull("Wrong result for null date", transformer.transform(null,
                context));
        verifyMocks();
    }

    /**
     * Tests transforming an empty string. Result should be null.
     */
    @Test
    public void testTransformEmpty() throws Exception
    {
        setUpContextMock(new HashMap<String, Object>());
        replayMocks();
        assertNull("Wrong result for empty string", transformer.transform("",
                context));
        verifyMocks();
    }

    /**
     * Tests updating the date part of a date/time object.
     */
    @Test
    public void testUpdateDatePart()
    {
        Calendar cal1 = Calendar.getInstance();
        cal1.clear();
        cal1.set(2008, Calendar.JANUARY, 29, 22, 17, 59);
        Calendar cal2 = Calendar.getInstance();
        cal2.set(2008, Calendar.FEBRUARY, 5);
        checkDate(DateTransformerBase.updateDatePart(cal1.getTime(), cal2
                .getTime()), 2008, Calendar.FEBRUARY, 5, 22, 17, 59, 0);
    }

    /**
     * Tests updating the time part of a date/time object.
     */
    @Test
    public void testUpdateTimePart()
    {
        Calendar cal1 = Calendar.getInstance();
        cal1.clear();
        cal1.set(2008, Calendar.JANUARY, 29, 22, 17, 59);
        Calendar cal2 = Calendar.getInstance();
        cal2.set(Calendar.HOUR_OF_DAY, 10);
        cal2.set(Calendar.MINUTE, 22);
        cal2.set(Calendar.SECOND, 5);
        cal2.set(Calendar.MILLISECOND, 0);
        checkDate(DateTransformerBase.updateTimePart(cal1.getTime(), cal2
                .getTime()), 2008, Calendar.JANUARY, 29, 10, 22, 5, 0);
    }

    /**
     * Tests the updateDatePart() method when a null date/time object is passed.
     * This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateDatePartNullDateTime()
    {
        DateTransformerBase.updateDatePart(null, new Date());
    }

    /**
     * Tests the updateDatePart() method when a null component object is passed.
     * This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateDatePartNullComponent()
    {
        DateTransformerBase.updateDatePart(new Date(), null);
    }

    /**
     * A concrete test implementation of DateTransformerBase.
     */
    private static class DateTransformerBaseTestImpl extends
            DateTransformerBase
    {
        /**
         * Returns the format object to use. This implementation always returns
         * a static format object.
         */
        @Override
        protected DateFormat createFormat(Locale locale, int style, Configuration config)
        {
            return DateFormat.getDateInstance(style, locale);
        }
    }
}
