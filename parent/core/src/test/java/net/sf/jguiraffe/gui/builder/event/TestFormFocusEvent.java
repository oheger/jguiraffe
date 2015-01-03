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
package net.sf.jguiraffe.gui.builder.event;

import net.sf.jguiraffe.JGuiraffeTestHelper;
import net.sf.jguiraffe.gui.forms.ComponentHandlerImpl;

import org.junit.Test;

/**
 * Test class for {@code FormFocusEvent}.
 *
 * @author Oliver Heger
 * @version $Id: $
 */
public class TestFormFocusEvent
{
    /** Constant for a test component handler. */
    private static final ComponentHandlerImpl HANDLER =
            new ComponentHandlerImpl();

    /** Constant for a component name. */
    private static final String NAME = "testFocusComponent";

    /**
     * Tests equals() if the expected result is true.
     */
    @Test
    public void testEqualsTrue()
    {
        FormFocusEvent event =
                new FormFocusEvent(this, HANDLER, NAME,
                        FormFocusEvent.Type.FOCUS_GAINED);
        JGuiraffeTestHelper.checkEquals(event, event, true);
        FormFocusEvent e2 =
                new FormFocusEvent(this, HANDLER, NAME,
                        FormFocusEvent.Type.FOCUS_GAINED);
        JGuiraffeTestHelper.checkEquals(event, e2, true);
    }

    /**
     * Tests equals() if the expected result is false.
     */
    @Test
    public void testEqualsFalse()
    {
        FormFocusEvent event =
                new FormFocusEvent(this, HANDLER, NAME,
                        FormFocusEvent.Type.FOCUS_GAINED);
        FormFocusEvent e2 =
                new FormFocusEvent(this, HANDLER, NAME + "_other",
                        FormFocusEvent.Type.FOCUS_GAINED);
        JGuiraffeTestHelper.checkEquals(event, e2, false);
        e2 = new FormFocusEvent(this, HANDLER, NAME, null);
        JGuiraffeTestHelper.checkEquals(event, e2, false);
        e2 =
                new FormFocusEvent(this, HANDLER, NAME,
                        FormFocusEvent.Type.FOCUS_LOST);
        JGuiraffeTestHelper.checkEquals(event, e2, false);
        JGuiraffeTestHelper.checkEquals(event, new FormChangeEvent(this,
                HANDLER, NAME), false);
    }
}
