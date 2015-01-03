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
package net.sf.jguiraffe.gui.layout;

import static org.junit.Assert.assertEquals;

import java.awt.Dimension;
import java.io.IOException;

import net.sf.jguiraffe.JGuiraffeTestHelper;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for BorderLayout.
 *
 * @author Oliver Heger
 * @version $Id: TestBorderLayout.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestBorderLayout
{
    /** Constant for the size of the left margin. */
    private static final int SZ_LEFT = 10;

    /** Constant for the size of the top margin. */
    private static final int SZ_TOP = 5;

    /** Constant for the size of the right margin. */
    private static final int SZ_RIGHT = 12;

    /** Constant for the size of the bottom margin. */
    private static final int SZ_BOTTOM = 6;

    /** Constant for the size of the gap. */
    private static final int SZ_GAP = 20;

    /** Constant for the left margin. */
    private static final NumberWithUnit LEFT = new NumberWithUnit(SZ_LEFT);

    /** Constant for the top margin. */
    private static final NumberWithUnit TOP = new NumberWithUnit(SZ_TOP);

    /** Constant for the right margin. */
    private static final NumberWithUnit RIGHT = new NumberWithUnit(SZ_RIGHT);

    /** Constant for the bottom margin. */
    private static final NumberWithUnit BOTTOM = new NumberWithUnit(SZ_BOTTOM);

    /** Constant for the GAP. */
    private static final NumberWithUnit GAP = new NumberWithUnit(SZ_GAP);

    /** The platform adapter. */
    private PercentLayoutPlatformAdapterImpl adapter;

    /** The layout to be tested. */
    private BorderLayout layout;

    @Before
    public void setUp() throws Exception
    {
        layout = new BorderLayout();
        adapter = new PercentLayoutPlatformAdapterImpl();
        layout.setPlatformAdapter(adapter);
    }

    /**
     * Helper method for testing a constraints object for a margin.
     *
     * @param constr the array with all constraints
     * @param index the index to test
     * @param margin the expected margin
     */
    private static void checkMargin(CellConstraints[] constr, int index,
            NumberWithUnit margin)
    {
        assertEquals("Wrong cell size", CellSize.NONE, constr[index]
                .getCellSize());
        assertEquals("Wrong minimum size", margin, constr[index].getMinSize());
    }

    /**
     * Tests whether the layout has the expected margins.
     */
    private void checkMargins()
    {
        CellConstraints[] cccol = layout.getAllColumnConstraints();
        assertEquals("Wrong number of column constraints", 7, cccol.length);
        checkMargin(cccol, 0, LEFT);
        checkMargin(cccol, 6, RIGHT);
        CellConstraints[] ccrow = layout.getAllRowConstraints();
        assertEquals("Wrong number of row constraints", 7, ccrow.length);
        checkMargin(ccrow, 0, TOP);
        checkMargin(ccrow, 6, BOTTOM);
    }

    /**
     * Helper method for testing a constraints object that represents a gap.
     *
     * @param cc an array with all constraints
     * @param index the index to test
     * @param gap a flag whether the gap should be there or not
     */
    private static void checkGap(CellConstraints[] cc, int index, boolean gap)
    {
        assertEquals("Wrong cell size", CellSize.NONE, cc[index].getCellSize());
        NumberWithUnit expected = gap ? GAP : NumberWithUnit.ZERO;
        assertEquals("Wrong gap size", expected, cc[index].getMinSize());
    }

    /**
     * Tests whether the correct gaps are set in the layout.
     *
     * @param north flag if the north gap should be present
     * @param west flag if the west gap should be present
     * @param south flag if the south gap should be present
     * @param east flag if the east gap should be present
     */
    private void checkGaps(boolean north, boolean west, boolean south,
            boolean east)
    {
        CellConstraints[] cc = layout.getAllColumnConstraints();
        checkGap(cc, 2, west);
        checkGap(cc, 4, east);
        cc = layout.getAllRowConstraints();
        checkGap(cc, 2, north);
        checkGap(cc, 4, south);
    }

    /**
     * Initializes the layout with margins.
     *
     * @param gap flag whether gaps should be set
     */
    private void initLayout(boolean gap)
    {
        layout.setCanShrink(false);
        layout.setBottomMargin(BOTTOM);
        layout.setTopMargin(TOP);
        layout.setLeftMargin(LEFT);
        layout.setRightMargin(RIGHT);
        if (gap)
        {
            layout.setNorthGap(GAP);
            layout.setSouthGap(GAP);
            layout.setWestGap(GAP);
            layout.setEastGap(GAP);
        }
    }

    /**
     * Tests the properties of a newly created instance.
     */
    @Test
    public void testInitialize()
    {
        assertEquals("Wrong left margin", NumberWithUnit.ZERO, layout
                .getLeftMargin());
        assertEquals("Wrong top margin", NumberWithUnit.ZERO, layout
                .getTopMargin());
        assertEquals("Wrong right margin", NumberWithUnit.ZERO, layout
                .getRightMargin());
        assertEquals("Wrong bottom margin", NumberWithUnit.ZERO, layout
                .getBottomMargin());
        assertEquals("Wrong north gap", NumberWithUnit.ZERO, layout
                .getNorthGap());
        assertEquals("Wrong west gap", NumberWithUnit.ZERO, layout.getWestGap());
        assertEquals("Wrong east gap", NumberWithUnit.ZERO, layout.getEastGap());
        assertEquals("Wrong south gap", NumberWithUnit.ZERO, layout
                .getSouthGap());
    }

    /**
     * Tests the layout size if only a center component is set.
     */
    @Test
    public void testLayoutSizeCenter()
    {
        final int prefWidth = 50;
        final int prefHeight = 16;
        adapter.createComponent(null, prefWidth, prefHeight,
                BorderLayout.CENTER);
        initLayout(true);
        Dimension size = layout.calcMinimumLayoutSize(this);
        assertEquals("Wrong minimum width", prefWidth + SZ_LEFT + SZ_RIGHT,
                size.width);
        assertEquals("Wrong minimum height", prefHeight + SZ_TOP + SZ_BOTTOM,
                size.height);
    }

    /**
     * Helper method for calculating the sum of an array.
     *
     * @param ar the array
     * @return the sum of this array
     */
    private static int sum(int[] ar)
    {
        int sum = 0;
        for (int value : ar)
        {
            sum += value;
        }
        return sum;
    }

    /**
     * Helper method for testing the layout calculation of a fully initialized
     * layout.
     *
     * @param gap flag whether gaps should be used
     */
    private void checkLayoutSizeFull(boolean gap)
    {
        initLayout(gap);
        int[] widths = {
                50, 100, 33
        };
        int[] heights = {
                10, 20, 8
        };
        adapter.createComponent(null, widths[0], heights[0], BorderLayout.WEST);
        adapter
                .createComponent(null, widths[0], heights[0],
                        BorderLayout.NORTH);
        adapter
                .createComponent(null, widths[2], heights[2],
                        BorderLayout.SOUTH);
        adapter.createComponent(null, widths[2], heights[2], BorderLayout.EAST);
        adapter.createComponent(null, widths[1], heights[1],
                BorderLayout.CENTER);
        Dimension size = layout.calcMinimumLayoutSize(this);
        int width = SZ_LEFT + SZ_RIGHT + sum(widths);
        int height = SZ_TOP + SZ_BOTTOM + sum(heights);
        if (gap)
        {
            width += 2 * SZ_GAP;
            height += 2 * SZ_GAP;
        }
        assertEquals("Wrong width", width, size.width);
        assertEquals("Wrong height", height, size.height);
    }

    /**
     * Tests the size calculation of a fully occupied layout.
     */
    @Test
    public void testLayoutSizeFull()
    {
        checkLayoutSizeFull(false);
    }

    /**
     * Tests the size calculation of a fully occupied layout if gaps are used.
     */
    @Test
    public void testLayoutSizeFullGap()
    {
        checkLayoutSizeFull(true);
    }

    /**
     * Tests a constraint that does not specify a valid position in the layout.
     * This should cause an exception.
     */
    @Test(expected = IllegalStateException.class)
    public void testInvalidConstraintsString()
    {
        adapter.createComponent(null, 10, 20, "Not a valid position");
        layout.getAllColumnConstraints();
    }

    /**
     * Tests a null constraint. This should cause an exception.
     */
    @Test(expected = IllegalStateException.class)
    public void testInvalidConstraintsNull()
    {
        adapter.createComponent(null, 10, 20, null);
        layout.getAllColumnConstraints();
    }

    /**
     * Tests the constraints created for a row if there are no east and west
     * components.
     */
    @Test
    public void testConstraintsHorizontalSingle()
    {
        initLayout(true);
        adapter.createComponent(null, SZ_LEFT, SZ_TOP, BorderLayout.NORTH);
        checkMargins();
        checkGaps(true, false, false, false);
        PercentData pd = layout.getPercentData(1, 1);
        assertEquals("Wrong target column", 3, pd.getTargetColumn());
        assertEquals("Wrong x span", 5, pd.getSpanX());
        assertEquals("Wrong y span", 1, pd.getSpanY());
    }

    /**
     * Tests the constraints created for a row if the west component is present.
     */
    @Test
    public void testConstraintsHorizontalWest()
    {
        initLayout(true);
        adapter.createComponent(null, SZ_LEFT, SZ_TOP, BorderLayout.WEST);
        adapter.createComponent(null, SZ_LEFT, SZ_TOP, BorderLayout.NORTH);
        checkMargins();
        checkGaps(true, true, false, false);
        PercentData pd = layout.getPercentData(3, 1);
        assertEquals("Wrong target column", 3, pd.getTargetColumn());
        assertEquals("Wrong x span", 3, pd.getSpanX());
        assertEquals("Wrong y span", 1, pd.getSpanY());
    }

    /**
     * Tests the constraints created for a row if the east component is present.
     */
    @Test
    public void testConstraintsHorizontalEast()
    {
        initLayout(true);
        adapter.createComponent(null, SZ_LEFT, SZ_TOP, BorderLayout.EAST);
        adapter.createComponent(null, SZ_LEFT, SZ_TOP, BorderLayout.NORTH);
        checkMargins();
        checkGaps(true, false, false, true);
        PercentData pd = layout.getPercentData(1, 1);
        assertEquals("Wrong target column", 3, pd.getTargetColumn());
        assertEquals("Wrong x span", 3, pd.getSpanX());
        assertEquals("Wrong y span", 1, pd.getSpanY());
    }

    /**
     * Tests the constraints of a row if both the west and the east component
     * are present.
     */
    @Test
    public void testConstraintsHorizontalWestEast()
    {
        initLayout(true);
        adapter.createComponent(null, SZ_LEFT, SZ_TOP, BorderLayout.EAST);
        adapter.createComponent(null, SZ_LEFT, SZ_TOP, BorderLayout.WEST);
        adapter.createComponent(null, SZ_LEFT, SZ_TOP, BorderLayout.NORTH);
        checkMargins();
        checkGaps(true, true, false, true);
        PercentData pd = layout.getPercentData(3, 1);
        assertEquals("Wrong target column", 3, pd.getTargetColumn());
        assertEquals("Wrong x span", 1, pd.getSpanX());
        assertEquals("Wrong y span", 1, pd.getSpanY());
    }

    /**
     * Tests the constraints for a column if there are not north or south
     * components.
     */
    @Test
    public void testConstraintsVerticalSingle()
    {
        initLayout(true);
        adapter.createComponent(null, SZ_LEFT, SZ_TOP, BorderLayout.WEST);
        checkMargins();
        checkGaps(false, true, false, false);
        PercentData pd = layout.getPercentData(1, 1);
        assertEquals("Wrong target row", 3, pd.getTargetRow());
        assertEquals("Wrong y span", 5, pd.getSpanY());
        assertEquals("Wrong x span", 1, pd.getSpanX());
    }

    /**
     * Tests the constraints for a column if the north component is present.
     */
    @Test
    public void testConstraintsVerticalNorth()
    {
        initLayout(true);
        adapter.createComponent(null, SZ_LEFT, SZ_TOP, BorderLayout.NORTH);
        adapter.createComponent(null, SZ_LEFT, SZ_TOP, BorderLayout.WEST);
        checkMargins();
        checkGaps(true, true, false, false);
        PercentData pd = layout.getPercentData(1, 3);
        assertEquals("Wrong target row", 3, pd.getTargetRow());
        assertEquals("Wrong y span", 3, pd.getSpanY());
        assertEquals("Wrong x span", 1, pd.getSpanX());
    }

    /**
     * Tests the constraints for a column if the south component is present.
     */
    @Test
    public void testConstraintsVerticalSouth()
    {
        initLayout(true);
        adapter.createComponent(null, SZ_LEFT, SZ_TOP, BorderLayout.SOUTH);
        adapter.createComponent(null, SZ_LEFT, SZ_TOP, BorderLayout.WEST);
        checkMargins();
        checkGaps(false, true, true, false);
        PercentData pd = layout.getPercentData(1, 1);
        assertEquals("Wrong target row", 3, pd.getTargetRow());
        assertEquals("Wrong y span", 3, pd.getSpanY());
        assertEquals("Wrong x span", 1, pd.getSpanX());
    }

    /**
     * Tests the constraints for a column if both the north and south components
     * are present.
     */
    @Test
    public void testConstraintsVerticalNorthSouth()
    {
        initLayout(true);
        adapter.createComponent(null, SZ_LEFT, SZ_TOP, BorderLayout.NORTH);
        adapter.createComponent(null, SZ_LEFT, SZ_TOP, BorderLayout.SOUTH);
        adapter.createComponent(null, SZ_LEFT, SZ_TOP, BorderLayout.WEST);
        checkMargins();
        checkGaps(true, true, true, false);
        PercentData pd = layout.getPercentData(1, 3);
        assertEquals("Wrong target row", 3, pd.getTargetRow());
        assertEquals("Wrong y span", 1, pd.getSpanY());
        assertEquals("Wrong x span", 1, pd.getSpanX());
    }

    /**
     * Tests the constraints of the center cell if there are no other
     * components.
     */
    @Test
    public void testConstraintsCenterSingle()
    {
        initLayout(true);
        adapter.createComponent(null, SZ_LEFT, SZ_TOP, BorderLayout.CENTER);
        checkMargins();
        checkGaps(false, false, false, false);
        PercentData pd = layout.getPercentData(1, 1);
        assertEquals("Wrong x span", 5, pd.getSpanX());
        assertEquals("Wrong y span", 5, pd.getSpanY());
        assertEquals("Wrong cell alignment x", CellAlignment.FULL, layout
                .getColumnConstraints(3).getAlignment());
        assertEquals("Wrong cell alignment y", CellAlignment.FULL, layout
                .getRowConstraints(3).getAlignment());
    }

    /**
     * Tests the constraints of the center cell if the west and the south
     * components are present.
     */
    @Test
    public void testConstraintsCenterWestSouth()
    {
        initLayout(true);
        adapter.createComponent(null, SZ_LEFT, SZ_TOP, BorderLayout.WEST);
        adapter.createComponent(null, SZ_LEFT, SZ_TOP, BorderLayout.SOUTH);
        adapter.createComponent(null, SZ_LEFT, SZ_TOP, BorderLayout.CENTER);
        checkMargins();
        checkGaps(false, true, true, false);
        PercentData pd = layout.getPercentData(3, 1);
        assertEquals("Wrong x span", 3, pd.getSpanX());
        assertEquals("Wrong y span", 3, pd.getSpanY());
    }

    /**
     * Tests the constraints of the center cell if the layout is fully
     * populated.
     */
    @Test
    public void testConstraintsCenterFullLayout()
    {
        initLayout(true);
        adapter.createComponent(null, SZ_LEFT, SZ_TOP, BorderLayout.WEST);
        adapter.createComponent(null, SZ_LEFT, SZ_TOP, BorderLayout.SOUTH);
        adapter.createComponent(null, SZ_LEFT, SZ_TOP, BorderLayout.CENTER);
        adapter.createComponent(null, SZ_LEFT, SZ_TOP, BorderLayout.NORTH);
        adapter.createComponent(null, SZ_LEFT, SZ_TOP, BorderLayout.EAST);
        checkMargins();
        checkGaps(true, true, true, true);
        PercentData pd = layout.getPercentData(3, 3);
        assertEquals("Wrong x span", 1, pd.getSpanX());
        assertEquals("Wrong y span", 1, pd.getSpanY());
    }

    /**
     * Tests whether the layout can be serialized.
     */
    @Test
    public void testSerialization() throws IOException
    {
        initLayout(true);
        BorderLayout layout2 = JGuiraffeTestHelper.serialize(layout);
        TestPercentLayout.compareLayouts(layout, layout2);
        assertEquals("Wrong left margin", layout.getLeftMargin(), layout2
                .getLeftMargin());
        assertEquals("Wrong right margin", layout.getRightMargin(), layout2
                .getRightMargin());
        assertEquals("Wrong top margin", layout.getTopMargin(), layout2
                .getTopMargin());
        assertEquals("Wrong bottom margin", layout.getBottomMargin(), layout2
                .getBottomMargin());
        assertEquals("Wrong north gap", layout.getNorthGap(), layout2
                .getNorthGap());
        assertEquals("Wrong west gap", layout.getWestGap(), layout2
                .getWestGap());
        assertEquals("Wrong south gap", layout.getSouthGap(), layout2
                .getSouthGap());
        assertEquals("Wrong east gap", layout.getEastGap(), layout2
                .getEastGap());
    }
}
