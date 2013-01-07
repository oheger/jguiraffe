/*
 * Copyright 2006-2013 The JGUIraffe Team.
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
import java.util.Arrays;

import javax.swing.JButton;

import net.sf.jguiraffe.gui.platform.swing.builder.event.ChangeListener;

import org.easymock.EasyMock;

import junit.framework.TestCase;

/**
 * Test class for SwingButtonHandler.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingButtonHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingButtonHandler extends TestCase
{
    /** Stores the managed button. */
    private JButton button;

    /** The handler to be tested. */
    private SwingButtonHandler handler;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        button = new JButton();
        handler = new SwingButtonHandler(button);
    }

    /**
     * Tests accessing the wrapped button.
     */
    public void testGetButton()
    {
        assertSame("Wrong button returned", button, handler.getButton());
        assertSame("Wrong component returned", button, handler.getComponent());
    }

    /**
     * Tests accessing the handler's data.
     */
    public void testGetData()
    {
        button.setSelected(true);
        assertEquals("Wrong data for selected button", Boolean.TRUE, handler
                .getData());
        button.setSelected(false);
        assertEquals("Wrong data for unselected button", Boolean.FALSE, handler
                .getData());
    }

    /**
     * Tests setting the button's data.
     */
    public void testSetData()
    {
        handler.setData(Boolean.TRUE);
        assertTrue("Button state not selected", button.isSelected());
        handler.setData(Boolean.FALSE);
        assertFalse("Button state is selected", button.isSelected());
    }

    /**
     * Tests setting the data to null. This should deselect the button.
     */
    public void testSetDataNull()
    {
        handler.setData(null);
        assertFalse("Button state is selected", button.isSelected());
    }

    /**
     * Tests obtaining the handler's data type.
     */
    public void testGetType()
    {
        assertEquals("Wrong data type", Boolean.TYPE, handler.getType());
    }

    /**
     * Tests whether an action listener can be added to a button handler.
     */
    public void testAddActionListener()
    {
        ActionListener l = EasyMock.createMock(ActionListener.class);
        EasyMock.replay(l);
        handler.addActionListener(l);
        assertTrue("Listener not added", Arrays.asList(
                button.getActionListeners()).contains(l));
        EasyMock.verify(l);
    }

    /**
     * Tests removing an action listener from a button handler.
     */
    public void testRemoveActionListener()
    {
        ActionListener l = EasyMock.createMock(ActionListener.class);
        EasyMock.replay(l);
        handler.addActionListener(l);
        handler.removeActionListener(l);
        assertFalse("Listener not removed", Arrays.asList(
                button.getActionListeners()).contains(l));
        EasyMock.verify(l);
    }

    /**
     * Tests whether a change listener can be added for a button handler.
     */
    public void testAddChangeListener()
    {
        final ItemEvent event = new ItemEvent(button, 42, this, 0);
        ChangeListener mockListener = EasyMock.createMock(ChangeListener.class);
        mockListener.componentChanged(event);
        EasyMock.replay(mockListener);
        handler.addChangeListener(mockListener);
        handler.itemStateChanged(event);
        EasyMock.verify(mockListener);
    }

    /**
     * Tests removing a change listener from a button handler.
     */
    public void testRemoveChangeListener()
    {
        final ItemEvent event = new ItemEvent(button, 42, this, 0);
        ChangeListener mockListener = EasyMock.createMock(ChangeListener.class);
        mockListener.componentChanged(event);
        EasyMock.replay(mockListener);
        handler.addChangeListener(mockListener);
        handler.itemStateChanged(event);
        handler.removeChangeListener(mockListener);
        handler.itemStateChanged(event);
        EasyMock.verify(mockListener);
    }
}
