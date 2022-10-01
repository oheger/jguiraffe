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
package net.sf.jguiraffe.gui.platform.swing.builder.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JComponent;

import net.sf.jguiraffe.gui.platform.swing.builder.event.ChangeListener;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for SwingComponentHandler. Here some of the basic methods are
 * tested.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingComponentHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingComponentHandler
{
    /** Stores the handler to be tested. */
    private SwingComponentHandlerTestImpl handler;

    /** A test component managed by the handler. */
    private JComponent component;

    @Before
    public void setUp() throws Exception
    {
        component = new JButton();
        handler = new SwingComponentHandlerTestImpl(component);
    }

    /**
     * Tests accessing the component.
     */
    @Test
    public void testGetComponent()
    {
        assertSame("Wrong component", component, handler.getComponent());
    }

    /**
     * Tests accessing the jComponent.
     */
    @Test
    public void testGetJComponent()
    {
        assertSame("Wrong jComponent", component, handler.getJComponent());
    }

    /**
     * Tests accessing the outer component.
     */
    @Test
    public void testGetOuterComponent()
    {
        assertSame("Wrong outer component", component, handler
                .getOuterComponent());
    }

    /**
     * Tests querying the enabled state.
     */
    @Test
    public void testIsEnabled()
    {
        component.setEnabled(true);
        assertTrue("Wrong enabled state (1)", handler.isEnabled());
        component.setEnabled(false);
        assertFalse("Wrong enabled state (2)", handler.isEnabled());
    }

    /**
     * Tests changing the enabled state.
     */
    @Test
    public void testSetEnabled()
    {
        handler.setEnabled(false);
        assertFalse("Component not disabled", component.isEnabled());
        handler.setEnabled(true);
        assertTrue("Component not enabled", component.isEnabled());
    }

    /**
     * Searches for the specified element in the array and returns a flag whether
     * it was found. This is a helper method for checking whether a listener was
     * registered.
     * @param arr the array
     * @param elem the element to search for
     * @return a flag whether the element was found
     */
    private static boolean findArrayElement(Object[] arr, Object elem)
    {
        for(Object o : arr)
        {
            if(o == elem)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Tests adding a focus listener.
     */
    @Test
    public void testAddFocusListener()
    {
        FocusListener[] listeners1 = component.getFocusListeners();
        FocusListener l = EasyMock.createMock(FocusListener.class);
        handler.addFocusListener(l);
        FocusListener[] listeners2 = component.getFocusListeners();
        assertEquals("Listener was not added", listeners1.length + 1,
                listeners2.length);
        assertTrue("New listener was not found", findArrayElement(listeners2, l));
    }

    /**
     * Tests removing a focus listener.
     */
    @Test
    public void testRemoveFocusListener()
    {
        FocusListener[] listeners1 = component.getFocusListeners();
        FocusListener l = EasyMock.createMock(FocusListener.class);
        handler.addFocusListener(l);
        handler.removeFocusListener(l);
        FocusListener[] listeners2 = component.getFocusListeners();
        assertEquals("Listener was not removed", listeners1.length,
                listeners2.length);
        assertFalse("Listener still registered", findArrayElement(listeners2, l));
    }

    /**
     * Tests whether a mouse listener can be added.
     */
    @Test
    public void testAddMouseListener()
    {
        MouseListener[] listeners1 = component.getMouseListeners();
        MouseListener l = EasyMock.createNiceMock(MouseListener.class);
        handler.addMouseListener(l);
        MouseListener[] listeners2 = component.getMouseListeners();
        assertEquals("Listener was not added", listeners1.length + 1,
                listeners2.length);
        assertTrue("New listener was not found", findArrayElement(listeners2, l));
    }

    /**
     * Tests whether a mouse listener can be removed.
     */
    @Test
    public void testRemoveMouseListener()
    {
        MouseListener[] listeners1 = component.getMouseListeners();
        MouseListener l = EasyMock.createNiceMock(MouseListener.class);
        handler.addMouseListener(l);
        handler.removeMouseListener(l);
        MouseListener[] listeners2 = component.getMouseListeners();
        assertEquals("Listener was not removed", listeners1.length,
                listeners2.length);
        assertFalse("Listener still registered", findArrayElement(listeners2, l));
    }

    /**
     * Tries to add an action listener. This is not supported.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testAddActionListener()
    {
        handler.addActionListener(EasyMock.createMock(ActionListener.class));
    }

    /**
     * Tries to remove an action listener. This is not supported.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testRemoveActionListener()
    {
        handler.removeActionListener(EasyMock.createMock(ActionListener.class));
    }

    /**
     * Tries adding a change listener. This is not supported.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testAddChangeListener()
    {
        handler.addChangeListener(EasyMock.createMock(ChangeListener.class));
    }

    /**
     * Tries to remove a change listener. This is not supported.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testRemoveChangeListener()
    {
        handler.removeChangeListener(null);
    }

    /**
     * Tests removing a change listener, which is not registered at this
     * component. This should be a noop.
     */
    @Test
    public void testRemoveChangeListenerUnregistered()
    {
        ChangeListener l = EasyMock.createMock(ChangeListener.class);
        EasyMock.replay(l);
        handler.removeChangeListener(l);
        EasyMock.verify(l);
    }

    /**
     * A concrete test implementation of SwingComponentHandler.
     */
    static class SwingComponentHandlerTestImpl extends SwingComponentHandler<Object>
    {
        protected SwingComponentHandlerTestImpl(JComponent comp)
        {
            super(comp);
        }

        /** Dummy implementation of this interface method. */
        public Object getData()
        {
            return null;
        }

        /** Dummy implementation of this interface method. */
        public Class<?> getType()
        {
            return String.class;
        }

        /** Dummy implementation of this interface method. */
        public void setData(Object data)
        {
        }
    }
}
