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
package net.sf.jguiraffe.transform;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.MapConfiguration;

/**
 * <p>
 * A generic <code>Transformer</code> implementation that transforms arbitrary
 * objects into formatted strings.
 * </p>
 * <p>
 * This implementation can be used for transforming data of the most common data
 * types to be presented in form input fields (especially text fields). Based on
 * the type of the passed in object a specific transforming method will be
 * invoked. The following data types are supported:
 * <ul>
 * <li><code>java.util.Date</code> objects will be formatted using a
 * <code>DateFormat</code> object for the current locale. With the
 * <code>setDateFormatStyle()</code> the specific formatting style can be
 * defined (one of the constants defined by the <code>DateFormat</code> class
 * like <code>SHORT</code>, <code>MEDIUM</code>, or <code>FULL</code>.
 * The class will always use a date instance for doing the formatting. So if
 * other formats are needed (e.g. only the time portion or both date and time),
 * a different <code>Transformer</code> implementation must be used.</li>
 * <li>Decimal numbers will be formatted using a <code>NumberFormat</code>
 * object initialized for the current locale. For configuring the format a set
 * of properties is available matching the options of <code>NumberFormat</code>
 * (e.g. the grouping flag or the maximum number of fraction digits).</li>
 * <li>Integer numbers are treated in a similar way as decimal numbers. They
 * are also transformed using a <code>NumberFormat</code> object, but no
 * fraction digits will be output.</li>
 * <li>For all other objects their <code>toString()</code> method will be
 * invoked.</li>
 * <li>A <b>null</b> input results in an empty string.</li>
 * </ul>
 * </p>
 * <p>
 * The properties that configure the formatting can be set through the
 * bean-style set methods. When declaring an input element in a builder script
 * and assigning a <code>Transformer</code> to it, it is also possible to
 * specify properties, which will then override the settings stored in the
 * object's properties. These properties have the same names as the bean
 * properties defined by this class, e.g. the <code>groupingUsed</code>
 * property corresponds to the <code>setGroupingUsed()</code> method.
 * </p>
 * <p>
 * An instance of this <code>Transformer</code> implementation can be shared
 * between multiple input components. It is possible to create a single instance
 * (e.g. using the dependency injection framework) and initialize it with
 * default settings. If single input components require different settings,
 * specific properties can be set for them overriding the defaults.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ToStringTransformer.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ToStringTransformer implements Transformer
{
    /** Constant for the date format style property. */
    public static final String PROP_DATE_FORMAT_STYLE = "dateFormatStyle";

    /** Constant for the maximum fraction digits property. */
    public static final String PROP_MAXIMUM_FRACTION_DIGITS = "maximumFractionDigits";

    /** Constant for the minimum fraction digits property. */
    public static final String PROP_MINIMUM_FRACTION_DIGITS = "minimumFractionDigits";

    /** Constant for the grouping used property. */
    public static final String PROP_GROUPING_USED = "groupingUsed";

    /** Constant for the result of a <b>null</b> transformation. */
    private static final Object NULL_RESULT = "";

    /** Constant for the default maximum fraction digits. */
    private static final int DEF_MAX_FRACTION_DIGITS = 2;

    /** The style to be used for the date format. */
    private int dateFormatStyle;

    /** The minimum number of fraction digits. */
    private int minimumFractionDigits;

    /** The maximum number of fraction digits. */
    private int maximumFractionDigits;

    /** The grouping used flag. */
    private boolean groupingUsed;

    /**
     * Creates a new instance of <code>ToStringTransformer</code>.
     */
    public ToStringTransformer()
    {
        setDateFormatStyle(DateFormat.SHORT);
        setMaximumFractionDigits(DEF_MAX_FRACTION_DIGITS);
    }

    /**
     * Returns the style to be used for formatting dates.
     *
     * @return the style for the formatting of dates
     */
    public int getDateFormatStyle()
    {
        return dateFormatStyle;
    }

    /**
     * Sets the style to be used for formatting dates. This is one of the style
     * constants defined by the <code>java.text.DateFormat</code> class.
     *
     * @param dateFormatStyle the style to be used
     */
    public void setDateFormatStyle(int dateFormatStyle)
    {
        this.dateFormatStyle = dateFormatStyle;
    }

    /**
     * Returns the minimum number of fraction digits.
     *
     * @return the minimum number of fraction digits
     */
    public int getMinimumFractionDigits()
    {
        return minimumFractionDigits;
    }

    /**
     * Sets the minimum number of fraction digits. This value is taken into
     * account when formatting decimal numbers.
     *
     * @param minimumFractionDigits the new minimum number of fraction digits
     */
    public void setMinimumFractionDigits(int minimumFractionDigits)
    {
        this.minimumFractionDigits = minimumFractionDigits;
    }

    /**
     * Returns the maximum number of fraction digits.
     *
     * @return the maximum number of fraction digits
     */
    public int getMaximumFractionDigits()
    {
        return maximumFractionDigits;
    }

    /**
     * Sets the maximum number of fraction digits. This value is taken into
     * account when formatting decimal numbers.
     *
     * @param maximumFractionDigits the new maximum number of fraction digits
     */
    public void setMaximumFractionDigits(int maximumFractionDigits)
    {
        this.maximumFractionDigits = maximumFractionDigits;
    }

    /**
     * Returns a flag whether a grouping character is to be used when formatting
     * numbers.
     *
     * @return the grouping flag
     */
    public boolean isGroupingUsed()
    {
        return groupingUsed;
    }

    /**
     * Sets the grouping used flag. When formatting numbers this flag controls
     * whether a grouping character is used for big numbers.
     *
     * @param groupingUsed the grouping flag
     */
    public void setGroupingUsed(boolean groupingUsed)
    {
        this.groupingUsed = groupingUsed;
    }

    /**
     * Transforms the specified object. Depending on the type of the object one
     * of the specialized transform methods will be called.
     *
     * @param o the object to be transformed
     * @param ctx the transformer context
     * @return the transformed object
     * @throws Exception if an error occurs
     */
    public Object transform(Object o, TransformerContext ctx) throws Exception
    {
        if (o == null)
        {
            return transformNull(ctx);
        }
        else if (o instanceof Date)
        {
            return transformDate((Date) o, ctx);
        }
        else if (isDecimalNumber(o))
        {
            return transformDecimalNumber(o, ctx);
        }
        else if (isIntegerNumber(o))
        {
            return transformIntegerNumber(o, ctx);
        }
        else
        {
            return transformObject(o, ctx);
        }
    }

    /**
     * Transforms a <b>null</b> object. This implementation returns an empty
     * string.
     *
     * @param ctx the transformer context
     * @return the transformed object
     * @throws Exception if an error occurs
     */
    protected Object transformNull(TransformerContext ctx) throws Exception
    {
        return NULL_RESULT;
    }

    /**
     * Transforms a date object. This implementation uses a date format object.
     *
     * @param dt the date to be formatted
     * @param ctx the transformer context
     * @return the transformed object
     * @throws Exception if an error occurs
     */
    protected Object transformDate(Date dt, TransformerContext ctx)
            throws Exception
    {
        Configuration config = new MapConfiguration(ctx.properties());
        int style = config.getInt(PROP_DATE_FORMAT_STYLE, getDateFormatStyle());
        DateFormat fmt = DateFormat.getDateInstance(style, ctx.getLocale());
        return fmt.format(dt);
    }

    /**
     * Transforms a decimal number object. This implementation uses a number
     * format object.
     *
     * @param number the number object to be formatted
     * @param ctx the transformer context
     * @return the transformed object
     * @throws Exception if an error occurs
     */
    protected Object transformDecimalNumber(Object number,
            TransformerContext ctx) throws Exception
    {
        return createNumberFormat(ctx, true).format(number);
    }

    /**
     * Transforms an integer number object. For this purpose a number format
     * object is used.
     *
     * @param number the number to be transformed
     * @param ctx the transformer context
     * @return the transformed object
     * @throws Exception if an error occurs
     */
    protected Object transformIntegerNumber(Object number,
            TransformerContext ctx) throws Exception
    {
        return createNumberFormat(ctx, false).format(number);
    }

    /**
     * Transforms an arbitrary object. This method is invoked if no other, more
     * specific transformation method can be found. It transforms the object
     * into a string by invoking its <code>toString()</code> method.
     *
     * @param o the object to be transformed
     * @param ctx the transformer context
     * @return the transformed object
     * @throws Exception if an error occurs
     */
    protected Object transformObject(Object o, TransformerContext ctx)
            throws Exception
    {
        return o.toString();
    }

    /**
     * Checks whether the passed in object is a decimal number. This
     * implementation checks for the typical Java types representing decimal
     * numbers.
     *
     * @param o the object to check
     * @return a flag whether the passed in object is a decimal number
     */
    protected boolean isDecimalNumber(Object o)
    {
        return o instanceof Float || o instanceof Double
                || o instanceof BigDecimal;
    }

    /**
     * Checks whether the passed in object is an integer number. This
     * implementation simply checks whether the object is an instance of
     * <code>java.lang.Number</code>. Because decimal numbers are checked
     * first, the remaining number objects are integers.
     *
     * @param o the object to check
     * @return a flag whether this object is an integer number
     */
    protected boolean isIntegerNumber(Object o)
    {
        return o instanceof Number;
    }

    /**
     * Creates and initializes a number format object. The argument is used to
     * distinguish between a format object for a decimal number or an integer
     * number.
     *
     * @param ctx the transformer context
     * @param decimal the decimal flag
     * @return the format object to use
     */
    private NumberFormat createNumberFormat(TransformerContext ctx,
            boolean decimal)
    {
        NumberFormat format;
        Configuration config = new MapConfiguration(ctx.properties());

        if (decimal)
        {
            format = NumberFormat.getInstance(ctx.getLocale());
            format.setMaximumFractionDigits(config.getInt(
                    PROP_MAXIMUM_FRACTION_DIGITS, getMaximumFractionDigits()));
            format.setMinimumFractionDigits(config.getInt(
                    PROP_MINIMUM_FRACTION_DIGITS, getMinimumFractionDigits()));
        }
        else
        {
            format = NumberFormat.getIntegerInstance(ctx.getLocale());
        }

        format.setGroupingUsed(config.getBoolean(PROP_GROUPING_USED,
                isGroupingUsed()));
        return format;
    }
}
