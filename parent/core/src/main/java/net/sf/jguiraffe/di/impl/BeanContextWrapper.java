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
package net.sf.jguiraffe.di.impl;

import java.util.Set;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.di.BeanCreationListener;
import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.ClassLoaderProvider;

/**
 * <p>
 * A simple wrapper implementation of the <code>BeanContext</code> interface.
 * </p>
 * <p>
 * The main purpose of this class is to override the default
 * <code>BeanStore</code> of a bean context. It is initialized with the
 * <code>BeanContext</code> object to be wrapped and the default
 * <code>BeanStore</code> to be used. All calls to methods defined by the
 * <code>BeanContext</code> interface are delegated to the wrapped bean context
 * object. If they address the default bean store (i.e. no bean store is
 * explicitly passed to the method), the bean store set for this instance is
 * used. That way an already configured bean context can be used in a different
 * context (which means, using another bean store as default entry in the look
 * up mechanism) without manipulating its state.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: BeanContextWrapper.java 205 2012-01-29 18:29:57Z oheger $
 */
public class BeanContextWrapper implements BeanContext
{
    /** Stores the wrapped bean context. */
    private final BeanContext context;

    /** Stores the default bean store to be used. */
    private BeanStore defaultBeanStore;

    /** A helper object for managing bean creation listeners. */
    private final BeanCreationListenerSupport beanCreationListeners;

    /**
     * Creates a new instance of <code>BeanContextWrapper</code> and sets the
     * wrapped context.
     *
     * @param wrappedContext the bean context to be wrapped (must not be
     *        <b>null</b>)
     * @throws IllegalArgumentException if the bean context is <b>null</b>
     */
    public BeanContextWrapper(BeanContext wrappedContext)
    {
        this(wrappedContext, null);
    }

    /**
     * Creates a new instance of <code>BeanContextWrapper</code> and sets the
     * wrapped context and the default bean store.
     *
     * @param wrappedContext the bean context to be wrapped (must not be
     *        <b>null</b>)
     * @param defaultStore the default bean store ton be used
     * @throws IllegalArgumentException if the bean context is <b>null</b>
     */
    public BeanContextWrapper(BeanContext wrappedContext, BeanStore defaultStore)
    {
        if (wrappedContext == null)
        {
            throw new IllegalArgumentException(
                    "Underlying context must not be null!");
        }

        context = wrappedContext;
        setDefaultBeanStore(defaultStore);
        beanCreationListeners = new BeanCreationListenerSupport(this);
        initWrappedContext(wrappedContext, beanCreationListeners);
    }

    /**
     * Returns the wrapped bean context.
     *
     * @return a reference to the original bean context
     */
    public BeanContext getWrappedContext()
    {
        return context;
    }

    /**
     * Returns a set of the classes of the beans available in this bean
     * context's default bean store. This implementation delegates to the
     * wrapped context.
     *
     * @return a set with the classes of the defined beans
     */
    public Set<Class<?>> beanClasses()
    {
        return getWrappedContext().beanClasses(getDefaultBeanStore());
    }

    /**
     * Returns a set of the classes of the beans available in the specified bean
     * store. This implementation delegates to the wrapped context.
     *
     * @param store the bean store
     * @return a set with the classes of the defined beans
     */
    public Set<Class<?>> beanClasses(BeanStore store)
    {
        return getWrappedContext().beanClasses(store);
    }

    /**
     * Returns a set with the names of the beans defined in this context's
     * default bean store. This implementation delegates to the wrapped context.
     *
     * @return a set with the names of the defined beans
     */
    public Set<String> beanNames()
    {
        return getWrappedContext().beanNames(getDefaultBeanStore());
    }

    /**
     * Returns a set with the names of the beans defined in the given bean
     * store. This implementation delegates to the wrapped context.
     *
     * @param store the bean store
     * @return a set with the names of the defined beans
     */
    public Set<String> beanNames(BeanStore store)
    {
        return getWrappedContext().beanNames(store);
    }

    /**
     * Checks whether a bean with the given name can be found in the default
     * bean store of this bean context. This implementation delegates to the
     * wrapped context.
     *
     * @param name the name of the searched bean
     * @return a flag whether this bean can be found
     */
    public boolean containsBean(String name)
    {
        return getWrappedContext().containsBean(name, getDefaultBeanStore());
    }

    /**
     * Checks whether a bean with the given name can be found in the specified
     * bean store. This implementation delegates to the wrapped context.
     *
     * @param name the name of the bean
     * @param store the bean store
     * @return a flag whether this bean can be found
     */
    public boolean containsBean(String name, BeanStore store)
    {
        return getWrappedContext().containsBean(name, store);
    }

    /**
     * Checks whether a bean with the given class can be found in this bean
     * context's default bean store. This implementation delegates to the
     * wrapped context.
     *
     * @param beanClass the class of the bean
     * @return a flag whether this bean can be found
     */
    public boolean containsBean(Class<?> beanClass)
    {
        return getWrappedContext().containsBean(beanClass,
                getDefaultBeanStore());
    }

    /**
     * Checks whether a bean with the given class can be found in the specified
     * bean store. This implementation delegates to the wrapped context.
     *
     * @param beanClass the class of the bean
     * @param store the bean store
     * @return a flag whether this bean can be found
     */
    public boolean containsBean(Class<?> beanClass, BeanStore store)
    {
        return getWrappedContext().containsBean(beanClass, store);
    }

    /**
     * Returns the bean with the given name starting the search with the default
     * bean store. This implementation delegates to the wrapped context.
     *
     * @param name the name of the desired bean
     * @return the bean
     */
    public Object getBean(String name)
    {
        return getWrappedContext().getBean(name, getDefaultBeanStore());
    }

    /**
     * Returns the bean with the given name starting the search with the
     * specified bean store. This implementation delegates to the wrapped
     * context.
     *
     * @param name the name of the desired bean
     * @param store the bean store to start with
     * @return the bean
     */
    public Object getBean(String name, BeanStore store)
    {
        return getWrappedContext().getBean(name, store);
    }

    /**
     * Returns the bean with the given class starting the search with the
     * default bean store. This implementation delegates to the wrapped context.
     *
     * @param <T> the type of the bean
     * @param beanCls the class of the desired bean
     * @return the bean
     */
    public <T> T getBean(Class<T> beanCls)
    {
        return getWrappedContext().getBean(beanCls, getDefaultBeanStore());
    }

    /**
     * Returns the bean with the given class starting the search with the
     * specified bean store. This implementation delegates to the wrapped
     * context.
     *
     * @param <T> the type of the bean
     * @param beanCls the class of the desired bean
     * @param store the store to start with
     * @return the bean
     */
    public <T> T getBean(Class<T> beanCls, BeanStore store)
    {
        return getWrappedContext().getBean(beanCls, store);
    }

    /**
     * Returns the name of the given {@code BeanProvider} starting the search
     * with the default bean store. This implementation delegates to the wrapped
     * context.
     *
     * @param beanProvider the {@code BeanProvider}
     * @return the name of this {@code BeanProvider} or <b>null</b>
     */
    public String beanNameFor(BeanProvider beanProvider)
    {
        return getWrappedContext().beanNameFor(beanProvider,
                getDefaultBeanStore());
    }

    /**
     * Returns the name of the given {@code BeanProvider} starting the search
     * with the specified bean store. This implementation delegates to the
     * wrapped context.
     *
     * @param beanProvider the {@code BeanProvider}
     * @param store the {@code BeanStore}
     * @return the name of this {@code BeanProvider} or <b>null</b>
     */
    public String beanNameFor(BeanProvider beanProvider, BeanStore store)
    {
        return getWrappedContext().beanNameFor(beanProvider, store);
    }

    /**
     * Returns the default bean store. This implementation returns the default
     * store set for this wrapped context and not the one of the underlying
     * context.
     *
     * @return the default bean store
     */
    public BeanStore getDefaultBeanStore()
    {
        return defaultBeanStore;
    }

    /**
     * Sets the default bean store. This implementation only changes the default
     * bean store of this wrapped context. The default store of the underlying
     * context is not touched.
     *
     * @param store the new default bean store
     */
    public void setDefaultBeanStore(BeanStore store)
    {
        defaultBeanStore = store;
    }

    /**
     * Adds the specified {@code BeanCreationListener} to this context. This
     * implementation ensures that events from the wrapped context are received.
     * However, the events are correctly transformed so that this context is set
     * as the source context of the event.
     *
     * @param l the listener to be added
     */
    public void addBeanCreationListener(BeanCreationListener l)
    {
        getBeanCreationListeners().addBeanCreationListener(l);
    }

    /**
     * Removes the specified {@code BeanCreationListener} from this context.
     *
     * @param l the listener to be removed
     */
    public void removeBeanCreationListener(BeanCreationListener l)
    {
        getBeanCreationListeners().removeBeanCreationListener(l);
    }

    /**
     * Closes this {@code BeanContext}. This implementation removes the {@code
     * BeanCreationListener} registered at the wrapped context. Note that the
     * wrapped context will not be closed! This is because the wrapped context
     * is typically shared.
     */
    public void close()
    {
        getWrappedContext().removeBeanCreationListener(
                getBeanCreationListeners());
    }

    /**
     * Returns the {@code ClassLoaderProvider} used by this context. This
     * implementation delegates to the wrapped context.
     *
     * @return the {@code ClassLoaderProvider}
     */
    public ClassLoaderProvider getClassLoaderProvider()
    {
        return getWrappedContext().getClassLoaderProvider();
    }

    /**
     * Sets the {@code ClassLoaderProvider} to be used by this context. This
     * implementation delegates to the wrapped context.
     *
     * @param clp the new {@code ClassLoaderProvider}
     */
    public void setClassLoaderProvider(ClassLoaderProvider clp)
    {
        getWrappedContext().setClassLoaderProvider(clp);
    }

    /**
     * Returns the helper object for managing bean creation listeners.
     *
     * @return the bean creation listeners
     */
    BeanCreationListenerSupport getBeanCreationListeners()
    {
        return beanCreationListeners;
    }

    /**
     * Initializes the wrapped context. This method is called by the
     * constructor. It registers the support object as bean creation listener at
     * the wrapped context, so that the correct source context can be set.
     *
     * @param wrappedContext the wrapped context
     * @param support the support object
     */
    void initWrappedContext(BeanContext wrappedContext,
            BeanCreationListenerSupport support)
    {
        wrappedContext.addBeanCreationListener(support);
    }
}
