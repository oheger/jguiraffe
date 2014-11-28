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
package net.sf.jguiraffe.gui.platform.swing.builder.window;

import java.awt.event.WindowEvent;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;

import net.sf.jguiraffe.gui.builder.window.WindowEvent.Type;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link InternalFrameListenerAdapter}.
 *
 * @author Oliver Heger
 * @version $Id: TestInternalFrameListenerAdapter.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestInternalFrameListenerAdapter
{
    /** Stores the window listener mock object. */
    private WindowListenerImpl listener;

    /** Stores the window helper. */
    private WindowHelper helper;

    /** Stores the adapter to test. */
    private InternalFrameListenerAdapter adapter;

    @Before
    public void setUp() throws Exception
    {
        listener = new WindowListenerImpl();
        SwingWindow wnd = EasyMock.createMock(SwingWindow.class);
        EasyMock.replay(wnd);
        listener.setExpectedWindow(wnd);
        helper = new WindowHelper(wnd, false);
        helper.addWindowListener(listener);
        adapter = new InternalFrameListenerAdapter(helper);
    }

    /**
     * Tests triggering the activated event.
     */
    @Test
    public void testWindowActivated()
    {
        listener.setExpectedCalls(Type.WINDOW_ACTIVATED, 1);
        adapter
                .internalFrameActivated(createEvent(WindowEvent.WINDOW_ACTIVATED));
        listener.check();
    }

    /**
     * Tests triggering the closed event.
     */
    @Test
    public void testWindowClosed()
    {
        listener.setExpectedCalls(Type.WINDOW_CLOSED, 1);
        adapter.internalFrameClosed(createEvent(WindowEvent.WINDOW_CLOSED));
        listener.check();
    }

    /**
     * Tests triggering the deactivated event.
     */
    @Test
    public void testWindowDeactivated()
    {
        listener.setExpectedCalls(Type.WINDOW_DEACTIVATED, 1);
        adapter
                .internalFrameDeactivated(createEvent(WindowEvent.WINDOW_DEACTIVATED));
        listener.check();
    }

    /**
     * Tests triggering the deiconified event.
     */
    @Test
    public void testWindowDeiconified()
    {
        listener.setExpectedCalls(Type.WINDOW_DEICONIFIED, 1);
        adapter
                .internalFrameDeiconified(createEvent(WindowEvent.WINDOW_DEICONIFIED));
        listener.check();
    }

    /**
     * Tests triggering the iconified event.
     */
    @Test
    public void testWindowIconified()
    {
        listener.setExpectedCalls(Type.WINDOW_ICONIFIED, 1);
        adapter
                .internalFrameIconified(createEvent(WindowEvent.WINDOW_ICONIFIED));
        listener.check();
    }

    /**
     * Tests triggering the opened event.
     */
    @Test
    public void testWindowOpened()
    {
        listener.setExpectedCalls(Type.WINDOW_OPENED, 1);
        adapter.internalFrameOpened(createEvent(WindowEvent.WINDOW_OPENED));
        listener.check();
    }

    /**
     * Tests triggering a closing event.
     */
    @Test
    public void testWindowClosing()
    {
        listener.setExpectedCalls(Type.WINDOW_CLOSING, 1);
        adapter.internalFrameClosing(createEvent(WindowEvent.WINDOW_CLOSING));
        listener.check();
    }

    /**
     * Creates the (AWT) internal frame event.
     *
     * @param code the event's ID
     * @return the event
     */
    protected InternalFrameEvent createEvent(int code)
    {
        InternalFrameEvent event = new InternalFrameEvent(new JInternalFrame(),
                code);
        listener.setExpectedSource(event);
        return event;
    }
}
