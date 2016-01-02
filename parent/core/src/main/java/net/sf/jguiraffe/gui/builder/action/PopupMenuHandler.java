/*
 * Copyright 2006-2016 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.action;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;

/**
 * <p>
 * Definition of an interface for controlling popup menus that can be associated
 * with GUI components.
 * </p>
 * <p>
 * With this interface a platform-independent way of associating popup menus
 * with specific UI elements can be realized. The basic idea is that the
 * <code>{@link ActionManager}</code> interface provides the
 * <code>registerPopupMenuHandler()</code> method for associating an object
 * implementing this interface with a UI element. This will cause a
 * platform-specific event handler being registered at said element, which
 * listens for gestures bringing up a context menu. When such a gesture is
 * detected the <code>PopupMenuHandler</code> is invoked passing in a
 * <code>{@link PopupMenuBuilder}</code> implementation. Using this builder
 * object the handler can create an arbitrary complex menu. It is especially
 * free to adapt this menu to the current status of the application (it is
 * completely up to the handler, which actions it adds to the menu; it can even
 * add different actions on each invocation). When it calls the builder's
 * <code>{@link PopupMenuBuilder#create()}</code> method the menu will be
 * displayed. (The handler can also decide not to invoke <code>create()</code>;
 * in this case no menu will be displayed.)
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: PopupMenuHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface PopupMenuHandler
{
    /**
     * Asks this handler to create a popup menu using the specified
     * <code>PopupMenuBuilder</code>. An implementation can use the methods
     * offered by the builder to create a menu with arbitrary actions and sub
     * menus. On calling the builder's <code>create()</code> method the menu
     * will be displayed. The <code>ComponentBuilderData</code> object is a
     * source of all available information: through the <code>BeanContext</code>
     * accessible through this object many important objects can be obtained
     * including the current form or the <code>ActionStore</code>. So all
     * actions required by the <code>PopupMenuHandler</code> should be reachable
     * through this object.
     *
     * @param builder the builder for creating the menu
     * @param compData the current <code>ComponentBuilderData</code> object
     *        providing access to lots of context information
     * @throws FormActionException if an error occurs when creating the menu
     */
    void constructPopup(PopupMenuBuilder builder, ComponentBuilderData compData)
            throws FormActionException;
}
