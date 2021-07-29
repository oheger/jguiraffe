/*
 * Copyright 2006-2018 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.window;

import net.sf.jguiraffe.gui.builder.event.BuilderEvent;

/**
 * <p>
 * An event class used by the window builder framework to deliver event
 * information related to windows.
 * </p>
 * <p>
 * Events related to windows, e.g. window closing or iconifying, are also
 * abstracted by the builder framework. This event class is used for this
 * purpose. In addition to the event's source (usually the original library
 * specific event object) a reference to the affected window object is provided.
 * This reference is usually an object created by the platform specific window
 * manager that implements the <code>Window</code> interface defined by the
 * window builder framework.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: WindowEvent.java 205 2012-01-29 18:29:57Z oheger $
 */
public class WindowEvent extends BuilderEvent
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 6168945067550400189L;

    /** Stores a reference to the window that caused this event. */
    private final Window sourceWindow;

    /** The type of this event. */
    private final Type type;

    /**
     * Creates an instance of <code>WindowEvent</code> and sets the properties.
     *
     * @param source the event's source
     * @param sourceWindow a reference to the affected window
     * @param type the event type
     */
    public WindowEvent(Object source, Window sourceWindow, Type type)
    {
        super(source);
        this.sourceWindow = sourceWindow;
        this.type = type;
    }

    /**
     * Returns the source window. This is the window that caused this event.
     *
     * @return the source window
     */
    public Object getSourceWindow()
    {
        return sourceWindow;
    }

    /**
     * Returns the type of this event. The type can be used to find out which
     * action was performed on the window when this event was triggered.
     *
     * @return the type of this event
     */
    public Type getType()
    {
        return type;
    }

    /**
     * An enumeration for the different types of window events. The type
     * determines the action that was performed on the window when an event was
     * fired.
     */
    public enum Type
    {
        /** The window was activated. */
        WINDOW_ACTIVATED,

        /** The window is about to be closed. */
        WINDOW_CLOSING,

        /** The window was closed. */
        WINDOW_CLOSED,

        /** The window was deactivated. */
        WINDOW_DEACTIVATED,

        /** A window that was minimized to an icon was restored. */
        WINDOW_DEICONIFIED,

        /** A window was minimized to an icon. */
        WINDOW_ICONIFIED,

        /**
         * The window was opened. This is the first event that can be fired by a
         * window.
         */
        WINDOW_OPENED
    }
}
