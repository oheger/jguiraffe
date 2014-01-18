/*
 * Copyright 2006-2014 The JGUIraffe Team.
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;

/**
 * Test class for ColorHelper.
 *
 * @author Oliver Heger
 * @version $Id: TestColorHelper.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestColorHelper
{
    /**
     * Tests resolving hexadecimal color definitions.
     */
    @Test
    public void testResolveHexColor() throws FormBuilderException
    {
        checkColor(ColorHelper.resolveColor("#ffff80"), 255, 255, 128);
        checkColor(ColorHelper.resolveColor("#FFFF80"), 255, 255, 128);
    }

    /**
     * Tests resolving an invalid hex color.
     */
    @Test(expected = FormBuilderException.class)
    public void testResolveHexColorInvalid() throws FormBuilderException
    {
        ColorHelper.resolveColor("#No valid color definition!");
    }

    /**
     * Tests resolving a hex color with a too large value.
     */
    @Test(expected = FormBuilderException.class)
    public void testResolveHexColorTooLarge() throws FormBuilderException
    {
        ColorHelper.resolveColor("#fffffff");
    }

    /**
     * Tests resolving RGB color definitions.
     */
    @Test
    public void testResolveRGBColor() throws FormBuilderException
    {
        checkColor(ColorHelper.resolveColor("(128, 64, 32)"), 128, 64, 32);
        checkColor(ColorHelper.resolveColor("(128,64,32)"), 128, 64, 32);
        checkColor(ColorHelper.resolveColor("(128;64, 32)"), 128, 64, 32);
    }

    /**
     * Tests resolving an RGB definition with too few components.
     */
    @Test(expected = FormBuilderException.class)
    public void testResolveRGBTooFew() throws FormBuilderException
    {
         ColorHelper.resolveColor("(128,64)");
    }

    /**
     * Tests resolving an RGB definition with too many components.
     */
    @Test(expected = FormBuilderException.class)
    public void testResolveRGBTooMany() throws FormBuilderException
    {
        ColorHelper.resolveColor("(128,64,32,16)");
    }

    /**
     * Tests resolving an RGB definition with a component that is out of range.
     */
    @Test(expected = FormBuilderException.class)
    public void testResolveRGBInvalidRange() throws FormBuilderException
    {
        ColorHelper.resolveColor("(128, 128, 256)");
    }

    /**
     * Tests resolving an RGB definition with a non-numeric component.
     */
    @Test(expected = FormBuilderException.class)
    public void testResolveRGBInvalidValue() throws FormBuilderException
    {
        ColorHelper.resolveColor("(128, test, 256)");
    }

    /**
     * Tests obtaining the default colors.
     */
    @Test
    public void testGetPredefinedColor() throws FormBuilderException
    {
        for (Iterator<String> it = ColorHelper.getPredefinedNames(); it
                .hasNext();)
        {
            String colName = it.next();
            assertNotNull("Predefined color cannot be resolved: " + colName,
                    ColorHelper.getPredefinedColor(colName));
        }
    }

    /**
     * Tests if a predefined color can be obtained with different case.
     */
    @Test
    public void testGetPredefinedColorCase() throws FormBuilderException
    {
        Color col = ColorHelper.getPredefinedColor("blUE");
        checkColor(col, 0, 0, 255);
    }

    /**
     * Tries to obtain an unknown predefined color. This should cause an
     * exception.
     */
    @Test(expected = FormBuilderException.class)
    public void testGetPredefinedColorUnknown() throws FormBuilderException
    {
        ColorHelper.getPredefinedColor("unknown color");
    }

    /**
     * Tries to obtain the predefined color null.
     */
    @Test(expected = FormBuilderException.class)
    public void testGetPredefinedColorNull() throws FormBuilderException
    {
        ColorHelper.getPredefinedColor(null);
    }

    /**
     * Tests resolving an unknown, invalid constant color.
     */
    @Test(expected = FormBuilderException.class)
    public void testResolveConstantColorInvalid() throws FormBuilderException
    {
        ColorHelper.resolveColor("unknown color");
    }

    /**
     * Tests resolving a null color. Result should again be null.
     */
    @Test
    public void testResolveConstantColorNull() throws FormBuilderException
    {
        assertNull("Null color not resolved to null", ColorHelper
                .resolveColor(null));
    }

    /**
     * Tests resolving predefined color names using the resolveColor() method.
     */
    @Test
    public void testResolveConstantColorPredefined()
            throws FormBuilderException
    {
        for (Iterator<String> it = ColorHelper.getPredefinedNames(); it
                .hasNext();)
        {
            String colorName = it.next();
            assertNotNull("Predef color could not be resolved: " + colorName,
                    ColorHelper.resolveColor(colorName));
        }
    }

    /**
     * Tests whether a logic color can be resolved.
     */
    @Test
    public void testResolveLogicColor() throws FormBuilderException
    {
        Color c = ColorHelper.resolveColor("~TestColor");
        assertTrue("Not a logic color", c.isLogicColor());
        assertEquals("Wrong color definition", "TestColor",
                c.getColorDefinition());
    }

    /**
     * Tests resolveColor() for a logic color with an empty definition.
     */
    @Test(expected = FormBuilderException.class)
    public void testResolveLogicColorNoDefinition() throws FormBuilderException
    {
        ColorHelper.resolveColor("~");
    }

    /**
     * Helper method for checking a color.
     *
     * @param c the color to check
     * @param r the expected r value
     * @param g the expected g value
     * @param b the expected b value
     */
    private void checkColor(Color c, int r, int g, int b)
    {
        assertEquals("Invalid r component", r, c.getRed());
        assertEquals("Invalid g component", g, c.getGreen());
        assertEquals("Invalid b component", b, c.getBlue());
    }
}
