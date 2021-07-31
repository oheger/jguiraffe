/*
 * Copyright 2006-2021 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.window.tags;

import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.builder.window.WindowBuilderData;
import net.sf.jguiraffe.gui.builder.window.WindowBuilderException;
import net.sf.jguiraffe.gui.builder.window.WindowManager;

/**
 * <p>
 * A tag handler class for creating internal frames.
 * </p>
 * <p>
 * Internal frames are windows that behave similar to frame windows, but are
 * restricted to the client area of their parent window. They can be used to
 * implement non modal windows in an application.
 * </p>
 * <p>
 * This tag handler class supports exactly the same attributes as its base
 * class.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: InternalFrameTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class InternalFrameTag extends WindowBaseTag
{
    /**
     * Creates the internal frame window using the passed in window manager.
     *
     * @param manager the window manager
     * @param data the window builder data
     * @return the new window
     * @throws WindowBuilderException if an error occurs
     */
    protected Window createWindow(WindowManager manager, WindowBuilderData data)
            throws WindowBuilderException
    {
        return manager.createInternalFrame(data, this, null);
    }

    /**
     * Initializes an internal frame window.
     *
     * @param manager the window manager
     * @param data the window builder data
     * @param wnd the window to initialize
     * @return the fully initialized window
     * @throws WindowBuilderException if an error occurs
     */
    protected Window initWindow(WindowManager manager, WindowBuilderData data,
            Window wnd) throws WindowBuilderException
    {
        return manager.createInternalFrame(data, this, wnd);
    }
}
