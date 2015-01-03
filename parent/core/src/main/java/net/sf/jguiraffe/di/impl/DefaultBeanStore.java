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
package net.sf.jguiraffe.di.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.ConversionHelper;
import net.sf.jguiraffe.di.MutableBeanStore;

import org.apache.commons.lang.ObjectUtils;

/**
 * <p>
 * A simple yet fully functional default implementation of the
 * <code>BeanStore</code> interface.
 * </p>
 * <p>
 * This implementation is based on a <code>HashMap</code>.
 * <code>BeanProvider</code> objects can be added to this map using the
 * <code>addBeanProvider()</code> method. They can then be queried through the
 * <code>getBeanProvider</code> method.
 * </p>
 * <p>
 * Note: This implementation is not thread-safe. The underlying map is not
 * synchronized. This does not cause any problems when used read-only by the
 * dependency injection framework. But if bean providers should be concurrently
 * added or other properties are to be manipulated, manual synchronization is
 * required. The intended use case is that an instance is created and populated
 * with {@link BeanProvider} objects in an initialization phase. Then it should
 * only be accessed in a read-only fashion by the dependency injection
 * framework.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: DefaultBeanStore.java 205 2012-01-29 18:29:57Z oheger $
 */
public class DefaultBeanStore implements MutableBeanStore
{
    /** Constant for the prefix used for anonymous bean providers. */
    private static final String PREFIX_ANONYMOUS = "_jguiraffe.anonymousBean_";

    /** A map with the contained bean providers. */
    private final Map<String, BeanProvider> providers;

    /** Stores the parent store. */
    private BeanStore parent;

    /** The conversion helper associated with this bean store. */
    private ConversionHelper conversionHelper;

    /** Stores the name of this bean store. */
    private String name;

    /**
     * Creates a new instance of <code>DefaultBeanStore</code>.
     */
    public DefaultBeanStore()
    {
        providers = new HashMap<String, BeanProvider>();
    }

    /**
     * Creates a new instance of <code>DefaultBeanStore</code> and sets the name
     * and the reference to the parent.
     *
     * @param name the name of this bean store
     * @param parent the reference to the parent bean store
     */
    public DefaultBeanStore(String name, BeanStore parent)
    {
        this();
        setName(name);
        setParent(parent);
    }

    /**
     * Returns the {@code ConversionHelper} associated with this object.
     *
     * @return the {@code ConversionHelper}
     */
    public ConversionHelper getConversionHelper()
    {
        return conversionHelper;
    }

    /**
     * Sets the {@code ConversionHelper} object to be associated with this
     * instance. The object passed to this method is returned by
     * {@link #getConversionHelper()}.
     *
     * @param conversionHelper the {@code ConversionHelper} object
     */
    public void setConversionHelper(ConversionHelper conversionHelper)
    {
        this.conversionHelper = conversionHelper;
    }

    /**
     * A convenience method for retrieving a {@code ConversionHelper} object
     * from a hierarchy of bean stores. This method queries the specified
     * {@code BeanStore} for its {@code ConversionHelper}. If it has one, the
     * helper object is returned. Otherwise, the parent {@code BeanStore} is
     * queried. This continues until a {@code ConversionHelper} object is found
     * or the top of the hierarchy is reached. If no {@code ConversionHelper}
     * can be found and the {@code createIfNecessary} flag is <b>true</b>, a new
     * default helper object is created. Otherwise, the method returns
     * <b>null</b> if no helper can be found.
     *
     * @param store the {@code BeanStore} where to start the search
     * @param createIfNecessary a flag whether a default helper instance should
     *        be created if none can be found
     * @return the found {@code ConversionHelper} or <b>null</b>
     */
    public static ConversionHelper fetchConversionHelper(BeanStore store,
            boolean createIfNecessary)
    {
        BeanStore currentStore = store;

        while (currentStore != null)
        {
            ConversionHelper ch = currentStore.getConversionHelper();
            if (ch != null)
            {
                return ch;
            }
            currentStore = currentStore.getParent();
        }

        return createIfNecessary ? new ConversionHelper() : null;
    }

    /**
     * Adds the specified <code>BeanProvider</code> to this bean store under the
     * given name.
     *
     * @param name the name of the bean provider (must not be <b>null</b>)
     * @param provider the <code>BeanProvider</code> to be registered
     * @throws IllegalArgumentException if the name or the provider is
     *         <b>null</b>
     */
    public void addBeanProvider(String name, BeanProvider provider)
    {
        if (name == null)
        {
            throw new IllegalArgumentException(
                    "Name of bean provider must not be null!");
        }
        if (provider == null)
        {
            throw new IllegalArgumentException(
                    "Bean providern must not be null!");
        }

        providers.put(name, provider);
    }

    /**
     * Adds an anonymous <code>BeanProvider</code>. This method will generate a
     * special name (which is mainly used internally) for the bean provider to
     * add and store it under this name. The name is returned.
     *
     * @param index the index of the bean provider
     * @param provider the <code>BeanProvider</code> to be added (must not be
     *        <b>null</b>)
     * @return the name used for this <code>BeanProvider</code>
     * @throws IllegalArgumentException if the provider is <b>null</b>
     */
    public String addAnonymousBeanProvider(int index, BeanProvider provider)
    {
        String name = PREFIX_ANONYMOUS + index;
        addBeanProvider(name, provider);
        return name;
    }

    /**
     * Removes the <code>BeanProvider</code> with the specified name from this
     * bean store. If this provider cannot be found, this operation has no
     * effect.
     *
     * @param name the name of the provider to remove
     * @return a reference to the removed provider or <b>null</b> if it could
     *         not be found
     */
    public BeanProvider removeBeanProvider(String name)
    {
        return providers.remove(name);
    }

    /**
     * Removes all <code>BeanProvider</code>s from this bean store.
     */
    public void clear()
    {
        providers.clear();
    }

    /**
     * Returns the <code>BeanProvider</code> with the specified name. If no such
     * element exists, <b>null</b> is returned.
     *
     * @param name the name of the desired provider
     * @return the <code>BeanProvider</code> with this name
     */
    public BeanProvider getBeanProvider(String name)
    {
        return providers.get(name);
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
     * @param n the new name
     */
    public void setName(String n)
    {
        name = n;
    }

    /**
     * Returns the parent of this bean store or <b>null</b> if this is a top
     * level store.
     *
     * @return the parent of this bean store
     */
    public BeanStore getParent()
    {
        return parent;
    }

    /**
     * Sets the parent for this bean store.
     *
     * @param p the parent
     */
    public void setParent(BeanStore p)
    {
        parent = p;
    }

    /**
     * Returns a set with the names of all contained bean providers. This
     * implementation ensures that the names of anonymous bean providers do not
     * appear in the set returned.
     *
     * @return the names of the registered bean providers
     */
    public Set<String> providerNames()
    {
        Set<String> names = new HashSet<String>(providers.keySet());

        // remove names of anonymous providers
        for (Iterator<String> it = names.iterator(); it.hasNext();)
        {
            if (it.next().startsWith(PREFIX_ANONYMOUS))
            {
                it.remove();
            }
        }

        return Collections.unmodifiableSet(names);
    }

    /**
     * Returns a string representation of this object. This implementation
     * returns a string listing all bean providers that belong to this store.
     *
     * @return a string for this object
     */
    @Override
    public String toString()
    {
        StringBuilder buf =
                new StringBuilder(ObjectUtils.identityToString(this));
        buf.append("[ name = ").append(getName());
        buf.append(" providers = { ");
        boolean first = true;
        for (Map.Entry<String, BeanProvider> e : providers.entrySet())
        {
            if (first)
            {
                first = false;
            }
            else
            {
                buf.append(", ");
            }
            buf.append(e.getKey()).append(" = ").append(e.getValue());
        }
        buf.append(" } ]");
        return buf.toString();
    }
}
