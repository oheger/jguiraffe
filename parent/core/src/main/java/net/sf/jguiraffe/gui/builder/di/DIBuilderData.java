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
package net.sf.jguiraffe.gui.builder.di;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.InvocationHelper;
import net.sf.jguiraffe.di.MutableBeanStore;
import net.sf.jguiraffe.di.impl.DefaultBeanStore;
import net.sf.jguiraffe.di.impl.DefaultClassLoaderProvider;

import org.apache.commons.jelly.JellyContext;

/**
 * <p>
 * A data class for maintaining all information required for a DI builder
 * operation.
 * </p>
 * <p>
 * An instance of this class will be present in the Jelly context when a Jelly
 * script for populating a <code>BeanContext</code> is running. The specific tag
 * handler classes can access the information they need for adding newly created
 * beans to the correct <code>BeanStore</code> or for accessing required helper
 * objects.
 * </p>
 * <p>
 * One important task of this class is dealing with <code>BeanStore</code>
 * objects: One root bean store must be provided, to which bean definitions are
 * added per default. In a builder script, it is possible to create an arbitrary
 * number of further bean stores below this root store. In a bean definition the
 * target bean store (where the bean should be stored) can be specified.
 * </p>
 * <p>
 * This class also maintains the current {@link InvocationHelper} instance which
 * is responsible for reflection operations and (indirectly) for data type
 * conversions required by the current builder script. Either an instance of
 * {@link InvocationHelper} can be set explicitly using the
 * {@link #setInvocationHelper(InvocationHelper)} method or a default instance
 * will be created. If the root {@link BeanStore} is created by this object, the
 * {@link net.sf.jguiraffe.di.ConversionHelper ConversionHelper} will be set
 * automatically. If the root {@link BeanStore} is set manually using the
 * {@link #initRootBeanStore(MutableBeanStore)} method, the caller is
 * responsible for initializing the root store with the correct
 * {@link net.sf.jguiraffe.di.ConversionHelper ConversionHelper} instance.
 * </p>
 * <p>
 * Implementation note: This class is not thread-safe. It is intended to be used
 * inside a Jelly script, which runs in a single thread.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: DIBuilderData.java 205 2012-01-29 18:29:57Z oheger $
 */
public class DIBuilderData
{
    /** Constant for the key for storing an instance in the Jelly context. */
    private static final String CTX_KEY = DIBuilderData.class.getName();

    /** A counter for generating unique indices for anonymous bean providers. */
    private static final AtomicInteger ANONYMOUS_COUNTER = new AtomicInteger();

    /** Stores the <code>ClassLoaderProvider</code> to be used. */
    private ClassLoaderProvider classLoaderProvider;

    /** Stores a map with the known bean stores. */
    private Map<String, MutableBeanStore> beanStores;

    /** Stores the root bean store. */
    private MutableBeanStore rootBeanStore;

    /** The current invocation helper. */
    private InvocationHelper invocationHelper;

    /**
     * Creates a new instance of <code>DIBuilderData</code>.
     */
    public DIBuilderData()
    {
        beanStores = new HashMap<String, MutableBeanStore>();
    }

    /**
     * Returns the <code>ClassLoaderProvider</code> to be used. This method
     * never returns <b>null</b>. If no specific
     * <code>ClassLoaderProvider</code> has been set so far (by using the
     * {@link #setClassLoaderProvider(ClassLoaderProvider)}
     * method), a new default provider is created and returned, which does not
     * contain any specific registered class loaders.
     *
     * @return the <code>ClassLoaderProvider</code> to be used
     */
    public ClassLoaderProvider getClassLoaderProvider()
    {
        if (classLoaderProvider == null)
        {
            // create default instance
            classLoaderProvider = new DefaultClassLoaderProvider();
        }
        return classLoaderProvider;
    }

    /**
     * Sets the <code>ClassLoaderProvider</code> to be used. This object is
     * especially required for resolving <code>ClassDescription</code> objects.
     *
     * @param classLoaderProvider the <code>ClassLoaderProvider</code> to be
     *        used
     */
    public void setClassLoaderProvider(ClassLoaderProvider classLoaderProvider)
    {
        this.classLoaderProvider = classLoaderProvider;
    }

    /**
     * Returns the current {@link InvocationHelper}. This object can be used by
     * all components involved in the builder process if reflection operations
     * or data type conversions are needed. If custom type converters are
     * needed, they have to be registered at conversion helper managed by this
     * instance.
     *
     * @return the {@link InvocationHelper}
     * @see #setInvocationHelper(InvocationHelper)
     */
    public InvocationHelper getInvocationHelper()
    {
        if (invocationHelper == null)
        {
            invocationHelper = new InvocationHelper();
        }
        return invocationHelper;
    }

    /**
     * Sets the {@link InvocationHelper} to be used by this object. Using this
     * method a specific {@link InvocationHelper} instance can be set which will
     * then be returned by {@link #getInvocationHelper()}. If
     * {@link #getInvocationHelper()} is called and no helper object has been
     * set, a new default instance is created automatically.
     *
     * @param invocationHelper the {@link InvocationHelper} to be used
     */
    public void setInvocationHelper(InvocationHelper invocationHelper)
    {
        this.invocationHelper = invocationHelper;
    }

    /**
     * Returns the root bean store. This method returns never <b>null</b>; if no
     * root bean store has been set so far, a default one is created now.
     *
     * @return the root bean store
     */
    public BeanStore getRootBeanStore()
    {
        return getBeanStore(null);
    }

    /**
     * Sets the root bean store. This is the default bean store, to which all
     * beans are added unless a specific bean store is specified. Using the
     * <code>addBeanStore()</code> method it is possible to create further bean
     * stores below this root store.
     *
     * @param rootBeanStore the root bean store
     */
    public void initRootBeanStore(MutableBeanStore rootBeanStore)
    {
        this.rootBeanStore = rootBeanStore;
    }

    /**
     * Returns the <code>BeanStore</code> with the specified name. The root
     * store can be obtained by passing in <b>null</b> for the name. If a name
     * cannot be resolved, an exception is thrown.
     *
     * @param name the name of the desired bean store
     * @return the bean store with this name
     * @throws NoSuchElementException if the name cannot be resolved
     */
    public BeanStore getBeanStore(String name)
    {
        return internalGetBeanStore(name);
    }

    /**
     * Returns a flag whether the bean store with the given name is present.
     *
     * @param name the name of the bean store in question
     * @return a flag whether this bean store is present
     */
    public boolean hasBeanStore(String name)
    {
        return name == null || beanStores.containsKey(name);
    }

    /**
     * Returns a set with the names of the existing bean stores. These are the
     * bean stores that have been added using the <code>addBeanStore()</code>
     * method. Note that the root bean store (which is identified by the name
     * <b>null</b>) is not contained in this set.
     *
     * @return a set with the names of the existing bean stores
     */
    public Set<String> getBeanStoreNames()
    {
        return beanStores.keySet();
    }

    /**
     * Creates a <code>BeanStore</code> with the specified name and adds it to
     * the internal list of existing bean stores. The new store is added as a
     * child of the specified parent bean store. If <b>null</b> is passed in for
     * the parent, the new store will become a child of the root bean store.
     * Note that the names of bean stores must be unique, even if they are at
     * different levels of the hierarchy.
     *
     * @param name the name of the bean store to be added (must not be
     *        <b>null</b>)
     * @param parentName the name of the parent bean store
     * @throws NoSuchElementException if the parent name cannot be resolved
     * @throws IllegalArgumentException if the name is <b>null</b> or is already
     *         used by an existing bean store
     */
    public void addBeanStore(String name, String parentName)
    {
        if (name == null)
        {
            throw new IllegalArgumentException(
                    "Name of bean store must not be null!");
        }
        if (hasBeanStore(name))
        {
            throw new IllegalArgumentException(
                    "A bean store with this name already exists: " + name);
        }

        DefaultBeanStore store =
                new DefaultBeanStore(name, internalGetBeanStore(parentName));
        beanStores.put(name, store);
    }

    /**
     * Adds a <code>BeanProvider</code> to a <code>BeanStore</code>. The bean
     * store with the specified name is resolved (<b>null</b> selects the root
     * bean store), and the provided <code>BeanProvider</code> is added to it
     * under the given name.
     *
     * @param storeName the name of the bean store
     * @param beanName the name of the bean provider (must not be <b>null</b>)
     * @param bean the bean provider to be added (must not be <b>null</b>)
     * @throws NoSuchElementException if the bean store cannot be resolved
     * @throws IllegalArgumentException if the bean name or the bean provider is
     *         <b>null</b>
     */
    public void addBeanProvider(String storeName, String beanName,
            BeanProvider bean)
    {
        if (beanName == null)
        {
            throw new IllegalArgumentException(
                    "Name of bean provider must not be null!");
        }
        if (bean == null)
        {
            throw new IllegalArgumentException(
                    "Bean provider must not be null!");
        }

        internalGetBeanStore(storeName).addBeanProvider(beanName, bean);
    }

    /**
     * Adds an &quot;anonymous&quot; <code>BeanProvider</code> to a
     * <code>BeanStore</code>. This method can be used for beans that are
     * visible in a local context only. It works similar to
     * <code>addBeanProvider()</code>, but the bean store's
     * <code>addAnonymousBeanProvider()</code> method will be called. This class
     * will keep a counter for generating indices for anonymous bean providers.
     * This ensures that the generated names used internally for these beans are
     * unique.
     *
     * @param storeName the name of the bean store
     * @param bean the bean provider to be added (must not be <b>null</b>)
     * @return the name generated for the bean provider added
     * @throws IllegalArgumentException if no provider is specified
     */
    public String addAnonymousBeanProvider(String storeName, BeanProvider bean)
    {
        if (bean == null)
        {
            throw new IllegalArgumentException(
                    "Bean provider must not be null!");
        }

        return internalGetBeanStore(storeName).addAnonymousBeanProvider(
                nextAnonymousIndex(), bean);
    }

    /**
     * Stores this instance in the specified context. It is stored there under a
     * default key.
     *
     * @param context the Jelly context
     */
    public void put(JellyContext context)
    {
        context.setVariable(CTX_KEY, this);
    }

    /**
     * Obtains an instance of this class from the specified Jelly context. The
     * instance is looked up under a default key, the same that is also used by
     * the <code>put()</code> method. If no instance can be found in this
     * context, <b>null</b> is returned.
     *
     * @param context the Jelly context
     * @return the instance found in this context
     */
    public static DIBuilderData get(JellyContext context)
    {
        return (DIBuilderData) context.getVariable(CTX_KEY);
    }

    /**
     * Returns the internal bean store with the given name. Internally this
     * class operates on mutable {@link DefaultBeanStore} objects. To its
     * clients only the immutable {@link BeanStore} interface is exposed.
     *
     * @param name the name of the bean store to be retrieved (<b>null</b> means
     *        the root bean store)
     * @return the bean store with this name
     * @throws NoSuchElementException if the bean store cannot be resolved
     */
    protected MutableBeanStore internalGetBeanStore(String name)
    {
        if (name == null)
        {
            // root bean store already created?
            if (rootBeanStore == null)
            {
                rootBeanStore = createRootStore();
            }
            return rootBeanStore;
        }

        MutableBeanStore result = beanStores.get(name);
        if (result == null)
        {
            throw new NoSuchElementException("Unknown bean store: " + name);
        }
        return result;
    }

    /**
     * Creates the root bean store. This method is called by
     * <code>internalGetBeanStore()</code> when the root bean store is accessed
     * for the first time and has not been explicitly initialized.
     *
     * @return the new root bean store
     */
    protected MutableBeanStore createRootStore()
    {
        DefaultBeanStore root = new DefaultBeanStore();
        root.setConversionHelper(getInvocationHelper().getConversionHelper());
        return root;
    }

    /**
     * Returns the next index for an anonymous bean provider.
     *
     * @return the next index
     */
    private static int nextAnonymousIndex()
    {
        return ANONYMOUS_COUNTER.incrementAndGet();
    }
}
