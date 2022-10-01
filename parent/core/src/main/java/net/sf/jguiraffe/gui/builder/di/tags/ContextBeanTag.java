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
package net.sf.jguiraffe.gui.builder.di.tags;

import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.impl.providers.ConstantBeanProvider;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;

/**
 * <p>
 * A specialized tag for the creation of beans from objects stored in the Jelly
 * context.
 * </p>
 * <p>
 * Jelly provides some native ways for creating objects and storing them in the
 * {@code JellyContext}. Also this tag library offers some functionality in this
 * area, for instance the {@link ConstantValueTag}. With this tag it is possible
 * to access such objects and expose them as regular beans of the dependency
 * injecting framework.
 * </p>
 * <p>
 * By inheriting from {@link AbstractBeanTag}, this tag has access to the
 * standard functionality related to bean definitions; so it allows the creation
 * of anonymous beans and named beans as well that are stored in a specific
 * {@code BeanContext}. The following table lists the attributes specific to
 * this tag:
 * </p>
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">name</td>
 * <td>Defines a name for the created bean definition. Using this name the bean
 * can be queried from an bean context.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">var</td>
 * <td>Refers to a variable in the Jelly context. This variable is looked up and
 * made available as bean in the selected {@code BeanContext}. Note that this
 * variable must exist; otherwise, the tag fails with an exception.</td>
 * <td valign="top">no</td>
 * </tr>
 * </table>
 * <p>
 * Below is an example how this tag can be used together with the
 * {@link ConstantValueTag} to expose a constant member field of a class as
 * bean. The {@code const} tag is used to obtain the value of a constant field
 * and store it in the Jelly context; from there it is picked up by the
 * {@code contextBean} tag and added to the current {@code BeanContext}:
 * </p>
 *
 * <pre>
 * &lt;di:const targetClassName="com.acme.MyService" field="INSTANCE"
 *   var="acme_service"/&gt;
 * &lt;di:contextBean name="acmeService" var="acme_service"/&gt;
 * </pre>
 *
 * @author Oliver Heger
 * @version $Id$
 * @since 1.4
 */
public class ContextBeanTag extends AbstractBeanTag
{
    /** The name of the bean to be created. */
    private String name;

    /** The variable in the Jelly context that is to be read. */
    private String var;

    /**
     * {@inheritDoc} This implementation returns the name as set by an attribute
     * of this tag. Thus named beans can be created in the usual way.
     */
    @Override
    public String getName()
    {
        return name;
    }

    /**
     * Set method of the {@code name} attribute.
     *
     * @param name the name for the bean to be created
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the name of the variable in the Jelly context that is to be read
     * by this tag.
     *
     * @return the name of the Jelly variable to be read
     */
    public String getVar()
    {
        return var;
    }

    /**
     * Set method of the {@code var} attribute.
     *
     * @param var the name of the source variable
     */
    public void setVar(String var)
    {
        this.var = var;
    }

    /**
     * {@inheritDoc} This implementation looks up the configured variable in the
     * Jelly context and creates a {@link ConstantBeanProvider} with it.
     */
    protected BeanProvider createBeanProvider() throws JellyTagException
    {
        if (getVar() == null)
        {
            throw new MissingAttributeException("var");
        }

        Object bean = getContext().findVariable(getVar());
        if (bean == null)
        {
            throw new JellyTagException("Cannot resolve variable '" + getVar()
                    + "' in the Jelly context!");
        }

        return ConstantBeanProvider.getInstance(bean);
    }
}
