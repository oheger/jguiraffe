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
package net.sf.jguiraffe.gui.builder.components;

import java.util.Iterator;

import net.sf.jguiraffe.gui.builder.components.ColorHelper;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

import junit.framework.TestCase;

/**
 * Test class for ColorHelper.
 *
 * @author Oliver Heger
 * @version $Id: TestColorHelper.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestColorHelper extends TestCase
{
    /**
     * Tests resolving hexadecimal color definitions.
     */
    public void testResolveHexColor() throws FormBuilderException
    {
        checkColor(ColorHelper.resolveColor("#ffff80"), 255, 255, 128);
        checkColor(ColorHelper.resolveColor("#FFFF80"), 255, 255, 128);
    }

    /**
     * Tests resolving an invalid hex color.
     */
    public void testResolveHexColorInvalid()
    {
        try
        {
            ColorHelper.resolveColor("#No valid color definition!");
            fail("Could resolve invalid color definition!");
        }
        catch (FormBuilderException fex)
        {
            // ok
        }
    }

    /**
     * Tests resolving a hex color with a too large value.
     */
    public void testResolveHexColorTooLarge()
    {
        try
        {
            ColorHelper.resolveColor("#fffffff");
            fail("Could resolve too large color definition!");
        }
        catch (FormBuilderException fex)
        {
            // ok
        }
    }

    /**
     * Tests resolving RGB color definitions.
     */
    public void testResolveRGBColor() throws FormBuilderException
    {
        checkColor(ColorHelper.resolveColor("(128, 64, 32)"), 128, 64, 32);
        checkColor(ColorHelper.resolveColor("(128,64,32)"), 128, 64, 32);
        checkColor(ColorHelper.resolveColor("(128;64, 32)"), 128, 64, 32);
    }

    /**
     * Tests resolving an RGB definition with too few components.
     */
    public void testResolveRGBTooFew()
    {
        try
        {
            ColorHelper.resolveColor("(128,64)");
            fail("Could resolve color with too few components!");
        }
        catch (FormBuilderException fex)
        {
            // ok
        }
    }

    /**
     * Tests resolving an RGB definition with too many components.
     */
    public void testResolveRGBTooMany()
    {
        try
        {
            ColorHelper.resolveColor("(128,64,32,16)");
            fail("Could resolve color with too many components!");
        }
        catch (FormBuilderException fex)
        {
            // ok
        }
    }

    /**
     * Tests resolving an RGB definition with a component that is out of range.
     */
    public void testResolveRGBInvalidRange()
    {
        try
        {
            ColorHelper.resolveColor("(128, 128, 256)");
            fail("Could resolve color with invalid components!");
        }
        catch (FormBuilderException fex)
        {
            // ok
        }
    }

    /**
     * Tests resolving an RGB definition with a non-numeric component.
     */
    public void testResolveRGBInvalidValue()
    {
        try
        {
            ColorHelper.resolveColor("(128, test, 256)");
            fail("Could resolve color with invalid components!");
        }
        catch (FormBuilderException fex)
        {
            // ok
        }
    }

    /**
     * Tests obtaining the default colors.
     */
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
    public void testGetPredefinedColorCase() throws FormBuilderException
    {
        Color col = ColorHelper.getPredefinedColor("blUE");
        checkColor(col, 0, 0, 255);
    }

    /**
     * Tries to obtain an unknown predefined color. This should cause an
     * exception.
     */
    public void testGetPredefinedColorUnknown()
    {
        try
        {
            ColorHelper.getPredefinedColor("unknown color");
            fail("Could obtain unknown color!");
        }
        catch (FormBuilderException fex)
        {
            // ok
        }
    }

    /**
     * Tries to obtain the predefined color null. This should cause an
     * exception.
     */
    public void testGetPredefinedColorNull()
    {
        try
        {
            ColorHelper.getPredefinedColor(null);
            fail("Could obtain predefined color null!");
        }
        catch (FormBuilderException fex)
        {
            // ok
        }
    }

    /**
     * Tests resolving an unknown, invalid constant color. This should cause an
     * exception.
     */
    public void testResolveConstantColorInvalid()
    {
        try
        {
            ColorHelper.resolveColor("unknown color");
            fail("Could resolve unknown color!");
        }
        catch (FormBuilderException fex)
        {
            // ok
        }
    }

    /**
     * Tests resolving a null color. Result should again be null.
     */
    public void testResolveConstantColorNull() throws FormBuilderException
    {
        assertNull("Null color not resolved to null", ColorHelper
                .resolveColor(null));
    }

    /**
     * Tests resolving predefined color names using the resolveColor() method.
     */
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
