/*
 * Copyright 2006-2017 The JGUIraffe Team.
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

import java.util.concurrent.atomic.AtomicBoolean;

import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.builder.window.WindowEvent;
import net.sf.jguiraffe.gui.builder.window.WindowListener;

/**
 * <p>
 * An internally used helper class that monitors whether the application's main
 * window has already been closed.
 * </p>
 * <p>
 * This class is used to implement proper shutdown handling. Its main
 * responsibility is to make sure that the main window - if present - is closed
 * exactly once when the application shuts down. A shutdown can be triggered by
 * closing the main window or by a direct invocation of the {@code shutdown()}
 * method. No matter which way is used, a single close operation must be
 * performed on the window.
 * </p>
 */
class MainWindowCloseTracker implements WindowListener
{
    /**
     * Stores a flag whether the window has been closed. Note that this flag is
     * true per default, indicating the state that no window is available (yet).
     */
    private final AtomicBoolean windowClosed = new AtomicBoolean(true);

    /**
     * Initializes this object by passing in the main window. This method must
     * be called once when the application starts up. The passed in window is
     * then tracked by this instance.
     *
     * @param window the application main window to be tracked
     */
    public void initMainWindow(Window window)
    {
        windowClosed.set(false);
        window.addWindowListener(this);
    }

    /**
     * Makes sure that the application's main window is closed once. If no close
     * notification has been received yet from the window, it is closed now.
     * Otherwise, this call is ignored.
     *
     * @param app the application
     */
    public void ensureMainWindowClosed(Application app)
    {
        if (!windowClosed.get())
        {
            final Window window = app.getApplicationContext().getMainWindow();
            app.getGUISynchronizer().syncInvoke(new Runnable()
            {
                public void run()
                {
                    window.close(true);
                }
            });
        }
    }

    /**
     * {@inheritDoc} Dummy implementation.
     */
    public void windowActivated(WindowEvent event)
    {
    }

    /**
     * {@inheritDoc} Dummy implementation.
     */
    public void windowClosing(WindowEvent event)
    {
    }

    /**
     * {@inheritDoc} Records that the window has been closed.
     */
    public void windowClosed(WindowEvent event)
    {
        windowClosed.set(true);
    }

    /**
     * {@inheritDoc} Dummy implementation.
     */
    public void windowDeactivated(WindowEvent event)
    {
    }

    /**
     * {@inheritDoc} Dummy implementation.
     */
    public void windowDeiconified(WindowEvent event)
    {
    }

    /**
     * {@inheritDoc} Dummy implementation.
     */
    public void windowIconified(WindowEvent event)
    {
    }

    /**
     * {@inheritDoc} Dummy implementation.
     */
    public void windowOpened(WindowEvent event)
    {
    }
}
