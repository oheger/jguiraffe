/*
 * Copyright 2006-2012 The JGUIraffe Team.
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
 * Definition of an interface for objects that are responsible of the binding of
 * form fields to model objects.
 * </p>
 * <p>
 * The main purpose of a form is to gather user input. This input must be stored
 * somewhere so that it can be accessed by the application in an appropriate and
 * convenient way. Applications typically use different ways of storing (and
 * further processing of) user input. An obvious way is to create specialized
 * Java bean classes whose properties correspond to the fields provided by the
 * form. That way the data entered into the form can be directly transfered into
 * Java objects. However, this is only one example, and applications may have
 * completely different requirements.
 * </p>
 * <p>
 * The purpose of this interface is to serve as an abstraction between form
 * objects and specific data models used by applications. When a form is asked
 * to read the data entered by the user or to populate its fields from the
 * application's model it delegates to a {@code BindingStrategy}. So it can be
 * independent on the concrete data model (technology) used by the application.
 * </p>
 * <p>
 * This interface defines low-level methods for reading and writing properties
 * from and to the data model. These are called by the {@link Form} class when
 * access to the model is needed. All methods defined by this interface
 * do not throw checked exceptions (depending on the concrete API or technology
 * a concrete implementation interacts with there will be different types of
 * exceptions). If something goes wrong, an implementation should throw a
 * {@link FormRuntimeException}.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: BindingStrategy.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface BindingStrategy
{
    /**
     * Reads the value of the specified property from the given model object.
     * This method is called when the form is initialized and its fields must be
     * populated from the underlying data model. An implementation has to obtain
     * the value of the specified property from the passed in data object. The
     * value will then be passed to the {@link FieldHandler} of the
     * corresponding form field.
     *
     * @param model the model object
     * @param propertyName the name of the property in question
     * @return the current value of this property
     */
    Object readProperty(Object model, String propertyName);

    /**
     * Writes the specified value of a property of the given model object. This
     * method is called when the user input is evaluated, i.e. the data the user
     * has entered into the fields needs to be saved in the underlying model. An
     * implementation has to ensure that the passed in value is correctly
     * written into the model data object.
     *
     * @param model the model object
     * @param propertyName the name of the property in question
     * @param value the value to be written into this property
     */
    void writeProperty(Object model, String propertyName, Object value);
}
