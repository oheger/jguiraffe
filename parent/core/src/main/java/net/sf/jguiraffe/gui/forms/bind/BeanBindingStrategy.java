/*
 * Copyright 2006-2022 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.forms.bind;

import net.sf.jguiraffe.gui.forms.BindingStrategy;
import net.sf.jguiraffe.gui.forms.FormRuntimeException;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * <p>
 * An implementation of the {@code BindingStrategy} interface that operates on
 * Java beans.
 * </p>
 * <p>
 * This implementation uses <a
 * href="http://commons.apache.org/beanutils">Commons Beanutils</a> for reading
 * and writing properties of Java bean components. The names of form fields are
 * mapped to corresponding names of bean properties. That way data exchange can
 * be performed in both directions.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: BeanBindingStrategy.java 205 2012-01-29 18:29:57Z oheger $
 */
public class BeanBindingStrategy implements BindingStrategy
{
    /**
     * Reads a property from a model object. All exceptions related to
     * reflection are re-thrown as {@link FormRuntimeException}.
     *
     * @param model the model object
     * @param propertyName the name of the property to read
     * @return the value of this property
     * @throws FormRuntimeException if an exception occurs
     */
    public Object readProperty(Object model, String propertyName)
    {
        try
        {
            return PropertyUtils.getProperty(model, propertyName);
        }
        catch (Exception ex)
        {
            // handle all reflection-related exceptions the same way
            throw new FormRuntimeException("Error when reading property "
                    + propertyName, ex);
        }
    }

    /**
     * Writes a property to a model object. All exceptions related to reflection
     * are re-thrown as {@link FormRuntimeException}.
     *
     * @param model the model object
     * @param propertyName the name of the property to read
     * @param value the new value of this property
     * @throws FormRuntimeException if an exception occurs
     */
    public void writeProperty(Object model, String propertyName, Object value)
    {
        try
        {
            PropertyUtils.setProperty(model, propertyName, value);
        }
        catch (Exception ex)
        {
            // handle all reflection-related exceptions the same way
            throw new FormRuntimeException("Error when reading property "
                    + propertyName, ex);
        }
    }
}
