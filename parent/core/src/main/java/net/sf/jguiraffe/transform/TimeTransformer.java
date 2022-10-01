/*
 * Copyright 2006-2022 The JGUIraffe Team.
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

import java.text.DateFormat;
import java.util.Locale;

import org.apache.commons.configuration.Configuration;

/**
 * <p>
 * A specialized transformer that transforms strings into date objects, taking
 * only the time portion into account.
 * </p>
 * <p>
 * Most of the required functionality is already implemented by the base class.
 * This class only creates an appropriate <code>DateFormat</code> object that
 * supports parsing time values.
 * </p>
 * <p>
 * For the documentation of the supported properties refer to the super class.
 * The error messaged produced by this class are analogous to the ones used by
 * <code>DateTransformerBase</code>, but specific for time objects. So an
 * application can display different error messages for invalid date and time
 * inputs. The following table lists the possible error messages: <table
 * border="1">
 * <tr>
 * <th>Message key</th>
 * <th>Description</th>
 * <th>Parameters</th>
 * </tr>
 * <tr>
 * <td valign="top"><code>{@value ValidationMessageConstants#ERR_INVALID_TIME}</code></td>
 * <td valign="top">The passed in string cannot be parsed to a time object.</td>
 * <td valign="top">{0} = the time string</td>
 * </tr>
 * <tr>
 * <td valign="top"><code>{@value ValidationMessageConstants#ERR_TIME_AFTER}</code></td>
 * <td valign="top">The entered time must be after the reference date.</td>
 * <td valign="top">{0} = the (formatted) reference date</td>
 * </tr>
 * <tr>
 * <td valign="top"><code>{@value ValidationMessageConstants#ERR_TIME_AFTER_EQUAL}</code></td>
 * <td valign="top">The entered time must be after or equal the reference date.</td>
 * <td valign="top">{0} = the (formatted) reference date</td>
 * </tr>
 * <tr>
 * <td valign="top"><code>{@value ValidationMessageConstants#ERR_TIME_BEFORE}</code></td>
 * <td valign="top">The entered time must be before the reference date.</td>
 * <td valign="top">{0} = the (formatted) reference date</td>
 * </tr>
 * <tr>
 * <td valign="top"><code>{@value ValidationMessageConstants#ERR_TIME_BEFORE_EQUAL}</code></td>
 * <td valign="top">The entered time must be before or equal the reference
 * date.</td>
 * <td valign="top">{0} = the (formatted) reference date</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TimeTransformer.java 205 2012-01-29 18:29:57Z oheger $
 * @see ValidationMessageConstants
 */
public class TimeTransformer extends DateTransformerBase
{
    /** Constant for the key of a date error message. */
    private static final String KEY_DATE_MSG = "DATE";

    /** Constant for the key of a time error message. */
    private static final String KEY_TIME_MSG = "TIME";

    /**
     * Returns a format object for formatting and parsing time objects.
     *
     * @param locale the locale
     * @param style the style to be used
     * @param config the configuration with the current properties
     * @return the format object
     */
    @Override
    protected DateFormat createFormat(Locale locale, int style, Configuration config)
    {
        return DateFormat.getTimeInstance(style, locale);
    }

    /**
     * Returns a validation result object with an error message. This
     * implementation transforms error messages for invalid dates to messages
     * for invalid times.
     *
     * @param errorKey the key of the error message
     * @param ctx the transformer context
     * @param params additional parameters
     * @return the validation result object with the specified error message
     */
    @Override
    protected ValidationResult errorResult(String errorKey,
            TransformerContext ctx, Object... params)
    {
        return super.errorResult(errorKey.replace(KEY_DATE_MSG, KEY_TIME_MSG),
                ctx, params);
    }
}
