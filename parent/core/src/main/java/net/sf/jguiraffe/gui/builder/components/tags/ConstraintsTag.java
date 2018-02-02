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
package net.sf.jguiraffe.gui.builder.components.tags;

import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;

/**
 * <p>
 * An abstract base class for creating layout constraints objects.
 * </p>
 * <p>
 * The task of this tag and its concrete sub classes is to create and initialize
 * a layout constraints object of a specific type and to pass this object to the
 * component tag this tag is nested into. From there it will be used when the
 * corresponding component is added into a container.
 * </p>
 * <p>
 * This base class already implements functionality for searching the associated
 * component tag and for calling the setter method for the constraints object.
 * Concrete sub classes only have to implement the logic for creating the
 * constraints object.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ConstraintsTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class ConstraintsTag extends FormBaseTag
{
    /**
     * Executes this tag. Searches for the corresponding component tag. Calls
     * <code>createConstraints()</code> to create the constraints. Then the
     * new constraints object is passed to the component tag.
     *
     * @throws JellyTagException if there is a jelly specific error
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected void process() throws JellyTagException, FormBuilderException
    {
        ComponentBaseTag compTag =
                (ComponentBaseTag) findAncestorWithClass(ComponentBaseTag.class);
        if (compTag == null)
        {
            throw new JellyTagException(
                    "ConstraintsTag must be nested inside a ComponentBaseTag!");
        }
        compTag.setConstraints(createConstraints(getBuilderData()
                .getComponentManager()));
    }

    /**
     * Creates the constraints object. This method must be implemented in
     * concrete sub classes to create an object of the specific layout
     * constraints class.
     *
     * @param manager the component manager
     * @return the layout constraints object
     * @throws FormBuilderException if an error occurs
     * @throws MissingAttributeException if required attributes are missing
     */
    protected abstract Object createConstraints(ComponentManager manager)
            throws FormBuilderException, MissingAttributeException;
}
