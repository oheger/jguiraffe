/*
 * Copyright 2006-2015 The JGUIraffe Team.
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
 * type <code>java.lang.Long</code>.
 * </p>
 * <p>
 * This class implements the abstract methods defined by its super classes in a
 * way suitable for <code>java.lang.Long</code> objects. It does not define
 * any new properties or error messages. Refer to the documentation of
 * <code>{@link NumberTransformerBase}</code> for a list of all supported
 * properties and possible error messages.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: LongTransformer.java 205 2012-01-29 18:29:57Z oheger $
 */
public class LongTransformer extends AbstractIntegerTransformer<Long>
{
    /**
     * Converts the given number into a <code>Long</code>. This
     * implementation also checks whether the number fits into the value range
     * of <code>Long</code> and throws an
     * <code>IllegalArgumentException</code> if not.
     *
     * @param n the number to convert
     * @return the converted number
     * @throws IllegalArgumentException if the number cannot be converted to a
     *         <code>Long</code>
     */
    @Override
    protected Long convert(Number n)
    {
        if (n.doubleValue() < Long.MIN_VALUE
                || n.doubleValue() > Long.MAX_VALUE)
        {
            throw new IllegalArgumentException(
                    "Number does not fit into the value range of Long: " + n);
        }

        return n.longValue();
    }

    /**
     * Fetches a long property from the specified configuration.
     *
     * @param config the configuration
     * @param property the property
     * @param defaultValue the default value
     * @return the value of this property
     */
    @Override
    protected Long fetchProperty(Configuration config, String property,
            Long defaultValue)
    {
        return config.getLong(property, defaultValue);
    }
}
