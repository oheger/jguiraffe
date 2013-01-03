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

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * <p>
 * A Swing-specific {@code ComponentHandler} implementation for slider
 * components.
 * </p>
 * <p>
 * This handler implementation supports change events. They are mapped to change
 * events of the slider. Action events are not supported.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingSliderHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
class SwingSliderHandler extends SwingComponentHandler<Integer> implements
        ChangeListener
{
    /**
     * Creates a new instance of {@code SwingSliderHandler} and initializes it
     * with the managed slider component.
     *
     * @param comp the slider component
     */
    public SwingSliderHandler(JSlider comp)
    {
        super(comp);
    }

    /**
     * Returns the managed slider component.
     *
     * @return the slider component
     */
    public final JSlider getSlider()
    {
        return (JSlider) getJComponent();
    }

    /**
     * Returns the data of this {@code ComponentHandler}. This is the current
     * value of the managed slider.
     *
     * @return the data of this handler
     */
    public Integer getData()
    {
        return getSlider().getValue();
    }

    /**
     * Returns the data type of this handler. This handler uses the slider's
     * current value as data. Therefore the data type is integer.
     *
     * @return the handler's data type
     */
    public Class<?> getType()
    {
        return Integer.class;
    }

    /**
     * Sets the data of this {@code ComponentHandler}. If a non-<b>null</b>
     * value is provided, the slider's current value is set.
     *
     * @param data the new data of this handler
     */
    public void setData(Integer data)
    {
        if (data != null)
        {
            getSlider().setValue(data.intValue());
        }
    }

    /**
     * Notifies this object about a change of the managed slider component. This
     * method is part of the support for change listeners. It fires a
     * platform-independent change event with the specified event as source.
     *
     * @param e the Swing-specific change event
     */
    public void stateChanged(ChangeEvent e)
    {
        if (!getSlider().getValueIsAdjusting())
        {
            fireChangeEvent(e);
        }
    }

    /**
     * Registers a change listener at the managed slider component. This
     * implementation registers this object as change listener at the slider.
     * The event processing method causes a platform-independent change event to
     * be fired.
     */
    @Override
    protected void registerChangeListener()
    {
        getSlider().addChangeListener(this);
    }

    /**
     * Unregisters the change listener which was registered by
     * {@link #registerChangeListener()}. This method is called when change
     * events are no more of interest.
     */
    @Override
    protected void unregisterChangeListener()
    {
        getSlider().removeChangeListener(this);
    }
}
