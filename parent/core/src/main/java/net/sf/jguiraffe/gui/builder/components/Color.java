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
package net.sf.jguiraffe.gui.builder.components;

import java.io.Serializable;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * A simple class representing a color in a platform-independent way.
 * </p>
 * <p>
 * In the most basic form, colors are defined using RGB components. In more
 * complex scenarios a color can be represented by a string - for instance, if
 * it is determined by a complex style sheet definition. The
 * {@code isLogicColor()} method can be used to distinguish between these kinds
 * of objects. If it returns <strong>true</strong>, the color is defined based
 * on a string, and thus concrete RGB values are not available.
 * </p>
 * <p>
 * {@code Color} objects are created using static factory methods. They are
 * immutable and thus can be shared between different threads.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: Color.java 205 2012-01-29 18:29:57Z oheger $
 */
public class Color implements Serializable
{
    /**
     * Constant for an undefined color component. This value is returned by the
     * access methods for the single color components ({@code getRed()},
     * {@code getGreen()}, or {@code getBlue()} to indicate that this component
     * is undefined - which is the case for logic colors.
     *
     * @since 1.3
     */
    public static final int COMPONENT_UNDEFINED = -1;

    /**
     * Constant for an undefined color. This constant defines a special instance
     * which does not have RGB components nor a logic color definition. It can
     * be used to represent an undefined color.
     *
     * @since 1.3
     */
    public static final Color UNDEFINED = new Color(COMPONENT_UNDEFINED,
            COMPONENT_UNDEFINED, COMPONENT_UNDEFINED, null);

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 20130606L;

    /** Constant for an error message pattern for an invalid color component. */
    private static final String ERR_INVALID_COMPONENT =
            "Invalid value for component %s: %d!";

    /** Constant for the maximum value of a RGB component. */
    private static final int MAX_RANGE = 255;

    /** Constant for the buffer size for the string generation. */
    private static final int BUF_SIZE = 32;

    /** The logic color definition. */
    private final String colorDefinition;

    /** The component for red. */
    private final int red;

    /** The component for green. */
    private final int green;

    /** The component for blue. */
    private final int blue;

    /**
     * Creates a new instance of {@code Color} and initializes its members.
     *
     * @param r the red component
     * @param g the green component
     * @param b the blue component
     * @param def the logic color definition
     */
    Color(int r, int g, int b, String def)
    {
        red = r;
        green = g;
        blue = b;
        colorDefinition = def;
    }

    /**
     * Returns a flag whether this {@code Color} instance is based on a logic
     * color definition. This means that it was constructed from a text-based
     * definition. In this case, the values of the RGB components are undefined.
     *
     * @return <strong>true</strong> if this {@code Color} instance is based on
     *         a logic definition, <strong>false</strong> otherwise
     * @since 1.3
     */
    public boolean isLogicColor()
    {
        return getRed() == COMPONENT_UNDEFINED
                || getGreen() == COMPONENT_UNDEFINED
                || getBlue() == COMPONENT_UNDEFINED;
    }

    /**
     * Returns the blue component of this color. This value is only defined if
     * {@code isLogicColor()} returns <strong>false</strong>.
     *
     * @return the blue component
     */
    public int getBlue()
    {
        return blue;
    }

    /**
     * Returns the green component of this color. This value is only defined if
     * {@code isLogicColor()} returns <strong>false</strong>.
     *
     * @return the green component
     */
    public int getGreen()
    {
        return green;
    }

    /**
     * Returns the red component of this color. This value is only defined if
     * {@code isLogicColor()} returns <strong>false</strong>.
     *
     * @return the red component
     */
    public int getRed()
    {
        return red;
    }

    /**
     * Returns the logic color definition this {@code Color} instance is based
     * on. This value is only defined of {@code isLogicColor()} returns
     * <strong>true</strong>.
     *
     * @return the logic color definition
     * @since 1.3
     */
    public String getColorDefinition()
    {
        return colorDefinition;
    }

    /**
     * Returns a String representation of this object.
     *
     * @return a String for this object
     */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder(BUF_SIZE);
        buf.append(getClass().getSimpleName()).append("[ ");

        if (isLogicColor())
        {
            buf.append("def = '").append(getColorDefinition()).append('\'');
        }
        else
        {
            buf.append("rgb = ");
            buf.append(ColorHelper.COLDEF_RGB_PREFIX);
            buf.append(getRed()).append(ColorHelper.SEPARATOR);
            buf.append(getGreen()).append(ColorHelper.SEPARATOR);
            buf.append(getBlue()).append(ColorHelper.COLDEF_RGB_SUFFIX);
        }

        buf.append(" ]");
        return buf.toString();
    }

    /**
     * Tests if a passed in object equals this object. Two {@code Color} objects
     * are considered equal if and only if all color components are equal.
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
        return getRed() == c.getRed()
                && getGreen() == c.getGreen()
                && getBlue() == c.getBlue()
                && ObjectUtils.equals(getColorDefinition(),
                        c.getColorDefinition());
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
        if (getColorDefinition() != null)
        {
            result = result * factor + getColorDefinition().hashCode();
        }
        return result;
    }

    /**
     * Creates a new instance of {@code Color} and initializes the single
     * components. This method will check whether the passed in values are
     * valid; if one is out of range, an exception will be thrown.
     *
     * @param r the r component
     * @param g the g component
     * @param b the b component
     * @return the new {@code Color} instance
     * @throws IllegalArgumentException if a component is invalid
     * @deprecated Use {@code newRGBInstance()} instead.
     */
    @Deprecated
    public static Color newInstance(int r, int g, int b)
    {
        return newRGBInstance(r, g, b);
    }

    /**
     * Creates a new instance of {@code Color} and initializes it with the given
     * components for the red, green, and blue part. This method checks whether
     * the passed in values are valid; if one argument is out of range, an
     * exception is thrown.
     *
     * @param r the r component
     * @param g the g component
     * @param b the b component
     * @return the new {@code Color} instance
     * @throws IllegalArgumentException if a component is invalid
     * @since 1.3
     */
    public static Color newRGBInstance(int r, int g, int b)
    {
        checkComponent(r, "red");
        checkComponent(g, "green");
        checkComponent(b, "blue");
        return new Color(r, g, b, null);
    }

    /**
     * Creates a new instance of {@code Color} based on a logic, text-based
     * color definition. The passed in string is just stored and not interpreted
     * in any form. It must not be <b>null</b>.
     *
     * @param coldef the logic color definition
     * @return the new {@code Color} instance
     * @throws IllegalArgumentException if the color definition is undefined
     * @since 1.3
     */
    public static Color newLogicInstance(String coldef)
    {
        if (StringUtils.isBlank(coldef))
        {
            throw new IllegalArgumentException(
                    "Color definition must be defined!");
        }
        return new Color(COMPONENT_UNDEFINED, COMPONENT_UNDEFINED,
                COMPONENT_UNDEFINED, coldef);
    }

    /**
     * Checks whether a component of a color is in the correct range. If not, an
     * exception is thrown.
     *
     * @param c the component to be checked
     * @param name the name of the component (for producing a meaningful
     *        exception message)
     * @throws IllegalArgumentException if the component has an invalid value
     */
    private static void checkComponent(int c, String name)
    {
        if (c < 0 || c > MAX_RANGE)
        {
            throw new IllegalArgumentException(String.format(
                    ERR_INVALID_COMPONENT, name, c));
        }
    }
}
