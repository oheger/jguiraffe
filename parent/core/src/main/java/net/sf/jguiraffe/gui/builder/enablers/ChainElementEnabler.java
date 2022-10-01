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
package net.sf.jguiraffe.gui.builder.enablers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

/**
 * <p>
 * A specialized implementation of the {@code ElementEnabler} interface that
 * maintains an arbitrary number of other {@code ElementEnabler} objects.
 * </p>
 * <p>
 * An instance of the class is initialized with a collection of other {@code
 * ElementEnabler} objects. Its implementation of the
 * {@link #setEnabledState(ComponentBuilderData, boolean)} method delegates to
 * all of these enablers. This is a natural way of combining {@code
 * ElementEnabler}s or building groups of them. For instance, it is possible to
 * create an {@code ElementEnabler} that manages a set of actions plus some
 * components.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ChainElementEnabler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ChainElementEnabler implements ElementEnabler
{
    /** Stores the child enablers. */
    private final Collection<ElementEnabler> childEnablers;

    /**
     * Creates a new instance of {@code ChainElementEnabler} and initializes it
     * with the given collection of child {@code ElementEnabler} objects. All
     * child enablers must not be <b>null</b>.
     *
     * @param children a collection with the child {@code ElementEnabler}
     *        objects (must not be <b>null</b>)
     * @throws IllegalArgumentException if the collection of child enablers is
     *         <b>null</b> or contains a <b>null</b> element
     */
    public ChainElementEnabler(Collection<ElementEnabler> children)
    {
        if (children == null)
        {
            throw new IllegalArgumentException(
                    "Child enabler collection must not be null!");
        }

        childEnablers = Collections
                .unmodifiableCollection(new ArrayList<ElementEnabler>(children));
        for (ElementEnabler en : childEnablers)
        {
            if (en == null)
            {
                throw new IllegalArgumentException(
                        "Child enabler must not be null!");
            }
        }
    }

    /**
     * Returns a collection of the {@code ElementEnabler} objects maintained by
     * this {@code ChainElementEnabler}.
     *
     * @return a collection of the child {@code ElementEnabler} objects
     */
    public Collection<ElementEnabler> getChildEnablers()
    {
        return childEnablers;
    }

    /**
     * Performs the change of the enabled state. This implementation calls the
     * {@code setElementState()} method of all child {@code ElementEnabler}
     * objects.
     *
     * @param compData the {@code ComponentBuilderData} instance
     * @param state the new enabled state
     * @throws FormBuilderException if the wrapped {@code ElementEnabler} throws
     *         an exception
     */
    public void setEnabledState(ComponentBuilderData compData, boolean state)
            throws FormBuilderException
    {
        for (ElementEnabler en : getChildEnablers())
        {
            en.setEnabledState(compData, state);
        }
    }
}
