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
package net.sf.jguiraffe.gui.builder.window.ctrl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.sf.jguiraffe.gui.builder.event.filter.EventFilter;
import net.sf.jguiraffe.gui.builder.event.filter.TypeEventFilter;

import org.junit.Test;

/**
 * Test class for {@code FormControllerFormEvent}.
 *
 * @author Oliver Heger
 * @version $Id: TestFormControllerFormEvent.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestFormControllerFormEvent
{
    /**
     * Tests whether a type filter can be applied to events of this type.
     */
    @Test
    public void testTypeFilter()
    {
        EventFilter filter = new TypeEventFilter("FORM_COMMITTED");
        assertTrue("Not accepted", filter.accept(new FormControllerFormEvent(
                new FormController(),
                FormControllerFormEvent.Type.FORM_COMMITTED)));
        assertFalse("Accepted", filter.accept(new FormControllerFormEvent(
                new FormController(),
                FormControllerFormEvent.Type.FORM_CANCELED)));
    }
}
