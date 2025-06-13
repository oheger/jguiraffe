/*
 * Copyright 2006-2025 The JGUIraffe Team.
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

import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;

/**
 * <p>
 * Definition of an interface for registering and unregistering Swing event
 * listeners.
 * </p>
 * <p>
 * This interface is used internally by the Swing specific form builder
 * implementation to receive Swing events and transform them to the general form
 * builder events. The Swing event manager needs a way to register itself at
 * different GUI components. This is done through the methods provided by this
 * interface.
 * </p>
 * <p>
 * To be compatible with the pattern used thoroughly in Swing for registering
 * and unregistering event listeners the methods are named
 * <code>addXXXListener()</code> and <code>removeXXXListener()</code>.
 * However, for each event type only a single listener will be set by the Swing
 * event manager.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingEventSource.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface SwingEventSource
{
    /**
     * Adds the specified action listener at this event source.
     *
     * @param l the listener to register
     */
    void addActionListener(ActionListener l);

    /**
     * Removes the specified action listener from this event source.
     *
     * @param l the listener to remove
     */
    void removeActionListener(ActionListener l);

    /**
     * Adds the specified focus listener at this event source.
     *
     * @param l the listener to register
     */
    void addFocusListener(FocusListener l);

    /**
     * Removes the specified focus listener from this event source.
     *
     * @param l the listener to remove
     */
    void removeFocusListener(FocusListener l);

    /**
     * Adds the specified change listener at this event source
     *
     * @param l the listener to register
     */
    void addChangeListener(ChangeListener l);

    /**
     * Removes the specified change listener from this event source.
     *
     * @param l the listener to remove
     */
    void removeChangeListener(ChangeListener l);

    /**
     * Adds the specified mouse listener to this event source.
     *
     * @param l the listener to be added
     */
    void addMouseListener(MouseListener l);

    /**
     * Removes the specified mouse listener from this event source.
     *
     * @param l the listener to remove
     */
    void removeMouseListener(MouseListener l);
}
