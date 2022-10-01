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
package net.sf.jguiraffe.gui.platform.swing.builder.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.sf.jguiraffe.gui.builder.event.FormActionEvent;
import net.sf.jguiraffe.gui.builder.event.FormActionListener;
import net.sf.jguiraffe.gui.builder.event.FormEventManager;
import net.sf.jguiraffe.gui.builder.event.FormListenerType;
import net.sf.jguiraffe.gui.forms.ComponentHandler;

/**
 * <p>
 * A specific event adapter for Swing action events.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ActionEventAdapter.java 205 2012-01-29 18:29:57Z oheger $
 */
class ActionEventAdapter extends SwingEventAdapter implements ActionListener
{
    /**
     * Creates a new instance of {@code ActionEventAdapter} that is associated
     * with an event manager.
     *
     * @param eventManager the event manager (must not be <b>null</b>)
     * @param handler the component handler
     * @param name the component's name
     * @throws IllegalArgumentException if the event manager is <b>null</b>
     */
    public ActionEventAdapter(FormEventManager eventManager,
            ComponentHandler<?> handler, String name)
    {
        super(eventManager, handler, name);
    }

    /**
     * Creates a new instance of {@code ActionEventAdapter} that is associated
     * with the specified event listener.
     *
     * @param actionListener the action listener (must not be <b>null</b>)
     * @param handler the component handler
     * @param name the component name
     * @throws IllegalArgumentException if the event listener is <b>null</b>
     */
    public ActionEventAdapter(FormActionListener actionListener,
            ComponentHandler<?> handler, String name)
    {
        super(actionListener, handler, name);
    }

    /**
     * Call back for action events.
     *
     * @param event the action event
     */
    public void actionPerformed(ActionEvent event)
    {
        fireEvent(createFormEvent(event));
    }

    /**
     * Creates a <code>FormActionEvent</code> from the passed in Swing action
     * event.
     *
     * @param event the Swing action event
     * @return the general form action event
     */
    protected FormActionEvent createFormEvent(ActionEvent event)
    {
        return new FormActionEvent(event, getHandler(), getName(), event
                .getActionCommand());
    }

    /**
     * Returns the form listener type of this adapter.
     *
     * @return the listener type
     */
    @Override
    protected FormListenerType getListenerType()
    {
        return FormListenerType.ACTION;
    }
}
