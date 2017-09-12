/*
 * Copyright 2006-2017 The JGUIraffe Team.
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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.ConversionHelper;
import net.sf.jguiraffe.di.impl.providers.ConstantBeanProvider;

/**
 * <p>
 * A helper class that simplifies implementations of the {@code BeanStore}
 * interface.
 * </p>
 * <p>
 * The purpose of this class is to support {@code BeanStore}
 * implementations based on classes that are not aware of {@link BeanProvider}s,
 * but provide a map-like interface for accessing their data. This is achieved
 * in the following ways:
 * <ul>
 * <li>Static data (in form of arbitrary objects) can directly be added to this
 * class. Internally {@link ConstantBeanProvider} objects are created for the
 * data objects to be managed. These providers are directly exposed through the
 * {@code BeanStore} methods.</li>
 * <li>If dynamic data is involved (i.e. objects that may change over time and
 * cannot be kept in a map), components can implement the internal
 * {@code BeanContributor} interface and register themselves at a
 * {@code SimpleBeanStoreImpl} instance. The {@code BeanConstributor}
 * interface is much easier to implement than the {@code BeanStore}
 * interface. The implementations of the {@code BeanStore} methods provided
 * by this class take the registered {@code BeanContributor}s into account.
 * </li>
 * <li>Methods of the {@code BeanStore} interface that do not directly deal
 * with bean providers are already implemented by this class.</li>
 * </ul>
 * </p>
 * <p>
 * Note: The class per se is not thread-safe. When used inside the
 * <em>dependency
 * injection</em> framework, proper synchronization is automatically applied.
 * But during initialization or for other use cases the developer has to ensure
 * that there are no concurrent accesses.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SimpleBeanStoreImpl.java 213 2012-07-14 19:40:51Z oheger $
 */
public class SimpleBeanStoreImpl implements BeanStore
{
    /** Stores a map with the managed beans. */
    private final Map<String, Object> beans;

    /** A list with the registered contributors. */
    private final Collection<BeanContributor> contributors;

    /** Stores the name of this bean store. */
    private String name;

    /** Stores the parent bean store. */
    private BeanStore parent;

    /** The conversion helper associated with this object. */
    private ConversionHelper conversionHelper;

    /**
     * Creates a new instance of {@code SimpleBeanStoreImpl}.
     */
    public SimpleBeanStoreImpl()
    {
        beans = new HashMap<String, Object>();
        contributors = new LinkedHashSet<BeanContributor>();
    }

    /**
     * Creates a new instance of {@code SimpleBeanStoreImpl} and sets the
     * name and the reference to the parent.
     *
     * @param name the name of this bean store
     * @param parent the reference to the parent bean store
     */
    public SimpleBeanStoreImpl(String name, BeanStore parent)
    {
        this();
        setName(name);
        setParent(parent);
    }

    /**
     * Adds a new {@code BeanContributor} to this object. This
     * contributor will be triggered when this bean store is accessed.
     *
     * @param contr the contributor to be added (must not be <b>null</b>)
     * @throws IllegalArgumentException if the contributor is <b>null</b>
     */
    public void addBeanContributor(BeanContributor contr)
    {
        if (contr == null)
        {
            throw new IllegalArgumentException(
                    "BeanContributor must not be null!");
        }
        contributors.add(contr);
    }

    /**
     * Removes the specified bean contributor from this object.
     *
     * @param contr the contributor to remove
     */
    public void removeBeanContributor(BeanContributor contr)
    {
        contributors.remove(contr);
    }

    /**
     * Adds the specified bean to this store. For the passed in bean a constant
     * bean provider is created. It can then be queried through the
     * {@code getBeanProvider()} method.
     *
     * @param name the name of the bean (must not be <b>null</b>)
     * @param bean the bean (must not be <b>null</b>)
     * @throws IllegalArgumentException if the name or the bean is <b>null</b>
     */
    public void addBean(String name, Object bean)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("Bean name must not be null!");
        }
        if (bean == null)
        {
            throw new IllegalArgumentException("Bean must not be null!");
        }
        beans.put(name, bean);
    }

    /**
     * Removes the bean with the given name.
     *
     * @param name the name of the bean to be removed
     * @return the removed bean (<b>null</b> if the bean was unknown)
     */
    public Object removeBean(String name)
    {
        return beans.remove(name);
    }

    /**
     * Returns a {@code BeanProvider} for the bean with the given name.
     * This implementation checks whether such a bean was directly added using
     * the {@code addBean()} method. If not, the registered bean
     * contributors are consulted. If no such bean can be found, <b>null</b> is
     * returned.
     *
     * @param name the name of the bean in question
     * @return a {@code BeanProvider} for this bean
     */
    public BeanProvider getBeanProvider(String name)
    {
        Object bean = beans.get(name);

        if (bean == null)
        {
            for (BeanContributor contr : contributors)
            {
                bean = contr.getBean(name);
                if (bean != null)
                {
                    break;
                }
            }
        }

        return (bean != null) ? providerFor(bean) : null;
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
     * Sets the name of this bean store.
     *
     * @param name the new name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the parent bean store.
     *
     * @return the parent store
     */
    public BeanStore getParent()
    {
        return parent;
    }

    /**
     * Sets the parent bean store.
     *
     * @param parent the parent bean store
     */
    public void setParent(BeanStore parent)
    {
        this.parent = parent;
    }

    /**
     * Returns the {@code ConversionHelper} associated with this instance.
     *
     * @return the {@code ConversionHelper}
     */
    public ConversionHelper getConversionHelper()
    {
        return conversionHelper;
    }

    /**
     * Sets the {@code ConversionHelper} associated with this object. The object
     * passed to this method will be returned by {@link #getConversionHelper()}.
     *
     * @param conversionHelper the {@code ConversionHelper}
     */
    public void setConversionHelper(ConversionHelper conversionHelper)
    {
        this.conversionHelper = conversionHelper;
    }

    /**
     * Returns a set with the names of the available bean providers. This
     * implementation will first obtain the names of all directly added beans.
     * Then the registered bean contributors are invoked to add their bean names
     * to the resulting list.
     *
     * @return a set with the names of the known bean providers
     */
    public Set<String> providerNames()
    {
        Set<String> beanNames = beans.keySet();

        if (!contributors.isEmpty())
        {
            // copy set so it can be modified
            beanNames = new HashSet<String>(beanNames);

            for (BeanContributor contr : contributors)
            {
                contr.beanNames(beanNames);
            }
        }

        return beanNames;
    }

    /**
     * Returns a bean provider for the specified bean. This method is called
     * whenever a bean has to be wrapped by a provider. This default
     * implementation returns a {@code ConstantBeanProvider} for the
     * passed in bean.
     *
     * @param bean the bean
     * @return a provider for this bean
     */
    protected BeanProvider providerFor(Object bean)
    {
        return ConstantBeanProvider.getInstance(bean);
    }

    /**
     * <p>
     * Definition of an interface for objects that can contribute beans for a
     * {@code SimpleBeanStoreImpl} object.
     * </p>
     * <p>
     * The methods defined in this interface allow an implementation to deliver
     * plain data objects (in contrast to {@code BeanProvider} objects.
     * The implementations of the {@code BeanStore} methods delegate to
     * these methods when the bean store is accessed.
     * </p>
     */
    public interface BeanContributor
    {
        /**
         * Obtains the names of the beans available by this contributor. This
         * method is invoked by the {@code providerNames()} method.
         *
         * @param names a set, in which to store the names of the available
         * beans
         */
        void beanNames(Set<String> names);

        /**
         * Returns the bean with the given name or <b>null</b> if the name is
         * unknown. When queried for a bean the {@code BeanStore}
         * implementation will iterate over all registered contributors and call
         * this method. The first non <b>null</b> value is returned.
         *
         * @param name the name of the queried bean
         * @return the bean with this name or <b>null</b>
         */
        Object getBean(String name);
    }
}
