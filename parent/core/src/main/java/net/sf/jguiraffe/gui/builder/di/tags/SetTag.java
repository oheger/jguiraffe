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

import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.impl.providers.SetBeanProvider;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A specialized <code>CollectionTag</code> implementation for creating sets.
 * </p>
 * <p>
 * This tag handler class is very similar to {@link ListTag}, but it allows the
 * creation of a <code>java.util.Set</code> object rather than a list. The tag
 * can be placed in the body of a tag derived from {@link DependencyTag and sets
 * the value of its parent tag to the newly created set. Alternatively the {
 * @code name} attribute can be specified; in this case the set produced by
 * this name is put under this name in the current bean store. The following
 * code fragment shows an example of using this tag:
 *
 * <pre>
 *   &lt;constructor&gt;
 *     &lt;param&gt;
 *       &lt;set elementClassName=&quot;java.lang.Integer&quot; ordered=&quot;true&quot;&gt;
 *         &lt;element value=&quot;1&quot;/&gt;
 *         &lt;element value=&quot;2&quot;/&gt;
 *         &lt;element value=&quot;12345678&quot; valueClassName=&quot;java.lang.Long&quot;/&gt;
 *       &lt;/list&gt;
 *     &lt;/param&gt;
 *   &lt;/constructor&gt;
 * </pre>
 *
 * Here a set will be created and passed as argument into a constructor (this
 * fragment is part of a complex bean declaration). The <code>ordered</code>
 * attribute means that the set created by this tag will keep the order of its
 * elements, i.e. when iterating over the set the elements are returned in the
 * order as they were specified in the Jelly script.
 * </p>
 * <p>
 * The following table lists all attributes supported by this tag:
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">name</td>
 * <td>Using this attribute a name for the set bean can be specified. The bean
 * can then be queried from the current bean store by this name. If no name is
 * provided, an anonymous bean is created; in this case the tag must be nested
 * in the body of a {@link DependencyTag}.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">elementClass</td>
 * <td>Here the class of the elements contained can be specified. When elements
 * are added, the tag will try to convert the objects to this class if
 * necessary. If an {@link ElementTag} in the body of this tag defines a value
 * class, this class will be used thus overriding the element class specified at
 * the collection level. If neither the collection tag nor the element tag
 * specifies a class, no type conversion will be performed.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">elementClassName</td>
 * <td>This attribute has the same effect as <code>elementClass</code>, but the
 * name of the element class is specified rather than the class object itself.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">elementClassLoader</td>
 * <td>If the name of the element class is specified, with this attribute a
 * class loader for resolving the class can be defined. The name specified here
 * will be passed to the current {@link net.sf.jguiraffe.di.ClassLoaderProvider
 * ClassLoaderProvider} for obtaining the desired class loader.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">ordered</td>
 * <td>Determines whether the set produced by this tag will remain the order of
 * the elements added to it. If set to <b>true</b>, a
 * <code>java.util.LinkedHashSet</code> will be created. A value of <b>false</b>
 * (which is also the default) will create a <code>java.util.HashSet</code>,
 * which does not have a specific order of its elements.</td>
 * <td valign="top">yes</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SetTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class SetTag extends CollectionTag
{
    /** Stores the value of the ordered attribute. */
    private boolean ordered;

    /**
     * Returns the <code>ordered</code> attribute. This attribute determines the
     * type of the set created by this tag.
     *
     * @return the value of the <code>ordered</code> attribute
     */
    public boolean isOrdered()
    {
        return ordered;
    }

    /**
     * Sets the value of the <code>ordered</code> attribute. If set to
     * <b>true</b>, a <code>LinkedHashSet</code> will be created that remembers
     * the order of its elements.
     *
     * @param ordered the <code>ordered</code> attribute
     */
    public void setOrdered(boolean ordered)
    {
        this.ordered = ordered;
    }

    /**
     * Creates the bean provider representing the collection managed by this
     * tag. This implementation will create a {@link SetBeanProvider} object.
     *
     * @return the bean provider produced by this tag
     * @throws JellyTagException if an error occurs
     */
    @Override
    protected BeanProvider createBeanProvider() throws JellyTagException
    {
        return new SetBeanProvider(getElementDependencies(), isOrdered());
    }
}
