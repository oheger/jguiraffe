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
package net.sf.jguiraffe.gui.builder.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.ConversionHelper;
import net.sf.jguiraffe.di.impl.providers.ConstantBeanProvider;

import org.apache.commons.jelly.JellyContext;

/**
 * <p>
 * A specialized implementation of the <code>BeanStore</code> interface that
 * is backed by a Jelly context.
 * </p>
 * <p>
 * This class provides access to the variables stored in a Jelly context through
 * the methods defined by the {@link BeanStore} interface. This
 * way a Jelly context (e.g. initialized by a builder operation) can
 * transparently be used by the dependency injection framework.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: JellyContextBeanStore.java 205 2012-01-29 18:29:57Z oheger $
 */
public class JellyContextBeanStore implements BeanStore
{
    /** Constant for the default name of the bean store. */
    static final String DEFAULT_NAME = JellyContextBeanStore.class.getName();

    /** Stores the underlying Jelly context. */
    private final JellyContext context;

    /** Stores the parent bean store. */
    private final BeanStore parent;

    /** Stores the name of this bean store. */
    private final String name;

    /**
     * Creates a new instance of <code>JellyContextBeanStore</code> and
     * initializes it with the underlying Jelly context, the parent bean store
     * and the name to be used.
     *
     * @param ctx the Jelly context (must not be <b>null</b>)
     * @param parentStore the parent store
     * @param storeName the name
     * @throws IllegalArgumentException if the passed in Jelly context is
     * <b>null</b>
     */
    public JellyContextBeanStore(JellyContext ctx, BeanStore parentStore,
            String storeName)
    {
        if (ctx == null)
        {
            throw new IllegalArgumentException(
                    "Jelly context must not be null!");
        }
        context = ctx;
        parent = parentStore;
        name = storeName;
    }

    /**
     * Creates a new instance of <code>JellyContextBeanStore</code> and
     * initializes it with the underlying Jelly context and the parent bean
     * store. For the name a default value is used.
     *
     * @param ctx the Jelly context (must not be <b>null</b>)
     * @param parentStore the parent store
     */
    public JellyContextBeanStore(JellyContext ctx, BeanStore parentStore)
    {
        this(ctx, parentStore, DEFAULT_NAME);
    }

    /**
     * Returns the underlying Jelly context.
     *
     * @return the Jelly context
     */
    public JellyContext getContext()
    {
        return context;
    }

    /**
     * Returns a <code>BeanProvider</code> for accessing the bean with the
     * given name. This implementation checks whether the Jelly context contains
     * a variable with the given name. If this is the case, a bean provider
     * serving this value is returned. Otherwise the result of this method is
     * <b>null</b>.
     *
     * @param name the name of the desired bean
     * @return a <code>BeanProvider</code> for this bean
     */
    public BeanProvider getBeanProvider(String name)
    {
        Object var = getContext().findVariable(name);
        return (var != null) ? ConstantBeanProvider.getInstance(var) : null;
    }

    /**
     * Returns the name of this bean store.
     *
     * @return the name of this bean store
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the parent bean store.
     *
     * @return the parent bean store
     */
    public BeanStore getParent()
    {
        return parent;
    }

    /**
     * Returns a set with the names of the existing <code>BeanProvider</code>s.
     * This implementation returns a set with the names of the variables defined
     * in the underlying Jelly context.
     *
     * @return a set with the names of the known bean providers
     */
    public Set<String> providerNames()
    {
        Set<String> result = new HashSet<String>();
        for (Iterator<?> it = getContext().getVariableNames(); it.hasNext();)
        {
            result.add((String) it.next());
        }
        return result;
    }

    /**
     * Returns the {@code ConversionHelper} used by this bean store. This
     * implementation always returns <b>null</b>.
     *
     * @return the {@code ConversionHelper}
     */
    public ConversionHelper getConversionHelper()
    {
        return null;
    }
}
