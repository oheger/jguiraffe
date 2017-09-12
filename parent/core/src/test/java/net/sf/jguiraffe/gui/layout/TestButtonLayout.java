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
package net.sf.jguiraffe.gui.layout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import net.sf.jguiraffe.JGuiraffeTestHelper;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for ButtonLayout.
 *
 * @author Oliver Heger
 * @version $Id: TestButtonLayout.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestButtonLayout
{
    /** Constant for the vertical margin. */
    private static final int VERT_MARGIN = 8;

    /** Constant for the horizontal margin. */
    private static final int HOR_MARGIN = 16;

    /** Constant for the gap between the buttons. */
    private static final int GAP = 10;

    /** Constant for the width of the widest button. */
    private static final int MAX_WIDTH = 80;

    /** Constant for the default height. */
    private static final int DEF_HEIGHT = 20;

    /** The layout adapter. */
    private PercentLayoutPlatformAdapterImpl adapter;

    /** The layout to be tested. */
    private ButtonLayout layout;

    @Before
    public void setUp() throws Exception
    {
        adapter = new PercentLayoutPlatformAdapterImpl();
        layout = new ButtonLayout();
        layout.setPlatformAdapter(adapter);
    }

    /**
     * Initializes a button layout with three buttons.
     *
     * @param align the layout's alignment
     */
    private void setUpLayout(ButtonLayout.Alignment align)
    {
        layout.setAlignment(align);
        layout.setTopMargin(new NumberWithUnit(VERT_MARGIN));
        layout.setBottomMargin(layout.getTopMargin());
        layout.setLeftMargin(new NumberWithUnit(HOR_MARGIN));
        layout.setRightMargin(layout.getLeftMargin());
        layout.setGap(new NumberWithUnit(GAP));

        adapter.createComponent(null, MAX_WIDTH - 10, DEF_HEIGHT, null);
        adapter.createComponent(null, MAX_WIDTH, DEF_HEIGHT, null);
        adapter.createComponent(null, MAX_WIDTH - 20, DEF_HEIGHT, null);
    }

    /**
     * Tests a newly created layout object.
     */
    @Test
    public void testInit()
    {
        assertEquals("Wrong left margin", ButtonLayout.DEFAULT_MARGIN, layout
                .getLeftMargin());
        assertEquals("Wrong right margin", ButtonLayout.DEFAULT_MARGIN, layout
                .getRightMargin());
        assertEquals("Wrong top margin", ButtonLayout.DEFAULT_MARGIN, layout
                .getTopMargin());
        assertEquals("Wrong bottom margin", ButtonLayout.DEFAULT_MARGIN, layout
                .getBottomMargin());
        assertEquals("Wrong gap", ButtonLayout.DEFAULT_GAP, layout.getGap());
        assertEquals("Wrong alignment", ButtonLayout.Alignment.RIGHT, layout
                .getAlignment());
    }

    /**
     * Tests setting the margins.
     */
    @Test
    public void testSetMargins()
    {
        NumberWithUnit n = new NumberWithUnit(1, Unit.CM);
        layout.setLeftMargin(n);
        assertEquals(n, layout.getLeftMargin());
        layout.setLeftMargin(null);
        assertEquals(NumberWithUnit.ZERO, layout.getLeftMargin());
        layout.setRightMargin(n);
        assertEquals(n, layout.getRightMargin());
        layout.setRightMargin(null);
        assertEquals(NumberWithUnit.ZERO, layout.getRightMargin());
        layout.setTopMargin(n);
        assertEquals(n, layout.getTopMargin());
        layout.setTopMargin(null);
        assertEquals(NumberWithUnit.ZERO, layout.getTopMargin());
        layout.setBottomMargin(n);
        assertEquals(n, layout.getBottomMargin());
        layout.setBottomMargin(null);
        assertEquals(NumberWithUnit.ZERO, layout.getBottomMargin());
        layout.setGap(n);
        assertEquals(n, layout.getGap());
        layout.setGap(null);
        assertEquals(NumberWithUnit.ZERO, layout.getGap());
    }

    /**
     * Tries to set a null alignment. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetAlignmentNull()
    {
        layout.setAlignment(null);
    }

    /**
     * Tests whether the specified constraint represents a gap of the given size
     *
     * @param cc the constraint
     * @param size the size of the gap
     * @param weight the expected weight factor
     */
    private void checkGap(CellConstraints cc, int size, int weight)
    {
        assertEquals("Got a cell size", CellSize.NONE, cc.getCellSize());
        assertEquals("Wrong size", new NumberWithUnit(size), cc.getMinSize());
        assertEquals("Wrong weight", weight, cc.getWeight());
    }

    /**
     * Tests the column constraint for a button.
     *
     * @param cc the constraint to be tested
     */
    private void checkButtonConstr(CellConstraints cc)
    {
        assertEquals("Wrong alignment", CellAlignment.FULL, cc.getAlignment());
        assertEquals("Wrong cell size", CellSize.PREFERRED, cc.getCellSize());
        assertEquals("Wrong minimum size", NumberWithUnit.ZERO, cc.getMinSize());
        assertEquals("Got a weight factor", 0, cc.getWeight());
    }

    /**
     * Tests the constraints of the test layout.
     *
     * @param weightLeft the expected weight factor for the left margin
     * @param weightRight the expected weight factor for the right margin
     */
    private void checkLayout(int weightLeft, int weightRight)
    {
        assertEquals("Wrong number of rows", 3, layout.getRowCount());
        checkGap(layout.getRowConstraints(0), VERT_MARGIN, 0);
        checkGap(layout.getRowConstraints(2), VERT_MARGIN, 0);
        CellConstraints cc = layout.getRowConstraints(1);
        assertEquals("Wrong cell alignment", CellAlignment.CENTER, cc
                .getAlignment());
        assertEquals("Wrong cell size", CellSize.PREFERRED, cc.getCellSize());

        CellConstraints[] cccols = layout.getAllColumnConstraints();
        assertEquals("Wrong number of constraints", 7, cccols.length);
        checkGap(cccols[0], HOR_MARGIN, weightLeft);
        checkButtonConstr(cccols[1]);
        checkGap(cccols[2], GAP, 0);
        checkButtonConstr(cccols[3]);
        checkGap(cccols[4], GAP, 0);
        checkButtonConstr(cccols[5]);
        checkGap(cccols[6], HOR_MARGIN, weightRight);

        assertTrue("Got row groups", layout.getRowGroups().isEmpty());
        Collection<CellGroup> colGroups = layout.getColumnGroups();
        assertEquals("Wrong number of column groups", 1, colGroups.size());
        CellGroup group = colGroups.iterator().next();
        int[] sizes = new int[] {
                HOR_MARGIN, MAX_WIDTH - 10, GAP, MAX_WIDTH, GAP,
                MAX_WIDTH - 20, HOR_MARGIN
        };
        int[] expSizes = new int[] {
                HOR_MARGIN, MAX_WIDTH, GAP, MAX_WIDTH, GAP, MAX_WIDTH,
                HOR_MARGIN
        };
        group.apply(sizes);
        assertTrue("Wrong sizes", Arrays.equals(expSizes, sizes));
    }

    /**
     * Tests the constraints of a layout that is left aligned.
     */
    @Test
    public void testConstraintsLeftAlign()
    {
        setUpLayout(ButtonLayout.Alignment.LEFT);
        checkLayout(0, 100);
    }

    /**
     * Tests the constraints of a layout that is right aligned.
     */
    @Test
    public void testConstraintsRightAlign()
    {
        setUpLayout(ButtonLayout.Alignment.RIGHT);
        checkLayout(100, 0);
    }

    /**
     * Tests the constraints of a layout that is center aligned.
     */
    @Test
    public void testConstraintsCenterAlign()
    {
        setUpLayout(ButtonLayout.Alignment.CENTER);
        checkLayout(50, 50);
    }

    /**
     * Helper method for checking a PercentData object.
     *
     * @param col the expected column
     * @param row the expected row
     */
    private void checkPercentData(int col, int row)
    {
        PercentData pd = layout.getPercentData(col, row);
        assertEquals("Wrong column", col, pd.getColumn());
        assertEquals("Wrong row", row, pd.getRow());
        assertEquals("Wrong span X", 1, pd.getSpanX());
        assertEquals("Wrong span Y", 1, pd.getSpanY());
        assertEquals("Got target column", PercentData.POS_UNDEF, pd
                .getTargetColumn());
        assertEquals("Got target row", PercentData.POS_UNDEF, pd.getTargetRow());
        assertNull("Got col constraints", pd.getColumnConstraints());
        assertNull("Got row constraints", pd.getRowConstraints());
    }

    /**
     * Tests the PercentData constraints created for the buttons.
     */
    @Test
    public void testPercentData()
    {
        setUpLayout(ButtonLayout.Alignment.LEFT);
        checkPercentData(1, 1);
        checkPercentData(3, 1);
        checkPercentData(5, 1);
    }

    /**
     * Tests a layout that contains only a single button.
     */
    @Test
    public void testLayoutSingleButton()
    {
        adapter.createComponent(null, MAX_WIDTH, DEF_HEIGHT, null);
        checkPercentData(1, 1);
    }

    /**
     * Tests whether the layout can be serialized.
     */
    @Test
    public void testSerialization() throws IOException
    {
        setUpLayout(ButtonLayout.Alignment.CENTER);
        ButtonLayout layout2 = JGuiraffeTestHelper.serialize(layout);
        TestPercentLayout.compareLayouts(layout, layout2);
        assertEquals("Wrong alignment", layout.getAlignment(), layout2
                .getAlignment());
        assertEquals("Wrong left margin", layout.getLeftMargin(), layout2
                .getLeftMargin());
        assertEquals("Wrong right margin", layout.getRightMargin(), layout2
                .getRightMargin());
        assertEquals("Wrong top margin", layout.getTopMargin(), layout2
                .getTopMargin());
        assertEquals("Wrong bottom margin", layout.getBottomMargin(), layout2
                .getBottomMargin());
        assertEquals("Wrong gap", layout.getGap(), layout2.getGap());
    }
}
