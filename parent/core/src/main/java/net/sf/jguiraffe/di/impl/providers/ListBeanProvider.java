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
package net.sf.jguiraffe.di.impl.providers;

import java.util.ArrayList;
import java.util.Collection;

import net.sf.jguiraffe.di.Dependency;

/**
 * <p>
 * A specific <code>CollectionBeanProvider</code> implementation that creates
 * a list bean.
 * </p>
 * <p>
 * This concrete implementation creates a <code>java.util.ArrayList</code> in
 * its <code>createCollection()</code> method.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ListBeanProvider.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ListBeanProvider extends CollectionBeanProvider
{
    /**
     * Creates a new instance of <code>ListBeanProvider</code> and initializes
     * it with the given dependencies for the list elements.
     *
     * @param deps a collection with the dependencies of the list elements
     */
    public ListBeanProvider(Collection<Dependency> deps)
    {
        super(deps);
    }

    /**
     * Creates the collection managed by this bean provider. This implementation
     * creates an array list with the specified initial capacity.
     *
     * @param size the size of the collection
     * @return the collection object
     */
    @Override
    protected Collection<Object> createCollection(int size)
    {
        return new ArrayList<Object>();
    }
}
