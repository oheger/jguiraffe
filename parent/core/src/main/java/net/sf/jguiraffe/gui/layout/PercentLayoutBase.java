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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * <p>
 * The main class of the percent layout manager.
 * </p>
 * <p>
 * Percent layout provides a table-like layout, which is organized in columns
 * and rows, each of which defined by a {@link CellConstraints}
 * object. With these constraints objects it is possible to set a cell's
 * alignment and its minimum size. A cell can also be assigned weight factors
 * for its height and width. If there is more space available than needed by the
 * existing cells, the remaining space is divided and assigned to cells with a
 * weight factor greater than 0. So each cell can be given a certain percentage
 * of the remaining space, thus the name of this layout manager.
 * </p>
 * <p>
 * Sometimes certain columns or rows in the layout should have the same size,
 * even if they contain components with different preferred or minimum sizes. To
 * achieve this, {@link CellGroup} objects can be added to this
 * layout manager. These objects define the indices of the columns and rows,
 * which belong to the same group. All cells in a group have the same initial
 * size. To ensure that the affected cells have always the same size, their
 * weight factors must also be equal.
 * </p>
 * <p>
 * This class is an abstract base class that implements the complete layouting
 * algorithm. There will be concrete implementations for different layout types
 * that are based on the central percent layout functionality. These classes
 * will serve as adapters for specific layouts; they will create a layout
 * description that can be handled by this base class.
 * </p>
 * <p>
 * The family of percent layout classes is independent on a concrete GUI
 * library. It can work together e.g. with Swing or SWT. To achieve this access
 * to the managed GUI components is encapsulated by the
 * {@link net.sf.jguiraffe.gui.layout.PercentLayoutPlatformAdapter}
 * interface. A platform specific implementation of this interface must be
 * passed to an instance of this class.
 * </p>
 * <p>
 * Note: The {@code PercentLayout} class is not thread safe. It should be
 * accessed by a single thread (the GUI thread) only.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: PercentLayoutBase.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class PercentLayoutBase implements Serializable
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 20090730L;

    /** Constant for the delimiters for cell constraints. */
    private static final String CONSTRAINTS_DELIMITERS = " ,;";

    /** Stores the column constraints. */
    private CellConstraints[] columnConstraints;

    /** Stores the row constraints. */
    private CellConstraints[] rowConstraints;

    /** Stores information about the contained components. */
    private CellData[][] cells;

    /** The builder for creating constraints objects. */
    private transient CellConstraints.Builder constraintsBuilder;

    /** Stores a reference to the associated platform adapter. */
    private PercentLayoutPlatformAdapter platformAdapter;

    /** Stores the column groups. */
    private Collection<CellGroup> columnGroups;

    /** Stores the row groups. */
    private Collection<CellGroup> rowGroups;

    /** Stores information about components that span multiple columns. */
    private final List<CellData> multiColumns = new LinkedList<CellData>();

    /** Stores information about components that span multiple rows. */
    private final List<CellData> multiRows = new LinkedList<CellData>();

    /** Stores the total weight factor for all columns. */
    private int totalWeightX = -1;

    /** Stores the total weight factor for all rows. */
    private int totalWeightY = -1;

    /** Helper flag that avoids re-entrance of the initCells() method. */
    private volatile boolean inInit;

    /** A flag whether this layout can shrink below its preferred size. */
    private boolean canShrink = true;

    /**
     * Creates a new, uninitialized instance of <code>PercentLayoutBase</code>.
     * If this constructor is used, the concrete implementation of the
     * {@link #initCells(PercentLayoutPlatformAdapter)} method must perform all
     * initialization.
     */
    protected PercentLayoutBase()
    {
        super();
    }

    /**
     * Creates a new instance of <code>PercentLayoutBase</code> and sets the
     * numbers of the rows and columns. The constraints for the cells are set to
     * default values.
     *
     * @param cols the number of columns
     * @param rows the number of rows
     */
    protected PercentLayoutBase(int cols, int rows)
    {
        this();
        initDimensions(cols, rows);
    }

    /**
     * Creates a new instance of <code>PercentLayoutBase</code> and initializes
     * it. The constraints for the columns and rows are specified in the passed
     * collections, which must contain instances of
     * <code>{@link CellConstraints}</code>.
     *
     * @param colConstr a collection with column constraints
     * @param rowConstr a collection with row constraints
     */
    protected PercentLayoutBase(
            Collection<? extends CellConstraints> colConstr,
            Collection<? extends CellConstraints> rowConstr)
    {
        this();
        if (colConstr == null || colConstr.size() < 1 || rowConstr == null
                || rowConstr.size() < 1)
        {
            throw new IllegalArgumentException(
                    "Undefined column or row constraints!");
        }
        initFromCollections(colConstr, rowConstr);
    }

    /**
     * Creates a new instance of <code>PercentLayoutBase</code> and initializes
     * it. The column and row constraints are defined as strings. These strings
     * must contain valid specifications of cell constraints as defined in the
     * documentation of <code>{@link CellConstraints}</code>. As separators
     * between two cell definitions the following characters can be used: &quot;
     * ,;&quot;.
     *
     * @param colConstr a string defining column constraints
     * @param rowConstr a string defining row constraints
     */
    protected PercentLayoutBase(String colConstr, String rowConstr)
    {
        this();
        Collection<CellConstraints> cols = parseConstraints(colConstr, true);
        Collection<CellConstraints> rows = parseConstraints(rowConstr, false);
        initFromCollections(cols, rows);
    }

    /**
     * Returns the platform adapter associated with this layout class.
     *
     * @return the platform adapter
     */
    public PercentLayoutPlatformAdapter getPlatformAdapter()
    {
        return platformAdapter;
    }

    /**
     * Sets the platform adapter for this layout manager. This adapter allows
     * access to and manipulation of the managed components.
     *
     * @param platformAdapter the platform adapter to use
     */
    public void setPlatformAdapter(PercentLayoutPlatformAdapter platformAdapter)
    {
        this.platformAdapter = platformAdapter;
    }

    /**
     * Returns a flag whether this layout can shrink below its preferred size.
     *
     * @return a flag whether this layout can shrink below its preferred size
     */
    public boolean isCanShrink()
    {
        return canShrink;
    }

    /**
     * Sets a flag whether this layout can shrink below its preferred size. If
     * this flag is set and the space available for the hosting container
     * becomes smaller than the layout's preferred size, the layout tries to
     * reduce its size further using the minimum size defined for the components
     * contained.
     *
     * @param canShrink the shrink flag
     */
    public void setCanShrink(boolean canShrink)
    {
        this.canShrink = canShrink;
    }

    /**
     * Returns the number of columns in this layout.
     *
     * @return the number of columns
     */
    public int getColumnCount()
    {
        ensureInit();
        return columnConstraints.length;
    }

    /**
     * Returns the number of rows in this layout.
     *
     * @return the number of rows
     */
    public int getRowCount()
    {
        ensureInit();
        return rowConstraints.length;
    }

    /**
     * Returns the column constraints object for the column with the given
     * index.
     *
     * @param idx the index (0 based)
     * @return the column constraints object for this column
     */
    public CellConstraints getColumnConstraints(int idx)
    {
        return getInternalAllColumnConstraints()[idx];
    }

    /**
     * Sets the column constraints object for the column with the given index.
     *
     * @param idx the index of the column (0 based)
     * @param cc the constraints object
     */
    public void setColumnConstraints(int idx, CellConstraints cc)
    {
        columnConstraints[idx] = cc;
        totalWeightX = -1;
    }

    /**
     * Returns an array with the current column constraints.
     *
     * @return the column constraints
     */
    public CellConstraints[] getAllColumnConstraints()
    {
        return getInternalAllColumnConstraints().clone();
    }

    /**
     * Returns the row constraints object for the row with the given index.
     *
     * @param idx the index (0 based)
     * @return the row constraints object for this row
     */
    public CellConstraints getRowConstraints(int idx)
    {
        return getInternalAllRowConstraints()[idx];
    }

    /**
     * Sets the row constraints object for the row with the given index.
     *
     * @param idx the index of the row (0 based)
     * @param cc the constraints object
     */
    public void setRowConstraints(int idx, CellConstraints cc)
    {
        rowConstraints[idx] = cc;
        totalWeightY = -1;
    }

    /**
     * Returns an array with the current row constraints.
     *
     * @return the row constraints
     */
    public CellConstraints[] getAllRowConstraints()
    {
        return getInternalAllRowConstraints().clone();
    }

    /**
     * Returns an unmodifiable collection with the column groups defined for
     * this layout. This collection may be empty, but never <b>null</b>.
     *
     * @return a collection with the <code>CellGroup</code> objects for columns
     */
    public Collection<CellGroup> getColumnGroups()
    {
        ensureInit();
        return unmodifiableCellGroups(columnGroups);
    }

    /**
     * Adds a <code>CellGroup</code> object for columns to this layout manager.
     * This causes the columns defined by this group object to have the same
     * width (as long as their weight factors are equal).
     *
     * @param grp the group object
     */
    public void addColumnGroup(CellGroup grp)
    {
        if (columnGroups == null)
        {
            columnGroups = new LinkedList<CellGroup>();
        }
        columnGroups.add(grp);
    }

    /**
     * Returns an unmodifiable collection with the row groups defined for this
     * layout. This collection may be empty, but never <b>null </b>.
     *
     * @return a collection with the <code>CellGroup</code> objects for rows
     */
    public Collection<CellGroup> getRowGroups()
    {
        return unmodifiableCellGroups(rowGroups);
    }

    /**
     * Adds a <code>CellGroup</code> object for rows to this layout manager.
     * This causes the rows defined by this group object to have the same height
     * (as long as their weight factors are equal).
     *
     * @param grp the group object
     */
    public void addRowGroup(CellGroup grp)
    {
        if (rowGroups == null)
        {
            rowGroups = new LinkedList<CellGroup>();
        }
        rowGroups.add(grp);
    }

    /**
     * Clears all cached values. Can be called if something has changed at the
     * associated container.
     */
    public void flushCache()
    {
        totalWeightX = -1;
        totalWeightY = -1;
        cells = null;
    }

    /**
     * Removes the specified component from this layout.
     *
     * @param comp the component to remove
     * @return a flag whether the component was found and could be removed
     */
    public boolean removeComponent(Object comp)
    {
        for (int i = 0; i < getColumnCount(); i++)
        {
            for (int j = 0; j < getRowCount(); j++)
            {
                Object c = getComponent(i, j);
                if (c != null && comp == c)
                {
                    PercentData pd = getPercentData(i, j);
                    if (pd.getSpanX() > 1)
                    {
                        removeMultiSpanComponent(multiColumns, comp);
                    }
                    if (pd.getSpanY() > 1)
                    {
                        removeMultiSpanComponent(multiRows, comp);
                    }
                    cells[i][j] = null;
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns a reference to the associated platform adapter. If no such
     * adapter has been set, an <code>IllegalStateException</code> exception is
     * thrown.
     *
     * @return the platform adapter
     * @throws IllegalStateException if no platform adapter is set
     */
    protected final PercentLayoutPlatformAdapter fetchPlatformAdapter()
    {
        if (getPlatformAdapter() == null)
        {
            throw new IllegalStateException(
                    "No platform adapter set for this percent layout!");
        }
        return getPlatformAdapter();
    }

    /**
     * Returns the builder instance for creating {@link CellConstraints}
     * objects. Each instance of this class is associated with such a builder.
     * Sub classes or clients can use it for creating their constraints.
     *
     * @return the builder instance for creating {@link CellConstraints} objects
     */
    public final CellConstraints.Builder getConstraintsBuilder()
    {
        if (constraintsBuilder == null)
        {
            // create on demand
            // this is not thread-safe; however, we have stated in the class
            // comment that the whole class is not thread-safe
            constraintsBuilder = new CellConstraints.Builder();
        }
        return constraintsBuilder;
    }

    /**
     * Initializes the dimensions of the table that holds the layout. Constructs
     * the internal arrays with the appropriate sizes and initializes them with
     * default values.
     *
     * @param cols the number of columns
     * @param rows the number of rows
     */
    protected final void initDimensions(int cols, int rows)
    {
        if (cols < 1 || rows < 1)
        {
            throw new IllegalArgumentException(
                    "Number of columns or rows must be greater 0!");
        }

        columnConstraints = new CellConstraints[cols];
        CellConstraints defColumn = getConstraintsBuilder().defaultColumn()
                .create();
        for (int i = 0; i < cols; i++)
        {
            columnConstraints[i] = defColumn;
        }
        rowConstraints = new CellConstraints[rows];
        CellConstraints defRow = getConstraintsBuilder().defaultRow().create();
        for (int i = 0; i < rows; i++)
        {
            rowConstraints[i] = defRow;
        }
    }

    /**
     * Returns the internal array of all cell constraints objects for the
     * layout's column. This method can be used by subclasses for direct
     * read-only access to the layout's column constraints. The public
     * {@link #getAllColumnConstraints()} method returns a defensive copy of
     * this array. So for performance reasons this method should be used by
     * subclasses.
     *
     * @return the array with the layout's column constraints objects
     */
    protected final CellConstraints[] getInternalAllColumnConstraints()
    {
        ensureInit();
        return columnConstraints;
    }

    /**
     * Returns the internal array of all cell constraints objects for the
     * layout's rows. This method can be used by subclasses for direct read-only
     * access to the layout's row constraints. The public
     * {@link #getAllRowConstraints()} method returns a defensive copy of this
     * array. So for performance reasons this method should be used by
     * subclasses.
     *
     * @return the array with the layout's row constraints objects
     */
    protected final CellConstraints[] getInternalAllRowConstraints()
    {
        ensureInit();
        return rowConstraints;
    }

    /**
     * Returns the component at the specified position of this layout.
     *
     * @param col the column
     * @param row the row
     * @return the component at this position (<b>null</b> if this cell is not
     *         occupied)
     */
    protected Object getComponent(int col, int row)
    {
        CellData cd = getCellData(col, row);
        return (cd != null) ? cd.getComponent() : null;
    }

    /**
     * Returns the constraints object for the component at the specified
     * position of this layout.
     *
     * @param col the column
     * @param row the row
     * @return the constraints at this position (<b>null</b> if this cell is not
     *         occupied)
     */
    protected PercentData getPercentData(int col, int row)
    {
        CellData cd = getCellData(col, row);
        return (cd != null) ? cd.getConstraints() : null;
    }

    /**
     * Checks the specified constraints object. This method ensures that the
     * passed in constraints object is an instance of {@link PercentData} and
     * that only valid values for column and row number and the spans are
     * accepted. If invalid values are detected, an {@code
     * IllegalStateException} exception is thrown.
     *
     * @param constraintsObj the constraints to check
     * @return the percent data object to use
     * @throws IllegalStateException if invalid constraints are detected
     */
    protected PercentData checkConstraints(Object constraintsObj)
    {
        if (!(constraintsObj instanceof PercentData))
        {
            throw new IllegalStateException(
                    "A constraints object of type PercentData must be provided: "
                            + constraintsObj);
        }

        PercentData constraints = (PercentData) constraintsObj;
        if (constraints.getColumn() + constraints.getSpanX() > getColumnCount()
                || constraints.getRow() + constraints.getSpanY() > getRowCount())
        {
            throw new IllegalStateException("Invalid column or row span: "
                    + constraints);
        }

        return constraints;
    }

    /**
     * Initializes the specified cell in the table-like layout. Information
     * about the component and its associated constraints are stored. This
     * method also checks whether the constrains object is of type
     * <code>{@link PercentData}</code> and that its indices and dimensions are
     * valid.
     *
     * @param component the component
     * @param constraints the constraints of this component
     */
    protected void initCell(Object component, Object constraints)
    {
        PercentData pd = checkConstraints(constraints);
        CellData cd = new CellData(component, pd);
        cells[pd.getColumn()][pd.getRow()] = cd;
    }

    /**
     * Resets the states of all cells in the layout. Uses the current column and
     * row count (which must have been initialized before).
     */
    protected void clearCells()
    {
        clearCells(getColumnCount(), getRowCount());
    }

    /**
     * Resets the states of all cells in the layout. After this method was
     * called the layout does not contain any information about components and
     * their constraints.
     *
     * @param columns the number of columns
     * @param rows the number of rows
     */
    protected void clearCells(int columns, int rows)
    {
        cells = new CellData[columns][rows];
    }

    /**
     * Returns a list with {@code CellData} objects for the components that span
     * multiple columns. This method exists mainly for testing purposes.
     *
     * @return a list with {@code CellData} objects for multi-column components
     */
    List<CellData> getMultiColumnData()
    {
        ensureInit();
        return multiColumns;
    }

    /**
     * Returns a list with {@code CellData} objects for the components that span
     * multiple rows. This method exists mainly for testing purposes.
     *
     * @return a list with {@code CellData} objects for multi-row components
     */
    List<CellData> getMultiRowData()
    {
        ensureInit();
        return multiRows;
    }

    /**
     * Calculates the final cell sizes in one direction (horizontal or
     * vertical). This method takes all factors related to sizing into account:
     * the cell constraints, cell groups, weight factors, and the shrinking
     * flag. The resulting array with cell sizes can then be passed to
     * {@link #performLayout(Object, int[], int[], int[], int[])}.
     *
     * @param constraints an array with the constraints for the columns or rows
     * @param count the number of cells in the opposite dimension (i.e. if
     *        columns are calculated, the number of rows and vice versa)
     * @param cellGroups a collection with the cell groups
     * @param container the container this layout belongs to
     * @param containerSize the total size available for the container
     * @param vert a flag if this calculation is for the X or Y direction
     * @return an array with the cell sizes
     */
    public int[] calcSizes(CellConstraints[] constraints, int count,
            Collection<CellGroup> cellGroups, Object container,
            int containerSize, boolean vert)
    {
        // get cell sizes for preferred size
        int[] sizes = calcCellSizesWithGroups(constraints, count, cellGroups,
                container, false, vert);

        // is there enough space?
        int requiredSpace = sumUpSizes(sizes);
        if (requiredSpace > containerSize && isCanShrink())
        {
            // no, try again with minimum size
            sizes = calcCellSizesWithGroups(constraints, count, cellGroups,
                    container, true, vert);
        }

        // distribute remaining space
        applyWeightFactors(sizes, containerSize, vert);
        return sizes;
    }

    /**
     * Layouts all components that are contained in the associated container.
     * This method determines the positions and sizes of all affected
     * components.
     *
     * @param container the container
     * @param colSizes an array with the sizes of all columns
     * @param rowSizes an array with the sizes of all rows
     * @param colPos the start positions of all columns
     * @param rowPos the start positions of all rows
     */
    public void performLayout(Object container, int[] colSizes, int[] rowSizes,
            int[] colPos, int[] rowPos)
    {
        Rectangle rect = new Rectangle();
        for (int col = 0; col < getColumnCount(); col++)
        {
            for (int row = 0; row < getRowCount(); row++)
            {
                if (getPercentData(col, row) != null)
                {
                    alignComponent(rect, col, row, colSizes, colPos[col], col,
                            container, false);
                    alignComponent(rect, col, row, rowSizes, rowPos[row], row,
                            container, true);
                    setComponentBounds(col, row, rect);
                }
            }
        }
    }

    /**
     * Layouts all components in the associated container calculating all
     * necessary intermediate sizes and positions. This method calculates the
     * cell sizes and positions and then delegates to the overloaded
     * {@code performLayout()} method.
     *
     * @param container the container
     * @param insets a rectangle with the container's insets
     * @param size the size of the container
     * @since 1.3
     */
    public void performLayout(Object container, Rectangle insets, Dimension size)
    {
        int[] colSizes =
                calcSizes(getAllColumnConstraints(), getRowCount(),
                        getColumnGroups(), container, size.width - insets.x
                                - insets.width, false);
        int[] rowSizes =
                calcSizes(getAllRowConstraints(), getColumnCount(),
                        getRowGroups(), container, size.height - insets.y
                                - insets.width, true);
        int[] colPos = calcCellPositions(colSizes, insets.x);
        int[] rowPos = calcCellPositions(rowSizes, insets.y);
        performLayout(container, colSizes, rowSizes, colPos, rowPos);
    }

    /**
     * Returns the preferred size of this layout. This method applies all cell
     * constraints to determine the optimum size of the layout.
     *
     * @param container the associated container object
     * @return the preferred layout size
     */
    public Dimension calcPreferredLayoutSize(Object container)
    {
        return calcLayoutSize(container, false);
    }

    /**
     * Returns the minimum size of this layout. The behavior of this method
     * depends on the {@code canShrink} flag: if this flag is <b>false</b>, it
     * returns the same result as {@link #calcPreferredLayoutSize(Object)} -
     * because the layout cannot shrink below its preferred size. Otherwise, a
     * size calculation is performed based on the component's minimum size
     * rather than their preferred sizes. Note that this mainly makes a
     * difference if cell constraints are used with the {@link CellSize}
     * <em>preferred</em>.
     *
     * @param container the associated container object
     * @return the minimum layout size
     */
    public Dimension calcMinimumLayoutSize(Object container)
    {
        return calcLayoutSize(container, isCanShrink());
    }

    /**
     * Calculates either the preferred or the minimum layout size.
     *
     * @param container the associated container object
     * @param minimum flag for minimum (<b>true</b>) or preferred (<b>false</b>)
     *        size
     * @return the corresponding layout size
     */
    private Dimension calcLayoutSize(Object container, boolean minimum)
    {
        int[] colSizes = calcCellSizesWithGroups(getInternalAllColumnConstraints(),
                getRowCount(), getColumnGroups(), container, minimum, false);
        int[] rowSizes = calcCellSizesWithGroups(getInternalAllRowConstraints(),
                getColumnCount(), getRowGroups(), container, minimum, true);
        return new Dimension(sumUpSizes(colSizes), sumUpSizes(rowSizes));
    }

    /**
     * Initializes this instance from the given collections with
     * {@link CellConstraints} objects.
     *
     * @param colConstr a collection with column constraints object
     * @param rowConstr a collection with row constraints object
     */
    protected final void initFromCollections(
            Collection<? extends CellConstraints> colConstr,
            Collection<? extends CellConstraints> rowConstr)
    {
        columnConstraints = copyConstraints(colConstr);
        rowConstraints = copyConstraints(rowConstr);
    }

    /**
     * Calculates the minimum size of either the columns or the rows in the
     * layout. Because of the passed in orientation flag this method can operate
     * on both columns and rows.
     *
     * @param constraints an array with the constraints for the columns or rows
     * @param count the number of cells in the opposite dimension (i.e. if
     *        columns are calculated, the number of rows and vice versa)
     * @param container the container this layout belongs to
     * @param minimum a flag whether the minimum size should be returned
     * @param vert a flag if this calculation is for the X or Y direction
     * @return an array with the cell sizes
     */
    protected int[] calcCellSizes(CellConstraints[] constraints, int count,
            Object container, boolean minimum, boolean vert)
    {
        int[] sizes = new int[constraints.length];
        for (int i = 0; i < constraints.length; i++)
        {
            sizes[i] = calcComponentSizes(constraints[i], i, count, container,
                    minimum, vert);
        }

        handleMultiSpans(sizes, constraints, vert ? multiRows : multiColumns,
                container, minimum, vert);
        return sizes;
    }

    /**
     * Calculates the size of either a column or a row in the layout. This
     * method iterates over all the components in the actual column or row.
     * Depending on their constraints either their minimum, their preferred or a
     * specified fix size is fetched, and the maximum of these sizes is
     * determined. Only components with a span of 1 are taken into account. With
     * the {@code minimum} parameter it is possible to force the method to
     * always return the minimum size. This is required if there is less space
     * available than is required for the preferred width.
     *
     * @param constraints the constraints object for the actual column or row
     * @param index the index of the actual column or row
     * @param count the number of cells in the opposite dimension
     * @param container the container this layout belongs to
     * @param minimum a flag whether the minimum size should be returned
     * @param vert a flag if this calculation is for the X or Y direction
     * @return the size of the actual column or row
     */
    protected int calcComponentSizes(CellConstraints constraints, int index,
            int count, Object container, boolean minimum, boolean vert)
    {
        int size = constraints.getMinSize().toPixel(getSizeHandler(),
                container, vert);
        int colIdx = 0;
        int rowIdx = 0;
        if (vert)
        {
            rowIdx = index;
        }
        else
        {
            colIdx = index;
        }

        for (int i = 0; i < count; i++)
        {
            if (vert)
            {
                colIdx = i;
            }
            else
            {
                rowIdx = i;
            }
            PercentData pd = getPercentData(colIdx, rowIdx);
            if (pd != null
                    && 1 == getOrientationValue(pd.getSpanX(), pd.getSpanY(),
                            vert))
            {
                int cellSize = calcCellSize(pd, colIdx, rowIdx, container,
                        minimum, vert);
                if (cellSize > size)
                {
                    size = cellSize;
                }
            }
        }

        return size;
    }

    /**
     * Checks the sizes of components that span multiple cells. For these cells
     * it must be tested whether their size fits into the cell sizes so far
     * calculated. If this is not the case, cells, for which this is possible,
     * must be enlarged.
     *
     * @param sizes an array with the so far calculated cell sizes
     * @param constraints an array with all cell constraints
     * @param components the list with the multi span components
     * @param container the container object
     * @param minimum a flag whether the minimum size should be returned
     * @param vert the orientation flag
     */
    protected void handleMultiSpans(int[] sizes, CellConstraints[] constraints,
            List<CellData> components, Object container, boolean minimum,
            boolean vert)
    {
        for (CellData cd : components)
        {
            PercentData pd = cd.getConstraints();
            int size = calcComponentSize(pd, cd.getComponent(), container,
                    minimum, vert);
            int idx1 = getOrientationValue(pd.getColumn(), pd.getRow(), vert);
            int idx2 = idx1
                    + getOrientationValue(pd.getSpanX(), pd.getSpanY(), vert);

            // determine available size
            int cellSize = 0;
            for (int i = idx1; i < idx2; i++)
            {
                cellSize += sizes[i];
            }

            if (size > cellSize)
            {
                enlargeCells(sizes, constraints, idx1, idx2, size - cellSize);
            }
        }
    }

    /**
     * Enlarges the cells in the specified index range by the given amount. This
     * method determines how many cells can be enlarged (by inspecting their
     * cell constraints). If there are any, they will be enlarged by the same
     * part.
     *
     * @param sizes an array with the so far calculated cell sizes
     * @param constraints the constraints
     * @param idx1 the first index
     * @param idx2 the end index (excluding)
     * @param amount the enlargement amount
     */
    private void enlargeCells(int[] sizes, CellConstraints[] constraints,
            int idx1, int idx2, int amount)
    {
        int cnt = 0;
        for (int i = idx1; i < idx2; i++)
        {
            if (constraints[i].getCellSize() != CellSize.NONE)
            {
                cnt++;
            }
        }

        if (cnt > 0)
        {
            int factor = amount / cnt;
            int modulo = amount % cnt;
            for (int i = idx1; i < idx2 && cnt > 0; i++)
            {
                if (constraints[i].getCellSize() != CellSize.NONE)
                {
                    sizes[i] += factor + ((modulo-- > 0) ? 1 : 0);
                    cnt--;
                }
            }
        }
    }

    /**
     * Determines the size of a single cell. Evaluates the constraints of this
     * cell and depending on the size value either the minimum, the preferred or
     * a fixed size is returned.
     *
     * @param pd the constraints object
     * @param colIdx the column index
     * @param rowIdx the row index
     * @param container the container this layout belongs to
     * @param minimum a flag whether the minimum size should be returned
     * @param vert a flag if this calculation is for the X or Y direction
     * @return the cell's size
     */
    protected int calcCellSize(PercentData pd, int colIdx, int rowIdx,
            Object container, boolean minimum, boolean vert)
    {
        return calcComponentSize(pd, getComponent(colIdx, rowIdx), container,
                minimum, vert);
    }

    /**
     * Determines the size of a component based on the given constraints object.
     *
     * @param pd the constraints object
     * @param comp the affected component
     * @param container the container this layout belongs to
     * @param minimum a flag whether the minimum size should be returned
     * @param vert a flag if this calculation is for the X or Y direction
     * @return the component's size
     */
    protected int calcComponentSize(PercentData pd, Object comp,
            Object container, boolean minimum, boolean vert)
    {
        CellConstraints constr = constraintsFor(pd, vert);
        int sz;

        if (constr.getCellSize() == CellSize.NONE)
        {
            sz = 0;
        }
        else if (minimum || constr.getCellSize() == CellSize.MINIMUM)
        {
            sz = fetchPlatformAdapter().getMinimumComponentSize(comp, vert);
        }
        else
        {
            sz = fetchPlatformAdapter().getPreferredComponentSize(comp, vert);
        }

        return Math.max(sz, constr.getMinSize().toPixel(getSizeHandler(),
                container, vert));
    }

    /**
     * Applies the defined cell groups to the so far calculated cell sizes.
     *
     * @param sizes an array with the (initial or minimum) cell sizes
     * @param cellGroups a collection with the defined cell groups
     */
    protected void applyCellGroups(int[] sizes, Collection<CellGroup> cellGroups)
    {
        for (CellGroup group : cellGroups)
        {
            group.apply(sizes);
        }
    }

    /**
     * Calculates the sizes of all columns or rows. This is a convenience
     * method, which combines calls to <code>calcCellSizes()</code> and
     * <code>applyCellGroups()</code>.
     *
     * @param constraints an array with the constraints for the columns or rows
     * @param count the number of cells in the opposite dimension (i.e. if
     *        columns are calculated, the number of rows and vice versa)
     * @param cellGroups a collection with the defined cell groups
     * @param container the container this layout belongs to
     * @param minimum a flag whether the minimum size should be returned
     * @param vert a flag if this calculation is for the X or Y direction
     * @return an array with the cell sizes
     * @throws NullPointerException if a required parameter is missing
     */
    protected int[] calcCellSizesWithGroups(CellConstraints[] constraints,
            int count, Collection<CellGroup> cellGroups, Object container,
            boolean minimum, boolean vert)
    {
        int[] sizes = calcCellSizes(constraints, count, container, minimum, vert);
        applyCellGroups(sizes, cellGroups);
        return sizes;
    }

    /**
     * Helper method for calculating the total weight factor.
     *
     * @param constraints an array with cell constraints
     * @return the total weight factor
     */
    protected int calcTotalWeight(CellConstraints[] constraints)
    {
        int result = 0;
        for (CellConstraints cc : constraints)
        {
            result += cc.getWeight();
        }
        return result;
    }

    /**
     * Returns the total weight factor for columns.
     *
     * @return the total column weight
     */
    protected int getTotalWeightX()
    {
        if (totalWeightX < 0)
        {
            totalWeightX = calcTotalWeight(columnConstraints);
        }
        return totalWeightX;
    }

    /**
     * Returns the total weight factor for rows.
     *
     * @return the total row weight
     */
    protected int getTotalWeightY()
    {
        if (totalWeightY < 0)
        {
            totalWeightY = calcTotalWeight(rowConstraints);
        }
        return totalWeightY;
    }

    /**
     * Processes the cells with a weight factor larger than 0. The remaining
     * available space is calculated and divided between the cells with a
     * defined weight factor.
     *
     * @param sizes the cell sizes without weight factors
     * @param containerSize the size of the container (without insets)
     * @param constraints the cell constraints
     * @param totalWeight the total weight factor
     */
    protected void applyWeightFactors(int[] sizes, int containerSize,
            CellConstraints[] constraints, int totalWeight)
    {
        if (totalWeight > 0)
        {
            int remaining = containerSize - sumUpSizes(sizes);
            if (remaining > 0)
            {
                for (int i = 0; i < constraints.length; i++)
                {
                    if (constraints[i].getWeight() > 0)
                    {
                        sizes[i] += (remaining * constraints[i].getWeight())
                                / totalWeight;
                    }
                }
            }
        }
    }

    /**
     * Processes the cells with a weight factor larger than 0. The remaining
     * available space is calculated and divided between the cells with a
     * defined weight factor.
     *
     * @param sizes the cell sizes without weight factors
     * @param containerSize the size of the container (without insets)
     * @param vert a flag if this calculation is for the X or Y direction
     */
    protected void applyWeightFactors(int[] sizes, int containerSize,
            boolean vert)
    {
        if (vert)
        {
            applyWeightFactors(sizes, containerSize, rowConstraints,
                    getTotalWeightY());
        }
        else
        {
            applyWeightFactors(sizes, containerSize, columnConstraints,
                    getTotalWeightX());
        }
    }

    /**
     * Calculates the start positions of all cells in a column or row.
     *
     * @param sizes the cell sizes (must not be <b>null</b>)
     * @param startPos the start position
     * @return an array with the start positions for all cells
     * @throws NullPointerException if the array with sizes is <b>null</b>
     */
    public int[] calcCellPositions(int[] sizes, int startPos)
    {
        int[] pos = new int[sizes.length];
        int p = startPos;
        for (int i = 0; i < sizes.length; p += sizes[i++])
        {
            pos[i] = p;
        }
        return pos;
    }

    /**
     * Aligns the specified component.
     *
     * @param bounds stores the bounds of the component
     * @param colIdx the column index
     * @param rowIdx the row index
     * @param sizes an array with the sizes of all cells
     * @param startPos the start position of the actual cell
     * @param idx the actual cell index
     * @param container the container this layout belongs to
     * @param vert a flag if this calculation is for the X or Y direction
     */
    protected void alignComponent(Rectangle bounds, int colIdx, int rowIdx,
            int[] sizes, int startPos, int idx, Object container, boolean vert)
    {
        // Determine available space, including cell spanning
        PercentData pd = getPercentData(colIdx, rowIdx);
        int span = getOrientationValue(pd.getSpanX(), pd.getSpanY(), vert);
        int availSpace = 0;
        for (int i = 0; i < span; i++)
        {
            availSpace += sizes[idx + i];
        }

        // Determine alignment
        int pos;
        int cellSize;
        if (constraintsFor(pd, vert).getAlignment() == CellAlignment.FULL)
        {
            // component fills the whole area
            pos = 0;
            cellSize = availSpace;
        }

        else
        {
            cellSize = calcCellSize(pd, colIdx, rowIdx, container, false, vert);
            CellAlignment align = constraintsFor(pd, vert).getAlignment();
            if (align == CellAlignment.CENTER)
            {
                pos = (availSpace - cellSize) >> 1;
            }
            else if (align == CellAlignment.END)
            {
                pos = availSpace - cellSize;
            }
            else
            {
                pos = 0;
            }
        }

        if (vert)
        {
            bounds.y = startPos + pos;
            bounds.height = cellSize;
        }
        else
        {
            bounds.x = startPos + pos;
            bounds.width = cellSize;
        }
    }

    /**
     * Helper method for copying a collection with constraints objects into an
     * array. If a constraints object is <b>null </b>, an exception is thrown.
     *
     * @param constr the collection with the constraints
     * @return an array with constraints
     * @throws IllegalArgumentException if the collection contains a <b>null</b>
     *         constraint
     */
    private static CellConstraints[] copyConstraints(
            Collection<? extends CellConstraints> constr)
    {
        CellConstraints[] result = new CellConstraints[constr.size()];
        Iterator<? extends CellConstraints> it = constr.iterator();
        for (int idx = 0; it.hasNext(); idx++)
        {
            result[idx] = it.next();
            if (result[idx] == null)
            {
                throw new IllegalArgumentException(
                        "Cell constraints must not be null!");
            }
        }
        return result;
    }

    /**
     * Helper method for parsing a string with cell definitions.
     *
     * @param constr the string
     * @param col a flag if column or row definitions are to be parsed
     * @return a collection with the corresponding constraints objects
     * @throws IllegalArgumentException if the string is invalid
     */
    private Collection<CellConstraints> parseConstraints(String constr,
            boolean col)
    {
        CellAlignment oldAlign = getConstraintsBuilder().getDefaultAlignment();
        getConstraintsBuilder().setDefaultAlignment(
                col ? CellAlignment.FULL : CellAlignment.CENTER);

        try
        {
            getConstraintsBuilder().reset();
            Collection<CellConstraints> result = new LinkedList<CellConstraints>();
            if (constr != null)
            {
                StringTokenizer tok = new StringTokenizer(constr,
                        CONSTRAINTS_DELIMITERS);
                while (tok.hasMoreTokens())
                {
                    result.add(getConstraintsBuilder().fromString(
                            tok.nextToken()));
                }
            }

            if (result.size() < 1)
            {
                throw new IllegalArgumentException(
                        "Undefined cell constraints!");
            }
            return result;
        }
        finally
        {
            getConstraintsBuilder().setDefaultAlignment(oldAlign);
        }
    }

    /**
     * Helper method for extracting a value from a 2D vector with the specified
     * orientation.
     *
     * @param v1 the x value
     * @param v2 the y value
     * @param vert the orientation flag (<b>true</b> for the y value,
     *        <b>false</b> for the x value)
     * @return the extracted value
     */
    public static int getOrientationValue(int v1, int v2, boolean vert)
    {
        return vert ? v2 : v1;
    }

    /**
     * Returns the minimum size of the component at the specified column and row
     * position in the given orientation.
     *
     * @param col the column index
     * @param row the row index
     * @param vert the orientation flag
     * @return the minimum size of the specified component
     */
    protected int getMinimumComponentSize(int col, int row, boolean vert)
    {
        return fetchPlatformAdapter().getMinimumComponentSize(
                getComponent(col, row), vert);
    }

    /**
     * Returns the preferred size of the component at the specified column and
     * row position in the given orientation.
     *
     * @param col the column index
     * @param row the row index
     * @param vert the orientation flag
     * @return the minimum size of the specified component
     */
    protected int getPreferredComponentSize(int col, int row, boolean vert)
    {
        return fetchPlatformAdapter().getPreferredComponentSize(
                getComponent(col, row), vert);
    }

    /**
     * Sets the bounds of the component at the specified column and row
     * position. This method is called after the exact position of this
     * component has been determined by the layout algorithm.
     *
     * @param col the column index
     * @param row the row index
     * @param bounds the new bounds for this component
     */
    protected void setComponentBounds(int col, int row, Rectangle bounds)
    {
        fetchPlatformAdapter().setComponentBounds(getComponent(col, row),
                bounds);
    }

    /**
     * Returns the currently used size handler implementation.
     *
     * @return the size handler implementation
     */
    protected UnitSizeHandler getSizeHandler()
    {
        return fetchPlatformAdapter().getSizeHandler();
    }

    /**
     * Returns the constraints object for the specified percent data and the
     * given orientation. If the percent data contains already a constraints
     * object for the given orientation, this object is returned. Otherwise the
     * constraints of the hosting cell (the target cell) are returned.
     *
     * @param pd the percent data object
     * @param vert the orientation flag
     * @return the constraints for this percent data object
     */
    protected CellConstraints constraintsFor(PercentData pd, boolean vert)
    {
        CellConstraints c = pd.getConstraints(vert);

        if (c == null)
        {
            c = vert ? getRowConstraints(targetRow(pd))
                    : getColumnConstraints(targetColumn(pd));
        }

        return c;
    }

    /**
     * Initializes the whole layout. This method is called on first access to
     * the layout information or whenever the layout changes. A concrete sub
     * class must here implement its initialization algorithm, which creates a
     * valid percent layout.
     *
     * @param adapter the currently used platform adapter
     */
    protected abstract void initCells(PercentLayoutPlatformAdapter adapter);

    /**
     * Determines the target column of the given constraints object. If a target
     * column is set, it is used. Otherwise the column of the data object is
     * returned.
     *
     * @param pd the {@code PercentData} object
     * @return the target column
     */
    private static int targetColumn(PercentData pd)
    {
        return (pd.getTargetColumn() <= PercentData.POS_UNDEF) ? pd.getColumn()
                : pd.getTargetColumn();
    }

    /**
     * Determines the target row of the given constraints object. If a target
     * row is set, it is used. Otherwise the row of the data object is returned.
     *
     * @param pd the {@code PercentData} object
     * @return the target row
     */
    private static int targetRow(PercentData pd)
    {
        return (pd.getTargetRow() <= PercentData.POS_UNDEF) ? pd.getRow() : pd
                .getTargetRow();
    }

    /**
     * Returns the <code>CellData</code> object for the specified cell. Ensures
     * that the cells have been initialized.
     *
     * @param col the column
     * @param row the row
     * @return the cell data object at this position (can be <b>null</b>)
     */
    private CellData getCellData(int col, int row)
    {
        ensureInit();
        return cells[col][row];
    }

    /**
     * Ensures that the layout has been initialized. This method is called by
     * several accessor methods.
     */
    private void ensureInit()
    {
        if (!inInit && cells == null)
        {
            inInit = true;
            try
            {
                initCells(fetchPlatformAdapter());
                initMultiCells();
            }
            finally
            {
                inInit = false;
            }
        }
    }

    /**
     * Helper method for obtaining information about components that span
     * multiple cells. If these components are associated a target cell, they
     * are stored at a special location.
     */
    private void initMultiCells()
    {
        int cols = getColumnCount();
        int rows = getRowCount();
        multiColumns.clear();
        multiRows.clear();

        for (int i = 0; i < cols; i++)
        {
            for (int j = 0; j < rows; j++)
            {
                PercentData pd = getPercentData(i, j);
                if (pd != null)
                {
                    if (pd.getSpanX() > 1)
                    {
                        multiColumns.add(getCellData(i, j));
                    }
                    if (pd.getSpanY() > 1)
                    {
                        multiRows.add(getCellData(i, j));
                    }
                }
            }
        }
    }

    /**
     * Calculates the width or the height of the associated component. This
     * method calculates the sum of the already determined cell sizes.
     *
     * @param sizes an array with the cell sizes
     * @return the layout size
     */
    private static int sumUpSizes(int[] sizes)
    {
        int result = 0;
        for (int sz : sizes)
        {
            result += sz;
        }

        return result;
    }

    /**
     * Removes a component from the multi span list.
     *
     * @param lst the multi span list
     * @param comp the component to remove
     */
    private static void removeMultiSpanComponent(List<CellData> lst, Object comp)
    {
        for (Iterator<CellData> it = lst.iterator(); it.hasNext();)
        {
            CellData cd = it.next();
            if (cd.getComponent() == comp)
            {
                it.remove();
                break;
            }
        }
    }

    /**
     * Helper method for returning an unmodifiable collection of cell groups.
     *
     * @param groups the source collection with cell groups
     * @return the unmodifiable collection
     */
    private static Collection<CellGroup> unmodifiableCellGroups(
            Collection<CellGroup> groups)
    {
        if (groups == null)
        {
            return Collections.emptyList();
        }
        else
        {
            return Collections.unmodifiableCollection(groups);
        }
    }

    /**
     * A helper class for storing information about a cell in the layout.
     */
    static class CellData
    {
        /** Stores the component that occupies this cell. */
        private final Object component;

        /** Stores the percent data object for this cell. */
        private final PercentData constraints;

        /**
         * Creates a new instance of <code>CellData</code> and initializes it.
         *
         * @param comp the component
         * @param constr the constraints
         */
        public CellData(Object comp, PercentData constr)
        {
            component = comp;
            constraints = constr;
        }

        /**
         * Returns the component stored in this cell.
         *
         * @return the component
         */
        public Object getComponent()
        {
            return component;
        }

        /**
         * Returns the constraints.
         *
         * @return the constraints
         */
        public PercentData getConstraints()
        {
            return constraints;
        }
    }
}
