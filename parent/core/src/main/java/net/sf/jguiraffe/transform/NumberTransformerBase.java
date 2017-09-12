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
package net.sf.jguiraffe.transform;

import java.lang.reflect.ParameterizedType;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.MapConfiguration;

/**
 * <p>
 * An abstract base class for transformers and validators for numbers.
 * </p>
 * <p>
 * This base class already provides the major part of functionality for
 * validating numeric input and transforming strings to number objects. Concrete
 * sub classes are responsible for the creation of a
 * <code>java.text.NumberFormat</code> object that is used for parsing the
 * user input. The class also supports certain semantic checks, especially
 * whether an entered number lies in a specified interval.
 * </p>
 * <p>
 * This class makes use of Java generics to be independent on a concrete number
 * type. The returned (transformed) object will be of this type, and also the
 * specified minimum or maximum values must use this type.
 * </p>
 * <p>
 * The following properties are supported by this class:
 * </p>
 * <table border="1">
 * <tr>
 * <th>Property</th>
 * <th>Description</th>
 * <th>Default</th>
 * </tr>
 * <tr>
 * <td valign="top">minimum</td>
 * <td>Here the minimum value can be defined. Entered numbers are checked to be
 * greater or equal than this number. If this property is undefined, no minimum
 * checks will be performed.</td>
 * <td valign="top">undefined</td>
 * </tr>
 * <tr>
 * <td valign="top">maximum</td>
 * <td>Here the maximum value can be defined. Entered numbers are checked to be
 * less or equal than this number. If this property is undefined, no maximum
 * checks will be performed.</td>
 * <td valign="top">undefined</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * Validation of user input can fail for multiple reasons. The following table
 * lists the possible error messages: <table border="1">
 * <tr>
 * <th>Message key</th>
 * <th>Description</th>
 * <th>Parameters</th>
 * </tr>
 * <tr>
 * <td valign="top"><code>{@value ValidationMessageConstants#ERR_INVALID_NUMBER}</code></td>
 * <td>The passed in string cannot be parsed to a number object.</td>
 * <td valign="top">{0} = the input string</td>
 * </tr>
 * <tr>
 * <td valign="top"><code>{@value ValidationMessageConstants#ERR_NUMBER_TOO_SMALL}</code></td>
 * <td>The entered number is too small. This error message is returned if the
 * number is less than the specified minimum number and no maximum number was
 * specified. (If both a minimum and a maximum number are specified, the error
 * code <code>{@value ValidationMessageConstants#ERR_NUMBER_INTERVAL}</code>
 * is used.)</td>
 * <td valign="top">{0} = the minimum number</td>
 * </tr>
 * <tr>
 * <td valign="top"><code>{@value ValidationMessageConstants#ERR_NUMBER_TOO_BIG}</code></td>
 * <td>The entered number is too big. This error message is returned if the
 * number is greater than the specified maximum number and no minimum number was
 * specified. (If both a minimum and a maximum number are specified, the error
 * code <code>{@value ValidationMessageConstants#ERR_NUMBER_INTERVAL}</code>
 * is used.)</td>
 * <td valign="top">{0} = the maximum number</td>
 * </tr>
 * <tr>
 * <td valign="top"><code>{@value ValidationMessageConstants#ERR_NUMBER_INTERVAL}</code></td>
 * <td>The entered number is not in the interval spanned by the minimum and the
 * maximum value. If both a minimum and a maximum are specified and the entered
 * number does not meet these constraints, this error message is produced rather
 * than one of
 * <code>{@value ValidationMessageConstants#ERR_NUMBER_TOO_SMALL}</code> or
 * <code>{@value ValidationMessageConstants#ERR_NUMBER_TOO_BIG}</code>.</td>
 * <td valign="top">{0} = the minimum value, {1} = the maximum value</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * The class implements both the <code>{@link Transformer}</code> and
 * {@link Validator} interfaces. It is safe to use an instance
 * concurrently as transformer and validator for the same or multiple input
 * fields.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: NumberTransformerBase.java 205 2012-01-29 18:29:57Z oheger $
 * @param <T> the type handled by this transformer
 */
public abstract class NumberTransformerBase<T extends Number> implements
        Transformer, Validator
{
    /** Constant for the minimum property. */
    protected static final String PROP_MINIMUM = "minimum";

    /** Constant for the maximum property. */
    protected static final String PROP_MAXIMUM = "maximum";

    /** Stores the minimum allowed value. */
    private T minimum;

    /** Stores the maximum allowed value. */
    private T maximum;

    /**
     * Returns the minimum value.
     *
     * @return the minimum value (can be <b>null</b>)
     */
    public T getMinimum()
    {
        return minimum;
    }

    /**
     * Sets the minimum value. If a minimum value is specified, the validator
     * implementation will check whether an entered number is not less than this
     * minimum value.
     *
     * @param minimum the minimum value
     */
    public void setMinimum(T minimum)
    {
        this.minimum = minimum;
    }

    /**
     * Returns the maximum value.
     *
     * @return the maximum value (can be <b>null</b>)
     */
    public T getMaximum()
    {
        return maximum;
    }

    /**
     * Sets the maximum value. If a maximum value is specified, the validator
     * implementation will check whether an entered number is not greater than
     * this maximum value.
     *
     * @param maximum the maximum value
     */
    public void setMaximum(T maximum)
    {
        this.maximum = maximum;
    }

    /**
     * Transforms the specified object to the target format. This implementation
     * tries to convert the passed in object to a <code>Number</code> of the
     * type specified by the generics parameter for this class. This is done by
     * using a <code>java.text.NumberFormat</code> object. If the passed in
     * object is <b>null</b>, <b>null</b> will also be returned.
     *
     * @param o the object to be transformed
     * @param ctx the transformer context
     * @return the transformed object
     * @throws Exception if conversion fails
     */
    public Object transform(Object o, TransformerContext ctx) throws Exception
    {
        return transformToNumber(o, ctx, createFormat(ctx.getLocale()));
    }

    /**
     * Validates the specified object. This implementation checks whether the
     * object can be transformed to a number. If this is the case,
     * <code>isNumberValid()</code> will be called to check whether the number
     * lies in a valid range. Depending on these checks a validation result
     * object is returned. A <b>null</b> object or an empty string are
     * considered valid.
     *
     * @param o the object to be validated
     * @param ctx the transformer context
     * @return an object with the results of the validation
     */
    public ValidationResult isValid(Object o, TransformerContext ctx)
    {
        NumberFormat fmt = createFormat(ctx.getLocale());
        try
        {
            T number = transformToNumber(o, ctx, fmt);
            if (number == null)
            {
                return DefaultValidationResult.VALID;
            }
            else
            {
                Configuration config = new MapConfiguration(ctx.properties());
                return isNumberValid(number, fmt, ctx, fetchProperty(config,
                        PROP_MINIMUM, getMinimum()), fetchProperty(config,
                        PROP_MAXIMUM, getMaximum()));
            }
        }
        catch (ParseException pex)
        {
            // the string could not be parsed to a number
            return errorResult(ValidationMessageConstants.ERR_INVALID_NUMBER,
                    ctx, o);
        }
        catch (IllegalArgumentException iex)
        {
            // the number does not fit into the allowed value range
            return errorResult(
                    ValidationMessageConstants.ERR_NUMBER_OUT_OF_RANGE, ctx, o);
        }
    }

    /**
     * Transforms the given object into a number. This method is called by both
     * <code>transform()</code> and <code>isValid()</code>. It performs the
     * actual transformation. The passed in object may be <b>null</b> or empty,
     * in which case <b>null</b> is returned.
     *
     * @param o the object to be transformed
     * @param ctx the transformer context
     * @param fmt the format object to be used
     * @return the converted number
     * @throws ParseException if transformation fails
     */
    protected T transformToNumber(Object o, TransformerContext ctx,
            NumberFormat fmt) throws ParseException
    {
        if (o == null)
        {
            return null;
        }

        String n = String.valueOf(o);
        if (n.length() < 1)
        {
            return null;
        }

        ParsePosition ppos = new ParsePosition(0);
        Number num = fmt.parse(n, ppos);
        if (ppos.getErrorIndex() >= 0 || ppos.getIndex() < n.length())
        {
            throw new ParseException("Invalid number: " + n, ppos.getIndex());
        }

        return convertToTarget(num);
    }

    /**
     * Validates an entered number. This method is called by
     * <code>isValid()</code> if the passed in object can be successfully
     * converted into a number. It checks this number against the minimum and
     * maximum values (if defined).
     *
     * @param n the number to check
     * @param fmt the format object (used for formatting the minimum and/or
     *        maximum values in error messages)
     * @param ctx the transformation context
     * @param min the minimum value (can be <b>null</b>)
     * @param max the maximum value (can be <b>null</b>)
     * @return an object with the results of the validation
     */
    @SuppressWarnings("unchecked")
    protected ValidationResult isNumberValid(T n, NumberFormat fmt,
            TransformerContext ctx, T min, T max)
    {
        Comparable<T> comp = (Comparable<T>) n;

        if (min != null && comp.compareTo(min) < 0)
        {
            return rangeErrorResult(true, ctx, min, max, fmt);
        }

        if (max != null && comp.compareTo(max) > 0)
        {
            return rangeErrorResult(false, ctx, min, max, fmt);
        }

        return DefaultValidationResult.VALID;
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
     * Converts the specified number to the target type supported by this
     * transformer. This method is called after the object to be transformed was
     * parsed by a <code>NumberFormat</code> object. There is no guarantee
     * that the result of this parsing process matches the desired type. If this
     * is not the case, this method will be called.
     *
     * @param n the number to be converted
     * @return the converted number
     * @throws IllegalArgumentException if the number cannot be converted to the
     *         target type (e.g. because it does not fit into the supported
     *         range)
     */
    protected abstract T convert(Number n);

    /**
     * Creates the format object for parsing a number. This method is called
     * whenever a string (entered by the user) has to be converted into a
     * number.
     *
     * @param locale the locale to use
     * @return the format object for parsing the number
     */
    protected abstract NumberFormat createFormat(Locale locale);

    /**
     * Fetches a property of the supported type from the specified configuration
     * object. This method is called for determining the current values of
     * type-related properties (e.g. the minimum and maximum values). An
     * implementation has to invoke the appropriate methods on the passed in
     * <code>Configuration</code> object.
     *
     * @param config the configuration object
     * @param property the name of the property to be obtained
     * @param defaultValue the default value for this property
     * @return the value of this property
     */
    protected abstract T fetchProperty(Configuration config, String property,
            T defaultValue);

    /**
     * Converts the passed in number to the target type if necessary. If the
     * number is already of the target type, nothing is done.
     *
     * @param n the number to convert
     * @return the converted number
     */
    @SuppressWarnings("unchecked")
    private T convertToTarget(Number n)
    {
        Class<?> targetClass = (Class<?>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        if (targetClass.equals(n.getClass()))
        {
            return (T) n;
        }
        else
        {
            return convert(n);
        }
    }

    /**
     * Creates a validation result object when the number is either too small or
     * too big. The error message depends on the fact whether both the minimum
     * and the maximum value are defined.
     *
     * @param tooSmall <b>true</b> if the entered number is too small, <b>false</b>
     *        otherwise
     * @param ctx the transformer context
     * @param min the minimum value
     * @param max the maximum value
     * @param fmt the format object
     * @return the validation result
     */
    private ValidationResult rangeErrorResult(boolean tooSmall,
            TransformerContext ctx, T min, T max, NumberFormat fmt)
    {
        boolean interval = tooSmall ? max != null : min != null;
        if (interval)
        {
            return errorResult(ValidationMessageConstants.ERR_NUMBER_INTERVAL,
                    ctx, fmt.format(min), fmt.format(max));
        }
        else
        {
            if (tooSmall)
            {
                return errorResult(
                        ValidationMessageConstants.ERR_NUMBER_TOO_SMALL, ctx,
                        fmt.format(min));
            }
            else
            {
                return errorResult(
                        ValidationMessageConstants.ERR_NUMBER_TOO_BIG, ctx, fmt
                                .format(max));
            }
        }
    }
}
