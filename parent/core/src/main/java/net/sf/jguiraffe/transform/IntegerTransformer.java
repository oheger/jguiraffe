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
 * type <code>java.lang.Integer</code>.
 * </p>
 * <p>
 * This class implements the abstract methods defined by its super classes in a
 * way suitable for <code>java.lang.Integer</code> objects. It does not define
 * any new properties or error messages. Refer to the documentation of
 * <code>{@link NumberTransformerBase}</code> for a list of all supported
 * properties and possible error messages.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: IntegerTransformer.java 205 2012-01-29 18:29:57Z oheger $
 */
public class IntegerTransformer extends AbstractIntegerTransformer<Integer>
{
    /**
     * Converts the given number to an <code>java.lang.Integer</code>. We
     * also check whether the number fits into the value range supported by
     * integers.
     *
     * @param n the number to convert
     * @return the converted number
     * @throws IllegalArgumentException if the number cannot be converted
     */
    @Override
    protected Integer convert(Number n)
    {
        if (n.longValue() < Integer.MIN_VALUE
                || n.longValue() > Integer.MAX_VALUE)
        {
            throw new IllegalArgumentException(
                    "Value loss when converting to integer: " + n);
        }

        return Integer.valueOf(n.intValue());
    }

    /**
     * Fetches a configuration property of type integer.
     *
     * @param config the configuration
     * @param property the property to fetch
     * @param defaultValue the default value
     * @return the property from the configuration
     */
    @Override
    protected Integer fetchProperty(Configuration config, String property,
            Integer defaultValue)
    {
        return config.getInteger(property, defaultValue);
    }
}
