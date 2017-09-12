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
package net.sf.jguiraffe.gui.builder.window;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.jguiraffe.gui.builder.components.Container;
import net.sf.jguiraffe.gui.builder.event.FormMouseListener;

/**
 * A test implementation of the <code>Window</code> interface. This class is
 * used for testing the window builder infrastructure. Properties exposed by the
 * interface are simply stored in member variables.
 *
 * @author Oliver Heger
 * @version $Id: WindowImpl.java 205 2012-01-29 18:29:57Z oheger $
 */
public class WindowImpl implements Window
{
    private Window parent;

    private WindowClosingStrategy closingStrategy;

    private Container rootContainer;

    private final Collection<WindowListener> windowListeners;

    private final Collection<FormMouseListener> mouseListeners;

    private Object windowController;

    private Object icon;

    private Object menuBar;

    private String title;

    private String flags;

    private String windowType;

    private int x;

    private int y;

    private int width;

    private int height;

    private boolean visible;

    private boolean center;

    private boolean autoClose;

    private boolean closeOnEsc;

    private Boolean modal;

    public WindowImpl()
    {
        this(null);
    }

    public WindowImpl(String type)
    {
        windowType = type;
        windowListeners = new CopyOnWriteArrayList<WindowListener>();
        mouseListeners = new CopyOnWriteArrayList<FormMouseListener>();
    }

    public boolean isVisible()
    {
        return visible;
    }

    public void setVisible(boolean f)
    {
        visible = f;
    }

    public void open()
    {
        // just a dummy
    }

    public boolean close(boolean force)
    {
        // just a dummy
        return true;
    }

    public int getXPos()
    {
        return x;
    }

    public int getYPos()
    {
        return y;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setBounds(int x, int y, int w, int h)
    {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    public Window getParentWindow()
    {
        return parent;
    }

    public void setParent(Window w)
    {
        parent = w;
    }

    public void addWindowListener(WindowListener l)
    {
        windowListeners.add(l);
    }

    public void removeWindowListener(WindowListener l)
    {
        windowListeners.remove(l);
    }

    public Collection<WindowListener> getWindowListeners()
    {
        return Collections.unmodifiableCollection(windowListeners);
    }

    public void addMouseListener(FormMouseListener l)
    {
        mouseListeners.add(l);
    }

    public void removeMouseListener(FormMouseListener l)
    {
        mouseListeners.remove(l);
    }

    public Collection<FormMouseListener> getMouseListeners()
    {
        return Collections.unmodifiableCollection(mouseListeners);
    }

    public WindowClosingStrategy getWindowClosingStrategy()
    {
        return closingStrategy;
    }

    public void setWindowClosingStrategy(
            WindowClosingStrategy windowClosingStrategy)
    {
        closingStrategy = windowClosingStrategy;
    }

    public Object getWindowController()
    {
        return windowController;
    }

    public void setWindowController(Object ctrl)
    {
        windowController = ctrl;
    }

    public Object getRootContainer()
    {
        if (rootContainer == null)
        {
            rootContainer = new Container("WindowRootContainer");
        }
        return rootContainer;
    }

    public Object getIcon()
    {
        return icon;
    }

    public void setIcon(Object icon)
    {
        this.icon = icon;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public boolean isCenter()
    {
        return center;
    }

    public void setCenter(boolean center)
    {
        this.center = center;
    }

    public String getFlags()
    {
        return flags;
    }

    public void setFlags(String flags)
    {
        this.flags = flags;
    }

    public Object getMenuBar()
    {
        return menuBar;
    }

    public void setMenuBar(Object menuBar)
    {
        this.menuBar = menuBar;
    }

    public String getWindowType()
    {
        return windowType;
    }

    public boolean isModal()
    {
        return (modal != null) ? modal.booleanValue() : false;
    }

    public void setModal(boolean modal)
    {
        this.modal = modal ? Boolean.TRUE : Boolean.FALSE;
    }

    public boolean isAutoClose()
    {
        return autoClose;
    }

    public void setAutoClose(boolean autoClose)
    {
        this.autoClose = autoClose;
    }

    public boolean isCloseOnEsc()
    {
        return closeOnEsc;
    }

    public void setCloseOnEsc(boolean closeOnEsc)
    {
        this.closeOnEsc = closeOnEsc;
    }

    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder("WINDOW ");
        buf.append(getWindowType());
        buf.append(" [ ");
        appendAttr(buf, "TITLE", getTitle());
        appendAttr(buf, "ICON", getIcon());
        appendAttr(buf, "FLAGS", getFlags());
        appendAttr(buf, "X", getXPos());
        appendAttr(buf, "Y", getYPos());
        appendAttr(buf, "W", getWidth());
        appendAttr(buf, "H", getHeight());
        if (isCenter())
        {
            buf.append("CENTER ");
        }
        appendAttr(buf, "MENU", getMenuBar());
        if (modal != null && !modal.booleanValue())
        {
            buf.append("NONMODAL ");
        }
        if (!isAutoClose())
        {
            buf.append("NOAUTOCLOSE ");
        }
        if (isCloseOnEsc())
        {
            buf.append("CLOSEONESC ");
        }
        buf.append("] { ");
        buf.append(getRootContainer());
        buf.append(" }");
        return buf.toString();
    }

    private static void appendAttr(StringBuilder buf, String name, Object value)
    {
        if (value != null)
        {
            buf.append(name).append(" = ").append(value).append(' ');
        }
    }

    private static void appendAttr(StringBuilder buf, String name, int value)
    {
        if (value != WindowData.UNDEFINED)
        {
            appendAttr(buf, name, String.valueOf(value));
        }
    }
}
