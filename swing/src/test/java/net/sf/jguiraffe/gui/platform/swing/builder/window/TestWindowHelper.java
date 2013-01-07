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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import net.sf.jguiraffe.gui.builder.event.FormMouseListener;
import net.sf.jguiraffe.gui.builder.window.WindowClosingStrategy;
import net.sf.jguiraffe.gui.builder.window.WindowData;
import net.sf.jguiraffe.gui.builder.window.WindowEvent;
import net.sf.jguiraffe.gui.builder.window.WindowListener;
import net.sf.jguiraffe.gui.platform.swing.builder.event.SwingEventAdapter;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for WindowHelper. This class tests some static utility methods in
 * the WindowHelper class. Other methods are tested together with the window
 * adapter implementations.
 *
 * @author Oliver Heger
 * @version $Id: TestWindowHelper.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestWindowHelper
{
    /** Constant for the number of test window listeners. */
    private static final int LISTENER_COUNT = 3;

    /** Stores a mock window. */
    private SwingWindow mockWindow;

    /** The helper to be tested. */
    private WindowHelperTestImpl helper;

    /**
     * Tests if a desktop pane is found that is directly passed to the method.
     */
    @Test
    public void testFindDesktopPaneDirectly()
    {
        JDesktopPane pane = new JDesktopPane();
        assertSame("Directly passed pane not found", pane, WindowHelper
                .findDesktopPane(pane));
    }

    /**
     * Tests if a desktop pane is found somewhere deep in a nested hierarchy.
     */
    @Test
    public void testFindDesktopPaneNested()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JLabel("test"), BorderLayout.WEST);
        JSplitPane split = new JSplitPane();
        panel.add(split, BorderLayout.CENTER);
        split.setLeftComponent(new JTextArea());
        JDesktopPane dp = new JDesktopPane();
        split.setRightComponent(dp);

        assertSame("Nested desktop pane could not be found", dp, WindowHelper
                .findDesktopPane(panel));
    }

    /**
     * Tests finding a desktop pane when invoked with a null argument.
     */
    @Test
    public void testFindDesktopPaneWithNull()
    {
        assertNull("Non null return when invoked with null", WindowHelper
                .findDesktopPane(null));
    }

    /**
     * Tests searching for a non existing desktop pane.
     */
    @Test
    public void testFindDesktopPaneNonExisting()
    {
        JPanel panel = new JPanel();
        panel.add(new JLabel("test"));
        JPanel p2 = new JPanel();
        panel.add(p2);
        p2.add(new JLabel("test2"));
        JPanel p3 = new JPanel();
        p2.add(p3);
        p3.add(new JLabel("test3"));
        p3 = new JPanel();
        p2.add(p3);
        p3.add(new JPanel());
        p3.add(new JLabel("test4"));

        assertNull("Found non existing pane", WindowHelper
                .findDesktopPane(panel));
    }

    /**
     * Tests if a defined size is detected.
     */
    @Test
    public void testSizeDefined()
    {
        WindowDataImpl data = new WindowDataImpl();
        data.setWidth(150);
        data.setHeight(100);
        assertTrue("Defined size not detected", WindowHelper.sizeDefined(data));
    }

    /**
     * Tests sizeDefined() if the size is only partly defined.
     */
    @Test
    public void testSizePartlyDefined()
    {
        WindowDataImpl data = new WindowDataImpl();
        data.setWidth(150);
        assertEquals("Height is defined", WindowData.UNDEFINED, data
                .getHeight());
        assertFalse("Partly defined size not detected", WindowHelper
                .sizeDefined(data));
    }

    /**
     * Tests if an undefined size is detected.
     */
    @Test
    public void testSizeUndefined()
    {
        WindowDataImpl data = new WindowDataImpl();
        assertEquals("Width is defined", WindowData.UNDEFINED, data.getWidth());
        assertEquals("Height is defined", WindowData.UNDEFINED, data
                .getHeight());
        assertFalse("Undefined size not detected", WindowHelper
                .sizeDefined(data));
    }

    /**
     * Tests whether a size of 0 is also considered undefined.
     */
    @Test
    public void testSizeDefinedZero()
    {
        assertFalse("Width 0 not undefined", WindowHelper.sizeDefined(0, 1));
        assertFalse("Height 0 not undefined", WindowHelper.sizeDefined(1, 0));
    }

    /**
     * Tests setting a component's bounds if all parameters are undefined.
     */
    @Test
    public void testInitComponentBoundsUndefined()
    {
        WindowDataImpl data = new WindowDataImpl();
        JFrame frame = new JFrame();
        WindowHelper.initComponentBounds(frame, data);
        assertEquals("Xpos not 0", 0, frame.getX());
        assertEquals("Ypos not 0", 0, frame.getY());
        assertEquals("Width is set", 0, frame.getWidth());
        assertEquals("Height is set", 0, frame.getHeight());
    }

    /**
     * Tests setting a component's bounds to defined values.
     */
    @Test
    public void testInitComponentBoundsDefined()
    {
        WindowDataImpl data = new WindowDataImpl();
        data.setXPos(10);
        data.setYPos(20);
        data.setWidth(300);
        data.setHeight(180);
        JFrame frame = new JFrame();
        WindowHelper.initComponentBounds(frame, data);
        assertEquals("Incorrect x", 10, frame.getX());
        assertEquals("Incorrect y", 20, frame.getY());
        assertEquals("Incorrect width", 300, frame.getWidth());
        assertEquals("Incorrect height", 180, frame.getHeight());
    }

    /**
     * Tests to center a component in the area of its parent window.
     */
    @Test
    public void testCenterInParentWindow()
    {
        setupHelper(true);
        JPanel c = new JPanel();
        c.setSize(150, 100);
        net.sf.jguiraffe.gui.builder.window.Window parent =
                EasyMock.createMock(net.sf.jguiraffe.gui.builder.window.Window.class);
        EasyMock.expect(parent.getXPos()).andReturn(10);
        EasyMock.expect(parent.getYPos()).andReturn(20);
        EasyMock.expect(parent.getWidth()).andReturn(400);
        EasyMock.expect(parent.getHeight()).andReturn(250);
        EasyMock.replay(parent);
        helper.center(c, parent);
        assertEquals("X pos incorrect", 135, c.getX());
        assertEquals("Y pos incorrect", 95, c.getY());
    }

    /**
     * Tests to center a component on the screen.
     */
    @Test
    public void testCenterOnScreen()
    {
        setupHelper(true);
        JPanel c = new JPanel();
        c.setSize(150, 100);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension d = toolkit.getScreenSize();
        helper.center(c, null);
        assertEquals("X pos incorrect", (d.width - 150) / 2, c.getX());
        assertEquals("Y pos incorrect", (d.height - 100) / 2, c.getY());
    }

    /**
     * Creates the test instance and initializes it.
     *
     * @param replayWndMock a flag whether the mock window should be replayed
     * @param center the center flag
     */
    private void setupHelper(boolean replayWndMock, boolean center)
    {
        mockWindow = EasyMock.createMock(SwingWindow.class);
        if (replayWndMock)
        {
            EasyMock.replay(mockWindow);
        }
        helper = new WindowHelperTestImpl(mockWindow, center);
    }

    /**
     * Creates the window helper test instance. Convenience method that sets the
     * center flag to false.
     *
     * @param replayWndMock a flag whether the mock window should be replayed
     */
    private void setupHelper(boolean replayWndMock)
    {
        setupHelper(replayWndMock, false);
    }

    /**
     * Tests adding a null listener. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddWindowListenerNull()
    {
        setupHelper(true);
        helper.addWindowListener(null);
    }

    /**
     * Tests whether window listeners can be removed.
     */
    @Test
    public void testRemoveWindowListener()
    {
        WindowListener l = EasyMock.createMock(WindowListener.class);
        EasyMock.replay(l);
        setupHelper(true);
        helper.addWindowListener(l);
        helper.removeWindowListener(l);
        helper.fireWindowOpened(this);
        EasyMock.verify(mockWindow);
    }

    /**
     * Prepares a test for firing a window event. Some listeners a created and
     * registered at the window helper. They are prepared to expect an event of
     * the specified type.
     *
     * @param type the event type
     * @return an array with the window listeners
     */
    private WindowListenerImpl[] prepareEventTest(WindowEvent.Type type)
    {
        setupHelper(true);
        WindowListenerImpl[] listeners = new WindowListenerImpl[LISTENER_COUNT];
        for (int i = 0; i < listeners.length; i++)
        {
            listeners[i] = new WindowListenerImpl();
            listeners[i].setExpectedSource(this);
            listeners[i].setExpectedWindow(mockWindow);
            listeners[i].setExpectedCalls(type, 1);
            helper.addWindowListener(listeners[i]);
        }
        return listeners;
    }

    /**
     * Verifies the specified listener mocks.
     *
     * @param listeners an array with the listeners
     */
    private void verifyListeners(WindowListenerImpl[] listeners)
    {
        for (WindowListenerImpl l : listeners)
        {
            l.check();
        }
        EasyMock.verify(mockWindow);
    }

    /**
     * Tests firing window activated events.
     */
    @Test
    public void testFireWindowActivated()
    {
        WindowListenerImpl[] listeners = prepareEventTest(WindowEvent.Type.WINDOW_ACTIVATED);
        helper.fireWindowActivated(this);
        verifyListeners(listeners);
    }

    /**
     * Tests firing window closed events.
     */
    @Test
    public void testFireWindowClosed()
    {
        WindowListenerImpl[] listeners = prepareEventTest(WindowEvent.Type.WINDOW_CLOSED);
        helper.fireWindowClosed(this);
        verifyListeners(listeners);
    }

    /**
     * Tests whether window closing events are correctly fired.
     */
    @Test
    public void testFireWindowClosing()
    {
        WindowListenerImpl[] listeners = prepareEventTest(WindowEvent.Type.WINDOW_CLOSING);
        helper.fireWindowClosing(this);
        verifyListeners(listeners);
    }

    /**
     * Tests firing window deactivated events.
     */
    @Test
    public void testFireWindowDeactivated()
    {
        WindowListenerImpl[] listeners = prepareEventTest(WindowEvent.Type.WINDOW_DEACTIVATED);
        helper.fireWindowDeactivated(this);
        verifyListeners(listeners);
    }

    /**
     * Tests firing window deiconified events.
     */
    @Test
    public void testFireWindowDeiconified()
    {
        WindowListenerImpl[] listeners = prepareEventTest(WindowEvent.Type.WINDOW_DEICONIFIED);
        helper.fireWindowDeiconified(this);
        verifyListeners(listeners);
    }

    /**
     * Tests firing window iconified events.
     */
    @Test
    public void testFireWindowIconified()
    {
        WindowListenerImpl[] listeners = prepareEventTest(WindowEvent.Type.WINDOW_ICONIFIED);
        helper.fireWindowIconified(this);
        verifyListeners(listeners);
    }

    /**
     * Tests firing window opened events.
     */
    @Test
    public void testFireWindowOpened()
    {
        WindowListenerImpl[] listeners = prepareEventTest(WindowEvent.Type.WINDOW_OPENED);
        helper.fireWindowOpened(this);
        verifyListeners(listeners);
    }

    /**
     * Tests opening a window on the event dispatch thread.
     */
    @Test
    public void testOpenWindowEDT() throws InterruptedException,
            InvocationTargetException
    {
        setupHelper(false);
        JComponent comp = prepareOpenTest();
        EasyMock.replay(mockWindow);
        SwingUtilities.invokeAndWait(new Runnable()
        {
            public void run()
            {
                helper.openWindow();
            }
        });
        assertTrue("Component not visible", comp.isVisible());
        EasyMock.verify(mockWindow);
    }

    /**
     * Prepares a test for opening a window. The mock window is configured
     * correspondingly.
     *
     * @return the window's main component
     */
    private JComponent prepareOpenTest()
    {
        JComponent comp = new JLabel();
        EasyMock.expect(mockWindow.getComponent()).andReturn(comp);
        mockWindow.packWindow();
        return comp;
    }

    /**
     * Tests opening a window when we are not on the EDT.
     */
    @Test
    public void testOpenWindowNonEDT()
    {
        setupHelper(false);
        JComponent comp = prepareOpenTest();
        EasyMock.replay(mockWindow);
        helper.openWindow();
        assertTrue("Component not visible", comp.isVisible());
        EasyMock.verify(mockWindow);
    }

    /**
     * Tests opening a window when we are not on the EDT and an exception
     * occurs.
     */
    @Test
    public void testOpenWindowNonEDTEx()
    {
        setupHelper(false);
        EasyMock.expect(mockWindow.getComponent()).andThrow(
                new RuntimeException("Test exception!"));
        EasyMock.replay(mockWindow);
        try
        {
            helper.openWindow();
            fail("Exception not detected!");
        }
        catch (RuntimeException rex)
        {
            EasyMock.verify(mockWindow);
        }
    }

    /**
     * Tests opening a window when a size is defined and hence pack() need not
     * be called.
     */
    @Test
    public void testOpenWindowNoPack()
    {
        setupHelper(false);
        @SuppressWarnings("serial")
        Window wnd = new Window(new Frame())
        {
            @Override
            public void setVisible(boolean b)
            {
                // just a mock
            }
        };
        wnd.setSize(320, 200);
        EasyMock.expect(mockWindow.getComponent()).andReturn(wnd);
        EasyMock.replay(mockWindow);
        helper.openWindow();
        EasyMock.verify(mockWindow);
    }

    /**
     * Prepares the helper object for a mouse listener test. Sets up the window
     * mock to return a test component.
     *
     * @param compAccessCount the expected number of accesses to the test
     *        component
     * @return the test component
     */
    private Component prepareMouseListenerTest(int compAccessCount)
    {
        setupHelper(false);
        Component comp = new Window(new Frame());
        EasyMock.expect(mockWindow.getComponent()).andReturn(comp).times(
                compAccessCount);
        EasyMock.replay(mockWindow);
        return comp;
    }

    /**
     * Helper method for searching for the specified form mouse listener.
     *
     * @param comp the component where the listener is registered
     * @param l the listener to search for
     * @return the number of occurrences
     */
    private static int findMouseListener(Component comp, FormMouseListener l)
    {
        int found = 0;
        for (MouseListener ml : comp.getMouseListeners())
        {
            if (ml instanceof SwingEventAdapter)
            {
                if (((SwingEventAdapter) ml).getEventListener().equals(l))
                {
                    found++;
                }
            }
        }
        return found;
    }

    /**
     * Tests whether a mouse listener can be added.
     */
    @Test
    public void testAddMouseListener()
    {
        FormMouseListener l = EasyMock.createMock(FormMouseListener.class);
        EasyMock.replay(l);
        Component c = prepareMouseListenerTest(1);
        helper.addMouseListener(l);
        assertEquals("Wrong number of registrations", 1,
                findMouseListener(c, l));
        EasyMock.verify(mockWindow, l);
    }

    /**
     * Tests whether the same mouse listener can be added multiple times.
     */
    @Test
    public void testAddMouseListenerMultipleTimes()
    {
        FormMouseListener l = EasyMock.createMock(FormMouseListener.class);
        EasyMock.replay(l);
        Component c = prepareMouseListenerTest(LISTENER_COUNT);
        for (int i = 0; i < LISTENER_COUNT; i++)
        {
            helper.addMouseListener(l);
        }
        assertEquals("Wrong number of registrations", LISTENER_COUNT,
                findMouseListener(c, l));
        EasyMock.verify(mockWindow, l);
    }

    /**
     * Tests whether null listeners are handled correctly.
     */
    @Test
    public void testAddMouseListenerNull()
    {
        setupHelper(true);
        helper.addMouseListener(null);
        EasyMock.verify(mockWindow);
    }

    /**
     * Tests whether a mouse listener can be removed.
     */
    @Test
    public void testRemoveMouseListener()
    {
        FormMouseListener l = EasyMock.createMock(FormMouseListener.class);
        EasyMock.replay(l);
        Component c = prepareMouseListenerTest(2);
        helper.addMouseListener(l);
        helper.removeMouseListener(l);
        assertEquals("Wrong number of registrations", 0,
                findMouseListener(c, l));
        EasyMock.verify(mockWindow, l);
    }

    /**
     * Tests whether mouse listeners can be removed that have been added
     * multiple times.
     */
    @Test
    public void testRemoveMouseListenerMultipleTimes()
    {
        FormMouseListener l = EasyMock.createMock(FormMouseListener.class);
        EasyMock.replay(l);
        Component c = prepareMouseListenerTest(LISTENER_COUNT * 2);
        for (int i = 0; i < LISTENER_COUNT; i++)
        {
            helper.addMouseListener(l);
        }
        helper.removeMouseListener(l);
        assertEquals("Wrong number of registrations (1)", LISTENER_COUNT - 1,
                findMouseListener(c, l));
        for (int i = 1; i < LISTENER_COUNT - 1; i++)
        {
            helper.removeMouseListener(l);
        }
        assertEquals("Wrong number of registrations (2)", 1, findMouseListener(
                c, l));
        helper.removeMouseListener(l);
        assertEquals("Wrong number of registrations (3)", 0, findMouseListener(
                c, l));
        EasyMock.verify(mockWindow, l);
    }

    /**
     * Tries to remove an unknown mouse listener. This should have no effect.
     */
    @Test
    public void testRemoveMouseListenerNotRegistered()
    {
        FormMouseListener l = EasyMock.createMock(FormMouseListener.class);
        FormMouseListener l2 = EasyMock.createMock(FormMouseListener.class);
        EasyMock.replay(l, l2);
        Component c = prepareMouseListenerTest(1);
        helper.addMouseListener(l);
        MouseListener[] listeners = c.getMouseListeners();
        helper.removeMouseListener(l2);
        MouseListener[] listeners2 = c.getMouseListeners();
        assertTrue("Mouse listeners have been modified", Arrays.equals(
                listeners, listeners2));
        EasyMock.verify(mockWindow, l, l2);
    }

    /**
     * Tests whether a window can be closed if the force flag is set to true.
     */
    @Test
    public void testCloseWindowForce()
    {
        setupHelper(false);
        WindowClosingStrategy strat = EasyMock
                .createMock(WindowClosingStrategy.class);
        mockWindow.dispose();
        EasyMock.replay(strat, mockWindow);
        helper.setWindowClosingStrategy(strat);
        assertTrue("Wrong result", helper.closeWindow(true));
        EasyMock.verify(strat, mockWindow);
    }

    /**
     * Tests closeWindow() if the strategy forbids closing the window.
     */
    @Test
    public void testCloseWindowStrategyForbids()
    {
        setupHelper(false);
        WindowClosingStrategy strat = EasyMock
                .createMock(WindowClosingStrategy.class);
        EasyMock.expect(strat.canClose(mockWindow)).andReturn(Boolean.FALSE);
        EasyMock.replay(strat, mockWindow);
        helper.setWindowClosingStrategy(strat);
        assertFalse("Wrong result", helper.closeWindow(false));
        EasyMock.verify(strat, mockWindow);
    }

    /**
     * Tests closeWindow() if the strategy allows closing the window.
     */
    @Test
    public void testCloseWindowStrategyAllows()
    {
        setupHelper(false);
        WindowClosingStrategy strat = EasyMock
                .createMock(WindowClosingStrategy.class);
        EasyMock.expect(strat.canClose(mockWindow)).andReturn(Boolean.TRUE);
        mockWindow.dispose();
        EasyMock.replay(strat, mockWindow);
        helper.setWindowClosingStrategy(strat);
        assertTrue("Wrong result", helper.closeWindow(false));
        EasyMock.verify(strat, mockWindow);
    }

    /**
     * Tests whether doOpen() calls center() if necessary.
     */
    @Test
    public void testDoOpenCentered()
    {
        setupHelper(false, true);
        net.sf.jguiraffe.gui.builder.window.Window parent = EasyMock
                .createMock(net.sf.jguiraffe.gui.builder.window.Window.class);
        EasyMock.expect(mockWindow.getParentWindow()).andReturn(parent);
        JComponent comp = prepareOpenTest();
        EasyMock.replay(mockWindow, parent);
        helper.mockCenter = true;
        helper.doOpenWindow();
        assertEquals("Wrong center component", comp, helper.centerComponent);
        assertEquals("Wrong center parent", parent, helper.centerParent);
        EasyMock.verify(mockWindow, parent);
    }

    /**
     * Tests doOpen() if the window should not be centered.
     */
    @Test
    public void testDoOpenNonCentered()
    {
        setupHelper(false, false);
        prepareOpenTest();
        EasyMock.replay(mockWindow);
        helper.doOpenWindow();
        assertNull("Got a center component", helper.centerComponent);
    }

    /**
     * A test window helper implementation that allows some stubbing
     * functionality.
     */
    private static class WindowHelperTestImpl extends WindowHelper
    {
        /** Stores the component to be centered. */
        Component centerComponent;

        /** The parent window for the center operation. */
        net.sf.jguiraffe.gui.builder.window.Window centerParent;

        /** A flag whether center is to be mocked. */
        boolean mockCenter;

        public WindowHelperTestImpl(SwingWindow window, boolean center)
        {
            super(window, center);
        }

        /**
         * {@inheritDoc} Records this invocation. Optionally mocks this method.
         */
        @Override
        void center(Component c,
                net.sf.jguiraffe.gui.builder.window.Window parent)
        {
            centerComponent = c;
            centerParent = parent;
            if (!mockCenter)
            {
                super.center(c, parent);
            }
        }
    }
}
