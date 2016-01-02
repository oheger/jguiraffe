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
package net.sf.jguiraffe.gui.platform.swing.builder.components.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import javax.swing.JTable;
import javax.swing.JTextField;

import net.sf.jguiraffe.gui.builder.components.tags.table.TableSelectionHandler;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableTag;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for SwingTableCellEditor.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingTableCellEditor.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingTableCellEditor extends AbstractTableModelTest
{
    /** Constant for the row index used for testing. */
    private static final int TEST_ROW = 2;

    /** Constant for the column index used for testing. */
    private static final int TEST_COL = 1;

    /** Stores the table model. */
    private SwingTableModelTestImpl model;

    /** Stores the editor to be tested. */
    private SwingTableCellEditor editor;

    @Before
    public void setUp() throws Exception
    {
        tableTag = setUpTableTag(null);
        model = new SwingTableModelTestImpl(tableTag, new JTable());
        editor = new SwingTableCellEditor(model);
    }

    /**
     * Tests the value returned by the editor. Because the values are transfered
     * to the model using the table's editor form after validation succeeds,
     * this method only returns null as a dummy value.
     */
    @Test
    public void testGetCellEditorValue()
    {
        assertNull("Wrong value of editor", editor.getCellEditorValue());
    }

    /**
     * Tests accessing the cell editor component if the selection flag is false.
     */
    @Test
    public void testGetTableCellEditorComponentNotSelected()
    {
        checkGetTableCellEditorComponent(false);
    }

    /**
     * Tests accessing the cell editor component if the selection flag is true.
     */
    @Test
    public void testGetTableCellEditorComponentSelected()
    {
        checkGetTableCellEditorComponent(true);
    }

    /**
     * Tests querying the cell editor component.
     *
     * @param selected the selected flag
     */
    private void checkGetTableCellEditorComponent(boolean selected)
    {
        JTextField editorComponent = new JTextField();
        TableColumnTagTestImpl colTag = (TableColumnTagTestImpl) tableTag
                .getColumn(1);
        colTag.installEditor(editorComponent);
        TableSelectionHandler selHandler = EasyMock
                .createMock(TableSelectionHandler.class);
        selHandler.prepareComponent(model.getTable(), tableTag,
                editorComponent, selected, false, TEST_ROW, TEST_COL);
        EasyMock.replay(selHandler);
        tableTag.setEditorSelectionHandler(selHandler);
        assertSame("Wrong editor component returned", editorComponent, editor
                .getTableCellEditorComponent(model.getTable(), null, selected,
                        TEST_ROW, TEST_COL));
        EasyMock.verify(selHandler);
    }

    /**
     * Tests the stopCellEditing() method if the input is valid.
     */
    @Test
    public void testStopCellEditingValid()
    {
        checkStopCellEditingValue(true);
    }

    /**
     * Tests the stopCellEditing() method if the input is invalid.
     */
    @Test
    public void testStopCellEditingInvalid()
    {
        checkStopCellEditingValue(false);
    }

    /**
     * Checks validation in the stopCellEditing() method.
     *
     * @param valid flag whether the input is valid
     */
    private void checkStopCellEditingValue(boolean valid)
    {
        model.validateResult = valid;
        checkGetTableCellEditorComponent(false);
        assertEquals("Wrong result of stopCellEditing()", valid, editor
                .stopCellEditing());
        assertEquals("Wrong row index passed to setValueAt", TEST_ROW,
                model.setValueRow);
        assertEquals("Wrong col index passed to setValueAt", TEST_COL,
                model.setValueCol);
        assertEquals("Wrong col index passed to validateColumn", TEST_COL,
                model.validateCol);
    }

    /**
     * A specialized table model implementation used for testing whether certain
     * model methods are correctly called.
     */
    @SuppressWarnings("serial")
    static class SwingTableModelTestImpl extends SwingTableModel
    {
        /** Stores the row index passed to setValueAt. */
        int setValueRow;

        /** Stores the column index passed to setValueAt. */
        int setValueCol;

        /** Stores the column index passed to validateColumn. */
        int validateCol;

        /** The value to be returned by validateColumn. */
        boolean validateResult;

        public SwingTableModelTestImpl(TableTag tt, JTable tab)
        {
            super(tt, tab);
        }

        /**
         * Records this call to setValueAt().
         */
        @Override
        public void setValueAt(Object value, int row, int col)
        {
            setValueRow = row;
            setValueCol = col;
        }

        /**
         * Records this call to validateColumn() and returns the specified
         * validation result.
         */
        @Override
        protected boolean validateColumn(int col)
        {
            validateCol = col;
            return validateResult;
        }
    }
}
