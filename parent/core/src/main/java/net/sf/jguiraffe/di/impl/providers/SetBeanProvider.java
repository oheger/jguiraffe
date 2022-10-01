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
package net.sf.jguiraffe.di.impl.providers;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;

import net.sf.jguiraffe.di.Dependency;

/**
 * <p>
 * A specialized <code>CollectionBeanProvider</code> implementation that
 * creates a <code>java.util.Set</code> bean.
 * </p>
 * <p>
 * The <code>createCollection()</code> method is implemented to create a
 * concrete implementation of the <code>java.util.Set</code> interface.
 * Depending on the <code>ordered</code> flag that can be passed to the
 * constructor either a <code>java.util.HashSet</code> (if
 * <code>ordered</code> is <b>false</b>) or a
 * <code>java.util.LinkedHashSet</code> (if <code>ordered</code> is <b>true</b>)
 * is created. In the latter case the set will remember the order of its
 * elements.
 * </p>
 * <p>
 * Implementation note: Objects of this class are immutable and thus can be
 * shared between multiple threads.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SetBeanProvider.java 205 2012-01-29 18:29:57Z oheger $
 */
public class SetBeanProvider extends CollectionBeanProvider
{
    /** Stores the ordered flag. */
    private final boolean ordered;

    /**
     * Creates a new instance of <code>SetBeanProvider</code> and initializes
     * it with the dependencies of its elements and the ordered flag.
     *
     * @param deps the dependencies representing the elements of the set
     * @param ordered a flag whether the set created should keep the order of
     *        its elements
     * @throws IllegalArgumentException if the collection with the dependencies
     *         is <b>null</b>
     */
    public SetBeanProvider(Collection<Dependency> deps, boolean ordered)
    {
        super(deps);
        this.ordered = ordered;
    }

    /**
     * Returns the <code>ordered</code> flag. This flag determines the type of
     * the collection created by this bean provider.
     *
     * @return the <code>ordered</code> flag
     */
    public boolean isOrdered()
    {
        return ordered;
    }

    /**
     * Creates the collection managed by this bean provider. This implementation
     * will return an implementation of the <code>java.util.Set</code>
     * interface (the concrete type depends on the <code>ordered</code>
     * property.
     *
     * @param size the size of the collection
     * @return a new instance of the collection managed by this bean provider
     */
    @Override
    protected Collection<Object> createCollection(int size)
    {
        return isOrdered() ? new LinkedHashSet<Object>()
                : new HashSet<Object>();
    }
}
