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
package net.sf.jguiraffe.gui.builder.components.tags;

import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.layout.NumberWithUnit;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A specific component tag that constructs a text area component.
 * </p>
 * <p>
 * A text area is closely related to a text field. In addition it can display
 * multiple lines of text and has some additional properties. The following
 * attributes are supported by this tag:
 * </p>
 * <p>
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">columns</td>
 * <td>Defines the number of columns the text field should have. This value acts
 * as a hint for the width of this component.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">rows</td>
 * <td>Defines the number of rows the text field should have. This value acts as
 * a hint for the height of this component.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">maxlength</td>
 * <td>Allows to define a maximum length for text the user can type into this
 * text field.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valing="top">wrap</td>
 * <td>This boolean attribute controls whether the text area should support word
 * wrapping.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valing="top">scrollWidth</td>
 * <td>Here the preferred width of the scroll pane enclosing the text area can
 * be specified as a number with unit (e.g. &quot;1.5cm&quot;). If specified,
 * the scroll pane will have exactly this preferred width. Otherwise, the width
 * is determined by the preferred width of the text area. In this case it is
 * recommended to set the {@code columns} attribute.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valing="top">scrollHeight</td>
 * <td>Here the preferred height of the scroll pane enclosing the text area can
 * be specified as a number with unit (e.g. &quot;10dlu&quot;). If specified,
 * the scroll pane will have exactly this preferred height. Otherwise, the
 * height is determined by the preferred height of the text area. In this case
 * it is recommended to set the {@code rows} attribute.</td>
 * <td valign="top">yes</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TextAreaTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TextAreaTag extends InputComponentTag implements ScrollSizeSupport
{
    /** The default preferred scroll size if the attributes are undefined. */
    private static final NumberWithUnit DEF_SCROLL_SIZE = NumberWithUnit.ZERO;

    /** The preferred scroll width as string. */
    private String scrollWidthStr;

    /** The preferred scroll height as string. */
    private String scrollHeightStr;

    /** The preferred scroll width converted to a number with unit. */
    private NumberWithUnit preferredScrollWidth;

    /** The preferred sroll height converted to a number with unit. */
    private NumberWithUnit preferredScrollHeight;

    /** Stores the columns attribute. */
    private int columns;

    /** Stores the rows attribute. */
    private int rows;

    /** Stores the maxlength attribute. */
    private int maxlength;

    /** Stores the wrap attribute. */
    private boolean wrap;

    /**
     * Returns the number of columns of this text area.
     *
     * @return the number of columns
     */
    public int getColumns()
    {
        return columns;
    }

    /**
     * Setter method of the columns attribute.
     *
     * @param columns the attribute value
     */
    public void setColumns(int columns)
    {
        this.columns = columns;
    }

    /**
     * Returns the maximum number of characters this text field will accept.
     *
     * @return the maximum text length
     */
    public int getMaxlength()
    {
        return maxlength;
    }

    /**
     * Setter method of the maxlength attribute.
     *
     * @param maxlength the attribute value
     */
    public void setMaxlength(int maxlength)
    {
        this.maxlength = maxlength;
    }

    /**
     * Returns the number of rows of this text area.
     *
     * @return the number of rows
     */
    public int getRows()
    {
        return rows;
    }

    /**
     * Setter method of the rows attribute.
     *
     * @param rows the attribute value
     */
    public void setRows(int rows)
    {
        this.rows = rows;
    }

    /**
     * Returns a flag whether this text area should automatically wrap long
     * lines.
     *
     * @return the wrapping flag
     */
    public boolean isWrap()
    {
        return wrap;
    }

    /**
     * Setter method of the wrap attribute.
     *
     * @param wrap the attribute value
     */
    public void setWrap(boolean wrap)
    {
        this.wrap = wrap;
    }

    /**
     * Set method of the {@code scrollWidth} attribute.
     *
     * @param s the attribute's value
     */
    public void setScrollWidth(String s)
    {
        scrollWidthStr = s;
    }

    /**
     * Set method of the {@code scrollHeight} attribute.
     *
     * @param s the attribute's value
     */
    public void setScrollHeight(String s)
    {
        scrollHeightStr = s;
    }

    /**
     * {@inheritDoc} This implementation obtains the preferred scroll width from
     * the value of the {@code scrollWidth} property during the processing of
     * this tag.
     */
    public NumberWithUnit getPreferredScrollWidth()
    {
        return preferredScrollWidth;
    }

    /**
     * {@inheritDoc} This implementation obtains the preferred scroll width from
     * the value of the {@code scrollHeight} property during the processing of
     * this tag.
     */
    public NumberWithUnit getPreferredScrollHeight()
    {
        return preferredScrollHeight;
    }

    /**
     * Creates a component handler for the text area defined by this tag.
     *
     * @param manager the component manager
     * @param create the create flag
     * @return the component handler
     * @throws JellyTagException if the tag is incorrectly used
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected ComponentHandler<?> createComponentHandler(
            ComponentManager manager, boolean create)
            throws FormBuilderException, JellyTagException
    {
        if (create)
        {
            preferredScrollWidth = convertToNumberWithUnit(scrollWidthStr,
                    DEF_SCROLL_SIZE);
            preferredScrollHeight = convertToNumberWithUnit(scrollHeightStr,
                    DEF_SCROLL_SIZE);
        }

        return manager.createTextArea(this, create);
    }
}
