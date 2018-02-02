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
package net.sf.jguiraffe.gui.builder.components.tags.table;

/**
 * <p>
 * An enumeration class that defines logic column classes.
 * </p>
 * <p>
 * The column of a table can contain data of different types, e.g. strings,
 * numbers, dates, or icons. Depending on the data type a different renderer has
 * to be used to ensure that the data is correctly displayed.
 * </p>
 * <p>
 * Different UI toolkits may use different mechanisms to associate a table
 * column with a specific renderer. The <em>JGUIraffe</em> library defines an
 * enumeration of logic column classes for the most frequently used data types.
 * The constants defined in this enumeration class can be passed to the {@code
 * columnClass} attribute of the {@link TableColumnTag} tag. If the standard
 * column classes defined here are not sufficient, it is also possible to set a
 * fully qualified Java class name.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ColumnClass.java 205 2012-01-29 18:29:57Z oheger $
 * @see TableColumnTag
 * @see TableColumnTag#setColumnClass(Object)
 */
public enum ColumnClass
{
    /**
     * The column class <em>String</em>. This is appropriate for plain texts to
     * be displayed. If the content of a cell is an arbitrary Java object, its
     * string representation is displayed.
     */
    STRING,

    /**
     * The column class <em>Number</em>. This class can be used for integer
     * numbers.
     */
    NUMBER,

    /**
     * The column class <em>Float</em>. This class can be used for floating
     * point numbers (with either single or double precision).
     */
    FLOAT,

    /**
     * The column class <em>Date</em>. Use this class for date values.
     */
    DATE,

    /**
     * The column class <em>Boolean</em>. This is the class of choice for
     * boolean data. Depending on the capabilities of the UI toolkit used, data
     * of this type may be rendered in a special way, e.g. as a checkbox.
     */
    BOOLEAN,

    /**
     * The column class <em>Icon</em>. If this class is set, a renderer for
     * displaying icons is used.
     */
    ICON
}
