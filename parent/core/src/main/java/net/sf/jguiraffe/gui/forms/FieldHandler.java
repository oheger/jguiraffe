/*
 * Copyright 2006-2021 The JGUIraffe Team.
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
 * Definition of an interface for accessing fields of a form.
 * </p>
 * <p>
 * A form consists of an arbitrary number of fields, i.e. interaction elements
 * where the user can enter data. Such a field must be able to interact with the
 * GUI widget used for data entering. In addition it supports validation of the
 * entered data and conversion into a target data type.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FieldHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface FieldHandler
{
    /**
     * Returns the <code>ComponentHandler</code> used by this
     * <code>FieldHandler</code>. With this handler, the underlying GUI
     * component can be controlled.
     *
     * @return the associated <code>ComponentHandler</code>
     */
    ComponentHandler<?> getComponentHandler();

    /**
     * Returns the data of this field. This is not necessary the same data
     * object as returned by the <code>ComponentHandler</code>. Instead it
     * can be the result of a conversion process to transform the data into a
     * certain target data type. For this conversion to succeed it must be
     * ensured that this method is only envoked after validation at the field
     * level has been successfull. The object returned here should be of the
     * same type as returned by <code>{@link #getType()}</code>.
     *
     * @return the data of this field
     */
    Object getData();

    /**
     * Sets the data of this field. An implementation must perform suitable
     * conversions and then write the data into the associated GUI component.
     * This is the opposite of <code>{@link #getData()}</code>.
     *
     * @param data the data to set
     */
    void setData(Object data);

    /**
     * Validates this field either at the field or the form level. The returned
     * <code>ValidationResult</code> object contains information if the
     * field's content is valid or which errors have been found.
     *
     * @param phase determines the validation phase
     * @return an object with the validation results
     */
    ValidationResult validate(ValidationPhase phase);

    /**
     * Returns the data type for this field. Valid user input will be converted
     * into this data type.
     *
     * @return the data type of this field
     */
    Class<?> getType();

    /**
     * Returns the name of the corresponding property in the form bean. This
     * method can return <b>null</b>, which means that the bean property has
     * the same name as this field. But by specifying a different name it is
     * possible to map to more complex bean properties, e.g. mapped or indexed
     * ones.
     *
     * @return the name of the corresponding bean property
     */
    String getPropertyName();

    /**
     * Returns the display name of this field. This should be a plain name in
     * the language of the current user. The method can return <b>null</b> if
     * no display name has been set. In this case the form will use the field
     * name also as display name.
     *
     * @return a display name for this field
     */
    String getDisplayName();
}
