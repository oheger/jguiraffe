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
package net.sf.jguiraffe.gui.builder.components.tags;

import net.sf.jguiraffe.gui.builder.components.model.TextIconAlignment;
import junit.framework.TestCase;

/**
 * Test class for StaticTextDataImpl.
 *
 * @author Oliver Heger
 * @version $Id: TestStaticTextDataImpl.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestStaticTextDataImpl extends TestCase
{
    /** The object to be tested. */
    private StaticTextDataImpl textData;

    protected void setUp() throws Exception
    {
        super.setUp();
        textData = new StaticTextDataImpl();
    }

    /**
     * Tests if the alignment is valid directly after an instance was created.
     */
    public void testGetAlignmentAfterInit()
    {
        assertEquals("Default alignment was not set",
                TextIconAlignment.LEFT, textData.getAlignment());
    }

    /**
     * Tests setting the alignment to null. This is not allowed.
     */
    public void testSetAlignmentNull()
    {
        try
        {
            textData.setAlignment(null);
            fail("Could set alignment to null!");
        }
        catch (IllegalArgumentException iex)
        {
            // ok
        }
    }

    /**
     * Tests the equals() method for two empty objects.
     */
    public void testEqualsUndefined()
    {
        assertTrue("Equals not reflexive", textData.equals(textData));
        StaticTextDataImpl std = new StaticTextDataImpl();
        checkEquals(std, true);
    }

    /**
     * Tests the equals method.
     */
    public void testEquals()
    {
        StaticTextDataImpl t2 = new StaticTextDataImpl();
        textData.setText("TestText");
        checkEquals(t2, false);
        t2.setText("TestTex");
        checkEquals(t2, false);
        t2.setText("TestText");
        checkEquals(t2, true);
        t2.setIcon("AnIcon");
        checkEquals(t2, false);
        textData.setIcon("AnIcon");
        checkEquals(t2, true);
        textData.setAlignment(TextIconAlignment.CENTER);
        checkEquals(t2, false);
        t2.setAlignment(TextIconAlignment.CENTER);
        checkEquals(t2, true);
    }

    /**
     * Tests comparing with a null object. Result should be false.
     */
    public void testEqualsNull()
    {
        assertFalse("Object is equal to null", textData.equals(null));
    }

    /**
     * Tests comparing with an object of a different class. Result should be
     * false.
     */
    public void testEqualsOtherClass()
    {
        assertFalse("Object equals object of different class", textData
                .equals("AnotherObject"));
    }

    /**
     * Helper method for checking equals. Tests multiple variants and symmetrie.
     *
     * @param other the object to compare the fixture with
     * @param expected the expected outcome
     */
    private void checkEquals(StaticTextDataImpl other, boolean expected)
    {
        assertEquals("Wrong result of equals", expected, textData.equals(other));
        assertEquals("Equals not symmetric", expected, other.equals(textData));
        if (expected)
        {
            assertEquals("Hash code is different", textData.hashCode(), other
                    .hashCode());
        }
    }
}
