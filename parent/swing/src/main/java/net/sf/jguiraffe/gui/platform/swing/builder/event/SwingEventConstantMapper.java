/*
 * Copyright 2006-2022 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.platform.swing.builder.event;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.jguiraffe.gui.builder.event.FormMouseEvent;
import net.sf.jguiraffe.gui.builder.event.Keys;
import net.sf.jguiraffe.gui.builder.event.Modifiers;

/**
 * <p>
 * An utility class for converting Swing-specific constants related to events to
 * the toolkit-independent constants used by the <em>JGUIraffe</em> library.
 * </p>
 * <p>
 * When dealing with events there are frequently constants involved, for
 * instance bit masks for modifier keys, mouse buttons, etc. The
 * <em>JGUIraffe</em> library provides corresponding constants for its own event
 * model. The Swing-specific adapter implementations have to translate these
 * constants to the values and data structures used by Swing. This is the main
 * purpose of this class.
 * </p>
 * <p>
 * This is a static utility class, no instance can be created. It provides
 * methods for constant conversions for several types of constants. The methods
 * follow a typical naming pattern: They start with {@code convert} and end with
 * the type of constants that are converted. The middle part of the name
 * determines the parameter that is passed to the method. This can be either
 * {@code Swing} for a Swing-specific constant or {@code Standard} for a
 * constant defined by <em>JGUIraffe</em>. The return value is a constant of the
 * opposite type. For instance, the method {@code convertSwingModifiers()}
 * expects Swing-specific keyboard modifiers and converts them into constants
 * used by <em>JGUIraffe</em>. The counterpart of this method is named {@code
 * convertStandardModifiers()}.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingEventConstantMapper.java 205 2012-01-29 18:29:57Z oheger $
 */
public final class SwingEventConstantMapper
{
    /** A mapping between standard modifiers and Swing constants. */
    private static final Map<Modifiers, Integer> MODIFIER_MAPPING;

    /** A mapping between standard key codes and Swing key codes. */
    private static final Map<Keys, Integer> STANDARD_KEY_MAPPING;

    /** A mapping between Swing key codes and standard key codes. */
    private static final Map<Integer, Keys> SWING_KEY_MAPPING;

    /**
     * Private constructor. No instances can be created.
     */
    private SwingEventConstantMapper()
    {
    }

    /**
     * Converts Swing-specific keyboard modifiers into standard modifiers.
     *
     * @param modifiers the Swing-specific bit mask with modifiers
     * @return the corresponding set with standard modifiers
     */
    public static Set<Modifiers> convertSwingModifiers(int modifiers)
    {
        Set<Modifiers> result = EnumSet.noneOf(Modifiers.class);

        for (Map.Entry<Modifiers, Integer> e : MODIFIER_MAPPING.entrySet())
        {
            if ((modifiers & e.getValue().intValue()) != 0)
            {
                result.add(e.getKey());
            }
        }

        return result;
    }

    /**
     * Converts standard keyboard modifiers into Swing-specific modifiers.
     *
     * @param modifiers the set with standard modifiers
     * @return the Swing-specific bit mask with modifiers
     */
    public static int convertStandardModifiers(Set<Modifiers> modifiers)
    {
        int result = 0;

        for (Modifiers m : modifiers)
        {
            result |= MODIFIER_MAPPING.get(m);
        }

        return result;
    }

    /**
     * Converts Swing-specific mouse button indices to standard button
     * constants. If the index is invalid, the constant for no button is
     * returned.
     *
     * @param button the button index
     * @return the corresponding standard button constant
     */
    public static int convertSwingButtons(int button)
    {
        switch (button)
        {
        case MouseEvent.BUTTON1:
            return FormMouseEvent.BUTTON1;
        case MouseEvent.BUTTON2:
            return FormMouseEvent.BUTTON2;
        case MouseEvent.BUTTON3:
            return FormMouseEvent.BUTTON3;
        default:
            return FormMouseEvent.NO_BUTTON;
        }
    }

    /**
     * Converts standard mouse button indices to Swing-specific button
     * constants. If the index is invalid, the constant for no button is
     * returned.
     *
     * @param button the button index
     * @return the corresponding Swing button constant
     */
    public static int convertStandardButtons(int button)
    {
        switch (button)
        {
        case FormMouseEvent.BUTTON1:
            return MouseEvent.BUTTON1;
        case FormMouseEvent.BUTTON2:
            return MouseEvent.BUTTON2;
        case FormMouseEvent.BUTTON3:
            return MouseEvent.BUTTON3;
        default:
            return MouseEvent.NOBUTTON;
        }
    }

    /**
     * Converts a Swing-specific key code to a standard {@code Keys} enumeration
     * constant. Result can be <b>null</b> if the code cannot be converted.
     *
     * @param key the Swing-specific key code
     * @return the corresponding {@code Keys} constant or <b>null</b>
     */
    public static Keys convertSwingKey(int key)
    {
        return SWING_KEY_MAPPING.get(key);
    }

    /**
     * Converts a standard key code to the Swing-specific equivalent. If
     * <b>null</b> is passed in, an exception is thrown.
     *
     * @param key the standard key
     * @return the corresponding Swing-specific key code
     */
    public static int convertStandardKey(Keys key)
    {
        if (key == null)
        {
            throw new IllegalArgumentException("Cannot convert null key!");
        }
        return STANDARD_KEY_MAPPING.get(key);
    }

    /**
     * Helper method for initializing the modifiers mapping.
     *
     * @return the map with the mapping
     */
    private static Map<Modifiers, Integer> initModifiersMapping()
    {
        Map<Modifiers, Integer> map = new EnumMap<Modifiers, Integer>(
                Modifiers.class);
        map.put(Modifiers.ALT, InputEvent.ALT_DOWN_MASK);
        map.put(Modifiers.ALT_GRAPH, InputEvent.ALT_GRAPH_DOWN_MASK);
        map.put(Modifiers.CONTROL, InputEvent.CTRL_DOWN_MASK);
        map.put(Modifiers.META, InputEvent.META_DOWN_MASK);
        map.put(Modifiers.SHIFT, InputEvent.SHIFT_DOWN_MASK);
        return map;
    }

    /**
     * Helper method for initializing the map with the standard key mapping.
     *
     * @return the map with the mapping
     */
    private static Map<Keys, Integer> initStandardKeyMapping()
    {
        Map<Keys, Integer> map = new EnumMap<Keys, Integer>(Keys.class);
        map.put(Keys.BACKSPACE, KeyEvent.VK_BACK_SPACE);
        map.put(Keys.DELETE, KeyEvent.VK_DELETE);
        map.put(Keys.DOWN, KeyEvent.VK_DOWN);
        map.put(Keys.END, KeyEvent.VK_END);
        map.put(Keys.ENTER, KeyEvent.VK_ENTER);
        map.put(Keys.ESCAPE, KeyEvent.VK_ESCAPE);
        map.put(Keys.F1, KeyEvent.VK_F1);
        map.put(Keys.F2, KeyEvent.VK_F2);
        map.put(Keys.F3, KeyEvent.VK_F3);
        map.put(Keys.F4, KeyEvent.VK_F4);
        map.put(Keys.F5, KeyEvent.VK_F5);
        map.put(Keys.F6, KeyEvent.VK_F6);
        map.put(Keys.F7, KeyEvent.VK_F7);
        map.put(Keys.F8, KeyEvent.VK_F8);
        map.put(Keys.F9, KeyEvent.VK_F9);
        map.put(Keys.F10, KeyEvent.VK_F10);
        map.put(Keys.F11, KeyEvent.VK_F11);
        map.put(Keys.F12, KeyEvent.VK_F12);
        map.put(Keys.F13, KeyEvent.VK_F13);
        map.put(Keys.F14, KeyEvent.VK_F14);
        map.put(Keys.F15, KeyEvent.VK_F15);
        map.put(Keys.F16, KeyEvent.VK_F16);
        map.put(Keys.HOME, KeyEvent.VK_HOME);
        map.put(Keys.INSERT, KeyEvent.VK_INSERT);
        map.put(Keys.LEFT, KeyEvent.VK_LEFT);
        map.put(Keys.PAGE_DOWN, KeyEvent.VK_PAGE_DOWN);
        map.put(Keys.PAGE_UP, KeyEvent.VK_PAGE_UP);
        map.put(Keys.PRINT_SCREEN, KeyEvent.VK_PRINTSCREEN);
        map.put(Keys.RIGHT, KeyEvent.VK_RIGHT);
        map.put(Keys.SPACE, KeyEvent.VK_SPACE);
        map.put(Keys.TAB, KeyEvent.VK_TAB);
        map.put(Keys.UP, KeyEvent.VK_UP);
        return map;
    }

    /**
     * Initializes the map with the Swing-specific key mapping. Note: This
     * implementation uses the standard key mapping, so
     * {@link #initStandardKeyMapping()} must have been called before.
     *
     * @return the map with the mapping
     */
    private static Map<Integer, Keys> initSwingKeyMapping()
    {
        assert STANDARD_KEY_MAPPING != null : "No standard key mapping!";

        Map<Integer, Keys> map = new HashMap<Integer, Keys>();
        for (Map.Entry<Keys, Integer> e : STANDARD_KEY_MAPPING.entrySet())
        {
            map.put(e.getValue(), e.getKey());
        }
        return map;
    }

    static
    {
        MODIFIER_MAPPING = initModifiersMapping();
        STANDARD_KEY_MAPPING = initStandardKeyMapping();
        SWING_KEY_MAPPING = initSwingKeyMapping();
    }
}
