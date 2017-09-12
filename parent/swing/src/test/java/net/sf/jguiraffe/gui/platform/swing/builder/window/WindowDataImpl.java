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

import junit.framework.Assert;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.builder.window.WindowData;

/**
 * An implementation of the <code>WindowData</code> interface that can be used
 * for testing. For all properties required by the interface getter and setter
 * methods are defined.
 *
 * @author Oliver Heger
 * @version $Id: WindowDataImpl.java 205 2012-01-29 18:29:57Z oheger $
 */
public class WindowDataImpl implements WindowData
{
    ComponentBuilderData componentBuilderData;

    Object controller;

    Object icon;

    Object menuBar;

    String title;

    int xPos;

    int yPos;

    int width;

    int height;

    boolean center;

    boolean closable;

    boolean iconifiable;

    boolean maximizable;

    boolean resizable;

    boolean autoClose;

    boolean closeOnEsc;

    /**
     * Creates a new instance of <code>WindowDataImpl</code>.
     */
    public WindowDataImpl()
    {
        xPos = yPos = UNDEFINED;
        width = height = UNDEFINED;
    }

    public int getXPos()
    {
        return xPos;
    }

    public int getYPos()
    {
        return yPos;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public boolean isCenter()
    {
        return center;
    }

    public String getTitle()
    {
        return title;
    }

    public Object getIcon()
    {
        return icon;
    }

    public boolean isResizable()
    {
        return resizable;
    }

    public boolean isMaximizable()
    {
        return maximizable;
    }

    public boolean isIconifiable()
    {
        return iconifiable;
    }

    public boolean isClosable()
    {
        return closable;
    }

    public Object getMenuBar()
    {
        return menuBar;
    }

    public Object getController()
    {
        return controller;
    }

    public void setCenter(boolean center)
    {
        this.center = center;
    }

    public void setClosable(boolean closable)
    {
        this.closable = closable;
    }

    public void setController(Object controller)
    {
        this.controller = controller;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public void setIcon(Object icon)
    {
        this.icon = icon;
    }

    public void setIconifiable(boolean iconifiable)
    {
        this.iconifiable = iconifiable;
    }

    public void setMaximizable(boolean maximizable)
    {
        this.maximizable = maximizable;
    }

    public void setMenuBar(Object menuBar)
    {
        this.menuBar = menuBar;
    }

    public void setResizable(boolean resizable)
    {
        this.resizable = resizable;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public void setXPos(int pos)
    {
        xPos = pos;
    }

    public void setYPos(int pos)
    {
        yPos = pos;
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

    public ComponentBuilderData getComponentBuilderData()
    {
        return componentBuilderData;
    }

    public void setComponentBuilderData(ComponentBuilderData componentBuilderData)
    {
        this.componentBuilderData = componentBuilderData;
    }

    /**
     * Checks if the properties of the given window match the data specified in
     * this object. If this is not the case, an assertion failed error will be
     * thrown.
     *
     * @param window the window to check
     */
    public void checkWindow(Window window)
    {
        if (!isCenter())
        {
            if (getXPos() != UNDEFINED)
            {
                Assert.assertEquals("wrong x pos", getXPos(), window.getXPos());
            }
            if (getYPos() != UNDEFINED)
            {
                Assert.assertEquals("wrong y pos", getYPos(), window.getYPos());
            }
        }
        if (getWidth() != UNDEFINED && getHeight() != UNDEFINED)
        {
            Assert.assertEquals("Wrong width", getWidth(), window.getWidth());
            Assert
                    .assertEquals("Wrong height", getHeight(), window
                            .getHeight());
        }

        Assert.assertEquals("Wrong title", getTitle(), window.getTitle());
        Assert.assertSame("Wrong controller", getController(), window
                .getWindowController());
    }
}
