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

/**
 * <p>
 * An adapter class for transforming events triggered by a {@code
 * java.awt.Window} into generic window events.
 * </p>
 * <p>
 * An instance of this class wraps a {@link WindowHelper} object. When a window
 * event arrives, the corresponding {@code fireEvent()} method of {@code
 * WindowHelper} is called. This will pass the event to all registered
 * listeners.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: WindowListenerAdapter.java 205 2012-01-29 18:29:57Z oheger $
 */
class WindowListenerAdapter implements java.awt.event.WindowListener
{
    /** Stores the wrapped window helper. */
    private final WindowHelper helper;

    /**
     * Creates a new instance of <code>WindowListenerAdapter</code> and
     * initializes it.
     *
     * @param hlp the wrapped window helper
     */
    public WindowListenerAdapter(WindowHelper hlp)
    {
        helper = hlp;
    }

    /**
     * Reacts on activated events.
     *
     * @param event the event
     */
    public void windowActivated(WindowEvent event)
    {
        helper.fireWindowActivated(event);
    }

    /**
     * Reacts on closed events. This implementation also removes this adapter
     * from the window which is the source of this event. This is a workaround
     * for a bug (or feature?) in Swing that multiple close events can occur for
     * a single window. However, the way windows are used in this library,
     * window listeners should receive only a single close event.
     *
     * @param event the event
     */
    public void windowClosed(WindowEvent event)
    {
        helper.fireWindowClosed(event);
        event.getWindow().removeWindowListener(this);
    }

    /**
     * Reacts on closing events.
     *
     * @param event the event
     */
    public void windowClosing(WindowEvent event)
    {
        helper.fireWindowClosing(event);
    }

    /**
     * Reacts on deactivated events.
     *
     * @param event the event
     */
    public void windowDeactivated(WindowEvent event)
    {
        helper.fireWindowDeactivated(event);
    }

    /**
     * Reacts on deiconified events.
     *
     * @param event the event
     */
    public void windowDeiconified(WindowEvent event)
    {
        helper.fireWindowDeiconified(event);
    }

    /**
     * Reacts on iconified events.
     *
     * @param event the event
     */
    public void windowIconified(WindowEvent event)
    {
        helper.fireWindowIconified(event);
    }

    /**
     * Reacts on opened events.
     *
     * @param event the event
     */
    public void windowOpened(WindowEvent event)
    {
        helper.fireWindowOpened(event);
    }
}
