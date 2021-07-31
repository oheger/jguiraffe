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
package net.sf.jguiraffe.gui.builder.components.tags;

import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.Composite;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A base tag handler class for layout tags.
 * </p>
 * <p>
 * A layout tag has to create a specific layout object and pass it to the
 * enclosing container. This base class already handles the latter part, i.e.
 * correctly initializing the hosting container object. A concrete sub class
 * will have to implement the creation of the specific layout object.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: LayoutTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class LayoutTag extends FormBaseTag
{
    /**
     * Executes this tag. This implementation fetches the corresponding
     * container, creates the layout and ensures that it is set in the
     * container.
     *
     * @throws FormBuilderException if an error occurs
     * @throws JellyTagException if a jelly specific error occurs
     */
    protected void process() throws JellyTagException, FormBuilderException
    {
        Composite comp = findContainer();
        comp.setLayout(createLayout(getBuilderData().getComponentManager()));
    }

    /**
     * Creates the specific layout object. This method must be defined in
     * concrete sub classes, it is called by this tag's main execution method.
     *
     * @param manager the component manager
     * @return the new layout object
     * @throws FormBuilderException if an error occurs
     * @throws JellyTagException if the tag is incorrectly used
     */
    protected abstract Object createLayout(ComponentManager manager)
            throws FormBuilderException, JellyTagException;
}
