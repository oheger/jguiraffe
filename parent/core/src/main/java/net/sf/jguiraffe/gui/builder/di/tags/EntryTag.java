/*
 * Copyright 2006-2018 The JGUIraffe Team.
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
 * A tag handler implementation for populating a map.
 * </p>
 * <p>
 * Tags of this type can be placed in the body of a {@link MapTag}. They define
 * both the key and the value of an entry. By inheriting from
 * {@link DependencyTag} the usual ways of defining a value are available. It is
 * even possible to place a {@link BeanTag} in the body of this tag to create a
 * complex value object (that way for instance a collection can be created as
 * value). For the key there are the typical set methods for specifying the key
 * object and optionally a class. If the key is a complex object, the
 * {@link EntryKeyTag} class must be used in the body of this tag.
 * </p>
 * <p>
 * The most simple use case is a tag defining the key and value directly as in
 * the following fragment:
 *
 * <pre>
 * &lt;entry key=&quot;answer&quot; value=&quot;42&quot;/&gt;
 * </pre>
 *
 * Here the data defined for the key and the value is directly passed to the
 * owning {@link MapTag}. If this tag defines key and value classes, these
 * classes will be used for performing a type conversion if needed. It is also
 * possible to specify the desired classes using attributes of this tag:
 *
 * <pre>
 * &lt;entry key=&quot;answer&quot; value=&quot;42&quot;
 *   valueClassName=&quot;java.lang.Long&quot;/&gt;
 * </pre>
 *
 * Classes for key or value defined at the <code>EntryTag</code> level will
 * override the settings of the {@link MapTag}. If the value is a complex value,
 * it can be defined in the tag's body:
 *
 * <pre>
 * &lt;entry key=&quot;answers&quot;&gt;
 *   &lt;list&gt;
 *     &lt;element value=&quot;42&quot;/&gt;
 *     &lt;element value=&quot;4711&quot;/&gt;
 *     &lt;element value=&quot;0815&quot;/&gt;
 *   &lt;/list&gt;
 * &lt;/entry&gt;
 * </pre>
 *
 * If the key is a complex value, a nested {@link EntryKeyTag} must be used as
 * in the following example:
 *
 * <pre>
 * &lt;entry&gt;
 *   &lt;entryKey&gt;
 *     &lt;list&gt;
 *       &lt;element value=&quot;answer&quot;/&gt;
 *       &lt;element value=&quot;universe&quot;/&gt;
 *     &lt;/list&gt;
 *   &lt;/entryKey&gt;
 *   &lt;list&gt;
 *     &lt;element value=&quot;42&quot;/&gt;
 *     &lt;element value=&quot;?&quot;/&gt;
 *   &lt;/list&gt;
 * &lt;/entry&gt;
 * </pre>
 *
 * This would create a map entry which uses lists as key and value.
 * </p>
 * <p>
 * The following table lists all attributes supported by this tag:
 * <table * border="1">
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
 * <tr>
 * <td valing="top">key</td>
 * <td>Sets the key of the entry directly. This is analogous to the
 * <code>value</code> attribute for the key value.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">keyClass</td>
 * <td>Here the class of the key can be specified. This is analogous to the
 * <code>valueClass</code> attribute for the key.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">keyClassName</td>
 * <td>Defines the name of the class of the key. This is analogous to the
 * <code>valueClassName</code> attribute for the key.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">keyClassLoader</td>
 * <td>Allows specifying the name of a class loader to be used for resolving the
 * key class. This is analogous to the <code>valueClassLoader</code> attribute
 * for the key.</td>
 * <td valign="top">yes</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: EntryTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class EntryTag extends DependencyTag
{
    /** The data about key of the entry. */
    private ValueData keyData;

    /** Stores the final dependency for the key. */
    private Dependency keyDependency;

    /**
     * Creates a new instance of <code>EntryTag</code>.
     */
    public EntryTag()
    {
        keyData = new ValueData(this);
    }

    /**
     * Returns the dependency for the key of the map entry.
     *
     * @return the dependency for the key
     */
    public Dependency getKeyDependency()
    {
        return keyDependency;
    }

    /**
     * Sets the dependency for the key of the map entry. This method can be
     * called by tags in the body of this tag to set a complex dependency for
     * the entry's key.
     *
     * @param keyDependency the dependency for the key
     */
    public void setKeyDependency(Dependency keyDependency)
    {
        this.keyDependency = keyDependency;
    }

    /**
     * Returns the data object for the value of the key.
     *
     * @return the data for the value
     */
    public ValueData getKeyData()
    {
        return keyData;
    }

    /**
     * Sets the key of this entry. This method can be used as attribute setter
     * or be called by tag handler classes in the body of this tag.
     *
     * @param key the value of the key of this entry
     */
    public void setKey(Object key)
    {
        getKeyData().setValue(key);
    }

    /**
     * Set method for the keyClass attribute.
     *
     * @param cls the value of the attribute
     */
    public void setKeyClass(Class<?> cls)
    {
        getKeyData().setValueClass(cls);
    }

    /**
     * Set method for the keyClassName attribute.
     *
     * @param clsName the value of the attribute
     */
    public void setKeyClassName(String clsName)
    {
        getKeyData().setValueClassName(clsName);
    }

    /**
     * Set method for the keyClassLoader attribute.
     *
     * @param loader the value of the attribute
     */
    public void setKeyClassLoader(String loader)
    {
        getKeyData().setValueClassLoader(loader);
    }

    /**
     * Executes this tag. This implementation checks whether this tag is in the
     * body of a <code>{@link MapTag}</code>. If this is the case, the
     * <code>addEntry()</code> method of the map tag will be called.
     *
     * @param output the output object
     * @throws JellyTagException if an error occurs or the tag is incorrectly
     *         used
     */
    public void doTag(XMLOutput output) throws JellyTagException
    {
        if (!(getParent() instanceof MapTag))
        {
            throw new JellyTagException(
                    "EntryTag must be placed in the body of a MapTag!");
        }
        MapTag mapTag = (MapTag) getParent();

        // if a value for the key is defined, create the dependency
        if (getKeyData().isValueDefined())
        {
            setKeyDependency(getKeyData().createValueProvider(
                    mapTag.getKeyClassDesc()));
        }

        invokeBody(output);

        Dependency depKey;
        Dependency depValue;
        depKey = (getKeyDependency() != null) ? getKeyDependency()
                : ConstantBeanProvider.NULL;
        depValue = hasDependency() ? getDependency()
                : ConstantBeanProvider.NULL;
        mapTag.addEntry(depKey, depValue);
    }

    /**
     * Creates the dependency if a value is defined for this tag. This
     * implementation ensures that a class description for the value specified
     * at the map level will be used as default if none is provided for the
     * entry level.
     *
     * @return the dependency for the value
     * @throws JellyTagException in case of an error
     */
    @Override
    protected Dependency createValueDependency() throws JellyTagException
    {
        assert getParent() instanceof MapTag : "Invalid parent tag!";
        MapTag mapTag = (MapTag) getParent();

        return getValueData().createValueProvider(mapTag.getValueClassDesc());
    }
}
