/*
 * Copyright 2006-2016 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.action;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code ActionDataImpl}. This class only tests non-trivial
 * logic of the class. Simple get and set methods are tested implicitly by other
 * test classes.
 *
 * @author Oliver Heger
 * @version $Id: TestActionDataImpl.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestActionDataImpl
{
    /** The object to be tested. */
    private ActionDataImpl data;

    @Before
    public void setUp() throws Exception
    {
        data = new ActionDataImpl();
    }

    /**
     * Creates a data object with default properties.
     *
     * @return the newly created object
     */
    private ActionDataImpl createData()
    {
        ActionDataImpl c = new ActionDataImpl();
        c.setName("actionName");
        c.setAccelerator(Accelerator.parse("A"));
        c.setIcon("Icon");
        c.setMnemonicKey('m');
        c.setTask("task");
        c.setText("text");
        c.setToolTip("toolTip");
        return c;
    }

    /**
     * Tests whether an object can be initialized from an action data object.
     */
    @Test
    public void testSetActionData()
    {
        ActionDataImpl c = createData();
        data.setActionData(c);
        assertEquals("Wrong action name", c.getName(), data.getName());
        assertEquals("Wrong accelerator", c.getAccelerator(), data
                .getAccelerator());
        assertEquals("Wrong icon", c.getIcon(), data.getIcon());
        assertEquals("Wrong mnemonic", c.getMnemonicKey(), data
                .getMnemonicKey());
        assertEquals("Wrong task", c.getTask(), data.getTask());
        assertEquals("Wrong text", c.getText(), data.getText());
        assertEquals("Wrong tool tip", c.getToolTip(), data.getToolTip());
    }

    /**
     * Tries to initialize an object from a null object.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetActionDataNull()
    {
        data.setActionData(null);
    }
}
