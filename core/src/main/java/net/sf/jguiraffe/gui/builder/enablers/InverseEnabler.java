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
package net.sf.jguiraffe.gui.builder.enablers;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

/**
 * <p>
 * A specialized implementation of the {@code ElementEnabler} interface that
 * wraps another {@code ElementEnabler} and inverses the {@code
 * setEnabledState()} implementation of this wrapped enabler.
 * </p>
 * <p>
 * An instance of this class is initialized with a reference to another {@code
 * ElementEnabler}. The {@link #setEnabledState(ComponentBuilderData, boolean)}
 * implementation delegates to this enabler, but the {@code state} argument is
 * inverted. This means if this enabler is told to enable elements, it tells the
 * wrapped enabler to disable its element and vice versa. This is useful for
 * instance for using an existing {@code ElementEnabler} in a context where the
 * exactly opposite behavior is required.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: InverseEnabler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class InverseEnabler implements ElementEnabler
{
    /** Stores the wrapped element enabler. */
    private final ElementEnabler wrappedEnabler;

    /**
     * Creates a new instance of {@code InverseEnabler} and initializes it with
     * {@code ElementEnabler} to be wrapped.
     *
     * @param wrapped the wrapped {@code ElementEnabler} (must not be
     *        <b>null</b>)
     * @throws IllegalArgumentException if the wrapped {@code ElementEnabler} is
     *         <b>null</b>
     */
    public InverseEnabler(ElementEnabler wrapped)
    {
        if (wrapped == null)
        {
            throw new IllegalArgumentException(
                    "Wrapped ElementEnabler must not be null!");
        }

        wrappedEnabler = wrapped;
    }

    /**
     * Returns the {@code ElementEnabler} wrapped by this object.
     *
     * @return the wrapped {@code ElementEnabler}
     */
    public ElementEnabler getWrappedEnabler()
    {
        return wrappedEnabler;
    }

    /**
     * Performs the change of the enabled state. This implementation calls the
     * wrapped {@code ElementEnabler} with the inverted {@code state} argument.
     *
     * @param compData the {@code ComponentBuilderData} instance
     * @param state the new enabled state
     * @throws FormBuilderException if the wrapped {@code ElementEnabler} throws
     *         an exception
     */
    public void setEnabledState(ComponentBuilderData compData, boolean state)
            throws FormBuilderException
    {
        getWrappedEnabler().setEnabledState(compData, !state);
    }
}
