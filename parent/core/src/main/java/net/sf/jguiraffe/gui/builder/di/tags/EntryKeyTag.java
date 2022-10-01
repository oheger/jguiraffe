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

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;

/**
 * <p>
 * A tag handler class for defining the key of an <code>{@link EntryTag}</code>.
 * </p>
 * <p>
 * When defining maps with the dependency injection framework the {@link MapTag}
 * together with the {@link EntryTag} are used. However if the map uses complex
 * objects as keys, it is not directly possible to set the key value with the
 * <code>EntryTag</code> (for the value this is no problem because an arbitrary
 * dependency - including an {@link AbstractBeanTag} in the body of the
 * <code>EntryTag</code> - can be defined for specifying the desired value).
 * </p>
 * <p>
 * With this tag the same is possible for the key. When it gets executed the tag
 * checks whether its parent is an <code>EntryTag</code>. By inheriting from
 * {@link DependencyTag} all possibilities of defining values are supported,
 * including the usage of complex value tags in this tag's body. The following
 * example shows how an entry for a map can be created whose key is a list:
 *
 * <pre>
 * &lt;map&gt;
 *   &lt;entry value=&quot;42&quot;&gt;
 *     &lt;entryKey&gt;
 *       &lt;list elementClass=&quot;java.lang.Integer&quot;&gt;
 *         &lt;element value=&quot;10&quot;/&gt;
 *         &lt;element value=&quot;20&quot;/&gt;
 *       &lt;/list&gt;
 *     &lt;/entryKey&gt;
 *   &lt;/entry&gt;
 * &lt;/map&gt;
 * </pre>
 *
 * Of course it is possible defining both the key and the value as complex
 * objects in the body of the <code>EntryTag</code>. The next fragment shows the
 * definition of an entry with a list as key and a set as value:
 *
 * <pre>
 * &lt;entry&gt;
 *   &lt;entryKey&gt;
 *     &lt;list elementClass=&quot;java.lang.Integer&quot;&gt;
 *       &lt;element value=&quot;10&quot;/&gt;
 *       &lt;element value=&quot;20&quot;/&gt;
 *     &lt;/list&gt;
 *   &lt;/entryKey&gt;
 *   &lt;set&gt;
 *     &lt;element value=&quot;ten&quot;/&gt;
 *     &lt;element value=&quot;twenty&quot;/&gt;
 *   &lt;/set&gt;
 * &lt;/entry&gt;
 * </pre>
 *
 * </p>
 * <p>
 * No matter how (either directly using the <code>value</code> attribute or by a
 * tag in the body), a value has to be defined for this tag, otherwise an
 * exception will be thrown. The following table lists all attributes supported
 * by this tag (these are the typical attributes of a {@link DependencyTag}:
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
 *
 * @author Oliver Heger
 * @version $Id: EntryKeyTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class EntryKeyTag extends DependencyTag
{
    /**
     * Executes this tag. Checks whether the parent tag is an
     * <code>{@link EntryTag}</code>. If this is the case, the key value will be
     * set based on the attributes defined for this tag.
     *
     * @param output the output object
     * @throws JellyTagException if the tag is incorrectly used or an error
     *         occurs
     */
    public void doTag(XMLOutput output) throws JellyTagException
    {
        if (!(getParent() instanceof EntryTag))
        {
            throw new JellyTagException(
                    "EntryKeyTag must be nested inside an EntryTag!");
        }
        EntryTag entry = (EntryTag) getParent();

        invokeBody(output);

        if (entry.getKeyDependency() != null)
        {
            throw new JellyTagException("EntryTag has already a key defined!");
        }
        entry.setKeyDependency(getDependency());
    }
}
