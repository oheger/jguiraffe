/*
 * Copyright 2006-2014 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.components;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * <p>
 * A straight-forward implementation of the {@code Composite} interface.
 * </p>
 * <p>
 * This implementation uses member fields to store and expose the data required
 * by the {@code Composite} interface. It can be used by other implementations
 * as delegate. This implementation is not thread-safe.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id$
 * @since 1.3
 */
public class CompositeImpl implements AccessibleComposite
{
    /** The collection with the components and constraints added to this object. */
    private final Collection<Object[]> components = new LinkedList<Object[]>();

    /** The layout object passed to this Composite. */
    private Object layout;

    /** The container. */
    private Object container;

    public void addComponent(Object comp, Object constraints)
    {
        components.add(new Object[] {
                comp, constraints
        });
    }

    public Object getLayout()
    {
        return layout;
    }

    public void setLayout(Object layout)
    {
        this.layout = layout;
    }

    public Object getContainer()
    {
        return container;
    }

    /**
     * Sets the container represented by this object. The object passed to this
     * method is directly returned by {@link #getContainer()}.
     *
     * @param container the container
     */
    public void setContainer(Object container)
    {
        this.container = container;
    }

    public Collection<Object[]> getComponents()
    {
        return Collections.unmodifiableCollection(components);
    }
}
