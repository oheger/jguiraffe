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
package net.sf.jguiraffe.gui.builder.enablers;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.forms.ComponentHandler;

/**
 * <p>
 * A specialized implementation of the {@code ElementEnabler} interface that can
 * change the enabled state of components.
 * </p>
 * <p>
 * An instance of this class is initialized with the name of the component it is
 * associated with. The implementation of the {@code setEnabledState()} method
 * obtains the {@link ComponentHandler} object for this component from the
 * {@link ComponentBuilderData} instance and changes its enabled state.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ComponentEnabler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ComponentEnabler implements ElementEnabler
{
    /** The name of the component this enabler deals with. */
    private final String componentName;

    /**
     * Creates a new instance of {@code ComponentEnabler} and initializes it
     * with the name of the component whose state is to be changed by this
     * enabler.
     *
     * @param compName the name of the component (must not be <b>null</b>)
     * @throws IllegalArgumentException if no component name is specified
     */
    public ComponentEnabler(String compName)
    {
        if (compName == null)
        {
            throw new IllegalArgumentException(
                    "Component name must not be null!");
        }

        componentName = compName;
    }

    /**
     * Returns the name of the component whose state is changed by this enabler.
     *
     * @return the name of the component
     */
    public String getComponentName()
    {
        return componentName;
    }

    /**
     * Performs the change of the enabled state. This implementation obtains the
     * component this enabler is responsible for and changes its enabled state
     * through the corresponding {@link ComponentHandler}. If no {@code
     * ComponentHandler} can be found in the specified {@code
     * ComponentBuilderData} object, a {@code FormBuilderException} is thrown.
     *
     * @param compData the {@code ComponentBuilderData} instance
     * @param state the new enabled state
     * @throws FormBuilderException if the {@code ComponentHandler} cannot be
     *         obtained
     */
    public void setEnabledState(ComponentBuilderData compData, boolean state)
            throws FormBuilderException
    {
        ComponentHandler<?> handler = compData
                .getComponentHandler(getComponentName());
        if (handler == null)
        {
            throw new FormBuilderException(
                    "Cannot resolve ComponentHandler for component "
                            + getComponentName());
        }

        handler.setEnabled(state);
    }
}
