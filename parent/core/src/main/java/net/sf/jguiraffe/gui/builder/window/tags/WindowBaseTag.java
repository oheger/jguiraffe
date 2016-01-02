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
package net.sf.jguiraffe.gui.builder.window.tags;

import java.util.Collection;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.components.tags.ContainerTag;
import net.sf.jguiraffe.gui.builder.components.tags.IconSupport;
import net.sf.jguiraffe.gui.builder.components.tags.TextData;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.builder.window.WindowBuilderData;
import net.sf.jguiraffe.gui.builder.window.WindowBuilderException;
import net.sf.jguiraffe.gui.builder.window.WindowData;
import net.sf.jguiraffe.gui.builder.window.WindowManager;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A base class for all tag handler classes that create windows.
 * </p>
 * <p>
 * This class provides common functionality for all tag handler classes that
 * allow the definition of different windows. Especially it implements the
 * <code>WindowData</code> interface used by the
 * {@link net.sf.jguiraffe.gui.builder.window.WindowManager WindowManager} to
 * access the windows' properties and defines corresponding setter methods. The
 * handling of the newly created window is also already implemented.
 * </p>
 * <p>
 * Concrete sub classes must implement two methods: <code>createWindow()</code>
 * for creating the window and <code>initWindow()</code> for its initialization.
 * Both methods get passed all necessary information.
 * </p>
 * <p>
 * The following table lists all attributes supported by this tag handler base
 * class. They are available in all derived tag classes, too:
 * </p>
 * <p>
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">xpos</td>
 * <td>Defines the x coordinate of the new window.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">ypos</td>
 * <td>Defines the y coordinate of the new window.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">width</td>
 * <td>Defines the window's width. If no width is provided, the window will be
 * packed, i.e. sized to its minimum width and height.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">height</td>
 * <td>Defines the window's height.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">center</td>
 * <td>If this boolean attribute is set to <b>true</b>, the window will be
 * centered on the screen. The coordinates will be ignored then.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">title</td>
 * <td>Allows to specify the window's title directly.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">titleres</td>
 * <td>With this attribute a resource ID for the window's title can be
 * specified.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">resgrp</td>
 * <td>Allows to specify a resource group for the title. If this is not defined,
 * the builder's default resource group will be used.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">closable</td>
 * <td>A flag indicating whether the new window can be closed.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">iconifiable</td>
 * <td>A flag indicating whether the new window can be iconified.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">maximizable</td>
 * <td>A flag indicating whether the new window can be maximized.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">resizable</td>
 * <td>A flag indicating whether the new window can be resized.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">autoClose</td>
 * <td>This boolean attribute controls the window's behavior when the user
 * clicks on the close icon in the window's title bar. If set to <b>true</b>
 * (which is the default), the window is then closed automatically. If set to
 * <b>false</b>, nothing happens. In this case the developer is responsible for
 * registering an event listener which reacts on the <em>window closing</em>
 * event and invokes the close operation manually.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">closeOnEsc</td>
 * <td>This boolean attribute controls the window's behavior when the user
 * presses the {@code ESCAPE} key. If set to <b>true</b>, the window is then
 * closed automatically. Otherwise, the {@code ESCAPE} key has no special
 * meaning. The handling of the {@code ESCAPE} key is useful especially for
 * dialogs where pressing {@code ESCAPE} usually means that the user wants to
 * cancel editing. The default value of this attribute depends on the window
 * type: it is <b>true</b> for dialog windows and <b>false</b> otherwise.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">menu</td>
 * <td>With this attribute the window's menu bar can be specified. The
 * attribute's value must match the name of a menu bar that has previously been
 * definied using a <code>&lt;menubar&gt;</code> tag.</td>
 * <td valign="top">yes</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: WindowBaseTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class WindowBaseTag extends ContainerTag implements WindowData,
        IconSupport
{
    /** Stores the created window. */
    private Window window;

    /** Stores information about the title. */
    private TextData titleData;

    /** Stores the window's icon. */
    private Object icon;

    /** Stores the window's controller. */
    private Object controller;

    /** Stores the window's menu bar. */
    private Object menuBar;

    /** Stores the name of the menu bar. */
    private String menuName;

    /** Stores the window's x coordinate. */
    private int xPos;

    /** Stores the window's y coordinate. */
    private int yPos;

    /** Stores the window's width. */
    private int width;

    /** Stores the window's height. */
    private int height;

    /** Stores the center flag. */
    private boolean center;

    /** Stores the closable flag. */
    private boolean closable;

    /** Stores the iconifiable flag. */
    private boolean iconifiable;

    /** Stores the maximizable flag. */
    private boolean maximizable;

    /** Stores the resizable flag. */
    private boolean resizable;

    /** Stores the auto-close flag. */
    private boolean autoClose;

    /** Stores the close-on-escape flag. */
    private boolean closeOnEsc;

    /**
     * Creates a new instance of <code>WindowBaseTag</code>.
     */
    protected WindowBaseTag()
    {
        titleData = new TextData(this);
        setXpos(UNDEFINED);
        setYpos(UNDEFINED);
        setWidth(UNDEFINED);
        setHeight(UNDEFINED);
        setAutoClose(true);
    }

    /**
     * Returns the window created by this tag.
     *
     * @return the window
     */
    public Window getWindow()
    {
        return window;
    }

    /**
     * Returns the x coordinate of the new window.
     *
     * @return the window's x coordinate
     */
    public int getXPos()
    {
        return xPos;
    }

    /**
     * Setter method for the xpos attribute.
     *
     * @param v the attribute value
     */
    public void setXpos(int v)
    {
        xPos = v;
    }

    /**
     * Returns the y coordinate of the new window.
     *
     * @return the window's y coordinate
     */
    public int getYPos()
    {
        return yPos;
    }

    /**
     * Setter method for the ypos attribute.
     *
     * @param v the attribute's value
     */
    public void setYpos(int v)
    {
        yPos = v;
    }

    /**
     * Returns the width of the new window.
     *
     * @return the window's width
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Setter method of the width attribute.
     *
     * @param v the attribute's value
     */
    public void setWidth(int v)
    {
        width = v;
    }

    /**
     * Returns the height of the new window.
     *
     * @return the window's height
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Setter method of the height attribute.
     *
     * @param v the attribute's value
     */
    public void setHeight(int v)
    {
        height = v;
    }

    /**
     * Returns a flag whether the new window should be centered on the screen.
     *
     * @return the center flag
     */
    public boolean isCenter()
    {
        return center;
    }

    /**
     * Setter method of the center attribute.
     *
     * @param f the attribute's value
     */
    public void setCenter(boolean f)
    {
        center = f;
    }

    /**
     * Returns the window's title, no matter how it was defined.
     *
     * @return the window's title
     */
    public String getTitle()
    {
        return titleData.getCaption();
    }

    /**
     * Setter method of the title attribute.
     *
     * @param s the attribute's value
     */
    public void setTitle(String s)
    {
        titleData.setText(s);
    }

    /**
     * Setter method of the titleres attribute.
     *
     * @param s the attribute's value
     */
    public void setTitleres(String s)
    {
        titleData.setTextres(s);
    }

    /**
     * Setter method of the resgrp attribute.
     *
     * @param s the attribute's value
     */
    public void setResgrp(String s)
    {
        titleData.setResgrp(s);
    }

    /**
     * Returns the icon for this window.
     *
     * @return the window's icon
     */
    public Object getIcon()
    {
        return icon;
    }

    /**
     * Allows to set this window's icon. This method can be invoked by
     * <code>&lt;icon&gt;</code> tags in the body of this tag.
     *
     * @param icon the icon of this window
     */
    public void setIcon(Object icon)
    {
        this.icon = icon;
    }

    /**
     * Returns the resizable flag.
     *
     * @return the resizable flag
     */
    public boolean isResizable()
    {
        return resizable;
    }

    /**
     * Setter method of the resizable attribute.
     *
     * @param f the attribute's value
     */
    public void setResizable(boolean f)
    {
        resizable = f;
    }

    /**
     * Returns the maximizable flag.
     *
     * @return the maximizable flag
     */
    public boolean isMaximizable()
    {
        return maximizable;
    }

    /**
     * Setter method of the maximizable attribute.
     *
     * @param f the attribute's value
     */
    public void setMaximizable(boolean f)
    {
        maximizable = f;
    }

    /**
     * Returns the iconifiable flag.
     *
     * @return the iconifiable flag
     */
    public boolean isIconifiable()
    {
        return iconifiable;
    }

    /**
     * Setter method of the iconifiable attribute.
     *
     * @param f the attribute's value
     */
    public void setIconifiable(boolean f)
    {
        iconifiable = f;
    }

    /**
     * Returns the closable flag.
     *
     * @return the closable flag
     */
    public boolean isClosable()
    {
        return closable;
    }

    /**
     * Setter method of the closable attribute.
     *
     * @param f the attribute's value
     */
    public void setClosable(boolean f)
    {
        closable = f;
    }

    /**
     * Returns the menu bar of this window.
     *
     * @return this window's menu bar
     */
    public Object getMenuBar()
    {
        return menuBar;
    }

    /**
     * Setter method of the menu attribute.
     *
     * @param s the attribute's value
     */
    public void setMenu(String s)
    {
        menuName = s;
    }

    /**
     * Returns the controller object of this window.
     *
     * @return the window's controller
     */
    public Object getController()
    {
        return controller;
    }

    /**
     * Sets the controller of this window. This method can be called by tags in
     * the body of this tag.
     *
     * @param ctrl the controller
     */
    public void setController(Object ctrl)
    {
        controller = ctrl;
    }

    /**
     * Returns the auto-close flag.
     *
     * @return the auto-close flag
     */
    public boolean isAutoClose()
    {
        return autoClose;
    }

    /**
     * Sets the auto-close flag.
     *
     * @param autoClose the auto-close flag
     */
    public void setAutoClose(boolean autoClose)
    {
        this.autoClose = autoClose;
    }

    /**
     * Returns the close on escape flag.
     *
     * @return a flag whether the window should be closed if {@code ESCAPE} is
     *         pressed
     */
    public boolean isCloseOnEsc()
    {
        return closeOnEsc;
    }

    /**
     * Sets the close on escape flag. This flag controls the window's reaction
     * on pressing the {@code ESCAPE} key.
     *
     * @param closeOnEsc the close on escape flag
     */
    public void setCloseOnEsc(boolean closeOnEsc)
    {
        this.closeOnEsc = closeOnEsc;
    }

    /**
     * Returns the current {@code ComponentBuilderData} object. This object is
     * easily available to all tag handler classes.
     *
     * @return the current {@code ComponentBuilderData} object
     */
    public ComponentBuilderData getComponentBuilderData()
    {
        return getBuilderData();
    }

    /**
     * {@inheritDoc} This implementation ensures that the name of the default
     * button is reset after processing of the tag's body.
     */
    @Override
    protected void process() throws JellyTagException, FormBuilderException
    {
        super.process();
        getBuilderData().setDefaultButtonName(null);
    }

    /**
     * Creates the window container. This implementation performs all necessary
     * steps for creating and initializing a window. It mainly calls the
     * abstract <code>createWindow()</code> and <code>initWindow()</code>
     * methods and takes care for storing the new window in the builder data
     * instance.
     *
     * @param manager the component manager (ignored)
     * @param create the create flag
     * @param components a collection with the container's children
     * @return the new container object (the window's root container)
     * @throws FormBuilderException if an error occurs
     * @throws JellyTagException if the tag is incorrectly used
     */
    @Override
    protected Object createContainer(ComponentManager manager, boolean create,
            Collection<Object[]> components) throws FormBuilderException,
            JellyTagException
    {
        WindowBuilderData data = WindowBuilderData.get(getContext());
        if (create)
        {
            window = createWindow(data.getWindowManager(), data);
            data.putWindow(getName(), window);
        }
        else
        {
            menuBar = (menuName != null) ? fetchMenuBar(menuName) : null;
            window = initWindow(data.getWindowManager(), data, window);
            data.putWindow(getName(), window);
        }

        return window.getRootContainer();
    }

    /**
     * Inserts the newly created component to its parent container. This
     * implementation is left empty. Windows must not be added to any root
     * container. Probably there is no root container at all when a window is
     * created.
     *
     * @param name the name of this component
     * @param comp the new component
     */
    @Override
    protected void insertComponent(String name, Object comp)
    {
    }

    /**
     * Tries to obtain the menu bar with the given name. This implementation
     * searches the Jelly context for a variable with this name.
     *
     * @param name the name of the menu bar
     * @return the menu bar as an object
     * @throws WindowBuilderException if the window cannot be obtained
     */
    protected Object fetchMenuBar(String name) throws WindowBuilderException
    {
        Object bar = getContext().findVariable(name);
        if (bar == null)
        {
            throw new WindowBuilderException("Cannot find menu bar with name "
                    + name);
        }
        return bar;
    }

    /**
     * Creates the window represented by the given data object using the current
     * window manager. This method must be defined by concrete sub classes to
     * call the correct method of the window manager, according to the window's
     * type.
     *
     * @param manager a reference to the current window manager
     * @param data the window builder data
     * @return the newly created window
     * @throws WindowBuilderException if the window cannot be created
     */
    protected abstract Window createWindow(WindowManager manager,
            WindowBuilderData data) throws WindowBuilderException;

    /**
     * Initializes the window that was created by the
     * <code>createWindow()</code> method. This method is invoked after all
     * properties of the window have been set (i.e. after the tag's body have
     * been executed). Thus, a complete initialization can now be performed.
     *
     * @param manager the current window manager
     * @param data the window builder data
     * @param wnd the window to be initialized
     * @return the fully initialized window
     * @throws WindowBuilderException if an error occurs
     */
    protected abstract Window initWindow(WindowManager manager,
            WindowBuilderData data, Window wnd) throws WindowBuilderException;
}
