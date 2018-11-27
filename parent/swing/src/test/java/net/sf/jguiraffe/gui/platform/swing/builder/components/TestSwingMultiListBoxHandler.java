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
package net.sf.jguiraffe.gui.platform.swing.builder.components;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import net.sf.jguiraffe.gui.builder.components.model.ListModel;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code SwingMultiListBoxHandler}.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingMultiListBoxHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingMultiListBoxHandler extends
        AbstractListBoxComponentHandlerTest
{
    /** The handler to be tested. */
    private SwingMultiListBoxHandler handler;

    @Override
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        component
                .setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        handler = new SwingMultiListBoxHandler(component, setUpListModel(), 0, 0);
    }

    @Override
    protected SwingListModelHandler getListModelHandler()
    {
        return handler;
    }

    /**
     * Tests the handler's type.
     */
    @Override
    @Test
    public void testGetType()
    {
        assertEquals("Wrong type", Object[].class, handler.getType());
    }

    /**
     * Tests obtaining the handler's data when nothing is selected.
     */
    @Test
    public void testGetDataNoSelection()
    {
        compareArray(new Object[0], handler.getData());
    }

    /**
     * Tests obtaining the handler's data if there is a selection.
     */
    @Test
    public void testGetDataSelection()
    {
        component.addSelectionInterval(0, 0);
        compareArray(new Object[]
        { "Value0" }, handler.getData());
        component.addSelectionInterval(4, 6);
        compareArray(new Object[]
        { "Value0", "Value4", "Value5", "Value6" }, handler.getData());
        component.addSelectionInterval(2, 2);
        compareArray(new Object[]
        { "Value0", "Value2", "Value4", "Value5", "Value6" }, handler.getData());
    }

    /**
     * Tests setting the handler's data.
     */
    @Test
    public void testSetData()
    {
        handler.setData(new Object[]
        { "Value5", "Value2", "Value9" });
        int[] indices = component.getSelectedIndices();
        assertEquals("Wrong selection length", 3, indices.length);
        assertEquals("Wrong sel index 1", 2, indices[0]);
        assertEquals("Wrong sel index 2", 5, indices[1]);
        assertEquals("Wrong sel index 3", 9, indices[2]);
    }

    /**
     * Tests setting the data to null. This should clear the selection.
     */
    @Test
    public void testSetDataNull()
    {
        component.addSelectionInterval(0, 5);
        handler.setData(null);
        assertTrue("Selection not cleared", component.isSelectionEmpty());
    }

    /**
     * Tests setting the data to an empty array. This should clear the
     * selection.
     */
    @Test
    public void testSetDataEmpty()
    {
        component.addSelectionInterval(0, 5);
        handler.setData(new Object[0]);
        assertTrue("Selection not cleared", component.isSelectionEmpty());
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
                new SwingMultiListBoxHandler(list, listModel, scr);

        assertEquals("Wrong list", list, listHandler.getComponent());
        assertEquals("Wrong list model", MODEL_SIZE, listModel.size());
        assertEquals("Wrong scroll pane", scr, listHandler.getOuterComponent());
    }

    /**
     * Helper method for comparing an array.
     *
     * @param expected the expected list values
     * @param value the value returned by getData()
     */
    private static void compareArray(Object[] expected, Object value)
    {
        assertTrue("Wrong value object", value instanceof Object[]);
        assertArrayEquals("Arrays not equal", expected, (Object[]) value);
    }
}
