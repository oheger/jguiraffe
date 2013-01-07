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

import junit.framework.TestCase;

/**
 * Test class for Color.
 *
 * @author Oliver Heger
 * @version $Id: TestColor.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestColor extends TestCase
{
    /**
     * Tests creating a new Color instance.
     */
    public void testNewInstance()
    {
        Color c = Color.newInstance(128, 192, 222);
        assertEquals("Wrong red component", 128, c.getRed());
        assertEquals("Wrong green component", 192, c.getGreen());
        assertEquals("Wrong blue component", 222, c.getBlue());
    }

    /**
     * Tests creating a new Color instance when one of the components is too
     * small. This should cause an exception.
     */
    public void testNewInstanceTooSmall()
    {
        checkInvalidComponent(-1, "Could create color with component < 0!");
    }

    /**
     * Tests creating a new Color instance when one of the components is too
     * big. This should cause an exception.
     */
    public void testNewInstanceTooBig()
    {
        checkInvalidComponent(256, "Could create color with component > 255!");
    }

    /**
     * Tries to create a color instance when one of its components has an
     * invalid value. This should cause an exception.
     *
     * @param value the invalid component value
     * @param msg the error message
     */
    private void checkInvalidComponent(int value, String msg)
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
                Color.newInstance(components[0], components[1], components[2]);
                fail(msg);
            }
            catch (IllegalArgumentException iex)
            {
                // ok
            }
        }
    }

    /**
     * Tests color objects for equality.
     */
    public void testEquals()
    {
        Color c1 = Color.newInstance(1, 2, 3);
        checkEquals(c1, c1, true);
        checkEquals(c1, Color.newInstance(1, 2, 0), false);
        checkEquals(c1, Color.newInstance(1, 0, 3), false);
        checkEquals(c1, Color.newInstance(0, 2, 3), false);
        checkEquals(c1, Color.newInstance(1, 2, 3), true);
    }

    /**
     * Tests the equals() implementation when null is passed in.
     */
    public void testEqualsNull()
    {
        checkEquals(Color.newInstance(1, 2, 3), null, false);
    }

    /**
     * Tests the equals() implementation when an object of a different class is
     * passed in.
     */
    public void testEqualsInvalid()
    {
        checkEquals(Color.newInstance(1, 2, 3), this, false);
    }

    /**
     * Helper method for checking the equals() and hashCode() implementations.
     *
     * @param obj1 object 1 to compare
     * @param obj2 object 2 to compare
     * @param expected the expected result
     */
    private static void checkEquals(Object obj1, Object obj2, boolean expected)
    {
        assertEquals("Wrong result for equals", expected, obj1.equals(obj2));
        if (obj2 != null)
        {
            assertEquals("Not symmetric", expected, obj2.equals(obj1));
        }
        if (expected)
        {
            assertEquals("Hash codes are different", obj1.hashCode(), obj2
                    .hashCode());
        }
    }

    /**
     * Tests the toString() implementation. Here we only check whether the
     * components of the color appear in the resulting string.
     */
    public void testToString()
    {
        Color c = Color.newInstance(64, 128, 192);
        assertTrue("Components not found", c.toString().indexOf(
                "(64, 128, 192)") >= 0);
    }
}
