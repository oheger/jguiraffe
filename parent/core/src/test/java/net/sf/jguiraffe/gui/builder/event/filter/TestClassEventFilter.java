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
import net.sf.jguiraffe.gui.builder.event.FormFocusEvent;
import net.sf.jguiraffe.gui.builder.window.WindowEvent;
import net.sf.jguiraffe.gui.builder.window.WindowImpl;

import org.junit.Test;

/**
 * Test class for ClassEventFilter.
 *
 * @author Oliver Heger
 * @version $Id: TestClassEventFilter.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestClassEventFilter extends AbstractEventFilterTest
{
    /**
     * Creates the filter to test.
     *
     * @return the filter
     */
    @Override
    protected AbstractEventFilter createFilter()
    {
        return new ClassEventFilter();
    }

    /**
     * Tests if a wrong class is not accepted.
     */
    @Test
    public void testAcceptWrongClass()
    {
        filter = new ClassEventFilter(WindowEvent.class);
        assertFalse("Wrong class accepted", filter.accept(new FormFocusEvent(
                this, null, "test", FormFocusEvent.Type.FOCUS_GAINED)));
    }

    /**
     * Tests if a correct class is accepted.
     */
    @Test
    public void testAcceptCorrectClass()
    {
        filter.setBaseClass(WindowEvent.class);
        assertTrue("Event not accepted", filter.accept(new WindowEvent(this,
                new WindowImpl(), WindowEvent.Type.WINDOW_DEACTIVATED)));
    }

    /**
     * Tests if a full initialization sets the correct properties.
     */
    @Test
    public void testFullInitialization()
    {
        filter = new ClassEventFilter(WindowEvent.class, true);
        assertEquals("Wrong base class", WindowEvent.class, filter
                .getBaseClass());
        assertTrue("Wrong acceptNull flag", filter.isAcceptNull());
    }
}
