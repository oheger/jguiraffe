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
package net.sf.jguiraffe.gui.builder.event;

import net.sf.jguiraffe.JGuiraffeTestHelper;
import net.sf.jguiraffe.gui.forms.ComponentHandlerImpl;

import org.junit.Test;

/**
 * Test class for {@code FormActionEvent}.
 *
 * @author Oliver Heger
 * @version $Id: $
 */
public class TestFormActionEvent
{
    /** Constant for a test component handler. */
    private static final ComponentHandlerImpl HANDLER =
            new ComponentHandlerImpl();

    /** Constant for a component name. */
    private static final String NAME = "testActionComponent";

    /** Constant for an action command. */
    private static final String CMD = "testCommand";

    /**
     * Tests equals() if the expected result is true.
     */
    @Test
    public void testEqualsTrue()
    {
        FormActionEvent event = new FormActionEvent(this, HANDLER, NAME, CMD);
        JGuiraffeTestHelper.checkEquals(event, event, true);
        FormActionEvent e2 = new FormActionEvent(this, HANDLER, NAME, CMD);
        JGuiraffeTestHelper.checkEquals(event, e2, true);
    }

    /**
     * Tests equals() if the expected result is false.
     */
    @Test
    public void testEqualsFalse()
    {
        FormActionEvent event = new FormActionEvent(this, HANDLER, NAME, CMD);
        FormActionEvent e2 =
                new FormActionEvent(this, HANDLER, NAME + "_other", CMD);
        JGuiraffeTestHelper.checkEquals(event, e2, false);
        e2 = new FormActionEvent(this, HANDLER, NAME, null);
        JGuiraffeTestHelper.checkEquals(event, e2, false);
        e2 = new FormActionEvent(this, HANDLER, NAME, CMD + "_other");
        JGuiraffeTestHelper.checkEquals(event, e2, false);
    }
}
