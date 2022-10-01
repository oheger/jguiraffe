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
package net.sf.jguiraffe.transform;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * <p>
 * A base class for number transformers that operate on integer numbers.
 * </p>
 * <p>
 * This base class already implements the creation of the
 * <code>NumberFormat</code> object used for parsing the entered numbers.
 * Concrete sub classes specialize on specific integer types like
 * <code>java.lang.Integer</code> or <code>java.lang.Long</code>.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: AbstractIntegerTransformer.java 205 2012-01-29 18:29:57Z oheger $
 * @param <T> the concrete type of integer numbers this transformer deals with
 */
public abstract class AbstractIntegerTransformer<T extends Number> extends
        NumberTransformerBase<T>
{
    /**
     * Creates the format object for parsing user input. This implementation
     * returns a format for parsing integers.
     *
     * @param locale the locale
     * @return the format object
     */
    @Override
    protected NumberFormat createFormat(Locale locale)
    {
        return NumberFormat.getIntegerInstance(locale);
    }
}
