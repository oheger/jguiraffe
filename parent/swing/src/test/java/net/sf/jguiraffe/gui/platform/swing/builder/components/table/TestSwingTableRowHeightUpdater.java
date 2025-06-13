/*
 * Copyright 2006-2025 The JGUIraffe Team.
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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.awt.Dimension;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
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
     * @param endRow the end row (excluding)
     * @return a latch for determining when the operation is finished
     */
    private static CountDownLatch expectRowProcessing(JTable table,
            int startRow, int endRow)
    {
        TableCellRenderer renderer =
                EasyMock.createMock(TableCellRenderer.class);
        Component component = EasyMock.createMock(Component.class);
        final CountDownLatch latch = new CountDownLatch(endRow - startRow);
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
            EasyMock.expectLastCall().andAnswer(new IAnswer<Object>()
            {
                public Object answer() throws Throwable
                {
                    assertTrue("Not in EDT",
                            SwingUtilities.isEventDispatchThread());
                    latch.countDown();
                    return null;
                }
            });
        }
        EasyMock.replay(renderer, component);
        return latch;
    }

    /**
     * Verifies the mock for the table to ensure that row height updates have
     * been performed correctly. This has to be done in the event dispatch
     * thread.
     *
     * @param table the table mock
     * @param latch the latch for synchronizing with the update operation
     */
    private static void verifyTableMock(JTable table, CountDownLatch latch)
    {
        try
        {
            assertTrue("Time out", latch.await(10, TimeUnit.SECONDS));
        }
        catch (InterruptedException iex)
        {
            fail("Waiting was interrupted: " + iex);
        }
        EasyMock.verify(table);
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
        CountDownLatch latch =
                expectRowProcessing(table, 0, EXPECTED_HEIGHTS.length);
        EasyMock.replay(table);

        updater.updateRowHeights(table);
        verifyTableMock(table, latch);
    }

    /**
     * Tests whether a specific range of table rows can be updated.
     */
    @Test
    public void testUpdateRowHeightsInRange()
    {
        JTable table = EasyMock.createMock(JTable.class);
        CountDownLatch latch = expectRowProcessing(table, 1, 2);
        EasyMock.replay(table);

        updater.updateRowHeights(table, 1, 1);
        verifyTableMock(table, latch);
    }
}
