/*
 * Copyright 2006-2022 The JGUIraffe Team.
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
 * A test implementation of the WindowManager interface. This implementation
 * creates <code>WindowImpl</code> objects that have been initialized
 * according to the passed in data. They can be used to test the functionality
 * of the window tags.
 *
 * @author Oliver Heger
 * @version $Id: WindowManagerImpl.java 205 2012-01-29 18:29:57Z oheger $
 */
public class WindowManagerImpl implements WindowManager
{
    /** Constant for the window type <em>FRAME</em>. */
    public static final String WINDOW_FRAME = "FRAME";

    /** Constant for the window type <em>DIALOG</em>. */
    public static final String WINDOW_DIALOG = "DIALOG";

    /** Constant for the window type <em>IFRAME</em>. */
    public static final String WINDOW_IFRAME = "IFRAME";

    /**
     * Creates a frame window.
     *
     * @param builderData the builder data
     * @param data the window data
     * @param window the window to initialize or <b>null</b> for being created
     * @return the initialized window
     * @throws WindowBuilderException if an error occurs
     */
    public Window createFrame(WindowBuilderData builderData, WindowData data,
            Window wnd) throws WindowBuilderException
    {
        return createWindow(builderData, data, wnd, WINDOW_FRAME);
    }

    /**
     * Creates an internal frame window.
     *
     * @param builderData the builder data
     * @param data the window data
     * @param window the window to initialize or <b>null</b> for being created
     * @return the initialized window
     * @throws WindowBuilderException if an error occurs
     */
    public Window createInternalFrame(WindowBuilderData builderData,
            WindowData data, Window wnd) throws WindowBuilderException
    {
        return createWindow(builderData, data, wnd, WINDOW_IFRAME);
    }

    /**
     * Creates a dialog window.
     *
     * @param builderData the builder data
     * @param data the window data
     * @param modal the modal flag
     * @param window the window to initialize or <b>null</b> for being created
     * @return the initialized window
     * @throws WindowBuilderException if an error occurs
     */
    public Window createDialog(WindowBuilderData builderData, WindowData data,
            boolean modal, Window wnd) throws WindowBuilderException
    {
        WindowImpl result = createWindow(builderData, data, wnd, WINDOW_DIALOG);
        result.setModal(modal);
        return result;
    }

    /**
     * Helper method for creating a (test) window of the specified type. If the
     * passed in window is <b>null</b>, a new window of the specified type will
     * be created. Otherwise the window will be initialized using the passed in
     * data object.
     *
     * @param builderData the builder data
     * @param data the window data
     * @param window the window to initialize
     * @param typeName the window's type
     * @return the initialized window
     */
    protected WindowImpl createWindow(WindowBuilderData builderData,
            WindowData data, Window wnd, String typeName)
    {
        return (wnd == null) ? new WindowImpl(typeName) : initWindow(
                builderData, data, wnd);
    }

    /**
     * Initializes the passed in window object based on the specified data
     * objects.
     *
     * @param builderData the builder data
     * @param data the window data
     * @param window the window to initialize
     * @return the initialized window
     */
    protected WindowImpl initWindow(WindowBuilderData builderData,
            WindowData data, Window window)
    {
        WindowImpl wnd = (WindowImpl) window;
        wnd.setParent(builderData.getParentWindow());
        wnd.setBounds(data.getXPos(), data.getYPos(), data.getWidth(), data
                .getHeight());
        wnd.setCenter(data.isCenter());
        wnd.setIcon(data.getIcon());
        wnd.setMenuBar(data.getMenuBar());
        wnd.setTitle(data.getTitle());
        wnd.setFlags(windowFlags(data));
        wnd.setAutoClose(data.isAutoClose());
        wnd.setCloseOnEsc(data.isCloseOnEsc());
        wnd.setWindowController(data.getController());

        return wnd;
    }

    /**
     * Returns a string representation of the window flags defined by the given
     * window data object.
     *
     * @param data the data object
     * @return the flags as string
     */
    protected String windowFlags(WindowData data)
    {
        StringBuffer buf = new StringBuffer(5);
        if (data.isClosable())
        {
            buf.append('C');
        }
        if (data.isIconifiable())
        {
            buf.append('I');
        }
        if (data.isMaximizable())
        {
            buf.append('M');
        }
        if (data.isResizable())
        {
            buf.append('R');
        }
        return (buf.length() > 0) ? buf.toString() : null;
    }
}
