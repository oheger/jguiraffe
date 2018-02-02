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
package net.sf.jguiraffe.di;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

/**
 * <p>
 * A specialized {@code Converter} implementation dealing with all types of
 * enumeration classes.
 * </p>
 * <p>
 * The implementation of the {@link #convert(Class, Object)} method expects that
 * the passed in value is a constant as defined for an enumeration class as
 * string. It tries to find the corresponding instance of the enumeration class.
 * </p>
 * <p>
 * An instance of this class is registered per default as base class converter
 * at any new {@link InvocationHelper} instance. Therefore conversions to
 * parameters or properties of {@code Enum} types are supported out of the box.
 * </p>
 * <p>
 * Implementation note: This class has no state. So a single instance can be
 * shared between multiple instances. This instance can be obtained using the
 * {@link #getInstance()} factory method; creating new instances is now allowed.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: EnumConverter.java 205 2012-01-29 18:29:57Z oheger $
 */
public final class EnumConverter implements Converter
{
    /** Constant for the single shared instance of this class. */
    private static final EnumConverter INSTANCE = new EnumConverter();

    /**
     * Private constructor so that no new instances can be created.
     */
    private EnumConverter()
    {
    }

    /**
     * Returns an instance of {@code EnumConverter}. Use this factory method to
     * obtain a new instance rather than invoking a constructor.
     *
     * @return the {@code EnumConverter} instance
     */
    public static EnumConverter getInstance()
    {
        return INSTANCE;
    }

    /**
     * Performs the type conversion to an enumeration constant. If this is not
     * possible, a {@code ConversionException} is thrown. There are multiple
     * reasons why this could happen, e.g. the value is not an exact string
     * representation of an enumeration constant, or the class might be no
     * {@code Enum} class.
     *
     * @param type the class to convert to
     * @param value the object to be converted
     * @return the result of the conversion
     */
    public Object convert(@SuppressWarnings("rawtypes") Class type, Object value)
    {
        assert type != null : "No target class!";
        if (value == null)
        {
            throw new ConversionException(
                    "Cannot convert null enumeration constant!");
        }

        try
        {
            // valueOf() will throw an IllegalArgumentException if the class is
            // not an Enum class.
            @SuppressWarnings("unchecked")
            Object result = Enum.valueOf(type, String.valueOf(value));
            return result;
        }
        catch (IllegalArgumentException iex)
        {
            throw new ConversionException("Cannot convert enum constant "
                    + value + " to class " + type, iex);
        }
    }
}
