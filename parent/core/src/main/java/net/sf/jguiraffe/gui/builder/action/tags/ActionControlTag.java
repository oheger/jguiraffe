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

import java.util.NoSuchElementException;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.lang.StringUtils;

import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.builder.action.ActionData;
import net.sf.jguiraffe.gui.builder.action.ActionManager;
import net.sf.jguiraffe.gui.builder.action.FormAction;
import net.sf.jguiraffe.gui.builder.action.FormActionException;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

/**
 * <p>
 * Definition of an abstract base class for tag handler classes that create
 * controls, which can be associated with actions.
 * </p>
 * <p>
 * This class provides basic functionality for defining an action aware control.
 * Concrete sub classes will deal with specific controls like menu items or
 * toolbar buttons.
 * </p>
 * <p>
 * This tag handler class supports two different ways of defining a control:
 * <ul>
 * <li>All action related properties (like a text, an icon, a tool tip etc.)
 * can be directly set using the same properties as supported by the base class
 * <code>{@link AbstractActionDataTag}</code>.</li>
 * <li>A reference to an action can be specified. Then the newly created
 * control will be associated with this action and obtain its properties from
 * there.</li>
 * </ul>
 * </p>
 * <p>
 * In addition of the attributes defined by the base class the following
 * attributes are supported by this tag handler class:
 * </p>
 * <p>
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">actionName</td>
 * <td>If this attribute is set, an action with this name will be looked up,
 * and this control will be associated with this action. In this case the values
 * of other attributes defining action related properties will be ignored
 * because these properties are directly obtained from the specified action.</td>
 * <td valign="top">depends</td>
 * </tr>
 * <tr>
 * <td valign="top">checked</td>
 * <td>A boolean flag that determines whether the control to create should have
 * checked semantics. This will be evaluated by concrete sub classes, which can
 * then create specific control instances, e.g. checkbox menu items or toggle
 * buttons.</td>
 * <td valign="top">yes</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ActionControlTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class ActionControlTag extends AbstractActionDataTag
{
    /** Stores the name of the associated action. */
    private String actionName;

    /** Stores the checked flag. */
    private boolean checked;

    /**
     * Returns the name of the associated action.
     *
     * @return the action name
     */
    public String getActionName()
    {
        return actionName;
    }

    /**
     * Setter method for the actionName attribute.
     *
     * @param actionName the attribute value
     */
    public void setActionName(String actionName)
    {
        this.actionName = actionName;
    }

    /**
     * Returns the value of the checked flag.
     *
     * @return the checked flag
     */
    public boolean isChecked()
    {
        return checked;
    }

    /**
     * Setter method for the checked attribute.
     *
     * @param checked the attribute value
     */
    public void setChecked(boolean checked)
    {
        this.checked = checked;
    }

    /**
     * Executes this tag. This implementation checks the provided attributes and
     * either calls <code>createActionControl()</code> or
     * <code>createElementHandler()</code> to create the concrete control.
     *
     * @throws JellyTagException if this tag is used in an invalid way
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected void process() throws JellyTagException, FormBuilderException
    {
        ActionContainer parentCont = (ActionContainer) findAncestorWithClass(getContainerClass());
        if (parentCont == null)
        {
            throw new JellyTagException(
                    "This tag must be nested inside a tag of class "
                            + getContainerClass().getName());
        }
        Object parent = parentCont.getContainer();

        if (StringUtils.isNotEmpty(getActionName()))
        {
            try
            {
                createActionControl(getActionManager(), getActionBuilder()
                        .getActionStore().getAction(getActionName()), parent);
            }
            catch (NoSuchElementException nex)
            {
                throw new FormActionException("Could not retrieve action!", nex);
            }
        }

        else
        {
            checkAttributes();
            ComponentHandler<?> handler = createElementHandler(getActionManager(),
                    this, parent);
            if (StringUtils.isNotEmpty(getName()))
            {
                getBuilderData().storeComponentHandler(getName(), handler);
            }
        }
    }

    /**
     * Dummy implementation of this interface method. This implementation always
     * returns <b>null</b>.
     *
     * @return the task for the action
     */
    public Object getTask()
    {
        return null;
    }

    /**
     * Returns the class of the nesting container tag. This method must be
     * defined in concrete sub classes. It is invoked by the implementation of
     * the <code>process()</code> method to find the tag handler class this
     * tag must be nested inside (e.g. a menu item tag must be placed in the
     * body of a menu tag). The class returned here must also implement the
     * <code>{@link ActionContainer}</code> interface.
     *
     * @return the nesting container tag class
     */
    protected abstract Class<?> getContainerClass();

    /**
     * Creates a control and associates it with the given action. This method is
     * invoked if the <code>actionName</code> attribute is defined. It must be
     * implemented in a concrete sub class to perform the necessary steps to
     * create the control based on the properties of the given action.
     *
     * @param manager the action manager
     * @param action the action this control should be associated with
     * @param parent the parent container to which the new control should be
     * added
     * @throws FormActionException if an error occurs
     */
    protected abstract void createActionControl(ActionManager manager,
            FormAction action, Object parent) throws FormActionException;

    /**
     * Creates a control based on the given action data object. This method is
     * invoked if the <code>actionName</code> attribute is not defined. A
     * concrete sub class must implement it to perform all necessary steps for
     * creating the control based on the given properties. The return value is a
     * component handler instance that will be stored in the current builder
     * data object. From there it can be accessed, e.g. for registering event
     * handlers.
     *
     * @param manager the action manager
     * @param data the action data object
     * @param parent the parent container to which the new control should be
     * added
     * @return a component handler representing the newly created control
     * @throws FormActionException if an error occurs
     */
    protected abstract ComponentHandler<?> createElementHandler(
            ActionManager manager, ActionData data, Object parent)
            throws FormActionException;
}
