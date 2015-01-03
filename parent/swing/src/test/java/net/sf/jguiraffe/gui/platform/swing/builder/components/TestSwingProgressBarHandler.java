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

import org.easymock.EasyMock;

import net.sf.jguiraffe.gui.platform.swing.builder.event.ChangeListener;

import junit.framework.TestCase;

/**
 * Test class for SwingProgressBarHandler.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingProgressBarHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingProgressBarHandler extends TestCase
{
    /** Constant for the test progress bar text. */
    private static final String TEST_TEXT = "TestProgressBarText";

    /** Constant for the test value. */
    private static final Integer TEST_VALUE = 42;

    /** Stores the progress bar wrapped by the handler. */
    private JProgressBar bar;

    /** Stores the handler to be tested. */
    private SwingProgressBarHandler handler;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        bar = new JProgressBar();
        handler = new SwingProgressBarHandler(bar);
    }

    /**
     * Tests whether the correct progress bar is returned.
     */
    public void testGetProgressBar()
    {
        assertSame("Wrong wrapped progress bar returned", bar, handler
                .getProgressBar());
    }

    /**
     * Tests accessing the handler's type.
     */
    public void testGetType()
    {
        assertEquals("Wrong data type", Integer.class, handler.getType());
    }

    /**
     * Tests obtaining the handler's data.
     */
    public void testGetData()
    {
        bar.setValue(TEST_VALUE.intValue());
        assertEquals("Wrong data returned", TEST_VALUE, handler.getData());
    }

    /**
     * Tests obtaining the value of the progress bar.
     */
    public void testGetValue()
    {
        bar.setValue(TEST_VALUE.intValue());
        assertEquals("Wrong value of progress bar", TEST_VALUE.intValue(),
                handler.getValue());
    }

    /**
     * Tests setting data of the handler.
     */
    public void testSetData()
    {
        handler.setData(TEST_VALUE);
        assertEquals("Value of bar was not set by setData()", TEST_VALUE
                .intValue(), bar.getValue());
    }

    /**
     * Tests setting the handler's data to null. This should be a noop.
     */
    public void testSetDataNull()
    {
        bar.setValue(TEST_VALUE.intValue());
        handler.setData(null);
        assertEquals("Value of bar was changed", TEST_VALUE.intValue(), bar
                .getValue());
    }

    /**
     * Tests setting the value through the handler.
     */
    public void testSetValue()
    {
        handler.setValue(TEST_VALUE.intValue());
        assertEquals("Value was not set", TEST_VALUE.intValue(), bar.getValue());
    }

    /**
     * Tests obtaining the bar's text.
     */
    public void testGetProgressText()
    {
        bar.setString(TEST_TEXT);
        assertEquals("Text was not set", TEST_TEXT, handler.getProgressText());
    }

    /**
     * Tests setting the text of the bar.
     */
    public void testSetProgressText()
    {
        handler.setProgressText(TEST_TEXT);
        assertEquals("Text was not set", TEST_TEXT, bar.getString());
    }

    /**
     * Tests setting the text of the bar to null.
     */
    public void testSetProgressTextNull()
    {
        bar.setString(TEST_TEXT);
        handler.setProgressText(null);
        assertEquals("Text was not reset", "", bar.getString());
    }

    /**
     * Tests registering a change listener at the handler.
     */
    public void testAddChangeListener()
    {
        ChangeListener mockListener1 = EasyMock
                .createMock(ChangeListener.class);
        EasyMock.replay(mockListener1);
        Object[] listeners = bar.getChangeListeners();
        handler.addChangeListener(mockListener1);
        assertEquals("Listener not registered", listeners.length + 1, bar
                .getChangeListeners().length);
        assertEquals("Listener not registered once", 1, findListener());
        EasyMock.verify(mockListener1);
    }

    /**
     * Tests removing a change listener.
     */
    public void testRemoveChangeListener()
    {
        ChangeListener mockListener1 = EasyMock
                .createMock(ChangeListener.class);
        EasyMock.replay(mockListener1);
        handler.addChangeListener(mockListener1);
        handler.removeChangeListener(mockListener1);
        assertEquals("Handler still registered", 0, findListener());
    }

    /**
     * Helper method for checking how often the handler is registered as change
     * listener at the component.
     *
     * @return the number of found registrations
     */
    private int findListener()
    {
        int count = 0;
        for (Object l : bar.getChangeListeners())
        {
            if (l == handler)
            {
                count++;
            }
        }
        return count;
    }

    /**
     * Tests whether change events are correctly fired.
     */
    public void testFireChangeEvent()
    {
        ChangeListener mockListener = EasyMock.createMock(ChangeListener.class);
        final ChangeEvent event = new ChangeEvent(bar);
        mockListener.componentChanged(event);
        EasyMock.replay(mockListener);
        handler.addChangeListener(mockListener);
        handler.stateChanged(event);
        handler.removeChangeListener(mockListener);
        handler.stateChanged(event);
        EasyMock.verify(mockListener);
    }
}
