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

import java.util.HashMap;
import java.util.Map;

import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A tag for creating a map with properties and passing it to a target tag.
 * </p>
 * <p>
 * This tag handler class constructs a map that can be populated with properties
 * by tags in the body of this tag. The final map can then either be passed to
 * the parent tag (which must implement the
 * <code>{@link PropertiesSupport}</code> interface) or stored as a variable
 * in the Jelly context. The following attributes are supported:
 * </p>
 * <p>
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">var</td>
 * <td>Here the name of a variable can be specified, under which the resulting
 * map should be stored in the Jelly context.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">ref</td>
 * <td>With this attribute the name of a variable can be specified, which will
 * be used for initializing the internally stored map. This variable must be of
 * type <code>java.util.Map</code>. Newly added properties will be stored in
 * this map. If this attribute is not specified, a new map will be created.
 * Usage of this attribute allows to reuse a map that was created by another
 * <code>PropertiesTag</code>.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * If the <code>var</code> attribute is not specified, the parent tag must
 * implement the <code>{@link PropertiesSupport}</code> interface. Otherwise
 * an exception will be thrown because no target for the resulting map can be
 * determined.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: PropertiesTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class PropertiesTag extends FormBaseTag implements PropertySupport
{
    /** Stores the map with the properties. */
    private Map<String, Object> properties;

    /** Stores the var attribute. */
    private String var;

    /** Stores the ref attribute. */
    private String ref;

    /**
     * Returns the name of a variable, under which the properties are to be
     * stored.
     *
     * @return a variable name for the properties
     */
    public String getVar()
    {
        return var;
    }

    /**
     * Set method of the var attribute.
     *
     * @param var the attribute's value
     */
    public void setVar(String var)
    {
        this.var = var;
    }

    /**
     * Returns the name of a variable, under which the initial map is stored.
     *
     * @return a avariable with the initial properties map
     */
    public String getRef()
    {
        return ref;
    }

    /**
     * Set method of the ref attribute.
     *
     * @param ref the attribute's value
     */
    public void setRef(String ref)
    {
        this.ref = ref;
    }

    /**
     * Processes this tag before its body gets executed. This implementation
     * will initialize the internal map with the properties.
     *
     * @throws JellyTagException if attributes have invalid values
     * @throws FormBuilderException if an error occurs
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void processBeforeBody() throws JellyTagException,
            FormBuilderException
    {
        if (getRef() != null)
        {
            Object obj = getContext().findVariable(getRef());
            if (obj == null)
            {
                throw new JellyTagException(
                        "Invalid ref attribute: Unknown variable " + getRef());
            }
            if (!(obj instanceof Map))
            {
                throw new JellyTagException("Invalid ref attribute: Variable "
                        + getRef() + " does not point to a map!");
            }

            properties = (Map<String, Object>) obj;
        }

        else
        {
            properties = new HashMap<String, Object>();
        }
    }

    /**
     * Processes this tag. This method does the major part of the required work.
     * It locates the target and passes it the map with the properties. If no
     * target can be determined, an exception will be thrown.
     *
     * @throws JellyTagException if the tag is incorrectly used
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected void process() throws JellyTagException, FormBuilderException
    {
        boolean hasParent = getParent() instanceof PropertiesSupport;

        if (hasParent)
        {
            ((PropertiesSupport) getParent()).setProperties(properties);
        }

        if (getVar() != null)
        {
            getContext().setVariable(getVar(), properties);
        }
        else
        {
            if (!hasParent)
            {
                // no target could be found
                throw new JellyTagException(
                        "No target specified for properties!");
            }
        }
    }

    /**
     * Sets a property. This method will be called by tags in the body.
     *
     * @param name the name of the property
     * @param value the value of the property
     */
    public void setProperty(String name, Object value)
    {
        properties.put(name, value);
    }
}
