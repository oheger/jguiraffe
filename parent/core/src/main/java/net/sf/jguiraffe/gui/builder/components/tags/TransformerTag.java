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
package net.sf.jguiraffe.gui.builder.components.tags;

import net.sf.jguiraffe.gui.forms.DefaultTransformerWrapper;
import net.sf.jguiraffe.transform.Transformer;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A tag handler class for creating {@link Transformer} objects and passing them
 * to input component tags.
 * </p>
 * <p>
 * With this tag a transformer object can be defined, either by specifying the
 * class name or by referencing an existing transformer. This can be done
 * through the means supported by the <code>UseBeanBaseTag</code>. In addition
 * to the properties supported by the base class the following attributes are
 * supported:
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
 * <td>With this attribute it can be determined whether a read or a write
 * transformer is to be defined: it can have the values <code>read</code> or
 * <code>write</code> (case does not matter; if it is missing, read is assumed).
 * Then the correct initialization method will be called on the enclosing input
 * component tag.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">componentType</td>
 * <td>Here the type of the associated input component can be specified if
 * necessary. The value of this attribute can be the fully qualified name of a
 * class. If defined, the input component's <code>setComponentType()</code>
 * method will be invoked.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TransformerTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TransformerTag extends TransformerBaseTag<Transformer>
{
    /** Constant for the name of the type attribute. */
    private static final String ATTR_TYPE = "type";

    /** Constant for the name of the component type attribute. */
    private static final String ATTR_COMP_TYPE = "componentType";

    /**
     * Creates a new instance of <code>TransformerTag</code>.
     */
    public TransformerTag()
    {
        super();
        addIgnoreProperty(ATTR_TYPE);
        addIgnoreProperty(ATTR_COMP_TYPE);
    }

    /**
     * Returns the type of the corresponding input component. This is the target
     * type of the transformer. This implementation returns the value of the
     * <code>componentType</code> attribute or <b>null</b> if it was not set.
     *
     * @return the component type
     */
    protected Class<?> getComponentType()
    {
        Object cls = getAttributes().get(ATTR_COMP_TYPE);
        return (cls != null) ? FormBaseTag.convertToClass(cls) : null;
    }

    /**
     * Determines the type of the transformer based on the <code>type</code>
     * attribute.
     *
     * @return the transformer type
     * @throws JellyTagException if the type is invalid
     */
    protected Type getTransformerType() throws JellyTagException
    {
        if (!getAttributes().containsKey(ATTR_TYPE))
        {
            return Type.READ;
        }

        try
        {
            return Type.valueOf(String.valueOf(getAttributes().get(ATTR_TYPE))
                    .toUpperCase());
        }
        catch (IllegalArgumentException iex)
        {
            throw new JellyTagException("Invalid transformer type: "
                    + getAttributes().get(ATTR_TYPE));
        }
    }

    /**
     * Initializes the passed in input component tag with a transformer.
     *
     * @param tag the input component tag
     * @param bean the transformer bean
     * @throws JellyTagException if an error occurs
     */
    @Override
    protected void handleInputComponentTag(InputComponentTag tag,
            Transformer bean) throws JellyTagException
    {
        if (getTransformerType() == Type.READ)
        {
            tag.setReadTransformer(new DefaultTransformerWrapper(bean,
                    getTransformerContext()));
        }
        else
        {
            tag.setWriteTransformer(new DefaultTransformerWrapper(bean,
                    getTransformerContext()));
        }

        Class<?> compType = getComponentType();
        if (compType != null)
        {
            tag.setComponentType(compType);
        }
    }

    /**
     * An enumeration for the possible types of a transformer.
     */
    public enum Type
    {
        /**
         * A read transformer. Such transformers are invoked when data is read
         * from an input component.
         */
        READ,

        /**
         * A write transformer. Write transformers are called when setting data
         * of a form field. They typically create a representation of the data
         * that is displayed in the GUI.
         */
        WRITE
    }
}
