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

import java.util.EnumMap;
import java.util.Map;

import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.builder.window.WindowEvent;
import net.sf.jguiraffe.gui.builder.window.WindowListener;

import org.junit.Assert;

/**
 * <p>
 * A mock implementation of the <code>WindowListener</code> interface.
 * </p>
 * <p>
 * This class is used to test adapters for platform specific window listeners.
 * It can be configured with the expected number of invocations for the single
 * event listener methods and with the expected source window. After the test
 * the <code>check()</code> method can be used to find out if the expectations
 * are met. If the number of calls of a certain type should not be checked, it
 * can be set to a value less zero.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: WindowListenerImpl.java 205 2012-01-29 18:29:57Z oheger $
 */
public class WindowListenerImpl implements WindowListener
{
    /** A map holding the expected listener invocations. */
    private final Map<WindowEvent.Type, Integer> expectedCalls;

    /** A map holding the actual listener invocations. */
    private final Map<WindowEvent.Type, Integer> actualCalls;

    /** Stores the source window of the incoming events. */
    private Window expectedWindow;

    /** Stores the expected source of the incoming events. */
    private Object expectedSource;

    /**
     * Creates a new instance of {@code WindowListenerImpl}.
     */
    public WindowListenerImpl()
    {
        actualCalls = new EnumMap<WindowEvent.Type, Integer>(
                WindowEvent.Type.class);
        expectedCalls = new EnumMap<WindowEvent.Type, Integer>(
                WindowEvent.Type.class);
    }

    /**
     * Returns the expected number of calls for the specified event type.
     *
     * @param type the event type
     * @return the expected number of calls for events of this type (<b>null</b>
     *         means undefined)
     */
    public Integer getExpectedCalls(WindowEvent.Type type)
    {
        return expectedCalls.get(type);
    }

    /**
     * Sets the expected number of calls for the specified event type. The
     * integer value can be <b>null</b> meaning that the number does not matter.
     *
     * @param type the event type
     * @param calls the expected number of calls for events of this type
     */
    public void setExpectedCalls(WindowEvent.Type type, Integer calls)
    {
        if (calls != null)
        {
            expectedCalls.put(type, calls);
        }
        else
        {
            expectedCalls.remove(type);
        }
    }

    /**
     * Returns the current number of event calls received for the specified
     * type.
     *
     * @param type the event type
     * @return the number of calls received for this event type
     */
    public int getCalls(WindowEvent.Type type)
    {
        Integer num = actualCalls.get(type);
        return (num != null) ? num.intValue() : 0;
    }

    /**
     * Returns the expected window.
     *
     * @return the expected window
     */
    public Window getExpectedWindow()
    {
        return expectedWindow;
    }

    /**
     * Sets the expected window. If a value different from <b>null</b> is set,
     * received events are checked for this source window.
     *
     * @param expectedWindow the expected window
     */
    public void setExpectedWindow(Window expectedWindow)
    {
        this.expectedWindow = expectedWindow;
    }

    /**
     * Returns the expected event source.
     *
     * @return the expected event source
     */
    public Object getExpectedSource()
    {
        return expectedSource;
    }

    /**
     * Sets the expected event source. If a value different from <b>null</b> is
     * set, received events are checked for this source.
     *
     * @param expectedSource the expected event source
     */
    public void setExpectedSource(Object expectedSource)
    {
        this.expectedSource = expectedSource;
    }

    public void windowActivated(WindowEvent event)
    {
        checkEvent(event, WindowEvent.Type.WINDOW_ACTIVATED);
    }

    public void windowClosed(WindowEvent event)
    {
        checkEvent(event, WindowEvent.Type.WINDOW_CLOSED);
    }

    public void windowClosing(WindowEvent event)
    {
        checkEvent(event, WindowEvent.Type.WINDOW_CLOSING);
    }

    public void windowDeactivated(WindowEvent event)
    {
        checkEvent(event, WindowEvent.Type.WINDOW_DEACTIVATED);
    }

    public void windowDeiconified(WindowEvent event)
    {
        checkEvent(event, WindowEvent.Type.WINDOW_DEICONIFIED);
    }

    public void windowIconified(WindowEvent event)
    {
        checkEvent(event, WindowEvent.Type.WINDOW_ICONIFIED);
    }

    public void windowOpened(WindowEvent event)
    {
        checkEvent(event, WindowEvent.Type.WINDOW_OPENED);
    }

    /**
     * Checks if the expectations are met. This method should be called at the
     * end of a test case. It compares the expected data with the real figures.
     * If a difference is found, an error is thrown.
     */
    public void check()
    {
        for (Map.Entry<WindowEvent.Type, Integer> e : expectedCalls.entrySet())
        {
            Assert.assertEquals("Wrong number of calls for type "
                    + e.getKey().name(), e.getValue().intValue(), getCalls(e
                    .getKey()));
        }
    }

    /**
     * Tests if all expectations are met. Works similar to
     * <code>{@link #check()}</code>, but does not throw an exception.
     *
     * @return a flag if the expectations are met
     */
    public boolean test()
    {
        for (Map.Entry<WindowEvent.Type, Integer> e : expectedCalls.entrySet())
        {
            if (e.getValue().intValue() != getCalls(e.getKey()))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Waits a certain amount of milliseconds for the expectations to be met.
     * This is useful because events may come in asynchronously. After the
     * thread has waited for the specified amount of time, the
     * <code>check()</code> method will be called (which will cause a failure if
     * expectations are still not met).
     *
     * @param millis the amount of milliseconds to wait
     */
    public synchronized void waitForExpectations(long millis)
    {
        if (!test())
        {
            try
            {
                wait(millis);
            }
            catch (InterruptedException iex)
            {
                // interrupted
            }
        }

        check();
    }

    /**
     * Resets the current numbers and counters. This is a short cut for {@code
     * reset(true);}.
     */
    public void reset()
    {
        reset(true);
    }

    /**
     * Resets the current numbers of events and the expected counters. Depending
     * on the check parameter the expected counters are set to 0 (if check is
     * <b>true</b>) or to <b>null</b> (which means "don't care").
     *
     * @param check the check flag
     */
    public void reset(boolean check)
    {
        actualCalls.clear();
        expectedCalls.clear();

        if (check)
        {
            resetExpectedCalls();
        }
    }

    /**
     * Checks the specified window event and records this invocation. The
     * properties of the event can also be tested.
     *
     * @param event the event to check
     * @param type the expected event type
     */
    protected synchronized void checkEvent(WindowEvent event,
            WindowEvent.Type type)
    {
        Assert.assertEquals("Wrong event type", type, event.getType());
        if (getExpectedWindow() != null)
        {
            Assert.assertSame("Incorrect source window", getExpectedWindow(),
                    event.getSourceWindow());
        }
        if (getExpectedSource() != null)
        {
            Assert.assertEquals("Incorrect source", getExpectedSource(), event
                    .getSource());
        }

        int count = getCalls(type);
        actualCalls.put(type, Integer.valueOf(count + 1));

        if (test())
        {
            notifyAll();
        }
    }

    /**
     * Helper method for resetting the expected counters to 0.
     */
    private void resetExpectedCalls()
    {
        for (WindowEvent.Type t : WindowEvent.Type.values())
        {
            expectedCalls.put(t, 0);
        }
    }
}
