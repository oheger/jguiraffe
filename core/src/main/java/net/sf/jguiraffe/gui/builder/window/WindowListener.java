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

import java.util.EventListener;

/**
 * <p>
 * Definition of an interface that must be implemented by objects that are
 * interested in window related events.
 * </p>
 * <p>
 * This event listener interface is an abstraction of platform specific window
 * event listener interfaces. It defines callback methods for typical window
 * events like window closing, activation, or iconifying.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: WindowListener.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface WindowListener extends EventListener
{
    /**
     * Callback method to notify a listener that a window was activated.
     *
     * @param event the window event
     */
    void windowActivated(WindowEvent event);

    /**
     * Callback method to notify a listener that a window is about to be closed.
     * Events of this type are sent when the user clicks the closing icon in the
     * window's title bar.
     *
     * @param event the window event
     */
    void windowClosing(WindowEvent event);

    /**
     * Callback method to notify a listener that a window was closed.
     *
     * @param event the window event
     */
    void windowClosed(WindowEvent event);

    /**
     * Callback method to notify a listener that a window was deactivated.
     *
     * @param event the window event
     */
    void windowDeactivated(WindowEvent event);

    /**
     * Callback method to notify a listener that a window was deiconified.
     *
     * @param event the window event
     */
    void windowDeiconified(WindowEvent event);

    /**
     * Callback method to notify a listener that a window was iconified.
     *
     * @param event the window event
     */
    void windowIconified(WindowEvent event);

    /**
     * Callback method to notify a listener that a window was opened.
     *
     * @param event the window event
     */
    void windowOpened(WindowEvent event);
}
