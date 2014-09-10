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
    /** The updater to be tested. */
    private SwingTableRowHeightUpdater updater;

    @Before
    public void setUp() throws Exception
    {
        updater = new SwingTableRowHeightUpdater();
    }

    /**
     * Tests whether the row heights are updated correctly.
     */
    @Test
    public void testUpdateRowHeights()
    {
        final int[][] cellHeights = {
                {
                        10, 8, 16, 8
                }, {
                        12, 50, 48, 16
                }
        };
        final int[] expectedHeights = {
                16, 50
        };
        JTable table = EasyMock.createMock(JTable.class);
        TableCellRenderer renderer =
                EasyMock.createMock(TableCellRenderer.class);
        Component component = EasyMock.createMock(Component.class);
        EasyMock.expect(table.getRowCount()).andReturn(expectedHeights.length)
                .anyTimes();
        EasyMock.expect(table.getColumnCount())
                .andReturn(cellHeights[0].length).anyTimes();
        for (int row = 0; row < expectedHeights.length; row++)
        {
            EasyMock.expect(table.getRowHeight(row)).andReturn(10);
            for (int col = 0; col < cellHeights[row].length; col++)
            {
                EasyMock.expect(table.getCellRenderer(row, col)).andReturn(
                        renderer);
                EasyMock.expect(table.prepareRenderer(renderer, row, col))
                        .andReturn(component);
                EasyMock.expect(component.getPreferredSize()).andReturn(
                        new Dimension(0, cellHeights[row][col]));
            }
            table.setRowHeight(row, expectedHeights[row]);
        }
        EasyMock.replay(table, renderer, component);

        updater.updateRowHeights(table);
        EasyMock.verify(table);
    }
}
