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
package net.sf.jguiraffe.gui.builder.window;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

/**
 * Test class for WindowUtils.
 *
 * @author Oliver Heger
 * @version $Id: TestWindowUtils.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestWindowUtils
{
    /**
     * Tests casting an object to a window, which indeed is a window.
     */
    @Test
    public void testToWindowWithValidWindow()
    {
        WindowImpl window = new WindowImpl();
        assertSame("Wrong window returned", window, WindowUtils
                .toWindow(window));
    }

    /**
     * Tests casting an object to a window, which is no window.
     */
    @Test
    public void testToWindowWithNonWindow()
    {
        assertNull("Non window was not detected", WindowUtils.toWindow("Hello"));
    }

    /**
     * Tests casting a null object to a window.
     */
    @Test
    public void testToWindowWithNull()
    {
        assertNull("Null object could be casted to a window", WindowUtils
                .toWindow(null));
    }

    /**
     * Tests casting an object to a window, which indeed is a window.
     */
    @Test
    public void testToWindowExWithValidWindow()
    {
        WindowImpl window = new WindowImpl();
        assertSame("Wrong window returned", window, WindowUtils
                .toWindowEx(window));
    }

    /**
     * Tests a forced cast to a window with a non window object. This should
     * cause an exception being thrown.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testToWindowExWithNonWindow()
    {
        WindowUtils.toWindowEx("No window");
    }

    /**
     * Tests a forced cast to a window with a null window object. This should
     * cause an exception being thrown.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testToWindowExWithNull()
    {
        WindowUtils.toWindowEx(null);
    }

    /**
     * Tests obtaining a window from an event that contains a valid window.
     */
    @Test
    public void testWindowFromEventWithValidWindow()
    {
        Window window = new WindowImpl();
        WindowEvent event = new WindowEvent(this, window,
                WindowEvent.Type.WINDOW_OPENED);
        assertSame("Wrong window returned from event", window, WindowUtils
                .windowFromEvent(event));
    }

    /**
     * Tests obtaining a window from an event that does not contain a valid
     * window.
     */
    @Test
    public void testWindowFromEventWithNonWindow()
    {
        WindowEvent event = new WindowEvent(this, null,
                WindowEvent.Type.WINDOW_OPENED);
        assertNull("Invalid window from event could be casted", WindowUtils
                .windowFromEvent(event));
    }

    /**
     * Tests obtaining a window from a null event.
     */
    @Test
    public void testWindowFromEventWithNullEvent()
    {
        assertNull("Could get window from null event", WindowUtils
                .windowFromEvent(null));
    }

    /**
     * Tests obtaining a window from an event that contains a valid window.
     */
    @Test
    public void testWindowFromEventExWithValidWindow()
    {
        Window window = new WindowImpl();
        WindowEvent event = new WindowEvent(this, window,
                WindowEvent.Type.WINDOW_OPENED);
        assertSame("Wrong window returned from event", window, WindowUtils
                .windowFromEventEx(event));
    }

    /**
     * Tests a forced cast from an event's window that is no valid window.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testWindowFromEventExWithNonWindow()
    {
        WindowUtils.windowFromEventEx(new WindowEvent(this, null,
                WindowEvent.Type.WINDOW_OPENED));
    }

    /**
     * Tests a forced cast from an event's window with a null event.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testWindowFromEventExWithNullEvent()
    {
        WindowUtils.windowFromEventEx(null);
    }

    /**
     * Tests accessing the platform specific window when it is the passed in
     * window itself.
     */
    @Test
    public void testGetPlatformWindowNonWrapped()
    {
        WindowImpl window = new WindowImpl();
        assertSame("Wrong platform window", window, WindowUtils
                .getPlatformWindow(window));
    }

    /**
     * Tests accessing the platform specific window when a wrapper is involved.
     */
    @Test
    public void testGetPlatformWindowWrapped()
    {
        Object platformWindow = "SomeTestWindow";
        assertSame("Platform window not found", platformWindow, WindowUtils
                .getPlatformWindow(new WindowWrapperImpl(platformWindow)));
    }

    /**
     * Tests accessing the platform specific window if it is wrapped multiple
     * times.
     */
    @Test
    public void testGetPlatformWindowRecursive()
    {
        Object platformWindow = "platFormWindow";
        Window wrap1 = new WindowWrapperImpl(platformWindow);
        Window wrap2 = new WindowWrapperImpl(wrap1);
        assertSame("Multiple times wrapped platform window not found",
                platformWindow, WindowUtils.getPlatformWindow(wrap2));
    }

    /**
     * Tests accessing the platform specific window with a null window.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetPlatformWindowWithNullArg()
    {
        WindowUtils.getPlatformWindow(null);
    }

    /**
     * Tests accessing the platform specific window if a wrapper returns null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetPlatformWindowWithNullWrapped()
    {
        Window wnd = new WindowWrapperImpl(null);
        WindowUtils.getPlatformWindow(wnd);
    }

    /**
     * An implementation of the <code>WindowWrapper</code> interface used for
     * testing.
     */
    static class WindowWrapperImpl extends WindowImpl implements WindowWrapper
    {
        /** Stores a reference to the wrapped window. */
        private Object wrappedWindow;

        /**
         * Creates a new instance of <code>WindowWrapperImpl</code> and sets
         * the wrapped object.
         *
         * @param wrapped the wrapped object
         */
        public WindowWrapperImpl(Object wrapped)
        {
            wrappedWindow = wrapped;
        }

        /**
         * Returns the wrapped window.
         *
         * @return the wrapped window
         */
        public Object getWrappedWindow()
        {
            return wrappedWindow;
        }

    }
}
