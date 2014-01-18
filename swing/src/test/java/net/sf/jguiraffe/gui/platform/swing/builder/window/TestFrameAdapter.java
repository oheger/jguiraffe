/*
 * Copyright 2006-2014 The JGUIraffe Team.
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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import net.sf.jguiraffe.gui.builder.window.WindowClosingStrategy;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for FrameAdapter.
 *
 * @author Oliver Heger
 * @version $Id: TestFrameAdapter.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestFrameAdapter extends BaseSwingWindowTest
{
    /**
     * Creates the correct window implementation. This is an adapter for a Swing
     * JFrame object.
     *
     * @return the window to test
     */
    @Override
    protected SwingWindow createSwingWindow()
    {
        JFrame frame = new MockFrame();
        return new FrameAdapter(frame, false);
    }

    /**
     * Tests if the correct root container is returned.
     */
    @Test
    public void testGetRootContainer()
    {
        assertSame("Wrong root container returned",
                ((FrameAdapter) swingWindow).getFrame().getContentPane(),
                swingWindow.getRootContainer());
    }

    /**
     * Tests whether a default window listener is registered.
     */
    @Test
    public void testEventListener()
    {
        FrameAdapter adapter = (FrameAdapter) createSwingWindow();
        WindowListener[] listeners = adapter.getWindow().getWindowListeners();
        boolean found = false;
        for (WindowListener l : listeners)
        {
            if (l instanceof WindowListenerAdapter)
            {
                found = true;
            }
        }
        assertTrue("No window listener adapter registered", found);
    }

    /**
     * Tests whether the packWindow() implementation works correctly.
     */
    @Test
    public void testPackWindow()
    {
        FrameAdapter adapter = (FrameAdapter) createSwingWindow();
        adapter.packWindow();
        MockFrame mf = (MockFrame) adapter.getWindow();
        assertEquals("Wrong number of pack() calls", 1, mf.packCalls);
    }

    /**
     * Tests whether an auto-close listener can be registered and whether it
     * works as expected.
     */
    @Test
    public void testRegisterAutoCloseListener()
    {
        FrameAdapter adapter = (FrameAdapter) createSwingWindow();
        MockFrame frame = (MockFrame) adapter.getWindow();
        List<WindowListener> listeners = Arrays.asList(frame
                .getWindowListeners());
        adapter.registerAutoCloseListener();
        List<WindowListener> diffList = new ArrayList<WindowListener>(Arrays
                .asList(frame.getWindowListeners()));
        diffList.removeAll(listeners);
        assertEquals("No listener added", 1, diffList.size());
        frame.setVisible(true);
        diffList.get(0).windowClosing(new WindowEvent(frame, 0));
        assertEquals("Dispose not called", 1, frame.disposeCalls);
    }

    /**
     * Tests whether the correct root pane is returned.
     */
    @Test
    public void testGetRootPane()
    {
        SwingWindow window = createSwingWindow();
        assertSame("Wrong root pane", ((FrameAdapter) window).getFrame()
                .getRootPane(), window.getRootPane());
    }

    /**
     * Tests the dispose() implementation.
     */
    @Test
    public void testDispose()
    {
        FrameAdapter adapter = (FrameAdapter) createSwingWindow();
        MockFrame frame = (MockFrame) adapter.getWindow();
        adapter.dispose();
        assertEquals("Wrong number of dispose() calls", 1, frame.disposeCalls);
    }

    /**
     * Tests a successful close() operation.
     */
    @Test
    public void testCloseSuccessful()
    {
        FrameAdapter adapter = (FrameAdapter) createSwingWindow();
        MockFrame frame = (MockFrame) adapter.getWindow();
        assertTrue("Wrong result", adapter.close(true));
        assertEquals("Wrong number of dispose() calls", 1, frame.disposeCalls);
    }

    /**
     * Tests a close() operation if the closing strategy forbids closing the
     * window.
     */
    @Test
    public void testCloseFailed()
    {
        WindowClosingStrategy strat = EasyMock
                .createMock(WindowClosingStrategy.class);
        FrameAdapter adapter = (FrameAdapter) createSwingWindow();
        EasyMock.expect(strat.canClose(adapter)).andReturn(Boolean.FALSE);
        EasyMock.replay(strat);
        adapter.setWindowClosingStrategy(strat);
        MockFrame frame = (MockFrame) adapter.getWindow();
        assertFalse("Wrong result", adapter.close(false));
        assertEquals("Wrong number of dispose() calls", 0, frame.disposeCalls);
        EasyMock.verify(strat);
    }

    /**
     * A mock frame class used for testing. This frame overrides the
     * setVisible() method so that it cannot really be opened, but the
     * invocation is recorded.
     */
    private static class MockFrame extends JFrame
    {
        /**
         * The serial version UID.
         */
        private static final long serialVersionUID = -3845527352268325567L;

        /** The visible flag. */
        private boolean vis;

        /** The number of invocations of the pack() method.*/
        int packCalls;

        /** The number of invocations of the dispose() method. */
        int disposeCalls;

        @Override
        public boolean isVisible()
        {
            return vis;
        }

        @Override
        public void setVisible(boolean f)
        {
            vis = f;
        }

        @Override
        public void dispose()
        {
            setVisible(false);
            disposeCalls++;
        }

        @Override
        public void pack()
        {
            packCalls++;
        }
    }
}
