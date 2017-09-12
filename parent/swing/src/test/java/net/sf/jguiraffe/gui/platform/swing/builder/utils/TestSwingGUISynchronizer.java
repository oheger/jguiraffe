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
package net.sf.jguiraffe.gui.platform.swing.builder.utils;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import net.sf.jguiraffe.gui.builder.utils.GUIRuntimeException;

import junit.framework.TestCase;

/**
 * Test class for SwingGUISynchronizer.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingGUISynchronizer.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingGUISynchronizer extends TestCase
{
    /** The object to be tested. */
    SwingGUISynchronizer sync;

    protected void setUp() throws Exception
    {
        super.setUp();
        sync = new SwingGUISynchronizer();
    }

    /**
     * Returns a runnable object for testing.
     *
     * @param r the runnable to be executed (can be <b>null</b>)
     * @return the runnable object
     */
    protected SyncRunnable createRunnable(Runnable r)
    {
        return new SyncRunnable(r);
    }

    /**
     * Tests an asynchronous invokation from outside the dispatch thread.
     */
    public void testAsyncInvoke()
    {
        SyncRunnable r = createRunnable(null);
        sync.asyncInvoke(r);
        r.waitFor();
        r.verify();
    }

    /**
     * Tests and asynchronous invokation from the event dispatch thread.
     */
    public void testAsyncInvokeFromDispatchThread()
    {
        final SyncRunnable r = createRunnable(null);
        SyncRunnable r2 = createRunnable(new Runnable()
        {
            public void run()
            {
                sync.asyncInvoke(r);
            }
        });
        sync.asyncInvoke(r2);
        r2.waitFor();
        r2.verify();
        r.waitFor();
        r.verify();
    }

    /**
     * Tests a synchronous invokation from outside the event dispatch thread.
     */
    public void testSyncInvoke()
    {
        SyncRunnable r = createRunnable(null);
        sync.syncInvoke(r);
        r.verify();
    }

    /**
     * Tests a synchronous invokation from the event dispatch thread.
     */
    public void testSyncInvokeFromDispatchThread()
    {
        final SyncRunnable r = createRunnable(null);
        SyncRunnable r2 = createRunnable(new Runnable()
        {
            public void run()
            {
                sync.syncInvoke(r);
            }
        });
        sync.syncInvoke(r2);
        r2.verify();
        r.verify();
    }

    /**
     * Tests a synchronous invokation when an exception is thrown.
     */
    public void testSyncInvokeWithException()
    {
        final RuntimeException rex = new RuntimeException("An exception");
        try
        {
            sync.syncInvoke(new Runnable()
            {
                public void run()
                {
                    throw rex;
                }
            });
            fail("Exception was not caught!");
        }
        catch (GUIRuntimeException grex)
        {
            assertTrue("Wrong nested exception",
                    grex.getCause() instanceof InvocationTargetException);
            InvocationTargetException itex = (InvocationTargetException) grex
                    .getCause();
            assertEquals("Wrong target exception", rex, itex.getCause());
        }
    }

    /**
     * Tests the isEventDispatchThread() method from outside the dispatch
     * thread.
     */
    public void testIsEventDispatchThreadNegative()
    {
        assertFalse("On dispatch thread?", sync.isEventDispatchThread());
    }

    /**
     * A test runnable implementation that can be used to check whether it is
     * executed on the event dispatch thread.
     */
    class SyncRunnable implements Runnable
    {
        /** A runnable that will be executed by the run() method. */
        private Runnable runnable;

        /** Stores the number of invocations. */
        private int calls;

        /**
         * Creates a new instance of <code>SyncRunnable</code> and sets the
         * runnable to be executed.
         *
         * @param r the runnable (can be <b>null</b>)
         */
        public SyncRunnable(Runnable r)
        {
            runnable = r;
        }

        /**
         * Returns the runnable that should be executed by the run() method.
         *
         * @return the runnable
         */
        public Runnable getRunnable()
        {
            return runnable;
        }

        /**
         * Returns the number of invocations of the run() method.
         *
         * @return the number how often run() was called
         */
        public int getCalls()
        {
            return calls;
        }

        /**
         * Waits until the run() method was executed. This can be used when
         * testing asynchronous invokations.
         */
        public synchronized void waitFor()
        {
            while (getCalls() < 1)
            {
                try
                {
                    wait();
                }
                catch (InterruptedException iex)
                {
                    fail("Waiting was interrupted: " + iex);
                }
            }
        }

        /**
         * Checkes if the run() method was exactly once executed.
         */
        public void verify()
        {
            assertEquals("run() was not executed once", 1, getCalls());
        }

        /**
         * Checks if this method is executed on the dispatch thread.
         */
        public void run()
        {
            assertTrue("Not on event dispatch thread", isOnDispatchThread());
            assertEquals("Wrong return value of synchronizer",
                    isOnDispatchThread(), sync.isEventDispatchThread());
            if (getRunnable() != null)
            {
                getRunnable().run();
            }
            finished();
        }

        /**
         * Called by run() at the end. If objects are waiting for the
         * termination of the run() method, they will be notified.
         */
        protected synchronized void finished()
        {
            calls++;
            notifyAll();
        }

        /**
         * Returns a flag whether this method is executed on the event dispatch
         * thread.
         *
         * @return a flag if this is the event dispatch thread
         */
        protected boolean isOnDispatchThread()
        {
            return SwingUtilities.isEventDispatchThread();
        }
    }
}
