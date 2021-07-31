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
package net.sf.jguiraffe.gui.platform.swing.builder.components;

import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractButton;

/**
 * <p>
 * A specific Swing component handler implementation that deals with several
 * kinds of button like components.
 * </p>
 * <p>
 * This component handler class handles buttons (command and toggle buttons),
 * check boxes and radio buttons. Data type is boolean, i.e. a flag whether the
 * button is selected. All type of event handlers are supported.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingButtonHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class SwingButtonHandler extends SwingComponentHandler<Boolean> implements
        ItemListener
{
    /**
     * Creates a new instance of <code>SwingButtonHandler</code>.
     *
     * @param button the button to handle
     */
    public SwingButtonHandler(AbstractButton button)
    {
        super(button);
    }

    /**
     * Returns the managed button.
     *
     * @return the managed button object
     */
    public AbstractButton getButton()
    {
        return (AbstractButton) getComponent();
    }

    /**
     * Returns the component's data.
     *
     * @return the component's data
     */
    public Boolean getData()
    {
        return (getButton().isSelected()) ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * Sets the component's data. This must be an object of type boolean.
     *
     * @param data the component's data.
     */
    public void setData(Boolean data)
    {
        boolean value;

        if (data == null)
        {
            value = false;
        }
        else
        {
            value = data.booleanValue();
        }

        getButton().setSelected(value);
    }

    /**
     * Returns the component's data type. This is boolean.
     *
     * @return the data type
     */
    public Class<?> getType()
    {
        return Boolean.TYPE;
    }

    /**
     * Adds an action listener at this component.
     *
     * @param l the listener to register
     */
    @Override
    public void addActionListener(ActionListener l)
    {
        getButton().addActionListener(l);
    }

    /**
     * Removes an action listener from this component.
     *
     * @param l the listener to be removed
     */
    @Override
    public void removeActionListener(ActionListener l)
    {
        getButton().removeActionListener(l);
    }

    /**
     * Registers this object as change listener at the managed button.
     */
    @Override
    protected void registerChangeListener()
    {
        getButton().addItemListener(this);
    }

    /**
     * Unregisters this object as change listener at the managed button.
     */
    @Override
    protected void unregisterChangeListener()
    {
        getButton().removeItemListener(this);
    }

    /**
     * Callback for item events. Occurring events are routed to the registered
     * change listeners.
     *
     * @param event the event
     */
    public void itemStateChanged(ItemEvent event)
    {
        fireChangeEvent(event);
    }
}
