/*
 * Copyright 2006-2017 The JGUIraffe Team.
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
import java.util.Collections;
import java.util.LinkedList;

import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.impl.ClassDescription;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A base tag handler class for defining collections in bean builder scripts.
 * </p>
 * <p>
 * This base class provides basic functionality for maintaining a collection
 * with the dependencies representing the elements of the collection to be
 * created. It defines an <code>addElement()</code> method to be called by tags
 * in the body of this tag for adding new element dependencies to the
 * collection. The type of the elements to add can be specified by attributes in
 * this class. When adding new elements a type conversion will be performed if
 * necessary. It is also possible to override the data type when adding a new
 * element. This way collections with heterogeneous content can be created.
 * However, the logic for type conversions is implemented by the tag handler
 * classes that invoke the <code>addElement()</code> method. This base class
 * also supports a {@code name} attribute. So both named and anonymous beans
 * can be created.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: CollectionTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class CollectionTag extends AbstractBeanTag
{
    /** Stores information about the class of the collection's elements. */
    private ClassDescData elementClassData;

    /** A class description for the collection's elements. */
    private ClassDescription elementClassDesc;

    /** A collection with the dependencies representing the elements. */
    private Collection<Dependency> dependencies;

    /** The name of the bean created by this tag. */
    private String name;

    /**
     * Creates a new instance of <code>CollectionTag</code>.
     */
    protected CollectionTag()
    {
        elementClassData = new ClassDescData();
        dependencies = new LinkedList<Dependency>();
    }

    /**
     * Returns the name of the bean produced by this tag.
     *
     * @return the bean name
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
     * Returns the object with the class definition data of the collection's
     * elements. This object is filled by the set methods for the attributes
     * related to the element class.
     *
     * @return the class definition data for the element class of the collection
     */
    public ClassDescData getElementClassData()
    {
        return elementClassData;
    }

    /**
     * Returns the class description for the elements of the represented
     * collection. This object is available during execution of this tag's body
     * if the element class is defined. Otherwise <b>null</b> is returned.
     *
     * @return the class description for the collection's elements
     */
    public ClassDescription getElementClassDesc()
    {
        return elementClassDesc;
    }

    /**
     * Set method for the elementClass attribute. With this attribute the class
     * of the collection elements can be set directly.
     *
     * @param cls the class of the elements of this collection
     */
    public void setElementClass(Class<?> cls)
    {
        getElementClassData().setTargetClass(cls);
    }

    /**
     * Set method for the elementClassName attribute. With this attribute the
     * class of the collection elements can be specified by name.
     *
     * @param clsName the name of the class of the elements of this collection
     */
    public void setElementClassName(String clsName)
    {
        getElementClassData().setTargetClassName(clsName);
    }

    /**
     * Set method for the elementClassLoader attribute. This attribute defines
     * the class loader to be used for loading the class of the collection's
     * elements.
     *
     * @param loader the name of the class loader
     */
    public void setElementClassLoader(String loader)
    {
        getElementClassData().setClassLoaderName(loader);
    }

    /**
     * Returns a collection with the dependencies for the elements of the
     * collection created by this tag. This information is required when
     * creating the bean provider representing the final collection.
     *
     * @return the dependencies for the collection elements
     */
    public Collection<Dependency> getElementDependencies()
    {
        return Collections.unmodifiableCollection(dependencies);
    }

    /**
     * Adds a new element to the collection managed by this tag. This method is
     * intended to be called by tags in the body of this tag. It adds the given
     * <code>Dependency</code> to the internal list of element dependencies.
     *
     * @param dep the dependency to be added (must not be <b>null</b>)
     * @throws IllegalArgumentException if the dependency is <b>null</b>
     */
    public void addElement(Dependency dep)
    {
        if (dep == null)
        {
            throw new IllegalArgumentException("Dependency must not be null!");
        }

        dependencies.add(dep);
    }

    /**
     * Performs tag-specific actions before the body gets executed. This
     * implementation validates the class description for the elements of the
     * collection.
     *
     * @throws JellyTagException if the tag is incorrectly used
     */
    @Override
    protected void processBeforeBody() throws JellyTagException
    {
        super.processBeforeBody();
        setElementClassDesc(getElementClassData().getOptionalClassDescription());
    }

    /**
     * Sets the class description for the elements of this collection. This
     * method is called by <code>processBeforeBody()</code> with a class
     * description derived from the attribute values.
     *
     * @param cdesc the class description of the collection elements
     */
    protected void setElementClassDesc(ClassDescription cdesc)
    {
        elementClassDesc = cdesc;
    }
}
