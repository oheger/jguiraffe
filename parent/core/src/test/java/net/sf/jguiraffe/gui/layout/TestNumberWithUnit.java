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
package net.sf.jguiraffe.gui.layout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import net.sf.jguiraffe.JGuiraffeTestHelper;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for NumberWithUnit.
 *
 * @author Oliver Heger
 * @version $Id: TestNumberWithUnit.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestNumberWithUnit
{
    /** Constant for the delta for double comparisons. */
    private static final double DELTA = 0.0001;

    /**
     * Helper method for checking string representations.
     *
     * @param n the number to be checked
     * @param expected the expected string
     */
    private static void checkBuildString(NumberWithUnit n, String expected)
    {
        assertEquals("Wrong unit string", expected, n.toUnitString());
    }

    /**
     * Tests the ZERO constant.
     */
    @Test
    public void testZero()
    {
        assertEquals("Wrong ZERO value", 0.0, NumberWithUnit.ZERO.getValue(),
                DELTA);
        assertEquals("Wrong unit for ZERO", Unit.PIXEL, NumberWithUnit.ZERO
                .getUnit());
    }

    /**
     * Tests the constructor for pixels.
     */
    @Test
    public void testInitPixels()
    {
        final int value = 10;
        NumberWithUnit n = new NumberWithUnit(value);
        assertEquals("Wrong value", value, n.getValue(), DELTA);
        assertEquals("Wrong unit", Unit.PIXEL, n.getUnit());
    }

    /**
     * Tests the constructor that takes a unit.
     */
    @Test
    public void testInitUnit()
    {
        final double value = 20;
        NumberWithUnit n = new NumberWithUnit(value, Unit.CM);
        assertEquals("Wrong value", value, n.getValue(), DELTA);
        assertEquals("Wrong unit", Unit.CM, n.getUnit());
    }

    /**
     * Tries to create an instance with a null unit. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitUnitNull()
    {
        new NumberWithUnit(123, null);
    }

    /**
     * Tests the toPixel() method if the unit is pixels.
     */
    @Test
    public void testToPixelUnitPixel()
    {
        NumberWithUnit n = new NumberWithUnit(10);
        assertEquals("Wrong result for pixel", 10, n.toPixel(null, null, false));
    }

    /**
     * Tests the toPixel() method if the unit is inch. We test whether the size
     * handler is invoked.
     */
    @Test
    public void testToPixelUnitInch()
    {
        UnitSizeHandler handler = EasyMock.createMock(UnitSizeHandler.class);
        final int resolution = 96;
        EasyMock.expect(handler.getScreenResolution()).andReturn(resolution);
        EasyMock.replay(handler);
        NumberWithUnit n = new NumberWithUnit(1, Unit.INCH);
        assertEquals("Wrong result for inch", resolution, n.toPixel(handler,
                null, true));
        EasyMock.verify(handler);
    }

    /**
     * Tests the unit string for the unit pixels.
     */
    @Test
    public void testToUnitStringPixel()
    {
        NumberWithUnit n = new NumberWithUnit(42);
        checkBuildString(n, "42px");
    }

    /**
     * Tests the unit string for other units that return double values.
     */
    @Test
    public void testToUnitStringOtherUnits()
    {
        NumberWithUnit n = new NumberWithUnit(42, Unit.CM);
        checkBuildString(n, "42.0cm");
        n = new NumberWithUnit(42.5, Unit.INCH);
        checkBuildString(n, "42.5in");
        n = new NumberWithUnit(12.25, Unit.DLU);
        checkBuildString(n, "12.25dlu");

    }

    /**
     * Helper method for testing the string constructor.
     *
     * @param unitStr the unit string to be parsed
     * @param expValue the expected value
     * @param expUnit the expected unit
     */
    private static void checkInitString(String unitStr, double expValue,
            Unit expUnit)
    {
        NumberWithUnit n = new NumberWithUnit(unitStr);
        assertEquals("Wrong value", expValue, n.getValue(), DELTA);
        assertEquals("Wrong unit", expUnit, n.getUnit());
    }

    /**
     * Tests the string constructor for a default unit string.
     */
    @Test
    public void testInitStringDefault()
    {
        checkInitString("42.5dlu", 42.5, Unit.DLU);
    }

    /**
     * Tests the string constructor for a unit string with whitespace between
     * the number and the unit.
     */
    @Test
    public void testInitStringSpace()
    {
        checkInitString("42.5 Cm", 42.5, Unit.CM);
    }

    /**
     * Tests initialization from a unit string with lots of space after the
     * number and the unit.
     */
    @Test
    public void testInitStringTrailingSpace()
    {
        checkInitString("1        IN   ", 1, Unit.INCH);
    }

    /**
     * Tests initialization from a unit string with leading spaces.
     */
    @Test
    public void testInitStringLeadingSpace()
    {
        checkInitString("    17in  ", 17, Unit.INCH);
    }

    /**
     * Tests initialization from a unit string without a unit. The default unit
     * pixel should be used.
     */
    @Test
    public void testInitStringNoUnit()
    {
        checkInitString("10", 10, Unit.PIXEL);
    }

    /**
     * Tests initialization from a unit string with an implied 0.
     */
    @Test
    public void testInitStringImplied0()
    {
        checkInitString(".1cm", .1, Unit.CM);
    }

    /**
     * Tests initialization from a unit string with a dot but no fraction
     * digits.
     */
    @Test
    public void testInitStringNoFractionDigits()
    {
        checkInitString("10.px", 10, Unit.PIXEL);
    }

    /**
     * Tries to initialize an instance from a null unit string. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitStringNull()
    {
        new NumberWithUnit((String) null);
    }

    /**
     * Tries to initialize an instance from an empty string. This should cause
     * an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitStringEmpty()
    {
        new NumberWithUnit("");
    }

    /**
     * Tries to initialize an instance from a unit string with an invalid value.
     * This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitStringInvalidValue()
    {
        new NumberWithUnit("Invalid!34.0px");
    }

    /**
     * Tests a unit string without a value. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitStringNoValue()
    {
        new NumberWithUnit("cm");
    }

    /**
     * Tries to initialize an instance from a unit string with an unknown unit.
     * This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitStringInvalidUnit()
    {
        new NumberWithUnit("10.12345TEST");
    }

    /**
     * Tries to initialize an instance from a unit string with too many decimal
     * dots. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitStringInvalidDots()
    {
        new NumberWithUnit("1..5dlu");
    }

    /**
     * Tests buildUnitString() for a null buffer. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testBuildUnitStringNull()
    {
        NumberWithUnit n = new NumberWithUnit(100);
        n.buildUnitString(null);
    }

    /**
     * Tests the equals() method if the expected result is true.
     */
    @Test
    public void testEqualsTrue()
    {
        NumberWithUnit n1 = new NumberWithUnit(10, Unit.CM);
        JGuiraffeTestHelper.checkEquals(n1, n1, true);
        NumberWithUnit n2 = new NumberWithUnit(n1.getValue(), n1.getUnit());
        JGuiraffeTestHelper.checkEquals(n1, n2, true);
    }

    /**
     * Tests the equals() method if the expected result is false.
     */
    @Test
    public void testEqualsFalse()
    {
        NumberWithUnit n1 = new NumberWithUnit(10, Unit.CM);
        NumberWithUnit n2 = new NumberWithUnit(10, Unit.DLU);
        JGuiraffeTestHelper.checkEquals(n1, n2, false);
        n2 = new NumberWithUnit(11, Unit.CM);
        JGuiraffeTestHelper.checkEquals(n1, n2, false);
    }

    /**
     * Tests equals() with other objects.
     */
    @Test
    public void testEqualsTrivial()
    {
        JGuiraffeTestHelper.testTrivialEquals(new NumberWithUnit(10));
    }

    /**
     * Tests the nonNull() method if a value is passed in.
     */
    @Test
    public void testNonNullWithValue()
    {
        NumberWithUnit num = new NumberWithUnit(2, Unit.CM);
        assertSame("Wrong number", num, NumberWithUnit.nonNull(num));
    }

    /**
     * Tests the nonNull() method if no value is passed in.
     */
    @Test
    public void testNonNullNoValue()
    {
        assertEquals("Wrong number", NumberWithUnit.ZERO, NumberWithUnit
                .nonNull(null));
    }

    /**
     * Tests the string representation.
     */
    @Test
    public void testToString()
    {
        NumberWithUnit n = new NumberWithUnit(27, Unit.DLU);
        String s = n.toString();
        assertEquals("Wrong string", "NumberWithUnit [ " + n.toUnitString()
                + " ]", s);
    }
}
