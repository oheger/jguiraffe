/*
 * Copyright 2006-2013 The JGUIraffe Team.
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
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * <p>
 * A simple helper class to define columns and rows with identical size in
 * {@link PercentLayout}.
 * </p>
 * <p>
 * In GUI design it is often desirable that certain columns and rows have
 * exactly the same size, independent from the components they contain. An
 * example would be columns with labels that have a different width. With this
 * class it is possible to define the indices of columns and rows that should
 * have the same size.
 * </p>
 * <p>
 * This class defines some convenience constructors for dealing with a limited
 * number of cells. There is also a generic constructor which expects an array
 * with cell indices. The numeric values passed to the constructors are
 * interpreted as 0-based indices of columns and rows that have already been
 * defined in <code>PercentLayout</code>. It is also possible to set the indices
 * of the affected cells using a string format. Valid strings simply contain the
 * numeric indices separated by one or more of the following characters: &quot;
 * ,;/&quot;. Examples for valid strings would be:
 * </p>
 * <p>
 *
 * <pre>
 * &quot;1 3 4&quot;
 * &quot;1, 3,4&quot;
 * &quot;1;3/4&quot;  etc.
 * </pre>
 *
 * </p>
 * <p>
 * <code>CellGroup</code> objects are immutable. There are no setter methods for
 * manipulating instances once they have been created.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: CellGroup.java 205 2012-01-29 18:29:57Z oheger $
 */
public class CellGroup implements Serializable
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 20090730L;

    /** Constant for the supported separator characters. */
    private static final String SEPARATORS = " ,;/";

    /** Stores the indices of the affected cells. */
    private final int[] indices;

    /**
     * Creates a new instance of {@code CellGroup} and initializes it with the
     * given array with indices. This constructor also performs some checks on
     * the array passed in.
     *
     * @param idxarray the array with the indices (must not be <b>null</b>)
     * @throws IllegalArgumentException if the array contains invalid indices
     */
    private CellGroup(int[] idxarray)
    {
        if (idxarray.length <= 1)
        {
            throw new IllegalArgumentException(
                    "Group must contain at least 2 indices!");
        }

        indices = idxarray.clone();
        Arrays.sort(indices);
        if (indices[0] < 0)
        {
            throw new IllegalArgumentException("Invalid index: " + indices[0]);
        }
    }

    /**
     * Creates a new instance of <code>CellGroup</code>. The group contains the
     * two passed in cells.
     *
     * @param idx1 the index of the first cell
     * @param idx2 the index of the second cell
     */
    public CellGroup(int idx1, int idx2)
    {
        this(new int[] {
                idx1, idx2
        });
    }

    /**
     * Creates a new instance of <code>CellGroup</code>. The group contains the
     * three passed in cells.
     *
     * @param idx1 the index of the first cell
     * @param idx2 the index of the second cell
     * @param idx3 the index of the third cell
     */
    public CellGroup(int idx1, int idx2, int idx3)
    {
        this(new int[] {
                idx1, idx2, idx3
        });
    }

    /**
     * Creates a new instance of <code>CellGroup</code>. The group contains the
     * four passed in cells.
     *
     * @param idx1 the index of the first cell
     * @param idx2 the index of the second cell
     * @param idx3 the index of the third cell
     * @param idx4 the index of the fourth cell
     */
    public CellGroup(int idx1, int idx2, int idx3, int idx4)
    {
        this(new int[] {
                idx1, idx2, idx3, idx4
        });
    }

    /**
     * Creates a new instance of <code>CellGroup</code> and initializes it with
     * the indices of the affected cells.
     *
     * @param idx an array with the cell indices
     * @return the newly created {@code CellGroup} object
     */
    public static CellGroup fromArray(int... idx)
    {
        if (idx == null)
        {
            throw new IllegalArgumentException("Index array must not be null!");
        }

        return new CellGroup(idx);
    }

    /**
     * Creates a new instance of <code>CellGroup</code> and initializes it from
     * the passed in string.
     *
     * @param s a string defining the cell indices
     * @return the newly created {@code CellGroup} object
     */
    public static CellGroup fromString(String s)
    {
        return new CellGroup(parse(s));
    }

    /**
     * Returns the number of elements contained in this group.
     *
     * @return the number of elements
     */
    public int groupSize()
    {
        return indices.length;
    }

    /**
     * Applies this group object to the given cell sizes. This method checks the
     * sizes of all cells that belong to this group and sets them to the
     * maximum.
     *
     * @param sizes an array with the cell sizes (must not be <b>null</b>)
     * @throws IllegalArgumentException if the array with sizes is <b>null</b>
     * @throws ArrayIndexOutOfBoundsException if indices in this group are too
     *         big
     */
    public void apply(int[] sizes)
    {
        if (sizes == null)
        {
            throw new IllegalArgumentException("Sizes array must not be null!");
        }

        // Calculate maximum size
        int max = 0;
        for (int i = 0; i < indices.length; i++)
        {
            if (sizes[indices[i]] > max)
            {
                max = sizes[indices[i]];
            }
        }

        // Apply this maximum size
        for (int i = 0; i < indices.length; i++)
        {
            sizes[indices[i]] = max;
        }
    }

    /**
     * Appends a string representation of this object to the given string
     * buffer. This string contains only the indices of this group separated by
     * ",".
     *
     * @param buf the target buffer (must not be <b>null</b>)
     * @throws IllegalArgumentException if the target buffer is <b>null</b>
     */
    public void buildString(StringBuilder buf)
    {
        if (buf == null)
        {
            throw new IllegalArgumentException("Buffer must not be null!");
        }

        buf.append(Arrays.toString(indices));
    }

    /**
     * Creates a string representation of this object.
     *
     * @return a string representation of this object
     */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append("CellGroup [ indices = ");
        buildString(buf);
        buf.append(" ]");
        return buf.toString();
    }

    /**
     * Compares this object with another one. Two instances of {@code CellGroup}
     * are equal if they contain the same indices (the order does not matter).
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
        if (!(obj instanceof CellGroup))
        {
            return false;
        }

        CellGroup c = (CellGroup) obj;
        return Arrays.equals(indices, c.indices);
    }

    /**
     * Returns a hash code for this object.
     *
     * @return a hash code
     */
    @Override
    public int hashCode()
    {
        return Arrays.hashCode(indices);
    }

    /**
     * Parses the specified string, which must contain cell indices. Refer to
     * the class comment for a description of the expected format. If the string
     * is invalid, an <code>IllegalArgumentException</code> exception is thrown.
     *
     * @param s the string
     * @return an array with the indices extracted from the string
     * @throws IllegalArgumentException if the string is invalid
     */
    private static int[] parse(String s)
    {
        if (s == null)
        {
            throw new IllegalArgumentException("String must not be null!");
        }

        StringTokenizer tok = new StringTokenizer(s, SEPARATORS);
        int[] indices = new int[tok.countTokens()];

        try
        {
            for (int i = 0; i < indices.length; i++)
            {
                indices[i] = Integer.parseInt(tok.nextToken());
            }
        }
        catch (NumberFormatException nex)
        {
            throw new IllegalArgumentException(
                    "Invalid specification string for CellGroup: " + s);
        }

        return indices;
    }
}
