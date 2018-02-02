/*
 * Copyright 2006-2018 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.platform.swing.builder.action;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JToggleButton;

import junit.framework.TestCase;

/**
 * Test class for SwingCheckedItemController.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingCheckedItemController.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingCheckedItemController extends TestCase
{
    SwingCheckedItemController controller;

    SwingFormAction action;

    JCheckBoxMenuItem item;

    protected void setUp() throws Exception
    {
        super.setUp();
        action = new SwingFormAction("MyAction", new Runnable()
        {
            public void run()
            {
                // just a dummy implementation
            }
        });
        item = new JCheckBoxMenuItem(action);
        controller = new SwingCheckedItemController(action, item);
    }

    /**
     * Tests setting the checked property.
     */
    public void testSetChecked()
    {
        assertFalse(action.isChecked());
        assertFalse(item.isSelected());
        action.setChecked(true);
        assertTrue(item.isSelected());
        item.setSelected(false);
        assertFalse(action.isChecked());
    }

    /**
     * Tests that setting other properties has no effect on the checked state.
     */
    public void testOtherProperties()
    {
        action.putValue(Action.SHORT_DESCRIPTION, "A text");
        assertFalse(item.isSelected());
    }

    /**
     * Tests multiple controls associated with an action.
     */
    public void testMultiControls()
    {
        JToggleButton button = new JToggleButton(action);
        new SwingCheckedItemController(action, button);
        action.setChecked(true);
        assertTrue(button.isSelected());
        assertTrue(item.isSelected());
        button.setSelected(false);
        assertFalse(item.isSelected());
        assertFalse(action.isChecked());
        item.setSelected(true);
        assertTrue(button.isSelected());
        assertTrue(action.isChecked());
        item.setSelected(true);
        assertTrue(action.isChecked());
        action.setChecked(true);
        assertTrue(item.isSelected());
        assertTrue(button.isSelected());
    }
}
