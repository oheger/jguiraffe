/*
 * Copyright 2006-2022 The JGUIraffe Team.
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableModel;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;

import net.sf.jguiraffe.gui.builder.components.ColorHelper;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableFormController;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableTag;
import net.sf.jguiraffe.gui.platform.swing.builder.components.table.SwingTableModel;
import net.sf.jguiraffe.gui.platform.swing.builder.event.ChangeListener;
import org.apache.commons.lang.mutable.MutableObject;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for SwingTableComponentHandler.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingTableComponentHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingTableComponentHandler
{
    /** Constant for the name of the data changed method. */
    private static final String METH_DATACHANGED = "dataChanged";

    /** Constant for the name of the rows inserted method. */
    private static final String METH_INSERTED = "rowsInserted";

    /** Constant for the name of the rows updated method. */
    private static final String METH_UPDATED = "rowsUpdated";

    /** Constant for the name of the rows deleted method. */
    private static final String METH_DELETED = "rowsDeleted";

    /** An array with model test data. */
    private static final Object[] MODEL_DATA = {
            "test", "another Test", 42, "more test data"
    };

    /** The list serving as table model. */
    private static final List<Object> MODEL_LIST = Arrays.asList(MODEL_DATA);

    /** Constant for the number of test rows. */
    private static final int ROW_COUNT = 10;

    /** Constant for the number of test columns. */
    private static final int COL_COUNT = 4;

    /** Constant for a start index of a modified row interval. */
    private static final int START_IDX = 2;

    /** Constant for an end index of a modified row interval. */
    private static final int END_IDX = 5;

    /** Stores the underlying table component. */
    private JTable table;

    /** Stores the handler under test. */
    private SwingTableComponentHandlerTestImpl handler;

    @Before
    public void setUp() throws Exception
    {
        table = new JTable();
        final TableFormController ctrl =
                EasyMock.createNiceMock(TableFormController.class);
        EasyMock.expect(ctrl.getDataModel()).andReturn(MODEL_LIST).anyTimes();
        EasyMock.replay(ctrl);
        table.setModel(new TableModelImpl(new TableTag()
        {
            @Override
            public TableFormController getTableFormController()
            {
                return ctrl;
            }
        }, table));
        handler = new SwingTableComponentHandlerTestImpl(table, 0, 0);
    }

    /**
     * Helper method for comparing an array. Used for checking selected indices.
     *
     * @param a1 array 1
     * @param a2 array 2
     */
    private static void compareArray(int[] a1, int[] a2)
    {
        assertEquals("Length is different", a1.length, a2.length);
        for (int i = 0; i < a1.length; i++)
        {
            assertEquals("Different value at index " + i, a1[i], a2[i]);
        }
    }

    /**
     * Initializes the test table to use single selection.
     */
    private void initSingleSel()
    {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * Initializes the test table to use multi selection.
     */
    private void initMultiSel()
    {
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    /**
     * Returns the table model used by the handler.
     *
     * @return the table model
     */
    private TableModelImpl getModel()
    {
        return (TableModelImpl) table.getModel();
    }

    /**
     * Tests whether the correct table is returned.
     */
    @Test
    public void testGetTable()
    {
        assertSame("Wrong table returned", table, handler.getTable());
    }

    /**
     * Tests obtaining the table model.
     */
    @Test
    public void testGetTableModel()
    {
        TableModel model = handler.getTableModel();
        assertTrue("Wrong model class: " + model.getClass().getName(),
                model instanceof TableModelImpl);
        assertSame("Wrong model returned", model, handler.getTable().getModel());
    }

    /**
     * Tests whether a table with multi selection support is detected.
     */
    @Test
    public void testIsMultiSelectionTrue()
    {
        initMultiSel();
        assertTrue("No multi selection detected (1)", handler
                .isMultiSelection());
        table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        assertTrue("No multi selection detected (2)", handler
                .isMultiSelection());
    }

    /**
     * Tests whether a table with single selection support is detected.
     */
    @Test
    public void testIsMultiSelectionFalse()
    {
        initSingleSel();
        assertFalse("No single selection detected", handler.isMultiSelection());
    }

    /**
     * Tests obtaining the data type in single selection mode.
     */
    @Test
    public void testGetTypeSingleSel()
    {
        initSingleSel();
        assertEquals("Wrong data type for single selection", Integer.TYPE,
                handler.getType());
    }

    /**
     * Tests obtaining the data type in multi selection mode.
     */
    @Test
    public void testGetTypeMultiSel()
    {
        final Class<?> expectedType = int[].class;
        initMultiSel();
        assertEquals("Wrong data type for multi selection (1)", expectedType,
                handler.getType());
        table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        assertEquals("Wrong data type for multi selection (2)", expectedType,
                handler.getType());
    }

    /**
     * Tests accessing the outer component. This should be a scroll pane.
     */
    @Test
    public void testGetOuterComponent()
    {
        Object outer = handler.getOuterComponent();
        assertTrue("Outer component no scroll pane: " + outer,
                outer instanceof JScrollPane);
        JScrollPane scp = (JScrollPane) outer;
        assertSame("Table not in viewport", table, scp.getViewport().getView());
    }

    /**
     * Tests the constructor that expects a scroll pane.
     */
    @Test
    public void testScrollPaneCanBePassedToConstructor()
    {
        JScrollPane scrollPane = new JScrollPane();
        SwingTableComponentHandler tableHandler =
                new SwingTableComponentHandler(table, scrollPane);

        assertEquals("Wrong scroll pane", scrollPane,
                tableHandler.getOuterComponent());
        assertEquals("Wrong table", table, tableHandler.getComponent());
    }

    /**
     * Tests the setData() method in single selection mode.
     */
    @Test
    public void testSetDataSingleSel()
    {
        initSingleSel();
        handler.setData(2);
        assertEquals("Selected row not set", 2, table.getSelectedRow());
    }

    /**
     * Tests the getData() method in single selection mode. This should return
     * the index of the selected row.
     */
    @Test
    public void testGetDataSingleSel()
    {
        initSingleSel();
        assertEquals("Wrong index for unselected table", -1, ((Integer) handler
                .getData()).intValue());
        table.addRowSelectionInterval(1, 1);
        assertEquals("Wrong index for selected row", 1, ((Integer) handler
                .getData()).intValue());
    }

    /**
     * Tests the setData() method in multi selection mode. In this mode it
     * should be possible to set the indices of the selected rows at once.
     */
    @Test
    public void testSetDataMultiSel()
    {
        initMultiSel();
        int[] selRows =
        { 0, 2, 3, 5 };
        handler.setData(selRows);
        checkTableSelection(selRows);
    }

    /**
     * Tests whether the expected row indices are selected.
     *
     * @param expectedSel an array with the expected selected indices
     */
    private void checkTableSelection(int[] expectedSel)
    {
        assertEquals("Wrong number of rows selected", expectedSel.length, table
                .getSelectedRowCount());
        for (int rowIdx : expectedSel)
        {
            assertTrue("Row not selected: " + rowIdx, table
                    .isRowSelected(rowIdx));
        }
    }

    /**
     * Tests setting the data to null. This should clear the selection.
     */
    @Test
    public void testSetDataNull()
    {
        table.addRowSelectionInterval(1, 5);
        handler.setData(null);
        assertEquals("Selection was not cleared", 0, table
                .getSelectedRowCount());
    }

    /**
     * Tests setting the data to an invalid value. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetDataInvalid()
    {
        handler.setData("Invalid data");
    }

    /**
     * Tests calling the setData() method with a Number object. This should be
     * automatically converted into an int.
     */
    @Test
    public void testSetDataNumber()
    {
        initSingleSel();
        handler.setData(2L);
        assertEquals("Selected index was not set", 2, table.getSelectedRow());
    }

    /**
     * Tests the getData() in multi selection mode.
     */
    @Test
    public void testGetDataMultiSel()
    {
        initMultiSel();
        table.addRowSelectionInterval(0, 0);
        table.addRowSelectionInterval(2, 4);
        int[] expSelRows =
        { 0, 2, 3, 4 };
        int[] selRows = (int[]) handler.getData();
        compareArray(expSelRows, selRows);
    }

    /**
     * Tests setting the selected index.
     */
    @Test
    public void testSetSelectedIndex()
    {
        initSingleSel();
        final int index = 2;
        handler.setSelectedIndex(index);
        assertEquals("Selected index was not set", index, table.getSelectedRow());
        assertEquals("Not scrolled", index, handler.getScrollingIndex());
    }

    /**
     * Tests calling setSelectedIndex() multiple times. Ensures that old
     * selections are reset before a new row is selected.
     */
    @Test
    public void testSetSelectedIndexTwice()
    {
        testSetSelectedIndex();
        handler.setSelectedIndex(4);
        assertEquals("Wrong number of selected rows", 1, table
                .getSelectedRowCount());
        assertEquals("Wrong selected row index", 4, table.getSelectedRow());
    }

    /**
     * Tries to set an invalid table index.
     */
    @Test
    public void testSetSelectedIndexInvalid()
    {
        initSingleSel();
        handler.setSelectedIndex(-1);
        assertEquals("Wrong number of selected rows", 0, table.getSelectedRowCount());
    }

    /**
     * Tests whether the handler makes the newly selected row visible.
     */
    @Test
    public void testHandleScrolling()
    {
        JViewport vp = EasyMock.createMock(JViewport.class);
        final Rectangle rect = EasyMock.createMock(Rectangle.class);
        EasyMock.expect(vp.getViewPosition()).andReturn(new Point(10, 111));
        EasyMock.expect(vp.getViewPosition()).andReturn(new Point(0, 100));
        vp.setViewPosition(new Point(10, 100));
        EasyMock.replay(rect, vp);
        final int index = 5;
        final MutableObject scrolledRect = new MutableObject();
        JTable tab = new JTable(table.getModel())
        {
            private static final long serialVersionUID = 1L;

            @Override
            public Rectangle getCellRect(int row, int column,
                    boolean includeSpacing)
            {
                assertEquals("Wrong row", index, row);
                assertEquals("Wrong column", 0, column);
                assertTrue("Wrong spacing flag", includeSpacing);
                return rect;
            }

            @Override
            public void scrollRectToVisible(Rectangle aRect)
            {
                scrolledRect.setValue(aRect);
            }
        };
        handler = new SwingTableComponentHandlerTestImpl(tab, 0, 0);
        handler.setMockScrollingToSelectedRow(false);
        handler.handleScrolling(vp, index);
        assertSame("Wrong rectangle", rect, scrolledRect.getValue());
        EasyMock.verify(rect, vp);
    }

    /**
     * Tests whether the view port is not modified if the x position did not
     * change when a cell was made visible.
     */
    @Test
    public void testUpdateViewportNoXChange()
    {
        JViewport vp = EasyMock.createMock(JViewport.class);
        EasyMock.expect(vp.getViewPosition()).andReturn(new Point(10, 100));
        EasyMock.replay(vp);
        handler.updateViewport(vp, new Point(10, 50));
        EasyMock.verify(vp);
    }

    /**
     * Tests querying the selected index.
     */
    @Test
    public void testGetSelectedIndex()
    {
        initSingleSel();
        assertEquals("Wrong index for unselected table", -1, handler
                .getSelectedIndex());
        table.addRowSelectionInterval(2, 2);
        assertEquals("Wrong selected index", 2, handler.getSelectedIndex());
    }

    /**
     * Tests setting a number of selected indices.
     */
    @Test
    public void testSetSelectedIndices()
    {
        final int[] selRows =
        { 0, 2, 4, 5, 6 };
        initMultiSel();
        handler.setSelectedIndices(selRows);
        checkTableSelection(selRows);
    }

    /**
     * Tests calling setSelectedIndices() multiple times to ensure that the
     * selection is cleared before new rows are selected.
     */
    @Test
    public void testSetSelectedIndicesTwice()
    {
        testSetSelectedIndices();
        final int[] selRows =
        { 1, 3, 7 };
        handler.setSelectedIndices(selRows);
        checkTableSelection(selRows);
    }

    /**
     * Tests querying the selected indices.
     */
    @Test
    public void testGetSelectedIndices()
    {
        initMultiSel();
        table.addRowSelectionInterval(1, 3);
        table.addRowSelectionInterval(5, 6);
        final int[] selRows =
        { 1, 2, 3, 5, 6 };
        compareArray(selRows, handler.getSelectedIndices());
    }

    /**
     * Tests the clearSelection() method in single selection mode.
     */
    @Test
    public void testClearSelectionSingle()
    {
        initSingleSel();
        table.addRowSelectionInterval(2, 2);
        handler.clearSelection();
        assertEquals("Selection not cleared", 0, table.getSelectedRowCount());
        assertEquals("Wrong selected row index", -1, handler.getSelectedIndex());
    }

    /**
     * Tests the clearSelection() method in multi selection mode.
     */
    @Test
    public void testClearSelectionMulti()
    {
        initMultiSel();
        table.addRowSelectionInterval(2, 5);
        handler.clearSelection();
        assertEquals("Selection not cleared", 0, table.getSelectedRowCount());
        assertEquals("Wrong selected indices", 0,
                handler.getSelectedIndices().length);
    }

    /**
     * Tests notifications for unspecific data changes.
     */
    @Test
    public void testTableDataChanged()
    {
        handler.tableDataChanged();
        getModel().checkNotification(METH_DATACHANGED);
    }

    /**
     * Tests notifications for deleted rows.
     */
    @Test
    public void testRowsDeleted()
    {
        handler.rowsDeleted(START_IDX, END_IDX);
        getModel().checkNotification(METH_DELETED, START_IDX, END_IDX);
    }

    /**
     * Tests notifications for inserted rows.
     */
    @Test
    public void testRowsInserted()
    {
        handler.rowsInserted(START_IDX, END_IDX);
        getModel().checkNotification(METH_INSERTED, START_IDX, END_IDX);
    }

    /**
     * Tests notifications for updated rows.
     */
    @Test
    public void testRowsUpdated()
    {
        handler.rowsUpdated(START_IDX, END_IDX);
        getModel().checkNotification(METH_UPDATED, START_IDX, END_IDX);
    }

    /**
     * Tests handling of change events when the adjusting flag is set. In this
     * case the change event must be ignored.
     */
    @Test
    public void testFireChangeEventAdjusting()
    {
        ChangeListener mockListener = EasyMock.createMock(ChangeListener.class);
        EasyMock.replay(mockListener);
        handler.addChangeListener(mockListener);
        handler.valueChanged(createSelectionEvent(true));
        EasyMock.verify(mockListener);
    }

    /**
     * Tests whether a change event is correctly propagated to registered change
     * listeners.
     */
    @Test
    public void testFireChangeEvent()
    {
        ChangeListener mockListener1 = EasyMock
                .createMock(ChangeListener.class);
        ListSelectionEvent event = createSelectionEvent(false);
        mockListener1.componentChanged(event);
        EasyMock.replay(mockListener1);
        handler.addChangeListener(mockListener1);
        handler.valueChanged(event);
        EasyMock.verify(mockListener1);
    }

    /**
     * Tests removing a change listener.
     */
    @Test
    public void testRemoveChangeListener()
    {
        ChangeListener mockListener1 = EasyMock
                .createMock(ChangeListener.class);
        ListSelectionEvent event = createSelectionEvent(false);
        mockListener1.componentChanged(event);
        EasyMock.replay(mockListener1);
        handler.addChangeListener(mockListener1);
        handler.valueChanged(event);
        handler.removeChangeListener(mockListener1);
        handler.valueChanged(event);
        EasyMock.verify(mockListener1);
    }

    /**
     * Tests whether the table's model can be queried.
     */
    @Test
    public void testGetModel()
    {
        assertSame("Wrong model", MODEL_LIST, handler.getModel());
    }

    /**
     * Tests whether the selection foreground color can be queried.
     */
    @Test
    public void testGetSelectionForeground()
    {
        table.setSelectionForeground(Color.CYAN);
        assertEquals("Wrong color", ColorHelper.NamedColor.CYAN.getColor(),
                handler.getSelectionForeground());
    }

    /**
     * Tests whether the selection foreground color can be set.
     */
    @Test
    public void testSetSelectionForeground()
    {
        handler
                .setSelectionForeground(ColorHelper.NamedColor.ORANGE
                        .getColor());
        assertEquals("Wrong color", Color.ORANGE, table
                .getSelectionForeground());
    }

    /**
     * Tests whether the selection background color can be queried.
     */
    @Test
    public void testGetSelectionBackground()
    {
        table.setSelectionBackground(Color.GREEN);
        assertEquals("Wrong color", ColorHelper.NamedColor.GREEN.getColor(),
                handler.getSelectionBackground());
    }

    /**
     * Tests whether the selection background color can be set.
     */
    @Test
    public void testSetSelectionBackground()
    {
        handler.setSelectionBackground(ColorHelper.NamedColor.MAGENTA
                .getColor());
        assertEquals("Wrong color", Color.MAGENTA, table
                .getSelectionBackground());
    }

    /**
     * Creates a list selection event that can be used for testing.
     *
     * @param adjusting the adjusting flag
     * @return the event
     */
    private ListSelectionEvent createSelectionEvent(boolean adjusting)
    {
        return new ListSelectionEvent(this, START_IDX, END_IDX, adjusting);
    }

    /**
     * A test implementation of the table handler with some mocing facilities.
     */
    private static class SwingTableComponentHandlerTestImpl extends
            SwingTableComponentHandler
    {
        /**
         * A flag whether scrolling to the selected row should be mocked. This
         * is true per default!
         */
        private boolean mockScrollingToSelectedRow;

        /** Stores the index passed to handleScrolling(). */
        private int scrollingIndex;

        public SwingTableComponentHandlerTestImpl(JTable table,
                int scrollWidth, int scrollHeight)
        {
            super(table, scrollWidth, scrollHeight);
            mockScrollingToSelectedRow = true;
            scrollingIndex = -1;
        }

        /**
         * Returns a flag whether scrolling to the selected row should be
         * mocked.
         *
         * @return the mocking flag
         */
        public boolean isMockScrollingToSelectedRow()
        {
            return mockScrollingToSelectedRow;
        }

        /**
         * Sets a flag whether scrolling to the selected should be mocked.
         *
         * @param mockScrollingToSelectedRow the flag
         */
        public void setMockScrollingToSelectedRow(
                boolean mockScrollingToSelectedRow)
        {
            this.mockScrollingToSelectedRow = mockScrollingToSelectedRow;
        }

        /**
         * Returns the index passed to handleScrolling().
         *
         * @return the scrolling index
         */
        public int getScrollingIndex()
        {
            return scrollingIndex;
        }

        /**
         * Checks the parameters, records this invocation. Optionally this
         * method is mocked.
         */
        @Override
        void handleScrolling(JViewport vp, int rowIdx)
        {
            if (isMockScrollingToSelectedRow())
            {
                assertSame("Wrong viewport",
                        ((JScrollPane) getOuterComponent()).getViewport(), vp);
                scrollingIndex = rowIdx;
            }
            else
            {
                super.handleScrolling(vp, rowIdx);
            }
        }
    }

    /**
     * A table model implementation used for testing. The test table will have a
     * model of this type.
     */
    private static class TableModelImpl extends SwingTableModel
    {
        private static final long serialVersionUID = 8768314516739492104L;

        /** Stores the name of the last called notification method. */
        private String notificationMethod;

        /** Stores the notification start index. */
        private int startIdx;

        /** Stores the notification end index. */
        private int endIdx;

        public TableModelImpl(TableTag tt, JTable tab)
        {
            super(tt, tab);
        }

        @Override
        public int getColumnCount()
        {
            return COL_COUNT;
        }

        @Override
        public String getColumnName(int col)
        {
            return "Column" + col;
        }

        @Override
        public int getRowCount()
        {
            return ROW_COUNT;
        }

        @Override
        public Object getValueAt(int row, int col)
        {
            StringBuilder buf = new StringBuilder(8);
            buf.append('(').append(row);
            buf.append(',').append(col);
            buf.append(')');
            return buf.toString();
        }

        @Override
        public void fireTableDataChanged()
        {
            notificationMethod = METH_DATACHANGED;
            startIdx = -1;
            endIdx = -1;
        }

        @Override
        public void fireTableRowsDeleted(int idx1, int idx2)
        {
            notificationMethod = METH_DELETED;
            startIdx = idx1;
            endIdx = idx2;
        }

        @Override
        public void fireTableRowsInserted(int idx1, int idx2)
        {
            notificationMethod = METH_INSERTED;
            startIdx = idx1;
            endIdx = idx2;
        }

        @Override
        public void fireTableRowsUpdated(int idx1, int idx2)
        {
            notificationMethod = METH_UPDATED;
            startIdx = idx1;
            endIdx = idx2;
        }

        /**
         * Tests whether the expected notification method was called.
         *
         * @param method the expected method name
         */
        public void checkNotification(String method)
        {
            checkNotification(method, -1, -1);
        }

        /**
         * Tests whether the expected notification method was called with the
         * expected indices.
         *
         * @param method the expected method name
         * @param idx1 the start index
         * @param idx2 the end index
         */
        public void checkNotification(String method, int idx1, int idx2)
        {
            assertEquals("Wrong notification method", method,
                    notificationMethod);
            assertEquals("Wrong start index", idx1, startIdx);
            assertEquals("Wrong end index", idx2, endIdx);
        }
    }
}
