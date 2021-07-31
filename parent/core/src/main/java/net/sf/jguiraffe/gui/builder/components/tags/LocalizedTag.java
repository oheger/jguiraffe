/*
 * Copyright 2006-2021 The JGUIraffe Team.
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
import net.sf.jguiraffe.transform.TransformerContext;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;

/**
 * <p>
 * A specialized tag handler class for loading localized texts and passing them
 * to tags implementing the {@link ValueSupport} interface.
 * </p>
 * <p>
 * There are a couple of tag handler classes that are able to assign values to
 * beans that have been created. This includes tags from the <em>dependency
 * injection framework</em> (e.g. {@code SetPropertyTag}) and tags from the
 * <em>component builder framework</em> (e.g. {@code PropertyTag}) as well. Per
 * default, the values to be assigned are hard-coded in builder scripts. This
 * can be a problem if string properties are involved whose values should be
 * translated (and thus be read from resource files).
 * </p>
 * <p>
 * This tag handler class provides a solution for this problem. It reads a text
 * resource defined by its attributes and passes its current localized value to
 * the parent tag. The parent tag must implement the {@link ValueSupport}
 * interface which allows nested tags to hook into the mechanism of defining
 * values. The following example fragment shows how this could look in practice:
 *
 * <pre>
 * &lt;di:bean name=&quot;listLocalized&quot;
 *   beanClassName=&quot;net.sf.jguiraffe.di.ReflectionTestClass&quot;&gt;
 *   &lt;di:setProperty property=&quot;data&quot;&gt;
 *     &lt;f:localized resid=&quot;myMessage&quot;/&gt;
 *   &lt;/di:setProperty&gt;
 * &lt;/di:bean&gt;
 * </pre>
 *
 * Here a bean is created using the <em>dependency injection framework</em>, and
 * the {@code data} property of the bean is assigned a value that is obtained
 * from the application's resources. So instead of hard-coding the property
 * value, the {@code <localized>} tag is placed in the body of the {@code
 * <setProperty>} tag.
 * </p>
 * <p>
 * The following table lists all attributes supported by this tag:
 * </p>
 * <p>
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">resid</td>
 * <td>The resource ID. This resource will be resolved in the current locale to
 * obtain the value of the property to set.</td>
 * <td valign="top">no</td>
 * </tr>
 * <tr>
 * <td valign="top">resgrp</td>
 * <td>Defines the resource group, to which the resource ID belongs.</td>
 * <td valign="top">yes</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: LocalizedTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class LocalizedTag extends FormBaseTag
{
    /** Stores the resID. */
    private String resid;

    /** Stores the resource group. */
    private String resgrp;

    /**
     * Returns the resource group to use when resolving the resID.
     *
     * @return the resource group
     */
    public String getResgrp()
    {
        return resgrp;
    }

    /**
     * Setter method for the resgrp attribute.
     *
     * @param resgrp the attribute value
     */
    public void setResgrp(String resgrp)
    {
        this.resgrp = resgrp;
    }

    /**
     * Returns the resID to be resolved.
     *
     * @return the resID
     */
    public String getResid()
    {
        return resid;
    }

    /**
     * Setter method for the resid attribute.
     *
     * @param resid the attribute value
     */
    public void setResid(String resid)
    {
        this.resid = resid;
    }

    /**
     * Returns the resource value specified for this tag. If a resource ID is
     * specified, it is resolved using the current resource manager, and the
     * result is returned. Otherwise an exception will be thrown.
     *
     * @return the value of the property
     * @throws JellyTagException if an error occurs
     */
    protected Object fetchResourceValue() throws JellyTagException
    {
        if (getResid() == null)
        {
            throw new MissingAttributeException("resid");
        }

        TransformerContext ctx = getBuilderData().getTransformerContext();
        return ctx.getResourceManager().getResource(ctx.getLocale(),
                getResgrp(), getResid());
    }

    /**
     * Executes this tag. This implementation checks whether the parent tag
     * implements the {@link ValueSupport} interface. If this is the case,
     * {@link #fetchResourceValue()} is called to obtain the resource value. The
     * result of this method is passed to the parent tag.
     *
     * @throws JellyTagException if the tag is used incorrectly
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected void process() throws JellyTagException, FormBuilderException
    {
        if (!(getParent() instanceof ValueSupport))
        {
            throw new JellyTagException("<localized> tag must be in the body "
                    + "of a tag implementing the ValueSupport interface!");
        }

        ((ValueSupport) getParent()).setValue(fetchResourceValue());
    }
}
