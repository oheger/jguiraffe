/*
 * Copyright 2006-2014 The JGUIraffe Team.
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

import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.impl.ClassDescription;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A simple data class for managing the components needed for a class
 * description.
 * </p>
 * <p>
 * A couple of tags of the <em>di</em> builder need to create
 * {@link ClassDescription} objects. This class encapsulates the
 * functionality required for this purpose. It defines properties for the
 * supported components and provides validation methods.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ClassDescData.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ClassDescData
{
    /** Stores the target class of the class description. */
    private Class<?> targetClass;

    /** Stores the name of the target class. */
    private String targetClassName;

    /** Stores the name of the class loader to be used. */
    private String classLoaderName;

    /**
     * Returns the target class for the class description.
     *
     * @return the target class
     */
    public Class<?> getTargetClass()
    {
        return targetClass;
    }

    /**
     * Sets the target class of the class description.
     *
     * @param targetClass the target class
     */
    public void setTargetClass(Class<?> targetClass)
    {
        this.targetClass = targetClass;
    }

    /**
     * Returns the name of the target class for the class description.
     *
     * @return the name of the target class
     */
    public String getTargetClassName()
    {
        return targetClassName;
    }

    /**
     * Sets the name of the target class for the class description.
     *
     * @param targetClassName the name of the target class
     */
    public void setTargetClassName(String targetClassName)
    {
        this.targetClassName = targetClassName;
    }

    /**
     * Returns the name of the class loader to be used.
     *
     * @return the name of the class loader
     */
    public String getClassLoaderName()
    {
        return classLoaderName;
    }

    /**
     * Sets the name of the class loader to be used.
     *
     * @param classLoaderName the name of the class loader to be used
     */
    public void setClassLoaderName(String classLoaderName)
    {
        this.classLoaderName = classLoaderName;
    }

    /**
     * Checks whether all required data for constructing a class description is
     * available.
     *
     * @return a flag whether enough information for constructing a class
     * description is available
     */
    public boolean isDefined()
    {
        return getTargetClass() != null || getTargetClassName() != null;
    }

    /**
     * Checks whether the properties of this object are valid. This
     * implementation checks whether either a target class or a class name is
     * set. If both are set, they must specify the same class. This
     * implementation expects that the properties are defined, i.e.
     * <code>{@link #isDefined()}</code> has returned <b>true</b>.
     *
     * @return a flag whether the properties are valid
     */
    public boolean isValid()
    {
        if (getTargetClass() != null && getTargetClassName() != null)
        {
            return getTargetClassName().equals(getTargetClass().getName());
        }
        return true;
    }

    /**
     * Creates a <code>ClassDescription</code> object from the internal data.
     * If not all required properties are set or if they contain invalid values,
     * an exception will be thrown.
     *
     * @return a <code>ClassDescription</code> object corresponding to the
     * values of the properties
     * @throws JellyTagException if not all required properties are set or
     * some properties are invalid
     */
    public ClassDescription createClassDescription() throws JellyTagException
    {
        if (!isDefined())
        {
            throw new JellyTagException("Required properties are missing!");
        }
        if (!isValid())
        {
            throw new JellyTagException(
                    "Properties are not valid (ambigous class definition)!");
        }

        return (getTargetClass() != null) ? ClassDescription
                .getInstance(getTargetClass()) : ClassDescription.getInstance(
                getTargetClassName(), getClassLoaderName());
    }

    /**
     * Returns a <code>ClassDescription</code> object from the internal data
     * or <b>null</b> if no data is defined. This is a convenience method. If
     * data of a class is defined, <code>isValid()</code> is called for
     * checking the validity of the entered data. If this fails, an exception is
     * thrown. If the class is undefined, simply <b>null</b> is returned. This
     * functionality is frequently needed when a class definition is optional:
     * if one is provided, it must be valid; otherwise <b>null</b> can be
     * returned.
     *
     * @return a <code>ClassDescription</code> object corresponding to the
     *         values of the properties or <b>null</b> if no class is defined
     * @throws JellyTagException if the class definition is not valid
     */
    public ClassDescription getOptionalClassDescription()
            throws JellyTagException
    {
        if (isDefined())
        {
            if (!isValid())
            {
                throw new JellyTagException(
                        "Properties are not valid (ambigous class definition)!");
            }

            return createClassDescription();
        }

        else
        {
            return null;
        }
    }

    /**
     * A convenience method for resolving the class specified by this data
     * object. This method creates the corresponding {@link ClassDescription}
     * and obtains the target class for it. No caching is performed, so this
     * method should only be used if resolving happens once.
     *
     * @param clProvider the {@code ClassLoaderProvider} to be used for class
     *        loading
     * @return the class specified by this description object
     * @throws JellyTagException if an error occurs during resolving
     * @see #createClassDescription()
     * @see ClassDescription#getTargetClass(ClassLoaderProvider)
     */
    public Class<?> resolveClass(ClassLoaderProvider clProvider)
            throws JellyTagException
    {
        return createClassDescription().getTargetClass(clProvider);
    }
}
