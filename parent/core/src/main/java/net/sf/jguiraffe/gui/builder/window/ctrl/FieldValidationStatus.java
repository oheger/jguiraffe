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
package net.sf.jguiraffe.gui.builder.window.ctrl;

import net.sf.jguiraffe.transform.DefaultValidationResult;
import net.sf.jguiraffe.transform.ValidationMessageLevel;
import net.sf.jguiraffe.transform.ValidationResult;

/**
 * <p>
 * An enumeration class describing the possible validation status values of a
 * form field.
 * </p>
 * <p>
 * While a user edits a form, validation can be performed concurrently. So the
 * user gets immediate feedback, which fields are valid and which are not. The
 * status of a field as related to validation is defined using this enumeration.
 * Possible states a field can be in are the following:
 * <ul>
 * <li>The field has already been visited and is valid.</li>
 * <li>The field has already been visited and is invalid.</li>
 * <li>The field has already been visited and is in warning state.</li>
 * <li>The field has not yet been visited and is valid.</li>
 * <li>The field has not yet been visited and is invalid. This could be the case
 * for instance for mandatory fields that do not have an initial value.</li>
 * <li>The field has not yet been visited and is in warning state.</li>
 * </ul>
 * </p>
 * <p>
 * The validation status is typically evaluated by objects that observe
 * validation operations. Such objects can register at a {@link FormController}
 * as {@link FormControllerValidationListener}. They are then notified whenever
 * a validation takes place which may impact the validation status of a field.
 * In reaction on such a change the UI may be updated, e.g. fields with invalid
 * content may be marked in a specific way.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FieldValidationStatus.java 205 2012-01-29 18:29:57Z oheger $
 */
public enum FieldValidationStatus
{
    /** The field is valid (and has already been visited). */
    VALID,

    /** The field is invalid (and has already been visited). */
    INVALID,

    /** The field is in warning state (and has already been visited). */
    WARNING,

    /** The field has not yet been visited and is valid. */
    NOT_VISITED_VALID,

    /** The field has not yet been visited and is invalid. */
    NOT_VISITED_INVALID,

    /** The field has not yet been visited and contains a warning. */
    NOT_VISITED_WARNING;

    /**
     * Returns the {@code FieldValidationStatus} instance that corresponds to
     * the given {@code ValidationResult} and visited status.
     *
     * @param vres the {@code ValidationResult} (can be <b>null</b>, then the
     *        result is considered valid)
     * @param visited the visited status
     * @return the corresponding {@code FieldValidationStatus} instance
     */
    public static FieldValidationStatus getStatus(ValidationResult vres,
            boolean visited)
    {
        ValidationResult result = (vres != null) ? vres
                : DefaultValidationResult.VALID;

        if (!result.isValid())
        {
            return visited ? INVALID : NOT_VISITED_INVALID;
        }

        if (result.hasMessages(ValidationMessageLevel.WARNING))
        {
            return visited ? WARNING : NOT_VISITED_WARNING;
        }

        return visited ? VALID : NOT_VISITED_VALID;
    }
}
