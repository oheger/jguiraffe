/*
 * Copyright 2006-2014 The JGUIraffe Team.
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

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import java.awt.Component;

/**
 * <p>
 * A helper class for setting the correct row heights for a Swing table.
 * </p>
 * <p>
 * Swing does not automatically adapt the height of a table's rows to the size
 * of the contained components. This is problematic especially if a custom cell
 * renderer is used. This class implements this functionality. It can be called
 * with a table as argument and then updates the heights of all rows so that the
 * column with the greatest height fits in.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id$
 * @since 1.3
 */
public class SwingTableRowHeightUpdater
{
    /**
     * Updates the heights of all rows in the specified table.
     *
     * @param table the table
     */
    public void updateRowHeights(JTable table)
    {
        updateRowHeights(table, 0, table.getRowCount() - 1);
    }

    /**
     * Updates the heights of all rows of the specified table in the given
     * range. Note that this obviously has to be done in a separate task in the
     * event queue; otherwise, row height updates do not have any effect.
     *
     * @param table the table
     * @param startRow the index of the first row
     * @param endRow the index of the last row (including)
     */
    public void updateRowHeights(final JTable table, final int startRow,
            final int endRow)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {

                for (int row = startRow; row <= endRow; row++)
                {
                    int rowHeight = table.getRowHeight(row);
                    for (int col = 0; col < table.getColumnCount(); col++)
                    {
                        Component component =
                                table.prepareRenderer(
                                        table.getCellRenderer(row, col), row,
                                        col);
                        rowHeight =
                                Math.max(rowHeight,
                                        component.getPreferredSize().height);
                    }
                    table.setRowHeight(row, rowHeight);
                }
            }
        });
    }
}
