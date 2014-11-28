/*
 * Copyright 2006-2014 The JGUIraffe Team.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;

import net.sf.jguiraffe.gui.builder.event.FormEventManager;
import net.sf.jguiraffe.gui.builder.event.FormListenerType;
import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.forms.ComponentHandlerImpl;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for SwingEventManager.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingEventManager.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingEventManager
{
    /** Constant for the name of the test component. */
    private static final String COMP_NAME = "myTestComponent";

    /** The swing specific event manager to be tested. */
    private SwingEventManager platformManager;

    /** The platform independent event manager. */
    private FormEventManager eventManager;

    /** A component handler used for testing. */
    private TestSwingComponentHandler handler;

    @Before
    public void setUp() throws Exception
    {
        platformManager = new SwingEventManager();
        eventManager = new FormEventManager(platformManager);
        handler = new TestSwingComponentHandler();
    }

    /**
     * Tests registering an invalid listener type.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRegisterListenerIllegalType()
    {
        platformManager.registerListener("comp1", handler, eventManager, null);
    }

    /**
     * Tests registering an action listener.
     */
    @Test
    public void testRegisterListenerAction()
    {
        platformManager.registerListener(COMP_NAME, handler, eventManager,
                FormListenerType.ACTION);
        assertNotNull("Action listener not registered", handler.actionListener);
    }

    /**
     * Tests registering a focus listener.
     */
    @Test
    public void testRegisterListenerFocus()
    {
        platformManager.registerListener(COMP_NAME, handler, eventManager,
                FormListenerType.FOCUS);
        assertNotNull("Focus listener not registered", handler.focusListener);
    }

    /**
     * Tests registering a change listener.
     */
    @Test
    public void testRegisterListenerChange()
    {
        platformManager.registerListener(COMP_NAME, handler, eventManager,
                FormListenerType.CHANGE);
        assertNotNull("Change listener not registered", handler.changeListener);
    }

    /**
     * Tests whether a mouse listener can be registered.
     */
    @Test
    public void testRegisterListenerMouse()
    {
        platformManager.registerListener(COMP_NAME, handler, eventManager,
                FormListenerType.MOUSE);
        assertNotNull("Mouse listener was not registered",
                handler.mouseListener);
    }

    /**
     * Tests unregistering an action listener.
     */
    @Test
    public void testUnregisterListenerAction()
    {
        testRegisterListenerAction();
        platformManager.unregisterListener(COMP_NAME, handler, eventManager,
                FormListenerType.ACTION);
        assertNull("Action listener was not removed", handler.actionListener);
    }

    /**
     * Tests unregistering a focus listener.
     */
    @Test
    public void testUnregisterListenerFocus()
    {
        testRegisterListenerFocus();
        platformManager.unregisterListener(COMP_NAME, handler, eventManager,
                FormListenerType.FOCUS);
        assertNull("Focus listener was not removed", handler.focusListener);
    }

    /**
     * Tests unregistering a change listener.
     */
    @Test
    public void testUnregisterListenerChange()
    {
        testRegisterListenerChange();
        platformManager.unregisterListener(COMP_NAME, handler, eventManager,
                FormListenerType.CHANGE);
        assertNull("Change listener was not removed", handler.changeListener);
    }

    /**
     * Tests whether a mouse listener can be unregistered.
     */
    @Test
    public void testUnregisterListenerMouse()
    {
        testRegisterListenerMouse();
        platformManager.unregisterListener(COMP_NAME, handler, eventManager,
                FormListenerType.MOUSE);
        assertNull("Mouse listener was not removed", handler.mouseListener);
    }

    /**
     * Tests the behavior of registerListener() if a component handler of an
     * unsupported type is passed in. This handler should be ignored.
     */
    @Test
    public void testRegisterListenerInvalidType()
    {
        ComponentHandler<?> ch = EasyMock.createMock(ComponentHandler.class);
        EasyMock.replay(ch);
        platformManager.registerListener(COMP_NAME, ch, eventManager,
                FormListenerType.ACTION);
        EasyMock.verify(ch);
    }

    /**
     * Tests the behavior of unregisterListener() if a component handler of an
     * unsupported type is passed in. This handler should be ignored.
     */
    @Test
    public void testUnregisterListenerInvalidType()
    {
        ComponentHandler<?> ch = EasyMock.createMock(ComponentHandler.class);
        EasyMock.replay(ch);
        platformManager.unregisterListener(COMP_NAME, ch, eventManager,
                FormListenerType.ACTION);
        EasyMock.verify(ch);
    }

    /**
     * Tries to remove a listener of type null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUnregisterListenerTypeNull()
    {
        platformManager.unregisterListener(COMP_NAME,
                EasyMock.createNiceMock(ComponentHandler.class), eventManager,
                null);
    }

    /**
     * A test implementation of a component handler that can be used to register
     * event listeners.
     */
    class TestSwingComponentHandler extends ComponentHandlerImpl implements
            SwingEventSource
    {
        /** Stores the action listener. */
        ActionListener actionListener;

        /** Stores the focus listener. */
        FocusListener focusListener;

        /** Stores the change listener. */
        ChangeListener changeListener;

        /** Stores the mouse listener. */
        MouseListener mouseListener;

        public void addActionListener(ActionListener l)
        {
            checkListener(l, ActionEventAdapter.class, FormListenerType.ACTION);
            actionListener = l;
        }

        public void addFocusListener(FocusListener l)
        {
            checkListener(l, FocusEventAdapter.class, FormListenerType.FOCUS);
            focusListener = l;
        }

        public void addChangeListener(ChangeListener l)
        {
            checkListener(l, ChangeEventAdapter.class, FormListenerType.CHANGE);
            changeListener = l;
        }

        public void removeActionListener(ActionListener l)
        {
            assertSame("Wrong action listener to remove", actionListener, l);
            actionListener = null;
        }

        public void removeChangeListener(ChangeListener l)
        {
            assertSame("Wrong change listener to remove", changeListener, l);
            changeListener = null;
        }

        public void removeFocusListener(FocusListener l)
        {
            assertSame("Wrong focus listener to remove", focusListener, l);
            focusListener = null;
        }

        public void addMouseListener(MouseListener l)
        {
            checkListener(l, MouseEventAdapter.class, FormListenerType.MOUSE);
            mouseListener = l;
        }

        public void removeMouseListener(MouseListener l)
        {
            assertSame("Wrong listener to remove", mouseListener, l);
            mouseListener = null;
        }

        /**
         * Tests the event listener to be registered. We check here whether the
         * Swing event manager passes in a correctly initialized event adapter
         * object.
         *
         * @param l the listener
         * @param expectedClass the expected listener class
         * @param expectedType the expected event listener type
         */
        private void checkListener(Object l, Class<?> expectedClass,
                FormListenerType expectedType)
        {
            assertEquals("Wrong listener class", expectedClass, l.getClass());
            SwingEventAdapter adapter = (SwingEventAdapter) l;
            assertEquals("Wrong component name", COMP_NAME, adapter.getName());
            assertEquals("Wrong event manager", eventManager, adapter
                    .getEventManager());
            assertEquals("Wrong event listener type", expectedType, adapter
                    .getListenerType());
            assertSame("Wrong component handler", this, adapter.getHandler());
        }
    }
}
