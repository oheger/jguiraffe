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

import net.sf.jguiraffe.gui.builder.di.DIBuilderData;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * <p>
 * A tag handler class for creating new {@link net.sf.jguiraffe.di.BeanStore
 * BeanStore} instances.
 * </p>
 * <p>
 * Using this tag a hierarchy of <code>BeanStore</code> objects can be created.
 * The {@link BeanTag} class allows storing the created
 * {@link net.sf.jguiraffe.di.BeanProvider BeanProvider} instances in these bean
 * stores. Using different bean stores for bean providers not only makes sense
 * in terms of a logical grouping, but also for defining the scope of the bean
 * providers: from a bean store all beans defined in its parent bean store can
 * be accessed, but not vice versa. So child bean stores can be used for dealing
 * with local bean providers that are important only for a specific module of an
 * application, while application-global bean providers are kept in the root
 * bean store.
 * </p>
 * <p>
 * This tag handler class is pretty easy to use. It defines the following
 * attributes:
 * </p>
 * <p>
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="true">name</td>
 * <td>Defines the name of the bean store. A unique name must be provided.</td>
 * <td valign="true">no</td>
 * </tr>
 * <tr>
 * <td valign="true">parentName</td>
 * <td>With this attribute the name of the parent bean store can be specified.
 * If no parent is defined, the newly created bean store will become a child of
 * the root bean store.</td>
 * <td valign="true">yes</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * <code>BeanStoreTag</code> tags can also be nested. In this case, an enclosing
 * <code>BeanStoreTag</code> will define the parent bean store of the inner tag.
 * (If the inner tag has a <code>parentName</code> attribute, the parent name
 * defined here has priority.)
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: BeanStoreTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class BeanStoreTag extends TagSupport
{
    /** Stores a reference to the parent tag. */
    private BeanStoreTag parentStoreTag;

    /** Stores the name of the bean store defined by this tag. */
    private String name;

    /** Stores the name of the parent bean store. */
    private String parentName;

    /**
     * Returns a reference to an enclosing <code>BeanStoreTag</code>,
     * defining the parent bean store for this tag.
     *
     * @return the enclosing <code>BeanStoreTag</code> (can be <b>null</b> if
     * there is none)
     */
    public BeanStoreTag getParentStoreTag()
    {
        return parentStoreTag;
    }

    /**
     * Returns the name of the bean store to be created.
     *
     * @return the name of the bean store
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set method of the name attribute.
     *
     * @param name the attribute's value
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the name of the parent bean store.
     *
     * @return the name of the parent bean store
     */
    public String getParentName()
    {
        return parentName;
    }

    /**
     * Set method of the parentName attribute.
     *
     * @param parentName the attribute's value
     */
    public void setParentName(String parentName)
    {
        this.parentName = parentName;
    }

    /**
     * The main method of this tag handler class. Delegates to the
     * <code>process()</code> method for creating the bean store, then
     * executes the tag's body.
     *
     * @param output the output object
     * @throws JellyTagException if the tag is incorrectly used
     */
    public void doTag(XMLOutput output) throws JellyTagException
    {
        setParentStoreTag((BeanStoreTag) findAncestorWithClass(getClass()));
        process();
        invokeBody(output);
    }

    /**
     * Sets the reference to the enclosing <code>BeanStoreTag</code>. If such
     * a tag exists, it can be used for determining the parent bean store of the
     * store to be created.
     *
     * @param parentStoreTag the parent bean store tag (can be <b>null</b>)
     */
    protected void setParentStoreTag(BeanStoreTag parentStoreTag)
    {
        this.parentStoreTag = parentStoreTag;
    }

    /**
     * Executes this tag. Creates the bean store if everything is okay.
     *
     * @throws JellyTagException if the tag is incorrectly used
     */
    protected void process() throws JellyTagException
    {
        if (getName() == null)
        {
            throw new MissingAttributeException("name");
        }

        String parentStoreName = getParentName();
        if (parentStoreName == null && getParentStoreTag() != null)
        {
            parentStoreName = getParentStoreTag().getName();
        }

        DIBuilderData.get(getContext())
                .addBeanStore(getName(), parentStoreName);
    }
}
