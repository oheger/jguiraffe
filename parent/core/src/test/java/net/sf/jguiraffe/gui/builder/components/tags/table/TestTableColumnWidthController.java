/*
 * Copyright 2006-2018 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.components.tags.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.ComponentManagerImpl;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.layout.NumberWithUnit;

import org.apache.commons.jelly.JellyContext;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class for {@code TableColumnWidthController}.
 *
 * @author Oliver Heger
 * @version $Id: TestTableColumnWidthController.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestTableColumnWidthController
{
    /** Constant for the name of a column. */
    private static final String COL_NAME = "col";

    /** An array with the pixel sizes of columns with a fixed width. */
    private static final int[] FIXED_PX_SIZES = {
            30, 40, 50
    };

    /** An array with default percent values for columns. */
    private static final int[] PERCENT_VALUES = {
            10, 50, 25, 15
    };

    /** Constant for the total table width. */
    private static final int TOTAL_WIDTH = 220;

    /** An array with fixed widths. */
    private static NumberWithUnit[] FIXED_WIDTHS;

    /** The test Jelly context . */
    private JellyContext context;

    /** A counter for generated test columns. */
    private int columnCounter;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        FIXED_WIDTHS = new NumberWithUnit[FIXED_PX_SIZES.length];
        for (int i = 0; i < FIXED_PX_SIZES.length; i++)
        {
            FIXED_WIDTHS[i] = new NumberWithUnit(FIXED_PX_SIZES[i]);
        }
    }

    @Before
    public void setUp() throws Exception
    {
        context = new JellyContext();
        ComponentBuilderData builderData = new ComponentBuilderData();
        builderData.setComponentManager(new ComponentManagerImpl());
        builderData.put(context);
    }

    /**
     * Generates the name of a test column.
     *
     * @return the column name
     */
    private String columnName()
    {
        columnCounter++;
        return COL_NAME + columnCounter;
    }

    /**
     * Creates a new column tag for testing purposes. The tag is partly
     * initialized. It is also added to the parent table tag.
     *
     * @param parent the parent tag
     * @param width the fixed with of the column tag
     * @return the new column tag
     */
    private TableColumnTag createColumnTag(TableTag parent, String width)
    {
        TableColumnTag colTag = new TableColumnTag();
        colTag.setParent(parent);
        colTag.setName(columnName());
        colTag.setWidth(width);
        try
        {
            colTag.setContext(context);
            colTag.processBeforeBody();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            fail("Unexpected exception: " + ex);
        }
        parent.addColumn(colTag);
        return colTag;
    }

    /**
     * Creates a table tag with columns that have the test widths.
     *
     * @param allPercent a flag whether all percent widths should be added
     * @return the initialized table tag
     */
    private TableTag setUpTableTag(boolean allPercent)
    {
        TableTag tt = setUpFixedWidthColumns();
        int percentCount = PERCENT_VALUES.length;
        if (!allPercent)
        {
            percentCount--;
        }
        for (int i = 0; i < percentCount; i++)
        {
            TableColumnTag colTag = createColumnTag(tt, null);
            colTag.setPercentWidth(PERCENT_VALUES[i]);
        }
        return tt;
    }

    /**
     * Creates a table tag that is initialized with columns that have a fixed
     * width.
     *
     * @return the new table tag
     */
    private TableTag setUpFixedWidthColumns()
    {
        TableTag tt = new TableTag();
        for (NumberWithUnit n : FIXED_WIDTHS)
        {
            createColumnTag(tt, n.toUnitString());
        }
        return tt;
    }

    /**
     * Tries to create a new instance without a tag.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNewInstanceNull() throws FormBuilderException
    {
        TableColumnWidthController.newInstance(null);
    }

    /**
     * Helper method for testing whether an invalid percent value is detected.
     *
     * @param width the invalid value
     * @throws FormBuilderException should be thrown due to the invalid value
     */
    private void checkNewInstanceInvalidPercentWidth(int width)
            throws FormBuilderException
    {
        TableTag tt = setUpTableTag(false);
        TableColumnTag colTag = createColumnTag(tt, null);
        colTag.setPercentWidth(width);
        TableColumnWidthController.newInstance(tt);
    }

    /**
     * Tests whether a negative percent width is detected.
     */
    @Test(expected = FormBuilderException.class)
    public void testNewInstancePercentWidthNegative()
            throws FormBuilderException
    {
        checkNewInstanceInvalidPercentWidth(-1);
    }

    /**
     * Tests whether a percent width that is too big is detected.
     */
    @Test(expected = FormBuilderException.class)
    public void testNewInstancePercentWidthTooLarge()
            throws FormBuilderException
    {
        checkNewInstanceInvalidPercentWidth(101);
    }

    /**
     * Tests whether a column is detected that has both a fixed and a relative
     * width.
     */
    @Test(expected = FormBuilderException.class)
    public void testNewInstanceDuplicateWidthSpec() throws FormBuilderException
    {
        TableTag tt = setUpTableTag(false);
        TableColumnTag colTag = createColumnTag(tt, FIXED_WIDTHS[0]
                .toUnitString());
        colTag.setPercentWidth(PERCENT_VALUES[0]);
        TableColumnWidthController.newInstance(tt);
    }

    /**
     * Tests whether percent values that sum up to more than 100 are detected.
     */
    @Test(expected = FormBuilderException.class)
    public void testNewInstanceMoreThan100Percent() throws FormBuilderException
    {
        TableTag tt = setUpTableTag(true);
        TableColumnTag colTag = createColumnTag(tt, null);
        colTag.setPercentWidth(10);
        TableColumnWidthController.newInstance(tt);
    }

    /**
     * Tests whether the correct number of columns is returned.
     */
    @Test
    public void testGetColumnCount() throws FormBuilderException
    {
        TableTag tt = setUpTableTag(true);
        TableColumnWidthController ctrl = TableColumnWidthController
                .newInstance(tt);
        assertEquals("Wrong number of columns", FIXED_WIDTHS.length
                + PERCENT_VALUES.length, ctrl.getColumnCount());
    }

    /**
     * Tests whether the fixed with is correctly returned.
     */
    @Test
    public void testGetOriginalFixedWidth() throws FormBuilderException
    {
        TableTag tt = setUpTableTag(true);
        TableColumnWidthController ctrl = TableColumnWidthController
                .newInstance(tt);
        for (int i = 0; i < FIXED_WIDTHS.length; i++)
        {
            assertEquals("Wrong fixed with at " + i, FIXED_WIDTHS[i], ctrl
                    .getOriginalFixedWidth(i));
        }
    }

    /**
     * Tests whether the original fixed width of a column can be queried if it
     * is undefined.
     */
    @Test
    public void testGetOriginalFixedWidthUndefined()
            throws FormBuilderException
    {
        TableTag tt = setUpTableTag(true);
        TableColumnWidthController ctrl = TableColumnWidthController
                .newInstance(tt);
        assertNull("Got a fixed width", ctrl
                .getOriginalFixedWidth(FIXED_WIDTHS.length));
    }

    /**
     * Tests whether the correct percent width flags are returned.
     */
    @Test
    public void testIsPercentWidth() throws FormBuilderException
    {
        TableTag tt = setUpTableTag(true);
        TableColumnWidthController ctrl = TableColumnWidthController
                .newInstance(tt);
        for (int i = 0; i < FIXED_WIDTHS.length; i++)
        {
            assertFalse("Wrong percent flag at " + i, ctrl.isPercentWidth(i));
        }
        for (int i = 0; i < PERCENT_VALUES.length; i++)
        {
            assertTrue("Wrong percent flag at " + i, ctrl
                    .isPercentWidth(FIXED_WIDTHS.length + i));
        }
    }

    /**
     * Tests whether the correct percent values are returned.
     */
    @Test
    public void testGetPercentValue() throws FormBuilderException
    {
        TableTag tt = setUpTableTag(true);
        TableColumnWidthController ctrl = TableColumnWidthController
                .newInstance(tt);
        for (int i = 0; i < PERCENT_VALUES.length; i++)
        {
            assertEquals("Wrong percent value at " + i, PERCENT_VALUES[i], ctrl
                    .getPercentValue(FIXED_WIDTHS.length + i));
        }
    }

    /**
     * Tests whether the percent value can be queried if it is undefined.
     */
    @Test
    public void testGetPercentValueUndefined() throws FormBuilderException
    {
        TableTag tt = setUpTableTag(true);
        TableColumnWidthController ctrl = TableColumnWidthController
                .newInstance(tt);
        for (int i = 0; i < FIXED_WIDTHS.length; i++)
        {
            assertEquals("Got percent value at " + i, 0, ctrl
                    .getPercentValue(i));
        }
    }

    /**
     * Tests the adjustment of percent values if the sum is less than 100.
     */
    @Test
    public void testGetPercentValueAdjust() throws FormBuilderException
    {
        TableTag tt = setUpTableTag(false);
        TableColumnWidthController ctrl = TableColumnWidthController
                .newInstance(tt);
        assertEquals("Wrong percent value (1)", 15, ctrl
                .getPercentValue(FIXED_WIDTHS.length));
        assertEquals("Wrong percent value (2)", 55, ctrl
                .getPercentValue(FIXED_WIDTHS.length + 1));
        assertEquals("Wrong percent value (3)", 30, ctrl
                .getPercentValue(FIXED_WIDTHS.length + 2));
    }

    /**
     * Tests whether remaining percent values are added to undefined values.
     */
    @Test
    public void testGetPercentValueAdjustUndefined()
            throws FormBuilderException
    {
        TableTag tt = setUpTableTag(false);
        for (int i = 0; i < 3; i++)
        {
            createColumnTag(tt, null);
        }
        TableColumnWidthController ctrl = TableColumnWidthController
                .newInstance(tt);
        for (int i = 0; i < PERCENT_VALUES.length - 1; i++)
        {
            assertEquals("Wrong percent value at " + i, PERCENT_VALUES[i], ctrl
                    .getPercentValue(FIXED_PX_SIZES.length + i));
        }
        for (int i = 0; i < 3; i++)
        {
            assertEquals("Wrong adjusted value at " + i, 5, ctrl
                    .getPercentValue(FIXED_PX_SIZES.length
                            + PERCENT_VALUES.length - 1 + i));
        }
    }

    /**
     * Tests whether the number of columns with a percent width is correctly
     * returned.
     */
    @Test
    public void testGetNumberOfColumnsWithPercentWidth()
            throws FormBuilderException
    {
        TableTag tt = setUpTableTag(true);
        TableColumnWidthController ctrl = TableColumnWidthController
                .newInstance(tt);
        assertEquals("Wrong number of columns", PERCENT_VALUES.length, ctrl
                .getNumberOfColumnWithPercentWidth());
    }

    /**
     * Tests initialization of the controller if there are no columns with
     * relative width.
     */
    @Test
    public void testGetNumberOfColumnsWithPercentWidthFixedOnly()
            throws FormBuilderException
    {
        TableTag tt = setUpFixedWidthColumns();
        TableColumnWidthController ctrl = TableColumnWidthController
                .newInstance(tt);
        assertEquals("Got columns with percent width", 0, ctrl
                .getNumberOfColumnWithPercentWidth());
    }

    /**
     * Tests whether the column widths are correctly returned.
     */
    @Test
    public void testCalculateWidths() throws FormBuilderException
    {
        TableTag tt = setUpTableTag(true);
        TableColumnWidthController ctrl = TableColumnWidthController
                .newInstance(tt);
        for (int i = 0; i < FIXED_WIDTHS.length; i++)
        {
            ctrl.setFixedWidth(i, FIXED_PX_SIZES[i]);
        }
        int[] widths = ctrl.calculateWidths(TOTAL_WIDTH);
        assertEquals("Wrong number of array elements", FIXED_WIDTHS.length
                + PERCENT_VALUES.length, widths.length);
        for (int i = 0; i < FIXED_WIDTHS.length; i++)
        {
            assertEquals("Wrong fixed width at " + i, FIXED_PX_SIZES[i],
                    widths[i]);
        }
        for (int i = 0; i < PERCENT_VALUES.length; i++)
        {
            assertEquals("Wrong percent width at " + i, PERCENT_VALUES[i],
                    widths[FIXED_WIDTHS.length + i]);
        }
    }

    /**
     * Tests that column widths cannot become negative if the total width is too
     * small.
     */
    @Test
    public void testCalculateWidthsTotalWidthTooSmall()
            throws FormBuilderException
    {
        TableTag tt = setUpTableTag(true);
        TableColumnWidthController ctrl = TableColumnWidthController
                .newInstance(tt);
        for (int i = 0; i < FIXED_WIDTHS.length; i++)
        {
            ctrl.setFixedWidth(i, FIXED_PX_SIZES[i]);
        }
        int[] widths = ctrl.calculateWidths(10);
        for (int i = 0; i < FIXED_WIDTHS.length; i++)
        {
            assertEquals("Wrong fixed width at " + i, FIXED_PX_SIZES[i],
                    widths[i]);
        }
        for (int i = 0; i < PERCENT_VALUES.length; i++)
        {
            assertEquals("Wrong percent width at " + i, 0,
                    widths[FIXED_WIDTHS.length + i]);
        }
    }

    /**
     * Tries to call recalibrate() with a null parameter.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRecalibrateNull() throws FormBuilderException
    {
        TableTag tt = setUpTableTag(false);
        TableColumnWidthController ctrl = TableColumnWidthController
                .newInstance(tt);
        ctrl.recalibrate(null);
    }

    /**
     * Tries to call recalibrate() with an array that has the wrong number of
     * elements.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRecalibrateWrongElementCount() throws FormBuilderException
    {
        TableTag tt = setUpTableTag(false);
        TableColumnWidthController ctrl = TableColumnWidthController
                .newInstance(tt);
        int[] sizes = new int[28];
        ctrl.recalibrate(sizes);
    }

    /**
     * Tests a recalibration with the given percent values.
     *
     * @param percentSizes the percent values
     * @throws FormBuilderException if an error occurs
     */
    private void checkRecalibrate(int[] percentSizes)
            throws FormBuilderException
    {
        TableTag tt = setUpTableTag(true);
        TableColumnWidthController ctrl = TableColumnWidthController
                .newInstance(tt);
        int[] sizes = new int[FIXED_WIDTHS.length + PERCENT_VALUES.length];
        System.arraycopy(FIXED_PX_SIZES, 0, sizes, 0, FIXED_PX_SIZES.length);
        System.arraycopy(percentSizes, 0, sizes, FIXED_PX_SIZES.length,
                percentSizes.length);
        ctrl.recalibrate(sizes);
        for (int i = 0; i < FIXED_PX_SIZES.length; i++)
        {
            assertEquals("Wrong fixed with at " + i, FIXED_PX_SIZES[i], ctrl
                    .getFixedWidth(i));
        }
        for (int i = 0; i < percentSizes.length; i++)
        {
            assertEquals("Wrong percent value at " + i, percentSizes[i], ctrl
                    .getPercentValue(i + FIXED_PX_SIZES.length));
        }
    }

    /**
     * Tests a successful recalibration of the column widths.
     */
    @Test
    public void testRecalibrate() throws FormBuilderException
    {
        final int[] percentSizes = {
                30, 10, 40, 20
        };
        checkRecalibrate(percentSizes);
    }

    /**
     * Tests recalibrate() if all percent columns have a width of 0.
     */
    @Test
    public void testRecalibrateNoRemainingPercentWidth()
            throws FormBuilderException
    {
        final int[] percentSizes = new int[PERCENT_VALUES.length];
        checkRecalibrate(percentSizes);
    }

    /**
     * Tests whether a series of updates of column sizes does not lead to major
     * rounding errors.
     */
    @Test
    public void testColumnSizeUpdatesWithPrecision()
            throws FormBuilderException
    {
        final int percentIndex = FIXED_WIDTHS.length - 1;
        final int totalSize = 1111;
        int[][] updates = { // array elements are column index and delta
                        {
                                0, 7
                        }, {
                                percentIndex, 19
                        }, {
                                percentIndex + 1, -13
                        }, {
                                percentIndex + 2, 3
                        }, {
                                2, -11
                        }, {
                                percentIndex, -5
                        }
                };
        TableTag tt = setUpTableTag(true);
        TableColumnWidthController ctrl =
                TableColumnWidthController.newInstance(tt);
        int[] widths = ctrl.calculateWidths(totalSize);

        for (int[] update : updates)
        {
            widths[update[0]] += update[1];
            widths[update[0] + 1] -= update[1];
            ctrl.recalibrate(widths);
            widths = ctrl.calculateWidths(totalSize);
        }

        int sum = 0;
        for (int w : widths)
        {
            sum += w;
        }
        assertEquals("Wrong total column width", totalSize, sum);
    }
}
