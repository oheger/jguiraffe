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
package net.sf.jguiraffe.transform;

import org.apache.commons.configuration.Configuration;

/**
 * <p>
 * A specialized number transformer implementation that deals with numbers of
 * type <code>java.lang.Float</code>.
 * </p>
 * <p>
 * This class implements the abstract methods defined by its super classes in a
 * way suitable for <code>java.lang.Float</code> objects. It does not define
 * any new properties or error messages. Refer to the documentation of
 * <code>{@link NumberTransformerBase}</code> for a list of all supported
 * properties and possible error messages.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FloatTransformer.java 205 2012-01-29 18:29:57Z oheger $
 */
public class FloatTransformer extends AbstractDecimalTransformer<Float>
{
    /**
     * Converts the specified number into a <code>Float</code>. If the number
     * exceeds the value range of a float, an exception is thrown.
     *
     * @param n the number to convert
     * @return the converted number
     * @throws IllegalArgumentException if the number cannot be converted
     */
    @Override
    protected Float convert(Number n)
    {
        if (n.doubleValue() < Float.MIN_VALUE
                || n.doubleValue() > Float.MAX_VALUE)
        {
            throw new IllegalArgumentException(
                    "Number does not fit into the value range of Float: " + n);
        }
        return n.floatValue();
    }

    /**
     * Fetches a float property from the specified configuration.
     *
     * @param config the configuration
     * @param property the property
     * @param defaultValue the default value
     * @return the value of this property
     */
    @Override
    protected Float fetchProperty(Configuration config, String property,
            Float defaultValue)
    {
        return config.getFloat(property, defaultValue);
    }
}
