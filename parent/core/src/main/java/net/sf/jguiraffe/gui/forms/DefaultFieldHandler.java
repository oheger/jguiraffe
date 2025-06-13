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

import net.sf.jguiraffe.transform.ValidationResult;

/**
 * <p>
 * A default implementation of the <code>FieldHandler</code> interface.
 * </p>
 * <p>
 * This class implements all necessary functionality to deal with form fields of
 * different types. It supports both validation and conversion of data.
 * Conversion works in two directions: from a bean property to a representation
 * that can be processed by a GUI widget and vice versa. Validation (on the
 * field and form level) ensures that data entered by the user is syntactically
 * and semantically valid.
 * </p>
 * <p>
 * To perform its tasks this class makes use of some
 * {@link net.sf.jguiraffe.transform.Validator Validator} and
 * {@link net.sf.jguiraffe.transform.Transformer Transformer}
 * objects that can be associated with an instance. It ensures that the services
 * provided by these objects are accessed when necessary. When creating an
 * instance of this class it must be ensured that the correct validators and
 * transformers are set; especially the data types they operate on must be
 * compatible.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: DefaultFieldHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class DefaultFieldHandler implements FieldHandler
{
    /** Stores the validator for validation on the syntax level. */
    private ValidatorWrapper syntaxValidator;

    /** Stores the validator for validation on the logic level. */
    private ValidatorWrapper logicValidator;

    /**
     * Stores the transformer to transform from the widget data to the target
     * data type.
     */
    private TransformerWrapper readTransformer;

    /**
     * Stores the transformer to transform from the target data type to the
     * widget data.
     */
    private TransformerWrapper writeTransformer;

    /** Stores the handler for the associated GUI component. */
    private ComponentHandler<?> componentHandler;

    /** Stores the data type of this field. */
    private Class<?> type;

    /** Stores the property name. */
    private String propertyName;

    /** Stores the display name. */
    private String displayName;

    /** Stores the latest validated data. */
    private Object fieldData;

    /**
     * Creates a new instance of <code>DefaultFieldHandler</code>.
     */
    public DefaultFieldHandler()
    {
        syntaxValidator = DummyWrapper.INSTANCE;
        logicValidator = DummyWrapper.INSTANCE;
        readTransformer = DummyWrapper.INSTANCE;
        writeTransformer = DummyWrapper.INSTANCE;
    }

    /**
     * Returns the validator used for validation on the syntax level. If no
     * specific validator is set, a dummy validator is used.
     *
     * @return the validator on the field level
     */
    public ValidatorWrapper getSyntaxValidator()
    {
        return syntaxValidator;
    }

    /**
     * Sets the validator used for validation on the syntax level.
     *
     * @param fieldValidator the field level validator
     */
    public void setSyntaxValidator(ValidatorWrapper fieldValidator)
    {
        this.syntaxValidator = (fieldValidator != null) ? fieldValidator
                : DummyWrapper.INSTANCE;
    }

    /**
     * Returns the validator used for validation on the logic level. If no
     * specific validator has been set, a dummy validator is used.
     *
     * @return the validator on the form level
     */
    public ValidatorWrapper getLogicValidator()
    {
        return logicValidator;
    }

    /**
     * Sets the validator used for validation on the logic level.
     *
     * @param formValidator the form level validator
     */
    public void setLogicValidator(ValidatorWrapper formValidator)
    {
        this.logicValidator = (formValidator != null) ? formValidator
                : DummyWrapper.INSTANCE;
    }

    /**
     * Returns the read transformer. If no specific transformer is set, a dummy
     * transformer is used.
     *
     * @return the read transformer
     */
    public TransformerWrapper getReadTransformer()
    {
        return readTransformer;
    }

    /**
     * Sets the read transformer. This transformer is used when reading data
     * from the associated GUI widget. It transforms from the widget's data type
     * to the target data type (provided that field level validation has been
     * successful).
     *
     * @param readTransformer the read transformer
     */
    public void setReadTransformer(TransformerWrapper readTransformer)
    {
        this.readTransformer = (readTransformer != null) ? readTransformer
                : DummyWrapper.INSTANCE;
    }

    /**
     * Returns the write transformer. If no specific write transformer has been
     * set, a dummy transformer is used.
     *
     * @return the write transformer
     */
    public TransformerWrapper getWriteTransformer()
    {
        return writeTransformer;
    }

    /**
     * Sets the write transformer. This transformer is used when initializing
     * the associated widget's data with form data. It transforms from the
     * field's target data type to the widget's data type.
     *
     * @param writeTransformer the write transformer
     */
    public void setWriteTransformer(TransformerWrapper writeTransformer)
    {
        this.writeTransformer = (writeTransformer != null) ? writeTransformer
                : DummyWrapper.INSTANCE;
    }

    /**
     * Returns the component handler.
     *
     * @return the component handler
     */
    public ComponentHandler<?> getComponentHandler()
    {
        return componentHandler;
    }

    /**
     * Sets the component handler.
     *
     * @param componentHandler the component handler
     */
    public void setComponentHandler(ComponentHandler<?> componentHandler)
    {
        this.componentHandler = componentHandler;
    }

    /**
     * Returns the data type of this field. If a type was explicitely set, this
     * type is returned. Otherwise the <code>ComponentHandler</code>'s type
     * is returned.
     *
     * @return the type of this field
     */
    public Class<?> getType()
    {
        if (type != null)
        {
            return type;
        }
        else
        {
            return (getComponentHandler() != null) ? getComponentHandler()
                    .getType() : null;
        }
    }

    /**
     * Sets the data type for this form field.
     *
     * @param type the data type
     */
    public void setType(Class<?> type)
    {
        this.type = type;
    }

    /**
     * Returns the property name of this field if one is set.
     *
     * @return the property name
     */
    public String getPropertyName()
    {
        return propertyName;
    }

    /**
     * Sets the property name of this field.
     *
     * @param n the property name
     */
    public void setPropertyName(String n)
    {
        propertyName = n;
    }

    /**
     * Returns the display name.
     *
     * @return the display name (can be <b>null</b>)
     */
    public String getDisplayName()
    {
        return displayName;
    }

    /**
     * Sets the display name.
     *
     * @param displayName the new display name
     */
    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    /**
     * Performs validation for the specified phase and returns the results.
     *
     * @param phase the validation phase
     * @return the validation result
     */
    public ValidationResult validate(ValidationPhase phase)
    {
        return (ValidationPhase.SYNTAX.equals(phase)) ? validateFieldLevel()
                : validateFormLevel();
    }

    /**
     * Fetches the data of this field. This method requires that validation of
     * both the field and form level succeeded (it is guaranteed that the
     * <code>{@link #validate(ValidationPhase)}</code> method was called for
     * these phases before the form framework invokes this method).
     *
     * @return the data of this field (converted to the resulting type)
     */
    public Object getData()
    {
        return fieldData;
    }

    /**
     * Sets the data for this field. Converts the passed in object to the
     * component handler's data type using the write transformer.
     *
     * @param data the data object for this field
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    // ClassCastException is handled explicitly
    public void setData(Object data)
    {
        ComponentHandler ch = fetchHandler();
        try
        {
            ch.setData(getWriteTransformer().transform(data));
        }
        catch (ClassCastException ccex)
        {
            throw new FormRuntimeException(
                    "Exception when setting data for field handler "
                            + "(displayName = "
                            + displayName
                            + ")! Probably an appropriate write transformer has to be set.",
                    ccex);
        }
    }

    /**
     * Performs validation on the field level.
     *
     * @return validation results
     */
    protected ValidationResult validateFieldLevel()
    {
        return getSyntaxValidator().isValid(fetchHandler().getData());
    }

    /**
     * Performs validation on the form level. This implementation transforms the
     * GUI component's data into its target data type (assuming that field level
     * validation was successful). Then the correctness is checked. If this
     * validation is successful, too, the data object is stored internally so it
     * can be accessed by the <code>getData()</code> method.
     *
     * @return validation results
     */
    protected ValidationResult validateFormLevel()
    {
        fieldData = null;
        Object data = fetchFieldData();
        ValidationResult result = validateFieldData(data);
        if (result.isValid())
        {
            fieldData = data;
        }
        return result;
    }

    /**
     * Fetches the form field's data directly from the component handler. This
     * method is called whenever access to the field data is needed, especially
     * by the <code>validate()</code> method. It assumes that validation on
     * the field level was successful.
     *
     * @return the field's data
     */
    protected Object fetchFieldData()
    {
        return getReadTransformer().transform(fetchHandler().getData());
    }

    /**
     * Performs a form level validation of this field. Checks if the passed in
     * data object is semantically correct.
     *
     * @param data the data to check
     * @return a validation result object with the results of the form level
     * validation
     */
    protected ValidationResult validateFieldData(Object data)
    {
        return getLogicValidator().isValid(data);
    }

    /**
     * Helper method for fetching the component handler. Throws an
     * <code>IllegalStateException</code> exception if none exists.
     *
     * @return the component handler
     */
    private ComponentHandler<?> fetchHandler()
    {
        ComponentHandler<?> result = getComponentHandler();
        if (result == null)
        {
            throw new IllegalStateException(
                    "ComponentHandler has not been set!");
        }
        return result;
    }
}
