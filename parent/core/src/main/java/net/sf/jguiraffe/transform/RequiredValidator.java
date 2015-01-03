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

import java.lang.reflect.Array;

/**
 * <p>
 * A specialized {@link Validator} implementation that checks
 * whether data was entered in mandatory fields.
 * </p>
 * <p>
 * This validator implementation can be assigned to mandatory input fields of
 * different types. When the form is validated it checks if the field contains
 * data. The {@code isValid()} method is implemented in a generic way supporting
 * multiple data types. Thus this validator can collaborate with input fields of
 * different types. An object passed to the {@code isValid()} method is
 * rejected in any of the following cases:
 * <ul>
 * <li>the object is <b>null</b></li>
 * <li>the object is a string that is empty or contains only whitespace</li>
 * <li>the object implements the {@code Iterable} interface, and the
 * iteration does not contain any elements</li>
 * <li>the object is an array with no elements</li>
 * </ul>
 * In any other case the object is accepted.
 * </p>
 * <p>
 * {@code RequiredValidator} does not support any properties. The
 * following table lists the error messages that are generated by this validator
 * class: <table border="1">
 * <tr>
 * <th>Message key</th>
 * <th>Description</th>
 * <th>Parameters</th>
 * </tr>
 * <tr>
 * <td valign="top"><code>{@value ValidationMessageConstants#ERR_FIELD_REQUIRED}</code></td>
 * <td valign="top">This input field must contain a value. This is the error
 * code which will be returned when the passed in object is rejected.</td>
 * <td valign="top">no parameters</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * An instance of this class can be shared between multiple input components. If
 * additional validations are to be performed for a mandatory input field, a
 * {@link ChainValidator} can be used that is initialized with a
 * {@code RequiredValidator} and arbitrary other validator
 * implementations.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: RequiredValidator.java 205 2012-01-29 18:29:57Z oheger $
 */
public class RequiredValidator implements Validator
{
    /**
     * Validates the passed in object. This implementation checks whether the
     * object is defined.
     *
     * @param o the object to be validated
     * @param ctx the transformer context
     * @return a result object indicating whether the object is valid
     */
    public ValidationResult isValid(Object o, TransformerContext ctx)
    {
        boolean valid;

        if (o == null)
        {
            valid = false;
        }
        else if (o instanceof String)
        {
            valid = ((String) o).trim().length() > 0;
        }
        else if (o instanceof Iterable<?>)
        {
            valid = ((Iterable<?>) o).iterator().hasNext();
        }
        else if (o.getClass().isArray())
        {
            valid = Array.getLength(o) > 0;
        }
        else
        {
            valid = true;
        }

        if (valid)
        {
            return DefaultValidationResult.VALID;
        }
        else
        {
            DefaultValidationResult vr = new DefaultValidationResult.Builder()
                    .addValidationMessage(
                            ctx
                                    .getValidationMessageHandler()
                                    .getValidationMessage(
                                            ctx,
                                            ValidationMessageConstants.ERR_FIELD_REQUIRED))
                    .build();
            return vr;
        }
    }
}
