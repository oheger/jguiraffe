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

import java.awt.event.MouseListener;
import java.util.Collection;

import net.sf.jguiraffe.gui.builder.event.FormEventManager;
import net.sf.jguiraffe.gui.builder.event.FormMouseListener;
import net.sf.jguiraffe.gui.builder.event.PlatformEventManager;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.builder.window.WindowClosingStrategy;
import net.sf.jguiraffe.gui.builder.window.WindowUtils;
import net.sf.jguiraffe.gui.platform.swing.builder.event.MouseEventAdapter;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * <p>
 * An abstract base class for Swing window implementation tests.
 * </p>
 * <p>
 * This class implements a couple of tests for window implementations that
 * conform to the <code>SwingWindow</code> interface. Concrete sub classes must
 * implement the <code>createSwingWindow()</code> method to create the concrete
 * adapter implementation.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: BaseSwingWindowTest.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class BaseSwingWindowTest
{
    /** Stores the window to test. */
    protected SwingWindow swingWindow;

    @Before
    public void setUp() throws Exception
    {
        swingWindow = createSwingWindow();
    }

    /**
     * Tests setting and getting the window's coordinates.
     */
    @Test
    public void testBounds()
    {
        getWindow().setBounds(10, 20, 200, 100);
        assertEquals("Wrong xpos", 10, getWindow().getXPos());
        assertEquals("Wrong ypos", 20, getWindow().getYPos());
        assertEquals("Wrong width", 200, getWindow().getWidth());
        assertEquals("Wrong height", 100, getWindow().getHeight());
    }

    /**
     * Tests if a closing strategy can be set and accessed.
     */
    @Test
    public void testClosingStrategy()
    {
        WindowClosingStrategy strategy = EasyMock.createMock(WindowClosingStrategy.class);
        EasyMock.replay(strategy);
        assertNotNull("Closing strategy is null", getWindow()
                .getWindowClosingStrategy());
        getWindow().setWindowClosingStrategy(strategy);
        assertSame("Closing strategy was not stored", strategy, getWindow()
                .getWindowClosingStrategy());
        getWindow().setWindowClosingStrategy(null);
        assertNotNull("Closing strategy is null", getWindow()
                .getWindowClosingStrategy());
    }

    /**
     * Tests opening and closing the window.
     */
    @Test
    public void testOpenAndClose()
    {
        getWindow().open();
        assertTrue("Window not visible", getWindow().isVisible());
        getWindow().close(true);
        assertFalse("Window is not hidden", getWindow().isVisible());
    }

    /**
     * Tests showing and hiding the window.
     */
    @Test
    public void testVisible()
    {
        getWindow().setVisible(true);
        assertTrue("Window not visible", getWindow().isVisible());
        getWindow().setVisible(false);
        assertFalse("Window is not hidden", getWindow().isVisible());
    }

    /**
     * Tests accessing the window's title.
     */
    @Test
    public void testWindowTitle()
    {
        getWindow().setTitle("My window");
        assertEquals("Title not set", "My window", getWindow().getTitle());
    }

    /**
     * Tests whether a component is returned by the window.
     */
    @Test
    public void testGetComponent()
    {
        assertNotNull("No wrapped window", swingWindow.getComponent());
    }

    /**
     * Tests dealing with the window's controller.
     */
    @Test
    public void testController()
    {
        assertNull("Controller after init is not null", swingWindow
                .getWindowController());
        Object ctrl = new Object();
        swingWindow.setWindowController(ctrl);
        assertSame("Controller not stored", ctrl, swingWindow
                .getWindowController());
    }

    /**
     * Tests whether the underlying window can be accessed.
     */
    @Test
    public void testGetUnderlyingWindow()
    {
        assertSame("Wrong underlying window", WindowUtils
                .getPlatformWindow(swingWindow), swingWindow.getComponent());
    }

    /**
     * Tests if window listeners can be correctly registered.
     */
    @Test
    public void testAddWindowListener()
    {
        assertEquals("Window listener registered at startup", 0, swingWindow
                .getWindowListeners().size());
        WindowListenerImpl listener = new WindowListenerImpl();
        swingWindow.addWindowListener(listener);
        assertEquals("Window listener was not registered", 1, swingWindow
                .getWindowListeners().size());
        assertEquals("Wrong listener", listener, swingWindow
                .getWindowListeners().iterator().next());
    }

    /**
     * Tests whether a window listener can be added using reflection.
     */
    @Test
    public void testAddWindowListenerReflection()
    {
        PlatformEventManager pe =
                EasyMock.createNiceMock(PlatformEventManager.class);
        EasyMock.replay(pe);
        WindowListenerImpl listener = new WindowListenerImpl();
        FormEventManager evMan = new FormEventManager(pe);
        evMan.addEventListenerToObject(swingWindow, "Window", listener);
        assertEquals("Window listener was not registered", 1, swingWindow
                .getWindowListeners().size());
        assertEquals("Wrong listener", listener, swingWindow
                .getWindowListeners().iterator().next());
    }

    /**
     * Tests adding a null window listener. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddWindowListenerNull()
    {
        swingWindow.addWindowListener(null);
    }

    /**
     * Tests removing a window listener.
     */
    @Test
    public void testRemoveWindowListener()
    {
        WindowListenerImpl l = new WindowListenerImpl();
        swingWindow.addWindowListener(l);
        swingWindow.removeWindowListener(l);
        assertTrue("Still got listeners", swingWindow.getWindowListeners()
                .isEmpty());
    }

    /**
     * Tests if a default closing strategy is set, which returns true.
     */
    @Test
    public void testDefaultClosingStrategy()
    {
        assertTrue("Not allowed to close", swingWindow
                .getWindowClosingStrategy().canClose(swingWindow));
    }

    /**
     * Tests if the closing strategy is called at a closing request.
     */
    @Test
    public void testClosingStrategyCalled()
    {
        WindowClosingStrategy strategy = EasyMock
                .createMock(WindowClosingStrategy.class);
        EasyMock.expect(strategy.canClose(swingWindow))
                .andReturn(Boolean.FALSE);
        EasyMock.replay(strategy);
        swingWindow.setWindowClosingStrategy(strategy);
        swingWindow.open();
        assertFalse("Wrong result", swingWindow.close(false));
        EasyMock.verify(strategy);
    }

    /**
     * Tests if the parent window can be correctly set and accessed.
     */
    @Test
    public void testParentWindow()
    {
        Window parent = EasyMock.createNiceMock(Window.class);
        EasyMock.replay(parent);
        swingWindow.setParentWindow(parent);
        assertSame("Parent window was not set", parent, swingWindow
                .getParentWindow());
    }

    /**
     * Tests whether mouse listeners can be added to the window.
     */
    @Test
    public void testAddMouseListener()
    {
        FormMouseListener l = EasyMock.createMock(FormMouseListener.class);
        EasyMock.replay(l);
        swingWindow.addMouseListener(l);
        Collection<MouseListener> listeners = swingWindow.getWindowHelper()
                .getMouseListeners();
        assertEquals("Wrong number of listeners", 1, listeners.size());
        MouseEventAdapter adapter = (MouseEventAdapter) listeners.iterator()
                .next();
        assertEquals("Wrong listener in adapter", l, adapter.getEventListener());
        EasyMock.verify(l);
    }

    /**
     * Tests whether mouse listeners can be removed.
     */
    @Test
    public void testRemoveMouseListener()
    {
        FormMouseListener l = EasyMock.createMock(FormMouseListener.class);
        EasyMock.replay(l);
        swingWindow.addMouseListener(l);
        swingWindow.removeMouseListener(l);
        assertTrue("Got still listeners", swingWindow.getWindowHelper()
                .getMouseListeners().isEmpty());
        EasyMock.verify(l);
    }

    /**
     * Returns the window to be tested.
     *
     * @return the test window
     */
    protected Window getWindow()
    {
        return swingWindow;
    }

    /**
     * Creates the concrete Swing window implementation to test.
     *
     * @return the new Swing window
     */
    protected abstract SwingWindow createSwingWindow();
}
