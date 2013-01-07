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
package net.sf.jguiraffe.gui.builder.components.tags;

import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.forms.ComponentHandler;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A specific input component tag that constructs a text field component.
 * </p>
 * <p>
 * Text fields are quite easy to define. In addition to the attributes inherited
 * from the base classes the following attributes are supported by this tag
 * handler class:
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
 * <td>Defines the number of columns the text field should have.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">maxlength</td>
 * <td>Allows to define a maximum length for text the user can type into this
 * text field.</td>
 * <td valign="top">yes</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TextFieldTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TextFieldTag extends InputComponentTag
{
    /** Stores the number of visible columns. */
    private int columns;

    /** Stores the maximum input length. */
    private int maxlength;

    /**
     * Returns the number of visible columns.
     *
     * @return the number of columns
     */
    public int getColumns()
    {
        return columns;
    }

    /**
     * Setter method for the columns attributes.
     *
     * @param columns the attribute value
     */
    public void setColumns(int columns)
    {
        this.columns = columns;
    }

    /**
     * Returns the maximum number of character that can be entered into this
     * field. Values of 0 or less mean that there is no limit.
     *
     * @return the number of characters this field can hold
     */
    public int getMaxlength()
    {
        return maxlength;
    }

    /**
     * Setter method for the maxlength attribute.
     *
     * @param maxlength the attribute value
     */
    public void setMaxlength(int maxlength)
    {
        this.maxlength = maxlength;
    }

    /**
     * Creates the component handler for this text field.
     *
     * @param manager the component manager
     * @param create the create flag
     * @return the component handler
     * @throws FormBuilderException if an error occurs
     * @throws JellyTagException if the tag is incorrectly used
     */
    protected ComponentHandler<?> createComponentHandler(ComponentManager manager,
            boolean create) throws FormBuilderException, JellyTagException
    {
        return manager.createTextField(this, create);
    }
}
