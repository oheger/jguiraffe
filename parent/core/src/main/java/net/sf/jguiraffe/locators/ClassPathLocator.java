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
package net.sf.jguiraffe.locators;

import java.net.URL;

import org.apache.commons.lang.ObjectUtils;

/**
 * <p>
 * A specific {@code Locator} implementation that can obtain resources from
 * the class path.
 * </p>
 * <p>
 * This class is initialized with a resource name. The {@code getURL()}
 * method tries to find this resource from the class path (using the
 * {@link LocatorUtils} class. No caching is performed; each invocation of
 * {@code getURL()} will look up the resource. Because a lookup might depend
 * on the context class loader, multiple invocations of {@code getURL()} may
 * yield different results. In addition, it is possible to specify the
 * class loader to be used for resource lookup.
 * </p>
 * <p>
 * Instances of this class are created using the {@code getInstance()}
 * factory method. They are immutable and thus can be shared between multiple
 * threads.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ClassPathLocator.java 211 2012-07-10 19:49:13Z oheger $
 */
public final class ClassPathLocator extends AbstractLocator
{
    /** Stores the name of the resource. */
    private final String resourceName;

    /** The default class loader for the resource lookup. */
    private final ClassLoader classLoader;

    /**
     * Creates a new instance of {@code ClassPathLocator} and sets the name
     * of the represented resource. To create an instance client code can use
     * the {@code getInstance()} factory method.
     *
     * @param resourceName the resource name
     * @param cl the default class loader
     */
    private ClassPathLocator(String resourceName, ClassLoader cl)
    {
        this.resourceName = resourceName;
        classLoader = cl;
    }

    /**
     * Returns the name of the resource represented by this locator.
     *
     * @return the resource's name
     */
    public String getResourceName()
    {
        return resourceName;
    }

    /**
     * Returns the default class loader used by this locator when looking up the
     * resource name. This is the class loader passed to the
     * {@code getInstance()} method. It may be <b>null</b> if no specific class
     * loader was provided when constructing this object.
     *
     * @return the default class loader for resource lookup
     * @since 1.2
     */
    public ClassLoader getClassLoader()
    {
        return classLoader;
    }

    /**
     * Returns a {@code ClassPathLocator} instance for the specified
     * resource name.
     *
     * @param resourceName the name of the resource (must not be <b>null</b>)
     * @return the instance with this resource name
     * @throws IllegalArgumentException if the resource name is <b>null</b>
     */
    public static ClassPathLocator getInstance(String resourceName)
    {
        return getInstance(resourceName, null);
    }

    /**
     * Returns a {@code ClassPathLocator} instance which looks up the specified
     * resource name using the given class loader. If a {@code ClassLoader}
     * reference is provided, this class loader is tried first when resolving
     * the resource name. Otherwise, the default order of class loaders is used
     * as implemented in {@link LocatorUtils}.
     *
     * @param resourceName the name of the resource (must not be <b>null</b>)
     * @param cl an optional class loader to be used for resource lookup
     * @return the instance with this resource name
     * @throws IllegalArgumentException if the resource name is <b>null</b>
     * @since 1.2
     */
    public static ClassPathLocator getInstance(String resourceName,
            ClassLoader cl)
    {
        if (resourceName == null)
        {
            throw new IllegalArgumentException(
                    "Resource name must not be null!");
        }

        return new ClassPathLocator(resourceName, cl);
    }

    /**
     * Returns the URL for the represented resource. This implementation uses
     * the {@link LocatorUtils#locateResource(String, ClassLoader)} method to
     * find the resource on the class path. If this fails, an exception is
     * thrown.
     *
     * @return the URL to the resource
     * @throws LocatorException if the resource URL cannot be obtained
     */
    public URL getURL()
    {
        URL result = LocatorUtils.locateResource(getResourceName(), getClassLoader());
        if (result == null)
        {
            throw new LocatorException("Cannot locate resource "
                    + getResourceName());
        }

        return result;
    }

    /**
     * Compares this object with another one. Two instances of this class are
     * equal if and only if they refer to the same resource name and use the
     * same default class loader.
     *
     * @param obj the object to be compared to
     * @return a flag whether the objects are equal
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof ClassPathLocator))
        {
            return false;
        }

        ClassPathLocator c = (ClassPathLocator) obj;
        return getResourceName().equals(c.getResourceName())
                && ObjectUtils.equals(getClassLoader(), c.getClassLoader());
    }

    /**
     * Returns a hash code for this object.
     *
     * @return a hash code
     */
    @Override
    public int hashCode()
    {
        final int factor = 31;
        int result = getResourceName().hashCode();
        if (getClassLoader() != null)
        {
            result = factor * result + getClassLoader().hashCode();
        }
        return result;
    }

    /**
     * Returns a string representation of this object. This string will at least
     * contain the resource name used by this locator.
     *
     * @return a string for this object
     */
    @Override
    public String toString()
    {
        return LocatorUtils.locatorToString(this, "resourceName = "
                + getResourceName());
    }
}
