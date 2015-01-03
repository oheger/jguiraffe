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
package net.sf.jguiraffe.di;

import java.util.EventObject;

/**
 * <p>
 * An event class for reporting the creation of a bean by the dependency
 * injection framework.
 * </p>
 * <p>
 * Objects of this event class are received by {@link BeanCreationListener}
 * implementations, which can be registered at a {@link BeanContext} object.
 * Whenever the {@code BeanContext} is queried for a bean, and this bean has to
 * be newly created (e.g. because of the first access of a singleton bean or
 * because it is a factory bean), an event of this type is triggered.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: BeanCreationEvent.java 205 2012-01-29 18:29:57Z oheger $
 */
public class BeanCreationEvent extends EventObject
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 3858761676828030611L;

    /** Stores the bean provider responsible for the creation. */
    private final transient BeanProvider beanProvider;

    /** The bean that was newly created. */
    private final Object bean;

    /** The dependency provider involved in the creation process. */
    private final transient DependencyProvider dependencyProvider;

    /**
     * Creates a new instance of {@code BeanCreationEvent} and initializes it.
     *
     * @param source the {@code BeanContext} that caused this event
     * @param provider the {@code BeanProvider} that created the bean
     * @param depProvider the {@code DependencyProvider} used for creating the
     *        bean
     * @param newBean the newly created bean
     */
    public BeanCreationEvent(BeanContext source, BeanProvider provider,
            DependencyProvider depProvider, Object newBean)
    {
        super(source);
        beanProvider = provider;
        dependencyProvider = depProvider;
        bean = newBean;
    }

    /**
     * Returns the {@code BeanContext} that caused this event.
     *
     * @return the source {@code BeanContext}
     */
    public BeanContext getBeanContext()
    {
        return (BeanContext) getSource();
    }

    /**
     * Returns the {@code BeanProvider} that created the new bean.
     *
     * @return the responsible {@code BeanProvider}
     */
    public BeanProvider getBeanProvider()
    {
        return beanProvider;
    }

    /**
     * Returns the {@code DependencyProvider} involved in the bean creation
     * process. This is the object that was passed to the {@code BeanProvider}
     * when it created the new bean.
     *
     * @return the {@code DependencyProvider} involved when creating the bean
     */
    public DependencyProvider getDependencyProvider()
    {
        return dependencyProvider;
    }

    /**
     * Returns the newly created bean.
     *
     * @return the bean
     */
    public Object getBean()
    {
        return bean;
    }
}
