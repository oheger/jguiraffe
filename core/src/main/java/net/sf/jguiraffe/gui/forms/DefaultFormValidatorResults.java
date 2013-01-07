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
package net.sf.jguiraffe.gui.forms;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import net.sf.jguiraffe.transform.DefaultValidationResult;
import net.sf.jguiraffe.transform.ValidationMessage;
import net.sf.jguiraffe.transform.ValidationResult;

/**
 * <p>
 * Default implementation of the {@code FormValidatorResults} interface.
 * </p>
 * <p>
 * This class provides a fully functional implementation of the {@code
 * FormValidatorResults} interface. Instances are initialized with a map that
 * contains the names of the validated fields and their corresponding
 * {@link ValidationResult} objects. They are immutable and thus can be shared
 * between multiple threads.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: DefaultFormValidatorResults.java 205 2012-01-29 18:29:57Z oheger $
 */
public class DefaultFormValidatorResults implements FormValidatorResults
{
    /** Stores the fields and their validation results. */
    private final Map<String, ValidationResult> fields;

    /** A set with the names of all fields. */
    private final Set<String> fieldNames;

    /** A list with the names of the error fields. */
    private final Set<String> errorFieldNames;

    /**
     * Creates a new instance of {@code DefaultFormValidatorResults} and
     * initializes it with a map holding information about fields and their
     * validation status.
     *
     * @param fieldData the map with the field data (must not be <b>null</b>
     * @throws IllegalArgumentException if the map is <b>null</b> or contains
     *         <b>null</b> values
     */
    public DefaultFormValidatorResults(
            Map<String, ? extends ValidationResult> fieldData)
    {
        if (fieldData == null)
        {
            throw new IllegalArgumentException(
                    "Map with field data must not be null!");
        }

        fields = new LinkedHashMap<String, ValidationResult>(fieldData);
        Set<String> errFlds = new LinkedHashSet<String>();
        for (Map.Entry<String, ValidationResult> e : fields.entrySet())
        {
            if (e.getValue() == null)
            {
                throw new IllegalArgumentException(
                        "Map with field data contains a null value!");
            }
            if (!e.getValue().isValid())
            {
                errFlds.add(e.getKey());
            }
        }

        fieldNames = Collections.unmodifiableSet(fields.keySet());
        errorFieldNames = Collections.unmodifiableSet(errFlds);
    }

    /**
     * Creates a {@code java.util.Map} with validation information for all
     * fields of the specified {@code Form}. Each field is assigned a valid
     * {@link ValidationResult} object. This method can be used by custom
     * {@code FormValidator} implementations to initialize a map with data about
     * all form fields. Then, during validation, fields that were detected to be
     * invalid can be updated in this map. Finally, a {@code
     * DefaultFormValidatorResults} object can be created based on this map.
     *
     * @param form the {@code Form} (must not be <b>null</b>)
     * @return a map with valid validation data for all fields of the form
     * @throws IllegalArgumentException if the {@code Form} is <b>null</b>
     */
    public static Map<String, ValidationResult> validResultMapForForm(Form form)
    {
        if (form == null)
        {
            throw new IllegalArgumentException("Form must not be null!");
        }

        Map<String, ValidationResult> map = new LinkedHashMap<String, ValidationResult>();
        for (String field : form.getFieldNames())
        {
            map.put(field, DefaultValidationResult.VALID);
        }

        return map;
    }

    /**
     * Creates a valid {@code DefaultFormValidatorResults} for the fields of the
     * specified {@code Form}. The resulting object stores a valid
     * {@link ValidationResult} object for all fields that belong to the form.
     *
     * @param form the {@code Form} (must not be <b>null</b>)
     * @return a valid {@code DefaultFormValidatorResults} object for this form
     * @throws IllegalArgumentException if the {@code Form} is <b>null</b>
     */
    public static DefaultFormValidatorResults validResultsForForm(Form form)
    {
        return new DefaultFormValidatorResults(validResultMapForForm(form));
    }

    /**
     * Obtains a {@code ValidationMessage} object for the specified key. The
     * message is obtained from the current
     * {@link net.sf.jguiraffe.transform.ValidationMessageHandler
     * ValidationMessageHandler} (which can be retrieved from the form's
     * {@code TransformerContext}).
     *
     * @param form the {@code Form} (must not be <b>null</b>)
     * @param key the key of the validation error message
     * @param params additional parameters for the validation error message
     * @return a {@code ValidationMessage} object for the specified error
     *         message
     * @throws IllegalArgumentException if the {@code Form} is <b>null</b>
     */
    public static ValidationMessage createValidationMessage(Form form,
            String key, Object... params)
    {
        if (form == null)
        {
            throw new IllegalArgumentException("Form must not be null!");
        }

        return DefaultValidationResult.createValidationMessage(form
                .getTransformerContext(), key, params);
    }

    /**
     * Creates a {@code ValidationResult} object with an error message for a
     * validation message. This is a convenience method that obtains the
     * validation error message from the current
     * {@link net.sf.jguiraffe.transform.ValidationMessageHandler
     * ValidationMessageHandler} and creates the corresponding
     * {@code ValidationResult} object. It can be used for instance by a
     * {@code FormValidator} that determines an incorrect field.
     *
     * @param form the {@code Form} (must not be <b>null</b>)
     * @param key the key of the validation error message
     * @param params additional parameters for the validation error message
     * @return a {@code ValidationResult} object initialized with this error
     *         message
     * @throws IllegalArgumentException if the {@code Form} is <b>null</b>
     */
    public static ValidationResult createValidationErrorResult(Form form,
            String key, Object... params)
    {
        if (form == null)
        {
            throw new IllegalArgumentException("Form must not be null!");
        }

        return DefaultValidationResult.createValidationErrorResult(form
                .getTransformerContext(), key, params);
    }

    /**
     * Returns a {@code FormValidatorResults} object with the combined
     * information of the specified {@code FormValidatorResults} objects. If one
     * of the passed in objects is <b>null</b>, the other one is returned.
     * Otherwise a new {@code FormValidatorResults} instance is created and
     * populated with the {@code ValidationResult} objects contained in both
     * parameter objects. If necessary, the {@code ValidationResult} objects are
     * also merged, so that the resulting object actually contains a union of
     * all validation messages.
     *
     * @param res1 the first {@code FormValidatorResults} object
     * @param res2 the second {@code FormValidatorResults} object
     * @return the merged results
     */
    public static FormValidatorResults merge(FormValidatorResults res1,
            FormValidatorResults res2)
    {
        if (res1 == null)
        {
            return res2;
        }
        if (res2 == null)
        {
            return res1;
        }

        Set<String> names = new HashSet<String>(res1.getFieldNames());
        names.addAll(res2.getFieldNames());
        Map<String, ValidationResult> map = new HashMap<String, ValidationResult>();
        for (String field : names)
        {
            map.put(field, DefaultValidationResult.merge(res1
                    .getResultsFor(field), res2.getResultsFor(field)));
        }

        return new DefaultFormValidatorResults(map);
    }

    /**
     * Checks whether form validation was successful.
     *
     * @return a flag if all fields are valid
     */
    public boolean isValid()
    {
        return errorFieldNames.isEmpty();
    }

    /**
     * Returns a set with all defined field names.
     *
     * @return a set with the field names
     */
    public Set<String> getFieldNames()
    {
        return fieldNames;
    }

    /**
     * Returns a set with the names of those fields that are invalid.
     *
     * @return a set with the names of the invalid fields
     */
    public Set<String> getErrorFieldNames()
    {
        return errorFieldNames;
    }

    /**
     * Returns the validation results for the specified field or <b>null</b> if
     * this field does not exist.
     *
     * @param field the name of the desired field
     * @return the validation result for this field
     */
    public ValidationResult getResultsFor(String field)
    {
        return fields.get(field);
    }

    /**
     * Compares this object with another one. Two form validator result objects
     * are considered equal if and only if they contain the same set of fields
     * with corresponding result objects.
     *
     * @param obj the object to compare to
     * @return a flag whether the objects are equal
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof DefaultFormValidatorResults))
        {
            return false;
        }
        return fields.equals(((DefaultFormValidatorResults) obj).fields);
    }

    /**
     * Determines a hash code for this object.
     *
     * @return a hash code
     */
    @Override
    public int hashCode()
    {
        return fields.hashCode();
    }

    /**
     * Returns a string representation for this object. This string contains the
     * string representation for the internal map with the fields and their
     * validation result objects. Provided that the {@code ValidationResult}
     * objects have proper {@code toString()} implementations, the string
     * generated by the map is exactly what is needed to see which field has
     * which validation status.
     *
     * @return a string for this object
     */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append(getClass().getName()).append('@');
        buf.append(System.identityHashCode(this));
        buf.append("[ ");
        buf.append(fields.toString());
        buf.append(" ]");
        return buf.toString();
    }
}
