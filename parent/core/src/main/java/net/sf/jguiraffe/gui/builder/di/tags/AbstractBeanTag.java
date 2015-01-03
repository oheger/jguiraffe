/*
 * Copyright 2006-2015 The JGUIraffe Team.
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
import net.sf.jguiraffe.gui.builder.di.DIBuilderData;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * <p>
 * An abstract base class for tag handler implementations that create bean
 * providers.
 * </p>
 * <p>
 * This class provides some common functionality related to the creation and
 * management of <code>{@link BeanProvider}</code> objects. The features
 * implemented here include the following:
 * <ul>
 * <li>A <code>store</code> attribute is implemented for selecting the bean
 * store, in which to store the provider created by this tag. Alternatively the
 * tag can be nested inside a <code>{@link BeanStoreTag}</code>.</li>
 * <li>Functionality is provided for passing a bean provider to a bean store.
 * Both standard and anonymous bean providers can be stored.</li>
 * <li>A framework of methods is defined that are called during the execution of
 * the tag. While most of these methods are fully functional, derived classes
 * can override some for adding specific functionality.</li>
 * <li>An abstract <code>createBeanProvider()</code> method is declared. Here
 * concrete subclasses have to implement the creation of their bean provider.</li>
 * </ul>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: AbstractBeanTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class AbstractBeanTag extends TagSupport
{
    /** Stores a reference to an enclosing bean store tag. */
    private BeanStoreTag beanStoreTag;

    /** The target dependency tag if this is an anonymous bean declaration. */
    private DependencyTag targetDependency;

    /** Stores the name of the bean store to use. */
    private String store;

    /**
     * Returns the name of the managed bean definition. This name is used when
     * the <code>BeanProvider</code> created by this tag is stored in a
     * <code>BeanStore</code>. In this base implementation the name is only
     * queried to distinguish whether this is a normal or an anonymous bean
     * definition. <code>getName()</code> always returns <b>null</b> here; if
     * derived classes support setting a name, they must override this method.
     *
     * @return the name of the bean definition
     */
    public String getName()
    {
        return null;
    }

    /**
     * Returns the name of the <code>BeanStore</code>, in which to place the
     * managed bean definition.
     *
     * @return the name of the bean store
     */
    public String getStore()
    {
        return store;
    }

    /**
     * Set method of the store attribute.
     *
     * @param store the attribute's value
     */
    public void setStore(String store)
    {
        this.store = store;
    }

    /**
     * Returns a reference to the enclosing <code>BeanStoreTag</code>. This can
     * be <b>null</b> if there is no such tag.
     *
     * @return a reference to the enclosing bean store tag
     */
    public BeanStoreTag getBeanStoreTag()
    {
        return beanStoreTag;
    }

    /**
     * Returns the target dependency. If this is a valid anonymous bean
     * declaration, there is a target dependency tag, on which to set the
     * reference to the declared bean.
     *
     * @return the target dependency tag
     */
    public DependencyTag getTargetDependency()
    {
        return targetDependency;
    }

    /**
     * Returns a flag whether this tag declares an anonymous bean. This is a
     * bean without a name that is only visible in the context it is declared.
     *
     * @return a flag if this is an anonymous bean
     */
    public boolean isAnonymous()
    {
        return getTargetDependency() != null;
    }

    /**
     * The main method of this tag. Delegates to the specific processing
     * methods.
     *
     * @param output the output object
     * @throws JellyTagException if an error occurs or the tag is incorrectly
     *         used
     */
    public void doTag(XMLOutput output) throws JellyTagException
    {
        setBeanStoreTag((BeanStoreTag) findAncestorWithClass(BeanStoreTag.class));
        processBeforeBody();
        invokeBody(output);
        process();
    }

    /**
     * Sets the reference to the enclosing <code>BeanStoreTag</code>. This can
     * be <b>null</b> if this tag is not placed in the body of a bean store tag.
     *
     * @param beanStoreTag the enclosing <code>BeanStoreTag</code>
     */
    protected void setBeanStoreTag(BeanStoreTag beanStoreTag)
    {
        this.beanStoreTag = beanStoreTag;
    }

    /**
     * Performs pre-processing before the body of this tag is evaluated. This
     * method is called by the <code>doTag()</code> method. It checks whether
     * this tag is nested inside a <code>{@link DependencyTag}</code>, which
     * will become the target dependency, i.e. if this is an anonymous bean
     * declaration, the target dependency will be initialized to point to this
     * bean.
     *
     * @throws JellyTagException if no name is defined for this tag, but no
     *         target dependency can be obtained or the target dependency is
     *         already initialized
     */
    protected void processBeforeBody() throws JellyTagException
    {
        targetDependency = null;
        if (getName() == null)
        {
            // Is this an anonymous tag?
            if (getParent() instanceof DependencyTag)
            {
                DependencyTag depParent = (DependencyTag) getParent();
                if (depParent.getRefName() == null)
                {
                    targetDependency = depParent;
                }
                else
                {
                    // This tag already has a dependency!
                    throw new JellyTagException(
                            "Cannot create anonymous bean: "
                                    + "Parent tag is already defined!");
                }
            }

            if (targetDependency == null)
            {
                throw new MissingAttributeException("name");
            }
        }
    }

    /**
     * Processes this tag after its body has been evaluated. Creates the bean
     * provider and adds it to the correct bean store.
     *
     * @throws JellyTagException if the tag is incorrectly used
     */
    protected void process() throws JellyTagException
    {
        store(createBeanProvider());
    }

    /**
     * Stores the newly created <code>BeanProvider</code> in the correct bean
     * store. This method is called by <code>process()</code> when a provider
     * could be successfully created.
     *
     * @param provider the newly created bean provider
     * @throws JellyTagException if an error occurs
     */
    protected void store(BeanProvider provider) throws JellyTagException
    {
        String storeName = getStore();
        if (storeName == null && getBeanStoreTag() != null)
        {
            storeName = getBeanStoreTag().getName();
        }

        if (isAnonymous())
        {
            String refName = DIBuilderData.get(getContext())
                    .addAnonymousBeanProvider(storeName, provider);
            getTargetDependency().setRefName(refName);
        }
        else
        {
            DIBuilderData.get(getContext()).addBeanProvider(storeName,
                    getName(), provider);
        }
    }

    /**
     * Creates the bean provider defined by this tag. This method is invoked by
     * <code>process()</code>. A concrete subclass has to implement it and
     * return a <code>BeanProvider</code>, which will be stored in the selected
     * bean store.
     *
     * @return the bean provider defined by this tag
     * @throws JellyTagException in case of an error
     */
    protected abstract BeanProvider createBeanProvider()
            throws JellyTagException;
}
