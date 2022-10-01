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
package net.sf.jguiraffe.gui.builder.components.tags;

import java.util.Locale;
import java.util.Map;

import net.sf.jguiraffe.gui.builder.components.model.StaticTextData;
import net.sf.jguiraffe.gui.builder.components.model.TextIconAlignment;
import net.sf.jguiraffe.transform.Transformer;
import net.sf.jguiraffe.transform.TransformerContext;

/**
 * <p>
 * A special implementation of the {@code Transformer} interface for converting
 * data to {@code StaticTextData} objects.
 * </p>
 * <p>
 * This transformer class is useful if {@link StaticTextTag} is used to produce
 * labels in forms which should display some data. The data is to be provided as
 * part of the form's model. If only textual data is involved, it is pretty
 * inconvenient to expose properties of type {@code StaticTextData} in the model
 * bean. Rather, this class can be assigned as {@code Transformer} to the static
 * text fields. It creates {@code StaticTextData} objects and populates them
 * from the object passed to the {@link #transform(Object, TransformerContext)}
 * method. The following objects can be handled:
 * <ul>
 * <li>Objects implementing the {@code CharSequence} interface become the text
 * of the {@code StaticTextData} object.</li>
 * <li>Objects of type {@link TextIconAlignment} are passed to the
 * {@code alignment} property.</li>
 * <li>If the object passed to {@code transform()} is already of type
 * {@code StaticTextData}, it is returned without changes.</li>
 * <li>Other objects are interpreted as icons and stored in the {@code icon}
 * property.</li>
 * </ul>
 * </p>
 * <p>
 * An instance defines properties for all elements of a {@code StaticTextData}
 * object. If set, the corresponding values are written in the newly created
 * objects. For instance, if the {@code icon} property is set and a
 * {@code CharSequence} object is passed to {@code transform()}, the resulting
 * object will have this icon and the text of the {@code CharSequence}. These
 * properties can be overridden by properties in the {@code TransformerContext}.
 * Here the following properties are supported:
 * <table border="1">
 * <tr>
 * <th>Property</th>
 * <th>Description</th>
 * <th>Default</th>
 * </tr>
 * <tr>
 * <td valign="top">text</td>
 * <td>The text to be written into the {@code StaticTextData} object.</td>
 * <td valign="top">undefined</td>
 * </tr>
 * <tr>
 * <td valign="top">icon</td>
 * <td>The icon to be written into the {@code StaticTextData} object.</td>
 * <td valign="top">undefined</td>
 * </tr>
 * <tr>
 * <td valign="top">alignment</td>
 * <td>The alignment for the {@code StaticTextData} object.</td>
 * <td valign="top">undefined</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: StaticTextDataTransformer.java 208 2012-02-11 20:57:33Z oheger $
 */
public class StaticTextDataTransformer implements Transformer
{
    /** Constant for the name of the property with the default text. */
    private static final String PROP_TEXT = "text";

    /** Constant for the name of the property with the default icon. */
    private static final String PROP_ICON = "icon";

    /** Constant for the name of the property with the default alignment. */
    private static final String PROP_ALIGNMENT = "alignment";

    /** The default text. */
    private String text;

    /** The default icon. */
    private Object icon;

    /** The default alignment. */
    private TextIconAlignment alignment;

    /**
     * Returns the default text for the newly created {@code StaticTextData}
     * objects.
     *
     * @return the default text
     */
    public String getText()
    {
        return text;
    }

    /**
     * Sets the default text for the newly created {@code StaticTextData}
     * objects.
     *
     * @param text the default text
     */
    public void setText(String text)
    {
        this.text = text;
    }

    /**
     * Returns the default icon for the newly created {@code StaticTextData}
     * objects.
     *
     * @return the default icon
     */
    public Object getIcon()
    {
        return icon;
    }

    /**
     * Sets the default icon for the newly created {@code StaticTextData}
     * objects.
     *
     * @param icon the default icon
     */
    public void setIcon(Object icon)
    {
        this.icon = icon;
    }

    /**
     * Returns the default {@code TextIconAlignment}.
     *
     * @return the default alignment
     */
    public TextIconAlignment getAlignment()
    {
        return alignment;
    }

    /**
     * Sets the default {@code TextIconAlignment}.
     *
     * @param alignment the default alignment
     */
    public void setAlignment(TextIconAlignment alignment)
    {
        this.alignment = alignment;
    }

    /**
     * {@inheritDoc} Performs the transformation as described in the class
     * comment.
     */
    public Object transform(Object o, TransformerContext ctx) throws Exception
    {
        if (o == null)
        {
            return null;
        }
        if (o instanceof StaticTextData)
        {
            return o;
        }

        StaticTextDataImpl result = new StaticTextDataImpl();
        populate(result, ctx);
        convertProperty(o, result, ctx);
        return result;
    }

    /**
     * Fills the specified data object with default values. This method is
     * called by {@code transform()} to write default values into the result
     * object. It evaluates the properties in the context and the member fields
     * of this instance.
     *
     * @param data the data object to be filled
     * @param ctx the {@code TransformerContext}
     */
    protected void populate(StaticTextDataImpl data, TransformerContext ctx)
    {
        Map<String, Object> props = ctx.properties();
        data.setText(fetchValue((String) props.get(PROP_TEXT), getText()));
        data.setIcon(fetchValue(props.get(PROP_ICON), getIcon()));
        TextIconAlignment align =
                fetchValue(convertAlignment(props.get(PROP_ALIGNMENT)),
                        getAlignment());
        if (align != null)
        {
            data.setAlignment(align);
        }
    }

    /**
     * Performs the transformation if the object to be converted must be set as
     * a property of a {@code StaticTextData} object. This method is called by
     * {@code transform()} if the passed in object is not <b>null</b> and not a
     * {@code StaticTextData} object. The base implementation sets the
     * corresponding property in the passed in data object based on the class of
     * the object to be converted.
     *
     * @param o the object to be converted
     * @param data the data object to be filled
     * @param ctx the {@code TransformerContext}
     * @throws Exception if an error occurs
     */
    protected void convertProperty(Object o, StaticTextDataImpl data,
            TransformerContext ctx) throws Exception
    {
        if (o instanceof CharSequence)
        {
            data.setText(((CharSequence) o).toString());
        }
        else if (o instanceof TextIconAlignment)
        {
            data.setAlignment((TextIconAlignment) o);
        }
        else
        {
            data.setIcon(o);
        }
    }

    /**
     * Helper method for converting an alignment value. This method can handle
     * strings which are converted to alignment constants.
     *
     * @param value the value to be converted
     * @return the converted value (can be <b>null</b>)
     */
    private static TextIconAlignment convertAlignment(Object value)
    {
        if (value instanceof TextIconAlignment)
        {
            return (TextIconAlignment) value;
        }
        if (value == null)
        {
            return null;
        }
        return TextIconAlignment.valueOf(value.toString().toUpperCase(
                Locale.ENGLISH));
    }

    /**
     * Helper method for obtaining a property value. If the value is defined, it
     * is casted and returned. Otherwise the default value is used.
     *
     * @param <T> the type of the value
     * @param value the value as object
     * @param defValue the default value
     * @return the final value
     */
    private static <T> T fetchValue(T value, T defValue)
    {
        return (value != null) ? value : defValue;
    }
}
