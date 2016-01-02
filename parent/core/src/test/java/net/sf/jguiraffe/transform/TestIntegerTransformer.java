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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for IntegerTransformer. This class also tests the base classes for
 * number transformers.
 *
 * @author Oliver Heger
 * @version $Id: TestIntegerTransformer.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestIntegerTransformer extends AbstractTransformerTest
{
    /** Constant for a minimum value. */
    private static final Integer MIN = 10;

    /** Constant for a maximum value. */
    private static final Integer MAX = 99;

    /** Stores the transformer to be tested. */
    private IntegerTransformer transformer;

    @Before
    public void setUp() throws Exception
    {
        transformer = new IntegerTransformer();
    }

    /**
     * Tests the properties of a newly created instance.
     */
    @Test
    public void testInit()
    {
        assertNull("Minimum value set", transformer.getMinimum());
        assertNull("Maximum value set", transformer.getMaximum());
    }

    /**
     * Tests validating a valid number.
     */
    @Test
    public void testIsValid()
    {
        setUpContextMock(new HashMap<String, Object>());
        replayMocks();
        assertTrue("Number not valid", transformer.isValid("1", context)
                .isValid());
        verifyMocks();
    }

    /**
     * Tests validating a valid number in a valid interval.
     */
    @Test
    public void testIsValidMinMax()
    {
        setUpContextMock(new HashMap<String, Object>());
        transformer.setMinimum(0);
        transformer.setMaximum(100);
        replayMocks();
        assertTrue("Number not valid", transformer.isValid("50", context)
                .isValid());
        verifyMocks();
    }

    /**
     * Tests parsing an invalid number.
     */
    @Test
    public void testIsValidInvalidNumber()
    {
        checkInvalidNumber("Not a number");
    }

    /**
     * Tests parsing a string that starts with a valid number followed by an
     * invalid character.
     */
    @Test
    public void testIsValidInvalidNumberSuffix()
    {
        checkInvalidNumber("123,");
    }

    /**
     * Tests parsing a string that is no valid number.
     *
     * @param input the input string
     */
    private void checkInvalidNumber(String input)
    {
        setUpContextMock(new HashMap<String, Object>());
        expectError(ValidationMessageConstants.ERR_INVALID_NUMBER, input);
        replayMocks();
        checkError(ValidationMessageConstants.ERR_INVALID_NUMBER, transformer
                .isValid(input, context));
        verifyMocks();
    }

    /**
     * Tests parsing a valid number that is too small.
     */
    @Test
    public void testIsValidTooSmall()
    {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(NumberTransformerBase.PROP_MINIMUM, MIN);
        setUpContextMock(props);
        expectError(ValidationMessageConstants.ERR_NUMBER_TOO_SMALL, MIN
                .toString());
        replayMocks();
        String val = String.valueOf(MIN.intValue() - 1);
        checkError(ValidationMessageConstants.ERR_NUMBER_TOO_SMALL, transformer
                .isValid(val, context));
        verifyMocks();
    }

    /**
     * Tests parsing a valid number that is below the allowed interval.
     */
    @Test
    public void testIsValidTooSmallInterval()
    {
        transformer.setMinimum(MIN);
        transformer.setMaximum(MAX);
        setUpContextMock(new HashMap<String, Object>());
        expectError(ValidationMessageConstants.ERR_NUMBER_INTERVAL, MIN
                .toString(), MAX.toString());
        replayMocks();
        String val = String.valueOf(MIN.intValue() - 1);
        checkError(ValidationMessageConstants.ERR_NUMBER_INTERVAL, transformer
                .isValid(val, context));
        verifyMocks();
    }

    /**
     * Tests parsing a valid number that is too big.
     */
    @Test
    public void testIsValidTooBig()
    {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(NumberTransformerBase.PROP_MAXIMUM, MAX);
        setUpContextMock(props);
        expectError(ValidationMessageConstants.ERR_NUMBER_TOO_BIG, MAX
                .toString());
        replayMocks();
        String val = String.valueOf(MAX.intValue() + 1);
        checkError(ValidationMessageConstants.ERR_NUMBER_TOO_BIG, transformer
                .isValid(val, context));
        verifyMocks();
    }

    /**
     * Tests parsing a valid number that is after the allowed interval.
     */
    @Test
    public void testIsValidTooBigInterval()
    {
        transformer.setMinimum(MIN);
        transformer.setMaximum(MAX);
        setUpContextMock(new HashMap<String, Object>());
        expectError(ValidationMessageConstants.ERR_NUMBER_INTERVAL, MIN
                .toString(), MAX.toString());
        replayMocks();
        String val = String.valueOf(MAX.intValue() + 1);
        checkError(ValidationMessageConstants.ERR_NUMBER_INTERVAL, transformer
                .isValid(val, context));
        verifyMocks();
    }

    /**
     * Tests validating a long value that is too big for an integer.
     */
    @Test
    public void testIsValidTooBigInteger()
    {
        checkIsValidIntegerRange((long) Integer.MAX_VALUE + 10);
    }

    /**
     * Tests validating a long value that is too small for an integer.
     */
    @Test
    public void testIsValidTooSmallInteger()
    {
        checkIsValidIntegerRange((long) Integer.MIN_VALUE - 10);
    }

    /**
     * Helper method for testing the validation of long values that do not fit
     * in the range of integer numbers. These values should not be accepted.
     *
     * @param value the value to be tested
     */
    private void checkIsValidIntegerRange(long value)
    {
        setUpContextMock(new HashMap<String, Object>());
        String val = String.valueOf(value);
        expectError(ValidationMessageConstants.ERR_NUMBER_OUT_OF_RANGE, val);
        replayMocks();
        checkError(ValidationMessageConstants.ERR_NUMBER_OUT_OF_RANGE,
                transformer.isValid(val, context));
        verifyMocks();
    }

    /**
     * Tests validating a null object. This should be valid.
     */
    @Test
    public void testIsValidNull()
    {
        setUpContextMock(new HashMap<String, Object>());
        replayMocks();
        assertTrue("Null object not valid", transformer.isValid(null, context)
                .isValid());
        verifyMocks();
    }

    /**
     * Tests validating an empty string. This should be valid.
     */
    @Test
    public void testIsValidEmpty()
    {
        setUpContextMock(new HashMap<String, Object>());
        replayMocks();
        assertTrue("Empty string not valid", transformer.isValid("", context)
                .isValid());
        verifyMocks();
    }

    /**
     * Tests transforming a valid number.
     */
    @Test
    public void testTransform() throws Exception
    {
        setUpContextMock(new HashMap<String, Object>());
        replayMocks();
        Integer i = (Integer) transformer.transform("100.000", context);
        assertEquals("Wrong value", 100000, i.intValue());
        verifyMocks();
    }

    /**
     * Tests transforming a null object. Result should also be null.
     */
    @Test
    public void testTransformNull() throws Exception
    {
        setUpContextMock(new HashMap<String, Object>());
        replayMocks();
        assertNull("Wrong result for null object", transformer.transform(null,
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
}
