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

import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.di.tags.ValueSupport;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * A tag for setting a property.
 * </p>
 * <p>
 * This tag can be placed in the body of a tag implementing the
 * {@link PropertySupport} interface (for instance a tag derived from the
 * {@link UseBeanBaseTag} class). From its attributes it will obtain the key and
 * the value of a property and set this property on its parent tag. The
 * following attributes are supported:
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">property</td>
 * <td>Defines the name of the property.</td>
 * <td valign="top">No</td>
 * </tr>
 * <tr>
 * <td valign="top">value</td>
 * <td>Defines the value of the property. This can be an arbitrary object, and
 * even be <b>null</b>. It will be passed as is to the parent tag.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * This tag handler class implements the {@link ValueSupport} interface.
 * Therefore tags in this tag's body can define its value which is then passed
 * to the parent tag.
 * </p>
 * <p>
 * Note: From its functionality this tag is similar to the
 * {@link net.sf.jguiraffe.gui.builder.di.tags.SetPropertyTag} class. It also
 * allows setting a property. However, the targets a different. While {@code
 * SetPropertyTag} is used for setting properties of beans created by the
 * <em>dependency injection framework</em>, this tag operates with tags
 * implementing the {@link PropertySupport} interface. It is mainly used
 * together with tags derived from {@link UseBeanBaseTag}.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: PropertyTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class PropertyTag extends FormBaseTag implements ValueSupport
{
    /** Stores the name of the property to set. */
    private String property;

    /** Stores the value of the property. */
    private Object value;

    /**
     * Returns the name of the property to be set by this tag.
     *
     * @return the property name
     */
    public String getProperty()
    {
        return property;
    }

    /**
     * Set method of the {@code property} attribute.
     *
     * @param property the attribute's value
     */
    public void setProperty(String property)
    {
        this.property = property;
    }

    /**
     * Set method of the value attribute.
     *
     * @param v the attribute's value
     */
    public void setValue(Object v)
    {
        value = v;
    }

    /**
     * Returns the value of the property.
     *
     * @return the value of the property
     */
    public Object getValue()
    {
        return value;
    }

    /**
     * Processes this tag. Tries to set the defined property at the parent tag.
     *
     * @throws JellyTagException if the tag is incorrectly used
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected void process() throws JellyTagException, FormBuilderException
    {
        if (StringUtils.isEmpty(getProperty()))
        {
            throw new MissingAttributeException("property");
        }
        if (!(getParent() instanceof PropertySupport))
        {
            throw new JellyTagException(
                    "Parent tag must implement PropertySupport interface!");
        }

        ((PropertySupport) getParent()).setProperty(getProperty(), getValue());
    }
}
