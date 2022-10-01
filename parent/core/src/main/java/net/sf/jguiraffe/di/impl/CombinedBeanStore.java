/*
 * Copyright 2006-2022 The JGUIraffe Team.
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.ConversionHelper;

/**
 * <p>
 * A specialized implementation of the {@code BeanStore} interface that combines
 * multiple physical {@code BeanStore} objects to a single logic view.
 * </p>
 * <p>
 * In some use cases a hierarchy of {@code BeanStore} objects is not sufficient
 * to express complex relations between stores. One example are {@code
 * BeanStore} objects created by separate builder operations or based on
 * different, disjunct implementations. If now beans in these stores refer to
 * each other, it may be impossible to find a parent-child relationship that
 * allows resolving all possible dependencies.
 * </p>
 * <p>
 * This implementation of {@code BeanStore} provides a solution for this
 * problem. It can be initialized with an arbitrary number of child {@code
 * BeanStore} objects and generates a logic view on top of these stores as if
 * their beans comprised a single {@code BeanStore}. This is achieved by
 * corresponding implementations of the methods defined by the {@code BeanStore}
 * interface, for instance the {@code providerNames()} method returns a union of
 * the names of all providers found in the child stores, or the {@code
 * getBeanProvider()} method checks all child stores whether the searched
 * provider can be found. More information about the implementation of the
 * single methods can be found in the Javadocs of the corresponding methods.
 * </p>
 * <p>
 * Implementation note: The thread-safety of this class depends on the
 * implementations of the child bean stores. This class stores the child stores
 * in immutable structures; so provided that all child stores are immutable, the
 * resulting object is immutable, too.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: CombinedBeanStore.java 205 2012-01-29 18:29:57Z oheger $
 */
public class CombinedBeanStore implements BeanStore
{
    /** A counter for generating unique names for combined stores. */
    private static final AtomicLong COUNTER = new AtomicLong();

    /** Constant for the prefix for generated names. */
    private static final String NAME_PREFIX = "jguiraffe.CombinedBeanStore_";

    /** An array with the child bean stores. */
    private final BeanStore[] childStores;

    /** The name of this store. */
    private final String name;

    /**
     * Creates a new instance of {@code CombinedBeanStore} and initializes it
     * with the given name and the child bean stores. A defensive copy of the
     * passed in array with bean stores is created. The single elements must not
     * be <b>null</b>, otherwise an exception is thrown.
     *
     * @param storeName the name of this {@code BeanStore}
     * @param beanStores the child bean stores
     * @throws IllegalArgumentException if the array with the child bean stores
     *         is <b>null</b> or contains <b>null</b> references
     */
    public CombinedBeanStore(String storeName, BeanStore... beanStores)
    {
        if (beanStores == null)
        {
            throw new IllegalArgumentException(
                    "Array with child stores must not be null!");
        }

        childStores = beanStores.clone();
        for (BeanStore st : childStores)
        {
            if (st == null)
            {
                throw new IllegalArgumentException(
                        "Child store must not be null!");
            }
        }

        name = (storeName != null) ? storeName : generateName();
    }

    /**
     * Creates a new instance of {@code CombinedBeanStore} and initializes it
     * with the given child bean stores. A default name for this store is
     * generated.
     *
     * @param beanStores the child bean stores
     * @throws IllegalArgumentException if the array with the child bean stores
     *         is <b>null</b> or contains <b>null</b> references
     */
    public CombinedBeanStore(BeanStore... beanStores)
    {
        this((String) null, beanStores);
    }

    /**
     * Returns the {@code BeanProvider} with the given name. This implementation
     * iterates over all child {@code BeanStore} objects. The first result
     * different from <b>null</b> is returned.
     *
     * @param name the name of the {@code BeanProvider}
     * @return the corresponding {@code BeanProvider} or <b>null</b> if it
     *         cannot be found
     */
    public BeanProvider getBeanProvider(String name)
    {
        for (BeanStore st : childStores)
        {
            BeanProvider p = st.getBeanProvider(name);
            if (p != null)
            {
                return p;
            }
        }

        return null;
    }

    /**
     * Returns the number of child {@code BeanStore} objects stored in this
     * combined store.
     *
     * @return the number of child stores
     */
    public int size()
    {
        return childStores.length;
    }

    /**
     * Returns the child {@code BeanStore} with the given index. Indices are
     * 0-based and must be in the range 0 &lt;= idx &lt; {@code size()}.
     *
     * @param idx the index
     * @return the child {@code BeanStore} at this index
     * @throws ArrayIndexOutOfBoundsException if the index is invalid
     */
    public BeanStore getChildStore(int idx)
    {
        return childStores[idx];
    }

    /**
     * Returns the name of this store. If a name was provided at construction
     * time, this name is returned. Otherwise, a name was automatically
     * generated for this store.
     *
     * @return the name of this store
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the parent store of this {@code CombinedBeanStore}. If none of
     * the child stores has a parent, result is <b>null</b>. If exactly one
     * child store has a parent store, then this store is returned. Otherwise,
     * result is again a {@code CombinedBeanStore} with all non-<b>null</b>
     * parent stores as its children.
     *
     * @return the parent store of this store
     */
    public BeanStore getParent()
    {
        BeanStore singleParent = null;
        List<BeanStore> parents = null;

        for (BeanStore child : childStores)
        {
            BeanStore parent = child.getParent();
            if (parent != null)
            {
                if (singleParent == null)
                {
                    singleParent = parent;
                }
                else
                {
                    if (parents == null)
                    {
                        parents = new LinkedList<BeanStore>();
                        parents.add(singleParent);
                    }
                    parents.add(parent);
                }
            }
        }

        if (parents != null)
        {
            BeanStore[] newChildren = parents.toArray(new BeanStore[parents
                    .size()]);
            return new CombinedBeanStore(newChildren);
        }
        else
        {
            return singleParent;
        }
    }

    /**
     * Returns a set with the names of all {@code BeanProvider} objects that can
     * be queried from this {@code CombinedBeanStore}. This implementation
     * queries all child stores and returns a union of their provider names.
     * Note: the returned set can be modified. It is not connected to this
     * object in any way.
     *
     * @return a set with the names of all {@code BeanProvider} objects
     *         available
     */
    public Set<String> providerNames()
    {
        Set<String> result = new HashSet<String>();
        for (BeanStore st : childStores)
        {
            result.addAll(st.providerNames());
        }

        return result;
    }

    /**
     * Returns the {@code ConversionHelper} associated with this instance. This
     * implementation iterates over the child stores. The first
     * {@code ConversionHelper} instance that is found is returned. If none of
     * the child stores returns such a helper object, result is <b>null</b>.
     *
     * @return the {@code ConversionHelper} associated with this instance
     */
    public ConversionHelper getConversionHelper()
    {
        for (BeanStore st : childStores)
        {
            ConversionHelper ch = st.getConversionHelper();
            if (ch != null)
            {
                return ch;
            }
        }

        return null;
    }

    /**
     * Returns a string representation of this object. This string contains the
     * name of this store and the string representations of all child stores.
     *
     * @return a string for this object
     */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append("CombinedBeanStore [ name = ").append(getName());
        buf.append(" childStores = ");
        buf.append(Arrays.toString(childStores));
        buf.append(" ]");
        return buf.toString();
    }

    /**
     * Generates a name for a {@code CombinedBeanStore}. This method is called
     * if no name was provided when a new instance is created.
     *
     * @return the name for the new store instance
     */
    private static String generateName()
    {
        return NAME_PREFIX + COUNTER.incrementAndGet();
    }
}
