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

/**
 * <p>
 * Definition of an interface used to control whether a window can be closed.
 * </p>
 * <p>
 * A <code>{@link Window}</code> object can be passed an implementation of
 * this interface. Whenever the user wants to close the window (by clicking the
 * close button or using a similar mechanism) the <code>canClose()</code>
 * method will be invoked to check if closing the window is allowed. If this
 * method returns <b>false</code> the close operation will be aborted.
 * </p>
 * <p>
 * A typical use case for this interface is displaying a confirmation message if
 * there is unsaved data: Only if the user confirms that changed data can be
 * thrown away, the window will be closed.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: WindowClosingStrategy.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface WindowClosingStrategy
{
    /**
     * Checks if the window can be closed. An implementation can perform any
     * necessary operation to find out if closing the window at this moment is
     * allowed. Only if <b>true</b> is returned, the window will be closed;
     * otherwise it will remain open.
     *
     * @param window a reference to the associated window
     * @return a flag whether the window can be closed
     */
    boolean canClose(Window window);
}
