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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableObject;

/**
 * <p>
 * A class for describing column and row constraints for the
 * {@link PercentLayout} layout manager.
 * </p>
 * <p>
 * Objects of this class define properties for the single columns and rows that
 * comprise the layout maintained by {@link PercentLayout}. Supported properties
 * are:
 * </p>
 * <p>
 * <ul>
 * <li>The alignment of the represented cell. This can be one of the constants
 * defined by the {@link CellAlignment} enumeration class.</li>
 * <li>An initial size. This property defines how the column's or row's size
 * depends on the sizes of the contained components. The possible values are
 * defined by the enumeration class {@link CellSize}.</li>
 * <li>A minimum size for this cell. This is a numeric value specifying the
 * minimum width for columns and minimum height for rows. To determine the size
 * of a column or row the layout first calculates the initial size. If this is
 * less than the minimum size, the minimum size is used. It is possible to use
 * different units for specifying this property.</li>
 * <li>A weight factor. This factor determines how this cell should behave if
 * additional space becomes available when the container's size changes (e.g. if
 * the user resizes the window). If this factor is 0, the cell won't change its
 * size; it will always keep its initial size. For other values than 0 the
 * remaining space is given to the affected cells with regard to their factors.
 * It is recommended to use percentage values for these factors, which sum up to
 * 100 percent (hence the name of the {@code PercentLayout} layout manager).
 * Then it is easy to understand that each cell with a weight factor greater
 * than 0 is granted as much percent of the remaining space.</li>
 * </ul>
 * </p>
 * <p>
 * Instances of this class are not created directly, but the inner {@code
 * Builder} class is used for this purpose (an application of the
 * <em>builder</em> pattern). A typical invocation sequence could look as
 * follows:
 *
 * <pre>
 * CellConstraints.Builder ccb = new CellConstraints.Builder();
 * CellConstraints cc = ccb.withCellSize(CellSize.Preferred).withMinimumSize(
 *         new NumberWithUnit(20, Unit.PIXEL)).withWeight(25).withCellAlignment(
 *         CellAlignment.FULL).create();
 * </pre>
 *
 * </p>
 * <p>
 * For instances of this class a string representation is defined. Strings
 * conforming to the format explained below can also be passed to the builder
 * for creating instances:
 * </p>
 * <p>
 *
 * <pre>
 * CONSTRAINT   ::= [ALIGN&quot;/&quot;]SIZE[&quot;/&quot;WEIGHT]
 * SIZE         ::= INITSIZE | MINSIZE | BOTHSIZES
 * BOTHSIZES    ::= INITSIZE &quot;(&quot; MINSIZE &quot;)&quot;
 * INITSIZE     ::= &quot;PREFERRED&quot; | &quot;MINIMUM&quot; | &quot;NONE&quot;
 * MINSIZE      ::= &lt;Positive number&gt; [UNIT]
 * UNIT         ::= &quot;px&quot; | &quot;cm&quot; | &quot;in&quot; | &quot;dlu&quot;
 * ALIGN        ::= &quot;START&quot; | &quot;CENTER&quot; | &quot;END&quot; | &quot;FULL&quot;
 * WEIGHT       ::= &lt;Positive number&gt;
 * </pre>
 *
 * Here are some examples:
 * <ul>
 * <li>{@code PREFERRED}: only the cell size is set, the other properties are
 * set to default values.</li>
 * <li>{@code CENTER/MINIMUM(20px)/33}: A {@code CellConstraints} object is
 * created with the {@link CellSize} {@code MINIMUM} and the alignment {@code
 * CENTER}. The minimum size is set to 20 pixels, and the weight factor is set
 * to 33.</li>
 * <li>{@code (20)}: Here only the minimum size of the cell is set. The
 * {@link CellSize} is automatically set to {@code NONE}.</li>
 * </ul>
 * </p>
 * <p>
 * Instances of this class are immutable and thus can be shared between multiple
 * threads. The {@code Builder} class makes use of this feature and caches the
 * instances that have been created. So if another constraint with already used
 * properties is requested, a shared instance can be returned.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: CellConstraints.java 205 2012-01-29 18:29:57Z oheger $
 */
public final class CellConstraints implements Serializable
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 20090730L;

    /** Constant for the minimum size start delimiter. */
    private static final char MINSIZE_START = '(';

    /** Constant for the minimum size end delimiter. */
    private static final char MINSIZE_END = ')';

    /** Constant for the separator used for cell constraints in string form. */
    private static final String DELIMITER = "/";

    /** Constant for the default string buffer size. */
    private static final int BUF_SIZE = 32;

    /** Constant for the maximum length of a parsed constraints specification. */
    private static final int LEN_MAX = 3;

    /** Constant for the middle length of a parsed constraints specification. */
    private static final int LEN_MEDIUM = 2;

    /** Constant for the minimum length of a parsed constraints specification. */
    private static final int LEN_MIN = 1;

    /** Stores the alignment. */
    private final CellAlignment alignment;

    /** Stores the cell size. */
    private final CellSize cellSize;

    /** Stores the cell's minimum size. */
    private final NumberWithUnit minSize;

    /** Stores the weight factor. */
    private final int weight;

    /**
     * Creates a new instance of {@code CellConstraints} and initializes it.
     * Client code uses the inner {@code Builder} class for creating instances.
     *
     * @param al the alignment
     * @param sz the size
     * @param minsz the minimum size
     * @param w the weight factor
     */
    private CellConstraints(CellAlignment al, CellSize sz,
            NumberWithUnit minsz, int w)
    {
        alignment = al;
        minSize = NumberWithUnit.nonNull(minsz);
        weight = w;
        cellSize = sz;
    }

    /**
     * Returns the alignment string.
     *
     * @return the alignment string
     */
    public CellAlignment getAlignment()
    {
        return alignment;
    }

    /**
     * Returns the size of the cell.
     *
     * @return the cell size
     */
    public CellSize getCellSize()
    {
        return cellSize;
    }

    /**
     * Returns the minimum size.
     *
     * @return the minimum size
     */
    public NumberWithUnit getMinSize()
    {
        return minSize;
    }

    /**
     * Returns the weight factor.
     *
     * @return the weight factor
     */
    public int getWeight()
    {
        return weight;
    }

    /**
     * Helper method for creating a specification string for the specified
     * constraint data.
     *
     * @param align the alignment string
     * @param size the cell size
     * @param minsz the minimum size
     * @param w the weight factor
     * @return a string specification for the passed in data
     */
    private static String toString(CellAlignment align, CellSize size,
            NumberWithUnit minsz, int w)
    {
        StringBuilder buf = new StringBuilder(BUF_SIZE);
        buf.append(align.name()).append(DELIMITER);
        buf.append(size.name());
        buf.append(MINSIZE_START);
        minsz.buildUnitString(buf);
        buf.append(MINSIZE_END);
        buf.append(DELIMITER).append(w);
        return buf.toString();
    }

    /**
     * Returns a string specification for this {@code CellConstraints} object.
     * This string conforms to the definition given in the class comment. It can
     * be passed to a {@code Builder} instance to obtain a corresponding {@code
     * CellConstraints} instance. The string returned by this method contains
     * the values of all properties of this object, even if some have been set
     * to default values.
     *
     * @return a specification string for this instance
     */
    public String toSpecificationString()
    {
        return toString(getAlignment(), getCellSize(), getMinSize(),
                getWeight());
    }

    /**
     * Returns a string representation of this constraints object. This string
     * contains the specification string produced by
     * {@link #toSpecificationString()}.
     *
     * @return a string representation for this object
     */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append("CellConstraints [ ");
        buf.append(toSpecificationString());
        buf.append(" ]");
        return buf.toString();
    }

    /**
     * Compares this object with another one. Two instances of {@code
     * CellConstraints} are equal if all of their properties are equal.
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
        if (!(obj instanceof CellConstraints))
        {
            return false;
        }

        CellConstraints c = (CellConstraints) obj;
        return getAlignment() == c.getAlignment()
                && getCellSize() == c.getCellSize()
                && getMinSize().equals(c.getMinSize())
                && getWeight() == c.getWeight();
    }

    /**
     * Returns a hash code for this object.
     *
     * @return a hash code for this object
     */
    @Override
    public int hashCode()
    {
        final int factor = 31;
        final int seed = 17;

        int result = seed;
        result = factor * result + getAlignment().hashCode();
        result = factor * result + getCellSize().hashCode();
        result = factor * result + getMinSize().hashCode();
        result = factor * result + getWeight();

        return result;
    }

    /**
     * Helper method for parsing the minimum size. This method tests whether the
     * passed in string contains a valid size definition. If this is the case,
     * the corresponding values are returned in the values of the passed in
     * mutable objects.
     *
     * @param sizeDef the string to be parsed
     * @param size takes the value of the size
     * @param minSize takes the value of the minimum size
     * @return a flag if the size could be successfully parsed
     */
    private static boolean parseSize(String sizeDef, MutableObject size,
            MutableObject minSize)
    {
        String s = sizeDef.trim();
        int pos = s.indexOf(MINSIZE_START);
        if (pos > 0)
        {
            // both initial size and minimum size
            if (!s.endsWith(String.valueOf(MINSIZE_END)))
            {
                return false;
            }

            try
            {
                size.setValue(valueOf(CellSize.class, s.substring(0, pos)));
                minSize.setValue(new NumberWithUnit(s.substring(pos + 1, s
                        .length() - 1)));
            }
            catch (IllegalArgumentException iex)
            {
                // not a valid literal or number with unit
                return false;
            }
        }

        else
        {
            try
            {
                // initial size defined?
                size.setValue(valueOf(CellSize.class, s));
                minSize.setValue(NumberWithUnit.ZERO);
            }
            catch (IllegalArgumentException iex)
            {
                // no, a minimum size?
                try
                {
                    minSize.setValue(new NumberWithUnit(s));
                    size.setValue(CellSize.NONE);
                }
                catch (IllegalArgumentException iex2)
                {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Helper method for parsing and validating a size definition. This method
     * works like {@link #parseSize(String, MutableObject, MutableObject)}, but
     * throws an exception if parsing fails.
     *
     * @param s the string to be parsed
     * @param size takes the value of the size
     * @param minSize takes the value of the minimum size
     */
    private static void parseSizeEx(String s, MutableObject size,
            MutableObject minSize)
    {
        if (!parseSize(s, size, minSize))
        {
            throw new IllegalArgumentException("Invalid size declaration: " + s);
        }
    }

    /**
     * Parses the given alignment specification.
     *
     * @param comps the components of the complete specification
     * @param index the index of the component to be parsed
     * @param defAlign the default alignment
     * @return the alignment
     * @throws IllegalArgumentException if the specification is invalid
     */
    private static CellAlignment parseAlignment(String[] comps, int index,
            CellAlignment defAlign)
    {
        if (index < 0)
        {
            return defAlign;
        }
        else
        {
            return valueOf(CellAlignment.class, comps[index]);
        }
    }

    /**
     * Parses the given specification of a weight factor.
     *
     * @param comps the components of the complete specification
     * @param index the index of the component to be parsed
     * @return the weight factor
     * @throws IllegalArgumentException if the specification is invalid
     */
    private static int parseWeight(String[] comps, int index)
    {
        if (index < 0)
        {
            return 0;
        }

        try
        {
            int weight = Integer.parseInt(comps[index].trim());
            if (weight < 0)
            {
                throw new IllegalArgumentException(
                        "Weight factor must be positive: " + comps[index]);
            }
            return weight;
        }
        catch (NumberFormatException nfex)
        {
            throw new IllegalArgumentException(
                    "Invalid weight factor specification: " + comps[index]);
        }
    }

    /**
     * Helper method for obtaining an enumeration literal for a given case. The
     * string is transformed to upper case before the literal is checked, so
     * this method is case insensitive.
     *
     * @param <T> the type of the enum class
     * @param enumType the type of the enumeration
     * @param s the string
     * @return the corresponding enum value
     * @throws IllegalArgumentException if the string is not a valid enumeration
     *         literal
     */
    private static <T extends Enum<T>> T valueOf(Class<T> enumType, String s)
    {
        return Enum.valueOf(enumType, s.trim().toUpperCase());
    }

    /**
     * Parses the given specification string for a {@code CellConstraints}
     * object and creates a corresponding instance.
     *
     * @param spec the string to be parsed
     * @param defAlign the default alignment
     * @return the corresponding instance
     */
    private static CellConstraints parse(String spec, CellAlignment defAlign)
    {
        if (StringUtils.isEmpty(spec))
        {
            throw new IllegalArgumentException(
                    "Undefined specification string!");
        }

        MutableObject size = new MutableObject();
        MutableObject minSize = new MutableObject();
        int indexAlign = -1;
        int indexWeight = -1;
        String[] comps = spec.split(DELIMITER);

        switch (comps.length)
        {
        case LEN_MAX:
            indexAlign = 0;
            indexWeight = 2;
            parseSizeEx(comps[1], size, minSize);
            break;

        case LEN_MEDIUM:
            if (parseSize(comps[0], size, minSize))
            {
                indexWeight = 1;
            }
            else
            {
                parseSizeEx(comps[1], size, minSize);
                indexAlign = 0;
            }
            break;

        case LEN_MIN:
            parseSizeEx(comps[0], size, minSize);
            break;

        default:
            throw new IllegalArgumentException(
                    "Invalid number of components in specification string: "
                            + spec);
        }

        NumberWithUnit nMinSize = (NumberWithUnit) minSize.getValue();
        if (nMinSize.getValue() < 0)
        {
            throw new IllegalArgumentException(
                    "Minimum size must be non-negative: " + nMinSize);
        }

        return new CellConstraints(parseAlignment(comps, indexAlign, defAlign),
                (CellSize) size.getValue(), nMinSize, parseWeight(comps,
                        indexWeight));
    }

    /**
     * <p>
     * A <em>builder</em> class for creating instances of {@code
     * CellConstraints}.
     * </p>
     * <p>
     * With the different {@code withXXXX()} methods the properties of the new
     * {@code CellConstraints} object are defined. Then, with the {@code
     * create()} method, the instance is actually created. Alternatively, with
     * the {@code parse()} method a {@code CellConstraints} instance for a
     * string representation can be requested.
     * </p>
     * <p>
     * This class is not thread-safe. It maintains an internal (unsynchronized)
     * cache of the {@code CellConstraints} instances that have already been
     * created. If instances with equal properties are requested, the same
     * instance is returned.
     * </p>
     * <p>
     * More information including a usage example of this class can be found in
     * the documentation for {@link CellConstraints}.
     * </p>
     *
     * @author Oliver Heger
     * @version $Id: CellConstraints.java 205 2012-01-29 18:29:57Z oheger $
     */
    public static final class Builder
    {
        /** The cache of instances created so far. */
        private final Map<String, CellConstraints> cache;

        /** The current cell size. */
        private CellSize cellSize;

        /** The current alignment. */
        private CellAlignment alignment;

        /** The default alignment. */
        private CellAlignment defaultAlignment;

        /** The current minimum size. */
        private NumberWithUnit minSize;

        /** The current weight factor. */
        private int weight;

        /**
         * Creates a new instance of {@code Builder}.
         */
        public Builder()
        {
            cache = new HashMap<String, CellConstraints>();
            defaultAlignment = CellAlignment.FULL;
            reset();
        }

        /**
         * Returns the default {@code CellAlignment} used by this builder.
         *
         * @return the default {@code CellAlignment}
         */
        public CellAlignment getDefaultAlignment()
        {
            return defaultAlignment;
        }

        /**
         * Sets the default {@code CellAlignment} used by this builder. This
         * alignment is set by the {@code reset()} method. Note: after calling
         * this method {@code reset()} must be called to apply the new default
         * alignment.
         *
         * @param defaultAlignment the new default alignment (must not be
         *        <b>null</b>)
         * @throws IllegalArgumentException if the parameter is <b>null</b>
         */
        public void setDefaultAlignment(CellAlignment defaultAlignment)
        {
            if (defaultAlignment == null)
            {
                throw new IllegalArgumentException(
                        "Default alignment must not be null!");
            }

            this.defaultAlignment = defaultAlignment;
        }

        /**
         * Sets the {@code size} property of the {@link CellConstraints}
         * instance to be created.
         *
         * @param sz the size of the cell (must not be <b>null</b>)
         * @return a reference to this builder for method chaining
         * @throws IllegalArgumentException if the {@code CellSize} object is
         *         <b>null</b>
         */
        public Builder withCellSize(CellSize sz)
        {
            if (sz == null)
            {
                throw new IllegalArgumentException(
                        "Cell size must not be null!");
            }
            cellSize = sz;
            return this;
        }

        /**
         * Sets the {@code minimumSize} property of the {@link CellConstraints}
         * instance to be created.
         *
         * @param minSize the minimum size of the cell (can be <b>null</b>, then
         *        a minimum size of 0 pixels is assumed)
         * @return a reference to this builder for method chaining
         * @throws IllegalArgumentException if the value of the minimum size is
         *         negative
         */
        public Builder withMinimumSize(NumberWithUnit minSize)
        {
            this.minSize = NumberWithUnit.nonNull(minSize);
            if (this.minSize.getValue() < 0)
            {
                throw new IllegalArgumentException(
                        "Minimum size must not be negative: " + this.minSize);
            }
            return this;
        }

        /**
         * Sets the {@code alignment} property of the {@link CellConstraints}
         * instance to be created.
         *
         * @param align the alignment of the cell (must not be <b>null</b>)
         * @return a reference to this builder for method chaining
         * @throws IllegalArgumentException if the alignment is <b>null</b>
         */
        public Builder withCellAlignment(CellAlignment align)
        {
            if (align == null)
            {
                throw new IllegalArgumentException(
                        "Cell alignment must not be null!");
            }
            alignment = align;
            return this;
        }

        /**
         * Sets the {@code weight} property of the {@link CellConstraints}
         * instance to be created. This must be a positive number.
         *
         * @param factor the weight factor
         * @return a reference to this builder for method chaining
         * @throws IllegalArgumentException if the number is less than 0
         */
        public Builder withWeight(int factor)
        {
            if (factor < 0)
            {
                throw new IllegalArgumentException(
                        "Weight factor must not be negative: " + factor);
            }
            weight = factor;
            return this;
        }

        /**
         * Initializes the properties of the {@link CellConstraints} instance to
         * be created with default values for a column. This means:
         * <ul>
         * <li>the alignment is set to {@code FULL}</li>
         * <li>the size is set to {@code PREFERRED}</li>
         * <li>no minimum size is set</li>
         * <li>the weight factor is set to 0</li>
         * </ul>
         * This method can be called first for initializing default values.
         * Then, with other {@code withXXXX()} methods specific values can be
         * set.
         *
         * @return a reference to this builder for method chaining
         */
        public Builder defaultColumn()
        {
            alignment = CellAlignment.FULL;
            cellSize = CellSize.PREFERRED;
            minSize = NumberWithUnit.ZERO;
            weight = 0;
            return this;
        }

        /**
         * Initializes the properties of the {@link CellConstraints} instance to
         * be created with default values for a row. This means:
         * <ul>
         * <li>the alignment is set to {@code CENTER}</li>
         * <li>the size is set to {@code PREFERRED}</li>
         * <li>no minimum size is set</li>
         * <li>the weight factor is set to 0</li>
         * </ul>
         * This method can be called first for initializing default values.
         * Then, with other {@code withXXXX()} methods specific values can be
         * set.
         *
         * @return a reference to this builder for method chaining
         */
        public Builder defaultRow()
        {
            alignment = CellAlignment.CENTER;
            cellSize = CellSize.PREFERRED;
            minSize = NumberWithUnit.ZERO;
            weight = 0;
            return this;
        }

        /**
         * Returns a {@link CellConstraints} instance for the properties defined
         * so far. After that all properties are reset, so that properties for a
         * new instance can be specified.
         *
         * @return a {@code CellConstraints} instance corresponding to the
         *         properties set so far
         * @throws IllegalStateException if required properties are missing
         */
        public CellConstraints create()
        {
            if (cellSize == null && minSize == null)
            {
                throw new IllegalStateException("Size of cell is unspecified!"
                        + " Set a cell size or a minimum size.");
            }
            if (cellSize == null)
            {
                cellSize = CellSize.NONE;
            }
            if (minSize == null)
            {
                minSize = NumberWithUnit.ZERO;
            }

            String spec = CellConstraints.toString(alignment, cellSize,
                    minSize, weight);
            CellConstraints cc = cache.get(spec);
            if (cc == null)
            {
                cc = new CellConstraints(alignment, cellSize, NumberWithUnit
                        .nonNull(minSize), weight);
                cache.put(spec, cc);
            }

            reset();
            return cc;
        }

        /**
         * Resets all properties set so far. This reverts all invocations of
         * {@code withXXXX()} methods since the last instance was created.
         */
        public void reset()
        {
            alignment = getDefaultAlignment();
            cellSize = null;
            minSize = null;
            weight = 0;
        }

        /**
         * Parses a string with the specification of a {@code CellConstraints}
         * object and returns a corresponding instance.
         *
         * @param spec the specification of the {@code CellConstraints} instance
         * @return the corresponding {@code CellConstraints} instance
         * @throws IllegalArgumentException if the string cannot be parsed
         */
        public CellConstraints fromString(String spec)
        {
            CellConstraints cc = cache.get(spec);
            if (cc != null)
            {
                return cc;
            }

            cc = CellConstraints.parse(spec, getDefaultAlignment());
            cache.put(spec, cc);
            String canonicalSpec = cc.toSpecificationString();
            if (!canonicalSpec.equals(spec))
            {
                cache.put(canonicalSpec, cc);
            }

            return cc;
        }
    }
}
