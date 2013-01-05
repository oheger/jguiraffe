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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.builder.window.WindowBuilderData;
import net.sf.jguiraffe.gui.builder.window.WindowBuilderException;
import net.sf.jguiraffe.gui.builder.window.WindowUtils;
import net.sf.jguiraffe.gui.forms.bind.BeanBindingStrategy;
import net.sf.jguiraffe.transform.TransformerContext;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for SwingWindowManager.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingWindowManager.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingWindowManager
{
    /** Constant for a dummy test controller object. */
    private static final Object TEST_CONTROLLER = "CONTROLLER";

    /** Constant for the title of the test window. */
    private static final String TEST_TITLE = "TestWindow";

    /** Constant for the name of the window's icon. */
    private static final String TEST_ICON = "/icon.gif";

    /** Constant for the test x pos. */
    private static final int TEST_X = 50;

    /** Constant for the text y pos. */
    private static final int TEST_Y = 40;

    /** Constant for the test width. */
    private static final int TEST_WIDTH = 300;

    /** Constant for the test height. */
    private static final int TEST_HEIGHT = 200;

    /** Constant for the parent width. */
    private static final int PARENT_WIDTH = 600;

    /** Constant for the parent height. */
    private static final int PARENT_HEIGHT = 400;

    /** The manager to be tested. */
    private SwingWindowManager manager;

    /** The window builder data object. */
    private WindowBuilderData builderData;

    /** Stores the icon used by windows. */
    private ImageIcon icon;

    @Before
    public void setUp() throws Exception
    {
        manager = new SwingWindowManager();
        builderData = new WindowBuilderData();
        builderData.setFormBean(manager);
    }

    /**
     * Tests creating a simple frame.
     */
    @Test
    public void testCreateFrame() throws WindowBuilderException
    {
        WindowDataImpl wdata = createWindowData(true);
        checkFrame(createFrame(wdata), wdata, false);
    }

    /**
     * Tests creating a frame with centered coordinates.
     */
    @Test
    public void testCreateCenteredFrame() throws WindowBuilderException
    {
        WindowDataImpl wdata = createWindowData(true);
        wdata.setCenter(true);
        checkFrame(createFrame(wdata), wdata, true);
    }

    /**
     * Tests creating a frame when no size is provided.
     */
    @Test
    public void testCreatePackedFrame() throws WindowBuilderException
    {
        WindowDataImpl wdata = createWindowData(false);
        TestFramePackObserver frame = new TestFramePackObserver();
        WindowAdapter adapter = new FrameAdapter(frame, false);
        checkFrame((SwingWindow) manager.createFrame(builderData, wdata,
                adapter), wdata, false);
        assertFalse("Pack was called", frame.packCalled);
    }

    /**
     * Tests creating a frame without an icon.
     */
    @Test
    public void testCreateFrameWithoutIcon() throws WindowBuilderException
    {
        WindowDataImpl wdata = createWindowData(true);
        wdata.setIcon(null);
        FrameAdapter adapter = (FrameAdapter) createFrame(wdata);
        assertNull("Frame has an icon", adapter.getFrame().getIconImage());
    }

    /**
     * Helper method for creating and initializing a frame window.
     *
     * @param data the window data object
     * @return the new frame
     * @throws WindowBuilderException if an error occurs
     */
    private SwingWindow createFrame(WindowDataImpl data)
            throws WindowBuilderException
    {
        SwingWindow window = (SwingWindow) manager.createFrame(builderData,
                data, null);
        return (SwingWindow) manager.createFrame(builderData, data, window);
    }

    /**
     * Helper method for testing if a frame window was correctly initialized.
     *
     * @param wnd the window
     * @param data the window data
     * @param center the expected center flag
     */
    private void checkFrame(SwingWindow wnd, WindowDataImpl data,
            boolean center)
    {
        checkWindow(wnd, data, center);
        assertTrue("Window is no JFrame",
                WindowUtils.getPlatformWindow(wnd) instanceof JFrame);
        JFrame frame = (JFrame) WindowUtils.getPlatformWindow(wnd);
        assertEquals("Wrong frame icon", icon.getImage(), frame.getIconImage());
        assertEquals("Wrong menu", data.getMenuBar(), frame.getJMenuBar());
        assertEquals("Wrong frame close operation", JFrame.DO_NOTHING_ON_CLOSE,
                frame.getDefaultCloseOperation());
    }

    /**
     * Tests creating a simple dialog.
     */
    @Test
    public void testCreateDialog() throws WindowBuilderException
    {
        checkCreateSimpleDialog(false, true);
    }

    /**
     * Tests creating a simple non modal dialog.
     */
    @Test
    public void testCreateNonModalDialog() throws WindowBuilderException
    {
        checkCreateSimpleDialog(false, false);
    }

    /**
     * Tests creating a dialog that is centered.
     */
    @Test
    public void testCreateCenteredDialog() throws WindowBuilderException
    {
        WindowDataImpl wdata = createWindowData(true);
        wdata.setCenter(true);
        SwingWindow window = createDialog(wdata, true);
        checkDialog(window, wdata, true, true);
    }

    /**
     * Tests creating a dialog when no size was set.
     */
    @Test
    public void testCreatePackedDialog() throws WindowBuilderException
    {
        WindowDataImpl wdata = createWindowData(false);
        TestDialogPackObserver dialog = new TestDialogPackObserver();
        WindowAdapter adapter = new DialogAdapter(dialog, false);
        checkDialog((SwingWindow) manager.createDialog(builderData, wdata,
                true, adapter), wdata, false, true);
        assertFalse("Pack was called", dialog.packCalled);
    }

    /**
     * Tests creating a dialog that can be resized.
     */
    @Test
    public void testCreateResizableDialog() throws WindowBuilderException
    {
        WindowDataImpl wdata = createWindowData(true);
        wdata.setResizable(true);
        checkDialog(createDialog(wdata, true), wdata, false, true);
    }

    /**
     * Tests creating a dialog that is a child window of a frame.
     */
    @Test
    public void testCreateDialogWithFrameOwner() throws WindowBuilderException
    {
        WindowDataImpl wdata = createWindowData(true);
        JFrame parent = new JFrame();
        builderData.setParentWindow(new FrameAdapter(parent, false));
        DialogAdapter adapter = (DialogAdapter) createDialog(wdata, true);
        assertEquals("Frame owner not set", parent, adapter.getDialog()
                .getOwner());
    }

    /**
     * Tests creating a dialog that is a child window of another dialog.
     */
    @Test
    public void testCreateDialogWithDialogOwner() throws WindowBuilderException
    {
        WindowDataImpl wdata = createWindowData(true);
        JDialog parent = new JDialog();
        builderData.setParentWindow(new DialogAdapter(parent, false));
        DialogAdapter adapter = (DialogAdapter) createDialog(wdata, true);
        assertEquals("Dialog owner not set", parent, adapter.getDialog()
                .getOwner());
    }

    /**
     * Helper method for testing the creation of simple dialogs, either modal or
     * non modal.
     *
     * @param center the center flag
     * @param modal the modal flag
     * @throws WindowBuilderException if an error occurs
     */
    private void checkCreateSimpleDialog(boolean center, boolean modal)
            throws WindowBuilderException
    {
        WindowDataImpl wdata = createWindowData(true);
        checkDialog(createDialog(wdata, modal), wdata, center, modal);
    }

    /**
     * Helper method for creating and initializing a dialog.
     *
     * @param data the data object
     * @param modal the modal flag
     * @return the dialog
     * @throws WindowBuilderException if an error occurs
     */
    private SwingWindow createDialog(WindowDataImpl data, boolean modal)
            throws WindowBuilderException
    {
        SwingWindow window = (SwingWindow) manager.createDialog(builderData,
                data, modal, null);
        return (SwingWindow) manager.createDialog(builderData, data, modal,
                window);
    }

    /**
     * Helper method for testing if a dialog window was correctly initialized.
     *
     * @param wnd the window
     * @param data the window data
     * @param center the center flag
     * @param modal a flag if this is a modal dialog
     */
    private void checkDialog(SwingWindow wnd, WindowDataImpl data,
            boolean center, boolean modal)
    {
        assertTrue("Window is not JDialog",
                WindowUtils.getPlatformWindow(wnd) instanceof JDialog);
        checkWindow(wnd, data, center);
        JDialog dlg = (JDialog) WindowUtils.getPlatformWindow(wnd);
        assertEquals("Wrong menu", data.getMenuBar(), dlg.getJMenuBar());
        assertEquals("Incorrect modal flag", modal, dlg.isModal());
        assertEquals("Incorrect resizable flag", data.isResizable(), dlg
                .isResizable());
        assertEquals("Wrong dialog close operation",
                JDialog.DO_NOTHING_ON_CLOSE, dlg.getDefaultCloseOperation());
    }

    /**
     * Tests creating a simple internal frame.
     */
    @Test
    public void testCreateInternalFrame() throws WindowBuilderException
    {
        WindowDataImpl wdata = createWindowData(true);
        checkInternalFrame(createInternalFrame(wdata, true), wdata, false);
    }

    /**
     * Tests creating an internal frame without a parent. This should cause an
     * exception.
     */
    @Test(expected = WindowBuilderException.class)
    public void testCreateInternalFrameWithoutParent() throws WindowBuilderException
    {
        createInternalFrame(createWindowData(true));
    }

    /**
     * Tests creating an internal frame when the parent frame does not contain a
     * desktop pane. This should cause an exception.
     */
    @Test(expected = WindowBuilderException.class)
    public void testCreateInternalFrameWithoutDesktop() throws WindowBuilderException
    {
        builderData.setParentWindow(new FrameAdapter(new JFrame(), false));
        createInternalFrame(createWindowData(true));
    }

    /**
     * Tests creating an internal frame that is centered relative to its desktop
     * pane.
     */
    @Test
    public void testCreateCenternedInternalFrame()
            throws WindowBuilderException
    {
        WindowDataImpl wdata = createWindowData(true);
        wdata.setCenter(true);
        InternalFrameAdapter iframe = (InternalFrameAdapter) createInternalFrame(
                wdata, true);
        checkInternalFrame(iframe, wdata, true);
    }

    /**
     * Tests creating an internal frame when no size was set.
     */
    @Test
    public void testCreatePackedInternalFrame() throws WindowBuilderException
    {
        WindowDataImpl wdata = createWindowData(false);
        setUpInternalFrameParent();
        TestInternalFramePackObserver iframe = new TestInternalFramePackObserver(
                false);
        manager.createInternalFrame(builderData, wdata, iframe);
        assertFalse("Pack was not called", iframe.packCalled);
    }

    /**
     * Creates a mock for a SwingWindow and prepares it for a
     * initSwingWindowProperties() invocation. All unconditional invocations are
     * expected.
     *
     * @param parent the parent window
     * @return the mock for the window
     */
    private SwingWindow prepareInitPropertiesTest(Window parent)
    {
        SwingWindow swingWindow = EasyMock.createMock(SwingWindow.class);
        swingWindow.setTitle(TEST_TITLE);
        swingWindow.setWindowController(TEST_CONTROLLER);
        swingWindow.setParentWindow(parent);
        return swingWindow;
    }

    /**
     * Helper method for checking the initialization of Swing-specific window
     * properties.
     *
     * @param autoClose the auto-close flag
     */
    private void checkInitSwingWindowProperties(boolean autoClose)
    {
        Window parent = EasyMock.createMock(Window.class);
        SwingWindow swingWindow = prepareInitPropertiesTest(parent);
        if (autoClose)
        {
            swingWindow.registerAutoCloseListener();
        }
        EasyMock.replay(swingWindow, parent);
        WindowDataImpl data = createWindowData(false);
        data.setAutoClose(autoClose);
        manager.initSwingWindowProperties(swingWindow, data, parent);
        EasyMock.verify(swingWindow, parent);
    }

    /**
     * Tests the initialization of Swing-specific window properties if the
     * auto-close flag is set.
     */
    @Test
    public void testInitSwingWindowPropertiesAutoClose()
    {
        checkInitSwingWindowProperties(true);
    }

    /**
     * Tests the initialization of Swing-specific window properties if the
     * auto-close flag is set to false.
     */
    @Test
    public void testInitSwingWindowPropertiesNoAutoClose()
    {
        checkInitSwingWindowProperties(false);
    }

    /**
     * Tests whether a key stroke for escape is registered if necessary.
     */
    @Test
    public void testInitSwingWindowPropertiesCloseOnEsc()
    {
        Window parent = EasyMock.createMock(Window.class);
        SwingWindow window = prepareInitPropertiesTest(parent);
        JRootPane rootPane = new JRootPane();
        EasyMock.expect(window.getRootPane()).andReturn(rootPane);
        EasyMock.expect(window.close(false)).andReturn(Boolean.TRUE);
        EasyMock.replay(window, parent);
        WindowDataImpl data = createWindowData(false);
        data.setCloseOnEsc(true);
        manager.initSwingWindowProperties(window, data, parent);
        Object obj = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .get(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
        assertNotNull("No key stroke registered for ESC", obj);
        Action action = rootPane.getActionMap().get(obj);
        assertNotNull("No action registered", action);
        action.actionPerformed(null);
        EasyMock.verify(window, parent);
    }

    /**
     * Tests whether the default button is handled correctly when initializing a
     * window.
     */
    @Test
    public void testInitSwingWindowPropertiesDefaultButton()
    {
        Window parent = EasyMock.createMock(Window.class);
        SwingWindow window = prepareInitPropertiesTest(parent);
        JRootPane rootPane = new JRootPane();
        EasyMock.expect(window.getRootPane()).andReturn(rootPane);
        EasyMock.replay(window, parent);
        final String defBtnName = "defaultButton";
        JButton defBtn = new JButton(defBtnName);
        WindowDataImpl data = createWindowData(false);
        data.getComponentBuilderData().storeComponent(defBtnName, defBtn);
        data.getComponentBuilderData().setDefaultButtonName(defBtnName);
        manager.initSwingWindowProperties(window, data, parent);
        assertSame("Wrong default button", defBtn, rootPane.getDefaultButton());
        EasyMock.verify(window, parent);
    }

    /**
     * Creates and initializes an internal frame.
     *
     * @param data the frame's data
     * @return the new internal frame
     * @throws WindowBuilderException if an error occurs
     */
    private SwingWindow createInternalFrame(WindowDataImpl data)
            throws WindowBuilderException
    {
        SwingWindow iframe = (SwingWindow) manager.createInternalFrame(
                builderData, data, null);
        return (SwingWindow) manager.createInternalFrame(builderData, data,
                iframe);
    }

    /**
     * Creates and initializes an internal frame. The internal frame's parent
     * window can also be created. If this parameter is set, a frame will be
     * created that contains a JDesktopPane.
     *
     * @param data the frame's data
     * @param withParent the parent flag
     * @return the new internal frame
     * @throws WindowBuilderException if an error occurs
     */
    private SwingWindow createInternalFrame(WindowDataImpl data,
            boolean withParent) throws WindowBuilderException
    {
        if (withParent)
        {
            setUpInternalFrameParent();
        }

        return createInternalFrame(data);
    }

    /**
     * Helper method for initializing a parent frame window that suits the needs
     * of an internal frame.
     */
    private void setUpInternalFrameParent()
    {
        JFrame frame = new JFrame("MainFrame");
        JDesktopPane desktop = new JDesktopPane();
        desktop.setPreferredSize(new Dimension(PARENT_WIDTH, PARENT_HEIGHT));
        frame.getContentPane().add(desktop, BorderLayout.CENTER);
        frame.pack();
        builderData.setParentWindow(new FrameAdapter(frame, false));
    }

    /**
     * Checks all attributes of the given internal frame.
     *
     * @param window the internal frame to check
     * @param data the expected data
     * @param center the expected center flag
     */
    private void checkInternalFrame(SwingWindow window, WindowDataImpl data,
            boolean center)
    {
        assertTrue("Window is no internal frame",
                window instanceof InternalFrameAdapter);
        checkWindow(window, data, center);
        JInternalFrame iframe = (InternalFrameAdapter) window;
        assertEquals("Wrong internal frame close operation",
                JInternalFrame.DO_NOTHING_ON_CLOSE,
                iframe.getDefaultCloseOperation());
        assertEquals("Wrong menu", data.getMenuBar(), iframe.getJMenuBar());
        assertEquals("Wrong resizable flag", data.isResizable(),
                iframe.isResizable());
        assertEquals("Wrong closable flag", data.isClosable(),
                iframe.isClosable());
        assertEquals("Wrong maximizable flag", data.isMaximizable(),
                iframe.isMaximizable());
        assertEquals("Wrong iconifiable flag", data.isIconifiable(),
                iframe.isIconifiable());
        assertEquals("Wrong icon", data.getIcon(), iframe.getFrameIcon());
        assertTrue("Wrong parent window",
                builderData.getParentWindow() instanceof FrameAdapter);
        JDesktopPane desktop = WindowHelper
                .findDesktopPane(((FrameAdapter) builderData.getParentWindow())
                        .getFrame());
        assertNotNull("No desktop pane", desktop);
        assertEquals("Internal frame was not added to desktop pane", 1,
                desktop.getComponentCount());
    }

    /**
     * Creates a window data object with test data.
     *
     * @param bounds a flag whether the window's bounds should be initialized
     * @return an initialized window data object
     */
    private WindowDataImpl createWindowData(boolean bounds)
    {
        TransformerContext tctx = EasyMock.createNiceMock(TransformerContext.class);
        EasyMock.replay(tctx);
        WindowDataImpl data = new WindowDataImpl();
        data.setController(TEST_CONTROLLER);
        data.setTitle(TEST_TITLE);
        icon = new ImageIcon(getClass().getResource(TEST_ICON));
        data.setIcon(icon);
        JMenuBar bar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menu.add(new JMenuItem("Exit"));
        bar.add(menu);
        data.setMenuBar(bar);
        ComponentBuilderData builderData = new ComponentBuilderData();
        builderData.initializeForm(tctx, new BeanBindingStrategy());
        data.setComponentBuilderData(builderData);

        if (bounds)
        {
            data.setXPos(TEST_X);
            data.setYPos(TEST_Y);
            data.setWidth(TEST_WIDTH);
            data.setHeight(TEST_HEIGHT);
        }

        return data;
    }

    /**
     * Tests if the window's properties match the given data object.
     *
     * @param window the window
     * @param data the data
     * @param center the expected center flag
     */
    private void checkWindow(SwingWindow window, WindowDataImpl data,
            boolean center)
    {
        data.checkWindow(window);
        assertEquals("Wrong parent window", builderData.getParentWindow(),
                window.getParentWindow());
        assertEquals("Wrong center flag", center, window.getWindowHelper()
                .isCenter());
    }

    /**
     * A test frame class that checks whether the pack() method was called.
     */
    static class TestFramePackObserver extends JFrame
    {
        /**
         * The serial version UID.
         */
        private static final long serialVersionUID = -1409228441149802638L;

        boolean packCalled;

        @Override
        public void pack()
        {
            super.pack();
            packCalled = true;
        }
    }

    /**
     * A test dialog class that checks whether the pack() method was called.
     */
    static class TestDialogPackObserver extends JDialog
    {
        /**
         * The serial version UID.
         */
        private static final long serialVersionUID = 4757232434966886907L;

        boolean packCalled;

        @Override
        public void pack()
        {
            super.pack();
            packCalled = true;
        }
    }

    /**
     * A test internal frame class that checks whether the pack() method was
     * called.
     */
    static class TestInternalFramePackObserver extends InternalFrameAdapter
    {
        /**
         * The serial version UID.
         */
        private static final long serialVersionUID = -3859327208598578295L;

        boolean packCalled;

        public TestInternalFramePackObserver(boolean center)
        {
            super(center);
        }

        @Override
        public void pack()
        {
            super.pack();
            packCalled = true;
        }
    }
}
