/*
 * Copyright 2006-2017 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.event;

import java.util.Locale;

/**
 * <p>
 * An enumeration class representing special modifier keys.
 * </p>
 * <p>
 * A typical key board contains some special modifier keys that can be pressed
 * together with other keys modifying their state and meaning, e.g. the
 * <em>SHIFT</em> or the <em>ALT</em> key. This class defines constants for such
 * keys. Most UI toolkits define similar constants. This enumeration class
 * provides a portable way of specifying such modifiers.
 * </p>
 * <p>
 * The constants defined by this class can be used by several other classes in
 * the <em>JGUIraffe</em> library. For instance, accelerators for actions (i.e.
 * key board short cuts) may be assigned some modifiers that need to be pressed
 * for activating the accelerator. When a mouse event is received, it is also
 * possible to query the status of the modifier keys.
 * </p>
 * <p>
 * <strong>Tip:</strong> When working with modifiers often sets are needed
 * because other objects (e.g. accelerators) can be associated with an arbitrary
 * number of modifiers. In such cases the {@code java.util.EnumSet} class can be
 * used. It provides convenience methods for creating sets containing specific
 * combinations of {@code Modifiers} constants. The following code fragment
 * demonstrates how such a set can be obtained:
 *
 * <pre>
 * EnumSet&lt;Modifiers&gt; mods = EnumSet.of(Modifiers.ALT, Modifiers.SHIFT);
 * </pre>
 *
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: Modifiers.java 205 2012-01-29 18:29:57Z oheger $
 */
public enum Modifiers
{
    /** Constant representing the ALT modifier key. */
    ALT,

    /** Constant representing the ALT GRAPH modifier key. */
    ALT_GRAPH,

    /** Constant representing the CONTROL modifier key. */
    CONTROL,

    /** Constant representing the META modifier key. */
    META,

    /** Constant representing the SHIFT modifier key. */
    SHIFT;

    /**
     * Tries to find a {@code Modifiers} constant for the specified string. The
     * string is compared with the constants defined in this class ignoring
     * case. If a match is found, the corresponding {@code Modifiers} constant
     * is returned. Otherwise, an exception is thrown.
     *
     * @param s the string to be parsed
     * @return the corresponding {@code Modifiers} constant
     * @throws IllegalArgumentException if no match can be found
     */
    public static Modifiers fromString(String s)
    {
        if (s == null)
        {
            throw new IllegalArgumentException(
                    "String to be parsed must not be null!");
        }

        return valueOf(s.toUpperCase(Locale.ENGLISH));
    }
}
