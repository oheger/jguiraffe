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
package net.sf.jguiraffe.gui.platform.swing.builder.components.table;

import static org.junit.Assert.assertSame;

import java.awt.Component;

import javax.swing.JLabel;

import net.sf.jguiraffe.gui.builder.components.tags.table.TableSelectionHandler;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for SwingTableCellRenderer.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingTableCellRenderer.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingTableCellRenderer extends AbstractTableModelTest
{
    /** Constant for the renderer component to be returned. */
    private static final Component RENDERER = new JLabel();

    /** Constant for the row index used for testing. */
    private static final int TEST_ROW = 1;

    /** Constant for the column index used for testing. */
    private static final int TEST_COL = 3;

    /** Stores the renderer to be tested. */
    private SwingTableCellRenderer renderer;

    @Before
    public void setUp() throws Exception
    {
        renderer = new SwingTableCellRenderer(createTableModel());
    }

    /**
     * Tests whether the selection handler is correctly invoked for the
     * specified attributes.
     *
     * @param selected the selected flag
     * @param focused the focused flag
     */
    private void checkSelectionHandler(boolean selected, boolean focused)
    {
        ((TableColumnTagTestImpl) renderer.getModel().getTableTag().getColumn(
                TEST_COL)).installRenderer(RENDERER);
        TableSelectionHandler selHandler = EasyMock
                .createMock(TableSelectionHandler.class);
        selHandler.prepareComponent(renderer.getModel().getTable(), tableTag,
                RENDERER, selected, focused, TEST_ROW, TEST_COL);
        EasyMock.replay(selHandler);
        tableTag.setRendererSelectionHandler(selHandler);
        assertSame("Wrong renderer component returned", RENDERER, renderer
                .getTableCellRendererComponent(renderer.getModel().getTable(),
                        null, selected, focused, TEST_ROW, TEST_COL));
        EasyMock.verify(selHandler);
    }

    /**
     * Tests obtaining the renderer component when the cell is neither selected
     * nor focused.
     */
    @Test
    public void testGetTableCellRendererComponentUnselected()
    {
        checkSelectionHandler(false, false);
    }

    /**
     * Tests obtaining the renderer component when the cell is selected.
     */
    @Test
    public void testGetTableCellRendererComponentSelected()
    {
        checkSelectionHandler(true, false);
    }

    /**
     * Tests obtaining the renderer component when the cell is focused.
     */
    @Test
    public void testGetTableCellRendererComponentFocused()
    {
        checkSelectionHandler(false, true);
    }

    /**
     * Tests obtaining the renderer component when the cell is selected and
     * focused.
     */
    @Test
    public void testGetTableCellRendererComponentSelectedFocused()
    {
        checkSelectionHandler(true, true);
    }
}
