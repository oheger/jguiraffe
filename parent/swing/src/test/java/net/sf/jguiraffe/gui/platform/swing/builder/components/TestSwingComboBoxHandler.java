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
package net.sf.jguiraffe.gui.platform.swing.builder.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for SwingComboBoxHandler.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingComboBoxHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingComboBoxHandler extends
        AbstractComboBoxComponentHandlerTest
{
    /** Stores the handler to be tested. */
    private SwingComboBoxHandler handler;

    @Override
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        handler = new SwingComboBoxHandler(component, setUpListModel());
    }

    @Override
    protected SwingListModelHandler getListModelHandler()
    {
        return handler;
    }

    /**
     * Tests reading data from the component when there is no selection.
     */
    @Test
    public void testGetDataUnselected()
    {
        component.setSelectedIndex(-1);
        assertNull("Data is returned for no selection", handler.getData());
    }

    /**
     * Tests reading data from the component when there is a valid selection.
     */
    @Test
    public void testGetDataSelected()
    {
        for (int i = 0; i < MODEL_SIZE; i++)
        {
            component.setSelectedIndex(i);
            assertEquals("Wrong data at " + i, ListModelImpl.VALUE_PREFIX + i,
                    handler.getData());
        }
    }

    /**
     * Tests setting the handler's data to a valid element.
     */
    @Test
    public void testSetDataSelected()
    {
        for (int i = 0; i < MODEL_SIZE; i++)
        {
            handler.setData(ListModelImpl.VALUE_PREFIX + i);
            assertEquals("Wrong selected index", i, component
                    .getSelectedIndex());
        }
    }

    /**
     * Tests setting data to null. This should clear the selection.
     */
    @Test
    public void testSetDataNull()
    {
        handler.setData(null);
        assertEquals("Selection not cleared", -1, component.getSelectedIndex());
    }

    /**
     * Tests setting data to an element that does not exist in the list. This
     * should clear the selection.
     */
    @Test
    public void testSetDataInvalid()
    {
        handler.setData("Non existing element");
        assertEquals("Selection not cleared for invalid element", -1, component
                .getSelectedIndex());
    }
}
