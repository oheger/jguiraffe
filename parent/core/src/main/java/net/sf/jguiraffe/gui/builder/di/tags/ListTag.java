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
import net.sf.jguiraffe.di.impl.providers.ListBeanProvider;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A specialized <code>CollectionTag</code> implementation for creating lists.
 * </p>
 * <p>
 * With this tag handler class list objects can be created that can be passed to
 * other tags supporting a value (the tag can be placed in the body of each tag
 * handler class derived from {@link DependencyTag}). Alternatively, the
 * {@code name} attribute can be specified; in this case the list bean can be
 * directly queried from the current bean store using this name. The content of
 * the list is defined by {@link ElementTag} tags in the body of this tag. It is
 * possible to define the type of the list elements as attributes. Alternatively
 * the <code>ElementTag</code> tags can have a class definition, which will
 * override the one set at this tag.
 * </p>
 * <p>
 * The following example fragment shows how this tag can be used to define a
 * list that will be passed as an argument of a constructor invocation:
 *
 * <pre>
 *   &lt;constructor&gt;
 *     &lt;param&gt;
 *       &lt;list elementClassName=&quot;java.lang.Integer&quot;&gt;
 *         &lt;element value=&quot;1&quot;/&gt;
 *         &lt;element value=&quot;2&quot;/&gt;
 *         &lt;element value=&quot;3&quot;/&gt;
 *       &lt;/list&gt;
 *     &lt;/param&gt;
 *   &lt;/constructor&gt;
 * </pre>
 *
 * </p>
 * <p>
 * <code>ListTag</code> supports the following attributes:
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">name</td>
 * <td>Using this attribute a name for the list bean can be specified. The bean
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
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ListTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ListTag extends CollectionTag
{
    /**
     * Creates the bean provider representing the collection managed by this
     * tag. This implementation will create a {@link ListBeanProvider} object.
     *
     * @return the bean provider produced by this tag
     * @throws JellyTagException if an error occurs
     */
    @Override
    protected BeanProvider createBeanProvider() throws JellyTagException
    {
        return new ListBeanProvider(getElementDependencies());
    }
}
