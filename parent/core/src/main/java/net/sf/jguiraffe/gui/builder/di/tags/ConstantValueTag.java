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
package net.sf.jguiraffe.gui.builder.di.tags;

import net.sf.jguiraffe.gui.builder.di.DIBuilderData;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * <p>
 * A specialized tag handler implementation for resolving values of constant
 * fields.
 * </p>
 * <p>
 * Tags implemented by this tag handler class can be used to obtain the values
 * of constant fields, i.e. members of a class declared as {@code public static
 * final}. This can be useful for instance, if the values of such constants are
 * to be passed to constructors, method invocations, or be set as properties.
 * </p>
 * <p>
 * This tag provides the same functionality as the {@code <getStatic>} tag from
 * the Jelly core tag library. However, there are the following differences:
 * <ul>
 * <li>This tag supports specifying the class loader for loading the target
 * class. It provides the typical facilities for specifying the class as
 * supported by other tags of the dependency injection framework.</li>
 * <li>If the parent tag of this tag implements the {@link ValueSupport}
 * interface, the resolved constant value is also passed to this tag. This makes
 * it very easy to integrate the functionality provided by this implementation
 * with other tags of the dependency injection framework.</li>
 * </ul>
 * </p>
 * <p>
 * The following table lists all attributes supported by this tag handler
 * implementation:
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">var</td>
 * <td>Here the name of a variable can be specified under which the resolved
 * constant value is stored in the Jelly context. This variable can later be
 * accessed by other tags, so the constant value can be reused.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">targetClass</td>
 * <td>Defines the class from which the constant field is to be resolved. Either
 * the class or the class name must be specified.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">targetClassName</td>
 * <td>Defines the name of the class from which the constant field is to be
 * resolved. Either the class or the class name must be specified.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">targetClassLoader</td>
 * <td>With this attribute the symbolic name of the class loader can be
 * specified that should be used for resolving the class name. A class loader
 * with this name is retrieved from the current
 * {@link net.sf.jguiraffe.di.ClassLoaderProvider ClassLoaderProvider} to load
 * the class. This attribute is only evaluated if the {@code targetClassName}
 * attribute is set.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">field</td>
 * <td>This attribute determines the field to be read from the target class.
 * Here the name of an accessible static field must be provided.</td>
 * <td valign="top">No</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * If neither the {@code var} attribute is set nor the tag is nested inside a
 * {@link ValueSupport} tag, an exception is thrown. The following example shows
 * how this tag can be used to pass the value of the {@code Integer.MAX_VALUE}
 * constant to a set property invocation:
 *
 * <pre>
 * &lt;di:setProperty property="number"&gt;
 *   &lt;di:const targetClassName="java.lang.Integer" field="MAX_VALUE"/&gt;
 * &lt;/di:setProperty&gt;
 * </pre>
 *
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ConstantValueTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ConstantValueTag extends TagSupport
{
    /** Stores the data object for the class description. */
    private final ClassDescData classDescData;

    /** The variable name for storing the resolved value. */
    private String var;

    /** The name of the field to retrieve. */
    private String field;

    /**
     * Creates a new instance of {@code ConstantValueTag}.
     */
    public ConstantValueTag()
    {
        classDescData = new ClassDescData();
    }

    /**
     * Returns the name of the variable under which the resolved value is to be
     * stored.
     *
     * @return the variable name
     */
    public String getVar()
    {
        return var;
    }

    /**
     * Set method of the {@code var} attribute.
     *
     * @param var the attribute's value
     */
    public void setVar(String var)
    {
        this.var = var;
    }

    /**
     * Returns the name of the constant field that is to be retrieved by this
     * tag.
     *
     * @return the name of the field
     */
    public String getField()
    {
        return field;
    }

    /**
     * Set method of the {@code field} attribute.
     *
     * @param field the attribute's value
     */
    public void setField(String field)
    {
        this.field = field;
    }

    /**
     * Set method of the {@code targetClass} attribute.
     *
     * @param cls the attribute's value
     */
    public void setTargetClass(Class<?> cls)
    {
        getClassDescData().setTargetClass(cls);
    }

    /**
     * Set method of the {@code targetClassName} attribute.
     *
     * @param clsName the attribute's value
     */
    public void setTargetClassName(String clsName)
    {
        getClassDescData().setTargetClassName(clsName);
    }

    /**
     * Set method of the {@code targetClassLoader} attribute.
     *
     * @param loader the attribute's value
     */
    public void setTargetClassLoader(String loader)
    {
        getClassDescData().setClassLoaderName(loader);
    }

    /**
     * Executes this tag. Delegates to the helper methods to check the
     * parameters, fetch the constant value and passing it to the parent tag or
     * storing it in a variable.
     *
     * @param output the output object
     * @throws JellyTagException if an error occurs
     */
    public void doTag(XMLOutput output) throws JellyTagException
    {
        checkAttributes();

        DIBuilderData builderData = DIBuilderData.get(getContext());
        Object value = resolveConstantValue(getClassDescData().resolveClass(
                builderData.getClassLoaderProvider()));

        storeValue(value);
    }

    /**
     * Obtains the value of the constant field from the target class. This
     * method is called by {@link #doTag(XMLOutput)}. When it is called it has
     * already been checked whether all mandatory attributes are present. This
     * implementation reads the value of the constant field using reflection.
     *
     * @param targetClass the target class
     * @return the value of the constant field
     * @throws JellyTagException if an error occurs
     */
    protected Object resolveConstantValue(Class<?> targetClass)
            throws JellyTagException
    {
        try
        {
            return targetClass.getField(getField()).get(null);
        }
        catch (Exception ex)
        {
            // handle all reflection-related exceptions the same way
            throw new JellyTagException("Error when accessing field "
                    + getField() + " in class " + targetClass, ex);
        }
    }

    /**
     * Returns the {@code ClassDescData} objects with the specification of the
     * target class.
     *
     * @return the internally used {@code ClassDescData} object
     */
    ClassDescData getClassDescData()
    {
        return classDescData;
    }

    /**
     * Tests whether all required attributes are provided. Throws an exception
     * if not.
     *
     * @throws JellyTagException if attributes are invalid or missing
     */
    private void checkAttributes() throws JellyTagException
    {
        if (getField() == null)
        {
            throw new MissingAttributeException("field");
        }
        if (getVar() == null)
        {
            if (!(getParent() instanceof ValueSupport))
            {
                throw new JellyTagException(
                        "No target for the resolved value: "
                                + "Specify the var attribute or place the tag "
                                + "in the body of a ValueSupport tag.");
            }
        }
    }

    /**
     * Stores the resolved value.
     *
     * @param value the value
     */
    private void storeValue(Object value)
    {
        if (getParent() instanceof ValueSupport)
        {
            ((ValueSupport) getParent()).setValue(value);
        }

        if (getVar() != null)
        {
            getContext().setVariable(getVar(), value);
        }
    }
}
