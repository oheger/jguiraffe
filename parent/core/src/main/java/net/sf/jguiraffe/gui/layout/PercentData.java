/*
 * Copyright 2006-2021 The JGUIraffe Team.
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

import java.io.Serializable;

import org.apache.commons.lang.ObjectUtils;

/**
 * <p>
 * A constraints class used by {@link PercentLayout}.
 * </p>
 * <p>
 * This class is used to define data needed by the percent layout manager when
 * new components are added to the managed container. The main data stored in
 * instances of this class is information about where in the logic grid
 * maintained by the layout manager the new component should be placed, i.e. the
 * coordinates of the cell and the number of cells in X and Y direction that are
 * spanned.
 * </p>
 * <p>
 * It is also possible to associate a <code>PercentData</code> object with row
 * and column constraints. If these constraints are defined, they override the
 * constraints set for the occupied column and row. This makes it possible e.g.
 * to set a different alignment for a certain component, while the other
 * components in this row or column use the default alignment.
 * </p>
 * <p>
 * Another information stored in instances of this class is the so-called
 * <em>target cell</em>. This information is evaluated only if multiple columns
 * or rows are occupied. In this case the target column determines to which
 * column the size of the associated component should be assigned. This is
 * optional; if no target column is set, the associated component's width will
 * not be taken into account when the minimum layout width is calculated. The
 * same applies for the target row.
 * </p>
 * <p>
 * Instances of this class are immutable and thus thread-safe. They are not
 * created directly, but the nested {@code Builder} class is used for this
 * purpose. A typical sequence for creating {@code PercentData} objects could
 * look as follows:
 *
 * <pre>
 * PercentData.Builder b = new PercentData.Builder();
 * PercentData pd1 = b.xy(1, 2).create();
 * PercentData pd2 = b.xy(1, 3).spanX(2).withColumnConstraints(columnConstr).create();
 * </pre>
 *
 * </p>
 *
 * @see net.sf.jguiraffe.gui.layout.CellConstraints
 * @author Oliver Heger
 * @version $Id: PercentData.java 205 2012-01-29 18:29:57Z oheger $
 */
public final class PercentData implements Serializable
{
    /** Constant for an undefined cell position. */
    public static final int POS_UNDEF = -1;

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 20090730L;

    /** Constant for the initial buffer size. */
    private static final int BUF_SIZE = 64;

    /** Stores the column. */
    private final int column;

    /** Stores the row. */
    private final int row;

    /** Stores the number of occupied columns. */
    private final int spanX;

    /** Stores the number of occupied rows. */
    private final int spanY;

    /** Stores the target column. */
    private final int targetColumn;

    /** Stores the targetRow. */
    private final int targetRow;

    /** Stores a reference to a constraints object for the column. */
    private final CellConstraints columnConstraints;

    /** Stores a reference to a constraints object for the row. */
    private final CellConstraints rowConstraints;

    /**
     * Creates a new instance of {@code PercentData} and initializes all
     * properties. This constructor is called by the builder.
     *
     * @param x the column index
     * @param y the row index
     * @param w the span in X direction
     * @param h the span in Y direction
     * @param tx the target column
     * @param ty the target row
     * @param cccol the column constraints
     * @param ccrow the row constraints
     */
    private PercentData(int x, int y, int w, int h, int tx, int ty,
            CellConstraints cccol, CellConstraints ccrow)
    {
        column = x;
        row = y;
        spanX = w;
        spanY = h;
        targetColumn = tx;
        targetRow = ty;
        columnConstraints = cccol;
        rowConstraints = ccrow;
    }

    /**
     * Returns the number of the column, in which the corresponding component is
     * to be placed.
     *
     * @return the column number
     */
    public int getColumn()
    {
        return column;
    }

    /**
     * Returns the number of the row, in which the corresponding component it to
     * be placed.
     *
     * @return the row number
     */
    public int getRow()
    {
        return row;
    }

    /**
     * Returns the number of occupied columns.
     *
     * @return the number of occupied columns
     */
    public int getSpanX()
    {
        return spanX;
    }

    /**
     * Returns the number of occupied rows.
     *
     * @return the number of occupied rows
     */
    public int getSpanY()
    {
        return spanY;
    }

    /**
     * Returns the target column.
     *
     * @return the target column
     */
    public int getTargetColumn()
    {
        return targetColumn;
    }

    /**
     * Returns the target row.
     *
     * @return the target row
     */
    public int getTargetRow()
    {
        return targetRow;
    }

    /**
     * Returns the associated constraints object for the column.
     *
     * @return the column constraints object (may be <b>null </b>)
     */
    public CellConstraints getColumnConstraints()
    {
        return columnConstraints;
    }

    /**
     * Returns the associated constraints object for the row.
     *
     * @return the row constraints object (may be <b>null </b>)
     */
    public CellConstraints getRowConstraints()
    {
        return rowConstraints;
    }

    /**
     * A convenience method for determining the cell constraints object for the
     * specified orientation. This method is useful for some generic methods in
     * <code>PercentLayoutBase</code> that can operate either on columns or on
     * rows.
     *
     * @param vert the orientation flag (<b>true</b> for vertical, <b>false</b>
     *        for horizontal)
     * @return the corresponding constraints object
     */
    public CellConstraints getConstraints(boolean vert)
    {
        return vert ? getRowConstraints() : getColumnConstraints();
    }

    /**
     * Appends a string representation of this object to the specified string
     * buffer. This string contains the properties of this instance with their
     * values. No further information (e.g. the class name) is written.
     *
     * @param buf the target buffer (must not be <b>null</b>)
     * @throws IllegalArgumentException if the buffer is <b>null</b>
     */
    public void buildString(StringBuilder buf)
    {
        if (buf == null)
        {
            throw new IllegalArgumentException("Buffer must not be null!");
        }

        buf.append("COL = ").append(getColumn());
        buf.append(" ROW = ").append(getRow());
        buf.append(" SPANX = ").append(getSpanX());
        buf.append(" SPANY = ").append(getSpanY());
        if (getTargetColumn() > POS_UNDEF)
        {
            buf.append(" TARGETCOL = ").append(getTargetColumn());
        }
        if (getTargetRow() > POS_UNDEF)
        {
            buf.append(" TARGETROW = ").append(getTargetRow());
        }
        if (getColumnConstraints() != null)
        {
            buf.append(" COLCONSTR = ").append(
                    getColumnConstraints().toSpecificationString());
        }
        if (getRowConstraints() != null)
        {
            buf.append(" ROWCONSTR = ").append(
                    getRowConstraints().toSpecificationString());
        }
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation
     */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder(BUF_SIZE);
        buf.append(getClass().getName());
        buf.append(" [ ");
        buildString(buf);
        buf.append(" ]");
        return buf.toString();
    }

    /**
     * Compares this object with another one. Two instances of {@code
     * PercentData} are considered equal if all of their properties are equal.
     *
     * @param obj the object to compare to
     * @return a flag whether these objects are equal
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof PercentData))
        {
            return false;
        }

        PercentData c = (PercentData) obj;
        return getColumn() == c.getColumn()
                && getRow() == c.getRow()
                && getSpanX() == c.getSpanX()
                && getSpanY() == c.getSpanY()
                && getTargetColumn() == c.getTargetColumn()
                && getTargetRow() == c.getTargetRow()
                && ObjectUtils.equals(getColumnConstraints(), c
                        .getColumnConstraints())
                && ObjectUtils.equals(getRowConstraints(), c
                        .getRowConstraints());
    }

    /**
     * Returns a hash code for this object.
     *
     * @return a hash code
     */
    @Override
    public int hashCode()
    {
        final int factor = 31;
        final int seed = 17;

        int result = seed;
        result = factor * result + getColumn();
        result = factor * result + getRow();
        result = factor * result + getSpanX();
        result = factor * result + getSpanY();
        result = factor * result + getTargetColumn();
        result = factor * result + getTargetRow();
        if (getColumnConstraints() != null)
        {
            result = factor * result + getColumnConstraints().hashCode();
        }
        if (getRowConstraints() != null)
        {
            result = factor * result + getRowConstraints().hashCode();
        }

        return result;
    }

    /**
     * <p>
     * A <em>builder</em> implementation for creating instances of {@code
     * PercentData}. Using this class {@code PercentData} objects with all
     * properties supported can be created in a convenient way. Refer to the
     * class comment for {@link PercentData} for example use cases for this
     * class.
     * </p>
     * <p>
     * Implementation note: This class is not thread-safe.
     * </p>
     *
     * @author Oliver Heger
     * @version $Id: PercentData.java 205 2012-01-29 18:29:57Z oheger $
     */
    public static class Builder implements Serializable
    {
        /**
         * The serial version UID.
         */
        private static final long serialVersionUID = 20090730L;

        /** The x position. */
        private int col;

        /** The y position. */
        private int row;

        /** The x span. */
        private int spanX;

        /** The y span. */
        private int spanY;

        /** The target column. */
        private int targetCol;

        /** The target row. */
        private int targetRow;

        /** The column constraints. */
        private CellConstraints ccCol;

        /** The row constraints. */
        private CellConstraints ccRow;

        /**
         * Creates a new instance of {@code Builder}.
         */
        public Builder()
        {
            initDefaults();
        }

        /**
         * Initializes the column and the row index of the {@code PercentData}
         * instance to be created.
         *
         * @param x the column index (must be greater or equal 0)
         * @param y the row index (must be greater or equal 0)
         * @return a reference to this builder for method chaining
         * @throws IllegalArgumentException if a parameter is invalid
         */
        public Builder xy(int x, int y)
        {
            checkPos(x, "Column");
            checkPos(y, "Row");

            col = x;
            row = y;
            return this;
        }

        /**
         * Initializes the number of columns occupied by the component
         * associated with the {@code PercentData} instance to be created. This
         * value is used to populate the {@code spanX} property of the {@code
         * PercentData} object.
         *
         * @param w the number of occupied columns (must be greater 0)
         * @return a reference to this builder for method chaining
         * @throws IllegalArgumentException if the number of columns is less or
         *         equal 0
         */
        public Builder spanX(int w)
        {
            if (w < 1)
            {
                throw new IllegalArgumentException("X span must be greater 0!");
            }

            spanX = w;
            return this;
        }

        /**
         * Initializes the number of rows occupied by the component associated
         * with the {@code PercentData} instance to be created. This value is
         * used to populate the {@code spanY} property of the {@code
         * PercentData} object.
         *
         * @param h the number of occupied rows (must be greater 0)
         * @return a reference to this builder for method chaining
         * @throws IllegalArgumentException if the number of rows is less or
         *         equal 0
         */
        public Builder spanY(int h)
        {
            if (h < 1)
            {
                throw new IllegalArgumentException("Y span must be greater 0!");
            }

            spanY = h;
            return this;
        }

        /**
         * Initializes the number of columns and rows occupied by the component
         * association with the {@code PercentData} instance to be created. This
         * is convenience method that combines calls to {@link #spanX(int)} and
         * {@link #spanY(int)}.
         *
         * @param w the number of occupied columns (must be greater 0)
         * @param h the number of occupied rows (must be greater 0)
         * @return a reference to this builder for method chaining
         * @throws IllegalArgumentException if the number of columns or rows is
         *         less or equal 0
         */
        public Builder span(int w, int h)
        {
            spanX(w);
            spanY(h);
            return this;
        }

        /**
         * Initializes the {@code targetColumn} property of the {@code
         * PercentData} instance to be created. If a component spans multiple
         * columns, the target column specifies, to which column the size of the
         * component should be applied. If no target column is set (which is the
         * default), the width of the component is not taken into account when
         * determining the width of the layout's columns.
         *
         * @param tc the index of the target column (must be greater or equal 0)
         * @return a reference to this builder for method chaining
         * @throws IllegalArgumentException if the target column is less than 0
         */
        public Builder withTargetColumn(int tc)
        {
            checkPos(tc, "Target column");
            targetCol = tc;
            return this;
        }

        /**
         * Initializes the {@code targetRow} property of the {@code PercentData}
         * instance to be created. If a component spans multiple rows, the
         * target row specifies, to which row the size of the component should
         * be applied. If no target row is set (which is the default), the
         * height of the component is not taken into account when determining
         * the height of the layout's rows.
         *
         * @param tr the index of the target row (must be greater or equal 0)
         * @return a reference to this builder for method chaining
         * @throws IllegalArgumentException if the target row is less than 0
         */
        public Builder withTargetRow(int tr)
        {
            checkPos(tr, "Target row");
            targetRow = tr;
            return this;
        }

        /**
         * Sets a {@code CellConstraints} reference for the column for the
         * {@code PercentData} instance to be created. With this method the
         * {@code columnConstraints} property of the {@code PercentData} object
         * can be specified.
         *
         * @param cc the {@code CellConstraints} object for the column
         * @return a reference to this builder for method chaining
         */
        public Builder withColumnConstraints(CellConstraints cc)
        {
            ccCol = cc;
            return this;
        }

        /**
         * Sets a {@code CellConstraints} reference for the row for the {@code
         * PercentData} instance to be created. With this method the {@code
         * rowConstraints} property of the {@code PercentData} object can be
         * specified.
         *
         * @param cc the {@code CellConstraints} object for the row
         * @return a reference to this builder for method chaining
         */
        public Builder withRowConstraints(CellConstraints cc)
        {
            ccRow = cc;
            return this;
        }

        /**
         * Returns a {@code PercentData} object with the specified column and
         * row index. This is a convenience method for the frequent use case
         * that only the position of a component in the layout needs to be
         * specified. It has the same effect as calling {@link #xy(int, int)}
         * followed by {@link #create()}. Properties that have been set before
         * are not cleared.
         *
         * @param x the column index (must be greater or equal 0)
         * @param y the row index (must be greater or equal 0)
         * @return the {@code PercentData} object with the given coordinates
         * @throws IllegalArgumentException if a parameter is invalid
         */
        public PercentData pos(int x, int y)
        {
            xy(x, y);
            return create();
        }

        /**
         * Creates the {@code PercentData} instance whose properties were
         * specified by the preceding method calls. This method requires that
         * the position was set using the {@link #xy(int, int)} method. All
         * other properties are optional and are initialized with the following
         * default values:
         * <ul>
         * <li>The span is set to (1, 1), i.e. the component covers a single
         * column and row.</li>
         * <li>The target column and row are set to -1, which means that they
         * are undefined.</li>
         * <li>The column constraints and row constraints are set to
         * <b>null</b>.</li>
         * </ul>
         *
         * @return a reference to the new {@code PercentData} instance
         * @throws IllegalStateException if required parameters have not been
         *         set
         */
        public PercentData create()
        {
            if (col <= POS_UNDEF)
            {
                throw new IllegalStateException(
                        "A position must be set before calling create()!");
            }

            PercentData res = new PercentData(col, row, spanX, spanY,
                    targetCol, targetRow, ccCol, ccRow);
            reset();
            return res;
        }

        /**
         * Resets all properties of this builder to default values. This method
         * is automatically called by {@link #create()}, so that the definition
         * of a new instance can be started. It can be invoked manually to undo
         * the effects of methods called before.
         */
        public void reset()
        {
            initDefaults();
        }

        /**
         * Sets default values for the builder properties.
         */
        private void initDefaults()
        {
            col = POS_UNDEF;
            row = POS_UNDEF;
            targetCol = POS_UNDEF;
            targetRow = POS_UNDEF;
            spanX = 1;
            spanY = 1;
            ccCol = null;
            ccRow = null;
        }

        /**
         * Helper method for validating a position. This method checks whether
         * the given position is valid. If not, an exception is thrown.
         *
         * @param p the position to check
         * @param name the name of the position for generating the exception
         *        message
         */
        private static void checkPos(int p, String name)
        {
            if (p <= POS_UNDEF)
            {
                throw new IllegalArgumentException(name
                        + " must be greater or equal 0!");
            }
        }
    }
}
