/*
 * Copyright 2006-2016 The JGUIraffe Team.
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

import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.layout.NumberWithUnit;

/**
 * <p>
 * A helper class that provides functionality for managing the widths of the
 * columns of a table.
 * </p>
 * <p>
 * An instance of this class is created and initialized by {@link TableTag}. It
 * can be used by platform-specific table implementations. It helps storing the
 * column widths of a table and provides some helper methods for calculating
 * widths if columns with a relative width are involved. Because this
 * functionality is not platform-specific it is collected in this helper class.
 * </p>
 * <p>
 * Implementation note: This class is not thread-safe.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TableColumnWidthController.java 205 2012-01-29 18:29:57Z oheger $
 */
public final class TableColumnWidthController implements
        TableColumnRecalibrator, TableColumnWidthCalculator
{
    /** Constant for percent calculations. */
    private static final double PERCENT = 100;

    /** An array with the original fixed widths of the columns. */
    private final NumberWithUnit[] originalWidths;

    /** An array with the current fixed widths. */
    private final int[] fixedWidths;

    /** Stores the current percent values. */
    private final double[] percentValues;

    /** The number of columns with a percent width. */
    private final int numberOfColumnsWithPercentWidth;

    /**
     * Creates a new instance of {@code TableColumnWidthController}. This
     * constructor is used internally. Clients create instances using the static
     * factory method.
     *
     * @param orgWidths an array with the original width definitions for columns
     *        with fixed width
     * @param percents the current percent widths for all columns
     * @param percentCnt the number of columns with a percent width
     */
    private TableColumnWidthController(NumberWithUnit[] orgWidths,
            double[] percents, int percentCnt)
    {
        originalWidths = orgWidths;
        percentValues = percents;
        numberOfColumnsWithPercentWidth = percentCnt;
        fixedWidths = new int[orgWidths.length];
    }

    /**
     * Creates a new instance of {@link TableColumnWidthController} and
     * initializes it with the information provided by the given {@code
     * TableTag}. This factory method first checks whether the widths of the
     * columns managed by the table tag are valid; if not, an exception is
     * thrown. Then an instance is created and initialized with the column
     * widths.
     *
     * @param tt the {@code TableTag} (must not be <b>null</b>)
     * @return a new instance of this class
     * @throws FormBuilderException if the widths of the table's columns are
     *         invalid
     * @throws IllegalArgumentException if the tag is <b>null</b>
     */
    public static TableColumnWidthController newInstance(TableTag tt)
            throws FormBuilderException
    {
        if (tt == null)
        {
            throw new IllegalArgumentException("TableTag must not be null!");
        }

        NumberWithUnit[] widths = new NumberWithUnit[tt.getColumnCount()];
        int sumPercents = 0;
        int percentCnt = 0;
        int undefPercentCnt = 0;

        for (int i = 0; i < widths.length; i++)
        {
            TableColumnTag colTag = tt.getColumn(i);
            checkColumnWidthSpec(colTag);
            if (isPercentWidth(colTag))
            {
                sumPercents += colTag.getPercentWidth();
                percentCnt++;
                if (colTag.getPercentWidth() == 0)
                {
                    undefPercentCnt++;
                }
            }
            else
            {
                widths[i] = colTag.getColumnWidth();
            }
        }

        return new TableColumnWidthController(widths,
                calculateInitialPercentValues(tt, percentCnt, sumPercents,
                        undefPercentCnt), percentCnt);
    }

    /**
     * Returns the number of columns managed by this instance.
     *
     * @return the number of columns
     */
    public int getColumnCount()
    {
        return originalWidths.length;
    }

    /**
     * Tests whether the column with the specified index has a relative width.
     * This means that the width is specified as a percent value.
     *
     * @param index the index of the column (0-based)
     * @return a flag whether this column has a percent width
     */
    public boolean isPercentWidth(int index)
    {
        return originalWidths[index] == null;
    }

    /**
     * Returns the original fixed width of the specified column. This is the
     * value defined by the {@code width} attribute of the table column tag. If
     * the column in question does not have a fixed width, this method returns
     * <b>null</b>.
     *
     * @param index the index of the column (0-based)
     * @return the original width of this column or <b>null</b>
     */
    public NumberWithUnit getOriginalFixedWidth(int index)
    {
        return originalWidths[index];
    }

    /**
     * Returns the fixed width (in pixels) of the column with the specified
     * index. Here the width of the column transformed into pixels is returned.
     * If this column does not have a fixed width, this method returns 0.
     *
     * @param index the index of the column (0-based)
     * @return the fixed width of this column in pixels
     */
    public int getFixedWidth(int index)
    {
        return fixedWidths[index];
    }

    /**
     * Sets the fixed width of the column with the specified index (in pixels).
     * This method is intended to be called by a platform-specific table
     * implementation. When the table is created the {@link NumberWithUnit}
     * values for column widths have to be transformed to pixel values. If later
     * the width of the column changes, this value has to be updated.
     *
     * @param index the index of the column (0-based)
     * @param width the new fixed width of the column
     */
    public void setFixedWidth(int index, int width)
    {
        fixedWidths[index] = width;
    }

    /**
     * Returns the relative width (in percent) of the column with the specified
     * index. If the column in question does not have a percent width, this
     * method returns 0.
     *
     * @param index the index of the column (0-based)
     * @return the width of this column in percent
     */
    public int getPercentValue(int index)
    {
        return roundInt(percentValues[index] * PERCENT);
    }

    /**
     * Returns the number of columns managed by this controller whose width is
     * specified as a percent value.
     *
     * @return the number of columns with a percent value as width
     */
    public int getNumberOfColumnWithPercentWidth()
    {
        return numberOfColumnsWithPercentWidth;
    }

    /**
     * {@inheritDoc} This implementation evaluates the current widths of columns
     * with fixed or percent widths.
     */
    public int[] calculateWidths(int totalSize)
    {
        int[] widths = new int[getColumnCount()];
        int sum = 0;

        // handle columns with a fixed width
        for (int i = 0; i < widths.length; i++)
        {
            if (!isPercentWidth(i))
            {
                widths[i] = fixedWidths[i];
                sum += fixedWidths[i];
            }
        }

        // handle columns with a percent width
        int remaining = totalSize - sum;
        if (remaining > 0)
        {
            for (int i = 0; i < widths.length; i++)
            {
                if (isPercentWidth(i))
                {
                    widths[i] = roundInt(remaining * percentValues[i]);
                }
            }
        }

        return widths;
    }

    /**
     * {@inheritDoc} This implementation adjusts the current width values for
     * columns with a fixed width and recalculates the percent values for the
     * other columns.
     */
    public void recalibrate(int[] columnSizes)
    {
        if (columnSizes == null)
        {
            throw new IllegalArgumentException(
                    "Array with column sizes must not be null!");
        }
        if (columnSizes.length != getColumnCount())
        {
            throw new IllegalArgumentException(
                    "Wrong number of elements in columns array!");
        }

        int totalSize = sumUpTotalSize(columnSizes);
        int fixedSize = recalibrateFixedWidths(columnSizes);
        double remainingSize = totalSize - fixedSize;

        for (int i = 0; i < columnSizes.length; i++)
        {
            if (isPercentWidth(i))
            {
                percentValues[i] = (remainingSize == 0) ? 0
                        : columnSizes[i] / remainingSize;
            }
        }
    }

    /**
     * Performs the recalibration of columns with a fixed width. This method
     * also sums up the width of all columns with a fixed width.
     *
     * @param columnSizes the array with the column sizes
     * @return the sum of the widths of the columns with a fixed width
     */
    private int recalibrateFixedWidths(int[] columnSizes)
    {
        int fixedWidth = 0;

        for (int i = 0; i < columnSizes.length; i++)
        {
            if (!isPercentWidth(i))
            {
                fixedWidths[i] = columnSizes[i];
                fixedWidth += columnSizes[i];
            }
        }

        return fixedWidth;
    }

    /**
     * Validates the width specification for the specified column tag. Throws an
     * exception if the specification is invalid, i.e. if both a fixed and a
     * relative width is specified or if the percent value is invalid.
     *
     * @param colTag the column tag to be checked
     * @throws FormBuilderException if the width specification is invalid
     */
    private static void checkColumnWidthSpec(TableColumnTag colTag)
            throws FormBuilderException
    {
        if (colTag.getColumnWidth() != null)
        {
            if (colTag.getPercentWidth() != 0)
            {
                throw new FormBuilderException(
                        "Column has both a fixed and a relative width!");
            }
        }
        else
        {
            if (colTag.getPercentWidth() < 0 || colTag.getPercentWidth() > PERCENT)
            {
                throw new FormBuilderException(String.format(
                        "Invalid percent width %d!", colTag.getPercentWidth()));
            }
        }
    }

    /**
     * Tests whether the column represented by the column tag has a percent
     * width.
     *
     * @param colTag the column tag
     * @return a flag whether this column has a percent value
     */
    private static boolean isPercentWidth(TableColumnTag colTag)
    {
        return colTag.getColumnWidth() == null;
    }

    /**
     * Calculates the percent values for all columns. This method is needed if
     * the sum of percent values is less than 100. In this case the difference
     * to 100 is determined, and the remaining number is added to all columns
     * with an undefined percent value in equal parts.
     *
     * @param tt the table tag
     * @param percentCnt the number of columns with a percent value
     * @param sumPercents the sum of the percent values
     * @param undefPercentCnt the number of columns with an undefined percent
     *        value
     * @return an array with all percent values
     * @throws FormBuilderException if the sum of percent values is invalid
     */
    private static double[] calculateInitialPercentValues(TableTag tt,
            int percentCnt, int sumPercents, int undefPercentCnt)
            throws FormBuilderException
    {
        if (sumPercents > PERCENT)
        {
            throw new FormBuilderException(
                    "Sum of percent values is greater than 100: " + sumPercents);
        }
        double[] percents = new double[tt.getColumnCount()];

        if (undefPercentCnt > 0)
        {
            double delta = (PERCENT - sumPercents) / undefPercentCnt;
            for (int i = 0; i < percents.length; i++)
            {
                TableColumnTag colTag = tt.getColumn(i);
                if (isPercentWidth(colTag))
                {
                    double percentWidth =
                            (colTag.getPercentWidth() != 0) ? colTag
                                    .getPercentWidth() : delta;
                    percents[i] = percentWidth / PERCENT;
                }
            }
        }

        else if (percentCnt > 0)
        {
            double delta = (PERCENT - sumPercents) / percentCnt / PERCENT;
            for (int i = 0; i < percents.length; i++)
            {
                TableColumnTag colTag = tt.getColumn(i);
                if (isPercentWidth(colTag))
                {
                    percents[i] = colTag.getPercentWidth() / PERCENT + delta;
                }
            }
        }

        return percents;
    }

    /**
     * Helper method for calculating the sum of the given array with column
     * sizes.
     *
     * @param columnSizes an array with column sizes
     * @return the sum of the sizes
     */
    private static int sumUpTotalSize(int[] columnSizes)
    {
        int totalSize = 0;
        for (int columnSize : columnSizes)
        {
            totalSize += columnSize;
        }

        return totalSize;
    }

    /**
     * Helper method for rounding a double value to an int.
     *
     * @param value the value
     * @return the rounded integer value
     */
    private static int roundInt(double value)
    {
        return (int) Math.round(value);
    }
}
