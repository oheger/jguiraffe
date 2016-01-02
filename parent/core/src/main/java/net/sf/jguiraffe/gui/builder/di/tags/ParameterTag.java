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
package net.sf.jguiraffe.gui.builder.di.tags;

import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.impl.ClassDescription;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;

/**
 * <p>
 * A tag handler class for defining the parameters of a method or constructor
 * invocation.
 * </p>
 * <p>
 * This tag can be placed multiple times in the body of an
 * <code>{@link InvocationTag}</code>. Each occurrence defines exactly one
 * parameter of the invocation. A parameter is defined by a dependency (several
 * ways of defining dependencies are supported). Optionally the data type class
 * of the parameter can be provided. (This is recommended because it makes
 * reflection calls more efficient.) The following table shows the attributes
 * supported by this tag:
 * </p>
 * <p>
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">refName</td>
 * <td>Specifies a dependency to another bean. The bean with this name will be
 * fetched and passed as parameter to the affected invocation. </td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">refClass</td>
 * <td>Specifies a dependency to another bean by its class. The bean with this
 * class will be fetched and passed as parameter to the affected invocation.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">refClassName</td>
 * <td>Specifies a dependency to another bean by its class name. The bean with
 * this class will be fetched and passed as parameter to the affected
 * invocation.</td>
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
 * <td>If the parameter is to be set to a constant value, this attribute can be
 * used. It allows to directly specify the value. </td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">valueClass</td>
 * <td>If a constant value is to be used for the parameter value, it may be
 * necessary to perform some type conversion. With this attribute the type of
 * the parameter can be specified. The value will then be converted to this
 * type.</td>
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
 * <td valign="top">parameterClass</td>
 * <td>Defines the data type class of this parameter. When resolving the method
 * to be invoked this data type is used. If it is unspecified, it will be
 * inferred from the type of the parameter value (which might be slightly less
 * efficient). </td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">parameterClassName</td>
 * <td>Like <code>paramClass</code>, but the data type of this parameter is
 * specified by its name.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">parameterClassLoader</td>
 * <td>Specifies the class loader for resolving the class of this parameter if
 * it is specified by name.</td>
 * <td valign="top">yes</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ParameterTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ParameterTag extends DependencyTag
{
    /** Stores the association InvocationTag. */
    private InvocationTag invocationTag;

    /** Stores the description of the parameter class. */
    private ClassDescription parameterClass;

    /** A data object for initializing the parameter class description. */
    private ClassDescData parameterClassData;

    /** Stores the dependency of this parameter. */
    private Dependency parameterDependency;

    /**
     * Creates a new instance of <code>ParameterTag</code>.
     */
    public ParameterTag()
    {
        parameterClassData = new ClassDescData();
    }

    /**
     * The main method of this tag. Finds the enclosing
     * <code>InvocationTag</code> and delegates to the <code>process()</code>
     * method.
     *
     * @param out the output object
     * @throws JellyTagException if an error occurs or the tag is not correctly
     * used
     */
    public void doTag(XMLOutput out) throws JellyTagException
    {
        initInvocationTag((InvocationTag) findAncestorWithClass(InvocationTag.class));
        invokeBody(out);
        process();
    }

    /**
     * Returns the dependency for this parameter. This is basically the
     * definition of the parameter value.
     *
     * @return the dependency of this parameter
     */
    public Dependency getParameterDependency()
    {
        return parameterDependency;
    }

    /**
     * Returns a <code>ClassDescription</code> object for the data type class
     * of this parameter. This may be <b>null</b> if it was not defined.
     *
     * @return the data type class of this parameter
     */
    public ClassDescription getParameterClassDesc()
    {
        return parameterClass;
    }

    /**
     * Returns a reference to the <code>InvocationTag</code>, to which this
     * tag belongs.
     *
     * @return the enclosing <code>InvocationTag</code>
     */
    public InvocationTag getInvocationTag()
    {
        return invocationTag;
    }

    /**
     * Returns the data object for the class description of this parameter.
     *
     * @return the parameter class data
     */
    public ClassDescData getParameterClassData()
    {
        return parameterClassData;
    }

    /**
     * Set method of the parameterClass attribute.
     *
     * @param c the attribute's value
     */
    public void setParameterClass(Class<?> c)
    {
        getParameterClassData().setTargetClass(c);
    }

    /**
     * Set method of the parameterClassName attribute.
     *
     * @param s the attribute's value
     */
    public void setParameterClassName(String s)
    {
        getParameterClassData().setTargetClassName(s);
    }

    /**
     * Set method of the parameterClassLoader attribute.
     *
     * @param s the attribute's value
     */
    public void setParameterClassLoader(String s)
    {
        getParameterClassData().setClassLoaderName(s);
    }

    /**
     * Executes this tag. This implementation will check and evaluate the
     * attributes and add itself to the owning <code>InvocationTag</code>.
     *
     * @throws JellyTagException if the tag is not correctly used
     */
    protected void process() throws JellyTagException
    {
        if (getInvocationTag() == null)
        {
            throw new JellyTagException(
                    "This tag must be nested inside an InvocationTag!");
        }

        parameterDependency = createDependency();
        if (getParameterClassData().isDefined())
        {
            parameterClass = getParameterClassData().createClassDescription();
        }

        getInvocationTag().addParameter(this);
    }

    /**
     * Initializes this tag with the reference to the associated
     * <code>InvocationTag</code>. This may be <b>null</b> if the tag is not
     * placed in the body of an <code>InvocationTag</code> (which is normally
     * an error).
     *
     * @param invTag the invocation tag
     */
    protected void initInvocationTag(InvocationTag invTag)
    {
        invocationTag = invTag;
    }
}
