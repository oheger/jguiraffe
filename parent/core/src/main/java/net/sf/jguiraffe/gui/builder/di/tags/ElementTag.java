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
import net.sf.jguiraffe.di.impl.providers.ConstantBeanProvider;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;

/**
 * <p>
 * A tag handler class that allows adding elements to collections.
 * </p>
 * <p>
 * Tags of this type can be placed in the body of a
 * <code>{@link CollectionTag}</code> tag. They define the elements to be added
 * to the collection. An example usage could be as follows:
 *
 * <pre>
 * &lt;list elementClassName=&quot;java.lang.Integer&quot;&gt;
 *   &lt;element value=&quot;10&quot;/&gt;
 *   &lt;element value=&quot;20&quot;/&gt;
 *   &lt;element refName=&quot;intBean&quot;/&gt;
 *   ...
 * &lt;/list&gt;
 * </pre>
 *
 * </p>
 * <p>
 * <code>ElementTag</code> is derived from <code>DependencyTag</code> and
 * inherits all possibilities for defining the value (e.g. passing the value
 * directly to the <code>value</code> attribute, specifying the value class with
 * support for automatic type conversions, defining the value as a complex
 * object in the tag's body, declaring a dependency to another bean, etc.). The
 * following table lists the supported attributes:
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">value</td>
 * <td>With this attribute the value can be directly set. If the value is
 * specified as a string constant and a value class is defined, an automatic
 * type conversion will be performed.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">valueClass</td>
 * <td>Here the class of the value can be specified. The tag will try to convert
 * the value to this class if necessary.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">valueClassName</td>
 * <td>This attribute has the same effect as <code>valueClass</code>, but the
 * name of the value class is specified rather than the class object itself.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">valueClassLoader</td>
 * <td>If the name of the value class is specified, with this attribute a class
 * loader for resolving the class can be defined. The name specified here will
 * be passed to the current {@link net.sf.jguiraffe.di.ClassLoaderProvider
 * ClassLoaderProvider} for obtaining the desired class loader.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">refName</td>
 * <td>With this attribute another bean can be referenced by its name. This bean
 * will be resolved and become the value of this tag.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">refClass</td>
 * <td>It is possible to refer to another bean by its class (for this purpose
 * there should only be a single bean with this class so there are no
 * ambiguities). The class of this bean can be specified by this attribute.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">refClassName</td>
 * <td>This attribute has the same meaning as the <code>refClass</code>
 * attribute, but the class of the bean that is referenced can be specified by
 * its name.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">refClassLoader</td>
 * <td>If the <code>refClassName</code> attribute is used for specifying the
 * class of a bean referenced, with this attribute a class loader can be
 * determined for resolving the class.</td>
 * <td valign="top">yes</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * It is also possible to define the tag's value using other tags that are
 * placed in the body of this tag. Only a single way of defining the value must
 * be used, otherwise an exception is thrown. If the value of this tag is
 * undefined, a <b>null</b> dependency will be added to the collection, i.e. the
 * corresponding element in the collection will be <b>null</b>.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ElementTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ElementTag extends DependencyTag
{
    /**
     * Executes this tag. Checks whether the parent tag is a collection tag. If
     * this is the case, the parent's <code>addElement()</code> method is called
     * with the dependency specified for this tag.
     *
     * @param output the output object
     * @throws JellyTagException if an error occurs or the tag is incorrectly
     *         used
     */
    public void doTag(XMLOutput output) throws JellyTagException
    {
        if (!(getParent() instanceof CollectionTag))
        {
            throw new JellyTagException(
                    "This tag must be nested in a CollectionTag!");
        }

        invokeBody(output);

        Dependency elemDependency;
        if (!hasDependency())
        {
            // no dependency definition => create a null dependency
            elemDependency = ConstantBeanProvider.getInstance(null);
        }
        else
        {
            elemDependency = getDependency();
        }

        ((CollectionTag) getParent()).addElement(elemDependency);
    }

    /**
     * Creates the dependency when a constant value is specified for this tag.
     * This implementation obtains the class description of the elements
     * specified at the collection level (if any). Then this description is used
     * as fall back for a type conversion.
     *
     * @return the dependency for a (constant) value
     * @throws JellyTagException if an error occurs
     */
    @Override
    protected Dependency createValueDependency() throws JellyTagException
    {
        assert getParent() instanceof CollectionTag : "Invalid parent tag!";
        CollectionTag colTag = (CollectionTag) getParent();

        return getValueData().createValueProvider(colTag.getElementClassDesc());
    }
}
