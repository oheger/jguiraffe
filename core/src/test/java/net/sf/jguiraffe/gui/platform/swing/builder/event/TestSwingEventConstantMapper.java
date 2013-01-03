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
package net.sf.jguiraffe.gui.platform.swing.builder.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EnumSet;
import java.util.Set;

import net.sf.jguiraffe.gui.builder.event.FormMouseEvent;
import net.sf.jguiraffe.gui.builder.event.Keys;
import net.sf.jguiraffe.gui.builder.event.Modifiers;

import org.junit.Test;

/**
 * Test class for {@code SwingEventConstantMapper}.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingEventConstantMapper.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingEventConstantMapper
{
    /** An array with the Swing modifiers. */
    private static final int[] SWING_MODIFIERS = {
            InputEvent.ALT_DOWN_MASK, InputEvent.ALT_GRAPH_DOWN_MASK,
            InputEvent.CTRL_DOWN_MASK, InputEvent.META_DOWN_MASK,
            InputEvent.SHIFT_DOWN_MASK
    };

    /** An array with standard modifiers. */
    private static final Modifiers[] STANDARD_MODIFIERS = {
            Modifiers.ALT, Modifiers.ALT_GRAPH, Modifiers.CONTROL,
            Modifiers.META, Modifiers.SHIFT
    };

    /** An array with the Swing mouse button constants. */
    private static final int[] SWING_BUTTONS = {
            MouseEvent.BUTTON1, MouseEvent.BUTTON2, MouseEvent.BUTTON3
    };

    /** An Array with the standard mouse button constants. */
    private static final int[] STANDARD_BUTTONS = {
            FormMouseEvent.BUTTON1, FormMouseEvent.BUTTON2,
            FormMouseEvent.BUTTON3
    };

    /** An array with the standard key codes. */
    private static final Keys[] STANDARD_KEYS = {
            Keys.BACKSPACE, Keys.DELETE, Keys.DOWN, Keys.END, Keys.ENTER,
            Keys.ESCAPE, Keys.F1, Keys.F2, Keys.F3, Keys.F4, Keys.F5, Keys.F6,
            Keys.F7, Keys.F8, Keys.F9, Keys.F10, Keys.F11, Keys.F12, Keys.F13,
            Keys.F14, Keys.F15, Keys.F16, Keys.HOME, Keys.INSERT, Keys.LEFT,
            Keys.PAGE_DOWN, Keys.PAGE_UP, Keys.PRINT_SCREEN, Keys.RIGHT,
            Keys.SPACE, Keys.TAB, Keys.UP
    };

    /** An array with the Swing-specific key codes. */
    private static final int[] SWING_KEYS = {
            KeyEvent.VK_BACK_SPACE, KeyEvent.VK_DELETE, KeyEvent.VK_DOWN,
            KeyEvent.VK_END, KeyEvent.VK_ENTER, KeyEvent.VK_ESCAPE,
            KeyEvent.VK_F1, KeyEvent.VK_F2, KeyEvent.VK_F3, KeyEvent.VK_F4,
            KeyEvent.VK_F5, KeyEvent.VK_F6, KeyEvent.VK_F7, KeyEvent.VK_F8,
            KeyEvent.VK_F9, KeyEvent.VK_F10, KeyEvent.VK_F11, KeyEvent.VK_F12,
            KeyEvent.VK_F13, KeyEvent.VK_F14, KeyEvent.VK_F15, KeyEvent.VK_F16,
            KeyEvent.VK_HOME, KeyEvent.VK_INSERT, KeyEvent.VK_LEFT,
            KeyEvent.VK_PAGE_DOWN, KeyEvent.VK_PAGE_UP,
            KeyEvent.VK_PRINTSCREEN, KeyEvent.VK_RIGHT, KeyEvent.VK_SPACE,
            KeyEvent.VK_TAB, KeyEvent.VK_UP
    };

    /**
     * Helper method for testing the content of a set.
     *
     * @param set the set
     * @param elements the expected elements
     */
    private static void checkSet(Set<?> set, Object... elements)
    {
        assertEquals("Wrong number of elements", elements.length, set.size());
        for (Object elem : elements)
        {
            assertTrue("Element not found: " + elem, set.contains(elem));
        }
    }

    /**
     * Helper method for constructing a bit mask from the given bits.
     *
     * @param bits the bits
     * @return the resulting map
     */
    private static int mask(int... bits)
    {
        int mask = 0;
        for (int i = 0; i < bits.length; i++)
        {
            mask |= bits[i];
        }
        return mask;
    }

    /**
     * Tests whether all Swing-specific modifiers can be converted.
     */
    @Test
    public void testConvertSwingModifiersSingle()
    {
        for (int i = 0; i < SWING_MODIFIERS.length; i++)
        {
            Set<Modifiers> mods = SwingEventConstantMapper
                    .convertSwingModifiers(SWING_MODIFIERS[i]);
            assertEquals("Wrong number of elements", 1, mods.size());
            assertTrue("Wrong modifier", mods.contains(STANDARD_MODIFIERS[i]));
        }
    }

    /**
     * Tests some combinations of Swing modifiers.
     */
    @Test
    public void testConvertSwingModifiersCombinations()
    {
        Set<Modifiers> mods = SwingEventConstantMapper
                .convertSwingModifiers(InputEvent.ALT_DOWN_MASK
                        | InputEvent.META_DOWN_MASK);
        checkSet(mods, Modifiers.ALT, Modifiers.META);
        mods = SwingEventConstantMapper
                .convertSwingModifiers(InputEvent.ALT_GRAPH_DOWN_MASK
                        | InputEvent.CTRL_DOWN_MASK
                        | InputEvent.SHIFT_DOWN_MASK);
        checkSet(mods, Modifiers.ALT_GRAPH, Modifiers.CONTROL, Modifiers.SHIFT);
    }

    /**
     * Tests whether all Swing modifiers can be converted at once.
     */
    @Test
    public void testConvertSwingModifiersAll()
    {
        int mask = mask(SWING_MODIFIERS);
        checkSet(SwingEventConstantMapper.convertSwingModifiers(mask),
                (Object[]) STANDARD_MODIFIERS);
    }

    /**
     * Tests whether all standard modifiers can be converted one by one.
     */
    @Test
    public void testConvertStandardModifiersSingle()
    {
        for (int i = 0; i < STANDARD_MODIFIERS.length; i++)
        {
            int mods = SwingEventConstantMapper
                    .convertStandardModifiers(EnumSet.of(STANDARD_MODIFIERS[i]));
            assertEquals("Wrong modifier", SWING_MODIFIERS[i], mods);
        }
    }

    /**
     * Tests some combinations of standard modifiers.
     */
    @Test
    public void testConvertStandardModifiersCombinations()
    {
        int mods = SwingEventConstantMapper.convertStandardModifiers(EnumSet
                .of(Modifiers.ALT, Modifiers.ALT_GRAPH, Modifiers.CONTROL));
        assertEquals("Wrong modifiers 1", mask(InputEvent.ALT_DOWN_MASK,
                InputEvent.ALT_GRAPH_DOWN_MASK, InputEvent.CTRL_DOWN_MASK),
                mods);
        mods = SwingEventConstantMapper.convertStandardModifiers(EnumSet.of(
                Modifiers.CONTROL, Modifiers.META, Modifiers.SHIFT));
        assertEquals("Wrong modifiers 2", mask(InputEvent.CTRL_DOWN_MASK,
                InputEvent.META_DOWN_MASK, InputEvent.SHIFT_DOWN_MASK), mods);
    }

    /**
     * Tests whether all standard modifiers can be converted at once.
     */
    @Test
    public void testConvertStandardModifiersAll()
    {
        assertEquals("Wrong modifiers", mask(SWING_MODIFIERS),
                SwingEventConstantMapper.convertStandardModifiers(EnumSet
                        .allOf(Modifiers.class)));
    }

    /**
     * Tests whether the button constants used by this test are complete.
     */
    @Test
    public void testButtonConstants()
    {
        assertEquals("Wrong lengths of button arrays", SWING_BUTTONS.length,
                STANDARD_BUTTONS.length);
    }

    /**
     * Tests the conversion of Swing-specific mouse buttons.
     */
    @Test
    public void testConvertSwingButtons()
    {
        for (int i = 0; i < SWING_BUTTONS.length; i++)
        {
            assertEquals("Wrong standard button at " + i, STANDARD_BUTTONS[i],
                    SwingEventConstantMapper
                            .convertSwingButtons(SWING_BUTTONS[i]));
        }
    }

    /**
     * Tests the conversion of Swing-specific mouse buttons if invalid button
     * indices are involved.
     */
    @Test
    public void testConvertSwingButtonsUnknownButton()
    {
        assertEquals("Got button (1)", FormMouseEvent.NO_BUTTON,
                SwingEventConstantMapper
                        .convertSwingButtons(MouseEvent.NOBUTTON));
        assertEquals("Got button (2)", FormMouseEvent.NO_BUTTON,
                SwingEventConstantMapper.convertSwingButtons(Integer.MAX_VALUE));
    }

    /**
     * Tests the conversion of standard mouse buttons to Swing constants.
     */
    @Test
    public void testConvertStandardButtons()
    {
        for (int i = 0; i < STANDARD_BUTTONS.length; i++)
        {
            assertEquals("Wrong Swing button at " + i, SWING_BUTTONS[i],
                    SwingEventConstantMapper
                            .convertStandardButtons(STANDARD_BUTTONS[i]));
        }
    }

    /**
     * Tests the conversion of standard mouse buttons to Swing constants if
     * invalid button indices are involved.
     */
    @Test
    public void testConvertStandardButtonsUnknownButtons()
    {
        assertEquals("Got button (1)", MouseEvent.NOBUTTON,
                SwingEventConstantMapper
                        .convertStandardButtons(FormMouseEvent.NO_BUTTON));
        assertEquals("Got button (1)", MouseEvent.NOBUTTON,
                SwingEventConstantMapper
                        .convertStandardButtons(Integer.MIN_VALUE));
    }

    /**
     * Tests whether the constants used by the key tests are complete.
     */
    @Test
    public void testKeyConstants()
    {
        assertEquals("Wrong number of key constants", Keys.values().length,
                STANDARD_KEYS.length);
        assertEquals("Wrong number of Swing keys", STANDARD_KEYS.length,
                SWING_KEYS.length);
    }

    /**
     * Tests whether Swing key codes can be converted.
     */
    @Test
    public void testConvertSwingKey()
    {
        for (int i = 0; i < SWING_KEYS.length; i++)
        {
            assertEquals("Wrong standard key for " + SWING_KEYS[i],
                    STANDARD_KEYS[i], SwingEventConstantMapper
                            .convertSwingKey(SWING_KEYS[i]));
        }
    }

    /**
     * Tries to convert an unknown Swing key.
     */
    @Test
    public void testConvertSwingKeyUnknown()
    {
        assertNull("Got a result", SwingEventConstantMapper
                .convertSwingKey(KeyEvent.VK_0));
    }

    /**
     * Tests whether standard key codes can be coverted.
     */
    @Test
    public void testConvertStandardKey()
    {
        for (int i = 0; i < STANDARD_KEYS.length; i++)
        {
            assertEquals("Wrong Swing key for " + STANDARD_KEYS[i],
                    SWING_KEYS[i], SwingEventConstantMapper
                            .convertStandardKey(STANDARD_KEYS[i]));
        }
    }

    /**
     * Tries to convert the null standard key.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConvertStandardKeyNull()
    {
        SwingEventConstantMapper.convertStandardKey(null);
    }
}
