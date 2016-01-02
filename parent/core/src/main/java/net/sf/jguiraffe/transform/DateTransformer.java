/*
 * Copyright 2006-2016 The JGUIraffe Team.
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

import java.text.DateFormat;
import java.util.Locale;

import org.apache.commons.configuration.Configuration;

/**
 * <p>
 * A specialized transformer that transforms strings into date objects.
 * </p>
 * <p>
 * Most of the required functionality is already implemented by the base class.
 * This class only creates an appropriate <code>DateFormat</code> object that
 * supports parsing dates.
 * </p>
 * <p>
 * For the documentation of the supported properties and error messages refer to
 * the super class.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: DateTransformer.java 205 2012-01-29 18:29:57Z oheger $
 */
public class DateTransformer extends DateTransformerBase
{
    /**
     * Returns the <code>DateFormat</code> to be used by this transformer.
     * This implementation returns a format object for processing dates.
     *
     * @param locale the locale
     * @param style the style to be used
     * @param config the configuration with the properties
     * @return the format object to be used
     */
    @Override
    protected DateFormat createFormat(Locale locale, int style,
            Configuration config)
    {
        return DateFormat.getDateInstance(style, locale);
    }
}
