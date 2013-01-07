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
package net.sf.jguiraffe.gui.platform.swing.builder.window;

import static org.junit.Assert.assertFalse;

import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Arrays;
import java.util.List;

import net.sf.jguiraffe.gui.builder.window.WindowEvent.Type;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for WindowListenerAdapter.
 *
 * @author Oliver Heger
 * @version $Id: TestWindowListenerAdapter.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestWindowListenerAdapter
{
    /** The window that is the source for test window events. */
    private java.awt.Window sourceWindow;

    /** Stores the window listener mock object. */
    private WindowListenerImpl listener;

    /** Stores the window helper. */
    private WindowHelper helper;

    /** Stores the adapter to test. */
    private WindowListenerAdapter adapter;

    @Before
    public void setUp() throws Exception
    {
        sourceWindow = new java.awt.Window(new Frame());
        listener = new WindowListenerImpl();
        SwingWindow wnd = EasyMock.createMock(SwingWindow.class);
        EasyMock.replay(wnd);
        listener.setExpectedWindow(wnd);
        helper = new WindowHelper(wnd, false);
        helper.addWindowListener(listener);
        adapter = new WindowListenerAdapter(helper);
        sourceWindow.addWindowListener(adapter);
    }

    /**
     * Tests triggering the activated event.
     */
    @Test
    public void testWindowActivated()
    {
        listener.setExpectedCalls(Type.WINDOW_ACTIVATED, 1);
        adapter.windowActivated(createEvent(WindowEvent.WINDOW_ACTIVATED));
        listener.check();
    }

    /**
     * Tests triggering the closed event.
     */
    @Test
    public void testWindowClosed()
    {
        listener.setExpectedCalls(Type.WINDOW_CLOSED, 1);
        adapter.windowClosed(createEvent(WindowEvent.WINDOW_CLOSED));
        listener.check();
    }

    /**
     * Tests the behavior of the adapter if multiple window close events are
     * fired. This can obviously happen for a single window. The adapter should
     * remove itself after the first close event, so that listeners registered
     * at it are triggered only once.
     */
    @Test
    public void testWindowClosedMultipleTimes()
    {
        listener.setExpectedCalls(Type.WINDOW_CLOSED, 1);
        adapter.windowClosed(createEvent(WindowEvent.WINDOW_CLOSED));
        List<WindowListener> listeners = Arrays.asList(sourceWindow
                .getWindowListeners());
        assertFalse("Adapter still registered", listeners.contains(adapter));
        listener.check();
    }

    /**
     * Tests triggering the deactivated event.
     */
    @Test
    public void testWindowDeactivated()
    {
        listener.setExpectedCalls(Type.WINDOW_DEACTIVATED, 1);
        adapter.windowDeactivated(createEvent(WindowEvent.WINDOW_DEACTIVATED));
        listener.check();
    }

    /**
     * Tests triggering the deiconified event.
     */
    @Test
    public void testWindowDeiconified()
    {
        listener.setExpectedCalls(Type.WINDOW_DEICONIFIED, 1);
        adapter.windowDeiconified(createEvent(WindowEvent.WINDOW_DEICONIFIED));
        listener.check();
    }

    /**
     * Tests triggering the iconified event.
     */
    @Test
    public void testWindowIconified()
    {
        listener.setExpectedCalls(Type.WINDOW_ICONIFIED, 1);
        adapter.windowIconified(createEvent(WindowEvent.WINDOW_ICONIFIED));
        listener.check();
    }

    /**
     * Tests triggering the opened event.
     */
    @Test
    public void testWindowOpened()
    {
        listener.setExpectedCalls(Type.WINDOW_OPENED, 1);
        adapter.windowOpened(createEvent(WindowEvent.WINDOW_OPENED));
        listener.check();
    }

    /**
     * Tests triggering a closing event.
     */
    @Test
    public void testWindowClosing()
    {
        listener.setExpectedCalls(Type.WINDOW_CLOSING, 1);
        adapter.windowClosing(createEvent(WindowEvent.WINDOW_CLOSING));
        listener.check();
    }

    /**
     * Creates the (AWT) window event.
     *
     * @param code the event's ID
     * @return the event
     */
    private WindowEvent createEvent(int code)
    {
        WindowEvent event = new WindowEvent(sourceWindow, code);
        listener.setExpectedSource(event);
        return event;
    }
}
