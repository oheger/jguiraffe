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

import java.util.Locale;

/**
 * <p>
 * An enumeration class that defines allowed values for the orientation of
 * components.
 * </p>
 * <p>
 * A couple of components can have either horizontal or vertical orientation,
 * for instance sliders or splitters. This enumeration class defines the allowed
 * values for orientation attributes and provides methods for checking them or
 * converting them to {@code Orientation} instances.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: Orientation.java 205 2012-01-29 18:29:57Z oheger $
 */
public enum Orientation
{
    /** Horizontal orientation. */
    HORIZONTAL,

    /** Vertical orientation. */
    VERTICAL;

    /**
     * Tries to match the given string value with an {@code Orientation}
     * instance. This method checks whether the passed in string corresponds to
     * the name of an enumeration literal (case does not matter when doing the
     * comparison). If a match is found, the corresponding {@code Orientation}
     * instance is returned. Otherwise, the result of this method is
     * <b>null</b>.
     *
     * @param value the value to be searched (case does not matter)
     * @return the corresponding {@code Orientation} instance or <b>null</b>
     */
    public static Orientation findOrientation(String value)
    {
        if (value == null)
        {
            return null;
        }

        String comp = value.toUpperCase(Locale.ENGLISH);
        for (Orientation o : values())
        {
            if (o.name().equals(comp))
            {
                return o;
            }
        }

        return null;
    }

    /**
     * Transforms the given string value into an instance of this enumeration
     * class, using the default {@code Orientation} if the string value is
     * <b>null</b>. This method tries to find a match for the given string. If
     * this is successful, the matching {@code Orientation} instance is
     * returned. Otherwise, an exception is thrown. If the passed in string is
     * <b>null</b> and a default {@code Orientation} is provided, then this
     * default value is returned. If the default {@code Orientation} is
     * <b>null</b>, <b>null</b> input also leads to an exception.
     *
     * @param value the value to be searched (case does not matter)
     * @param defaultOrientation the default {@code Orientation} to be returned
     *        for <b>null</b> strings
     * @return the found {@code Orientation}
     * @throws FormBuilderException if no match is found
     */
    public static Orientation getOrientation(String value,
            Orientation defaultOrientation) throws FormBuilderException
    {
        Orientation o = findOrientation(value);
        if (o != null)
        {
            return o;
        }

        if (value == null && defaultOrientation != null)
        {
            return defaultOrientation;
        }

        throw new FormBuilderException("Cannot resolve Orientation value: "
                + value);
    }

    /**
     * Transforms the given string value into an instance of this enumeration
     * class. This method is basically the same as calling the overloaded
     * {@link #getOrientation(String, Orientation)} method with a default
     * {@code Orientation} of <b>null</b>; i.e. <b>null</b> input also causes an
     * exception.
     *
     * @param value the value to be searched (case does not matter)
     * @return the found {@code Orientation}
     * @throws FormBuilderException if no match is found
     */
    public static Orientation getOrientation(String value)
            throws FormBuilderException
    {
        return getOrientation(value, null);
    }
}
