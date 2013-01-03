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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JDialog;

import net.sf.jguiraffe.gui.builder.window.WindowClosingStrategy;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for DialogAdapter.
 *
 * @author Oliver Heger
 * @version $Id: TestDialogAdapter.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestDialogAdapter extends BaseSwingWindowTest
{
    /**
     * Creates the window to test. This is an instance of
     * <code>DialogAdapter</code>.
     *
     * @return the window to test
     */
    @Override
    protected SwingWindow createSwingWindow()
    {
        return new DialogAdapter(new MockDialog(), false);
    }

    /**
     * Tests if the correct root container is obtained.
     */
    @Test
    public void testGetRootContainer()
    {
        assertSame("Wrong root container", ((DialogAdapter) swingWindow)
                .getDialog().getContentPane(), swingWindow.getRootContainer());
    }

    /**
     * Tests whether a default window listener is registered.
     */
    @Test
    public void testEventListener()
    {
        DialogAdapter adapter = (DialogAdapter) createSwingWindow();
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
     * Tests the implementation of packWindow().
     */
    @Test
    public void testPackWindow()
    {
        DialogAdapter adapter = (DialogAdapter) createSwingWindow();
        adapter.packWindow();
        MockDialog md = (MockDialog) adapter.getWindow();
        assertEquals("Wrong number of pack() invocations", 1, md.packCalls);
    }

    /**
     * Tests whether an auto-close listener can be registered and whether it
     * works as expected.
     */
    @Test
    public void testRegisterAutoCloseListener()
    {
        DialogAdapter adapter = (DialogAdapter) createSwingWindow();
        MockDialog dlg = (MockDialog) adapter.getWindow();
        List<WindowListener> listeners = Arrays
                .asList(dlg.getWindowListeners());
        adapter.registerAutoCloseListener();
        List<WindowListener> diffList = new ArrayList<WindowListener>(Arrays
                .asList(dlg.getWindowListeners()));
        diffList.removeAll(listeners);
        assertEquals("No listener added", 1, diffList.size());
        dlg.setVisible(true);
        diffList.get(0).windowClosing(new WindowEvent(dlg, 0));
        assertEquals("Dispose not called", 1, dlg.disposeCalls);
    }

    /**
     * Tests whether the correct root pane is returned.
     */
    @Test
    public void testGetRootPane()
    {
        SwingWindow window = createSwingWindow();
        assertSame("Wrong root pane", ((DialogAdapter) window).getDialog()
                .getRootPane(), window.getRootPane());
    }

    /**
     * Tests the dispose() implementation.
     */
    @Test
    public void testDispose()
    {
        DialogAdapter adapter = (DialogAdapter) createSwingWindow();
        MockDialog dlg = (MockDialog) adapter.getWindow();
        adapter.dispose();
        assertEquals("Wrong number of dispose() calls", 1, dlg.disposeCalls);
    }

    /**
     * Tests a successful close() operation.
     */
    @Test
    public void testCloseSuccessful()
    {
        DialogAdapter adapter = (DialogAdapter) createSwingWindow();
        MockDialog dlg = (MockDialog) adapter.getWindow();
        assertTrue("Wrong result", adapter.close(true));
        assertEquals("Wrong number of dispose() calls", 1, dlg.disposeCalls);
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
        DialogAdapter adapter = (DialogAdapter) createSwingWindow();
        EasyMock.expect(strat.canClose(adapter)).andReturn(Boolean.FALSE);
        EasyMock.replay(strat);
        adapter.setWindowClosingStrategy(strat);
        MockDialog dlg = (MockDialog) adapter.getWindow();
        assertFalse("Wrong result", adapter.close(false));
        assertEquals("Wrong number of dispose() calls", 0, dlg.disposeCalls);
        EasyMock.verify(strat);
    }

    /**
     * A mock dialog class used for testing. This dialog overrides the
     * setVisible() method so that it cannot really be opened, but the
     * invocation is recorded.
     */
    private static class MockDialog extends JDialog
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
