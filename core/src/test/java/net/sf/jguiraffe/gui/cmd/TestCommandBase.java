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
package net.sf.jguiraffe.gui.cmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for {@link CommandBase}.
 *
 * @author Oliver Heger
 * @version $Id: TestCommandBase.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestCommandBase
{
    /**
     * Tests an instance that was created using the standard constructor.
     */
    @Test
    public void testInit()
    {
        CommandBaseTestImpl cmd = new CommandBaseTestImpl();
        assertTrue("UI update flag not set", cmd.isUpdateGUI());
        assertNotNull("No logger set", cmd.getLog());
        assertNull("Got an exception", cmd.getException());
    }

    /**
     * Tests querying the GUI updater when the update flag is set.
     */
    @Test
    public void testGetGUIUpdaterUpdate()
    {
        CommandBaseTestImpl cmd = new CommandBaseTestImpl();
        Runnable r = cmd.getGUIUpdater();
        assertNotNull("No GUI updater", r);
        r.run();
        assertEquals("performGUIUpdate() not called", 1,
                cmd.performGUIUpdateCount);
    }

    /**
     * Tests querying the GUI updater when no update is desired.
     */
    @Test
    public void testGetGUIUpdaterNoUpdate()
    {
        CommandBaseTestImpl cmd = new CommandBaseTestImpl(false);
        Runnable r = cmd.getGUIUpdater();
        assertNull("Got an updater", r);
        assertEquals("performGUIUpdate() was called", 0,
                cmd.performGUIUpdateCount);
    }

    /**
     * Tests whether the exception passed to onException() is recorded.
     */
    @Test
    public void testOnException()
    {
        Throwable t = new RuntimeException("A test exception!");
        CommandBaseTestImpl cmd = new CommandBaseTestImpl();
        cmd.onException(t);
        assertEquals("Exception not set", t, cmd.getException());
    }

    /**
     * A concrete test implementation of CommandBase.
     */
    private static class CommandBaseTestImpl extends CommandBase
    {
        /** The number of invocations of performGUIUpdate(). */
        int performGUIUpdateCount;

        public CommandBaseTestImpl()
        {
            super();
        }

        public CommandBaseTestImpl(boolean updateGUI)
        {
            super(updateGUI);
        }

        public void execute() throws Exception
        {
        }

        /**
         * Records this invocation.
         */
        @Override
        protected void performGUIUpdate()
        {
            super.performGUIUpdate();
            performGUIUpdateCount++;
        }
    }
}
