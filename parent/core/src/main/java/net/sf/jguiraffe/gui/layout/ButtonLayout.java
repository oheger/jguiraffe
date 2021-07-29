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
package net.sf.jguiraffe.gui.layout;

/**
 * <p>
 * A layout manager that deals with button bars for dialogs.
 * </p>
 * <p>
 * With this layout manager typical horizontal button bars for dialogs can be
 * created in an easy way. The button objects simply need to be added to the
 * container, no constraints need to be passed. These button bars have the
 * following properties:
 * <ul>
 * <li>All buttons have the same width. This is the width of the widest button.</li>
 * <li>Between the buttons there is a gap, which can be configured.</li>
 * <li>Margins can be defined for all directions. All margins and the gap
 * between the buttons can be defined in several units.</li>
 * <li>The alignment of the buttons can be defined. This can be left aligned
 * (i.e. the right margin grows if more space is available in the dialog), right
 * aligned (the left margins grows) or center aligned (both margins grow).</li>
 * </ul>
 * </p>
 * <p>
 * This class is a special implementation of <code>PercentLayoutBase</code>. It
 * implements the button layout on top of a percent layout. As its ancestor this
 * class is platform neutral, i.e. it can work together with different GUI
 * libraries for which a {@link PercentLayoutPlatformAdapter} implementation is
 * available.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ButtonLayout.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ButtonLayout extends PercentLayoutBase
{
    /** Constant for the default margin value. */
    static final NumberWithUnit DEFAULT_MARGIN = new NumberWithUnit(2, Unit.DLU);

    /** Constant for the default gap value. */
    static final NumberWithUnit DEFAULT_GAP = new NumberWithUnit(1, Unit.DLU);

    /** Constant for the number of rows of this layout. */
    private static final int ROW_SIZE = 3;

    /** Constant for the full weight factor. */
    private static final int WEIGHT_FULL = 100;

    /** Constant for the half weight factor. */
    private static final int WEIGHT_HALF = 50;

    /** Constant for no weight factor. */
    private static final int WEIGHT_NULL = 0;

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 20090730L;

    /** Stores the top margin. */
    private NumberWithUnit topMargin;

    /** Stores the bottom margin. */
    private NumberWithUnit bottomMargin;

    /** Stores the left margin. */
    private NumberWithUnit leftMargin;

    /** Stores the right margin. */
    private NumberWithUnit rightMargin;

    /** Stores the width of the gap between the buttons. */
    private NumberWithUnit gap;

    /** Stores the alignment flag. */
    private Alignment alignment;

    /**
     * Creates a new instance of <code>ButtonLayout</code>. All properties are
     * set to default values.
     */
    public ButtonLayout()
    {
        leftMargin = DEFAULT_MARGIN;
        rightMargin = DEFAULT_MARGIN;
        topMargin = DEFAULT_MARGIN;
        bottomMargin = DEFAULT_MARGIN;
        gap = DEFAULT_GAP;
        alignment = Alignment.RIGHT;
    }

    /**
     * Returns the top margin.
     *
     * @return the top margin
     */
    public NumberWithUnit getTopMargin()
    {
        return topMargin;
    }

    /**
     * Sets the top margin. This is the space above the buttons.
     *
     * @param topMargin the top margin (a <b>null</b> reference is converted to
     *        a value of 0)
     */
    public void setTopMargin(NumberWithUnit topMargin)
    {
        this.topMargin = NumberWithUnit.nonNull(topMargin);
    }

    /**
     * Returns the bottom margin.
     *
     * @return the bottom margin
     */
    public NumberWithUnit getBottomMargin()
    {
        return bottomMargin;
    }

    /**
     * Sets the bottom margin. This is the space below the buttons.
     *
     * @param bottomMargin the bottom margin
     */
    public void setBottomMargin(NumberWithUnit bottomMargin)
    {
        this.bottomMargin = NumberWithUnit.nonNull(bottomMargin);
    }

    /**
     * Returns the left margin.
     *
     * @return the left margin
     */
    public NumberWithUnit getLeftMargin()
    {
        return leftMargin;
    }

    /**
     * Sets the left margin. This is the space between the window's left edge
     * and the first button. This value is fixed only if the button bar is left
     * aligned; otherwise this space may grow.
     *
     * @param leftMargin the left margin (a <b>null</b> reference is converted
     *        to a value of 0)
     */
    public void setLeftMargin(NumberWithUnit leftMargin)
    {
        this.leftMargin = NumberWithUnit.nonNull(leftMargin);
    }

    /**
     * Returns the right margin.
     *
     * @return the right margin
     */
    public NumberWithUnit getRightMargin()
    {
        return rightMargin;
    }

    /**
     * Sets the right margin. This is the space between the window's right edge
     * and the last button. This value is fixed only if the button bar is right
     * aligned; otherwise this space may grow.
     *
     * @param rightMargin the right margin (a <b>null</b> reference is converted
     *        to a value of 0)
     */
    public void setRightMargin(NumberWithUnit rightMargin)
    {
        this.rightMargin = NumberWithUnit.nonNull(rightMargin);
    }

    /**
     * Returns the width of the gap between the buttons.
     *
     * @return the gap
     */
    public NumberWithUnit getGap()
    {
        return gap;
    }

    /**
     * Sets the width of the gap between the buttons.
     *
     * @param gap the gap's width (a <b>null</b> reference is converted to a
     *        value of 0)
     */
    public void setGap(NumberWithUnit gap)
    {
        this.gap = NumberWithUnit.nonNull(gap);
    }

    /**
     * Returns the alignment of the button bar.
     *
     * @return the alignment
     */
    public Alignment getAlignment()
    {
        return alignment;
    }

    /**
     * Sets the alignment of the button bar.
     *
     * @param alignment the new alignment (must not be <b>null</b>)
     * @throws IllegalArgumentException if the alignment is <b>null</b>
     */
    public void setAlignment(Alignment alignment)
    {
        if (alignment == null)
        {
            throw new IllegalArgumentException("Alignment must not be null!");
        }
        this.alignment = alignment;
    }

    /**
     * Initializes the whole layout. Creates a percent layout based on the
     * current property values and the contained buttons.
     *
     * @param adapter the currently used platform adapter
     */
    @Override
    protected void initCells(PercentLayoutPlatformAdapter adapter)
    {
        int buttonCount = adapter.getComponentCount();
        int lastIndex = 2 * buttonCount;
        initDimensions(lastIndex + 1, ROW_SIZE);
        clearCells(lastIndex + 1, ROW_SIZE);

        initRowConstraints();
        initColumnConstraints(lastIndex, buttonCount);
        insertButtons(adapter, buttonCount);
    }

    /**
     * Initializes the row constraints.
     */
    protected void initRowConstraints()
    {
        CellConstraints.Builder cBuilder = getConstraintsBuilder();
        setRowConstraints(0, cBuilder.withMinimumSize(getTopMargin()).create());
        setRowConstraints(1, cBuilder.defaultRow().create());
        setRowConstraints(2, cBuilder.withMinimumSize(getBottomMargin())
                .create());
    }

    /**
     * Initializes the column constraints.
     *
     * @param lastIndex the index of the last column
     * @param buttonCount the number of buttons
     */
    protected void initColumnConstraints(int lastIndex, int buttonCount)
    {
        CellConstraints.Builder cBuilder = getConstraintsBuilder();
        setColumnConstraints(0, cBuilder.withCellAlignment(CellAlignment.FULL)
                .withMinimumSize(getLeftMargin()).withWeight(
                        calcWeightFactor(Alignment.LEFT)).create());
        setColumnConstraints(lastIndex, cBuilder.withCellAlignment(
                CellAlignment.FULL).withMinimumSize(getRightMargin())
                .withWeight(calcWeightFactor(Alignment.RIGHT)).create());

        CellConstraints ccGap = cBuilder.withMinimumSize(getGap()).create();
        for (int i = 2; i < lastIndex - 1; i += 2)
        {
            setColumnConstraints(i, ccGap);
        }

        CellConstraints ccBtn = cBuilder.withCellAlignment(CellAlignment.FULL)
                .withCellSize(CellSize.PREFERRED).create();
        int[] buttonIndices = new int[buttonCount];
        int btnIdx = 0;
        for (int i = 1; i < lastIndex; i += 2, btnIdx++)
        {
            buttonIndices[btnIdx] = i;
            setColumnConstraints(i, ccBtn);
        }

        if (buttonCount > 1)
        {
            addColumnGroup(CellGroup.fromArray(buttonIndices));
        }
    }

    /**
     * Inserts all buttons into the percent layout.
     *
     * @param adapter the platform adapter
     * @param buttonCount the number of available buttons
     */
    protected void insertButtons(PercentLayoutPlatformAdapter adapter,
            int buttonCount)
    {
        PercentData.Builder pcb = new PercentData.Builder();

        for (int i = 0; i < buttonCount; i++)
        {
            PercentData pd = pcb.pos(2 * i + 1, 1);
            initCell(adapter.getComponent(i), pd);
        }
    }

    /**
     * Calculates the weight factor for the left or right margin cell.
     *
     * @param align the alignment of this margin
     * @return the weight factor for this margin cell
     */
    private int calcWeightFactor(Alignment align)
    {
        if (getAlignment() == Alignment.CENTER)
        {
            return WEIGHT_HALF;
        }
        else
        {
            return (getAlignment() != align) ? WEIGHT_FULL : WEIGHT_NULL;
        }
    }

    /**
     * An enumeration class for the alignment of a {@code ButtonLayout}. The
     * alignment defines the horizontal orientation of the buttons in the
     * layout.
     *
     * @author Oliver Heger
     * @version $Id: ButtonLayout.java 205 2012-01-29 18:29:57Z oheger $
     */
    public enum Alignment
    {
        /**
         * The alignment <em>left</em>. All buttons are aligned to the left
         * margin of the layout. If more space becomes available, the right
         * margin grows.
         */
        LEFT,

        /**
         * The alignment <em>right</em>. All buttons are aligned to the right
         * margin of the layout. If more space becomes available, the left
         * margin grows.
         */
        RIGHT,

        /**
         * The alignment <em>center</em>. The buttons are centered in the
         * hosting container. If more space becomes available, both the left and
         * the right margin grow.
         */
        CENTER
    }
}
