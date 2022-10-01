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
package net.sf.jguiraffe.gui.builder.event.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.sf.jguiraffe.gui.builder.event.BuilderEvent;
import net.sf.jguiraffe.gui.builder.event.FormFocusEvent;
import net.sf.jguiraffe.gui.builder.window.WindowEvent;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for AbstractEventFilter.
 *
 * @author Oliver Heger
 * @version $Id: TestAbstractEventFilter.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestAbstractEventFilter
{
    /** Stores the event filter object to test. */
    private TestEventFilter filter;

    @Before
    public void setUp() throws Exception
    {
        filter = new TestEventFilter();
    }

    /**
     * Tests a newly initialized object.
     */
    @Test
    public void testInitialize()
    {
        assertEquals("Wrong base class", BuilderEvent.class, filter
                .getBaseClass());
        assertFalse("Null values accepted", filter.isAcceptNull());
    }

    /**
     * Tests setting a valid base class.
     */
    @Test
    public void testSetBaseClass()
    {
        filter.setBaseClass(WindowEvent.class);
        assertEquals("Base class was not set", WindowEvent.class, filter
                .getBaseClass());
    }

    /**
     * Tests setting a null base class. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetBaseClassNull()
    {
        filter.setBaseClass(null);
    }

    /**
     * Tests setting an invalid base class. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetBaseClassInvalid()
    {
        filter.setBaseClass(String.class);
    }

    /**
     * Tests invoking the accept() method.
     */
    @Test
    public void testAcceptEvent()
    {
        filter.accept(new BuilderEvent(this));
        assertEquals("acceptEvent() was not called", 1, filter.acceptCount);
    }

    /**
     * Tests invoking the accept method with a generic event when a specific
     * event class has been set.
     */
    @Test
    public void testAcceptValidEvent()
    {
        filter.setBaseClass(FormFocusEvent.class);
        assertFalse("Wrong return value", filter.accept(new BuilderEvent(this)));
        assertEquals("acceptEvent() was called", 0, filter.acceptCount);
    }

    /**
     * Tests invoking the accept method with a non supported object class.
     */
    @Test
    public void testAcceptNotSupportedClass()
    {
        assertFalse("Non supported class accepted", filter.accept(new Object()));
        assertEquals("acceptEvent() was called", 0, filter.acceptCount);
    }

    /**
     * Tests invoking the accept method with a null value.
     */
    @Test
    public void testAcceptNullFalse()
    {
        assertFalse("Null value was accepted", filter.accept(null));
        assertEquals("acceptEvent() was called", 0, filter.acceptCount);
    }

    /**
     * Tests invoking the accept method with a null value when the acceptNull
     * flag is set.
     */
    @Test
    public void testAcceptNullTrue()
    {
        filter.setAcceptNull(true);
        assertTrue("Null value was not accepted", filter.accept(null));
        assertEquals("acceptEvent() was called", 0, filter.acceptCount);
    }

    /**
     * A test implementation of AbstractEventFilter that is used to find out if
     * the <code>acceptEvent()</code> method is correctly called.
     */
    private static class TestEventFilter extends AbstractEventFilter
    {
        public int acceptCount;

        public TestEventFilter()
        {
            super();
        }

        @Override
        protected boolean acceptEvent(BuilderEvent event)
        {
            ++acceptCount;
            return false;
        }
    }
}
