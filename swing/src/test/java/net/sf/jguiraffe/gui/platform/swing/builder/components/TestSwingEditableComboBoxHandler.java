/*
 * Copyright 2006-2012 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.platform.swing.builder.components;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class or SwingEditableComboBoxHandler.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingEditableComboBoxHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingEditableComboBoxHandler extends
        AbstractComboBoxComponentHandlerTest
{
    /** Stores the handler to be tested. */
    private SwingEditableComboBoxHandler handler;

    @Override
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        component.setEditable(true);
        handler = new SwingEditableComboBoxHandler(component, setUpListModel());
    }

    @Override
    protected SwingListModelHandler getListModelHandler()
    {
        return handler;
    }

    /**
     * Tests obtaining the handler's data when it was directly entered.
     */
    @Test
    public void testGetData()
    {
        component.setSelectedItem(ListModelImpl.DISPLAY_PREFIX);
        assertEquals("Wrong entered data", ListModelImpl.DISPLAY_PREFIX,
                handler.getData());
    }

    /**
     * Tests setting data for the handler.
     */
    @Test
    public void testSetData()
    {
        final String newData = "New data for the box";
        handler.setData(newData);
        assertEquals("Data was not set", newData, component.getSelectedItem());
    }
}
