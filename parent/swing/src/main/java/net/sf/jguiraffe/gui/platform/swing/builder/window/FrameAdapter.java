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

import javax.swing.JFrame;
import javax.swing.JRootPane;

/**
 * <p>
 * A window adapter implementation for Swing frames.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FrameAdapter.java 205 2012-01-29 18:29:57Z oheger $
 */
class FrameAdapter extends WindowAdapter
{
    /**
     * Creates a new instance of <code>FrameAdapter</code> and initializes it
     * with the wrapped frame.
     *
     * @param frame the frame
     * @param center a flag whether the frame should be centered
     */
    public FrameAdapter(JFrame frame, boolean center)
    {
        super(frame, center);
        frame.addWindowListener(new WindowListenerAdapter(getWindowHelper()));
    }

    /**
     * Returns the wrapped frame window.
     *
     * @return the frame
     */
    public JFrame getFrame()
    {
        return (JFrame) getWindow();
    }

    /**
     * Returns the frame's title.
     *
     * @return the title
     */
    public String getTitle()
    {
        return getFrame().getTitle();
    }

    /**
     * Sets the frame's title.
     *
     * @param s the new title
     */
    public void setTitle(String s)
    {
        getFrame().setTitle(s);
    }

    /**
     * Returns the root container of this frame.
     *
     * @return the root container
     */
    public Object getRootContainer()
    {
        return getFrame().getContentPane();
    }

    /**
     * Returns the root pane. This method delegates to the wrapped frame.
     *
     * @return the root pane of the wrapped frame
     */
    public JRootPane getRootPane()
    {
        return getFrame().getRootPane();
    }

    /**
     * Disposes this window. This implementation delegates to the wrapped frame.
     */
    public void dispose()
    {
        getFrame().dispose();
    }
}
