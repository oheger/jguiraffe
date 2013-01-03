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
package net.sf.jguiraffe.transform;

import java.text.DateFormat;
import java.util.Locale;

import org.apache.commons.configuration.Configuration;

/**
 * <p>
 * A specialized transformer that transforms strings into date objects with both
 * a date and time component.
 * </p>
 * <p>
 * This transformer class allows user to enter a date and a time in a single
 * input field, such as <code>1/31/2008 10:19</code>. Most of the required
 * functionality is already implemented by the base class. This class creates an
 * appropriate <code>DateFormat</code> object that supports parsing time
 * stamps.
 * </p>
 * <p>
 * For the documentation of the supported error messages refer to the super
 * class. In addition to the properties defined by
 * <code>DateTransformerBase</code>, the following properties can be set:
 * <table border="1">
 * <tr>
 * <th>Property</th>
 * <th>Description</th>
 * <th>Default</th>
 * </tr>
 * <tr>
 * <td valign="top">timeStyle</td>
 * <td>Defines the style of the time component. This can be one of the style
 * constants declared by the <code>java.text.DateFormat</code> class like
 * <code>SHORT</code> or <code>FULL</code>. While the style for the date
 * component is set by the inherited <code>style</code> property, with this
 * property the style for the time component can be set separately.</td>
 * <td valign="top">SHORT</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: DateTimeTransformer.java 205 2012-01-29 18:29:57Z oheger $
 */
public class DateTimeTransformer extends DateTransformerBase
{
    /** Constant for the time style property. */
    protected static final String PROP_TIME_STYLE = "timeStyle";

    /** Stores the formatting style to be used for the time portion. */
    private int timeStyle;

    /**
     * Creates a new instance of <code>DateTimeTransformer</code>.
     */
    public DateTimeTransformer()
    {
        setTimeStyle(DateFormat.SHORT);
    }

    /**
     * Returns the style for the time portion.
     *
     * @return the style for the time
     */
    public int getTimeStyle()
    {
        return timeStyle;
    }

    /**
     * Sets the style for the time. The styles for the date portion and the time
     * portion are separately set. Here one of the constants supported by
     * <code>java.text.DateFormat</code> (e.g. <code>SHORT</code>,
     * <code>MEDIUM</code> etc.) can be passed in.
     *
     * @param timeStyle the style for the time
     */
    public void setTimeStyle(int timeStyle)
    {
        this.timeStyle = timeStyle;
    }

    /**
     * Creates the format object to be used by this transformer. This
     * implementation returns a date/time instance of <code>DateFormat</code>.
     *
     * @param locale the locale
     * @param style the style for the date portion
     * @param config the configuration with the properties
     * @return the format object
     */
    @Override
    protected DateFormat createFormat(Locale locale, int style,
            Configuration config)
    {
        return DateFormat.getDateTimeInstance(style, config.getInt(
                PROP_TIME_STYLE, getTimeStyle()), locale);
    }
}
