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

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * An enumeration class for defining units.
 * </p>
 * <p>
 * Some of the layout classes can deal with different units, e.g. with
 * centimeters, pixels or dialog units. This class is an enumeration class which
 * defines constants for the supported units. It also provides functionality for
 * converting a number in their represented unit into the default unit pixel.
 * </p>
 * <p>
 * Usually this class will not be used directly. Instead convenience classes
 * will be used which combine numeric values with a unit.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: Unit.java 205 2012-01-29 18:29:57Z oheger $
 */
public enum Unit
{
    /**
     * The unit <em>pixel</em>. Pixels are the default unit. They do not require
     * any special conversion logic.
     */
    PIXEL("px")
    {
        /**
         * {@inheritDoc} This implementation just rounds the floating point
         * number to obtain the integer pixel value.
         */
        @Override
        public int toPixel(double value, UnitSizeHandler handler, Object comp,
                boolean y)
        {
            return (int) Math.round(value);
        }
    },

    /**
     * The unit <em>inch</em>. Inches can be converted to pixels based on the
     * current screen resolution, which is defined in pixels per inch.
     */
    INCH("in")
    {
        /**
         * {@inheritDoc} This implementation applies the screen resolution to
         * the inch value.
         */
        @Override
        public int toPixel(double value, UnitSizeHandler handler, Object comp,
                boolean y)
        {
            return (int) Math.round(value * handler.getScreenResolution());
        }
    },

    /**
     * The unit <em>cm</em>. Centimeters work similar to inches. The conversion
     * to pixels is also based on the current screen resolution. However, a
     * different conversion factor is used.
     */
    CM("cm")
    {
        /**
         * {@inheritDoc} This implementation transforms the centimeter value to
         * inches and applies the screen resolution.
         */
        @Override
        public int toPixel(double value, UnitSizeHandler handler, Object comp,
                boolean y)
        {
            return (int) Math.round(value * CM_FACTOR
                    * handler.getScreenResolution());
        }
    },

    /**
     * The unit <em>dlu</em>. A &quot;dialog unit&quot; is a unit whose exact
     * size depends on the font of the container it is used in. A horizontal
     * dialog unit equals the fourth part of the container's font's average
     * character width; a vertical dialog unit equals the eighth part of the
     * character height. So to transform a value in this unit to pixels always
     * the corresponding container component must be known.
     */
    DLU("dlu")
    {
        /**
         * {@inheritDoc} This implementation performs the transformation
         * depending on the direction and the font size of the container object.
         */
        @Override
        public int toPixel(double value, UnitSizeHandler handler, Object comp,
                boolean y)
        {
            int factor = y ? DLU_Y_FACTOR : DLU_X_FACTOR;
            return (int) Math.round((value * handler.getFontSize(comp, y))
                    / factor);
        }
    };

    /** Constant for the DLU width factor. */
    private static final int DLU_X_FACTOR = 4;

    /** Constant for the DLU height factor. */
    private static final int DLU_Y_FACTOR = 8;

    /** Constant for the inch factor. */
    private static final double INCH_FACTOR = 2.54;

    /** Constant for transforming a centimeter value to an inch. */
    private static final double CM_FACTOR = 1 / INCH_FACTOR;

    /** The mapping between short unit names and enumeration constants. */
    private static final Map<String, Unit> UNIT_NAMES;

    /** Stores the short name of the unit. */
    private final String unitName;

    /**
     * Creates a new instance of {@code Unit} and sets the short name of the
     * unit.
     *
     * @param n the unit short name
     */
    Unit(String n)
    {
        unitName = n;
    }

    /**
     * Returns the short unit name. This is something like "cm" or "in". This
     * name can be passed to the {@link #fromString(String)} method for
     * obtaining the corresponding enumeration literal.
     *
     * @return the short unit name
     */
    public String getUnitName()
    {
        return unitName;
    }

    /**
     * Converts the specified value from this unit into pixels. This can be used
     * for calculating values in the default unit pixels.
     *
     * @param value the value to be converted
     * @param handler the size handler implementation; this is needed by complex
     *        unit classes
     * @param comp the affected component; can be used by derived classes to
     *        obtain needed information
     * @param y a flag whether the value is to be converted for the X or the Y
     *        direction; some units may make a distinction
     * @return the value in pixels
     */
    public abstract int toPixel(double value, UnitSizeHandler handler,
            Object comp, boolean y);

    /**
     * Returns the {@code Unit} constant for the unit with the given short name.
     * This method works like the default {@code valueOf()} method available for
     * each enumeration class. However, it operates on the short unit names as
     * returned by {@link #getUnitName()}. It is case insensitive and throws an
     * exception if the name cannot be resolved.
     *
     * @param name the short name of the unit
     * @return the {@code Unit} constant with this short name
     * @throws IllegalArgumentException if the name cannot be resolved
     */
    public static Unit fromString(String name)
    {
        Unit result = null;
        if (name != null)
        {
            result = UNIT_NAMES.get(name.toLowerCase());
        }

        if (result == null)
        {
            throw new IllegalArgumentException("Unknown unit: " + name);
        }
        return result;
    }

    // static initializer
    static
    {
        UNIT_NAMES = new HashMap<>();
        for (Unit u : values())
        {
            UNIT_NAMES.put(u.getUnitName(), u);
        }
    }
}
