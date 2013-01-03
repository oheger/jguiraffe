/*
 * Copyright 2006-2012 The JGUIraffe Team.
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

import net.sf.jguiraffe.di.impl.Invokable;
import net.sf.jguiraffe.di.impl.MethodInvocation;
import net.sf.jguiraffe.di.impl.providers.MethodInvocationBeanProvider;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;

/**
 * <p>
 * A tag for defining a <em>bean factory</em> that is used as creation
 * <code>BeanProvider</code> for a {@link BeanTag}.
 * </p>
 * <p>
 * This tag handler class creates a
 * {@link MethodInvocationBeanProvider} and passes it to its
 * enclosing <code>BeanTag</code>. This way it is possible to use a kind of
 * factory for creating beans. The factory is defined by an optional dependency
 * to another bean and a required <code>MethodInvocation</code> defining the
 * method to be invoked on the factory; if no dependency is specified, the
 * <code>MethodInvocation</code> must refer to a static method. The method to
 * be invoked must be defined by a {@link MethodInvocationTag} in
 * the body of this tag. The optional dependency is specified through the
 * attributes of this tag:
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
 * <td>Specifies a dependency to another bean. On this bean the factory method
 * will be invoked.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">refClass</td>
 * <td>Specifies a dependency to another bean by its class. On this bean the
 * factory method will be invoked.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">refClassName</td>
 * <td>Specifies a dependency to another bean by its class name. On this bean
 * the factory method will be invoked.</td>
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
 * <td>If the factory object is a constant object, this attribute can be used.
 * It allows to directly specify the value.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">valueClass</td>
 * <td>If a constant value is to be used for the factory object, it may be
 * necessary to perform some type conversion. With this attribute the type of
 * the factory can be specified. The value will then be converted to this type.</td>
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
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FactoryTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class FactoryTag extends DependencyTag implements InvokableSupport
{
    /** Stores a reference to the enclosing bean tag. */
    private BeanTag beanTag;

    /** A flag whether an <code>Invokable</code> object has been added. */
    private boolean invokableAdded;

    /**
     * Returns a reference to the enclosing <code>BeanTag</code>.
     *
     * @return the enclosing bean tag
     */
    public BeanTag getBeanTag()
    {
        return beanTag;
    }

    /**
     * The main method of this tag. Delegates to the processing methods.
     *
     * @param output the output object
     * @throws JellyTagException if an error occurs
     */
    public void doTag(XMLOutput output) throws JellyTagException
    {
        if (getParent() instanceof BeanTag)
        {
            setBeanTag((BeanTag) getParent());
        }

        processBeforeBody();
        invokeBody(output);
        process();
    }

    /**
     * Adds an <code>Invokable</code> to this object. This implementation
     * expects that a single {@link MethodInvocation} object is passed, which
     * will be used for creating a {@link MethodInvocationBeanProvider}.
     *
     * @param inv the <code>Invokable</code> to be added
     * @throws JellyTagException if the passed in object is not accepted
     */
    public void addInvokable(Invokable inv) throws JellyTagException
    {
        if (!(inv instanceof MethodInvocation))
        {
            throw new JellyTagException(
                    "Only MethodInvocation objects are supported!");
        }
        if (invokableAdded)
        {
            throw new JellyTagException("Only a single Invokable can be added!");
        }

        assert getBeanTag() != null : "No bean tag is set!";
        if (getBeanTag().getBeanClassDesc() == null)
        {
            throw new JellyTagException(
                    "A bean class must be defined for the bean tag "
                            + "when using a factory tag!");
        }

        MethodInvocationBeanProvider creator = new MethodInvocationBeanProvider(
                hasDependency() ? getDependency() : null,
                (MethodInvocation) inv, getBeanTag().getBeanClassDesc());
        getBeanTag().setBeanCreator(creator);
        invokableAdded = true;
    }

    /**
     * Initializes the reference to the enclosing <code>BeanTag</code>. This
     * method is called by the tag's main method if a <code>BeanTag</code> can
     * be found.
     *
     * @param beanTag the enclosing <code>BeanTag</code>
     */
    protected void setBeanTag(BeanTag beanTag)
    {
        this.beanTag = beanTag;
    }

    /**
     * Performs some pre-processing before the tag's body is evaluated.
     * Registers the tag as an <code>InvokableSupport</code> object.
     *
     * @throws JellyTagException if the tag is incorrectly used
     */
    protected void processBeforeBody() throws JellyTagException
    {
        if (getBeanTag() == null)
        {
            throw new JellyTagException(
                    "This tag must be nested inside a BeanTag!");
        }
        invokableAdded = false;
        InvocationData.get(getContext()).registerInvokableSupport(this);
    }

    /**
     * The main processing method. This method is invoked after the tag's body
     * has been processed. It performs some validity checks.
     *
     * @throws JellyTagException if the tag is incorrectly used
     */
    protected void process() throws JellyTagException
    {
        InvocationData.get(getContext()).unregisterInvokableSupport();
        if (!invokableAdded)
        {
            throw new JellyTagException("No MethodInvocation object was added!");
        }
    }
}
