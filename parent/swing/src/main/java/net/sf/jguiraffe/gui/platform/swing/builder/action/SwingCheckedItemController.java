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
package net.sf.jguiraffe.gui.platform.swing.builder.action;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractButton;

/**
 * <p>
 * A helper class used for controlling the <code>checked</code> property of
 * action controls.
 * </p>
 * <p>
 * Swing does not directly support the <code>checked</code> property for
 * actions that is required by the action builder framework. Here it is possible
 * to create checked menu items or toolbar buttons whose <code>checked</code>
 * state is connected to the corresponding action. This class establishes such a
 * connection. It registers itself as a <code>PropertyChangeListener</code> at
 * a Swing action. Whenever the value of the <code>checked</code> property
 * changes, the associated control is updated, too. The other direction is also
 * supported: If the <code>checked</code> state of a control is changed, the
 * action will be notified.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingCheckedItemController.java 205 2012-01-29 18:29:57Z oheger $
 */
class SwingCheckedItemController implements PropertyChangeListener,
        ItemListener
{
    /** Stores a reference to the component to control. */
    private AbstractButton button;

    /** Stores a reference to the action object. */
    private SwingFormAction action;

    /**
     * Creates a new instance of <code>SwingCheckedItemController</code> and
     * initializes it.
     *
     * @param formAction the action that must be associated with the control
     * @param aButton the button controlled by this object
     */
    public SwingCheckedItemController(SwingFormAction formAction,
            AbstractButton aButton)
    {
        button = aButton;
        action = formAction;
        button.addItemListener(this);
        action.addPropertyChangeListener(this);
    }

    /**
     * Returns the wrapped button component.
     *
     * @return the button
     */
    public AbstractButton getButton()
    {
        return button;
    }

    /**
     * Returns the wrapped Swing form action object.
     *
     * @return the action
     */
    public SwingFormAction getAction()
    {
        return action;
    }

    /**
     * Reacts on property change events of the associated action. If the
     * <code>checked</code> property is affected, the button's state is
     * updated.
     *
     * @param event the change event
     */
    public void propertyChange(PropertyChangeEvent event)
    {
        if (SwingFormAction.CHECKED.equals(event.getPropertyName()))
        {
            boolean newValue = ((Boolean) event.getNewValue()).booleanValue();
            if (getButton().isSelected() != newValue)
            {
                getButton().setSelected(newValue);
            } /* if */
        } /* if */
    }

    /**
     * Reacts on state changes of the associated button. Ensures that the action
     * will be updated if necessary.
     *
     * @param event the item event
     */
    public void itemStateChanged(ItemEvent event)
    {
        boolean checked = event.getStateChange() == ItemEvent.SELECTED;
        if (checked != getAction().isChecked())
        {
            getAction().setChecked(checked);
        } /* if */
    }
}
