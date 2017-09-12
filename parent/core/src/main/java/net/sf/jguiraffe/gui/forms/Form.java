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
package net.sf.jguiraffe.gui.forms;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.jguiraffe.transform.DefaultValidationResult;
import net.sf.jguiraffe.transform.TransformerContext;
import net.sf.jguiraffe.transform.ValidationResult;

/**
 * <p>
 * This class represents a form.
 * </p>
 * <p>
 * Instances of this class can be used to deal with forms, e.g. initializing the
 * form's widgets with data obtained from a model object or validating the
 * user's input. A {@code Form} object must be initialized with objects
 * representing the form elements or fields. These objects are of type
 * {@link FieldHandler} and contain all information needed for correctly
 * handling GUI widgets and the data they may contain.
 * </p>
 * <p>
 * An important functionality of this class is to enable data transfer between
 * the form's fields and the properties of a model object. By properly
 * initializing the {@link FieldHandler} objects with transformers and
 * validators it can be assured that suitable validation and data conversion
 * take place. Model objects are accessed through a {@link BindingStrategy};
 * therefore this class can collaborate with different types of model objects
 * provided that a corresponding {@code BindingStrategy} implementation exists.
 * </p>
 * <p>
 * After a {@code Form} object and its corresponding fields haven been
 * initialized usage of this class is quite simple. To initialize the GUI
 * widgets associated with this form call the {@link #initFields(Object)} method
 * and pass in a model object instance with the values for the fields. (Of
 * course, this model object must be compatible with the {@link BindingStrategy}
 * the form was initialized with.) To perform validation and read the user's
 * input back into a model object the {@link #validate(Object)} method can be
 * used. This method invokes all registered validators, and if validation
 * succeeds, the user input is converted into the correct types and transfered
 * into the given model object.
 * </p>
 * <p>
 * Implementation node: This class is not thread safe; instances should be
 * accessed by a single thread only.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: Form.java 205 2012-01-29 18:29:57Z oheger $
 */
public class Form
{
    /**
     * Constant for a default {@code FormValidator} object. This instance is
     * used if no other {@code FormValidator} was set. It always returns a valid
     * result object.
     */
    private static final FormValidator DEF_FORM_VALIDATOR = new FormValidator()
    {
        // always return a valid results object for this form
        public FormValidatorResults isValid(Form form)
        {
            return DefaultFormValidatorResults.validResultsForForm(form);
        }
    };

    /** Stores the components that belong to this form. */
    private final ComponentStore fields;

    /** The binding strategy for accessing the data of the model object. */
    private final BindingStrategy bindingStrategy;

    /** Stores the registered form validator. */
    private FormValidator formValidator;

    /** Stores the transformer context. */
    private final TransformerContext transformerContext;

    /**
     * Creates a new instance of {@code Form} and initializes it with all
     * required helper objects.
     *
     * @param ctx the {@code TransformerContext} (must not be <b>null</b>)
     * @param strat the {@code BindingStrategy} (must not be <b>null</b>)
     * @throws IllegalArgumentException if a required parameter is <b>null</b>
     */
    public Form(TransformerContext ctx, BindingStrategy strat)
    {
        if (ctx == null)
        {
            throw new IllegalArgumentException(
                    "TransformerContext must not be null!");
        }
        if (strat == null)
        {
            throw new IllegalArgumentException(
                    "BindingStrategy must not be null!");
        }

        transformerContext = ctx;
        bindingStrategy = strat;
        fields = new ComponentStoreImpl();
    }

    /**
     * Returns the transformer context.
     *
     * @return the transformer context
     */
    public TransformerContext getTransformerContext()
    {
        return transformerContext;
    }

    /**
     * Returns the {@code BindingStrategy} used by this form.
     *
     * @return the {@code BindingStrategy}
     */
    public final BindingStrategy getBindingStrategy()
    {
        return fetchBindingStrategy();
    }

    /**
     * Returns the form validator. This can be <b>null</b> if no specific form
     * validator has been set.
     *
     * @return the object used for validating the form
     */
    public FormValidator getFormValidator()
    {
        return formValidator;
    }

    /**
     * Sets the form validator.
     *
     * @param formValidator the form validator
     */
    public void setFormValidator(FormValidator formValidator)
    {
        this.formValidator = formValidator;
    }

    /**
     * Adds the specified field to this form. This method must be called for
     * each field that should be managed by this form object.
     *
     * @param name the field's (internal) name
     * @param fld the field handler for this field
     */
    public void addField(String name, FieldHandler fld)
    {
        fields.addFieldHandler(name, fld);
    }

    /**
     * Fills the form's fields with the properties of the passed in bean. This
     * method can be used to initialize the form.
     *
     * @param bean the form bean; can be <b>null</b>, then this operation has no
     *        effect
     * @throws FormRuntimeException if an error occurs when initializing a field
     */
    public void initFields(Object bean)
    {
        initFields(bean, getFieldNames());
    }

    /**
     * Fills a sub set of the form's fields with the properties of the passed in
     * bean. This method will iterate over all fields specified in the given set
     * and initialize them from the corresponding properties of the specified
     * bean. The set must contain only valid names of fields that belong to this
     * form; otherwise an exception will be thrown.
     *
     * @param bean the form bean; can be <b>null</b>, then this operation has no
     *        effect
     * @param names a set with the names of the fields to be initialized
     * @throws FormRuntimeException if a field cannot be initialized
     * @throws IllegalArgumentException if the set is <b>null</b>
     */
    public void initFields(Object bean, Set<String> names)
    {
        if (bean == null)
        {
            // nothing to do
            return;
        }

        if (names == null)
        {
            throw new IllegalArgumentException("Sub set must not be null!");
        }

        for (String fldName : names)
        {
            FieldHandler fh = fetchField(fldName);
            try
            {
                Object data = readModelProperty(bean, propertyName(fldName));
                fh.setData(data);
            }
            catch (Exception ex)
            {
                // handle all possible exceptions the same way
                throw new FormRuntimeException("Error when initializing field "
                        + fldName, ex);
            }
        }
    }

    /**
     * Validates this form and writes its content into the specified model
     * object if validation is successful. This method performs validation on
     * both the field and the form level. The former validation ensures that all
     * fields contain syntactically correct data, i.e. the data they contain can
     * be converted to their expected data type (e.g. the string entered by the
     * user is indeed a valid date). The latter validation takes the form as the
     * whole into account. Here for instance relations between fields can be
     * checked (e.g. the date of delivery is greater than the shipment date).
     * The passed in model object is populated with the form's data when all
     * validation steps succeed. It must be compatible with the
     * {@link BindingStrategy} used by the form. It is modified only if
     * validation is successful; otherwise it is not changed.
     *
     * @param model the model object in which to write the form fields; can be
     *        <b>null</b>, then no data is copied
     * @return an object with validation results
     */
    public FormValidatorResults validate(Object model)
    {
        FormValidatorResults results = validateFields();
        if (results.isValid())
        {
            // read form fields into bean and perform form level validation
            results = DefaultFormValidatorResults.merge(results,
                    validateForm(model));
        }
        return results;
    }

    /**
     * Returns a set with the names of all defined fields.
     *
     * @return a set with the field names
     */
    public Set<String> getFieldNames()
    {
        return fields.getFieldHandlerNames();
    }

    /**
     * Returns the <code>FieldHandler</code> object for the field with the given
     * name. If no such field exists, <b>null </b> is returned.
     *
     * @param name the name of the desired field
     * @return the field handler for this field
     */
    public FieldHandler getField(String name)
    {
        return fields.findFieldHandler(name);
    }

    /**
     * Returns the display name for the specified field. This implementation
     * checks whether a display name is explicitly defined for the field handler
     * with the given name. If this is the case, it is returned. Otherwise the
     * field's name is returned. If the field is unknown, <b>null</b> is
     * returned.
     *
     * @param fldName the name of the field
     * @return the display name for this field
     */
    public String getDisplayName(String fldName)
    {
        FieldHandler fh = getField(fldName);
        if (fh == null)
        {
            return null;
        }
        return (fh.getDisplayName() != null) ? fh.getDisplayName() : fldName;
    }

    /**
     * Returns the component store of this form. In this object all components
     * that belong to this form are stored.
     *
     * @return the component store of this form
     */
    public ComponentStore getComponentStore()
    {
        return fields;
    }

    /**
     * Validates the fields of this form. This method ensures that all form
     * fields are syntactically and semantically correct, i.e. it performs
     * validation on both the fields and form level. After this method has been
     * called and returned a positive result, the form bean is available and
     * contains the current data.
     *
     * @return an object with results of the validation
     */
    public FormValidatorResults validateFields()
    {
        return validateFields(getFieldNames());
    }

    /**
     * Validates a sub set of the fields of this form. This method works like
     * the overloaded version, but only fields whose name is contained in the
     * passed in set are taken into account. This is useful if a partly
     * validation is to be performed. If the set contains an invalid field name,
     * a runtime exception will be thrown.
     *
     * @param names a set with the names of the fields to be validated
     * @return an object with results of the validation
     * @throws FormRuntimeException if an invalid field name is specified
     * @throws IllegalArgumentException if the set is <b>null</b>
     * @see #validateFields()
     */
    public FormValidatorResults validateFields(Set<String> names)
    {
        if (names == null)
        {
            throw new IllegalArgumentException("Sub set must not be null!");
        }

        Map<String, ValidationResult> results = new HashMap<String, ValidationResult>();
        performFieldValidation(ValidationPhase.SYNTAX, results, names);
        performFieldValidation(ValidationPhase.LOGIC, results, names);

        return new DefaultFormValidatorResults(results);
    }

    /**
     * Validates the whole form using the {@code FormValidator}. This is an
     * additional validation that can be performed after it was ensured that all
     * fields are syntactically and semantically correct. The aim of this method
     * is to apply high level validation rules that are able to check
     * dependencies between form fields. Calling this method requires that
     * validation of the field level has already been performed (e.g. by
     * {@link #validateFields()}). If validation is successful (or if no {@code
     * FormValidator} is defined), the passed in model object is populated with
     * the content of this form. Otherwise it is not modified.
     *
     * @param model the model object; can be <b>null</b>, then no data is copied
     * @return an object with the results of the validation
     */
    public FormValidatorResults validateForm(Object model)
    {
        FormValidatorResults results = fetchFormValidator().isValid(this);

        if (results.isValid())
        {
            // update the model object after a successful validation
            readFields(model);
        }

        return results;
    }

    /**
     * Reads the form's fields and copies their content into the passed in form
     * bean. Before this method can be called validation of the form's fields
     * must have been successful, i.e. {@link #validateFields()} must have been
     * invoked and returned a positive result. If {@link #validateFields()} has
     * not been called before, the passed in bean won't contain the current data
     * of the form's fields. The contents of the fields is converted to the
     * correct data types and written into the bean's properties.
     *
     * @param bean the bean in which to store the fields' content; can be
     *        <b>null</b>, then this operation has no effect
     * @throws FormRuntimeException if a field cannot be read
     */
    public void readFields(Object bean)
    {
        readFields(bean, getFieldNames());
    }

    /**
     * Reads a sub set of this form's fields and writes their content into the
     * specified bean. This method works like the overloaded variant, but
     * operates on a sub set of the fields only. If the passed in set contains
     * an invalid name, a runtime exception is thrown.
     *
     * @param bean the bean in which to store the fields' content; can be
     *        <b>null</b>, then this operation has no effect
     * @param names the set with the names of the fields to read
     * @throws FormRuntimeException if a field cannot be read
     * @throws IllegalArgumentException if the set is <b>null</b>
     */
    public void readFields(Object bean, Set<String> names)
    {
        if (bean == null)
        {
            return;
        }

        if (names == null)
        {
            throw new IllegalArgumentException("Sub set must not be null!");
        }

        for (String fldName : names)
        {
            FieldHandler fh = fetchField(fldName);
            try
            {
                writeModelProperty(bean, propertyName(fldName), fh.getData());
            }
            catch (Exception ex)
            {
                // Handle all reflection exceptions the same way
                throw new FormRuntimeException("Error when reading field "
                        + fldName, ex);
            }
        }
    }

    /**
     * Fetches the validator to be used for form validation. In contrast to
     * {@link #getFormValidator()} this method never returns <b>null</b>. If no
     * {@link FormValidator} has been set, a dummy implementation is returned.
     *
     * @return the {@code FormValidator} to be used
     */
    FormValidator fetchFormValidator()
    {
        return (getFormValidator() != null) ? getFormValidator()
                : DEF_FORM_VALIDATOR;
    }

    /**
     * Obtains the current {@code BindingStrategy}. (This method mainly exists
     * for testing purposes.)
     *
     * @return the {@code BindingStrategy}
     */
    BindingStrategy fetchBindingStrategy()
    {
        return bindingStrategy;
    }

    /**
     * Reads a property from the given model object. This method is called by
     * {@code initFields()}. It delegates to the {@code BindingStrategy} to read
     * the data.
     *
     * @param model the model object
     * @param propertyName the name of the property to read
     * @return the value of this property
     */
    Object readModelProperty(Object model, String propertyName)
    {
        return fetchBindingStrategy().readProperty(model, propertyName);
    }

    /**
     * Writes a property of the given model object. This method is called by
     * {@code readFields()}. It delegates to the {@code BindingStrategy} to
     * write the data.
     *
     * @param model the model object
     * @param propertyName the name of the property to write
     * @param value the value to be written
     */
    void writeModelProperty(Object model, String propertyName, Object value)
    {
        fetchBindingStrategy().writeProperty(model, propertyName, value);
    }

    /**
     * Helper method for performing validation on the form's fields. This method
     * checks only those fields that either have not been checked ore are valid.
     *
     * @param phase the validation phase
     * @param validationResults the validation results to be filled
     * @param names a set with the names of the affected fields
     */
    private void performFieldValidation(ValidationPhase phase,
            Map<String, ValidationResult> validationResults, Set<String> names)
    {
        for (String fldName : names)
        {
            if (getField(fldName) == null)
            {
                throw new FormRuntimeException("Invalid field name: " + fldName);
            }

            ValidationResult vres = validationResults.get(fldName);
            if (vres == null || vres.isValid())
            {
                validationResults.put(fldName, DefaultValidationResult.merge(
                        vres, getField(fldName).validate(phase)));
            }
        }
    }

    /**
     * Returns the name of the property for the specified field. If the
     * corresponding field handler defines a property name, this name is
     * returned. Otherwise the name of the field itself is used.
     *
     * @param field the name of the field
     * @return the corresponding property name
     * @throws FormRuntimeException if the field is unknown
     */
    private String propertyName(String field)
    {
        FieldHandler fh = fetchField(field);
        return (fh.getPropertyName() != null) ? fh.getPropertyName() : field;
    }

    /**
     * Fetches the field handler for the specified field. If the field is
     * unknown, an exception is thrown.
     *
     * @param field the name of the desired field
     * @return the handler for this field
     * @throws FormRuntimeException if the field is unknown
     */
    private FieldHandler fetchField(String field)
    {
        FieldHandler fh = getField(field);
        if (fh == null)
        {
            throw new FormRuntimeException("Cannot resolve field: " + field);
        }

        return fh;
    }
}
