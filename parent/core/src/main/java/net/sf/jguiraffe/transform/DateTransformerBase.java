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

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * An abstract base class for date transformer objects.
 * </p>
 * <p>
 * Date transformers know how to handle certain kinds of date formats. They can
 * <ul>
 * <li>validate a string entered by a user to verify that it contains a valid
 * date according to the supported format</li>
 * <li>perform certain semantic checks, e.g. whether the date is in the past or
 * future</li>
 * <li>transform a valid string into a <code>java.util.Date</code> object</li>
 * <li>transform a <code>java.util.Date</code> object into a string
 * representation.</li>
 * </ul>
 * </p>
 * <p>
 * This base class already implements the major part of the required
 * functionality. Concrete sub classes are responsible of creating an
 * appropriate <code>DateFormat</code> object that is able to parse the
 * specific date format.
 * </p>
 * <p>
 * There are some properties for customizing the parsing of date strings. These
 * properties can be set either through the set methods provided by this class
 * or using a <code>&lt;properties&gt;</code> section in the builder script
 * that declares the transformer. The following properties are supported: <table
 * border="1">
 * <tr>
 * <th>Property</th>
 * <th>Description</th>
 * <th>Default</th>
 * </tr>
 * <tr>
 * <td valign="top">style</td>
 * <td>Defines the style of the date. This can be one of the style constants
 * declared by the <code>java.text.DateFormat</code> class like
 * <code>SHORT</code> or <code>FULL</code>.</td>
 * <td valign="top">SHORT</td>
 * </tr>
 * <tr>
 * <td valign="top">lenient</td>
 * <td>Specifies the lenient mode for parsing dates. The lenient flag has the
 * same meaning as described in the documentation of the
 * <code>java.text.DateFormat</code> class and controls how strict the parsing
 * process is. Note that lenient mode is turned off per default.</td>
 * <td valign="top">false</td>
 * </tr>
 * <tr>
 * <td valign="top">referenceDate</td>
 * <td>With this property a reference date can be specified that is used for
 * testing semantic correctness. For instance, if one of the <code>after</code>
 * or <code>before</code> flags described below are set, it can be tested
 * whether the entered date is after or before this reference date. The property
 * must be a string conforming to one of the formats supported by
 * <code>java.sql.Timestamp</code>, <code>java.sql.Date</code>, or
 * <code>java.sql.Time</code>.</td>
 * <td valign="top">current date</td>
 * </tr>
 * <tr>
 * <td valign="top">after</td>
 * <td>If this boolean flag is set, the entered date must be after the
 * reference date.</td>
 * <td valign="top">false</td>
 * </tr>
 * <tr>
 * <td valign="top">before</td>
 * <td>If this boolean flag is set, the entered date must be before the
 * reference date. Note that the properties <code>before</code> and
 * <code>after</code> are mutual exclusive.</td>
 * <td valign="top">false</td>
 * </tr>
 * <tr>
 * <td valign="top">equal</td>
 * <td>This flag is evaluated only if <code>after</code> or
 * <code>before</code> is <b>true</b>. In this case, it controls whether the
 * reference date is included in the comparison. So a comparison can be
 * specified whether the entered date is before or equal a reference date.</td>
 * <td valign="top">false</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * Depending on the performed validations this validator implementation can
 * create a bunch of error messages. The following table lists all supported
 * error messages: <table border="1">
 * <tr>
 * <th>Message key</th>
 * <th>Description</th>
 * <th>Parameters</th>
 * </tr>
 * <tr>
 * <td valign="top"><code>{@value ValidationMessageConstants#ERR_INVALID_DATE}</code></td>
 * <td valign="top">The passed in string cannot be parsed to a date object.</td>
 * <td valign="top">{0} = the date string</td>
 * </tr>
 * <tr>
 * <td valign="top"><code>{@value ValidationMessageConstants#ERR_DATE_AFTER}</code></td>
 * <td valign="top">The entered date must be after the reference date.</td>
 * <td valign="top">{0} = the (formatted) reference date</td>
 * </tr>
 * <tr>
 * <td valign="top"><code>{@value ValidationMessageConstants#ERR_DATE_AFTER_EQUAL}</code></td>
 * <td valign="top">The entered date must be after or equal the reference date.</td>
 * <td valign="top">{0} = the (formatted) reference date</td>
 * </tr>
 * <tr>
 * <td valign="top"><code>{@value ValidationMessageConstants#ERR_DATE_BEFORE}</code></td>
 * <td valign="top">The entered date must be before the reference date.</td>
 * <td valign="top">{0} = the (formatted) reference date</td>
 * </tr>
 * <tr>
 * <td valign="top"><code>{@value ValidationMessageConstants#ERR_DATE_BEFORE_EQUAL}</code></td>
 * <td valign="top">The entered date must be before or equal the reference
 * date.</td>
 * <td valign="top">{0} = the (formatted) reference date</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * This class implements both the <code>{@link Transformer}</code> and the
 * <code>{@link Validator}</code> interfaces. The <code>Transformer</code>
 * implementation can work in both directions: if a <code>Date</code> object
 * is passed in, it will format the date to a string using the specified format.
 * Otherwise the passed in object is tried to be converted to a date.
 * </p>
 * <p>
 * Instances can be shared between multiple input components. It is especially
 * possible to use an instance as both (read and write) transformer and
 * validator for an input component at the same time (provided that the same
 * properties are used). However the class is not thread-safe.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: DateTransformerBase.java 205 2012-01-29 18:29:57Z oheger $
 * @see ValidationMessageConstants
 */
public abstract class DateTransformerBase implements Transformer, Validator
{
    /** Constant for the style property. */
    protected static final String PROP_STYLE = "style";

    /** Constant for the lenient property. */
    protected static final String PROP_LENIENT = "lenient";

    /** Constant for the referenceDate property. */
    protected static final String PROP_REFERENCE_DATE = "referenceDate";

    /** Constant for the before property. */
    protected static final String PROP_BEFORE = "before";

    /** Constant for the after property. */
    protected static final String PROP_AFTER = "after";

    /** Constant for the equal property. */
    protected static final String PROP_EQUAL = "equal";

    /** An array with the calendar fields used for a date component. */
    private static final int[] CALENDAR_DATE_FIELDS = {
            Calendar.YEAR, Calendar.MONTH, Calendar.DATE
    };

    /** An array with the calendar fields used for a time component. */
    private static final int[] CALENDAR_TIME_FIELDS = {
            Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND,
            Calendar.MILLISECOND
    };

    /** The logger. */
    private final Log log = LogFactory.getLog(getClass());

    /**
     * Stores the internally used reference date that has been converted into a
     * date object.
     */
    private Date internalReferenceDate;

    /** Stores the reference date for comparisons. */
    private String referenceDate;

    /** Stores the style of the date. */
    private int dateStyle;

    /** Stores the lenient flag. */
    private boolean lenient;

    /** Stores the before flag. */
    private boolean before;

    /** Stores the after flag. */
    private boolean after;

    /** Stores the equal flag. */
    private boolean equal;

    /**
     * Creates a new instance of <code>DateTransformerBase</code>.
     */
    protected DateTransformerBase()
    {
        setStyle(DateFormat.SHORT);
    }

    /**
     * Returns the style for the date to be parsed.
     *
     * @return the date style
     */
    public int getStyle()
    {
        return dateStyle;
    }

    /**
     * Sets the style for the date to be parsed. This is one of the style
     * constants defined by the <code>DateFormat</code> class, e.g.
     * <code>DateFormat.SHORT</code> or <code>DateFormat.MEDIUM</code>.
     *
     * @param dateStyle the style for the date
     */
    public void setStyle(int dateStyle)
    {
        this.dateStyle = dateStyle;
    }

    /**
     * Returns the lenient flag.
     *
     * @return the lenient flag
     */
    public boolean isLenient()
    {
        return lenient;
    }

    /**
     * Sets the lenient flag.
     *
     * @param lenient the lenient flag
     */
    public void setLenient(boolean lenient)
    {
        this.lenient = lenient;
    }

    /**
     * Returns the reference date.
     *
     * @return the reference date
     */
    public String getReferenceDate()
    {
        return referenceDate;
    }

    /**
     * Sets the reference date. This date will be used for before or after
     * comparisons. The date is set as a string. This string must conform to the
     * format supported by the date classes in the <code>java.sql</code>
     * package.
     *
     * @param referenceDate the reference date
     * @throws IllegalArgumentException if the date has not the expected format
     */
    public void setReferenceDate(String referenceDate)
    {
        this.referenceDate = referenceDate;
        internalReferenceDate = (referenceDate == null) ? null
                : transformSqlDate(referenceDate);
    }

    /**
     * Returns the before flag.
     *
     * @return the before flag
     */
    public boolean isBefore()
    {
        return before;
    }

    /**
     * Sets the before flag. If set, the validate method checks whether the
     * passed in date is before the reference date.
     *
     * @param before the before flag
     */
    public void setBefore(boolean before)
    {
        this.before = before;
    }

    /**
     * Returns the after flag.
     *
     * @return the after flag
     */
    public boolean isAfter()
    {
        return after;
    }

    /**
     * Sets the after flag. If set, the validate method checks whether the
     * passed in date is after the reference date.
     *
     * @param after the after flag
     */
    public void setAfter(boolean after)
    {
        this.after = after;
    }

    /**
     * Returns the equal flag.
     *
     * @return the equal flag
     */
    public boolean isEqual()
    {
        return equal;
    }

    /**
     * Sets the equal flag. This flag is evaluated if one of the
     * <code>before</code> or <code>after</code> flags is set. In this case
     * the reference date is included into the comparison.
     *
     * @param equal the value of the equal flag
     */
    public void setEqual(boolean equal)
    {
        this.equal = equal;
    }

    /**
     * Transforms the specified object. This implementation is able to transform
     * a date in string form to a <code>java.util.Date</code> object. If the
     * date is invalid, an exception is thrown. The method does not perform any
     * additional validity checks. This means that any valid date will be
     * returned, even if it conflicts with a reference date.
     *
     * @param o the object to be transformed
     * @param ctx the transformer context
     * @return the transformed object
     * @throws Exception if an error occurs
     */
    public Object transform(Object o, TransformerContext ctx) throws Exception
    {
        if (o instanceof Date)
        {
            return transformToString((Date) o, ctx);
        }
        else
        {
            return transformToDate(o, ctx);
        }
    }

    /**
     * Validates the passed in object. This implementation transforms the object
     * into a string and checks whether it represents a valid date. If the
     * <code>before</code> or <code>after</code> flags have been set, the
     * date will also be compared to a reference date. A <b>null</b> input will
     * be considered valid.
     *
     * @param o the object to be validated
     * @param ctx the transformer context
     * @return an object with the results of the validation
     */
    public ValidationResult isValid(Object o, TransformerContext ctx)
    {
        String strDate = checkDefinedDate(o);
        if (strDate == null)
        {
            return DefaultValidationResult.VALID;
        }

        Configuration config = new MapConfiguration(ctx.properties());
        DateFormat fmt = initializeFormat(ctx.getLocale(), config);
        try
        {
            Date dt = transformDate(strDate, fmt);
            return isDateValid(dt, fmt, ctx, config);
        }
        catch (ParseException pex)
        {
            return errorResult(ValidationMessageConstants.ERR_INVALID_DATE,
                    ctx, strDate);
        }
    }

    /**
     * Writes the given date part into the specified date object. This method
     * will write the date component into a combined date/time object leaving
     * the time component untouched. This is useful for instance if a GUI has
     * different input fields for the date and the time, but in the data model
     * only a single <code>Date</code> object is used. For example, if the
     * <code>dateTime</code> parameter has the value
     * <code>2008-01-29 22:17:59</code> and <code>datePart</code> is
     * <code>2008-02-05</code>, the result will be
     * <code>2008-02-05 22:17:59</code>.
     *
     * @param dateTime the combined date/time object
     * @param datePart the date part
     * @return the changed date/time object
     * @throws IllegalArgumentException if one of the date parameters is <b>null</b>
     */
    public static Date updateDatePart(Date dateTime, Date datePart)
    {
        return updateComponent(dateTime, datePart, CALENDAR_DATE_FIELDS);
    }

    /**
     * Writes the given time part into the specified date object. This method
     * will write the time component into a combined date/time object leaving
     * the date component untouched. This is useful for instance if a GUI has
     * different input fields for the date and the time, but in the data model
     * only a single <code>Date</code> object is used. For example, if the
     * <code>dateTime</code> parameter has the value
     * <code>2008-01-29 22:17:59</code> and <code>timePart</code> is
     * <code>10:22:05</code>, the result will be
     * <code>2008-02-05 10:22:05</code>.
     *
     * @param dateTime the combined date/time object
     * @param timePart the time part
     * @return the changed date/time object
     * @throws IllegalArgumentException if one of the date parameters is <b>null</b>
     */
    public static Date updateTimePart(Date dateTime, Date timePart)
    {
        return updateComponent(dateTime, timePart, CALENDAR_TIME_FIELDS);
    }

    /**
     * Performs a transformation to a date object. Tries to parse the string
     * representation of the parsed in object.
     *
     * @param o the object to be transformed
     * @param ctx the transformer context
     * @return the transformed object
     * @throws Exception if an error occurs
     */
    protected Object transformToDate(Object o, TransformerContext ctx)
            throws Exception
    {
        String strDate = checkDefinedDate(o);
        if (strDate == null)
        {
            return null;
        }

        Configuration config = new MapConfiguration(ctx.properties());
        return transformDate(strDate, initializeFormat(ctx.getLocale(), config));
    }

    /**
     * Performs a transformation from a date to string. This method is called if
     * the object to be transformed is already a date. In this case this
     * transformer class works in the opposite direction.
     *
     * @param dt the date to be transformed
     * @param ctx the transformer context
     * @return the transformed object
     * @throws Exception if an error occurs
     */
    protected Object transformToString(Date dt, TransformerContext ctx)
            throws Exception
    {
        Configuration config = new MapConfiguration(ctx.properties());
        return initializeFormat(ctx.getLocale(), config).format(dt);
    }

    /**
     * Returns the reference date to be used. If a reference date is defined in
     * the configuration, it is used. Otherwise the internally set reference
     * date will be returned. If no reference date has been set, the
     * <code>getDefaultReferenceDate()</code> method is called.
     *
     * @param config the configuration with the current properties
     * @return the reference date
     * @throws IllegalArgumentException if the reference date is in an incorrect
     *         format
     */
    protected Date getReferenceDateProperty(Configuration config)
    {
        String strDate = config.getString(PROP_REFERENCE_DATE);
        if (strDate != null)
        {
            return transformSqlDate(strDate);
        }
        else
        {
            return (internalReferenceDate != null) ? internalReferenceDate
                    : getDefaultReferenceDate();
        }
    }

    /**
     * Creates a new default reference date. This method is invoked when before
     * or after comparisons have to be performed, but no reference date has been
     * set. This implementation returns a <code>Date</code> object for the
     * current date (only date, no time portion).
     *
     * @return the default reference date
     */
    protected Date getDefaultReferenceDate()
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        return cal.getTime();
    }

    /**
     * Transforms a date in string form to a date object. This method expects
     * that the date is in a <code>java.sql</code> compatible format.
     *
     * @param strDate the date as a string
     * @return the converted date object
     * @throws IllegalArgumentException if the date cannot be converted
     */
    protected Date transformSqlDate(String strDate)
    {
        if (log.isDebugEnabled())
        {
            log.debug("Trying to transform date string " + strDate);
        }
        try
        {
            return Timestamp.valueOf(strDate);
        }
        catch (IllegalArgumentException iex)
        {
            // no timestamp
            log.debug("Not a time stamp.");
        }

        try
        {
            return java.sql.Date.valueOf(strDate);
        }
        catch (IllegalArgumentException iex)
        {
            // no date
            log.debug("Not a date.");
        }

        return Time.valueOf(strDate);
    }

    /**
     * Parses the specified date string. This implementation uses the passed in
     * <code>DateFormat</code> object for this purpose.
     *
     * @param date the date to be parsed
     * @param fmt the <code>DateFormat</code> to be used
     * @return the parsed date
     * @throws ParseException if the date cannot be parsed
     */
    protected Date transformDate(String date, DateFormat fmt)
            throws ParseException
    {
        ParsePosition ppos = new ParsePosition(0);
        Date result = fmt.parse(date, ppos);
        if (ppos.getErrorIndex() >= 0 || ppos.getIndex() < date.length())
        {
            throw new ParseException("Invalid date: " + date, ppos.getIndex());
        }
        return result;
    }

    /**
     * Returns an initialized format object. This implementation calls
     * <code>createFormat()</code> for obtaining a new format object. Then the
     * object is initialized based on the currently set properties.
     *
     * @param locale the locale
     * @param config the properties associated with the current context
     * @return the initialized format object
     */
    protected DateFormat initializeFormat(Locale locale, Configuration config)
    {
        DateFormat fmt = createFormat(locale, config.getInt(PROP_STYLE,
                getStyle()), config);
        fmt.setLenient(config.getBoolean(PROP_LENIENT, isLenient()));
        return fmt;
    }

    /**
     * Checks the specified date. This method is called by
     * <code>isValid()</code> if the entered date is syntactically correct. It
     * checks for semantic correctness, e.g. whether the date is in correct
     * relation to the reference date.
     *
     * @param date the date to check
     * @param fmt the date format object to be used
     * @param ctx the transformer context
     * @param config the configuration with the properties
     * @return a <code>ValidationResult</code> object with the result of the
     *         validation
     */
    protected ValidationResult isDateValid(Date date, DateFormat fmt,
            TransformerContext ctx, Configuration config)
    {
        String errorKey = null;

        if (config.getBoolean(PROP_AFTER, isAfter())
                || config.getBoolean(PROP_BEFORE, isBefore()))
        {
            int comp = date.compareTo(getReferenceDateProperty(config));
            boolean eqProp = config.getBoolean(PROP_EQUAL, isEqual());
            boolean eq = eqProp && comp == 0;

            if (config.getBoolean(PROP_AFTER, isAfter()))
            {
                if (comp <= 0 && !eq)
                {
                    errorKey = eqProp ? ValidationMessageConstants.ERR_DATE_AFTER_EQUAL
                            : ValidationMessageConstants.ERR_DATE_AFTER;
                }
            }

            if (config.getBoolean(PROP_BEFORE, isBefore()))
            {
                if (comp >= 0 && !eq)
                {
                    errorKey = eqProp ? ValidationMessageConstants.ERR_DATE_BEFORE_EQUAL
                            : ValidationMessageConstants.ERR_DATE_BEFORE;
                }
            }
        }

        if (errorKey != null)
        {
            // an error has occurred => create an error message
            // with the properly formatted reference date as parameter
            String refDate = fmt.format(getReferenceDateProperty(config));
            return errorResult(errorKey, ctx, refDate);
        }
        else
        {
            return DefaultValidationResult.VALID;
        }
    }

    /**
     * Creates a validation result if an error occurred.
     *
     * @param errorKey the key of the error message
     * @param ctx the transformer context
     * @param params optional parameters for the error message
     * @return the validation result with this error
     */
    protected ValidationResult errorResult(String errorKey,
            TransformerContext ctx, Object... params)
    {
        DefaultValidationResult vr = new DefaultValidationResult.Builder()
                .addValidationMessage(
                        ctx.getValidationMessageHandler().getValidationMessage(
                                ctx, errorKey, params)).build();
        return vr;
    }

    /**
     * Creates a <code>DateFormat</code> object for parsing dates of the
     * supported format. Concrete sub classes have to return an appropriate
     * instance of <code>DateFormat</code> (an implementation will probably
     * call the correct <code>getXXXInstance()</code> factory method of
     * <code>DateFormat</code>).
     *
     * @param locale the locale
     * @param style the style to be used
     * @param config a configuration object for accessing the current properties
     * @return the <code>DateFormat</code> object to be used for parsing
     */
    protected abstract DateFormat createFormat(Locale locale, int style,
            Configuration config);

    /**
     * Checks whether the date is defined. It is defined if it is not <b>null</b>
     * and no empty string.
     *
     * @param o the object to be checked
     * @return the object transformed to a string (<b>null</b> if the
     *         parameter is undefined)
     */
    private String checkDefinedDate(Object o)
    {
        if (o == null)
        {
            return null;
        }

        String strDate = String.valueOf(o);
        return (strDate.length() < 1) ? null : strDate;
    }

    /**
     * Converts a date object to a calendar. If the date is undefined, an
     * exception will be thrown.
     *
     * @param dt the date
     * @return the calendar
     * @throws IllegalArgumentException if the date parameter is undefined
     */
    private static Calendar dateToCalendar(Date dt)
    {
        if (dt == null)
        {
            throw new IllegalArgumentException(
                    "Date parameter must not be null!");
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        return cal;
    }

    /**
     * Copies a set of fields from one calendar to another one.
     *
     * @param cal1 the target calendar
     * @param cal2 the source calendar
     * @param fields the fields to copy
     */
    private static void copyCalendarFields(Calendar cal1, Calendar cal2,
            int[] fields)
    {
        for (int field : fields)
        {
            cal1.set(field, cal2.get(field));
        }
    }

    /**
     * Updates a component of a date/time object.
     *
     * @param dateTime the date/time object
     * @param component the component
     * @param fields the fields to be copied for the component
     * @return the resulting date/time object
     * @throws IllegalArgumentException if one of the dates is undefined
     */
    private static Date updateComponent(Date dateTime, Date component,
            int[] fields)
    {
        Calendar cal1 = dateToCalendar(dateTime);
        Calendar cal2 = dateToCalendar(component);
        copyCalendarFields(cal1, cal2, fields);
        return cal1.getTime();
    }
}
