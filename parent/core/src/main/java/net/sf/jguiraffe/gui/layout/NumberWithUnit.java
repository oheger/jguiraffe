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
package net.sf.jguiraffe.gui.layout;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * A class that combines a value with a unit.
 * </p>
 * <p>
 * This class can be used to work with values that have an associated unit. For
 * instance spaces in a layout can be defined in arbitrary units like pixels,
 * dialog units, inches or centimeters. To support this, an instance of this
 * class stores a value (as a floating point number) and a reference to a
 * {@link Unit} object. This <code>Unit</code> object is also used
 * to perform conversions of the stored value to the default unit pixels.
 * </p>
 * <p>
 * Instances of this class are immutable. Once created, they cannot be changed
 * any more. Thus they can be shared between multiple threads.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: NumberWithUnit.java 205 2012-01-29 18:29:57Z oheger $
 */
public class NumberWithUnit implements Serializable
{
    /** Constant for the special value zero. */
    public static final NumberWithUnit ZERO;

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 20090730L;

    /** Constant for the pattern of a valid unit string. */
    private static final Pattern PAT_UNITSTR = Pattern
            .compile("([0-9]*\\.?[0-9]*)\\s*(\\S*)");

    /** Constant for the initial string buffer size. */
    private static final int BUF_SIZE = 64;

    /** Constant for the number of bits for shifting a long number. */
    private static final int LONG_SHIFT = 32;

    /** Stores the unit. */
    private final Unit unit;

    /** Stores the value. */
    private final double value;

    /**
     * Creates a new instance of <code>NumberWithUnit</code> with the numeric
     * value set to 0 and the unit pixels.
     */
    private NumberWithUnit()
    {
        this(0, Unit.PIXEL);
    }

    /**
     * Creates a new instance of <code>NumberWithUnit</code> with the given
     * value and the unit pixels.
     *
     * @param val the value
     */
    public NumberWithUnit(int val)
    {
        this(val, Unit.PIXEL);
    }

    /**
     * Creates a new instance of <code>NumberWithUnit</code> and initializes it.
     *
     * @param val the numeric value
     * @param unit the unit (must not be <b>null </b>)
     */
    public NumberWithUnit(double val, Unit unit)
    {
        if (unit == null)
        {
            throw new IllegalArgumentException("Unit must not be null!");
        }

        this.unit = unit;
        value = val;
    }

    /**
     * Creates a new instance of {@code NumberWithUnit} and initializes it from
     * the given string representation. The string passed to this method must
     * start with a valid double number. Then after optional whitespace the name
     * of the unit must follow. If no unit is provided, pixel is assumed. Valid
     * strings are for instance "10", "10cm", "10.5 cm".
     *
     * @param s the string to be parsed (must not be <b>null</b>)
     * @throws IllegalArgumentException if the passed in string is not a valid
     *         unit string
     * @see #toUnitString()
     */
    public NumberWithUnit(String s)
    {
        if (s == null)
        {
            throw new IllegalArgumentException("Unit string must not be null!");
        }

        Matcher m = PAT_UNITSTR.matcher(s.trim());
        if (!m.matches())
        {
            throw new IllegalArgumentException("Not a valid unit string: " + s);
        }

        try
        {
            value = Double.parseDouble(m.group(1));
        }
        catch (NumberFormatException nfex)
        {
            throw new IllegalArgumentException("Not a valid unit string: " + s
                    + ". Not a valid number.");
        }

        if (m.group(2).length() <= 0)
        {
            unit = Unit.PIXEL;
        }
        else
        {
            unit = Unit.fromString(m.group(2));
        }
    }

    /**
     * Returns the numeric value.
     *
     * @return the value
     */
    public final double getValue()
    {
        return value;
    }

    /**
     * Returns the unit of this number.
     *
     * @return the unit
     */
    public final Unit getUnit()
    {
        return unit;
    }

    /**
     * Converts this number into a pixel value. This method calls the
     * corresponding conversion method on the actual <code>Unit</code> object.
     *
     * @param handler the size handler
     * @param comp the component
     * @param y flag for X or Y direction
     * @return the pixel value
     * @see Unit#toPixel(double, UnitSizeHandler, Object, boolean)
     */
    public int toPixel(UnitSizeHandler handler, Object comp, boolean y)
    {
        return getUnit().toPixel(getValue(), handler, comp, y);
    }

    /**
     * Appends a string representation of this number and its unit to the given
     * string buffer. First the number is written and then the name of the unit.
     * This is the same as {@link #toUnitString()}, but the string is directly
     * appended to the given buffer.
     *
     * @param buf the string buffer (must not be <b>null</b>)
     * @throws IllegalArgumentException if the buffer is <b>null</b>
     */
    public void buildUnitString(StringBuilder buf)
    {
        if (buf == null)
        {
            throw new IllegalArgumentException("Buffer must not be null!");
        }

        if (Unit.PIXEL.equals(getUnit()))
        {
            // pixels are always integer numbers
            buf.append(Unit.PIXEL.toPixel(getValue(), null, null, false));
        }
        else
        {
            buf.append(getValue());
        }
        buf.append(getUnit().getUnitName());
    }

    /**
     * Returns a string representation for the stored value and the unit. This
     * string contains the value immediately followed by the short unit name,
     * e.g. 10px, 100dlu, 2.5in. Strings in this format can be passed to the
     * constructor that takes a string argument.
     *
     * @return a string for the value and the unit
     */
    public String toUnitString()
    {
        StringBuilder buf = new StringBuilder();
        buildUnitString(buf);
        return buf.toString();
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string for this object
     */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder(BUF_SIZE);
        buf.append("NumberWithUnit [ ");
        buildUnitString(buf);
        buf.append(" ]");
        return buf.toString();
    }

    /**
     * Returns a hash code for this object.
     *
     * @return a hash code
     */
    @Override
    public int hashCode()
    {
        final int factor = 31;
        final int seed = 17;

        int result = seed;
        result = factor * result + getUnit().hashCode();
        long f = Double.doubleToLongBits(getValue());
        result = factor * result + (int) (f ^ (f >>> LONG_SHIFT));

        return result;
    }

    /**
     * Compares two objects. Two instances of this class are equal if they have
     * the same unit and the same value.
     *
     * @param obj the object to compare to
     * @return a flag if the objects are equal
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof NumberWithUnit))
        {
            return false;
        }

        NumberWithUnit c = (NumberWithUnit) obj;
        return getUnit() == c.getUnit()
                && Double.compare(getValue(), c.getValue()) == 0;
    }

    /**
     * A convenience method for performing checks for <b>null</b> values. This
     * method converts a <b>null</b> argument into an instance with the value 0.
     * Other arguments are directly returned. This is useful if <b>null</b>
     * references are to be avoided.
     *
     * @param n the source number
     * @return the guaranteed non null result number
     */
    public static NumberWithUnit nonNull(NumberWithUnit n)
    {
        return (n != null) ? n : ZERO;
    }

    static
    {
        ZERO = new NumberWithUnit();
    }
}
