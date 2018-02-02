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
package net.sf.jguiraffe.di.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.di.BeanCreationEvent;
import net.sf.jguiraffe.di.BeanCreationListener;
import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.DependencyProvider;

/**
 * <p>
 * A helper class that provides functionality for maintaining a collection of
 * {@link BeanCreationListener} objects.
 * </p>
 * <p>
 * There are multiple {@link BeanContext} implementations that require support
 * for listeners for bean creation events. This class implements basic
 * functionality for keeping a list of event listeners and firing events. There
 * is also support for transforming events triggered by another context, which
 * is needed by {@code BeanContext} implementations that wrap other contexts.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: BeanCreationListenerSupport.java 205 2012-01-29 18:29:57Z oheger $
 */
class BeanCreationListenerSupport implements BeanCreationListener
{
    /** Stores the context associated with this helper object. */
    private final BeanContext context;

    /** A list with the registered listeners. */
    private final List<BeanCreationListener> creationListeners;

    /**
     * Creates a new instance of {@code BeanCreationListenerSupport} and
     * initializes it with the associated {@code BeanContext}.
     *
     * @param ctx the associated context
     */
    public BeanCreationListenerSupport(BeanContext ctx)
    {
        context = ctx;
        creationListeners = new CopyOnWriteArrayList<BeanCreationListener>();
    }

    /**
     * Returns the {@code BeanContext} this helper object is associated with.
     *
     * @return the associated {@code BeanContext}
     */
    public BeanContext getContext()
    {
        return context;
    }

    /**
     * Adds a {@code BeanCreationListener} to this object. This listener will be
     * notified whenever the {@code fireBeanCreationEvent()} method is called.
     *
     * @param l the listener to be added (must not be <b>null</b>)
     * @throws IllegalArgumentException if the listener is <b>null</b>
     */
    public void addBeanCreationListener(BeanCreationListener l)
    {
        if (l == null)
        {
            throw new IllegalArgumentException(
                    "Creation listener must not be null!");
        }

        creationListeners.add(l);
    }

    /**
     * Removes a {@code BeanCreationListener} from this object.
     *
     * @param l the listener to remove
     * @return a flag whether the listener could be removed successfully
     */
    public boolean removeBeanCreationListener(BeanCreationListener l)
    {
        return creationListeners.remove(l);
    }

    /**
     * Notifies all {@code BeanCreationListener} objects registered at this
     * object about the creation of a new bean. From the passed in parameters a
     * {@code BeanCreationEvent} is created and passed to the listeners.
     *
     * @param beanProvider the {@code BeanProvider} that created the bean
     * @param depProvider the {@code DependencyProvider} involved in the
     *        creation
     * @param bean the new bean
     */
    public void fireBeanCreationEvent(BeanProvider beanProvider,
            DependencyProvider depProvider, Object bean)
    {
        BeanCreationEvent event = null;

        for (BeanCreationListener l : creationListeners)
        {
            if (event == null)
            {
                event = new BeanCreationEvent(getContext(), beanProvider,
                        depProvider, bean);
            }
            l.beanCreated(event);
        }
    }

    /**
     * Notifies this object about the creation of a new bean. This class is
     * itself a {@code BeanCreationListener}. Receiving {@code
     * BeanCreationEvent} events makes sense for implementations of the {@code
     * BeanContext} interface that wrap other {@code BeanContext} instances. In
     * this case typically bean creation events triggered by the wrapped
     * contexts have to be caught and converted into events pointing to the
     * wrapping context. This implementation does exactly this: It calls {@code
     * fireBeanCreationEvent()} with the parameters of a new {@code
     * BeanCreationEvent} that has the associated {@code BeanContext} as its
     * source.
     *
     * @param event the event
     */
    public void beanCreated(BeanCreationEvent event)
    {
        event.getDependencyProvider().setCreationBeanContext(getContext());
        fireBeanCreationEvent(event.getBeanProvider(), event
                .getDependencyProvider(), event.getBean());
    }

    /**
     * Returns the list with the listeners registered at this object.
     *
     * @return a list with the registered listeners
     */
    List<BeanCreationListener> getCreationListeners()
    {
        return creationListeners;
    }
}
