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
package net.sf.jguiraffe.gui.platform.swing.builder.components.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.LineBorder;

import net.sf.jguiraffe.gui.builder.components.tags.table.TableTag;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for SwingSelectionHandler.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingSelectionHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingSelectionHandler extends AbstractTableModelTest
{
    /** Stores the handler to be tested. */
    private SwingTableSelectionHandler handler;

    /** Stores the table tag. */
    private TableTag tableTag;

    /** Stores the component. */
    private JComponent component;

    @Before
    public void setUp() throws Exception
    {
        tableTag = setUpTableTag(null);
        handler = new SwingTableSelectionHandler();
        component = createCellComponent();
    }

    /**
     * Creates the table needed for testing.
     *
     * @return the test table
     */
    private JTable createTable()
    {
        return new JTable();
    }

    /**
     * Creates the component that will be passed to the handler.
     *
     * @return the cell renderer or editor component
     */
    private JComponent createCellComponent()
    {
        return new JLabel();
    }

    /**
     * Test for the default status: not selected and not focused.
     */
    @Test
    public void testPrepareComponentDefault()
    {
        JTable table = createTable();
        handler
                .prepareComponent(table, tableTag, component, false, false, 0,
                        0);
        assertNull("Component has a border", component.getBorder());
        assertEquals("Wrong background color", table.getBackground(), component
                .getBackground());
        assertEquals("Wrong foreground color", table.getForeground(), component
                .getForeground());
    }

    /**
     * Tests the selected status.
     */
    @Test
    public void testPrepareComponentSelected()
    {
        JTable table = createTable();
        handler.prepareComponent(table, tableTag, component, true, false, 0, 0);
        assertNull("Component has a border", component.getBorder());
        assertEquals("Wrong background color", table.getSelectionBackground(),
                component.getBackground());
        assertEquals("Wrong foreground color", table.getSelectionForeground(),
                component.getForeground());
    }

    /**
     * Tests the focused status.
     */
    @Test
    public void testPrepareComponentFocused()
    {
        JTable table = createTable();
        handler.prepareComponent(table, tableTag, component, false, true, 0, 0);
        assertTrue("Component has no line border",
                component.getBorder() instanceof LineBorder);
        assertEquals("Wrong background color", table.getBackground(), component
                .getBackground());
        assertEquals("Wrong foreground color", table.getForeground(), component
                .getForeground());
    }

    /**
     * Tests the selected and focused status.
     */
    @Test
    public void testPrepareComponentSelectedFocused()
    {
        JTable table = createTable();
        handler.prepareComponent(table, tableTag, component, true, true, 0, 0);
        assertTrue("Component has no line border",
                component.getBorder() instanceof LineBorder);
        assertEquals("Wrong background color", table.getSelectionBackground(),
                component.getBackground());
        assertEquals("Wrong foreground color", table.getSelectionForeground(),
                component.getForeground());
    }
}
