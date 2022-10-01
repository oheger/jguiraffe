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
package net.sf.jguiraffe.gui.builder.action.tags;

import net.sf.jguiraffe.gui.builder.action.FormActionException;
import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.components.tags.SimpleComponentTag;

import org.apache.commons.jelly.MissingAttributeException;

/**
 * <p>
 * A specialized tag handler class for creating a toolbar component.
 * </p>
 * <p>
 * This tag handler class uses the current <code>ActionManager</code>
 * implementation for creating a new platform specific toolbar object. The
 * toolbar is a normal GUI component that will be added to the enclosing
 * container. A constraints object can be provided if needed. All attributes
 * defined by the base class <code>SimpleComponentTag</code> can be used for
 * this tag, too. Additional attributes are not supported.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ToolbarTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ToolbarTag extends SimpleComponentTag implements ActionContainer
{
    /**
     * Creates the toolbar component. This implementation will use the current
     * <code>ActionManager</code> to create a toolbar.
     *
     * @param manager the component manager (ignored by this tag)
     * @param create a flag if the component should be created or initialized
     * @return the new toolbar
     * @throws MissingAttributeException if a required attribute is missing
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected Object createComponent(ComponentManager manager, boolean create)
            throws MissingAttributeException, FormBuilderException
    {
        if (create)
        {
            return ActionBaseTag.getActionManager(getContext()).createToolbar(
                    ActionBaseTag.getActionBuilder(getContext()));
        }
        else
        {
            return getComponent();
        }
    }

    /**
     * Returns the container object. This is the created toolbar.
     *
     * @return the action container object
     */
    public Object getContainer()
    {
        return getComponent();
    }

    /**
     * Adds a separator to the toolbar.
     *
     * @throws FormActionException if an error occurs
     */
    public void addSeparator() throws FormActionException
    {
        ActionBaseTag.getActionManager(getContext()).addToolBarSeparator(
                ActionBaseTag.getActionBuilder(getContext()), getContainer());
    }
}
