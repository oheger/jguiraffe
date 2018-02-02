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
package net.sf.jguiraffe.gui.forms;

/**
 * <p>
 * Definition of an interface for objects that can validate forms.
 * </p>
 * <p>
 * While a normal validator can check only single fields of a form a {@code
 * FormValidator} can deal with a form as a whole and thus can evaluate complex
 * conditions and dependencies between the single elements.
 * </p>
 * <p>
 * This kind of validation takes places only after validation on both the field
 * and form level have succeeded. Validations performed by {@code FormValidator}
 * implementations are logically related to form level validations. The main
 * difference is that a <code>FormValidator</code> can access all form fields at
 * once and so is able to check relations between fields, too.
 * </p>
 * <p>
 * Validation of a form as a whole is very specific and strongly depends on the
 * data fields contained in the form and its model. So there is no default base
 * implementation of this interface. A concrete implementation can access the
 * whole data that was entered into the form - either by querying the
 * {@link FieldHandler} objects of the form or by calling the
 * {@link Form#readFields(Object)} method passing in an appropriate data object
 * - and perform arbitrary checks.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FormValidator.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface FormValidator
{
    /**
     * Validates the specified form. When this method is invoked by the form
     * framework it is guaranteed that field and form level validation have
     * passed. Thus all {@link FieldHandler} objects contained in the form have
     * been initialized with the current data entered by the user. One way to
     * obtain this data is by calling {@code getData()} on a {@code
     * FieldHandler}. An alternative is to call {@code readFields()} on the
     * {@code Form} object and let the data be copied into a corresponding model
     * object. (This model object must of course be compatible with the
     * {@link BindingStrategy} used by the {@code Form}.
     *
     * @param form the form object to be validated
     * @return an object with validation results
     */
    FormValidatorResults isValid(Form form);
}
