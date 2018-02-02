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
package net.sf.jguiraffe.transform;

/**
 * <p>
 * This class defines constants for the keys of validation messages.
 * </p>
 * <p>
 * In this class the keys for the validation messages generated by the default
 * validators shipped with the framework are defined.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ValidationMessageConstants.java 205 2012-01-29 18:29:57Z oheger $
 */
public final class ValidationMessageConstants
{
    /**
     * The default caption of a message box for displaying validation messages.
     * If validation messages have to be displayed to the end user directly,
     * this constant can be used for resolving the default caption of this
     * message box.
     */
    public static final String ERR_MESSAGE_CAPTION = "ERR_MESSAGE_CAPTION";

    /**
     * The entered string is no valid date.
     */
    public static final String ERR_INVALID_DATE = "ERR_INVALID_DATE";

    /**
     * The entered date must be before a reference date.
     */
    public static final String ERR_DATE_BEFORE = "ERR_DATE_BEFORE";

    /**
     * The entered date must be before or equal a reference date.
     */
    public static final String ERR_DATE_BEFORE_EQUAL = "ERR_DATE_BEFORE_EQUAL";

    /**
     * The entered date must be after a reference date.
     */
    public static final String ERR_DATE_AFTER = "ERR_DATE_AFTER";

    /**
     * The entered date must be after or equal a reference date.
     */
    public static final String ERR_DATE_AFTER_EQUAL = "ERR_DATE_AFTER_EQUAL";

    /**
     * The entered string is no valid time.
     */
    public static final String ERR_INVALID_TIME = "ERR_INVALID_TIME";

    /**
     * The entered time must be before a reference date.
     */
    public static final String ERR_TIME_BEFORE = "ERR_TIME_BEFORE";

    /**
     * The entered time must be before or equal a reference date.
     */
    public static final String ERR_TIME_BEFORE_EQUAL = "ERR_TIME_BEFORE_EQUAL";

    /**
     * The entered time must be after a reference date.
     */
    public static final String ERR_TIME_AFTER = "ERR_TIME_AFTER";

    /**
     * The entered time must be after or equal a reference date.
     */
    public static final String ERR_TIME_AFTER_EQUAL = "ERR_TIME_AFTER_EQUAL";

    /**
     * The entered string is not a valid number.
     */
    public static final String ERR_INVALID_NUMBER = "ERR_INVALID_NUMBER";

    /**
     * The entered number is too small or too big for the supported value range.
     */
    public static final String ERR_NUMBER_OUT_OF_RANGE = "ERR_NUMBER_OUT_OF_RANGE";

    /**
     * The entered number is too small.
     */
    public static final String ERR_NUMBER_TOO_SMALL = "ERR_NUMBER_TOO_SMALL";

    /**
     * The entered number is too big.
     */
    public static final String ERR_NUMBER_TOO_BIG = "ERR_NUMBER_TOO_BIG";

    /**
     * The entered number must be in a specified interval.
     */
    public static final String ERR_NUMBER_INTERVAL = "ERR_NUMBER_INTERVAL";

    /**
     * This is a mandatory field. A value must be entered.
     */
    public static final String ERR_FIELD_REQUIRED = "ERR_FIELD_REQUIRED";

    /**
     * The input does not match the specified pattern.
     */
    public static final String ERR_PATTERN = "ERR_PATTERN";

    /**
     * Private constructor so that no instances can be created.
     */
    private ValidationMessageConstants()
    {
    }
}
