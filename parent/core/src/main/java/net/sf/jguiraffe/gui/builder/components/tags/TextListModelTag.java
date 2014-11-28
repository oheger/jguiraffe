/*
 * Copyright 2006-2014 The JGUIraffe Team.
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

import java.util.ArrayList;
import java.util.List;

import net.sf.jguiraffe.gui.builder.components.model.ListModel;
import net.sf.jguiraffe.gui.builder.di.DIBuilderData;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * A tag handler class that creates a text based {@link ListModel}.
 * </p>
 * <p>
 * This tag creates a list model whose values can be immediately defined in the
 * jelly script. This is appropriate for simple combo boxes or list boxes that
 * only display a fixed set of items. The model's content is defined by tags in
 * the body of this tag.
 * </p>
 * <p>
 * After the model has been created and initialized by the tag's body this tag
 * tries to find an enclosing tag that implements the {@link ListModelSupport}
 * interface. If such a tag can be found, it is passed the newly created model.
 * It is also possible to specify a name for the new model using the
 * <code>var</code> attribute. The new model will then be stored in the jelly
 * context, where it can be accessed by other components.
 * </p>
 * <p>
 * With the {@link #addItem(String, Object)} method new text items can be added
 * to the list model. This method will be called by specific tags that are
 * intended to be placed in this tag's body. This method expects a text label
 * that is displayed in the UI and an optional value; if the text is selected in
 * the list component, the corresponding value object is written into the form
 * data. If necessary, a type conversion is performed to the model's value type.
 * The following table lists the attributes supported by the {@code
 * TextListModelTag} class:
 * </p>
 * <p>
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">type</td>
 * <td>The data type class of the values of the data model created by this tag.
 * Values specified for this model are automatically converted to this data
 * type. If no value data type is specified, the type depends on the values
 * specified for model elements: if values are defined, it is set to {@code
 * java.lang.Integer} (representing the index of the selected item); otherwise
 * {@code java.lang.String} is assumed.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">var</td>
 * <td>With this attribute a variable name can be specified under which the
 * model created by this tag is stored in the Jelly context. Using this variable
 * the model can be accessed by other components.</td> </td
 * valign="top">Yes</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TextListModelTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TextListModelTag extends FormBaseTag
{
    /** Constant for a warning message about inconsistent model values. */
    private static final String VALUE_WARNING =
            "List model contains both null and non null values!";

    /** Holds a reference to the logger to use. */
    private final Log log = LogFactory.getLog(getClass());

    /** Stores the newly created list model. */
    private TextListModel model;

    /** Stores the data type of the value objects.*/
    private Object valueType;

    /**
     * Stores the name under which this list model should be stored in the
     * context.
     */
    private String var;

    /**
     * Setter method for the type attribute.
     *
     * @param c the attribute value
     */
    public void setType(Object c)
    {
        valueType = c;
    }

    /**
     * Setter method for the var attribute.
     *
     * @param s the attribute's value
     */
    public void setVar(String s)
    {
        var = s;
    }

    /**
     * Adds a new item to the newly created model.
     *
     * @param display the display text
     * @param value the corresponding value (can be <b>null </b>)
     */
    public void addItem(String display, Object value)
    {
        DIBuilderData diData = DIBuilderData.get(getContext());
        model.addItem(display, diData.getInvocationHelper()
                .getConversionHelper().convert(model.getType(), value));
    }

    /**
     * Called before evaluation of the tag's body. Creates the list model
     * object.
     */
    @Override
    protected void processBeforeBody()
    {
        model = new TextListModel();
        if (valueType != null)
        {
            model.setType(convertToClass(valueType));
        }
    }

    /**
     * Executes this tag.
     */
    @Override
    protected void process()
    {
        if (model.size() < 1)
        {
            log.warn("Model has no content!");
        }

        ListModelSupport parent = (ListModelSupport) findAncestorWithClass(ListModelSupport.class);
        if (parent != null)
        {
            parent.setListModel(model);
        }

        if (!StringUtils.isEmpty(var))
        {
            getContext().setVariable(var, model);
        }
    }

    /**
     * A simple implementation of the <code>ListModel</code> interface that
     * maintains a list of display texts and a list with the corresponding
     * values. The value list can be <b>null </b> if no special values are
     * needed.
     */
    protected static class TextListModel implements ListModel
    {
        /** Stores the list with the display texts. */
        private List<String> listDisplay;

        /** Stores the list with the values. */
        private List<Object> listValues;

        /** Stores the type of this list model. */
        private Class<?> type;

        /** The logger for this model. */
        private final Log log = LogFactory.getLog(getClass());

        /**
         * Creates a new instance of <code>TextListModel</code>.
         */
        public TextListModel()
        {
            listDisplay = new ArrayList<String>();
        }

        /**
         * Adds a new item to this list model.
         *
         * @param display the display text
         * @param value the value of this item (can be <b>null </b>)
         */
        public void addItem(String display, Object value)
        {
            listDisplay.add(display);
            if (value != null)
            {
                if (listValues == null)
                {
                    listValues = new ArrayList<Object>();
                    if (listDisplay.size() > 1)
                    {
                        // fill the values list with missing values
                        for (int i = 0; i < listDisplay.size() - 1; i++)
                        {
                            listValues.add(null);
                        }
                        log.warn(VALUE_WARNING);
                    }
                }
                listValues.add(value);
            }

            else
            {
                // if a value list exists, an additional null value must be
                // added
                if (listValues != null)
                {
                    listValues.add(null);
                    log.warn(VALUE_WARNING);
                }
            }
        }

        /**
         * Returns this model's size.
         *
         * @return the size
         */
        public int size()
        {
            return listDisplay.size();
        }

        /**
         * Returns the display object with the given index.
         *
         * @param index the index
         * @return the display object with this index
         */
        public Object getDisplayObject(int index)
        {
            return listDisplay.get(index);
        }

        /**
         * Returns the value object with the given index.
         *
         * @param index the index
         * @return the value object with this index
         */
        public Object getValueObject(int index)
        {
            return (listValues == null) ? null : listValues.get(index);
        }

        /**
         * Returns the type of this model's value. If a type has explicitly
         * been set, this type is returned. Otherwise the return value depends
         * on the definition of value objects: if there are value objects, the
         * type String is assumed, otherwise Integer.
         *
         * @return the type of the values
         */
        public Class<?> getType()
        {
            if (type != null)
            {
                return type;
            }
            else
            {
                return (listValues != null) ? String.class : Integer.class;
            }
        }

        /**
         * Sets the type of the values of this list model.
         *
         * @param type the type
         */
        public void setType(Class<?> type)
        {
            this.type = type;
        }
    }
}
