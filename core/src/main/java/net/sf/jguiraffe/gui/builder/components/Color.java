/*
 * Copyright 2006-2012 The JGUIraffe Team.
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

import java.io.Serializable;

/**
 * <p>
 * A simple class representing a color in a platform-independent way.
 * </p>
 * <p>
 * Colors are defined using RGB components. A <code>Color</code> object can be
 * created using the static <code>newInstance()</code> method. Then access to
 * its single components is possible.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: Color.java 205 2012-01-29 18:29:57Z oheger $
 */
public class Color implements Serializable
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = -7333025495245659868L;

    /** Constant for the maximum value of a RGB component. */
    private static final int MAX_RANGE = 255;

    /** Constant for the buffer size for the string generation. */
    private static final int BUF_SIZE = 32;

    /** The component for red. */
    private int red;

    /** The component for green. */
    private int green;

    /** The component for blue. */
    private int blue;

    /**
     * Creates a new instance of <code>Color</code>.
     *
     * @param r the red component
     * @param g the green component
     * @param b the blue component
     * @throws IllegalArgumentException if a component has an invalid value
     */
    Color(int r, int g, int b)
    {
        if (r < 0 || r > MAX_RANGE || g < 0 || g > MAX_RANGE || b < 0
                || b > MAX_RANGE)
        {
            throw new IllegalArgumentException(
                    "Invalid value for RGB component!");
        }

        red = r;
        green = g;
        blue = b;
    }

    /**
     * Returns the blue component of this color.
     *
     * @return the blue component
     */
    public int getBlue()
    {
        return blue;
    }

    /**
     * Returns the green component of this color.
     *
     * @return the green component
     */
    public int getGreen()
    {
        return green;
    }

    /**
     * Returns the red component of this color.
     *
     * @return the red component
     */
    public int getRed()
    {
        return red;
    }

    /**
     * Returns a String representation of this object.
     *
     * @return a String for this object
     */
    @Override
    public String toString()
    {
        StringBuffer buf = new StringBuffer(BUF_SIZE);
        buf.append(ColorHelper.COLDEF_RGB_PREFIX);
        buf.append(getRed()).append(ColorHelper.SEPARATOR);
        buf.append(getGreen()).append(ColorHelper.SEPARATOR);
        buf.append(getBlue()).append(ColorHelper.COLDEF_RGB_SUFFIX);
        return buf.toString();
    }

    /**
     * Tests if a passed in object equals this object. Two <code>Color</code>
     * objects are considered equal if and only if all color components are
     * equal.
     *
     * @param obj the object to compare to
     * @return a flag whether the objects are equal
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof Color))
        {
            return false;
        }
        Color c = (Color) obj;
        return getRed() == c.getRed() && getGreen() == c.getGreen()
                && getBlue() == c.getBlue();
    }

    /**
     * Determines a hash code for this object.
     *
     * @return a hash code
     */
    @Override
    public int hashCode()
    {
        final int seed = 13;
        final int factor = 43;

        int result = seed;
        result = result * factor + getRed();
        result = result * factor + getGreen();
        result = result * factor + getBlue();
        return result;
    }

    /**
     * Creates a new instance of <code>Color</code> and initializes the single
     * components. This method will check whether the passed in values are
     * valid; if one is out of range, an exception will be thrown.
     *
     * @param r the r component
     * @param g the g component
     * @param b the b component
     * @return the new <code>Color</code> instance
     * @throws IllegalArgumentException if a component is invalid
     */
    public static Color newInstance(int r, int g, int b)
    {
        return new Color(r, g, b);
    }
}
