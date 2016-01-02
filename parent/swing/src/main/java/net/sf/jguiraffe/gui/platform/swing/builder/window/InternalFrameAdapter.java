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
package net.sf.jguiraffe.gui.platform.swing.builder.window;

import java.awt.Component;
import java.util.Collection;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;

import net.sf.jguiraffe.gui.builder.event.FormMouseListener;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.builder.window.WindowClosingStrategy;
import net.sf.jguiraffe.gui.builder.window.WindowListener;

/**
 * <p>
 * A window adapter implementation for Swing internal frames.
 * </p>
 * <p>
 * Unfortunately in Swing's inheritance hierarchy the
 * <code>JInternalFrame</code> class is not derived from
 * <code>java.awt.Window</code>. So it needs special treatment and its very own
 * adapter class.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: InternalFrameAdapter.java 205 2012-01-29 18:29:57Z oheger $
 */
public class InternalFrameAdapter extends JInternalFrame implements SwingWindow
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = -4797987880771056436L;

    /** Stores the helper object used by this implementation. */
    private final WindowHelper helper;

    /**
     * Creates a new instance of {@code InternalFrameAdapter}.
     *
     * @param center a flag whether the internal frame should be centered
     */
    public InternalFrameAdapter(boolean center)
    {
        super();
        helper = new WindowHelper(this, center);
        addInternalFrameListener(new InternalFrameListenerAdapter(helper));
    }

    /**
     * Opens this internal frame. This will be done synchronously on the event
     * dispatch thread.
     */
    public void open()
    {
        getWindowHelper().openWindow();
    }

    /**
     * Closes this window. This implementation delegates to the {@code
     * WindowHelper}.
     *
     * @param force the force flag
     * @return a flag whether the window could be closed
     */
    public boolean close(boolean force)
    {
        return getWindowHelper().closeWindow(force);
    }

    /**
     * Returns the xpos of this internal frame.
     *
     * @return the xpos
     */
    public int getXPos()
    {
        return getX();
    }

    /**
     * Returns the ypos of this internal frame.
     *
     * @return the ypos
     */
    public int getYPos()
    {
        return getY();
    }

    /**
     * Registers the specified listener at this window.
     *
     * @param l the new listener
     */
    public void addWindowListener(WindowListener l)
    {
        getWindowHelper().addWindowListener(l);
    }

    /**
     * Removes the specified window listener.
     *
     * @param l the listener to remove
     */
    public void removeWindowListener(WindowListener l)
    {
        getWindowHelper().removeWindowListener(l);
    }

    /**
     * Returns this window's closing strategy.
     *
     * @return the window closing strategy
     */
    public WindowClosingStrategy getWindowClosingStrategy()
    {
        return getWindowHelper().getWindowClosingStrategy();
    }

    /**
     * Sets the closing strategy for this window.
     *
     * @param windowClosingStrategy the new closing strategy
     */
    public void setWindowClosingStrategy(
            WindowClosingStrategy windowClosingStrategy)
    {
        getWindowHelper().setWindowClosingStrategy(windowClosingStrategy);
    }

    /**
     * Returns this window's controller.
     *
     * @return the controller
     */
    public Object getWindowController()
    {
        return getWindowHelper().getWindowController();
    }

    /**
     * Returns the root container for this internal frame. This is the frame's
     * content pane.
     *
     * @return the root container
     */
    public Object getRootContainer()
    {
        return getContentPane();
    }

    /**
     * Returns this window's parent.
     *
     * @return the parent window
     */
    public Window getParentWindow()
    {
        return getWindowHelper().getParentWindow();
    }

    /**
     * Returns a collection with the registered window listeners.
     *
     * @return the registered window listeners
     */
    public Collection<WindowListener> getWindowListeners()
    {
        return getWindowHelper().getWindowListeners();
    }

    /**
     * Returns the window helper used by this window implementation.
     *
     * @return the window helper
     */
    public final WindowHelper getWindowHelper()
    {
        return helper;
    }

    /**
     * Returns the component representing this window.
     *
     * @return the component
     */
    public Component getComponent()
    {
        return this;
    }

    /**
     * Sets this window's parent window.
     *
     * @param parent the new parent
     */
    public void setParentWindow(Window parent)
    {
        getWindowHelper().setParent(parent);
    }

    /**
     * Sets the window's controller.
     *
     * @param ctrl the new controller
     */
    public void setWindowController(Object ctrl)
    {
        getWindowHelper().setWindowController(ctrl);
    }

    /**
     * Packs this internal frame. This method delegates to the inherited {@code
     * pack()} method.
     */
    public void packWindow()
    {
        pack();
    }

    /**
     * Adds a mouse listener to this window. This implementation delegates to
     * the {@code WindowHelper}.
     *
     * @param l the listener to be added
     */
    public void addMouseListener(FormMouseListener l)
    {
        getWindowHelper().addMouseListener(l);
    }

    /**
     * Removes a mouse listener from this window. This implementation delegates
     * to the {@code WindowHelper}.
     *
     * @param l the listener to be removed
     */
    public void removeMouseListener(FormMouseListener l)
    {
        getWindowHelper().removeMouseListener(l);
    }

    /**
     * Registers an internal listener that handles auto-close operations. This
     * implementation registers an {@code InternalFrameListener} that reacts on
     * the {@code internalFrameClosing} event and then delegates to the window
     * helper in order to close this internal frame.
     */
    public void registerAutoCloseListener()
    {
        addInternalFrameListener(new javax.swing.event.InternalFrameAdapter()
        {
            @Override
            public void internalFrameClosing(InternalFrameEvent event)
            {
                close(false);
            }
        });
    }
}
