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
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.awt.Dimension;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code SwingTableRowHeightUpdater}.
 *
 * @author Oliver Heger
 * @version $Id$
 */
public class TestSwingTableRowHeightUpdater
{
    /**
     * Constant for the heights of the cells in the test table.
     */
    private static final int[][] CELL_HEIGHTS = new int[][] {
            {
                    10, 8, 16, 8
            }, {
                    12, 50, 48, 16
            }, {
                    24, 18, 16, 12
            }
    };

    /**
     * Constant for the maximum heights in the cells of the test table per row.
     * These are the values the updater has to determine.
     */
    private static final int[] EXPECTED_HEIGHTS = new int[] {
            16, 50, 24
    };

    /** The updater to be tested. */
    private SwingTableRowHeightUpdater updater;

    @Before
    public void setUp() throws Exception
    {
        updater = new SwingTableRowHeightUpdater();
    }

    /**
     * Expects a processing of the heights of the rows in the specified range.
     *
     * @param table the table mock
     * @param startRow the start row
     * @param endRow the end row (including)
     */
    private static void expectRowProcessing(JTable table, int startRow,
            int endRow)
    {
        TableCellRenderer renderer =
                EasyMock.createMock(TableCellRenderer.class);
        Component component = EasyMock.createMock(Component.class);
        EasyMock.expect(table.getColumnCount())
                .andReturn(CELL_HEIGHTS[0].length).anyTimes();
        for (int row = startRow; row < endRow; row++)
        {
            EasyMock.expect(table.getRowHeight(row)).andReturn(10);
            for (int col = 0; col < CELL_HEIGHTS[row].length; col++)
            {
                EasyMock.expect(table.getCellRenderer(row, col)).andReturn(
                        renderer);
                EasyMock.expect(table.prepareRenderer(renderer, row, col))
                        .andReturn(component);
                EasyMock.expect(component.getPreferredSize()).andReturn(
                        new Dimension(0, CELL_HEIGHTS[row][col]));
            }
            table.setRowHeight(row, EXPECTED_HEIGHTS[row]);
        }
        EasyMock.replay(renderer, component);
    }

    /**
     * Tests whether the row heights of a full table are updated correctly.
     */
    @Test
    public void testUpdateRowHeights()
    {
        JTable table = EasyMock.createMock(JTable.class);
        EasyMock.expect(table.getRowCount()).andReturn(EXPECTED_HEIGHTS.length)
                .anyTimes();
        expectRowProcessing(table, 0, EXPECTED_HEIGHTS.length);
        EasyMock.replay(table);

        updater.updateRowHeights(table);
        EasyMock.verify(table);
    }

    /**
     * Tests whether a specific range of table rows can be updated.
     */
    @Test
    public void testUpdateRowHeightsInRange()
    {
        JTable table = EasyMock.createMock(JTable.class);
        expectRowProcessing(table, 1, 2);
        EasyMock.replay(table);

        updater.updateRowHeights(table, 1, 1);
        EasyMock.verify(table);
    }
}
