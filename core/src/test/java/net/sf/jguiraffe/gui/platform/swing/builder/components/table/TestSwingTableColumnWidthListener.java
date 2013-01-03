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

import java.awt.event.ComponentEvent;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import net.sf.jguiraffe.gui.builder.components.tags.table.TableColumnTag;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableColumnWidthController;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableTag;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code SwingTableColumnWidthListener}.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingTableColumnWidthListener.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingTableColumnWidthListener
{
    /** Constant for the relative column widths. */
    private static final int[] WIDTHS = {
            20, 30, 10, 25, 15
    };

    /** Constant for the total table width. */
    private static final int TABLE_WIDTH = 200;

    /** The table monitored by the test listener. */
    private JTable table;

    /** The column width controller. */
    private TableColumnWidthController columnWidthController;

    /**
     * A flag whether access to the column model is forbidden. If set to true,
     * the test table throws an exception if its column model is accessed.
     */
    private boolean forbidColumnModelAccess;

    /** The listener to be tested. */
    private SwingTableColumnWidthListener listener;

    @SuppressWarnings("serial")
    @Before
    public void setUp() throws Exception
    {
        TableTag tt = setUpTableTag();
        table = new JTable(1, WIDTHS.length)
        {
            /**
             * If the forbidColumnModelAccess flag is set, an exception is
             * thrown. Otherwise, the super method is called.
             */
            @Override
            public TableColumnModel getColumnModel()
            {
                if (forbidColumnModelAccess)
                {
                    throw new UnsupportedOperationException(
                            "Unexpected method call!");
                }
                return super.getColumnModel();
            }
        };
        table.setSize(TABLE_WIDTH, TABLE_WIDTH);
        columnWidthController = tt.getColumnWidthController();
        listener = new SwingTableColumnWidthListener(table,
                columnWidthController);
    }

    /**
     * Creates a table tag with test columns.
     *
     * @return the table tag
     */
    private static TableTag setUpTableTag()
    {
        TableTag tt = new TableTag();
        for (int width : WIDTHS)
        {
            TableColumnTag colTag = new TableColumnTag();
            colTag.setPercentWidth(width);
            tt.addColumn(colTag);
        }
        return tt;
    }

    /**
     * Tests whether the listener reacts correctly on resize events.
     */
    @Test
    public void testComponentResized()
    {
        ComponentEvent event = new ComponentEvent(table,
                ComponentEvent.COMPONENT_RESIZED);
        listener.componentResized(event);
        for (int i = 0; i < WIDTHS.length; i++)
        {
            TableColumn col = table.getColumnModel().getColumn(i);
            assertEquals("Wrong column width at " + i, 2*WIDTHS[i], col
                    .getWidth());
        }
    }

    /**
     * Tests whether a change of the column size is detected.
     */
    @Test
    public void testColumnMarginChanged()
    {
        final int len = WIDTHS.length;
        for (int i = 0; i < len; i++)
        {
            table.getColumnModel().getColumn(i)
                    .setMinWidth(WIDTHS[len - i - 1]);
            table.getColumnModel().getColumn(i).setWidth(WIDTHS[len - i - 1]);
        }
        table.getTableHeader().setResizingColumn(
                table.getColumnModel().getColumn(0));
        listener.columnMarginChanged(null);
        for (int i = 0; i < WIDTHS.length; i++)
        {
            assertEquals("Wrong width at " + i, WIDTHS[len - i - 1],
                    columnWidthController.getPercentValue(i));
        }
    }

    /**
     * Tests whether a column margin changed event is ignored if there is no
     * columns currently resized.
     */
    @Test
    public void testColumnMarginChangedIgnore()
    {
        forbidColumnModelAccess = true;
        listener.columnMarginChanged(null);
    }

    /**
     * Tests the columnAdded() implementation. We can only test that the table's
     * model is not touched.
     */
    @Test
    public void testColumnAdded()
    {
        forbidColumnModelAccess = true;
        listener.columnAdded(null);
    }

    /**
     * Tests the columnMoved() implementation. We can only test that the table's
     * model is not touched.
     */
    @Test
    public void testColumnMoved()
    {
        forbidColumnModelAccess = true;
        listener.columnMoved(null);
    }

    /**
     * Tests the columnRemoved() implementation. We can only test that the
     * table's model is not touched.
     */
    @Test
    public void testColumnRemoved()
    {
        forbidColumnModelAccess = true;
        listener.columnRemoved(null);
    }

    /**
     * Tests the columnSelectionChanged() implementation. We can only test that
     * the table's model is not touched.
     */
    @Test
    public void testColumnSelectionChanged()
    {
        forbidColumnModelAccess = true;
        listener.columnSelectionChanged(null);
    }
}
