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
package net.sf.jguiraffe.gui.builder.enablers;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

/**
 * <p>
 * A specialized converter implementation for {@link ElementEnabler} objects.
 * </p>
 * <p>
 * This converter class is a thin wrapper around the {@link EnablerBuilder}
 * class. In its {@link #convert(Class, Object)} method it expects a string
 * representation of an {@link ElementEnabler} which is compatible with the
 * specifications understood by the builder. It passes this string to the
 * builder and uses it to create the enabler. The resulting
 * {@link ElementEnabler} is returned.
 * </p>
 * <p>
 * Implementation note: This class is stateless; therefore an instance can be
 * shared between multiple components and invoked concurrently.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: EnablerConverter.java 205 2012-01-29 18:29:57Z oheger $
 */
public class EnablerConverter implements Converter
{
    /**
     * Performs the type conversion. This implementation uses
     * {@link EnablerBuilder} to create an {@link ElementEnabler} implementation
     * from the string representation of the passed in object. For <b>null</b>
     * values, or if the conversion fails, a {@code ConversionException} is
     * thrown.
     *
     * @param type the target class of the conversion
     * @param value the object to be converted
     * @return the converted object
     * @throws ConversionException if conversion is not possible
     */
    public Object convert(@SuppressWarnings("rawtypes") Class type, Object value)
    {
        if (value == null)
        {
            throw new ConversionException(
                    "Conversion of null value is not allowed!");
        }

        try
        {
            return createEnablerBuilder().addSpecification(
                    String.valueOf(value)).build();
        }
        catch (IllegalArgumentException istex)
        {
            throw new ConversionException(
                    "Failed to convert enabler specification: " + value, istex);
        }
    }

    /**
     * Creates a new {@link EnablerBuilder} object. This method is called by the
     * main conversion method. The builder returned here is used for the
     * conversion.
     *
     * @return the new {@link EnablerBuilder} for the conversion
     */
    EnablerBuilder createEnablerBuilder()
    {
        return new EnablerBuilder();
    }
}
