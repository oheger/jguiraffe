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
package net.sf.jguiraffe.gui.app;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;

import net.sf.jguiraffe.gui.builder.utils.GUISynchronizer;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.builder.window.WindowEvent;
import net.sf.jguiraffe.gui.builder.window.WindowListener;

/**
 * Test class for {@code MainWindowCloseTracker}.
 */
public class TestMainWindowCloseTracker
{
    /** The tracker to be tested. */
    private MainWindowCloseTracker tracker;

    @Before
    public void setUp() throws Exception
    {
        tracker = new MainWindowCloseTracker();
    }

    /**
     * Tests that the tracker registers itself as listener at the main window.
     */
    @Test
    public void testListenerRegistrationOnInit()
    {
        Window window = EasyMock.createMock(Window.class);
        window.addWindowListener(tracker);
        EasyMock.replay(window);

        tracker.initMainWindow(window);
        EasyMock.verify(window);
    }

    /**
     * Tests the dummy implementation for the activated event.
     */
    @Test
    public void testWindowActivated()
    {
        WindowEvent event = EasyMock.createMock(WindowEvent.class);
        EasyMock.replay(event);

        tracker.windowActivated(event);
    }

    /**
     * Tests the dummy implementation for the deactivated event.
     */
    @Test
    public void testWindowDeactivated()
    {
        WindowEvent event = EasyMock.createMock(WindowEvent.class);
        EasyMock.replay(event);

        tracker.windowDeactivated(event);
    }

    /**
     * Tests the dummy implementation for the iconified event.
     */
    @Test
    public void testWindowIconified()
    {
        WindowEvent event = EasyMock.createMock(WindowEvent.class);
        EasyMock.replay(event);

        tracker.windowIconified(event);
    }

    /**
     * Tests the dummy implementation for the de-iconified event.
     */
    @Test
    public void testWindowDeiconified()
    {
        WindowEvent event = EasyMock.createMock(WindowEvent.class);
        EasyMock.replay(event);

        tracker.windowDeiconified(event);
    }

    /**
     * Tests the dummy implementation for the opened event.
     */
    @Test
    public void testWindowOpened()
    {
        WindowEvent event = EasyMock.createMock(WindowEvent.class);
        EasyMock.replay(event);

        tracker.windowOpened(event);
    }

    /**
     * Tests the dummy implementation for the closing event.
     */
    @Test
    public void testWindowClosing()
    {
        WindowEvent event = EasyMock.createMock(WindowEvent.class);
        EasyMock.replay(event);

        tracker.windowClosing(event);
    }

    /**
     * Tests that the class can deal with an application that does not have a
     * main window.
     */
    @Test
    public void testEnsureClosedNoMainWindow()
    {
        Application app = EasyMock.createMock(Application.class);
        EasyMock.replay(app);

        tracker.ensureMainWindowClosed(app);
    }

    /**
     * Tests that the main window is closed if necessary.
     */
    @Test
    public void testEnsureCloseWindowMustBeClosed()
    {
        Application app = EasyMock.createMock(Application.class);
        ApplicationContext appCtx =
                EasyMock.createMock(ApplicationContext.class);
        GUISynchronizer sync = EasyMock.createMock(GUISynchronizer.class);
        Window window = EasyMock.createMock(Window.class);
        sync.syncInvoke(EasyMock.anyObject(Runnable.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>()
        {
            public Object answer() throws Throwable
            {
                Runnable task = (Runnable) EasyMock.getCurrentArguments()[0];
                task.run();
                return null;
            }
        });
        EasyMock.expect(app.getApplicationContext()).andStubReturn(appCtx);
        EasyMock.expect(appCtx.getMainWindow()).andStubReturn(window);
        EasyMock.expect(app.getGUISynchronizer()).andReturn(sync);
        window.addWindowListener(EasyMock.anyObject(WindowListener.class));
        EasyMock.expect(window.close(true)).andReturn(Boolean.TRUE);
        EasyMock.replay(app, appCtx, sync, window);
        tracker.initMainWindow(window);

        tracker.ensureMainWindowClosed(app);
        EasyMock.verify(window);
    }

    /**
     * Tests that a window is not closed a second time.
     */
    @Test
    public void testEnsureCloseWindowAlreadyClosed()
    {
        Application app = EasyMock.createMock(Application.class);
        Window window = EasyMock.createMock(Window.class);
        window.addWindowListener(EasyMock.anyObject(WindowListener.class));
        EasyMock.replay(app, window);
        tracker.initMainWindow(window);

        tracker.windowClosed(null);
        tracker.ensureMainWindowClosed(app);
    }
}
