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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sf.jguiraffe.JGuiraffeTestHelper;

import org.apache.commons.lang.mutable.MutableInt;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for PercentLayout.
 *
 * @author Oliver Heger
 * @version $Id: TestPercentLayout.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestPercentLayout
{
    /** Constant for the maximum width of a column. */
    private static final int MAX_WIDTH = 120;

    /** Constant for the default width of a column. */
    private static final int DEF_WIDTH = 80;

    /** Constant for the default height of a row. */
    private static final int DEF_HEIGHT = 40;

    /** An array with the preferred column sizes of the test layout. */
    private static final int[] COL_SIZES = {
            MAX_WIDTH, 10, 50, 20, DEF_WIDTH, 10, 60
    };

    /** An array with the preferred row heights of the test layout. */
    private static final int[] ROW_SIZES = {
            30, DEF_HEIGHT, 20, DEF_HEIGHT + 10, 20, DEF_HEIGHT, 30
    };

    /** An array with enlarged cell sizes. */
    private static final int[] ENLARGED_SIZES = {
            ROW_SIZES[0] + 30, ROW_SIZES[1], ROW_SIZES[2] + 20, ROW_SIZES[3],
            ROW_SIZES[4] + 20, ROW_SIZES[5], ROW_SIZES[6] + 30
    };

    /** Constant for the enlarged container size. */
    private static final int ENLARGED_CONTAINER_SIZE = sum(ROW_SIZES) + 100;

    /** Constant for the name of the multi-span component. */
    private static final String COMP_MULTI = "multiSpanComponent";

    /** Constant for the name of the component used for alignment tests. */
    private static final String COMP_ALIGN = "alignmentComponent";

    /** Constant for the X start position used by alignment tests. */
    private static final int AL_STARTX = 12;

    /** Constant for the Y start position used by alignment tests. */
    private static final int AL_STARTY = 8;

    /** Constant for the cell width used by alignment tests. */
    private static final int AL_WIDTH = 200;

    /** Constant for the cell height used by alignment tests. */
    private static final int AL_HEIGHT = 150;

    /** Constant for the component width of the alignment component. */
    private static final int AL_COMPWIDTH = 150;

    /** Constant for the component height of the alignment component. */
    private static final int AL_COMPHEIGHT = 100;

    /** Constant for the column index used by alignment tests. */
    private static final int AL_X = 0;

    /** Constant for the row index used by alignment tests. */
    private static final int AL_Y = 1;

    /** Constant for the column of the multi-span component. */
    private static final int MULTI_X = 2;

    /** Constant for the row of the multi-span component. */
    private static final int MULTI_Y = 5;

    /** The builder for creating cell constraints. */
    private CellConstraints.Builder builder;

    @Before
    public void setUp() throws Exception
    {
        builder = new CellConstraints.Builder();
    }

    /**
     * Copies the specified array.
     *
     * @param src the array to copy
     * @return the copy
     */
    private static int[] copy(int[] src)
    {
        return src.clone();
    }

    /**
     * Helper method for testing if the layout is empty, i.e. does not contain
     * any components.
     *
     * @param layout the layout to test
     */
    private static void checkEmpty(PercentLayout layout)
    {
        for (int col = 0; col < layout.getColumnCount(); col++)
        {
            for (int row = 0; row < layout.getRowCount(); row++)
            {
                assertNull("Got a component", layout.getComponent(col, row));
                assertNull("Got constraints", layout.getPercentData(col, row));
            }
        }
    }

    /**
     * Creates constraints for the columns of a test layout.
     *
     * @return the collection with the constraints
     */
    private Collection<CellConstraints> createColumnConstraints()
    {
        Collection<CellConstraints> columns = new ArrayList<CellConstraints>();
        columns.add(builder.withMinimumSize(new NumberWithUnit(10)).create());
        columns.add(builder.withCellAlignment(CellAlignment.END).withCellSize(
                CellSize.MINIMUM).create());
        columns.add(builder.withMinimumSize(new NumberWithUnit(10)).create());
        columns.add(builder.withCellSize(CellSize.PREFERRED).withWeight(100)
                .create());
        return columns;
    }

    /**
     * Creates constraints for the rows of a test layout.
     *
     * @return the collection with the constraints
     */
    private Collection<CellConstraints> createRowConstraints()
    {
        Collection<CellConstraints> rows = new ArrayList<CellConstraints>();
        rows.add(builder.withMinimumSize(new NumberWithUnit(10)).create());
        rows.add(builder.defaultRow().create());
        return rows;
    }

    /**
     * Checks whether the given layout has the expected column constraints.
     *
     * @param layout the layout
     * @param constr the expected column constraints
     */
    private static void checkColConstraints(PercentLayout layout,
            Collection<CellConstraints> constr)
    {
        assertEquals("Wrong number of columns", constr.size(), layout
                .getColumnCount());
        int index = 0;
        for (CellConstraints cc : constr)
        {
            assertEquals("Wrong constraints at " + index, cc, layout
                    .getColumnConstraints(index));
            index++;
        }
    }

    /**
     * Checks whether the given layout has the expected row constraints.
     *
     * @param layout the layout
     * @param constr the expected row constraints
     */
    private static void checkRowConstraints(PercentLayout layout,
            Collection<CellConstraints> constr)
    {
        assertEquals("Wrong number of rows", constr.size(), layout
                .getRowCount());
        int index = 0;
        for (CellConstraints cc : constr)
        {
            assertEquals("Wrong constraints at " + index, cc, layout
                    .getRowConstraints(index));
            index++;
        }
    }

    /**
     * Helper method for setting the constraints for the test layout. The layout
     * consists of two horizontal sections each consisting of a label column and
     * a field column. Between these sections there is a column with a fix width
     * as gap. There are some rows with default heights and gap rows between
     * them.
     *
     * @param minimum a flag whether the cell size should be set to minimum or
     *        preferred
     * @param groups a flag whether cell groups should be applied
     * @return the test layout object
     */
    private PercentLayout setupTestLayout(boolean minimum, boolean groups)
    {
        PercentLayout layout = new PercentLayout(7, 7);
        initTestLayout(layout, minimum, groups);
        return layout;
    }

    /**
     * Initializes the constraints for the test layout.
     *
     * @param layout the test layout
     * @param minimum a flag whether the cell size should be set to minimum or
     *        preferred
     * @param groups a flag whether cell groups should be applied
     */
    private void initTestLayout(PercentLayout layout, boolean minimum,
            boolean groups)
    {
        CellSize csize = minimum ? CellSize.MINIMUM : CellSize.PREFERRED;
        layout.setColumnConstraints(0, builder.withCellAlignment(
                CellAlignment.END).withCellSize(csize).create());
        layout.setColumnConstraints(1, builder.withMinimumSize(
                new NumberWithUnit(COL_SIZES[1])).create());
        layout.setColumnConstraints(2, builder.withCellSize(csize).withWeight(
                50).create());
        layout.setColumnConstraints(3, builder.withMinimumSize(
                new NumberWithUnit(COL_SIZES[3])).create());
        layout.setColumnConstraints(4, builder.withCellAlignment(
                CellAlignment.END).withCellSize(csize).create());
        layout.setColumnConstraints(5, builder.withMinimumSize(
                new NumberWithUnit(COL_SIZES[5])).create());
        layout.setColumnConstraints(6, builder.withCellSize(csize).withWeight(
                50).create());

        layout.setRowConstraints(0, builder
                .withCellAlignment(CellAlignment.END).withMinimumSize(
                        new NumberWithUnit(ROW_SIZES[0])).withWeight(30)
                .create());
        layout.setRowConstraints(1, builder.defaultRow().withCellSize(csize)
                .create());
        layout.setRowConstraints(2, builder
                .withCellAlignment(CellAlignment.END).withMinimumSize(
                        new NumberWithUnit(ROW_SIZES[2])).withWeight(20)
                .create());
        layout.setRowConstraints(3, builder.defaultRow().withCellSize(csize)
                .create());
        layout.setRowConstraints(4, builder
                .withCellAlignment(CellAlignment.END).withMinimumSize(
                        new NumberWithUnit(ROW_SIZES[4])).withWeight(20)
                .create());
        layout.setRowConstraints(5, builder.withCellAlignment(
                CellAlignment.CENTER).withCellSize(csize).withMinimumSize(
                new NumberWithUnit(ROW_SIZES[5])).create());
        layout.setRowConstraints(6, builder
                .withCellAlignment(CellAlignment.END).withMinimumSize(
                        new NumberWithUnit(ROW_SIZES[6])).withWeight(30)
                .create());

        if (groups)
        {
            layout.addColumnGroup(new CellGroup(0, 4));
            layout.addRowGroup(new CellGroup(5, 3));
        }
        layout.setPlatformAdapter(new PercentLayoutPlatformAdapterImpl());
    }

    /**
     * Helper method for adding components to the test layout.
     *
     * @param layout the test layout
     */
    private void addComponents(PercentLayout layout)
    {
        PercentLayoutPlatformAdapterImpl adapter = (PercentLayoutPlatformAdapterImpl) layout
                .getPlatformAdapter();
        PercentData.Builder pcb = new PercentData.Builder();
        adapter.createComponent(COMP_ALIGN, COL_SIZES[0], ROW_SIZES[1] - 8, pcb
                .pos(0, 1));
        adapter.createComponent(null, COL_SIZES[2] - 10, ROW_SIZES[1], pcb.pos(
                2, 1));
        adapter.createComponent(null, COL_SIZES[4] - 20, ROW_SIZES[1], pcb.pos(
                4, 1));
        adapter.createComponent(null, COL_SIZES[6], ROW_SIZES[1] - 4, pcb.pos(
                6, 1));

        adapter.createComponent(null, COL_SIZES[0] - 20, ROW_SIZES[3] - 8, pcb
                .pos(0, 3));
        adapter.createComponent(null, COL_SIZES[2], ROW_SIZES[3] - 4, pcb.pos(
                2, 3));
        adapter.createComponent(null, COL_SIZES[4], ROW_SIZES[3] - 2, pcb.pos(
                4, 3));
        adapter.createComponent(null, COL_SIZES[6] - 10, ROW_SIZES[3], pcb.pos(
                6, 3));

        adapter.createComponent(null, DEF_WIDTH, DEF_HEIGHT, pcb.pos(0, 5));
        adapter.createComponent(COMP_MULTI, COL_SIZES[2] + COL_SIZES[3],
                ROW_SIZES[5], pcb.xy(MULTI_X, MULTI_Y).spanX(4).create());
    }

    /**
     * Tests the constructor that takes the dimensions of the layout.
     */
    @Test
    public void testInitByDimensions()
    {
        final int cols = 12;
        final int rows = 9;
        PercentLayout layout = new PercentLayout(cols, rows);
        layout.setPlatformAdapter(new PercentLayoutPlatformAdapterImpl());
        assertEquals("Wrong number of columns", cols, layout.getColumnCount());
        assertEquals("Wrong number of rows", rows, layout.getRowCount());

        CellConstraints defCol = builder.defaultColumn().create();
        for (int i = 0; i < layout.getColumnCount(); i++)
        {
            assertEquals("No default column constraint at " + i, defCol, layout
                    .getColumnConstraints(i));
        }
        CellConstraints defRow = builder.defaultRow().create();
        for (int i = 0; i < layout.getRowCount(); i++)
        {
            assertEquals("No default row constraint at " + i, defRow, layout
                    .getRowConstraints(i));
        }
        checkEmpty(layout);

        assertTrue("Got column groups", layout.getColumnGroups().isEmpty());
        assertTrue("Got row groups", layout.getRowGroups().isEmpty());
    }

    /**
     * Tries to create a layout with an invalid number of columns. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitByDimensionsInvalidCols()
    {
        new PercentLayout(0, 5);
    }

    /**
     * Tries to create a layout with an invalid number of rows. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitByDimensionsInvalidRows()
    {
        new PercentLayout(12, -1);
    }

    /**
     * Tests initialization using the constructor that takes two collections.
     */
    @Test
    public void testInitCollections()
    {
        Collection<CellConstraints> columns = createColumnConstraints();
        Collection<CellConstraints> rows = createRowConstraints();
        PercentLayout layout = new PercentLayout(columns, rows);
        layout.setPlatformAdapter(new PercentLayoutPlatformAdapterImpl());
        checkColConstraints(layout, columns);
        checkRowConstraints(layout, rows);
        checkEmpty(layout);
    }

    /**
     * Tries to initialize a layout with a null columns collection. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitCollectionsNullColumns()
    {
        new PercentLayout(null, createRowConstraints());
    }

    /**
     * Tries to initialize a layout with a null rows collection. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitCollectionsNullRows()
    {
        new PercentLayout(createColumnConstraints(), null);
    }

    /**
     * Tries to initialize a layout with a collection that contains a null
     * entry. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitCollectionsNullValue()
    {
        Collection<CellConstraints> ccCols = createColumnConstraints();
        ccCols.add(null);
        new PercentLayout(ccCols, createRowConstraints());
    }

    /**
     * Tries to initialize a layout with an empty columns collection. This
     * should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitCollectionsEmptyColumns()
    {
        new PercentLayout(new ArrayList<CellConstraints>(),
                createRowConstraints());
    }

    /**
     * Tries to initialize a layout with an empty rows collection. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitCollectionsEmptyRows()
    {
        new PercentLayout(createColumnConstraints(),
                new ArrayList<CellConstraints>());
    }

    /**
     * Tests whether a defensive copy of the collections is made when creating
     * an instance.
     */
    @Test
    public void testInitCollectionsModify()
    {
        Collection<CellConstraints> ccCols = createColumnConstraints();
        Collection<CellConstraints> ccRows = createRowConstraints();
        Collection<CellConstraints> ccColsInit = new ArrayList<CellConstraints>(
                ccCols);
        Collection<CellConstraints> ccRowsInit = new ArrayList<CellConstraints>(
                ccRows);
        PercentLayout layout = new PercentLayout(ccColsInit, ccRowsInit);
        layout.setPlatformAdapter(new PercentLayoutPlatformAdapterImpl());
        ccColsInit.add(builder.defaultColumn().create());
        ccRowsInit.clear();
        checkColConstraints(layout, ccCols);
        checkRowConstraints(layout, ccRows);
    }

    /**
     * Transforms a collection with cell constraints into a string
     * representation.
     *
     * @param col the collection with constraints
     * @param seperator the separator to be used
     * @return the string
     */
    private static String createConstrString(Collection<CellConstraints> col,
            String seperator)
    {
        StringBuilder buf = new StringBuilder();
        boolean first = true;
        for (CellConstraints cc : col)
        {
            if (first)
            {
                first = false;
            }
            else
            {
                buf.append(seperator);
            }
            buf.append(cc.toSpecificationString());
        }
        return buf.toString();
    }

    /**
     * Tests initialization using strings.
     */
    @Test
    public void testInitStrings()
    {
        Collection<CellConstraints> ccCols = createColumnConstraints();
        Collection<CellConstraints> ccRows = createRowConstraints();
        String sCols = createConstrString(ccCols, ", ");
        String sRows = createConstrString(ccRows, ";");
        PercentLayout layout = new PercentLayout(sCols, sRows);
        layout.setPlatformAdapter(new PercentLayoutPlatformAdapterImpl());
        checkColConstraints(layout, ccCols);
        checkRowConstraints(layout, ccRows);
        checkEmpty(layout);
    }

    /**
     * Tries to create a layout with a null string for the columns. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitStringsNullColumns()
    {
        new PercentLayout(null, createConstrString(createRowConstraints(), ","));
    }

    /**
     * Tries to create a layout with an undefined string for the rows. This
     * should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitStringsUndefinedRows()
    {
        new PercentLayout(createConstrString(createColumnConstraints(), ";"),
                "");
    }

    /**
     * Tries to pass an invalid string specification to the layout. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitStringInvalid()
    {
        new PercentLayout("10, preferred, 10, minimum(10), END/preferred/10",
                "10, END/PREFERRED, 10dlu, invalid");
    }

    /**
     * Tries to obtain the platform adapter if it has not yet been set. This
     * should cause an exception.
     */
    @Test(expected = IllegalStateException.class)
    public void testFetchPlatformAdapterUndefined()
    {
        PercentLayout layout = new PercentLayout(5, 2);
        layout.fetchPlatformAdapter();
    }

    /**
     * Checks the calculation of the cell sizes in the given layout.
     *
     * @param layout the test layout
     * @param expected the expected results
     * @param minimum a flag whether the minimum size is to be applied
     * @param vert the vertical flag
     * @param groups a flag whether the cell groups should be taken into account
     */
    private void checkCellSizes(PercentLayout layout, int[] expected,
            boolean minimum, boolean vert, boolean groups)
    {
        int count = vert ? layout.getRowCount() : layout.getColumnCount();
        CellConstraints[] constr = new CellConstraints[count];
        for (int i = 0; i < count; i++)
        {
            constr[i] = vert ? layout.getRowConstraints(i) : layout
                    .getColumnConstraints(i);
        }
        int[] sizes = layout.calcCellSizes(constr, vert ? layout
                .getColumnCount() : layout.getRowCount(), this, minimum, vert);
        if (groups)
        {
            layout.applyCellGroups(sizes, vert ? layout.getRowGroups() : layout
                    .getColumnGroups());
        }
        assertTrue("Wrong sizes: " + Arrays.toString(expected) + " != "
                + Arrays.toString(sizes), Arrays.equals(expected, sizes));
    }

    /**
     * Helper method for checking the calculation of the cell sizes. This method
     * creates the standard test layout.
     *
     * @param expected the expected results
     * @param minimum a flag whether the minimum size is to be applied
     * @param vert the vertical flag
     * @param groups a flag whether the cell groups should be taken into account
     */
    private void checkCellSizes(int[] expected, boolean minimum, boolean vert,
            boolean groups)
    {
        PercentLayout layout = setupTestLayout(minimum, groups);
        addComponents(layout);
        checkCellSizes(layout, expected, minimum, vert, groups);
    }

    /**
     * Tests calculation of the column sizes if the preferred width is used.
     */
    @Test
    public void testCalcCellSizesColumnsPreferred()
    {
        checkCellSizes(COL_SIZES, false, false, false);
    }

    /**
     * Tests calculation of the row sizes if the preferred height is used.
     */
    @Test
    public void testCalcCellSizesRowsPreferred()
    {
        checkCellSizes(ROW_SIZES, false, true, false);
    }

    /**
     * Transforms an array with preferred sizes to minimum sizes. Only every 2nd
     * cell is affected as all cells have a fix size.
     *
     * @param sizes the array with the preferred sizes
     * @param startIdx the start index for manipulating the sizes (0 for
     *        columns, 1 for rows)
     * @return the array with the minimum sizes
     */
    private static int[] minSizeArray(int[] sizes, int startIdx)
    {
        int[] mins = copy(sizes);
        for (int i = startIdx; i < sizes.length; i += 2)
        {
            mins[i] = sizes[i] / 2;
        }
        return mins;
    }

    /**
     * Returns expected row sizes if the minimum cell size is used. This is a
     * bit special as row 5 of the test layout has a minimum size, so its size
     * is not changed compared to the preferred height.
     *
     * @return the array with the minimum row heights
     */
    private static int[] minRowSizeArray()
    {
        int[] expected = minSizeArray(ROW_SIZES, 1);
        expected[5] = ROW_SIZES[5];
        return expected;
    }

    /**
     * Tests calculation of the column sizes if the minimum width is used.
     */
    @Test
    public void testCalcCellSizesColumnsMinimum()
    {
        checkCellSizes(minSizeArray(COL_SIZES, 0), true, false, false);
    }

    /**
     * Tests calculation of the row sizes if the minimum height is used.
     */
    @Test
    public void testCalcCellSizesRowsMinimum()
    {
        checkCellSizes(minRowSizeArray(), true, true, false);
    }

    /**
     * Returns an array with expected sizes that is aware of cell groups.
     *
     * @param sizes the original sizes
     * @param idx1 index1 of the cell group
     * @param idx2 index2 of the cell group
     * @return the resulting array
     */
    private static int[] groupSizeArray(int[] sizes, int idx1, int idx2)
    {
        int[] expected = copy(sizes);
        expected[idx1] = expected[idx2];
        return expected;
    }

    /**
     * Tests whether column groups are taken into account.
     */
    @Test
    public void testCalcCellSizesColumnsGroups()
    {
        checkCellSizes(groupSizeArray(COL_SIZES, 4, 0), false, false, true);
    }

    /**
     * Tests whether row groups are taken into account.
     */
    @Test
    public void testCalcCellSizesRowsGroups()
    {
        checkCellSizes(groupSizeArray(ROW_SIZES, 5, 3), false, true, true);
    }

    /**
     * Tests calcComponentSize() if the size is set to NONE.
     */
    @Test
    public void testCalcComponentSizeSizeNone()
    {
        final int minSize = 12;
        PercentData.Builder pcb = new PercentData.Builder();
        PercentData pd = pcb.xy(1, 2).withColumnConstraints(
                builder.withCellSize(CellSize.NONE).withMinimumSize(
                        new NumberWithUnit(minSize)).create()).create();
        PercentLayout layout = setupTestLayout(false, false);
        assertEquals("Wrong component size", minSize, layout.calcComponentSize(
                pd, this, this, false, false));
    }

    /**
     * Tests a multi span component that requires enlargement of cells.
     */
    @Test
    public void testCalcCellSizesMultiSpanEnlarge()
    {
        final int startIdx = 2;
        final int endIdx = 5;
        final int delta = 25;
        final int part = delta / 2;
        int space = 0;
        for (int i = startIdx; i <= endIdx; i++)
        {
            space += COL_SIZES[i];
        }
        PercentLayout layout = setupTestLayout(false, false);
        addComponents(layout);
        PercentLayoutPlatformAdapterImpl adapter = (PercentLayoutPlatformAdapterImpl) layout
                .getPlatformAdapter();
        PercentLayoutPlatformAdapterImpl.Component comp = adapter
                .getComponentByName(COMP_MULTI);
        comp.preferredWidth = space + delta;
        int[] expected = copy(COL_SIZES);
        expected[2] += part + 1;
        expected[4] += part;
        checkCellSizes(layout, expected, false, false, false);
    }

    /**
     * Tests a multi-span component that cannot be enlarged due to constraints.
     */
    @Test
    public void testHandleMultiSpanEnlargeNotPossible()
    {
        final int span = 3;
        final int size = 10;
        int[] sizes = new int[span];
        CellConstraints[] cc = new CellConstraints[span];
        Arrays.fill(sizes, size);
        Arrays.fill(cc, builder.withCellSize(CellSize.NONE).create());
        PercentData pd = new PercentData.Builder().xy(0, 0).spanX(span)
                .withColumnConstraints(
                        builder.withCellSize(CellSize.PREFERRED).create())
                .create();
        PercentLayoutPlatformAdapterImpl.Component comp = new PercentLayoutPlatformAdapterImpl.Component();
        comp.preferredWidth = span * size + 100;
        comp.preferredHeight = 20;
        PercentLayoutBase.CellData cd = new PercentLayoutBase.CellData(comp, pd);
        int[] expectedSizes = sizes.clone();
        PercentLayout layout = setupTestLayout(false, false);
        layout.handleMultiSpans(sizes, cc, Collections.singletonList(cd), this,
                false, false);
        assertTrue("Sizes were changed", Arrays.equals(expectedSizes, sizes));
    }

    /**
     * Helper method for calculating the sum of an array with sizes.
     *
     * @param sizes the sizes
     * @return the sum
     */
    private static int sum(int[] sizes)
    {
        int sum = 0;
        for (int size : sizes)
        {
            sum += size;
        }
        return sum;
    }

    /**
     * Helper method for checking the calculation of the minimum or preferred
     * layout size.
     *
     * @param expectedColSizes the expected column sizes
     * @param expectedRowSizes the expected row sizes
     * @param minimum flag whether the minimum size is to be used for
     *        constraints
     * @param groups the groups flag
     * @param prefSize true for preferred size, false for minimum size
     * @param shrink the shrink flag
     */
    private void checkCalcMinimumLayoutSize(int[] expectedColSizes,
            int[] expectedRowSizes, boolean minimum, boolean groups,
            boolean prefSize, Boolean shrink)
    {
        PercentLayout layout = setupTestLayout(minimum, groups);
        if (shrink != null)
        {
            layout.setCanShrink(shrink.booleanValue());
        }
        addComponents(layout);
        Dimension d = prefSize ? layout.calcPreferredLayoutSize(this) : layout
                .calcMinimumLayoutSize(this);
        assertEquals("Wrong x size", sum(expectedColSizes), d.width);
        assertEquals("Wrong y size", sum(expectedRowSizes), d.height);
    }

    /**
     * Tests whether the preferred layout size is correctly calculated if the
     * preferred size is used in cell constraints.
     */
    @Test
    public void testCalcPreferredLayoutSizePreferred()
    {
        checkCalcMinimumLayoutSize(COL_SIZES, ROW_SIZES, false, false, true,
                null);
    }

    /**
     * Tests whether the preferred layout size is correctly calculated if the
     * minimum size is used in cell constraints.
     */
    @Test
    public void testCalcMinimumLayoutSizeMinimum()
    {
        checkCalcMinimumLayoutSize(minSizeArray(COL_SIZES, 0),
                minRowSizeArray(), true, false, true, null);
    }

    /**
     * Tests whether the preferred layout size can be determined if cell groups
     * are involved.
     */
    @Test
    public void testCalcPreferredLayoutSizeGroups()
    {
        checkCalcMinimumLayoutSize(groupSizeArray(COL_SIZES, 4, 0),
                groupSizeArray(ROW_SIZES, 5, 3), false, true, true, null);
    }

    /**
     * Tests whether the minimum layout size can be calculated if shrinking is
     * allowed.
     */
    @Test
    public void testCalcMinimumLayoutSizeCanShrink()
    {
        checkCalcMinimumLayoutSize(minSizeArray(COL_SIZES, 0),
                minRowSizeArray(), false, false, false, Boolean.TRUE);
    }

    /**
     * Tests the behavior of calcMinimumLayoutSize() if shrinking is not
     * allowed.
     */
    @Test
    public void testCalcMinimumLayoutSizeNoShrink()
    {
        checkCalcMinimumLayoutSize(COL_SIZES, ROW_SIZES, false, false, false,
                Boolean.FALSE);
    }

    /**
     * Tests whether the total weight factor X is correctly calculated.
     */
    @Test
    public void testGetTotalWeightX()
    {
        PercentLayout layout = setupTestLayout(false, false);
        assertEquals("Wrong weight factor", 100, layout.getTotalWeightX());
        assertEquals("Wrong weight factor 2", 100, layout.getTotalWeightX());
    }

    /**
     * Tests whether the total weight factor Y is correctly calculated.
     */
    @Test
    public void testGetTotalWeightY()
    {
        PercentLayout layout = setupTestLayout(false, false);
        assertEquals("Wrong weight factor", 100, layout.getTotalWeightY());
        assertEquals("Wrong weight factor 2", 100, layout.getTotalWeightY());
    }

    /**
     * Tests if the weight factors are correctly applied.
     */
    @Test
    public void testApplyWeightFactors()
    {
        PercentLayout layout = setupTestLayout(false, false);
        int[] sizes = copy(ROW_SIZES);
        layout.applyWeightFactors(sizes, ENLARGED_CONTAINER_SIZE, true);
        assertTrue("Wrong sizes: " + Arrays.toString(sizes), Arrays.equals(
                ENLARGED_SIZES, sizes));
    }

    /**
     * Tests the behavior of applyWeightFactors() if no weight factors have been
     * set.
     */
    @Test
    public void testApplyWeightFactorsNoWeight()
    {
        PercentLayout layout = setupTestLayout(false, false);
        final int count = 5;
        final int size = 10;
        final int containerSize = count * size + 100;
        int[] sizes = new int[count];
        Arrays.fill(sizes, size);
        int[] expectedSizes = sizes.clone();
        layout.applyWeightFactors(sizes, containerSize, null, 0);
        assertTrue("Sizes were changed", Arrays.equals(expectedSizes, sizes));
    }

    /**
     * Tests the behavior of applyWeightFactors() if there is no remaining
     * space.
     */
    @Test
    public void testApplyWeightFactorsNoRemaining()
    {
        PercentLayout layout = setupTestLayout(false, false);
        final int count = 5;
        final int size = 10;
        final int containerSize = count * size;
        int[] sizes = new int[count];
        Arrays.fill(sizes, size);
        int[] expectedSizes = sizes.clone();
        layout.applyWeightFactors(sizes, containerSize, true);
        assertTrue("Sizes were changed", Arrays.equals(expectedSizes, sizes));
    }

    /**
     * Tests whether the start positions of cells are correctly calculated.
     */
    @Test
    public void testCalcCellPositions()
    {
        int[] sizes = {
                1, 2, 3
        };
        final int startPos = 10;
        int[] expected = {
                10, 11, 13
        };
        PercentLayout layout = setupTestLayout(false, false);
        int[] pos = layout.calcCellPositions(sizes, startPos);
        assertTrue("Wrong start positions: " + Arrays.toString(pos), Arrays
                .equals(expected, pos));
    }

    /**
     * Tests whether the minimum component size is correctly obtained from the
     * platform adapter.
     */
    @Test
    public void testGetMinimumComponentSize()
    {
        PercentLayout layout = setupTestLayout(false, false);
        addComponents(layout);
        PercentLayoutPlatformAdapterImpl adapter = (PercentLayoutPlatformAdapterImpl) layout
                .getPlatformAdapter();
        PercentLayoutPlatformAdapterImpl.Component comp = adapter
                .getComponentByName(COMP_MULTI);
        assertEquals("Wrong width", comp.minWidth, layout
                .getMinimumComponentSize(MULTI_X, MULTI_Y, false));
        assertEquals("Wrong height", comp.minHeight, layout
                .getMinimumComponentSize(MULTI_X, MULTI_Y, true));
    }

    /**
     * Tests whether the preferred component size is correctly obtained from the
     * platform adapter.
     */
    @Test
    public void testGetPreferredComponentSize()
    {
        PercentLayout layout = setupTestLayout(false, false);
        addComponents(layout);
        PercentLayoutPlatformAdapterImpl adapter = (PercentLayoutPlatformAdapterImpl) layout
                .getPlatformAdapter();
        PercentLayoutPlatformAdapterImpl.Component comp = adapter
                .getComponentByName(COMP_MULTI);
        assertEquals("Wrong width", comp.preferredWidth, layout
                .getPreferredComponentSize(MULTI_X, MULTI_Y, false));
        assertEquals("Wrong height", comp.preferredHeight, layout
                .getPreferredComponentSize(MULTI_X, MULTI_Y, true));
    }

    /**
     * Helper method for testing the alignComponent() method using the specified
     * layout object.
     *
     * @param layout the test layout
     * @param align the alignment
     * @param x the expected x position
     * @param y the expected y position
     * @param w the expected width
     * @param h the expected height
     */
    private void checkAlignComponent(PercentLayout layout, CellAlignment align,
            int x, int y, int w, int h)
    {
        PercentLayoutPlatformAdapterImpl adapter = (PercentLayoutPlatformAdapterImpl) layout
                .getPlatformAdapter();
        PercentLayoutPlatformAdapterImpl.Component comp = adapter
                .getComponentByName(COMP_ALIGN);
        comp.preferredWidth = AL_COMPWIDTH;
        comp.preferredHeight = AL_COMPHEIGHT;
        layout.setColumnConstraints(AL_X, builder.defaultColumn()
                .withCellAlignment(align).create());
        layout.setRowConstraints(AL_Y, builder.defaultRow().withCellAlignment(
                align).create());
        int[] colSizes = copy(COL_SIZES);
        colSizes[AL_X] = AL_WIDTH;
        int[] rowSizes = copy(ROW_SIZES);
        rowSizes[AL_Y] = AL_HEIGHT;
        Rectangle rect = new Rectangle();
        layout.alignComponent(rect, AL_X, AL_Y, colSizes, AL_STARTX, AL_X,
                this, false);
        layout.alignComponent(rect, AL_X, AL_Y, rowSizes, AL_STARTY, AL_Y,
                this, true);
        assertEquals("Wrong x", x, rect.x);
        assertEquals("Wrong y", y, rect.y);
        assertEquals("Wrong w", w, rect.width);
        assertEquals("Wrong h", h, rect.height);
    }

    /**
     * Helper method for testing the alignComponent() method. Creates a default
     * test layout.
     *
     * @param align the alignment
     * @param x the expected x position
     * @param y the expected y position
     * @param w the expected width
     * @param h the expected height
     */
    private void checkAlignComponent(CellAlignment align, int x, int y, int w,
            int h)
    {
        PercentLayout layout = setupTestLayout(false, false);
        addComponents(layout);
        checkAlignComponent(layout, align, x, y, w, h);
    }

    /**
     * Tests alignment of a component if the alignment is FULL.
     */
    @Test
    public void testAlignComponentFull()
    {
        checkAlignComponent(CellAlignment.FULL, AL_STARTX, AL_STARTY, AL_WIDTH,
                AL_HEIGHT);
    }

    /**
     * Tests alignment of a component if the alignment is START.
     */
    @Test
    public void testAlignComponentStart()
    {
        checkAlignComponent(CellAlignment.START, AL_STARTX, AL_STARTY,
                AL_COMPWIDTH, AL_COMPHEIGHT);
    }

    /**
     * Tests alignment of a component if the alignment is END.
     */
    @Test
    public void testAlignComponentEnd()
    {
        checkAlignComponent(CellAlignment.END, AL_STARTX + AL_WIDTH
                - AL_COMPWIDTH, AL_STARTY + AL_HEIGHT - AL_COMPHEIGHT,
                AL_COMPWIDTH, AL_COMPHEIGHT);
    }

    /**
     * Tests alignment of a component if the alignment is CENTER.
     */
    @Test
    public void testAlignComponentCenter()
    {
        checkAlignComponent(CellAlignment.CENTER, AL_STARTX
                + (AL_WIDTH - AL_COMPWIDTH) / 2, AL_STARTY
                + (AL_HEIGHT - AL_COMPHEIGHT) / 2, AL_COMPWIDTH, AL_COMPHEIGHT);
    }

    /**
     * Tests the alignment of a component that spans multiple cells.
     */
    @Test
    public void testAlignComponentMultiSpan()
    {
        PercentLayout layout = setupTestLayout(false, false);
        addComponents(layout);
        PercentLayoutPlatformAdapterImpl adapter = (PercentLayoutPlatformAdapterImpl) layout
                .getPlatformAdapter();
        adapter.createComponent(COMP_ALIGN, AL_COMPWIDTH, AL_COMPHEIGHT,
                new PercentData.Builder().xy(AL_X, AL_Y).span(2, 2).create());
        checkAlignComponent(layout, CellAlignment.FULL, AL_STARTX, AL_STARTY,
                AL_WIDTH + COL_SIZES[AL_X + 1], AL_HEIGHT + ROW_SIZES[AL_Y + 1]);
    }

    /**
     * Tests calculating the complete layout. We only test whether
     * alignComponent() is called the expected number of times and whether the
     * components have been positioned.
     */
    @Test
    public void testPerformLayout()
    {
        final MutableInt alignCount = new MutableInt();
        final Object testContainer = new Object();
        @SuppressWarnings("serial")
        PercentLayout layout = new PercentLayout(7, 7)
        {
            /**
             * Records this invocation.
             */
            @Override
            protected void alignComponent(Rectangle bounds, int colIdx,
                    int rowIdx, int[] sizes, int startPos, int idx,
                    Object container, boolean vert)
            {
                assertEquals("Wrong container", testContainer, container);
                alignCount.increment();
                super.alignComponent(bounds, colIdx, rowIdx, sizes, startPos,
                        idx, testContainer, vert);
            }

            /**
             * Checks the arrays with sizes.
             */
            @Override
            public void performLayout(Object container, int[] colSizes,
                    int[] rowSizes, int[] colPos, int[] rowPos)
            {
                assertTrue("Wrong cell widths",
                        Arrays.equals(COL_SIZES, colSizes));
                assertTrue("Wrong cell heights",
                        Arrays.equals(ROW_SIZES, rowSizes));
                super.performLayout(container, colSizes, rowSizes, colPos,
                        rowPos);
            }
        };
        initTestLayout(layout, false, false);
        addComponents(layout);
        PercentLayoutPlatformAdapterImpl adapter =
                (PercentLayoutPlatformAdapterImpl) layout.getPlatformAdapter();
        Rectangle insets = new Rectangle(5, 6, 7, 8);
        Dimension size =
                new Dimension(sum(COL_SIZES) + 12, sum(ROW_SIZES) + 14);
        layout.performLayout(testContainer, insets, size);
        assertEquals("Wrong number of align invocations",
                2 * adapter.getComponentCount(), alignCount.intValue());
        for (int i = 0; i < adapter.getComponentCount(); i++)
        {
            PercentLayoutPlatformAdapterImpl.Component comp =
                    adapter.getComponent(i);
            assertTrue("Wrong bounds", comp.x >= AL_X && comp.y >= AL_Y
                    && comp.width > 0 && comp.height > 0);
        }
    }

    /**
     * Tests the calculation of the cell sizes if the container size matches the
     * preferred size.
     */
    @Test
    public void testCalcSizesExactSize()
    {
        PercentLayout layout = setupTestLayout(false, false);
        addComponents(layout);
        int[] sizes = layout.calcSizes(layout.getAllRowConstraints(), layout
                .getColumnCount(), layout.getRowGroups(), this, sum(ROW_SIZES),
                true);
        assertTrue("Wrong sizes", Arrays.equals(ROW_SIZES, sizes));
    }

    /**
     * Tests whether cell sizes are enlarged if more space becomes available.
     */
    @Test
    public void testCalcSizesAdditionalSpace()
    {
        PercentLayout layout = setupTestLayout(false, false);
        addComponents(layout);
        int[] sizes = layout.calcSizes(layout.getAllRowConstraints(), layout
                .getColumnCount(), layout.getRowGroups(), this,
                ENLARGED_CONTAINER_SIZE, true);
        assertTrue("Wrong sizes: " + Arrays.toString(sizes), Arrays.equals(
                ENLARGED_SIZES, sizes));
    }

    /**
     * Tests the calculation of cell sizes if not enough space is available, but
     * shrinking is disabled.
     */
    @Test
    public void testCalcSizesLessSpaceNoShrink()
    {
        PercentLayout layout = setupTestLayout(false, false);
        layout.setCanShrink(false);
        addComponents(layout);
        int[] sizes = layout.calcSizes(layout.getAllRowConstraints(), layout
                .getColumnCount(), layout.getRowGroups(), this,
                sum(ROW_SIZES) - 25, true);
        assertTrue("Wrong sizes: " + Arrays.toString(sizes), Arrays.equals(
                ROW_SIZES, sizes));
    }

    /**
     * Tests the calculation of cell sizes if not enough space is available and
     * shrinking is allowed.
     */
    @Test
    public void testCalcSizesLessSpaceShrink()
    {
        PercentLayout layout = setupTestLayout(false, false);
        addComponents(layout);
        int[] sizes = layout.calcSizes(layout.getAllRowConstraints(), layout
                .getColumnCount(), layout.getRowGroups(), this,
                sum(ROW_SIZES) - 35, true);
        int[] expected = minRowSizeArray();
        expected[0] += 3;
        expected[2] += 2;
        expected[4] += 2;
        expected[6] += 3;
        assertTrue("Wrong sizes: " + Arrays.toString(sizes), Arrays.equals(
                expected, sizes));
    }

    /**
     * Tests the calculation of cell sizes if the available space does not
     * satisfy the minimum space requirements.
     */
    @Test
    public void testCalcSizesLessSpaceThanMinimum()
    {
        PercentLayout layout = setupTestLayout(false, false);
        layout.setCanShrink(true);
        addComponents(layout);
        int[] sizes = layout.calcSizes(layout.getAllRowConstraints(), layout
                .getColumnCount(), layout.getRowGroups(), this,
                sum(ROW_SIZES) - 100, true);
        assertTrue("Wrong sizes: " + Arrays.toString(sizes), Arrays.equals(
                minRowSizeArray(), sizes));
    }

    /**
     * Tests the checkConstraints() method if an invalid constraints object is
     * passed. This should cause an exception.
     */
    @Test(expected = IllegalStateException.class)
    public void testCheckConstraintsInvalidObject()
    {
        PercentLayout layout = setupTestLayout(false, false);
        layout.checkConstraints(this);
    }

    /**
     * Tests a layout with a component whose X coordinates are too big. This
     * should cause an exception.
     */
    @Test(expected = IllegalStateException.class)
    public void testCheckConstraintsColumnTooBig()
    {
        PercentLayout layout = setupTestLayout(false, false);
        PercentLayoutPlatformAdapterImpl adapter = (PercentLayoutPlatformAdapterImpl) layout
                .getPlatformAdapter();
        PercentData.Builder pcb = new PercentData.Builder();
        PercentData data = pcb.xy(3, 1).spanX(layout.getColumnCount() - 1)
                .create();
        adapter.createComponent(null, 10, 10, data);
        layout.flushCache();
        layout.getRowConstraints(1);
    }

    /**
     * Tests a layout with a component whose Y coordinates are too big. This
     * should cause an exception.
     */
    @Test(expected = IllegalStateException.class)
    public void testCheckConstraintsRowTooBig()
    {
        PercentLayout layout = setupTestLayout(false, false);
        PercentLayoutPlatformAdapterImpl adapter = (PercentLayoutPlatformAdapterImpl) layout
                .getPlatformAdapter();
        PercentData.Builder pcb = new PercentData.Builder();
        PercentData data = pcb.xy(1, 3).spanY(layout.getRowCount() - 2)
                .create();
        adapter.createComponent(null, 10, 10, data);
        layout.flushCache();
        layout.getRowConstraints(1);
    }

    /**
     * Tests the getColumnGroups() method if there are no groups.
     */
    @Test
    public void testGetColumnGroupsEmpty()
    {
        PercentLayout layout = setupTestLayout(false, false);
        assertTrue("Got column groups", layout.getColumnGroups().isEmpty());
    }

    /**
     * Tries to modify the collection with column groups. This should cause an
     * exception.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetColumnGroupsModify()
    {
        PercentLayout layout = setupTestLayout(false, true);
        Collection<CellGroup> colGroups = layout.getColumnGroups();
        assertEquals("Wrong number of column groups", 1, colGroups.size());
        Iterator<CellGroup> it = colGroups.iterator();
        it.next();
        it.remove();
    }

    /**
     * Tests getColumnGroups() if multiple groups have been added.
     */
    @Test
    public void testGetColumnGroupsMultiple()
    {
        PercentLayout layout = setupTestLayout(false, false);
        CellGroup g1 = new CellGroup(0, 1);
        CellGroup g2 = new CellGroup(2, 3);
        layout.addColumnGroup(g1);
        layout.addColumnGroup(g2);
        Iterator<CellGroup> it = layout.getColumnGroups().iterator();
        assertEquals("Wrong group 1", g1, it.next());
        assertEquals("Wrong group 2", g2, it.next());
        assertFalse("Too many groups", it.hasNext());
    }

    /**
     * Tests the getRowGroups() method if there are no groups.
     */
    @Test
    public void testGetRowGroupsEmpty()
    {
        PercentLayout layout = setupTestLayout(false, false);
        assertTrue("Got row groups", layout.getRowGroups().isEmpty());
    }

    /**
     * Tries to modify the collection with row groups. This should cause an
     * exception.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetRowGroupsModify()
    {
        PercentLayout layout = setupTestLayout(false, true);
        Collection<CellGroup> rowGroups = layout.getRowGroups();
        assertEquals("Wrong number of row groups", 1, rowGroups.size());
        Iterator<CellGroup> it = rowGroups.iterator();
        it.next();
        it.remove();
    }

    /**
     * Tests getRowGroups() if multiple groups have been added.
     */
    @Test
    public void testGetRowGroupsMultiple()
    {
        PercentLayout layout = setupTestLayout(false, false);
        CellGroup g1 = new CellGroup(0, 1);
        CellGroup g2 = new CellGroup(2, 3);
        layout.addRowGroup(g1);
        layout.addRowGroup(g2);
        Iterator<CellGroup> it = layout.getRowGroups().iterator();
        assertEquals("Wrong group 1", g1, it.next());
        assertEquals("Wrong group 2", g2, it.next());
        assertFalse("Too many groups", it.hasNext());
    }

    /**
     * Tests whether the specified component is contained in the given list of
     * components that span multiple components.
     *
     * @param compData the list with the multi components
     * @param comp the component in question
     * @return a flag whether this component was found
     */
    private static boolean isMultiComponent(
            List<PercentLayoutBase.CellData> compData, Object comp)
    {
        for (PercentLayoutBase.CellData cd : compData)
        {
            if (comp.equals(cd.getComponent()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Tests whether a component can be removed.
     */
    @Test
    public void testRemoveComponent()
    {
        PercentLayout layout = setupTestLayout(false, false);
        PercentLayoutPlatformAdapterImpl adapter = (PercentLayoutPlatformAdapterImpl) layout
                .getPlatformAdapter();
        addComponents(layout);
        Object comp = adapter.getComponent(0);
        PercentData constr = (PercentData) adapter.getConstraints(0);
        assertTrue("Wrong result", layout.removeComponent(comp));
        assertNull("Component still found", layout.getComponent(constr
                .getColumn(), constr.getRow()));
    }

    /**
     * Tests whether a component that spans multiple cells can be removed.
     */
    @Test
    public void testRemoveComponentMultiCells()
    {
        PercentLayout layout = setupTestLayout(false, false);
        PercentLayoutPlatformAdapterImpl adapter = (PercentLayoutPlatformAdapterImpl) layout
                .getPlatformAdapter();
        PercentData.Builder pcb = new PercentData.Builder();
        PercentData pd = pcb.xy(0, 0).span(2, 2).create();
        Object comp = adapter.createComponent(null, 20, 10, pd);
        adapter.createComponent(null, 30, 40, pcb.xy(3, 0).span(2, 2).create());
        assertEquals("Wrong component", comp, layout.getComponent(0, 0));
        assertTrue("No multi column", isMultiComponent(layout
                .getMultiColumnData(), comp));
        assertTrue("No multi row", isMultiComponent(layout.getMultiRowData(),
                comp));
        assertTrue("Wrong result", layout.removeComponent(comp));
        assertNull("Component still found", layout.getComponent(0, 0));
        assertFalse("Still multi column", isMultiComponent(layout
                .getMultiColumnData(), comp));
        assertFalse("Still multi row", isMultiComponent(layout
                .getMultiRowData(), comp));
    }

    /**
     * Tries to remove a non-existing component.
     */
    @Test
    public void testRemoveComponentNonExisting()
    {
        PercentLayout layout = setupTestLayout(false, false);
        addComponents(layout);
        assertFalse("Wrong result", layout.removeComponent(this));
    }

    /**
     * Tries to remove a null component.
     */
    @Test
    public void testRemoveComponentNull()
    {
        PercentLayout layout = setupTestLayout(false, false);
        addComponents(layout);
        assertFalse("Wrong result", layout.removeComponent(null));
    }

    /**
     * Tests whether flushCache() also affects the multi components.
     */
    @Test
    public void testFlushCacheMultiComponents()
    {
        PercentLayout layout = setupTestLayout(false, false);
        addComponents(layout);
        PercentLayoutPlatformAdapterImpl adapter = (PercentLayoutPlatformAdapterImpl) layout
                .getPlatformAdapter();
        PercentData pd = new PercentData.Builder().xy(0, 0).span(2, 2).create();
        Object comp = adapter.createComponent(null, 20, 10, pd);
        int multiCols = layout.getMultiColumnData().size();
        int multiRows = layout.getMultiRowData().size();
        assertTrue("No multi column components", multiCols > 0);
        assertTrue("No multi row components", multiRows > 0);
        adapter.removeComponent(comp);
        layout.flushCache();
        assertEquals("Multi columns not cleared", multiCols - 1, layout
                .getMultiColumnData().size());
        assertEquals("Multi rows not cleared", multiRows - 1, layout
                .getMultiRowData().size());
    }

    /**
     * Tests whether a defensive copy of the array of column constraints is
     * returned.
     */
    @Test
    public void testGetAllColumnConstraintsModify()
    {
        PercentLayout layout = setupTestLayout(false, false);
        CellConstraints[] ccs = layout.getAllColumnConstraints();
        CellConstraints cc = ccs[0];
        ccs[0] = null;
        assertEquals("Array was modified", cc,
                layout.getAllColumnConstraints()[0]);
    }

    /**
     * Tests whether a defensive copy of the array of row constraints is
     * returned.
     */
    @Test
    public void testGetAllRowConstraintsModify()
    {
        PercentLayout layout = setupTestLayout(false, false);
        CellConstraints[] ccs = layout.getAllRowConstraints();
        CellConstraints cc = ccs[0];
        ccs[0] = null;
        assertEquals("Array was modified", cc, layout.getAllRowConstraints()[0]);
    }

    /**
     * Helper method for comparing two layout objects. This method checks
     * whether the constraints of the given layout objects are equal.
     *
     * @param layout1 layout 1
     * @param layout2 layout 2
     */
    static void compareLayouts(PercentLayoutBase layout1,
            PercentLayoutBase layout2)
    {
        assertEquals("Different column count", layout1.getColumnCount(),
                layout2.getColumnCount());
        assertEquals("Different row count", layout1.getRowCount(), layout2
                .getRowCount());
        assertTrue("Different col constraints", Arrays.equals(layout1
                .getAllColumnConstraints(), layout2.getAllColumnConstraints()));
        assertTrue("Different row constraints", Arrays.equals(layout1
                .getAllRowConstraints(), layout2.getAllRowConstraints()));
        JGuiraffeTestHelper.collectionEquals(layout1.getColumnGroups(), layout2
                .getColumnGroups());
        JGuiraffeTestHelper.collectionEquals(layout1.getRowGroups(), layout2
                .getRowGroups());
        for (int i = 0; i < layout1.getColumnCount(); i++)
        {
            for (int j = 0; j < layout1.getRowCount(); j++)
            {
                assertEquals("Wrong percent data at (" + i + ", " + j + ")",
                        layout1.getPercentData(i, j), layout2.getPercentData(i,
                                j));
            }
        }
    }

    /**
     * Helper method for testing the serialization of a layout object.
     *
     * @param layout the layout
     * @throws IOException if an IO error occurs
     */
    static void checkSerialization(PercentLayoutBase layout) throws IOException
    {
        PercentLayoutBase layout2 = JGuiraffeTestHelper.serialize(layout);
        compareLayouts(layout, layout2);
    }

    /**
     * Tests whether the layout can be serialized.
     */
    @Test
    public void testSerialization() throws IOException
    {
        PercentLayout layout = setupTestLayout(false, true);
        addComponents(layout);
        checkSerialization(layout);
    }
}
