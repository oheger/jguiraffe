/*
 * Copyright 2006-2017 The JGUIraffe Team.
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

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.jguiraffe.gui.builder.components.Color;
import net.sf.jguiraffe.gui.builder.components.model.TableHandler;
import net.sf.jguiraffe.gui.platform.swing.builder.components.table.SwingTableModel;

/**
 * <p>
 * The Swing-specific implementation of a component handler for a table.
 * </p>
 * <p>
 * This class wraps a <code>javax.swing.JTable</code> component and implements
 * the methods required for Swing component handlers in a suitable way. The
 * following functionality is provided:
 * <ul>
 * <li>The data handling depends on the table's selection model. If the table
 * supports single selection only, the handler's data type is an int value
 * representing the selected row. Otherwise the type is an array of int values
 * storing the indices of the selected rows. The <code>getData()</code> and
 * <code>setData()</code> methods work correspondingly.</li>
 * <li>Methods are available for directly querying and manipulating the tables
 * selection. These methods are defined by the platform-neutral
 * {@link TableHandler} interface.</li>
 * <li>A set of methods support notifications when data in the table's model
 * changes. These methods are also defined by the
 * {@link TableHandler} interface. They are delegated to the
 * (Swing) table model implementation.</li>
 * <li>Platform-neutral change listeners can register at handlers of this type.
 * They will then be notified whenever the table's selection changes.</li>
 * <li>The colors of the table's selection can be queried and manipulated.</li>
 * <li>A scroll pane for the table is automatically created and maintained.</li>
 * </ul>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingTableComponentHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
class SwingTableComponentHandler extends SwingComponentHandler<Object> implements
        TableHandler, ListSelectionListener
{
    /** Stores the table's scroll pane. */
    private final JScrollPane scrollPane;

    /**
     * Creates a new instance of {@code SwingTableComponentHandler} and sets the
     * wrapped table.
     *
     * @param table the wrapped table component
     * @param scrollWidth the preferred scroll width
     * @param scrollHeight the preferred scroll height
     */
    public SwingTableComponentHandler(JTable table, int scrollWidth,
            int scrollHeight)
    {
        super(table);
        scrollPane = SwingComponentUtils.scrollPaneFor(table, scrollWidth,
                scrollHeight);
    }

    /**
     * Returns the table component.
     *
     * @return the table
     */
    public JTable getTable()
    {
        return (JTable) getComponent();
    }

    /**
     * Returns the table model.
     *
     * @return the table model
     */
    public SwingTableModel getTableModel()
    {
        return (SwingTableModel) getTable().getModel();
    }

    /**
     * Returns a flag whether the wrapped table supports multi selection.
     *
     * @return the multi selection flag
     */
    public boolean isMultiSelection()
    {
        return getTable().getSelectionModel().getSelectionMode()
            != ListSelectionModel.SINGLE_SELECTION;
    }

    /**
     * Clears any information about selected rows.
     */
    public void clearSelection()
    {
        getTable().clearSelection();
    }

    /**
     * Returns the index of the selected row. This method is intended to be used
     * in single selection mode. If no row is selected, result will be -1.
     *
     * @return the (0-based) index of the selected row
     */
    public int getSelectedIndex()
    {
        return getTable().getSelectedRow();
    }

    /**
     * Returns an array with the indices of the currently selected rows. This
     * method is intended to be used in multi selection mode. If no rows are
     * selected, an empty array will be returned.
     *
     * @return an array with the (0-based) indices of the selected rows
     */
    public int[] getSelectedIndices()
    {
        return getTable().getSelectedRows();
    }

    /**
     * Notifies the table about deleted rows. This will cause a redraw if
     * necessary.
     *
     * @param startIdx the start index of the affected row interval
     * @param endIdx the end index of the affected row interval
     */
    public void rowsDeleted(int startIdx, int endIdx)
    {
        getTableModel().fireTableRowsDeleted(startIdx, endIdx);
    }

    /**
     * Notifies the table about inserted rows. This will cause a redraw if
     * necessary.
     *
     * @param startIdx the start index of the affected row interval
     * @param endIdx the end index of the affected row interval
     */
    public void rowsInserted(int startIdx, int endIdx)
    {
        getTableModel().fireTableRowsInserted(startIdx, endIdx);
    }

    /**
     * Notifies the table about updated rows. This will cause a redraw if
     * necessary.
     *
     * @param startIdx the start index of the affected row interval
     * @param endIdx the end index of the affected row interval
     */
    public void rowsUpdated(int startIdx, int endIdx)
    {
        getTableModel().fireTableRowsUpdated(startIdx, endIdx);
    }

    /**
     * Sets the index of the selected row.
     *
     * @param rowIdx the (0-based) index of the row to select
     */
    public void setSelectedIndex(int rowIdx)
    {
        clearSelection();
        if (rowIdx >= 0)
        {
            getTable().addRowSelectionInterval(rowIdx, rowIdx);
            handleScrolling(scrollPane.getViewport(), rowIdx);
        }
    }

    /**
     * Selects a number of rows. This method is available in multi selection
     * mode.
     *
     * @param rowIndices the indices of the rows to be selected
     */
    public void setSelectedIndices(int[] rowIndices)
    {
        clearSelection();
        for (int row : rowIndices)
        {
            getTable().addRowSelectionInterval(row, row);
        }
    }

    /**
     * Notifies the table about an unspecific change of the data of its model.
     * This will cause a redraw.
     */
    public void tableDataChanged()
    {
        getTableModel().fireTableDataChanged();
    }

    /**
     * Returns the data of the underlying component. This is the table's
     * selection. Depending on the selection mode (single or multi) either a
     * single row index of an array of row indices is returned.
     *
     * @return the data of the represented component
     */
    public Object getData()
    {
        return isMultiSelection() ? getSelectedIndices() : getSelectedIndex();
    }

    /**
     * Returns the outer most component. For tables this is the scroll pane the
     * table is embedded.
     *
     * @return the outer component
     */
    @Override
    public Object getOuterComponent()
    {
        return scrollPane;
    }

    /**
     * Returns the type of this component handler. The type depends on the
     * table's selection mode.
     *
     * @return the type of this handler
     */
    public Class<?> getType()
    {
        return isMultiSelection() ? int[].class : Integer.TYPE;
    }

    /**
     * Sets the data of this component. Supported are objects of type
     * <code>Number</code> or an array of int values. A value of <b>null</b>
     * is also accepted, it will reset the selection.
     *
     * @param data the new data for the component
     * @throws IllegalArgumentException if the passed in value is not supported
     */
    public void setData(Object data)
    {
        if (data == null)
        {
            clearSelection();
        }
        else if (data instanceof Number)
        {
            setSelectedIndex(((Number) data).intValue());
        }
        else if (data instanceof int[])
        {
            setSelectedIndices((int[]) data);
        }
        else
        {
            throw new IllegalArgumentException("Unsupported type for setData: "
                    + data);
        }
    }

    /**
     * Returns the model of this table. This is the list with the beans
     * representing the rows of this table. It is obtained from the table model.
     * Note that no defensive copy of the list is created, so it can be
     * manipulated by callers directly. This is legal, but then the change
     * methods like {@code rowsDeleted()} or {@code rowsUpdated()} should be
     * called to notify the table about these external changes.
     *
     * @return the model of this table
     */
    public List<Object> getModel()
    {
        return getTableModel().getModelData();
    }

    /**
     * Returns the selection background color. This implementation obtains the
     * color from the underlying table and converts it to a platform-independent
     * {@code Color} object.
     *
     * @return the selection background color
     */
    public Color getSelectionBackground()
    {
        return SwingComponentUtils.swing2LogicColor(getTable()
                .getSelectionBackground());
    }

    /**
     * Returns the selection foreground color. This implementation obtains the
     * color from the underlying table and converts it to a platform-independent
     * {@code Color} object.
     *
     * @return the selection foreground color
     */
    public Color getSelectionForeground()
    {
        return SwingComponentUtils.swing2LogicColor(getTable()
                .getSelectionForeground());
    }

    /**
     * Sets the selection background color. This implementation converts the
     * specified {@code Color} object to the corresponding {@code
     * java.awt.Color} and passes it to the underlying table.
     *
     * @param c the new selection background color
     */
    public void setSelectionBackground(Color c)
    {
        getTable().setSelectionBackground(SwingComponentUtils.logic2SwingColor(c));
    }

    /**
     * Sets the selection foreground color. This implementation converts the
     * specified {@code Color} object to the corresponding {@code
     * java.awt.Color} and passes it to the underlying table.
     *
     * @param c the new selection foreground color
     */
    public void setSelectionForeground(Color c)
    {
        getTable().setSelectionForeground(SwingComponentUtils.logic2SwingColor(c));
    }

    /**
     * Reacts on changes of the table's selection. This will cause registered
     * change listeners to be triggered.
     *
     * @param e the list selection event
     */
    public void valueChanged(ListSelectionEvent e)
    {
        if (!e.getValueIsAdjusting())
        {
            fireChangeEvent(e);
        }
    }

    /**
     * Registers a change listener at the underlying component. This
     * implementation registers this object itself as a list selection lister at
     * the wrapped table's row selection model.
     */
    @Override
    protected void registerChangeListener()
    {
        getTable().getSelectionModel().addListSelectionListener(this);
    }

    /**
     * Unregisters this handler as change listener from the underlying component.
     */
    @Override
    protected void unregisterChangeListener()
    {
        getTable().getSelectionModel().removeListSelectionListener(this);
    }

    /**
     * Handles scrolling when a row is selected. This method ensures that the
     * newly selected row becomes visible. However, the horizontal scrolling
     * position should not be changed.
     *
     * @param vp the view port
     * @param rowIdx the index of the row to be made visible
     */
    void handleScrolling(JViewport vp, int rowIdx)
    {
        Point orgPos = vp.getViewPosition();
        Rectangle rect = getTable().getCellRect(rowIdx, 0, true);
        getTable().scrollRectToVisible(rect);
        updateViewport(vp, orgPos);
    }

    /**
     * Updates the scroll position of the view port. This method is called when
     * a row is selected. In this case the row should be made visible, but the
     * horizontal position should not be changed. This method ensures that the X
     * offset is restored if it was changed.
     *
     * @param vp the {@code JViewport}
     * @param orgPos the original position of the {@code JViewport}
     */
    void updateViewport(JViewport vp, Point orgPos)
    {
        Point newPos = vp.getViewPosition();
        if (orgPos.x != newPos.x)
        {
            vp.setViewPosition(new Point(orgPos.x, newPos.y));
        }
    }
}
