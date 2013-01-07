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
package net.sf.jguiraffe.gui.layout;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for the unit classes.
 *
 * @author Oliver Heger
 * @version $Id: TestUnits.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestUnits
{
    /** Constant for the screen resolution. */
    private static final int SCREEN_RESOLUTION = 96;

    /**
     * Helper method for testing whether a string literal can be resolved for an
     * enum.
     *
     * @param expected the expected enum
     * @param name the name
     */
    private void checkFromString(Unit expected, String name)
    {
        for (int i = 0; i < 2 << name.length(); i++)
        {
            StringBuilder buf = new StringBuilder(name.length());
            for (int j = 0; j < name.length(); j++)
            {
                char c = name.charAt(j);
                if ((i & (2 << j)) != 0)
                {
                    buf.append(Character.toUpperCase(c));
                }
                else
                {
                    buf.append(c);
                }
            }
            assertEquals("Invalid unit for " + buf, expected, Unit
                    .fromString(buf.toString()));
        }
    }

    /**
     * Tests the fromString() method for valid unit names.
     */
    @Test
    public void testFromString()
    {
        for (Unit u : Unit.values())
        {
            checkFromString(u, u.getUnitName().toLowerCase());
        }
    }

    /**
     * Tests fromString() for null input. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFromStringNull()
    {
        Unit.fromString(null);
    }

    /**
     * Tests the fromString() method for an invalid unit name. This should cause
     * an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFromStringInvalid()
    {
        Unit.fromString("a non existing unit name!");
    }

    /**
     * Tests the pixel unit.
     */
    @Test
    public void testPixel()
    {
        assertEquals(42, Unit.PIXEL.toPixel(42, null, null, true));
        assertEquals(42, Unit.PIXEL.toPixel(42, null, null, false));
        assertEquals(42, Unit.PIXEL.toPixel(42.25, null, null, true));
        assertEquals(43, Unit.PIXEL.toPixel(42.5, null, null, true));
    }

    /**
     * Tests the inch unit.
     */
    @Test
    public void testInch()
    {
        UnitSizeHandler handler = EasyMock.createMock(UnitSizeHandler.class);
        EasyMock.expect(handler.getScreenResolution()).andReturn(
                SCREEN_RESOLUTION).times(2);
        EasyMock.replay(handler);
        assertEquals("Wrong inch result", 2 * SCREEN_RESOLUTION, Unit.INCH
                .toPixel(2, handler, null, true));
        assertEquals("Wrong inch result 2", SCREEN_RESOLUTION / 2, Unit.INCH
                .toPixel(0.5, handler, null, false));
        EasyMock.verify(handler);
    }

    /**
     * Tests the cm unit.
     */
    @Test
    public void testCm()
    {
        UnitSizeHandler handler = EasyMock.createMock(UnitSizeHandler.class);
        EasyMock.expect(handler.getScreenResolution()).andReturn(
                SCREEN_RESOLUTION).times(2);
        EasyMock.replay(handler);
        assertEquals("Wrong cm result 1", SCREEN_RESOLUTION, Unit.CM.toPixel(
                2.54, handler, null, true));
        assertEquals("Wrong cm result 2", 2 * SCREEN_RESOLUTION, Unit.CM
                .toPixel(2 * 2.54, handler, null, false));
    }

    /**
     * Helper method for testing conversions with the DLU unit.
     *
     * @param y a flag whether the Y direction is desired
     * @param factor the factor depending on the direction
     */
    private void checkDLU(boolean y, int factor)
    {
        UnitSizeHandler handler = EasyMock.createMock(UnitSizeHandler.class);
        final double fontSize = 12;
        final Object comp = new Object();
        EasyMock.expect(handler.getFontSize(comp, y)).andReturn(fontSize);
        EasyMock.replay(handler);
        double value = 100;
        double expected = value * fontSize / factor;
        assertEquals("Wrong DLU result", (int) Math.round(expected), Unit.DLU
                .toPixel(value, handler, comp, y));
        EasyMock.verify(handler);
    }

    /**
     * Tests the dialog units in X direction.
     */
    @Test
    public void testDLUX()
    {
        checkDLU(false, 4);
    }

    /**
     * Tests the unit DLU in Y direction.
     */
    @Test
    public void testDLUY()
    {
        checkDLU(true, 8);
    }
}
