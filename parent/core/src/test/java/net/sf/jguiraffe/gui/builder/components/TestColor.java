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
package net.sf.jguiraffe.gui.builder.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import net.sf.jguiraffe.JGuiraffeTestHelper;

import org.junit.Test;

/**
 * Test class for Color.
 *
 * @author Oliver Heger
 * @version $Id: TestColor.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestColor
{
    /** Constant for the R component. */
    private static final int RED = 128;

    /** Constant for the G component. */
    private static final int GREEN = 192;

    /** Constant for the B component. */
    private static final int BLUE = 222;

    /** Constant for a logic color definition. */
    private static final String COLDEF = "mouse-gray";

    /**
     * Tests whether an instance can be created with RGB components in the old
     * way.
     */
    @Test
    public void testNewInstance()
    {
        @SuppressWarnings("deprecation")
        Color c = Color.newInstance(RED, GREEN, BLUE);
        assertEquals("Wrong red component", RED, c.getRed());
        assertEquals("Wrong green component", GREEN, c.getGreen());
        assertEquals("Wrong blue component", BLUE, c.getBlue());
    }

    /**
     * Tests whether an instance can be created with RGB components.
     */
    @Test
    public void testNewRGBInstance()
    {
        Color c = Color.newRGBInstance(RED, GREEN, BLUE);
        assertEquals("Wrong red component", RED, c.getRed());
        assertEquals("Wrong green component", GREEN, c.getGreen());
        assertEquals("Wrong blue component", BLUE, c.getBlue());
    }

    /**
     * Tests whether an instance can be created from a logic definition.
     */
    @Test
    public void testNewLogicInstance()
    {
        Color c = Color.newLogicInstance(COLDEF);
        assertEquals("Wrong color definition", COLDEF, c.getColorDefinition());
    }

    /**
     * Tests whether an RGB component which is too small causes an exception.
     */
    @Test
    public void testNewInstanceTooSmall()
    {
        checkInvalidComponent(-1, "Could create color with component < 0!");
    }

    /**
     * Tests whether an RGB component which is too big causes an exception.
     */
    @Test
    public void testNewInstanceTooBig()
    {
        checkInvalidComponent(256, "Could create color with component > 255!");
    }

    /**
     * Tries to create a color instance when one of its components has an
     * invalid value. This should cause an exception. This is tested for all
     * color components.
     *
     * @param value the invalid component value
     * @param msg the error message
     */
    private static void checkInvalidComponent(int value, String msg)
    {
        int[] components = new int[3];
        for (int i = 0; i < components.length; i++)
        {
            for (int j = 0; j < components.length; j++)
            {
                components[j] = (i == j) ? value : 100;
            }
            try
            {
                Color.newRGBInstance(components[0], components[1],
                        components[2]);
                fail(msg);
            }
            catch (IllegalArgumentException iex)
            {
                // ok
            }
        }
    }

    /**
     * Tries to create a new logic instance without a color definition.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNewLogicInstanceNullDefinition()
    {
        Color.newLogicInstance(null);
    }

    /**
     * Tries to create a new logic instance with an empty color definition.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNewLogicInstanceEmptyDefinition()
    {
        Color.newLogicInstance("");
    }

    /**
     * Tests isLogicColor() if the expected result is true.
     */
    @Test
    public void testIsLogicColorTrue()
    {
        Color col = Color.newLogicInstance(COLDEF);
        assertTrue("Wrong result (1)", col.isLogicColor());
        col = new Color(Color.COMPONENT_UNDEFINED, GREEN, BLUE, null);
        assertTrue("Wrong result (2)", col.isLogicColor());
        col = new Color(RED, Color.COMPONENT_UNDEFINED, BLUE, null);
        assertTrue("Wrong result (3)", col.isLogicColor());
        col = new Color(RED, GREEN, Color.COMPONENT_UNDEFINED, null);
        assertTrue("Wrong result (4)", col.isLogicColor());
    }

    /**
     * Tests isLogicColor() if the expected result is false.
     */
    @Test
    public void testIsLogicColorFalse()
    {
        Color col = Color.newRGBInstance(RED, GREEN, BLUE);
        assertFalse("Wrong result", col.isLogicColor());
    }

    /**
     * Tests the equals() method if the expected result is true.
     */
    @Test
    public void testEqualsTrue()
    {
        Color c = Color.newRGBInstance(RED, GREEN, BLUE);
        JGuiraffeTestHelper.checkEquals(c, c, true);
        JGuiraffeTestHelper.checkEquals(c,
                Color.newRGBInstance(RED, GREEN, BLUE), true);
        c = Color.newLogicInstance(COLDEF);
        JGuiraffeTestHelper
                .checkEquals(c, Color.newLogicInstance(COLDEF), true);
    }

    /**
     * Tests equals() if the expected result is false.
     */
    @Test
    public void testEqualsFalse()
    {
        Color c1 = Color.newRGBInstance(RED, GREEN, BLUE);
        JGuiraffeTestHelper.checkEquals(c1,
                Color.newRGBInstance(RED, GREEN, BLUE + 1), false);
        JGuiraffeTestHelper.checkEquals(c1,
                Color.newRGBInstance(RED, GREEN - 1, BLUE), false);
        JGuiraffeTestHelper.checkEquals(c1,
                Color.newRGBInstance(RED + 1, GREEN, BLUE), false);
        Color c2 = Color.newLogicInstance(COLDEF);
        JGuiraffeTestHelper.checkEquals(c1, c2, false);
        JGuiraffeTestHelper.checkEquals(c2,
                Color.newLogicInstance(COLDEF + "_other"), false);
    }

    /**
     * Tests equals() with other objects.
     */
    @Test
    public void testEqualsTrivial()
    {
        JGuiraffeTestHelper.testTrivialEquals(Color.newRGBInstance(RED, GREEN,
                BLUE));
    }

    /**
     * Tests the toString() implementation for an RGB-based color. Here we only
     * check whether the components of the color appear in the resulting string.
     */
    @Test
    public void testToStringRGB()
    {
        Color c = Color.newRGBInstance(RED, GREEN, BLUE);
        String s = c.toString();
        String expected = "rgb = (" + RED + ", " + GREEN + ", " + BLUE + ")";
        assertTrue("Components not found: " + s, s.indexOf(expected) >= 0);
        assertTrue("Got a logic definition: " + s, s.indexOf("def = ") < 0);
    }

    /**
     * Tests the string representation of a logic color.
     */
    @Test
    public void testToStringLogic()
    {
        Color c = Color.newLogicInstance(COLDEF);
        String s = c.toString();
        String expected = "def = '" + COLDEF + "'";
        assertTrue("Definition not found: " + s, s.indexOf(expected) >= 0);
        assertTrue("Got RGB components: " + s, s.indexOf("rgb = ") < 0);
    }

    /**
     * Tests whether an instance can be serialized.
     */
    @Test
    public void testSerialization() throws IOException
    {
        Color c = Color.newRGBInstance(RED, GREEN, BLUE);
        Color c2 = JGuiraffeTestHelper.serialize(c);
        assertEquals("Serialized object not equal", c, c2);
    }
}
