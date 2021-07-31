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
package net.sf.jguiraffe.gui.builder.components.model;

import java.util.List;

import net.sf.jguiraffe.gui.builder.components.Color;
import net.sf.jguiraffe.gui.forms.ComponentHandler;

/**
 * <p>
 * A specialized {@code ComponentHandler} interface dealing with specific
 * functionality provided by tables.
 * </p>
 * <p>
 * Tables provide some special functionality that is not covered by the default
 * {@code ComponentHandler} interface. So this extended interface was
 * introduced. It provides access to the selected index (or indices in
 * multi-selection mode), or allows sending change notifications if the data of
 * the table's model has changed.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TableHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface TableHandler extends ComponentHandler<Object>
{
    /**
     * Returns the index of the selected row. This method is applicable in
     * single selection mode. The returned index is 0-based. A return value of
     * -1 indicates that no row is selected.
     *
     * @return the index of the selected row
     */
    int getSelectedIndex();

    /**
     * Sets the index of the selected row. In single selection mode this method
     * can be used to select a row.
     *
     * @param rowIdx the (0-based) index of the row to select
     */
    void setSelectedIndex(int rowIdx);

    /**
     * Returns an array with the indices of the selected rows. This method is
     * applicable in multi selection mode. It works like
     * {@code getSelectedIndex()}, but multiple indices can be returned.
     *
     * @return an array with the indices of the selected rows (never <b>null</b>)
     */
    int[] getSelectedIndices();

    /**
     * Sets the indices of the selected rows in multi selection mode. With this
     * method an arbitrary number of rows can be selected at once.
     *
     * @param rowIndices an array with the indices of the selected rows (must
     * not be <b>null</b>; use {@code clearSelection()} for clearing any
     * selected rows)
     */
    void setSelectedIndices(int[] rowIndices);

    /**
     * Clears the table's selection. This method can be used in both single and
     * multi selection mode for removing any selection.
     */
    void clearSelection();

    /**
     * Notifies the table about an unspecific change in the data of its model.
     * When some properties of the beans that form the table's data model are
     * changed, the table is not able to pick up these changes automatically.
     * Instead it has to be notified that something has changed, which will
     * eventually cause a redraw operation. This is the most unspecific method
     * for change notifications. It simply says that something in the model's
     * data has changed. The table will have to redraw itself to stay in sync
     * with the model's data. If the change can be described in a more precise
     * way, it will be probably more efficient to use one of the other change
     * notification methods.
     */
    void tableDataChanged();

    /**
     * Notifies the table that new rows have been inserted. This method can be
     * called if objects have been added to the table's model. It works like
     * {@code tableDataChanged()}, but will probably be more efficient
     * because the table only needs to be redrawn if necessary.
     *
     * @param startIdx the start index of the newly added rows
     * @param endIdx the end index of the newly added rows (inclusive)
     * @see #tableDataChanged()
     */
    void rowsInserted(int startIdx, int endIdx);

    /**
     * Notifies the table that a number of rows has been deleted. This method
     * can be called if some objects have been deleted from the table's model.
     * It works like {@code tableDataChanged()}, but will probably be
     * more efficient because the table only needs to be redrawn if necessary.
     *
     * @param startIdx the index of the first affected row
     * @param endIdx the index of the last affected row (inclusive)
     */
    void rowsDeleted(int startIdx, int endIdx);

    /**
     * Notifies the table that a number of rows has been modified. This method
     * can be called if some data objects of the table's model have been
     * changed. It works like {@code tableDataChanged()}, but will
     * probably be more efficient because the table only needs to be redrawn if
     * necessary.
     *
     * @param startIdx the index of the first affected row
     * @param endIdx the index of the last affected row (inclusive)
     */
    void rowsUpdated(int startIdx, int endIdx);

    /**
     * Returns the underlying model of this table. This is a list that contains
     * beans representing the single rows of the table.
     *
     * @return the model serving as the model of this table
     */
    List<Object> getModel();

    /**
     * Returns the current background color of the selection.
     *
     * @return the selection background color
     */
    Color getSelectionBackground();

    /**
     * Sets the background color for the selection.
     *
     * @param c the selection background color
     */
    void setSelectionBackground(Color c);

    /**
     * Returns the current foreground color of the selection.
     *
     * @return the selection foreground color
     */
    Color getSelectionForeground();

    /**
     * Sets the foreground color for the selection.
     *
     * @param c the selection foreground color
     */
    void setSelectionForeground(Color c);
}
