/*
 * Copyright 2006-2021 The JGUIraffe Team.
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

import java.util.Stack;

import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.impl.ChainedInvocation;
import net.sf.jguiraffe.di.impl.Invokable;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * An internally used helper class for dealing with invocation objects.
 * </p>
 * <p>
 * Invocation objects (e.g. <code>MethodInvocation</code> or
 * <code>ConstructorInvocation</code>) play an important role in the dependency
 * injection framework. Many bean providers support them for performing creation
 * and initialization of beans. The tags that create such objects need a generic
 * way of passing their invocation objects to the correct receivers. This task
 * is handled by this class.
 * </p>
 * <p>
 * An instance of this class lives in the Jelly context (or is automatically
 * created when it is accessed for the first time). Tasks that support
 * invocation objects have to register itself at this instance before they
 * execute their body. This can either be done by passing an
 * <code>InvokableSupport</code> object to the
 * <code>registerInvokableSupport()</code> method or an instance of
 * {@link ChainedInvocation}. In the latter case some enhanced functionality is
 * available, e.g. obtaining dependencies to local variables or setting a result
 * and a source variable.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: InvocationData.java 205 2012-01-29 18:29:57Z oheger $
 */
final class InvocationData
{
    /**
     * Constant for the key, under which an instance is stored in the Jelly
     * context.
     */
    private static final String CTX_KEY = InvocationData.class.getName();

    /** A stack with the registered support objects. */
    private Stack<SupportData> supportObjects;

    /**
     * Creates a new instance of <code>InvocationData</code>. For obtaining
     * an instance use the static <code>get()</code> method.
     */
    private InvocationData()
    {
        supportObjects = new Stack<SupportData>();
    }

    /**
     * Returns the object of this class that can be found in the specified Jelly
     * context. If the context does not contain such an instance, a new instance
     * is created, stored in the context, and returned.
     *
     * @param context the Jelly context
     * @return the instance for this context
     */
    public static InvocationData get(JellyContext context)
    {
        InvocationData result = (InvocationData) context.getVariable(CTX_KEY);
        if (result == null)
        {
            result = new InvocationData();
            context.setVariable(CTX_KEY, result);
        }
        return result;
    }

    /**
     * Registers the specified <code>InvokableSupport</code> object at this
     * instance. This means that following invocations of the
     * <code>addInvokable()</code> method are routed to this object. This
     * method should be called by tag handler classes that deal with
     * <code>Invokable</code> objects at the beginning of their processing. It
     * can be called multiple times for dealing with arbitrary nested
     * hierarchies of <code>InvokableSupport</code> objects.
     *
     * @param invSupport the support object to register
     * @throws IllegalArgumentException if the object to register is undefined
     */
    public void registerInvokableSupport(InvokableSupport invSupport)
    {
        if (invSupport == null)
        {
            throw new IllegalArgumentException(
                    "Support object must not be null!");
        }
        supportObjects.push(new InvokableSupportData(invSupport));
    }

    /**
     * Registers the specified <code>ChainedInvocation</code> object at this
     * instance. <code>Invokable</code> objects added after this call will now
     * be passed to this chain. This method is very similar to the overloaded
     * variant. However for <code>ChainedInvocation</code> objects some
     * special features are available.
     *
     * @param chain the chain object to register
     * @throws IllegalArgumentException if the object to register is undefined
     * @see #registerInvokableSupport(InvokableSupport)
     */
    public void registerInvokableSupport(ChainedInvocation chain)
    {
        if (chain == null)
        {
            throw new IllegalArgumentException("Chain must not be null!");
        }
        supportObjects.push(new ChainSupportData(chain));
    }

    /**
     * Unregisters an <code>InvokableSupport</code> object. This method must
     * be called for each invocation of the
     * <code>registerInvokableSupport()</code> method at the end of the
     * responsible tag handler class. (Internally a stack of these registrations
     * is maintained, so it is not necessary to specify the concrete object to
     * be unregistered).
     *
     * @throws IllegalStateException if nothing can be unregistered
     */
    public void unregisterInvokableSupport()
    {
        if (supportObjects.isEmpty())
        {
            throw new IllegalStateException("Nothing to unregister!");
        }
        supportObjects.pop();
    }

    /**
     * Adds an <code>Invokable</code> object. This method is invoked by tag
     * handler classes that create <code>Invokable</code> objects. It ensures
     * that the provided object is passed to the correct receiver. This might
     * cause an exception if the <code>Invokable</code> object does not fit
     * into the current context.
     *
     * @param inv the <code>Invokable</code> object to add
     * @param result the name of the result variable (can be <b>null</b>)
     * @param source the name of the variable, which contains the target object
     * for this invocation
     * @throws JellyTagException if the passed in object is rejected by its
     * receiver for some reasons
     * @see ChainedInvocation#addInvokable(Invokable, String, String)
     */
    public void addInvokable(Invokable inv, String result, String source)
            throws JellyTagException
    {
        getCurrentSupportData().addInvokable(inv, result, source);
    }

    /**
     * Returns a <code>Dependency</code> for a local variable of a
     * <code>{@link ChainedInvocation}</code>. This method is available only
     * if the current <code>InvokableSupport</code> target object is a
     * <code>ChainedInvocation</code>.
     *
     * @param varName the name of the variable for the dependency
     * @return the dependency to this local variable
     * @throws JellyTagException if this method cannot be called in the current
     * context
     */
    public Dependency getVariableDependency(String varName)
            throws JellyTagException
    {
        return getCurrentSupportData().getVariableDependency(varName);
    }

    /**
     * Obtains the currently active support object. If there is none, an
     * exception will be thrown.
     *
     * @return the active support object
     * @throws JellyTagException if this method cannot be called in the current
     * context
     */
    private SupportData getCurrentSupportData() throws JellyTagException
    {
        if (supportObjects.isEmpty())
        {
            throw new JellyTagException(
                    "No InvokableSupport object registered!");
        }
        return supportObjects.peek();
    }

    /**
     * An internally used class for managing a support object. There will be
     * different concrete implementations for the different types of support
     * objects.
     */
    private abstract static class SupportData
    {
        /**
         * Adds an Invokable object to the managed support object if possible.
         *
         * @param inv the object to add
         * @param result the name of the result variable
         * @param source the name of the source variable
         * @throws JellyTagException if the object cannot be added
         */
        public abstract void addInvokable(Invokable inv, String result,
                String source) throws JellyTagException;

        /**
         * Returns a dependency to a local variable if possible.
         *
         * @param name the name of the variable
         * @return the dependency
         * @throws JellyTagException if local variables are not supported
         */
        public abstract Dependency getVariableDependency(String name)
                throws JellyTagException;
    }

    /**
     * A concrete <code>SupportData</code> implementation that deals with
     * arbitrary <code>InvokableSupport</code> objects.
     */
    private static class InvokableSupportData extends SupportData
    {
        /** Stores the support object. */
        private InvokableSupport support;

        /**
         * Creates a new instance of <code>InvokableSupportData</code> and
         * initializes it with the support object.
         *
         * @param sup the support object
         */
        public InvokableSupportData(InvokableSupport sup)
        {
            support = sup;
        }

        /**
         * Adds the invokable to the managed support objects. Names for result
         * or source variables are not allowed and cause an exception.
         *
         * @param inv the object to add
         * @param result the name of the result variable
         * @param source the name of the source variable
         * @throws JellyTagException if the object cannot be added
         */
        @Override
        public void addInvokable(Invokable inv, String result, String source)
                throws JellyTagException
        {
            if (result != null || source != null)
            {
                throw new JellyTagException(
                        "Variable names are not allowed outside a chain context!");
            }
            support.addInvokable(inv);
        }

        /**
         * Requests a dependency to a local variable. This implementation always
         * throws an exception because there is no chain in the context.
         *
         * @param name the name of the variable
         * @return the dependency
         * @throws JellyTagException if local variables are not supported
         */
        @Override
        public Dependency getVariableDependency(String name)
                throws JellyTagException
        {
            throw new JellyTagException("Local variables are not supported!");
        }
    }

    /**
     * A concrete <code>SupportData</code> implementation that deals with
     * <code>ChainedInvocation</code> objects.
     */
    private static class ChainSupportData extends SupportData
    {
        /** Stores the chain object. */
        private ChainedInvocation chain;

        /**
         * Creates a new instance of <code>ChainSupportData</code> and
         * initializes it with the chain object.
         *
         * @param c the chain
         */
        public ChainSupportData(ChainedInvocation c)
        {
            chain = c;
        }

        /**
         * Adds an Invokable to the managed chain. Variable names are fully
         * supported.
         *
         * @param inv the object to add
         * @param result the name of the result variable
         * @param source the name of the source variable
         * @throws JellyTagException if the object cannot be added
         */
        @Override
        public void addInvokable(Invokable inv, String result, String source)
                throws JellyTagException
        {
            chain.addInvokable(inv, result, source);
        }

        /**
         * Requests a dependency to a local variable. This request is delegated
         * to the chain.
         *
         * @param name the name of the variable
         * @return the dependency
         * @throws JellyTagException if local variables are not supported
         */
        @Override
        public Dependency getVariableDependency(String name)
                throws JellyTagException
        {
            return chain.getChainDependency(name);
        }
    }
}
