/*
 * Copyright 2006-2025 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.forms;

import java.util.Set;

import net.sf.jguiraffe.transform.ValidationResult;

/**
 * <p>
 * Definition of an interface for describing results of a form validation.
 * </p>
 * <p>
 * This interface is closely related to the
 * <code>{@link net.sf.jguiraffe.transform.ValidationResult ValidationResult}</code>
 * interface from the <code>transform</code> package. The difference is that
 * it does not represent results of a single field's validation, but can contain
 * multiple result objects for an arbitrary number of fields. So it can easily
 * be checked, which fields of a form are valid and which are not, and for
 * fields with invalid data the corresponding error messages can be retrieved.
 * </p>
 * <p>
 * Basically objects implementing this interface can be seen as composite
 * validation result objects. By providing the name of a field the corresponding
 * <code>ValidationResult</code> object can be obtained. There are also
 * methods for testing if the whole validation was successful or for retrieving
 * only the names of the fields that contain invalid data.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FormValidatorResults.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface FormValidatorResults
{
    /**
     * Checks if the whole validation was successful. If this method returns
     * <b>true </b>, there are no form fields that caused validation errors.
     *
     * @return a flag if the validation of the form was successful
     */
    boolean isValid();

    /**
     * Returns a set with the names of all fields, for which result objects are
     * stored in this object.
     *
     * @return a set with the names of all available fields
     */
    Set<String> getFieldNames();

    /**
     * Returns a set with the names of only those fields, for which validation
     * has failed.
     *
     * @return a set with the names of the error fields
     */
    Set<String> getErrorFieldNames();

    /**
     * Returns the <code>ValidationResult</code> object for the specified
     * field. This object can then be used to check if validation of this field
     * was successful or to retrieve all available error messages.
     *
     * @param field the name of the desired field
     * @return the validation result object for this field
     */
    ValidationResult getResultsFor(String field);
}
