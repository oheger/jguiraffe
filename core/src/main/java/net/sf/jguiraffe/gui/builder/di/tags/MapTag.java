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

import java.util.Collection;
import java.util.LinkedList;

import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.impl.ClassDescription;
import net.sf.jguiraffe.di.impl.providers.MapBeanProvider;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A specialized <code>BeanTag</code> implementation for creating maps.
 * </p>
 * <p>
 * This tag handler class can be placed either in the body of an arbitrary tag
 * derived from {@link DependencyTag}. It will then create a map and pass it to
 * the enclosing {@link DependencyTag}. Alternatively, the {@code name}
 * attribute can be specified, then the map bean is directly stored in the
 * corresponding {@code BeanStore}. The map can be populated by
 * <code>&lt;entry&gt;</code> tags in the body of this tag. The type of the key
 * and the value of the map entries can be specified as attributes. A typical
 * example of using this tag could look as follows:
 * </p>
 * <p>
 *
 * <pre>
 *   &lt;constructor&gt;
 *     &lt;param&gt;
 *       &lt;map keyClassName=&quot;java.lang.String&quot;
 *         valueClassName=&quot;java.lang.Integer&quot;&gt;
 *         &lt;entry key=&quot;k1&quot; value=&quot;1&quot;/&gt;
 *         &lt;entry key=&quot;k2&quot; value=&quot;2&quot;/&gt;
 *         &lt;entry key=&quot;k3&quot; value=&quot;3&quot;/&gt;
 *       &lt;/map&gt;
 *     &lt;/param&gt;
 *   &lt;/constructor&gt;
 * </pre>
 *
 * Here a map is created, populated with some key value pairs and passed as
 * argument to a constructor.
 * </p>
 * <p>
 * <code>MapTag</code> supports the following attributes:
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">name</td>
 * <td>Using this attribute a name for the map bean can be specified. The bean
 * can then be queried from the current bean store by this name. If no name is
 * provided, an anonymous bean is created; in this case the tag must be nested
 * in the body of a {@link DependencyTag}.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">keyClass</td>
 * <td>Here the class of the keys of the map entries can be specified. When
 * elements are added, the tag will try to convert the key objects to this class
 * if necessary. If an {@link EntryTag} in the body of this tag defines a key
 * class, this class will be used thus overriding the key class specified at the
 * map level. If neither the map tag nor the entry tag specifies a key class, no
 * type conversion will be performed.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">keyClassName</td>
 * <td>This attribute has the same effect as <code>keyClass</code>, but the name
 * of the key class is specified rather than the class object itself.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">keyClassLoader</td>
 * <td>If the name of the key class is specified, with this attribute a class
 * loader for resolving the class can be defined. The name specified here will
 * be passed to the current {@link net.sf.jguiraffe.di.ClassLoaderProvider
 * ClassLoaderProvider} for obtaining the desired class loader.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">valueClass</td>
 * <td>With this attribute the class of the map values can be specified. This is
 * analogous to the <code>keyClass</code> attribute, but defines a default class
 * for the values stored in the map.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">valueClassName</td>
 * <td>Analogous to <code>valueClass</code>, but the name of the value class is
 * specified.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">valueClassLoader</td>
 * <td>Defines the class loader to be used for resolving the value class when
 * only the name is specified. This attribute has the same meaning as the
 * <code>keyClassLoader</code> attribute for the value class.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">ordered</td>
 * <td>Per default this tag will create a <code>java.util.HashMap</code> object
 * that does not maintain a specific order of its elements. If this attribute is
 * set to <b>true</b>, an instance of <code>java.util.LinkedHashMap</code> will
 * be created instead. Then the map will remember the order in which its
 * elements have been added.</td>
 * <td valign="top">yes</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: MapTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class MapTag extends AbstractBeanTag
{
    /** The data object for the key class. */
    private ClassDescData keyClassData;

    /** The class description for the key class. */
    private ClassDescription keyClassDesc;

    /** The data object for the value class. */
    private ClassDescData valueClassData;

    /** The class description for the value class. */
    private ClassDescription valueClassDesc;

    /** A collection for storing the key dependencies. */
    private Collection<Dependency> keyDependencies;

    /** A collection for storing the value dependencies. */
    private Collection<Dependency> valueDependencies;

    /** The name of the map bean produced by this tag. */
    private String name;

    /** The ordered attribute. */
    private boolean ordered;

    /**
     * Creates a new instance of <code>MapTag</code>.
     */
    public MapTag()
    {
        keyClassData = new ClassDescData();
        valueClassData = new ClassDescData();
        keyDependencies = new LinkedList<Dependency>();
        valueDependencies = new LinkedList<Dependency>();
    }

    /**
     * Returns the name under which the map bean is to be stored in the bean
     * store.
     *
     * @return the name of the map bean
     */
    @Override
    public String getName()
    {
        return name;
    }

    /**
     * Set method of the {@code name} attribute.
     *
     * @param name the attribute's value
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the <code>ClassDescData</code> object for the key class. This
     * object is populated by corresponding set methods for the key class or its
     * name.
     *
     * @return the data object for defining the key class
     */
    public ClassDescData getKeyClassData()
    {
        return keyClassData;
    }

    /**
     * Sets the class of the keys of this map.
     *
     * @param cls the key class
     */
    public void setKeyClass(Class<?> cls)
    {
        getKeyClassData().setTargetClass(cls);
    }

    /**
     * Sets the name of the class for the keys of this map.
     *
     * @param clsName the name of the key class
     */
    public void setKeyClassName(String clsName)
    {
        getKeyClassData().setTargetClassName(clsName);
    }

    /**
     * Specifies the class loader to be used for the resolving the key class.
     *
     * @param loaderName the name of the class loader
     */
    public void setKeyClassLoader(String loaderName)
    {
        getKeyClassData().setClassLoaderName(loaderName);
    }

    /**
     * Returns the class description for the key class. This object is
     * initialized from the <code>ClassDescData</code> for the key class before
     * the tag's body is executed. It may be <b>null</b> if no value class is
     * specified.
     *
     * @return the class description for the key class
     */
    public ClassDescription getKeyClassDesc()
    {
        return keyClassDesc;
    }

    /**
     * Returns the <code>ClassDescData</code> object for the value class.
     *
     * @return the data object for defining the value class
     */
    public ClassDescData getValueClassData()
    {
        return valueClassData;
    }

    /**
     * Sets the class of the values stored in this map.
     *
     * @param cls the value class
     */
    public void setValueClass(Class<?> cls)
    {
        getValueClassData().setTargetClass(cls);
    }

    /**
     * Sets the name of the class of the values stored in this map.
     *
     * @param clsName the name of the value class
     */
    public void setValueClassName(String clsName)
    {
        getValueClassData().setTargetClassName(clsName);
    }

    /**
     * Specifies the class loader to be used for the resolving the value class.
     *
     * @param loaderName the name of the class loader
     */
    public void setValueClassLoader(String loaderName)
    {
        getValueClassData().setClassLoaderName(loaderName);
    }

    /**
     * Returns the class description for the value class. This object is
     * initialized before the tag's body gets executed. It may be <b>null</b> if
     * no value class is specified.
     *
     * @return the class description for the value class
     */
    public ClassDescription getValueClassDesc()
    {
        return valueClassDesc;
    }

    /**
     * Returns the value of the ordered attribute.
     *
     * @return the ordered attribute
     */
    public boolean isOrdered()
    {
        return ordered;
    }

    /**
     * Sets the value of the ordered attribute. This attribute determines the
     * type of the map created by this tag: if set to <b>true</b>, a linked map
     * is created; the default value of <b>false</b> will cause a plain hash map
     * to be created.
     *
     * @param ordered the value of the ordered attribute
     */
    public void setOrdered(boolean ordered)
    {
        this.ordered = ordered;
    }

    /**
     * Adds a new entry to this map. This method is intended to be called by
     * tags in the body of this tag. Key and value are passed in as
     * dependencies. Their actual values are resolved when the map bean is
     * created.
     *
     * @param depKey the dependency for the key (must not be <b>null</b>)
     * @param depValue the dependency for the value (must not be <b>null</b>)
     * @throws IllegalArgumentException if the key or the value dependency is
     *         <b>null</b>
     */
    public void addEntry(Dependency depKey, Dependency depValue)
    {
        if (depKey == null)
        {
            throw new IllegalArgumentException(
                    "Key dependency must not be null!");
        }
        if (depValue == null)
        {
            throw new IllegalArgumentException(
                    "Value dependency must not be null!");
        }

        getKeyDependencies().add(depKey);
        getValueDependencies().add(depValue);
    }

    /**
     * Returns the collection with the key dependencies.
     *
     * @return the key dependencies
     */
    protected Collection<Dependency> getKeyDependencies()
    {
        return keyDependencies;
    }

    /**
     * Returns the collection with the value dependencies.
     *
     * @return the value dependencies
     */
    protected Collection<Dependency> getValueDependencies()
    {
        return valueDependencies;
    }

    /**
     * Performs some processing before the body gets executed. This
     * implementation will initialize the class descriptions for the key and
     * value class.
     *
     * @throws JellyTagException if the tag is incorrectly used
     */
    @Override
    protected void processBeforeBody() throws JellyTagException
    {
        super.processBeforeBody();
        keyClassDesc = getKeyClassData().getOptionalClassDescription();
        valueClassDesc = getValueClassData().getOptionalClassDescription();
    }

    /**
     * Creates the <code>BeanProvider</code> produced by this tag. This
     * implementation creates a {@link MapBeanProvider} and initialize it with
     * the dependencies for the keys and values that have been added so far.
     *
     * @return the <code>BeanProvider</code> produced by this tag
     * @throws JellyTagException if an error occurs
     */
    @Override
    protected BeanProvider createBeanProvider() throws JellyTagException
    {
        return new MapBeanProvider(getKeyDependencies(),
                getValueDependencies(), isOrdered());
    }
}
