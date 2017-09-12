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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test class for {@code Modifiers}.
 *
 * @author Oliver Heger
 * @version $Id: TestModifiers.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestModifiers
{
    /**
     * Tests whether strings can be parsed to modifiers.
     */
    @Test
    public void testFromString()
    {
        for (Modifiers m : Modifiers.values())
        {
            String s = m.name();
            assertEquals("Wrong modifier", m, Modifiers.fromString(s));
        }
    }

    /**
     * Tests that case is ignored when parsing modifier strings.
     */
    @Test
    public void testFromStringCase()
    {
        assertEquals("Wrong modifier 1", Modifiers.ALT, Modifiers
                .fromString("alt"));
        assertEquals("Wrong modifier 2", Modifiers.ALT, Modifiers
                .fromString("Alt"));
        assertEquals("Wrong modifier 3", Modifiers.ALT, Modifiers
                .fromString("alT"));
        assertEquals("Wrong modifier 1", Modifiers.ALT, Modifiers
                .fromString("AlT"));
    }

    /**
     * Tries to parse an unknown modifiers string. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFromStringUnknown()
    {
        Modifiers.fromString("Not a valid modifier!");
    }

    /**
     * Tries to parse a null modifiers string. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFromStringNull()
    {
        Modifiers.fromString(null);
    }

    /**
     * Tries to parse an empty modifiers string. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFromStringEmpty()
    {
        Modifiers.fromString("");
    }
}
