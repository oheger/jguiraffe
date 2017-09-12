/*
 * Copyright 2006-2017 The JGUIraffe Team.
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
 * A tag handler class for creating (main) frame windows.
 * </p>
 * <p>
 * This tag can be used for creating frame windows, which are usually (but non
 * always) top level windows. All attributes defined in the base class are
 * supported, but no more.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FrameTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class FrameTag extends WindowBaseTag
{
    /**
     * Creates the frame window.
     *
     * @param manager the window manager
     * @param data the window builder data
     * @return the newly created window object
     * @throws WindowBuilderException if an error occurs
     */
    @Override
    protected Window createWindow(WindowManager manager, WindowBuilderData data)
            throws WindowBuilderException
    {
        return manager.createFrame(data, this, null);
    }

    /**
     * Initializes the frame window.
     *
     * @param manager the window manager
     * @param data the window builder data
     * @return the window object
     * @param wnd the previously created window
     * @throws WindowBuilderException if an error occurs
     */
    @Override
    protected Window initWindow(WindowManager manager, WindowBuilderData data,
            Window wnd) throws WindowBuilderException
    {
        return manager.createFrame(data, this, wnd);
    }
}
