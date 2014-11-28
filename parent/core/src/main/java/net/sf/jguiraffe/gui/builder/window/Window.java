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
package net.sf.jguiraffe.gui.builder.window;

import net.sf.jguiraffe.gui.builder.event.FormMouseListener;

/**
 * <p>
 * Definition of an interface for describing windows in a platform independent
 * way.
 * </p>
 * <p>
 * This interface is an abstraction of a typical window. It defines methods for
 * querying and setting window related properties. For each supported GUI
 * library (or platform) there will be an implementation that hides the
 * specifics of window objects of that library. So application code can simply
 * manipulate these objects through the methods provided here.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: Window.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface Window
{
    /**
     * Returns a flag if this window is visible.
     *
     * @return the visible flag
     */
    boolean isVisible();

    /**
     * Sets the window's visible flag. This method can be used to hide and later
     * show the window again.
     *
     * @param f the flag's value
     */
    void setVisible(boolean f);

    /**
     * Opens the window. This method must be called to make the window visible
     * for the first time.
     */
    void open();

    /**
     * Closes this window. This should cause all resources obtained by the
     * window to be freed. After invocation, the window instance should not be
     * used any longer. The {@code force} parameter determines whether the
     * window's {@link WindowClosingStrategy} is to be invoked: if set to
     * <b>false</b>, the {@link WindowClosingStrategy} is queried, and the
     * window is only closed if permitted. Otherwise, the window is always
     * closed. The return value indicates the success of the operation. A value
     * of <b>false</b> means that the window could not be closed because the
     * {@link WindowClosingStrategy} prohibited this operation.
     *
     * @param force a flag whether the window is to be closed unconditionally
     * @return a flag whether this operation was successful
     */
    boolean close(boolean force);

    /**
     * Returns the window's x position.
     *
     * @return the window's x position
     */
    int getXPos();

    /**
     * Returns the window's y position.
     *
     * @return the window's y position
     */
    int getYPos();

    /**
     * Returns the window's width.
     *
     * @return the window's width
     */
    int getWidth();

    /**
     * Returns the window's height.
     *
     * @return the window's height
     */
    int getHeight();

    /**
     * Allows to set the window's bounds. This method can be called to position
     * and/or resize the window.
     *
     * @param x the new x position
     * @param y the new y position
     * @param w the new width
     * @param h the new height
     */
    void setBounds(int x, int y, int w, int h);

    /**
     * Returns the window's title.
     *
     * @return the title (can be <b>null</b>)
     */
    String getTitle();

    /**
     * Sets the window's title.
     *
     * @param s the new title
     */
    void setTitle(String s);

    /**
     * Returns the (platform independent abstraction of) window's parent window.
     * For top level windows this method will return <b>null</b>.
     *
     * @return the window's parent window
     */
    Window getParentWindow();

    /**
     * Adds a window listener for this window.
     *
     * @param l the listener to add
     */
    void addWindowListener(WindowListener l);

    /**
     * Removes a window listener for this window.
     *
     * @param l the listener to remove
     */
    void removeWindowListener(WindowListener l);

    /**
     * Returns the current <code>WindowClosingStrategy</code> of this window.
     *
     * @return the <code>WindowClosingStrategy</code>; this can be <b>null</b>
     */
    WindowClosingStrategy getWindowClosingStrategy();

    /**
     * Sets the <code>WindowClosingStrategy</code> for this window. This
     * object checks if a window can be closed.
     *
     * @param windowClosingStrategy the new <code>WindowClosingStrategy</code>
     */
    void setWindowClosingStrategy(WindowClosingStrategy windowClosingStrategy);

    /**
     * Returns the controller object for this window. The window's controller is
     * a simple POJO provided by the application that can react on window or
     * component events, thus acting as a typical controller.
     *
     * @return the window's controller (can be <b>null</b> if no controller was
     * specified when the window was created)
     */
    Object getWindowController();

    /**
     * Returns the window's root container. This container must be known if new
     * components should be added to the window, especially during the builder
     * process.
     *
     * @return the window's root container
     */
    Object getRootContainer();

    /**
     * Adds a listener for mouse events to this window. The listener is then
     * informed about the standard mouse events supported by the
     * platform-independent {@code FormMouseListener} interface.
     *
     * @param l the mouse listener to be added
     */
    void addMouseListener(FormMouseListener l);

    /**
     * Removes the specified mouse listener from this window. If the listener
     * was not registered, this method has no effect.
     *
     * @param l the listener to remove
     */
    void removeMouseListener(FormMouseListener l);
}
