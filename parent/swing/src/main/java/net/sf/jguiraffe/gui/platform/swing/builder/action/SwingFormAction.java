/*
 * Copyright 2006-2017 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.platform.swing.builder.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import net.sf.jguiraffe.gui.builder.action.ActionHelper;
import net.sf.jguiraffe.gui.builder.action.FormAction;
import net.sf.jguiraffe.gui.builder.event.BuilderEvent;
import net.sf.jguiraffe.gui.builder.event.FormActionEvent;

/**
 * <p>
 * A Swing specific implementation of the <code>FormAction</code> interface.
 * </p>
 * <p>
 * This class serves as an adapter between the generic <code>FormAction</code>
 * interface and Swing specific actions.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingFormAction.java 205 2012-01-29 18:29:57Z oheger $
 */
public class SwingFormAction extends AbstractAction implements FormAction
{
    /** Constant for the CHECKED property. */
    public static final String CHECKED = "CHECKED";

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 2180796112140195669L;

    /** Stores the task to execute when the action is triggered. */
    private Object task;

    /** Stores the name of this action. */
    private String name;

    /**
     * Creates a new instance of <code>SwingFormAction</code> and initializes
     * it. The task is checked (using <code>{@link ActionHelper}</code>)
     * whether it is of an allowed type.
     *
     * @param aName the name of the action
     * @param aTask the task with the executable code
     * @throws IllegalArgumentException if the name is <b>null</b> or the task
     * is invalid
     */
    public SwingFormAction(String aName, Object aTask)
    {
        if (aName == null)
        {
            throw new IllegalArgumentException("Action name must be provided!");
        }

        setTask(aTask);
        name = aName;
    }

    /**
     * Returns the name of this action.
     *
     * @return the action's name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the value of the <code>checked</code> property.
     *
     * @return the <code>checked</code> property.
     */
    public boolean isChecked()
    {
        Boolean value = (Boolean) getValue(CHECKED);
        return (value == null) ? false : value.booleanValue();
    }

    /**
     * Sets the value of the <code>checked</code> property. This property is
     * used for checked menu items or toggle buttons in toolbars.
     *
     * @param f the value of the property
     */
    public void setChecked(boolean f)
    {
        putValue(CHECKED, f ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Returns the task of this action.
     *
     * @return the task
     */
    public Object getTask()
    {
        return task;
    }

    /**
     * Sets the task of this action. The passed in object must be supported by
     * this action. To check this, <code>{@link ActionHelper}</code> is used.
     *
     * @param task the new task
     * @throws IllegalArgumentException if the task object is not allowed
     */
    public void setTask(Object task)
    {
        ActionHelper.checkActionTask(task);
        this.task = task;
    }

    /**
     * Executes this action. This method delegates the call to the internally
     * stored task object. Invocation of this task is delegated to the
     * <code>{@link ActionHelper}</code> class.
     *
     * @param event the causing event
     */
    public void execute(BuilderEvent event)
    {
        ActionHelper.invokeActionTask(getTask(), this, event);
    }

    /**
     * Callback method for action events. This method is called when the
     * associated action is triggered. It delegates the call to the internal
     * task object.
     *
     * @param event the action event
     */
    public void actionPerformed(ActionEvent event)
    {
        execute(new FormActionEvent(event, null, null, event.getActionCommand()));
    }
}
