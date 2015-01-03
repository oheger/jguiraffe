/*
 * Copyright 2006-2015 The JGUIraffe Team.
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

import javax.swing.JProgressBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.jguiraffe.gui.builder.components.model.ProgressBarHandler;

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * An internally used handler class that deals with components of type
 * <code>JProgressBar</code>.
 * </p>
 * <p>
 * The data type of this component handler is an integer representing the
 * current value of the progress bar. This class also implements the
 * <code>ProgressBarHandler</code> interface, which allows direct access to
 * important properties.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingProgressBarHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
class SwingProgressBarHandler extends SwingComponentHandler<Integer> implements
        ProgressBarHandler, ChangeListener
{
    /**
     * Creates a new instance of <code>SwingProgressBarHandler</code> and
     * initializes it with the managed component.
     *
     * @param bar the progress bar to be wrapped
     */
    public SwingProgressBarHandler(JProgressBar bar)
    {
        super(bar);
    }

    /**
     * Returns the managed <code>JProgressBar</code> component.
     *
     * @return the managed progress bar
     */
    public JProgressBar getProgressBar()
    {
        return (JProgressBar) getJComponent();
    }

    /**
     * Returns the text to be displayed on the progress bar.
     *
     * @return the progress text
     */
    public String getProgressText()
    {
        return getProgressBar().getString();
    }

    /**
     * Returns the current value of the progress bar.
     *
     * @return the current value
     */
    public int getValue()
    {
        return getProgressBar().getValue();
    }

    /**
     * Sets the text to be displayed on the progress bar. The text will only be
     * displayed if the <code>allowText</code> property was set when the
     * progress bar was created.
     *
     * @param s the progress text
     */
    public void setProgressText(String s)
    {
        getProgressBar().setString((s != null) ? s : StringUtils.EMPTY);
    }

    /**
     * Sets the current value of the progress bar.
     *
     * @param v the new value
     */
    public void setValue(int v)
    {
        getProgressBar().setValue(v);
    }

    /**
     * Returns the data of the managed component.
     *
     * @return the component's data
     */
    public Integer getData()
    {
        return Integer.valueOf(getValue());
    }

    /**
     * Returns the data type of the managed component. In this case this is an
     * integer.
     *
     * @return the handler's data type
     */
    public Class<?> getType()
    {
        return Integer.class;
    }

    /**
     * Sets the data of the managed component. The passed in value must be an
     * integer. It is interpreted as the new value of the progress bar.
     *
     * @param data the new data
     */
    public void setData(Integer data)
    {
        if (data != null)
        {
            setValue(data.intValue());
        }
    }

    /**
     * Notification method for change events. Notifies the registered change
     * listeners.
     *
     * @param e the event
     */
    public void stateChanged(ChangeEvent e)
    {
        fireChangeEvent(e);
    }

    /**
     * Registers this object as change listener at the managed component.
     */
    @Override
    protected void registerChangeListener()
    {
        getProgressBar().addChangeListener(this);
    }

    /**
     * Unregisters this handler as change listener from the managed component.
     */
    @Override
    protected void unregisterChangeListener()
    {
        getProgressBar().removeChangeListener(this);
    }
}
