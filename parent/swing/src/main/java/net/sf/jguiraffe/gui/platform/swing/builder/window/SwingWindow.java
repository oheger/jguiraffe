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

import java.awt.Component;
import java.util.Collection;

import javax.swing.JRootPane;

import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.builder.window.WindowListener;

/**
 * <p>
 * Definition of an extended window interface implemented by Swing window
 * implementations.
 * </p>
 * <p>
 * This interface has the purpose to simplify implementations of Swing based
 * windows and support testing. It is used internally in this package.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingWindow.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface SwingWindow extends Window
{
    /**
     * Returns a collection with all registered window listeners.
     *
     * @return a collection with the registered window listeners
     */
    Collection<WindowListener> getWindowListeners();

    /**
     * Returns the window helper used by this window.
     *
     * @return the window helper
     */
    WindowHelper getWindowHelper();

    /**
     * Returns the Swing component that represents this window.
     *
     * @return the component for this window
     */
    Component getComponent();

    /**
     * Sets this window's parent window.
     *
     * @param parent the new parent
     */
    void setParentWindow(Window parent);

    /**
     * Sets the window's controller.
     *
     * @param ctrl the controller
     */
    void setWindowController(Object ctrl);

    /**
     * "Packs" the window. This method is called when the window is opened and
     * no size has been set. In this case {@code pack()} must be called to
     * ensure that a reasonable default size is calculated.
     */
    void packWindow();

    /**
     * Registers an internal window listener that closes this window when the
     * user hits the close icon in the window's title bar. This method is called
     * by the window manager after the creation of the window if auto-close is
     * desired.
     */
    void registerAutoCloseListener();

    /**
     * Returns the root pane of the window.
     *
     * @return the window's root pane
     */
    JRootPane getRootPane();

    /**
     * Closes this window and frees all its resources. This method is called
     * when the {@code SwingWindow} is actually to be removed.
     */
    void dispose();
}
