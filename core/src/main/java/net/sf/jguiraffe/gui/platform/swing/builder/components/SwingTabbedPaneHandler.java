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
package net.sf.jguiraffe.gui.platform.swing.builder.components;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * <p>
 * A specific component handler implementation that deals with a tabbed pane
 * object.
 * </p>
 * <p>
 * The data of a tabbed pane is defined as its selected index. This is an
 * integer value. Clients can register itself as change listeners; they are then
 * notified whenever the select index of the tabbed pane changes. Focus
 * listeners are also supported.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingTabbedPaneHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
class SwingTabbedPaneHandler extends SwingComponentHandler<Integer> implements
        ChangeListener
{
    /**
     * Creates a new instance of <code>SwingTabbedPaneHandler</code> and
     * initializes it with the component to manage.
     *
     * @param pane the managed tab pane
     */
    public SwingTabbedPaneHandler(JTabbedPane pane)
    {
        super(pane);
    }

    /**
     * Returns the tabbed pane component that is wrapped by this handler.
     *
     * @return the internal tabbed pane component
     */
    public JTabbedPane getTabbedPane()
    {
        return (JTabbedPane) getJComponent();
    }

    /**
     * This method gets called when the index of the selected tab changes. This
     * event is sent to all registered change listeners.
     *
     * @param event the source event
     */
    public void stateChanged(ChangeEvent event)
    {
        fireChangeEvent(event);
    }

    /**
     * Returns the data of the managed component. This is an integer
     * representing the index of the selected tab.
     *
     * @return the data of the managed component
     */
    public Integer getData()
    {
        return Integer.valueOf(getTabbedPane().getSelectedIndex());
    }

    /**
     * Returns the data type of this handler. In this case this is a single
     * <code>Integer</code> object representing the selected index of the tab.
     *
     * @return the data type of this handler
     */
    public Class<?> getType()
    {
        return Integer.class;
    }

    /**
     * Sets the data of the managed component. This is an integer representing
     * the index of the selected tab. So this method can be used to switch to a
     * certain tab.
     *
     * @param data the data of the managed component
     */
    public void setData(Integer data)
    {
        if (data != null)
        {
            getTabbedPane().setSelectedIndex(data.intValue());
        }
    }

    /**
     * Registers this handler as change listener at the managed component.
     */
    @Override
    protected void registerChangeListener()
    {
        getTabbedPane().addChangeListener(this);
    }

    /**
     * Unregisteres this handler as change listener from the managed component.
     */
    @Override
    protected void unregisterChangeListener()
    {
        getTabbedPane().removeChangeListener(this);
    }
}
