/*
 * Copyright 2006-2012 The JGUIraffe Team.
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

import java.util.Locale;

/**
 * <p>
 * A GUI library independent implementation of the AWT layout manager
 * <em>BorderLayout</em>.
 * </p>
 * <p>
 * This layout manager implements the <code>BorderLayout</code> functionality
 * based on the <code>PercentLayout</code> layout manager. Because of that it
 * can be used for all platforms for which an adapter is available.
 * </p>
 * <p>
 * This implementation differs from the original <code>BorderLayout</code> in
 * only a few points: Margins can be defined around the hosting container and
 * individual gaps are supported between all hosted components. These margins
 * and gaps can be specified using all supported units.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: BorderLayout.java 205 2012-01-29 18:29:57Z oheger $
 */
public class BorderLayout extends PercentLayoutBase
{
    /** Constant for the layout constraints <em>North</em>. */
    public static final String NORTH = "NORTH";

    /** Constant for the layout constraints <em>East</em>. */
    public static final String EAST = "EAST";

    /** Constant for the layout constraints <em>South</em>. */
    public static final String SOUTH = "SOUTH";

    /** Constant for the layout constraints <em>West</em>. */
    public static final String WEST = "WEST";

    /** Constant for the layout constraints <em>Center</em>. */
    public static final String CENTER = "CENTER";

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 20090730L;

    /** Constant for the number of columns and rows. */
    private static final int SIZE = 7;

    /** Constant for the index of the 1st cell. */
    private static final int IDX_1 = 1;

    /** Constant for the index of the 2nd cell. */
    private static final int IDX_2 = 2;

    /** Constant for the index of the 3rd cell. */
    private static final int IDX_3 = 3;

    /** Constant for the index of the 4th cell. */
    private static final int IDX_4 = 4;

    /** Constant for the index of the 5th cell. */
    private static final int IDX_5 = 5;

    /** Constant for the index of the 6th cell. */
    private static final int IDX_6 = 6;

    /** Constant for the maximum weight factor. */
    private static final int WEIGHT_FULL = 100;

    /** A builder for percent data objects. */
    private final PercentData.Builder pcb;

    /** Stores the top margin. */
    private NumberWithUnit topMargin;

    /** Stores the left margin. */
    private NumberWithUnit leftMargin;

    /** Stores the bottom margin. */
    private NumberWithUnit bottomMargin;

    /** Stores the right margin. */
    private NumberWithUnit rightMargin;

    /** Stores the north gap. */
    private NumberWithUnit northGap;

    /** Stores the east gap. */
    private NumberWithUnit eastGap;

    /** Stores the south gap. */
    private NumberWithUnit southGap;

    /** Stores the west gap. */
    private NumberWithUnit westGap;

    /**
     * Creates a new instance of {@code BorderLayout}.
     */
    public BorderLayout()
    {
        pcb = new PercentData.Builder();
    }

    /**
     * Returns the bottom margin.
     *
     * @return the bottom margin
     */
    public NumberWithUnit getBottomMargin()
    {
        return NumberWithUnit.nonNull(bottomMargin);
    }

    /**
     * Sets the bottom margin.
     *
     * @param bottomMargin the bottom margin
     */
    public void setBottomMargin(NumberWithUnit bottomMargin)
    {
        this.bottomMargin = bottomMargin;
    }

    /**
     * Returns the east gap.
     *
     * @return the east gap
     */
    public NumberWithUnit getEastGap()
    {
        return NumberWithUnit.nonNull(eastGap);
    }

    /**
     * Sets the east gap. This is a gap between the center and the east
     * component.
     *
     * @param eastGap the east gap
     */
    public void setEastGap(NumberWithUnit eastGap)
    {
        this.eastGap = eastGap;
    }

    /**
     * Returns the left margin.
     *
     * @return the left margin
     */
    public NumberWithUnit getLeftMargin()
    {
        return NumberWithUnit.nonNull(leftMargin);
    }

    /**
     * Sets the left margin.
     *
     * @param leftMargin the left margin
     */
    public void setLeftMargin(NumberWithUnit leftMargin)
    {
        this.leftMargin = leftMargin;
    }

    /**
     * Returns the north gap.
     *
     * @return the north gap
     */
    public NumberWithUnit getNorthGap()
    {
        return NumberWithUnit.nonNull(northGap);
    }

    /**
     * Sets the north gap. This is a gap between the north and the center
     * component.
     *
     * @param northGap the north gap
     */
    public void setNorthGap(NumberWithUnit northGap)
    {
        this.northGap = northGap;
    }

    /**
     * Returns the right margin.
     *
     * @return the right margin
     */
    public NumberWithUnit getRightMargin()
    {
        return NumberWithUnit.nonNull(rightMargin);
    }

    /**
     * Sets the right margin.
     *
     * @param rightMargin the right margin
     */
    public void setRightMargin(NumberWithUnit rightMargin)
    {
        this.rightMargin = rightMargin;
    }

    /**
     * Returns the south gap.
     *
     * @return the south gap
     */
    public NumberWithUnit getSouthGap()
    {
        return NumberWithUnit.nonNull(southGap);
    }

    /**
     * Sets the south gap. This is a gap between the south and the center
     * component.
     *
     * @param southGap the south gap
     */
    public void setSouthGap(NumberWithUnit southGap)
    {
        this.southGap = southGap;
    }

    /**
     * Returns the top margin.
     *
     * @return the top margin
     */
    public NumberWithUnit getTopMargin()
    {
        return NumberWithUnit.nonNull(topMargin);
    }

    /**
     * Sets the top margin.
     *
     * @param topMargin the top margin
     */
    public void setTopMargin(NumberWithUnit topMargin)
    {
        this.topMargin = topMargin;
    }

    /**
     * Returns the west gap.
     *
     * @return the west gap
     */
    public NumberWithUnit getWestGap()
    {
        return NumberWithUnit.nonNull(westGap);
    }

    /**
     * Sets the west gap. This is a gap between the west and the center
     * component.
     *
     * @param westGap the west gap
     */
    public void setWestGap(NumberWithUnit westGap)
    {
        this.westGap = westGap;
    }

    /**
     * Initializes the percent layout. This implementation creates a layout with
     * 7 columns and 7 rows and places the contained components in the
     * appropriate cells according to their constraints.
     *
     * @param adapter the platform adapter
     */
    @Override
    protected void initCells(PercentLayoutPlatformAdapter adapter)
    {
        initDimensions(SIZE, SIZE);
        clearCells(SIZE, SIZE);

        int centerIndex = -1;
        boolean north = false;
        boolean west = false;
        boolean south = false;
        boolean east = false;

        for (int i = 0; i < adapter.getComponentCount(); i++)
        {
            Object o = adapter.getConstraints(i);
            if (o == null)
            {
                throw new IllegalStateException(
                        "A constraints object must be defined for BorderLayout!");
            }
            String c = o.toString().toUpperCase(Locale.ENGLISH);
            PercentData pd;

            if (NORTH.equals(c))
            {
                pd = initHorizontalCell(IDX_1, west, east);
                north = true;
            }
            else if (SOUTH.equals(c))
            {
                pd = initHorizontalCell(IDX_5, west, east);
                south = true;
            }
            else if (WEST.equals(c))
            {
                pd = initVerticalCell(IDX_1, north, south);
                west = true;
            }
            else if (EAST.equals(c))
            {
                pd = initVerticalCell(IDX_5, north, south);
                east = true;
            }
            else if (CENTER.equals(c))
            {
                centerIndex = i;
                pd = null;
            }

            else
            {
                throw new IllegalStateException("Invalid constraints object: "
                        + c);
            }

            if (pd != null)
            {
                initCell(adapter.getComponent(i), pd);
            }
        }

        if (centerIndex >= 0)
        {
            initCell(adapter.getComponent(centerIndex), initCenterCell(north,
                    south, west, east));
        }

        initConstraints(north, south, west, east);
    }

    /**
     * Initializes the column and row constraints for the percent layout. They
     * depend on the occupied positions in the border layout.
     *
     * @param north flag whether the north position is occupied
     * @param south flag whether the south position is occupied
     * @param west flag whether the west position is occupied
     * @param east flag whether the east position is occupied
     */
    protected void initConstraints(boolean north, boolean south, boolean west,
            boolean east)
    {
        CellConstraints.Builder cBuilder = getConstraintsBuilder();

        setColumnConstraints(0, cBuilder.withMinimumSize(getLeftMargin())
                .create());
        setColumnConstraints(IDX_1, cBuilder.withCellSize(CellSize.PREFERRED)
                .create());
        setColumnConstraints(IDX_2, cBuilder.withMinimumSize(
                west ? getWestGap() : NumberWithUnit.ZERO).create());
        setColumnConstraints(IDX_3, cBuilder.withCellSize(CellSize.PREFERRED)
                .withWeight(WEIGHT_FULL).create());
        setColumnConstraints(IDX_4, cBuilder.withMinimumSize(
                east ? getEastGap() : NumberWithUnit.ZERO).create());
        setColumnConstraints(IDX_5, cBuilder.withCellSize(CellSize.PREFERRED)
                .create());
        setColumnConstraints(IDX_6, cBuilder.withMinimumSize(getRightMargin())
                .create());

        setRowConstraints(0, cBuilder.withMinimumSize(getTopMargin()).create());
        setRowConstraints(IDX_1, cBuilder.withCellAlignment(CellAlignment.FULL)
                .withCellSize(CellSize.PREFERRED).create());
        setRowConstraints(IDX_2, cBuilder.withMinimumSize(
                north ? getNorthGap() : NumberWithUnit.ZERO).create());
        setRowConstraints(IDX_3, cBuilder.withCellAlignment(CellAlignment.FULL)
                .withCellSize(CellSize.PREFERRED).withWeight(WEIGHT_FULL).create());
        setRowConstraints(IDX_4, cBuilder.withMinimumSize(
                south ? getSouthGap() : NumberWithUnit.ZERO).create());
        setRowConstraints(IDX_5, cBuilder.withCellAlignment(CellAlignment.FULL)
                .withCellSize(CellSize.PREFERRED).create());
        setRowConstraints(IDX_6, cBuilder.withMinimumSize(getBottomMargin())
                .create());
    }

    /**
     * Initializes a constraints object for the NORTH or SOUTH cell.
     *
     * @param row the row number
     * @param west the west flag
     * @param east the east flag
     * @return the constraints
     */
    private PercentData initHorizontalCell(int row, boolean west, boolean east)
    {
        PercentData pd = pcb.xy(calcPosition(west, east), row).spanX(
                calcSpan(west, east)).withTargetColumn(IDX_3).create();
        return pd;
    }

    /**
     * Initializes a constraints object for the WEST or EAST cell.
     *
     * @param col the column index
     * @param north the north flag
     * @param south the south flag
     * @return the constraints
     */
    private PercentData initVerticalCell(int col, boolean north, boolean south)
    {
        PercentData pd = pcb.xy(col, calcPosition(north, south)).spanY(
                calcSpan(north, south)).withTargetRow(IDX_3).create();
        return pd;
    }

    /**
     * Initializes a constraints object for the CENTER cell.
     *
     * @param north the north flag
     * @param south the south flag
     * @param west the west flag
     * @param east the east flag
     * @return the constraints
     */
    private PercentData initCenterCell(boolean north, boolean south,
            boolean west, boolean east)
    {
        PercentData pd =
                pcb.xy(calcPosition(west, east), calcPosition(north, south))
                        .span(calcSpan(east, west), calcSpan(north, south))
                        .withTargetColumn(IDX_3).withTargetRow(IDX_3).create();
        return pd;
    }

    /**
     * Determines the position of a component depending on already available
     * components.
     *
     * @param f1 flag for the first adjacent component
     * @param f2 flag for the second adjacent component
     * @return the position
     */
    private static int calcPosition(boolean f1, boolean f2)
    {
        return f1 ? IDX_3 : IDX_1;
    }

    /**
     * Determines the span of a component depending on already available
     * components.
     *
     * @param f1 flag for the first adjacent component
     * @param f2 flag for the second adjacent component
     * @return the span
     */
    private static int calcSpan(boolean f1, boolean f2)
    {
        int span = 1;
        if (!f1)
        {
            span += 2;
        }
        if (!f2)
        {
            span += 2;
        }
        return span;
    }
}
