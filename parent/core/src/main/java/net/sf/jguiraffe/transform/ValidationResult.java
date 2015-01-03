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

import java.util.Collection;

/**
 * <p>
 * An interface that defines the results of a validation process.
 * </p>
 * <p>
 * Instances of this class are returned by {@link Validator} objects. They
 * contain all information about the validation results: a flag whether the
 * validation was successful and a list with messages generating during the
 * validation operation. Messages can be either error or warning messages.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ValidationResult.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface ValidationResult
{
    /**
     * Returns a flag if the validation was successful. If this method returns
     * <b>true</b>, the checked object can be considered valid.
     *
     * @return a flag if the validation was successful
     */
    boolean isValid();

    /**
     * Returns a collection with all {@code ValidationMessage} objects that were
     * created during validation. If {@link #isValid()} returns <b>false</b>,
     * this collection should at least contain one element with the
     * {@link ValidationMessageLevel} {@code ERROR}. The objects in the returned
     * collection can be used to find out, which specific errors have been
     * occurred and for displaying messages to the user. The returned list must
     * not be <b>null</b>.
     *
     * @return a list with validation messages
     */
    Collection<ValidationMessage> getValidationMessages();

    /**
     * Returns a flag whether this object contains validation messages of the
     * specified level. This is convenient to find out whether there are errors
     * or warnings without having to actually retrieve the messages.
     *
     * @param level the {@code ValidationMessageLevel} to check
     * @return a flag whether there are messages of this {@code
     *         ValidationMessageLevel}
     */
    boolean hasMessages(ValidationMessageLevel level);

    /**
     * Returns a collection with all {@code ValidationMessage} objects of the
     * specified level that were created during validation. This method allows
     * filtering for a specific message level. An implementation should never
     * return <b>null</b>.
     *
     * @param level the {@code ValidationMessageLevel}
     * @return a collection with the available messages of this level
     */
    Collection<ValidationMessage> getValidationMessages(
            ValidationMessageLevel level);
}
