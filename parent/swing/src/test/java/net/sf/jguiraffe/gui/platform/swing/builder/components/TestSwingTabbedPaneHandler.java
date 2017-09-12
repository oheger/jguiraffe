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
package net.sf.jguiraffe.gui.platform.swing.builder.components;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;

import org.easymock.EasyMock;

import net.sf.jguiraffe.gui.platform.swing.builder.event.ChangeListener;

import junit.framework.TestCase;

/**
 * Test class for SwingTabbedPaneHandler.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingTabbedPaneHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingTabbedPaneHandler extends TestCase
{
    /** Constant for the number of tabs. */
    private static final int TAB_COUNT = 3;

    /** Stores the underlying tabbed pane component. */
    private JTabbedPane pane;

    /** Stores the handler to be tested. */
    private SwingTabbedPaneHandler handler;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        pane = new JTabbedPane();
        for (int i = 0; i < TAB_COUNT; i++)
        {
            pane.addTab("Tab" + i, new JPanel());
        }
        handler = new SwingTabbedPaneHandler(pane);
    }

    /**
     * Tests whether the tabbed pane can correctly be accessed.
     */
    public void testGetTabPane()
    {
        assertSame("Wrong wrapped tab pane", pane, handler.getTabbedPane());
    }

    /**
     * Tests accessing the handler's type.
     */
    public void testGetType()
    {
        assertEquals("Wrong data type", Integer.class, handler.getType());
    }

    /**
     * Tests accessing the handler's data. This is the selected index of the tab
     * pane.
     */
    public void testGetData()
    {
        for (int i = 0; i < TAB_COUNT; i++)
        {
            pane.setSelectedIndex(i);
            assertEquals("Wrong handler data", i, ((Integer) handler.getData())
                    .intValue());
        }
    }

    /**
     * Tests setting the handler's data.
     */
    public void testSetData()
    {
        handler.setData(Integer.valueOf(1));
        assertEquals("Selected index was not set", 1, pane.getSelectedIndex());
    }

    /**
     * Tests setting the handler's data to null. This should be a noop.
     */
    public void testSetDataNull()
    {
        pane.setSelectedIndex(1);
        handler.setData(null);
        assertEquals("Selected index was changed", 1, pane.getSelectedIndex());
    }

    /**
     * Tests adding a change listener.
     */
    public void testAddChangeListener()
    {
        ChangeListener mockListener = EasyMock.createMock(ChangeListener.class);
        EasyMock.replay(mockListener);
        assertEquals("Already a change listener registered", 0, findListeners());
        handler.addChangeListener(mockListener);
        assertEquals("Listener not registered", 1, findListeners());
        EasyMock.verify(mockListener);
    }

    /**
     * Tests removing a change listener.
     */
    public void testRemoveChangeListener()
    {
        ChangeListener mockListener = EasyMock.createMock(ChangeListener.class);
        EasyMock.replay(mockListener);
        handler.addChangeListener(mockListener);
        handler.removeChangeListener(mockListener);
        assertEquals("Still registered", 0, findListeners());
        EasyMock.verify(mockListener);
    }

    /**
     * Helper method for checking how often the handler is registered as change
     * listener at the component.
     *
     * @return the number of found registrations
     */
    private int findListeners()
    {
        int count = 0;
        for (Object l : pane.getChangeListeners())
        {
            if (l == handler)
            {
                count++;
            }
        }
        return count;
    }

    /**
     * Tests firing a change event and notifying the registered listener.
     */
    public void testFireChangeEvent()
    {
        ChangeListener mockListener = EasyMock.createMock(ChangeListener.class);
        final ChangeEvent event = new ChangeEvent(pane);
        mockListener.componentChanged(event);
        EasyMock.replay(mockListener);
        handler.addChangeListener(mockListener);
        handler.stateChanged(event);
        handler.removeChangeListener(mockListener);
        handler.stateChanged(event);
        EasyMock.verify(mockListener);
    }
}
