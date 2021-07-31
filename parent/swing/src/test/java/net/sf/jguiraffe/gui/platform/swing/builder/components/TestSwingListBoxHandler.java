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

import javax.swing.JList;
import javax.swing.JScrollPane;

import net.sf.jguiraffe.gui.builder.components.model.ListModel;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for SwingListBoxHandler.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingListBoxHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingListBoxHandler extends
        AbstractListBoxComponentHandlerTest
{
    /** The handler to be tested. */
    private SwingListBoxHandler handler;

    @Override
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        handler = new SwingListBoxHandler(component, setUpListModel(), 0, 0);
    }

    @Override
    protected SwingListModelHandler getListModelHandler()
    {
        return handler;
    }

    /**
     * Tests fetching the handler's data when no list element is selected.
     */
    @Test
    public void testGetDataUnselected()
    {
        assertNull("Data returned for no selection", handler.getData());
    }

    /**
     * Tests fetching the handler's data when an element is selected.
     */
    @Test
    public void testGetDataSelected()
    {
        component.setSelectedIndex(3);
        assertEquals("Wrong data", ListModelImpl.VALUE_PREFIX + "3", handler
                .getData());
    }

    /**
     * Tests setting valid data for the handler.
     */
    @Test
    public void testSetDataValid()
    {
        handler.setData(ListModelImpl.VALUE_PREFIX + "4");
        assertEquals("Wrong selected index", 4, component.getSelectedIndex());
    }

    /**
     * Tests setting data to null. This should clear the selection.
     */
    @Test
    public void testSetDataNull()
    {
        component.setSelectedIndex(1);
        handler.setData(null);
        assertEquals("Selection not cleared for null data", -1, component
                .getSelectedIndex());
    }

    /**
     * Tests setting an invalid data object. This should clear the selection.
     */
    @Test
    public void testSetDataInvalid()
    {
        component.setSelectedIndex(1);
        handler.setData("No list element");
        assertEquals("Selection not cleared for invalid data", -1, component
                .getSelectedIndex());
    }

    /**
     * Tests whether a scroll pane can be passed to the constructor.
     */
    @Test
    public void testInitWithScrollPane()
    {
        JList list = new JList();
        ListModel listModel = setUpListModel();
        JScrollPane scr = new JScrollPane();
        SwingListBoxHandler listHandler =
                new SwingListBoxHandler(list, listModel, scr);

        assertEquals("Wrong list", list, listHandler.getComponent());
        assertEquals("Wrong list model", MODEL_SIZE, listModel.size());
        assertEquals("Wrong scroll pane", scr, listHandler.getOuterComponent());
    }
}
