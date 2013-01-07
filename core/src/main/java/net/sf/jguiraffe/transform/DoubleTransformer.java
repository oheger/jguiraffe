/*
 * Copyright 2006-2013 The JGUIraffe Team.
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

import java.math.BigDecimal;

import org.apache.commons.configuration.Configuration;

/**
 * <p>
 * A specialized number transformer implementation that deals with numbers of
 * type <code>java.lang.Double</code>.
 * </p>
 * <p>
 * This class implements the abstract methods defined by its super classes in a
 * way suitable for <code>java.lang.Double</code> objects. It does not define
 * any new properties or error messages. Refer to the documentation of
 * <code>{@link NumberTransformerBase}</code> for a list of all supported
 * properties and possible error messages.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: DoubleTransformer.java 205 2012-01-29 18:29:57Z oheger $
 */
public final class DoubleTransformer extends AbstractDecimalTransformer<Double>
{
    /**
     * Converts the specified number into a <code>java.lang.Double</code>. A
     * range check will be performed.
     *
     * @param n the number to be converted
     * @return the converted number
     * @throws IllegalArgumentException if the number cannot be converted into a
     *         <code>Double</code>
     */
    @Override
    protected Double convert(Number n)
    {
        if (compare(n, Double.MIN_VALUE) < 0
                || compare(n, Double.MAX_VALUE) > 0)
        {
            throw new IllegalArgumentException(
                    "Number does not fit into the value range of Dobule: " + n);
        }
        return n.doubleValue();
    }

    /**
     * Returns a configuration property of type <code>Double</code>.
     *
     * @param config the configuration
     * @param property the property
     * @param defaultValue the default value
     * @return the value of the property
     */
    @Override
    protected Double fetchProperty(Configuration config, String property,
            Double defaultValue)
    {
        return config.getDouble(property, defaultValue);
    }

    /**
     * Compares the specified number with the given double value.
     *
     * @param n the number
     * @param val the double value
     * @return the result of the comparison (indicates, which number is bigger)
     */
    private int compare(Number n, double val)
    {
        BigDecimal bd;
        if (n instanceof BigDecimal)
        {
            bd = (BigDecimal) n;
        }
        else
        {
            bd = new BigDecimal(n.toString());
        }

        return bd.compareTo(BigDecimal.valueOf(val));
    }
}
