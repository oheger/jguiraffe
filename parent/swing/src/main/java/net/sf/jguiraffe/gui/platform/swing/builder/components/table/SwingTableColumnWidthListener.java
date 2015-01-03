/*
 * Copyright 2006-2015 The JGUIraffe Team.
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

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;

import net.sf.jguiraffe.gui.builder.components.tags.table.TableColumnWidthController;

/**
 * <p>
 * A specialized event listener class for keeping track of and resizing the
 * columns of a table.
 * </p>
 * <p>
 * This class plays an important role in the Swing-specific implementation of
 * tables with relative column widths. An instance is associated with a
 * {@link TableColumnWidthController} object and registered as listener for
 * certain events at a table component. In this constellation this class
 * performs the following tasks:
 * <ul>
 * <li>When the table component is resized, it ensures that the widths of the
 * columns are correctly set. This is especially important for columns with a
 * percent width: the space available is distributed to the columns based on
 * their relative width.</li>
 * <li>When the user manually resizes a column the
 * {@link TableColumnWidthController} is updated so that it is notified about
 * the new column width.</li>
 * </ul>
 * </p>
 * <p>
 * This class is used internally by the Swing-specific table implementation. If
 * a table has columns with a percent width, the component manager
 * implementation creates an instance and registers it at the table. It is not
 * intended to be used by applications directly.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingTableColumnWidthListener.java 205 2012-01-29 18:29:57Z oheger $
 */
public class SwingTableColumnWidthListener extends ComponentAdapter implements
        TableColumnModelListener
{
    /** The table to be monitored by this listener. */
    private final JTable table;

    /** The associated width controller. */
    private final TableColumnWidthController columnWidthController;

    /**
     * Creates a new instance of {@code SwingTableColumnWidthListener} and
     * initializes it.
     *
     * @param tab the table to be monitored
     * @param ctrl the associated {@code TableColumnWidthController}
     */
    public SwingTableColumnWidthListener(JTable tab,
            TableColumnWidthController ctrl)
    {
        table = tab;
        columnWidthController = ctrl;
    }

    /**
     * The table monitored by this listener has been resized. This
     * implementation adjusts the column widths according to their
     * specifications.
     *
     * @param e the event
     */
    @Override
    public void componentResized(ComponentEvent e)
    {
        int[] widths = columnWidthController
                .calculateWidths(table.getSize().width);

        for (int i = 0; i < widths.length; i++)
        {
            TableColumn col = table.getColumnModel().getColumn(i);
            col.setPreferredWidth(widths[i]);
            col.setWidth(widths[i]);
        }
    }

    /**
     * Dummy implementation of this method of the {@code
     * TableColumnModelListener} interface.
     *
     * @param e the event
     */
    public void columnAdded(TableColumnModelEvent e)
    {
    }

    /**
     * The width of a column has been changed. This implementation notifies the
     * {@link TableColumnWidthController} about this change. Note that this
     * event is only processed if a column is currently resized. This is due to
     * the fact that events of this type are also triggered by the automatic
     * resizing mechanism.
     *
     * @param e the change event
     */
    public void columnMarginChanged(ChangeEvent e)
    {
        TableColumn column = table.getTableHeader().getResizingColumn();
        if (column != null)
        {
            int[] widths = new int[columnWidthController.getColumnCount()];

            for (int i = 0; i < widths.length; i++)
            {
                widths[i] = table.getColumnModel().getColumn(i).getWidth();
            }

            column.setPreferredWidth(column.getWidth());
            columnWidthController.recalibrate(widths);
        }
    }

    /**
     * Dummy implementation of this method of the {@code
     * TableColumnModelListener} interface.
     *
     * @param e the event
     */
    public void columnMoved(TableColumnModelEvent e)
    {
    }

    /**
     * Dummy implementation of this method of the {@code
     * TableColumnModelListener} interface.
     *
     * @param e the event
     */
    public void columnRemoved(TableColumnModelEvent e)
    {
    }

    /**
     * Dummy implementation of this method of the {@code
     * TableColumnModelListener} interface.
     *
     * @param e the event
     */
    public void columnSelectionChanged(ListSelectionEvent e)
    {
    }
}
