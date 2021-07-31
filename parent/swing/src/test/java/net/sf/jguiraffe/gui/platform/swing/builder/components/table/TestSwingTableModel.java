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
package net.sf.jguiraffe.gui.platform.swing.builder.components.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import net.sf.jguiraffe.gui.builder.components.tags.table.ColumnClass;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableFormController;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableTag;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for SwingTableModel.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingTableModel.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingTableModel
{
    /** Constant for a test column index. */
    private static final int COL_IDX = 3;

    /** A mock for the table form controller. */
    private TableFormController controller;

    /** The table associated with the tag. */
    private JTable table;

    /** Stores the model to be tested. */
    private SwingTableModel model;

    @Before
    public void setUp() throws Exception
    {
        controller = EasyMock.createMock(TableFormController.class);
        TableTag tableTag = EasyMock.createMock(TableTag.class);
        EasyMock.expect(tableTag.getTableFormController())
                .andReturn(controller).anyTimes();
        EasyMock.replay(tableTag);
        table = new JTable();
        model = new SwingTableModel(tableTag, table);
    }

    /**
     * Convenience method for replaying the mock objects used by the tests.
     */
    private void replay()
    {
        EasyMock.replay(controller);
    }

    /**
     * Convenience method for verifying the mock objects used by the tests.
     */
    private void verify()
    {
        EasyMock.verify(controller);
    }

    /**
     * Tests whether the data model can be queried.
     */
    @Test
    public void testGetModelData()
    {
        List<Object> data = Collections.<Object>singletonList("TestData");
        EasyMock.expect(controller.getDataModel()).andReturn(data).anyTimes();
        replay();
        assertEquals("Wrong data", data, model.getModelData());
    }

    /**
     * Tests the number of columns in the model.
     */
    @Test
    public void testGetColumnCount()
    {
        EasyMock.expect(controller.getColumnCount()).andReturn(8);
        replay();
        assertEquals("Wrong number of columns", 8, model.getColumnCount());
    }

    /**
     * Tests querying the number of rows in the model.
     */
    @Test
    public void testGetRowCount()
    {
        EasyMock.expect(controller.getRowCount()).andReturn(24);
        replay();
        assertEquals("Wrong number of rows", 24, model.getRowCount());
    }

    /**
     * Tests whether the class for the columns can be queried if normal Java
     * classes are specified.
     */
    @Test
    public void testGetColumnClassJava()
    {
        controller.getDataClass(COL_IDX);
        EasyMock.expectLastCall().andReturn(String.class).anyTimes();
        EasyMock.expect(controller.getLogicDataClass(COL_IDX)).andReturn(null)
                .anyTimes();
        replay();
        assertEquals("Wrong column class", String.class,
                model.getColumnClass(COL_IDX));
    }

    /**
     * Helper method for checking whether logic column classes are correctly
     * mapped to Java classes.
     *
     * @param logicClass the logic class
     * @param expected the expected Java class
     */
    private void checkGetColumnClassLogic(final ColumnClass logicClass,
            Class<?> expected)
    {
        EasyMock.expect(controller.getLogicDataClass(COL_IDX))
                .andReturn(logicClass).anyTimes();
        replay();
        assertEquals("Wrong column class", expected,
                model.getColumnClass(COL_IDX));
    }

    /**
     * Tests whether the logic column class STRING is correctly mapped.
     */
    @Test
    public void testGetColumnClassLogicString()
    {
        checkGetColumnClassLogic(ColumnClass.STRING, String.class);
    }

    /**
     * Tests whether the logic column class BOOLEAN is correctly mapped.
     */
    @Test
    public void testGetColumnClassLogicBoolean()
    {
        checkGetColumnClassLogic(ColumnClass.BOOLEAN, Boolean.class);
    }

    /**
     * Tests whether the logic column class DATE is correctly mapped.
     */
    @Test
    public void testGetColumnClassLogicDate()
    {
        checkGetColumnClassLogic(ColumnClass.DATE, Date.class);
    }

    /**
     * Tests whether the logic column class FLOAT is correctly mapped.
     */
    @Test
    public void testGetColumnClassLogicFloat()
    {
        checkGetColumnClassLogic(ColumnClass.FLOAT, Double.class);
    }

    /**
     * Tests whether the logic column class NUMBER is correctly mapped.
     */
    @Test
    public void testGetColumnClassLogicNumber()
    {
        checkGetColumnClassLogic(ColumnClass.NUMBER, Number.class);
    }

    /**
     * Tests whether the logic column class ICON is correctly mapped.
     */
    @Test
    public void testGetColumnClassLogicIcon()
    {
        checkGetColumnClassLogic(ColumnClass.ICON, Icon.class);
    }

    /**
     * Tests querying the name for the columns.
     */
    @Test
    public void testGetColumnName()
    {
        final String colName = "TestColumnName";
        EasyMock.expect(controller.getColumnName(COL_IDX)).andReturn(colName)
                .anyTimes();
        replay();
        assertEquals("Wrong column name", colName, model.getColumnName(COL_IDX));
    }

    /**
     * Tests the editable flags for the columns.
     */
    @Test
    public void testIsCellEditable()
    {
        EasyMock.expect(controller.isColumnEditable(COL_IDX)).andReturn(
                Boolean.FALSE);
        replay();
        assertFalse("Wrong editable flag", model.isCellEditable(0, COL_IDX));
        verify();
    }

    /**
     * Tests accessing data from the model.
     */
    @Test
    public void testGetValueAt()
    {
        final Object value = "CellValue";
        final int row = 8;
        controller.selectCurrentRow(row);
        EasyMock.expect(controller.getColumnValue(COL_IDX)).andReturn(value);
        replay();
        assertEquals("Wrong value", value, model.getValueAt(row, COL_IDX));
        verify();
    }

    /**
     * Tests the hasEditor() method.
     */
    @Test
    public void testHasEditor()
    {
        EasyMock.expect(controller.hasEditor(COL_IDX)).andReturn(Boolean.TRUE);
        replay();
        assertTrue("Wrong result", model.hasEditor(COL_IDX));
    }

    /**
     * Tests the hasRenderer() method.
     */
    @Test
    public void testHasRenderer()
    {
        EasyMock.expect(controller.hasRenderer(COL_IDX)).andReturn(Boolean.FALSE);
        replay();
        assertFalse("Wrong result", model.hasRenderer(COL_IDX));
        verify();
    }

    /**
     * Tests accessing the custom editor implementation for the table model.
     */
    @Test
    public void testGetEditor()
    {
        replay();
        TableCellEditor editor = model.getEditor();
        assertTrue("Wrong editor returned",
                editor instanceof SwingTableCellEditor);
        SwingTableCellEditor swingEd = (SwingTableCellEditor) editor;
        assertSame("Model not initialized", model, swingEd.getModel());
    }

    /**
     * Tests whether always the same editor is returned.
     */
    @Test
    public void testGetEditorCached()
    {
        replay();
        TableCellEditor editor = model.getEditor();
        assertSame("Multiple editor instances created", editor, model
                .getEditor());
    }

    /**
     * Tests accessing the custom renderer implementation for the table model.
     */
    @Test
    public void testGetRenderer()
    {
        replay();
        TableCellRenderer renderer = model.getRenderer();
        assertTrue("Wrong renderer returned",
                renderer instanceof SwingTableCellRenderer);
        SwingTableCellRenderer swingRend = (SwingTableCellRenderer) renderer;
        assertSame("Model not initialized", model, swingRend.getModel());
    }

    /**
     * Tests whether always the same renderer instance is returned.
     */
    @Test
    public void testGetRendererCached()
    {
        replay();
        TableCellRenderer renderer = model.getRenderer();
        assertSame("Multiple renderer instances created", renderer, model
                .getRenderer());
    }

    /**
     * Tests whether a cell value can be set.
     */
    @Test
    public void testSetValueAt()
    {
        final Object newValue = "Harry";
        final int row = 16;
        controller.selectCurrentRow(row);
        controller.setColumnValue(table, COL_IDX, newValue);
        replay();
        model.setValueAt(newValue, row, COL_IDX);
        verify();
    }

    /**
     * Tests whether model changed events are correctly propagated.
     */
    @Test
    public void testFireTableChanged()
    {
        TableModelListener listener =
                EasyMock.createMock(TableModelListener.class);
        TableModelEvent event = new TableModelEvent(model, 4, 8);
        listener.tableChanged(event);
        controller.invalidateRange(event.getFirstRow(), event.getLastRow());
        EasyMock.replay(listener);
        replay();

        model.addTableModelListener(listener);
        model.fireTableChanged(event);
        EasyMock.verify(listener);
        verify();
    }
}
