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

import net.sf.jguiraffe.di.impl.ChainedInvocation;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * <p>
 * A tag handler class that defines a shutdown script for a {@code BeanProvider}
 * .
 * </p>
 * <p>
 * This tag can appear in the body of a {@link BeanTag}. In its body an
 * arbitrary number of tags derived from {@link InvocationTag} can be placed
 * defining a set of operations to be performed when shutting down the
 * corresponding singleton bean. All theses operations are added to a
 * {@link ChainedInvocation} object, which will then become the
 * <em>shutdown handler</em> of the singleton {@code BeanProvider} created by
 * the hosting bean tag.
 * </p>
 * <p>
 * Using this tag is an alternative to the {@code shutdownMethod} attribute
 * supported by {@link BeanTag}. With the attribute only a single,
 * parameter-less method can be specified to be invoked on shutdown of the bean.
 * This tag in contrast supports much more complex shutdown scripts: all tags
 * producing {@code Invokable} objects can be used. However, care must be taken
 * with the dependencies used by the shutdown script. Because the owning {@code
 * BeanStore} may already been partly destroyed when the shutdown script is
 * invoked, access to other beans living in the same bean store is not
 * permitted. It is possible to invoke arbitrary methods on the bean managed by
 * the {@code BeanProvider} (this bean is also the default target of the
 * invocation, so all method invocations or property access operations per
 * default have this object as target), set or query properties on it, or making
 * use of script-local variables.
 * </p>
 * <p>
 * This tag does not define any attributes. In its body the
 * {@link InvocationTag} objects can be placed that make up the shutdown script.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ShutdownHandlerTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ShutdownHandlerTag extends TagSupport
{
    /** The chained invocation storing the shutdown script. */
    private ChainedInvocation shutdownScript;

    /**
     * Executes this tag. This implementation delegates to the
     * {@link #processBeforeBody()} and {@link #process()} methods.
     *
     * @param output the output object
     * @throws JellyTagException if the tag is used incorrectly
     */
    public void doTag(XMLOutput output) throws JellyTagException
    {
        processBeforeBody();
        invokeBody(output);
        process();
    }

    /**
     * Performs some processing of this tag before the body is evaluated.
     *
     * @throws JellyTagException if the tag is used incorrectly
     */
    protected void processBeforeBody() throws JellyTagException
    {
        if (!(getParent() instanceof BeanTag))
        {
            throw new JellyTagException(
                    "This tag must be nested inside a bean tag!");
        }

        shutdownScript = new ChainedInvocation();
        InvocationData.get(getContext()).registerInvokableSupport(
                shutdownScript);
    }

    /**
     * Performs the actual processing of this tag. This method is called by
     * {@code doTag()} after the body has been evaluated.
     *
     * @throws JellyTagException if an error occurs
     */
    protected void process() throws JellyTagException
    {
        ((BeanTag) getParent()).setShutdownHandler(shutdownScript);
        InvocationData.get(getContext()).unregisterInvokableSupport();
    }
}
