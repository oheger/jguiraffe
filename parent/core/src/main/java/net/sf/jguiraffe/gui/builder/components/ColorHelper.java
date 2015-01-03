/*
 * Copyright 2006-2015 The JGUIraffe Team.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * <p>
 * A helper class for dealing with colors.
 * </p>
 * <p>
 * This class defines helper methods for converting color values specified in
 * form builder Jelly scripts. In this model colors can be defined in the
 * following different ways:
 * <ul>
 * <li>Using a symbolic name. For this purpose this class defines constants
 * that roughly correspond to the colors defined by the
 * {@code java.awt.Color} class.</li>
 * <li>As a hexadecimal numeric value. In this case the color definition must
 * start with a &quot;#&quot; sign, e.g. {@code #80FF80}. This is
 * analogous to color definitions in HTML.</li>
 * <li>As triple of decimal rgb values. Definitions of this type look like
 * {@code (r, g, b)}, with r, g, b in the range from 0 to 255.</li>
 * <li>Logic color definitions. These are arbitrary strings which are not
 * further interpreted, e.g. style sheet names. To be recognized, such a string
 * must start with a &quot;~&quot; character, e.g. {@code ~MyStyle}.</li>
 * </ul>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ColorHelper.java 205 2012-01-29 18:29:57Z oheger $
 */
public final class ColorHelper
{
    /** Constant for the RGB color definition prefix. */
    static final String COLDEF_RGB_PREFIX = "(";

    /** Constant for the RGB color definition suffix. */
    static final String COLDEF_RGB_SUFFIX = ")";

    /** Constant for a separator used by the toString() method. */
    static final String SEPARATOR = ", ";

    /** Constant for the RGB color definition delimiters. */
    private static final String RGB_DELIMITERS = COLDEF_RGB_PREFIX
            + COLDEF_RGB_SUFFIX + ",; ";

    /** Constant for the hexadecimal color definition character. */
    private static final String COLDEF_HEXA = "#";

    /** Constant for the prefix for logic color definitions. */
    private static final String COLDEF_LOGIC = "~";

    /** Constant for the bit mask for removing the high byte. */
    private static final int BYTE_MASK = 0xFF;

    /** Constant for shifting a byte. */
    private static final int BYTE = 8;

    /** Constant for shifting a word. */
    private static final int WORD = 16;

    /** Constant for the base 16. */
    private static final int BASE_16 = 16;

    /** A list with the names of the existing predefined color names. */
    private static final List<String> PREDEFINED_COLOR_NAMES;

    /**
     * Private constructor so no instance can be created.
     */
    private ColorHelper()
    {
    }

    /**
     * Returns the predefined color with the given name. The passed in name must
     * be one of the names returned by the {@code getPredefinedNames()}
     * method (case does not matter).
     *
     * @param name the name of the desired color
     * @return the color
     * @throws FormBuilderException if this color does not exist
     */
    public static Color getPredefinedColor(String name)
            throws FormBuilderException
    {
        try
        {
            return NamedColor.valueOf(name.toUpperCase(Locale.ENGLISH)).getColor();
        }
        catch (IllegalArgumentException iex)
        {
            throw new FormBuilderException(
                    "No predefined color found: " + name, iex);
        }
        catch (NullPointerException npex)
        {
            throw new FormBuilderException(
                    "Name of predefined color must not be null!");
        }
    }

    /**
     * Returns an iterator with the names (Strings) of all predefined colors.
     * These names can be passed to the {@code getPredefinedColor()}
     * method.
     *
     * @return the names of the predefined colors
     */
    public static Iterator<String> getPredefinedNames()
    {
        return PREDEFINED_COLOR_NAMES.iterator();
    }

    /**
     * The main method for resolving a color definition. This method can be
     * given a color definition in one of the supported flavors. It will try to
     * resolve this definition and return the corresponding {@code Color}
     * object. If this fails, an exception will be thrown.
     *
     * @param c the color definition
     * @return the corresponding color object or <b>null</b> if the passed in
     * color definition was <b>null</b>
     * @throws FormBuilderException if the color definition cannot be resolved
     */
    public static Color resolveColor(String c) throws FormBuilderException
    {
        if (c == null)
        {
            return null;
        }
        else if (c.startsWith(COLDEF_LOGIC))
        {
            return resolveLogicColor(c);
        }
        else if (c.startsWith(COLDEF_HEXA))
        {
            return resolveHexColor(c);
        }
        else if (c.startsWith(COLDEF_RGB_PREFIX)
                && c.endsWith(COLDEF_RGB_SUFFIX))
        {
            return resolveRGBColor(c);
        }
        else
        {
            return getPredefinedColor(c);
        }
    }

    /**
     * Resolves a logic color definition.
     *
     * @param c the color definition
     * @return the resolved {@code Color} instance
     * @throws FormBuilderException if the definition is invalid
     */
    private static Color resolveLogicColor(String c)
            throws FormBuilderException
    {
        try
        {
            return Color.newLogicInstance(c.substring(COLDEF_LOGIC.length()));
        }
        catch (IllegalArgumentException iex)
        {
            throw new FormBuilderException("Invalid logic color definition: "
                    + c, iex);
        }
    }

    /**
     * Resolves the given color definition in the hexadecimal flavor.
     *
     * @param s the color definition
     * @return the resolved color
     * @throws FormBuilderException if the color definition is invalid
     */
    private static Color resolveHexColor(String s) throws FormBuilderException
    {
        try
        {
            int value =
                    Integer.parseInt(s.substring(COLDEF_HEXA.length()), BASE_16);
            return Color.newRGBInstance(value >> WORD,
                    (value >> BYTE) & BYTE_MASK, value & BYTE_MASK);
        }
        catch (NumberFormatException nex)
        {
            throw new FormBuilderException("Invalid color definition: " + s,
                    nex);
        }
        catch (IllegalArgumentException iex)
        {
            throw new FormBuilderException(
                    "Color component out of range in color definition: " + s,
                    iex);
        }
    }

    /**
     * Resolves the given color definition in decimal RGB flavor.
     *
     * @param c the color definition
     * @return the resolved color
     * @throws FormBuilderException if the color definition is invalid
     */
    private static Color resolveRGBColor(String c) throws FormBuilderException
    {
        StringTokenizer tok = new StringTokenizer(c, RGB_DELIMITERS);
        try
        {
            Color col = Color.newRGBInstance(Integer.parseInt(tok.nextToken()),
                    Integer.parseInt(tok.nextToken()), Integer.parseInt(tok
                            .nextToken()));
            if (tok.hasMoreTokens())
            {
                throw new FormBuilderException(
                        "Too many components in color definition: " + c);
            }
            return col;
        }

        catch (NumberFormatException nex)
        {
            throw new FormBuilderException(
                    "Invalid RGB value in color definition: " + c, nex);
        }
        catch (NoSuchElementException nse)
        {
            throw new FormBuilderException(
                    "Too few components in color definition: " + c, nse);
        }
        catch (IllegalArgumentException iex)
        {
            throw new FormBuilderException(
                    "Color component out of range in color definition: " + c,
                    iex);
        }
    }

    // static initializer; initializes the list with the predefined color names
    static
    {
        NamedColor[] values = NamedColor.values();
        List<String> names = new ArrayList<String>(values.length);
        for (NamedColor nc : values)
        {
            names.add(nc.name());
        }
        PREDEFINED_COLOR_NAMES = Collections.unmodifiableList(names);
    }

    /**
     * An enumeration with predefined color constants. The names defined here
     * can be passed to {@code getPredefinedColor()}.
     */
    public static enum NamedColor
    {
        /** Default color black. */
        BLACK(Color.newRGBInstance(0, 0, 0)),

        /** Default color blue. */
        BLUE(Color.newRGBInstance(0, 0, 255)),

        /** Default color cyan. */
        CYAN(Color.newRGBInstance(0, 255, 255)),

        /** Default color dark gray. */
        DARK_GRAY(Color.newRGBInstance(64, 64, 64)),

        /** Default color gray. */
        GRAY(Color.newRGBInstance(128, 128, 128)),

        /** Default color green. */
        GREEN(Color.newRGBInstance(0, 255, 0)),

        /** Default color light gray. */
        LIGHT_GRAY(Color.newRGBInstance(192, 192, 192)),

        /** Default color magenta. */
        MAGENTA(Color.newRGBInstance(255, 0, 255)),

        /** Default color orange. */
        ORANGE(Color.newRGBInstance(255, 200, 0)),

        /** Default color pink. */
        PINK(Color.newRGBInstance(255, 175, 175)),

        /** Default color red. */
        RED(Color.newRGBInstance(255, 0, 0)),

        /** Default color white. */
        WHITE(Color.newRGBInstance(255, 255, 255)),

        /** Default color yellow. */
        YELLOW(Color.newRGBInstance(255, 255, 0));

        /** Stores the referenced {@code Color} object. */
        private final Color color;

        /**
         * Creates a new instance of {@code NamedColor} and sets the
         * associated color.
         *
         * @param c the associated color
         */
        private NamedColor(Color c)
        {
            color = c;
        }

        /**
         * Returns the associated {@code Color} object.
         *
         * @return the {@code Color} represented by this object
         */
        public Color getColor()
        {
            return color;
        }
    }
}
