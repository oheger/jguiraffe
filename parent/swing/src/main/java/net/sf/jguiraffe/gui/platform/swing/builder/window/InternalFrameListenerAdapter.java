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

import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

/**
 * <p>
 * An adapter class that maps internal frame events to generic window events.
 * </p>
 * <p>
 * This class is analogous to {@link WindowListenerAdapter} for internal frames.
 * We cannot use inheritance here because in Swing an internal frame is not
 * related to other frame window classes.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: InternalFrameListenerAdapter.java 205 2012-01-29 18:29:57Z oheger $
 */
class InternalFrameListenerAdapter implements InternalFrameListener
{
    /** Stores the window helper for passing through the events received. */
    private final WindowHelper helper;

    /**
     * Creates a new instance of <code>InternalFrameListenerAdapter</code> and
     * sets the window helper.
     *
     * @param hlp the helper
     */
    public InternalFrameListenerAdapter(WindowHelper hlp)
    {
        helper = hlp;
    }

    /**
     * Reacts on activated events.
     *
     * @param event the event
     */
    public void internalFrameActivated(InternalFrameEvent event)
    {
        helper.fireWindowActivated(event);
    }

    /**
     * Reacts on closed events.
     *
     * @param event the event
     */
    public void internalFrameClosed(InternalFrameEvent event)
    {
        helper.fireWindowClosed(event);
    }

    /**
     * Reacts on closing events.
     *
     * @param event the event
     */
    public void internalFrameClosing(InternalFrameEvent event)
    {
        helper.fireWindowClosing(event);
    }

    /**
     * Reacts on deactivated events.
     *
     * @param event the event
     */
    public void internalFrameDeactivated(InternalFrameEvent event)
    {
        helper.fireWindowDeactivated(event);
    }

    /**
     * Reacts on deiconified events.
     *
     * @param event the event
     */
    public void internalFrameDeiconified(InternalFrameEvent event)
    {
        helper.fireWindowDeiconified(event);
    }

    /**
     * Reacts on iconified events.
     *
     * @param event the event
     */
    public void internalFrameIconified(InternalFrameEvent event)
    {
        helper.fireWindowIconified(event);
    }

    /**
     * Reacts on opened events.
     *
     * @param event the event
     */
    public void internalFrameOpened(InternalFrameEvent event)
    {
        helper.fireWindowOpened(event);
    }
}
