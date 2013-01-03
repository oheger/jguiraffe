/*
 * Copyright 2006-2012 The JGUIraffe Team.
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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.builder.window.WindowBuilderData;
import net.sf.jguiraffe.gui.builder.window.WindowBuilderException;
import net.sf.jguiraffe.gui.builder.window.WindowData;
import net.sf.jguiraffe.gui.builder.window.WindowManager;

/**
 * <p>
 * The Swing specific implementation of the <code>WindowManager</code>
 * interface.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingWindowManager.java 205 2012-01-29 18:29:57Z oheger $
 */
public class SwingWindowManager implements WindowManager
{
    /** Constant for the escape command. */
    private static final String CMD_ESCAPE = "ESCAPE";

    /**
     * Creates a frame window. This implementation returns a wrapper for a
     * <code>javax.swing.JFrame</code> object.
     *
     * @param builderData the builder data object
     * @param data the data for the window
     * @param wnd the window to be initialized
     * @return the window
     * @throws WindowBuilderException if an error occurs
     */
    public Window createFrame(WindowBuilderData builderData, WindowData data,
            Window wnd) throws WindowBuilderException
    {
        FrameAdapter result;

        if (wnd == null)
        {
            result = new FrameAdapter(new JFrame(), data.isCenter());
        }

        else
        {
            result = (FrameAdapter) wnd;
            initWindowBounds(result, data, builderData.getParentWindow());
            initSwingWindowProperties(result, data, builderData
                    .getParentWindow());
            JFrame frame = result.getFrame();
            if (data.getIcon() != null)
            {
                frame.setIconImage(((ImageIcon) data.getIcon()).getImage());
            }
            frame.setJMenuBar((JMenuBar) data.getMenuBar());
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }

        return result;
    }

    /**
     * Creates and initializes an internal frame window and adds it to its
     * parent frame. This implementation will return an object derived from
     * Swing's <code>JInternalFrame</code> class. This internal frame will
     * already have been added to the parent frame's desktop. For this to work
     * the parent window must be a Swing <code>JFrame</code> and it must
     * contain a <code>JDesktopPane</code> instance; otherwise an exception
     * will be thrown.
     *
     * @param builderData the builder data object
     * @param data the data for the window
     * @param wnd the window to be initialized
     * @return the window
     * @throws WindowBuilderException if an error occurs
     */
    public Window createInternalFrame(WindowBuilderData builderData,
            WindowData data, Window wnd) throws WindowBuilderException
    {
        if (wnd == null)
        {
            return new InternalFrameAdapter(data.isCenter());
        }

        else
        {
            InternalFrameAdapter iframe = (InternalFrameAdapter) wnd;
            initInternalFrameBounds(iframe, data, builderData.getParentWindow());
            initSwingWindowProperties(iframe, data, builderData
                    .getParentWindow());
            iframe.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
            iframe.setJMenuBar((JMenuBar) data.getMenuBar());
            iframe.setFrameIcon((Icon) data.getIcon());
            iframe.setClosable(data.isClosable());
            iframe.setIconifiable(data.isIconifiable());
            iframe.setMaximizable(data.isMaximizable());
            iframe.setResizable(data.isResizable());

            return iframe;
        }
    }

    /**
     * Creates a dialog window. This implementation returns a wrapper for a
     * <code>javax.swing.JDialog</code> object.
     *
     * @param builderData the builder data object
     * @param data the data for the window
     * @param modal the modal flag
     * @param wnd the window to be initialized
     * @return the window
     * @throws WindowBuilderException if an error occurs
     */
    public Window createDialog(WindowBuilderData builderData, WindowData data,
            boolean modal, Window wnd) throws WindowBuilderException
    {
        DialogAdapter result;

        if (wnd == null)
        {
            result = new DialogAdapter(createJDialog(builderData),
                    data.isCenter());
        }

        else
        {
            result = (DialogAdapter) wnd;
            initWindowBounds(result, data, builderData.getParentWindow());
            initSwingWindowProperties(result, data, builderData
                    .getParentWindow());
            JDialog dlg = result.getDialog();
            dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            dlg.setModal(modal);
            dlg.setResizable(data.isResizable());
            dlg.setJMenuBar((JMenuBar) data.getMenuBar());
        }

        return result;
    }

    /**
     * Initializes the bounds of the given AWT window object.
     *
     * @param window the adapter for the window to initialize
     * @param data the window data
     * @param parent the parent window
     */
    protected void initWindowBounds(WindowAdapter window, WindowData data,
            Window parent)
    {
        initWindowBounds((SwingWindow) window, data, parent);
    }

    /**
     * Initializes the bounds of a newly created window. Checks if bounds are
     * provided in the given window data object.
     *
     * @param window the window to initialize
     * @param data the window data
     * @param parent the parent window
     */
    protected void initWindowBounds(SwingWindow window, WindowData data,
            Window parent)
    {
        WindowHelper.initComponentBounds(window.getComponent(), data);
    }

    /**
     * Initializes the given Swing window implementation from the given data
     * object.
     *
     * @param window the window
     * @param data the data object
     * @param parent the parent window
     */
    protected void initSwingWindowProperties(SwingWindow window,
            WindowData data, Window parent)
    {
        window.setTitle(data.getTitle());
        window.setWindowController(data.getController());
        window.setParentWindow(parent);

        if (data.isAutoClose())
        {
            window.registerAutoCloseListener();
        }

        handleCloseOnEsc(window, data);
        handleDefaultButton(window, data);
    }

    /**
     * Creates a new dialog object. This method ensures that the correct owner
     * window will be set.
     *
     * @param builderData the builder data object (contains the parent window)
     * @return the new dialog
     */
    protected JDialog createJDialog(WindowBuilderData builderData)
    {
        if (builderData.getParentWindow() != null)
        {
            if (builderData.getParentWindow() instanceof FrameAdapter)
            {
                return new JDialog(((FrameAdapter) (builderData
                        .getParentWindow())).getFrame());
            }

            if (builderData.getParentWindow() instanceof DialogAdapter)
            {
                return new JDialog(((DialogAdapter) builderData
                        .getParentWindow()).getDialog());
            }
        }

        return new JDialog();
    }

    /**
     * Initializes the internal frame's bounds. It will also be added to its
     * parent's desktop (if this is not possible, an exception will be thrown).
     *
     * @param iframe the internal frame to initialize
     * @param data the data
     * @param parent the parent window
     * @throws WindowBuilderException if the internal frame cannot be added to
     * its parent
     */
    private void initInternalFrameBounds(InternalFrameAdapter iframe,
            WindowData data, Window parent) throws WindowBuilderException
    {
        if (!(parent instanceof FrameAdapter))
        {
            throw new WindowBuilderException(
                    "Parent window must be defined and a Swing frame window!");
        }

        JDesktopPane desktop = WindowHelper
                .findDesktopPane(((FrameAdapter) parent).getFrame());
        if (desktop == null)
        {
            throw new WindowBuilderException(
                    "No JDesktopPane found for adding the internal frame! "
                            + "Please use the DesktopPanelTag to define the parent "
                            + "frame's desktop panel.");
        }

        WindowHelper.initComponentBounds(iframe, data);
        if (data.isCenter())
        {
            iframe.setLocation((desktop.getWidth() - iframe.getWidth()) / 2,
                    (desktop.getHeight() - iframe.getHeight()) / 2);
        }

        desktop.add(iframe);
    }

    /**
     * Takes care about closing the window when the {@code ESCAPE} key is
     * pressed. This method checks whether the close on escape flag is set. If
     * so, it registers an action for this key that closes the window.
     *
     * @param window the window to be handled
     * @param data the window data object
     */
    @SuppressWarnings("serial")
    private void handleCloseOnEsc(final SwingWindow window, WindowData data)
    {
        if (data.isCloseOnEsc())
        {
            JRootPane rootPane = window.getRootPane();

            rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                    KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), CMD_ESCAPE);
            rootPane.getActionMap().put(CMD_ESCAPE, new AbstractAction()
            {
                public void actionPerformed(ActionEvent event)
                {
                    window.close(false);
                }
            });
        }
    }

    /**
     * Takes care about the default button of the specified window. This method
     * checks whether a default button is specified. If so, it is obtained from
     * the current {@link ComponentBuilderData} object and set as the window's
     * root pane's default button.
     *
     * @param window the window to be initialized
     * @param data the window data object
     */
    private void handleDefaultButton(SwingWindow window, WindowData data)
    {
        ComponentBuilderData builderData = data.getComponentBuilderData();
        if (builderData.getDefaultButtonName() != null)
        {
            JButton defBtn = (JButton) builderData.getComponent(builderData
                    .getDefaultButtonName());
            window.getRootPane().setDefaultButton(defBtn);
        }
    }
}
