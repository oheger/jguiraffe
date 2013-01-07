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
package net.sf.jguiraffe.gui.builder.window;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;

/**
 * <p>
 * Definition of an interface for providing (platform-independent) information
 * about a window to be created.
 * </p>
 * <p>
 * This interface is used in communication with the {@link WindowManager}
 * implementation. The methods it defines can be called to extract all
 * information necessary for creating a new window object. Usually an
 * implementation of this interface is created and initialized by tag handler
 * classes of the window builder tag library.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: WindowData.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface WindowData
{
    /** Constant for an undefined coordinate or size information. */
    int UNDEFINED = -1;

    /**
     * Returns the X position of the new window.
     *
     * @return the window's X position or <code>UNDEFINED</code> if this is
     * not defined
     */
    int getXPos();

    /**
     * Returns the Y position of the new window.
     *
     * @return the window's Y position or <code>UNDEFINED</code> if this is
     * not defined
     */
    int getYPos();

    /**
     * Returns the width of the new window.
     *
     * @return the window's width or <code>UNDEFINED</code> if this is not
     * defined
     */
    int getWidth();

    /**
     * Returns the height of the new window.
     *
     * @return the window's height or <code>UNDEFINED</code> if this is not
     * defined
     */
    int getHeight();

    /**
     * Returns a flag whether the new window should be centered on the screen.
     * If this flag is set, eventually set X and Y coordinates are ignored, and
     * the window manager will itself determine appropriate coordinates.
     *
     * @return the center flag
     */
    boolean isCenter();

    /**
     * Returns the window's title.
     *
     * @return the window's title
     */
    String getTitle();

    /**
     * Returns an icon for the new window.
     *
     * @return the window's icon (can be <b>null</b>)
     */
    Object getIcon();

    /**
     * Returns a flag whether the new window should be resizable.
     *
     * @return the resizable flag
     */
    boolean isResizable();

    /**
     * Returns a flag whether the new window should be maximizable. Note that
     * this flag might not be supported for all platforms and window types.
     *
     * @return the maximizable flag
     */
    boolean isMaximizable();

    /**
     * Returns a flag whether the new window should be iconifiable. Note that
     * this flag might not be supported for all platforms and window types.
     *
     * @return the iconifiable flag
     */
    boolean isIconifiable();

    /**
     * Returns a flag whether the new window should have a close icon. If set to
     * <b>false</b>, the user can not close the window directly. Note that this
     * flag might not be supported for all platforms and window types.
     *
     * @return the closable flag
     */
    boolean isClosable();

    /**
     * Returns a flag whether auto-close is active for the new window. If set to
     * <b>true</b>, the window should automatically close itself when the user
     * clicks the closing icon. Otherwise, the developer has to handle the close
     * operation manually.
     *
     * @return the auto-close flag
     */
    boolean isAutoClose();

    /**
     * Returns a flag whether the window should close itself if the user presses
     * the {@code ESCAPE} key. This is especially useful for dialog windows.
     *
     * @return a flag whether the {@code ESCAPE} key should close the window
     */
    boolean isCloseOnEsc();

    /**
     * Returns the menu bar for the new window. The object returned by this
     * method must be compatible with the platform specific window manager
     * implementation, i.e. must represent a valid menu bar for this platform.
     * This should be the case if it was constructed by the action builder
     * library.
     *
     * @return the window's menu bar (can be <b>null</b> if the window does not
     * have a menu bar)
     */
    Object getMenuBar();

    /**
     * Returns the new window's controller. This object is not really evaluated
     * by the window manager, but should be passed to the platform specific
     * implementation of the <code>Window</code> interface, so that the
     * window's controller can be queried by application code.
     *
     * @return the window's controller object (can be <b>null</b>)
     */
    Object getController();

    /**
     * Returns the current {@code ComponentBuilderData} object. This object can
     * be queried by a {@link WindowManager} implementation to obtain context
     * information needed for the creation of a window.
     *
     * @return the current {@code ComponentBuilderData} object
     */
    ComponentBuilderData getComponentBuilderData();
}
