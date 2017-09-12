/*
 * Copyright 2006-2017 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.di.tags;

import net.sf.jguiraffe.di.impl.SetPropertyInvocation;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;

/**
 * <p>
 * A tag handler class that creates a <code>{@link SetPropertyInvocation}</code>
 * object.
 * </p>
 * <p>
 * This tag is initialized with the name of a property to be set and a
 * <code>Dependency</code> to the property's value. It will then create a
 * <code>SetPropertyInvocation</code> object with this information and add it
 * to the <code>{@link InvokableSupport}</code> object found in the current
 * context. The following attributes are supported:
 * </p>
 * <p>
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">property</td>
 * <td>Defines the name of the property to set. This property must exist in the
 * target class of the invocation.</td>
 * <td valign="top">no</td>
 * </tr>
 * <tr>
 * <td valign="top">refName</td>
 * <td>Specifies a dependency to another bean. The bean with this name will be
 * fetched and passed to the property setter method. </td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">refClass</td>
 * <td>Specifies a dependency to another bean by its class. The bean with this
 * class will be fetched and passed to the property setter method.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">refClassName</td>
 * <td>Specifies a dependency to another bean by its class name. The bean with
 * this class will be fetched and passed to the property setter method.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">refClassLoader</td>
 * <td>With this attribute a symbolic name for the class loader to be used can
 * be specified. It is evaluated only if the <code>refClassName</code>
 * attribute was set. In this case the class loader specified here will be used
 * for resolving the class name.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">value</td>
 * <td>If the property is to be set to a constant value, this attribute can be
 * used. It allows to directly specify the value. </td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">valueClass</td>
 * <td>If a constant value is to be used for the property value, it may be
 * necessary to perform some type conversion. With this attribute the type of
 * the property can be specified. The value will then be converted to this type.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">valueClassName</td>
 * <td>Like <code>valueClass</code>, but the name of the property's data
 * type class is specified.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">valueClassLoader</td>
 * <td>If the data type class of the value is specified by its name only, with
 * this attribute the class loader can be determined for resolving the class.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">var</td>
 * <td>If this invocation is part of a <code>ChainedInvocation</code>, it is
 * possible to refer to a local variable of this chain. This is done with this
 * attribute.</td>
 * <td valign="top">yes</td>.
 * </tr>
 * <tr>
 * <td valign="top">source</td>
 * <td>Defines the name of the local variable, on which this
 * <code>Invokable</code> object is to be executed. This attribute can only be
 * used if a <code>ChainedInvocation</code> is in the current scope. </td>
 * <td valign="top">yes</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * From all the different ways of defining a dependency exactly one must be
 * used.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SetPropertyTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class SetPropertyTag extends DependencyTag
{
    /** Stores the name of the property to set. */
    private String property;

    /** Stores the source attribute. */
    private String source;

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
     * Set method of the property attribute.
     *
     * @param property the attribute's value
     */
    public void setProperty(String property)
    {
        this.property = property;
    }

    /**
     * Returns the name of the source variable. This may be used if this tag is
     * used inside an invocation chain.
     *
     * @return the name of the source variable
     */
    public String getSource()
    {
        return source;
    }

    /**
     * Set method of the source attribute.
     *
     * @param source the attribute's value
     */
    public void setSource(String source)
    {
        this.source = source;
    }

    /**
     * The main method of this tag. Invokes the body and delegates to
     * <code>process()</code>.
     *
     * @param out the output object
     * @throws JellyTagException in case of an error
     */
    public void doTag(XMLOutput out) throws JellyTagException
    {
        invokeBody(out);
        process();
    }

    /**
     * Executes this tag. This method is invoked by <code>doTag()</code> and
     * does the real work.
     *
     * @throws JellyTagException if an error occurs or the tag is incorrectly
     * used
     */
    protected void process() throws JellyTagException
    {
        if (getProperty() == null)
        {
            throw new MissingAttributeException("property");
        }
        InvocationData.get(getContext()).addInvokable(
                new SetPropertyInvocation(getProperty(), createDependency()),
                null, getSource());
    }
}
