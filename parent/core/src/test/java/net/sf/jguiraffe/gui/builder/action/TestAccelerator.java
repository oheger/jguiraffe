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
package net.sf.jguiraffe.gui.builder.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.EnumSet;
import java.util.Set;

import net.sf.jguiraffe.gui.builder.event.Keys;
import net.sf.jguiraffe.gui.builder.event.Modifiers;

import org.junit.Test;

/**
 * Test class for Accelerator.
 *
 * @author Oliver Heger
 * @version $Id: TestAccelerator.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestAccelerator
{
    /**
     * Tests obtaining an instance for a special key.
     */
    @Test
    public void testGetInstanceSpecialKey()
    {
        Accelerator acc = Accelerator.getInstance(Keys.F1, null);
        assertEquals("Wrong special key", Keys.F1, acc
                .getSpecialKey());
        assertNull("Character is set", acc.getKey());
        assertNull("Key code is set", acc.getKeyCode());
    }

    /**
     * Tests obtaining an instance for a null special key. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetInstanceSpecialKeyNull()
    {
        Accelerator.getInstance((Keys) null, null);
    }

    /**
     * Tests obtaining an instance for a printable key.
     */
    @Test
    public void testGetInstanceKey()
    {
        Accelerator acc = Accelerator.getInstance('a', null);
        assertEquals("Wrong key", Character.valueOf('a'), acc.getKey());
        assertNull("Special key is set", acc.getSpecialKey());
        assertNull("Key code is set", acc.getKeyCode());
    }

    /**
     * Tests obtaining an instance for a null key. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetInstanceKeyNull()
    {
        Accelerator.getInstance((Character) null, null);
    }

    /**
     * Tests obtaining an instance for a key code.
     */
    @Test
    public void testGetInstanceKeyCode()
    {
        Accelerator acc = Accelerator.getInstance(42, null);
        assertEquals("Wrong key code", Integer.valueOf(42), acc.getKeyCode());
        assertNull("Character is set", acc.getKey());
        assertNull("Special key is set", acc.getSpecialKey());
    }

    /**
     * Tests obtaining an instance for a null key code. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetInstanceKeyCodeNull()
    {
        Accelerator.getInstance((Integer) null, null);
    }

    /**
     * Tests obtaining an instance with modifiers.
     */
    @Test
    public void testGetInstanceModifiers()
    {
        Accelerator acc = Accelerator.getInstance('x', EnumSet.of(
                Modifiers.SHIFT, Modifiers.ALT));
        Set<Modifiers> mods = acc.getModifiers();
        assertEquals("Wrong number of modifiers", 2, mods.size());
        assertTrue("ALT not found", mods.contains(Modifiers.ALT));
        assertTrue("SHIFT not found", mods.contains(Modifiers.SHIFT));
    }

    /**
     * Tests whether a copy of the modifiers set is created.
     */
    @Test
    public void testGetInstanceModifiersDefensiveCopy()
    {
        EnumSet<Modifiers> mods = EnumSet.of(Modifiers.SHIFT);
        Accelerator acc = Accelerator.getInstance('x', mods);
        mods.add(Modifiers.ALT);
        assertEquals("Modifiers were changed", 1, acc.getModifiers().size());
        assertTrue("Wrong modifier", acc.getModifiers().contains(
                Modifiers.SHIFT));
    }

    /**
     * Tests that the set returned by getModifiers() cannot be modified.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetModifiersModify()
    {
        Accelerator acc = Accelerator.getInstance('x', EnumSet.of(
                Modifiers.SHIFT, Modifiers.ALT));
        Set<Modifiers> mods = acc.getModifiers();
        mods.clear();
    }

    /**
     * Tests parsing a simple char.
     */
    @Test
    public void testParseChar()
    {
        Accelerator acc = Accelerator.parse("J");
        assertEquals("Wrong char", 'J', acc.getKey().charValue());
        assertTrue("Got modifiers", acc.getModifiers().isEmpty());
    }

    /**
     * Tests parsing a numeric key code.
     */
    @Test
    public void testParseKeyCode()
    {
        Accelerator acc = Accelerator.parse("42");
        assertEquals("Wrong key code", 42, acc.getKeyCode().intValue());
        assertTrue("Got modifiers", acc.getModifiers().isEmpty());
    }

    /**
     * Tests parsing a numeric character. This has to be tested explicitly
     * because it interferes with numeric key codes.
     */
    @Test
    public void testParseCharNumeric()
    {
        Accelerator acc = Accelerator.parse("5");
        assertEquals("Wrong char", '5', acc.getKey().charValue());
    }

    /**
     * Tests parsing a numeric key less than 10. Such codes must be padded with
     * a leading 0 to distinguish them from characters.
     */
    @Test
    public void testParseKeyCodeSingleDigit()
    {
        Accelerator acc = Accelerator.parse("05");
        assertEquals("Wrong key code", 5, acc.getKeyCode().intValue());
    }

    /**
     * Tests parsing a special key.
     */
    @Test
    public void testParseSpecialKey()
    {
        Accelerator acc = Accelerator.parse("HOME");
        assertEquals("Wrong special key", Keys.HOME, acc
                .getSpecialKey());
        assertTrue("Got modifiers", acc.getModifiers().isEmpty());
    }

    /**
     * Tests that case does not matter when parsing special keys.
     */
    @Test
    public void testParseSpecialKeyCase()
    {
        Accelerator acc = Accelerator.parse("Page_DowN");
        assertEquals("Wrong special key", Keys.PAGE_DOWN, acc
                .getSpecialKey());
    }

    /**
     * Tests parsing an invalid key. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testParseInvalidKey()
    {
        Accelerator.parse("HOME1");
    }

    /**
     * Tests parsing an accelerator with modifiers.
     */
    @Test
    public void testParseModifiers()
    {
        Accelerator acc = Accelerator
                .parse("ALT alt_Graph   Control\tmeTA \n\rShift j");
        assertEquals("Wrong modifiers", EnumSet.allOf(Modifiers.class), acc
                .getModifiers());
        assertEquals("Wrong char", 'j', acc.getKey().charValue());
    }

    /**
     * Tests parsing a string that contains only modifiers. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testParseModifiersOnly()
    {
        Accelerator.parse("alt control");
    }

    /**
     * Tests parsing a string that contains an invalid modifier. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testParseInvalidModifier()
    {
        Accelerator.parse("control shift altGraph X");
    }

    /**
     * Tests parsing a string with modifiers and an invalid key specification.
     * This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testParseModifiersInvalidKeySpec()
    {
        Accelerator.parse("shift HomeX");
    }

    /**
     * Tests parsing a null string. Result should be null.
     */
    @Test
    public void testParseNull()
    {
        assertNull("Wrong result for null string", Accelerator.parse(null));
    }

    /**
     * Tests parsing an empty string. Result should be null.
     */
    @Test
    public void testParseEmpty()
    {
        assertNull("Wrong result for empty string", Accelerator.parse(""));
    }

    /**
     * Tests parsing a string that contains only whitespace. Result should be
     * null.
     */
    @Test
    public void testParseWhitespaceOnly()
    {
        assertNull("Wrong result for whitespace string (1)", Accelerator
                .parse(" \t\n\r"));
        assertNull("Wrong result for whitespace string (2)", Accelerator
                .parse("    "));
    }

    /**
     * Tests toString() for a character accelerator.
     */
    @Test
    public void testToStringCharacter()
    {
        Accelerator acc = Accelerator.getInstance('j', null);
        assertEquals("Wrong string for character", "j", acc.toString());
    }

    /**
     * Tests toString() for an accelerator with a key code.
     */
    @Test
    public void testToStringKeyCode()
    {
        Accelerator acc = Accelerator.getInstance(Integer.valueOf(42), null);
        assertEquals("Wrong string for key code", "42", acc.toString());
    }

    /**
     * Tests toString() for a key code with a single digit. Here a leading 0
     * must be added.
     */
    @Test
    public void testToStringKeyCodeSingleDigit()
    {
        Accelerator acc = Accelerator.getInstance(Integer.valueOf(5), null);
        assertEquals("Wrong string for key code with single digit", "05", acc
                .toString());
    }

    /**
     * Tests toString for an accelerator with a special key.
     */
    @Test
    public void testToStringSpecialKey()
    {
        Accelerator acc = Accelerator.getInstance(Keys.BACKSPACE,
                null);
        assertEquals("Wrong string for special key", "BACKSPACE", acc
                .toString());
    }

    /**
     * Tests toString() when modifiers are involved.
     */
    @Test
    public void testToStringModifiers()
    {
        Accelerator acc = Accelerator.getInstance(Keys.ENTER,
                EnumSet.of(Modifiers.CONTROL, Modifiers.ALT));
        assertEquals("Wrong string with modifiers", "ALT CONTROL ENTER", acc
                .toString());
        ;
    }

    /**
     * Helper method for checking the equals() implementation.
     *
     * @param acc the accelerator object
     * @param c the object to compare to
     * @param expected the expected outcome of equals
     */
    private static void checkEquals(Accelerator acc, Object c, boolean expected)
    {
        assertEquals("Wrong result of equals", expected, acc.equals(c));
        if (c != null)
        {
            assertEquals("Not symmetric", expected, c.equals(acc));
        }
        if (expected)
        {
            assertEquals("Different hash codes", acc.hashCode(), c.hashCode());
        }
    }

    /**
     * Tests whether an accelerator object equals itself.
     */
    @Test
    public void testEqualsReflexive()
    {
        Accelerator acc = Accelerator.getInstance('j', null);
        checkEquals(acc, acc, true);
    }

    /**
     * Tests various variants of comparisons where no modifiers are involved.
     */
    @Test
    public void testEqualsNoModifiers()
    {
        Accelerator acc = Accelerator.getInstance('j', null);
        Accelerator accCode = Accelerator.getInstance(Integer.valueOf('j'),
                null);
        Accelerator accSpec = Accelerator.getInstance(Keys.DOWN,
                null);
        checkEquals(acc, Accelerator.getInstance('j', null), true);
        checkEquals(acc, accSpec, false);
        checkEquals(acc, accCode, false);
        checkEquals(accSpec, accCode, false);
        checkEquals(accSpec, Accelerator.getInstance(Keys.DOWN,
                null), true);
        checkEquals(accCode, Accelerator
                .getInstance(Integer.valueOf('j'), null), true);
    }

    /**
     * Tests comparisons with modifier flags.
     */
    @Test
    public void testEqualsModifiers()
    {
        Accelerator acc = Accelerator.getInstance('j', null);
        Accelerator acc2 = Accelerator.getInstance('j', EnumSet
                .of(Modifiers.SHIFT));
        checkEquals(acc, acc2, false);
        acc = Accelerator.getInstance('j', EnumSet.of(Modifiers.CONTROL));
        checkEquals(acc, acc2, false);
        acc2 = Accelerator.getInstance('j', EnumSet.of(Modifiers.CONTROL));
        checkEquals(acc, acc2, true);
        acc = Accelerator.getInstance('j', EnumSet.allOf(Modifiers.class));
        checkEquals(acc, acc2, false);
        acc2 = Accelerator.getInstance('j', EnumSet.allOf(Modifiers.class));
        checkEquals(acc, acc2, true);
    }

    /**
     * Tests equals when an object of another class is passed in.
     */
    @Test
    public void testEqualsOtherClass()
    {
        checkEquals(Accelerator.getInstance('j', null), this, false);
    }
}
