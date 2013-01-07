/*
 * Copyright 2006-2013 The JGUIraffe Team.
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

import net.sf.jguiraffe.di.impl.ClassDescription;
import net.sf.jguiraffe.di.impl.ConstructorInvocation;
import net.sf.jguiraffe.di.impl.Invokable;
import net.sf.jguiraffe.di.impl.providers.ConstructorBeanProvider;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;

/**
 * <p>
 * A specialized constructor invocation tag that is intended to be used inside a
 * <code>&lt;bean&gt;</code> tag for defining the <code>BeanProvider</code>
 * for creating the managed bean.
 * </p>
 * <p>
 * This tag handler class differs from its ancestor in the following ways:
 * <ul>
 * <li>It must be a direct child of a <code>&lt;bean&gt;</code> tag.</li>
 * <li>The target class (on which the constructor is to be invoked) need not
 * directly be defined using the tag's attributes. It can also be obtained from
 * the enclosing bean tag.</li>
 * <li>A <code>{@link ConstructorBeanProvider}</code> is created and
 * initialized with the also created <code>{@link ConstructorInvocation}</code>;
 * this bean provider is then passed to the enclosing bean tag.</li>
 * </ul>
 * </p>
 * <p>
 * All attributes available for the super class are also supported by this tag
 * handler class.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ConstructorTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ConstructorTag extends ConstructorInvocationTag implements
        InvokableSupport
{
    /** Stores a reference to the enclosing bean tag. */
    private BeanTag beanTag;

    /** A flag whether an <code>Invokable</code> has already been added. */
    private boolean invokableAdded;

    /**
     * Adds an <code>Invokable</code> object to this tag. Because this tag
     * registers itself as <code>InvokableSupport</code>, this method will be
     * automatically called by the super class. This makes it possible to obtain
     * the reference to the created <code>{@link ConstructorInvocation}</code>.
     *
     * @param inv the invocation object to add
     * @throws JellyTagException if the invocation object is not supported or
     * another error occurs
     */
    public void addInvokable(Invokable inv) throws JellyTagException
    {
        if (!(inv instanceof ConstructorInvocation))
        {
            throw new JellyTagException("Unsupported Invokable type: " + inv);
        }
        if (invokableAdded)
        {
            throw new JellyTagException("Only a single Invokable is allowed!");
        }

        ConstructorBeanProvider creator = new ConstructorBeanProvider(
                (ConstructorInvocation) inv);
        assert getBeanTag() != null : "No bean tag set!";
        getBeanTag().setBeanCreator(creator);
        invokableAdded = true;
    }

    /**
     * Returns the enclosing bean tag.
     *
     * @return the enclosing bean tag
     */
    public BeanTag getBeanTag()
    {
        return beanTag;
    }

    /**
     * Returns the target class of the constructor to be invoked. This
     * implementation tries to obtain this information from the bean tag if it
     * is not explicitly set.
     *
     * @return the target class
     */
    @Override
    public ClassDescription getTargetClassDescription()
    {
        ClassDescription cd = super.getTargetClassDescription();
        if (cd == null)
        {
            assert getBeanTag() != null : "No bean tag set!";
            cd = getBeanTag().getBeanClassDesc();
        }
        return cd;
    }

    /**
     * The main method if this tag. Tries to obtain the enclosing
     * <code>{@link BeanTag}</code>.
     *
     * @param out the output object
     * @throws JellyTagException if an error occurs
     */
    @Override
    public void doTag(XMLOutput out) throws JellyTagException
    {
        if (getParent() instanceof BeanTag)
        {
            setBeanTag((BeanTag) getParent());
        }
        super.doTag(out);
    }

    /**
     * Initializes the reference to the enclosing <code>BeanTag</code>. For
     * fulfilling its task this tag has to interact with its parent bean tag.
     *
     * @param tag the enclosing bean tag
     */
    protected void setBeanTag(BeanTag tag)
    {
        beanTag = tag;
    }

    /**
     * Executes this tag.
     *
     * @throws JellyTagException if an error occurs
     */
    @Override
    protected void process() throws JellyTagException
    {
        if (getBeanTag() == null)
        {
            throw new JellyTagException(
                    "This tag must be nested inside a BeanTag!");
        }

        invokableAdded = false;
        InvocationData.get(getContext()).registerInvokableSupport(this);
        try
        {
            super.process();
        }
        finally
        {
            InvocationData.get(getContext()).unregisterInvokableSupport();
        }
    }
}
