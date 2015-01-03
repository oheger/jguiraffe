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
package net.sf.jguiraffe.gui.platform.swing.builder.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import javax.swing.JLabel;
import java.awt.event.MouseEvent;

import net.sf.jguiraffe.gui.builder.action.ActionBuilder;
import net.sf.jguiraffe.gui.builder.action.ActionManager;
import net.sf.jguiraffe.gui.builder.action.FormActionException;
import net.sf.jguiraffe.gui.builder.action.PopupMenuBuilder;
import net.sf.jguiraffe.gui.builder.action.PopupMenuHandler;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.FormBuilderRuntimeException;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for SwingPopupListener.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingPopupListener.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingPopupListener
{
    /** Constant for a mouse event that is a popup trigger. */
    private static final MouseEvent EVENT_TRIGGER = new MouseEvent(
            new JLabel(), 42, System.currentTimeMillis(), 0, 10, 20, 1, true);

    /** Constant for a mouse event that is no popup trigger. */
    private static final MouseEvent EVENT_NOTRIGGER = new MouseEvent(
            new JLabel(), 42, System.currentTimeMillis(), 0, 10, 20, 1, false);

    /** A mock for the popup handler. */
    private PopupMenuHandler handler;

    /** Stores the component builder data object. */
    private ComponentBuilderData compData;

    /** A mock for the action manager. */
    private ActionManager actionManager;

    /** A mock for the action builder. */
    private ActionBuilder actionBuilder;

    /** The listener to be tested. */
    private SwingPopupListenerTestImpl listener;

    @Before
    public void setUp() throws Exception
    {
        handler = EasyMock.createMock(PopupMenuHandler.class);
        compData = new ComponentBuilderData();
        actionManager = EasyMock.createMock(ActionManager.class);
        actionBuilder = EasyMock.createMock(ActionBuilder.class);
        listener =
                new SwingPopupListenerTestImpl(handler, compData,
                        actionManager, actionBuilder);
    }

    /**
     * Tests creating an instance without a handler. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoHandler()
    {
        new SwingPopupListener(null, compData, null, null);
    }

    /**
     * Tests creating an instance without a component builder data object. This
     * should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoCompData()
    {
        new SwingPopupListener(handler, null, null, null);
    }

    /**
     * Tests creating the menu builder.
     */
    @Test
    public void testCreateMenuBuilder()
    {
        SwingPopupMenuBuilder builder = (SwingPopupMenuBuilder) listener
                .createMenuBuilder(EVENT_TRIGGER);
        assertEquals("Wrong triggering event", EVENT_TRIGGER, builder
                .getTriggeringEvent());
        assertSame("Wrong action builder", actionBuilder, builder.getActionBuilder());
        assertSame("Wrong action manager", actionManager, builder.getActionManager());
    }

    /**
     * Tests the mouse pressed call back when the event triggers a menu.
     */
    @Test
    public void testMousePressedTrigger() throws FormActionException
    {
        PopupMenuBuilder builder = EasyMock.createMock(PopupMenuBuilder.class);
        listener.builder = builder;
        handler.constructPopup(builder, compData);
        EasyMock.replay(builder, handler);
        listener.mousePressed(EVENT_TRIGGER);
        EasyMock.verify(builder, handler);
    }

    /**
     * Tests the mouse pressed call back when the event does not trigger a menu.
     */
    @Test
    public void testMousePressedNoTrigger()
    {
        PopupMenuBuilder builder = EasyMock.createMock(PopupMenuBuilder.class);
        listener.builder = builder;
        EasyMock.replay(builder, handler);
        listener.mousePressed(EVENT_NOTRIGGER);
        EasyMock.verify(builder, handler);
    }

    /**
     * Tests the mouse released call back when the event triggers a menu.
     */
    @Test
    public void testMouseReleasedTrigger() throws FormActionException
    {
        PopupMenuBuilder builder = EasyMock.createMock(PopupMenuBuilder.class);
        listener.builder = builder;
        handler.constructPopup(builder, compData);
        EasyMock.replay(builder, handler);
        listener.mouseReleased(EVENT_TRIGGER);
        EasyMock.verify(builder, handler);
    }

    /**
     * Tests the mouse released call back when the event does not trigger a
     * menu.
     */
    @Test
    public void testMouseReleasedNoTrigger()
    {
        PopupMenuBuilder builder = EasyMock.createMock(PopupMenuBuilder.class);
        listener.builder = builder;
        EasyMock.replay(builder, handler);
        listener.mouseReleased(EVENT_NOTRIGGER);
        EasyMock.verify(builder, handler);
    }

    /**
     * Tests invoking the menu handler if it throws an exception. This exception
     * should be wrapped by a runtime exception.
     */
    @Test
    public void testMaybeShowPopupException() throws FormActionException
    {
        PopupMenuBuilder builder = EasyMock.createMock(PopupMenuBuilder.class);
        listener.builder = builder;
        FormActionException ex = new FormActionException("Test exception");
        handler.constructPopup(builder, compData);
        EasyMock.expectLastCall().andThrow(ex);
        EasyMock.replay(builder, handler);
        try
        {
            listener.maybeShowPopup(EVENT_TRIGGER);
            fail("Exception not detected!");
        }
        catch (FormBuilderRuntimeException fbrex)
        {
            assertEquals("Wrong cause", ex, fbrex.getCause());
            EasyMock.verify(builder, handler);
        }
    }

    /**
     * An easier to test implementation of SwingPopupListener.
     */
    private static class SwingPopupListenerTestImpl extends SwingPopupListener
    {
        /** A mock for the builder to be returned by createMenuBuilder(). */
        PopupMenuBuilder builder;

        public SwingPopupListenerTestImpl(PopupMenuHandler handler,
                ComponentBuilderData compData, ActionManager actMan,
                ActionBuilder builder)
        {
            super(handler, compData, actMan, builder);
        }

        /**
         * Either returns the mock builder or invokes the super method.
         */
        @Override
        protected PopupMenuBuilder createMenuBuilder(MouseEvent event)
        {
            return (builder != null) ? builder : super.createMenuBuilder(event);
        }
    }
}
