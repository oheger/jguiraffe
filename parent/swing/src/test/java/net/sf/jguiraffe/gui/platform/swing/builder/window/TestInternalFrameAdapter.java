/*
 * Copyright 2006-2025 The JGUIraffe Team.
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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import net.sf.jguiraffe.gui.builder.window.WindowClosingStrategy;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for InternalFrameAdapter.
 *
 * @author Oliver Heger
 * @version $Id: TestInternalFrameAdapter.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestInternalFrameAdapter extends BaseSwingWindowTest
{
    /**
     * Creates the window to test.
     *
     * @return the test window
     */
    @Override
    protected SwingWindow createSwingWindow()
    {
        return new InternalFrameAdapterTestImpl(false);
    }

    /**
     * Tests if the root container can be correctly accessed.
     */
    @Test
    public void testGetRootContainer()
    {
        assertEquals("Root container could not be obtained",
                ((JInternalFrame) swingWindow).getContentPane(), swingWindow
                        .getRootContainer());
    }

    /**
     * Tests whether a default window listener is registered.
     */
    @Test
    public void testEventListener()
    {
        InternalFrameAdapter adapter = (InternalFrameAdapter) createSwingWindow();
        InternalFrameListener[] listeners = adapter.getInternalFrameListeners();
        boolean found = false;
        for (InternalFrameListener l : listeners)
        {
            if (l instanceof InternalFrameListenerAdapter)
            {
                found = true;
            }
        }
        assertTrue("No internal frame listener adapter registered", found);
    }

    /**
     * Tests the packWindow() implementation.
     */
    @Test
    public void testPackWindow()
    {
        InternalFrameAdapterTestImpl adapter = (InternalFrameAdapterTestImpl) createSwingWindow();
        adapter.packWindow();
        assertEquals("Wrong number of pack() invocations", 1, adapter.packCalls);
    }

    /**
     * Tests whether an auto-close listener can be registered and whether it
     * works as expected.
     */
    @Test
    public void testRegisterAutoCloseListener()
    {
        InternalFrameAdapterTestImpl adapter = (InternalFrameAdapterTestImpl) createSwingWindow();
        List<InternalFrameListener> listeners = Arrays.asList(adapter
                .getInternalFrameListeners());
        adapter.registerAutoCloseListener();
        List<InternalFrameListener> diffList = new ArrayList<InternalFrameListener>(
                Arrays.asList(adapter.getInternalFrameListeners()));
        diffList.removeAll(listeners);
        assertEquals("No listener added", 1, diffList.size());
        adapter.setVisible(true);
        diffList.get(0)
                .internalFrameClosing(new InternalFrameEvent(adapter, 0));
        assertEquals("Dispose not called", 1, adapter.disposeCalls);
    }

    /**
     * Tests whether a correct root pane is returned.
     */
    @Test
    public void testGetRootPane()
    {
        assertNotNull("No root pane", createSwingWindow().getRootPane());
    }

    /**
     * Tests a successful close() operation.
     */
    @Test
    public void testCloseSuccessful()
    {
        InternalFrameAdapterTestImpl adapter = (InternalFrameAdapterTestImpl) createSwingWindow();
        assertTrue("Wrong result", adapter.close(true));
        assertEquals("Wrong number of dispose() calls", 1, adapter.disposeCalls);
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
        InternalFrameAdapterTestImpl adapter = (InternalFrameAdapterTestImpl) createSwingWindow();
        EasyMock.expect(strat.canClose(adapter)).andReturn(Boolean.FALSE);
        EasyMock.replay(strat);
        adapter.setWindowClosingStrategy(strat);
        assertFalse("Wrong result", adapter.close(false));
        assertEquals("Wrong number of dispose() calls", 0, adapter.disposeCalls);
        EasyMock.verify(strat);
    }

    /**
     * A specialized test implementation of InternalFrameAdapter. This class
     * overrides the setVisible() method so that the frame cannot really be made
     * visible. However, the invocation is recorded.
     */
    private static class InternalFrameAdapterTestImpl extends
            InternalFrameAdapter
    {
        /**
         * The serial version UID.
         */
        private static final long serialVersionUID = -3845527352268325567L;

        /** The visible flag. */
        private boolean vis;

        /** The number of invocations of the pack() method. */
        int packCalls;

        /** The number of invocations of the dispose() method. */
        int disposeCalls;

        public InternalFrameAdapterTestImpl(boolean center)
        {
            super(center);
        }

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
