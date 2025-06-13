/*
 * Copyright 2006-2025 The JGUIraffe Team.
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

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;

/**
 * <p>
 * A tag handler class that creates an item for a list model.
 * </p>
 * <p>
 * Tags of this class can appear in the body of a
 * <code>{@link TextListModelTag}</code> tag. Each tag defines one item of the
 * model with a mandatory display text (which can be set either directly or
 * through a resource definition) and an optional value. The following table
 * lists all supported attributes:
 * </p>
 * <p>
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">text</td>
 * <td>Defines the display text of this model item directly.</td>
 * <td rowspan="2">exactly one of these</td>
 * </tr>
 * <tr>
 * <td valign="top">textres</td>
 * <td>Allows to define the display text of this model item from a resource
 * property. If no resource group is specified, the builder's default resource
 * will be used.</td>
 * </tr>
 * <tr>
 * <td valign="top">resgrp</td>
 * <td>Sets the resource group to use to resolve the resource provided by the
 * <code>textres</code> attribute.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">value</td>
 * <td>The corresponding value of this list model item. This can be an arbitrary
 * object.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">valueRef</td>
 * <td>Using this attribute the value of the corresponding list item can be
 * specified as a reference. It will be looked up in the current {@code
 * BeanContext}. This way the full power of the dependency injection framework
 * can be used for creating and initializing Java objects. If both the {@code
 * value} and the {@code valueRef} attributes are specified, the {@code value}
 * attribute takes precedence.</td>
 * <td valign="top">yes</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ListModelItemTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ListModelItemTag extends FormBaseTag
{
    /** The text data object for managing the display text. */
    private TextData textData;

    /** Stores the value of the model item. */
    private Object value;

    /** The reference to the value. */
    private String valueRef;

    /**
     * Creates a new instance of {@code ListModelItemTag}.
     */
    public ListModelItemTag()
    {
        textData = new TextData(this);
    }

    /**
     * Returns the text data object with the definition of the display text.
     *
     * @return the display text data
     */
    public TextData getTextData()
    {
        return textData;
    }

    /**
     * Setter method of the text attribute.
     *
     * @param s the attribute value
     */
    public void setText(String s)
    {
        getTextData().setText(s);
    }

    /**
     * Setter method of the textres attribute.
     *
     * @param s the attribute value
     */
    public void setTextres(String s)
    {
        getTextData().setTextres(s);
    }

    /**
     * Setter method of the resgrp attribute.
     *
     * @param s the attribute value
     */
    public void setResgrp(String s)
    {
        getTextData().setResgrp(s);
    }

    /**
     * Returns the value of this list item.
     *
     * @return the value (can be <b>null </b>)
     */
    public Object getValue()
    {
        return value;
    }

    /**
     * Setter method of the value attribute.
     *
     * @param v the attribute value
     */
    public void setValue(Object v)
    {
        this.value = v;
    }

    /**
     * Returns the reference to the value.
     *
     * @return the reference name of the value bean
     */
    public String getValueRef()
    {
        return valueRef;
    }

    /**
     * Set method of the {@code valueRef} attribute
     *
     * @param valueRef the attribute value
     */
    public void setValueRef(String valueRef)
    {
        this.valueRef = valueRef;
    }

    /**
     * Executes this tag.
     *
     * @throws JellyTagException if this tag is used incorrectly
     */
    @Override
    protected void process() throws JellyTagException
    {
        if (!getTextData().isDefined())
        {
            throw new MissingAttributeException("text");
        }
        TextListModelTag parent = (TextListModelTag) findAncestorWithClass(TextListModelTag.class);
        if (parent == null)
        {
            throw new JellyTagException(
                    "This tag must be nested inside a TextListModelTag!");
        }
        parent.addItem(getTextData().getCaption(), fetchValue());
    }

    /**
     * Returns the value of this item. This method is called by {@code
     * process()}. It inspects the attributes {@code value} and {@code valueRef}
     * and processes them correspondingly.
     *
     * @return the value of this item (can be <b>null</b> if unspecified)
     */
    protected Object fetchValue()
    {
        Object value = getValue();

        if (value == null && getValueRef() != null)
        {
            value = getBuilderData().getBeanContext().getBean(getValueRef());
        }

        return value;
    }
}
