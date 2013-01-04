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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code SwingSliderHandler}.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingSliderHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingSliderHandler
{
    /** Constant for the test value of the slider. */
    private static final int VALUE = 55;

    /** The test slider component. */
    private JSlider slider;

    /** The handler to be tested. */
    private SwingSliderHandlerTestImpl handler;

    @Before
    public void setUp() throws Exception
    {
        slider = new JSlider();
        slider.setMinimum(0);
        slider.setMaximum(100);
        handler = new SwingSliderHandlerTestImpl(slider);
    }

    /**
     * Tests whether the correct slider component is returned.
     */
    @Test
    public void testGetSlider()
    {
        assertSame("Wrong slider", slider, handler.getSlider());
    }

    /**
     * Tests whether the correct data type is returned.
     */
    @Test
    public void testGetType()
    {
        assertEquals("Wrong data type", Integer.class, handler.getType());
    }

    /**
     * Tests whether the data of the handler can be queried.
     */
    @Test
    public void testGetData()
    {
        slider.setValue(VALUE);
        assertEquals("Wrong data", VALUE, handler.getData().intValue());
    }

    /**
     * Tests whether the data of the slider can be set.
     */
    @Test
    public void testSetData()
    {
        handler.setData(VALUE);
        assertEquals("Wrong slider value", VALUE, slider.getValue());
    }

    /**
     * Tries to set the handler's data to null. This should leave the slider
     * untouched.
     */
    @Test
    public void testSetDataNull()
    {
        slider.setValue(VALUE);
        handler.setData(null);
        assertEquals("Wrong slider value", VALUE, slider.getValue());
    }

    /**
     * Tests whether the handler has registered itself as change listener at the
     * slider.
     *
     * @return a flag whether the listener was found
     */
    private boolean isChangeListenerRegistered()
    {
        for (ChangeListener l : slider.getChangeListeners())
        {
            if (l == handler)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Tests whether the handler registers itself as change listener.
     */
    @Test
    public void testRegisterChangeListener()
    {
        handler.registerChangeListener();
        assertTrue("Listener not found", isChangeListenerRegistered());
    }

    /**
     * Tests whether a change listener can be unregistered.
     */
    @Test
    public void testUnregisterChangeListener()
    {
        handler.registerChangeListener();
        handler.unregisterChangeListener();
        assertFalse("Change listener still registered",
                isChangeListenerRegistered());
    }

    /**
     * Tests the stateChanged() method if the slider already has its final
     * value.
     */
    @Test
    public void testStateChangedFinalValue()
    {
        slider.setValueIsAdjusting(false);
        ChangeEvent event = new ChangeEvent(slider);
        handler.stateChanged(event);
        assertSame("Wrong event fired", event, handler.changeEvent);
    }

    /**
     * Tests stateChanged() if the slider's value is still adjusting.
     */
    @Test
    public void testStateChangedValueAdjusting()
    {
        slider.setValueIsAdjusting(true);
        ChangeEvent event = new ChangeEvent(slider);
        handler.stateChanged(event);
        assertNull("Event fired", handler.changeEvent);
    }

    /**
     * A test implementation of SwingSliderHandler that allows mocking of the
     * fire event mechanism.
     */
    private static class SwingSliderHandlerTestImpl extends SwingSliderHandler
    {
        /** The event passed to fireChangeEvent(). */
        Object changeEvent;

        public SwingSliderHandlerTestImpl(JSlider comp)
        {
            super(comp);
        }

        /**
         * Records this invocation.
         */
        @Override
        protected void fireChangeEvent(Object event)
        {
            changeEvent = event;
        }
    }
}
