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
package net.sf.jguiraffe.gui.platform.swing.builder.window;

import java.awt.Component;
import java.awt.event.WindowEvent;
import java.util.Collection;

import net.sf.jguiraffe.gui.builder.event.FormMouseListener;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.builder.window.WindowClosingStrategy;
import net.sf.jguiraffe.gui.builder.window.WindowListener;
import net.sf.jguiraffe.gui.builder.window.WindowWrapper;

/**
 * <p>
 * Abstract base class for Swing <code>Window</code> implementations that are
 * based on <code>java.awt.Window</code>.
 * </p>
 * <p>
 * This class wraps an instance of <code>java.awt.Window</code> and implements
 * parts of the methods required by the
 * {@link net.sf.jguiraffe.gui.builder.window.Window Window} interface
 * in a way that they access this window's properties. Concrete sub classes will
 * have to implement the properties that are not supported by the
 * <code>java.awt.Window</code> class.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: WindowAdapter.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class WindowAdapter implements SwingWindow, WindowWrapper
{
    /** Stores the wrapped window instance. */
    private final java.awt.Window window;

    /** Stores the window helper object. */
    private final WindowHelper helper;

    /**
     * Creates a new instance of <code>WindowAdapter</code> and initializes it
     * with the wrapped window.
     *
     * @param wrappedWindow the (AWT) window to wrap
     * @param center a flag whether the window should be centered
     */
    protected WindowAdapter(java.awt.Window wrappedWindow, boolean center)
    {
        window = wrappedWindow;
        helper = new WindowHelper(this, center);
    }

    /**
     * Returns the window helper object used by this window.
     *
     * @return the window helper
     */
    public WindowHelper getWindowHelper()
    {
        return helper;
    }

    /**
     * Returns the wrapped window object.
     *
     * @return the wrapped (AWT) window
     */
    public java.awt.Window getWindow()
    {
        return window;
    }

    /**
     * Checks if this window is visible.
     *
     * @return the visible flag
     */
    public boolean isVisible()
    {
        return getWindow().isVisible();
    }

    /**
     * Sets the visible flag for this window.
     *
     * @param f the flag's value
     */
    public void setVisible(boolean f)
    {
        getWindow().setVisible(f);
    }

    /**
     * Opens this window. This implementation ensures that this action is
     * performed on the event dispatching thread, but synchronously.
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
     * Returns the window's x pos.
     *
     * @return the x pos
     */
    public int getXPos()
    {
        return getWindow().getX();
    }

    /**
     * Returns the window's y pos.
     *
     * @return the y pos
     */
    public int getYPos()
    {
        return getWindow().getY();
    }

    /**
     * Returns the window's width.
     *
     * @return the width
     */
    public int getWidth()
    {
        return getWindow().getWidth();
    }

    /**
     * Returns the window's height.
     *
     * @return the height
     */
    public int getHeight()
    {
        return getWindow().getHeight();
    }

    /**
     * Sets all coordinates for this window.
     *
     * @param x the x pos
     * @param y the y pos
     * @param w the width
     * @param h the height
     */
    public void setBounds(int x, int y, int w, int h)
    {
        getWindow().setBounds(x, y, w, h);
    }

    /**
     * Returns the window's parent.
     *
     * @return the parent window
     */
    public Window getParentWindow()
    {
        return getWindowHelper().getParentWindow();
    }

    /**
     * Sets the window's parent.
     *
     * @param parent the parent window
     */
    public void setParentWindow(Window parent)
    {
        getWindowHelper().setParent(parent);
    }

    /**
     * Registers the specified window listener at this window. This is only done
     * if this listener has not been registered before.
     *
     * @param l the listener to register
     */
    public void addWindowListener(WindowListener l)
    {
        getWindowHelper().addWindowListener(l);
    }

    /**
     * Removes the specified window listener from this window. If this listener
     * is not registered at this window, this operation has no effect.
     *
     * @param l the listener to remove
     */
    public void removeWindowListener(WindowListener l)
    {
        getWindowHelper().removeWindowListener(l);
    }

    /**
     * Returns a collection with all registered window listeners.
     *
     * @return a collection with the registered window listeners
     */
    public Collection<WindowListener> getWindowListeners()
    {
        return getWindowHelper().getWindowListeners();
    }

    /**
     * Returns the window's closing strategy. This implementation will never
     * return <b>null</b>. If no closing strategy has been set, a default
     * instance will be returned.
     *
     * @return the window closing strategy
     */
    public WindowClosingStrategy getWindowClosingStrategy()
    {
        return getWindowHelper().getWindowClosingStrategy();
    }

    /**
     * Sets the window's closing strategy.
     *
     * @param windowClosingStrategy the new closing strategy
     */
    public void setWindowClosingStrategy(
            WindowClosingStrategy windowClosingStrategy)
    {
        getWindowHelper().setWindowClosingStrategy(windowClosingStrategy);
    }

    /**
     * Returns the window's controller.
     *
     * @return the window's controller
     */
    public Object getWindowController()
    {
        return getWindowHelper().getWindowController();
    }

    /**
     * Allows to set the window's controller.
     *
     * @param ctrl the new controller
     */
    public void setWindowController(Object ctrl)
    {
        getWindowHelper().setWindowController(ctrl);
    }

    /**
     * Returns the wrapped (AWT) window.
     *
     * @return the wrapped window
     */
    public Object getWrappedWindow()
    {
        return getWindow();
    }

    /**
     * Returns the underlying component.
     *
     * @return the component
     */
    public Component getComponent()
    {
        return getWindow();
    }

    /**
     * Packs the window. This implementation delegates to the {@code pack()}
     * method of {@code java.awt.Window}.
     */
    public void packWindow()
    {
        getWindow().pack();
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
     * Registers a window listener that closes the underlying window if the user
     * hits the close icon in the title bar. This implementation registers a
     * listener that reacts on the <em>window closing</em> event. If this event
     * is received, the window helper is asked to close this window.
     */
    public void registerAutoCloseListener()
    {
        getWindow().addWindowListener(new java.awt.event.WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent event)
            {
                close(false);
            }
        });
    }
}
