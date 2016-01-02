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
package net.sf.jguiraffe.gui.builder.components.tags;

import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.locators.ClassPathLocator;
import net.sf.jguiraffe.locators.Locator;
import net.sf.jguiraffe.locators.URLLocator;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * A tag for adding an icon to a component.
 * </p>
 * <p>
 * This tag supports multiple ways for defining an icon:
 * <ul>
 * <li>By specifying an URL using the {@code url} attribute.</li>
 * <li>By specifying a resource name using the {@code resource} attribute. In
 * this case the icon is searched on the class path. Per default, the current
 * default class loader is used for resolving the resource name. If a different
 * class loader is desired, its name (as registered at the current
 * {@code ClassLoaderProvider}) can be specified using the
 * {@code resourceLoader} attribute.</li>
 * <li>By specifying an arbitrary {@link Locator} pointing to the icon data
 * using the {@code locator} attribute. This is actually the generic form of the
 * ways described above. The locator is looked up by its name in the current
 * bean context.</li>
 * <li>With the {@code ref} attribute an icon can be referenced that has already
 * been created and stored in the Jelly context (e.g by another {@code icon} tag
 * that had a {@code var} attribute (see below).</li>
 * </ul>
 * Exactly one of these attributes must be present.
 * </p>
 * <p>
 * The icon is created using the component manager. Then the tag searches for an
 * enclosing tag that implements the {@link IconSupport} interface. If one is
 * found, this tag is passed the icon. With the {@code var} attribute the name
 * of a variable can be specified, which is assigned the icon. So it can be
 * reused. At least one of the ways for storing an icon (the {@code var}
 * attribute or an {@code IconSupport} tag) must be defined, otherwise an
 * exception is thrown.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: IconTag.java 211 2012-07-10 19:49:13Z oheger $
 */
public class IconTag extends FormBaseTag
{
    /** Stores the URL of the icon. */
    private String url;

    /** Stores the resource name of the icon. */
    private String resource;

    /** The name of the class loader for resolving a resource name. */
    private String resourceLoader;

    /** Stores the name of a variable, from which the icon should be fetched. */
    private String ref;

    /** Stores the name of a variable, to which the icon should be assigned. */
    private String var;

    /** Stores the name of the locator pointing to the icon data. */
    private String locator;

    /**
     * Returns the resource name of the icon.
     *
     * @return the resource name
     */
    public String getResource()
    {
        return resource;
    }

    /**
     * Setter method for the resource attribute. Specifies the name under which
     * the icon can be found on the classpath. Either this resource name or an
     * URL must be specified.
     *
     * @param resource the resource name
     */
    public void setResource(String resource)
    {
        this.resource = resource;
    }

    /**
     * Returns the name of the class loader for resolving the resource name.
     *
     * @return the name of the class loader
     * @since 1.2
     */
    public String getResourceLoader()
    {
        return resourceLoader;
    }

    /**
     * Setter method for the {@code resourceLoader} attribute. Here the name of
     * a class loader for resolving the resource name can be provided. A class
     * loader with this name is looked up using the current
     * {@code ClassLoaderProvider}. If no class loader has been specified, the
     * default one is used.
     *
     * @param resourceLoader the name of the class loader for resolving class
     *        path resources
     * @since 1.2
     */
    public void setResourceLoader(String resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Returns the URL that points to the icon.
     *
     * @return the URL
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Setter method for the URL attribute. Specifies an URL from which the icon
     * can be loaded. Either this URL or a resource name must be specified.
     *
     * @param url the URL
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    /**
     * Returns the name of the variable that stores the icon.
     *
     * @return the name of a variable reference
     */
    public String getRef()
    {
        return ref;
    }

    /**
     * Setter method for the var attribute. If no URL or resource name are
     * specified, the icon can be obtained from a variable.
     *
     * @param ref the attribute's value
     */
    public void setRef(String ref)
    {
        this.ref = ref;
    }

    /**
     * Returns the name of the variable, to which the icon should be assigned.
     *
     * @return the name of a target variable
     */
    public String getVar()
    {
        return var;
    }

    /**
     * Setter method of the var attribute.
     *
     * @param var the attribute's value
     */
    public void setVar(String var)
    {
        this.var = var;
    }

    /**
     * Returns the name of the {@code Locator}, from which the icon should
     * be obtained.
     *
     * @return the name of the {@code Locator}
     */
    public String getLocator()
    {
        return locator;
    }

    /**
     * Setter method of the {@code locator} attribute. With this attribute
     * an arbitrary {@code Locator} can be specified, from which the icon
     * data is loaded. The locator is looked up in the current bean context
     * under the name specified here.
     *
     * @param locator the name of {@code Locator} with the icon data
     */
    public void setLocator(String locator)
    {
        this.locator = locator;
    }

    /**
     * Executes this tag. Tries to create the icon and set it on an enclosing
     * {@link IconSupport} tag.
     *
     * @throws JellyTagException if an error occurs
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected void process() throws JellyTagException, FormBuilderException
    {
        int defCount = countIconDefinitions();
        if (defCount < 1)
        {
            throw new MissingAttributeException(
                    "Either url or resource or ref attribute must be set!");
        }
        if (defCount > 1)
        {
            throw new FormBuilderException(
                    "Exactly one of these attributes must be used: "
                            + "url, resource, ref");
        }

        IconSupport parent = (IconSupport) findAncestorWithClass(IconSupport.class);
        if (parent == null && StringUtils.isEmpty(getVar()))
        {
            throw new JellyTagException(
                    "Icon tag must be nested inside an IconSupport tag!");
        }

        Object icon = createIcon();
        if (parent != null)
        {
            parent.setIcon(icon);
        }
        if (getVar() != null)
        {
            getContext().setVariable(getVar(), icon);
        }
    }

    /**
     * Creates the icon. This method is called after all syntactic checks were
     * successful. Here the icon must be created, depending on the defined
     * attributes.
     *
     * @return the icon
     * @throws FormBuilderException if an error occurs
     */
    protected Object createIcon() throws FormBuilderException
    {
        if (getRef() != null)
        {
            Object icon = getContext().getVariable(getRef());
            if (icon == null)
            {
                throw new FormBuilderException(
                        "Cannot find referenced variable: " + getRef());
            }
            return icon;
        }
        else
        {
            return getBuilderData().getComponentManager().createIcon(
                    fetchLocator());
        }
    }

    /**
     * Fetches the locator for the icon. This method is called by
     * {@code createIcon()} when no reference attribute is specified, i.e. the
     * icon has to be obtained from a locator. If the {@code locator} attribute
     * is specified, the locator is looked up from the current bean context.
     * Otherwise a locator is created from the other convenience attributes (
     * {@code url} or {@code resource}).
     *
     * @return the {@code Locator} that defines the icon
     */
    protected Locator fetchLocator()
    {
        if (getLocator() != null)
        {
            return (Locator) getBuilderData().getBeanContext().getBean(
                    getLocator());
        }
        else if (getResource() != null)
        {
            ClassLoader cl =
                    getBuilderData().getBeanContext().getClassLoaderProvider()
                            .getClassLoader(getResourceLoader());
            return ClassPathLocator.getInstance(getResource(), cl);
        }
        else
        {
            assert getUrl() != null : "No locator defined!";
            return URLLocator.getInstance(getUrl());
        }
    }

    /**
     * Counts in how many ways the icon is defined. There should be exactly one
     * way.
     *
     * @return the number of attributes that define the icon
     */
    private int countIconDefinitions()
    {
        int count = 0;
        if (!StringUtils.isEmpty(getUrl()))
        {
            count++;
        }
        if (!StringUtils.isEmpty(getResource()))
        {
            count++;
        }
        if (!StringUtils.isEmpty(getRef()))
        {
            count++;
        }
        if (!StringUtils.isEmpty(getLocator()))
        {
            count++;
        }
        return count;
    }
}
