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
package net.sf.jguiraffe.gui.builder.components.tags;

import java.util.MissingResourceException;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.Composite;
import net.sf.jguiraffe.gui.builder.components.ContainerSelector;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.components.FormBuilderRuntimeException;
import net.sf.jguiraffe.gui.layout.NumberWithUnit;
import net.sf.jguiraffe.transform.TransformerContext;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.util.ClassLoaderUtils;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * A base class for tags of the form builder framework.
 * </p>
 * <p>
 * This class implements some common functionality used by form builder tags.
 * Especially conditional execution and access to the central builder data
 * object is implemented here.
 * </p>
 * <p>
 * Many operations are available both in an instance and a static form. This
 * makes it easy for other tag classes that can not inherit from this class to
 * make use of the provided functionality.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FormBaseTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class FormBaseTag extends TagSupport implements ConditionalTag
{
    /** Stores the value of the ifName attribute. */
    private String ifName;

    /** Stores the value of the unlessName attribute. */
    private String unlessName;

    /**
     * Returns the ifName attribute.
     *
     * @return the value of the ifName attribute
     */
    public String getIfName()
    {
        return ifName;
    }

    /**
     * Sets the value of the ifName attribute. This attribute is used by the
     * mechanism for conditional execution. This method is called by jelly.
     *
     * @param s the attribute value
     */
    public void setIfName(String s)
    {
        ifName = s;
    }

    /**
     * Returns the unlessName attribute.
     *
     * @return the value of the unlessName attribute
     */
    public String getUnlessName()
    {
        return unlessName;
    }

    /**
     * Sets the value of the unlessName attribute. This attribute is used by the
     * mechanism for conditional execution. This method is called by jelly.
     *
     * @param s the attribute value
     */
    public void setUnlessName(String s)
    {
        unlessName = s;
    }

    /**
     * The main method of this tag class. This implementation checks if the tag
     * can be executed (by calling {@link #canProcess()}. If
     * this is the case, control is passed to the
     * {@link #process()} method.
     *
     * @param output the output object (ignored because form builder tags do not
     * produce any output)
     * @throws JellyTagException if any exception occurs
     */
    public void doTag(XMLOutput output) throws JellyTagException
    {
        if (canProcess())
        {
            try
            {
                processBeforeBody();
                // invoke body, which may cause additional properties to be set
                invokeBody(output);
                process();
            }
            catch (FormBuilderException fex)
            {
                throw new JellyTagException(fex);
            }
        }
    }

    /**
     * Returns the current {@link ComponentBuilderData} object from the context.
     *
     * @param context the jelly context
     * @return the builder data
     */
    public static ComponentBuilderData getBuilderData(JellyContext context)
    {
        return ComponentBuilderData.get(context);
    }

    /**
     * Tests if the specified conditional tag should be executed. This method
     * determines based on the values of the ifName and unlessName attributes
     * whether execution is allowed. If the ifName attribute is defined,
     * execution is only allowed if the current builder's name exactly matches
     * the attribute value. If the unlessName attribute is defined, then the tag
     * is only executed if the current builder's name does not match the
     * attribute's value. (If both attributes are defined, the ifName attribute
     * takes precedence.)
     *
     * @param tag the tag
     * @return a flag if the tag can be executed
     */
    public static boolean canProcess(ConditionalTag tag)
    {
        if (!StringUtils.isEmpty(tag.getIfName()))
        {
            return StringUtils.equals(tag.getIfName(), getBuilderData(
                    tag.getContext()).getBuilderName());
        }
        if (!StringUtils.isEmpty(tag.getUnlessName()))
        {
            return !StringUtils.equals(tag.getUnlessName(), getBuilderData(
                    tag.getContext()).getBuilderName());
        }

        // No attributes defined: execute always
        return true;
    }

    /**
     * Resolves the specified resource. This is a convenience method for builder
     * components that support i11n.
     *
     * @param context the actual jelly context
     * @param resGrp the resource group
     * @param resId the resource ID
     * @return the text of the resource
     * @throws FormBuilderRuntimeException if the resource cannot be resolved
     */
    public static String getResourceText(JellyContext context, Object resGrp,
            Object resId) throws FormBuilderRuntimeException
    {
        try
        {
            TransformerContext tctx = getBuilderData(context)
                    .getTransformerContext();
            return tctx.getResourceManager().getText(
                    tctx.getLocale(),
                    (resGrp != null) ? resGrp : getBuilderData(context)
                            .getDefaultResourceGroup(), resId);
        }
        catch (MissingResourceException mex)
        {
            throw new FormBuilderRuntimeException(mex);
        }
    }

    /**
     * Helper method for converting attributes of type class. Jelly does not
     * itself directly support class attributes, so this is handled by this
     * helper method. The passed in object can either be directly of type class
     * or it is interpreted as the name of a class. In the latter case the class
     * name is resolved and the class object is returned.
     *
     * @param clsObj an object specifying a class
     * @return the resolved class object
     * @throws FormBuilderRuntimeException if the class cannot be resolved
     */
    public static Class<?> convertToClass(Object clsObj)
            throws FormBuilderRuntimeException
    {
        if (clsObj == null)
        {
            throw new IllegalArgumentException("Class object must not be null!");
        }
        if (clsObj instanceof Class<?>)
        {
            return (Class<?>) clsObj;
        }
        else
        {
            try
            {
                return ClassLoaderUtils.loadClass(clsObj.toString(),
                        FormBaseTag.class);
            }
            catch (ClassNotFoundException cex)
            {
                throw new FormBuilderRuntimeException(
                        "Could not resolve class " + clsObj, cex);
            }
        }
    }

    /**
     * Converts a string attribute into the corresponding number with unit. If
     * this fails, an exception will be thrown.
     *
     * @param s the string
     * @return the converted number (<b>null</b> if the string is undefined)
     * @throws FormBuilderException if conversion fails
     */
    protected static NumberWithUnit convertToNumberWithUnit(String s)
            throws FormBuilderException
    {
        if (s == null)
        {
            return null;
        }
        else
        {
            try
            {
                return new NumberWithUnit(s);
            }
            catch (IllegalArgumentException iex)
            {
                throw new FormBuilderException("Invalid number declaration!",
                        iex);
            }
        }
    }

    /**
     * Converts a string attribute into the corresponding {@link NumberWithUnit}
     * returning the default value if the attribute has not been set. If the
     * conversion fails, an exception is thrown.
     *
     * @param s the string to be converted
     * @param defValue the default value
     * @return the converted number
     * @throws FormBuilderException if conversion fails
     */
    protected static NumberWithUnit convertToNumberWithUnit(String s,
            NumberWithUnit defValue) throws FormBuilderException
    {
        NumberWithUnit result = convertToNumberWithUnit(s);
        return (result == null) ? defValue : result;
    }

    /**
     * Returns the current <code>{@link ComponentBuilderData}</code> object for this
     * builder operation.
     *
     * @return the builder data
     */
    protected ComponentBuilderData getBuilderData()
    {
        return getBuilderData(getContext());
    }

    /**
     * Convenience method for resolving resources.
     *
     * @param resGrp the resource group
     * @param resId the resource ID
     * @return the text of the specified resource
     * @throws FormBuilderRuntimeException if the resource cannot be resolved
     */
    protected String getResourceText(Object resGrp, Object resId)
            throws FormBuilderRuntimeException
    {
        return getResourceText(getContext(), resGrp, resId);
    }

    /**
     * Tests whether this tag should be executed. Evaluates the attributes for
     * conditional execution.
     *
     * @return a flag if this tag should be executed
     * @see #canProcess(ConditionalTag)
     */
    protected boolean canProcess()
    {
        return canProcess(this);
    }

    /**
     * Tries to determine the container to which this component should be added.
     * This implementation first looks for an enclosing {@code <container>} tag
     * (i.e. a tag implementing the {@link Composite} interface. If this is
     * found, the container object defined there is used (determined by the
     * current {@link ContainerSelector}). Otherwise the method assumes that the
     * new component should be added to the top level container, which can be
     * obtained from the builder data object.
     *
     * @return the container object
     */
    public Composite findContainer()
    {
        Composite result = (Composite) findAncestorWithClass(Composite.class);
        return (result != null) ? getBuilderData().getContainerSelector()
                .getComposite(result) : getBuilderData();
    }

    /**
     * Performs processing of this tag before its body is evaluated. This can be
     * necessary if some initialization has to be performed first which is
     * required by nested tags. This method is automatically called by
     * {@code doTag()}. This base implementation is left empty.
     *
     * @throws JellyTagException if a jelly specific error occurs
     * @throws FormBuilderException if an error in the builder framework occurs
     */
    protected void processBeforeBody() throws JellyTagException, FormBuilderException
    {
    }

    /**
     * Executes this tag. Here concrete sub classes must place their tag logic.
     * This method is invoked by {@code doTag()} if execution is allowed.
     *
     * @throws JellyTagException if a jelly specific error occurs
     * @throws FormBuilderException if an error in the builder framework occurs
     */
    protected abstract void process() throws JellyTagException, FormBuilderException;
}
