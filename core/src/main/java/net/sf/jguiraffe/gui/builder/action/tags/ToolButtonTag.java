/*
 * Copyright 2006-2012 The JGUIraffe Team.
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

import net.sf.jguiraffe.gui.builder.action.ActionData;
import net.sf.jguiraffe.gui.builder.action.ActionManager;
import net.sf.jguiraffe.gui.builder.action.FormAction;
import net.sf.jguiraffe.gui.builder.action.FormActionException;
import net.sf.jguiraffe.gui.forms.ComponentHandler;

/**
 * <p>
 * A specific tag handler class for creating toolbar buttons.
 * </p>
 * <p>
 * Tags represented by this class can appear in the body of a
 * <code>&lt;toolbar&gt;</code> tag. They define a button to be added to the
 * enclosing toolbar. A button can either be directly defined using attributes
 * like <code>text</code> or <code>resgrp</code> that are inherited from the
 * base class. Alternatively a button can be associated with an action object;
 * then the <code>actionName</code> attribute must be provided.
 * </p>
 * <p>
 * This tag does not define any attributes in addition to the ones inherited
 * from its super classes.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ToolButtonTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ToolButtonTag extends ActionControlTag
{
    /**
     * Returns the class of the container tag this tag must be nested inside.
     * This is a toolbar tag.
     *
     * @return the class of the nesting container
     */
    @Override
    protected Class<?> getContainerClass()
    {
        return ToolbarTag.class;
    }

    /**
     * Creates a toolbar button that is associated with an action.
     *
     * @param manager the action manager
     * @param action the action
     * @param parent the container component
     * @throws FormActionException if an error occurs
     */
    @Override
    protected void createActionControl(ActionManager manager,
            FormAction action, Object parent) throws FormActionException
    {
        manager.createToolbarButton(getActionBuilder(), action, isChecked(),
                parent);
    }

    /**
     * Creates a toolbar button based on the given data object.
     *
     * @param manager the action manager
     * @param data the action data object
     * @param parent the parent container component
     * @return the component handler for the tool bar button
     * @throws FormActionException if an error occurs
     */
    @Override
    protected ComponentHandler<?> createElementHandler(ActionManager manager,
            ActionData data, Object parent) throws FormActionException
    {
        return manager.createToolbarButton(getActionBuilder(), data,
                isChecked(), parent);
    }
}
