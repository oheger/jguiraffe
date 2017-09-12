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
package net.sf.jguiraffe.gui.platform.swing.builder.window;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JDesktopPane;
import javax.swing.SwingUtilities;

import net.sf.jguiraffe.gui.builder.event.FormMouseListener;
import net.sf.jguiraffe.gui.builder.window.InvariantWindowClosingStrategy;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.builder.window.WindowClosingStrategy;
import net.sf.jguiraffe.gui.builder.window.WindowData;
import net.sf.jguiraffe.gui.builder.window.WindowEvent;
import net.sf.jguiraffe.gui.builder.window.WindowListener;
import net.sf.jguiraffe.gui.platform.swing.builder.event.MouseEventAdapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * An internally used helper class for the Swing window package.
 * </p>
 * <p>
 * This class implements some functionality common to all Swing window
 * implementations. It further defines some helper methods. Because of Swing's
 * inheritance hierarchy it is not possible to implement this functionality in
 * common base classes, so the composition approach is used.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: WindowHelper.java 205 2012-01-29 18:29:57Z oheger $
 */
class WindowHelper
{
    /** The logger. */
    private final Log log = LogFactory.getLog(getClass());

    /** Stores the associated Swing window. */
    private final SwingWindow swingWindow;

    /** Stores the window's closing strategy. */
    private WindowClosingStrategy windowClosingStrategy;

    /** Stores the window's parent. */
    private Window parent;

    /** Stores the window's controller. */
    private Object controller;

    /** Stores the registered window listeners. */
    private final List<WindowListener> listeners;

    /** Stores the mouse listeners registered at this window. */
    private final List<MouseEventAdapter> mouseListeners;

    /** A flag whether the window is to be centered when it is opened. */
    private final boolean center;

    /**
     * Creates a new instance of <code>WindowHelper</code> and initializes it
     * with the associated window.
     *
     * @param window the window
     * @param centerOnOpen a flag whether the window is to be centered
     */
    public WindowHelper(SwingWindow window, boolean centerOnOpen)
    {
        swingWindow = window;
        listeners = new CopyOnWriteArrayList<WindowListener>();
        mouseListeners = new LinkedList<MouseEventAdapter>();
        center = centerOnOpen;
    }

    /**
     * Returns the associated swing window.
     *
     * @return the swing window
     */
    public SwingWindow getSwingWindow()
    {
        return swingWindow;
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
        return (windowClosingStrategy != null) ? windowClosingStrategy
                : InvariantWindowClosingStrategy.DEFAULT_INSTANCE;
    }

    /**
     * Sets the window's closing strategy.
     *
     * @param windowClosingStrategy the new closing strategy
     */
    public void setWindowClosingStrategy(
            WindowClosingStrategy windowClosingStrategy)
    {
        this.windowClosingStrategy = windowClosingStrategy;
    }

    /**
     * Returns the window's controller.
     *
     * @return the window's controller
     */
    public Object getWindowController()
    {
        return controller;
    }

    /**
     * Allows to set the window's controller.
     *
     * @param ctrl the new controller
     */
    public void setWindowController(Object ctrl)
    {
        controller = ctrl;
    }

    /**
     * Returns the window's parent.
     *
     * @return the parent window
     */
    public Window getParentWindow()
    {
        return parent;
    }

    /**
     * Sets the window's parent.
     *
     * @param parent the parent window
     */
    public void setParent(Window parent)
    {
        this.parent = parent;
    }

    /**
     * Registers the specified window listener at this window.
     *
     * @param l the listener to register (must not be <b>null</b>)
     * @throws IllegalArgumentException if the listener is <b>null</b>
     */
    public void addWindowListener(WindowListener l)
    {
        if (l == null)
        {
            throw new IllegalArgumentException(
                    "Window listener must not be null!");
        }

        listeners.add(l);
    }

    /**
     * Removes the specified window listener from this window. If this listener
     * is not registered at this window, this operation has no effect.
     *
     * @param l the listener to remove
     */
    public void removeWindowListener(WindowListener l)
    {
        listeners.remove(l);
    }

    /**
     * Returns a collection with all registered window listeners.
     *
     * @return a collection with the registered window listeners
     */
    public Collection<WindowListener> getWindowListeners()
    {
        return Collections.unmodifiableCollection(listeners);
    }

    /**
     * Dispatches a window activated event to all registered event listeners.
     *
     * @param src the source event
     */
    public void fireWindowActivated(Object src)
    {
        WindowEvent event = null;
        for (WindowListener l : listeners)
        {
            if (event == null)
            {
                event = createEvent(src, WindowEvent.Type.WINDOW_ACTIVATED);
            }
            l.windowActivated(event);
        }
    }

    /**
     * Dispatches a window closed event to all registered event listeners.
     *
     * @param src the source event
     */
    public void fireWindowClosed(Object src)
    {
        WindowEvent event = null;
        for (WindowListener l : listeners)
        {
            if (event == null)
            {
                event = createEvent(src, WindowEvent.Type.WINDOW_CLOSED);
            }
            l.windowClosed(event);
        }
    }

    /**
     * Dispatches a window closing event to all registered event listeners.
     *
     * @param src the source event
     */
    public void fireWindowClosing(Object src)
    {
        WindowEvent event = null;
        for (WindowListener l : listeners)
        {
            if (event == null)
            {
                event = createEvent(src, WindowEvent.Type.WINDOW_CLOSING);
            }
            l.windowClosing(event);
        }
    }

    /**
     * Dispatches a window deactivated event to all registered event listeners.
     *
     * @param src the source event
     */
    public void fireWindowDeactivated(Object src)
    {
        WindowEvent event = null;
        for (WindowListener l : listeners)
        {
            if (event == null)
            {
                event = createEvent(src, WindowEvent.Type.WINDOW_DEACTIVATED);
            }
            l.windowDeactivated(event);
        }
    }

    /**
     * Dispatches a window deiconified event to all registered event listeners.
     *
     * @param src the source event
     */
    public void fireWindowDeiconified(Object src)
    {
        WindowEvent event = null;
        for (WindowListener l : listeners)
        {
            if (event == null)
            {
                event = createEvent(src, WindowEvent.Type.WINDOW_DEICONIFIED);
            }
            l.windowDeiconified(event);
        }
    }

    /**
     * Dispatches a window iconified event to all registered event listeners.
     *
     * @param src the source event
     */
    public void fireWindowIconified(Object src)
    {
        WindowEvent event = null;
        for (WindowListener l : listeners)
        {
            if (event == null)
            {
                event = createEvent(src, WindowEvent.Type.WINDOW_ICONIFIED);
            }
            l.windowIconified(event);
        }
    }

    /**
     * Dispatches a window opened event to all registered event listeners.
     *
     * @param src the source event
     */
    public void fireWindowOpened(Object src)
    {
        WindowEvent event = null;
        for (WindowListener l : listeners)
        {
            if (event == null)
            {
                event = createEvent(src, WindowEvent.Type.WINDOW_OPENED);
            }
            l.windowOpened(event);
        }
    }

    /**
     * Called when the window should be closed. Depending on the {@code force}
     * parameter the closing strategy is triggered. If it allows closing the
     * window or if the {@code force} parameter is <b>true</b>, the window is
     * disposed.
     *
     * @param force the force flag
     * @return a flag whether the window could be closed
     */
    public boolean closeWindow(boolean force)
    {
        if (force || getWindowClosingStrategy().canClose(getSwingWindow()))
        {
            getSwingWindow().dispose();
            return true;
        }

        return false;
    }

    /**
     * Adds a mouse listener to the associated window. This implementation
     * creates an adapter that transforms Swing-specific mouse events to the
     * standard mouse events supported by the <em>JGUIraffe</em> library. It is
     * possible to add the same listener multiple times. If the listener is
     * <b>null</b>, this method has no effect.
     *
     * @param l the mouse listener to be added
     */
    public void addMouseListener(FormMouseListener l)
    {
        if (l != null)
        {
            MouseEventAdapter adapter = new MouseEventAdapter(l, null, null);
            getSwingWindow().getComponent().addMouseListener(adapter);

            synchronized (mouseListeners)
            {
                mouseListeners.add(adapter);
            }
        }
    }

    /**
     * Removes the specified mouse listener from the associated window. If the
     * listener is not registered at this window, this method has no effect.
     * Only one listener registration is removed by a single method call. If the
     * listener has been added multiple times, it is necessary to invoke this
     * method the same number of times to fully remove the listener.
     *
     * @param l the mouse listener to be removed
     */
    public void removeMouseListener(FormMouseListener l)
    {
        MouseEventAdapter adapter = null;

        synchronized (mouseListeners)
        {
            for (Iterator<MouseEventAdapter> it = mouseListeners.iterator(); it
                    .hasNext();)
            {
                MouseEventAdapter a = it.next();
                if (a.getEventListener().equals(l))
                {
                    it.remove();
                    adapter = a;
                    break;
                }
            }
        }

        if (adapter != null)
        {
            getSwingWindow().getComponent().removeMouseListener(adapter);
        }
    }

    /**
     * Returns a collection with the mouse listeners that have been registered
     * at this helper. This method is mainly used for testing purposes.
     *
     * @return a collection with the registered mouse listeners
     */
    public Collection<MouseListener> getMouseListeners()
    {
        return new ArrayList<MouseListener>(mouseListeners);
    }

    /**
     * Opens this window. This implementation ensures that this action is
     * performed on the event dispatching thread, but synchronously.
     */
    public void openWindow()
    {
        log.debug("Opening window.");
        if (SwingUtilities.isEventDispatchThread())
        {
            doOpenWindow();
        }
        else
        {
            try
            {
                SwingUtilities.invokeAndWait(new Runnable()
                {
                    public void run()
                    {
                        doOpenWindow();
                    }
                });
            }
            catch (InterruptedException iex)
            {
                // ignore
                log.info("Interrupted exception when opening window", iex);
            }
            catch (InvocationTargetException itex)
            {
                log.error("Error when opening window", itex);
                throw new RuntimeException(itex.getMessage());
            }
        }
    }

    /**
     * Checks if in the given window data object the window's size is fully
     * defined. If this is not the case, the window must be packed.
     *
     * @param data the window data object
     * @return a flag if the window's size is defined
     */
    public static boolean sizeDefined(WindowData data)
    {
        return sizeDefined(data.getWidth(), data.getHeight());
    }

    /**
     * Checks if the given window size is fully defined. If this is not the
     * case, the window must be packed.
     * @param width the width of the window
     * @param height the height of the window
     * @return a flag whether the size is fully defined
     */
    public static boolean sizeDefined(int width, int height)
    {
        return width > 0 && height > 0;
    }

    /**
     * Tries to find a desktop pane in the given container or its children. This
     * method is useful if an internal frame is to be added to a frame window's
     * desktop. The container's children are recursively searched until a
     * desktop pane component is found.
     *
     * @param container the container to search
     * @return the desktop panel or <b>null</b> if none is found
     */
    public static JDesktopPane findDesktopPane(Container container)
    {
        if (container == null)
        {
            return null;
        }

        if (container instanceof JDesktopPane)
        {
            return (JDesktopPane) container;
        }

        Component[] components = container.getComponents();
        for (int i = 0; i < components.length; i++)
        {
            if (components[i] instanceof Container)
            {
                JDesktopPane result = findDesktopPane((Container) components[i]);
                if (result != null)
                {
                    return result;
                }
            }
        }

        return null;
    }

    /**
     * Determines the bounds of the given component based on the passed in
     * window data object. This method does the following: If the window's
     * bounds are fully defined, they are simply set. If the location is
     * missing, default values are set. The size is only set, if it is fully
     * defined (otherwise the calling must ensure that the <code>pack()</code>
     * method is invoked on the window).
     *
     * @param comp the component to initialize
     * @param data the data object with the bounds
     */
    public static void initComponentBounds(Component comp, WindowData data)
    {
        comp.setLocation((data.getXPos() == WindowData.UNDEFINED) ? 0 : data
                .getXPos(), (data.getYPos() == WindowData.UNDEFINED) ? 0 : data
                .getYPos());
        if (sizeDefined(data))
        {
            comp.setSize(data.getWidth(), data.getHeight());
        }
    }

    /**
     * Opens the window directly. Called by <code>openWindow()</code>.
     */
    protected void doOpenWindow()
    {
        Component comp = getSwingWindow().getComponent();
        if (!sizeDefined(comp.getWidth(), comp.getHeight()))
        {
            getSwingWindow().packWindow();
        }
        if (isCenter())
        {
            center(comp, getSwingWindow().getParentWindow());
        }
        comp.setVisible(true);
    }

    /**
     * Returns a flag whether the window is to be centered when it is opened.
     *
     * @return the center flag
     */
    protected boolean isCenter()
    {
        return center;
    }

    /**
     * Creates a window event to be passed to the registered listeners.
     *
     * @param source the source of the event
     * @param type the type of the event
     * @return the event
     */
    protected WindowEvent createEvent(Object source, WindowEvent.Type type)
    {
        return new WindowEvent(source, getSwingWindow(), type);
    }

    /**
     * Sets the location of the specified component so that it gets centered in
     * the area of its parent window. The size of the component must have been
     * determined before. If no parent window exists, the component is centered
     * on the screen.
     *
     * @param c the component to center
     * @param parent the parent window
     */
    void center(Component c, Window parent)
    {
        int parentX;
        int parentY;
        int parentW;
        int parentH;

        if (parent == null)
        {
            Dimension scrSz = Toolkit.getDefaultToolkit().getScreenSize();
            parentX = 0;
            parentY = 0;
            parentW = scrSz.width;
            parentH = scrSz.height;
        }
        else
        {
            parentX = parent.getXPos();
            parentY = parent.getYPos();
            parentW = parent.getWidth();
            parentH = parent.getHeight();
        }

        c.setLocation((parentW - c.getWidth()) / 2 + parentX, (parentH - c
                .getHeight())
                / 2 + parentY);
    }
}
