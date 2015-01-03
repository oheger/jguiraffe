/*
 * Copyright 2006-2015 The JGUIraffe Team.
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

import java.net.URL;

import net.sf.jguiraffe.gui.builder.di.DIBuilderData;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * A tag for resolving resources on the class path and creating corresponding
 * URI strings.
 * </p>
 * <p>
 * Sometimes an application ships with data like properties files, style sheets,
 * etc., which is stored on the class path. Per default, such data is resolved
 * using the {@code getResource()} method of {@code ClassLoader}. In
 * environments with complex class loader structures, it is not trivial to get
 * the correct class loader; for this purpose, <em>JGUIraffe</em> defines the
 * {@code ClassLoaderProvider} interface. With this tag it is possible to shield
 * application code from the mechanism of resolving a resource with the correct
 * class loader. The idea is to use this tag to do the resolving and store the
 * resulting URL string in a Jelly variable. This variable can then be used when
 * declaring a bean as constructor argument or property. Thus, beans get
 * initialized with correctly resolved URLs without having to no the details of
 * this process.
 * </p>
 * <p>
 * In its most simple form this tag is passed a resource name and the name of
 * the target variable: <code>
 *     &lt;di:resource resource=&quot;myResource&quot; var=&quot;url&quot;/&gt;
 * </code>
 * </p>
 * <p>
 * It is also possible to specify the class loader to be used for the resolving
 * operation by its name: <code>
 *     &lt;di:resource resource=&quot;myResource&quot; classLoader=&quot;myLoader&quot;
 *       var=&quot;url&quot; /&gt;
 * </code>
 * </p>
 * <p>
 * A single variable can be used as target for multiple resolve operations. In
 * this case, the URL strings are concatenated with a configurable delimiter
 * string. The example below shows how two resources a resolved resulting in a
 * string with their corresponding URLs using a comma as delimiter: <code>
 *     &lt;di:resource resource=&quot;myResource1&quot; var=&quot;url&quot; delimiter=&quot;,&quot;/&gt;
 *     &lt;di:resource resource=&quot;myResource2&quot; var=&quot;url&quot; delimiter=&quot;,&quot;/&gt;
 * </code>
 * </p>
 * <p>
 * The following attributes are supported:
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">resource</td>
 * <td>The name of the resource to be resolved on the class path. If this
 * resource cannot be resolved, the tag throws an exception.</td>
 * <td valign="top">No</td>
 * </tr>
 * <tr>
 * <td valign="top">var</td>
 * <td>The name of the variable in the Jelly context which stores the result,
 * i.e. the resolved URI string. This variable is overwritten if it exists
 * unless the {@code delimiter} attribute is used (see below).</td>
 * <td valign="top">No</td>
 * </tr>
 * <tr>
 * <td valign="top">classLoader</td>
 * <td>The name of the class loader to be used for resolving the resource name.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">delimiter</td>
 * <td>When using this attribute multiple resolve operations use the same output
 * variable. If the specified variable does not have a value yet, it is assigned
 * the resolved URI string. Otherwise, the URI string is added to the variable
 * using the delimiter string defined here.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id$
 * @since 1.3
 */
public class ResourceTag extends TagSupport
{
    /** The name of the resource to be resolved. */
    private String resource;

    /** The name of the target variable. */
    private String var;
    private String classLoader;
    private String delimiter;

    /**
     * Set method for the {@code resource} attribute.
     *
     * @param resource the resource name
     */
    public void setResource(String resource)
    {
        this.resource = resource;
    }

    /**
     * Returns the name of the resource to be resolved.
     *
     * @return the resource name
     */
    public String getResource()
    {
        return resource;
    }

    /**
     * Set method for the {@code var} attribute.
     *
     * @param var the name of the target variable
     */
    public void setVar(String var)
    {
        this.var = var;
    }

    /**
     * Returns the name of the target variable.
     *
     * @return the name of the target variable
     */
    public String getVar()
    {
        return var;
    }

    /**
     * Set method for the {@code classLoader} attribute.
     *
     * @param classLoader the name of the class loader to be used
     */
    public void setClassLoader(String classLoader)
    {
        this.classLoader = classLoader;
    }

    /**
     * Returns the name of the class loader to be used for resolving the
     * resource.
     *
     * @return the class loader name
     */
    public String getClassLoader()
    {
        return classLoader;
    }

    /**
     * Set method for the {@code delimiter} attribute.
     *
     * @param delimiter the delimiter
     */
    public void setDelimiter(String delimiter)
    {
        this.delimiter = delimiter;
    }

    /**
     * Returns the delimiter to be used for concatenating the results of
     * multiple resolve operations.
     *
     * @return the delimiter
     */
    public String getDelimiter()
    {
        return delimiter;
    }

    public void doTag(XMLOutput output) throws JellyTagException
    {
        if (getResource() == null)
        {
            throw new MissingAttributeException("resource");
        }
        if (getVar() == null)
        {
            throw new MissingAttributeException("var");
        }

        storeResult(resolveResource());
    }

    /**
     * Stores the result of the resolve operation in the specified target
     * variable. This method also handles the concatenation of multiple results
     * if a delimiter has been specified.
     *
     * @param result the result to be stored
     */
    private void storeResult(String result)
    {
        if (getDelimiter() == null)
        {
            writeResultVariable(result);
        }
        else
        {
            Object oldResult = getContext().getVariable(getVar());
            if (!(oldResult instanceof String)
                    || StringUtils.isEmpty((String) oldResult))
            {
                writeResultVariable(result);
            }
            else
            {
                writeResultVariable(oldResult + getDelimiter() + result);
            }
        }
    }

    /**
     * Writes the result variable. Stores the specified result string.
     *
     * @param result the result string
     */
    private void writeResultVariable(String result)
    {
        getContext().setVariable(getVar(), result);
    }

    /**
     * Resolves the resource as specified in this tag's attributes.
     *
     * @return the URI string representing the resource
     * @throws JellyTagException if the resource cannot be resolved
     */
    private String resolveResource() throws JellyTagException
    {
        DIBuilderData data = DIBuilderData.get(getContext());
        URL url =
                data.getClassLoaderProvider().getClassLoader(getClassLoader())
                        .getResource(getResource());
        if (url == null)
        {
            throw new JellyTagException("Could not resolve resource name: "
                    + getResource());
        }

        return url.toExternalForm();
    }
}
