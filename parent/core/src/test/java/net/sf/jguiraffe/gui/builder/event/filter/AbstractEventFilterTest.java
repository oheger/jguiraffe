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
package net.sf.jguiraffe.gui.builder.event.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.sf.jguiraffe.gui.builder.event.BuilderEvent;
import net.sf.jguiraffe.gui.builder.window.WindowEvent;

import org.junit.Before;
import org.junit.Test;

/**
 * An abstract base class for testing different filter implementations derived
 * from AbstractEventFilter. Some properties of this base class will be tested.
 * Concrete sub classes have to implement the <code>createFilter()</code>
 * method.
 *
 * @author Oliver Heger
 * @version $Id: AbstractEventFilterTest.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class AbstractEventFilterTest
{
    /** The filter to be tested. */
    protected AbstractEventFilter filter;

    @Before
    public void setUp() throws Exception
    {
        filter = createFilter();
    }

    /**
     * Creates the event filter to be tested.
     *
     * @return the filter object
     */
    protected abstract AbstractEventFilter createFilter();

    /**
     * Tests if null values are accepted if the acceptNull flag is not set.
     */
    @Test
    public void testAcceptNullFalse()
    {
        assertFalse("Null value accepted", filter.accept(null));
    }

    /**
     * Tests if null values are accepted if the acceptNull flag is set.
     */
    @Test
    public void testAcceptNullTrue()
    {
        filter.setAcceptNull(true);
        assertTrue("Null value not accepted", filter.accept(null));
    }

    /**
     * Tests if a non supported class will not be accepted.
     */
    @Test
    public void testNonSupportedClass()
    {
        assertFalse("Non supported class accepted", filter.accept(new Object()));
    }

    /**
     * Tests if a filter that was constructed using its default constructor is
     * correctly initialized.
     */
    @Test
    public void testInitialize()
    {
        assertEquals("Incorrect base class", BuilderEvent.class, filter
                .getBaseClass());
        assertFalse("Null values are accepted", filter.isAcceptNull());
    }

    /**
     * Tests setting the filter's base class to a valid class.
     */
    @Test
    public void testSetBaseClassValid()
    {
        filter.setBaseClass(WindowEvent.class);
        assertEquals("Base class was not accepted", WindowEvent.class, filter
                .getBaseClass());
    }

    /**
     * Tests setting an invalid class as base class. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetBaseClassInvalid()
    {
        filter.setBaseClass(Object.class);
    }
}
