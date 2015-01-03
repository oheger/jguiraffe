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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Locale;

import org.junit.Test;

/**
 * Test class for {@code Orientation}.
 *
 * @author Oliver Heger
 * @version $Id: TestOrientation.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestOrientation
{
    /**
     * Tests findOrientation() if the value exists.
     */
    @Test
    public void testFindOrientationExisting()
    {
        for (Orientation o : Orientation.values())
        {
            assertSame("Orientation not found: " + o, o, Orientation
                    .findOrientation(o.name()));
        }
    }

    /**
     * Tests whether findOrientation() ignores case.
     */
    @Test
    public void testFindOrientationCase()
    {
        for (Orientation o : Orientation.values())
        {
            assertSame("Orientation not found: " + o, o, Orientation
                    .findOrientation(o.name().toLowerCase(Locale.ENGLISH)));
        }
    }

    /**
     * Tests findOrientation() for an unknown orientation.
     */
    @Test
    public void testFindOrientationUnknown()
    {
        assertNull("Got a result", Orientation
                .findOrientation("unknown orientation"));
    }

    /**
     * Tests findOrientation() for null input.
     */
    @Test
    public void testFindOrientationNull()
    {
        assertNull("Got a result", Orientation.findOrientation(null));
    }

    /**
     * Tests getOrientation() for existing values.
     */
    @Test
    public void testGetOrientationExisting() throws FormBuilderException
    {
        for (Orientation o : Orientation.values())
        {
            assertSame("Orientation not found: " + o, o, Orientation
                    .getOrientation(o.name(), Orientation.HORIZONTAL));
        }
    }

    /**
     * Tests whether a default orientation value is returned.
     */
    @Test
    public void testGetOrientationDefault() throws FormBuilderException
    {
        assertSame("Wrong default orientation", Orientation.VERTICAL,
                Orientation.getOrientation(null, Orientation.VERTICAL));
    }

    /**
     * Tests getOrientation() for an unknown orientation.
     */
    @Test(expected = FormBuilderException.class)
    public void testGetOrientationUnknown() throws FormBuilderException
    {
        Orientation.getOrientation("unknown orientation",
                Orientation.HORIZONTAL);
    }

    /**
     * Tests getOrientation() for null input if there is no default value.
     */
    @Test(expected = FormBuilderException.class)
    public void testGetOrientationNullNoDefault() throws FormBuilderException
    {
        Orientation.getOrientation(null);
    }
}
